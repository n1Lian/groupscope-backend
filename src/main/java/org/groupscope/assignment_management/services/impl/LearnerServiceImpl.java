package org.groupscope.assignment_management.services.impl;

import static org.groupscope.assignment_management.entity.LearningRole.EDITOR;
import static org.groupscope.assignment_management.entity.LearningRole.HEADMAN;
import static org.groupscope.assignment_management.entity.LearningRole.STUDENT;
import static org.groupscope.util.FunctionInfo.getCurrentMethodName;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dao.repositories.GradeRepository;
import org.groupscope.assignment_management.dao.repositories.LearnerRepository;
import org.groupscope.assignment_management.dao.repositories.LearningGroupRepository;
import org.groupscope.assignment_management.dto.LearnerDTO;
import org.groupscope.assignment_management.entity.Learner;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.groupscope.assignment_management.entity.LearningRole;
import org.groupscope.assignment_management.entity.Subject;
import org.groupscope.assignment_management.entity.Task;
import org.groupscope.assignment_management.entity.grade.Grade;
import org.groupscope.assignment_management.entity.grade.GradeKey;
import org.groupscope.assignment_management.services.LearnerService;
import org.groupscope.exceptions.EntityNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * @author Mykyta Liashko
 */
@Primary
@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class LearnerServiceImpl implements LearnerService {

    private final LearnerRepository learnerRepository;

    private final LearningGroupRepository learningGroupRepository;

    private final GradeRepository gradeRepository;


    /**
     * For saving new user or existing user
     */
    @Override
    @Transactional
    public Learner addLearner(
        @NotNull Learner learner,
        @NotNull String inviteCode
    ) {
        LearningGroup learningGroup = learningGroupRepository.getLearningGroupByInviteCode(inviteCode)
                .orElseThrow(() -> new EntityNotFoundException("Learning group with invite code = " + inviteCode + " not found"));

        boolean isGroupContainsLearner = learningGroup.getLearners().contains(learner);

        if (!isGroupContainsLearner) {
            // If learner is already in group
            // then we need to delete him from group
            // and choose new headman if needed
            processLearnerWithdrawal(learner);

            learner.setLearningGroup(learningGroup);
            List<Learner> learners = new ArrayList<>(learningGroup.getLearners());
            learners.add(learner);
            learningGroup.setLearners(learners);

            learner.setRole(STUDENT);

            learnerRepository.save(learner);
            learningGroupRepository.save(learningGroup);

            return refreshLearnerGrades(learner, learningGroup);
        } else
            throw new IllegalArgumentException("Learner = " + learner + " has been already including in group = " + learningGroup);

    }

    @Override
    @Transactional
    public Learner addFreeLearner(
        @NotNull LearnerDTO learnerDTO
    ) {
        learnerDTO.setLearningGroup(null);
        learnerDTO.setGrades(null);

        Learner learner = learnerDTO.toLearner();
        return learnerRepository.save(learner);
    }

    @Override
    @Transactional
    public Learner manageEditorRole(
        @NotNull Long learnerId,
        @NotNull LearningGroup group,
        boolean active
    ) {
        Learner learner = learnerRepository.findById(learnerId)
            .orElseThrow(() -> new EntityNotFoundException(Learner.class, learnerId));

        if (HEADMAN.equals(learner.getRole())) {
            throw new IllegalArgumentException("Can not change headman role to a lower one");
        }

        Long groupId = group.getId();
        Long learnerGroupId = learner.getLearningGroup().getId();

        if (groupId.equals(learnerGroupId)) {

            LearningRole learnerRole = learner.getRole();
            LearningRole newRole = active ? EDITOR : STUDENT;

            if (!newRole.equals(learnerRole)) {
                learner.setRole(newRole);
                learner = learnerRepository.save(learner);

                return learner;
            } else {
                throw new IllegalArgumentException("Learner: " + learner + ", already has role: " + newRole);
            }
        } else
            throw new IllegalArgumentException("Group = " + group + " not contains " + learner + " in " + getCurrentMethodName());
    }

    @Override
    @Transactional
    public Learner updateLearner(
        @NotNull LearnerDTO learnerDTO,
        @NotNull Learner learner
    ) {
        if (learnerDTO.getNewName() != null) {
            learner.setName(learnerDTO.getNewName());
        }
        if (learnerDTO.getNewLastname() != null) {
            learner.setLastname(learnerDTO.getNewLastname());
        }
        return learnerRepository.save(learner);
    }

    @Override
    @Transactional
    public void deleteLearner(
        @NotNull Learner learner
    ) {
        learnerRepository.delete(learner);
    }

    private Learner refreshLearnerGrades(
        @NotNull Learner learner,
        @NotNull LearningGroup newGroup
    ) {
        if (newGroup.getLearners().contains(learner)) {
            // Delete all grades of learner
            gradeRepository.deleteGradesByLearner(learner);
            learner.setGrades(new ArrayList<>());

            // Create new grades from new group
            for (Subject subject : newGroup.getSubjects()) {
                for (Task task : subject.getTasks()) {
                    GradeKey gradeKey = new GradeKey(learner.getId(), task.getId());
                    Grade grade = new Grade(gradeKey, learner, task, false, 0);

                    learner.getGrades().add(grade);
                }
            }

            gradeRepository.saveAll(learner.getGrades());
            return learnerRepository.save(learner);
        } else {
            throw new IllegalArgumentException("Learner = " + learner +
                    " is not include in group = " + newGroup);
        }
    }

    private void processLearnerWithdrawal(
        @NotNull Learner learner
    ) {
        LearningGroup group = learner.getLearningGroup();

        if (group == null) {
            return;
        }

        if (learner.getRole().equals(HEADMAN)) {
            if (group.getLearners().size() > 1) {
                // If headman is not the only one in group
                // then we need to choose new headman
                Learner newHeadman = group.getLearners().stream()
                        .filter(l -> !l.equals(group.getHeadmen()))
                        .findFirst()
                        .orElse(null);

                group.setHeadmen(newHeadman);
                group.getLearners().remove(learner);
                learningGroupRepository.save(group);
                learner.setLearningGroup(null);
            } else {
                // If headman is the only one in group
                // then we need to delete group
                learner.setLearningGroup(null);
                group.setHeadmen(null);
                learningGroupRepository.delete(group);
            }
        } else {
            group.getLearners().remove(learner);
            learner.setLearningGroup(null);
            learningGroupRepository.save(group);
        }
    }

}

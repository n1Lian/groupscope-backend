package org.groupscope.assignment_management.services.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dao.repositories.GradeRepository;
import org.groupscope.assignment_management.dao.repositories.LearnerRepository;
import org.groupscope.assignment_management.dao.repositories.LearningGroupRepository;
import org.groupscope.assignment_management.dto.LearnerDTO;
import org.groupscope.assignment_management.dto.LearningGroupDTO;
import org.groupscope.assignment_management.dto.NewHeadmanUpdateRequest;
import org.groupscope.assignment_management.entity.Learner;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.groupscope.assignment_management.entity.LearningRole;
import org.groupscope.assignment_management.services.GroupService;
import org.groupscope.exceptions.EntityNotFoundException;
import org.groupscope.exceptions.StudentNotInGroupException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mykyta Liashko
 */
@Primary
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final LearningGroupRepository learningGroupRepository;

    private final LearnerRepository learnerRepository;

    private final GradeRepository gradeRepository;

    @Override
    @Transactional
    public LearningGroupDTO getGroup(Learner learner) {
        return LearningGroupDTO.from(learner.getLearningGroup());
    }

    @Override
    @Transactional
    public LearningGroup createGroup(
        @NotNull LearnerDTO headmanDTO
    ) {
        LearningGroup group = new LearningGroup();

        Learner headman = headmanDTO.toLearner();
        headman.setRole(LearningRole.HEADMAN);
        headman.setLearningGroup(group);

        group.setHeadmen(headman);

        if (group.getHeadmen().getId() != null) {
            gradeRepository.deleteGradesByLearner(group.getHeadmen());
        }

        return learningGroupRepository.save(group);
    }

    @Override
    public LearningGroup getGroupById(Long id) {

      return learningGroupRepository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException(LearningGroup.class, id));
    }

    @Override
    public LearningGroup getGroupByInviteCode(String inviteCode) {
      return learningGroupRepository.getLearningGroupByInviteCode(inviteCode)
          .orElseThrow(() -> new EntityNotFoundException(LearningGroup.class, inviteCode));
    }

    @Override
    @Transactional
    public LearningGroup updateHeadmanOfGroup(LearningGroup group, NewHeadmanUpdateRequest request) {
        Learner oldHeadman = group.getHeadmen();
        Learner newHeadman = learnerRepository.findById(request.getId())
            .orElseThrow(() -> new EntityNotFoundException(Learner.class, request.getId()));

        Long groupId = group.getId();
        Long newHeadmanGroupId = newHeadman.getLearningGroup().getId();

        if (groupId.equals(newHeadmanGroupId)) {
            oldHeadman.setRole(LearningRole.STUDENT);
            newHeadman.setRole(LearningRole.HEADMAN);
            group.setHeadmen(newHeadman);

            learnerRepository.save(oldHeadman);
            learnerRepository.save(newHeadman);
            return learningGroupRepository.save(group);
        } else {
            throw new StudentNotInGroupException(newHeadman, group);
        }
    }


}

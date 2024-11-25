package org.groupscope.assignment_management.services.impl;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dao.repositories.GradeRepository;
import org.groupscope.assignment_management.dao.repositories.SubjectRepository;
import org.groupscope.assignment_management.dao.repositories.TaskRepository;
import org.groupscope.assignment_management.dto.GradeDTO;
import org.groupscope.assignment_management.dto.LearnerDTO;
import org.groupscope.assignment_management.entity.Learner;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.groupscope.assignment_management.entity.Subject;
import org.groupscope.assignment_management.entity.Task;
import org.groupscope.assignment_management.entity.grade.Grade;
import org.groupscope.assignment_management.entity.grade.GradeKey;
import org.groupscope.assignment_management.services.GradeService;
import org.groupscope.exceptions.EntityNotFoundException;
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
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;

    private final SubjectRepository subjectRepository;

    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public List<GradeDTO> getAllGradesOfSubject(
        @NotNull Long subjectId,
        @NotNull Learner learner
    ) {
        return learner.getGrades().stream()
                .filter(grade -> subjectId.equals(grade.getTask().getSubject().getId()))
                .map(GradeDTO::from)
                .toList();
    }

    @Override
    public List<LearnerDTO> getGradesOfSubjectFromGroup(
        @NotNull Long subjectId,
        @NotNull LearningGroup group
    ) {
        return group.getLearners().stream()
                .map(learner -> {
                    List<Grade> grades = gradeRepository.findAllByLearner(learner).stream()
                        .filter(grade -> subjectId.equals(grade.getTask().getSubject().getId()))
                        .toList();
                    learner.setGrades(grades);

                    return LearnerDTO.from(learner);
                })
                .toList();
    }

    @Override
    @Transactional
    public void updateGrade(
        @NotNull GradeDTO gradeDTO,
        @NotNull Learner learner
    ) {
        if (!gradeDTO.isValid())
            throw new IllegalArgumentException("The gradeDTO not valid ");

        Subject subject = subjectRepository.getSubjectByNameAndGroup_Id(
                gradeDTO.getSubjectName(),
                learner.getLearningGroup().getId()
        );
        if (subject == null) {
            throw new EntityNotFoundException("Subject not found with name: " + gradeDTO.getSubjectName());
        }

        Task task = taskRepository.getTaskByNameAndSubject_Id(
                gradeDTO.getTaskName(),
                subject.getId()
        );
        if (task == null) {
            throw new EntityNotFoundException("Task not found with name: " + gradeDTO.getTaskName());
        }

        GradeKey gradeKey = new GradeKey(learner.getId(), task.getId());
        Grade grade = gradeRepository.findGradeById(gradeKey);
        grade.setCompletion(gradeDTO.getCompletion());
        grade.setMark(gradeDTO.getMark());
        gradeRepository.save(grade);
    }
}

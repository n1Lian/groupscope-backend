package org.groupscope.assignment_management.services;

import org.groupscope.assignment_management.dto.GradeDTO;
import org.groupscope.assignment_management.dto.LearnerDTO;
import org.groupscope.assignment_management.entity.Learner;
import org.groupscope.assignment_management.entity.LearningGroup;

import java.util.List;

/**
 * @author Mykyta Liashko
 */
public interface GradeService {

    List<GradeDTO> getAllGradesOfSubject(Long subjectId, Learner learner);

    List<LearnerDTO> getGradesOfSubjectFromGroup(Long subjectId, LearningGroup group);

    void updateGrade(GradeDTO gradeDTO, Learner learner);

}

package org.groupscope.assignment_management.services;

import org.groupscope.assignment_management.dto.SubjectCreateRequest;
import org.groupscope.assignment_management.dto.SubjectDTO;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.groupscope.assignment_management.entity.Subject;

import java.util.List;

/**
 * @author Mykyta Liashko
 */
public interface SubjectService {

    Subject addSubject(LearningGroup group, SubjectCreateRequest createRequest);

    Subject getSubjectByName(String subjectName, LearningGroup group);

    List<SubjectDTO> getAllSubjectsByGroup(LearningGroup group);

    Subject updateSubject(Long subjectId, SubjectDTO subjectDTO, LearningGroup group);

    void deleteSubject(Long subjectId, LearningGroup group);

}

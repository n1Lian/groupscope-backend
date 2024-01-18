package org.groupscope.assignment_management.services;


import org.groupscope.assignment_management.dto.*;
import org.groupscope.assignment_management.entity.Learner;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.groupscope.assignment_management.entity.Subject;
import org.groupscope.assignment_management.entity.Task;

import java.util.List;

public interface AssignmentManagerService {

    Subject addSubject(SubjectDTO subjectDTO, LearningGroup group);

    Subject getSubjectByName(String subjectName, LearningGroup group);

    List<SubjectDTO> getAllSubjectDTOsByGroup(LearningGroup group, Learner learner);

    Subject updateSubject(SubjectDTO subjectDTO, LearningGroup group);

    void deleteSubject(String subjectName, LearningGroup group);

    ///////////////////////////////////////////////////
    Task addTask(TaskDTO taskDTO, String subjectName, LearningGroup group);

    List<TaskDTO> getAllTaskDTOsOfSubject(Long id, LearningGroup group, Learner learner);

    Task getTaskById(Long id);

    void updateTask(TaskDTO taskDTO, String subjectName, LearningGroup group);

    void deleteTask(String subjectName, TaskDTO taskDTO, LearningGroup group);

    ///////////////////////////////////////////////////

    List<GradeDTO> getAllGradesOfSubject(String subjectName, Learner learner);

    List<LearnerDTO> getGradesOfSubjectFromGroup(String subjectName, LearningGroup group);

    void updateGrade(GradeDTO gradeDTO, Learner learner);

    ///////////////////////////////////////////////////

    Learner addLearner(Learner learner, String inviteCode);

    Learner addFreeLearner(LearnerDTO learnerDTO);

    Learner manageEditorRole(Long id, LearningGroup group, boolean active);

    Learner getLearnerById(Long id);

    Learner updateLearner(LearnerDTO learnerDTO, Learner learner);

    void deleteLearner(Learner learner);

    Learner refreshLearnerGrades(Learner learner, LearningGroup newGroup);

    void processLearnerWithdrawal(Learner learner);

    ///////////////////////////////////////////////////

    LearningGroupDTO getGroup(Learner learner);

    LearningGroup addGroup(LearningGroupDTO learningGroupDTO);

    LearningGroup getGroupById(Long id);

    LearningGroup getGroupByInviteCode(String inviteCode);

    LearningGroup updateHeadmanOfGroup(LearningGroup group, Long learnerId);

}

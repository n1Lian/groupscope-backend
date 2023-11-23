package org.groupscope.assignment_management.dao;


import org.groupscope.assignment_management.entity.Learner;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.groupscope.assignment_management.entity.Subject;
import org.groupscope.assignment_management.entity.Task;
import org.groupscope.assignment_management.entity.grade.Grade;
import org.groupscope.assignment_management.entity.grade.GradeKey;

import java.util.List;

public interface AssignmentManagerDAO {

    Subject saveSubject(Subject subject);

    Subject findSubjectByName(String subjectName);

    Subject findSubjectByNameAndGroupId(String name, Long groupId);

    Subject findSubjectById(Long subjectId);

    List<Subject> findAllSubjects();

    List<Subject> findAllSubjectsByGroupName(String groupName);

    Subject updateSubject(Subject subject);

    void deleteSubject(Subject subject);

    void deleteSubjectByName(String name);

    void deleteSubjectByNameAndGroupId(String name, Long groupId);


    void saveTask(Task task);

    void saveAllTasks(List<Task> tasks);

    List<Task> findAllTasksOfSubject(Subject subject);

    Task findTaskByName(String name);

    Task findTaskById(Long id);

    Task findTaskByNameAndSubjectId(String name, Long subjectId);

    void updateTask(Task task);

    void deleteTask(Task task);

    void deleteTaskById(Long id);


    Learner saveLearner(Learner learner);

    Learner findLearnerByName(String name);

    Learner findLearnersByNameAndLastname(String name, String lastname);

    Learner findLearnerById(Long id);

    Learner updateLearner(Learner learner);

    void deleteLearner(Learner learner);

    void deleteLearnerById(Long id);


    LearningGroup saveGroup(LearningGroup learningGroup);

    LearningGroup findGroupById(Long id);

    LearningGroup findLearningGroupByInviteCode(String inviteCode);

    // TODO: Complete method for deleting group
    void deleteGroup(LearningGroup group);

    List<LearningGroup> getAllGroups();


    Grade saveGrade(Grade grade);

    List<Grade> saveAllGrades(List<Grade> grades);

    Grade findGradeByLearnerAndTask(Learner learner, Task task);

    Grade findGradeById(GradeKey id);

    List<Grade> findAllGradesByLearner(Learner learner);

    void deleteGradesByLearner(Learner learner);

    void deleteGradesByTask(Task task);

    void deleteGrades(List<Grade> grades);
}

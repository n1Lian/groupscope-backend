package org.groupscope.assignment_management.dao;

import lombok.extern.slf4j.Slf4j;

import org.groupscope.assignment_management.dao.repositories.*;
import org.groupscope.assignment_management.entity.*;
import org.groupscope.assignment_management.entity.grade.Grade;
import org.groupscope.assignment_management.entity.grade.GradeKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.groupscope.util.FunctionInfo.*;
import static org.groupscope.util.ObjectUtil.isNull;

// TODO make return values for methods null or empty list

@Component
@Slf4j
public class AssignmentManagerDAOImpl implements AssignmentManagerDAO {
    private final SubjectRepository subjectRepository;

    private final TaskRepository taskRepository;

    private final LearnerRepository learnerRepository;

    private final LearningGroupRepository learningGroupRepository;

    private final GradeRepository gradeRepository;

    @Autowired
    public AssignmentManagerDAOImpl(SubjectRepository subjectRepository,
                                    TaskRepository taskRepository,
                                    LearnerRepository learnerRepository,
                                    LearningGroupRepository learningGroupRepository,
                                    GradeRepository gradeRepository) {
        this.subjectRepository = subjectRepository;
        this.taskRepository = taskRepository;
        this.learnerRepository = learnerRepository;
        this.learningGroupRepository = learningGroupRepository;
        this.gradeRepository = gradeRepository;
    }

    public static void removeDuplicates (List<? extends ObjectWithId> objects) {
        if(objects == null)
            return;

        Set<Long> seenIds = new HashSet<>();
        Iterator<? extends ObjectWithId> iterator = objects.listIterator();

        while (iterator.hasNext()) {
            ObjectWithId object = iterator.next();
            if(seenIds.contains(object.getId())) {
                iterator.remove();
            } else {
                seenIds.add(object.getId());
            }
        }
    }



    @Override
    public Subject saveSubject(Subject subject) {
        if(isNull(subject)) {
            log.error("Subject is null in " + getCurrentMethodName());
            return null;
        }

        Subject result = subjectRepository.save(subject);
        log.info("Subject: " + subject.toString() + " saved/updated");
        return result;
    }

    @Override
    public Subject findSubjectByName(String name) {
        if(isNull(name)) {
            log.error("Subject name is null in " + getCurrentMethodName());
            return null;
        }

        return subjectRepository.getSubjectByName(name);
    }

    @Override
    public Subject findSubjectByNameAndGroupId(String name, Long groupId) {
        if(isNull(name, groupId)) {
            log.error("Subject name or group id is null in " + getCurrentMethodName());
            return null;
        }

        return subjectRepository.getSubjectByNameAndGroup_Id(name, groupId);
    }

    @Override
    public Subject findSubjectById(Long subjectId) {
        if(isNull(subjectId)) {
            log.error("Subject id is null in " + getCurrentMethodName());
            return null;
        }

        Optional<Subject> subject = subjectRepository.findById(subjectId);
        return subject.orElse(null);
    }

    @Override
    public List<Subject> findAllSubjects() {
        return (List<Subject>) subjectRepository.findAll();
    }

    @Override
    public List<Subject> findAllSubjectsByGroupName(String groupName) {
        if(isNull(groupName)) {
            log.error("Group name  is null in " + getCurrentMethodName());
            return null;
        }

        List<Subject> subjects = subjectRepository.findAllByGroup_Name(groupName);
        removeDuplicates(subjects);
        return subjects;
    }

    @Override
    public Subject updateSubject(Subject subject) {
        if(isNull(subject)) {
            log.error("Subject is null in " + getCurrentMethodName());
            return null;
        }

        return subjectRepository.save(subject);
    }

    @Override
    public void deleteSubject(Subject subject) {
        if(isNull(subject)) {
            log.error("Subject is null in " + getCurrentMethodName());
            return;
        }

        subjectRepository.delete(subject);
    }

    @Override
    public void deleteSubjectByName(String name) {
        if(isNull(name)) {
            log.error("Subject name is null in " + getCurrentMethodName());
            return;
        }

        subjectRepository.deleteSubjectByName(name);
    }

    @Override
    public void deleteSubjectByNameAndGroupId(String name, Long groupId) {
        if(isNull(name, groupId)) {
            log.error("Subject name or group id is null in " + getCurrentMethodName());
            return;
        }

        subjectRepository.deleteSubjectByNameAndGroup_Id(name, groupId);
    }

    @Override
    public void saveTask(Task task) {
        if(isNull(task)) {
            log.error("Task is null in " + getCurrentMethodName());
            return;
        }

        taskRepository.save(task);
        log.info("Task " + task.toString() + " saved");
    }

    @Override
    public void saveAllTasks(List<Task> tasks) {
        if(isNull(tasks)) {
            log.error("List of tasks is null in " + getCurrentMethodName());
            return;
        }

        taskRepository.saveAll(tasks);
    }

    @Override
    public List<Task> findAllTasksOfSubject(Subject subject) {
        if(isNull(subject)) {
            log.error("Subject is null in " + getCurrentMethodName());
            return null;
        }

        return taskRepository.findTasksBySubject(subject);
    }

    @Override
    public Task findTaskByName(String name) {
        if(isNull(name)) {
            log.error("Task name is null in " + getCurrentMethodName());
            return null;
        }

        return taskRepository.getTaskByName(name);
    }

    @Override
    public Task findTaskById(Long id) {
        if(isNull(id)) {
            log.error("Task id is null in " + getCurrentMethodName());
            return null;
        }

        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public Task findTaskByNameAndSubjectId(String name, Long subjectId) {
        if(isNull(name, subjectId)) {
            log.error("Task name or subject id is null in " + getCurrentMethodName());
            return null;
        }

        return taskRepository.getTaskByNameAndSubject_Id(name, subjectId);
    }

    @Override
    public void updateTask(Task task) {
        if(isNull(task)) {
            log.error("Task is null in " + getCurrentMethodName());
            return;
        }

        taskRepository.save(task);
    }

    @Override
    public void deleteTask(Task task) {
        if(isNull(task)) {
            log.error("Task is null in " + getCurrentMethodName());
            return;
        }

        taskRepository.delete(task);
    }

    @Override
    public void deleteTaskById(Long id) {
        if(isNull(id)) {
            log.error("Task id is null in " + getCurrentMethodName());
            return;
        }

        taskRepository.deleteById(id);
    }



    @Override
    public Learner saveLearner(Learner learner) {
        if(isNull(learner)) {
            log.error("Learner is null in " + getCurrentMethodName());
            return null;
        }

        Learner result =  learnerRepository.save(learner);
        if(result != null) {
            log.info("Learner " + learner.toString() + " saved");
        }
        return result;
    }

    @Override
    public Learner findLearnerByName(String name) {
        if(isNull(name)) {
            log.error("Learner name is null in " + getCurrentMethodName());
            return null;
        }

        Optional<Learner> learner = learnerRepository.getLearnerByName(name);
        return learner.orElse(null);
    }

    @Override
    public Learner findLearnersByNameAndLastname(String name, String lastname) {
        if(isNull(name, lastname)) {
            log.error("Learner name or lastname is null in " + getCurrentMethodName());
            return null;
        }

        Optional<Learner> learner = learnerRepository.getLearnersByNameAndLastname(name, lastname);
        return learner.orElse(null);
    }

    @Override
    public Learner findLearnerById(Long id) {
        if(isNull(id)) {
            log.error("Learner id is null in " + getCurrentMethodName());
            return null;
        }

        Optional<Learner> learner = learnerRepository.findById(id);
        return learner.orElse(null);
    }

    @Override
    public Learner updateLearner(Learner learner) {
        if(isNull(learner)) {
            log.error("Learner is null in " + getCurrentMethodName());
            return null;
        }

        log.info("Update " + learner.toString());
        return learnerRepository.save(learner);
    }

    @Override
    public void deleteLearner(Learner learner) {
        if(isNull(learner)) {
            log.error("Learner is null in " + getCurrentMethodName());
            return;
        }

        learnerRepository.delete(learner);
    }

    @Override
    public void deleteLearnerById(Long id) {
        if(isNull(id)) {
            log.error("Learner id is null in " + getCurrentMethodName());
            return;
        }

        learnerRepository.deleteById(id);
    }


    @Override
    public LearningGroup saveGroup(LearningGroup learningGroup) {
        if(isNull(learningGroup)) {
            log.error("LearningGroup is null in " + getCurrentMethodName());
            return null;
        }

        LearningGroup group = learningGroupRepository.save(learningGroup);
        if(group != null) {
            log.info("Learning Group " + group.toString() + " saved");
        }
        return group;
    }

    @Override
    public LearningGroup findGroupById(Long id) {
        if(isNull(id)) {
            log.error("LearningGroup id is null in " + getCurrentMethodName());
            return null;
        }

        Optional<LearningGroup> learningGroup = learningGroupRepository.findById(id);
        removeDuplicates(learningGroup.get().getSubjects());
        return learningGroup.orElse(null);
    }

    @Override
    public LearningGroup findLearningGroupByInviteCode(String inviteCode) {
        if(isNull(inviteCode)) {
            log.error("LearningGroup inviteCode is null in " + getCurrentMethodName());
            return null;
        }

        Optional<LearningGroup> learningGroup = learningGroupRepository.getLearningGroupByInviteCode(inviteCode);
        removeDuplicates(learningGroup.get().getSubjects());
        return learningGroup.orElse(null);

    }

    @Override
    public void deleteGroup(LearningGroup group) {
        if(isNull(group)) {
            log.error("LearningGroup is null in " + getCurrentMethodName());
            return;
        }

        learningGroupRepository.delete(group);
    }

    @Override
    public List<LearningGroup> getAllGroups() {
        return (List<LearningGroup>) learningGroupRepository.findAll();
    }



    @Override
    public Grade findGradeByLearnerAndTask(Learner learner, Task task) {
        if(isNull(learner, task)) {
            log.error("Learner or task is null in " + getCurrentMethodName());
            return null;
        }

        return gradeRepository.findGradeByLearnerAndTask(learner, task);
    }

    @Override
    public Grade findGradeById(GradeKey id) {
        if(isNull(id)) {
            log.error("GradeKey is null in " + getCurrentMethodName());
            return null;
        }

        return gradeRepository.findGradeById(id);
    }

    @Override
    public List<Grade> findAllGradesByLearner(Learner learner) {
        if(isNull(learner)) {
            log.error("Learner is null in " + getCurrentMethodName());
            return null;
        }

        return gradeRepository.findAllByLearner(learner);
    }

    @Override
    public Grade saveGrade(Grade grade) {
        if(isNull(grade)) {
            log.error("Grade is null in " + getCurrentMethodName());
            return null;
        }

        return gradeRepository.save(grade);
    }

    @Override
    public List<Grade> saveAllGrades(List<Grade> grades) {
        if(isNull(grades)) {
            log.error("List of Grade is null in " + getCurrentMethodName());
            return null;
        }

        return (List<Grade>) gradeRepository.saveAll(grades);
    }

    @Override
    public void deleteGradesByLearner(Learner learner) {
        if(isNull(learner)) {
            log.error("Learner is null in " + getCurrentMethodName());
            return;
        }

        gradeRepository.deleteGradesByLearner(learner);
    }

    @Override
    public void deleteGradesByTask(Task task) {
        if(isNull(task)) {
            log.error("Task is null in " + getCurrentMethodName());
            return;
        }

        gradeRepository.deleteGradesByTask(task);
    }

    @Override
    public void deleteGrades(List<Grade> grades) {
        if(isNull(grades)) {
            log.error("List of Grade is null in " + getCurrentMethodName());
            return;
        }

        gradeRepository.deleteAll(grades);
    }
}

package org.groupscope.assignment_management.services;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dao.AssignmentManagerDAO;
import org.groupscope.assignment_management.dto.*;
import org.groupscope.assignment_management.entity.*;
import org.groupscope.assignment_management.entity.grade.Grade;
import org.groupscope.assignment_management.entity.grade.GradeKey;
import org.groupscope.exceptions.DuplicateEntityException;
import org.groupscope.exceptions.EntityNotFoundException;
import org.groupscope.exceptions.StudentNotInGroupException;
import org.groupscope.schedule_nure.dao.ScheduleRedisDAO;
import org.groupscope.schedule_nure.dto.NureGroupDTO;
import org.groupscope.schedule_nure.dto.NureSubjectDTO;
import org.groupscope.schedule_nure.entity.NureGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.groupscope.util.FunctionInfo.getCurrentMethodName;

// TODO realize deletion of empty groups

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = PRIVATE)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentManagerServiceImpl implements AssignmentManagerService {

    AssignmentManagerDAO assignmentManagerDAO;

    ScheduleRedisDAO scheduleRedisDAO;

    // TODO update API doc for this method(or realize swagger for this project ^_^)
    @Override
    @Transactional
    public Subject addSubject(Long NureSubjectId, LearningGroup group) {
        requireNonNull(NureSubjectId, "Nure subject id is null");
        requireNonNull(group, "Learning group was null");

        Long nureGroupId = scheduleRedisDAO.getNureGroupByLinkId(group.getId()).getId();
        NureSubjectDTO nureSubjectDTO = scheduleRedisDAO.getSubjectsByNureGroupId(nureGroupId).stream()
                .filter(subject -> subject.getId().equals(NureSubjectId))
                .findFirst()
                .orElse(null);

        if(nureSubjectDTO == null)
            throw new EntityNotFoundException(NureSubjectDTO.class, NureSubjectId);

        Subject subject = new Subject(
                1L,
                nureSubjectDTO.getTitle(),
                nureSubjectDTO.getBrief(),
                false,
                new ArrayList<>(),
                group
        );

        if (group.getSubjects().contains(subject)) {
            throw new DuplicateEntityException("Subject: " + subject.getBrief() + " has been already existing in group = " + group.getName());
        }

        assignmentManagerDAO.saveSubject(subject);
        scheduleRedisDAO.saveNureSubjectByLinkId(subject.getId(), nureSubjectDTO.getId());
        return subject;
    }

    @Override
    public Subject getSubjectByName(String subjectName, LearningGroup group) {
        requireNonNull(group, "Learning group is null");
        requireNonNull(subjectName, "Subject name is null");

        Subject subject = group.getSubjects().stream()
                .filter(s -> s.getName().equals(subjectName))
                .findFirst().orElse(null);

        return requireNonNull(subject, "Subject with name = " + subjectName + "not found");
    }

    @Override
    @Transactional
    public Subject updateSubject(SubjectDTO subjectDTO, LearningGroup group) {
        requireNonNull(group, "Learning group is null");
        requireNonNull(subjectDTO, "SubjectDTO is null");

        Subject subject = assignmentManagerDAO.findSubjectByNameAndGroupId(subjectDTO.getName(), group.getId());

        requireNonNull(subject, "Subject not found with name: " + subjectDTO.getName());

        if (subjectDTO.getName() != null)
            subject.setName(subjectDTO.getName());
        if (subjectDTO.getBrief() != null)
            subject.setBrief(subjectDTO.getBrief());
        if (subjectDTO.getIsExam() != null)
            subject.setIsExam(subjectDTO.getIsExam());

        return assignmentManagerDAO.updateSubject(subject);
    }

    @Override
    @Transactional
    public void deleteSubject(String subjectName, LearningGroup group) {
        requireNonNull(group, "Learning group is null");
        Subject subject = assignmentManagerDAO.findSubjectByNameAndGroupId(subjectName, group.getId());

        requireNonNull(subject, "Subject not found with name: " + subjectName);
        assignmentManagerDAO.deleteSubject(subject);
    }

    @Override
    public List<SubjectDTO> getAllSubjectsByGroup(LearningGroup group) {
        requireNonNull(group, "Group doesnt exist");
        List<Subject> subjects = group.getSubjects();
        if (subjects != null)
            return subjects.stream()
                    .map(SubjectDTO::from)
                    .collect(Collectors.toList());
        else
            return new ArrayList<>();
    }

    // TODO when new task has added, subject duplicating
    //  P.S. was fixed by GroupScopeDAOImpl.removeDuplicates() function, but still not fixed in Hibernate response
    @Override
    @Transactional
    public Task addTask(TaskDTO taskDTO, String subjectName, LearningGroup group) {
        Task task = taskDTO.toTask();
        Subject subject = assignmentManagerDAO.findSubjectByNameAndGroupId(subjectName, group.getId());
        requireNonNull(subject, "Subject not found with name: " + subjectName);
        task.setSubject(subject);

        if (!subject.getTasks().contains(task)) {
            subject.getGroup().getLearners()
                    .forEach(learner -> {
                        GradeKey gradeKey = new GradeKey(learner.getId(), task.getId());
                        Grade grade = new Grade(gradeKey, learner, task, false, 0);

                        task.getGrades().add(grade);
                    });
            assignmentManagerDAO.saveTask(task);
            assignmentManagerDAO.saveAllGrades(task.getGrades());
            return task;
        } else
            throw new IllegalArgumentException("Task = " + task.toString() + " has been already existing");

    }

    @Override
    public List<TaskDTO> getAllTasksOfSubject(String subjectName, LearningGroup group) {
        requireNonNull(subjectName, "Subject name is null");
        requireNonNull(group, "Learning group is null");

        Subject subject = assignmentManagerDAO.findSubjectByNameAndGroupId(subjectName, group.getId());

        requireNonNull(subject, "Subject not found with name: " + subjectName);
        return subject.getTasks()
                .stream()
                .map(TaskDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Task getTaskById(Long id) {
        requireNonNull(id, "Id is null");
        Task task = assignmentManagerDAO.findTaskById(id);

        requireNonNull(task, "Task not found with id = " + id);
        return task;
    }

    @Override
    @Transactional
    public void updateTask(TaskDTO taskDTO, String subjectName, LearningGroup group) {
        requireNonNull(taskDTO, "TaskDTO is null");
        requireNonNull(subjectName, "Subject name is null");
        requireNonNull(group, "Learning group is null");

        Subject subject = assignmentManagerDAO.findSubjectByNameAndGroupId(subjectName, group.getId());
        requireNonNull(subject, "Subject not found with name: " + subjectName);

        Task task = assignmentManagerDAO.findTaskByNameAndSubjectId(taskDTO.getName(), subject.getId());
        requireNonNull(task, "Task not found with name: " + taskDTO.getName());

        if (taskDTO.getNewName() != null) {
            task.setName(taskDTO.getNewName());
        }
        if (taskDTO.getType() != null) {
            task.setType(taskDTO.getType());
        }
        if (taskDTO.getInfo() != null) {
            task.setInfo(taskDTO.getInfo());
        }
        if (taskDTO.getDeadline() != null) {
            if (taskDTO.isValidDeadline()) {
                task.setDeadline(taskDTO.getDeadline());
            } else {
                throw new IllegalArgumentException("TaskDTO = " + taskDTO + " is not valid in " + getCurrentMethodName());
            }
        }
        if (taskDTO.getMaxMark() != null) {
            if (taskDTO.isValidMaxMark()) {
                task.setMaxMark(taskDTO.getMaxMark());
            } else {
                throw new IllegalArgumentException("TaskDTO = " + taskDTO + " is not valid in " + getCurrentMethodName());
            }
        }
        assignmentManagerDAO.updateTask(task);

    }


    @Override
    @Transactional
    public void deleteTask(String subjectName, TaskDTO taskDTO, LearningGroup group) {
        Subject subject = assignmentManagerDAO.findSubjectByNameAndGroupId(subjectName, group.getId());
        requireNonNull(subject, "Subject not found with name: " + subjectName);

        Task task = assignmentManagerDAO.findTaskByNameAndSubjectId(taskDTO.getName(), subject.getId());
        requireNonNull(task, "Task not found with name: " + taskDTO.getName());

        assignmentManagerDAO.deleteGradesByTask(task);
        assignmentManagerDAO.deleteTaskById(task.getId());
    }

    @Override
    @Transactional
    public List<GradeDTO> getAllGradesOfSubject(String subjectName, Learner learner) {
        requireNonNull(subjectName, "Subject name is null");
        requireNonNull(learner, "Learner is null");

        return learner.getGrades().stream()
                .filter(grade -> grade.getTask().getSubject().getName().equals(subjectName))
                .map(GradeDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<LearnerDTO> getGradesOfSubjectFromGroup(String subjectName, LearningGroup group) {
        requireNonNull(subjectName, "Subject name is null");
        requireNonNull(group, "Learning group is null");

        return group.getLearners().stream()
                .peek(learner -> {
                    List<Grade> grades = assignmentManagerDAO.findAllGradesByLearner(learner).stream()
                            .filter(grade -> grade.getTask().getSubject().getName().equals(subjectName))
                            .collect(Collectors.toList());
                    learner.setGrades(grades);
                })
                .map(LearnerDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateGrade(GradeDTO gradeDTO, Learner learner) {
        requireNonNull(gradeDTO, "GradeDTO is null");
        requireNonNull(learner, "Learner is null");

        if (!gradeDTO.isValid())
            throw new IllegalArgumentException("The gradeDTO not valid ");

        Subject subject = assignmentManagerDAO.findSubjectByNameAndGroupId(
                gradeDTO.getSubjectName(),
                learner.getLearningGroup().getId()
        );
        requireNonNull(subject, "Subject not found with name: " + gradeDTO.getSubjectName());


        Task task = assignmentManagerDAO.findTaskByNameAndSubjectId(
                gradeDTO.getTaskName(),
                subject.getId()
        );
        requireNonNull(task, "Task not found with name = " + gradeDTO.getTaskName());

        GradeKey gradeKey = new GradeKey(learner.getId(), task.getId());
        Grade grade = assignmentManagerDAO.findGradeById(gradeKey);
        grade.setCompletion(gradeDTO.getCompletion());
        grade.setMark(gradeDTO.getMark());
        assignmentManagerDAO.saveGrade(grade);
    }

    /**
     * For saving new user or existing user
     */
    @Override
    @Transactional
    public Learner addLearner(Learner learner, String inviteCode) {
        requireNonNull(learner, "Learner code is null");
        requireNonNull(inviteCode, "Invite code is null");

        LearningGroup learningGroup = assignmentManagerDAO.findLearningGroupByInviteCode(inviteCode);
        if (learningGroup == null)
            throw new EntityNotFoundException(LearningGroup.class, inviteCode);

        boolean isGroupContainsLearner = learningGroup.getLearners().contains(learner);
        processLearnerWithdrawal(learner);

        if (!isGroupContainsLearner) {
            learner.setLearningGroup(learningGroup);
            List<Learner> learners = new ArrayList<>(learningGroup.getLearners());
            learners.add(learner);
            learningGroup.setLearners(learners);

            learner.setRole(LearningRole.STUDENT);

            assignmentManagerDAO.saveLearner(learner);
            assignmentManagerDAO.saveGroup(learningGroup);

            return refreshLearnerGrades(learner, learningGroup);
        } else
            throw new IllegalArgumentException("Learner = " + learner + " has been already including in group = " + learningGroup);

    }

    @Override
    @Transactional
    public Learner addFreeLearner(LearnerDTO learnerDTO) {
        requireNonNull(learnerDTO, "LearnerDTO is null");

        learnerDTO.setLearningGroup(null);
        learnerDTO.setGrades(null);

        Learner learner = learnerDTO.toLearner();
        return assignmentManagerDAO.saveLearner(learner);
    }

    @Override
    @Transactional
    public Learner manageEditorRole(Long id, LearningGroup group, boolean active) {
        requireNonNull(id, "Id is null");
        requireNonNull(group, "Learning group is null");

        Learner learner = getLearnerById(id);
        if (learner.getRole() == LearningRole.HEADMAN)
            throw new IllegalArgumentException("Can not change headman role in " + getCurrentMethodName());

        requireNonNull(group, "Group is null");

        if (group.getLearners().contains(learner)) {
            if (active && learner.getRole() != LearningRole.EDITOR) {
                learner.setRole(LearningRole.EDITOR);
                learner = assignmentManagerDAO.updateLearner(learner);
            } else if (!active && learner.getRole() != LearningRole.STUDENT) {
                learner.setRole(LearningRole.STUDENT);
                learner = assignmentManagerDAO.updateLearner(learner);
            }
            return learner;
        } else
            throw new IllegalArgumentException("Group = " + group + " not contains " + learner + " in " + getCurrentMethodName());
    }

    @Override
    public Learner getLearnerById(Long id) {
        requireNonNull(id, "Id is null");

        Learner learner = assignmentManagerDAO.findLearnerById(id);
        requireNonNull(learner, "Learner with id = " + id + " not found");

        return learner;
    }

    @Override
    @Transactional
    public Learner updateLearner(LearnerDTO learnerDTO, Learner learner) {
        requireNonNull(learnerDTO, "LearnerDTO is null");
        requireNonNull(learner, "Learner is null");

        if (learnerDTO.getNewName() != null)
            learner.setName(learnerDTO.getNewName());
        if (learnerDTO.getNewLastname() != null)
            learner.setLastname(learnerDTO.getNewLastname());
        return assignmentManagerDAO.updateLearner(learner);
    }

    @Override
    @Transactional
    public void deleteLearner(Learner learner) {
        requireNonNull(learner, "Learner is null");
        assignmentManagerDAO.deleteLearner(learner);
    }

    @Override
    @Transactional
    public LearningGroupDTO getGroup(Learner learner) {
        requireNonNull(learner, "Learner is null");

        requireNonNull(learner.getLearningGroup(), "Learning group is null");

        for (Learner lr : learner.getLearningGroup().getLearners())
            lr.setGrades(assignmentManagerDAO.findAllGradesByLearner(lr));

        return LearningGroupDTO.from(learner.getLearningGroup());
    }

    // TODO update API doc for this method(or realize swagger for this project ^_^)
    @Override
    @Transactional
    public LearningGroup addGroup(Long nureGroupId, LearnerDTO headman) {
        requireNonNull(nureGroupId, "Nure group id is null");
        requireNonNull(headman, "Headman is null");

        NureGroupDTO nureGroup = scheduleRedisDAO.getGroupById(nureGroupId);

        LearningGroup group = new LearningGroup(nureGroup.getName());
        group.setHeadmen(headman.toLearner());
        group.getHeadmen().setRole(LearningRole.HEADMAN);
        group.getHeadmen().setLearningGroup(group);

        if (group.getHeadmen().getId() != null)
            assignmentManagerDAO.deleteGradesByLearner(group.getHeadmen());

        HashMap<Long, Long> linkIdToNureId = scheduleRedisDAO.getGroupsLinks();

        if(linkIdToNureId.containsValue(nureGroupId))
            throw new DuplicateEntityException(LearningGroup.class, group.getName());

        group = assignmentManagerDAO.saveGroup(group);
        group.generateInviteCode();
        scheduleRedisDAO.saveNureGroupByLinkId(group.getId(), nureGroupId);
        return assignmentManagerDAO.saveGroup(group);
    }

    @Override
    public LearningGroup getGroupById(Long id) {
        requireNonNull(id, "Id is null");

        LearningGroup learningGroup = assignmentManagerDAO.findGroupById(id);
        if (learningGroup == null)
            throw new EntityNotFoundException(LearningGroup.class, id);

        return learningGroup;
    }

    @Override
    public LearningGroup getGroupByInviteCode(String inviteCode) {
        requireNonNull(inviteCode, "Invite code is null");

        LearningGroup learningGroup = assignmentManagerDAO.findLearningGroupByInviteCode(inviteCode);
        requireNonNull(learningGroup, "Group with inviteCode = " + inviteCode + " not found");

        return learningGroup;
    }

    @Override
    @Transactional
    public LearningGroup updateHeadmanOfGroup(LearningGroup group, LearnerDTO learnerDTO) {
        requireNonNull(group, "learning group is null");
        requireNonNull(learnerDTO, "LearnerDTO is null");


        Learner oldHeadman = group.getHeadmen();
        Learner newHeadman = assignmentManagerDAO.findLearnersByNameAndLastname(learnerDTO.getName(), learnerDTO.getLastname());
        if (newHeadman == null)
            throw new EntityNotFoundException(Learner.class, learnerDTO.getId());

        if (group.getLearners().contains(newHeadman)) {
            oldHeadman.setRole(LearningRole.STUDENT);
            newHeadman.setRole(LearningRole.HEADMAN);
            group.setHeadmen(newHeadman);

            assignmentManagerDAO.saveLearner(oldHeadman);
            assignmentManagerDAO.saveLearner(newHeadman);
            return assignmentManagerDAO.saveGroup(group);
        } else
            throw new StudentNotInGroupException(newHeadman, group);
    }

    /**
     * The learner must be included in new group
     */
    @Override
    @Transactional
    public Learner refreshLearnerGrades(Learner learner, LearningGroup newGroup) {
        requireNonNull(learner, "Learner is null");
        requireNonNull(newGroup, "New learning group is null");

        if (newGroup.getLearners().contains(learner)) {
            assignmentManagerDAO.deleteGradesByLearner(learner);
            learner.setGrades(new ArrayList<>());

            for (Subject subject : newGroup.getSubjects()) {
                for (Task task : subject.getTasks()) {
                    GradeKey gradeKey = new GradeKey(learner.getId(), task.getId());
                    Grade grade = new Grade(gradeKey, learner, task, false, 0);

                    learner.getGrades().add(grade);
                }
            }

            assignmentManagerDAO.saveAllGrades(learner.getGrades());
            return assignmentManagerDAO.saveLearner(learner);
        } else {
            throw new IllegalArgumentException("Learner = " + learner +
                    " is not include in group = " + newGroup);
        }
    }

    @Override
    public void processLearnerWithdrawal(Learner learner) {
        requireNonNull(learner, "Learner is null");

        LearningGroup group = learner.getLearningGroup();

        if (group == null) {
            return;
        }

        if (learner.getRole().equals(LearningRole.HEADMAN)) {
            if (group.getLearners().size() > 1) {
                Learner newHeadman = group.getLearners().stream()
                        .filter(l -> !l.equals(group.getHeadmen()))
                        .findFirst()
                        .orElse(null);

                group.setHeadmen(newHeadman);
                group.getLearners().remove(learner);
                assignmentManagerDAO.saveGroup(group);
                learner.setLearningGroup(null);
            } else {
                learner.setLearningGroup(null);
                group.setHeadmen(null);
                assignmentManagerDAO.deleteGroup(group);
            }
        } else {
            group.getLearners().remove(learner);
            learner.setLearningGroup(null);
            assignmentManagerDAO.saveGroup(group);
        }
    }
}

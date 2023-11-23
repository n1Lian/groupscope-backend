package org.groupscope.assignment_management.services;

import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dao.AssignmentManagerDAO;
import org.groupscope.assignment_management.dto.*;
import org.groupscope.assignment_management.entity.*;
import org.groupscope.assignment_management.entity.grade.Grade;
import org.groupscope.assignment_management.entity.grade.GradeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.groupscope.util.FunctionInfo.*;

// TODO realize deletion of empty groups

@Slf4j
@Service
@Transactional(readOnly = true)
public class AssignmentManagerServiceImpl implements AssignmentManagerService {

    private final AssignmentManagerDAO assignmentManagerDAO;

    @Autowired
    public AssignmentManagerServiceImpl(AssignmentManagerDAO assignmentManagerDAO) {
        this.assignmentManagerDAO = assignmentManagerDAO;
    }

    @Override
    @Transactional
    public Subject addSubject(SubjectDTO subjectDTO, LearningGroup group) {
        Subject subject = subjectDTO.toSubject();
        if(group != null) {
            subject.setGroup(group);

            if(!subject.getGroup().getSubjects().contains(subject)) {
                assignmentManagerDAO.saveSubject(subject);
                return subject;
            } else
                throw new IllegalArgumentException("Subject = " + subject.toString() + " has been already existing");
        }
        else {
            throw new NullPointerException("Learning group was null");
        }
    }

    @Override
    public Subject getSubjectByName(String subjectName, LearningGroup group) {
        if(group != null && subjectName != null) {
            Optional<Subject> subject = group.getSubjects().stream()
                    .filter(s -> s.getName().equals(subjectName))
                    .findFirst();
            if(subject.isPresent())
                return subject.get();
            else
                throw new NullPointerException("Subject with name = " + subjectName + "not found");
        } else
            throw new NullPointerException("Learning group is null");
    }

    @Override
    @Transactional
    public Subject updateSubject(SubjectDTO subjectDTO, LearningGroup group) {
        if(group != null && subjectDTO != null) {
            Subject subject = assignmentManagerDAO.findSubjectByNameAndGroupId(subjectDTO.getName(), group.getId());

            if(subject != null) {
                if (subjectDTO.getNewName() != null)
                    subject.setName(subjectDTO.getNewName());
                if(subjectDTO.getIsExam() != null)
                    subject.setIsExam(subjectDTO.getIsExam());

                return assignmentManagerDAO.updateSubject(subject);
            }
            else
                throw new NullPointerException("Subject not found with name: " + subjectDTO.getName());
        } else
            throw new NullPointerException("Learning group or subjectDTO are null");
    }

    @Override
    @Transactional
    public void deleteSubject(String subjectName, LearningGroup group) {
        if (group != null) {
            Subject subject = assignmentManagerDAO.findSubjectByNameAndGroupId(subjectName, group.getId());
            if (subject != null) {
                assignmentManagerDAO.deleteSubject(subject);
            } else
                throw new NullPointerException("Subject not found with name: " + subjectName);

        } else
            throw new NullPointerException("Learning group is null");
    }

    @Override
    public List<SubjectDTO> getAllSubjectsByGroup(LearningGroup group) {
        if(group != null) {
            List<Subject> subjects = group.getSubjects();
            if (subjects != null)
                return subjects.stream()
                        .map(SubjectDTO::from)
                        .collect(Collectors.toList());
            else
                return new ArrayList<>();
        } else
            throw new NullPointerException("Group doesnt exist");
    }

    // TODO when new task has added, subject duplicating
    //  P.S. was fixed by GroupScopeDAOImpl.removeDuplicates() function, but still not fixed in Hibernate response
    @Override
    @Transactional
    public Task addTask(TaskDTO taskDTO, String subjectName, LearningGroup group) {
        Task task = taskDTO.toTask();
        Subject subject = assignmentManagerDAO.findSubjectByNameAndGroupId(subjectName, group.getId());
        if(subject != null) {
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
        else
            throw new NullPointerException("Subject not found with name: " + subjectName);
    }

    @Override
    public List<TaskDTO> getAllTasksOfSubject(String subjectName, LearningGroup group) {
        if(subjectName != null && group != null) {
            Subject subject = assignmentManagerDAO.findSubjectByNameAndGroupId(subjectName, group.getId());

            if (subject != null) {
                return subject.getTasks()
                        .stream()
                        .map(TaskDTO::from)
                        .collect(Collectors.toList());
            } else
                throw new NullPointerException("Subject not found with name: " + subjectName);
        } else
            throw new NullPointerException("Subject name or group is null");
    }

    @Override
    @Transactional
    public Task getTaskById(Long id) {
        if(id != null) {
            Task task = assignmentManagerDAO.findTaskById(id);

            if(task != null) {
                return task;
            } else
                throw new NullPointerException("Task not found with id: " + id + " in " + getCurrentMethodName());
        } else
            throw new NullPointerException("Id is null in " + getCurrentMethodName());
    }

    @Override
    @Transactional
    public void updateTask(TaskDTO taskDTO, String subjectName, LearningGroup group) {
        Subject subject = assignmentManagerDAO.findSubjectByNameAndGroupId(subjectName, group.getId());
        if(subject != null) {
            Task task = assignmentManagerDAO.findTaskByNameAndSubjectId(taskDTO.getName(), subject.getId());
            if (task != null) {
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
                    if(taskDTO.isValidDeadline()) {
                        task.setDeadline(taskDTO.getDeadline());
                    } else {
                        throw new IllegalArgumentException("TaskDTO = " + taskDTO + " is not valid in " + getCurrentMethodName());
                    }
                }
                if (taskDTO.getMaxMark() != null) {
                    if(taskDTO.isValidMaxMark()) {
                        task.setMaxMark(taskDTO.getMaxMark());
                    } else {
                        throw new IllegalArgumentException("TaskDTO = " + taskDTO + " is not valid in " + getCurrentMethodName());
                    }
                }
                assignmentManagerDAO.updateTask(task);
            } else {
                throw new NullPointerException("Task not found with name: " + taskDTO.getName());
            }
        } else
            throw new NullPointerException("Subject not found with name: " + subjectName);
    }

    @Override
    @Transactional
    public void deleteTask(String subjectName, TaskDTO taskDTO, LearningGroup group) {
        Subject subject = assignmentManagerDAO.findSubjectByNameAndGroupId(subjectName, group.getId());

        if(subject != null) {
            Task task = assignmentManagerDAO.findTaskByNameAndSubjectId(taskDTO.getName(), subject.getId());
            if (task != null) {
                assignmentManagerDAO.deleteGradesByTask(task);
                assignmentManagerDAO.deleteTaskById(task.getId());
            } else
                throw new NullPointerException("Task not found with name: " + taskDTO.getName());
        } else
            throw new NullPointerException("Subject not found with name: " + subjectName);
    }

    @Override
    @Transactional
    public List<GradeDTO> getAllGradesOfSubject(String subjectName, Learner learner) {
        if(subjectName == null || learner == null)
            throw (subjectName == null) ? new NullPointerException("Subject name is null") :
                                        new NullPointerException("Learner is null");

        return learner.getGrades().stream()
                .filter(grade -> grade.getTask().getSubject().getName().equals(subjectName))
                .map(GradeDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<LearnerDTO> getGradesOfSubjectFromGroup(String subjectName, LearningGroup group) {
        if(subjectName != null && group != null) {
            return group.getLearners().stream()
                    .peek(learner -> {
                        List<Grade> grades = assignmentManagerDAO.findAllGradesByLearner(learner).stream()
                                        .filter(grade -> grade.getTask().getSubject().getName().equals(subjectName))
                                                .collect(Collectors.toList());
                        learner.setGrades(grades);
                    })
                    .map(LearnerDTO::from)
                    .collect(Collectors.toList());
        } else
            throw new NullPointerException("Subject name or group is null");

    }

    @Override
    @Transactional
    public void updateGrade(GradeDTO gradeDTO, Learner learner) {
        if(!gradeDTO.isValid())
            throw new IllegalArgumentException("The gradeDTO not valid ");

        Subject subject = assignmentManagerDAO.findSubjectByNameAndGroupId(
                gradeDTO.getSubjectName(),
                learner.getLearningGroup().getId()
        );

        if(subject != null) {
            Task task = assignmentManagerDAO.findTaskByNameAndSubjectId(
                    gradeDTO.getTaskName(),
                    subject.getId()
            );

            if(task != null) {
                GradeKey gradeKey = new GradeKey(learner.getId(), task.getId());
                Grade grade = assignmentManagerDAO.findGradeById(gradeKey);
                grade.setCompletion(gradeDTO.getCompletion());
                grade.setMark(gradeDTO.getMark());
                assignmentManagerDAO.saveGrade(grade);
            } else
                throw new NullPointerException("Task not found with name: " + gradeDTO.getTaskName());
        } else
            throw new NullPointerException("Subject not found with name: " + gradeDTO.getSubjectName());
    }

    /**
     For saving new user or existing user
     */
    @Override
    @Transactional
    public Learner addLearner(Learner learner, String inviteCode) {
        if(learner != null && inviteCode != null) {
            LearningGroup learningGroup = assignmentManagerDAO.findLearningGroupByInviteCode(inviteCode);

            if (learningGroup != null) {
                boolean isGroupContainsLearner = learningGroup.getLearners().contains(learner);
                processLearnerWithdrawal(learner);

                if(!isGroupContainsLearner) {
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
            } else
                throw new NullPointerException("Group with inviteCode = " + inviteCode + " not found");
        } else
            throw new NullPointerException("Learner or invite code is null");
    }

    @Override
    @Transactional
    public Learner addFreeLearner(LearnerDTO learnerDTO) {
        if (learnerDTO != null) {
            learnerDTO.setLearningGroup(null);
            learnerDTO.setGrades(null);

            Learner learner = learnerDTO.toLearner();
            return assignmentManagerDAO.saveLearner(learner);
        } else
            throw new NullPointerException("LearnerDTO is null");
    }

    @Override
    @Transactional
    public Learner manageEditorRole(Long id, LearningGroup group, boolean active) {
        Learner learner = getLearnerById(id);
        if(learner.getRole() == LearningRole.HEADMAN)
            throw new IllegalArgumentException("Can not change headman role in " + getCurrentMethodName());

        if(group != null) {
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
        } else
            throw new NullPointerException("Group is null in " + getCurrentMethodName());
    }

    @Override
    public Learner getLearnerById(Long id) {
        Learner learner = assignmentManagerDAO.findLearnerById(id);
        if(learner != null)
            return learner;
        else
            throw new NullPointerException("Learner with id = " + id + " not found");
    }

    @Override
    @Transactional
    public Learner updateLearner(LearnerDTO learnerDTO, Learner learner) {
        if (learnerDTO != null && learner != null) {
            if (learnerDTO.getNewName() != null)
                learner.setName(learnerDTO.getNewName());
            if (learnerDTO.getNewLastname() != null)
                learner.setLastname(learnerDTO.getNewLastname());
            return assignmentManagerDAO.updateLearner(learner);
        } else {
            throw new NullPointerException("LearnerDTO or Learner is null in " + getCurrentMethodName());
        }
    }
    @Override
    @Transactional
    public void deleteLearner(Learner learner) {
        if (learner != null)
            assignmentManagerDAO.deleteLearner(learner);
        else {
            throw new NullPointerException("Learner is null in " + getCurrentMethodName());
        }
    }

    @Override
    @Transactional
    public LearningGroupDTO getGroup(Learner learner) {
        if(learner != null) {
            if(learner.getLearningGroup() != null) {
                for (Learner lr : learner.getLearningGroup().getLearners())
                    lr.setGrades(assignmentManagerDAO.findAllGradesByLearner(lr));

                return LearningGroupDTO.from(learner.getLearningGroup());
            }
            throw new NullPointerException("Learning group is null in " + getCurrentMethodName());
        } else {
            throw new NullPointerException("Learner is null in " + getCurrentMethodName());
        }
    }

    @Override
    @Transactional
    public LearningGroup addGroup(LearningGroupDTO learningGroupDTO) {
        if(learningGroupDTO != null) {
            LearningGroup group = learningGroupDTO.toLearningGroup();
            group.getHeadmen().setRole(LearningRole.HEADMAN);
            group.getHeadmen().setLearningGroup(group);

            if (group.getHeadmen().getId() != null)
                assignmentManagerDAO.deleteGradesByLearner(group.getHeadmen());

            List<LearningGroup> allGroups = assignmentManagerDAO.getAllGroups();

            if (!allGroups.contains(group)) {
                group = assignmentManagerDAO.saveGroup(group);
                group.generateInviteCode();
                return assignmentManagerDAO.saveGroup(group);
            } else {
                throw new IllegalArgumentException("Group " + group.getName() + " has been already existing");
            }
        } else {
            throw new NullPointerException("Learning group DTO is null in " + getCurrentMethodName());
        }
    }

    @Override
    public LearningGroup getGroupById(Long id) {
        LearningGroup learningGroup = assignmentManagerDAO.findGroupById(id);
        if(learningGroup != null)
            return learningGroup;
        else
            throw new NullPointerException("Group with id = " + id + " not found");
    }

    @Override
    public LearningGroup getGroupByInviteCode(String inviteCode) {
        LearningGroup learningGroup = assignmentManagerDAO.findLearningGroupByInviteCode(inviteCode);
        if(learningGroup != null)
            return learningGroup;
        else
            throw new NullPointerException("Group with inviteCode = " + inviteCode + " not found");
    }

    @Override
    @Transactional
    public LearningGroup updateHeadmanOfGroup(LearningGroup group, LearnerDTO learnerDTO) {
        if(group != null && learnerDTO != null) {
            Learner oldHeadman = group.getHeadmen();
            Learner newHeadman = assignmentManagerDAO.findLearnersByNameAndLastname(learnerDTO.getName(), learnerDTO.getLastname());
            if(newHeadman != null && group.getLearners().contains(newHeadman)) {
                oldHeadman.setRole(LearningRole.STUDENT);
                newHeadman.setRole(LearningRole.HEADMAN);
                group.setHeadmen(newHeadman);

                assignmentManagerDAO.saveLearner(oldHeadman);
                assignmentManagerDAO.saveLearner(newHeadman);
                return assignmentManagerDAO.saveGroup(group);
            } else
                throw new IllegalArgumentException("Learner = " + newHeadman + " does not contains in group = " + group
                        + " in " + getCurrentMethodName());
        } else
            throw new NullPointerException("Group = " + group + ", learnerDto = " + learnerDTO
                    + " in " + getCurrentMethodName());
    }

    /**
        The learner must be included in new group
     */
    @Override
    @Transactional
    public Learner refreshLearnerGrades(Learner learner, LearningGroup newGroup) {
        if(learner != null && newGroup != null) {
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
        } else {
            throw new NullPointerException("Learner = " + learner +
                    " Learning group = " + newGroup + " in " + getCurrentMethodName());
        }
    }

    @Override
    public void processLearnerWithdrawal(Learner learner) {
        if(learner != null) {
            LearningGroup group = learner.getLearningGroup();

            if(group == null)
                return;

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
        } else {
            throw new NullPointerException("Learner = " + learner + " in " + getCurrentMethodName());
        }
    }
}

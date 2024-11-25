package org.groupscope.assignment_management.services.impl;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dao.repositories.GradeRepository;
import org.groupscope.assignment_management.dao.repositories.SubjectRepository;
import org.groupscope.assignment_management.dao.repositories.TaskRepository;
import org.groupscope.assignment_management.dto.TaskDTO;
import org.groupscope.assignment_management.entity.Subject;
import org.groupscope.assignment_management.entity.Task;
import org.groupscope.assignment_management.entity.grade.Grade;
import org.groupscope.assignment_management.entity.grade.GradeKey;
import org.groupscope.assignment_management.services.TaskService;
import org.groupscope.exceptions.DuplicateEntityException;
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
public class TaskServiceImpl implements TaskService {

  private final GradeRepository gradeRepository;

  private final SubjectRepository subjectRepository;

  private final TaskRepository taskRepository;


  // TODO when new task has added, subject duplicating
  //  P.S. was fixed by GroupScopeDAOImpl.removeDuplicates() function, but still not fixed in Hibernate response
  @Override
  @Transactional
  public Task addTask(
      @NotNull Long subjectId,
      @NotNull TaskDTO taskDTO
  ) {
    Subject subject = subjectRepository.findById(subjectId)
        .orElseThrow(() -> new EntityNotFoundException("Subject not found with id = " + subjectId));

    Task task = taskDTO.toTask();
    task.setSubject(subject);

    boolean isTaskExists = subject.getTasks().stream()
        .anyMatch(t -> t.getName().equals(task.getName())
            && t.getType().equals(task.getType()));

    if (!isTaskExists) {
      List<Grade> grades = subject.getGroup().getLearners()
          .stream()
          .map(learner -> {
            GradeKey gradeKey = new GradeKey(learner.getId(), task.getId());
            return new Grade(gradeKey, learner, task, false, 0);
          })
          .toList();

      task.setGrades(grades);

      taskRepository.save(task);
      gradeRepository.saveAll(grades);
      return task;
    } else {
      throw new DuplicateEntityException("Task: " + task + " has been already existing");
    }

  }

  @Override
  @Transactional
  public List<TaskDTO> getAllTasksOfSubject(
      @NotNull Long subjectId
  ) {
    Subject subject = subjectRepository.findById(subjectId)
        .orElseThrow(() -> new EntityNotFoundException("Subject not found with id = " + subjectId));

    return subject.getTasks()
        .stream()
        .map(TaskDTO::from)
        .toList();
  }

  @Override
  @Transactional
  public Task getTaskById(
      @NotNull Long taskId
  ) {
    return taskRepository.findById(taskId)
        .orElseThrow(() -> new EntityNotFoundException(Task.class, taskId));
  }

  @Override
  @Transactional
  public void updateTask(
      @NotNull Long taskId,
      @NotNull TaskDTO taskDTO
  ) {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new EntityNotFoundException(Task.class, taskId));

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
        throw new IllegalArgumentException("Deadline: " + taskDTO.getDeadline() + " is not valid");
      }
    }
    if (taskDTO.getMaxMark() != null) {
      if (taskDTO.isValidMaxMark()) {
        task.setMaxMark(taskDTO.getMaxMark());
      } else {
        throw new IllegalArgumentException("Max mark: " + taskDTO.getMaxMark() + " is not valid");
      }
    }

    taskRepository.save(task);
  }


  @Override
  @Transactional
  public void deleteTask(
      @NotNull Long taskId
  ) {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new EntityNotFoundException("Task not found with id = " + taskId));

    gradeRepository.deleteGradesByTask(task);
    taskRepository.deleteById(task.getId());
  }
}

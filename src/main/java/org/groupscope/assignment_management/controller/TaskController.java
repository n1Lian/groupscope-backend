package org.groupscope.assignment_management.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dto.TaskDTO;
import org.groupscope.assignment_management.services.TaskService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mykyta Liashko
 */
@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class TaskController {

  private final TaskService taskService;

  @GetMapping("/subject/{subjectId}/task/all")
  public List<TaskDTO> getAllTasksOfSubject(
      @PathVariable("subjectId") Long subjectId
  ) {
    return taskService.getAllTasksOfSubject(subjectId);
  }

  @PostMapping("/subject/{subjectId}/task/add")
  @PreAuthorize("hasAnyRole('HEADMAN', 'EDITOR')")
  public void addTask(
      @PathVariable("subjectId") Long subjectId,
      @RequestBody TaskDTO taskDTO
  ) {
    taskService.addTask(subjectId, taskDTO);
  }

  @PatchMapping("/task/{taskId}/patch")
  @PreAuthorize("hasAnyRole('HEADMAN', 'EDITOR')")
  public void patchTask(
      @PathVariable("taskId") Long taskId,
      @RequestBody TaskDTO taskDTO
  ) {
    taskService.updateTask(taskId, taskDTO);
  }

  @DeleteMapping("/task/{taskId}/delete")
  @PreAuthorize("hasAnyRole('HEADMAN', 'EDITOR')")
  public void deleteTask(
      @PathVariable("taskId") Long taskId
  ) {
    taskService.deleteTask(taskId);
  }

}

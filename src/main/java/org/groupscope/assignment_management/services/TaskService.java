package org.groupscope.assignment_management.services;

import org.groupscope.assignment_management.dto.TaskDTO;
import org.groupscope.assignment_management.entity.Task;

import java.util.List;

/**
 * @author Mykyta Liashko
 */
public interface TaskService {

    Task addTask(Long subjectId, TaskDTO taskDTO);

    List<TaskDTO> getAllTasksOfSubject(Long subjectId);

    Task getTaskById(Long id);

    void updateTask(Long taskId, TaskDTO taskDTO);

    void deleteTask(Long taskId);

}

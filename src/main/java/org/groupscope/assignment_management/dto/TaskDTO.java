package org.groupscope.assignment_management.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.groupscope.assignment_management.entity.Task;
import org.groupscope.assignment_management.entity.TaskType;
import org.groupscope.assignment_management.entity.grade.Grade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;

    private String name;

    // TODO remove this field
    private String newName;


    private TaskType type;

    private Integer maxMark;

    private Boolean completion;

    private Integer mark;

    private String info;

    private String deadline;

    public TaskDTO(String name, TaskType type, String info, String deadline, Integer maxMark) {
        this.name = name;
        this.type = type;
        this.info = info;
        this.deadline = deadline;
        this.maxMark = maxMark;
    }

    public TaskDTO(String name, TaskType type, Integer maxMark, Boolean completion, Integer mark, String info, String deadline) {
        this.name = name;
        this.type = type;
        this.maxMark = maxMark;
        this.completion = completion;
        this.mark = mark;
        this.info = info;
        this.deadline = deadline;
    }

    public static TaskDTO from(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setType(task.getType());
        dto.setInfo(task.getInfo());
        dto.setDeadline(task.getDeadline());
        dto.setMaxMark(task.getMaxMark());

        return dto;
    }

    public static TaskDTO from(Task task, Grade grade) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setType(task.getType());
        dto.setInfo(task.getInfo());
        dto.setDeadline(task.getDeadline());
        dto.setMaxMark(task.getMaxMark());

        dto.setMark(grade.getMark());
        dto.setCompletion(grade.getCompletion());

        return dto;
    }

    public Task toTask() {
        if(this.isValid()) {
            Task task = new Task();
            task.setId(this.id);
            task.setName(this.name);
            task.setType(this.type);
            task.setInfo(this.info);
            task.setDeadline(this.deadline);
            task.setMaxMark(this.maxMark);

            return task;
        } else
            throw new IllegalArgumentException("Wrong task type or date format in Task object");
    }

    @JsonIgnore
    public boolean isValid() {
        return this.isValidDeadline() && isValidMaxMark();
    }

    @JsonIgnore
    public boolean isValidDeadline() {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate.parse(this.deadline, dateFormatter);

            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @JsonIgnore
    public boolean isValidMaxMark() {
        return this.maxMark > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskDTO taskDTO = (TaskDTO) o;
        return Objects.equals(name, taskDTO.name) && type == taskDTO.type && Objects.equals(info, taskDTO.info) && Objects.equals(deadline, taskDTO.deadline) && Objects.equals(maxMark, taskDTO.maxMark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, info, deadline, maxMark);
    }
}

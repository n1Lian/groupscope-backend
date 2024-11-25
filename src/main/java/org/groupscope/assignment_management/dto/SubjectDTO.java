package org.groupscope.assignment_management.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.groupscope.assignment_management.entity.Subject;
import org.groupscope.assignment_management.entity.Task;
import org.groupscope.assignment_management.entity.grade.Grade;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class SubjectDTO {

    private Long id;

    private String name;

    private String brief;

    private List<TaskDTO> tasks;

    private String group;

    private Boolean isExam;

    public SubjectDTO(String name) {
        this.name = name;
    }

    public static SubjectDTO from(Subject subject) {
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setName(subject.getName());
        dto.setBrief(subject.getBrief());
        dto.group = subject.getGroup().getName();
        dto.isExam = subject.getIsExam();

        List<TaskDTO> taskDTOList = subject.getTasks().stream()
                .map(TaskDTO::from).collect(Collectors.toList());

        dto.setTasks(taskDTOList);
        return dto;
    }

    public static SubjectDTO from(Subject subject, List<Grade> learnerGrades) {
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getId());
        dto.setName(subject.getName());
        dto.group = subject.getGroup().getName();
        dto.isExam = subject.getIsExam();

        List<TaskDTO> taskDTOList = subject.getTasks().stream()
                .map(task -> {
                    Grade grade = learnerGrades.stream()
                            .filter(x -> x.getTask().equals(task))
                            .findFirst()
                            .orElse(new Grade());
                    return TaskDTO.from(task, grade);
                }).collect(Collectors.toList());

        dto.setTasks(taskDTOList);
        return dto;
    }

    public Subject toSubject() {
        Subject subject = new Subject(this.name);
        subject.setId(this.id);
        subject.setIsExam(this.isExam);
        subject.setBrief(this.brief);

        if(!CollectionUtils.isEmpty(this.tasks)) {
            List<Task> taskList = this.tasks.stream()
                    .map(TaskDTO::toTask)
                    .peek(learner -> learner.setSubject(subject))
                    .collect(Collectors.toList());

            subject.setTasks(taskList);
        }

        return subject;
    }
}

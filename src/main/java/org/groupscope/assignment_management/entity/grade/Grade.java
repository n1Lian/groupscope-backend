package org.groupscope.assignment_management.entity.grade;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.groupscope.assignment_management.entity.Learner;
import org.groupscope.assignment_management.entity.Task;

import jakarta.persistence.*;
import java.util.Objects;

/*
 * This class represents a grade for a specific task given to a learner.
 * It contains information such as the learner, the task, completion status, and the mark.
 */

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    // EmbeddedId annotation indicates that this entity has a composite primary key (GradeKey).
    @EmbeddedId
    GradeKey id;

    // Many-to-one relationship with the Learner entity. Each grade is associated with a learner.
    @ManyToOne
    @MapsId("learnerId")
    @JoinColumn(name = "learner_id")
    Learner learner;

    // Many-to-one relationship with the Task entity. Each grade is associated with a task.
    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "task_id")
    Task task;

    // Indicates if the task has been completed by the learner.
    @Column(name = "completion")
    private Boolean completion;

    // The grade/mark received by the learner for the task.
    @Column(name = "mark")
    private Integer mark;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade = (Grade) o;
        return Objects.equals(id, grade.id) && Objects.equals(learner, grade.learner) && Objects.equals(task, grade.task) && Objects.equals(completion, grade.completion) && Objects.equals(mark, grade.mark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, learner, task, completion, mark);
    }
}

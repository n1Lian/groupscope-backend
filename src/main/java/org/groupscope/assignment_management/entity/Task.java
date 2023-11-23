package org.groupscope.assignment_management.entity;

import lombok.Data;
import org.groupscope.assignment_management.entity.grade.Grade;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a task in the learning system.
 * Tasks are associated with a specific subject and can have multiple grades associated with them.
 */

@Data
@Entity
@Table(name = "tasks")
public class Task implements ObjectWithId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    // Represents the type of the task (e.g., practical, laboratory, test).
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TaskType type;

    @Column(name = "max_mark")
    private Integer maxMark;

    // Additional information or description about the task.
    @Column(name = "info", length = 255)
    private String info;

    // The deadline for completing the task.
    @Column(name = "deadline")
    private String deadline;

    // Many-to-one relationship with the Subject entity. Each task belongs to a subject.
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    // One-to-many relationship with the Grade entity. Each task can have multiple grades.
    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Grade> grades;

    public Task() {
        grades = new ArrayList<>();
    }

    public Task(String name, TaskType type, String info, String deadline, Integer maxMark) {
        this.name = name;
        this.type = type;
        this.info = info;
        this.deadline = deadline;
        this.maxMark = maxMark;
    }

    public Task(String name, TaskType type, String info, String deadline, Integer maxMark, Subject subject) {
        this.name = name;
        this.type = type;
        this.info = info;
        this.deadline = deadline;
        this.maxMark = maxMark;
        this.subject = subject;
    }

    @PrePersist
    public void setDefaultValues() {
        if (maxMark == null) {
            maxMark = 100;
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", subject=" + subject.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && type == task.type && Objects.equals(maxMark, task.maxMark) && Objects.equals(info, task.info) && Objects.equals(deadline, task.deadline) && Objects.equals(subject, task.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, maxMark, info, deadline, subject);
    }
}

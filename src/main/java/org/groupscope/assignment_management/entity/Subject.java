package org.groupscope.assignment_management.entity;


import lombok.Data;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a subject in the learning system.
 * Each subject can have multiple tasks associated with it and belongs to a specific learning group.
 */

@Data
@Entity
@Table(name = "subjects")
public class Subject implements ObjectWithId {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "is_exam")
    private Boolean isExam;

    // One-to-many relationship with the Task entity. Each subject can have multiple tasks.
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "subject_id")
    private List<Task> tasks;

    // Many-to-one relationship with the LearningGroup entity. Each subject belongs to a group.
    @ManyToOne
    @JoinColumn(name = "group_id")
    private LearningGroup group;

    public Subject() {
        tasks = new ArrayList<>();
    }

    public Subject(String name) {
        this.name = name;
        tasks = new ArrayList<>();
    }

    public Subject(String name, LearningGroup group) {
        this.name = name;
        this.group = group;
        tasks = new ArrayList<>();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(name, subject.name) &&
                Objects.equals(isExam, subject.isExam) &&
                Objects.equals(group, subject.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isExam, group);
    }
}

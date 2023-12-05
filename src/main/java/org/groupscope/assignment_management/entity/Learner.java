package org.groupscope.assignment_management.entity;


import lombok.Data;
import org.groupscope.assignment_management.entity.grade.Grade;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * It`s a main entity, which will use in our project.
 * This class represents a learner (student) in the system.
 */

@Data
@Entity
@Table(name = "learners")
public class Learner implements ObjectWithId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "lastname")
    private String lastname;

    // Learner role, now it`s can be Student or Headmen. The last can monitor student`s grades
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private LearningRole role;

    // Everyone lerner must belong to some group
    @ManyToOne
    @JoinColumn(name = "group_id")
    private LearningGroup learningGroup;

    // One-to-many relationship with the Grade entity. Each learner can have multiple grades.
    @OneToMany(mappedBy = "learner", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Grade> grades;

    public Learner() {
        this.grades = new ArrayList<>();
    }

    public Learner(String name, String lastname, LearningRole role, LearningGroup learningGroup) {
        this.name = name;
        this.lastname = lastname;
        this.role = role;
        this.learningGroup = learningGroup;
        this.grades = new ArrayList<>();
    }

    public void setGrades(List<Grade> grades) {
        if(grades != null)
            this.grades = grades;
    }

    @Override
    public String toString() {
        return "Learner{" +
                "name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", role='" + role + '\'' +
                ", group='" + learningGroup +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Learner learner = (Learner) o;
        return Objects.equals(name, learner.name) &&
                Objects.equals(lastname, learner.lastname) &&
                role == learner.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lastname, role, learningGroup);
    }
}

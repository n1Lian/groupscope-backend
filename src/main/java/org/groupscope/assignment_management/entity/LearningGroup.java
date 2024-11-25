package org.groupscope.assignment_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * This class allows to unite and manage our group of learners.
 * Represents a learning group that contains learners and subjects.
 */
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "groups")
public class LearningGroup implements ObjectWithId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String inviteCode;

    // Every group have a headman
    @OneToOne(cascade = CascadeType.MERGE, targetEntity = Learner.class)
    @JoinColumn(name = "headmen_id")
    private Learner headmen;

    // Every group has subjects that the headmen has added
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private List<Subject> subjects = new ArrayList<>();

    // List of learners in the group.
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, targetEntity = Learner.class)
    @JoinColumn(name = "group_id")
    private List<Learner> learners = new ArrayList<>();

    public LearningGroup() {
        generateInviteCode();
    }

    public LearningGroup(String groupName) {
        generateInviteCode();
        this.name = groupName;
    }

    // Generate a random invite code for the group.
    public void generateInviteCode(){
        if (this.inviteCode == null) {
            String uuid = UUID.randomUUID().toString();

            SecureRandom secureRandom = new SecureRandom(uuid.getBytes());
            this.inviteCode = new BigInteger(32, secureRandom).toString(32);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LearningGroup that = (LearningGroup) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

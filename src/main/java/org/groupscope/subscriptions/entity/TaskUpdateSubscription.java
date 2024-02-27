package org.groupscope.subscriptions.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task_update_subscription")
public class TaskUpdateSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private final SubscriptionType type = SubscriptionType.AUTO_TASK_UPDATE;

    @Column(name = "group_id", nullable = false)
    private Long learningGroupId;

    @ElementCollection
    @CollectionTable(name = "task_update_sub_subjects", joinColumns = @JoinColumn(name = "task_update_sub_id"))
    @Column(name = "subject_id")
    private List<Long> subjectIds;

    public TaskUpdateSubscription(Long learningGroupId, List<Long> subjectIds) {
        this.learningGroupId = learningGroupId;
        this.subjectIds = subjectIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskUpdateSubscription that = (TaskUpdateSubscription) o;
        return Objects.equals(learningGroupId, that.learningGroupId) && Objects.equals(subjectIds, that.subjectIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(learningGroupId, subjectIds);
    }

    @Override
    public String toString() {
        return "TaskUpdateSubscription{" +
                "id=" + id +
                ", learningGroupId=" + learningGroupId +
                ", subjectIds=" + subjectIds +
                '}';
    }
}

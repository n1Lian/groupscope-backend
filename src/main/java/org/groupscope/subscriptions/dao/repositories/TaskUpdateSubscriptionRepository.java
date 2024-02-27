package org.groupscope.subscriptions.dao.repositories;

import org.groupscope.subscriptions.entity.TaskUpdateSubscription;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TaskUpdateSubscriptionRepository extends CrudRepository<TaskUpdateSubscription, Long> {
    Optional<TaskUpdateSubscription> findByLearningGroupId(Long id);
}

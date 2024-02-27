package org.groupscope.subscriptions.dao;

import org.groupscope.subscriptions.entity.TaskUpdateSubscription;

public interface SubscriptionDAO {

    TaskUpdateSubscription save(TaskUpdateSubscription subscription);

    TaskUpdateSubscription findById(Long id);

    void delete(TaskUpdateSubscription subscription);

    void deleteById(Long id);

    Iterable<TaskUpdateSubscription> findAll();

    TaskUpdateSubscription findByLearningGroupId(Long id);
}

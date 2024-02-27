package org.groupscope.subscriptions.service;

import org.groupscope.security.entity.User;
import org.groupscope.subscriptions.entity.TaskUpdateSubscription;

import java.util.List;

public interface SubscriptionService {
    TaskUpdateSubscription subscribeTaskUpdating(User user, List<Long> subjectIds);

    void unsubscribeTaskUpdating(User user);

    TaskUpdateSubscription getTaskUpdateSubscription(User user);

    List<TaskUpdateSubscription> getAllTaskUpdateSubscriptions();
}

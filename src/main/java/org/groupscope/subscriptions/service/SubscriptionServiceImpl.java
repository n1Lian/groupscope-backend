package org.groupscope.subscriptions.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.groupscope.exceptions.EntityNotFoundException;
import org.groupscope.security.entity.User;
import org.groupscope.subscriptions.dao.SubscriptionDAO;
import org.groupscope.subscriptions.entity.TaskUpdateSubscription;
import org.groupscope.exceptions.StudentNotInGroupException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = PRIVATE)
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    SubscriptionDAO subscriptionDAO;

    @Override
    @Transactional
    public TaskUpdateSubscription subscribeTaskUpdating(User user, List<Long> subjectIds) {
        Objects.requireNonNull(user, "User is null");
        Objects.requireNonNull(user.getLearner(), "User's learner is null");
        Objects.requireNonNull(subjectIds, "Subject ids list is null");

        if (user.getLearner().getLearningGroup() == null) {
            throw new StudentNotInGroupException("User's learning group is null");
        }
        TaskUpdateSubscription subscription = new TaskUpdateSubscription(
                user.getLearner().getLearningGroup().getId(),
                subjectIds
        );
        return subscriptionDAO.save(subscription);
    }

    @Override
    @Transactional
    public void unsubscribeTaskUpdating(User user) {
        Objects.requireNonNull(user, "User is null");
        Objects.requireNonNull(user.getLearner(), "User's learner is null");

        if (user.getLearner().getLearningGroup() == null)
            throw new StudentNotInGroupException("User's learning group is null");

        LearningGroup group = user.getLearner().getLearningGroup();
        TaskUpdateSubscription subscription = subscriptionDAO.findByLearningGroupId(group.getId());

        if(subscription == null)
            throw new EntityNotFoundException("TaskUpdateSubscription not found for group with id " + group.getId());

        subscriptionDAO.delete(subscription);
    }

    @Override
    @Transactional
    public TaskUpdateSubscription getTaskUpdateSubscription(User user) {
        Objects.requireNonNull(user, "User is null");
        Objects.requireNonNull(user.getLearner(), "User's learner is null");

        if (user.getLearner().getLearningGroup() == null)
            throw new StudentNotInGroupException("User's learning group is null");

        LearningGroup group = user.getLearner().getLearningGroup();
        TaskUpdateSubscription subscription = subscriptionDAO.findByLearningGroupId(group.getId());

        if(subscription == null)
            throw new EntityNotFoundException("TaskUpdateSubscription not found for group with id " + group.getId());

        return subscription;
    }

    @Override
    @Transactional
    public List<TaskUpdateSubscription> getAllTaskUpdateSubscriptions() {
        return (List<TaskUpdateSubscription>) subscriptionDAO.findAll();
    }
}

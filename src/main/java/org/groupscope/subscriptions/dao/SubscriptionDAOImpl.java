package org.groupscope.subscriptions.dao;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.subscriptions.dao.repositories.TaskUpdateSubscriptionRepository;
import org.groupscope.subscriptions.entity.TaskUpdateSubscription;
import org.springframework.stereotype.Component;

import static lombok.AccessLevel.PRIVATE;
import static org.groupscope.util.ObjectUtil.isNull;

@Slf4j
@Component
@FieldDefaults(makeFinal = true, level = PRIVATE)
@RequiredArgsConstructor
public class SubscriptionDAOImpl implements SubscriptionDAO {

    TaskUpdateSubscriptionRepository taskUpdateSubscriptionRepository;

    @Override
    public TaskUpdateSubscription save(TaskUpdateSubscription subscription) {
        if(isNull(subscription.getId())) {
            log.error("TaskUpdateSubscription is null when trying to save it");
            return null;
        }

        return taskUpdateSubscriptionRepository.save(subscription);
    }

    @Override
    public TaskUpdateSubscription findById(Long id) {
        if(isNull(id)) {
            log.error("TaskUpdateSubscription id is null when trying to find it");
            return null;
        }

        return taskUpdateSubscriptionRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(TaskUpdateSubscription subscription) {
        if(isNull(subscription)) {
            log.error("TaskUpdateSubscription is null when trying to delete it");
            return;
        }

        taskUpdateSubscriptionRepository.delete(subscription);
    }

    @Override
    public void deleteById(Long id) {
        if(isNull(id)) {
            log.error("TaskUpdateSubscription id is null when trying to delete it");
            return;
        }

        taskUpdateSubscriptionRepository.deleteById(id);
    }

    @Override
    public Iterable<TaskUpdateSubscription> findAll() {
        return taskUpdateSubscriptionRepository.findAll();
    }

    @Override
    public TaskUpdateSubscription findByLearningGroupId(Long id) {
        if(isNull(id)) {
            log.error("Learning group id is null when trying to find TaskUpdateSubscription by it");
            return null;
        }

        return taskUpdateSubscriptionRepository.findByLearningGroupId(id).orElse(null);
    }
}

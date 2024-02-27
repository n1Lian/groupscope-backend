package org.groupscope.subscriptions.service;

import org.groupscope.assignment_management.entity.Learner;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.groupscope.exceptions.EntityNotFoundException;
import org.groupscope.security.entity.User;
import org.groupscope.subscriptions.dao.SubscriptionDAO;
import org.groupscope.subscriptions.entity.TaskUpdateSubscription;
import org.groupscope.exceptions.StudentNotInGroupException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupTaskSubscriptionServiceTest {

    @Mock
    private SubscriptionDAO subscriptionDAO;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Test
    void subscribeTaskUpdating_returnCorrectResponse() {
        User user = new User();
        user.setLearner(new Learner());
        LearningGroup learningGroup = new LearningGroup();
        learningGroup.setId(1L);
        user.getLearner().setLearningGroup(learningGroup);

        List<Long> subjectIds = List.of(1L, 2L, 3L);

        Mockito.doAnswer(invocation -> {
            return invocation.<TaskUpdateSubscription>getArgument(0);
        }).when(subscriptionDAO).save(any(TaskUpdateSubscription.class));

        TaskUpdateSubscription subscription = subscriptionService.subscribeTaskUpdating(user, subjectIds);

        assertNotNull(subscription);
        assertEquals(learningGroup.getId(), subscription.getLearningGroupId());
        assertIterableEquals(subjectIds, subscription.getSubjectIds());
    }

    @Test
    void subscribeTaskUpdating_withNullLearningGroup_throwStudentNotInGroupException() {
        User user = new User();
        user.setLearner(new Learner());

        List<Long> subjectIds = List.of(1L, 2L, 3L);

        assertThrows(StudentNotInGroupException.class, () -> subscriptionService.subscribeTaskUpdating(user, subjectIds));
    }

    @Test
    void subscribeTaskUpdating_withNullArgs_throwNullPointerException() {
        User user = new User();

        List<Long> subjectIds = List.of(1L, 2L, 3L);

        assertThrows(NullPointerException.class, () -> subscriptionService.subscribeTaskUpdating(null, subjectIds));
        assertThrows(NullPointerException.class, () -> subscriptionService.subscribeTaskUpdating(user, null));
        assertThrows(NullPointerException.class, () -> subscriptionService.subscribeTaskUpdating(null, null));
        assertThrows(NullPointerException.class, () -> subscriptionService.subscribeTaskUpdating(user, subjectIds));
    }

    @Test
    void unsubscribeTaskUpdating_checkCurrentProcessing() {
        User user = new User();
        user.setLearner(new Learner());
        LearningGroup learningGroup = new LearningGroup();
        learningGroup.setId(1L);
        user.getLearner().setLearningGroup(learningGroup);

        TaskUpdateSubscription subscription = new TaskUpdateSubscription(1L, List.of(1L, 2L, 3L));

        when(subscriptionDAO.findByLearningGroupId(learningGroup.getId())).thenReturn(subscription);

        subscriptionService.unsubscribeTaskUpdating(user);

        Mockito.verify(subscriptionDAO).delete(subscription);
    }

    @Test
    void unsubscribeTaskUpdating_withNullLearningGroup_throwStudentNotInGroupException() {
        User user = new User();
        user.setLearner(new Learner());

        assertThrows(StudentNotInGroupException.class, () -> subscriptionService.unsubscribeTaskUpdating(user));
    }

    @Test
    void unsubscribeTaskUpdating_withNullFoundedSub_throwEntityNotFoundException() {
        User user = new User();
        user.setLearner(new Learner());
        LearningGroup learningGroup = new LearningGroup();
        learningGroup.setId(1L);
        user.getLearner().setLearningGroup(learningGroup);

        when(subscriptionDAO.findByLearningGroupId(learningGroup.getId())).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> subscriptionService.unsubscribeTaskUpdating(user));
    }

    @Test
    void unsubscribeTaskUpdating_withNullArgs_throwNullPointerException() {
        User user = new User();

        assertThrows(NullPointerException.class, () -> subscriptionService.unsubscribeTaskUpdating(null));
        assertThrows(NullPointerException.class, () -> subscriptionService.unsubscribeTaskUpdating(user));
    }

    @Test
    void getTaskUpdateSubscription_returnCorrectResponse() {
        User user = new User();
        user.setLearner(new Learner());
        LearningGroup learningGroup = new LearningGroup();
        learningGroup.setId(1L);
        user.getLearner().setLearningGroup(learningGroup);

        TaskUpdateSubscription subscription = new TaskUpdateSubscription(1L, List.of(1L, 2L, 3L));

        when(subscriptionDAO.findByLearningGroupId(learningGroup.getId())).thenReturn(subscription);

        TaskUpdateSubscription result = subscriptionService.getTaskUpdateSubscription(user);

        assertNotNull(result);
        assertEquals(subscription, result);
    }

    @Test
    void getTaskUpdateSubscription_withNullLearningGroup_throwStudentNotInGroupException() {
        User user = new User();
        user.setLearner(new Learner());

        assertThrows(StudentNotInGroupException.class, () -> subscriptionService.getTaskUpdateSubscription(user));
    }

    @Test
    void getTaskUpdateSubscription_withNullFoundedSub_throwEntityNotFoundException() {
        User user = new User();
        user.setLearner(new Learner());
        LearningGroup learningGroup = new LearningGroup();
        learningGroup.setId(1L);
        user.getLearner().setLearningGroup(learningGroup);

        when(subscriptionDAO.findByLearningGroupId(learningGroup.getId())).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> subscriptionService.getTaskUpdateSubscription(user));
    }

    @Test
    void getAllTaskUpdateSubscriptions_returnCorrectResponse() {
        List<TaskUpdateSubscription> subscriptions = List.of(
                new TaskUpdateSubscription(1L, List.of(1L, 2L, 3L)),
                new TaskUpdateSubscription(2L, List.of(4L, 5L, 6L))
        );

        when(subscriptionDAO.findAll()).thenReturn(subscriptions);

        List<TaskUpdateSubscription> result = subscriptionService.getAllTaskUpdateSubscriptions();

        assertNotNull(result);
        assertIterableEquals(subscriptions, result);
    }
}

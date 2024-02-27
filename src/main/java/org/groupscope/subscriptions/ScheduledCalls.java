package org.groupscope.subscriptions;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dao.AssignmentManagerDAO;
import org.groupscope.schedule_nure.dao.ScheduleRedisDAO;
import org.groupscope.schedule_nure.dto.EventTypes;
import org.groupscope.schedule_nure.dto.NureEventDTO;
import org.groupscope.schedule_nure.service.ScheduleService;
import org.groupscope.subscriptions.entity.TaskUpdateSubscription;
import org.groupscope.subscriptions.entity.TaskUpdateSubscriptionDetails;
import org.groupscope.subscriptions.service.SubscriptionService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@FieldDefaults(makeFinal = true, level = PRIVATE)
@RequiredArgsConstructor
public class ScheduledCalls {

    static long SECONDS_IN_DAY = 24 * 60 * 60;

    SubscriptionService subscriptionService;

    ScheduleService scheduleService;

    ScheduleRedisDAO scheduleRedisDAO;

    AssignmentManagerDAO assignmentManagerDAO;

    TaskScheduler taskScheduler;

    @Scheduled(cron = "0 0 4 * * ?") // 4:00 AM every day
    private void checkSubscriptions() {
        List<TaskUpdateSubscription> subscriptions = subscriptionService.getAllTaskUpdateSubscriptions();
        List<TaskUpdateSubscriptionDetails> detailsList = new ArrayList<>();

        for(TaskUpdateSubscription subscription : subscriptions) {
            TaskUpdateSubscriptionDetails details = new TaskUpdateSubscriptionDetails();
            details.setLearningGroup(assignmentManagerDAO.findGroupById(subscription.getLearningGroupId()));
            details.setNureGroup(scheduleRedisDAO.getNureGroupByLinkId(details.getLearningGroup().getId()));

            for (Long subjectId : subscription.getSubjectIds()) {
                Long nureSubjectId = scheduleRedisDAO.getNureSubjectByLinkId(subjectId).getId();
                details.putSubject(subjectId, nureSubjectId);
            }

            detailsList.add(details);
        }

        for(TaskUpdateSubscriptionDetails details : detailsList) {
            List<NureEventDTO> events = scheduleService.getEvents(
                    details.getNureGroup().getId(),
                    EventTypes.GROUP,
                    Instant.now().getEpochSecond(),
                    Instant.now().plusSeconds(SECONDS_IN_DAY).getEpochSecond()
            );
            createScheduledNotifications(details, events);
        }
    }

    private void createScheduledNotifications(TaskUpdateSubscriptionDetails details, List<NureEventDTO> events) {
        for(NureEventDTO event : events) {
            if(details.getSubjects().containsKey(event.getSubject().getId())) {
                taskScheduler.schedule(
                        () -> {
                            log.info("Notification for event: " + event.getSubject().getTitle() + " and lessons type: " + event.getType().getType() + ".");
                        },
                        Instant.ofEpochSecond(event.getStartTime()).minusSeconds(5 * 60) // 5 minutes before the event
                );
            }
        }
    }

}

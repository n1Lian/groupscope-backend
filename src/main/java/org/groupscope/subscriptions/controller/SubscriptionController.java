package org.groupscope.subscriptions.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.security.entity.User;
import org.groupscope.subscriptions.entity.SubscriptionType;
import org.groupscope.subscriptions.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController("/api")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class SubscriptionController {

    SubscriptionService subscriptionService;

    private void logRequestMapping(User user, HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        log.info("{} {}\t{}", request.getMethod(), requestPath, user);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestParam("type") @NotNull Integer type,
                                       @RequestBody @NotNull @NotEmpty List<Long> subjectIds) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logRequestMapping(user, request);

        switch (SubscriptionType.fromId(type)) {
            case AUTO_TASK_UPDATE -> {
                return ResponseEntity.ok(
                        subscriptionService.subscribeTaskUpdating(user, subjectIds)
                );
            }
            case LESSON_START_NOTIFICATION, ASSIGNMENT_DEADLINE_NOTIFICATION -> {
                return ResponseEntity.noContent().build();
            }
            default -> {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    @GetMapping("/subscription")
    public ResponseEntity<?> getSubscription(@RequestParam("type") @NotNull Integer type) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logRequestMapping(user, request);

        switch (SubscriptionType.fromId(type)) {
            case AUTO_TASK_UPDATE -> {
                return ResponseEntity.ok(
                        subscriptionService.getTaskUpdateSubscription(user)
                );
            }
            case LESSON_START_NOTIFICATION, ASSIGNMENT_DEADLINE_NOTIFICATION -> {
                return ResponseEntity.noContent().build();
            }
            default -> {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@RequestParam("type") @NotNull Integer type) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logRequestMapping(user, request);

        switch (SubscriptionType.fromId(type)) {
            case AUTO_TASK_UPDATE -> {
                subscriptionService.unsubscribeTaskUpdating(user);
                return ResponseEntity.ok().build();
            }
            case LESSON_START_NOTIFICATION, ASSIGNMENT_DEADLINE_NOTIFICATION -> {
                return ResponseEntity.noContent().build();
            }
            default -> {
                return ResponseEntity.badRequest().build();
            }
        }
    }
}

package org.groupscope.subscriptions.entity;

public enum SubscriptionType {
    AUTO_TASK_UPDATE(1),
    LESSON_START_NOTIFICATION(2),
    ASSIGNMENT_DEADLINE_NOTIFICATION(3);

    private final int id;

    SubscriptionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static SubscriptionType fromId(int id) {
        return switch (id) {
            case 1 -> AUTO_TASK_UPDATE;
            case 2 -> LESSON_START_NOTIFICATION;
            case 3 -> ASSIGNMENT_DEADLINE_NOTIFICATION;
            default -> throw new IllegalArgumentException("Invalid id: " + id);
        };
    }
}

package org.groupscope.schedule_nure.dto;

public enum EventTypes {
    GROUP(1),
    TEACHER(2),
    AUDITORY(3),
    SUBJECT(4);

    private final int id;

    EventTypes(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return id;
    }
}

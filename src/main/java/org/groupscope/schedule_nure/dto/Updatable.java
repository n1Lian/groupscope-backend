package org.groupscope.schedule_nure.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.groupscope.assignment_management.entity.ObjectWithId;

import java.util.Date;

public abstract class Updatable implements ObjectWithId, Scheduled {

    protected Long id;

    @JsonIgnore
    protected String schedule;

    @JsonIgnore
    protected Date lastUpdated;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getSchedule() {
        return schedule;
    }

    @Override
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

package org.groupscope.subscriptions.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.groupscope.schedule_nure.dto.NureGroupDTO;

import java.util.HashMap;

@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateSubscriptionDetails {

    @Getter
    @Setter
    private LearningGroup learningGroup;

    @Getter
    @Setter
    private NureGroupDTO nureGroup;

    private HashMap<Long, Long> subjects = new HashMap<>(); // subjectId, nureSubjectId

    public void putSubject(Long subject, Long nureSubject) {
        this.subjects.put(subject, nureSubject);
    }

    public void putSubjects(HashMap<Long, Long> subjects) {
        this.subjects.putAll(subjects);
    }

    public Long getSubject(Long subject) {
        return this.subjects.get(subject);
    }

    public HashMap<Long, Long> getSubjects() {
        return new HashMap<>(this.subjects);
    }
}

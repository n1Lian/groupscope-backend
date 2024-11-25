package org.groupscope.exceptions;

import org.groupscope.assignment_management.entity.Learner;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StudentNotInGroupException extends RuntimeException {
    public StudentNotInGroupException() {
        super("Student is not a member of any group");
    }

    public StudentNotInGroupException(Learner learner, LearningGroup group) {
        super("Student " + learner + " is not a member of group " + group);
    }

    public StudentNotInGroupException(String message) {
        super(message);
    }

    public StudentNotInGroupException(String message, Throwable cause) {
        super(message, cause);
    }
}

package org.groupscope.assignment_management.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.groupscope.assignment_management.entity.grade.Grade;

// This class used to get grade changes from the client
// Then update database

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeDTO {

    private String subjectName;

    private String taskName;

    private Boolean completion;

    private Integer mark;

    public static GradeDTO from(Grade grade) {
        GradeDTO dto = new GradeDTO();
        dto.subjectName = grade.getTask().getSubject().toString();
        dto.taskName = grade.getTask().getName();
        dto.completion = grade.getCompletion();
        dto.mark = grade.getMark();
        return dto;
    }

    public Grade toGrade() {
        Grade grade = new Grade();
        grade.setCompletion(this.completion);
        grade.setMark(this.mark);
        return grade;
    }

    // TODO Maybe move to constructor?
    @JsonIgnore
    public boolean isValid() {
        return (mark >= 0 && mark <= 100) && subjectName != null && taskName != null;
    }
}
package org.groupscope.schedule_nure.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NureSubjectDTO {

    private Long id;

    private String title;

    private String brief;

    @JsonIgnore
    private String lastUpdated;

    public NureSubjectDTO(Long id, String title, String brief) {
        this.id = id;
        this.title = title;
        this.brief = brief;
    }
}

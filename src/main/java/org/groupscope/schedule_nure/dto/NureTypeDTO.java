package org.groupscope.schedule_nure.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NureTypeDTO {

    private Long id;

    private String fullName;

    private String shortName;

    private String type;

    @JsonIgnore
    private String lastUpdated;

    public NureTypeDTO(Long id, String fullName, String shortName, String type) {
        this.id = id;
        this.fullName = fullName;
        this.shortName = shortName;
        this.type = type;
    }
}

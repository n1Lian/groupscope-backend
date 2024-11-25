package org.groupscope.schedule_nure.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.HashMap;

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

    public static HashMap<Long, NureSubjectDTO> parse(String json) {
        HashMap<Long, NureSubjectDTO> groups = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(json);

            if(rootNode.has("subjects")) {
                JsonNode subjectsNode = rootNode.get("subjects");

                for (JsonNode subjectNode : subjectsNode) {
                    NureSubjectDTO subject = new NureSubjectDTO (
                            subjectNode.get("id").asLong(),
                            subjectNode.get("title").asText(),
                            subjectNode.get("brief").asText()
                    );

                    groups.put(subject.getId(), subject);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return groups;
    }
}

package org.groupscope.schedule_nure.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.groupscope.schedule_nure.entity.NureAuditory;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NureAuditoryDTO extends Updatable {

    private String name;

    private String corps;

    public NureAuditoryDTO(Long id, String name, String corps, String schedule) {
        this.id = id;
        this.name = name;
        this.corps = corps;
        this.schedule = schedule;
    }

    public static HashMap<Long, NureAuditoryDTO> parse(String json) {
        HashMap<Long, NureAuditoryDTO> auditories = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(json);

            if (rootNode.has("university") && rootNode.get("university").has("buildings")) {
                JsonNode buildingsNode = rootNode.get("university").get("buildings");

                for(JsonNode buildingNode : buildingsNode) {
                    if(buildingNode.has("auditories")) {
                        JsonNode auditoriesNode = buildingNode.get("auditories");

                        for(JsonNode auditoryNode : auditoriesNode) {
                            NureAuditoryDTO auditory = new NureAuditoryDTO(
                                    auditoryNode.get("id").asLong(),
                                    auditoryNode.get("short_name").asText(),
                                    buildingNode.get("full_name").asText(),
                                    ""
                            );

                            auditories.put(auditory.getId(), auditory);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return auditories;
    }

    public NureAuditory toEntity() {
        return new NureAuditory(id, name, corps);
    }

    public static List<NureAuditoryDTO> sortByCorps(List<NureAuditoryDTO> auditories) {
        auditories.sort(Comparator.comparing(NureAuditoryDTO::getCorps));
        return auditories;
    }
}

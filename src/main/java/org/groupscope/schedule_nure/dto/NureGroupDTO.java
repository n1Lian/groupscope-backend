package org.groupscope.schedule_nure.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.groupscope.schedule_nure.entity.NureGroup;

import java.io.IOException;
import java.util.*;

@Data
@NoArgsConstructor
public class NureGroupDTO extends Updatable {

    private String name;

    public NureGroupDTO(Long id, String name, String schedule) {
        this.id = id;
        this.name = name;
        this.schedule = schedule;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NureGroupDTO group = (NureGroupDTO) obj;
        return Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static HashMap<Long, NureGroupDTO> parse(String json) {
        HashMap<Long, NureGroupDTO> groups = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(json);

            if (rootNode.has("university") && rootNode.get("university").has("faculties")) {
                JsonNode facultiesNode = rootNode.get("university").get("faculties");

                for (JsonNode facultyNode : facultiesNode) {
                    if (facultyNode.has("directions")) {
                        for (JsonNode directionNode : facultyNode.get("directions")) {
                            JsonNode groupsNode = directionNode.get("groups");
                            addGroupsFromNode(groups, groupsNode);

                            if (directionNode.has("specialities")) {
                                for(JsonNode specialityNode : directionNode.get("specialities")) {
                                    addGroupsFromNode(groups, specialityNode.get("groups"));
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return groups;
    }

    public NureGroup toEntity() {
        return new NureGroup(this.id, this.name);
    }

    public static List<NureGroupDTO> sortByName(List<NureGroupDTO> groups) {
        List<NureGroupDTO> groupsTmp = new ArrayList<>(groups);
        groupsTmp.sort(Comparator.comparing(NureGroupDTO::getName));
        return groupsTmp;
    }

    private static void addGroupsFromNode(HashMap<Long, NureGroupDTO> groups, JsonNode node) {
        if (node != null) {
            for (JsonNode groupNode : node) {
                NureGroupDTO group = new NureGroupDTO(
                        groupNode.get("id").asLong(),
                        groupNode.get("name").asText(),
                        "");

                groups.put(group.getId(), group);
            }
        }
    }

}

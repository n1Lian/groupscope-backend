package org.groupscope.schedule_nure.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@NoArgsConstructor
public class NureEventDTO {

    private Integer numberPair;

    private NureSubjectDTO subject;

    private Long startTime;

    private Long endTime;

    private String auditory;

    private NureTypeDTO type;

    private List<NureTeacherDTO> teachers = new ArrayList<>();

    private List<NureGroupDTO> groups = new ArrayList<>();

    @JsonIgnore
    private String lastUpdated;

    public NureEventDTO(Integer numberPair, Long startTime, Long endTime, String auditory) {
        this.numberPair = numberPair;
        this.startTime = startTime;
        this.endTime = endTime;
        this.auditory = auditory;
    }

    public static List<NureEventDTO> parse(String json) {
        List<NureEventDTO> events = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(json);
            if(rootNode.has("events")) {
                JsonNode eventsNode = rootNode.get("events");
                for (JsonNode eventNode : eventsNode) {
                    NureEventDTO event = new NureEventDTO(
                            eventNode.get("number_pair").asInt(),
                            eventNode.get("start_time").asLong(),
                            eventNode.get("end_time").asLong(),
                            eventNode.get("auditory").asText()
                    );

                    event.setType(findTypeById(rootNode.get("types"), eventNode.get("type").asLong()));
                    event.setSubject(findSubjectById(rootNode.get("subjects"), eventNode.get("subject_id").asLong()));

                    List<NureTeacherDTO> teachers = new ArrayList<>();
                    for (JsonNode teacherNode : eventNode.get("teachers")) {
                        teachers.add(findTeacherById(rootNode.get("teachers"), teacherNode.asLong()));
                    }

                    event.setTeachers(teachers);

                    List<NureGroupDTO> groups = new ArrayList<>();
                    for (JsonNode groupNode : eventNode.get("groups")) {
                        groups.add(findGroupById(rootNode.get("groups"), groupNode.asLong()));
                    }

                    event.setGroups(groups);

                    events.add(event);
                }
            }

            events.sort(Comparator.comparing(NureEventDTO::getStartTime));
            return events;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return events;
    }

    private static NureTypeDTO findTypeById(JsonNode types, Long id) {
        for (JsonNode typeNode : types) {
            if(typeNode.get("id").asLong() == id) {
                return new NureTypeDTO(
                        typeNode.get("id").asLong(),
                        typeNode.get("full_name").asText(),
                        typeNode.get("short_name").asText(),
                        typeNode.get("type").asText(),
                        ""
                );
            }
        }
        return new NureTypeDTO();
    }

    private static NureSubjectDTO findSubjectById(JsonNode subjects, Long id) {
        for (JsonNode subjectNode : subjects) {
            if(subjectNode.get("id").asLong() == id) {
                return new NureSubjectDTO(
                        subjectNode.get("id").asLong(),
                        subjectNode.get("title").asText(),
                        subjectNode.get("brief").asText(),
                        ""
                );
            }
        }
        return new NureSubjectDTO();
    }

    private static NureTeacherDTO findTeacherById(JsonNode teachers, Long id) {
        for (JsonNode teacherNode : teachers) {
            if(teacherNode.get("id").asLong() == id) {
                return new NureTeacherDTO(
                        teacherNode.get("id").asLong(),
                        teacherNode.get("full_name").asText(),
                        teacherNode.get("short_name").asText()
                );
            }
        }
        return new NureTeacherDTO();
    }

    private static NureGroupDTO findGroupById(JsonNode groups, Long id) {
        for (JsonNode groupNode : groups) {
            if(groupNode.get("id").asLong() == id) {
                return new NureGroupDTO(
                        groupNode.get("id").asLong(),
                        groupNode.get("name").asText(),
                        ""
                );
            }
        }
        return new NureGroupDTO();
    }
}

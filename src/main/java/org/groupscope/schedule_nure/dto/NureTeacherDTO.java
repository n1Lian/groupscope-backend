package org.groupscope.schedule_nure.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.groupscope.schedule_nure.entity.NureTeacher;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Data
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "fullName", "shortName"})
@NoArgsConstructor
@AllArgsConstructor
public class NureTeacherDTO extends Updatable {

    private String fullName;

    private String shortName;

    public NureTeacherDTO(Long id, String fullName, String shortName) {
        this.id = id;
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public static HashMap<Long, NureTeacherDTO> parse(String json) {
        HashMap<Long, NureTeacherDTO> teachers = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(json);

            if (rootNode.has("university") && rootNode.get("university").has("faculties")) {
                JsonNode facultiesNode = rootNode.get("university").get("faculties");

                for (JsonNode facultyNode : facultiesNode) {
                    if (facultyNode.has("departments")) {
                        for (JsonNode departmentNode : facultyNode.get("departments")) {
                            addTeachersFromNode(teachers, departmentNode.get("teachers"));

                            if (departmentNode.has("departments")) {
                                for (JsonNode childDepartmentNode : departmentNode.get("departments")) {
                                    addTeachersFromNode(teachers, childDepartmentNode.get("teachers"));
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return teachers;
    }

    public NureTeacher toEntity() {
        return new NureTeacher(id, fullName, shortName);
    }

    private static void addTeachersFromNode(HashMap<Long, NureTeacherDTO> teachers, JsonNode node) {
        if (node != null) {
            for (JsonNode teacherNode : node) {
                NureTeacherDTO teacher = new NureTeacherDTO (
                        teacherNode.get("id").asLong(),
                        teacherNode.get("full_name").asText(),
                        teacherNode.get("short_name").asText()
                );
                teachers.put(teacher.getId(), teacher);
            }
        }
    }

    public static List<NureTeacherDTO> sortByName(List<NureTeacherDTO> teachers) {
        teachers.sort(Comparator.comparing(NureTeacherDTO::getShortName));
        return teachers;
    }
}

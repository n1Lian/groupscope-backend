package org.groupscope.schedule_nure.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class NureGroupDTOTest {
    @Test
    public void parse_returnCorrectMap() {
        String json = "{" +
                            "\"university\":" +
                                "{" +
                                    "\"short_name\": \"ХНУРЕ\"," +
                                    "\"full_name\": \"ХНУРЕ\"," +
                                    "\"faculties\":[" +
                                        "{" +
                                            "\"id\": 1," +
                                            "\"short_name\": \"Faculty 1\"," +
                                            "\"full_name\": \"Faculty Name\"," +
                                            "\"directions\": [" +
                                                "{" +
                                                    "\"id\": 2," +
                                                    "\"short_name\": \"Direction 1\"," +
                                                    "\"full_name\": \"Direction name 1\"," +
                                                    "\"specialities\": [" +
                                                        "{" +
                                                            "\"id\": 3," +
                                                            "\"short_name\": \"S 1\"," +
                                                            "\"full_name\": \"Speciality 1\"," +
                                                                "\"groups\": [" +
                                                                    "{" +
                                                                        "\"id\": 4," +
                                                                        "\"name\": \"Group 1\"" +
                                                                    "}," +
                                                                    "{" +
                                                                        "\"id\": 5," +
                                                                        "\"name\": \"Group 2\"" +
                                                                    "}" +
                                                                "]" +
                                                        "}" +
                                                    "]," +
                                                    "\"groups\": [" +
                                                        "{" +
                                                            "\"id\": 6," +
                                                            "\"name\": \"Group 3\"" +
                                                        "}," +
                                                        "{" +
                                                            "\"id\": 7," +
                                                            "\"name\": \"Group 4\"" +
                                                        "}" +
                                                    "]" +
                                                "}" +
                                            "]" +
                                        "}" +
                                    "]" +
                                "}" +
                        "}";

        HashMap<Long, NureGroupDTO> groupsResponse = NureGroupDTO.parse(json);

        assertFalse(groupsResponse.isEmpty());
        assertEquals(groupsResponse.get(4L).getName(), "Group 1");
        assertEquals(groupsResponse.get(5L).getName(), "Group 2");
        assertEquals(groupsResponse.get(6L).getName(), "Group 3");
        assertEquals(groupsResponse.get(7L).getName(), "Group 4");
    }

    @Test
    public void parse_WithNullArgs_throwsNullPointerException() {
        assertThrows(IllegalArgumentException.class, () -> NureGroupDTO.parse(null));
    }

    @Test
    public void parse_WithInvalidJson_returnEmptyMap() {
        String json = "{" +
                "\"university\":" +
                "{" +
                "\"short_name\": \"ХНУРЕ\"," +
                "\"full_name\": \"ХНУРЕ\"," +
                "\"faculties\":[" +
                "{" +
                "\"id\": 1," +
                "\"short_name\": \"Faculty 1\"," +
                "\"full_name\": \"Faculty Name\"," +
                "\"directions\": [";

        HashMap<Long, NureGroupDTO> groupsResponse = NureGroupDTO.parse(json);

        assertTrue(groupsResponse.isEmpty());
    }
}

package org.groupscope.schedule_nure.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class NureTeacherDTOTest {
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
                                        "\"departments\": [" +
                                            "{" +
                                                "\"id\": 2," +
                                                "\"short_name\": \"Department 1\"," +
                                                "\"full_name\": \"Direction name 1\"," +
                                                "\"departments\": [" +
                                                    "{" +
                                                        "\"id\": 3," +
                                                        "\"short_name\": \"D 1\"," +
                                                        "\"full_name\": \"Department 2\"," +
                                                        "\"teachers\": [" +
                                                            "{" +
                                                                "\"id\": 4," +
                                                                "\"full_name\": \"full_name 1\"," +
                                                                "\"short_name\": \"short_name 1\"" +
                                                            "}," +
                                                            "{" +
                                                                "\"id\": 5," +
                                                                "\"full_name\": \"full_name 2\"," +
                                                                "\"short_name\": \"short_name 2\"" +
                                                            "}" +
                                                        "]" +
                                                    "}" +
                                                "]," +
                                                "\"teachers\": [" +
                                                    "{" +
                                                        "\"id\": 6," +
                                                        "\"full_name\": \"full_name 3\"," +
                                                        "\"short_name\": \"short_name 3\"" +
                                                    "}," +
                                                    "{" +
                                                        "\"id\": 7," +
                                                        "\"full_name\": \"full_name 4\"," +
                                                        "\"short_name\": \"short_name 4\"" +
                                                    "}" +
                                                "]" +
                                            "}" +
                                        "]" +
                                    "}" +
                                "]" +
                            "}" +
                        "}";

        HashMap<Long, NureTeacherDTO> teachersResponse = NureTeacherDTO.parse(json);

        assertFalse(teachersResponse.isEmpty());
        assertEquals(teachersResponse.get(4L).getShortName(), "short_name 1");
        assertEquals(teachersResponse.get(4L).getFullName(), "full_name 1");
        assertEquals(teachersResponse.get(5L).getShortName(), "short_name 2");
        assertEquals(teachersResponse.get(5L).getFullName(), "full_name 2");
        assertEquals(teachersResponse.get(6L).getShortName(), "short_name 3");
        assertEquals(teachersResponse.get(6L).getFullName(), "full_name 3");
        assertEquals(teachersResponse.get(7L).getShortName(), "short_name 4");
        assertEquals(teachersResponse.get(7L).getFullName(), "full_name 4");

    }

    @Test
    public void parse_WithNullArgs_throwsNullPointerException() {
        assertThrows(IllegalArgumentException.class, () -> NureTeacherDTO.parse(null));
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
                "\"departments\": [";

        HashMap<Long, NureTeacherDTO> teachersResponse = NureTeacherDTO.parse(json);

        assertTrue(teachersResponse.isEmpty());
    }
}

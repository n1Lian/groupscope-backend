package org.groupscope.schedule_nure.dto;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class NureAuditoryDTOTest {
    @Test
    public void parse_returnCorrectMap() {
        String json = "{" +
                        "\"university\":" +
                            "{" +
                                "\"buildings\": [" +
                                    "{" +
                                        "\"full_name\": \"Building A\"," +
                                        "\"auditories\": [" +
                                            "{" +
                                                "\"id\": 1," +
                                                "\"short_name\": \"101\"" +
                                            "}," +
                                            "{" +
                                                "\"id\": 2," +
                                                "\"short_name\": \"102\"" +

                                            "}" +
                                        "]" +
                                    "}," +
                                    "{" +
                                        "\"full_name\": \"Building B\"," +
                                        "\"auditories\": [" +
                                            "{" +
                                                "\"id\": 3," +
                                                "\"short_name\": \"201\"" +
                                            "}," +
                                            "{" +
                                                "\"id\": 4," +
                                                "\"short_name\": \"202\"" +
                                            "}" +
                                        "]" +
                                    "}" +
                                "]" +
                            "}" +
                        "}";

        HashMap<Long, NureAuditoryDTO> auditoriesResponse = NureAuditoryDTO.parse(json);

        assertFalse(auditoriesResponse.isEmpty());
        assertEquals(auditoriesResponse.get(1L).getName(), "101");
        assertEquals(auditoriesResponse.get(1L).getCorps(), "Building A");
        assertEquals(auditoriesResponse.get(2L).getName(), "102");
        assertEquals(auditoriesResponse.get(2L).getCorps(), "Building A");
        assertEquals(auditoriesResponse.get(3L).getName(), "201");
        assertEquals(auditoriesResponse.get(3L).getCorps(), "Building B");
        assertEquals(auditoriesResponse.get(4L).getName(), "202");
        assertEquals(auditoriesResponse.get(4L).getCorps(), "Building B");

    }

    @Test
    public void parse_WithNullArgs_throwsNullPointerException() {
        assertThrows(IllegalArgumentException.class, () -> NureAuditoryDTO.parse(null));
    }

    @Test
    public void parse_WithInvalidJson_returnEmptyMap() {
        String json = "{" +
                "\"university\":" +
                "{" +
                "\"buildings\": [" +
                "{" +
                "\"full_name\": \"Building A\"," +
                "\"auditories\": [";

        HashMap<Long, NureAuditoryDTO> auditoriesResponse = NureAuditoryDTO.parse(json);

        assertTrue(auditoriesResponse.isEmpty());
    }
}

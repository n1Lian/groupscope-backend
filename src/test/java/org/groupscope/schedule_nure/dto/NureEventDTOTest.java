package org.groupscope.schedule_nure.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class NureEventDTOTest {

    @Test
    public void parse_returnCorrectMap() {
        String json = "{" +
                "   \"time-zone\":\"Europe/Kiev\"," +
                "   \"events\":[" +
                "      {" +
                "         \"subject_id\":1," +
                "         \"start_time\":1707815700," +
                "         \"end_time\":1707821400," +
                "         \"type\":0," +
                "         \"number_pair\":3," +
                "         \"auditory\":\"ФІЛІЯ\"," +
                "         \"teachers\":[" +
                "            1" +
                "         ]," +
                "         \"groups\":[" +
                "            1" +
                "         ]" +
                "      }" +
                "   ]," +
                "   \"groups\":[" +
                "      {" +
                "         \"id\":1," +
                "         \"name\":\"Group 1\"" +
                "      }" +
                "   ]," +
                "   \"teachers\":[" +
                "      {" +
                "         \"id\":1," +
                "         \"full_name\":\"Teacher 1\"," +
                "         \"short_name\":\"T 1\"" +
                "      }" +
                "   ]," +
                "   \"subjects\":[" +
                "      {" +
                "         \"id\":1," +
                "         \"brief\":\"S 1\"," +
                "         \"title\":\"Subject 1\"" +
                "      }" +
                "   ]," +
                "   \"types\":[" +
                "      {" +
                "         \"id\":0," +
                "         \"short_name\":\"Лк\"," +
                "         \"full_name\":\"Лекція\"," +
                "         \"id_base\":0," +
                "         \"type\":\"lecture\"" +
                "      }" +
                "   ]" +
                "}";

        List<NureEventDTO> eventsResponse = NureEventDTO.parse(json);

        assertNotNull(eventsResponse);
        assertFalse(eventsResponse.isEmpty());

        assertNotNull(eventsResponse.get(0));
        NureEventDTO eventResponse = eventsResponse.get(0);


        assertNotNull(eventResponse.getGroups());
        List<NureGroupDTO> groupsResponse = eventResponse.getGroups();
        assertTrue(groupsResponse.get(0).getName() != null &&
                groupsResponse.get(0).getName().length() > 0);

        assertNotNull(eventResponse.getTeachers());
        List<NureTeacherDTO> teachersResponse = eventResponse.getTeachers();
        assertTrue(teachersResponse.get(0).getFullName() != null &&
                teachersResponse.get(0).getFullName().length() > 0);

        assertTrue(eventResponse.getType().getFullName() != null &&
                eventResponse.getType().getFullName().length() > 0);

        assertTrue(eventResponse.getSubject().getTitle() != null &&
                eventResponse.getSubject().getTitle().length() > 0);

        assertTrue(eventResponse.getAuditory() != null &&
                eventResponse.getAuditory().length() > 0);

        assertTrue(eventResponse.getNumberPair() != null &&
                eventResponse.getStartTime() != null &&
                eventResponse.getEndTime() != null);
    }

    @Test
    public void parse_WithNullArgs_throwsNullPointerException() {
        assertThrows(IllegalArgumentException.class, () -> NureEventDTO.parse(null));
    }

    @Test
    public void parse_WithInvalidJson_returnEmptyMap() {
        String json = "{" +
                "   \"time-zone\":\"Europe/Kiev\"," +
                "   \"events\":[" +
                "      {" +
                "         \"subject_id\":1," +
                "         \"start_time\":1707815700," +
                "         \"end_time\":1707821400," +
                "         \"type\":0," +
                "         \"number_pair\":3," +
                "         \"auditory\":\"ФІЛІЯ\"," +
                "         \"teachers\":[" +
                "            1" +
                "         ]," +
                "         \"groups\":[" +
                "            1" +
                "         ]";

        List<NureEventDTO> eventsResponse = NureEventDTO.parse(json);

        assertTrue(eventsResponse.isEmpty());
    }

}

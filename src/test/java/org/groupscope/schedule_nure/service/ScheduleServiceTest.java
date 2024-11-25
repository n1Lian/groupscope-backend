package org.groupscope.schedule_nure.service;

import org.groupscope.schedule_nure.dao.ScheduleRedisDAO;
import org.groupscope.schedule_nure.dao.ScheduleNureDAO;
import org.groupscope.schedule_nure.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {

    @Mock
    private ScheduleNureDAO scheduleNureDAO;

    @Mock
    private ScheduleRedisDAO scheduleRedisDAO;

    private ScheduleService scheduleService;



    @BeforeEach
    void setUp() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        scheduleService = new ScheduleService(restTemplateBuilder, scheduleNureDAO, scheduleRedisDAO);
    }

    @Test
    void getGroupsMap() {
        HashMap<Long, NureGroupDTO> response = scheduleService.getGroupsMap();

        assertNotNull(response);
        assertFalse(response.isEmpty());

        verify(scheduleRedisDAO, atLeastOnce()).saveGroupsMap(any(HashMap.class));
    }

    @Test
    void getTeachersMap() {
        HashMap<Long, NureTeacherDTO> response = scheduleService.getTeachersMap();

        assertNotNull(response);
        assertFalse(response.isEmpty());

        verify(scheduleRedisDAO, atLeastOnce()).saveTeachersMap(any(HashMap.class));
    }

    @Test
    void getAuditoriesMap() {
        HashMap<Long, NureAuditoryDTO> response = scheduleService.getAuditoriesMap();

        assertNotNull(response);
        assertFalse(response.isEmpty());

        verify(scheduleRedisDAO, atLeastOnce()).saveAuditoriesMap(any(HashMap.class));
    }

    @Test
    void getEvents() {

        NureGroupDTO group = scheduleService.getGroupsMap().get(9556687L);

        when(scheduleRedisDAO.getGroupById(9556687L))
                .thenReturn(group);

        List<NureEventDTO> response = scheduleService.getEvents(
                9556687L, EventTypes.GROUP, 1707815700, 1715962500
        );

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
}

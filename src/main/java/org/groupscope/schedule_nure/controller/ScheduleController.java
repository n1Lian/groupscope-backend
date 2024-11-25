package org.groupscope.schedule_nure.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.schedule_nure.dto.*;
import org.groupscope.schedule_nure.service.ScheduleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ScheduleController {

    ScheduleService scheduleService;

    @GetMapping("/groups")
    public List<NureGroupDTO> getGroupsFromNure() {
        List<NureGroupDTO> groupDTOS = scheduleService.getGroupsMap().values().stream().toList();
        return NureGroupDTO.sortByName(groupDTOS);
    }

    @GetMapping("/teachers")
    public List<NureTeacherDTO> getTeachersFromNure() {
        return scheduleService.getTeachersMap().values().stream().toList();
    }

    @GetMapping("/aud")
    public List<NureAuditoryDTO> getAuditories() {
        return scheduleService.getAuditoriesMap().values().stream().toList();
    }

    @GetMapping("/subjects")
    public List<NureSubjectDTO> getSubjects(@RequestParam(name = "id") long id) {
        return scheduleService.getSubjectsByNureGroupId(id);
    }

    @GetMapping("/sch")
    public List<NureEventDTO> getEvents(@RequestParam(name = "id") int id,
                                        @RequestParam(name = "type") EventTypes type) {
        return scheduleService.getEvents(id, type);
    }

    @GetMapping("/schedule")
    public List<NureEventDTO> getEventsInInterval(@RequestParam(name = "id") int id,
                                        @RequestParam(name = "type") EventTypes type,
                                        @RequestParam(name = "startTime") long startTime,
                                        @RequestParam(name = "endTime") long endTime) {
        return scheduleService.getEvents(id, type, startTime, endTime);
    }

}

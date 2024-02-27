package org.groupscope.schedule_nure.dao;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.schedule_nure.dto.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ScheduleRedisDAO {

    RedisTemplate<String, HashMap<Long, Object>> redisTemplate;

    RedisTemplate<String, HashMap<Long, Long>> linksRedisTemplate;

    RedisTemplate<String, HashMap<Long, List<NureSubjectDTO>>> linksBetweenNureGroupAndSubjects;


    private static Long getLongValue(Map.Entry<Object, Object> entry) {
        if (entry.getKey() instanceof Integer) {
            return ((Integer) entry.getKey()).longValue();
        } else {
            return (Long) entry.getKey();
        }
    }

    public HashMap<Long, NureGroupDTO> getGroupsMap() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(EventTypes.GROUP.name());
        return entries.entrySet().stream()
                .collect(Collectors.toMap(
                        ScheduleRedisDAO::getLongValue,
                        entry -> (NureGroupDTO) entry.getValue(), // Cast value to NureGroupDTO
                        (a, b) -> a, // Merge function
                        HashMap::new // Supplier
                ));
    }


    public NureGroupDTO getGroupById(Long id) {
        return (NureGroupDTO) redisTemplate.opsForHash().get(EventTypes.GROUP.name(), id);
    }

    public void saveGroupsMap(HashMap<Long, NureGroupDTO> map) {
        for(Long id : map.keySet()) {
            redisTemplate.opsForHash().put(EventTypes.GROUP.name(), id, map.get(id));
        }
    }


    public HashMap<Long, NureTeacherDTO> getTeachersMap() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(EventTypes.TEACHER.name());
        return entries.entrySet().stream()
                .collect(Collectors.toMap(
                        ScheduleRedisDAO::getLongValue,
                        entry -> (NureTeacherDTO) entry.getValue(), // Cast value to NureGroupDTO
                        (a, b) -> a, // Merge function
                        HashMap::new // Supplier
                ));
    }

    public NureTeacherDTO getTeacherById(Long id) {
        return (NureTeacherDTO) redisTemplate.opsForHash().get(EventTypes.TEACHER.name(), id);
    }

    public void saveTeachersMap(HashMap<Long, NureTeacherDTO> map) {
        for(Long id : map.keySet()) {
            redisTemplate.opsForHash().put(EventTypes.TEACHER.name(), id, map.get(id));
        }
    }


    public HashMap<Long, NureAuditoryDTO> getAuditoriesMap() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(EventTypes.AUDITORY.name());
        return entries.entrySet().stream()
                .collect(Collectors.toMap(
                        ScheduleRedisDAO::getLongValue,
                        entry -> (NureAuditoryDTO) entry.getValue(), // Cast value to NureGroupDTO
                        (a, b) -> a, // Merge function
                        HashMap::new // Supplier
                ));
    }

    public NureAuditoryDTO getAuditoryById(Long id) {
        return (NureAuditoryDTO) redisTemplate.opsForHash().get(EventTypes.AUDITORY.name(), id);
    }

    public void saveAuditoriesMap(HashMap<Long, NureAuditoryDTO> map) {
        for(Long id : map.keySet()) {
            redisTemplate.opsForHash().put(EventTypes.AUDITORY.name(), id, map.get(id));
        }
    }



    public NureGroupDTO getNureGroupByLinkId(Long groupId) {
        Long nureGroupId = ((Integer) linksRedisTemplate.opsForHash().get("groups-links", groupId)).longValue();
        return nureGroupId == null ? null : (NureGroupDTO) redisTemplate.opsForHash().get(EventTypes.GROUP.name(), nureGroupId);
    }

    public HashMap<Long, Long> getGroupsLinks() {
        Map<Object, Object> entries = linksRedisTemplate.opsForHash().entries("groups-links");
        return entries.entrySet().stream()
                .collect(Collectors.toMap(
                        ScheduleRedisDAO::getLongValue,
                        ScheduleRedisDAO::getLongValue,
                        (a, b) -> a, // Merge function
                        HashMap::new // Supplier
                ));
    }

    public void saveNureGroupByLinkId(Long groupId, Long nureGroupId) {
        linksRedisTemplate.opsForHash().put("groups-links", groupId, nureGroupId);
    }



    public NureSubjectDTO getNureSubjectByLinkId(Long subjectId) {
        Long nureSubjectId = ((Integer) linksRedisTemplate.opsForHash().get("subjects-links", subjectId)).longValue();
        return nureSubjectId == null ? null : (NureSubjectDTO) redisTemplate.opsForHash().get(EventTypes.SUBJECT.name(), nureSubjectId);
    }

    public void saveNureSubjectByLinkId(Long subjectId, Long nureSubjectId) {
        linksRedisTemplate.opsForHash().put("subjects-links", subjectId, nureSubjectId);
    }



    public void addLinkBetweenNureGroupAndSubjects(Long nureGroupId, List<NureSubjectDTO> subjects) {
        linksBetweenNureGroupAndSubjects.opsForHash().put("groups-subjects", nureGroupId, subjects);
    }

    public List<NureSubjectDTO> getSubjectsByNureGroupId(Long nureGroupId) {
        Set<Long> keys = linksBetweenNureGroupAndSubjects.opsForHash().keys("groups-subjects").stream()
                .map(key -> ((Integer) key).longValue())
                .collect(Collectors.toSet());

        if (keys.contains(nureGroupId)) {
            return (List<NureSubjectDTO>) linksBetweenNureGroupAndSubjects.opsForHash().get("groups-subjects", nureGroupId);
        }

        return null;
    }
}

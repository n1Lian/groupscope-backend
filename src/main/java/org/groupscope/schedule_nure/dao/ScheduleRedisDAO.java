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
import java.util.stream.Collectors;

@Slf4j
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ScheduleRedisDAO {

    RedisTemplate<String, HashMap<Long, Object>> redisTemplate;

    RedisTemplate<String, HashMap<Long, Long>> linksRedisTemplate;

    RedisTemplate<String, HashMap<Long, List<NureSubjectDTO>>> linksBetweenNureGroupAndSubjects;


    public HashMap<Long, NureGroupDTO> getGroupsMap() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(EventTypes.GROUP.name());
        return entries.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> {
                            if (entry.getKey() instanceof Integer) {
                                return ((Integer) entry.getKey()).longValue(); // Convert Integer key to Long
                            } else {
                                return (Long) entry.getKey(); // Cast key to Long
                            }
                        },
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
                        entry -> {
                            if (entry.getKey() instanceof Integer) {
                                return ((Integer) entry.getKey()).longValue(); // Convert Integer key to Long
                            } else {
                                return (Long) entry.getKey(); // Cast key to Long
                            }
                        },
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
                        entry -> {
                            if (entry.getKey() instanceof Integer) {
                                return ((Integer) entry.getKey()).longValue(); // Convert Integer key to Long
                            } else {
                                return (Long) entry.getKey(); // Cast key to Long
                            }
                        },
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
        Long nureGroupId = linksRedisTemplate.opsForValue().get("groups").get(groupId);
        return (NureGroupDTO) redisTemplate.opsForValue().get(EventTypes.GROUP).get(nureGroupId);
    }

    public void saveNureGroupByLinkId(Long groupId, Long nureGroupId) {
        linksRedisTemplate.opsForValue().get("groups").put(groupId, nureGroupId);
    }



    public NureSubjectDTO getNureSubjectByLinkId(Long subjectId) {
        Long nureSubjectId = linksRedisTemplate.opsForValue().get("subjects").get(subjectId);
        return (NureSubjectDTO) redisTemplate.opsForValue().get(EventTypes.SUBJECT).get(nureSubjectId);
    }


    public void saveNureSubjectByLinkId(Long subjectId, Long nureSubjectId) {
        linksRedisTemplate.opsForValue().get("subjects").put(subjectId, nureSubjectId);
    }



    public void addLinkBetweenNureGroupAndSubjects(Long nureGroupId, List<NureSubjectDTO> subjects) {
        linksBetweenNureGroupAndSubjects.opsForHash().put("groups_subjects", nureGroupId, subjects);
    }

    public List<NureSubjectDTO> getSubjectsByNureGroupId(Long nureGroupId) {
        HashMap<Long, List<NureSubjectDTO>> links = linksBetweenNureGroupAndSubjects.opsForValue().get("nureGroupSubjects");

        String groupKey = nureGroupId.toString();

        if (links != null && links.containsKey(groupKey)) {
            return links.get(groupKey);
        }

        return null;
    }

}

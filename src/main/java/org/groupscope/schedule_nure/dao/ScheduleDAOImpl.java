package org.groupscope.schedule_nure.dao;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.groupscope.schedule_nure.dto.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ScheduleDAOImpl implements ScheduleDAO {

    RedisTemplate<String, HashMap<Long, ?>> redisTemplate;

    @Override
    public HashMap<Long, NureGroupDTO> getGroupsMap() {
        return (HashMap<Long, NureGroupDTO>) redisTemplate.opsForValue().get(EventTypes.GROUP);
    }

    @Override
    public NureGroupDTO getGroupById(Long id) {
        return (NureGroupDTO) redisTemplate.opsForValue().get(EventTypes.GROUP).get(id);
    }

    @Override
    public void saveGroupsMap(HashMap<Long, NureGroupDTO> map) {
        redisTemplate.opsForValue().set(EventTypes.GROUP.name(), map);
    }

    @Override
    public HashMap<Long, NureTeacherDTO> getTeachersMap() {
        return (HashMap<Long, NureTeacherDTO>) redisTemplate.opsForValue().get(EventTypes.TEACHER);
    }

    @Override
    public NureTeacherDTO getTeacherById(Long id) {
        return (NureTeacherDTO) redisTemplate.opsForValue().get(EventTypes.TEACHER).get(id);
    }

    @Override
    public void saveTeachersMap(HashMap<Long, NureTeacherDTO> map) {
        redisTemplate.opsForValue().set(EventTypes.TEACHER.name(), map);
    }

    @Override
    public HashMap<Long, NureAuditoryDTO> getAuditoriesMap() {
        return (HashMap<Long, NureAuditoryDTO>) redisTemplate.opsForValue().get(EventTypes.AUDITORY);
    }

    @Override
    public NureAuditoryDTO getAuditoryById(Long id) {
        return (NureAuditoryDTO) redisTemplate.opsForValue().get(EventTypes.AUDITORY).get(id);
    }

    @Override
    public void saveAuditoriesMap(HashMap<Long, NureAuditoryDTO> map) {
        redisTemplate.opsForValue().set(EventTypes.AUDITORY.name(), map);
    }
}

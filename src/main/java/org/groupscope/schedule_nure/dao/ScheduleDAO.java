package org.groupscope.schedule_nure.dao;

import org.groupscope.schedule_nure.dto.NureAuditoryDTO;
import org.groupscope.schedule_nure.dto.NureGroupDTO;
import org.groupscope.schedule_nure.dto.NureTeacherDTO;

import java.util.HashMap;

public interface ScheduleDAO {

    HashMap<Long, NureGroupDTO> getGroupsMap();

    NureGroupDTO getGroupById(Long id);

    void saveGroupsMap(HashMap<Long, NureGroupDTO> map);


    HashMap<Long, NureTeacherDTO> getTeachersMap();

    NureTeacherDTO getTeacherById(Long id);

    void saveTeachersMap(HashMap<Long, NureTeacherDTO> map);


    HashMap<Long, NureAuditoryDTO> getAuditoriesMap();

    NureAuditoryDTO getAuditoryById(Long id);

    void saveAuditoriesMap(HashMap<Long, NureAuditoryDTO> map);

}

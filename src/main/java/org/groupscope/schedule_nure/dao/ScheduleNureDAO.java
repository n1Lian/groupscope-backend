package org.groupscope.schedule_nure.dao;

import org.groupscope.schedule_nure.entity.NureAuditory;
import org.groupscope.schedule_nure.entity.NureGroup;
import org.groupscope.schedule_nure.entity.NureTeacher;

import java.util.List;

public interface ScheduleNureDAO {

    NureGroup saveNureGroup(NureGroup nureGroup);

    List<NureGroup> saveNureGroups(List<NureGroup> nureGroups);

    NureGroup updateNureGroup(NureGroup nureGroup);

    NureGroup getNureGroup(Long id);

    List<NureGroup> getAllNureGroups();

    void deleteNureGroup(Long id);


    NureTeacher saveNureTeacher(NureTeacher nureTeacher);

    List<NureTeacher> saveNureTeachers(List<NureTeacher> nureTeachers);

    NureTeacher updateNureTeacher(NureTeacher nureTeacher);

    NureTeacher getNureTeacher(Long id);

    List<NureTeacher> getAllNureTeachers();

    void deleteNureTeacher(Long id);


    NureAuditory saveNureAuditory(NureAuditory nureAuditory);

    List<NureAuditory> saveNureAuditories(List<NureAuditory> nureAuditories);

    NureAuditory updateNureAuditory(NureAuditory nureAuditory);

    NureAuditory getNureAuditory(Long id);

    List<NureAuditory> getAllNureAuditories();

    void deleteNureAuditory(Long id);
}

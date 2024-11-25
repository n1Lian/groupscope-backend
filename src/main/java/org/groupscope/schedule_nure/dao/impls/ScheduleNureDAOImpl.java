package org.groupscope.schedule_nure.dao.impls;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.schedule_nure.dao.ScheduleNureDAO;
import org.groupscope.schedule_nure.dao.repositories.NureAuditoryRepository;
import org.groupscope.schedule_nure.dao.repositories.NureGroupRepository;
import org.groupscope.schedule_nure.dao.repositories.NureTeacherRepository;
import org.groupscope.schedule_nure.entity.NureAuditory;
import org.groupscope.schedule_nure.entity.NureGroup;
import org.groupscope.schedule_nure.entity.NureTeacher;
import org.groupscope.util.ObjectUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ScheduleNureDAOImpl implements ScheduleNureDAO {

    NureGroupRepository nureGroupRepository;

    NureTeacherRepository nureTeacherRepository;

    NureAuditoryRepository nureAuditoryRepository;

    @Override
    public NureGroup saveNureGroup(NureGroup nureGroup) {
        if(ObjectUtil.isNull(nureGroup)) {
            log.error("NureGroup is null when trying to save it");
            return null;
        }

        return nureGroupRepository.save(nureGroup);
    }

    @Override
    public List<NureGroup> saveNureGroups(List<NureGroup> nureGroups) {
        if (ObjectUtil.isNull(nureGroups)) {
            log.error("NureGroups is null when trying to save it");
            return null;
        }

        nureGroupRepository.deleteAll();
        return (List<NureGroup>) nureGroupRepository.saveAll(nureGroups);
    }

    @Override
    public NureGroup updateNureGroup(NureGroup nureGroup) {
        if(ObjectUtil.isNull(nureGroup)) {
            log.error("NureGroup is null when trying to update it");
            return null;
        }

        return nureGroupRepository.save(nureGroup);
    }

    @Override
    public NureGroup getNureGroup(Long id) {
        if(ObjectUtil.isNull(id)) {
            log.error("NureGroup id is null when trying to get it");
            return null;
        }

        return nureGroupRepository.findById(id).orElse(null);
    }

    @Override
    public List<NureGroup> getAllNureGroups() {
        return (List<NureGroup>) nureGroupRepository.findAll();
    }

    @Override
    public void deleteNureGroup(Long id) {
        if(ObjectUtil.isNull(id)) {
            log.error("NureGroup id is null when trying to delete it");
            return;
        }

        nureGroupRepository.deleteById(id);
    }

    @Override
    public NureTeacher saveNureTeacher(NureTeacher nureTeacher) {
        if(ObjectUtil.isNull(nureTeacher)) {
            log.error("NureTeacher is null when trying to save it");
            return null;
        }

        return nureTeacherRepository.save(nureTeacher);
    }

    @Override
    public List<NureTeacher> saveNureTeachers(List<NureTeacher> nureTeachers) {
        if (ObjectUtil.isNull(nureTeachers)) {
            log.error("NureTeachers is null when trying to save it");
            return null;
        }

        nureTeacherRepository.deleteAll();
        return (List<NureTeacher>) nureTeacherRepository.saveAll(nureTeachers);
    }

    @Override
    public NureTeacher updateNureTeacher(NureTeacher nureTeacher) {
        if(ObjectUtil.isNull(nureTeacher)) {
            log.error("NureTeacher is null when trying to update it");
            return null;
        }

        return nureTeacherRepository.save(nureTeacher);
    }

    @Override
    public NureTeacher getNureTeacher(Long id) {
        if(ObjectUtil.isNull(id)) {
            log.error("NureTeacher id is null when trying to get it");
            return null;
        }

        return nureTeacherRepository.findById(id).orElse(null);
    }

    @Override
    public List<NureTeacher> getAllNureTeachers() {
        return (List<NureTeacher>) nureTeacherRepository.findAll();
    }

    @Override
    public void deleteNureTeacher(Long id) {
        if(ObjectUtil.isNull(id)) {
            log.error("NureTeacher id is null when trying to delete it");
            return;
        }

        nureTeacherRepository.deleteById(id);
    }

    @Override
    public NureAuditory saveNureAuditory(NureAuditory nureAuditory) {
        if(ObjectUtil.isNull(nureAuditory)) {
            log.error("NureAuditory is null when trying to save it");
            return null;
        }

        return nureAuditoryRepository.save(nureAuditory);
    }

    @Override
    public List<NureAuditory> saveNureAuditories(List<NureAuditory> nureAuditories) {
        if (ObjectUtil.isNull(nureAuditories)) {
            log.error("NureAuditories is null when trying to save it");
            return null;
        }

        nureAuditoryRepository.deleteAll();
        return (List<NureAuditory>) nureAuditoryRepository.saveAll(nureAuditories);
    }

    @Override
    public NureAuditory updateNureAuditory(NureAuditory nureAuditory) {
        if(ObjectUtil.isNull(nureAuditory)) {
            log.error("NureAuditory is null when trying to update it");
            return null;
        }

        return nureAuditoryRepository.save(nureAuditory);
    }

    @Override
    public NureAuditory getNureAuditory(Long id) {
        if(ObjectUtil.isNull(id)) {
            log.error("NureAuditory id is null when trying to get it");
            return null;
        }

        return nureAuditoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<NureAuditory> getAllNureAuditories() {
        return (List<NureAuditory>) nureAuditoryRepository.findAll();
    }

    @Override
    public void deleteNureAuditory(Long id) {
        if(ObjectUtil.isNull(id)) {
            log.error("NureAuditory id is null when trying to delete it");
            return;
        }

        nureAuditoryRepository.deleteById(id);
    }
}

package org.groupscope.schedule_nure.dao.repositories;

import org.groupscope.schedule_nure.entity.NureTeacher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NureTeacherRepository extends CrudRepository<NureTeacher, Long> {
}

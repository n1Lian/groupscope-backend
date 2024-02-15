package org.groupscope.schedule_nure.dao.repositories;

import org.groupscope.schedule_nure.entity.NureAuditory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NureAuditoryRepository extends CrudRepository<NureAuditory, Long> {
}

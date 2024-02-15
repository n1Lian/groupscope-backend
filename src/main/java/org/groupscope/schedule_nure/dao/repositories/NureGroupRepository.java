package org.groupscope.schedule_nure.dao.repositories;

import org.groupscope.schedule_nure.entity.NureGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NureGroupRepository extends CrudRepository<NureGroup, Long> {
}

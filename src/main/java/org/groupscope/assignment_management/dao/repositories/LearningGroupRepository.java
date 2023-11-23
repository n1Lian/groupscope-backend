package org.groupscope.assignment_management.dao.repositories;

import org.groupscope.assignment_management.entity.LearningGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
//import org.springframework.transaction.annotation.Transactional;


@Repository
public interface LearningGroupRepository extends CrudRepository<LearningGroup, Long> {
    Optional<LearningGroup> getLearningGroupByInviteCode(String inviteCode);
}

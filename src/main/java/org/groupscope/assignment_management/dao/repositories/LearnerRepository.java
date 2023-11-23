package org.groupscope.assignment_management.dao.repositories;

import org.groupscope.assignment_management.entity.Learner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearnerRepository extends CrudRepository<Learner, Long> {
    Optional<Learner> getLearnerByName(String name);

    Optional<Learner> getLearnersByNameAndLastname(String name, String lastname);
}

package org.groupscope.assignment_management.dao.repositories;

import org.groupscope.security.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByLogin(String login);
}

package org.groupscope.security.services.auth;

import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.dao.AssignmentManagerDAOImpl;
import org.groupscope.assignment_management.dao.repositories.UserRepository;
import org.groupscope.security.entity.User;
import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username);

        if(user == null) {
            throw new UsernameNotFoundException("Unknown user: " + username);
        }
        Hibernate.initialize(user.getLearner().getGrades());
        if(user.getLearner().getLearningGroup() != null)
            AssignmentManagerDAOImpl.removeDuplicates(user.getLearner().getLearningGroup().getSubjects());
        return user;
    }
}

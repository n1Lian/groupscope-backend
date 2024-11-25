package org.groupscope.security.services.auth;


import lombok.RequiredArgsConstructor;
import org.groupscope.assignment_management.dao.repositories.UserRepository;
import org.groupscope.assignment_management.dto.LearnerDTO;
import org.groupscope.assignment_management.entity.Learner;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.groupscope.assignment_management.entity.LearningRole;
import org.groupscope.assignment_management.services.GroupService;
import org.groupscope.assignment_management.services.LearnerService;
import org.groupscope.exceptions.EntityNotFoundException;
import org.groupscope.security.dto.RegistrationRequest;
import org.groupscope.security.entity.Provider;
import org.groupscope.security.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final LearnerService learnerService;

    private final GroupService groupService;


    /**
     * Saves a custom user to the repository and performs additional actions based on the registration request.
     * If a user is registering with an invite code, they will be added to an existing group.
     * If a user is registering with a group name, a new group will be created, and the user will be set as the headman.
     * If a user is registering without a group addition, they will be registered as a free learner.
     *
     * @param user The User to be saved.
     * @param request The RegistrationRequest containing additional registration information.
     * @param provider The Provider associated with the registration (e.g., email, social media).
     * @return The saved custom user or null if the user couldn't be saved.
     */
    @Transactional
    public User saveUser(
            User user,
            RegistrationRequest request,
            Provider provider
    ) {
        if(user.getPassword() != null) user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setProvider(provider);

        LearnerDTO learnerDTO = new LearnerDTO(request);

        if(request.getInviteCode() != null) {
            return addLearnerByInviteCode(learnerDTO, request, user);

        } else if (request.getNureGroupId() != null) {
            return addLearnerToNewGroup(user, request);

        } else {
            return addFreeLearner(learnerDTO, user);
        }
    }

    private User addFreeLearner(LearnerDTO learnerDTO, User user) {
        Learner learner = Optional.of(learnerService.addFreeLearner(learnerDTO))
                .orElseThrow(() -> new NullPointerException("Learner not created"));

        user.setLearner(learner);
        return userRepository.save(user);
    }

    private User addLearnerToNewGroup(User user, RegistrationRequest request) {
        LearnerDTO headman = new LearnerDTO(request.getLearnerName(),
                request.getLearnerLastname(),
                LearningRole.HEADMAN);

        LearningGroup learningGroup = groupService.createGroup(headman);

        user.setLearner(learningGroup.getHeadmen());
        return userRepository.save(user);
    }

    private User addLearnerByInviteCode(LearnerDTO learnerDTO, RegistrationRequest request, User user) {
        String inviteCode = request.getInviteCode();

        Learner learner = Optional.of(learnerService.addLearner(learnerDTO.toLearner(), inviteCode))
                .orElseThrow(() -> new NullPointerException("Learner not created"));

        user.setLearner(learner);
        return userRepository.save(user);
    }

    /*
     * Find a custom user by their login (username).
     * Returns the custom user if found, or null if not found.
     */
    @Transactional
    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    /*
     * Find a custom user by their login (username) and password.
     * Returns the custom user if found and the provided password matches the stored password, or null if not found or password doesn't match.
     */
    public User findByLoginAndPassword(String login, String password) {
        User user = findByLogin(login);
        if(user != null) {
            if (passwordEncoder.matches(password, user.getPassword())){
                return user;
            } else
                throw new IllegalArgumentException("Incorrect password");
        } else
            throw new EntityNotFoundException("User not found");
    }


}

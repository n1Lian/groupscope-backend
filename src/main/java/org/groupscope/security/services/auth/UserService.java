package org.groupscope.security.services.auth;


import org.groupscope.assignment_management.dao.repositories.UserRepository;
import org.groupscope.assignment_management.dto.LearnerDTO;
import org.groupscope.assignment_management.dto.LearningGroupDTO;
import org.groupscope.assignment_management.entity.Learner;
import org.groupscope.assignment_management.entity.LearningGroup;
import org.groupscope.assignment_management.entity.LearningRole;
import org.groupscope.security.dto.RegistrationRequest;
import org.groupscope.security.entity.Provider;
import org.groupscope.security.entity.User;
import org.groupscope.assignment_management.services.AssignmentManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.groupscope.util.FunctionInfo.getCurrentMethodName;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AssignmentManagerService assignmentManagerService;


    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AssignmentManagerService assignmentManagerService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.assignmentManagerService = assignmentManagerService;
    }


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
    public User saveUser(User user, RegistrationRequest request, Provider provider) {
        if(user == null || request == null || provider == null) {
            throw new NullPointerException("One of arguments is null: " + user + ", " + request + ", " + provider +
                    " in " + getCurrentMethodName());
        }

        if(user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setProvider(provider);
        LearnerDTO learnerDTO = new LearnerDTO(request.getLearnerName(),
                request.getLearnerLastname(),
                LearningRole.STUDENT);

        // Add new learner to an existing group based on the invite code
        if(request.getInviteCode() != null) {
            Learner student = assignmentManagerService.addLearner(learnerDTO.toLearner(), request.getInviteCode());
            return processLearner(user, student);

        // Add new learner and create a new group
        } else if (request.getGroupName() != null) {
            LearnerDTO headman = new LearnerDTO(request.getLearnerName(),
                    request.getLearnerLastname(),
                    LearningRole.HEADMAN);
            LearningGroupDTO learningGroupDTO = new LearningGroupDTO(request.getGroupName(), headman);
            LearningGroup learningGroup = assignmentManagerService.addGroup(learningGroupDTO);
            if(learningGroup != null) {
                user.setLearner(learningGroup.getHeadmen());
                return userRepository.save(user);
            } else {
                return null;
            }

        // Add new learner without group addition
        } else {
            Learner student = assignmentManagerService.addFreeLearner(learnerDTO);
            return processLearner(user, student);
        }
    }

    /**
     * This method associates a Learner with a User and saves the updated User in the repository.
     *
     * @param user The User to be updated with the new Learner.
     * @param learner The Learner to be associated with the User.
     * @return The updated User after the association, or null if the Learner is null.
     */
    private User processLearner(User user, Learner learner) {
        if (learner != null) {
            user.setLearner(learner);
            return userRepository.save(user);
        } else {
            return null;
        }
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
    @Transactional
    public User findByLoginAndPassword(String login, String password) {
        User user = findByLogin(login);
        if(user != null) {
            if (passwordEncoder.matches(password, user.getPassword())){
                return user;
            } else
                throw new IllegalArgumentException("Incorrect password");
        } else
            throw new NullPointerException("User with login = " + login + "not found");
    }


}

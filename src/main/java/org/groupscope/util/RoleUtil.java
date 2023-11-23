package org.groupscope.util;

import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.entity.LearningRole;
import org.groupscope.security.entity.UserRole;

@Slf4j
public class RoleUtil {
    public static int getPriority(Enum<?> role) {
        if (role instanceof UserRole) {
            UserRole userRole = (UserRole) role;
            switch (userRole) {
                case ADMIN:
                    return 2;
                case MODERATOR:
                    return 1;
                case USER:
                    return 0;
            }
        } else if (role instanceof LearningRole) {
            LearningRole learningRole = (LearningRole) role;
            switch (learningRole) {
                case HEADMAN:
                    return 2;
                case EDITOR:
                    return 1;
                case STUDENT:
                    return 0;
            }
        } else {
            log.error("Unknown role: " + role);
        }
        return -1;
    }
}

package org.groupscope.util;

import lombok.extern.slf4j.Slf4j;
import org.groupscope.assignment_management.entity.LearningRole;
import org.groupscope.security.entity.UserRole;

@Slf4j
public class RoleUtil {

    public static int getPriority(Enum<?> role) {
        if (role instanceof UserRole userRole) {
            return switch (userRole) {
                case ADMIN -> 2;
                case MODERATOR -> 1;
                case USER -> 0;
            };
        } else if (role instanceof LearningRole learningRole) {
            return switch (learningRole) {
                case HEADMAN -> 2;
                case EDITOR -> 1;
                case STUDENT -> 0;
            };
        } else {
          log.error("Unknown role: {}", role);
        }
        return -1;
    }
}

package org.groupscope.assignment_management.util;

import org.groupscope.assignment_management.entity.LearningRole;
import org.groupscope.security.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.groupscope.util.RoleUtil.getPriority;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RoleUtilTest {
    private enum WrongRole{
        WRONG_ROLE
    }
    @Test
    public void getPriorityTestURAdmin(){
        assertEquals(2, getPriority(UserRole.ADMIN));
    }
    @Test
    public void getPriorityTestURModerator(){
        assertEquals(1, getPriority(UserRole.MODERATOR));
    }
    @Test
    public void getPriorityTestURUser(){
        assertEquals(0, getPriority(UserRole.USER));
    }
    @Test
    public void getPriorityTestLRHeadman(){
        assertEquals(2, getPriority(LearningRole.HEADMAN));
    }

    @Test
    public void getPriorityTestLREditor(){
        assertEquals(1, getPriority(LearningRole.EDITOR));
    }

    @Test
    public void getPriorityTestLRStudent(){
        assertEquals(0, getPriority(LearningRole.STUDENT));
    }

    @Test
    public void getPriorityTestWringRole(){
        assertEquals(-1, getPriority(WrongRole.WRONG_ROLE));
    }
}

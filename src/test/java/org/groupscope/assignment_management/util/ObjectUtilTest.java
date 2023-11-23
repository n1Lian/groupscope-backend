package org.groupscope.assignment_management.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.groupscope.util.ObjectUtil.isNull;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ObjectUtilTest {
    @Test
    public void isNullTestOneNull(){
        assertTrue(isNull(1,2,null,8));
    }

    @Test
    public void isNullTestNoNull(){
        assertFalse(isNull(1, 2, 5, 8));
    }

    @Test
    public void isNullTestEmpty(){
        assertFalse(isNull());
    }
}

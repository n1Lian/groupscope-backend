package org.groupscope.util;

public class ObjectUtil {
    public static boolean isNull(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                return true;
            }
        }
        return false;
    }
}

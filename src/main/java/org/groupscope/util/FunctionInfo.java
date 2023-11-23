package org.groupscope.util;

public class FunctionInfo {

    public static String getCurrentMethodName() {
        return new Throwable()
                .getStackTrace()[1]
                .getMethodName();
    }
}

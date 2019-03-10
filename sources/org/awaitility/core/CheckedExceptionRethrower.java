package org.awaitility.core;

public class CheckedExceptionRethrower {
    public static <T> T safeRethrow(Throwable t) {
        safeRethrow0(t);
        return null;
    }

    private static <T extends Throwable> void safeRethrow0(Throwable t) throws Throwable {
        throw t;
    }
}

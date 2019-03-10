package com.robotium.solo;

public class Timeout {
    private static int largeTimeout;
    private static int smallTimeout;

    public static void setLargeTimeout(int milliseconds) {
        largeTimeout = milliseconds;
    }

    public static void setSmallTimeout(int milliseconds) {
        smallTimeout = milliseconds;
    }

    public static int getLargeTimeout() {
        return largeTimeout;
    }

    public static int getSmallTimeout() {
        return smallTimeout;
    }
}

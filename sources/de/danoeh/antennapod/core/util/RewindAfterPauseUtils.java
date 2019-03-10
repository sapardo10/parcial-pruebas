package de.danoeh.antennapod.core.util;

import java.util.concurrent.TimeUnit;

public class RewindAfterPauseUtils {
    public static final long ELAPSED_TIME_FOR_LONG_REWIND = TimeUnit.DAYS.toMillis(1);
    public static final long ELAPSED_TIME_FOR_MEDIUM_REWIND = TimeUnit.HOURS.toMillis(1);
    public static final long ELAPSED_TIME_FOR_SHORT_REWIND = TimeUnit.MINUTES.toMillis(1);
    public static final long LONG_REWIND = TimeUnit.SECONDS.toMillis(20);
    public static final long MEDIUM_REWIND = TimeUnit.SECONDS.toMillis(10);
    public static final long SHORT_REWIND = TimeUnit.SECONDS.toMillis(3);

    private RewindAfterPauseUtils() {
    }

    public static int calculatePositionWithRewind(int currentPosition, long lastPlayedTime) {
        if (currentPosition <= 0 || lastPlayedTime <= 0) {
            return currentPosition;
        }
        long elapsedTime = System.currentTimeMillis() - lastPlayedTime;
        long rewindTime = 0;
        if (elapsedTime > ELAPSED_TIME_FOR_LONG_REWIND) {
            rewindTime = LONG_REWIND;
        } else if (elapsedTime > ELAPSED_TIME_FOR_MEDIUM_REWIND) {
            rewindTime = MEDIUM_REWIND;
        } else if (elapsedTime > ELAPSED_TIME_FOR_SHORT_REWIND) {
            rewindTime = SHORT_REWIND;
        }
        int newPosition = currentPosition - ((int) rewindTime);
        return newPosition > 0 ? newPosition : 0;
    }
}

package android.support.v7.media;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.util.TimeUtils;

public final class MediaSessionStatus {
    static final String KEY_EXTRAS = "extras";
    static final String KEY_QUEUE_PAUSED = "queuePaused";
    static final String KEY_SESSION_STATE = "sessionState";
    static final String KEY_TIMESTAMP = "timestamp";
    public static final int SESSION_STATE_ACTIVE = 0;
    public static final int SESSION_STATE_ENDED = 1;
    public static final int SESSION_STATE_INVALIDATED = 2;
    final Bundle mBundle;

    public static final class Builder {
        private final Bundle mBundle;

        public Builder(int sessionState) {
            this.mBundle = new Bundle();
            setTimestamp(SystemClock.elapsedRealtime());
            setSessionState(sessionState);
        }

        public Builder(MediaSessionStatus status) {
            if (status != null) {
                this.mBundle = new Bundle(status.mBundle);
                return;
            }
            throw new IllegalArgumentException("status must not be null");
        }

        public Builder setTimestamp(long elapsedRealtimeTimestamp) {
            this.mBundle.putLong(MediaSessionStatus.KEY_TIMESTAMP, elapsedRealtimeTimestamp);
            return this;
        }

        public Builder setSessionState(int sessionState) {
            this.mBundle.putInt(MediaSessionStatus.KEY_SESSION_STATE, sessionState);
            return this;
        }

        public Builder setQueuePaused(boolean queuePaused) {
            this.mBundle.putBoolean(MediaSessionStatus.KEY_QUEUE_PAUSED, queuePaused);
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.mBundle.putBundle(MediaSessionStatus.KEY_EXTRAS, extras);
            return this;
        }

        public MediaSessionStatus build() {
            return new MediaSessionStatus(this.mBundle);
        }
    }

    MediaSessionStatus(Bundle bundle) {
        this.mBundle = bundle;
    }

    public long getTimestamp() {
        return this.mBundle.getLong(KEY_TIMESTAMP);
    }

    public int getSessionState() {
        return this.mBundle.getInt(KEY_SESSION_STATE, 2);
    }

    public boolean isQueuePaused() {
        return this.mBundle.getBoolean(KEY_QUEUE_PAUSED);
    }

    public Bundle getExtras() {
        return this.mBundle.getBundle(KEY_EXTRAS);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("MediaSessionStatus{ ");
        result.append("timestamp=");
        TimeUtils.formatDuration(SystemClock.elapsedRealtime() - getTimestamp(), result);
        result.append(" ms ago");
        result.append(", sessionState=");
        result.append(sessionStateToString(getSessionState()));
        result.append(", queuePaused=");
        result.append(isQueuePaused());
        result.append(", extras=");
        result.append(getExtras());
        result.append(" }");
        return result.toString();
    }

    private static String sessionStateToString(int sessionState) {
        switch (sessionState) {
            case 0:
                return "active";
            case 1:
                return "ended";
            case 2:
                return "invalidated";
            default:
                return Integer.toString(sessionState);
        }
    }

    public Bundle asBundle() {
        return this.mBundle;
    }

    public static MediaSessionStatus fromBundle(Bundle bundle) {
        return bundle != null ? new MediaSessionStatus(bundle) : null;
    }
}

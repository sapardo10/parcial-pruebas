package org.antennapod.audio;

import android.util.Log;

class SonicAudioPlayerState {
    static final int END = 8;
    static final int ERROR = 9;
    static final int IDLE = 0;
    static final int INITIALIZED = 1;
    static final int PAUSED = 5;
    static final int PLAYBACK_COMPLETED = 7;
    static final int PREPARED = 3;
    static final int PREPARING = 2;
    static final int STARTED = 4;
    static final int STOPPED = 6;
    private static final String TAG = "SonicAudioPlayerState";
    private int currentState = 0;

    SonicAudioPlayerState() {
    }

    void changeTo(int state) {
        this.currentState = state;
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Changed to ");
        stringBuilder.append(toString());
        Log.d(str, stringBuilder.toString());
    }

    public String toString() {
        switch (this.currentState) {
            case 0:
                return "IDLE";
            case 1:
                return "INITIALIZED";
            case 2:
                return "PREPARING";
            case 3:
                return "PREPARED";
            case 4:
                return "STARTED";
            case 5:
                return "PAUSED";
            case 6:
                return "STOPPED";
            case 7:
                return "PLAYBACK_COMPLETED";
            case 8:
                return "END";
            case 9:
                return "ERROR";
            default:
                return "UNKNOWN_STATE";
        }
    }

    boolean is(int state) {
        return this.currentState == state;
    }

    boolean seekingAllowed() {
        if (!(is(4) || is(3) || is(5))) {
            if (!is(7)) {
                return false;
            }
        }
        return true;
    }

    boolean stoppingAllowed() {
        if (!(is(3) || is(4) || is(6) || is(5))) {
            if (!is(7)) {
                return false;
            }
        }
        return true;
    }

    boolean settingDataSourceAllowed() {
        return is(0);
    }
}

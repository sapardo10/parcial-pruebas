package de.danoeh.antennapod.core.service.playback;

public enum PlayerStatus {
    INDETERMINATE(0),
    ERROR(-1),
    PREPARING(19),
    PAUSED(30),
    PLAYING(40),
    STOPPED(5),
    PREPARED(20),
    SEEKING(29),
    INITIALIZING(9),
    INITIALIZED(10);
    
    private static final PlayerStatus[] fromOrdinalLookup = null;
    private final int statusValue;

    static {
        fromOrdinalLookup = values();
    }

    private PlayerStatus(int val) {
        this.statusValue = val;
    }

    public static PlayerStatus fromOrdinal(int o) {
        return fromOrdinalLookup[o];
    }

    public boolean isAtLeast(PlayerStatus other) {
        if (other != null) {
            if (this.statusValue < other.statusValue) {
                return false;
            }
        }
        return true;
    }
}

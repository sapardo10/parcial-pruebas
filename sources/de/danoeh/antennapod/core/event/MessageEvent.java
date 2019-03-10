package de.danoeh.antennapod.core.event;

import android.support.annotation.Nullable;

public class MessageEvent {
    @Nullable
    public final Runnable action;
    public final String message;

    public MessageEvent(String message) {
        this(message, null);
    }

    public MessageEvent(String message, Runnable action) {
        this.message = message;
        this.action = action;
    }
}

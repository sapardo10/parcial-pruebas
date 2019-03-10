package de.danoeh.antennapod.core.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ProgressEvent {
    public final Action action;
    public final String message;

    public enum Action {
        START,
        END
    }

    private ProgressEvent(Action action, String message) {
        this.action = action;
        this.message = message;
    }

    public static ProgressEvent start(String message) {
        return new ProgressEvent(Action.START, message);
    }

    public static ProgressEvent end() {
        return new ProgressEvent(Action.END, null);
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("action", this.action).append("message", this.message).toString();
    }
}

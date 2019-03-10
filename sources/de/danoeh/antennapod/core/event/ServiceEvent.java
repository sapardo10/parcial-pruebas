package de.danoeh.antennapod.core.event;

public class ServiceEvent {
    public final Action action;

    public enum Action {
        SERVICE_STARTED
    }

    public ServiceEvent(Action action) {
        this.action = action;
    }
}

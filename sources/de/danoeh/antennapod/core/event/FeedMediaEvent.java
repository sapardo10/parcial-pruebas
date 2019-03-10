package de.danoeh.antennapod.core.event;

import de.danoeh.antennapod.core.feed.FeedMedia;

public class FeedMediaEvent {
    private final Action action;
    private final FeedMedia media;

    public enum Action {
        UPDATE
    }

    private FeedMediaEvent(Action action, FeedMedia media) {
        this.action = action;
        this.media = media;
    }

    public static FeedMediaEvent update(FeedMedia media) {
        return new FeedMediaEvent(Action.UPDATE, media);
    }
}

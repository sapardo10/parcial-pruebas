package de.danoeh.antennapod.core.event;

import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.syndication.namespace.NSRSS20;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FavoritesEvent {
    private final Action action;
    private final FeedItem item;

    public enum Action {
        ADDED,
        REMOVED
    }

    private FavoritesEvent(Action action, FeedItem item) {
        this.action = action;
        this.item = item;
    }

    public static FavoritesEvent added(FeedItem item) {
        return new FavoritesEvent(Action.ADDED, item);
    }

    public static FavoritesEvent removed(FeedItem item) {
        return new FavoritesEvent(Action.REMOVED, item);
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("action", this.action).append(NSRSS20.ITEM, this.item).toString();
    }
}

package de.danoeh.antennapod.core.event;

import android.support.annotation.NonNull;
import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FeedItemEvent {
    @NonNull
    private final Action action;
    @NonNull
    public final List<FeedItem> items;

    public enum Action {
        UPDATE,
        DELETE_MEDIA
    }

    private FeedItemEvent(Action action, List<FeedItem> items) {
        this.action = action;
        this.items = items;
    }

    public static FeedItemEvent deletedMedia(List<FeedItem> items) {
        return new FeedItemEvent(Action.DELETE_MEDIA, items);
    }

    public static FeedItemEvent updated(List<FeedItem> items) {
        return new FeedItemEvent(Action.UPDATE, items);
    }

    public static FeedItemEvent updated(FeedItem... items) {
        return new FeedItemEvent(Action.UPDATE, Arrays.asList(items));
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("action", this.action).append("items", this.items).toString();
    }
}

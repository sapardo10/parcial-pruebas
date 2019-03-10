package de.danoeh.antennapod.core.event;

import android.support.annotation.Nullable;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.core.syndication.namespace.NSRSS20;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class QueueEvent {
    public final Action action;
    public final FeedItem item;
    public final List<FeedItem> items;
    public final int position;

    public enum Action {
        ADDED,
        ADDED_ITEMS,
        SET_QUEUE,
        REMOVED,
        IRREVERSIBLE_REMOVED,
        CLEARED,
        DELETED_MEDIA,
        SORTED,
        MOVED
    }

    private QueueEvent(Action action, @Nullable FeedItem item, @Nullable List<FeedItem> items, int position) {
        this.action = action;
        this.item = item;
        this.items = items;
        this.position = position;
    }

    public static QueueEvent added(FeedItem item, int position) {
        return new QueueEvent(Action.ADDED, item, null, position);
    }

    public static QueueEvent setQueue(List<FeedItem> queue) {
        return new QueueEvent(Action.SET_QUEUE, null, queue, -1);
    }

    public static QueueEvent removed(FeedItem item) {
        return new QueueEvent(Action.REMOVED, item, null, -1);
    }

    public static QueueEvent irreversibleRemoved(FeedItem item) {
        return new QueueEvent(Action.IRREVERSIBLE_REMOVED, item, null, -1);
    }

    public static QueueEvent cleared() {
        return new QueueEvent(Action.CLEARED, null, null, -1);
    }

    public static QueueEvent sorted(List<FeedItem> sortedQueue) {
        return new QueueEvent(Action.SORTED, null, sortedQueue, -1);
    }

    public static QueueEvent moved(FeedItem item, int newPosition) {
        return new QueueEvent(Action.MOVED, item, null, newPosition);
    }

    public boolean contains(long id) {
        FeedItem feedItem = this.item;
        boolean z = true;
        if (feedItem != null) {
            if (feedItem.getId() != id) {
                z = false;
            }
            return z;
        }
        for (FeedItem item : this.items) {
            if (item.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("action", this.action).append(NSRSS20.ITEM, this.item).append("items", this.items).append(PodDBAdapter.KEY_POSITION, this.position).toString();
    }
}

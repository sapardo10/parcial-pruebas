package de.danoeh.antennapod.core.util;

import de.danoeh.antennapod.core.feed.FeedItem;
import java.util.Iterator;
import java.util.List;

public abstract class QueueAccess {
    public abstract boolean contains(long j);

    public abstract boolean remove(long j);

    private QueueAccess() {
    }

    public static QueueAccess ItemListAccess(final List<FeedItem> items) {
        return new QueueAccess() {
            public boolean contains(long id) {
                List<FeedItem> list = items;
                if (list == null) {
                    return false;
                }
                for (FeedItem item : list) {
                    if (item.getId() == id) {
                        return true;
                    }
                }
                return false;
            }

            public boolean remove(long id) {
                Iterator<FeedItem> it = items.iterator();
                while (it.hasNext()) {
                    if (((FeedItem) it.next()).getId() == id) {
                        it.remove();
                        return true;
                    }
                }
                return false;
            }
        };
    }
}

package de.danoeh.antennapod.core.storage;

import android.database.Cursor;
import java.util.Date;

public class FeedItemStatistics {
    private static final Date UNKNOWN_DATE = new Date(0);
    private final long feedID;
    private final Date lastUpdate;
    private final int numberOfInProgressItems;
    private final int numberOfItems;
    private final int numberOfNewItems;

    private FeedItemStatistics(long feedID, int numberOfItems, int numberOfNewItems, int numberOfInProgressItems, Date lastUpdate) {
        this.feedID = feedID;
        this.numberOfItems = numberOfItems;
        this.numberOfNewItems = numberOfNewItems;
        this.numberOfInProgressItems = numberOfInProgressItems;
        if (numberOfItems > 0) {
            this.lastUpdate = lastUpdate != null ? (Date) lastUpdate.clone() : null;
        } else {
            this.lastUpdate = UNKNOWN_DATE;
        }
    }

    public static FeedItemStatistics fromCursor(Cursor cursor) {
        return new FeedItemStatistics(cursor.getLong(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(4), new Date(cursor.getLong(3)));
    }

    public long getFeedID() {
        return this.feedID;
    }

    public int getNumberOfItems() {
        return this.numberOfItems;
    }

    public int getNumberOfNewItems() {
        return this.numberOfNewItems;
    }

    public int getNumberOfInProgressItems() {
        return this.numberOfInProgressItems;
    }

    public Date getLastUpdate() {
        Date date = this.lastUpdate;
        return date != null ? (Date) date.clone() : null;
    }

    public boolean lastUpdateKnown() {
        return this.lastUpdate != UNKNOWN_DATE;
    }
}

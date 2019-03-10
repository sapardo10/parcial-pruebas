package de.danoeh.antennapod.adapter;

import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.util.LongList;

interface ActionButtonCallback {
    void onActionButtonPressed(FeedItem feedItem, LongList longList);
}

package de.danoeh.antennapod.core.syndication.handler;

import de.danoeh.antennapod.core.feed.Feed;
import java.util.Map;

public class FeedHandlerResult {
    public final Map<String, String> alternateFeedUrls;
    public final Feed feed;

    public FeedHandlerResult(Feed feed, Map<String, String> alternateFeedUrls) {
        this.feed = feed;
        this.alternateFeedUrls = alternateFeedUrls;
    }
}

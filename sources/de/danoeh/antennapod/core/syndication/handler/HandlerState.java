package de.danoeh.antennapod.core.syndication.handler;

import android.support.v4.util.ArrayMap;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.syndication.namespace.Namespace;
import de.danoeh.antennapod.core.syndication.namespace.SyndElement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class HandlerState {
    final Map<String, String> alternateUrls = new ArrayMap();
    protected StringBuilder contentBuf;
    private FeedItem currentItem;
    final Stack<Namespace> defaultNamespaces = new Stack();
    Feed feed;
    private final ArrayList<FeedItem> items = new ArrayList();
    final Map<String, Namespace> namespaces = new ArrayMap();
    final Stack<SyndElement> tagstack = new Stack();
    private final Map<String, Object> tempObjects = new ArrayMap();

    public HandlerState(Feed feed) {
        this.feed = feed;
    }

    public Feed getFeed() {
        return this.feed;
    }

    public ArrayList<FeedItem> getItems() {
        return this.items;
    }

    public FeedItem getCurrentItem() {
        return this.currentItem;
    }

    public Stack<SyndElement> getTagstack() {
        return this.tagstack;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public void setCurrentItem(FeedItem currentItem) {
        this.currentItem = currentItem;
    }

    public SyndElement getSecondTag() {
        SyndElement second = (SyndElement) this.tagstack.peek();
        this.tagstack.push((SyndElement) this.tagstack.pop());
        return second;
    }

    public SyndElement getThirdTag() {
        SyndElement top = (SyndElement) this.tagstack.pop();
        SyndElement third = (SyndElement) this.tagstack.peek();
        this.tagstack.push((SyndElement) this.tagstack.pop());
        this.tagstack.push(top);
        return third;
    }

    public StringBuilder getContentBuf() {
        return this.contentBuf;
    }

    public void addAlternateFeedUrl(String title, String url) {
        this.alternateUrls.put(url, title);
    }

    public Map<String, Object> getTempObjects() {
        return this.tempObjects;
    }
}

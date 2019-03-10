package de.danoeh.antennapod.core.feed;

public class SearchResult {
    private final FeedComponent component;
    private String subtitle;
    private final int value;

    public SearchResult(FeedComponent component, int value, String subtitle) {
        this.component = component;
        this.value = value;
        this.subtitle = subtitle;
    }

    public FeedComponent getComponent() {
        return this.component;
    }

    public String getSubtitle() {
        return this.subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getValue() {
        return this.value;
    }
}

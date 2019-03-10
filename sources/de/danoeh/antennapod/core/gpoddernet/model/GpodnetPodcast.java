package de.danoeh.antennapod.core.gpoddernet.model;

import android.support.annotation.NonNull;

public class GpodnetPodcast {
    private final String description;
    private final String logoUrl;
    private final String mygpoLink;
    private final int subscribers;
    private final String title;
    private final String url;
    private final String website;

    public GpodnetPodcast(@NonNull String url, @NonNull String title, @NonNull String description, int subscribers, String logoUrl, String website, String mygpoLink) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.subscribers = subscribers;
        this.logoUrl = logoUrl;
        this.website = website;
        this.mygpoLink = mygpoLink;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("GpodnetPodcast [url=");
        stringBuilder.append(this.url);
        stringBuilder.append(", title=");
        stringBuilder.append(this.title);
        stringBuilder.append(", description=");
        stringBuilder.append(this.description);
        stringBuilder.append(", subscribers=");
        stringBuilder.append(this.subscribers);
        stringBuilder.append(", logoUrl=");
        stringBuilder.append(this.logoUrl);
        stringBuilder.append(", website=");
        stringBuilder.append(this.website);
        stringBuilder.append(", mygpoLink=");
        stringBuilder.append(this.mygpoLink);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public String getUrl() {
        return this.url;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public int getSubscribers() {
        return this.subscribers;
    }

    public String getLogoUrl() {
        return this.logoUrl;
    }

    public String getWebsite() {
        return this.website;
    }

    public String getMygpoLink() {
        return this.mygpoLink;
    }
}

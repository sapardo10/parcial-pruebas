package de.danoeh.antennapod.core.service.download;

import android.os.Bundle;
import android.support.annotation.NonNull;
import de.danoeh.antennapod.core.feed.FeedFile;
import de.danoeh.antennapod.core.util.URLChecker;

public class DownloadRequest$Builder {
    private Bundle arguments;
    private boolean deleteOnFailure = false;
    private final String destination;
    private final long feedfileId;
    private final int feedfileType;
    private String lastModified;
    private String password;
    private final String source;
    private final String title;
    private String username;

    public DownloadRequest$Builder(@NonNull String destination, @NonNull FeedFile item) {
        this.destination = destination;
        this.source = URLChecker.prepareURL(item.getDownload_url());
        this.title = item.getHumanReadableIdentifier();
        this.feedfileId = item.getId();
        this.feedfileType = item.getTypeAsInt();
    }

    public DownloadRequest$Builder deleteOnFailure(boolean deleteOnFailure) {
        this.deleteOnFailure = deleteOnFailure;
        return this;
    }

    public DownloadRequest$Builder lastModified(String lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public DownloadRequest$Builder withAuthentication(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    public DownloadRequest build() {
        return new DownloadRequest(this, null);
    }

    public DownloadRequest$Builder withArguments(Bundle args) {
        this.arguments = args;
        return this;
    }
}

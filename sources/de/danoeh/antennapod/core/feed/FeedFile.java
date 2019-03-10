package de.danoeh.antennapod.core.feed;

import android.text.TextUtils;
import java.io.File;

public abstract class FeedFile extends FeedComponent {
    protected String download_url;
    boolean downloaded;
    String file_url;

    public abstract int getTypeAsInt();

    public FeedFile(String file_url, String download_url, boolean downloaded) {
        this.file_url = file_url;
        this.download_url = download_url;
        boolean z = file_url != null && downloaded;
        this.downloaded = z;
    }

    public FeedFile() {
        this(null, null, false);
    }

    void updateFromOther(FeedFile other) {
        super.updateFromOther(other);
        this.download_url = other.download_url;
    }

    boolean compareWithOther(FeedFile other) {
        if (!super.compareWithOther(other) && TextUtils.equals(this.download_url, other.download_url)) {
            return false;
        }
        return true;
    }

    public boolean fileExists() {
        String str = this.file_url;
        if (str == null) {
            return false;
        }
        return new File(str).exists();
    }

    public String getFile_url() {
        return this.file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
        if (file_url == null) {
            this.downloaded = false;
        }
    }

    public String getDownload_url() {
        return this.download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public boolean isDownloaded() {
        return this.downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }
}

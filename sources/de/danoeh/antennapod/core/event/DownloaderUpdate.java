package de.danoeh.antennapod.core.event;

import de.danoeh.antennapod.core.service.download.Downloader;
import de.danoeh.antennapod.core.util.LongList;
import java.util.Arrays;
import java.util.List;

public class DownloaderUpdate {
    public final List<Downloader> downloaders;
    public final long[] feedIds;
    public final long[] mediaIds;

    public DownloaderUpdate(List<Downloader> downloaders) {
        this.downloaders = downloaders;
        LongList feedIds1 = new LongList();
        LongList mediaIds1 = new LongList();
        for (Downloader d1 : downloaders) {
            int type = d1.getDownloadRequest().getFeedfileType();
            long id = d1.getDownloadRequest().getFeedfileId();
            if (type == 0) {
                feedIds1.add(id);
            } else if (type == 2) {
                mediaIds1.add(id);
            }
        }
        this.feedIds = feedIds1.toArray();
        this.mediaIds = mediaIds1.toArray();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DownloaderUpdate{downloaders=");
        stringBuilder.append(this.downloaders);
        stringBuilder.append(", feedIds=");
        stringBuilder.append(Arrays.toString(this.feedIds));
        stringBuilder.append(", mediaIds=");
        stringBuilder.append(Arrays.toString(this.mediaIds));
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}

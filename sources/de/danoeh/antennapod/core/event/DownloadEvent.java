package de.danoeh.antennapod.core.event;

import de.danoeh.antennapod.core.service.download.Downloader;
import java.util.ArrayList;
import java.util.List;

public class DownloadEvent {
    public final DownloaderUpdate update;

    private DownloadEvent(DownloaderUpdate downloader) {
        this.update = downloader;
    }

    public static DownloadEvent refresh(List<Downloader> list) {
        return new DownloadEvent(new DownloaderUpdate(new ArrayList(list)));
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DownloadEvent{update=");
        stringBuilder.append(this.update);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}

package de.danoeh.antennapod.core.util.comparator;

import de.danoeh.antennapod.core.service.download.DownloadStatus;
import java.util.Comparator;

public class DownloadStatusComparator implements Comparator<DownloadStatus> {
    public int compare(DownloadStatus lhs, DownloadStatus rhs) {
        return rhs.getCompletionDate().compareTo(lhs.getCompletionDate());
    }
}

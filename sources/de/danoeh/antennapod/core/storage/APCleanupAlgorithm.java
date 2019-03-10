package de.danoeh.antennapod.core.storage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedMedia;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class APCleanupAlgorithm extends EpisodeCleanupAlgorithm {
    private static final String TAG = "APCleanupAlgorithm";
    private final int numberOfHoursAfterPlayback;

    public APCleanupAlgorithm(int numberOfHoursAfterPlayback) {
        this.numberOfHoursAfterPlayback = numberOfHoursAfterPlayback;
    }

    public int getReclaimableItems() {
        return getCandidates().size();
    }

    public int performCleanup(Context context, int numberOfEpisodesToDelete) {
        List<FeedItem> delete;
        List<FeedItem> candidates = getCandidates();
        Collections.sort(candidates, -$$Lambda$APCleanupAlgorithm$86jFFXwweRXNBHeV60r_vtmjcUI.INSTANCE);
        if (candidates.size() > numberOfEpisodesToDelete) {
            delete = candidates.subList(0, numberOfEpisodesToDelete);
        } else {
            delete = candidates;
        }
        for (FeedItem item : delete) {
            try {
                DBWriter.deleteFeedMediaOfItem(context, item.getMedia().getId()).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int counter = delete.size();
        Log.i(TAG, String.format("Auto-delete deleted %d episodes (%d requested)", new Object[]{Integer.valueOf(counter), Integer.valueOf(numberOfEpisodesToDelete)}));
        return counter;
    }

    static /* synthetic */ int lambda$performCleanup$0(FeedItem lhs, FeedItem rhs) {
        Date l = lhs.getMedia().getPlaybackCompletionDate();
        Date r = rhs.getMedia().getPlaybackCompletionDate();
        if (l == null) {
            l = new Date();
        }
        if (r == null) {
            r = new Date();
        }
        return l.compareTo(r);
    }

    @VisibleForTesting
    Date calcMostRecentDateForDeletion(@NonNull Date currentDate) {
        return minusHours(currentDate, this.numberOfHoursAfterPlayback);
    }

    @NonNull
    private List<FeedItem> getCandidates() {
        List<FeedItem> candidates = new ArrayList();
        List<FeedItem> downloadedItems = DBReader.getDownloadedItems();
        Date mostRecentDateForDeletion = calcMostRecentDateForDeletion(new Date());
        for (FeedItem item : downloadedItems) {
            if (item.hasMedia()) {
                if (item.getMedia().isDownloaded()) {
                    if (!item.isTagged(FeedItem.TAG_QUEUE)) {
                        if (item.isPlayed()) {
                            if (!item.isTagged(FeedItem.TAG_FAVORITE)) {
                                FeedMedia media = item.getMedia();
                                if (media != null) {
                                    if (media.getPlaybackCompletionDate() != null) {
                                        if (media.getPlaybackCompletionDate().before(mostRecentDateForDeletion)) {
                                            candidates.add(item);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return candidates;
    }

    public int getDefaultCleanupParameter() {
        return getNumEpisodesToCleanup(0);
    }

    @VisibleForTesting
    public int getNumberOfHoursAfterPlayback() {
        return this.numberOfHoursAfterPlayback;
    }

    private static Date minusHours(Date baseDate, int numberOfHours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(baseDate);
        cal.add(11, numberOfHours * -1);
        return cal.getTime();
    }
}

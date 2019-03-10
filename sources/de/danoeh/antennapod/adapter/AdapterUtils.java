package de.danoeh.antennapod.adapter;

import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.joanzapata.iconify.Iconify;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedItem.State;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.NetworkUtils;

class AdapterUtils {
    private static final String TAG = AdapterUtils.class.getSimpleName();

    private AdapterUtils() {
    }

    static void updateEpisodePlaybackProgress(FeedItem item, TextView txtvPos, ProgressBar episodeProgress) {
        FeedMedia media = item.getMedia();
        episodeProgress.setVisibility(8);
        if (media == null) {
            txtvPos.setVisibility(8);
            return;
        }
        txtvPos.setVisibility(0);
        State state = item.getState();
        if (state != State.PLAYING) {
            if (state != State.IN_PROGRESS) {
                if (media.isDownloaded()) {
                    txtvPos.setText(Converter.getDurationStringLong(media.getDuration()));
                } else if (media.getSize() > 0) {
                    txtvPos.setText(Converter.byteToString(media.getSize()));
                } else if (!NetworkUtils.isDownloadAllowed() || media.checkedOnSizeButUnknown()) {
                    txtvPos.setText("");
                } else {
                    txtvPos.setText("{fa-spinner}");
                    Iconify.addIcons(new TextView[]{txtvPos});
                    NetworkUtils.getFeedMediaSizeObservable(media).subscribe(new -$$Lambda$AdapterUtils$x93gLTgAcuVWFelUrQYlHYxtkIo(txtvPos), new -$$Lambda$AdapterUtils$yjMslKNAGdEwJF3ihEa282Ko0Aw(txtvPos));
                }
            }
        }
        if (media.getDuration() > 0) {
            episodeProgress.setVisibility(0);
            double position = (double) media.getPosition();
            double duration = (double) media.getDuration();
            Double.isNaN(position);
            Double.isNaN(duration);
            episodeProgress.setProgress((int) ((position / duration) * 100.0d));
            txtvPos.setText(Converter.getDurationStringLong(media.getDuration() - media.getPosition()));
        }
    }

    static /* synthetic */ void lambda$updateEpisodePlaybackProgress$0(TextView txtvPos, Long size) throws Exception {
        if (size.longValue() > 0) {
            txtvPos.setText(Converter.byteToString(size.longValue()));
        } else {
            txtvPos.setText("");
        }
    }

    static /* synthetic */ void lambda$updateEpisodePlaybackProgress$1(TextView txtvPos, Throwable error) throws Exception {
        txtvPos.setText("");
        Log.e(TAG, Log.getStackTraceString(error));
    }
}

package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import de.danoeh.antennapod.core.dialog.DownloadRequestErrorDialogCreator;
import de.danoeh.antennapod.core.feed.FeedFile;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.DownloadRequestException;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.core.util.LongList;
import de.danoeh.antennapod.core.util.NetworkUtils;
import de.danoeh.antennapod.core.util.playback.PlaybackServiceStarter;
import de.danoeh.antennapod.debug.R;
import org.apache.commons.lang3.Validate;

public class DefaultActionButtonCallback implements ActionButtonCallback {
    private static final String TAG = "DefaultActionButtonCallback";
    private static final int TEN_MINUTES_IN_MILLIS = 600000;
    private static long allowMobileDownloadsTimestamp;
    private static long onlyAddToQueueTimeStamp;
    private final Context context;

    public DefaultActionButtonCallback(Context context) {
        Validate.notNull(context);
        this.context = context;
    }

    public static boolean userAllowedMobileDownloads() {
        return System.currentTimeMillis() - allowMobileDownloadsTimestamp < 600000;
    }

    public static boolean userChoseAddToQueue() {
        return System.currentTimeMillis() - onlyAddToQueueTimeStamp < 600000;
    }

    public void onActionButtonPressed(FeedItem item, LongList queueIds) {
        if (item.hasMedia()) {
            FeedFile media = item.getMedia();
            boolean isDownloading = DownloadRequester.getInstance().isDownloadingFile(media);
            if (!isDownloading && !media.isDownloaded()) {
                if (!NetworkUtils.isDownloadAllowed()) {
                    if (!userAllowedMobileDownloads()) {
                        if (!userChoseAddToQueue() || queueIds.contains(item.getId())) {
                            confirmMobileDownload(this.context, item);
                        } else {
                            DBWriter.addQueueItem(this.context, item);
                            Toast.makeText(this.context, R.string.added_to_queue_label, 0).show();
                        }
                    }
                }
                try {
                    DBTasks.downloadFeedItems(this.context, item);
                    Toast.makeText(this.context, R.string.status_downloading_label, 0).show();
                } catch (DownloadRequestException e) {
                    e.printStackTrace();
                    DownloadRequestErrorDialogCreator.newRequestErrorDialog(this.context, e.getMessage());
                }
            } else if (isDownloading) {
                DownloadRequester.getInstance().cancelDownload(this.context, media);
                if (UserPreferences.isEnableAutodownload()) {
                    DBWriter.setFeedItemAutoDownload(media.getItem(), false);
                    Toast.makeText(this.context, R.string.download_canceled_autodownload_enabled_msg, 1).show();
                } else {
                    Toast.makeText(this.context, R.string.download_canceled_msg, 1).show();
                }
            } else if (media.isCurrentlyPlaying()) {
                new PlaybackServiceStarter(this.context, media).startWhenPrepared(true).shouldStream(false).start();
                IntentUtils.sendLocalBroadcast(this.context, PlaybackService.ACTION_PAUSE_PLAY_CURRENT_EPISODE);
            } else if (media.isCurrentlyPaused()) {
                new PlaybackServiceStarter(this.context, media).startWhenPrepared(true).shouldStream(false).start();
                IntentUtils.sendLocalBroadcast(this.context, PlaybackService.ACTION_RESUME_PLAY_CURRENT_EPISODE);
            } else {
                DBTasks.playMedia(this.context, media, false, true, false);
            }
        } else if (!item.isPlayed()) {
            DBWriter.markItemPlayed(item, 1, true);
        }
    }

    private void confirmMobileDownload(Context context, FeedItem item) {
        Builder builder = new Builder(context);
        builder.title((int) R.string.confirm_mobile_download_dialog_title).content((int) R.string.confirm_mobile_download_dialog_message).positiveText(context.getText(R.string.confirm_mobile_download_dialog_enable_temporarily)).onPositive(new C1023x652a66f8(context, item));
        if (!DBReader.getQueueIDList().contains(item.getId())) {
            builder.content((int) R.string.confirm_mobile_download_dialog_message_not_in_queue).neutralText((int) R.string.confirm_mobile_download_dialog_only_add_to_queue).onNeutral(new C1022xac3f880f(context, item));
        }
        builder.show();
    }

    static /* synthetic */ void lambda$confirmMobileDownload$0(Context context, FeedItem item, MaterialDialog dialog, DialogAction which) {
        allowMobileDownloadsTimestamp = System.currentTimeMillis();
        try {
            DBTasks.downloadFeedItems(context, item);
            Toast.makeText(context, R.string.status_downloading_label, 0).show();
        } catch (DownloadRequestException e) {
            e.printStackTrace();
            DownloadRequestErrorDialogCreator.newRequestErrorDialog(context, e.getMessage());
        }
    }

    static /* synthetic */ void lambda$confirmMobileDownload$1(Context context, FeedItem item, MaterialDialog dialog, DialogAction which) {
        onlyAddToQueueTimeStamp = System.currentTimeMillis();
        DBWriter.addQueueItem(context, item);
        Toast.makeText(context, R.string.added_to_queue_label, 0).show();
    }
}

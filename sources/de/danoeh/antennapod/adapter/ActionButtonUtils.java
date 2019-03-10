package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.widget.ImageButton;
import de.danoeh.antennapod.core.feed.FeedFile;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.debug.R;
import org.apache.commons.lang3.Validate;

class ActionButtonUtils {
    private final Context context;
    private final TypedArray drawables;
    private final int[] labels = new int[]{R.string.play_label, R.string.cancel_download_label, R.string.download_label, R.string.mark_read_label, R.string.add_to_queue_label};

    public ActionButtonUtils(Context context) {
        Validate.notNull(context);
        this.context = context.getApplicationContext();
        this.drawables = context.obtainStyledAttributes(new int[]{R.attr.av_play, R.attr.navigation_cancel, R.attr.av_download, R.attr.av_pause, R.attr.navigation_accept, R.attr.content_new});
    }

    public void configureActionButton(ImageButton butSecondary, FeedItem item, boolean isInQueue) {
        boolean z = (butSecondary == null || item == null) ? false : true;
        Validate.isTrue(z, "butSecondary or item was null", new Object[0]);
        FeedFile media = item.getMedia();
        if (media != null) {
            boolean isDownloadingMedia = DownloadRequester.getInstance().isDownloadingFile(media);
            if (media.isDownloaded()) {
                butSecondary.setVisibility(0);
                if (media.isCurrentlyPlaying()) {
                    butSecondary.setImageDrawable(this.drawables.getDrawable(3));
                } else {
                    butSecondary.setImageDrawable(this.drawables.getDrawable(0));
                }
                butSecondary.setContentDescription(this.context.getString(this.labels[0]));
            } else if (isDownloadingMedia) {
                butSecondary.setVisibility(0);
                butSecondary.setImageDrawable(this.drawables.getDrawable(1));
                butSecondary.setContentDescription(this.context.getString(this.labels[1]));
            } else {
                if (!DefaultActionButtonCallback.userAllowedMobileDownloads()) {
                    if (DefaultActionButtonCallback.userChoseAddToQueue()) {
                        if (!isInQueue) {
                            butSecondary.setVisibility(0);
                            butSecondary.setImageDrawable(this.drawables.getDrawable(5));
                            butSecondary.setContentDescription(this.context.getString(this.labels[4]));
                        }
                    }
                }
                butSecondary.setVisibility(0);
                butSecondary.setImageDrawable(this.drawables.getDrawable(2));
                butSecondary.setContentDescription(this.context.getString(this.labels[2]));
            }
        } else if (item.isPlayed()) {
            butSecondary.setVisibility(4);
        } else {
            butSecondary.setVisibility(0);
            butSecondary.setImageDrawable(this.drawables.getDrawable(4));
            butSecondary.setContentDescription(this.context.getString(this.labels[3]));
        }
    }
}

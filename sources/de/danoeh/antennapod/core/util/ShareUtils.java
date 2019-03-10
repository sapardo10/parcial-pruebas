package de.danoeh.antennapod.core.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.content.FileProvider;
import android.util.Log;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedMedia;
import java.io.File;
import org.apache.commons.lang3.StringUtils;

public class ShareUtils {
    private static final String TAG = "ShareUtils";

    private ShareUtils() {
    }

    public static void shareLink(Context context, String text) {
        Intent i = new Intent("android.intent.action.SEND");
        i.setType("text/plain");
        i.putExtra("android.intent.extra.TEXT", text);
        context.startActivity(Intent.createChooser(i, context.getString(C0734R.string.share_url_label)));
    }

    public static void shareFeedlink(Context context, Feed feed) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(feed.getTitle());
        stringBuilder.append(": ");
        stringBuilder.append(feed.getLink());
        shareLink(context, stringBuilder.toString());
    }

    public static void shareFeedDownloadLink(Context context, Feed feed) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(feed.getTitle());
        stringBuilder.append(": ");
        stringBuilder.append(feed.getDownload_url());
        shareLink(context, stringBuilder.toString());
    }

    public static void shareFeedItemLink(Context context, FeedItem item) {
        shareFeedItemLink(context, item, false);
    }

    public static void shareFeedItemDownloadLink(Context context, FeedItem item) {
        shareFeedItemDownloadLink(context, item, false);
    }

    private static String getItemShareText(FeedItem item) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(item.getFeed().getTitle());
        stringBuilder.append(": ");
        stringBuilder.append(item.getTitle());
        return stringBuilder.toString();
    }

    public static boolean hasLinkToShare(FeedItem item) {
        return FeedItemUtil.getLinkWithFallback(item) != null;
    }

    public static void shareFeedItemLink(Context context, FeedItem item, boolean withPosition) {
        String text = new StringBuilder();
        text.append(getItemShareText(item));
        text.append(StringUtils.SPACE);
        text.append(FeedItemUtil.getLinkWithFallback(item));
        text = text.toString();
        if (withPosition) {
            int pos = item.getMedia().getPosition();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(text);
            stringBuilder.append(" [");
            stringBuilder.append(Converter.getDurationStringLong(pos));
            stringBuilder.append("]");
            text = stringBuilder.toString();
        }
        shareLink(context, text);
    }

    public static void shareFeedItemDownloadLink(Context context, FeedItem item, boolean withPosition) {
        String text = new StringBuilder();
        text.append(getItemShareText(item));
        text.append(StringUtils.SPACE);
        text.append(item.getMedia().getDownload_url());
        text = text.toString();
        if (withPosition) {
            int pos = item.getMedia().getPosition();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(text);
            stringBuilder.append(" [");
            stringBuilder.append(Converter.getDurationStringLong(pos));
            stringBuilder.append("]");
            text = stringBuilder.toString();
        }
        shareLink(context, text);
    }

    public static void shareFeedItemFile(Context context, FeedMedia media) {
        Intent i = new Intent("android.intent.action.SEND");
        i.setType(media.getMime_type());
        Uri fileUri = FileProvider.getUriForFile(context, context.getString(C0734R.string.provider_authority), new File(media.getLocalMediaUrl()));
        i.putExtra("android.intent.extra.STREAM", fileUri);
        i.addFlags(1);
        if (VERSION.SDK_INT <= 19) {
            for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentActivities(i, 65536)) {
                context.grantUriPermission(resolveInfo.activityInfo.packageName, fileUri, 1);
            }
        }
        context.startActivity(Intent.createChooser(i, context.getString(C0734R.string.share_file_label)));
        Log.e(TAG, "shareFeedItemFile called");
    }
}

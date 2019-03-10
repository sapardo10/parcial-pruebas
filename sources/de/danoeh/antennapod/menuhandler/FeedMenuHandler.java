package de.danoeh.antennapod.menuhandler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import de.danoeh.antennapod.core.dialog.ConfirmationDialog;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.DownloadRequestException;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.core.util.ShareUtils;
import de.danoeh.antennapod.debug.R;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FeedMenuHandler {
    private static final String TAG = "FeedMenuHandler";

    private FeedMenuHandler() {
    }

    public static boolean onCreateOptionsMenu(MenuInflater inflater, Menu menu) {
        inflater.inflate(R.menu.feedlist, menu);
        return true;
    }

    public static boolean onPrepareOptionsMenu(Menu menu, Feed selectedFeed) {
        if (selectedFeed == null) {
            return true;
        }
        Log.d(TAG, "Preparing options menu");
        if (selectedFeed.getPaymentLink() == null || !selectedFeed.getFlattrStatus().flattrable()) {
            menu.findItem(R.id.support_item).setVisible(false);
        } else {
            menu.findItem(R.id.support_item).setVisible(true);
        }
        menu.findItem(R.id.refresh_complete_item).setVisible(selectedFeed.isPaged());
        return true;
    }

    public static boolean onOptionsItemClicked(Context context, MenuItem item, final Feed selectedFeed) throws DownloadRequestException {
        switch (item.getItemId()) {
            case R.id.filter_items:
                showFilterDialog(context, selectedFeed);
                break;
            case R.id.mark_all_read_item:
                new ConfirmationDialog(context, R.string.mark_all_read_label, R.string.mark_all_read_feed_confirmation_msg) {
                    public void onConfirmButtonPressed(DialogInterface dialog) {
                        dialog.dismiss();
                        DBWriter.markFeedRead(selectedFeed.getId());
                    }
                }.createNewDialog().show();
                break;
            case R.id.refresh_complete_item:
                DBTasks.forceRefreshCompleteFeed(context, selectedFeed);
                break;
            case R.id.refresh_item:
                DBTasks.forceRefreshFeed(context, selectedFeed);
                break;
            case R.id.share_download_url_item:
                ShareUtils.shareFeedDownloadLink(context, selectedFeed);
                break;
            case R.id.share_link_item:
                ShareUtils.shareFeedlink(context, selectedFeed);
                break;
            case R.id.support_item:
                DBTasks.flattrFeedIfLoggedIn(context, selectedFeed);
                break;
            case R.id.visit_website_item:
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(selectedFeed.getLink()));
                if (!IntentUtils.isCallable(context, intent)) {
                    Toast.makeText(context, context.getString(R.string.download_error_malformed_url), 0).show();
                    break;
                }
                context.startActivity(intent);
                break;
            default:
                return false;
        }
        return true;
    }

    private static void showFilterDialog(Context context, Feed feed) {
        CharSequence[] items = context.getResources().getStringArray(R.array.episode_filter_options);
        String[] values = context.getResources().getStringArray(R.array.episode_filter_values);
        boolean[] checkedItems = new boolean[items.length];
        Set<String> filter = new HashSet(Arrays.asList(feed.getItemFilter().getValues()));
        Iterator<String> it = filter.iterator();
        while (it.hasNext()) {
            if (TextUtils.isEmpty((CharSequence) it.next())) {
                it.remove();
            }
        }
        for (int i = 0; i < values.length; i++) {
            if (filter.contains(values[i])) {
                checkedItems[i] = true;
            }
        }
        Builder builder = new Builder(context);
        builder.setTitle((int) R.string.filter);
        builder.setMultiChoiceItems(items, checkedItems, new -$$Lambda$FeedMenuHandler$8vPmhob_n--FLJ8SJXUOKZu-7dE(filter, values));
        builder.setPositiveButton((int) R.string.confirm_label, new -$$Lambda$FeedMenuHandler$kl1tgKIylLfhjSEbx5s1oEke3Mg(feed, filter));
        builder.setNegativeButton((int) R.string.cancel_label, null);
        builder.create().show();
    }

    static /* synthetic */ void lambda$showFilterDialog$0(Set filter, String[] values, DialogInterface dialog, int which, boolean isChecked) {
        if (isChecked) {
            filter.add(values[which]);
        } else {
            filter.remove(values[which]);
        }
    }

    static /* synthetic */ void lambda$showFilterDialog$1(Feed feed, Set filter, DialogInterface dialog, int which) {
        feed.setItemFilter((String[]) filter.toArray(new String[filter.size()]));
        DBWriter.setFeedItemsFilter(feed.getId(), filter);
    }
}

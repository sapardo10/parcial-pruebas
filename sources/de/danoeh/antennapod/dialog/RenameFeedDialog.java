package de.danoeh.antennapod.dialog;

import android.app.Activity;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.debug.R;
import java.lang.ref.WeakReference;

public class RenameFeedDialog {
    private final WeakReference<Activity> activityRef;
    private final Feed feed;

    public RenameFeedDialog(Activity activity, Feed feed) {
        this.activityRef = new WeakReference(activity);
        this.feed = feed;
    }

    public void show() {
        Activity activity = (Activity) this.activityRef.get();
        if (activity != null) {
            new Builder(activity).title((int) R.string.rename_feed_label).inputType(1).input(this.feed.getTitle(), this.feed.getTitle(), true, new -$$Lambda$RenameFeedDialog$6j0-T65y_fhZpRBkTzJSqD6K35M()).neutralText((int) R.string.reset).onNeutral(new -$$Lambda$RenameFeedDialog$fM7j8FvgpF4I69YD88k_Au0RGo0()).negativeText((int) R.string.cancel_label).onNegative(-$$Lambda$RenameFeedDialog$euMp-9L63cBUm0UaAAiH9em0-NI.INSTANCE).autoDismiss(false).show();
        }
    }

    public static /* synthetic */ void lambda$show$0(RenameFeedDialog renameFeedDialog, MaterialDialog dialog, CharSequence input) {
        renameFeedDialog.feed.setCustomTitle(input.toString());
        DBWriter.setFeedCustomTitle(renameFeedDialog.feed);
        dialog.dismiss();
    }
}

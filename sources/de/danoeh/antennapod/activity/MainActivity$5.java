package de.danoeh.antennapod.activity;

import android.content.Context;
import android.content.DialogInterface;
import de.danoeh.antennapod.core.dialog.ConfirmationDialog;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.storage.DBWriter;

class MainActivity$5 extends ConfirmationDialog {
    final /* synthetic */ MainActivity this$0;
    final /* synthetic */ Feed val$feed;

    MainActivity$5(MainActivity this$0, Context x0, int x1, int x2, Feed feed) {
        this.this$0 = this$0;
        this.val$feed = feed;
        super(x0, x1, x2);
    }

    public void onConfirmButtonPressed(DialogInterface dialog) {
        dialog.dismiss();
        DBWriter.markFeedRead(this.val$feed.getId());
    }
}

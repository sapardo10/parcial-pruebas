package de.danoeh.antennapod.core.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.util.IntentUtils;

public class FeedRemover extends AsyncTask<Void, Void, Void> {
    private final Context context;
    private ProgressDialog dialog;
    private final Feed feed;
    public boolean skipOnCompletion = false;

    public FeedRemover(Context context, Feed feed) {
        this.context = context;
        this.feed = feed;
    }

    protected Void doInBackground(Void... params) {
        try {
            DBWriter.deleteFeed(this.context, this.feed.getId()).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void result) {
        ProgressDialog progressDialog = this.dialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            this.dialog.dismiss();
        }
        if (this.skipOnCompletion) {
            IntentUtils.sendLocalBroadcast(this.context, PlaybackService.ACTION_SKIP_CURRENT_EPISODE);
        }
    }

    protected void onPreExecute() {
        this.dialog = new ProgressDialog(this.context);
        this.dialog.setMessage(this.context.getString(C0734R.string.feed_remover_msg));
        this.dialog.setIndeterminate(true);
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    public void executeAsync() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }
}

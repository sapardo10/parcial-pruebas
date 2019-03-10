package de.danoeh.antennapod.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import de.danoeh.antennapod.activity.OpmlImportHolder;
import de.danoeh.antennapod.core.export.opml.OpmlElement;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.storage.DownloadRequestException;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.debug.R;
import java.util.Arrays;

public class OpmlFeedQueuer extends AsyncTask<Void, Void, Void> {
    private final Context context;
    private ProgressDialog progDialog;
    private final int[] selection;

    public OpmlFeedQueuer(Context context, int[] selection) {
        this.context = context;
        this.selection = Arrays.copyOf(selection, selection.length);
    }

    protected void onPostExecute(Void result) {
        this.progDialog.dismiss();
    }

    protected void onPreExecute() {
        this.progDialog = new ProgressDialog(this.context);
        this.progDialog.setMessage(this.context.getString(R.string.processing_label));
        this.progDialog.setCancelable(false);
        this.progDialog.setIndeterminate(true);
        this.progDialog.show();
    }

    protected Void doInBackground(Void... params) {
        DownloadRequester requester = DownloadRequester.getInstance();
        for (int selected : this.selection) {
            OpmlElement element = (OpmlElement) OpmlImportHolder.getReadElements().get(selected);
            try {
                requester.downloadFeed(this.context.getApplicationContext(), new Feed(element.getXmlUrl(), null, element.getText()));
            } catch (DownloadRequestException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void executeAsync() {
        executeOnExecutor(THREAD_POOL_EXECUTOR, new Void[0]);
    }
}

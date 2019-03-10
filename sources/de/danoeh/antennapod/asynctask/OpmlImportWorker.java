package de.danoeh.antennapod.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import de.danoeh.antennapod.core.export.opml.OpmlElement;
import de.danoeh.antennapod.core.export.opml.OpmlReader;
import de.danoeh.antennapod.debug.R;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;

public class OpmlImportWorker extends AsyncTask<Void, Void, ArrayList<OpmlElement>> {
    private static final String TAG = "OpmlImportWorker";
    private final Context context;
    private Exception exception;
    private final Reader mReader;
    private ProgressDialog progDialog;

    public OpmlImportWorker(Context context, Reader reader) {
        this.context = context;
        this.mReader = reader;
    }

    protected ArrayList<OpmlElement> doInBackground(Void... params) {
        Log.d(TAG, "Starting background work");
        if (this.mReader == null) {
            return null;
        }
        try {
            ArrayList<OpmlElement> result = new OpmlReader().readDocument(this.mReader);
            this.mReader.close();
            return result;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            this.exception = e;
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            this.exception = e2;
            return null;
        }
    }

    protected void onPostExecute(ArrayList<OpmlElement> arrayList) {
        Reader reader = this.mReader;
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.progDialog.dismiss();
        if (this.exception != null) {
            Log.d(TAG, "An error occurred while trying to parse the opml document");
            Builder alert = new Builder(this.context);
            alert.setTitle((int) R.string.error_label);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.context.getString(R.string.opml_reader_error));
            stringBuilder.append(this.exception.getMessage());
            alert.setMessage(stringBuilder.toString());
            alert.setNeutralButton(17039370, -$$Lambda$OpmlImportWorker$7S1vOQ5JPaw7j3pbI23HFDNzjYw.INSTANCE);
            alert.create().show();
        }
    }

    protected void onPreExecute() {
        this.progDialog = new ProgressDialog(this.context);
        this.progDialog.setMessage(this.context.getString(R.string.reading_opml_label));
        this.progDialog.setIndeterminate(true);
        this.progDialog.setCancelable(false);
        this.progDialog.show();
    }

    public boolean wasSuccessful() {
        return this.exception != null;
    }

    public void executeAsync() {
        executeOnExecutor(THREAD_POOL_EXECUTOR, new Void[0]);
    }
}

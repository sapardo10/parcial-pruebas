package de.danoeh.antennapod.core.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import de.danoeh.antennapod.core.BuildConfig;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.util.flattr.FlattrUtils;
import org.shredzone.flattr4j.exception.FlattrException;
import org.shredzone.flattr4j.oauth.AccessToken;
import org.shredzone.flattr4j.oauth.AndroidAuthenticator;

public class FlattrTokenFetcher extends AsyncTask<Void, Void, AccessToken> {
    private static final String TAG = "FlattrTokenFetcher";
    private final AndroidAuthenticator auth;
    private final Context context;
    private ProgressDialog dialog;
    private FlattrException exception;
    private AccessToken token;
    private final Uri uri;

    public FlattrTokenFetcher(Context context, AndroidAuthenticator auth, Uri uri) {
        this.context = context;
        this.auth = auth;
        this.uri = uri;
    }

    protected void onPostExecute(AccessToken result) {
        if (result != null) {
            FlattrUtils.storeToken(result);
        }
        this.dialog.dismiss();
        FlattrException flattrException = this.exception;
        if (flattrException == null) {
            ClientConfig.flattrCallbacks.handleFlattrAuthenticationSuccess(result);
        } else {
            FlattrUtils.showErrorDialog(this.context, flattrException.getMessage());
        }
    }

    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog = new ProgressDialog(this.context);
        this.dialog.setMessage(this.context.getString(C0734R.string.processing_label));
        this.dialog.setIndeterminate(true);
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    protected AccessToken doInBackground(Void... params) {
        try {
            this.token = this.auth.fetchAccessToken(this.uri);
            if (this.token != null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Successfully got token");
                }
                return this.token;
            }
            Log.w(TAG, "Flattr token was null");
            return null;
        } catch (FlattrException e) {
            e.printStackTrace();
            this.exception = e;
            return null;
        }
    }

    public void executeAsync() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }
}

package de.danoeh.antennapod.core.util.flattr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.util.Log;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.asynctask.FlattrTokenFetcher;
import de.danoeh.antennapod.core.storage.DBWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import org.apache.commons.lang3.time.DateUtils;
import org.shredzone.flattr4j.FlattrService;
import org.shredzone.flattr4j.exception.FlattrException;
import org.shredzone.flattr4j.model.Flattr;
import org.shredzone.flattr4j.model.Thing;
import org.shredzone.flattr4j.oauth.AccessToken;
import org.shredzone.flattr4j.oauth.AndroidAuthenticator;
import org.shredzone.flattr4j.oauth.Scope;

public class FlattrUtils {
    private static final String HOST_NAME = "de.danoeh.antennapod";
    private static final String PREF_ACCESS_TOKEN = "de.danoeh.antennapod.preference.flattrAccessToken";
    private static final String TAG = "FlattrUtils";
    private static volatile AccessToken cachedToken;

    private FlattrUtils() {
    }

    private static AndroidAuthenticator createAuthenticator() {
        return new AndroidAuthenticator(HOST_NAME, ClientConfig.flattrCallbacks.getFlattrAppKey(), ClientConfig.flattrCallbacks.getFlattrAppSecret());
    }

    public static void startAuthProcess(Context context) throws FlattrException {
        AndroidAuthenticator auth = createAuthenticator();
        auth.setScope(EnumSet.of(Scope.FLATTR));
        context.startActivity(auth.createAuthenticateIntent());
    }

    private static AccessToken retrieveToken() {
        if (cachedToken == null) {
            Log.d(TAG, "Retrieving access token");
            String token = PreferenceManager.getDefaultSharedPreferences(ClientConfig.applicationCallbacks.getApplicationInstance()).getString(PREF_ACCESS_TOKEN, null);
            if (token != null) {
                Log.d(TAG, "Found access token. Caching.");
                cachedToken = new AccessToken(token);
            } else {
                Log.d(TAG, "No access token found");
                return null;
            }
        }
        return cachedToken;
    }

    public static boolean hasAPICredentials() {
        if (!TextUtils.isEmpty(ClientConfig.flattrCallbacks.getFlattrAppKey())) {
            if (!TextUtils.isEmpty(ClientConfig.flattrCallbacks.getFlattrAppSecret())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasToken() {
        return retrieveToken() != null;
    }

    public static void storeToken(AccessToken token) {
        Log.d(TAG, "Storing token");
        Editor editor = PreferenceManager.getDefaultSharedPreferences(ClientConfig.applicationCallbacks.getApplicationInstance()).edit();
        if (token != null) {
            editor.putString(PREF_ACCESS_TOKEN, token.getToken());
        } else {
            editor.putString(PREF_ACCESS_TOKEN, null);
        }
        editor.commit();
        cachedToken = token;
    }

    private static void deleteToken() {
        Log.d(TAG, "Deleting flattr token");
        storeToken(null);
    }

    public static void clickUrl(Context context, String url) throws FlattrException {
        if (hasToken()) {
            FlattrServiceCreator.getService(retrieveToken()).flattr(url);
        } else {
            Log.e(TAG, "clickUrl was called with null access token");
        }
    }

    public static List<Flattr> retrieveFlattredThings() throws FlattrException {
        ArrayList<Flattr> myFlattrs = new ArrayList();
        if (hasToken()) {
            FlattrService fs = FlattrServiceCreator.getService(retrieveToken());
            Calendar firstOfMonth = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            firstOfMonth.set(14, 0);
            firstOfMonth.set(13, 0);
            firstOfMonth.set(12, 0);
            firstOfMonth.set(11, 0);
            firstOfMonth.set(5, Calendar.getInstance().getActualMinimum(5));
            Date firstOfMonthDate = new Date(firstOfMonth.getTime().getTime() - DateUtils.MILLIS_PER_HOUR);
            for (int page = 0; page < 5; page++) {
                for (Flattr fl : fs.getMyFlattrs(Integer.valueOf(30), Integer.valueOf(page))) {
                    if (!fl.getCreated().after(firstOfMonthDate)) {
                        break;
                    }
                    myFlattrs.add(fl);
                }
            }
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Got my flattrs list of length ");
            stringBuilder.append(Integer.toString(myFlattrs.size()));
            stringBuilder.append(" comparison date");
            stringBuilder.append(firstOfMonthDate);
            Log.d(str, stringBuilder.toString());
            Iterator it = myFlattrs.iterator();
            while (it.hasNext()) {
                Flattr fl2 = (Flattr) it.next();
                Thing thing = fl2.getThing();
                String str2 = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Flattr thing: ");
                stringBuilder2.append(fl2.getThingId());
                stringBuilder2.append(" name: ");
                stringBuilder2.append(thing.getTitle());
                stringBuilder2.append(" url: ");
                stringBuilder2.append(thing.getUrl());
                stringBuilder2.append(" on: ");
                stringBuilder2.append(fl2.getCreated());
                Log.d(str2, stringBuilder2.toString());
            }
        } else {
            Log.e(TAG, "retrieveFlattrdThings was called with null access token");
        }
        return myFlattrs;
    }

    public static void handleCallback(Context context, Uri uri) {
        new FlattrTokenFetcher(context, createAuthenticator(), uri).executeAsync();
    }

    public static void revokeAccessToken(Context context) {
        Log.d(TAG, "Revoking access token");
        deleteToken();
        FlattrServiceCreator.deleteFlattrService();
        showRevokeDialog(context);
        DBWriter.clearAllFlattrStatus();
    }

    private static void showRevokeDialog(Context context) {
        Builder builder = new Builder(context);
        builder.setTitle(C0734R.string.access_revoked_title);
        builder.setMessage(C0734R.string.access_revoked_info);
        builder.setNeutralButton(17039370, -$$Lambda$FlattrUtils$-muzX1VI7RY3S2A8wVr5IEZSYK4.INSTANCE);
        builder.create().show();
    }

    public static void showNoTokenDialogOrRedirect(Context context, String url) {
        if (hasAPICredentials()) {
            Builder builder = new Builder(context);
            builder.setTitle(C0734R.string.no_flattr_token_title);
            builder.setMessage(C0734R.string.no_flattr_token_msg);
            builder.setPositiveButton(C0734R.string.authenticate_now_label, new -$$Lambda$FlattrUtils$gx_cPjJUXcYTioYOADAnnT_EYfA(context));
            builder.setNegativeButton(C0734R.string.visit_website_label, new -$$Lambda$FlattrUtils$_v89GxL_0DIL0QUhXwJOJHwC9JU(url, context));
            builder.create().show();
            return;
        }
        context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
    }

    public static void showErrorDialog(Context context, String msg) {
        Builder builder = new Builder(context);
        builder.setTitle(C0734R.string.error_label);
        builder.setMessage((CharSequence) msg);
        builder.setNeutralButton(17039370, -$$Lambda$FlattrUtils$5A5lE0gRPpW9-3MmdjB9SPsL3mE.INSTANCE);
        builder.create().show();
    }
}

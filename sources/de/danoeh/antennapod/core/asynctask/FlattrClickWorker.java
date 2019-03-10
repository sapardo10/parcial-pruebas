package de.danoeh.antennapod.core.asynctask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.Toast;
import de.danoeh.antennapod.core.BuildConfig;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.util.NetworkUtils;
import de.danoeh.antennapod.core.util.flattr.FlattrThing;
import de.danoeh.antennapod.core.util.flattr.FlattrUtils;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.shredzone.flattr4j.exception.FlattrException;

public class FlattrClickWorker extends AsyncTask<Void, Integer, ExitCode> {
    private static final int NOTIFICATION_ID = 4;
    private static final String TAG = "FlattrClickWorker";
    private final Context context;
    private final AtomicInteger countFailed;
    private final AtomicInteger countSuccess;
    private volatile FlattrException exception;
    private volatile FlattrThing extraFlattrThing;

    public enum ExitCode {
        EXIT_NORMAL,
        NO_TOKEN,
        NO_NETWORK,
        NO_THINGS
    }

    public FlattrClickWorker(@NonNull Context context) {
        this.countFailed = new AtomicInteger();
        this.countSuccess = new AtomicInteger();
        this.context = context.getApplicationContext();
    }

    public FlattrClickWorker(Context context, FlattrThing extraFlattrThing) {
        this(context);
        this.extraFlattrThing = extraFlattrThing;
    }

    protected ExitCode doInBackground(Void... params) {
        if (!FlattrUtils.hasToken()) {
            return ExitCode.NO_TOKEN;
        }
        if (!NetworkUtils.networkAvailable()) {
            return ExitCode.NO_NETWORK;
        }
        List<FlattrThing> flattrQueue = DBReader.getFlattrQueue();
        if (this.extraFlattrThing != null) {
            flattrQueue.add(this.extraFlattrThing);
        } else if (flattrQueue.size() == 1) {
            this.extraFlattrThing = (FlattrThing) flattrQueue.get(0);
        }
        if (flattrQueue.isEmpty()) {
            return ExitCode.NO_THINGS;
        }
        List<Future<?>> dbFutures = new LinkedList();
        for (FlattrThing thing : flattrQueue) {
            if (BuildConfig.DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Processing ");
                stringBuilder.append(thing.getTitle());
                Log.d(str, stringBuilder.toString());
            }
            try {
                thing.getFlattrStatus().setUnflattred();
                FlattrUtils.clickUrl(this.context, thing.getPaymentLink());
                thing.getFlattrStatus().setFlattred();
                publishProgress(new Integer[]{Integer.valueOf(C0734R.string.flattr_click_success)});
                this.countSuccess.incrementAndGet();
            } catch (FlattrException e) {
                e.printStackTrace();
                if (this.countFailed.incrementAndGet() == 1) {
                    this.exception = e;
                }
            }
            Future<?> f = DBWriter.setFlattredStatus(this.context, thing, false);
            if (f != null) {
                dbFutures.add(f);
            }
        }
        for (Future<?> f2 : dbFutures) {
            try {
                f2.get();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return ExitCode.EXIT_NORMAL;
    }

    protected void onPostExecute(ExitCode exitCode) {
        super.onPostExecute(exitCode);
        switch (exitCode) {
            case EXIT_NORMAL:
                if (this.countFailed.get() > 0) {
                    postFlattrFailedNotification();
                    return;
                }
                return;
            case NO_NETWORK:
                postToastNotification(C0734R.string.flattr_click_enqueued);
                return;
            case NO_TOKEN:
                postNoTokenNotification();
                return;
            default:
                return;
        }
    }

    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        postToastNotification(values[0].intValue());
    }

    private void postToastNotification(int msg) {
        Context context = this.context;
        Toast.makeText(context, context.getString(msg), 1).show();
    }

    private void postNoTokenNotification() {
        ((NotificationManager) this.context.getSystemService("notification")).notify(4, new Builder(this.context, "error").setStyle(new BigTextStyle().bigText(this.context.getString(C0734R.string.no_flattr_token_notification_msg))).setContentIntent(PendingIntent.getActivity(this.context, 0, ClientConfig.flattrCallbacks.getFlattrAuthenticationActivityIntent(this.context), 0)).setContentTitle(this.context.getString(C0734R.string.no_flattr_token_title)).setTicker(this.context.getString(C0734R.string.no_flattr_token_title)).setSmallIcon(C0734R.drawable.stat_notify_sync_error).setOngoing(false).setAutoCancel(true).build());
    }

    private void postFlattrFailedNotification() {
        int failed = this.countFailed.get();
        if (failed != 0) {
            String title;
            String exceptionMsg;
            PendingIntent contentIntent = ClientConfig.flattrCallbacks.getFlattrFailedNotificationContentIntent(this.context);
            if (failed == 1) {
                title = this.context.getString(C0734R.string.flattrd_failed_label);
                exceptionMsg = this.exception.getMessage() != null ? this.exception.getMessage() : "";
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.context.getString(C0734R.string.flattr_click_failure, new Object[]{this.extraFlattrThing.getTitle()}));
                stringBuilder.append("\n");
                stringBuilder.append(exceptionMsg);
                exceptionMsg = stringBuilder.toString();
            } else {
                title = this.context.getString(C0734R.string.flattrd_label);
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(this.context.getString(C0734R.string.flattr_click_success_count, new Object[]{Integer.valueOf(this.countSuccess.get())}));
                stringBuilder2.append("\n");
                stringBuilder2.append(this.context.getString(C0734R.string.flattr_click_failure_count, new Object[]{Integer.valueOf(failed)}));
                exceptionMsg = stringBuilder2.toString();
            }
            ((NotificationManager) this.context.getSystemService("notification")).notify(4, new Builder(this.context, "error").setStyle(new BigTextStyle().bigText(exceptionMsg)).setContentIntent(contentIntent).setContentTitle(title).setTicker(title).setSmallIcon(C0734R.drawable.stat_notify_sync_error).setOngoing(false).setAutoCancel(true).build());
        }
    }

    public void executeAsync() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }
}

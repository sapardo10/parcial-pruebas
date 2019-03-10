package de.danoeh.antennapod.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.google.android.exoplayer2.C0555C;
import de.danoeh.antennapod.debug.R;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class RatingDialog {
    private static final int AFTER_DAYS = 7;
    private static final String KEY_FIRST_START_DATE = "KEY_FIRST_HIT_DATE";
    private static final String KEY_RATED = "KEY_WAS_RATED";
    private static final String PREFS_NAME = "RatingPrefs";
    private static final String TAG = RatingDialog.class.getSimpleName();
    private static WeakReference<Context> mContext;
    private static Dialog mDialog;
    private static SharedPreferences mPreferences;

    private RatingDialog() {
    }

    public static void init(Context context) {
        mContext = new WeakReference(context);
        mPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        if (mPreferences.getLong(KEY_FIRST_START_DATE, 0) == 0) {
            resetStartDate();
        }
    }

    public static void check() {
        Dialog dialog = mDialog;
        if (dialog == null || !dialog.isShowing()) {
            if (shouldShow()) {
                try {
                    mDialog = createDialog();
                    if (mDialog != null) {
                        mDialog.show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }
    }

    private static void rateNow() {
        Context context = (Context) mContext.get();
        if (context != null) {
            String appPackage = "de.danoeh.antennapod";
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=de.danoeh.antennapod"));
            intent.setFlags(C0555C.ENCODING_PCM_MU_LAW);
            context.startActivity(intent);
            saveRated();
        }
    }

    private static boolean rated() {
        return mPreferences.getBoolean(KEY_RATED, false);
    }

    private static void saveRated() {
        mPreferences.edit().putBoolean(KEY_RATED, true).apply();
    }

    private static void resetStartDate() {
        mPreferences.edit().putLong(KEY_FIRST_START_DATE, System.currentTimeMillis()).apply();
    }

    private static boolean shouldShow() {
        boolean z = false;
        if (rated()) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (TimeUnit.DAYS.convert(now - mPreferences.getLong(KEY_FIRST_START_DATE, now), TimeUnit.MILLISECONDS) >= 7) {
            z = true;
        }
        return z;
    }

    @Nullable
    private static MaterialDialog createDialog() {
        Context context = (Context) mContext.get();
        if (context == null) {
            return null;
        }
        return new Builder(context).title((int) R.string.rating_title).content((int) R.string.rating_message).positiveText((int) R.string.rating_now_label).negativeText((int) R.string.rating_never_label).neutralText((int) R.string.rating_later_label).onPositive(-$$Lambda$RatingDialog$oQkeoHxAelYeE10J_OR_0WkThkE.INSTANCE).onNegative(-$$Lambda$RatingDialog$UXlhRD8jLtRAGPc1YKaO-MIAWs0.INSTANCE).onNeutral(-$$Lambda$RatingDialog$Tmp40985_k1AUI0DLYFrkOTzFEo.INSTANCE).cancelListener(-$$Lambda$RatingDialog$lwwGxiaH-Qp8hLXQkXY_xDYtbKw.INSTANCE).build();
    }
}

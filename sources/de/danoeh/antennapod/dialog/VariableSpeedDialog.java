package de.danoeh.antennapod.dialog;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.debug.R;
import java.util.Arrays;
import java.util.List;
import org.antennapod.audio.MediaPlayer;

public class VariableSpeedDialog {
    private static final String TAG = VariableSpeedDialog.class.getSimpleName();
    private static final Intent playStoreIntent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.falconware.prestissimo"));

    private VariableSpeedDialog() {
    }

    public static void showDialog(Context context) {
        if (!MediaPlayer.isPrestoLibraryInstalled(context)) {
            if (!UserPreferences.useSonic()) {
                if (VERSION.SDK_INT < 23) {
                    showGetPluginDialog(context, true);
                    return;
                }
            }
        }
        showSpeedSelectorDialog(context);
    }

    public static void showGetPluginDialog(Context context) {
        showGetPluginDialog(context, false);
    }

    private static void showGetPluginDialog(Context context, boolean showSpeedSelector) {
        Builder builder = new Builder(context);
        builder.title((int) R.string.no_playback_plugin_title);
        builder.content((int) R.string.no_playback_plugin_or_sonic_msg);
        builder.positiveText((int) R.string.enable_sonic);
        builder.negativeText((int) R.string.download_plugin_label);
        builder.neutralText((int) R.string.close_label);
        builder.onPositive(new -$$Lambda$VariableSpeedDialog$4p45gT3il_hfhQi82cwVRrS79ts(showSpeedSelector, context));
        builder.onNegative(new -$$Lambda$VariableSpeedDialog$CR_h-vOU7fmZLWoAs62zSNp2zF4(context));
        builder.forceStacking(true);
        MaterialDialog dialog = builder.show();
        if (VERSION.SDK_INT < 16) {
            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        }
        if (!IntentUtils.isCallable(context.getApplicationContext(), playStoreIntent)) {
            dialog.getActionButton(DialogAction.NEGATIVE).setEnabled(false);
        }
    }

    static /* synthetic */ void lambda$showGetPluginDialog$0(boolean showSpeedSelector, Context context, MaterialDialog dialog, DialogAction which) {
        if (VERSION.SDK_INT >= 16) {
            UserPreferences.enableSonic();
            if (showSpeedSelector) {
                showSpeedSelectorDialog(context);
            }
        }
    }

    static /* synthetic */ void lambda$showGetPluginDialog$1(Context context, MaterialDialog dialog, DialogAction which) {
        try {
            context.startActivity(playStoreIntent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private static void showSpeedSelectorDialog(Context context) {
        String[] speedValues = context.getResources().getStringArray(R.array.playback_speed_values);
        boolean[] speedChecked = new boolean[speedValues.length];
        List<String> selectedSpeedList = Arrays.asList(UserPreferences.getPlaybackSpeedArray());
        for (int i = 0; i < speedValues.length; i++) {
            speedChecked[i] = selectedSpeedList.contains(speedValues[i]);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle((int) R.string.set_playback_speed_label);
        builder.setMultiChoiceItems((int) R.array.playback_speed_values, speedChecked, new -$$Lambda$VariableSpeedDialog$xMLtPuKzi1I-IyUOy8jyJdwj2Yo(speedChecked));
        builder.setNegativeButton(17039360, null);
        builder.setPositiveButton(17039370, new -$$Lambda$VariableSpeedDialog$qcCbhh-qprGMAw-zQ82RuRZwh_8(speedChecked, speedValues));
        builder.create().show();
    }

    static /* synthetic */ void lambda$showSpeedSelectorDialog$3(boolean[] speedChecked, String[] speedValues, DialogInterface dialog, int which) {
        int choiceCount = 0;
        for (boolean checked : speedChecked) {
            if (checked) {
                choiceCount++;
            }
        }
        String[] newSpeedValues = new String[choiceCount];
        int i = 0;
        for (int i2 = 0; i2 < speedChecked.length; i2++) {
            if (speedChecked[i2]) {
                int newSpeedIndex = i + 1;
                newSpeedValues[i] = speedValues[i2];
                i = newSpeedIndex;
            }
        }
        UserPreferences.setPlaybackSpeedArray(newSpeedValues);
    }
}

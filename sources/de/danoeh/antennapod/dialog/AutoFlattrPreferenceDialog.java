package de.danoeh.antennapod.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog.Builder;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.debug.R;
import org.apache.commons.lang3.Validate;

public class AutoFlattrPreferenceDialog {

    public interface AutoFlattrPreferenceDialogInterface {
        void onCancelled();

        void onConfirmed(boolean z, float f);
    }

    private AutoFlattrPreferenceDialog() {
    }

    public static void newAutoFlattrPreferenceDialog(final Activity activity, AutoFlattrPreferenceDialogInterface callback) {
        Validate.notNull(activity);
        Validate.notNull(callback);
        Builder builder = new Builder(activity);
        View view = activity.getLayoutInflater().inflate(R.layout.autoflattr_preference_dialog, null);
        CheckBox chkAutoFlattr = (CheckBox) view.findViewById(R.id.chkAutoFlattr);
        SeekBar skbPercent = (SeekBar) view.findViewById(R.id.skbPercent);
        final TextView txtvStatus = (TextView) view.findViewById(R.id.txtvStatus);
        chkAutoFlattr.setChecked(UserPreferences.isAutoFlattr());
        skbPercent.setEnabled(chkAutoFlattr.isChecked());
        txtvStatus.setEnabled(chkAutoFlattr.isChecked());
        int initialValue = (int) (UserPreferences.getAutoFlattrPlayedDurationThreshold() * 1120403456);
        setStatusMsgText(activity, txtvStatus, initialValue);
        skbPercent.setProgress(initialValue);
        chkAutoFlattr.setOnClickListener(new -$$Lambda$AutoFlattrPreferenceDialog$wFN9rVxMEyBB1TU76Leb1Mg4svw(skbPercent, chkAutoFlattr, txtvStatus));
        skbPercent.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                AutoFlattrPreferenceDialog.setStatusMsgText(activity, txtvStatus, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        builder.setTitle((int) R.string.pref_auto_flattr_title).setView(view).setPositiveButton((int) R.string.confirm_label, new -$$Lambda$AutoFlattrPreferenceDialog$L5jZ1BWNBHmOAJBz2dUFa82XLo0(skbPercent, callback, chkAutoFlattr)).setNegativeButton((int) R.string.cancel_label, new -$$Lambda$AutoFlattrPreferenceDialog$ZJkwzYTJHwEIJICWA-KmEg4ePcQ(callback)).setCancelable(false).show();
    }

    static /* synthetic */ void lambda$newAutoFlattrPreferenceDialog$0(SeekBar skbPercent, CheckBox chkAutoFlattr, TextView txtvStatus, View v) {
        skbPercent.setEnabled(chkAutoFlattr.isChecked());
        txtvStatus.setEnabled(chkAutoFlattr.isChecked());
    }

    static /* synthetic */ void lambda$newAutoFlattrPreferenceDialog$1(SeekBar skbPercent, AutoFlattrPreferenceDialogInterface callback, CheckBox chkAutoFlattr, DialogInterface dialog, int which) {
        callback.onConfirmed(chkAutoFlattr.isChecked(), ((float) skbPercent.getProgress()) / 100.0f);
        dialog.dismiss();
    }

    static /* synthetic */ void lambda$newAutoFlattrPreferenceDialog$2(AutoFlattrPreferenceDialogInterface callback, DialogInterface dialog, int which) {
        callback.onCancelled();
        dialog.dismiss();
    }

    private static void setStatusMsgText(Context context, TextView txtvStatus, int progress) {
        if (progress == 0) {
            txtvStatus.setText(R.string.auto_flattr_ater_beginning);
        } else if (progress == 100) {
            txtvStatus.setText(R.string.auto_flattr_ater_end);
        } else {
            txtvStatus.setText(context.getString(R.string.auto_flattr_after_percent, new Object[]{Integer.valueOf(progress)}));
        }
    }
}

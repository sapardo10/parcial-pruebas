package de.danoeh.antennapod.core.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import de.danoeh.antennapod.core.C0734R;

public abstract class ConfirmationDialog {
    private static final String TAG = ConfirmationDialog.class.getSimpleName();
    private final Context context;
    private final String message;
    private int negativeText;
    private int positiveText;
    private final int titleId;

    public abstract void onConfirmButtonPressed(DialogInterface dialogInterface);

    public ConfirmationDialog(Context context, int titleId, int messageId) {
        this(context, titleId, context.getString(messageId));
    }

    public ConfirmationDialog(Context context, int titleId, String message) {
        this.context = context;
        this.titleId = titleId;
        this.message = message;
    }

    private void onCancelButtonPressed(DialogInterface dialog) {
        Log.d(TAG, "Dialog was cancelled");
        dialog.dismiss();
    }

    public void setPositiveText(int id) {
        this.positiveText = id;
    }

    public void setNegativeText(int id) {
        this.negativeText = id;
    }

    public final AlertDialog createNewDialog() {
        Builder builder = new Builder(this.context);
        builder.setTitle(this.titleId);
        builder.setMessage(this.message);
        int i = this.positiveText;
        if (i == 0) {
            i = C0734R.string.confirm_label;
        }
        builder.setPositiveButton(i, new -$$Lambda$ConfirmationDialog$Vplom3vxt3FIdlWqiENHduZboYs());
        i = this.negativeText;
        if (i == 0) {
            i = C0734R.string.cancel_label;
        }
        builder.setNegativeButton(i, new -$$Lambda$ConfirmationDialog$ETtxpsoEXhW_379n31OTo7zEGCM());
        builder.setOnCancelListener(new -$$Lambda$ConfirmationDialog$tf2Kp94g-5DgxIN1o-Sgp7V53U4());
        return builder.create();
    }
}

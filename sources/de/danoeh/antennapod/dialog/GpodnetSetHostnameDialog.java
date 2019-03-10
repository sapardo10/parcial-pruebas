package de.danoeh.antennapod.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import de.danoeh.antennapod.core.gpoddernet.GpodnetService;
import de.danoeh.antennapod.core.preferences.GpodnetPreferences;
import de.danoeh.antennapod.debug.R;

public class GpodnetSetHostnameDialog {
    private static final String TAG = "GpodnetSetHostnameDialog";

    private GpodnetSetHostnameDialog() {
    }

    public static AlertDialog createDialog(Context context) {
        Builder dialog = new Builder(context);
        EditText et = new EditText(context);
        et.setText(GpodnetPreferences.getHostname());
        et.setInputType(16);
        dialog.setTitle((int) R.string.pref_gpodnet_sethostname_title).setView(setupContentView(context, et)).setPositiveButton((int) R.string.confirm_label, new -$$Lambda$GpodnetSetHostnameDialog$Bgd5hOkclAvmJaWrWJUq8s9AyQA(et)).setNegativeButton((int) R.string.cancel_label, -$$Lambda$GpodnetSetHostnameDialog$NfFBmshvzuL30rIZ5cRHl-7XTvo.INSTANCE).setNeutralButton((int) R.string.pref_gpodnet_sethostname_use_default_host, -$$Lambda$GpodnetSetHostnameDialog$SmNsaQ5VAN1GNKq0ckFujEKFyNU.INSTANCE).setCancelable(true);
        return dialog.show();
    }

    static /* synthetic */ void lambda$createDialog$0(EditText et, DialogInterface dialog1, int which) {
        Editable e = et.getText();
        if (e != null) {
            GpodnetPreferences.setHostname(e.toString());
        }
        dialog1.dismiss();
    }

    static /* synthetic */ void lambda$createDialog$2(DialogInterface dialog1, int which) {
        GpodnetPreferences.setHostname(GpodnetService.DEFAULT_BASE_HOST);
        dialog1.dismiss();
    }

    private static View setupContentView(Context context, EditText et) {
        LinearLayout ll = new LinearLayout(context);
        ll.addView(et);
        LayoutParams params = (LayoutParams) et.getLayoutParams();
        if (params != null) {
            params.setMargins(8, 8, 8, 8);
            params.width = -1;
            params.height = -1;
        }
        return ll;
    }
}

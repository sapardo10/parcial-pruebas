package de.danoeh.antennapod.dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.CheckBox;
import android.widget.SeekBar;
import de.danoeh.antennapod.dialog.AutoFlattrPreferenceDialog.AutoFlattrPreferenceDialogInterface;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AutoFlattrPreferenceDialog$L5jZ1BWNBHmOAJBz2dUFa82XLo0 implements OnClickListener {
    private final /* synthetic */ SeekBar f$0;
    private final /* synthetic */ AutoFlattrPreferenceDialogInterface f$1;
    private final /* synthetic */ CheckBox f$2;

    public /* synthetic */ -$$Lambda$AutoFlattrPreferenceDialog$L5jZ1BWNBHmOAJBz2dUFa82XLo0(SeekBar seekBar, AutoFlattrPreferenceDialogInterface autoFlattrPreferenceDialogInterface, CheckBox checkBox) {
        this.f$0 = seekBar;
        this.f$1 = autoFlattrPreferenceDialogInterface;
        this.f$2 = checkBox;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AutoFlattrPreferenceDialog.lambda$newAutoFlattrPreferenceDialog$1(this.f$0, this.f$1, this.f$2, dialogInterface, i);
    }
}

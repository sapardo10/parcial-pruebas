package de.danoeh.antennapod.dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VariableSpeedDialog$qcCbhh-qprGMAw-zQ82RuRZwh_8 implements OnClickListener {
    private final /* synthetic */ boolean[] f$0;
    private final /* synthetic */ String[] f$1;

    public /* synthetic */ -$$Lambda$VariableSpeedDialog$qcCbhh-qprGMAw-zQ82RuRZwh_8(boolean[] zArr, String[] strArr) {
        this.f$0 = zArr;
        this.f$1 = strArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        VariableSpeedDialog.lambda$showSpeedSelectorDialog$3(this.f$0, this.f$1, dialogInterface, i);
    }
}

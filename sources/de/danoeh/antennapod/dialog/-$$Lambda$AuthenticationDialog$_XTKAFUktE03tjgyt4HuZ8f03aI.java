package de.danoeh.antennapod.dialog;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AuthenticationDialog$_XTKAFUktE03tjgyt4HuZ8f03aI implements OnClickListener {
    private final /* synthetic */ AuthenticationDialog f$0;
    private final /* synthetic */ EditText f$1;
    private final /* synthetic */ EditText f$2;
    private final /* synthetic */ CheckBox f$3;

    public /* synthetic */ -$$Lambda$AuthenticationDialog$_XTKAFUktE03tjgyt4HuZ8f03aI(AuthenticationDialog authenticationDialog, EditText editText, EditText editText2, CheckBox checkBox) {
        this.f$0 = authenticationDialog;
        this.f$1 = editText;
        this.f$2 = editText2;
        this.f$3 = checkBox;
    }

    public final void onClick(View view) {
        AuthenticationDialog.lambda$onCreate$2(this.f$0, this.f$1, this.f$2, this.f$3, view);
    }
}

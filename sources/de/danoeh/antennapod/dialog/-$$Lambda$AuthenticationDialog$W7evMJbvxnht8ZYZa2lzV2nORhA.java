package de.danoeh.antennapod.dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AuthenticationDialog$W7evMJbvxnht8ZYZa2lzV2nORhA implements OnCancelListener {
    private final /* synthetic */ AuthenticationDialog f$0;

    public /* synthetic */ -$$Lambda$AuthenticationDialog$W7evMJbvxnht8ZYZa2lzV2nORhA(AuthenticationDialog authenticationDialog) {
        this.f$0 = authenticationDialog;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.onCancelled();
    }
}

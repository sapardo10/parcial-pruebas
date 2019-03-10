package de.danoeh.antennapod.dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$RatingDialog$lwwGxiaH-Qp8hLXQkXY_xDYtbKw implements OnCancelListener {
    public static final /* synthetic */ -$$Lambda$RatingDialog$lwwGxiaH-Qp8hLXQkXY_xDYtbKw INSTANCE = new -$$Lambda$RatingDialog$lwwGxiaH-Qp8hLXQkXY_xDYtbKw();

    private /* synthetic */ -$$Lambda$RatingDialog$lwwGxiaH-Qp8hLXQkXY_xDYtbKw() {
    }

    public final void onCancel(DialogInterface dialogInterface) {
        RatingDialog.resetStartDate();
    }
}

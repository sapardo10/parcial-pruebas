package de.danoeh.antennapod.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import de.danoeh.antennapod.activity.DirectoryChooserActivity.C07111;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DirectoryChooserActivity$1$F_bEBQNwY6JSD_Fe89AruJWPfdE implements OnClickListener {
    private final /* synthetic */ C07111 f$0;

    public /* synthetic */ -$$Lambda$DirectoryChooserActivity$1$F_bEBQNwY6JSD_Fe89AruJWPfdE(C07111 c07111) {
        this.f$0 = c07111;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        C07111.lambda$showNonEmptyDirectoryWarning$1(this.f$0, dialogInterface, i);
    }
}

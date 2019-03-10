package de.danoeh.antennapod.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import java.util.List;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaplayerInfoActivity$4jtK0qJBzxabYbcpkkxRZFWQ1Ao implements OnMultiChoiceClickListener {
    private final /* synthetic */ List f$0;

    public /* synthetic */ -$$Lambda$MediaplayerInfoActivity$4jtK0qJBzxabYbcpkkxRZFWQ1Ao(List list) {
        this.f$0 = list;
    }

    public final void onClick(DialogInterface dialogInterface, int i, boolean z) {
        MediaplayerInfoActivity.lambda$showDrawerPreferencesDialog$3(this.f$0, dialogInterface, i, z);
    }
}

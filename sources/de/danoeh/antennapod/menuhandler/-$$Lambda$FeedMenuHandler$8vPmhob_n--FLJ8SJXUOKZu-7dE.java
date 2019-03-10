package de.danoeh.antennapod.menuhandler;

import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import java.util.Set;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FeedMenuHandler$8vPmhob_n--FLJ8SJXUOKZu-7dE implements OnMultiChoiceClickListener {
    private final /* synthetic */ Set f$0;
    private final /* synthetic */ String[] f$1;

    public /* synthetic */ -$$Lambda$FeedMenuHandler$8vPmhob_n--FLJ8SJXUOKZu-7dE(Set set, String[] strArr) {
        this.f$0 = set;
        this.f$1 = strArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i, boolean z) {
        FeedMenuHandler.lambda$showFilterDialog$0(this.f$0, this.f$1, dialogInterface, i, z);
    }
}

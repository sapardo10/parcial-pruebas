package de.danoeh.antennapod.menuhandler;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import de.danoeh.antennapod.core.feed.Feed;
import java.util.Set;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FeedMenuHandler$kl1tgKIylLfhjSEbx5s1oEke3Mg implements OnClickListener {
    private final /* synthetic */ Feed f$0;
    private final /* synthetic */ Set f$1;

    public /* synthetic */ -$$Lambda$FeedMenuHandler$kl1tgKIylLfhjSEbx5s1oEke3Mg(Feed feed, Set set) {
        this.f$0 = feed;
        this.f$1 = set;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        FeedMenuHandler.lambda$showFilterDialog$1(this.f$0, this.f$1, dialogInterface, i);
    }
}

package de.danoeh.antennapod.activity;

import android.widget.ImageView;
import android.widget.TextView;
import de.danoeh.antennapod.core.feed.Feed;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FeedInfoActivity$aPsxmgKLrSqLLfH7zmCmSyyMnhU implements Consumer {
    private final /* synthetic */ FeedInfoActivity f$0;
    private final /* synthetic */ ImageView f$1;
    private final /* synthetic */ TextView f$2;

    public /* synthetic */ -$$Lambda$FeedInfoActivity$aPsxmgKLrSqLLfH7zmCmSyyMnhU(FeedInfoActivity feedInfoActivity, ImageView imageView, TextView textView) {
        this.f$0 = feedInfoActivity;
        this.f$1 = imageView;
        this.f$2 = textView;
    }

    public final void accept(Object obj) {
        FeedInfoActivity.lambda$onCreate$1(this.f$0, this.f$1, this.f$2, (Feed) obj);
    }
}

package de.danoeh.antennapod.activity;

import android.widget.ImageView;
import android.widget.TextView;
import de.danoeh.antennapod.core.feed.Feed;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FeedSettingsActivity$tHvev_0PaM7NIxCI2Lg5wxFppTg implements Consumer {
    private final /* synthetic */ FeedSettingsActivity f$0;
    private final /* synthetic */ ImageView f$1;
    private final /* synthetic */ TextView f$2;

    public /* synthetic */ -$$Lambda$FeedSettingsActivity$tHvev_0PaM7NIxCI2Lg5wxFppTg(FeedSettingsActivity feedSettingsActivity, ImageView imageView, TextView textView) {
        this.f$0 = feedSettingsActivity;
        this.f$1 = imageView;
        this.f$2 = textView;
    }

    public final void accept(Object obj) {
        FeedSettingsActivity.lambda$onCreate$5(this.f$0, this.f$1, this.f$2, (Feed) obj);
    }
}

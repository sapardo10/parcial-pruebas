package de.danoeh.antennapod.adapter;

import android.widget.TextView;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AdapterUtils$yjMslKNAGdEwJF3ihEa282Ko0Aw implements Consumer {
    private final /* synthetic */ TextView f$0;

    public /* synthetic */ -$$Lambda$AdapterUtils$yjMslKNAGdEwJF3ihEa282Ko0Aw(TextView textView) {
        this.f$0 = textView;
    }

    public final void accept(Object obj) {
        AdapterUtils.lambda$updateEpisodePlaybackProgress$1(this.f$0, (Throwable) obj);
    }
}

package de.danoeh.antennapod.adapter;

import android.widget.TextView;
import io.reactivex.functions.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AdapterUtils$x93gLTgAcuVWFelUrQYlHYxtkIo implements Consumer {
    private final /* synthetic */ TextView f$0;

    public /* synthetic */ -$$Lambda$AdapterUtils$x93gLTgAcuVWFelUrQYlHYxtkIo(TextView textView) {
        this.f$0 = textView;
    }

    public final void accept(Object obj) {
        AdapterUtils.lambda$updateEpisodePlaybackProgress$0(this.f$0, (Long) obj);
    }
}

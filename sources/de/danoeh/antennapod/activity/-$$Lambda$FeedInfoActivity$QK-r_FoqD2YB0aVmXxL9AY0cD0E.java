package de.danoeh.antennapod.activity;

import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FeedInfoActivity$QK-r_FoqD2YB0aVmXxL9AY0cD0E implements MaybeOnSubscribe {
    private final /* synthetic */ long f$0;

    public /* synthetic */ -$$Lambda$FeedInfoActivity$QK-r_FoqD2YB0aVmXxL9AY0cD0E(long j) {
        this.f$0 = j;
    }

    public final void subscribe(MaybeEmitter maybeEmitter) {
        FeedInfoActivity.lambda$onCreate$0(this.f$0, maybeEmitter);
    }
}

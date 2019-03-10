package de.danoeh.antennapod.activity;

import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AboutActivity$4mHdNVmGvIYIEZaim457ElT8K_g implements SingleOnSubscribe {
    private final /* synthetic */ AboutActivity f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$AboutActivity$4mHdNVmGvIYIEZaim457ElT8K_g(AboutActivity aboutActivity, String str) {
        this.f$0 = aboutActivity;
        this.f$1 = str;
    }

    public final void subscribe(SingleEmitter singleEmitter) {
        AboutActivity.lambda$loadAsset$0(this.f$0, this.f$1, singleEmitter);
    }
}

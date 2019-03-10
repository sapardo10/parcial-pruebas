package de.danoeh.antennapod.activity;

import android.view.ViewTreeObserver.OnGlobalLayoutListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VideoplayerActivity$fqbpuLBPv2dBHnvxa6vB8ldYNZc implements OnGlobalLayoutListener {
    private final /* synthetic */ VideoplayerActivity f$0;

    public /* synthetic */ -$$Lambda$VideoplayerActivity$fqbpuLBPv2dBHnvxa6vB8ldYNZc(VideoplayerActivity videoplayerActivity) {
        this.f$0 = videoplayerActivity;
    }

    public final void onGlobalLayout() {
        this.f$0.videoview.setAvailableSize((float) this.f$0.videoframe.getWidth(), (float) this.f$0.videoframe.getHeight());
    }
}

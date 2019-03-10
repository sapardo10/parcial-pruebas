package de.danoeh.antennapod.activity;

import android.widget.SeekBar;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaplayerActivity$3$M3c24AkvoREySER4uTVoWjzQA7Q implements Runnable {
    private final /* synthetic */ SeekBar f$0;
    private final /* synthetic */ float f$1;
    private final /* synthetic */ float f$2;

    public /* synthetic */ -$$Lambda$MediaplayerActivity$3$M3c24AkvoREySER4uTVoWjzQA7Q(SeekBar seekBar, float f, float f2) {
        this.f$0 = seekBar;
        this.f$1 = f;
        this.f$2 = f2;
    }

    public final void run() {
        this.f$0.setProgress((int) ((this.f$1 - this.f$2) / 0.05f));
    }
}

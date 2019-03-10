package de.danoeh.antennapod.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaplayerActivity$bgA_kHKEQ9rQcgYzZNbHaerj6K4 implements OnClickListener {
    private final /* synthetic */ MediaplayerActivity f$0;
    private final /* synthetic */ SeekBar f$1;

    public /* synthetic */ -$$Lambda$MediaplayerActivity$bgA_kHKEQ9rQcgYzZNbHaerj6K4(MediaplayerActivity mediaplayerActivity, SeekBar seekBar) {
        this.f$0 = mediaplayerActivity;
        this.f$1 = seekBar;
    }

    public final void onClick(View view) {
        MediaplayerActivity.lambda$onOptionsItemSelected$4(this.f$0, this.f$1, view);
    }
}

package de.danoeh.antennapod.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaplayerActivity$KOnMUXoGP3_aAn2BMJZG9Ihoe_U implements OnClickListener {
    private final /* synthetic */ MediaplayerActivity f$0;
    private final /* synthetic */ SeekBar f$1;

    public /* synthetic */ -$$Lambda$MediaplayerActivity$KOnMUXoGP3_aAn2BMJZG9Ihoe_U(MediaplayerActivity mediaplayerActivity, SeekBar seekBar) {
        this.f$0 = mediaplayerActivity;
        this.f$1 = seekBar;
    }

    public final void onClick(View view) {
        MediaplayerActivity.lambda$onOptionsItemSelected$3(this.f$0, this.f$1, view);
    }
}

package de.danoeh.antennapod.activity;

import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaplayerActivity$7EvhBkMefeeyI2ZRMOeDjNHb1DY implements OnClickListener {
    private final /* synthetic */ MediaplayerActivity f$0;
    private final /* synthetic */ SharedPreferences f$1;

    public /* synthetic */ -$$Lambda$MediaplayerActivity$7EvhBkMefeeyI2ZRMOeDjNHb1DY(MediaplayerActivity mediaplayerActivity, SharedPreferences sharedPreferences) {
        this.f$0 = mediaplayerActivity;
        this.f$1 = sharedPreferences;
    }

    public final void onClick(View view) {
        MediaplayerActivity.lambda$setupGUI$8(this.f$0, this.f$1, view);
    }
}

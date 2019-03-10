package de.danoeh.antennapod.activity;

import android.widget.SeekBar;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.debug.R;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaplayerActivity$zA9bThKGpXmpLOwUCee1KHEL-DI implements SingleButtonCallback {
    public static final /* synthetic */ -$$Lambda$MediaplayerActivity$zA9bThKGpXmpLOwUCee1KHEL-DI INSTANCE = new -$$Lambda$MediaplayerActivity$zA9bThKGpXmpLOwUCee1KHEL-DI();

    private /* synthetic */ -$$Lambda$MediaplayerActivity$zA9bThKGpXmpLOwUCee1KHEL-DI() {
    }

    public final void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
        UserPreferences.setVolume(((SeekBar) materialDialog.findViewById(R.id.volume_left)).getProgress(), ((SeekBar) materialDialog.findViewById(R.id.volume_right)).getProgress());
    }
}

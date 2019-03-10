package de.danoeh.antennapod.activity;

import android.view.View;
import android.view.View.OnLongClickListener;
import de.danoeh.antennapod.dialog.VariableSpeedDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AudioplayerActivity$AOnmPMy6XmOMBv2lRwkkxi71acI implements OnLongClickListener {
    private final /* synthetic */ AudioplayerActivity f$0;

    public /* synthetic */ -$$Lambda$AudioplayerActivity$AOnmPMy6XmOMBv2lRwkkxi71acI(AudioplayerActivity audioplayerActivity) {
        this.f$0 = audioplayerActivity;
    }

    public final boolean onLongClick(View view) {
        return VariableSpeedDialog.showDialog(this.f$0);
    }
}

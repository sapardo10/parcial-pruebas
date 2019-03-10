package de.danoeh.antennapod.dialog;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AutoFlattrPreferenceDialog$wFN9rVxMEyBB1TU76Leb1Mg4svw implements OnClickListener {
    private final /* synthetic */ SeekBar f$0;
    private final /* synthetic */ CheckBox f$1;
    private final /* synthetic */ TextView f$2;

    public /* synthetic */ -$$Lambda$AutoFlattrPreferenceDialog$wFN9rVxMEyBB1TU76Leb1Mg4svw(SeekBar seekBar, CheckBox checkBox, TextView textView) {
        this.f$0 = seekBar;
        this.f$1 = checkBox;
        this.f$2 = textView;
    }

    public final void onClick(View view) {
        AutoFlattrPreferenceDialog.lambda$newAutoFlattrPreferenceDialog$0(this.f$0, this.f$1, this.f$2, view);
    }
}

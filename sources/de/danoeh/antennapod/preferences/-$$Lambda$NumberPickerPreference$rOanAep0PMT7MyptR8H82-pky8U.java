package de.danoeh.antennapod.preferences;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NumberPickerPreference$rOanAep0PMT7MyptR8H82-pky8U implements OnClickListener {
    private final /* synthetic */ NumberPickerPreference f$0;
    private final /* synthetic */ EditText f$1;

    public /* synthetic */ -$$Lambda$NumberPickerPreference$rOanAep0PMT7MyptR8H82-pky8U(NumberPickerPreference numberPickerPreference, EditText editText) {
        this.f$0 = numberPickerPreference;
        this.f$1 = editText;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        NumberPickerPreference.lambda$onClick$1(this.f$0, this.f$1, dialogInterface, i);
    }
}

package de.danoeh.antennapod.preferences;

import android.text.InputFilter;
import android.text.Spanned;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NumberPickerPreference$H02MuUBWtIRF7qrCn8yg9CFYDWE implements InputFilter {
    private final /* synthetic */ NumberPickerPreference f$0;

    public /* synthetic */ -$$Lambda$NumberPickerPreference$H02MuUBWtIRF7qrCn8yg9CFYDWE(NumberPickerPreference numberPickerPreference) {
        this.f$0 = numberPickerPreference;
    }

    public final CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        return NumberPickerPreference.lambda$onClick$0(this.f$0, charSequence, i, i2, spanned, i3, i4);
    }
}

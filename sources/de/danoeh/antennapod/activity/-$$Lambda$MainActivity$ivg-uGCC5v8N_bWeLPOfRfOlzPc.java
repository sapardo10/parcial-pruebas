package de.danoeh.antennapod.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import java.util.List;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MainActivity$ivg-uGCC5v8N_bWeLPOfRfOlzPc implements OnMultiChoiceClickListener {
    private final /* synthetic */ List f$0;

    public /* synthetic */ -$$Lambda$MainActivity$ivg-uGCC5v8N_bWeLPOfRfOlzPc(List list) {
        this.f$0 = list;
    }

    public final void onClick(DialogInterface dialogInterface, int i, boolean z) {
        MainActivity.lambda$showDrawerPreferencesDialog$3(this.f$0, dialogInterface, i, z);
    }
}

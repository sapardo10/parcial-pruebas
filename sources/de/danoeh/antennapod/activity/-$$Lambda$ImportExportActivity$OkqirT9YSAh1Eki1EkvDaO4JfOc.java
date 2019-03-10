package de.danoeh.antennapod.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImportExportActivity$OkqirT9YSAh1Eki1EkvDaO4JfOc implements OnClickListener {
    private final /* synthetic */ ImportExportActivity f$0;

    public /* synthetic */ -$$Lambda$ImportExportActivity$OkqirT9YSAh1Eki1EkvDaO4JfOc(ImportExportActivity importExportActivity) {
        this.f$0 = importExportActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.startActivity(Intent.makeRestartActivityTask(new Intent(this.f$0.getApplicationContext(), SplashActivity.class).getComponent()));
    }
}

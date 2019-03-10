package de.danoeh.antennapod.activity.gpoddernet;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Spinner;
import java.util.concurrent.atomic.AtomicReference;

/* compiled from: lambda */
/* renamed from: de.danoeh.antennapod.activity.gpoddernet.-$$Lambda$GpodnetAuthenticationActivity$euESC1B6caQfuLZDNcImCsfEOxI */
public final /* synthetic */ class C0720x9b42b696 implements OnClickListener {
    private final /* synthetic */ GpodnetAuthenticationActivity f$0;
    private final /* synthetic */ Spinner f$1;
    private final /* synthetic */ AtomicReference f$2;

    public /* synthetic */ C0720x9b42b696(GpodnetAuthenticationActivity gpodnetAuthenticationActivity, Spinner spinner, AtomicReference atomicReference) {
        this.f$0 = gpodnetAuthenticationActivity;
        this.f$1 = spinner;
        this.f$2 = atomicReference;
    }

    public final void onClick(View view) {
        GpodnetAuthenticationActivity.lambda$setupDeviceView$1(this.f$0, this.f$1, this.f$2, view);
    }
}

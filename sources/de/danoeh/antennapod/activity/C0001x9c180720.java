package de.danoeh.antennapod.activity;

import android.view.View;
import android.view.View.OnClickListener;
import de.danoeh.antennapod.core.service.download.DownloadRequest;

/* compiled from: lambda */
/* renamed from: de.danoeh.antennapod.activity.-$$Lambda$DownloadAuthenticationActivity$JabnmQcU71HQBr_B6lB3rwJHUPY */
public final /* synthetic */ class C0001x9c180720 implements OnClickListener {
    private final /* synthetic */ DownloadAuthenticationActivity f$0;
    private final /* synthetic */ DownloadRequest f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ C0001x9c180720(DownloadAuthenticationActivity downloadAuthenticationActivity, DownloadRequest downloadRequest, boolean z) {
        this.f$0 = downloadAuthenticationActivity;
        this.f$1 = downloadRequest;
        this.f$2 = z;
    }

    public final void onClick(View view) {
        DownloadAuthenticationActivity.lambda$onCreate$0(this.f$0, this.f$1, this.f$2, view);
    }
}

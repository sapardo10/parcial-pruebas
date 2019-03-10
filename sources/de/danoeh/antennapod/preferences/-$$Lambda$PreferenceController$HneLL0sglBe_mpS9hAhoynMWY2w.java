package de.danoeh.antennapod.preferences;

import android.content.Context;
import android.support.v7.app.AlertDialog.Builder;
import io.reactivex.functions.Consumer;
import java.io.File;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$HneLL0sglBe_mpS9hAhoynMWY2w implements Consumer {
    private final /* synthetic */ Builder f$0;
    private final /* synthetic */ Context f$1;

    public /* synthetic */ -$$Lambda$PreferenceController$HneLL0sglBe_mpS9hAhoynMWY2w(Builder builder, Context context) {
        this.f$0 = builder;
        this.f$1 = context;
    }

    public final void accept(Object obj) {
        PreferenceController.lambda$export$47(this.f$0, this.f$1, (File) obj);
    }
}

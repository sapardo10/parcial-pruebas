package de.danoeh.antennapod.core.storage;

import de.danoeh.antennapod.core.util.Permutor;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DBWriter$rTE-EtLe8qrHL4iUHxHMWMHHfdk implements Runnable {
    private final /* synthetic */ Permutor f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$DBWriter$rTE-EtLe8qrHL4iUHxHMWMHHfdk(Permutor permutor, boolean z) {
        this.f$0 = permutor;
        this.f$1 = z;
    }

    public final void run() {
        DBWriter.lambda$reorderQueue$36(this.f$0, this.f$1);
    }
}

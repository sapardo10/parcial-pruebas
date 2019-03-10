package de.danoeh.antennapod.asynctask;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ExportWorker$-myZCdCAwEaEnpnQSJwi_slHoBM implements ObservableOnSubscribe {
    private final /* synthetic */ ExportWorker f$0;

    public /* synthetic */ -$$Lambda$ExportWorker$-myZCdCAwEaEnpnQSJwi_slHoBM(ExportWorker exportWorker) {
        this.f$0 = exportWorker;
    }

    public final void subscribe(ObservableEmitter observableEmitter) {
        ExportWorker.lambda$exportObservable$0(this.f$0, observableEmitter);
    }
}

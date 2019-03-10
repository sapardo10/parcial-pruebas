package de.danoeh.antennapod.fragment;

import de.danoeh.antennapod.core.storage.DBReader;
import io.reactivex.Observable;
import java.util.concurrent.Callable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ItemDescriptionFragment$GzfFCrNltlhhgAVQyW62dUF9Fis implements Callable {
    private final /* synthetic */ long f$0;

    public /* synthetic */ -$$Lambda$ItemDescriptionFragment$GzfFCrNltlhhgAVQyW62dUF9Fis(long j) {
        this.f$0 = j;
    }

    public final Object call() {
        return Observable.just(DBReader.getFeedItem(this.f$0));
    }
}

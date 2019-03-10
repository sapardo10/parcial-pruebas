package de.danoeh.antennapod.dialog;

import io.reactivex.functions.Consumer;
import okhttp3.Response;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProxyDialog$bB74oIEadoytBfYgdEldiUWZnDs implements Consumer {
    private final /* synthetic */ ProxyDialog f$0;

    public /* synthetic */ -$$Lambda$ProxyDialog$bB74oIEadoytBfYgdEldiUWZnDs(ProxyDialog proxyDialog) {
        this.f$0 = proxyDialog;
    }

    public final void accept(Object obj) {
        ProxyDialog.lambda$test$4(this.f$0, (Response) obj);
    }
}

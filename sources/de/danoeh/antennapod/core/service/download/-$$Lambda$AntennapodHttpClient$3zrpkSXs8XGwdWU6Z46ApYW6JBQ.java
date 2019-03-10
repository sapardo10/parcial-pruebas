package de.danoeh.antennapod.core.service.download;

import okhttp3.Interceptor;
import okhttp3.Interceptor$Chain;
import okhttp3.Response;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AntennapodHttpClient$3zrpkSXs8XGwdWU6Z46ApYW6JBQ implements Interceptor {
    private final /* synthetic */ String f$0;

    public /* synthetic */ -$$Lambda$AntennapodHttpClient$3zrpkSXs8XGwdWU6Z46ApYW6JBQ(String str) {
        this.f$0 = str;
    }

    public final Response intercept(Interceptor$Chain interceptor$Chain) {
        return interceptor$Chain.proceed(interceptor$Chain.request().newBuilder().header("Proxy-Authorization", this.f$0).build());
    }
}

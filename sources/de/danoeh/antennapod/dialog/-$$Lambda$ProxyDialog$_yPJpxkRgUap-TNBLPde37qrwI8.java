package de.danoeh.antennapod.dialog;

import okhttp3.Interceptor;
import okhttp3.Interceptor$Chain;
import okhttp3.Response;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProxyDialog$_yPJpxkRgUap-TNBLPde37qrwI8 implements Interceptor {
    private final /* synthetic */ String f$0;

    public /* synthetic */ -$$Lambda$ProxyDialog$_yPJpxkRgUap-TNBLPde37qrwI8(String str) {
        this.f$0 = str;
    }

    public final Response intercept(Interceptor$Chain interceptor$Chain) {
        return interceptor$Chain.proceed(interceptor$Chain.request().newBuilder().header("Proxy-Authorization", this.f$0).build());
    }
}

package de.mfietz.fyydlin;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.Rfc3339DateJsonAdapter;
import io.reactivex.Single;
import java.util.Date;
import kotlin.Lazy;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Retrofit.Builder;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Metadata(bv = {1, 0, 2}, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\u0018\u0000 \u00132\u00020\u0001:\u0001\u0013B\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004B\u001b\b\u0007\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0007J%\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\u0006\u0010\u000f\u001a\u00020\u00032\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0011¢\u0006\u0002\u0010\u0012R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0014"}, d2 = {"Lde/mfietz/fyydlin/FyydClient;", "", "baseUrl", "", "(Ljava/lang/String;)V", "client", "Lokhttp3/OkHttpClient;", "(Lokhttp3/OkHttpClient;Ljava/lang/String;)V", "service", "Lde/mfietz/fyydlin/FyydService;", "getService", "()Lde/mfietz/fyydlin/FyydService;", "searchPodcasts", "Lio/reactivex/Single;", "Lde/mfietz/fyydlin/FyydResponse;", "title", "limit", "", "(Ljava/lang/String;Ljava/lang/Integer;)Lio/reactivex/Single;", "FyydClientDefaults", "fyydlin"}, k = 1, mv = {1, 1, 11})
/* compiled from: FyydClient.kt */
public final class FyydClient {
    public static final FyydClientDefaults FyydClientDefaults = new FyydClientDefaults();
    private static final String defaultBaseUrl = defaultBaseUrl;
    private static final Lazy defaultClient$delegate = LazyKt__LazyJVMKt.lazy(FyydClient$FyydClientDefaults$defaultClient$2.INSTANCE);
    @NotNull
    private final FyydService service;

    @Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004XD¢\u0006\u0002\n\u0000R\u001b\u0010\u0005\u001a\u00020\u00068BX\u0002¢\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\b¨\u0006\u000b"}, d2 = {"Lde/mfietz/fyydlin/FyydClient$FyydClientDefaults;", "", "()V", "defaultBaseUrl", "", "defaultClient", "Lokhttp3/OkHttpClient;", "getDefaultClient", "()Lokhttp3/OkHttpClient;", "defaultClient$delegate", "Lkotlin/Lazy;", "fyydlin"}, k = 1, mv = {1, 1, 11})
    /* compiled from: FyydClient.kt */
    public static final class FyydClientDefaults {
        static final /* synthetic */ KProperty[] $$delegatedProperties = new KProperty[]{Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(FyydClientDefaults.class), "defaultClient", "getDefaultClient()Lokhttp3/OkHttpClient;"))};

        private final OkHttpClient getDefaultClient() {
            Lazy access$getDefaultClient$cp = FyydClient.defaultClient$delegate;
            KProperty kProperty = $$delegatedProperties[0];
            return (OkHttpClient) access$getDefaultClient$cp.getValue();
        }

        private FyydClientDefaults() {
        }
    }

    @JvmOverloads
    public FyydClient() {
        this(null, null, 3, null);
    }

    @JvmOverloads
    public FyydClient(@NotNull OkHttpClient okHttpClient) {
        this(okHttpClient, null, 2, null);
    }

    @JvmOverloads
    public FyydClient(@NotNull OkHttpClient client, @NotNull String baseUrl) {
        Intrinsics.checkParameterIsNotNull(client, "client");
        Intrinsics.checkParameterIsNotNull(baseUrl, "baseUrl");
        Object create = new Builder().baseUrl(baseUrl).client(client).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).addConverterFactory(MoshiConverterFactory.create(new Moshi.Builder().add(Date.class, new Rfc3339DateJsonAdapter()).build())).build().create(FyydService.class);
        Intrinsics.checkExpressionValueIsNotNull(create, "retrofit.create(FyydService::class.java)");
        this.service = (FyydService) create;
    }

    @JvmOverloads
    public /* synthetic */ FyydClient(OkHttpClient okHttpClient, String str, int i, DefaultConstructorMarker defaultConstructorMarker) {
        if ((i & 1) != null) {
            okHttpClient = FyydClientDefaults.getDefaultClient();
        }
        if ((i & 2) != 0) {
            str = defaultBaseUrl;
        }
        this(okHttpClient, str);
    }

    public FyydClient(@NotNull String baseUrl) {
        Intrinsics.checkParameterIsNotNull(baseUrl, "baseUrl");
        this(FyydClientDefaults.getDefaultClient(), baseUrl);
    }

    @NotNull
    public final FyydService getService() {
        return this.service;
    }

    @NotNull
    public static /* bridge */ /* synthetic */ Single searchPodcasts$default(FyydClient fyydClient, String str, Integer num, int i, Object obj) {
        if ((i & 2) != 0) {
            num = null;
        }
        return fyydClient.searchPodcasts(str, num);
    }

    @NotNull
    public final Single<FyydResponse> searchPodcasts(@NotNull String title, @Nullable Integer limit) {
        Intrinsics.checkParameterIsNotNull(title, "title");
        return this.service.searchPodcasts(title, limit);
    }
}

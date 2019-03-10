package de.mfietz.fyydlin;

import com.squareup.moshi.Json;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0004\b\b\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0016\u001a\u00020\tHÆ\u0003J1\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00052\b\b\u0003\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tHÆ\u0001J\u0013\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001b\u001a\u00020\tHÖ\u0001J\t\u0010\u001c\u001a\u00020\u0007HÖ\u0001R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012¨\u0006\u001d"}, d2 = {"Lde/mfietz/fyydlin/MetaData;", "", "paging", "Lde/mfietz/fyydlin/Paging;", "apiInfo", "Lde/mfietz/fyydlin/ApiInfo;", "server", "", "duration", "", "(Lde/mfietz/fyydlin/Paging;Lde/mfietz/fyydlin/ApiInfo;Ljava/lang/String;I)V", "getApiInfo", "()Lde/mfietz/fyydlin/ApiInfo;", "getDuration", "()I", "getPaging", "()Lde/mfietz/fyydlin/Paging;", "getServer", "()Ljava/lang/String;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "fyydlin"}, k = 1, mv = {1, 1, 11})
/* compiled from: FyydResponse.kt */
public final class MetaData {
    @NotNull
    private final ApiInfo apiInfo;
    private final int duration;
    @NotNull
    private final Paging paging;
    @NotNull
    private final String server;

    @NotNull
    public static /* bridge */ /* synthetic */ MetaData copy$default(MetaData metaData, Paging paging, ApiInfo apiInfo, String str, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            paging = metaData.paging;
        }
        if ((i2 & 2) != 0) {
            apiInfo = metaData.apiInfo;
        }
        if ((i2 & 4) != 0) {
            str = metaData.server;
        }
        if ((i2 & 8) != 0) {
            i = metaData.duration;
        }
        return metaData.copy(paging, apiInfo, str, i);
    }

    @NotNull
    public final Paging component1() {
        return this.paging;
    }

    @NotNull
    public final ApiInfo component2() {
        return this.apiInfo;
    }

    @NotNull
    public final String component3() {
        return this.server;
    }

    public final int component4() {
        return this.duration;
    }

    @NotNull
    public final MetaData copy(@NotNull Paging paging, @NotNull @Json(name = "API_INFO") ApiInfo apiInfo, @NotNull @Json(name = "SERVER") String str, int i) {
        Intrinsics.checkParameterIsNotNull(paging, "paging");
        Intrinsics.checkParameterIsNotNull(apiInfo, "apiInfo");
        Intrinsics.checkParameterIsNotNull(str, "server");
        return new MetaData(paging, apiInfo, str, i);
    }

    public boolean equals(Object obj) {
        if (this != obj) {
            if (obj instanceof MetaData) {
                MetaData metaData = (MetaData) obj;
                if (Intrinsics.areEqual(this.paging, metaData.paging) && Intrinsics.areEqual(this.apiInfo, metaData.apiInfo) && Intrinsics.areEqual(this.server, metaData.server)) {
                    if ((this.duration == metaData.duration ? 1 : null) != null) {
                    }
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        Paging paging = this.paging;
        int i = 0;
        int hashCode = (paging != null ? paging.hashCode() : 0) * 31;
        ApiInfo apiInfo = this.apiInfo;
        hashCode = (hashCode + (apiInfo != null ? apiInfo.hashCode() : 0)) * 31;
        String str = this.server;
        if (str != null) {
            i = str.hashCode();
        }
        return ((hashCode + i) * 31) + this.duration;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MetaData(paging=");
        stringBuilder.append(this.paging);
        stringBuilder.append(", apiInfo=");
        stringBuilder.append(this.apiInfo);
        stringBuilder.append(", server=");
        stringBuilder.append(this.server);
        stringBuilder.append(", duration=");
        stringBuilder.append(this.duration);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public MetaData(@NotNull Paging paging, @NotNull @Json(name = "API_INFO") ApiInfo apiInfo, @NotNull @Json(name = "SERVER") String server, int duration) {
        Intrinsics.checkParameterIsNotNull(paging, "paging");
        Intrinsics.checkParameterIsNotNull(apiInfo, "apiInfo");
        Intrinsics.checkParameterIsNotNull(server, "server");
        this.paging = paging;
        this.apiInfo = apiInfo;
        this.server = server;
        this.duration = duration;
    }

    @NotNull
    public final Paging getPaging() {
        return this.paging;
    }

    @NotNull
    public final ApiInfo getApiInfo() {
        return this.apiInfo;
    }

    @NotNull
    public final String getServer() {
        return this.server;
    }

    public final int getDuration() {
        return this.duration;
    }
}

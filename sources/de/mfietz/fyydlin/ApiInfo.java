package de.mfietz.fyydlin;

import com.squareup.moshi.Json;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\b\u0018\u00002\u00020\u0001B\u000f\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\f\u001a\u00020\rHÖ\u0001J\t\u0010\u000e\u001a\u00020\u000fHÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0010"}, d2 = {"Lde/mfietz/fyydlin/ApiInfo;", "", "apiVersion", "", "(D)V", "getApiVersion", "()D", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "", "fyydlin"}, k = 1, mv = {1, 1, 11})
/* compiled from: FyydResponse.kt */
public final class ApiInfo {
    private final double apiVersion;

    @NotNull
    public static /* bridge */ /* synthetic */ ApiInfo copy$default(ApiInfo apiInfo, double d, int i, Object obj) {
        if ((i & 1) != 0) {
            d = apiInfo.apiVersion;
        }
        return apiInfo.copy(d);
    }

    public final double component1() {
        return this.apiVersion;
    }

    @NotNull
    public final ApiInfo copy(@Json(name = "API_VERSION") double d) {
        return new ApiInfo(d);
    }

    public boolean equals(Object obj) {
        if (this != obj) {
            if (obj instanceof ApiInfo) {
                if (Double.compare(this.apiVersion, ((ApiInfo) obj).apiVersion) == 0) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        long doubleToLongBits = Double.doubleToLongBits(this.apiVersion);
        return (int) (doubleToLongBits ^ (doubleToLongBits >>> 32));
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ApiInfo(apiVersion=");
        stringBuilder.append(this.apiVersion);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public ApiInfo(@Json(name = "API_VERSION") double apiVersion) {
        this.apiVersion = apiVersion;
    }

    public final double getApiVersion() {
        return this.apiVersion;
    }
}

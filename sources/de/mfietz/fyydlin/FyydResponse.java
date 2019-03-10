package de.mfietz.fyydlin;

import android.support.v4.app.NotificationCompat;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0004\b\b\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\u0002\u0010\u000bJ\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0007HÆ\u0003J\u000f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\n0\tHÆ\u0003J7\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tHÆ\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001c\u001a\u00020\u0003HÖ\u0001J\t\u0010\u001d\u001a\u00020\u0005HÖ\u0001R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u001e"}, d2 = {"Lde/mfietz/fyydlin/FyydResponse;", "", "status", "", "msg", "", "meta", "Lde/mfietz/fyydlin/MetaData;", "data", "", "Lde/mfietz/fyydlin/SearchHit;", "(ILjava/lang/String;Lde/mfietz/fyydlin/MetaData;Ljava/util/List;)V", "getData", "()Ljava/util/List;", "getMeta", "()Lde/mfietz/fyydlin/MetaData;", "getMsg", "()Ljava/lang/String;", "getStatus", "()I", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "fyydlin"}, k = 1, mv = {1, 1, 11})
/* compiled from: FyydResponse.kt */
public final class FyydResponse {
    @NotNull
    private final List<SearchHit> data;
    @NotNull
    private final MetaData meta;
    @NotNull
    private final String msg;
    private final int status;

    @NotNull
    public static /* bridge */ /* synthetic */ FyydResponse copy$default(FyydResponse fyydResponse, int i, String str, MetaData metaData, List list, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = fyydResponse.status;
        }
        if ((i2 & 2) != 0) {
            str = fyydResponse.msg;
        }
        if ((i2 & 4) != 0) {
            metaData = fyydResponse.meta;
        }
        if ((i2 & 8) != 0) {
            list = fyydResponse.data;
        }
        return fyydResponse.copy(i, str, metaData, list);
    }

    public final int component1() {
        return this.status;
    }

    @NotNull
    public final String component2() {
        return this.msg;
    }

    @NotNull
    public final MetaData component3() {
        return this.meta;
    }

    @NotNull
    public final List<SearchHit> component4() {
        return this.data;
    }

    @NotNull
    public final FyydResponse copy(int i, @NotNull String str, @NotNull MetaData metaData, @NotNull List<SearchHit> list) {
        Intrinsics.checkParameterIsNotNull(str, NotificationCompat.CATEGORY_MESSAGE);
        Intrinsics.checkParameterIsNotNull(metaData, "meta");
        Intrinsics.checkParameterIsNotNull(list, "data");
        return new FyydResponse(i, str, metaData, list);
    }

    public boolean equals(Object obj) {
        if (this != obj) {
            if (obj instanceof FyydResponse) {
                FyydResponse fyydResponse = (FyydResponse) obj;
                if ((this.status == fyydResponse.status ? 1 : null) != null && Intrinsics.areEqual(this.msg, fyydResponse.msg) && Intrinsics.areEqual(this.meta, fyydResponse.meta) && Intrinsics.areEqual(this.data, fyydResponse.data)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        int i = this.status * 31;
        String str = this.msg;
        int i2 = 0;
        i = (i + (str != null ? str.hashCode() : 0)) * 31;
        MetaData metaData = this.meta;
        i = (i + (metaData != null ? metaData.hashCode() : 0)) * 31;
        List list = this.data;
        if (list != null) {
            i2 = list.hashCode();
        }
        return i + i2;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FyydResponse(status=");
        stringBuilder.append(this.status);
        stringBuilder.append(", msg=");
        stringBuilder.append(this.msg);
        stringBuilder.append(", meta=");
        stringBuilder.append(this.meta);
        stringBuilder.append(", data=");
        stringBuilder.append(this.data);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public FyydResponse(int status, @NotNull String msg, @NotNull MetaData meta, @NotNull List<SearchHit> data) {
        Intrinsics.checkParameterIsNotNull(msg, NotificationCompat.CATEGORY_MESSAGE);
        Intrinsics.checkParameterIsNotNull(meta, "meta");
        Intrinsics.checkParameterIsNotNull(data, "data");
        this.status = status;
        this.msg = msg;
        this.meta = meta;
        this.data = data;
    }

    public final int getStatus() {
        return this.status;
    }

    @NotNull
    public final String getMsg() {
        return this.msg;
    }

    @NotNull
    public final MetaData getMeta() {
        return this.meta;
    }

    @NotNull
    public final List<SearchHit> getData() {
        return this.data;
    }
}

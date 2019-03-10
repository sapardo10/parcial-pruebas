package de.mfietz.fyydlin;

import com.squareup.moshi.Json;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\b\u0018\u00002\u00020\u0001BA\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0003\u0012\n\b\u0001\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0001\u0010\b\u001a\u0004\u0018\u00010\u0003¢\u0006\u0002\u0010\tJ\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u0003HÆ\u0003¢\u0006\u0002\u0010\u000fJ\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0003HÆ\u0003¢\u0006\u0002\u0010\u000fJN\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0003\u0010\u0005\u001a\u00020\u00032\b\b\u0003\u0010\u0006\u001a\u00020\u00032\n\b\u0003\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0003\u0010\b\u001a\u0004\u0018\u00010\u0003HÆ\u0001¢\u0006\u0002\u0010\u001aJ\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001e\u001a\u00020\u0003HÖ\u0001J\t\u0010\u001f\u001a\u00020 HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0015\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000bR\u0015\u0010\b\u001a\u0004\u0018\u00010\u0003¢\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u0012\u0010\u000f¨\u0006!"}, d2 = {"Lde/mfietz/fyydlin/Paging;", "", "count", "", "page", "firstPage", "lastPage", "nextPage", "prevPage", "(IIIILjava/lang/Integer;Ljava/lang/Integer;)V", "getCount", "()I", "getFirstPage", "getLastPage", "getNextPage", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getPage", "getPrevPage", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "(IIIILjava/lang/Integer;Ljava/lang/Integer;)Lde/mfietz/fyydlin/Paging;", "equals", "", "other", "hashCode", "toString", "", "fyydlin"}, k = 1, mv = {1, 1, 11})
/* compiled from: FyydResponse.kt */
public final class Paging {
    private final int count;
    private final int firstPage;
    private final int lastPage;
    @Nullable
    private final Integer nextPage;
    private final int page;
    @Nullable
    private final Integer prevPage;

    @NotNull
    public static /* bridge */ /* synthetic */ Paging copy$default(Paging paging, int i, int i2, int i3, int i4, Integer num, Integer num2, int i5, Object obj) {
        if ((i5 & 1) != 0) {
            i = paging.count;
        }
        if ((i5 & 2) != 0) {
            i2 = paging.page;
        }
        int i6 = i2;
        if ((i5 & 4) != 0) {
            i3 = paging.firstPage;
        }
        int i7 = i3;
        if ((i5 & 8) != 0) {
            i4 = paging.lastPage;
        }
        int i8 = i4;
        if ((i5 & 16) != 0) {
            num = paging.nextPage;
        }
        Integer num3 = num;
        if ((i5 & 32) != 0) {
            num2 = paging.prevPage;
        }
        return paging.copy(i, i6, i7, i8, num3, num2);
    }

    public final int component1() {
        return this.count;
    }

    public final int component2() {
        return this.page;
    }

    public final int component3() {
        return this.firstPage;
    }

    public final int component4() {
        return this.lastPage;
    }

    @Nullable
    public final Integer component5() {
        return this.nextPage;
    }

    @Nullable
    public final Integer component6() {
        return this.prevPage;
    }

    @NotNull
    public final Paging copy(int i, int i2, @Json(name = "first_page") int i3, @Json(name = "last_page") int i4, @Nullable @Json(name = "next_page") Integer num, @Nullable @Json(name = "prev_page") Integer num2) {
        return new Paging(i, i2, i3, i4, num, num2);
    }

    public boolean equals(Object obj) {
        if (this != obj) {
            if (obj instanceof Paging) {
                Paging paging = (Paging) obj;
                if ((this.count == paging.count ? 1 : null) != null) {
                    if ((this.page == paging.page ? 1 : null) != null) {
                        if ((this.firstPage == paging.firstPage ? 1 : null) != null) {
                            if ((this.lastPage == paging.lastPage ? 1 : null) != null && Intrinsics.areEqual(this.nextPage, paging.nextPage) && Intrinsics.areEqual(this.prevPage, paging.prevPage)) {
                            }
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        int i = ((((((this.count * 31) + this.page) * 31) + this.firstPage) * 31) + this.lastPage) * 31;
        Integer num = this.nextPage;
        int i2 = 0;
        i = (i + (num != null ? num.hashCode() : 0)) * 31;
        num = this.prevPage;
        if (num != null) {
            i2 = num.hashCode();
        }
        return i + i2;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Paging(count=");
        stringBuilder.append(this.count);
        stringBuilder.append(", page=");
        stringBuilder.append(this.page);
        stringBuilder.append(", firstPage=");
        stringBuilder.append(this.firstPage);
        stringBuilder.append(", lastPage=");
        stringBuilder.append(this.lastPage);
        stringBuilder.append(", nextPage=");
        stringBuilder.append(this.nextPage);
        stringBuilder.append(", prevPage=");
        stringBuilder.append(this.prevPage);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public Paging(int count, int page, @Json(name = "first_page") int firstPage, @Json(name = "last_page") int lastPage, @Nullable @Json(name = "next_page") Integer nextPage, @Nullable @Json(name = "prev_page") Integer prevPage) {
        this.count = count;
        this.page = page;
        this.firstPage = firstPage;
        this.lastPage = lastPage;
        this.nextPage = nextPage;
        this.prevPage = prevPage;
    }

    public final int getCount() {
        return this.count;
    }

    public final int getPage() {
        return this.page;
    }

    public final int getFirstPage() {
        return this.firstPage;
    }

    public final int getLastPage() {
        return this.lastPage;
    }

    @Nullable
    public final Integer getNextPage() {
        return this.nextPage;
    }

    @Nullable
    public final Integer getPrevPage() {
        return this.prevPage;
    }
}

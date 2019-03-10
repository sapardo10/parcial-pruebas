package de.mfietz.fyydlin;

import com.squareup.moshi.Json;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.util.Arrays;
import java.util.Date;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\r\n\u0002\u0010\u0015\n\u0000\n\u0002\u0018\u0002\n\u0002\b6\n\u0002\u0010\u000b\n\u0002\b\u0004\b\b\u0018\u00002\u00020\u0001B¹\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0001\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0005\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\u0003\u0012\u0006\u0010\f\u001a\u00020\u0003\u0012\u0006\u0010\r\u001a\u00020\u0003\u0012\u0006\u0010\u000e\u001a\u00020\u0003\u0012\u0006\u0010\u000f\u001a\u00020\u0003\u0012\u0006\u0010\u0010\u001a\u00020\u0003\u0012\u0006\u0010\u0011\u001a\u00020\u0003\u0012\u0006\u0010\u0012\u001a\u00020\u0013\u0012\b\b\u0001\u0010\u0014\u001a\u00020\u0015\u0012\u0006\u0010\u0016\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0017\u001a\u00020\u0003\u0012\u0006\u0010\u0018\u001a\u00020\u0003\u0012\u0006\u0010\u0019\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u001a\u001a\u00020\u0005¢\u0006\u0002\u0010\u001bJ\t\u00105\u001a\u00020\u0003HÆ\u0003J\t\u00106\u001a\u00020\u0003HÆ\u0003J\t\u00107\u001a\u00020\u0003HÆ\u0003J\t\u00108\u001a\u00020\u0003HÆ\u0003J\t\u00109\u001a\u00020\u0003HÆ\u0003J\t\u0010:\u001a\u00020\u0003HÆ\u0003J\t\u0010;\u001a\u00020\u0013HÆ\u0003J\t\u0010<\u001a\u00020\u0015HÆ\u0003J\t\u0010=\u001a\u00020\u0005HÆ\u0003J\t\u0010>\u001a\u00020\u0003HÆ\u0003J\t\u0010?\u001a\u00020\u0003HÆ\u0003J\t\u0010@\u001a\u00020\u0005HÆ\u0003J\t\u0010A\u001a\u00020\u0003HÆ\u0003J\t\u0010B\u001a\u00020\u0005HÆ\u0003J\t\u0010C\u001a\u00020\u0003HÆ\u0003J\t\u0010D\u001a\u00020\u0003HÆ\u0003J\t\u0010E\u001a\u00020\u0003HÆ\u0003J\t\u0010F\u001a\u00020\u0005HÆ\u0003J\t\u0010G\u001a\u00020\u0003HÆ\u0003J\t\u0010H\u001a\u00020\u0003HÆ\u0003J\t\u0010I\u001a\u00020\u0003HÆ\u0003JÛ\u0001\u0010J\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0003\u0010\u0006\u001a\u00020\u00032\b\b\u0003\u0010\u0007\u001a\u00020\u00032\b\b\u0003\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00052\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u00032\b\b\u0002\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u00032\b\b\u0002\u0010\u0011\u001a\u00020\u00032\b\b\u0002\u0010\u0012\u001a\u00020\u00132\b\b\u0003\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00052\b\b\u0003\u0010\u0017\u001a\u00020\u00032\b\b\u0002\u0010\u0018\u001a\u00020\u00032\b\b\u0002\u0010\u0019\u001a\u00020\u00032\b\b\u0003\u0010\u001a\u001a\u00020\u0005HÆ\u0001J\u0013\u0010K\u001a\u00020L2\b\u0010M\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010N\u001a\u00020\u0005HÖ\u0001J\t\u0010O\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0012\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u001a\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\u0018\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0011\u0010\u0011\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010!R\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b#\u0010!R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001fR\u0011\u0010\b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b%\u0010!R\u0011\u0010\u000f\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b&\u0010!R\u0011\u0010\u0014\u001a\u00020\u0015¢\u0006\b\n\u0000\u001a\u0004\b'\u0010(R\u0011\u0010\u0010\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b)\u0010!R\u0011\u0010\u000b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b*\u0010!R\u0011\u0010\u000e\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b+\u0010!R\u0011\u0010\u0016\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b,\u0010\u001fR\u0011\u0010\n\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b-\u0010!R\u0011\u0010\r\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b.\u0010!R\u0011\u0010\t\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b/\u0010\u001fR\u0011\u0010\u0019\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b0\u0010!R\u0011\u0010\f\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b1\u0010!R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b2\u0010!R\u0011\u0010\u0017\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b3\u0010!R\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b4\u0010!¨\u0006P"}, d2 = {"Lde/mfietz/fyydlin/SearchHit;", "", "title", "", "id", "", "xmlUrl", "htmlUrl", "imageUrl", "status", "slug", "layoutImageUrl", "thumbImageURL", "smallImageURL", "microImageURL", "language", "lastpoll", "generator", "categories", "", "lastPubDate", "Ljava/util/Date;", "rank", "urlFyyd", "description", "subtitle", "countEpisodes", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[ILjava/util/Date;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V", "getCategories", "()[I", "getCountEpisodes", "()I", "getDescription", "()Ljava/lang/String;", "getGenerator", "getHtmlUrl", "getId", "getImageUrl", "getLanguage", "getLastPubDate", "()Ljava/util/Date;", "getLastpoll", "getLayoutImageUrl", "getMicroImageURL", "getRank", "getSlug", "getSmallImageURL", "getStatus", "getSubtitle", "getThumbImageURL", "getTitle", "getUrlFyyd", "getXmlUrl", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component21", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "toString", "fyydlin"}, k = 1, mv = {1, 1, 11})
/* compiled from: FyydResponse.kt */
public final class SearchHit {
    @NotNull
    private final int[] categories;
    private final int countEpisodes;
    @NotNull
    private final String description;
    @NotNull
    private final String generator;
    @NotNull
    private final String htmlUrl;
    private final int id;
    @NotNull
    private final String imageUrl;
    @NotNull
    private final String language;
    @NotNull
    private final Date lastPubDate;
    @NotNull
    private final String lastpoll;
    @NotNull
    private final String layoutImageUrl;
    @NotNull
    private final String microImageURL;
    private final int rank;
    @NotNull
    private final String slug;
    @NotNull
    private final String smallImageURL;
    private final int status;
    @NotNull
    private final String subtitle;
    @NotNull
    private final String thumbImageURL;
    @NotNull
    private final String title;
    @NotNull
    private final String urlFyyd;
    @NotNull
    private final String xmlUrl;

    @NotNull
    public static /* bridge */ /* synthetic */ SearchHit copy$default(SearchHit searchHit, String str, int i, String str2, String str3, String str4, int i2, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, int[] iArr, Date date, int i3, String str13, String str14, String str15, int i4, int i5, Object obj) {
        Date date2;
        int i6;
        String str16;
        SearchHit searchHit2 = searchHit;
        int i7 = i5;
        String str17 = (i7 & 1) != 0 ? searchHit2.title : str;
        int i8 = (i7 & 2) != 0 ? searchHit2.id : i;
        String str18 = (i7 & 4) != 0 ? searchHit2.xmlUrl : str2;
        String str19 = (i7 & 8) != 0 ? searchHit2.htmlUrl : str3;
        String str20 = (i7 & 16) != 0 ? searchHit2.imageUrl : str4;
        int i9 = (i7 & 32) != 0 ? searchHit2.status : i2;
        String str21 = (i7 & 64) != 0 ? searchHit2.slug : str5;
        String str22 = (i7 & 128) != 0 ? searchHit2.layoutImageUrl : str6;
        String str23 = (i7 & 256) != 0 ? searchHit2.thumbImageURL : str7;
        String str24 = (i7 & 512) != 0 ? searchHit2.smallImageURL : str8;
        String str25 = (i7 & 1024) != 0 ? searchHit2.microImageURL : str9;
        String str26 = (i7 & 2048) != 0 ? searchHit2.language : str10;
        String str27 = (i7 & 4096) != 0 ? searchHit2.lastpoll : str11;
        str12 = (i7 & 8192) != 0 ? searchHit2.generator : str12;
        int[] iArr2 = (i7 & 16384) != 0 ? searchHit2.categories : iArr;
        if ((i7 & 32768) != 0) {
            iArr = iArr2;
            date2 = searchHit2.lastPubDate;
        } else {
            iArr = iArr2;
            date2 = date;
        }
        if ((i7 & 65536) != 0) {
            date = date2;
            i6 = searchHit2.rank;
        } else {
            date = date2;
            i6 = i3;
        }
        if ((i7 & 131072) != 0) {
            i3 = i6;
            str16 = searchHit2.urlFyyd;
        } else {
            i3 = i6;
            str16 = str13;
        }
        if ((i7 & 262144) != 0) {
            str13 = str16;
            str16 = searchHit2.description;
        } else {
            str13 = str16;
            str16 = str14;
        }
        if ((i7 & 524288) != 0) {
            str14 = str16;
            str16 = searchHit2.subtitle;
        } else {
            str14 = str16;
            str16 = str15;
        }
        return searchHit.copy(str17, i8, str18, str19, str20, i9, str21, str22, str23, str24, str25, str26, str27, str12, iArr, date, i3, str13, str14, str16, (i7 & 1048576) != 0 ? searchHit2.countEpisodes : i4);
    }

    @NotNull
    public final String component1() {
        return this.title;
    }

    @NotNull
    public final String component10() {
        return this.smallImageURL;
    }

    @NotNull
    public final String component11() {
        return this.microImageURL;
    }

    @NotNull
    public final String component12() {
        return this.language;
    }

    @NotNull
    public final String component13() {
        return this.lastpoll;
    }

    @NotNull
    public final String component14() {
        return this.generator;
    }

    @NotNull
    public final int[] component15() {
        return this.categories;
    }

    @NotNull
    public final Date component16() {
        return this.lastPubDate;
    }

    public final int component17() {
        return this.rank;
    }

    @NotNull
    public final String component18() {
        return this.urlFyyd;
    }

    @NotNull
    public final String component19() {
        return this.description;
    }

    public final int component2() {
        return this.id;
    }

    @NotNull
    public final String component20() {
        return this.subtitle;
    }

    public final int component21() {
        return this.countEpisodes;
    }

    @NotNull
    public final String component3() {
        return this.xmlUrl;
    }

    @NotNull
    public final String component4() {
        return this.htmlUrl;
    }

    @NotNull
    public final String component5() {
        return this.imageUrl;
    }

    public final int component6() {
        return this.status;
    }

    @NotNull
    public final String component7() {
        return this.slug;
    }

    @NotNull
    public final String component8() {
        return this.layoutImageUrl;
    }

    @NotNull
    public final String component9() {
        return this.thumbImageURL;
    }

    @NotNull
    public final SearchHit copy(@NotNull String str, int i, @NotNull @Json(name = "xmlURL") String str2, @NotNull @Json(name = "htmlURL") String str3, @NotNull @Json(name = "imgURL") String str4, int i2, @NotNull String str5, @NotNull String str6, @NotNull String str7, @NotNull String str8, @NotNull String str9, @NotNull String str10, @NotNull String str11, @NotNull String str12, @NotNull int[] iArr, @NotNull @Json(name = "lastpub") Date date, int i3, @NotNull @Json(name = "url_fyyd") String str13, @NotNull String str14, @NotNull String str15, @Json(name = "count_episodes") int i4) {
        String str16 = str;
        int i5 = i;
        String str17 = str2;
        String str18 = str3;
        String str19 = str4;
        int i6 = i2;
        String str20 = str5;
        String str21 = str6;
        String str22 = str7;
        String str23 = str8;
        String str24 = str9;
        String str25 = str10;
        String str26 = str11;
        String str27 = str12;
        int[] iArr2 = iArr;
        Date date2 = date;
        int i7 = i3;
        String str28 = str13;
        String str29 = str14;
        String str30 = str15;
        int i8 = i4;
        String str31 = str16;
        Intrinsics.checkParameterIsNotNull(str16, "title");
        Intrinsics.checkParameterIsNotNull(str2, "xmlUrl");
        Intrinsics.checkParameterIsNotNull(str3, "htmlUrl");
        Intrinsics.checkParameterIsNotNull(str4, "imageUrl");
        Intrinsics.checkParameterIsNotNull(str5, "slug");
        Intrinsics.checkParameterIsNotNull(str6, "layoutImageUrl");
        Intrinsics.checkParameterIsNotNull(str7, "thumbImageURL");
        Intrinsics.checkParameterIsNotNull(str8, "smallImageURL");
        Intrinsics.checkParameterIsNotNull(str9, "microImageURL");
        Intrinsics.checkParameterIsNotNull(str10, PodDBAdapter.KEY_LANGUAGE);
        Intrinsics.checkParameterIsNotNull(str11, "lastpoll");
        Intrinsics.checkParameterIsNotNull(str12, "generator");
        Intrinsics.checkParameterIsNotNull(iArr, "categories");
        Intrinsics.checkParameterIsNotNull(date, "lastPubDate");
        Intrinsics.checkParameterIsNotNull(str13, "urlFyyd");
        Intrinsics.checkParameterIsNotNull(str14, PodDBAdapter.KEY_DESCRIPTION);
        Intrinsics.checkParameterIsNotNull(str15, "subtitle");
        return new SearchHit(str31, i5, str17, str18, str19, i6, str20, str21, str22, str23, str24, str25, str26, str27, iArr2, date2, i7, str28, str29, str30, i8);
    }

    public boolean equals(Object obj) {
        if (this != obj) {
            if (obj instanceof SearchHit) {
                SearchHit searchHit = (SearchHit) obj;
                if (Intrinsics.areEqual(this.title, searchHit.title)) {
                    if ((this.id == searchHit.id ? 1 : null) != null && Intrinsics.areEqual(this.xmlUrl, searchHit.xmlUrl) && Intrinsics.areEqual(this.htmlUrl, searchHit.htmlUrl) && Intrinsics.areEqual(this.imageUrl, searchHit.imageUrl)) {
                        if ((this.status == searchHit.status ? 1 : null) != null && Intrinsics.areEqual(this.slug, searchHit.slug) && Intrinsics.areEqual(this.layoutImageUrl, searchHit.layoutImageUrl) && Intrinsics.areEqual(this.thumbImageURL, searchHit.thumbImageURL) && Intrinsics.areEqual(this.smallImageURL, searchHit.smallImageURL) && Intrinsics.areEqual(this.microImageURL, searchHit.microImageURL) && Intrinsics.areEqual(this.language, searchHit.language) && Intrinsics.areEqual(this.lastpoll, searchHit.lastpoll) && Intrinsics.areEqual(this.generator, searchHit.generator) && Intrinsics.areEqual(this.categories, searchHit.categories) && Intrinsics.areEqual(this.lastPubDate, searchHit.lastPubDate)) {
                            if ((this.rank == searchHit.rank ? 1 : null) != null && Intrinsics.areEqual(this.urlFyyd, searchHit.urlFyyd) && Intrinsics.areEqual(this.description, searchHit.description) && Intrinsics.areEqual(this.subtitle, searchHit.subtitle)) {
                                if ((this.countEpisodes == searchHit.countEpisodes ? 1 : null) != null) {
                                }
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
        String str = this.title;
        int i = 0;
        int hashCode = (((str != null ? str.hashCode() : 0) * 31) + this.id) * 31;
        String str2 = this.xmlUrl;
        hashCode = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        str2 = this.htmlUrl;
        hashCode = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        str2 = this.imageUrl;
        hashCode = (((hashCode + (str2 != null ? str2.hashCode() : 0)) * 31) + this.status) * 31;
        str2 = this.slug;
        hashCode = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        str2 = this.layoutImageUrl;
        hashCode = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        str2 = this.thumbImageURL;
        hashCode = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        str2 = this.smallImageURL;
        hashCode = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        str2 = this.microImageURL;
        hashCode = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        str2 = this.language;
        hashCode = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        str2 = this.lastpoll;
        hashCode = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        str2 = this.generator;
        hashCode = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        int[] iArr = this.categories;
        hashCode = (hashCode + (iArr != null ? Arrays.hashCode(iArr) : 0)) * 31;
        Date date = this.lastPubDate;
        hashCode = (((hashCode + (date != null ? date.hashCode() : 0)) * 31) + this.rank) * 31;
        str2 = this.urlFyyd;
        hashCode = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        str2 = this.description;
        hashCode = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        str2 = this.subtitle;
        if (str2 != null) {
            i = str2.hashCode();
        }
        return ((hashCode + i) * 31) + this.countEpisodes;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SearchHit(title=");
        stringBuilder.append(this.title);
        stringBuilder.append(", id=");
        stringBuilder.append(this.id);
        stringBuilder.append(", xmlUrl=");
        stringBuilder.append(this.xmlUrl);
        stringBuilder.append(", htmlUrl=");
        stringBuilder.append(this.htmlUrl);
        stringBuilder.append(", imageUrl=");
        stringBuilder.append(this.imageUrl);
        stringBuilder.append(", status=");
        stringBuilder.append(this.status);
        stringBuilder.append(", slug=");
        stringBuilder.append(this.slug);
        stringBuilder.append(", layoutImageUrl=");
        stringBuilder.append(this.layoutImageUrl);
        stringBuilder.append(", thumbImageURL=");
        stringBuilder.append(this.thumbImageURL);
        stringBuilder.append(", smallImageURL=");
        stringBuilder.append(this.smallImageURL);
        stringBuilder.append(", microImageURL=");
        stringBuilder.append(this.microImageURL);
        stringBuilder.append(", language=");
        stringBuilder.append(this.language);
        stringBuilder.append(", lastpoll=");
        stringBuilder.append(this.lastpoll);
        stringBuilder.append(", generator=");
        stringBuilder.append(this.generator);
        stringBuilder.append(", categories=");
        stringBuilder.append(Arrays.toString(this.categories));
        stringBuilder.append(", lastPubDate=");
        stringBuilder.append(this.lastPubDate);
        stringBuilder.append(", rank=");
        stringBuilder.append(this.rank);
        stringBuilder.append(", urlFyyd=");
        stringBuilder.append(this.urlFyyd);
        stringBuilder.append(", description=");
        stringBuilder.append(this.description);
        stringBuilder.append(", subtitle=");
        stringBuilder.append(this.subtitle);
        stringBuilder.append(", countEpisodes=");
        stringBuilder.append(this.countEpisodes);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public SearchHit(@NotNull String title, int id, @NotNull @Json(name = "xmlURL") String xmlUrl, @NotNull @Json(name = "htmlURL") String htmlUrl, @NotNull @Json(name = "imgURL") String imageUrl, int status, @NotNull String slug, @NotNull String layoutImageUrl, @NotNull String thumbImageURL, @NotNull String smallImageURL, @NotNull String microImageURL, @NotNull String language, @NotNull String lastpoll, @NotNull String generator, @NotNull int[] categories, @NotNull @Json(name = "lastpub") Date lastPubDate, int rank, @NotNull @Json(name = "url_fyyd") String urlFyyd, @NotNull String description, @NotNull String subtitle, @Json(name = "count_episodes") int countEpisodes) {
        SearchHit searchHit = this;
        String str = title;
        String str2 = xmlUrl;
        String str3 = htmlUrl;
        String str4 = imageUrl;
        String str5 = slug;
        String str6 = layoutImageUrl;
        String str7 = thumbImageURL;
        String str8 = smallImageURL;
        String str9 = microImageURL;
        String str10 = language;
        String str11 = lastpoll;
        String str12 = generator;
        Object obj = categories;
        Date date = lastPubDate;
        String str13 = urlFyyd;
        String str14 = description;
        Intrinsics.checkParameterIsNotNull(str, "title");
        Intrinsics.checkParameterIsNotNull(str2, "xmlUrl");
        Intrinsics.checkParameterIsNotNull(str3, "htmlUrl");
        Intrinsics.checkParameterIsNotNull(str4, "imageUrl");
        Intrinsics.checkParameterIsNotNull(str5, "slug");
        Intrinsics.checkParameterIsNotNull(str6, "layoutImageUrl");
        Intrinsics.checkParameterIsNotNull(str7, "thumbImageURL");
        Intrinsics.checkParameterIsNotNull(str8, "smallImageURL");
        Intrinsics.checkParameterIsNotNull(str9, "microImageURL");
        Intrinsics.checkParameterIsNotNull(str10, PodDBAdapter.KEY_LANGUAGE);
        Intrinsics.checkParameterIsNotNull(str11, "lastpoll");
        Intrinsics.checkParameterIsNotNull(str12, "generator");
        Intrinsics.checkParameterIsNotNull(obj, "categories");
        Intrinsics.checkParameterIsNotNull(date, "lastPubDate");
        Intrinsics.checkParameterIsNotNull(str13, "urlFyyd");
        Intrinsics.checkParameterIsNotNull(description, PodDBAdapter.KEY_DESCRIPTION);
        Intrinsics.checkParameterIsNotNull(subtitle, "subtitle");
        str13 = description;
        this.title = str;
        this.id = id;
        this.xmlUrl = str2;
        this.htmlUrl = str3;
        this.imageUrl = str4;
        this.status = status;
        this.slug = str5;
        this.layoutImageUrl = str6;
        this.thumbImageURL = str7;
        this.smallImageURL = str8;
        this.microImageURL = str9;
        this.language = str10;
        this.lastpoll = str11;
        this.generator = str12;
        this.categories = obj;
        this.lastPubDate = date;
        this.rank = rank;
        this.urlFyyd = urlFyyd;
        this.description = str13;
        this.subtitle = subtitle;
        this.countEpisodes = countEpisodes;
    }

    @NotNull
    public final String getTitle() {
        return this.title;
    }

    public final int getId() {
        return this.id;
    }

    @NotNull
    public final String getXmlUrl() {
        return this.xmlUrl;
    }

    @NotNull
    public final String getHtmlUrl() {
        return this.htmlUrl;
    }

    @NotNull
    public final String getImageUrl() {
        return this.imageUrl;
    }

    public final int getStatus() {
        return this.status;
    }

    @NotNull
    public final String getSlug() {
        return this.slug;
    }

    @NotNull
    public final String getLayoutImageUrl() {
        return this.layoutImageUrl;
    }

    @NotNull
    public final String getThumbImageURL() {
        return this.thumbImageURL;
    }

    @NotNull
    public final String getSmallImageURL() {
        return this.smallImageURL;
    }

    @NotNull
    public final String getMicroImageURL() {
        return this.microImageURL;
    }

    @NotNull
    public final String getLanguage() {
        return this.language;
    }

    @NotNull
    public final String getLastpoll() {
        return this.lastpoll;
    }

    @NotNull
    public final String getGenerator() {
        return this.generator;
    }

    @NotNull
    public final int[] getCategories() {
        return this.categories;
    }

    @NotNull
    public final Date getLastPubDate() {
        return this.lastPubDate;
    }

    public final int getRank() {
        return this.rank;
    }

    @NotNull
    public final String getUrlFyyd() {
        return this.urlFyyd;
    }

    @NotNull
    public final String getDescription() {
        return this.description;
    }

    @NotNull
    public final String getSubtitle() {
        return this.subtitle;
    }

    public final int getCountEpisodes() {
        return this.countEpisodes;
    }
}

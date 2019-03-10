package de.danoeh.antennapod.core.service.download;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DownloadRequest implements Parcelable {
    public static final Creator<DownloadRequest> CREATOR = new DownloadRequest$1();
    private final Bundle arguments;
    private final boolean deleteOnFailure;
    private final String destination;
    private final long feedfileId;
    private final int feedfileType;
    private String lastModified;
    private String password;
    private int progressPercent;
    private long size;
    private long soFar;
    private final String source;
    private int statusMsg;
    private final String title;
    private String username;

    public DownloadRequest(@NonNull String destination, @NonNull String source, @NonNull String title, long feedfileId, int feedfileType, String username, String password, boolean deleteOnFailure, Bundle arguments) {
        this.destination = destination;
        this.source = source;
        this.title = title;
        this.feedfileId = feedfileId;
        this.feedfileType = feedfileType;
        this.username = username;
        this.password = password;
        this.deleteOnFailure = deleteOnFailure;
        this.arguments = arguments != null ? arguments : new Bundle();
    }

    public DownloadRequest(String destination, String source, String title, long feedfileId, int feedfileType) {
        this(destination, source, title, feedfileId, feedfileType, null, null, true, null);
    }

    private DownloadRequest(DownloadRequest$Builder builder) {
        this.destination = DownloadRequest$Builder.access$000(builder);
        this.source = DownloadRequest$Builder.access$100(builder);
        this.title = DownloadRequest$Builder.access$200(builder);
        this.feedfileId = DownloadRequest$Builder.access$300(builder);
        this.feedfileType = DownloadRequest$Builder.access$400(builder);
        this.username = DownloadRequest$Builder.access$500(builder);
        this.password = DownloadRequest$Builder.access$600(builder);
        this.lastModified = DownloadRequest$Builder.access$700(builder);
        this.deleteOnFailure = DownloadRequest$Builder.access$800(builder);
        this.arguments = DownloadRequest$Builder.access$900(builder) != null ? DownloadRequest$Builder.access$900(builder) : new Bundle();
    }

    private DownloadRequest(Parcel in) {
        this.destination = in.readString();
        this.source = in.readString();
        this.title = in.readString();
        this.feedfileId = in.readLong();
        this.feedfileType = in.readInt();
        this.lastModified = in.readString();
        this.deleteOnFailure = in.readByte() > (byte) 0;
        this.arguments = in.readBundle();
        if (in.dataAvail() > 0) {
            this.username = in.readString();
        } else {
            this.username = null;
        }
        if (in.dataAvail() > 0) {
            this.password = in.readString();
        } else {
            this.password = null;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.destination);
        dest.writeString(this.source);
        dest.writeString(this.title);
        dest.writeLong(this.feedfileId);
        dest.writeInt(this.feedfileType);
        dest.writeString(this.lastModified);
        dest.writeByte(this.deleteOnFailure);
        dest.writeBundle(this.arguments);
        String str = this.username;
        if (str != null) {
            dest.writeString(str);
        }
        str = this.password;
        if (str != null) {
            dest.writeString(str);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r9) {
        /*
        r8 = this;
        r0 = 1;
        if (r8 != r9) goto L_0x0004;
    L_0x0003:
        return r0;
    L_0x0004:
        r1 = 0;
        if (r9 == 0) goto L_0x00b4;
    L_0x0007:
        r2 = r9 instanceof de.danoeh.antennapod.core.service.download.DownloadRequest;
        if (r2 != 0) goto L_0x000d;
    L_0x000b:
        goto L_0x00b4;
    L_0x000d:
        r2 = r9;
        r2 = (de.danoeh.antennapod.core.service.download.DownloadRequest) r2;
        r3 = r8.lastModified;
        if (r3 == 0) goto L_0x001d;
    L_0x0014:
        r4 = r2.lastModified;
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x0022;
    L_0x001c:
        goto L_0x0021;
    L_0x001d:
        r3 = r2.lastModified;
        if (r3 == 0) goto L_0x0022;
    L_0x0021:
        return r1;
        r3 = r8.deleteOnFailure;
        r4 = r2.deleteOnFailure;
        if (r3 == r4) goto L_0x002a;
    L_0x0029:
        return r1;
    L_0x002a:
        r3 = r8.feedfileId;
        r5 = r2.feedfileId;
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 == 0) goto L_0x0033;
    L_0x0032:
        return r1;
    L_0x0033:
        r3 = r8.feedfileType;
        r4 = r2.feedfileType;
        if (r3 == r4) goto L_0x003a;
    L_0x0039:
        return r1;
    L_0x003a:
        r3 = r8.progressPercent;
        r4 = r2.progressPercent;
        if (r3 == r4) goto L_0x0041;
    L_0x0040:
        return r1;
    L_0x0041:
        r3 = r8.size;
        r5 = r2.size;
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 == 0) goto L_0x004a;
    L_0x0049:
        return r1;
    L_0x004a:
        r3 = r8.soFar;
        r5 = r2.soFar;
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 == 0) goto L_0x0053;
    L_0x0052:
        return r1;
    L_0x0053:
        r3 = r8.statusMsg;
        r4 = r2.statusMsg;
        if (r3 == r4) goto L_0x005a;
    L_0x0059:
        return r1;
    L_0x005a:
        r3 = r8.arguments;
        r4 = r2.arguments;
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x0065;
    L_0x0064:
        return r1;
    L_0x0065:
        r3 = r8.destination;
        r4 = r2.destination;
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x0070;
    L_0x006f:
        return r1;
    L_0x0070:
        r3 = r8.password;
        if (r3 == 0) goto L_0x007d;
    L_0x0074:
        r4 = r2.password;
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x0082;
    L_0x007c:
        goto L_0x0081;
    L_0x007d:
        r3 = r2.password;
        if (r3 == 0) goto L_0x0082;
    L_0x0081:
        return r1;
        r3 = r8.source;
        r4 = r2.source;
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x008e;
    L_0x008d:
        return r1;
    L_0x008e:
        r3 = r8.title;
        if (r3 == 0) goto L_0x009b;
    L_0x0092:
        r4 = r2.title;
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x00a0;
    L_0x009a:
        goto L_0x009f;
    L_0x009b:
        r3 = r2.title;
        if (r3 == 0) goto L_0x00a0;
    L_0x009f:
        return r1;
    L_0x00a0:
        r3 = r8.username;
        if (r3 == 0) goto L_0x00ad;
    L_0x00a4:
        r4 = r2.username;
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x00b2;
    L_0x00ac:
        goto L_0x00b1;
    L_0x00ad:
        r3 = r2.username;
        if (r3 == 0) goto L_0x00b2;
    L_0x00b1:
        return r1;
        return r0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.service.download.DownloadRequest.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        int hashCode = ((this.destination.hashCode() * 31) + this.source.hashCode()) * 31;
        String str = this.title;
        int i = 0;
        int hashCode2 = (hashCode + (str != null ? str.hashCode() : 0)) * 31;
        str = this.username;
        hashCode = (hashCode2 + (str != null ? str.hashCode() : 0)) * 31;
        str = this.password;
        hashCode2 = (hashCode + (str != null ? str.hashCode() : 0)) * 31;
        str = this.lastModified;
        if (str != null) {
            i = str.hashCode();
        }
        hashCode2 = (((hashCode2 + i) * 31) + this.deleteOnFailure) * 31;
        long j = this.feedfileId;
        hashCode2 = (((((((hashCode2 + ((int) (j ^ (j >>> 32)))) * 31) + this.feedfileType) * 31) + this.arguments.hashCode()) * 31) + this.progressPercent) * 31;
        j = this.soFar;
        hashCode = (hashCode2 + ((int) (j ^ (j >>> 32)))) * 31;
        j = this.size;
        return ((hashCode + ((int) (j ^ (j >>> 32)))) * 31) + this.statusMsg;
    }

    public String getDestination() {
        return this.destination;
    }

    public String getSource() {
        return this.source;
    }

    public String getTitle() {
        return this.title;
    }

    public long getFeedfileId() {
        return this.feedfileId;
    }

    public int getFeedfileType() {
        return this.feedfileType;
    }

    public int getProgressPercent() {
        return this.progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }

    public long getSoFar() {
        return this.soFar;
    }

    public void setSoFar(long soFar) {
        this.soFar = soFar;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setStatusMsg(int statusMsg) {
        this.statusMsg = statusMsg;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DownloadRequest setLastModified(@Nullable String lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    @Nullable
    public String getLastModified() {
        return this.lastModified;
    }

    public boolean isDeleteOnFailure() {
        return this.deleteOnFailure;
    }

    public Bundle getArguments() {
        return this.arguments;
    }
}

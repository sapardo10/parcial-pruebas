package com.google.android.exoplayer2.drm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class DrmInitData implements Comparator<SchemeData>, Parcelable {
    public static final Creator<DrmInitData> CREATOR = new C05791();
    private int hashCode;
    public final int schemeDataCount;
    private final SchemeData[] schemeDatas;
    @Nullable
    public final String schemeType;

    /* renamed from: com.google.android.exoplayer2.drm.DrmInitData$1 */
    static class C05791 implements Creator<DrmInitData> {
        C05791() {
        }

        public DrmInitData createFromParcel(Parcel in) {
            return new DrmInitData(in);
        }

        public DrmInitData[] newArray(int size) {
            return new DrmInitData[size];
        }
    }

    public static final class SchemeData implements Parcelable {
        public static final Creator<SchemeData> CREATOR = new C05801();
        public final byte[] data;
        private int hashCode;
        @Nullable
        public final String licenseServerUrl;
        public final String mimeType;
        public final boolean requiresSecureDecryption;
        private final UUID uuid;

        /* renamed from: com.google.android.exoplayer2.drm.DrmInitData$SchemeData$1 */
        static class C05801 implements Creator<SchemeData> {
            C05801() {
            }

            public SchemeData createFromParcel(Parcel in) {
                return new SchemeData(in);
            }

            public SchemeData[] newArray(int size) {
                return new SchemeData[size];
            }
        }

        public SchemeData(UUID uuid, String mimeType, byte[] data) {
            this(uuid, mimeType, data, false);
        }

        public SchemeData(UUID uuid, String mimeType, byte[] data, boolean requiresSecureDecryption) {
            this(uuid, null, mimeType, data, requiresSecureDecryption);
        }

        public SchemeData(UUID uuid, @Nullable String licenseServerUrl, String mimeType, byte[] data, boolean requiresSecureDecryption) {
            this.uuid = (UUID) Assertions.checkNotNull(uuid);
            this.licenseServerUrl = licenseServerUrl;
            this.mimeType = (String) Assertions.checkNotNull(mimeType);
            this.data = data;
            this.requiresSecureDecryption = requiresSecureDecryption;
        }

        SchemeData(Parcel in) {
            this.uuid = new UUID(in.readLong(), in.readLong());
            this.licenseServerUrl = in.readString();
            this.mimeType = in.readString();
            this.data = in.createByteArray();
            this.requiresSecureDecryption = in.readByte() != (byte) 0;
        }

        public boolean matches(UUID schemeUuid) {
            if (!C0555C.UUID_NIL.equals(this.uuid)) {
                if (!schemeUuid.equals(this.uuid)) {
                    return false;
                }
            }
            return true;
        }

        public boolean canReplace(SchemeData other) {
            return hasData() && !other.hasData() && matches(other.uuid);
        }

        public boolean hasData() {
            return this.data != null;
        }

        public SchemeData copyWithData(@Nullable byte[] data) {
            return new SchemeData(this.uuid, this.licenseServerUrl, this.mimeType, data, this.requiresSecureDecryption);
        }

        public boolean equals(@Nullable Object obj) {
            if (!(obj instanceof SchemeData)) {
                return false;
            }
            boolean z = true;
            if (obj == this) {
                return true;
            }
            SchemeData other = (SchemeData) obj;
            if (Util.areEqual(this.licenseServerUrl, other.licenseServerUrl)) {
                if (Util.areEqual(this.mimeType, other.mimeType)) {
                    if (Util.areEqual(this.uuid, other.uuid)) {
                        if (Arrays.equals(this.data, other.data)) {
                            return z;
                        }
                    }
                }
            }
            z = false;
            return z;
        }

        public int hashCode() {
            if (this.hashCode == 0) {
                int result = this.uuid.hashCode() * 31;
                String str = this.licenseServerUrl;
                this.hashCode = ((((result + (str == null ? 0 : str.hashCode())) * 31) + this.mimeType.hashCode()) * 31) + Arrays.hashCode(this.data);
            }
            return this.hashCode;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.uuid.getMostSignificantBits());
            dest.writeLong(this.uuid.getLeastSignificantBits());
            dest.writeString(this.licenseServerUrl);
            dest.writeString(this.mimeType);
            dest.writeByteArray(this.data);
            dest.writeByte((byte) this.requiresSecureDecryption);
        }
    }

    @Nullable
    public static DrmInitData createSessionCreationData(@Nullable DrmInitData manifestData, @Nullable DrmInitData mediaData) {
        List result = new ArrayList();
        String schemeType = null;
        if (manifestData != null) {
            schemeType = manifestData.schemeType;
            for (SchemeData data : manifestData.schemeDatas) {
                if (data.hasData()) {
                    result.add(data);
                }
            }
        }
        if (mediaData != null) {
            if (schemeType == null) {
                schemeType = mediaData.schemeType;
            }
            int manifestDatasCount = result.size();
            for (SchemeData data2 : mediaData.schemeDatas) {
                if (data2.hasData() && !containsSchemeDataWithUuid(result, manifestDatasCount, data2.uuid)) {
                    result.add(data2);
                }
            }
        }
        return result.isEmpty() ? null : new DrmInitData(schemeType, result);
    }

    public DrmInitData(List<SchemeData> schemeDatas) {
        this(null, false, (SchemeData[]) schemeDatas.toArray(new SchemeData[schemeDatas.size()]));
    }

    public DrmInitData(String schemeType, List<SchemeData> schemeDatas) {
        this(schemeType, false, (SchemeData[]) schemeDatas.toArray(new SchemeData[schemeDatas.size()]));
    }

    public DrmInitData(SchemeData... schemeDatas) {
        this(null, schemeDatas);
    }

    public DrmInitData(@Nullable String schemeType, SchemeData... schemeDatas) {
        this(schemeType, true, schemeDatas);
    }

    private DrmInitData(@Nullable String schemeType, boolean cloneSchemeDatas, SchemeData... schemeDatas) {
        this.schemeType = schemeType;
        if (cloneSchemeDatas) {
            schemeDatas = (SchemeData[]) schemeDatas.clone();
        }
        Arrays.sort(schemeDatas, this);
        this.schemeDatas = schemeDatas;
        this.schemeDataCount = schemeDatas.length;
    }

    DrmInitData(Parcel in) {
        this.schemeType = in.readString();
        this.schemeDatas = (SchemeData[]) in.createTypedArray(SchemeData.CREATOR);
        this.schemeDataCount = this.schemeDatas.length;
    }

    @Deprecated
    public SchemeData get(UUID uuid) {
        for (SchemeData schemeData : this.schemeDatas) {
            if (schemeData.matches(uuid)) {
                return schemeData;
            }
        }
        return null;
    }

    public SchemeData get(int index) {
        return this.schemeDatas[index];
    }

    public DrmInitData copyWithSchemeType(@Nullable String schemeType) {
        if (Util.areEqual(this.schemeType, schemeType)) {
            return this;
        }
        return new DrmInitData(schemeType, false, this.schemeDatas);
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            String str = this.schemeType;
            this.hashCode = ((str == null ? 0 : str.hashCode()) * 31) + Arrays.hashCode(this.schemeDatas);
        }
        return this.hashCode;
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                DrmInitData other = (DrmInitData) obj;
                if (Util.areEqual(this.schemeType, other.schemeType)) {
                    if (Arrays.equals(this.schemeDatas, other.schemeDatas)) {
                        return z;
                    }
                }
                z = false;
                return z;
            }
        }
        return false;
    }

    public int compare(SchemeData first, SchemeData second) {
        if (C0555C.UUID_NIL.equals(first.uuid)) {
            return C0555C.UUID_NIL.equals(second.uuid) ? 0 : 1;
        } else {
            return first.uuid.compareTo(second.uuid);
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.schemeType);
        dest.writeTypedArray(this.schemeDatas, 0);
    }

    private static boolean containsSchemeDataWithUuid(ArrayList<SchemeData> datas, int limit, UUID uuid) {
        for (int i = 0; i < limit; i++) {
            if (((SchemeData) datas.get(i)).uuid.equals(uuid)) {
                return true;
            }
        }
        return false;
    }
}

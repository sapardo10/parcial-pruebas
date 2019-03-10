package com.google.android.exoplayer2.source.dash.manifest;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.UriUtil;

public final class RangedUri {
    private int hashCode;
    public final long length;
    private final String referenceUri;
    public final long start;

    public RangedUri(@Nullable String referenceUri, long start, long length) {
        this.referenceUri = referenceUri == null ? "" : referenceUri;
        this.start = start;
        this.length = length;
    }

    public Uri resolveUri(String baseUri) {
        return UriUtil.resolveToUri(baseUri, this.referenceUri);
    }

    public String resolveUriString(String baseUri) {
        return UriUtil.resolve(baseUri, this.referenceUri);
    }

    @Nullable
    public RangedUri attemptMerge(@Nullable RangedUri other, String baseUri) {
        String resolvedUri = resolveUriString(baseUri);
        if (other != null) {
            if (resolvedUri.equals(other.resolveUriString(baseUri))) {
                long j;
                long j2;
                long j3 = this.length;
                if (j3 != -1) {
                    j = this.start;
                    if (j + j3 == other.start) {
                        j2 = other.length;
                        return new RangedUri(resolvedUri, j, j2 == -1 ? -1 : j3 + j2);
                    }
                }
                j3 = other.length;
                if (j3 != -1) {
                    j = other.start;
                    if (j + j3 == this.start) {
                        j2 = this.length;
                        return new RangedUri(resolvedUri, j, j2 == -1 ? -1 : j3 + j2);
                    }
                }
                return null;
            }
        }
        return null;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = (((((17 * 31) + ((int) this.start)) * 31) + ((int) this.length)) * 31) + this.referenceUri.hashCode();
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
                RangedUri other = (RangedUri) obj;
                if (this.start == other.start && this.length == other.length) {
                    if (this.referenceUri.equals(other.referenceUri)) {
                        return z;
                    }
                }
                z = false;
                return z;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("RangedUri(referenceUri=");
        stringBuilder.append(this.referenceUri);
        stringBuilder.append(", start=");
        stringBuilder.append(this.start);
        stringBuilder.append(", length=");
        stringBuilder.append(this.length);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}

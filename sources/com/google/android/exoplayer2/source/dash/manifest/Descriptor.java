package com.google.android.exoplayer2.source.dash.manifest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;

public final class Descriptor {
    @Nullable
    public final String id;
    @NonNull
    public final String schemeIdUri;
    @Nullable
    public final String value;

    public Descriptor(@NonNull String schemeIdUri, @Nullable String value, @Nullable String id) {
        this.schemeIdUri = schemeIdUri;
        this.value = value;
        this.id = id;
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                Descriptor other = (Descriptor) obj;
                if (Util.areEqual(this.schemeIdUri, other.schemeIdUri) && Util.areEqual(this.value, other.value)) {
                    if (Util.areEqual(this.id, other.id)) {
                        return z;
                    }
                }
                z = false;
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        String str = this.schemeIdUri;
        int i = 0;
        int result = (str != null ? str.hashCode() : 0) * 31;
        String str2 = this.value;
        int hashCode = (result + (str2 != null ? str2.hashCode() : 0)) * 31;
        str2 = this.id;
        if (str2 != null) {
            i = str2.hashCode();
        }
        return hashCode + i;
    }
}

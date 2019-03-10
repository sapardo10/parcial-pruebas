package com.google.android.exoplayer2.source.dash.manifest;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;

public class ProgramInformation {
    public final String copyright;
    public final String lang;
    public final String moreInformationURL;
    public final String source;
    public final String title;

    public ProgramInformation(String title, String source, String copyright, String moreInformationURL, String lang) {
        this.title = title;
        this.source = source;
        this.copyright = copyright;
        this.moreInformationURL = moreInformationURL;
        this.lang = lang;
    }

    public boolean equals(@Nullable Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                ProgramInformation other = (ProgramInformation) obj;
                if (Util.areEqual(this.title, other.title)) {
                    if (Util.areEqual(this.source, other.source)) {
                        if (Util.areEqual(this.copyright, other.copyright)) {
                            if (Util.areEqual(this.moreInformationURL, other.moreInformationURL)) {
                                if (Util.areEqual(this.lang, other.lang)) {
                                    return z;
                                }
                            }
                        }
                    }
                }
                z = false;
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        int result = 17 * 31;
        String str = this.title;
        int i = 0;
        int hashCode = (result + (str != null ? str.hashCode() : 0)) * 31;
        str = this.source;
        result = (hashCode + (str != null ? str.hashCode() : 0)) * 31;
        str = this.copyright;
        hashCode = (result + (str != null ? str.hashCode() : 0)) * 31;
        str = this.moreInformationURL;
        result = (hashCode + (str != null ? str.hashCode() : 0)) * 31;
        str = this.lang;
        if (str != null) {
            i = str.hashCode();
        }
        return result + i;
    }
}

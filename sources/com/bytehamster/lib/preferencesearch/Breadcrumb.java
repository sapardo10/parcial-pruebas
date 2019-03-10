package com.bytehamster.lib.preferencesearch;

import android.support.annotation.Nullable;
import android.text.TextUtils;

class Breadcrumb {
    private Breadcrumb() {
    }

    static String concat(@Nullable String s1, String s2) {
        if (TextUtils.isEmpty(s1)) {
            return s2;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(s1);
        stringBuilder.append(" > ");
        stringBuilder.append(s2);
        return stringBuilder.toString();
    }
}

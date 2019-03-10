package com.bytehamster.lib.preferencesearch;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.FuzzyScore;

class PreferenceItem extends ListItem {
    static final int TYPE = 2;
    private static FuzzyScore fuzzyScore = new FuzzyScore(Locale.getDefault());
    String breadcrumbs;
    String entries;
    String key;
    ArrayList<String> keyBreadcrumbs = new ArrayList();
    String keywords;
    private String lastKeyword = null;
    private float lastScore = 0.0f;
    int resId;
    String summary;
    String title;

    PreferenceItem() {
    }

    boolean hasData() {
        if (this.title == null) {
            if (this.summary == null) {
                return false;
            }
        }
        return true;
    }

    boolean matchesFuzzy(String keyword) {
        return ((double) getScore(keyword)) > 0.3d;
    }

    boolean matches(String keyword) {
        return getInfo().contains(keyword);
    }

    float getScore(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return 0.0f;
        }
        if (TextUtils.equals(this.lastKeyword, keyword)) {
            return this.lastScore;
        }
        String info = getInfo();
        FuzzyScore fuzzyScore = fuzzyScore;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ø");
        stringBuilder.append(keyword);
        this.lastScore = ((float) fuzzyScore.fuzzyScore(info, stringBuilder.toString()).intValue()) / ((float) (((keyword.length() + 1) * 3) - 2));
        this.lastKeyword = keyword;
        return this.lastScore;
    }

    private String getInfo() {
        StringBuilder infoBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(this.title)) {
            infoBuilder.append("ø");
            infoBuilder.append(this.title);
        }
        if (!TextUtils.isEmpty(this.summary)) {
            infoBuilder.append("ø");
            infoBuilder.append(this.summary);
        }
        if (!TextUtils.isEmpty(this.entries)) {
            infoBuilder.append("ø");
            infoBuilder.append(this.entries);
        }
        if (!TextUtils.isEmpty(this.breadcrumbs)) {
            infoBuilder.append("ø");
            infoBuilder.append(this.breadcrumbs);
        }
        if (!TextUtils.isEmpty(this.keywords)) {
            infoBuilder.append("ø");
            infoBuilder.append(this.keywords);
        }
        return infoBuilder.toString();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PreferenceItem: ");
        stringBuilder.append(this.title);
        stringBuilder.append(StringUtils.SPACE);
        stringBuilder.append(this.summary);
        stringBuilder.append(StringUtils.SPACE);
        stringBuilder.append(this.key);
        return stringBuilder.toString();
    }

    public int getType() {
        return 2;
    }
}

package com.bytehamster.lib.preferencesearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.bytehamster.lib.preferencesearch.SearchConfiguration.SearchIndexItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

class PreferenceParser {
    private static final List<String> BLACKLIST = Arrays.asList(new String[]{SearchPreference.class.getName(), "PreferenceCategory"});
    private static final List<String> CONTAINERS = Arrays.asList(new String[]{"PreferenceCategory", "PreferenceScreen"});
    private static final int MAX_RESULTS = 10;
    private static final String NS_ANDROID = "http://schemas.android.com/apk/res/android";
    private static final String NS_SEARCH = "http://schemas.android.com/apk/com.bytehamster.lib.preferencesearch";
    private ArrayList<PreferenceItem> allEntries = new ArrayList();
    private Context context;

    PreferenceParser(Context context) {
        this.context = context;
    }

    void addResourceFile(SearchIndexItem item) {
        this.allEntries.addAll(parseFile(item));
    }

    private ArrayList<PreferenceItem> parseFile(SearchIndexItem item) {
        ArrayList<PreferenceItem> results = new ArrayList();
        XmlPullParser xpp = this.context.getResources().getXml(item.getResId());
        try {
            xpp.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
            xpp.setFeature("http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes", true);
            ArrayList<String> breadcrumbs = new ArrayList();
            ArrayList<String> keyBreadcrumbs = new ArrayList();
            if (!TextUtils.isEmpty(item.getBreadcrumb())) {
                breadcrumbs.add(item.getBreadcrumb());
            }
            while (xpp.getEventType() != 1) {
                if (xpp.getEventType() == 2) {
                    PreferenceItem result = parseSearchResult(xpp);
                    result.resId = item.getResId();
                    if (!BLACKLIST.contains(xpp.getName()) && result.hasData()) {
                        result.breadcrumbs = joinBreadcrumbs(breadcrumbs);
                        result.keyBreadcrumbs = cleanupKeyBreadcrumbs(keyBreadcrumbs);
                        if (!"true".equals(getAttribute(xpp, NS_SEARCH, "ignore"))) {
                            results.add(result);
                        }
                    }
                    if (CONTAINERS.contains(xpp.getName())) {
                        breadcrumbs.add(result.title == null ? "" : result.title);
                    }
                    if (xpp.getName().equals("PreferenceScreen")) {
                        keyBreadcrumbs.add(getAttribute(xpp, "key"));
                    }
                } else if (xpp.getEventType() == 3 && CONTAINERS.contains(xpp.getName())) {
                    breadcrumbs.remove(breadcrumbs.size() - 1);
                    if (xpp.getName().equals("PreferenceScreen")) {
                        keyBreadcrumbs.remove(keyBreadcrumbs.size() - 1);
                    }
                    xpp.next();
                }
                xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private ArrayList<String> cleanupKeyBreadcrumbs(ArrayList<String> keyBreadcrumbs) {
        ArrayList<String> result = new ArrayList();
        Iterator it = keyBreadcrumbs.iterator();
        while (it.hasNext()) {
            String keyBreadcrumb = (String) it.next();
            if (keyBreadcrumb != null) {
                result.add(keyBreadcrumb);
            }
        }
        return result;
    }

    private String joinBreadcrumbs(ArrayList<String> breadcrumbs) {
        String result = "";
        Iterator it = breadcrumbs.iterator();
        while (it.hasNext()) {
            String crumb = (String) it.next();
            if (!TextUtils.isEmpty(crumb)) {
                result = Breadcrumb.concat(result, crumb);
            }
        }
        return result;
    }

    private String getAttribute(XmlPullParser xpp, @Nullable String namespace, @NonNull String attribute) {
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            Log.d("ns", xpp.getAttributeNamespace(i));
            if (attribute.equals(xpp.getAttributeName(i))) {
                if (namespace != null) {
                    if (namespace.equals(xpp.getAttributeNamespace(i))) {
                    }
                }
                return xpp.getAttributeValue(i);
            }
        }
        return null;
    }

    private String getAttribute(XmlPullParser xpp, @NonNull String attribute) {
        if (hasAttribute(xpp, NS_SEARCH, attribute)) {
            return getAttribute(xpp, NS_SEARCH, attribute);
        }
        return getAttribute(xpp, NS_ANDROID, attribute);
    }

    private boolean hasAttribute(XmlPullParser xpp, @Nullable String namespace, @NonNull String attribute) {
        return getAttribute(xpp, namespace, attribute) != null;
    }

    private PreferenceItem parseSearchResult(XmlPullParser xpp) {
        PreferenceItem result = new PreferenceItem();
        result.title = readString(getAttribute(xpp, "title"));
        result.summary = readString(getAttribute(xpp, "summary"));
        result.key = readString(getAttribute(xpp, "key"));
        result.entries = readStringArray(getAttribute(xpp, "entries"));
        result.keywords = readString(getAttribute(xpp, NS_SEARCH, "keywords"));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Found: ");
        stringBuilder.append(xpp.getName());
        stringBuilder.append("/");
        stringBuilder.append(result);
        Log.d("PreferenceParser", stringBuilder.toString());
        return result;
    }

    private String readStringArray(@Nullable String s) {
        if (s == null) {
            return null;
        }
        if (!s.startsWith("@")) {
            return s;
        }
        try {
            return TextUtils.join(",", this.context.getResources().getStringArray(Integer.parseInt(s.substring(1))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readString(@Nullable String s) {
        if (s == null) {
            return null;
        }
        if (!s.startsWith("@")) {
            return s;
        }
        try {
            return this.context.getString(Integer.parseInt(s.substring(1)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    List<PreferenceItem> searchFor(final String keyword, boolean fuzzy) {
        if (TextUtils.isEmpty(keyword)) {
            return new ArrayList();
        }
        ArrayList<PreferenceItem> results = new ArrayList();
        Iterator it = this.allEntries.iterator();
        while (it.hasNext()) {
            PreferenceItem item = (PreferenceItem) it.next();
            if (fuzzy) {
                if (item.matchesFuzzy(keyword)) {
                    results.add(item);
                }
            }
            if (!fuzzy) {
                if (!item.matches(keyword)) {
                }
                results.add(item);
            }
        }
        Collections.sort(results, new Comparator<PreferenceItem>() {
            public int compare(PreferenceItem i1, PreferenceItem i2) {
                return PreferenceParser.floatCompare(i2.getScore(keyword), i1.getScore(keyword));
            }
        });
        if (results.size() > 10) {
            return results.subList(0, 10);
        }
        return results;
    }

    private static int floatCompare(float x, float y) {
        if (x < y) {
            return -1;
        }
        return x == y ? 0 : 1;
    }
}

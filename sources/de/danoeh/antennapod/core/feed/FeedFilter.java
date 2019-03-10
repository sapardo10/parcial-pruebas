package de.danoeh.antennapod.core.feed;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeedFilter {
    private static final String TAG = "FeedFilter";
    private final String excludeFilter;
    private final String includeFilter;

    public FeedFilter() {
        this("", "");
    }

    public FeedFilter(String includeFilter, String excludeFilter) {
        this.includeFilter = includeFilter;
        this.excludeFilter = excludeFilter;
    }

    private List<String> parseTerms(String filter) {
        List<String> list = new ArrayList();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(filter);
        while (m.find()) {
            list.add(m.group(1).replace("\"", ""));
        }
        return list;
    }

    public boolean shouldAutoDownload(FeedItem item) {
        List<String> includeTerms = parseTerms(this.includeFilter);
        List<String> excludeTerms = parseTerms(this.excludeFilter);
        if (includeTerms.size() == 0 && excludeTerms.size() == 0) {
            return true;
        }
        String title = item.getTitle().toLowerCase();
        for (String term : excludeTerms) {
            if (title.contains(term.trim().toLowerCase())) {
                return false;
            }
        }
        for (String term2 : includeTerms) {
            if (title.contains(term2.trim().toLowerCase())) {
                return true;
            }
        }
        if (hasIncludeFilter() || !hasExcludeFilter()) {
            return false;
        }
        return true;
    }

    public String getIncludeFilter() {
        return this.includeFilter;
    }

    public String getExcludeFilter() {
        return this.excludeFilter;
    }

    public boolean includeOnly() {
        return hasIncludeFilter() && !hasExcludeFilter();
    }

    public boolean excludeOnly() {
        return hasExcludeFilter() && !hasIncludeFilter();
    }

    public boolean hasIncludeFilter() {
        return this.includeFilter.length() > 0;
    }

    public boolean hasExcludeFilter() {
        return this.excludeFilter.length() > 0;
    }
}

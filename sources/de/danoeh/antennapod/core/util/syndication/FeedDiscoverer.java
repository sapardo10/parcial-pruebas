package de.danoeh.antennapod.core.util.syndication;

import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FeedDiscoverer {
    private static final String MIME_ATOM = "application/atom+xml";
    private static final String MIME_RSS = "application/rss+xml";

    public Map<String, String> findLinks(File in, String baseUrl) throws IOException {
        return findLinks(Jsoup.parse(in, null), baseUrl);
    }

    public Map<String, String> findLinks(String in, String baseUrl) {
        return findLinks(Jsoup.parse(in), baseUrl);
    }

    private Map<String, String> findLinks(Document document, String baseUrl) {
        Map<String, String> res = new ArrayMap();
        Iterator it = document.head().getElementsByTag(PodDBAdapter.KEY_LINK).iterator();
        while (it.hasNext()) {
            Element link = (Element) it.next();
            String rel = link.attr("rel");
            String href = link.attr("href");
            if (!TextUtils.isEmpty(href)) {
                if (!rel.equals("alternate")) {
                    if (rel.equals(PodDBAdapter.KEY_FEED)) {
                    }
                }
                String type = link.attr("type");
                if (!type.equals(MIME_RSS)) {
                    if (type.equals(MIME_ATOM)) {
                    }
                }
                String title = link.attr("title");
                String processedUrl = processURL(baseUrl, href);
                if (processedUrl != null) {
                    res.put(processedUrl, TextUtils.isEmpty(title) ? href : title);
                }
            }
        }
        return res;
    }

    private String processURL(String baseUrl, String strUrl) {
        if (!Uri.parse(strUrl).isRelative()) {
            return strUrl;
        }
        Uri res = Uri.parse(baseUrl).buildUpon().path(strUrl).build();
        return res != null ? res.toString() : null;
    }
}

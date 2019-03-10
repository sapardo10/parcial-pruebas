package de.danoeh.antennapod.core.export.html;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import de.danoeh.antennapod.core.export.CommonSymbols;
import de.danoeh.antennapod.core.export.ExportWriter;
import de.danoeh.antennapod.core.feed.Feed;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.xmlpull.v1.XmlSerializer;

public class HtmlWriter implements ExportWriter {
    private static final String ENCODING = "UTF-8";
    private static final String HTML_TITLE = "AntennaPod Subscriptions";
    private static final String TAG = "HtmlWriter";

    public void writeDocument(List<Feed> feeds, Writer writer) throws IllegalArgumentException, IllegalStateException, IOException {
        Log.d(TAG, "Starting to write document");
        XmlSerializer xs = Xml.newSerializer();
        xs.setFeature(CommonSymbols.XML_FEATURE_INDENT_OUTPUT, true);
        xs.setOutput(writer);
        xs.startDocument("UTF-8", Boolean.valueOf(false));
        xs.startTag(null, "html");
        xs.startTag(null, "head");
        xs.startTag(null, "title");
        xs.text(HTML_TITLE);
        xs.endTag(null, "title");
        xs.endTag(null, "head");
        xs.startTag(null, "body");
        xs.startTag(null, "h1");
        xs.text(HTML_TITLE);
        xs.endTag(null, "h1");
        xs.startTag(null, "ol");
        for (Feed feed : feeds) {
            xs.startTag(null, "li");
            xs.text(feed.getTitle());
            if (!TextUtils.isEmpty(feed.getLink())) {
                xs.text(" [");
                xs.startTag(null, "a");
                xs.attribute(null, "href", feed.getLink());
                xs.text("Website");
                xs.endTag(null, "a");
                xs.text("]");
            }
            xs.text(" [");
            xs.startTag(null, "a");
            xs.attribute(null, "href", feed.getDownload_url());
            xs.text("Feed");
            xs.endTag(null, "a");
            xs.text("]");
            xs.endTag(null, "li");
        }
        xs.endTag(null, "ol");
        xs.endTag(null, "body");
        xs.endTag(null, "html");
        xs.endDocument();
        Log.d(TAG, "Finished writing document");
    }

    public String fileExtension() {
        return "html";
    }
}

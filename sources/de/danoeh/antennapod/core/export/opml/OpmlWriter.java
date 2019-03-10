package de.danoeh.antennapod.core.export.opml;

import android.util.Log;
import android.util.Xml;
import de.danoeh.antennapod.core.export.CommonSymbols;
import de.danoeh.antennapod.core.export.ExportWriter;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.util.DateUtils;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import org.xmlpull.v1.XmlSerializer;

public class OpmlWriter implements ExportWriter {
    private static final String ENCODING = "UTF-8";
    private static final String OPML_TITLE = "AntennaPod Subscriptions";
    private static final String OPML_VERSION = "2.0";
    private static final String TAG = "OpmlWriter";

    public void writeDocument(List<Feed> feeds, Writer writer) throws IllegalArgumentException, IllegalStateException, IOException {
        Log.d(TAG, "Starting to write document");
        XmlSerializer xs = Xml.newSerializer();
        xs.setFeature(CommonSymbols.XML_FEATURE_INDENT_OUTPUT, true);
        xs.setOutput(writer);
        xs.startDocument("UTF-8", Boolean.valueOf(false));
        xs.startTag(null, OpmlSymbols.OPML);
        xs.attribute(null, "version", OPML_VERSION);
        xs.startTag(null, "head");
        xs.startTag(null, "title");
        xs.text(OPML_TITLE);
        xs.endTag(null, "title");
        xs.startTag(null, "dateCreated");
        xs.text(DateUtils.formatRFC822Date(new Date()));
        xs.endTag(null, "dateCreated");
        xs.endTag(null, "head");
        xs.startTag(null, "body");
        for (Feed feed : feeds) {
            xs.startTag(null, "outline");
            xs.attribute(null, "text", feed.getTitle());
            xs.attribute(null, "title", feed.getTitle());
            if (feed.getType() != null) {
                xs.attribute(null, "type", feed.getType());
            }
            xs.attribute(null, "xmlUrl", feed.getDownload_url());
            if (feed.getLink() != null) {
                xs.attribute(null, "htmlUrl", feed.getLink());
            }
            xs.endTag(null, "outline");
        }
        xs.endTag(null, "body");
        xs.endTag(null, OpmlSymbols.OPML);
        xs.endDocument();
        Log.d(TAG, "Finished writing document");
    }

    public String fileExtension() {
        return OpmlSymbols.OPML;
    }
}

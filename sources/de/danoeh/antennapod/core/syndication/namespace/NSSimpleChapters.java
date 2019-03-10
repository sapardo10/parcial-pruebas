package de.danoeh.antennapod.core.syndication.namespace;

import android.util.Log;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.SimpleChapter;
import de.danoeh.antennapod.core.syndication.handler.HandlerState;
import de.danoeh.antennapod.core.util.DateUtils;
import java.util.ArrayList;
import org.xml.sax.Attributes;

public class NSSimpleChapters extends Namespace {
    private static final String CHAPTER = "chapter";
    private static final String CHAPTERS = "chapters";
    private static final String HREF = "href";
    public static final String NSTAG = "psc|sc";
    public static final String NSURI = "http://podlove.org/simple-chapters";
    private static final String START = "start";
    private static final String TAG = "NSSimpleChapters";
    private static final String TITLE = "title";

    public SyndElement handleElementStart(String localName, HandlerState state, Attributes attributes) {
        FeedItem currentItem = state.getCurrentItem();
        if (currentItem != null) {
            if (localName.equals(CHAPTERS)) {
                currentItem.setChapters(new ArrayList());
            } else if (localName.equals(CHAPTER)) {
                try {
                    currentItem.getChapters().add(new SimpleChapter(DateUtils.parseTimeString(attributes.getValue("start")), attributes.getValue("title"), currentItem, attributes.getValue(HREF)));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Unable to read chapter", e);
                }
            }
        }
        return new SyndElement(localName, this);
    }

    public void handleElementEnd(String localName, HandlerState state) {
    }
}

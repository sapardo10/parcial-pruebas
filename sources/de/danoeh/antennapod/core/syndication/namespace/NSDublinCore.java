package de.danoeh.antennapod.core.syndication.namespace;

import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.syndication.handler.HandlerState;
import de.danoeh.antennapod.core.util.DateUtils;
import org.xml.sax.Attributes;

public class NSDublinCore extends Namespace {
    private static final String DATE = "date";
    private static final String ITEM = "item";
    public static final String NSTAG = "dc";
    public static final String NSURI = "http://purl.org/dc/elements/1.1/";
    private static final String TAG = "NSDublinCore";

    public SyndElement handleElementStart(String localName, HandlerState state, Attributes attributes) {
        return new SyndElement(localName, this);
    }

    public void handleElementEnd(String localName, HandlerState state) {
        if (state.getCurrentItem() != null && state.getContentBuf() != null) {
            if (state.getTagstack() != null && state.getTagstack().size() >= 2) {
                FeedItem currentItem = state.getCurrentItem();
                String top = ((SyndElement) state.getTagstack().peek()).getName();
                String second = state.getSecondTag().getName();
                if (DATE.equals(top) && "item".equals(second)) {
                    currentItem.setPubDate(DateUtils.parse(state.getContentBuf().toString()));
                }
            }
        }
    }
}

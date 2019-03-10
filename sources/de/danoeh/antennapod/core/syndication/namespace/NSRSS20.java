package de.danoeh.antennapod.core.syndication.namespace;

import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.syndication.handler.HandlerState;
import de.danoeh.antennapod.core.syndication.util.SyndTypeUtils;
import de.danoeh.antennapod.core.util.DateUtils;
import org.xml.sax.Attributes;

public class NSRSS20 extends Namespace {
    public static final String CHANNEL = "channel";
    private static final String DESCR = "description";
    private static final String ENCLOSURE = "enclosure";
    private static final String ENC_LEN = "length";
    private static final String ENC_TYPE = "type";
    private static final String ENC_URL = "url";
    private static final String GUID = "guid";
    private static final String IMAGE = "image";
    public static final String ITEM = "item";
    private static final String LANGUAGE = "language";
    private static final String LINK = "link";
    private static final String PUBDATE = "pubDate";
    private static final String TAG = "NSRSS20";
    private static final String TITLE = "title";
    private static final String URL = "url";

    public SyndElement handleElementStart(String localName, HandlerState state, Attributes attributes) {
        String str = localName;
        Attributes attributes2 = attributes;
        if (ITEM.equals(str)) {
            state.setCurrentItem(new FeedItem());
            state.getItems().add(state.getCurrentItem());
            state.getCurrentItem().setFeed(state.getFeed());
        } else {
            HandlerState handlerState = state;
            if (ENCLOSURE.equals(str)) {
                String type;
                String type2 = attributes2.getValue("type");
                String url = attributes2.getValue("url");
                boolean validType = SyndTypeUtils.enclosureTypeValid(type2);
                boolean validType2;
                if (validType) {
                    type = type2;
                    validType2 = validType;
                } else {
                    type2 = SyndTypeUtils.getMimeTypeFromUrl(url);
                    type = type2;
                    validType2 = SyndTypeUtils.enclosureTypeValid(type2);
                }
                boolean validUrl = TextUtils.isEmpty(url) ^ 1;
                if (state.getCurrentItem() != null && state.getCurrentItem().getMedia() == null && validType && validUrl) {
                    long size;
                    try {
                        long size2 = Long.parseLong(attributes2.getValue(ENC_LEN));
                        if (size2 < PlaybackStateCompat.ACTION_PREPARE) {
                            size2 = 0;
                        }
                        size = size2;
                    } catch (NumberFormatException e) {
                        Log.d(TAG, "Length attribute could not be parsed.");
                        size = 0;
                    }
                    state.getCurrentItem().setMedia(new FeedMedia(state.getCurrentItem(), url, size, type));
                }
            }
        }
        return new SyndElement(str, this);
    }

    public void handleElementEnd(String localName, HandlerState state) {
        if (ITEM.equals(localName)) {
            if (state.getCurrentItem() != null) {
                FeedItem currentItem = state.getCurrentItem();
                if (currentItem.getTitle() == null) {
                    currentItem.setTitle(currentItem.getDescription());
                }
                if (state.getTempObjects().containsKey("duration")) {
                    if (currentItem.hasMedia()) {
                        currentItem.getMedia().setDuration(((Integer) state.getTempObjects().get("duration")).intValue());
                    }
                    state.getTempObjects().remove("duration");
                }
            }
            state.setCurrentItem(null);
        } else if (state.getTagstack().size() >= 2 && state.getContentBuf() != null) {
            String content = state.getContentBuf().toString();
            String top = ((SyndElement) state.getTagstack().peek()).getName();
            String second = state.getSecondTag().getName();
            String third = null;
            if (state.getTagstack().size() >= 3) {
                third = state.getThirdTag().getName();
            }
            if (GUID.equals(top) && ITEM.equals(second)) {
                if (!TextUtils.isEmpty(content) && state.getCurrentItem() != null) {
                    state.getCurrentItem().setItemIdentifier(content);
                }
            } else if ("title".equals(top)) {
                String title = content.trim();
                if (ITEM.equals(second) && state.getCurrentItem() != null) {
                    state.getCurrentItem().setTitle(title);
                } else if (CHANNEL.equals(second) && state.getFeed() != null) {
                    state.getFeed().setTitle(title);
                }
            } else if ("link".equals(top)) {
                if (CHANNEL.equals(second) && state.getFeed() != null) {
                    state.getFeed().setLink(content);
                } else if (ITEM.equals(second) && state.getCurrentItem() != null) {
                    state.getCurrentItem().setLink(content);
                }
            } else if ("pubDate".equals(top) && ITEM.equals(second) && state.getCurrentItem() != null) {
                state.getCurrentItem().setPubDate(DateUtils.parse(content));
            } else if ("url".equals(top) && "image".equals(second) && CHANNEL.equals(third)) {
                if (state.getFeed() != null) {
                    state.getFeed().setImageUrl(content);
                }
            } else if ("description".equals(localName)) {
                if (CHANNEL.equals(second) && state.getFeed() != null) {
                    state.getFeed().setDescription(content);
                } else if (ITEM.equals(second) && state.getCurrentItem() != null) {
                    state.getCurrentItem().setDescription(content);
                }
            } else if ("language".equals(localName) && state.getFeed() != null) {
                state.getFeed().setLanguage(content.toLowerCase());
            }
        }
    }
}

package de.danoeh.antennapod.core.syndication.namespace.atom;

import android.text.TextUtils;
import android.util.Log;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.syndication.handler.HandlerState;
import de.danoeh.antennapod.core.syndication.namespace.Namespace;
import de.danoeh.antennapod.core.syndication.namespace.SyndElement;
import de.danoeh.antennapod.core.syndication.util.SyndTypeUtils;
import de.danoeh.antennapod.core.util.DateUtils;
import org.xml.sax.Attributes;

public class NSAtom extends Namespace {
    private static final String AUTHOR = "author";
    private static final String AUTHOR_NAME = "name";
    private static final String CONTENT = "content";
    private static final String ENTRY = "entry";
    private static final String FEED = "feed";
    private static final String ID = "id";
    private static final String IMAGE_ICON = "icon";
    private static final String IMAGE_LOGO = "logo";
    private static final String LINK = "link";
    private static final String LINK_HREF = "href";
    private static final String LINK_LENGTH = "length";
    private static final String LINK_REL = "rel";
    private static final String LINK_REL_ALTERNATE = "alternate";
    private static final String LINK_REL_ARCHIVES = "archives";
    private static final String LINK_REL_ENCLOSURE = "enclosure";
    private static final String LINK_REL_NEXT = "next";
    private static final String LINK_REL_PAYMENT = "payment";
    private static final String LINK_TITLE = "title";
    private static final String LINK_TYPE = "type";
    private static final String LINK_TYPE_ATOM = "application/atom+xml";
    private static final String LINK_TYPE_HTML = "text/html";
    private static final String LINK_TYPE_RSS = "application/rss+xml";
    private static final String LINK_TYPE_XHTML = "application/xml+xhtml";
    public static final String NSTAG = "atom";
    public static final String NSURI = "http://www.w3.org/2005/Atom";
    private static final String PUBLISHED = "published";
    private static final String SUBTITLE = "subtitle";
    private static final String SUMMARY = "summary";
    private static final String TAG = "NSAtom";
    private static final String TEXT_TYPE = "type";
    private static final String TITLE = "title";
    private static final String UPDATED = "updated";
    private static final String isFeed = "feed|channel";
    private static final String isFeedItem = "entry|item";
    private static final String isText = "title|content|subtitle|summary";

    public SyndElement handleElementStart(String localName, HandlerState state, Attributes attributes) {
        Namespace namespace = this;
        String str = localName;
        HandlerState handlerState = state;
        Attributes attributes2 = attributes;
        if (ENTRY.equals(str)) {
            handlerState.setCurrentItem(new FeedItem());
            state.getItems().add(state.getCurrentItem());
            state.getCurrentItem().setFeed(state.getFeed());
        } else if (str.matches(isText)) {
            return new AtomText(str, namespace, attributes2.getValue("type"));
        } else {
            if ("link".equals(str)) {
                String href = attributes2.getValue(LINK_HREF);
                String rel = attributes2.getValue(LINK_REL);
                SyndElement parent = (SyndElement) state.getTagstack().peek();
                String type;
                if (parent.getName().matches(isFeedItem)) {
                    if (LINK_REL_ALTERNATE.equals(rel)) {
                        state.getCurrentItem().setLink(href);
                    } else if (LINK_REL_ENCLOSURE.equals(rel)) {
                        long size;
                        String strSize = attributes2.getValue(LINK_LENGTH);
                        long size2 = 0;
                        if (strSize != null) {
                            try {
                                size2 = Long.parseLong(strSize);
                            } catch (NumberFormatException e) {
                                Log.d(TAG, "Length attribute could not be parsed.");
                                size = 0;
                            }
                        }
                        size = size2;
                        type = attributes2.getValue("type");
                        if (type == null) {
                            type = SyndTypeUtils.getMimeTypeFromUrl(href);
                        }
                        long j;
                        if (SyndTypeUtils.enclosureTypeValid(type)) {
                            FeedItem currItem = state.getCurrentItem();
                            if (currItem == null || currItem.hasMedia()) {
                                j = size;
                                size = currItem;
                            } else {
                                FeedMedia feedMedia = r5;
                                long j2 = size;
                                size = currItem;
                                FeedMedia feedMedia2 = new FeedMedia(currItem, href, j2, type);
                                size.setMedia(feedMedia);
                            }
                        } else {
                            j = size;
                        }
                    } else if (LINK_REL_PAYMENT.equals(rel)) {
                        state.getCurrentItem().setPaymentLink(href);
                    }
                } else if (parent.getName().matches(isFeed)) {
                    String title;
                    if (LINK_REL_ALTERNATE.equals(rel)) {
                        type = attributes2.getValue("type");
                        if (state.getFeed() != null) {
                            if (type == null) {
                                if (state.getFeed().getLink() == null) {
                                    state.getFeed().setLink(href);
                                }
                            }
                            if (!LINK_TYPE_HTML.equals(type)) {
                                if (LINK_TYPE_XHTML.equals(type)) {
                                }
                            }
                            state.getFeed().setLink(href);
                        }
                        if (!LINK_TYPE_ATOM.equals(type)) {
                            if (LINK_TYPE_RSS.equals(type)) {
                            }
                        }
                        title = attributes2.getValue("title");
                        if (TextUtils.isEmpty(title)) {
                            title = href;
                        }
                        handlerState.addAlternateFeedUrl(title, href);
                    } else if (LINK_REL_ARCHIVES.equals(rel) && state.getFeed() != null) {
                        type = attributes2.getValue("type");
                        if (!LINK_TYPE_ATOM.equals(type)) {
                            if (!LINK_TYPE_RSS.equals(type)) {
                                if (!LINK_TYPE_HTML.equals(type)) {
                                    LINK_TYPE_XHTML.equals(type);
                                }
                            }
                        }
                        title = attributes2.getValue("title");
                        if (TextUtils.isEmpty(title)) {
                            title = href;
                        }
                        handlerState.addAlternateFeedUrl(title, href);
                    } else if (LINK_REL_PAYMENT.equals(rel) && state.getFeed() != null) {
                        state.getFeed().setPaymentLink(href);
                    } else if (LINK_REL_NEXT.equals(rel) && state.getFeed() != null) {
                        state.getFeed().setPaged(true);
                        state.getFeed().setNextPageLink(href);
                    }
                }
            }
        }
        return new SyndElement(str, namespace);
    }

    public void handleElementEnd(String localName, HandlerState state) {
        if (ENTRY.equals(localName)) {
            if (state.getCurrentItem() != null) {
                if (state.getTempObjects().containsKey("duration")) {
                    FeedItem currentItem = state.getCurrentItem();
                    if (currentItem.hasMedia()) {
                        currentItem.getMedia().setDuration(((Integer) state.getTempObjects().get("duration")).intValue());
                    }
                    state.getTempObjects().remove("duration");
                }
            }
            state.setCurrentItem(null);
        }
        if (state.getTagstack().size() >= 2) {
            String content;
            AtomText textElement = null;
            if (state.getContentBuf() != null) {
                content = state.getContentBuf().toString();
            } else {
                content = "";
            }
            SyndElement topElement = (SyndElement) state.getTagstack().peek();
            String top = topElement.getName();
            String second = state.getSecondTag().getName();
            if (top.matches(isText)) {
                textElement = (AtomText) topElement;
                textElement.setContent(content);
            }
            if ("id".equals(top)) {
                if ("feed".equals(second) && state.getFeed() != null) {
                    state.getFeed().setFeedIdentifier(content);
                } else if (ENTRY.equals(second) && state.getCurrentItem() != null) {
                    state.getCurrentItem().setItemIdentifier(content);
                }
            } else if (!"title".equals(top) || textElement == null) {
                if (SUBTITLE.equals(top) && "feed".equals(second) && textElement != null) {
                    if (state.getFeed() != null) {
                        state.getFeed().setDescription(textElement.getProcessedContent());
                        return;
                    }
                }
                if ("content".equals(top) && ENTRY.equals(second) && textElement != null) {
                    if (state.getCurrentItem() != null) {
                        state.getCurrentItem().setDescription(textElement.getProcessedContent());
                        return;
                    }
                }
                if (SUMMARY.equals(top) && ENTRY.equals(second) && textElement != null) {
                    if (state.getCurrentItem() != null && state.getCurrentItem().getDescription() == null) {
                        state.getCurrentItem().setDescription(textElement.getProcessedContent());
                        return;
                    }
                }
                if (UPDATED.equals(top) && ENTRY.equals(second) && state.getCurrentItem() != null) {
                    if (state.getCurrentItem().getPubDate() == null) {
                        state.getCurrentItem().setPubDate(DateUtils.parse(content));
                        return;
                    }
                }
                if (PUBLISHED.equals(top) && ENTRY.equals(second) && state.getCurrentItem() != null) {
                    state.getCurrentItem().setPubDate(DateUtils.parse(content));
                } else if (IMAGE_LOGO.equals(top) && state.getFeed() != null && state.getFeed().getImageUrl() == null) {
                    state.getFeed().setImageUrl(content);
                } else if (IMAGE_ICON.equals(top) && state.getFeed() != null) {
                    state.getFeed().setImageUrl(content);
                } else if (!"name".equals(top) || !"author".equals(second)) {
                } else {
                    if (state.getFeed() != null && state.getCurrentItem() == null) {
                        String currentName = state.getFeed().getAuthor();
                        if (currentName == null) {
                            state.getFeed().setAuthor(content);
                            return;
                        }
                        Feed feed = state.getFeed();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(currentName);
                        stringBuilder.append(", ");
                        stringBuilder.append(content);
                        feed.setAuthor(stringBuilder.toString());
                    }
                }
            } else if ("feed".equals(second) && state.getFeed() != null) {
                state.getFeed().setTitle(textElement.getProcessedContent());
            } else if (ENTRY.equals(second) && state.getCurrentItem() != null) {
                state.getCurrentItem().setTitle(textElement.getProcessedContent());
            }
        }
    }
}

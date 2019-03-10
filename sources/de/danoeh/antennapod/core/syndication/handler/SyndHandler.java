package de.danoeh.antennapod.core.syndication.handler;

import android.util.Log;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.syndication.handler.TypeGetter.Type;
import de.danoeh.antennapod.core.syndication.namespace.NSContent;
import de.danoeh.antennapod.core.syndication.namespace.NSDublinCore;
import de.danoeh.antennapod.core.syndication.namespace.NSITunes;
import de.danoeh.antennapod.core.syndication.namespace.NSMedia;
import de.danoeh.antennapod.core.syndication.namespace.NSRSS20;
import de.danoeh.antennapod.core.syndication.namespace.NSSimpleChapters;
import de.danoeh.antennapod.core.syndication.namespace.Namespace;
import de.danoeh.antennapod.core.syndication.namespace.atom.NSAtom;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class SyndHandler extends DefaultHandler {
    private static final String DEFAULT_PREFIX = "";
    private static final String TAG = "SyndHandler";
    final HandlerState state;

    public SyndHandler(Feed feed, Type type) {
        this.state = new HandlerState(feed);
        if (type != Type.RSS20) {
            if (type != Type.RSS091) {
                return;
            }
        }
        this.state.defaultNamespaces.push(new NSRSS20());
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.state.contentBuf = new StringBuilder();
        Namespace handler = getHandlingNamespace(uri, qName);
        if (handler != null) {
            this.state.tagstack.push(handler.handleElementStart(localName, this.state, attributes));
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!this.state.tagstack.empty()) {
            if (this.state.getTagstack().size() < 2) {
                return;
            }
            if (this.state.contentBuf != null) {
                this.state.contentBuf.append(ch, start, length);
            }
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        Namespace handler = getHandlingNamespace(uri, qName);
        if (handler != null) {
            handler.handleElementEnd(localName, this.state);
            this.state.tagstack.pop();
        }
        this.state.contentBuf = null;
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        if (this.state.defaultNamespaces.size() > 1 && prefix.equals("")) {
            this.state.defaultNamespaces.pop();
        }
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (!this.state.namespaces.containsKey(uri)) {
            if (!uri.equals(NSAtom.NSURI)) {
                if (uri.equals(NSContent.NSURI)) {
                    if (prefix.equals(NSContent.NSTAG)) {
                        this.state.namespaces.put(uri, new NSContent());
                        Log.d(TAG, "Recognized Content namespace");
                        return;
                    }
                }
                if (uri.equals(NSITunes.NSURI)) {
                    if (prefix.equals(NSITunes.NSTAG)) {
                        this.state.namespaces.put(uri, new NSITunes());
                        Log.d(TAG, "Recognized ITunes namespace");
                        return;
                    }
                }
                if (uri.equals(NSSimpleChapters.NSURI)) {
                    if (prefix.matches(NSSimpleChapters.NSTAG)) {
                        this.state.namespaces.put(uri, new NSSimpleChapters());
                        Log.d(TAG, "Recognized SimpleChapters namespace");
                        return;
                    }
                }
                if (uri.equals(NSMedia.NSURI)) {
                    if (prefix.equals("media")) {
                        this.state.namespaces.put(uri, new NSMedia());
                        Log.d(TAG, "Recognized media namespace");
                        return;
                    }
                }
                if (!uri.equals(NSDublinCore.NSURI)) {
                    return;
                }
                if (prefix.equals(NSDublinCore.NSTAG)) {
                    this.state.namespaces.put(uri, new NSDublinCore());
                    Log.d(TAG, "Recognized DublinCore namespace");
                }
            } else if (prefix.equals("")) {
                this.state.defaultNamespaces.push(new NSAtom());
            } else if (prefix.equals("atom")) {
                this.state.namespaces.put(uri, new NSAtom());
                Log.d(TAG, "Recognized Atom namespace");
            }
        }
    }

    private Namespace getHandlingNamespace(String uri, String qName) {
        Namespace handler = (Namespace) this.state.namespaces.get(uri);
        if (handler != null || this.state.defaultNamespaces.empty()) {
            return handler;
        }
        if (qName.contains(":")) {
            return handler;
        }
        return (Namespace) this.state.defaultNamespaces.peek();
    }

    public void endDocument() throws SAXException {
        super.endDocument();
        this.state.getFeed().setItems(this.state.getItems());
    }

    public HandlerState getState() {
        return this.state;
    }
}

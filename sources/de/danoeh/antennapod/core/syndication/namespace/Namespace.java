package de.danoeh.antennapod.core.syndication.namespace;

import de.danoeh.antennapod.core.syndication.handler.HandlerState;
import org.xml.sax.Attributes;

public abstract class Namespace {
    public static final String NSTAG = null;
    public static final String NSURI = null;

    public abstract void handleElementEnd(String str, HandlerState handlerState);

    public abstract SyndElement handleElementStart(String str, HandlerState handlerState, Attributes attributes);
}

package de.danoeh.antennapod.core.syndication.handler;

import de.danoeh.antennapod.core.feed.Feed;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.io.input.XmlStreamReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class FeedHandler {
    public FeedHandlerResult parseFeed(Feed feed) throws SAXException, IOException, ParserConfigurationException, UnsupportedFeedtypeException {
        SyndHandler handler = new SyndHandler(feed, new TypeGetter().getType(feed));
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser saxParser = factory.newSAXParser();
        Reader inputStreamReader = new XmlStreamReader(new File(feed.getFile_url()));
        saxParser.parse(new InputSource(inputStreamReader), handler);
        inputStreamReader.close();
        return new FeedHandlerResult(handler.state.feed, handler.state.alternateUrls);
    }
}

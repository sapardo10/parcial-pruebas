package de.danoeh.antennapod.core.export.opml;

import android.util.Log;
import de.danoeh.antennapod.core.BuildConfig;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class OpmlReader {
    private static final String TAG = "OpmlReader";
    private ArrayList<OpmlElement> elementList;
    private boolean isInOpml = false;

    public ArrayList<OpmlElement> readDocument(Reader reader) throws XmlPullParserException, IOException {
        this.elementList = new ArrayList();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(reader);
        for (int eventType = xpp.getEventType(); eventType != 1; eventType = xpp.next()) {
            if (eventType != 0) {
                if (eventType == 2) {
                    if (xpp.getName().equals(OpmlSymbols.OPML)) {
                        this.isInOpml = true;
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Reached beginning of OPML tree.");
                        }
                    } else if (this.isInOpml && xpp.getName().equals("outline")) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Found new Opml element");
                        }
                        OpmlElement element = new OpmlElement();
                        String title = xpp.getAttributeValue(null, "title");
                        if (title != null) {
                            String str = TAG;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Using title: ");
                            stringBuilder.append(title);
                            Log.i(str, stringBuilder.toString());
                            element.setText(title);
                        } else {
                            Log.i(TAG, "Title not found, using text");
                            element.setText(xpp.getAttributeValue(null, "text"));
                        }
                        element.setXmlUrl(xpp.getAttributeValue(null, "xmlUrl"));
                        element.setHtmlUrl(xpp.getAttributeValue(null, "htmlUrl"));
                        element.setType(xpp.getAttributeValue(null, "type"));
                        if (element.getXmlUrl() != null) {
                            if (element.getText() == null) {
                                Log.i(TAG, "Opml element has no text attribute.");
                                element.setText(element.getXmlUrl());
                            }
                            this.elementList.add(element);
                        } else if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Skipping element because of missing xml url");
                        }
                    }
                }
            } else if (BuildConfig.DEBUG) {
                Log.d(TAG, "Reached beginning of document");
            }
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Parsing finished.");
        }
        return this.elementList;
    }
}

package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.core.export.opml.OpmlElement;
import java.util.ArrayList;

public class OpmlImportHolder {
    private static ArrayList<OpmlElement> readElements;

    private OpmlImportHolder() {
    }

    public static ArrayList<OpmlElement> getReadElements() {
        return readElements;
    }

    public static void setReadElements(ArrayList<OpmlElement> _readElements) {
        readElements = _readElements;
    }
}

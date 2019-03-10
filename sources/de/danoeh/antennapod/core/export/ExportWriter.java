package de.danoeh.antennapod.core.export;

import de.danoeh.antennapod.core.feed.Feed;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public interface ExportWriter {
    String fileExtension();

    void writeDocument(List<Feed> list, Writer writer) throws IllegalArgumentException, IllegalStateException, IOException;
}

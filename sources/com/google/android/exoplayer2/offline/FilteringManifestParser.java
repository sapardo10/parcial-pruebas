package com.google.android.exoplayer2.offline;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public final class FilteringManifestParser<T extends FilterableManifest<T>> implements Parser<T> {
    private final Parser<T> parser;
    private final List<StreamKey> streamKeys;

    public FilteringManifestParser(Parser<T> parser, List<StreamKey> streamKeys) {
        this.parser = parser;
        this.streamKeys = streamKeys;
    }

    public T parse(Uri uri, InputStream inputStream) throws IOException {
        T manifest = (FilterableManifest) this.parser.parse(uri, inputStream);
        List list = this.streamKeys;
        if (list != null) {
            if (!list.isEmpty()) {
                return (FilterableManifest) manifest.copy(this.streamKeys);
            }
        }
        return manifest;
    }
}

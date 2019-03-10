package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.upstream.DataSource.-CC;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class DummyDataSource implements DataSource {
    public static final Factory FACTORY = -$$Lambda$DummyDataSource$5JL9ytmtADrptG840gjTuddaBKA.INSTANCE;
    public static final DummyDataSource INSTANCE = new DummyDataSource();

    public /* synthetic */ Map<String, List<String>> getResponseHeaders() {
        return -CC.$default$getResponseHeaders(this);
    }

    private DummyDataSource() {
    }

    public void addTransferListener(TransferListener transferListener) {
    }

    public long open(DataSpec dataSpec) throws IOException {
        throw new IOException("Dummy source");
    }

    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public Uri getUri() {
        return null;
    }

    public void close() throws IOException {
    }
}

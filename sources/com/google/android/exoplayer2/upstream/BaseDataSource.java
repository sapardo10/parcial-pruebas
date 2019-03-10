package com.google.android.exoplayer2.upstream;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.upstream.DataSource.-CC;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseDataSource implements DataSource {
    @Nullable
    private DataSpec dataSpec;
    private final boolean isNetwork;
    private int listenerCount;
    private final ArrayList<TransferListener> listeners = new ArrayList(1);

    public /* synthetic */ Map<String, List<String>> getResponseHeaders() {
        return -CC.$default$getResponseHeaders(this);
    }

    protected BaseDataSource(boolean isNetwork) {
        this.isNetwork = isNetwork;
    }

    public final void addTransferListener(TransferListener transferListener) {
        if (!this.listeners.contains(transferListener)) {
            this.listeners.add(transferListener);
            this.listenerCount++;
        }
    }

    protected final void transferInitializing(DataSpec dataSpec) {
        for (int i = 0; i < this.listenerCount; i++) {
            ((TransferListener) this.listeners.get(i)).onTransferInitializing(this, dataSpec, this.isNetwork);
        }
    }

    protected final void transferStarted(DataSpec dataSpec) {
        this.dataSpec = dataSpec;
        for (int i = 0; i < this.listenerCount; i++) {
            ((TransferListener) this.listeners.get(i)).onTransferStart(this, dataSpec, this.isNetwork);
        }
    }

    protected final void bytesTransferred(int bytesTransferred) {
        DataSpec dataSpec = (DataSpec) Util.castNonNull(this.dataSpec);
        for (int i = 0; i < this.listenerCount; i++) {
            ((TransferListener) this.listeners.get(i)).onBytesTransferred(this, dataSpec, this.isNetwork, bytesTransferred);
        }
    }

    protected final void transferEnded() {
        DataSpec dataSpec = (DataSpec) Util.castNonNull(this.dataSpec);
        for (int i = 0; i < this.listenerCount; i++) {
            ((TransferListener) this.listeners.get(i)).onTransferEnd(this, dataSpec, this.isNetwork);
        }
        this.dataSpec = null;
    }
}

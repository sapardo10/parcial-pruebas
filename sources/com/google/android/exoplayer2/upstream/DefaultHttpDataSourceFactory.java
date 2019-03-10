package com.google.android.exoplayer2.upstream;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.upstream.HttpDataSource.BaseFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource.RequestProperties;

public final class DefaultHttpDataSourceFactory extends BaseFactory {
    private final boolean allowCrossProtocolRedirects;
    private final int connectTimeoutMillis;
    @Nullable
    private final TransferListener listener;
    private final int readTimeoutMillis;
    private final String userAgent;

    public DefaultHttpDataSourceFactory(String userAgent) {
        this(userAgent, null);
    }

    public DefaultHttpDataSourceFactory(String userAgent, @Nullable TransferListener listener) {
        this(userAgent, listener, 8000, 8000, false);
    }

    public DefaultHttpDataSourceFactory(String userAgent, int connectTimeoutMillis, int readTimeoutMillis, boolean allowCrossProtocolRedirects) {
        this(userAgent, null, connectTimeoutMillis, readTimeoutMillis, allowCrossProtocolRedirects);
    }

    public DefaultHttpDataSourceFactory(String userAgent, @Nullable TransferListener listener, int connectTimeoutMillis, int readTimeoutMillis, boolean allowCrossProtocolRedirects) {
        this.userAgent = userAgent;
        this.listener = listener;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.allowCrossProtocolRedirects = allowCrossProtocolRedirects;
    }

    protected DefaultHttpDataSource createDataSourceInternal(RequestProperties defaultRequestProperties) {
        DefaultHttpDataSource dataSource = new DefaultHttpDataSource(this.userAgent, null, this.connectTimeoutMillis, this.readTimeoutMillis, this.allowCrossProtocolRedirects, defaultRequestProperties);
        TransferListener transferListener = this.listener;
        if (transferListener != null) {
            dataSource.addTransferListener(transferListener);
        }
        return dataSource;
    }
}

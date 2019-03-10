package com.google.android.exoplayer2.upstream;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.upstream.DataSource.Factory;

public final class FileDataSourceFactory implements Factory {
    @Nullable
    private final TransferListener listener;

    public FileDataSourceFactory() {
        this(null);
    }

    public FileDataSourceFactory(@Nullable TransferListener listener) {
        this.listener = listener;
    }

    public DataSource createDataSource() {
        FileDataSource dataSource = new FileDataSource();
        TransferListener transferListener = this.listener;
        if (transferListener != null) {
            dataSource.addTransferListener(transferListener);
        }
        return dataSource;
    }
}

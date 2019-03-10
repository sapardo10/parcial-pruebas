package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Base64;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.net.URLDecoder;

public final class DataSchemeDataSource extends BaseDataSource {
    public static final String SCHEME_DATA = "data";
    private int bytesRead;
    @Nullable
    private byte[] data;
    @Nullable
    private DataSpec dataSpec;

    public DataSchemeDataSource() {
        super(false);
    }

    public long open(DataSpec dataSpec) throws IOException {
        transferInitializing(dataSpec);
        this.dataSpec = dataSpec;
        Uri uri = dataSpec.uri;
        String scheme = uri.getScheme();
        if ("data".equals(scheme)) {
            String[] uriParts = Util.split(uri.getSchemeSpecificPart(), ",");
            if (uriParts.length == 2) {
                String dataString = uriParts[1];
                if (uriParts[0].contains(";base64")) {
                    try {
                        this.data = Base64.decode(dataString, 0);
                    } catch (IllegalArgumentException e) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Error while parsing Base64 encoded string: ");
                        stringBuilder.append(dataString);
                        throw new ParserException(stringBuilder.toString(), e);
                    }
                }
                this.data = Util.getUtf8Bytes(URLDecoder.decode(dataString, "US-ASCII"));
                transferStarted(dataSpec);
                return (long) this.data.length;
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Unexpected URI format: ");
            stringBuilder2.append(uri);
            throw new ParserException(stringBuilder2.toString());
        }
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append("Unsupported scheme: ");
        stringBuilder3.append(scheme);
        throw new ParserException(stringBuilder3.toString());
    }

    public int read(byte[] buffer, int offset, int readLength) {
        if (readLength == 0) {
            return 0;
        }
        int remainingBytes = this.data.length - this.bytesRead;
        if (remainingBytes == 0) {
            return -1;
        }
        readLength = Math.min(readLength, remainingBytes);
        System.arraycopy(this.data, this.bytesRead, buffer, offset, readLength);
        this.bytesRead += readLength;
        bytesTransferred(readLength);
        return readLength;
    }

    @Nullable
    public Uri getUri() {
        DataSpec dataSpec = this.dataSpec;
        return dataSpec != null ? dataSpec.uri : null;
    }

    public void close() throws IOException {
        if (this.data != null) {
            this.data = null;
            transferEnded();
        }
        this.dataSpec = null;
    }
}

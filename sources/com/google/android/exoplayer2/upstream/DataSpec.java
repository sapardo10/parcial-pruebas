package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public final class DataSpec {
    public static final int FLAG_ALLOW_CACHING_UNKNOWN_LENGTH = 2;
    public static final int FLAG_ALLOW_GZIP = 1;
    public static final int HTTP_METHOD_GET = 1;
    public static final int HTTP_METHOD_HEAD = 3;
    public static final int HTTP_METHOD_POST = 2;
    public final long absoluteStreamPosition;
    public final int flags;
    @Nullable
    public final byte[] httpBody;
    public final int httpMethod;
    @Nullable
    public final String key;
    public final long length;
    public final long position;
    @Nullable
    @Deprecated
    public final byte[] postBody;
    public final Uri uri;

    public DataSpec(Uri uri) {
        this(uri, 0);
    }

    public DataSpec(Uri uri, int flags) {
        this(uri, 0, -1, null, flags);
    }

    public DataSpec(Uri uri, long absoluteStreamPosition, long length, @Nullable String key) {
        this(uri, absoluteStreamPosition, absoluteStreamPosition, length, key, 0);
    }

    public DataSpec(Uri uri, long absoluteStreamPosition, long length, @Nullable String key, int flags) {
        this(uri, absoluteStreamPosition, absoluteStreamPosition, length, key, flags);
    }

    public DataSpec(Uri uri, long absoluteStreamPosition, long position, long length, @Nullable String key, int flags) {
        this(uri, null, absoluteStreamPosition, position, length, key, flags);
    }

    public DataSpec(Uri uri, @Nullable byte[] postBody, long absoluteStreamPosition, long position, long length, @Nullable String key, int flags) {
        this(uri, postBody != null ? 2 : 1, postBody, absoluteStreamPosition, position, length, key, flags);
    }

    public DataSpec(Uri uri, int httpMethod, @Nullable byte[] httpBody, long absoluteStreamPosition, long position, long length, @Nullable String key, int flags) {
        DataSpec dataSpec = this;
        byte[] bArr = httpBody;
        long j = absoluteStreamPosition;
        long j2 = position;
        long j3 = length;
        boolean z = true;
        Assertions.checkArgument(j >= 0);
        Assertions.checkArgument(j2 >= 0);
        if (j3 <= 0) {
            if (j3 != -1) {
                z = false;
            }
        }
        Assertions.checkArgument(z);
        dataSpec.uri = uri;
        dataSpec.httpMethod = httpMethod;
        byte[] bArr2 = (bArr == null || bArr.length == 0) ? null : bArr;
        dataSpec.httpBody = bArr2;
        dataSpec.postBody = dataSpec.httpBody;
        dataSpec.absoluteStreamPosition = j;
        dataSpec.position = j2;
        dataSpec.length = j3;
        dataSpec.key = key;
        dataSpec.flags = flags;
    }

    public boolean isFlagSet(int flag) {
        return (this.flags & flag) == flag;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DataSpec[");
        stringBuilder.append(getHttpMethodString());
        stringBuilder.append(StringUtils.SPACE);
        stringBuilder.append(this.uri);
        stringBuilder.append(", ");
        stringBuilder.append(Arrays.toString(this.httpBody));
        stringBuilder.append(", ");
        stringBuilder.append(this.absoluteStreamPosition);
        stringBuilder.append(", ");
        stringBuilder.append(this.position);
        stringBuilder.append(", ");
        stringBuilder.append(this.length);
        stringBuilder.append(", ");
        stringBuilder.append(this.key);
        stringBuilder.append(", ");
        stringBuilder.append(this.flags);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public final String getHttpMethodString() {
        return getStringForHttpMethod(this.httpMethod);
    }

    public static String getStringForHttpMethod(int httpMethod) {
        switch (httpMethod) {
            case 1:
                return "GET";
            case 2:
                return "POST";
            case 3:
                return "HEAD";
            default:
                throw new AssertionError(httpMethod);
        }
    }

    public DataSpec subrange(long offset) {
        long j = this.length;
        long j2 = -1;
        if (j != -1) {
            j2 = j - offset;
        }
        return subrange(offset, j2);
    }

    public DataSpec subrange(long offset, long length) {
        DataSpec dataSpec = this;
        if (offset == 0 && dataSpec.length == length) {
            return dataSpec;
        }
        return new DataSpec(dataSpec.uri, dataSpec.httpMethod, dataSpec.httpBody, dataSpec.absoluteStreamPosition + offset, dataSpec.position + offset, length, dataSpec.key, dataSpec.flags);
    }

    public DataSpec withUri(Uri uri) {
        return new DataSpec(uri, this.httpMethod, this.httpBody, this.absoluteStreamPosition, this.position, this.length, this.key, this.flags);
    }
}

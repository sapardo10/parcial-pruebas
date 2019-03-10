package com.google.android.exoplayer2.upstream;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Predicate;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface HttpDataSource extends DataSource {
    public static final Predicate<String> REJECT_PAYWALL_TYPES = -$$Lambda$HttpDataSource$fz-i4cgBB9tTB1JUdq8hmlAPFIw.INSTANCE;

    public final /* synthetic */ class -CC {
        public static /* synthetic */ boolean lambda$static$0(String contentType) {
            contentType = Util.toLowerInvariant(contentType);
            if (!TextUtils.isEmpty(contentType)) {
                if (contentType.contains("text")) {
                    if (contentType.contains(MimeTypes.TEXT_VTT)) {
                    }
                }
                if (!contentType.contains("html")) {
                    if (!contentType.contains("xml")) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static class HttpDataSourceException extends IOException {
        public static final int TYPE_CLOSE = 3;
        public static final int TYPE_OPEN = 1;
        public static final int TYPE_READ = 2;
        public final DataSpec dataSpec;
        public final int type;

        public HttpDataSourceException(DataSpec dataSpec, int type) {
            this.dataSpec = dataSpec;
            this.type = type;
        }

        public HttpDataSourceException(String message, DataSpec dataSpec, int type) {
            super(message);
            this.dataSpec = dataSpec;
            this.type = type;
        }

        public HttpDataSourceException(IOException cause, DataSpec dataSpec, int type) {
            super(cause);
            this.dataSpec = dataSpec;
            this.type = type;
        }

        public HttpDataSourceException(String message, IOException cause, DataSpec dataSpec, int type) {
            super(message, cause);
            this.dataSpec = dataSpec;
            this.type = type;
        }
    }

    public static final class RequestProperties {
        private final Map<String, String> requestProperties = new HashMap();
        private Map<String, String> requestPropertiesSnapshot;

        public synchronized void set(String name, String value) {
            this.requestPropertiesSnapshot = null;
            this.requestProperties.put(name, value);
        }

        public synchronized void set(Map<String, String> properties) {
            this.requestPropertiesSnapshot = null;
            this.requestProperties.putAll(properties);
        }

        public synchronized void clearAndSet(Map<String, String> properties) {
            this.requestPropertiesSnapshot = null;
            this.requestProperties.clear();
            this.requestProperties.putAll(properties);
        }

        public synchronized void remove(String name) {
            this.requestPropertiesSnapshot = null;
            this.requestProperties.remove(name);
        }

        public synchronized void clear() {
            this.requestPropertiesSnapshot = null;
            this.requestProperties.clear();
        }

        public synchronized Map<String, String> getSnapshot() {
            if (this.requestPropertiesSnapshot == null) {
                this.requestPropertiesSnapshot = Collections.unmodifiableMap(new HashMap(this.requestProperties));
            }
            return this.requestPropertiesSnapshot;
        }
    }

    public interface Factory extends com.google.android.exoplayer2.upstream.DataSource.Factory {

        public final /* synthetic */ class -CC {
        }

        @Deprecated
        void clearAllDefaultRequestProperties();

        @Deprecated
        void clearDefaultRequestProperty(String str);

        HttpDataSource createDataSource();

        RequestProperties getDefaultRequestProperties();

        @Deprecated
        void setDefaultRequestProperty(String str, String str2);
    }

    public static final class InvalidContentTypeException extends HttpDataSourceException {
        public final String contentType;

        public InvalidContentTypeException(String contentType, DataSpec dataSpec) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid content type: ");
            stringBuilder.append(contentType);
            super(stringBuilder.toString(), dataSpec, 1);
            this.contentType = contentType;
        }
    }

    public static final class InvalidResponseCodeException extends HttpDataSourceException {
        public final Map<String, List<String>> headerFields;
        public final int responseCode;
        @Nullable
        public final String responseMessage;

        @Deprecated
        public InvalidResponseCodeException(int responseCode, Map<String, List<String>> headerFields, DataSpec dataSpec) {
            this(responseCode, null, headerFields, dataSpec);
        }

        public InvalidResponseCodeException(int responseCode, @Nullable String responseMessage, Map<String, List<String>> headerFields, DataSpec dataSpec) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Response code: ");
            stringBuilder.append(responseCode);
            super(stringBuilder.toString(), dataSpec, 1);
            this.responseCode = responseCode;
            this.responseMessage = responseMessage;
            this.headerFields = headerFields;
        }
    }

    public static abstract class BaseFactory implements Factory {
        private final RequestProperties defaultRequestProperties = new RequestProperties();

        protected abstract HttpDataSource createDataSourceInternal(RequestProperties requestProperties);

        public final HttpDataSource createDataSource() {
            return createDataSourceInternal(this.defaultRequestProperties);
        }

        public final RequestProperties getDefaultRequestProperties() {
            return this.defaultRequestProperties;
        }

        @Deprecated
        public final void setDefaultRequestProperty(String name, String value) {
            this.defaultRequestProperties.set(name, value);
        }

        @Deprecated
        public final void clearDefaultRequestProperty(String name) {
            this.defaultRequestProperties.remove(name);
        }

        @Deprecated
        public final void clearAllDefaultRequestProperties() {
            this.defaultRequestProperties.clear();
        }
    }

    void clearAllRequestProperties();

    void clearRequestProperty(String str);

    void close() throws HttpDataSourceException;

    Map<String, List<String>> getResponseHeaders();

    long open(DataSpec dataSpec) throws HttpDataSourceException;

    int read(byte[] bArr, int i, int i2) throws HttpDataSourceException;

    void setRequestProperty(String str, String str2);
}

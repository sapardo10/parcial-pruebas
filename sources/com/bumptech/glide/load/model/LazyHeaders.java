package com.bumptech.glide.load.model;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class LazyHeaders implements Headers {
    private volatile Map<String, String> combinedHeaders;
    private final Map<String, List<LazyHeaderFactory>> headers;

    public static final class Builder {
        private static final Map<String, List<LazyHeaderFactory>> DEFAULT_HEADERS;
        private static final String DEFAULT_USER_AGENT = getSanitizedUserAgent();
        private static final String USER_AGENT_HEADER = "User-Agent";
        private boolean copyOnModify = true;
        private Map<String, List<LazyHeaderFactory>> headers = DEFAULT_HEADERS;
        private boolean isUserAgentDefault = true;

        static {
            Map<String, List<LazyHeaderFactory>> temp = new HashMap(2);
            if (!TextUtils.isEmpty(DEFAULT_USER_AGENT)) {
                temp.put(USER_AGENT_HEADER, Collections.singletonList(new StringHeaderFactory(DEFAULT_USER_AGENT)));
            }
            DEFAULT_HEADERS = Collections.unmodifiableMap(temp);
        }

        public Builder addHeader(String key, String value) {
            return addHeader(key, new StringHeaderFactory(value));
        }

        public Builder addHeader(String key, LazyHeaderFactory factory) {
            if (this.isUserAgentDefault && USER_AGENT_HEADER.equalsIgnoreCase(key)) {
                return setHeader(key, factory);
            }
            copyIfNecessary();
            getFactories(key).add(factory);
            return this;
        }

        public Builder setHeader(String key, String value) {
            return setHeader(key, value == null ? null : new StringHeaderFactory(value));
        }

        public Builder setHeader(String key, LazyHeaderFactory factory) {
            copyIfNecessary();
            if (factory == null) {
                this.headers.remove(key);
            } else {
                List<LazyHeaderFactory> factories = getFactories(key);
                factories.clear();
                factories.add(factory);
            }
            if (this.isUserAgentDefault && USER_AGENT_HEADER.equalsIgnoreCase(key)) {
                this.isUserAgentDefault = false;
            }
            return this;
        }

        private List<LazyHeaderFactory> getFactories(String key) {
            List<LazyHeaderFactory> factories = (List) this.headers.get(key);
            if (factories != null) {
                return factories;
            }
            ArrayList factories2 = new ArrayList();
            this.headers.put(key, factories2);
            return factories2;
        }

        private void copyIfNecessary() {
            if (this.copyOnModify) {
                this.copyOnModify = false;
                this.headers = copyHeaders();
            }
        }

        public LazyHeaders build() {
            this.copyOnModify = true;
            return new LazyHeaders(this.headers);
        }

        private Map<String, List<LazyHeaderFactory>> copyHeaders() {
            Map<String, List<LazyHeaderFactory>> result = new HashMap(this.headers.size());
            for (Entry<String, List<LazyHeaderFactory>> entry : this.headers.entrySet()) {
                result.put(entry.getKey(), new ArrayList((Collection) entry.getValue()));
            }
            return result;
        }

        @VisibleForTesting
        static String getSanitizedUserAgent() {
            String defaultUserAgent = System.getProperty("http.agent");
            if (TextUtils.isEmpty(defaultUserAgent)) {
                return defaultUserAgent;
            }
            int length = defaultUserAgent.length();
            StringBuilder sb = new StringBuilder(defaultUserAgent.length());
            for (int i = 0; i < length; i++) {
                char c = defaultUserAgent.charAt(i);
                if ((c > '\u001f' || c == '\t') && c < '') {
                    sb.append(c);
                } else {
                    sb.append('?');
                }
            }
            return sb.toString();
        }
    }

    static final class StringHeaderFactory implements LazyHeaderFactory {
        private final String value;

        StringHeaderFactory(String value) {
            this.value = value;
        }

        public String buildHeader() {
            return this.value;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("StringHeaderFactory{value='");
            stringBuilder.append(this.value);
            stringBuilder.append('\'');
            stringBuilder.append('}');
            return stringBuilder.toString();
        }

        public boolean equals(Object o) {
            if (!(o instanceof StringHeaderFactory)) {
                return false;
            }
            return this.value.equals(((StringHeaderFactory) o).value);
        }

        public int hashCode() {
            return this.value.hashCode();
        }
    }

    LazyHeaders(Map<String, List<LazyHeaderFactory>> headers) {
        this.headers = Collections.unmodifiableMap(headers);
    }

    public Map<String, String> getHeaders() {
        if (this.combinedHeaders == null) {
            synchronized (this) {
                if (this.combinedHeaders == null) {
                    this.combinedHeaders = Collections.unmodifiableMap(generateHeaders());
                }
            }
        }
        return this.combinedHeaders;
    }

    private Map<String, String> generateHeaders() {
        Map<String, String> combinedHeaders = new HashMap();
        for (Entry<String, List<LazyHeaderFactory>> entry : this.headers.entrySet()) {
            String values = buildHeaderValue((List) entry.getValue());
            if (!TextUtils.isEmpty(values)) {
                combinedHeaders.put(entry.getKey(), values);
            }
        }
        return combinedHeaders;
    }

    @NonNull
    private String buildHeaderValue(@NonNull List<LazyHeaderFactory> factories) {
        StringBuilder sb = new StringBuilder();
        int size = factories.size();
        for (int i = 0; i < size; i++) {
            String header = ((LazyHeaderFactory) factories.get(i)).buildHeader();
            if (!TextUtils.isEmpty(header)) {
                sb.append(header);
                if (i != factories.size() - 1) {
                    sb.append(',');
                }
            }
        }
        return sb.toString();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("LazyHeaders{headers=");
        stringBuilder.append(this.headers);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public boolean equals(Object o) {
        if (!(o instanceof LazyHeaders)) {
            return false;
        }
        return this.headers.equals(((LazyHeaders) o).headers);
    }

    public int hashCode() {
        return this.headers.hashCode();
    }
}

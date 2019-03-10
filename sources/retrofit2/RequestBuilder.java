package retrofit2;

import javax.annotation.Nullable;
import okhttp3.FormBody$Builder;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Builder;
import okhttp3.MultipartBody.Part;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

final class RequestBuilder {
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final String PATH_SEGMENT_ALWAYS_ENCODE_SET = " \"<>^`{}|\\?#";
    private final HttpUrl baseUrl;
    @Nullable
    private RequestBody body;
    @Nullable
    private MediaType contentType;
    @Nullable
    private FormBody$Builder formBuilder;
    private final boolean hasBody;
    private final String method;
    @Nullable
    private Builder multipartBuilder;
    @Nullable
    private String relativeUrl;
    private final Request.Builder requestBuilder = new Request.Builder();
    @Nullable
    private HttpUrl.Builder urlBuilder;

    RequestBuilder(String method, HttpUrl baseUrl, @Nullable String relativeUrl, @Nullable Headers headers, @Nullable MediaType contentType, boolean hasBody, boolean isFormEncoded, boolean isMultipart) {
        this.method = method;
        this.baseUrl = baseUrl;
        this.relativeUrl = relativeUrl;
        this.contentType = contentType;
        this.hasBody = hasBody;
        if (headers != null) {
            this.requestBuilder.headers(headers);
        }
        if (isFormEncoded) {
            this.formBuilder = new FormBody$Builder();
        } else if (isMultipart) {
            this.multipartBuilder = new Builder();
            this.multipartBuilder.setType(MultipartBody.FORM);
        }
    }

    void setRelativeUrl(Object relativeUrl) {
        this.relativeUrl = relativeUrl.toString();
    }

    void addHeader(String name, String value) {
        if ("Content-Type".equalsIgnoreCase(name)) {
            MediaType type = MediaType.parse(value);
            if (type != null) {
                this.contentType = type;
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Malformed content type: ");
            stringBuilder.append(value);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        this.requestBuilder.addHeader(name, value);
    }

    void addPathParam(String name, String value, boolean encoded) {
        String str = this.relativeUrl;
        if (str != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{");
            stringBuilder.append(name);
            stringBuilder.append("}");
            this.relativeUrl = str.replace(stringBuilder.toString(), canonicalizeForPath(value, encoded));
            return;
        }
        throw new AssertionError();
    }

    private static String canonicalizeForPath(String input, boolean alreadyEncoded) {
        int i = 0;
        int limit = input.length();
        while (i < limit) {
            int codePoint = input.codePointAt(i);
            if (codePoint >= 32 && codePoint < 127) {
                if (PATH_SEGMENT_ALWAYS_ENCODE_SET.indexOf(codePoint) == -1) {
                    if (!alreadyEncoded) {
                        if (codePoint != 47) {
                            if (codePoint == 37) {
                            }
                        }
                    }
                    i += Character.charCount(codePoint);
                }
            }
            Buffer out = new Buffer();
            out.writeUtf8(input, 0, i);
            canonicalizeForPath(out, input, i, limit, alreadyEncoded);
            return out.readUtf8();
        }
        return input;
    }

    private static void canonicalizeForPath(Buffer out, String input, int pos, int limit, boolean alreadyEncoded) {
        Buffer utf8Buffer = null;
        int i = pos;
        while (i < limit) {
            int codePoint = input.codePointAt(i);
            if (!alreadyEncoded || (codePoint != 9 && codePoint != 10 && codePoint != 12 && codePoint != 13)) {
                if (codePoint >= 32 && codePoint < 127) {
                    if (PATH_SEGMENT_ALWAYS_ENCODE_SET.indexOf(codePoint) == -1) {
                        if (!alreadyEncoded) {
                            if (codePoint != 47) {
                                if (codePoint == 37) {
                                }
                            }
                        }
                        out.writeUtf8CodePoint(codePoint);
                    }
                }
                if (utf8Buffer == null) {
                    utf8Buffer = new Buffer();
                }
                utf8Buffer.writeUtf8CodePoint(codePoint);
                while (!utf8Buffer.exhausted()) {
                    int b = utf8Buffer.readByte() & 255;
                    out.writeByte(37);
                    out.writeByte(HEX_DIGITS[(b >> 4) & 15]);
                    out.writeByte(HEX_DIGITS[b & 15]);
                }
            }
            i += Character.charCount(codePoint);
        }
    }

    void addQueryParam(String name, @Nullable String value, boolean encoded) {
        String str = this.relativeUrl;
        if (str != null) {
            this.urlBuilder = this.baseUrl.newBuilder(str);
            if (this.urlBuilder != null) {
                this.relativeUrl = null;
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Malformed URL. Base: ");
                stringBuilder.append(this.baseUrl);
                stringBuilder.append(", Relative: ");
                stringBuilder.append(this.relativeUrl);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
        if (encoded) {
            this.urlBuilder.addEncodedQueryParameter(name, value);
        } else {
            this.urlBuilder.addQueryParameter(name, value);
        }
    }

    void addFormField(String name, String value, boolean encoded) {
        if (encoded) {
            this.formBuilder.addEncoded(name, value);
        } else {
            this.formBuilder.add(name, value);
        }
    }

    void addPart(Headers headers, RequestBody body) {
        this.multipartBuilder.addPart(headers, body);
    }

    void addPart(Part part) {
        this.multipartBuilder.addPart(part);
    }

    void setBody(RequestBody body) {
        this.body = body;
    }

    Request build() {
        HttpUrl url;
        HttpUrl.Builder urlBuilder = this.urlBuilder;
        if (urlBuilder != null) {
            url = urlBuilder.build();
        } else {
            url = this.baseUrl.resolve(this.relativeUrl);
            if (url == null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Malformed URL. Base: ");
                stringBuilder.append(this.baseUrl);
                stringBuilder.append(", Relative: ");
                stringBuilder.append(this.relativeUrl);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
        RequestBody body = this.body;
        if (body == null) {
            FormBody$Builder formBody$Builder = this.formBuilder;
            if (formBody$Builder != null) {
                body = formBody$Builder.build();
            } else {
                Builder builder = this.multipartBuilder;
                if (builder != null) {
                    body = builder.build();
                } else if (this.hasBody) {
                    body = RequestBody.create(null, new byte[0]);
                }
            }
        }
        MediaType contentType = this.contentType;
        if (contentType != null) {
            if (body != null) {
                body = new RequestBuilder$ContentTypeOverridingRequestBody(body, contentType);
            } else {
                this.requestBuilder.addHeader("Content-Type", contentType.toString());
            }
        }
        return this.requestBuilder.url(url).method(this.method, body).build();
    }
}

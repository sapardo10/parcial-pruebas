package okhttp3;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class FormBody$Builder {
    private final Charset charset;
    private final List<String> names;
    private final List<String> values;

    public FormBody$Builder() {
        this(null);
    }

    public FormBody$Builder(Charset charset) {
        this.names = new ArrayList();
        this.values = new ArrayList();
        this.charset = charset;
    }

    public FormBody$Builder add(String name, String value) {
        if (name == null) {
            throw new NullPointerException("name == null");
        } else if (value != null) {
            this.names.add(HttpUrl.canonicalize(name, " \"':;<=>@[]^`{}|/\\?#&!$(),~", false, false, true, true, this.charset));
            this.values.add(HttpUrl.canonicalize(value, " \"':;<=>@[]^`{}|/\\?#&!$(),~", false, false, true, true, this.charset));
            return this;
        } else {
            throw new NullPointerException("value == null");
        }
    }

    public FormBody$Builder addEncoded(String name, String value) {
        if (name == null) {
            throw new NullPointerException("name == null");
        } else if (value != null) {
            this.names.add(HttpUrl.canonicalize(name, " \"':;<=>@[]^`{}|/\\?#&!$(),~", true, false, true, true, this.charset));
            this.values.add(HttpUrl.canonicalize(value, " \"':;<=>@[]^`{}|/\\?#&!$(),~", true, false, true, true, this.charset));
            return this;
        } else {
            throw new NullPointerException("value == null");
        }
    }

    public FormBody build() {
        return new FormBody(this.names, this.values);
    }
}

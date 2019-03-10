package org.jsoup.nodes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;
import kotlin.text.Typography;
import org.apache.commons.lang3.concurrent.AbstractCircuitBreaker;
import org.jsoup.SerializationException;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Document.OutputSettings.Syntax;

public class Attribute implements Entry<String, String>, Cloneable {
    private static final String[] booleanAttributes = new String[]{"allowfullscreen", "async", "autofocus", "checked", "compact", "declare", "default", "defer", "disabled", "formnovalidate", "hidden", "inert", "ismap", "itemscope", "multiple", "muted", "nohref", "noresize", "noshade", "novalidate", "nowrap", AbstractCircuitBreaker.PROPERTY_NAME, "readonly", "required", "reversed", "seamless", "selected", "sortable", "truespeed", "typemustmatch"};
    private String key;
    Attributes parent;
    private String val;

    public Attribute(String key, String value) {
        this(key, value, null);
    }

    public Attribute(String key, String val, Attributes parent) {
        Validate.notNull(key);
        this.key = key.trim();
        Validate.notEmpty(key);
        this.val = val;
        this.parent = parent;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        Validate.notNull(key);
        key = key.trim();
        Validate.notEmpty(key);
        int i = this.parent;
        if (i != 0) {
            i = i.indexOfKey(this.key);
            if (i != -1) {
                this.parent.keys[i] = key;
            }
        }
        this.key = key;
    }

    public String getValue() {
        return this.val;
    }

    public String setValue(String val) {
        String oldVal = this.parent.get(this.key);
        int i = this.parent;
        if (i != 0) {
            i = i.indexOfKey(this.key);
            if (i != -1) {
                this.parent.vals[i] = val;
            }
        }
        this.val = val;
        return oldVal;
    }

    public String html() {
        StringBuilder accum = new StringBuilder();
        try {
            html(accum, new Document("").outputSettings());
            return accum.toString();
        } catch (Throwable exception) {
            throw new SerializationException(exception);
        }
    }

    protected static void html(String key, String val, Appendable accum, OutputSettings out) throws IOException {
        accum.append(key);
        if (!shouldCollapseAttribute(key, val, out)) {
            accum.append("=\"");
            Entities.escape(accum, Attributes.checkNotNull(val), out, true, false, false);
            accum.append(Typography.quote);
        }
    }

    protected void html(Appendable accum, OutputSettings out) throws IOException {
        html(this.key, this.val, accum, out);
    }

    public String toString() {
        return html();
    }

    public static Attribute createFromEncoded(String unencodedKey, String encodedValue) {
        return new Attribute(unencodedKey, Entities.unescape(encodedValue, true), null);
    }

    protected boolean isDataAttribute() {
        return isDataAttribute(this.key);
    }

    protected static boolean isDataAttribute(String key) {
        return key.startsWith("data-") && key.length() > "data-".length();
    }

    protected final boolean shouldCollapseAttribute(OutputSettings out) {
        return shouldCollapseAttribute(this.key, this.val, out);
    }

    protected static boolean shouldCollapseAttribute(String key, String val, OutputSettings out) {
        if (!(val == null || "".equals(val))) {
            if (!val.equalsIgnoreCase(key)) {
                return false;
            }
        }
        if (out.syntax() == Syntax.html) {
            if (isBooleanAttribute(key)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isBooleanAttribute() {
        if (Arrays.binarySearch(booleanAttributes, this.key) < 0) {
            if (this.val != null) {
                return false;
            }
        }
        return true;
    }

    protected static boolean isBooleanAttribute(String key) {
        return Arrays.binarySearch(booleanAttributes, key) >= 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r6) {
        /*
        r5 = this;
        r0 = 1;
        if (r5 != r6) goto L_0x0004;
    L_0x0003:
        return r0;
    L_0x0004:
        r1 = 0;
        if (r6 == 0) goto L_0x0039;
    L_0x0007:
        r2 = r5.getClass();
        r3 = r6.getClass();
        if (r2 == r3) goto L_0x0012;
    L_0x0011:
        goto L_0x0039;
    L_0x0012:
        r2 = r6;
        r2 = (org.jsoup.nodes.Attribute) r2;
        r3 = r5.key;
        if (r3 == 0) goto L_0x0022;
    L_0x0019:
        r4 = r2.key;
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x0027;
    L_0x0021:
        goto L_0x0026;
    L_0x0022:
        r3 = r2.key;
        if (r3 == 0) goto L_0x0027;
    L_0x0026:
        return r1;
    L_0x0027:
        r3 = r5.val;
        if (r3 == 0) goto L_0x0032;
    L_0x002b:
        r0 = r2.val;
        r0 = r3.equals(r0);
        goto L_0x0038;
    L_0x0032:
        r3 = r2.val;
        if (r3 != 0) goto L_0x0037;
    L_0x0036:
        goto L_0x0038;
    L_0x0037:
        r0 = 0;
    L_0x0038:
        return r0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jsoup.nodes.Attribute.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        String str = this.key;
        int i = 0;
        int result = (str != null ? str.hashCode() : 0) * 31;
        String str2 = this.val;
        if (str2 != null) {
            i = str2.hashCode();
        }
        return result + i;
    }

    public Attribute clone() {
        try {
            return (Attribute) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

package org.hamcrest;

import java.util.Arrays;
import java.util.Iterator;
import kotlin.text.Typography;
import org.apache.commons.lang3.CharUtils;
import org.hamcrest.internal.ArrayIterator;
import org.hamcrest.internal.SelfDescribingValueIterator;

public abstract class BaseDescription implements Description {
    protected abstract void append(char c);

    public Description appendText(String text) {
        append(text);
        return this;
    }

    public Description appendDescriptionOf(SelfDescribing value) {
        value.describeTo(this);
        return this;
    }

    public Description appendValue(Object value) {
        if (value == null) {
            append("null");
        } else if (value instanceof String) {
            toJavaSyntax((String) value);
        } else if (value instanceof Character) {
            append((char) Typography.quote);
            toJavaSyntax(((Character) value).charValue());
            append((char) Typography.quote);
        } else if (value instanceof Short) {
            append((char) Typography.less);
            append(descriptionOf(value));
            append("s>");
        } else if (value instanceof Long) {
            append((char) Typography.less);
            append(descriptionOf(value));
            append("L>");
        } else if (value instanceof Float) {
            append((char) Typography.less);
            append(descriptionOf(value));
            append("F>");
        } else if (value.getClass().isArray()) {
            appendValueList("[", ", ", "]", new ArrayIterator(value));
        } else {
            append((char) Typography.less);
            append(descriptionOf(value));
            append((char) Typography.greater);
        }
        return this;
    }

    private String descriptionOf(Object value) {
        try {
            return String.valueOf(value);
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(value.getClass().getName());
            stringBuilder.append("@");
            stringBuilder.append(Integer.toHexString(value.hashCode()));
            return stringBuilder.toString();
        }
    }

    public <T> Description appendValueList(String start, String separator, String end, T... values) {
        return appendValueList(start, separator, end, Arrays.asList(values));
    }

    public <T> Description appendValueList(String start, String separator, String end, Iterable<T> values) {
        return appendValueList(start, separator, end, values.iterator());
    }

    private <T> Description appendValueList(String start, String separator, String end, Iterator<T> values) {
        return appendList(start, separator, end, new SelfDescribingValueIterator(values));
    }

    public Description appendList(String start, String separator, String end, Iterable<? extends SelfDescribing> values) {
        return appendList(start, separator, end, values.iterator());
    }

    private Description appendList(String start, String separator, String end, Iterator<? extends SelfDescribing> i) {
        boolean separate = false;
        append(start);
        while (i.hasNext()) {
            if (separate) {
                append(separator);
            }
            appendDescriptionOf((SelfDescribing) i.next());
            separate = true;
        }
        append(end);
        return this;
    }

    protected void append(String str) {
        for (int i = 0; i < str.length(); i++) {
            append(str.charAt(i));
        }
    }

    private void toJavaSyntax(String unformatted) {
        append((char) Typography.quote);
        for (int i = 0; i < unformatted.length(); i++) {
            toJavaSyntax(unformatted.charAt(i));
        }
        append((char) Typography.quote);
    }

    private void toJavaSyntax(char ch) {
        if (ch == CharUtils.CR) {
            append("\\r");
        } else if (ch != Typography.quote) {
            switch (ch) {
                case '\t':
                    append("\\t");
                    return;
                case '\n':
                    append("\\n");
                    return;
                default:
                    append(ch);
                    return;
            }
        } else {
            append("\\\"");
        }
    }
}

package org.apache.commons.lang3.time;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.Validate;

abstract class FormatCache<F extends Format> {
    static final int NONE = -1;
    private static final ConcurrentMap<MultipartKey, String> cDateTimeInstanceCache = new ConcurrentHashMap(7);
    private final ConcurrentMap<MultipartKey, F> cInstanceCache = new ConcurrentHashMap(7);

    private static class MultipartKey {
        private int hashCode;
        private final Object[] keys;

        MultipartKey(Object... keys) {
            this.keys = keys;
        }

        public boolean equals(Object obj) {
            return Arrays.equals(this.keys, ((MultipartKey) obj).keys);
        }

        public int hashCode() {
            if (this.hashCode == 0) {
                int rc = 0;
                for (Object key : this.keys) {
                    if (key != null) {
                        rc = (rc * 7) + key.hashCode();
                    }
                }
                this.hashCode = rc;
            }
            return this.hashCode;
        }
    }

    protected abstract F createInstance(String str, TimeZone timeZone, Locale locale);

    FormatCache() {
    }

    public F getInstance() {
        return getDateTimeInstance(3, 3, TimeZone.getDefault(), Locale.getDefault());
    }

    public F getInstance(String pattern, TimeZone timeZone, Locale locale) {
        Validate.notNull(pattern, "pattern must not be null", new Object[0]);
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        MultipartKey key = new MultipartKey(pattern, timeZone, locale);
        Format format = (Format) this.cInstanceCache.get(key);
        if (format != null) {
            return format;
        }
        F format2 = createInstance(pattern, timeZone, locale);
        F previousValue = (Format) this.cInstanceCache.putIfAbsent(key, format2);
        if (previousValue != null) {
            return previousValue;
        }
        return format2;
    }

    private F getDateTimeInstance(Integer dateStyle, Integer timeStyle, TimeZone timeZone, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return getInstance(getPatternForStyle(dateStyle, timeStyle, locale), timeZone, locale);
    }

    F getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone, Locale locale) {
        return getDateTimeInstance(Integer.valueOf(dateStyle), Integer.valueOf(timeStyle), timeZone, locale);
    }

    F getDateInstance(int dateStyle, TimeZone timeZone, Locale locale) {
        return getDateTimeInstance(Integer.valueOf(dateStyle), null, timeZone, locale);
    }

    F getTimeInstance(int timeStyle, TimeZone timeZone, Locale locale) {
        return getDateTimeInstance(null, Integer.valueOf(timeStyle), timeZone, locale);
    }

    static String getPatternForStyle(Integer dateStyle, Integer timeStyle, Locale locale) {
        MultipartKey key = new MultipartKey(dateStyle, timeStyle, locale);
        String pattern = (String) cDateTimeInstanceCache.get(key);
        if (pattern == null) {
            ClassCastException ex;
            if (dateStyle == null) {
                try {
                    ex = DateFormat.getTimeInstance(timeStyle.intValue(), locale);
                } catch (ClassCastException e) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("No date time pattern for locale: ");
                    stringBuilder.append(locale);
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
            } else if (timeStyle == null) {
                ex = DateFormat.getDateInstance(dateStyle.intValue(), locale);
            } else {
                ex = DateFormat.getDateTimeInstance(dateStyle.intValue(), timeStyle.intValue(), locale);
            }
            pattern = ((SimpleDateFormat) ex).toPattern();
            String previous = (String) cDateTimeInstanceCache.putIfAbsent(key, pattern);
            if (previous != null) {
                pattern = previous;
            }
        }
        return pattern;
    }
}

package org.apache.commons.text.lookup;

import android.support.v4.app.NotificationCompat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class InterpolatorStringLookup extends AbstractStringLookup {
    private static final char PREFIX_SEPARATOR = ':';
    private final StringLookup defaultStringLookup;
    private final Map<String, StringLookup> stringLookupMap;

    InterpolatorStringLookup() {
        this((Map) null);
    }

    <V> InterpolatorStringLookup(Map<String, V> defaultMap) {
        this(MapStringLookup.on(defaultMap == null ? new HashMap() : defaultMap));
        this.stringLookupMap.put(NotificationCompat.CATEGORY_SYSTEM, SystemPropertyStringLookup.INSTANCE);
        this.stringLookupMap.put("env", EnvironmentVariableStringLookup.INSTANCE);
        this.stringLookupMap.put("java", JavaPlatformStringLookup.INSTANCE);
        this.stringLookupMap.put("date", DateStringLookup.INSTANCE);
        this.stringLookupMap.put("localhost", LocalHostStringLookup.INSTANCE);
    }

    InterpolatorStringLookup(StringLookup defaultStringLookup) {
        this.stringLookupMap = new HashMap();
        this.defaultStringLookup = defaultStringLookup;
    }

    public Map<String, StringLookup> getStringLookupMap() {
        return this.stringLookupMap;
    }

    public String lookup(String var) {
        if (var == null) {
            return null;
        }
        int prefixPos = var.indexOf(58);
        if (prefixPos >= 0) {
            String prefix = var.substring(0, prefixPos).toLowerCase(Locale.US);
            String name = var.substring(prefixPos + 1);
            StringLookup lookup = (StringLookup) this.stringLookupMap.get(prefix);
            String value = null;
            if (lookup != null) {
                value = lookup.lookup(name);
            }
            if (value != null) {
                return value;
            }
            var = var.substring(prefixPos + 1);
        }
        StringLookup stringLookup = this.defaultStringLookup;
        if (stringLookup != null) {
            return stringLookup.lookup(var);
        }
        return null;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getClass().getName());
        stringBuilder.append(" [stringLookupMap=");
        stringBuilder.append(this.stringLookupMap);
        stringBuilder.append(", defaultStringLookup=");
        stringBuilder.append(this.defaultStringLookup);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}

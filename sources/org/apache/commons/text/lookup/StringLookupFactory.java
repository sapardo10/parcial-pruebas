package org.apache.commons.text.lookup;

import java.util.Map;

public final class StringLookupFactory {
    public static final StringLookupFactory INSTANCE = new StringLookupFactory();

    private StringLookupFactory() {
    }

    public StringLookup dateStringLookup() {
        return DateStringLookup.INSTANCE;
    }

    public StringLookup environmentVariableStringLookup() {
        return EnvironmentVariableStringLookup.INSTANCE;
    }

    public StringLookup interpolatorStringLookup() {
        return new InterpolatorStringLookup();
    }

    public <V> StringLookup interpolatorStringLookup(Map<String, V> map) {
        return new InterpolatorStringLookup((Map) map);
    }

    public StringLookup interpolatorStringLookup(StringLookup defaultStringLookup) {
        return new InterpolatorStringLookup(defaultStringLookup);
    }

    public StringLookup javaPlatformStringLookup() {
        return JavaPlatformStringLookup.INSTANCE;
    }

    public StringLookup localHostStringLookup() {
        return LocalHostStringLookup.INSTANCE;
    }

    public <V> StringLookup mapStringLookup(Map<String, V> map) {
        return MapStringLookup.on(map);
    }

    public StringLookup nullStringLookup() {
        return NullStringLookup.INSTANCE;
    }

    public StringLookup resourceBundleStringLookup() {
        return ResourceBundleStringLookup.INSTANCE;
    }

    public StringLookup systemPropertyStringLookup() {
        return SystemPropertyStringLookup.INSTANCE;
    }
}

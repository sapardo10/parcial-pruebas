package org.apache.commons.text.lookup;

final class EnvironmentVariableStringLookup extends AbstractStringLookup {
    static final EnvironmentVariableStringLookup INSTANCE = new EnvironmentVariableStringLookup();

    private EnvironmentVariableStringLookup() {
    }

    public String lookup(String key) {
        return key != null ? System.getenv(key) : null;
    }
}

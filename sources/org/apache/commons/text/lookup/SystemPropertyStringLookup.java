package org.apache.commons.text.lookup;

final class SystemPropertyStringLookup extends AbstractStringLookup {
    static final SystemPropertyStringLookup INSTANCE = new SystemPropertyStringLookup();

    private SystemPropertyStringLookup() {
    }

    public String lookup(String key) {
        try {
            return System.getProperty(key);
        } catch (SecurityException e) {
            return null;
        }
    }
}

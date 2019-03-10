package org.apache.commons.text.lookup;

import java.util.ResourceBundle;

final class ResourceBundleStringLookup extends AbstractStringLookup {
    static final ResourceBundleStringLookup INSTANCE = new ResourceBundleStringLookup();

    private ResourceBundleStringLookup() {
    }

    public String lookup(String key) {
        if (key == null) {
            return null;
        }
        String[] keys = key.split(":");
        int i = 2;
        if (keys.length == 2) {
            String bundleName = keys[0];
            String bundleKey = keys[1];
            try {
                i = ResourceBundle.getBundle(bundleName).getString(bundleKey);
                return i;
            } catch (Exception e) {
                Object[] objArr = new Object[i];
                objArr[0] = bundleName;
                objArr[1] = bundleKey;
                throw IllegalArgumentExceptions.format(e, "Error looking up ResourceBundle [%s] and key [%s].", objArr);
            }
        }
        throw IllegalArgumentExceptions.format("Bad ResourceBundle key format [%s]. Expected format is BundleName:KeyName.", key);
    }
}

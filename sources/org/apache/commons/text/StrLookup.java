package org.apache.commons.text;

import java.util.Map;
import java.util.ResourceBundle;
import org.apache.commons.text.lookup.StringLookup;

@Deprecated
public abstract class StrLookup<V> implements StringLookup {
    private static final StrLookup<String> NONE_LOOKUP = new MapStrLookup(null);
    private static final StrLookup<String> SYSTEM_PROPERTIES_LOOKUP = new SystemPropertiesStrLookup();

    static class MapStrLookup<V> extends StrLookup<V> {
        private final Map<String, V> map;

        MapStrLookup(Map<String, V> map) {
            this.map = map;
        }

        public String lookup(String key) {
            Object obj = this.map;
            if (obj == null) {
                return null;
            }
            obj = obj.get(key);
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(super.toString());
            stringBuilder.append(" [map=");
            stringBuilder.append(this.map);
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
    }

    private static final class ResourceBundleLookup extends StrLookup<String> {
        private final ResourceBundle resourceBundle;

        private ResourceBundleLookup(ResourceBundle resourceBundle) {
            this.resourceBundle = resourceBundle;
        }

        public String lookup(String key) {
            ResourceBundle resourceBundle = this.resourceBundle;
            if (!(resourceBundle == null || key == null)) {
                if (resourceBundle.containsKey(key)) {
                    return this.resourceBundle.getString(key);
                }
            }
            return null;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(super.toString());
            stringBuilder.append(" [resourceBundle=");
            stringBuilder.append(this.resourceBundle);
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
    }

    private static final class SystemPropertiesStrLookup extends StrLookup<String> {
        private SystemPropertiesStrLookup() {
        }

        public String lookup(String key) {
            if (key.length() <= 0) {
                return null;
            }
            try {
                return System.getProperty(key);
            } catch (SecurityException e) {
                return null;
            }
        }
    }

    public static StrLookup<?> noneLookup() {
        return NONE_LOOKUP;
    }

    public static StrLookup<String> systemPropertiesLookup() {
        return SYSTEM_PROPERTIES_LOOKUP;
    }

    public static <V> StrLookup<V> mapLookup(Map<String, V> map) {
        return new MapStrLookup(map);
    }

    public static StrLookup<String> resourceBundleLookup(ResourceBundle resourceBundle) {
        return new ResourceBundleLookup(resourceBundle);
    }

    protected StrLookup() {
    }
}

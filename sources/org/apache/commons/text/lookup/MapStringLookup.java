package org.apache.commons.text.lookup;

import java.util.Map;

final class MapStringLookup<V> implements StringLookup {
    private final Map<String, V> map;

    static <T> MapStringLookup<T> on(Map<String, T> map) {
        return new MapStringLookup(map);
    }

    private MapStringLookup(Map<String, V> map) {
        this.map = map;
    }

    Map<String, V> getMap() {
        return this.map;
    }

    public String lookup(String key) {
        V obj = this.map;
        String str = null;
        if (obj == null) {
            return null;
        }
        try {
            obj = obj.get(key);
            if (obj != null) {
                str = obj.toString();
            }
            return str;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getClass().getName());
        stringBuilder.append(" [map=");
        stringBuilder.append(this.map);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}

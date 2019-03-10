package org.apache.commons.text.similarity;

import java.util.HashMap;
import java.util.Map;

final class Counter {
    private Counter() {
    }

    public static Map<CharSequence, Integer> of(CharSequence[] tokens) {
        Map<CharSequence, Integer> innerCounter = new HashMap();
        for (CharSequence token : tokens) {
            if (innerCounter.containsKey(token)) {
                innerCounter.put(token, Integer.valueOf(((Integer) innerCounter.get(token)).intValue() + 1));
            } else {
                innerCounter.put(token, Integer.valueOf(1));
            }
        }
        return innerCounter;
    }
}

package de.greenrobot.event.util;

import android.util.Log;
import de.greenrobot.event.EventBus;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ExceptionToResourceMapping {
    public final Map<Class<? extends Throwable>, Integer> throwableToMsgIdMap = new HashMap();

    public Integer mapThrowable(Throwable throwable) {
        Throwable throwableToCheck = throwable;
        int depthToGo = 20;
        while (true) {
            Integer resId = mapThrowableFlat(throwableToCheck);
            if (resId == null) {
                throwableToCheck = throwableToCheck.getCause();
                depthToGo--;
                if (depthToGo <= 0 || throwableToCheck == throwable) {
                    break;
                } else if (throwableToCheck == null) {
                    break;
                }
            } else {
                return resId;
            }
        }
        String str = EventBus.TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("No specific message ressource ID found for ");
        stringBuilder.append(throwable);
        Log.d(str, stringBuilder.toString());
        return null;
    }

    protected Integer mapThrowableFlat(Throwable throwable) {
        Class<? extends Throwable> throwableClass = throwable.getClass();
        Integer resId = (Integer) this.throwableToMsgIdMap.get(throwableClass);
        if (resId == null) {
            Class<? extends Throwable> closestClass = null;
            for (Entry<Class<? extends Throwable>, Integer> mapping : this.throwableToMsgIdMap.entrySet()) {
                Class<? extends Throwable> candidate = (Class) mapping.getKey();
                if (candidate.isAssignableFrom(throwableClass)) {
                    if (closestClass != null) {
                        if (closestClass.isAssignableFrom(candidate)) {
                        }
                    }
                    closestClass = candidate;
                    resId = (Integer) mapping.getValue();
                }
            }
        }
        return resId;
    }

    public ExceptionToResourceMapping addMapping(Class<? extends Throwable> clazz, int msgId) {
        this.throwableToMsgIdMap.put(clazz, Integer.valueOf(msgId));
        return this;
    }
}

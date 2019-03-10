package de.greenrobot.event.util;

import android.content.res.Resources;
import android.util.Log;
import de.greenrobot.event.EventBus;

public class ErrorDialogConfig {
    int defaultDialogIconId;
    final int defaultErrorMsgId;
    Class<?> defaultEventTypeOnDialogClosed;
    final int defaultTitleId;
    EventBus eventBus;
    boolean logExceptions = true;
    final ExceptionToResourceMapping mapping;
    final Resources resources;
    String tagForLoggingExceptions;

    public ErrorDialogConfig(Resources resources, int defaultTitleId, int defaultMsgId) {
        this.resources = resources;
        this.defaultTitleId = defaultTitleId;
        this.defaultErrorMsgId = defaultMsgId;
        this.mapping = new ExceptionToResourceMapping();
    }

    public ErrorDialogConfig addMapping(Class<? extends Throwable> clazz, int msgId) {
        this.mapping.addMapping(clazz, msgId);
        return this;
    }

    public int getMessageIdForThrowable(Throwable throwable) {
        Integer resId = this.mapping.mapThrowable(throwable);
        if (resId != null) {
            return resId.intValue();
        }
        String str = EventBus.TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("No specific message ressource ID found for ");
        stringBuilder.append(throwable);
        Log.d(str, stringBuilder.toString());
        return this.defaultErrorMsgId;
    }

    public void setDefaultDialogIconId(int defaultDialogIconId) {
        this.defaultDialogIconId = defaultDialogIconId;
    }

    public void setDefaultEventTypeOnDialogClosed(Class<?> defaultEventTypeOnDialogClosed) {
        this.defaultEventTypeOnDialogClosed = defaultEventTypeOnDialogClosed;
    }

    public void disableExceptionLogging() {
        this.logExceptions = false;
    }

    public void setTagForLoggingExceptions(String tagForLoggingExceptions) {
        this.tagForLoggingExceptions = tagForLoggingExceptions;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    EventBus getEventBus() {
        EventBus eventBus = this.eventBus;
        return eventBus != null ? eventBus : EventBus.getDefault();
    }
}

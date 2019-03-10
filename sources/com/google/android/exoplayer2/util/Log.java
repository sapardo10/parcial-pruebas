package com.google.android.exoplayer2.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;

public final class Log {
    public static final int LOG_LEVEL_ALL = 0;
    public static final int LOG_LEVEL_ERROR = 3;
    public static final int LOG_LEVEL_INFO = 1;
    public static final int LOG_LEVEL_OFF = Integer.MAX_VALUE;
    public static final int LOG_LEVEL_WARNING = 2;
    private static int logLevel = 0;
    private static boolean logStackTraces = true;

    private Log() {
    }

    public static int getLogLevel() {
        return logLevel;
    }

    public boolean getLogStackTraces() {
        return logStackTraces;
    }

    public static void setLogLevel(int logLevel) {
        logLevel = logLevel;
    }

    public static void setLogStackTraces(boolean logStackTraces) {
        logStackTraces = logStackTraces;
    }

    /* renamed from: d */
    public static void m4d(String tag, String message) {
        if (logLevel == 0) {
            android.util.Log.d(tag, message);
        }
    }

    /* renamed from: d */
    public static void m5d(String tag, String message, @Nullable Throwable throwable) {
        if (!logStackTraces) {
            m4d(tag, appendThrowableMessage(message, throwable));
        }
        if (logLevel == 0) {
            android.util.Log.d(tag, message, throwable);
        }
    }

    /* renamed from: i */
    public static void m8i(String tag, String message) {
        if (logLevel <= 1) {
            android.util.Log.i(tag, message);
        }
    }

    /* renamed from: i */
    public static void m9i(String tag, String message, @Nullable Throwable throwable) {
        if (!logStackTraces) {
            m8i(tag, appendThrowableMessage(message, throwable));
        }
        if (logLevel <= 1) {
            android.util.Log.i(tag, message, throwable);
        }
    }

    /* renamed from: w */
    public static void m10w(String tag, String message) {
        if (logLevel <= 2) {
            android.util.Log.w(tag, message);
        }
    }

    /* renamed from: w */
    public static void m11w(String tag, String message, @Nullable Throwable throwable) {
        if (!logStackTraces) {
            m10w(tag, appendThrowableMessage(message, throwable));
        }
        if (logLevel <= 2) {
            android.util.Log.w(tag, message, throwable);
        }
    }

    /* renamed from: e */
    public static void m6e(String tag, String message) {
        if (logLevel <= 3) {
            android.util.Log.e(tag, message);
        }
    }

    /* renamed from: e */
    public static void m7e(String tag, String message, @Nullable Throwable throwable) {
        if (!logStackTraces) {
            m6e(tag, appendThrowableMessage(message, throwable));
        }
        if (logLevel <= 3) {
            android.util.Log.e(tag, message, throwable);
        }
    }

    private static String appendThrowableMessage(String message, @Nullable Throwable throwable) {
        if (throwable == null) {
            return message;
        }
        String str;
        String throwableMessage = throwable.getMessage();
        if (TextUtils.isEmpty(throwableMessage)) {
            str = message;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(message);
            stringBuilder.append(" - ");
            stringBuilder.append(throwableMessage);
            str = stringBuilder.toString();
        }
        return str;
    }
}

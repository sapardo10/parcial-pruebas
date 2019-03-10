package com.bumptech.glide.load;

import android.support.annotation.Nullable;
import java.io.IOException;

public final class HttpException extends IOException {
    public static final int UNKNOWN = -1;
    private static final long serialVersionUID = 1;
    private final int statusCode;

    public HttpException(int statusCode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Http request failed with status code: ");
        stringBuilder.append(statusCode);
        this(stringBuilder.toString(), statusCode);
    }

    public HttpException(String message) {
        this(message, -1);
    }

    public HttpException(String message, int statusCode) {
        this(message, statusCode, null);
    }

    public HttpException(String message, int statusCode, @Nullable Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}

package org.jsoup;

import java.io.IOException;

public class HttpStatusException extends IOException {
    private int statusCode;
    private String url;

    public HttpStatusException(String message, int statusCode, String url) {
        super(message);
        this.statusCode = statusCode;
        this.url = url;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getUrl() {
        return this.url;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append(". Status=");
        stringBuilder.append(this.statusCode);
        stringBuilder.append(", URL=");
        stringBuilder.append(this.url);
        return stringBuilder.toString();
    }
}

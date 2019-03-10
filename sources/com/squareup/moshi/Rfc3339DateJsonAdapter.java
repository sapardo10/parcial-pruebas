package com.squareup.moshi;

import java.io.IOException;
import java.util.Date;

public final class Rfc3339DateJsonAdapter extends JsonAdapter<Date> {
    com.squareup.moshi.adapters.Rfc3339DateJsonAdapter delegate = new com.squareup.moshi.adapters.Rfc3339DateJsonAdapter();

    public Date fromJson(JsonReader reader) throws IOException {
        return this.delegate.fromJson(reader);
    }

    public void toJson(JsonWriter writer, Date value) throws IOException {
        this.delegate.toJson(writer, value);
    }
}

package com.squareup.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.util.Date;

public final class Rfc3339DateJsonAdapter extends JsonAdapter<Date> {
    public synchronized Date fromJson(JsonReader reader) throws IOException {
        return Iso8601Utils.parse(reader.nextString());
    }

    public synchronized void toJson(JsonWriter writer, Date value) throws IOException {
        writer.value(Iso8601Utils.format(value));
    }
}

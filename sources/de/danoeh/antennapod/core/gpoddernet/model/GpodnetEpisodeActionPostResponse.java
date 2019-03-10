package de.danoeh.antennapod.core.gpoddernet.model;

import android.support.v4.util.ArrayMap;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GpodnetEpisodeActionPostResponse {
    public final long timestamp;
    private final Map<String, String> updatedUrls;

    private GpodnetEpisodeActionPostResponse(long timestamp, Map<String, String> updatedUrls) {
        this.timestamp = timestamp;
        this.updatedUrls = updatedUrls;
    }

    public static GpodnetEpisodeActionPostResponse fromJSONObject(String objectString) throws JSONException {
        JSONObject object = new JSONObject(objectString);
        long timestamp = object.getLong("timestamp");
        JSONArray urls = object.getJSONArray("update_urls");
        Map<String, String> updatedUrls = new ArrayMap(urls.length());
        for (int i = 0; i < urls.length(); i++) {
            JSONArray urlPair = urls.getJSONArray(i);
            updatedUrls.put(urlPair.getString(0), urlPair.getString(1));
        }
        return new GpodnetEpisodeActionPostResponse(timestamp, updatedUrls);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

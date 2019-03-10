package de.danoeh.antennapod.core.gpoddernet.model;

import android.support.v4.util.ArrayMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GpodnetUploadChangesResponse {
    public final long timestamp;
    private final Map<String, String> updatedUrls;

    private GpodnetUploadChangesResponse(long timestamp, Map<String, String> updatedUrls) {
        this.timestamp = timestamp;
        this.updatedUrls = updatedUrls;
    }

    public static GpodnetUploadChangesResponse fromJSONObject(String objectString) throws JSONException {
        JSONObject object = new JSONObject(objectString);
        long timestamp = object.getLong("timestamp");
        Map<String, String> updatedUrls = new ArrayMap();
        JSONArray urls = object.getJSONArray("update_urls");
        for (int i = 0; i < urls.length(); i++) {
            JSONArray urlPair = urls.getJSONArray(i);
            updatedUrls.put(urlPair.getString(0), urlPair.getString(1));
        }
        return new GpodnetUploadChangesResponse(timestamp, updatedUrls);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("GpodnetUploadChangesResponse{timestamp=");
        stringBuilder.append(this.timestamp);
        stringBuilder.append(", updatedUrls=");
        stringBuilder.append(this.updatedUrls);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}

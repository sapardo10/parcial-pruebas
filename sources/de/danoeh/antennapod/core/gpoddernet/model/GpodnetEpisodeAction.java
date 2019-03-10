package de.danoeh.antennapod.core.gpoddernet.model;

import android.text.TextUtils;
import android.util.Log;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.preferences.GpodnetPreferences;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.core.util.DateUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.json.JSONException;
import org.json.JSONObject;

public class GpodnetEpisodeAction {
    private static final String TAG = "GpodnetEpisodeAction";
    private final Action action;
    private final String deviceId;
    private final String episode;
    private final String podcast;
    private final int position;
    private final int started;
    private final Date timestamp;
    private final int total;

    public enum Action {
        NEW,
        DOWNLOAD,
        PLAY,
        DELETE
    }

    public static class Builder {
        private final Action action;
        private String deviceId;
        private final String episode;
        private final String podcast;
        private int position;
        private int started;
        private Date timestamp;
        private int total;

        public Builder(FeedItem item, Action action) {
            this(item.getFeed().getDownload_url(), item.getMedia().getDownload_url(), action);
        }

        public Builder(String podcast, String episode, Action action) {
            this.deviceId = "";
            this.started = -1;
            this.position = -1;
            this.total = -1;
            this.podcast = podcast;
            this.episode = episode;
            this.action = action;
        }

        public Builder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder currentDeviceId() {
            return deviceId(GpodnetPreferences.getDeviceID());
        }

        public Builder timestamp(Date timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder currentTimestamp() {
            return timestamp(new Date());
        }

        public Builder started(int seconds) {
            if (this.action == Action.PLAY) {
                this.started = seconds;
            }
            return this;
        }

        public Builder position(int seconds) {
            if (this.action == Action.PLAY) {
                this.position = seconds;
            }
            return this;
        }

        public Builder total(int seconds) {
            if (this.action == Action.PLAY) {
                this.total = seconds;
            }
            return this;
        }

        public GpodnetEpisodeAction build() {
            return new GpodnetEpisodeAction();
        }
    }

    private GpodnetEpisodeAction(Builder builder) {
        this.podcast = builder.podcast;
        this.episode = builder.episode;
        this.action = builder.action;
        this.deviceId = builder.deviceId;
        this.timestamp = builder.timestamp;
        this.started = builder.started;
        this.position = builder.position;
        this.total = builder.total;
    }

    public static GpodnetEpisodeAction readFromString(String s) {
        String[] fields = s.split("\t");
        GpodnetEpisodeAction gpodnetEpisodeAction = null;
        if (fields.length != 8) {
            return null;
        }
        String podcast = fields[null];
        String episode = fields[1];
        try {
            gpodnetEpisodeAction = new Builder(podcast, episode, Action.valueOf(fields[3])).deviceId(fields[2]).timestamp(new Date(Long.parseLong(fields[4]))).started(Integer.parseInt(fields[5])).position(Integer.parseInt(fields[6])).total(Integer.parseInt(fields[7])).build();
            return gpodnetEpisodeAction;
        } catch (IllegalArgumentException e) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("readFromString(");
            stringBuilder.append(s);
            stringBuilder.append("): ");
            stringBuilder.append(e.getMessage());
            Log.e(str, stringBuilder.toString());
            return gpodnetEpisodeAction;
        }
    }

    public static GpodnetEpisodeAction readFromJSONObject(JSONObject object) {
        String podcast = object.optString("podcast", null);
        String episode = object.optString("episode", null);
        String actionString = object.optString("action", null);
        if (!(TextUtils.isEmpty(podcast) || TextUtils.isEmpty(episode))) {
            if (!TextUtils.isEmpty(actionString)) {
                try {
                    Action action = Action.valueOf(actionString.toUpperCase());
                    Builder builder = new Builder(podcast, episode, action).deviceId(object.optString("device", ""));
                    String utcTimestamp = object.optString("timestamp", null);
                    if (!TextUtils.isEmpty(utcTimestamp)) {
                        builder.timestamp(DateUtils.parse(utcTimestamp));
                    }
                    if (action == Action.PLAY) {
                        int started = object.optInt("started", -1);
                        int position = object.optInt(PodDBAdapter.KEY_POSITION, -1);
                        int total = object.optInt("total", -1);
                        if (started >= 0 && position > 0 && total > 0) {
                            builder.started(started).position(position).total(total);
                        }
                    }
                    return builder.build();
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public String getPodcast() {
        return this.podcast;
    }

    public String getEpisode() {
        return this.episode;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public Action getAction() {
        return this.action;
    }

    private String getActionString() {
        return this.action.name().toLowerCase();
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public int getStarted() {
        return this.started;
    }

    public int getPosition() {
        return this.position;
    }

    public int getTotal() {
        return this.total;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r6) {
        /*
        r5 = this;
        r0 = 1;
        if (r5 != r6) goto L_0x0004;
    L_0x0003:
        return r0;
    L_0x0004:
        r1 = 0;
        if (r6 == 0) goto L_0x0077;
    L_0x0007:
        r2 = r6 instanceof de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction;
        if (r2 != 0) goto L_0x000d;
    L_0x000b:
        goto L_0x0077;
    L_0x000d:
        r2 = r6;
        r2 = (de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction) r2;
        r3 = r5.started;
        r4 = r2.started;
        if (r3 == r4) goto L_0x0017;
    L_0x0016:
        return r1;
    L_0x0017:
        r3 = r5.position;
        r4 = r2.position;
        if (r3 == r4) goto L_0x001e;
    L_0x001d:
        return r1;
    L_0x001e:
        r3 = r5.total;
        r4 = r2.total;
        if (r3 == r4) goto L_0x0025;
    L_0x0024:
        return r1;
    L_0x0025:
        r3 = r5.podcast;
        if (r3 == 0) goto L_0x0032;
    L_0x0029:
        r4 = r2.podcast;
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x0037;
    L_0x0031:
        goto L_0x0036;
    L_0x0032:
        r3 = r2.podcast;
        if (r3 == 0) goto L_0x0037;
    L_0x0036:
        return r1;
    L_0x0037:
        r3 = r5.episode;
        if (r3 == 0) goto L_0x0044;
    L_0x003b:
        r4 = r2.episode;
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x0049;
    L_0x0043:
        goto L_0x0048;
    L_0x0044:
        r3 = r2.episode;
        if (r3 == 0) goto L_0x0049;
    L_0x0048:
        return r1;
    L_0x0049:
        r3 = r5.deviceId;
        if (r3 == 0) goto L_0x0056;
    L_0x004d:
        r4 = r2.deviceId;
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x005b;
    L_0x0055:
        goto L_0x005a;
    L_0x0056:
        r3 = r2.deviceId;
        if (r3 == 0) goto L_0x005b;
    L_0x005a:
        return r1;
        r3 = r5.action;
        r4 = r2.action;
        if (r3 == r4) goto L_0x0063;
    L_0x0062:
        return r1;
    L_0x0063:
        r3 = r5.timestamp;
        if (r3 == 0) goto L_0x0070;
    L_0x0067:
        r4 = r2.timestamp;
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x0074;
    L_0x006f:
        goto L_0x0075;
    L_0x0070:
        r3 = r2.timestamp;
        if (r3 != 0) goto L_0x0075;
    L_0x0074:
        goto L_0x0076;
    L_0x0075:
        r0 = 0;
    L_0x0076:
        return r0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        String str = this.podcast;
        int i = 0;
        int result = (str != null ? str.hashCode() : 0) * 31;
        String str2 = this.episode;
        int hashCode = (result + (str2 != null ? str2.hashCode() : 0)) * 31;
        str2 = this.deviceId;
        result = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        Action action = this.action;
        hashCode = (result + (action != null ? action.hashCode() : 0)) * 31;
        Date date = this.timestamp;
        if (date != null) {
            i = date.hashCode();
        }
        return ((((((hashCode + i) * 31) + this.started) * 31) + this.position) * 31) + this.total;
    }

    public String writeToString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.podcast);
        stringBuilder.append("\t");
        stringBuilder.append(this.episode);
        stringBuilder.append("\t");
        stringBuilder.append(this.deviceId);
        stringBuilder.append("\t");
        stringBuilder.append(this.action);
        stringBuilder.append("\t");
        stringBuilder.append(this.timestamp.getTime());
        stringBuilder.append("\t");
        stringBuilder.append(String.valueOf(this.started));
        stringBuilder.append("\t");
        stringBuilder.append(String.valueOf(this.position));
        stringBuilder.append("\t");
        stringBuilder.append(String.valueOf(this.total));
        return stringBuilder.toString();
    }

    public JSONObject writeToJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.putOpt("podcast", this.podcast);
            obj.putOpt("episode", this.episode);
            obj.put("device", this.deviceId);
            obj.put("action", getActionString());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            obj.put("timestamp", formatter.format(this.timestamp));
            if (getAction() == Action.PLAY) {
                obj.put("started", this.started);
                obj.put(PodDBAdapter.KEY_POSITION, this.position);
                obj.put("total", this.total);
            }
            return obj;
        } catch (JSONException e) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("writeToJSONObject(): ");
            stringBuilder.append(e.getMessage());
            Log.e(str, stringBuilder.toString());
            return null;
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("GpodnetEpisodeAction{podcast='");
        stringBuilder.append(this.podcast);
        stringBuilder.append('\'');
        stringBuilder.append(", episode='");
        stringBuilder.append(this.episode);
        stringBuilder.append('\'');
        stringBuilder.append(", deviceId='");
        stringBuilder.append(this.deviceId);
        stringBuilder.append('\'');
        stringBuilder.append(", action=");
        stringBuilder.append(this.action);
        stringBuilder.append(", timestamp=");
        stringBuilder.append(this.timestamp);
        stringBuilder.append(", started=");
        stringBuilder.append(this.started);
        stringBuilder.append(", position=");
        stringBuilder.append(this.position);
        stringBuilder.append(", total=");
        stringBuilder.append(this.total);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}

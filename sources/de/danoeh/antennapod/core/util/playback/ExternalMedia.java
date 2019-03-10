package de.danoeh.antennapod.core.util.playback;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import de.danoeh.antennapod.core.feed.Chapter;
import de.danoeh.antennapod.core.feed.MediaType;
import de.danoeh.antennapod.core.util.ChapterUtils;
import de.danoeh.antennapod.core.util.playback.Playable.PlayableException;
import java.util.List;
import java.util.concurrent.Callable;

public class ExternalMedia implements Playable {
    public static final Creator<ExternalMedia> CREATOR = new C07611();
    public static final int PLAYABLE_TYPE_EXTERNAL_MEDIA = 2;
    public static final String PREF_LAST_PLAYED_TIME = "ExternalMedia.PrefLastPlayedTime";
    public static final String PREF_MEDIA_TYPE = "ExternalMedia.PrefMediaType";
    public static final String PREF_POSITION = "ExternalMedia.PrefPosition";
    public static final String PREF_SOURCE_URL = "ExternalMedia.PrefSourceUrl";
    private List<Chapter> chapters;
    private int duration;
    private String episodeTitle;
    private String feedTitle;
    private long lastPlayedTime;
    private MediaType mediaType;
    private int position;
    private final String source;

    /* renamed from: de.danoeh.antennapod.core.util.playback.ExternalMedia$1 */
    static class C07611 implements Creator<ExternalMedia> {
        C07611() {
        }

        public ExternalMedia createFromParcel(Parcel in) {
            int position;
            long lastPlayedTime;
            String source = in.readString();
            MediaType type = MediaType.valueOf(in.readString());
            if (in.dataAvail() > 0) {
                position = in.readInt();
            } else {
                position = 0;
            }
            if (in.dataAvail() > 0) {
                lastPlayedTime = in.readLong();
            } else {
                lastPlayedTime = 0;
            }
            return new ExternalMedia(source, type, position, lastPlayedTime);
        }

        public ExternalMedia[] newArray(int size) {
            return new ExternalMedia[size];
        }
    }

    public ExternalMedia(String source, MediaType mediaType) {
        this.mediaType = MediaType.AUDIO;
        this.source = source;
        this.mediaType = mediaType;
    }

    public ExternalMedia(String source, MediaType mediaType, int position, long lastPlayedTime) {
        this(source, mediaType);
        this.position = position;
        this.lastPlayedTime = lastPlayedTime;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.source);
        dest.writeString(this.mediaType.toString());
        dest.writeInt(this.position);
        dest.writeLong(this.lastPlayedTime);
    }

    public void writeToPreferences(Editor prefEditor) {
        prefEditor.putString(PREF_SOURCE_URL, this.source);
        prefEditor.putString(PREF_MEDIA_TYPE, this.mediaType.toString());
        prefEditor.putInt(PREF_POSITION, this.position);
        prefEditor.putLong(PREF_LAST_PLAYED_TIME, this.lastPlayedTime);
    }

    public void loadMetadata() throws PlayableException {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(this.source);
            this.episodeTitle = mmr.extractMetadata(7);
            this.feedTitle = mmr.extractMetadata(1);
            try {
                this.duration = Integer.parseInt(mmr.extractMetadata(9));
                ChapterUtils.loadChaptersFromFileUrl(this);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new PlayableException("NumberFormatException when reading duration of media file");
            }
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            throw new PlayableException("IllegalArgumentException when setting up MediaMetadataReceiver");
        } catch (RuntimeException e3) {
            e3.printStackTrace();
            throw new PlayableException("RuntimeException when setting up MediaMetadataRetriever");
        }
    }

    public void loadChapterMarks() {
    }

    public String getEpisodeTitle() {
        return this.episodeTitle;
    }

    public Callable<String> loadShownotes() {
        return -$$Lambda$ExternalMedia$s1DQ_qAWMOx-H4tf00xoOwrOhFE.INSTANCE;
    }

    public List<Chapter> getChapters() {
        return this.chapters;
    }

    public String getWebsiteLink() {
        return null;
    }

    public String getPaymentLink() {
        return null;
    }

    public String getFeedTitle() {
        return this.feedTitle;
    }

    public Object getIdentifier() {
        return this.source;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getPosition() {
        return this.position;
    }

    public long getLastPlayedTime() {
        return this.lastPlayedTime;
    }

    public MediaType getMediaType() {
        return this.mediaType;
    }

    public String getLocalMediaUrl() {
        return this.source;
    }

    public String getStreamUrl() {
        return null;
    }

    public boolean localFileAvailable() {
        return true;
    }

    public boolean streamAvailable() {
        return false;
    }

    public void saveCurrentPosition(SharedPreferences pref, int newPosition, long timestamp) {
        Editor editor = pref.edit();
        editor.putInt(PREF_POSITION, newPosition);
        editor.putLong(PREF_LAST_PLAYED_TIME, timestamp);
        this.position = newPosition;
        this.lastPlayedTime = timestamp;
        editor.commit();
    }

    public void setPosition(int newPosition) {
        this.position = newPosition;
    }

    public void setDuration(int newDuration) {
        this.duration = newDuration;
    }

    public void setLastPlayedTime(long lastPlayedTime) {
        this.lastPlayedTime = lastPlayedTime;
    }

    public void onPlaybackStart() {
    }

    public void onPlaybackPause(Context context) {
    }

    public void onPlaybackCompleted(Context context) {
    }

    public int getPlayableType() {
        return 2;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public String getImageLocation() {
        if (localFileAvailable()) {
            return getLocalMediaUrl();
        }
        return null;
    }
}

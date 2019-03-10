package de.danoeh.antennapod.core.feed;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaDescriptionCompat.Builder;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction.Action;
import de.danoeh.antennapod.core.preferences.GpodnetPreferences;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.core.util.ChapterUtils;
import de.danoeh.antennapod.core.util.flattr.FlattrUtils;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.util.playback.Playable.PlayableException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class FeedMedia extends FeedFile implements Playable {
    private static final int CHECKED_ON_SIZE_BUT_UNKNOWN = Integer.MIN_VALUE;
    public static final Creator<FeedMedia> CREATOR = new C07371();
    public static final int FEEDFILETYPE_FEEDMEDIA = 2;
    public static final int PLAYABLE_TYPE_FEEDMEDIA = 1;
    private static final String PREF_FEED_ID = "FeedMedia.PrefFeedId";
    public static final String PREF_MEDIA_ID = "FeedMedia.PrefMediaId";
    private static final String TAG = "FeedMedia";
    private int duration;
    private Boolean hasEmbeddedPicture;
    @Nullable
    private volatile FeedItem item;
    private long itemID;
    private long lastPlayedTime;
    private String mime_type;
    private Date playbackCompletionDate;
    private int playedDurationWhenStarted;
    private int played_duration;
    private int position;
    private long size;
    private int startPosition;

    /* renamed from: de.danoeh.antennapod.core.feed.FeedMedia$1 */
    static class C07371 implements Creator<FeedMedia> {
        C07371() {
        }

        public FeedMedia createFromParcel(Parcel in) {
            long itemID = in.readLong();
            FeedMedia result = new FeedMedia(in.readLong(), null, in.readInt(), in.readInt(), in.readLong(), in.readString(), in.readString(), in.readString(), in.readByte() != (byte) 0, new Date(in.readLong()), in.readInt(), in.readLong());
            result.itemID = itemID;
            return result;
        }

        public FeedMedia[] newArray(int size) {
            return new FeedMedia[size];
        }
    }

    public FeedMedia(FeedItem i, String download_url, long size, String mime_type) {
        super(null, download_url, false);
        this.startPosition = -1;
        this.item = i;
        this.size = size;
        this.mime_type = mime_type;
    }

    public FeedMedia(long id, FeedItem item, int duration, int position, long size, String mime_type, String file_url, String download_url, boolean downloaded, Date playbackCompletionDate, int played_duration, long lastPlayedTime) {
        Date date;
        int i = played_duration;
        super(file_url, download_url, downloaded);
        this.startPosition = -1;
        this.id = id;
        this.item = item;
        this.duration = duration;
        this.position = position;
        this.played_duration = i;
        this.playedDurationWhenStarted = i;
        this.size = size;
        this.mime_type = mime_type;
        if (playbackCompletionDate == null) {
            date = null;
        } else {
            date = (Date) playbackCompletionDate.clone();
        }
        r0.playbackCompletionDate = date;
        r0.lastPlayedTime = lastPlayedTime;
    }

    private FeedMedia(long id, FeedItem item, int duration, int position, long size, String mime_type, String file_url, String download_url, boolean downloaded, Date playbackCompletionDate, int played_duration, Boolean hasEmbeddedPicture, long lastPlayedTime) {
        this(id, item, duration, position, size, mime_type, file_url, download_url, downloaded, playbackCompletionDate, played_duration, lastPlayedTime);
        this.hasEmbeddedPicture = hasEmbeddedPicture;
    }

    public static FeedMedia fromCursor(Cursor cursor) {
        Date playbackCompletionDate;
        Boolean hasEmbeddedPicture;
        Cursor cursor2 = cursor;
        int indexId = cursor2.getColumnIndex("id");
        int indexPlaybackCompletionDate = cursor2.getColumnIndex(PodDBAdapter.KEY_PLAYBACK_COMPLETION_DATE);
        int indexDuration = cursor2.getColumnIndex("duration");
        int indexPosition = cursor2.getColumnIndex(PodDBAdapter.KEY_POSITION);
        int indexSize = cursor2.getColumnIndex(PodDBAdapter.KEY_SIZE);
        int indexMimeType = cursor2.getColumnIndex(PodDBAdapter.KEY_MIME_TYPE);
        int indexFileUrl = cursor2.getColumnIndex(PodDBAdapter.KEY_FILE_URL);
        int indexDownloadUrl = cursor2.getColumnIndex(PodDBAdapter.KEY_DOWNLOAD_URL);
        int indexDownloaded = cursor2.getColumnIndex(PodDBAdapter.KEY_DOWNLOADED);
        int indexPlayedDuration = cursor2.getColumnIndex(PodDBAdapter.KEY_PLAYED_DURATION);
        int indexLastPlayedTime = cursor2.getColumnIndex(PodDBAdapter.KEY_LAST_PLAYED_TIME);
        long mediaId = cursor2.getLong(indexId);
        long playbackCompletionTime = cursor2.getLong(indexPlaybackCompletionDate);
        if (playbackCompletionTime > 0) {
            playbackCompletionDate = new Date(playbackCompletionTime);
        } else {
            playbackCompletionDate = null;
        }
        switch (cursor2.getInt(cursor2.getColumnIndex(PodDBAdapter.KEY_HAS_EMBEDDED_PICTURE))) {
            case 0:
                hasEmbeddedPicture = Boolean.FALSE;
                break;
            case 1:
                hasEmbeddedPicture = Boolean.TRUE;
                break;
            default:
                hasEmbeddedPicture = null;
                break;
        }
        return new FeedMedia(mediaId, null, cursor2.getInt(indexDuration), cursor2.getInt(indexPosition), cursor2.getLong(indexSize), cursor2.getString(indexMimeType), cursor2.getString(indexFileUrl), cursor2.getString(indexDownloadUrl), cursor2.getInt(indexDownloaded) > 0, playbackCompletionDate, cursor2.getInt(indexPlayedDuration), hasEmbeddedPicture, cursor2.getLong(indexLastPlayedTime));
    }

    public String getHumanReadableIdentifier() {
        if (this.item == null || this.item.getTitle() == null) {
            return this.download_url;
        }
        return this.item.getTitle();
    }

    public MediaItem getMediaItem() {
        return new MediaItem(new Builder().setMediaId(String.valueOf(this.id)).setTitle(getEpisodeTitle()).setDescription(getFeedTitle()).setSubtitle(getFeedTitle()).build(), 2);
    }

    public MediaType getMediaType() {
        return MediaType.fromMimeType(this.mime_type);
    }

    public void updateFromOther(FeedMedia other) {
        super.updateFromOther(other);
        long j = other.size;
        if (j > 0) {
            this.size = j;
        }
        String str = other.mime_type;
        if (str != null) {
            this.mime_type = str;
        }
    }

    public boolean compareWithOther(FeedMedia other) {
        if (super.compareWithOther(other)) {
            return true;
        }
        String str = other.mime_type;
        if (str != null) {
            String str2 = this.mime_type;
            if (str2 != null) {
                if (str2.equals(str)) {
                }
            }
            return true;
        }
        long j = other.size;
        if (j <= 0 || j == this.size) {
            return false;
        }
        return true;
    }

    public boolean isPlaying() {
        if (PlaybackPreferences.getCurrentlyPlayingMedia() == 1) {
            if (PlaybackPreferences.getCurrentlyPlayingFeedMediaId() == this.id) {
                return true;
            }
        }
        return false;
    }

    public boolean isCurrentlyPlaying() {
        if (isPlaying() && PlaybackService.isRunning) {
            if (PlaybackPreferences.getCurrentPlayerStatus() == 1) {
                return true;
            }
        }
        return false;
    }

    public boolean isCurrentlyPaused() {
        if (isPlaying()) {
            if (PlaybackPreferences.getCurrentPlayerStatus() == 2) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAlmostEnded() {
        return this.position >= this.duration - (UserPreferences.getSmartMarkAsPlayedSecs() * 1000);
    }

    public int getTypeAsInt() {
        return 2;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setLastPlayedTime(long lastPlayedTime) {
        this.lastPlayedTime = lastPlayedTime;
    }

    public int getPlayedDuration() {
        return this.played_duration;
    }

    public void setPlayedDuration(int played_duration) {
        this.played_duration = played_duration;
    }

    public int getPosition() {
        return this.position;
    }

    public long getLastPlayedTime() {
        return this.lastPlayedTime;
    }

    public void setPosition(int position) {
        this.position = position;
        if (position > 0 && this.item != null && this.item.isNew()) {
            this.item.setPlayed(false);
        }
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setCheckedOnSizeButUnknown() {
        this.size = -2147483648L;
    }

    public boolean checkedOnSizeButUnknown() {
        return -2147483648L == this.size;
    }

    public String getMime_type() {
        return this.mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    @Nullable
    public FeedItem getItem() {
        return this.item;
    }

    public void setItem(FeedItem item) {
        this.item = item;
        if (item != null && item.getMedia() != this) {
            item.setMedia(this);
        }
    }

    public Date getPlaybackCompletionDate() {
        Date date = this.playbackCompletionDate;
        if (date == null) {
            return null;
        }
        return (Date) date.clone();
    }

    public void setPlaybackCompletionDate(Date playbackCompletionDate) {
        Date date;
        if (playbackCompletionDate == null) {
            date = null;
        } else {
            date = (Date) playbackCompletionDate.clone();
        }
        this.playbackCompletionDate = date;
    }

    public boolean isInProgress() {
        return this.position > 0;
    }

    public int describeContents() {
        return 0;
    }

    public boolean hasEmbeddedPicture() {
        if (this.hasEmbeddedPicture == null) {
            checkEmbeddedPicture();
        }
        return this.hasEmbeddedPicture.booleanValue();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        long j = 0;
        dest.writeLong(this.item != null ? this.item.getId() : 0);
        dest.writeInt(this.duration);
        dest.writeInt(this.position);
        dest.writeLong(this.size);
        dest.writeString(this.mime_type);
        dest.writeString(this.file_url);
        dest.writeString(this.download_url);
        dest.writeByte((byte) this.downloaded);
        Date date = this.playbackCompletionDate;
        if (date != null) {
            j = date.getTime();
        }
        dest.writeLong(j);
        dest.writeInt(this.played_duration);
        dest.writeLong(this.lastPlayedTime);
    }

    public void writeToPreferences(Editor prefEditor) {
        if (this.item == null || this.item.getFeed() == null) {
            prefEditor.putLong(PREF_FEED_ID, 0);
        } else {
            prefEditor.putLong(PREF_FEED_ID, this.item.getFeed().getId());
        }
        prefEditor.putLong(PREF_MEDIA_ID, this.id);
    }

    public void loadMetadata() throws PlayableException {
        if (this.item == null) {
            long j = this.itemID;
            if (j != 0) {
                this.item = DBReader.getFeedItem(j);
            }
        }
    }

    public void loadChapterMarks() {
        if (this.item == null) {
            long j = this.itemID;
            if (j != 0) {
                this.item = DBReader.getFeedItem(j);
                if (this.item != null) {
                    if (this.item.getChapters() != null) {
                        if (this.item.hasChapters()) {
                            if (localFileAvailable()) {
                                ChapterUtils.loadChaptersFromStreamUrl(this);
                            } else {
                                ChapterUtils.loadChaptersFromFileUrl(this);
                            }
                            if (this.item.getChapters() != null) {
                                DBWriter.setFeedItem(this.item);
                            }
                        } else {
                            DBReader.loadChaptersOfFeedItem(this.item);
                        }
                    }
                }
            }
        }
        if (this.item != null) {
            if (this.item.getChapters() != null) {
                if (this.item.hasChapters()) {
                    if (localFileAvailable()) {
                        ChapterUtils.loadChaptersFromStreamUrl(this);
                    } else {
                        ChapterUtils.loadChaptersFromFileUrl(this);
                    }
                    if (this.item.getChapters() != null) {
                        DBWriter.setFeedItem(this.item);
                    }
                } else {
                    DBReader.loadChaptersOfFeedItem(this.item);
                }
            }
        }
    }

    public String getEpisodeTitle() {
        if (this.item == null) {
            return null;
        }
        if (this.item.getTitle() != null) {
            return this.item.getTitle();
        }
        return this.item.getIdentifyingValue();
    }

    public List<Chapter> getChapters() {
        if (this.item == null) {
            return null;
        }
        return this.item.getChapters();
    }

    public String getWebsiteLink() {
        if (this.item == null) {
            return null;
        }
        return this.item.getLink();
    }

    public String getFeedTitle() {
        if (this.item != null) {
            if (this.item.getFeed() != null) {
                return this.item.getFeed().getTitle();
            }
        }
        return null;
    }

    public Object getIdentifier() {
        return Long.valueOf(this.id);
    }

    public String getLocalMediaUrl() {
        return this.file_url;
    }

    public String getStreamUrl() {
        return this.download_url;
    }

    public String getPaymentLink() {
        if (this.item == null) {
            return null;
        }
        return this.item.getPaymentLink();
    }

    public boolean localFileAvailable() {
        return isDownloaded() && this.file_url != null;
    }

    public boolean streamAvailable() {
        return this.download_url != null;
    }

    public void saveCurrentPosition(SharedPreferences pref, int newPosition, long timeStamp) {
        if (this.item != null && this.item.isNew()) {
            DBWriter.markItemPlayed(0, this.item.getId());
        }
        setPosition(newPosition);
        setLastPlayedTime(timeStamp);
        int i = this.startPosition;
        if (i >= 0) {
            int i2 = this.position;
            if (i2 > i) {
                setPlayedDuration((this.playedDurationWhenStarted + i2) - i);
                DBWriter.setFeedMediaPlaybackInformation(this);
            }
        }
        DBWriter.setFeedMediaPlaybackInformation(this);
    }

    public void onPlaybackStart() {
        int i = this.position;
        if (i <= 0) {
            i = 0;
        }
        this.startPosition = i;
        this.playedDurationWhenStarted = this.played_duration;
    }

    public void onPlaybackPause(Context context) {
        int i = this.position;
        int i2 = this.startPosition;
        if (i > i2) {
            this.played_duration = (this.playedDurationWhenStarted + i) - i2;
            this.playedDurationWhenStarted = this.played_duration;
        }
        postPlaybackTasks(context, false);
        this.startPosition = this.position;
    }

    public void onPlaybackCompleted(Context context) {
        postPlaybackTasks(context, true);
        this.startPosition = -1;
    }

    private void postPlaybackTasks(Context context, boolean completed) {
        if (this.item != null) {
            int i = this.startPosition;
            if (i >= 0 && (completed || i < this.position)) {
                if (GpodnetPreferences.loggedIn()) {
                    GpodnetPreferences.enqueueEpisodeAction(new GpodnetEpisodeAction.Builder(this.item, Action.PLAY).currentDeviceId().currentTimestamp().started(this.startPosition / 1000).position((completed ? this.duration : this.position) / 1000).total(this.duration / 1000).build());
                }
            }
            float autoFlattrThreshold = UserPreferences.getAutoFlattrPlayedDurationThreshold();
            if (!FlattrUtils.hasToken()) {
                return;
            }
            if (!UserPreferences.isAutoFlattr()) {
                return;
            }
            if (this.item.getPaymentLink() == null) {
                return;
            }
            if (this.item.getFlattrStatus().getUnflattred() && ((completed && autoFlattrThreshold <= 1.0f) || ((float) this.played_duration) >= ((float) this.duration) * autoFlattrThreshold)) {
                DBTasks.flattrItemIfLoggedIn(context, this.item);
            }
        }
    }

    public int getPlayableType() {
        return 1;
    }

    public void setChapters(List<Chapter> chapters) {
        if (this.item != null) {
            this.item.setChapters(chapters);
        }
    }

    public Callable<String> loadShownotes() {
        return new -$$Lambda$FeedMedia$Cfn7AABbAA5vqKNK9lWlYPfrU6c();
    }

    public static /* synthetic */ String lambda$loadShownotes$0(FeedMedia feedMedia) throws Exception {
        if (feedMedia.item == null) {
            feedMedia.item = DBReader.getFeedItem(feedMedia.itemID);
        }
        return (String) feedMedia.item.loadShownotes().call();
    }

    public String getImageLocation() {
        if (hasEmbeddedPicture()) {
            return getLocalMediaUrl();
        }
        if (this.item != null) {
            return this.item.getImageLocation();
        }
        return null;
    }

    public void setHasEmbeddedPicture(Boolean hasEmbeddedPicture) {
        this.hasEmbeddedPicture = hasEmbeddedPicture;
    }

    public void setDownloaded(boolean downloaded) {
        super.setDownloaded(downloaded);
        if (this.item != null && downloaded) {
            this.item.setPlayed(false);
        }
    }

    public void setFile_url(String file_url) {
        super.setFile_url(file_url);
    }

    public void checkEmbeddedPicture() {
        if (localFileAvailable()) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            try {
                mmr.setDataSource(getLocalMediaUrl());
                if (mmr.getEmbeddedPicture() != null) {
                    this.hasEmbeddedPicture = Boolean.TRUE;
                } else {
                    this.hasEmbeddedPicture = Boolean.FALSE;
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.hasEmbeddedPicture = Boolean.FALSE;
            }
            return;
        }
        this.hasEmbeddedPicture = Boolean.FALSE;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (FeedMediaFlavorHelper.instanceOfRemoteMedia(o)) {
            return o.equals(this);
        }
        return super.equals(o);
    }
}

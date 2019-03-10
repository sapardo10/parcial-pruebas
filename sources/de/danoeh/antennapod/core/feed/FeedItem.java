package de.danoeh.antennapod.core.feed;

import android.database.Cursor;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import de.danoeh.antennapod.core.asynctask.ImageResource;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.core.util.ShownotesProvider;
import de.danoeh.antennapod.core.util.flattr.FlattrStatus;
import de.danoeh.antennapod.core.util.flattr.FlattrThing;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FeedItem extends FeedComponent implements ShownotesProvider, FlattrThing, ImageResource {
    public static final int NEW = -1;
    public static final int PLAYED = 1;
    public static final String TAG_FAVORITE = "Favorite";
    public static final String TAG_QUEUE = "Queue";
    public static final int UNPLAYED = 0;
    private long autoDownload;
    private List<Chapter> chapters;
    private String contentEncoded;
    private String description;
    private Feed feed;
    private long feedId;
    private final FlattrStatus flattrStatus;
    private final boolean hasChapters;
    private String imageUrl;
    private String itemIdentifier;
    private String link;
    private FeedMedia media;
    private String paymentLink;
    private Date pubDate;
    private int state;
    private final Set<String> tags;
    private String title;

    public enum State {
        UNREAD,
        IN_PROGRESS,
        READ,
        PLAYING
    }

    public FeedItem() {
        this.autoDownload = 1;
        this.tags = new HashSet();
        this.state = 0;
        this.flattrStatus = new FlattrStatus();
        this.hasChapters = false;
    }

    public FeedItem(long id, String title, String link, Date pubDate, String paymentLink, long feedId, FlattrStatus flattrStatus, boolean hasChapters, String imageUrl, int state, String itemIdentifier, long autoDownload) {
        this.autoDownload = 1;
        this.tags = new HashSet();
        this.id = id;
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.paymentLink = paymentLink;
        this.feedId = feedId;
        this.flattrStatus = flattrStatus;
        this.hasChapters = hasChapters;
        this.imageUrl = imageUrl;
        this.state = state;
        this.itemIdentifier = itemIdentifier;
        this.autoDownload = autoDownload;
    }

    public FeedItem(long id, String title, String itemIdentifier, String link, Date pubDate, int state, Feed feed) {
        this.autoDownload = 1;
        this.tags = new HashSet();
        this.id = id;
        this.title = title;
        this.itemIdentifier = itemIdentifier;
        this.link = link;
        this.pubDate = pubDate != null ? (Date) pubDate.clone() : null;
        this.state = state;
        this.feed = feed;
        this.flattrStatus = new FlattrStatus();
        this.hasChapters = false;
    }

    public FeedItem(long id, String title, String itemIdentifier, String link, Date pubDate, int state, Feed feed, boolean hasChapters) {
        this.autoDownload = 1;
        this.tags = new HashSet();
        this.id = id;
        this.title = title;
        this.itemIdentifier = itemIdentifier;
        this.link = link;
        this.pubDate = pubDate != null ? (Date) pubDate.clone() : null;
        this.state = state;
        this.feed = feed;
        this.flattrStatus = new FlattrStatus();
        this.hasChapters = hasChapters;
    }

    public static FeedItem fromCursor(Cursor cursor) {
        Cursor cursor2 = cursor;
        int indexId = cursor2.getColumnIndex("id");
        int indexTitle = cursor2.getColumnIndex("title");
        int indexLink = cursor2.getColumnIndex(PodDBAdapter.KEY_LINK);
        int indexPubDate = cursor2.getColumnIndex(PodDBAdapter.KEY_PUBDATE);
        int indexPaymentLink = cursor2.getColumnIndex(PodDBAdapter.KEY_PAYMENT_LINK);
        int indexFeedId = cursor2.getColumnIndex(PodDBAdapter.KEY_FEED);
        int indexFlattrStatus = cursor2.getColumnIndex(PodDBAdapter.KEY_FLATTR_STATUS);
        int indexHasChapters = cursor2.getColumnIndex(PodDBAdapter.KEY_HAS_CHAPTERS);
        int indexRead = cursor2.getColumnIndex(PodDBAdapter.KEY_READ);
        int indexItemIdentifier = cursor2.getColumnIndex(PodDBAdapter.KEY_ITEM_IDENTIFIER);
        int indexAutoDownload = cursor2.getColumnIndex(PodDBAdapter.KEY_AUTO_DOWNLOAD);
        int indexImageUrl = cursor2.getColumnIndex(PodDBAdapter.KEY_IMAGE_URL);
        return new FeedItem((long) cursor2.getInt(indexId), cursor2.getString(indexTitle), cursor2.getString(indexLink), new Date(cursor2.getLong(indexPubDate)), cursor2.getString(indexPaymentLink), cursor2.getLong(indexFeedId), new FlattrStatus(cursor2.getLong(indexFlattrStatus)), cursor2.getInt(indexHasChapters) > 0, cursor2.getString(indexImageUrl), cursor2.getInt(indexRead), cursor2.getString(indexItemIdentifier), cursor2.getLong(indexAutoDownload));
    }

    public void updateFromOther(FeedItem other) {
        super.updateFromOther(other);
        String str = other.imageUrl;
        if (str != null) {
            this.imageUrl = str;
        }
        str = other.title;
        if (str != null) {
            this.title = str;
        }
        if (other.getDescription() != null) {
            this.description = other.getDescription();
        }
        if (other.getContentEncoded() != null) {
            this.contentEncoded = other.contentEncoded;
        }
        str = other.link;
        if (str != null) {
            this.link = str;
        }
        Date date = other.pubDate;
        if (date != null && date.equals(this.pubDate)) {
            this.pubDate = other.pubDate;
        }
        FeedMedia feedMedia = other.media;
        if (feedMedia != null) {
            FeedMedia feedMedia2 = this.media;
            if (feedMedia2 == null) {
                setMedia(feedMedia);
                setNew();
            } else if (feedMedia2.compareWithOther(feedMedia)) {
                this.media.updateFromOther(other.media);
            }
        }
        str = other.paymentLink;
        if (str != null) {
            this.paymentLink = str;
        }
        List list = other.chapters;
        if (list == null) {
            return;
        }
        if (!this.hasChapters) {
            this.chapters = list;
        }
    }

    public String getIdentifyingValue() {
        String str = this.itemIdentifier;
        if (str != null && !str.isEmpty()) {
            return this.itemIdentifier;
        }
        str = this.title;
        if (str == null || str.isEmpty()) {
            return this.link;
        }
        return this.title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPubDate() {
        Date date = this.pubDate;
        if (date != null) {
            return (Date) date.clone();
        }
        return null;
    }

    public void setPubDate(Date pubDate) {
        if (pubDate != null) {
            this.pubDate = (Date) pubDate.clone();
        } else {
            this.pubDate = null;
        }
    }

    @Nullable
    public FeedMedia getMedia() {
        return this.media;
    }

    public void setMedia(FeedMedia media) {
        this.media = media;
        if (media != null && media.getItem() != this) {
            media.setItem(this);
        }
    }

    public Feed getFeed() {
        return this.feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public boolean isNew() {
        return this.state == -1;
    }

    public void setNew() {
        this.state = -1;
    }

    public boolean isPlayed() {
        return this.state == 1;
    }

    public void setPlayed(boolean played) {
        if (played) {
            this.state = 1;
        } else {
            this.state = 0;
        }
    }

    private boolean isInProgress() {
        FeedMedia feedMedia = this.media;
        return feedMedia != null && feedMedia.isInProgress();
    }

    public String getContentEncoded() {
        return this.contentEncoded;
    }

    public void setContentEncoded(String contentEncoded) {
        this.contentEncoded = contentEncoded;
    }

    public FlattrStatus getFlattrStatus() {
        return this.flattrStatus;
    }

    public String getPaymentLink() {
        return this.paymentLink;
    }

    public void setPaymentLink(String paymentLink) {
        this.paymentLink = paymentLink;
    }

    public List<Chapter> getChapters() {
        return this.chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public String getItemIdentifier() {
        return this.itemIdentifier;
    }

    public void setItemIdentifier(String itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }

    public boolean hasMedia() {
        return this.media != null;
    }

    private boolean isPlaying() {
        FeedMedia feedMedia = this.media;
        return feedMedia != null && feedMedia.isPlaying();
    }

    public Callable<String> loadShownotes() {
        return new -$$Lambda$FeedItem$15ipW7K1aN6EDRXYRl-MSZIZsX4();
    }

    public static /* synthetic */ String lambda$loadShownotes$0(FeedItem feedItem) throws Exception {
        double length;
        double length2;
        if (feedItem.contentEncoded != null) {
            if (feedItem.description != null) {
                if (TextUtils.isEmpty(feedItem.contentEncoded)) {
                    return feedItem.description;
                }
                if (TextUtils.isEmpty(feedItem.description)) {
                    return feedItem.contentEncoded;
                }
                length = (double) feedItem.description.length();
                length2 = (double) feedItem.contentEncoded.length();
                Double.isNaN(length2);
                if (length <= length2 * 1.25d) {
                    return feedItem.description;
                }
                return feedItem.contentEncoded;
            }
        }
        DBReader.loadExtraInformationOfFeedItem(feedItem);
        if (TextUtils.isEmpty(feedItem.contentEncoded)) {
            return feedItem.description;
        }
        if (TextUtils.isEmpty(feedItem.description)) {
            return feedItem.contentEncoded;
        }
        length = (double) feedItem.description.length();
        length2 = (double) feedItem.contentEncoded.length();
        Double.isNaN(length2);
        if (length <= length2 * 1.25d) {
            return feedItem.contentEncoded;
        }
        return feedItem.description;
    }

    public String getImageLocation() {
        FeedMedia feedMedia = this.media;
        if (feedMedia != null && feedMedia.hasEmbeddedPicture()) {
            return this.media.getImageLocation();
        }
        String str = this.imageUrl;
        if (str != null) {
            return str;
        }
        Feed feed = this.feed;
        if (feed != null) {
            return feed.getImageLocation();
        }
        return null;
    }

    public State getState() {
        if (hasMedia()) {
            if (isPlaying()) {
                return State.PLAYING;
            }
            if (isInProgress()) {
                return State.IN_PROGRESS;
            }
        }
        return isPlayed() ? State.READ : State.UNREAD;
    }

    public long getFeedId() {
        return this.feedId;
    }

    public void setFeedId(long feedId) {
        this.feedId = feedId;
    }

    public String getImageUrl() {
        String str = this.imageUrl;
        return str != null ? str : this.feed.getImageUrl();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHumanReadableIdentifier() {
        return this.title;
    }

    public boolean hasChapters() {
        return this.hasChapters;
    }

    public void setAutoDownload(boolean autoDownload) {
        this.autoDownload = autoDownload ? 1 : 0;
    }

    public boolean getAutoDownload() {
        return this.autoDownload > 0;
    }

    public int getFailedAutoDownloadAttempts() {
        long j = this.autoDownload;
        if (j <= 1) {
            return 0;
        }
        int failedAttempts = (int) (j % 10);
        if (failedAttempts == 0) {
            failedAttempts = 10;
        }
        return failedAttempts;
    }

    public boolean isAutoDownloadable() {
        FeedMedia feedMedia = this.media;
        if (!(feedMedia == null || feedMedia.isPlaying() || this.media.isDownloaded())) {
            long j = this.autoDownload;
            if (j != 0) {
                boolean z = true;
                if (j == 1) {
                    return true;
                }
                double pow = Math.pow(1.767d, (double) (getFailedAutoDownloadAttempts() - 1));
                double d = (double) 3600000;
                Double.isNaN(d);
                long waitingTime = (long) (pow * d);
                if (System.currentTimeMillis() <= (this.autoDownload + waitingTime) - TimeUnit.MINUTES.toMillis(5)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public boolean isTagged(String tag) {
        return this.tags.contains(tag);
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public void removeTag(String tag) {
        this.tags.remove(tag);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

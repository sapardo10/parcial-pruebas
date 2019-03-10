package de.danoeh.antennapod.core.feed;

import android.database.Cursor;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import de.danoeh.antennapod.core.asynctask.ImageResource;
import de.danoeh.antennapod.core.feed.FeedPreferences.AutoDeleteAction;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.core.util.flattr.FlattrStatus;
import de.danoeh.antennapod.core.util.flattr.FlattrThing;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Feed extends FeedFile implements FlattrThing, ImageResource {
    public static final int FEEDFILETYPE_FEED = 0;
    public static final String TYPE_ATOM1 = "atom";
    public static final String TYPE_RSS2 = "rss";
    private String author;
    private String customTitle;
    private String description;
    private String feedIdentifier;
    private String feedTitle;
    private FlattrStatus flattrStatus;
    private String imageUrl;
    private FeedItemFilter itemfilter;
    private List<FeedItem> items;
    private String language;
    private String lastUpdate;
    private boolean lastUpdateFailed;
    private String link;
    private String nextPageLink;
    private int pageNr;
    private boolean paged;
    private String paymentLink;
    private FeedPreferences preferences;
    private String type;

    public Feed(long id, String lastUpdate, String title, String customTitle, String link, String description, String paymentLink, String author, String language, String type, String feedIdentifier, String imageUrl, String fileUrl, String downloadUrl, boolean downloaded, FlattrStatus status, boolean paged, String nextPageLink, String filter, boolean lastUpdateFailed) {
        String str = filter;
        super(fileUrl, downloadUrl, downloaded);
        this.id = id;
        this.feedTitle = title;
        this.customTitle = customTitle;
        this.lastUpdate = lastUpdate;
        this.link = link;
        this.description = description;
        this.paymentLink = paymentLink;
        this.author = author;
        this.language = language;
        this.type = type;
        this.feedIdentifier = feedIdentifier;
        this.imageUrl = imageUrl;
        this.flattrStatus = status;
        this.paged = paged;
        this.nextPageLink = nextPageLink;
        this.items = new ArrayList();
        if (str != null) {
            r0.itemfilter = new FeedItemFilter(str);
        } else {
            r0.itemfilter = new FeedItemFilter(new String[0]);
        }
        r0.lastUpdateFailed = lastUpdateFailed;
    }

    public Feed(long id, String lastUpdate, String title, String link, String description, String paymentLink, String author, String language, String type, String feedIdentifier, String imageUrl, String fileUrl, String downloadUrl, boolean downloaded) {
        long j = id;
        String str = lastUpdate;
        String str2 = title;
        String str3 = link;
        String str4 = description;
        String str5 = paymentLink;
        String str6 = author;
        String str7 = language;
        String str8 = type;
        String str9 = feedIdentifier;
        String str10 = imageUrl;
        String str11 = fileUrl;
        String str12 = downloadUrl;
        boolean z = downloaded;
        FlattrStatus flattrStatus = r5;
        FlattrStatus flattrStatus2 = new FlattrStatus();
        this(j, str, str2, null, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, flattrStatus, false, null, null, false);
    }

    public Feed() {
        this.flattrStatus = new FlattrStatus();
    }

    public Feed(String url, String lastUpdate) {
        super(null, url, false);
        this.lastUpdate = lastUpdate;
        this.flattrStatus = new FlattrStatus();
    }

    public Feed(String url, String lastUpdate, String title) {
        this(url, lastUpdate);
        this.feedTitle = title;
        this.flattrStatus = new FlattrStatus();
    }

    public Feed(String url, String lastUpdate, String title, String username, String password) {
        this(url, lastUpdate, title);
        this.preferences = new FeedPreferences(0, true, AutoDeleteAction.GLOBAL, username, password);
    }

    public static Feed fromCursor(Cursor cursor) {
        Cursor cursor2 = cursor;
        int indexId = cursor2.getColumnIndex("id");
        int indexLastUpdate = cursor2.getColumnIndex(PodDBAdapter.KEY_LASTUPDATE);
        int indexTitle = cursor2.getColumnIndex("title");
        int indexCustomTitle = cursor2.getColumnIndex(PodDBAdapter.KEY_CUSTOM_TITLE);
        int indexLink = cursor2.getColumnIndex(PodDBAdapter.KEY_LINK);
        int indexDescription = cursor2.getColumnIndex(PodDBAdapter.KEY_DESCRIPTION);
        int indexPaymentLink = cursor2.getColumnIndex(PodDBAdapter.KEY_PAYMENT_LINK);
        int indexAuthor = cursor2.getColumnIndex(PodDBAdapter.KEY_AUTHOR);
        int indexLanguage = cursor2.getColumnIndex(PodDBAdapter.KEY_LANGUAGE);
        int indexType = cursor2.getColumnIndex("type");
        int indexFeedIdentifier = cursor2.getColumnIndex(PodDBAdapter.KEY_FEED_IDENTIFIER);
        int indexFileUrl = cursor2.getColumnIndex(PodDBAdapter.KEY_FILE_URL);
        int indexDownloadUrl = cursor2.getColumnIndex(PodDBAdapter.KEY_DOWNLOAD_URL);
        int indexDownloaded = cursor2.getColumnIndex(PodDBAdapter.KEY_DOWNLOADED);
        int indexFlattrStatus = cursor2.getColumnIndex(PodDBAdapter.KEY_FLATTR_STATUS);
        int indexIsPaged = cursor2.getColumnIndex(PodDBAdapter.KEY_IS_PAGED);
        int indexNextPageLink = cursor2.getColumnIndex(PodDBAdapter.KEY_NEXT_PAGE_LINK);
        int indexHide = cursor2.getColumnIndex(PodDBAdapter.KEY_HIDE);
        int indexLastUpdateFailed = cursor2.getColumnIndex(PodDBAdapter.KEY_LAST_UPDATE_FAILED);
        int indexImageUrl = cursor2.getColumnIndex(PodDBAdapter.KEY_IMAGE_URL);
        long j = cursor2.getLong(indexId);
        String string = cursor2.getString(indexLastUpdate);
        String string2 = cursor2.getString(indexTitle);
        String string3 = cursor2.getString(indexCustomTitle);
        String string4 = cursor2.getString(indexLink);
        String string5 = cursor2.getString(indexDescription);
        String string6 = cursor2.getString(indexPaymentLink);
        String string7 = cursor2.getString(indexAuthor);
        String string8 = cursor2.getString(indexLanguage);
        String string9 = cursor2.getString(indexType);
        String string10 = cursor2.getString(indexFeedIdentifier);
        String string11 = cursor2.getString(indexImageUrl);
        String string12 = cursor2.getString(indexFileUrl);
        String string13 = cursor2.getString(indexDownloadUrl);
        boolean z = cursor2.getInt(indexDownloaded) > 0;
        indexLastUpdate = indexFlattrStatus;
        FlattrStatus flattrStatus = new FlattrStatus(cursor2.getLong(indexLastUpdate));
        boolean z2 = cursor2.getInt(indexIsPaged) > 0;
        Feed feed = new Feed(j, string, string2, string3, string4, string5, string6, string7, string8, string9, string10, string11, string12, string13, z, flattrStatus, z2, cursor2.getString(indexNextPageLink), cursor2.getString(indexHide), cursor2.getInt(indexLastUpdateFailed) > 0);
        feed.setPreferences(FeedPreferences.fromCursor(cursor));
        return feed;
    }

    public int getNumOfItems() {
        return this.items.size();
    }

    public FeedItem getItemAtIndex(int position) {
        return (FeedItem) this.items.get(position);
    }

    public String getIdentifyingValue() {
        String str = this.feedIdentifier;
        if (str != null && !str.isEmpty()) {
            return this.feedIdentifier;
        }
        if (this.download_url != null && !this.download_url.isEmpty()) {
            return this.download_url;
        }
        str = this.feedTitle;
        if (str == null || str.isEmpty()) {
            return this.link;
        }
        return this.feedTitle;
    }

    public String getHumanReadableIdentifier() {
        String str = this.feedTitle;
        if (str != null) {
            return str;
        }
        return this.download_url;
    }

    public void updateFromOther(Feed other) {
        String str = other.imageUrl;
        if (str != null) {
            this.imageUrl = str;
        }
        str = other.feedTitle;
        if (str != null) {
            this.feedTitle = str;
        }
        str = other.feedIdentifier;
        if (str != null) {
            this.feedIdentifier = str;
        }
        str = other.link;
        if (str != null) {
            this.link = str;
        }
        str = other.description;
        if (str != null) {
            this.description = str;
        }
        str = other.language;
        if (str != null) {
            this.language = str;
        }
        str = other.author;
        if (str != null) {
            this.author = str;
        }
        str = other.paymentLink;
        if (str != null) {
            this.paymentLink = str;
        }
        FlattrStatus flattrStatus = other.flattrStatus;
        if (flattrStatus != null) {
            this.flattrStatus = flattrStatus;
        }
        if (!this.paged) {
            boolean z = other.paged;
            if (z) {
                this.paged = z;
                this.nextPageLink = other.nextPageLink;
            }
        }
    }

    public boolean compareWithOther(Feed other) {
        if (super.compareWithOther(other)) {
            return true;
        }
        CharSequence charSequence = other.imageUrl;
        if (charSequence != null) {
            CharSequence charSequence2 = this.imageUrl;
            if (charSequence2 != null) {
                if (TextUtils.equals(charSequence2, charSequence)) {
                }
            }
            return true;
        }
        if (!TextUtils.equals(this.feedTitle, other.feedTitle)) {
            return true;
        }
        String str;
        String str2 = other.feedIdentifier;
        if (str2 != null) {
            str = this.feedIdentifier;
            if (str != null) {
                if (str.equals(str2)) {
                }
            }
            return true;
        }
        str2 = other.link;
        if (str2 != null) {
            str = this.link;
            if (str != null) {
                if (str.equals(str2)) {
                }
            }
            return true;
        }
        str2 = other.description;
        if (str2 != null) {
            str = this.description;
            if (str != null) {
                if (str.equals(str2)) {
                }
            }
            return true;
        }
        str2 = other.language;
        if (str2 != null) {
            str = this.language;
            if (str != null) {
                if (str.equals(str2)) {
                }
            }
            return true;
        }
        str2 = other.author;
        if (str2 != null) {
            str = this.author;
            if (str != null) {
                if (str.equals(str2)) {
                }
            }
            return true;
        }
        str2 = other.paymentLink;
        if (str2 != null) {
            str = this.paymentLink;
            if (str != null) {
                if (str.equals(str2)) {
                }
            }
            return true;
        }
        if ((!other.isPaged() || isPaged()) && TextUtils.equals(other.getNextPageLink(), getNextPageLink())) {
            return false;
        }
        return true;
    }

    public FeedItem getMostRecentItem() {
        Date mostRecentDate = new Date(0);
        FeedItem mostRecentItem = null;
        for (FeedItem item : this.items) {
            if (item.getPubDate().after(mostRecentDate)) {
                mostRecentDate = item.getPubDate();
                mostRecentItem = item;
            }
        }
        return mostRecentItem;
    }

    public int getTypeAsInt() {
        return 0;
    }

    public String getTitle() {
        return !TextUtils.isEmpty(this.customTitle) ? this.customTitle : this.feedTitle;
    }

    public void setTitle(String title) {
        this.feedTitle = title;
    }

    public String getFeedTitle() {
        return this.feedTitle;
    }

    @Nullable
    public String getCustomTitle() {
        return this.customTitle;
    }

    public void setCustomTitle(String customTitle) {
        if (customTitle != null) {
            if (!customTitle.equals(this.feedTitle)) {
                this.customTitle = customTitle;
                return;
            }
        }
        this.customTitle = null;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<FeedItem> getItems() {
        return this.items;
    }

    public void setItems(List<FeedItem> list) {
        this.items = list;
    }

    public String getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate(String lastModified) {
        this.lastUpdate = lastModified;
    }

    public String getFeedIdentifier() {
        return this.feedIdentifier;
    }

    public void setFeedIdentifier(String feedIdentifier) {
        this.feedIdentifier = feedIdentifier;
    }

    public void setFlattrStatus(FlattrStatus status) {
        this.flattrStatus = status;
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

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPreferences(FeedPreferences preferences) {
        this.preferences = preferences;
    }

    public FeedPreferences getPreferences() {
        return this.preferences;
    }

    public void savePreferences() {
        DBWriter.setFeedPreferences(this.preferences);
    }

    public void setId(long id) {
        super.setId(id);
        FeedPreferences feedPreferences = this.preferences;
        if (feedPreferences != null) {
            feedPreferences.setFeedID(id);
        }
    }

    public String getImageLocation() {
        return this.imageUrl;
    }

    public int getPageNr() {
        return this.pageNr;
    }

    public void setPageNr(int pageNr) {
        this.pageNr = pageNr;
    }

    public boolean isPaged() {
        return this.paged;
    }

    public void setPaged(boolean paged) {
        this.paged = paged;
    }

    public String getNextPageLink() {
        return this.nextPageLink;
    }

    public void setNextPageLink(String nextPageLink) {
        this.nextPageLink = nextPageLink;
    }

    @Nullable
    public FeedItemFilter getItemFilter() {
        return this.itemfilter;
    }

    public void setItemFilter(String[] properties) {
        if (properties != null) {
            this.itemfilter = new FeedItemFilter(properties);
        }
    }

    public boolean hasLastUpdateFailed() {
        return this.lastUpdateFailed;
    }

    public void setLastUpdateFailed(boolean lastUpdateFailed) {
        this.lastUpdateFailed = lastUpdateFailed;
    }
}

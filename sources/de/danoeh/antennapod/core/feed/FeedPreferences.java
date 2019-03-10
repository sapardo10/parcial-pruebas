package de.danoeh.antennapod.core.feed;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.PodDBAdapter;

public class FeedPreferences {
    private boolean autoDownload;
    private AutoDeleteAction auto_delete_action;
    private long feedID;
    @NonNull
    private FeedFilter filter;
    private boolean keepUpdated;
    private String password;
    private String username;

    public enum AutoDeleteAction {
        GLOBAL,
        YES,
        NO
    }

    public FeedPreferences(long feedID, boolean autoDownload, AutoDeleteAction auto_delete_action, String username, String password) {
        this(feedID, autoDownload, true, auto_delete_action, username, password, new FeedFilter());
    }

    private FeedPreferences(long feedID, boolean autoDownload, boolean keepUpdated, AutoDeleteAction auto_delete_action, String username, String password, @NonNull FeedFilter filter) {
        this.feedID = feedID;
        this.autoDownload = autoDownload;
        this.keepUpdated = keepUpdated;
        this.auto_delete_action = auto_delete_action;
        this.username = username;
        this.password = password;
        this.filter = filter;
    }

    public static FeedPreferences fromCursor(Cursor cursor) {
        Cursor cursor2 = cursor;
        int indexId = cursor2.getColumnIndex("id");
        int indexAutoDownload = cursor2.getColumnIndex(PodDBAdapter.KEY_AUTO_DOWNLOAD);
        int indexAutoRefresh = cursor2.getColumnIndex(PodDBAdapter.KEY_KEEP_UPDATED);
        int indexAutoDeleteAction = cursor2.getColumnIndex(PodDBAdapter.KEY_AUTO_DELETE_ACTION);
        int indexUsername = cursor2.getColumnIndex(PodDBAdapter.KEY_USERNAME);
        int indexPassword = cursor2.getColumnIndex(PodDBAdapter.KEY_PASSWORD);
        int indexIncludeFilter = cursor2.getColumnIndex(PodDBAdapter.KEY_INCLUDE_FILTER);
        int indexExcludeFilter = cursor2.getColumnIndex(PodDBAdapter.KEY_EXCLUDE_FILTER);
        long feedId = cursor2.getLong(indexId);
        boolean autoDownload = cursor2.getInt(indexAutoDownload) > 0;
        boolean autoRefresh = cursor2.getInt(indexAutoRefresh) > 0;
        AutoDeleteAction autoDeleteAction = AutoDeleteAction.values()[cursor2.getInt(indexAutoDeleteAction)];
        String username = cursor2.getString(indexUsername);
        String password = cursor2.getString(indexPassword);
        String includeFilter = cursor2.getString(indexIncludeFilter);
        String excludeFilter = cursor2.getString(indexExcludeFilter);
        return new FeedPreferences(feedId, autoDownload, autoRefresh, autoDeleteAction, username, password, new FeedFilter(includeFilter, excludeFilter));
    }

    @NonNull
    public FeedFilter getFilter() {
        return this.filter;
    }

    public void setFilter(@NonNull FeedFilter filter) {
        this.filter = filter;
    }

    public boolean getKeepUpdated() {
        return this.keepUpdated;
    }

    public void setKeepUpdated(boolean keepUpdated) {
        this.keepUpdated = keepUpdated;
    }

    public boolean compareWithOther(FeedPreferences other) {
        if (other != null && TextUtils.equals(this.username, other.username) && TextUtils.equals(this.password, other.password)) {
            return false;
        }
        return true;
    }

    public void updateFromOther(FeedPreferences other) {
        if (other != null) {
            this.username = other.username;
            this.password = other.password;
        }
    }

    public long getFeedID() {
        return this.feedID;
    }

    public void setFeedID(long feedID) {
        this.feedID = feedID;
    }

    public boolean getAutoDownload() {
        return this.autoDownload;
    }

    public void setAutoDownload(boolean autoDownload) {
        this.autoDownload = autoDownload;
    }

    public AutoDeleteAction getAutoDeleteAction() {
        return this.auto_delete_action;
    }

    public void setAutoDeleteAction(AutoDeleteAction auto_delete_action) {
        this.auto_delete_action = auto_delete_action;
    }

    public boolean getCurrentAutoDelete() {
        switch (this.auto_delete_action) {
            case GLOBAL:
                return UserPreferences.isAutoDelete();
            case YES:
                return true;
            case NO:
                return false;
            default:
                return false;
        }
    }

    public void save(Context context) {
        DBWriter.setFeedPreferences(this);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

package de.danoeh.antennapod.core.service.download;

import android.database.Cursor;
import android.support.annotation.NonNull;
import de.danoeh.antennapod.core.feed.FeedFile;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.core.util.DownloadError;
import java.util.Date;

public class DownloadStatus {
    public static final int SIZE_UNKNOWN = -1;
    private boolean cancelled;
    private Date completionDate;
    private boolean done;
    private final long feedfileId;
    private final int feedfileType;
    private long id;
    private DownloadError reason;
    private String reasonDetailed;
    private boolean successful;
    private final String title;

    private DownloadStatus(long id, String title, long feedfileId, int feedfileType, boolean successful, DownloadError reason, Date completionDate, String reasonDetailed) {
        this.id = id;
        this.title = title;
        this.done = true;
        this.feedfileId = feedfileId;
        this.reason = reason;
        this.successful = successful;
        this.completionDate = (Date) completionDate.clone();
        this.reasonDetailed = reasonDetailed;
        this.feedfileType = feedfileType;
    }

    public DownloadStatus(@NonNull DownloadRequest request, DownloadError reason, boolean successful, boolean cancelled, String reasonDetailed) {
        this.title = request.getTitle();
        this.feedfileId = request.getFeedfileId();
        this.feedfileType = request.getFeedfileType();
        this.reason = reason;
        this.successful = successful;
        this.cancelled = cancelled;
        this.reasonDetailed = reasonDetailed;
        this.completionDate = new Date();
    }

    public DownloadStatus(@NonNull FeedFile feedfile, String title, DownloadError reason, boolean successful, String reasonDetailed) {
        this.title = title;
        this.done = true;
        this.feedfileId = feedfile.getId();
        this.feedfileType = feedfile.getTypeAsInt();
        this.reason = reason;
        this.successful = successful;
        this.completionDate = new Date();
        this.reasonDetailed = reasonDetailed;
    }

    public DownloadStatus(long feedfileId, int feedfileType, String title, DownloadError reason, boolean successful, String reasonDetailed) {
        this.title = title;
        this.done = true;
        this.feedfileId = feedfileId;
        this.feedfileType = feedfileType;
        this.reason = reason;
        this.successful = successful;
        this.completionDate = new Date();
        this.reasonDetailed = reasonDetailed;
    }

    public static DownloadStatus fromCursor(Cursor cursor) {
        Cursor cursor2 = cursor;
        int indexId = cursor2.getColumnIndex("id");
        int indexTitle = cursor2.getColumnIndex("title");
        int indexFeedFile = cursor2.getColumnIndex(PodDBAdapter.KEY_FEEDFILE);
        int indexFileFileType = cursor2.getColumnIndex(PodDBAdapter.KEY_FEEDFILETYPE);
        int indexSuccessful = cursor2.getColumnIndex(PodDBAdapter.KEY_SUCCESSFUL);
        int indexReason = cursor2.getColumnIndex(PodDBAdapter.KEY_REASON);
        int indexCompletionDate = cursor2.getColumnIndex(PodDBAdapter.KEY_COMPLETION_DATE);
        int indexReasonDetailed = cursor2.getColumnIndex(PodDBAdapter.KEY_REASON_DETAILED);
        return new DownloadStatus(cursor2.getLong(indexId), cursor2.getString(indexTitle), cursor2.getLong(indexFeedFile), cursor2.getInt(indexFileFileType), cursor2.getInt(indexSuccessful) > 0, DownloadError.fromCode(cursor2.getInt(indexReason)), new Date(cursor2.getLong(indexCompletionDate)), cursor2.getString(indexReasonDetailed));
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DownloadStatus [id=");
        stringBuilder.append(this.id);
        stringBuilder.append(", title=");
        stringBuilder.append(this.title);
        stringBuilder.append(", reason=");
        stringBuilder.append(this.reason);
        stringBuilder.append(", reasonDetailed=");
        stringBuilder.append(this.reasonDetailed);
        stringBuilder.append(", successful=");
        stringBuilder.append(this.successful);
        stringBuilder.append(", completionDate=");
        stringBuilder.append(this.completionDate);
        stringBuilder.append(", feedfileId=");
        stringBuilder.append(this.feedfileId);
        stringBuilder.append(", feedfileType=");
        stringBuilder.append(this.feedfileType);
        stringBuilder.append(", done=");
        stringBuilder.append(this.done);
        stringBuilder.append(", cancelled=");
        stringBuilder.append(this.cancelled);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public DownloadError getReason() {
        return this.reason;
    }

    public String getReasonDetailed() {
        return this.reasonDetailed;
    }

    public boolean isSuccessful() {
        return this.successful;
    }

    public Date getCompletionDate() {
        return (Date) this.completionDate.clone();
    }

    public long getFeedfileId() {
        return this.feedfileId;
    }

    public int getFeedfileType() {
        return this.feedfileType;
    }

    public boolean isDone() {
        return this.done;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setSuccessful() {
        this.successful = true;
        this.reason = DownloadError.SUCCESS;
        this.done = true;
    }

    public void setFailed(DownloadError reason, String reasonDetailed) {
        this.successful = false;
        this.reason = reason;
        this.reasonDetailed = reasonDetailed;
        this.done = true;
    }

    public void setCancelled() {
        this.successful = false;
        this.reason = DownloadError.ERROR_DOWNLOAD_CANCELLED;
        this.done = true;
        this.cancelled = true;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = (Date) completionDate.clone();
    }

    public void setId(long id) {
        this.id = id;
    }
}

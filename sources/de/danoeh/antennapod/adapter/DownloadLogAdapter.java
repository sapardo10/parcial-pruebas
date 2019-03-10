package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.joanzapata.iconify.widget.IconButton;
import com.joanzapata.iconify.widget.IconTextView;
import de.danoeh.antennapod.core.dialog.DownloadRequestErrorDialogCreator;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.service.download.DownloadStatus;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.storage.DownloadRequestException;
import de.danoeh.antennapod.debug.R;

public class DownloadLogAdapter extends BaseAdapter {
    private static final String TAG = "DownloadLogAdapter";
    private final OnClickListener clickListener = new C07291();
    private final Context context;
    private final ItemAccess itemAccess;

    /* renamed from: de.danoeh.antennapod.adapter.DownloadLogAdapter$1 */
    class C07291 implements OnClickListener {
        C07291() {
        }

        public void onClick(View v) {
            ButtonHolder holder = (ButtonHolder) v.getTag();
            String str;
            StringBuilder stringBuilder;
            if (holder.typeId == 0) {
                Feed feed = DBReader.getFeed(holder.id);
                if (feed != null) {
                    try {
                        DBTasks.forceRefreshFeed(DownloadLogAdapter.this.context, feed);
                    } catch (DownloadRequestException e) {
                        e.printStackTrace();
                    }
                } else {
                    str = DownloadLogAdapter.TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Could not find feed for feed id: ");
                    stringBuilder.append(holder.id);
                    Log.wtf(str, stringBuilder.toString());
                }
            } else if (holder.typeId == 2) {
                if (DBReader.getFeedMedia(holder.id) != null) {
                    try {
                        DBTasks.downloadFeedItems(DownloadLogAdapter.this.context, media.getItem());
                        Toast.makeText(DownloadLogAdapter.this.context, R.string.status_downloading_label, 0).show();
                    } catch (DownloadRequestException e2) {
                        e2.printStackTrace();
                        DownloadRequestErrorDialogCreator.newRequestErrorDialog(DownloadLogAdapter.this.context, e2.getMessage());
                    }
                } else {
                    str = DownloadLogAdapter.TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Could not find media for id: ");
                    stringBuilder.append(holder.id);
                    Log.wtf(str, stringBuilder.toString());
                }
            } else {
                String str2 = DownloadLogAdapter.TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Unexpected type id: ");
                stringBuilder2.append(holder.typeId);
                Log.wtf(str2, stringBuilder2.toString());
            }
            v.setVisibility(8);
        }
    }

    static class ButtonHolder {
        long id;
        int typeId;

        ButtonHolder() {
        }
    }

    static class Holder {
        TextView date;
        IconTextView icon;
        TextView reason;
        IconButton retry;
        TextView title;
        TextView type;

        Holder() {
        }
    }

    public interface ItemAccess {
        int getCount();

        DownloadStatus getItem(int i);
    }

    public DownloadLogAdapter(Context context, ItemAccess itemAccess) {
        this.itemAccess = itemAccess;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        DownloadStatus status = getItem(position);
        if (convertView == null) {
            holder = new Holder();
            convertView = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.downloadlog_item, parent, false);
            holder.icon = (IconTextView) convertView.findViewById(R.id.txtvIcon);
            holder.retry = (IconButton) convertView.findViewById(R.id.btnRetry);
            holder.date = (TextView) convertView.findViewById(R.id.txtvDate);
            holder.title = (TextView) convertView.findViewById(R.id.txtvTitle);
            if (VERSION.SDK_INT >= 23) {
                holder.title.setHyphenationFrequency(2);
            }
            holder.type = (TextView) convertView.findViewById(R.id.txtvType);
            holder.reason = (TextView) convertView.findViewById(R.id.txtvReason);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (status.getFeedfileType() == 0) {
            holder.type.setText(R.string.download_type_feed);
        } else if (status.getFeedfileType() == 2) {
            holder.type.setText(R.string.download_type_media);
        }
        if (status.getTitle() != null) {
            holder.title.setText(status.getTitle());
        } else {
            holder.title.setText(R.string.download_log_title_unknown);
        }
        holder.date.setText(DateUtils.getRelativeTimeSpanString(status.getCompletionDate().getTime(), System.currentTimeMillis(), 0, 0));
        if (status.isSuccessful()) {
            holder.icon.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color.download_success_green));
            holder.icon.setText("{fa-check-circle}");
            holder.retry.setVisibility(8);
            holder.reason.setVisibility(8);
        } else {
            holder.icon.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color.download_failed_red));
            holder.icon.setText("{fa-times-circle}");
            String reasonText = status.getReason().getErrorString(this.context);
            if (status.getReasonDetailed() != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(reasonText);
                stringBuilder.append(": ");
                stringBuilder.append(status.getReasonDetailed());
                reasonText = stringBuilder.toString();
            }
            holder.reason.setText(reasonText);
            holder.reason.setVisibility(0);
            if (newerWasSuccessful(position, status.getFeedfileType(), status.getFeedfileId())) {
                holder.retry.setVisibility(8);
                holder.retry.setOnClickListener(null);
                holder.retry.setTag(null);
            } else {
                ButtonHolder btnHolder;
                holder.retry.setVisibility(0);
                holder.retry.setOnClickListener(this.clickListener);
                if (holder.retry.getTag() != null) {
                    btnHolder = (ButtonHolder) holder.retry.getTag();
                } else {
                    btnHolder = new ButtonHolder();
                }
                btnHolder.typeId = status.getFeedfileType();
                btnHolder.id = status.getFeedfileId();
                holder.retry.setTag(btnHolder);
            }
        }
        return convertView;
    }

    private boolean newerWasSuccessful(int position, int feedTypeId, long id) {
        for (int i = 0; i < position; i++) {
            DownloadStatus status = getItem(i);
            if (status.getFeedfileType() == feedTypeId && status.getFeedfileId() == id) {
                if (status.isSuccessful()) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getCount() {
        return this.itemAccess.getCount();
    }

    public DownloadStatus getItem(int position) {
        return this.itemAccess.getItem(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }
}

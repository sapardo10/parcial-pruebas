package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.danoeh.antennapod.core.service.download.DownloadRequest;
import de.danoeh.antennapod.core.service.download.Downloader;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.ThemeUtils;
import de.danoeh.antennapod.debug.R;

public class DownloadlistAdapter extends BaseAdapter {
    private static final int SELECTION_NONE = -1;
    private final OnClickListener butSecondaryListener = new C07311();
    private final Context context;
    private final ItemAccess itemAccess;
    private int selectedItemIndex = -1;

    /* renamed from: de.danoeh.antennapod.adapter.DownloadlistAdapter$1 */
    class C07311 implements OnClickListener {
        C07311() {
        }

        public void onClick(View v) {
            DownloadlistAdapter.this.itemAccess.onSecondaryActionClick((Downloader) v.getTag());
        }
    }

    static class Holder {
        ImageButton butSecondary;
        TextView downloaded;
        TextView percent;
        ProgressBar progbar;
        TextView title;

        Holder() {
        }
    }

    public interface ItemAccess {
        int getCount();

        Downloader getItem(int i);

        void onSecondaryActionClick(Downloader downloader);
    }

    public DownloadlistAdapter(Context context, ItemAccess itemAccess) {
        this.context = context;
        this.itemAccess = itemAccess;
    }

    public int getCount() {
        return this.itemAccess.getCount();
    }

    public Downloader getItem(int position) {
        return this.itemAccess.getItem(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        Downloader downloader = getItem(position);
        DownloadRequest request = downloader.getDownloadRequest();
        if (convertView == null) {
            holder = new Holder();
            convertView = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.downloadlist_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.txtvTitle);
            holder.downloaded = (TextView) convertView.findViewById(R.id.txtvDownloaded);
            holder.percent = (TextView) convertView.findViewById(R.id.txtvPercent);
            holder.progbar = (ProgressBar) convertView.findViewById(R.id.progProgress);
            holder.butSecondary = (ImageButton) convertView.findViewById(R.id.butSecondaryAction);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (position == this.selectedItemIndex) {
            convertView.setBackgroundColor(ContextCompat.getColor(convertView.getContext(), ThemeUtils.getSelectionBackgroundColor()));
        } else {
            convertView.setBackgroundResource(0);
        }
        holder.title.setText(request.getTitle());
        holder.progbar.setIndeterminate(request.getSoFar() <= 0);
        String strDownloaded = Converter.byteToString(request.getSoFar());
        if (request.getSize() != -1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(strDownloaded);
            stringBuilder.append(" / ");
            stringBuilder.append(Converter.byteToString(request.getSize()));
            strDownloaded = stringBuilder.toString();
            TextView textView = holder.percent;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(request.getProgressPercent());
            stringBuilder2.append("%");
            textView.setText(stringBuilder2.toString());
            holder.progbar.setProgress(request.getProgressPercent());
            holder.percent.setVisibility(0);
        } else {
            holder.progbar.setProgress(0);
            holder.percent.setVisibility(4);
        }
        holder.downloaded.setText(strDownloaded);
        holder.butSecondary.setFocusable(false);
        holder.butSecondary.setTag(downloader);
        holder.butSecondary.setOnClickListener(this.butSecondaryListener);
        return convertView;
    }
}

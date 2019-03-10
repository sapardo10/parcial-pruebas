package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.core.storage.DBReader.StatisticsItem;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.debug.R;
import java.util.ArrayList;
import java.util.List;

public class StatisticsListAdapter extends BaseAdapter {
    private final Context context;
    private boolean countAll = true;
    private List<StatisticsItem> feedTime = new ArrayList();

    static class StatisticsHolder {
        ImageView image;
        TextView time;
        TextView title;

        StatisticsHolder() {
        }
    }

    public StatisticsListAdapter(Context context) {
        this.context = context;
    }

    public void setCountAll(boolean countAll) {
        this.countAll = countAll;
    }

    public int getCount() {
        return this.feedTime.size();
    }

    public StatisticsItem getItem(int position) {
        return (StatisticsItem) this.feedTime.get(position);
    }

    public long getItemId(int position) {
        return ((StatisticsItem) this.feedTime.get(position)).feed.getId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        StatisticsHolder holder;
        long j;
        Feed feed = ((StatisticsItem) this.feedTime.get(position)).feed;
        if (convertView == null) {
            holder = new StatisticsHolder();
            convertView = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.statistics_listitem, parent, false);
            holder.image = (ImageView) convertView.findViewById(R.id.imgvCover);
            holder.title = (TextView) convertView.findViewById(R.id.txtvTitle);
            holder.time = (TextView) convertView.findViewById(R.id.txtvTime);
            convertView.setTag(holder);
        } else {
            holder = (StatisticsHolder) convertView.getTag();
        }
        Glide.with(this.context).load(feed.getImageLocation()).apply(new RequestOptions().placeholder((int) R.color.light_gray).error((int) R.color.light_gray).diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).fitCenter().dontAnimate()).into(holder.image);
        holder.title.setText(feed.getTitle());
        TextView textView = holder.time;
        Context context = this.context;
        if (this.countAll) {
            j = ((StatisticsItem) this.feedTime.get(position)).timePlayedCountAll;
        } else {
            j = ((StatisticsItem) this.feedTime.get(position)).timePlayed;
        }
        textView.setText(Converter.shortLocalizedDuration(context, j));
        return convertView;
    }

    public void update(List<StatisticsItem> feedTime) {
        this.feedTime = feedTime;
        notifyDataSetChanged();
    }
}

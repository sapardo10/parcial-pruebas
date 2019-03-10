package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedItem.State;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.DateUtils;
import de.danoeh.antennapod.debug.R;

public class DownloadedEpisodesListAdapter extends BaseAdapter {
    private final Context context;
    private final ItemAccess itemAccess;
    private final OnClickListener secondaryActionListener = new C07301();

    /* renamed from: de.danoeh.antennapod.adapter.DownloadedEpisodesListAdapter$1 */
    class C07301 implements OnClickListener {
        C07301() {
        }

        public void onClick(View v) {
            DownloadedEpisodesListAdapter.this.itemAccess.onFeedItemSecondaryAction((FeedItem) v.getTag());
        }
    }

    static class Holder {
        ImageButton butSecondary;
        ImageView imageView;
        TextView pubDate;
        ImageView queueStatus;
        TextView title;
        TextView txtvSize;

        Holder() {
        }
    }

    public interface ItemAccess {
        int getCount();

        FeedItem getItem(int i);

        void onFeedItemSecondaryAction(FeedItem feedItem);
    }

    public DownloadedEpisodesListAdapter(Context context, ItemAccess itemAccess) {
        this.context = context;
        this.itemAccess = itemAccess;
    }

    public int getCount() {
        return this.itemAccess.getCount();
    }

    public FeedItem getItem(int position) {
        return this.itemAccess.getItem(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        FeedItem item = getItem(position);
        if (item == null) {
            return null;
        }
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.downloaded_episodeslist_item, parent, false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imgvImage);
            holder.title = (TextView) convertView.findViewById(R.id.txtvTitle);
            if (VERSION.SDK_INT >= 23) {
                holder.title.setHyphenationFrequency(2);
            }
            holder.txtvSize = (TextView) convertView.findViewById(R.id.txtvSize);
            holder.queueStatus = (ImageView) convertView.findViewById(R.id.imgvInPlaylist);
            holder.pubDate = (TextView) convertView.findViewById(R.id.txtvPublished);
            holder.butSecondary = (ImageButton) convertView.findViewById(R.id.butSecondaryAction);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        Glide.with(this.context).load(item.getImageLocation()).apply(new RequestOptions().placeholder((int) R.color.light_gray).error((int) R.color.light_gray).diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).fitCenter().dontAnimate()).into(holder.imageView);
        if (item.isPlayed()) {
            convertView.setAlpha(0.5f);
        } else {
            convertView.setAlpha(1.0f);
        }
        holder.title.setText(item.getTitle());
        holder.txtvSize.setText(Converter.byteToString(item.getMedia().getSize()));
        holder.queueStatus.setVisibility(item.isTagged(FeedItem.TAG_QUEUE) ? 0 : 8);
        holder.pubDate.setText(DateUtils.formatAbbrev(this.context, item.getPubDate()));
        if (item.getState() == State.PLAYING && PlaybackService.isRunning) {
            holder.butSecondary.setEnabled(false);
            holder.butSecondary.setAlpha(0.5f);
        } else {
            holder.butSecondary.setEnabled(true);
            holder.butSecondary.setAlpha(1.0f);
        }
        holder.butSecondary.setFocusable(false);
        holder.butSecondary.setTag(item);
        holder.butSecondary.setOnClickListener(this.secondaryActionListener);
        return convertView;
    }
}

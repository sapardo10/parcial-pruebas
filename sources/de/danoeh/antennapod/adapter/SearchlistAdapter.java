package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedComponent;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.SearchResult;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.debug.R;

public class SearchlistAdapter extends BaseAdapter {
    private final Context context;
    private final ItemAccess itemAccess;

    static class Holder {
        ImageView cover;
        TextView subtitle;
        TextView title;

        Holder() {
        }
    }

    public interface ItemAccess {
        int getCount();

        SearchResult getItem(int i);
    }

    public SearchlistAdapter(Context context, ItemAccess itemAccess) {
        this.context = context;
        this.itemAccess = itemAccess;
    }

    public int getCount() {
        return this.itemAccess.getCount();
    }

    public SearchResult getItem(int position) {
        return this.itemAccess.getItem(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        SearchResult result = getItem(position);
        FeedComponent component = result.getComponent();
        if (convertView == null) {
            holder = new Holder();
            convertView = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.searchlist_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.txtvTitle);
            if (VERSION.SDK_INT >= 23) {
                holder.title.setHyphenationFrequency(2);
            }
            holder.cover = (ImageView) convertView.findViewById(R.id.imgvFeedimage);
            holder.subtitle = (TextView) convertView.findViewById(R.id.txtvSubtitle);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (component.getClass() == Feed.class) {
            Feed feed = (Feed) component;
            holder.title.setText(feed.getTitle());
            holder.subtitle.setVisibility(8);
            Glide.with(this.context).load(feed.getImageLocation()).apply(new RequestOptions().placeholder((int) R.color.light_gray).error((int) R.color.light_gray).diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).fitCenter().dontAnimate()).into(holder.cover);
        } else if (component.getClass() == FeedItem.class) {
            FeedItem item = (FeedItem) component;
            holder.title.setText(item.getTitle());
            if (result.getSubtitle() != null) {
                holder.subtitle.setVisibility(0);
                holder.subtitle.setText(result.getSubtitle());
            }
            convertView.setAlpha(item.isPlayed() ? 0.5f : 1.0f);
            Glide.with(this.context).load(item.getFeed().getImageLocation()).apply(new RequestOptions().placeholder((int) R.color.light_gray).error((int) R.color.light_gray).diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).fitCenter().dontAnimate()).into(holder.cover);
            return convertView;
        }
        return convertView;
    }
}

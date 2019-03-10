package de.danoeh.antennapod.adapter;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.fragment.AddFeedFragment;
import de.danoeh.antennapod.fragment.ItemlistFragment;
import java.lang.ref.WeakReference;
import jp.shts.android.library.TriangleLabelView;

public class SubscriptionsAdapter extends BaseAdapter implements OnItemClickListener {
    public static final Object ADD_ITEM_OBJ = new Object();
    private static final int ADD_POSITION = -1;
    private static final String TAG = "SubscriptionsAdapter";
    private final ItemAccess itemAccess;
    private final WeakReference<MainActivity> mainActivityRef;

    static class Holder {
        public TriangleLabelView count;
        public TextView feedTitle;
        public ImageView imageView;

        Holder() {
        }
    }

    public interface ItemAccess {
        int getCount();

        int getFeedCounter(long j);

        Feed getItem(int i);
    }

    public SubscriptionsAdapter(MainActivity mainActivity, ItemAccess itemAccess) {
        this.mainActivityRef = new WeakReference(mainActivity);
        this.itemAccess = itemAccess;
    }

    private int getAddTilePosition() {
        return getCount() - 1;
    }

    private int getAdjustedPosition(int origPosition) {
        return origPosition < getAddTilePosition() ? origPosition : origPosition - 1;
    }

    public int getCount() {
        return this.itemAccess.getCount() + 1;
    }

    public Object getItem(int position) {
        if (position == getAddTilePosition()) {
            return ADD_ITEM_OBJ;
        }
        return this.itemAccess.getItem(getAdjustedPosition(position));
    }

    public boolean hasStableIds() {
        return true;
    }

    public long getItemId(int position) {
        if (position == getAddTilePosition()) {
            return 0;
        }
        return this.itemAccess.getItem(getAdjustedPosition(position)).getId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = ((LayoutInflater) ((MainActivity) this.mainActivityRef.get()).getSystemService("layout_inflater")).inflate(R.layout.subscription_item, parent, false);
            holder.feedTitle = (TextView) convertView.findViewById(R.id.txtvTitle);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imgvCover);
            holder.count = (TriangleLabelView) convertView.findViewById(R.id.triangleCountView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (position == getAddTilePosition()) {
            TextView textView = holder.feedTitle;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{md-add 500%}\n\n");
            stringBuilder.append(((MainActivity) this.mainActivityRef.get()).getString(R.string.add_feed_label));
            textView.setText(stringBuilder.toString());
            holder.feedTitle.setVisibility(0);
            holder.count.setPrimaryText("");
            holder.count.setVisibility(4);
            Glide.with((FragmentActivity) this.mainActivityRef.get()).clear(holder.imageView);
            return convertView;
        }
        Feed feed = (Feed) getItem(position);
        if (feed == null) {
            return null;
        }
        holder.feedTitle.setText(feed.getTitle());
        holder.feedTitle.setVisibility(0);
        if (this.itemAccess.getFeedCounter(feed.getId()) > 0) {
            holder.count.setPrimaryText(String.valueOf(this.itemAccess.getFeedCounter(feed.getId())));
            holder.count.setVisibility(0);
        } else {
            holder.count.setVisibility(8);
        }
        new CoverLoader((MainActivity) this.mainActivityRef.get()).withUri(feed.getImageLocation()).withPlaceholderView(holder.feedTitle).withCoverView(holder.imageView).withError(R.color.light_gray).load();
        return convertView;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (position == getAddTilePosition()) {
            ((MainActivity) this.mainActivityRef.get()).loadChildFragment(new AddFeedFragment());
            return;
        }
        ((MainActivity) this.mainActivityRef.get()).loadChildFragment(ItemlistFragment.newInstance(getItemId(position)));
    }
}

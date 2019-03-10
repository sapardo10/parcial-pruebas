package de.danoeh.antennapod.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.widget.IconTextView;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.fragment.DownloadsFragment;
import de.danoeh.antennapod.fragment.EpisodesFragment;
import de.danoeh.antennapod.fragment.QueueFragment;
import de.danoeh.antennapod.fragment.SubscriptionFragment;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class NavListAdapter extends BaseAdapter implements OnSharedPreferenceChangeListener {
    public static final String SUBSCRIPTION_LIST_TAG = "SubscriptionList";
    private static final int VIEW_TYPE_COUNT = 3;
    public static final int VIEW_TYPE_NAV = 0;
    public static final int VIEW_TYPE_SECTION_DIVIDER = 1;
    private static final int VIEW_TYPE_SUBSCRIPTION = 2;
    private static List<String> tags;
    private static String[] titles;
    private final WeakReference<Activity> activity;
    private final ItemAccess itemAccess;
    private boolean showSubscriptionList = true;

    static class FeedHolder {
        TextView count;
        IconTextView failure;
        ImageView image;
        TextView title;

        FeedHolder() {
        }
    }

    public interface ItemAccess {
        int getCount();

        int getFeedCounter(long j);

        int getFeedCounterSum();

        Feed getItem(int i);

        int getNumberOfDownloadedItems();

        int getNumberOfNewItems();

        int getQueueSize();

        int getReclaimableItems();

        int getSelectedItemIndex();
    }

    static class NavHolder {
        TextView count;
        ImageView image;
        TextView title;

        NavHolder() {
        }
    }

    public NavListAdapter(ItemAccess itemAccess, Activity context) {
        this.itemAccess = itemAccess;
        this.activity = new WeakReference(context);
        titles = context.getResources().getStringArray(R.array.nav_drawer_titles);
        loadItems();
        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(UserPreferences.PREF_HIDDEN_DRAWER_ITEMS)) {
            loadItems();
        }
    }

    private void loadItems() {
        List<String> newTags = new ArrayList(Arrays.asList(MainActivity.NAV_DRAWER_TAGS));
        newTags.removeAll(UserPreferences.getHiddenDrawerItems());
        if (newTags.contains(SUBSCRIPTION_LIST_TAG)) {
            this.showSubscriptionList = true;
            newTags.remove(SUBSCRIPTION_LIST_TAG);
        } else {
            this.showSubscriptionList = false;
        }
        tags = newTags;
        notifyDataSetChanged();
    }

    public String getLabel(String tag) {
        return titles[ArrayUtils.indexOf(MainActivity.NAV_DRAWER_TAGS, tag)];
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.drawable.Drawable getDrawable(java.lang.String r7) {
        /*
        r6 = this;
        r0 = r6.activity;
        r0 = r0.get();
        r0 = (android.app.Activity) r0;
        r1 = 0;
        if (r0 != 0) goto L_0x000c;
    L_0x000b:
        return r1;
    L_0x000c:
        r2 = -1;
        r3 = r7.hashCode();
        r4 = 1;
        r5 = 0;
        switch(r3) {
            case -1578080595: goto L_0x005d;
            case -1205705240: goto L_0x0053;
            case -58242769: goto L_0x0049;
            case 28587112: goto L_0x003f;
            case 378123323: goto L_0x0035;
            case 791417833: goto L_0x002b;
            case 2051192649: goto L_0x0021;
            case 2146299489: goto L_0x0017;
            default: goto L_0x0016;
        };
    L_0x0016:
        goto L_0x0066;
    L_0x0017:
        r3 = "QueueFragment";
        r3 = r7.equals(r3);
        if (r3 == 0) goto L_0x0016;
    L_0x001f:
        r2 = 0;
        goto L_0x0066;
    L_0x0021:
        r3 = "PlaybackHistoryFragment";
        r3 = r7.equals(r3);
        if (r3 == 0) goto L_0x0016;
    L_0x0029:
        r2 = 5;
        goto L_0x0066;
    L_0x002b:
        r3 = "AllEpisodesFragment";
        r3 = r7.equals(r3);
        if (r3 == 0) goto L_0x0016;
    L_0x0033:
        r2 = 3;
        goto L_0x0066;
    L_0x0035:
        r3 = "DownloadsFragment";
        r3 = r7.equals(r3);
        if (r3 == 0) goto L_0x0016;
    L_0x003d:
        r2 = 4;
        goto L_0x0066;
    L_0x003f:
        r3 = "EpisodesFragment";
        r3 = r7.equals(r3);
        if (r3 == 0) goto L_0x0016;
    L_0x0047:
        r2 = 2;
        goto L_0x0066;
    L_0x0049:
        r3 = "AddFeedFragment";
        r3 = r7.equals(r3);
        if (r3 == 0) goto L_0x0016;
    L_0x0051:
        r2 = 7;
        goto L_0x0066;
    L_0x0053:
        r3 = "NewEpisodesFragment";
        r3 = r7.equals(r3);
        if (r3 == 0) goto L_0x0016;
    L_0x005b:
        r2 = 1;
        goto L_0x0066;
    L_0x005d:
        r3 = "SubscriptionFragment";
        r3 = r7.equals(r3);
        if (r3 == 0) goto L_0x0016;
    L_0x0065:
        r2 = 6;
    L_0x0066:
        switch(r2) {
            case 0: goto L_0x0086;
            case 1: goto L_0x0082;
            case 2: goto L_0x007e;
            case 3: goto L_0x007a;
            case 4: goto L_0x0076;
            case 5: goto L_0x0072;
            case 6: goto L_0x006e;
            case 7: goto L_0x006a;
            default: goto L_0x0069;
        };
    L_0x0069:
        return r1;
    L_0x006a:
        r1 = 2130968750; // 0x7f0400ae float:1.7546162E38 double:1.0528384517E-314;
        goto L_0x008a;
    L_0x006e:
        r1 = 2130968875; // 0x7f04012b float:1.7546416E38 double:1.0528385135E-314;
        goto L_0x008a;
    L_0x0072:
        r1 = 2130968876; // 0x7f04012c float:1.7546418E38 double:1.052838514E-314;
        goto L_0x008a;
    L_0x0076:
        r1 = 2130968642; // 0x7f040042 float:1.7545943E38 double:1.0528383984E-314;
        goto L_0x008a;
    L_0x007a:
        r1 = 2130968832; // 0x7f040100 float:1.7546329E38 double:1.0528384922E-314;
        goto L_0x008a;
    L_0x007e:
        r1 = 2130968832; // 0x7f040100 float:1.7546329E38 double:1.0528384922E-314;
        goto L_0x008a;
    L_0x0082:
        r1 = 2130968881; // 0x7f040131 float:1.7546428E38 double:1.0528385165E-314;
        goto L_0x008a;
    L_0x0086:
        r1 = 2130969185; // 0x7f040261 float:1.7547045E38 double:1.0528386667E-314;
    L_0x008a:
        r2 = new int[r4];
        r2[r5] = r1;
        r2 = r0.obtainStyledAttributes(r2);
        r3 = r2.getDrawable(r5);
        r2.recycle();
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.adapter.NavListAdapter.getDrawable(java.lang.String):android.graphics.drawable.Drawable");
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public int getCount() {
        int baseCount = getSubscriptionOffset();
        if (this.showSubscriptionList) {
            return baseCount + this.itemAccess.getCount();
        }
        return baseCount;
    }

    public Object getItem(int position) {
        int viewType = getItemViewType(position);
        if (viewType == 0) {
            return getLabel((String) tags.get(position));
        }
        if (viewType == 1) {
            return "";
        }
        return this.itemAccess.getItem(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int getItemViewType(int position) {
        if (position >= 0 && position < tags.size()) {
            return 0;
        }
        if (position < getSubscriptionOffset()) {
            return 1;
        }
        return 2;
    }

    public int getViewTypeCount() {
        return 3;
    }

    public int getSubscriptionOffset() {
        return tags.size() > 0 ? tags.size() + 1 : 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        int viewType = getItemViewType(position);
        if (viewType == 0) {
            v = getNavView((String) getItem(position), position, convertView, parent);
        } else if (viewType == 1) {
            v = getSectionDividerView(convertView, parent);
        } else {
            v = getFeedView(position, convertView, parent);
        }
        if (v != null && viewType != 1) {
            TextView txtvTitle = (TextView) v.findViewById(R.id.txtvTitle);
            TypedValue typedValue = new TypedValue();
            if (position == this.itemAccess.getSelectedItemIndex()) {
                txtvTitle.setTypeface(null, 1);
                v.getContext().getTheme().resolveAttribute(R.attr.drawer_activated_color, typedValue, true);
                v.setBackgroundResource(typedValue.resourceId);
            } else {
                txtvTitle.setTypeface(null, 0);
                v.getContext().getTheme().resolveAttribute(R.attr.nav_drawer_background, typedValue, true);
                v.setBackgroundResource(typedValue.resourceId);
            }
        }
        return v;
    }

    private View getNavView(String title, int position, View convertView, ViewGroup parent) {
        Activity context = (Activity) this.activity.get();
        if (context == null) {
            return null;
        }
        NavHolder holder;
        if (convertView == null) {
            holder = new NavHolder();
            convertView = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.nav_listitem, parent, false);
            holder.image = (ImageView) convertView.findViewById(R.id.imgvCover);
            holder.title = (TextView) convertView.findViewById(R.id.txtvTitle);
            holder.count = (TextView) convertView.findViewById(R.id.txtvCount);
            convertView.setTag(holder);
        } else {
            holder = (NavHolder) convertView.getTag();
        }
        holder.title.setText(title);
        holder.count.setVisibility(8);
        holder.count.setOnClickListener(null);
        String tag = (String) tags.get(position);
        int queueSize;
        if (tag.equals(QueueFragment.TAG)) {
            queueSize = this.itemAccess.getQueueSize();
            if (queueSize > 0) {
                holder.count.setText(String.valueOf(queueSize));
                holder.count.setVisibility(0);
            }
        } else if (tag.equals(EpisodesFragment.TAG)) {
            queueSize = this.itemAccess.getNumberOfNewItems();
            if (queueSize > 0) {
                holder.count.setText(String.valueOf(queueSize));
                holder.count.setVisibility(0);
            }
        } else if (tag.equals(SubscriptionFragment.TAG)) {
            queueSize = this.itemAccess.getFeedCounterSum();
            if (queueSize > 0) {
                holder.count.setText(String.valueOf(queueSize));
                holder.count.setVisibility(0);
            }
        } else if (tag.equals(DownloadsFragment.TAG) && UserPreferences.isEnableAutodownload()) {
            queueSize = UserPreferences.getEpisodeCacheSize();
            int spaceUsed = this.itemAccess.getNumberOfDownloadedItems() - this.itemAccess.getReclaimableItems();
            if (queueSize > 0 && spaceUsed >= queueSize) {
                holder.count.setText("{md-disc-full 150%}");
                Iconify.addIcons(new TextView[]{holder.count});
                holder.count.setVisibility(0);
                holder.count.setOnClickListener(new -$$Lambda$NavListAdapter$-oN_B3f0EONynHCtcnFh7Nhe6D0(context));
            }
        }
        holder.image.setImageDrawable(getDrawable((String) tags.get(position)));
        return convertView;
    }

    static /* synthetic */ void lambda$null$0(DialogInterface dialog, int which) {
    }

    private View getSectionDividerView(View convertView, ViewGroup parent) {
        Activity context = (Activity) this.activity.get();
        if (context == null) {
            return null;
        }
        convertView = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.nav_section_item, parent, false);
        convertView.setEnabled(false);
        convertView.setOnClickListener(null);
        return convertView;
    }

    private View getFeedView(int position, View convertView, ViewGroup parent) {
        Activity context = (Activity) this.activity.get();
        if (context == null) {
            return null;
        }
        FeedHolder holder;
        Feed feed = this.itemAccess.getItem(position - getSubscriptionOffset());
        if (convertView == null) {
            holder = new FeedHolder();
            convertView = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.nav_feedlistitem, parent, false);
            holder.image = (ImageView) convertView.findViewById(R.id.imgvCover);
            holder.title = (TextView) convertView.findViewById(R.id.txtvTitle);
            holder.failure = (IconTextView) convertView.findViewById(R.id.itxtvFailure);
            holder.count = (TextView) convertView.findViewById(R.id.txtvCount);
            convertView.setTag(holder);
        } else {
            holder = (FeedHolder) convertView.getTag();
        }
        Glide.with(context).load(feed.getImageLocation()).apply(new RequestOptions().placeholder((int) R.color.light_gray).error((int) R.color.light_gray).diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).fitCenter().dontAnimate()).into(holder.image);
        holder.title.setText(feed.getTitle());
        if (feed.hasLastUpdateFailed()) {
            ((LayoutParams) holder.title.getLayoutParams()).addRule(0, R.id.itxtvFailure);
            holder.failure.setVisibility(0);
        } else {
            ((LayoutParams) holder.title.getLayoutParams()).addRule(0, R.id.txtvCount);
            holder.failure.setVisibility(8);
        }
        int counter = this.itemAccess.getFeedCounter(feed.getId());
        if (counter > 0) {
            holder.count.setVisibility(0);
            holder.count.setText(String.valueOf(counter));
            if (this.itemAccess.getSelectedItemIndex() == position) {
                holder.count.setTypeface(null, 1);
            } else {
                holder.count.setTypeface(null, 0);
            }
        } else {
            holder.count.setVisibility(8);
        }
        return convertView;
    }
}

package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.joanzapata.iconify.Iconify;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.feed.FeedFile;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedItem.State;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.DateUtils;
import de.danoeh.antennapod.core.util.LongList;
import de.danoeh.antennapod.core.util.NetworkUtils;
import de.danoeh.antennapod.core.util.ThemeUtils;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.fragment.ItemFragment;
import de.danoeh.antennapod.menuhandler.FeedItemMenuHandler;
import de.danoeh.antennapod.menuhandler.FeedItemMenuHandler.MenuInterface;
import java.lang.ref.WeakReference;

public class AllEpisodesRecycleAdapter extends Adapter<Holder> {
    private static final String TAG = AllEpisodesRecycleAdapter.class.getSimpleName();
    private final ActionButtonCallback actionButtonCallback;
    private final ActionButtonUtils actionButtonUtils;
    private final ItemAccess itemAccess;
    private final WeakReference<MainActivity> mainActivityRef;
    private final int normalBackGroundColor;
    private final int playingBackGroundColor;
    private final OnClickListener secondaryActionListener = new C07281();
    private FeedItem selectedItem;
    private final boolean showOnlyNewEpisodes;

    /* renamed from: de.danoeh.antennapod.adapter.AllEpisodesRecycleAdapter$1 */
    class C07281 implements OnClickListener {
        C07281() {
        }

        public void onClick(View v) {
            AllEpisodesRecycleAdapter.this.actionButtonCallback.onActionButtonPressed((FeedItem) v.getTag(), AllEpisodesRecycleAdapter.this.itemAccess.getQueueIds());
        }
    }

    public interface ItemAccess {
        int getCount();

        FeedItem getItem(int i);

        int getItemDownloadProgressPercent(FeedItem feedItem);

        LongList getItemsIds();

        LongList getQueueIds();

        boolean isInQueue(FeedItem feedItem);
    }

    public interface ItemTouchHelperViewHolder {
        void onItemClear();

        void onItemSelected();
    }

    public class Holder extends ViewHolder implements OnClickListener, OnCreateContextMenuListener, ItemTouchHelperViewHolder {
        ImageButton butSecondary;
        FrameLayout container;
        LinearLayout content;
        ImageView cover;
        FeedItem item;
        WeakReference<MainActivity> mainActivityRef;
        TextView placeholder;
        ProgressBar progress;
        TextView pubDate;
        ImageView queueStatus;
        View statusUnread;
        TextView title;
        TextView txtvDuration;

        public Holder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void onClick(View v) {
            MainActivity mainActivity = (MainActivity) this.mainActivityRef.get();
            if (mainActivity != null) {
                mainActivity.loadChildFragment(ItemFragment.newInstance(AllEpisodesRecycleAdapter.this.itemAccess.getItemsIds().toArray(), getAdapterPosition()));
            }
        }

        public void onItemSelected() {
            this.itemView.setAlpha(0.5f);
        }

        public void onItemClear() {
            this.itemView.setAlpha(1.0f);
        }

        public FeedItem getFeedItem() {
            return this.item;
        }

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            FeedItem item = AllEpisodesRecycleAdapter.this.itemAccess.getItem(getAdapterPosition());
            ((MainActivity) this.mainActivityRef.get()).getMenuInflater().inflate(R.menu.allepisodes_context, menu);
            if (item != null) {
                menu.setHeaderTitle(item.getTitle());
            }
            MenuInterface contextMenuInterface = new C1021x80b0a1ef(menu);
            FeedItemMenuHandler.onPrepareMenu(contextMenuInterface, item, true, null);
            contextMenuInterface.setItemVisibility(R.id.mark_as_seen_item, item.isNew());
        }

        static /* synthetic */ void lambda$onCreateContextMenu$0(ContextMenu menu, int id, boolean visible) {
            if (menu != null) {
                MenuItem item1 = menu.findItem(id);
                if (item1 != null) {
                    item1.setVisible(visible);
                }
            }
        }
    }

    public AllEpisodesRecycleAdapter(MainActivity mainActivity, ItemAccess itemAccess, ActionButtonCallback actionButtonCallback, boolean showOnlyNewEpisodes) {
        this.mainActivityRef = new WeakReference(mainActivity);
        this.itemAccess = itemAccess;
        this.actionButtonUtils = new ActionButtonUtils(mainActivity);
        this.actionButtonCallback = actionButtonCallback;
        this.showOnlyNewEpisodes = showOnlyNewEpisodes;
        this.playingBackGroundColor = ThemeUtils.getColorFromAttr(mainActivity, R.attr.currently_playing_background);
        this.normalBackGroundColor = ContextCompat.getColor(mainActivity, 17170445);
    }

    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_episodes_listitem, parent, false);
        Holder holder = new Holder(view);
        holder.container = (FrameLayout) view.findViewById(R.id.container);
        holder.content = (LinearLayout) view.findViewById(R.id.content);
        holder.placeholder = (TextView) view.findViewById(R.id.txtvPlaceholder);
        holder.title = (TextView) view.findViewById(R.id.txtvTitle);
        if (VERSION.SDK_INT >= 23) {
            holder.title.setHyphenationFrequency(2);
        }
        holder.pubDate = (TextView) view.findViewById(R.id.txtvPublished);
        holder.statusUnread = view.findViewById(R.id.statusUnread);
        holder.butSecondary = (ImageButton) view.findViewById(R.id.butSecondaryAction);
        holder.queueStatus = (ImageView) view.findViewById(R.id.imgvInPlaylist);
        holder.progress = (ProgressBar) view.findViewById(R.id.pbar_progress);
        holder.cover = (ImageView) view.findViewById(R.id.imgvCover);
        holder.txtvDuration = (TextView) view.findViewById(R.id.txtvDuration);
        holder.item = null;
        holder.mainActivityRef = this.mainActivityRef;
        view.setTag(holder);
        return holder;
    }

    public void onBindViewHolder(Holder holder, int position) {
        FeedItem item = this.itemAccess.getItem(position);
        if (item != null) {
            FeedFile media;
            boolean isDownloadingMedia;
            State state;
            double position2;
            double duration;
            holder.itemView.setOnLongClickListener(new -$$Lambda$AllEpisodesRecycleAdapter$IRWLtnYZ4oI8csKpZXWsJ1YLjM0(this, item));
            holder.item = item;
            holder.placeholder.setVisibility(0);
            holder.placeholder.setText(item.getFeed().getTitle());
            holder.title.setText(item.getTitle());
            holder.pubDate.setText(DateUtils.formatAbbrev((Context) this.mainActivityRef.get(), item.getPubDate()));
            if (!this.showOnlyNewEpisodes) {
                if (item.isNew()) {
                    holder.statusUnread.setVisibility(0);
                    if (item.isPlayed()) {
                        holder.content.setAlpha(1.0f);
                    } else {
                        holder.content.setAlpha(0.5f);
                    }
                    media = item.getMedia();
                    if (media == null) {
                        isDownloadingMedia = DownloadRequester.getInstance().isDownloadingFile(media);
                        if (media.getDuration() > 0) {
                            holder.txtvDuration.setText(Converter.getDurationStringLong(media.getDuration()));
                        } else if (media.getSize() <= 0) {
                            holder.txtvDuration.setText(Converter.byteToString(media.getSize()));
                        } else if (NetworkUtils.isDownloadAllowed() || media.checkedOnSizeButUnknown()) {
                            holder.txtvDuration.setText("");
                        } else {
                            holder.txtvDuration.setText("{fa-spinner}");
                            Iconify.addIcons(new TextView[]{holder.txtvDuration});
                            NetworkUtils.getFeedMediaSizeObservable(media).subscribe(new -$$Lambda$AllEpisodesRecycleAdapter$7FfYgmvPyIuO9G9rVjfeCcEJk4c(holder), new -$$Lambda$AllEpisodesRecycleAdapter$Ghqnq7AEaj85NI4rKrd0SkhV6xQ(holder));
                        }
                        state = item.getState();
                        if (isDownloadingMedia) {
                            if (state != State.PLAYING) {
                                if (state == State.IN_PROGRESS) {
                                    holder.progress.setVisibility(4);
                                }
                            }
                            if (media.getDuration() > 0) {
                                position2 = (double) media.getPosition();
                                Double.isNaN(position2);
                                position2 *= 100.0d;
                                duration = (double) media.getDuration();
                                Double.isNaN(duration);
                                holder.progress.setProgress((int) (position2 / duration));
                                holder.progress.setVisibility(0);
                            }
                        } else {
                            holder.progress.setVisibility(0);
                            holder.progress.setProgress(this.itemAccess.getItemDownloadProgressPercent(item));
                        }
                        if (media.isCurrentlyPlaying()) {
                            holder.container.setBackgroundColor(this.normalBackGroundColor);
                        } else {
                            holder.container.setBackgroundColor(this.playingBackGroundColor);
                        }
                    } else {
                        holder.progress.setVisibility(4);
                        holder.txtvDuration.setVisibility(8);
                    }
                    isDownloadingMedia = this.itemAccess.isInQueue(item);
                    if (isDownloadingMedia) {
                        holder.queueStatus.setVisibility(4);
                    } else {
                        holder.queueStatus.setVisibility(0);
                    }
                    this.actionButtonUtils.configureActionButton(holder.butSecondary, item, isDownloadingMedia);
                    holder.butSecondary.setFocusable(false);
                    holder.butSecondary.setTag(item);
                    holder.butSecondary.setOnClickListener(this.secondaryActionListener);
                    new CoverLoader((MainActivity) this.mainActivityRef.get()).withUri(item.getImageLocation()).withFallbackUri(item.getFeed().getImageLocation()).withPlaceholderView(holder.placeholder).withCoverView(holder.cover).load();
                }
            }
            holder.statusUnread.setVisibility(4);
            if (item.isPlayed()) {
                holder.content.setAlpha(1.0f);
            } else {
                holder.content.setAlpha(0.5f);
            }
            media = item.getMedia();
            if (media == null) {
                holder.progress.setVisibility(4);
                holder.txtvDuration.setVisibility(8);
            } else {
                isDownloadingMedia = DownloadRequester.getInstance().isDownloadingFile(media);
                if (media.getDuration() > 0) {
                    holder.txtvDuration.setText(Converter.getDurationStringLong(media.getDuration()));
                } else if (media.getSize() <= 0) {
                    if (NetworkUtils.isDownloadAllowed()) {
                    }
                    holder.txtvDuration.setText("");
                } else {
                    holder.txtvDuration.setText(Converter.byteToString(media.getSize()));
                }
                state = item.getState();
                if (isDownloadingMedia) {
                    if (state != State.PLAYING) {
                        if (state == State.IN_PROGRESS) {
                            holder.progress.setVisibility(4);
                        }
                    }
                    if (media.getDuration() > 0) {
                        position2 = (double) media.getPosition();
                        Double.isNaN(position2);
                        position2 *= 100.0d;
                        duration = (double) media.getDuration();
                        Double.isNaN(duration);
                        holder.progress.setProgress((int) (position2 / duration));
                        holder.progress.setVisibility(0);
                    }
                } else {
                    holder.progress.setVisibility(0);
                    holder.progress.setProgress(this.itemAccess.getItemDownloadProgressPercent(item));
                }
                if (media.isCurrentlyPlaying()) {
                    holder.container.setBackgroundColor(this.normalBackGroundColor);
                } else {
                    holder.container.setBackgroundColor(this.playingBackGroundColor);
                }
            }
            isDownloadingMedia = this.itemAccess.isInQueue(item);
            if (isDownloadingMedia) {
                holder.queueStatus.setVisibility(4);
            } else {
                holder.queueStatus.setVisibility(0);
            }
            this.actionButtonUtils.configureActionButton(holder.butSecondary, item, isDownloadingMedia);
            holder.butSecondary.setFocusable(false);
            holder.butSecondary.setTag(item);
            holder.butSecondary.setOnClickListener(this.secondaryActionListener);
            new CoverLoader((MainActivity) this.mainActivityRef.get()).withUri(item.getImageLocation()).withFallbackUri(item.getFeed().getImageLocation()).withPlaceholderView(holder.placeholder).withCoverView(holder.cover).load();
        }
    }

    static /* synthetic */ void lambda$onBindViewHolder$1(Holder holder, Long size) throws Exception {
        if (size.longValue() > 0) {
            holder.txtvDuration.setText(Converter.byteToString(size.longValue()));
        } else {
            holder.txtvDuration.setText("");
        }
    }

    static /* synthetic */ void lambda$onBindViewHolder$2(Holder holder, Throwable error) throws Exception {
        holder.txtvDuration.setText("");
        Log.e(TAG, Log.getStackTraceString(error));
    }

    @Nullable
    public FeedItem getSelectedItem() {
        return this.selectedItem;
    }

    public long getItemId(int position) {
        FeedItem item = this.itemAccess.getItem(position);
        return item != null ? item.getId() : -1;
    }

    public int getItemCount() {
        return this.itemAccess.getCount();
    }
}

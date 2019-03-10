package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.joanzapata.iconify.Iconify;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.feed.FeedFile;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedItem.State;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.DateUtils;
import de.danoeh.antennapod.core.util.LongList;
import de.danoeh.antennapod.core.util.NetworkUtils;
import de.danoeh.antennapod.core.util.ThemeUtils;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.fragment.ItemFragment;
import de.danoeh.antennapod.menuhandler.FeedItemMenuHandler;
import java.lang.ref.WeakReference;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

public class QueueRecyclerAdapter extends Adapter<ViewHolder> {
    private static final String TAG = QueueRecyclerAdapter.class.getSimpleName();
    private final ActionButtonCallback actionButtonCallback;
    private final ActionButtonUtils actionButtonUtils;
    private final ItemAccess itemAccess;
    private final ItemTouchHelper itemTouchHelper;
    private boolean locked;
    private final WeakReference<MainActivity> mainActivity;
    private final int normalBackGroundColor;
    private final int playingBackGroundColor;
    private final OnClickListener secondaryActionListener = new C07331();
    private FeedItem selectedItem;

    /* renamed from: de.danoeh.antennapod.adapter.QueueRecyclerAdapter$1 */
    class C07331 implements OnClickListener {
        C07331() {
        }

        public void onClick(View v) {
            QueueRecyclerAdapter.this.actionButtonCallback.onActionButtonPressed((FeedItem) v.getTag(), QueueRecyclerAdapter.this.itemAccess.getQueueIds());
        }
    }

    public interface ItemAccess {
        int getCount();

        FeedItem getItem(int i);

        int getItemDownloadProgressPercent(FeedItem feedItem);

        long getItemDownloadSize(FeedItem feedItem);

        long getItemDownloadedBytes(FeedItem feedItem);

        LongList getQueueIds();
    }

    public interface ItemTouchHelperViewHolder {
        void onItemClear();

        void onItemSelected();
    }

    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements OnClickListener, OnCreateContextMenuListener, ItemTouchHelperViewHolder {
        private final ImageButton butSecondary;
        private final FrameLayout container;
        private final ImageView cover;
        private final ImageView dragHandle;
        private FeedItem item;
        private final TextView placeholder;
        private final ProgressBar progressBar;
        private final TextView progressLeft;
        private final TextView progressRight;
        private final TextView pubDate;
        private final TextView title;

        public ViewHolder(View v) {
            super(v);
            this.container = (FrameLayout) v.findViewById(R.id.container);
            this.dragHandle = (ImageView) v.findViewById(R.id.drag_handle);
            this.placeholder = (TextView) v.findViewById(R.id.txtvPlaceholder);
            this.cover = (ImageView) v.findViewById(R.id.imgvCover);
            this.title = (TextView) v.findViewById(R.id.txtvTitle);
            if (VERSION.SDK_INT >= 23) {
                this.title.setHyphenationFrequency(2);
            }
            this.pubDate = (TextView) v.findViewById(R.id.txtvPubDate);
            this.progressLeft = (TextView) v.findViewById(R.id.txtvProgressLeft);
            this.progressRight = (TextView) v.findViewById(R.id.txtvProgressRight);
            this.butSecondary = (ImageButton) v.findViewById(R.id.butSecondaryAction);
            this.progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            v.setTag(this);
            v.setOnClickListener(this);
            v.setOnCreateContextMenuListener(this);
            this.dragHandle.setOnTouchListener(new C0727xcd9b0b1f());
        }

        public static /* synthetic */ boolean lambda$new$0(ViewHolder viewHolder, View v1, MotionEvent event) {
            if (MotionEventCompat.getActionMasked(event) == 0) {
                Log.d(QueueRecyclerAdapter.TAG, "startDrag()");
                QueueRecyclerAdapter.this.itemTouchHelper.startDrag(viewHolder);
            }
            return false;
        }

        public void onClick(View v) {
            MainActivity activity = (MainActivity) QueueRecyclerAdapter.this.mainActivity.get();
            if (activity != null) {
                long[] ids = QueueRecyclerAdapter.this.itemAccess.getQueueIds().toArray();
                activity.loadChildFragment(ItemFragment.newInstance(ids, ArrayUtils.indexOf(ids, this.item.getId())));
            }
        }

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            FeedItem item = QueueRecyclerAdapter.this.itemAccess.getItem(getAdapterPosition());
            ((MainActivity) QueueRecyclerAdapter.this.mainActivity.get()).getMenuInflater().inflate(R.menu.queue_context, menu);
            if (item != null) {
                menu.setHeaderTitle(item.getTitle());
            }
            FeedItemMenuHandler.onPrepareMenu(new C1026x158e7f10(menu), item, true, QueueRecyclerAdapter.this.itemAccess.getQueueIds());
        }

        static /* synthetic */ void lambda$onCreateContextMenu$1(ContextMenu menu, int id, boolean visible) {
            if (menu != null) {
                MenuItem item1 = menu.findItem(id);
                if (item1 != null) {
                    item1.setVisible(visible);
                }
            }
        }

        public void onItemSelected() {
            this.itemView.setAlpha(0.5f);
        }

        public void onItemClear() {
            this.itemView.setAlpha(1.0f);
        }

        public void bind(FeedItem item) {
            boolean isDownloadingMedia;
            State state;
            this.item = item;
            if (QueueRecyclerAdapter.this.locked) {
                this.dragHandle.setVisibility(8);
            } else {
                this.dragHandle.setVisibility(0);
            }
            this.placeholder.setText(item.getFeed().getTitle());
            this.title.setText(item.getTitle());
            FeedFile media = item.getMedia();
            this.title.setText(item.getTitle());
            String pubDateStr = DateUtils.formatAbbrev((Context) QueueRecyclerAdapter.this.mainActivity.get(), item.getPubDate());
            int index = 0;
            if (QueueRecyclerAdapter.countMatches(pubDateStr, ' ') != 1) {
                if (QueueRecyclerAdapter.countMatches(pubDateStr, ' ') != 2) {
                    if (QueueRecyclerAdapter.countMatches(pubDateStr, '.') == 2) {
                        index = pubDateStr.lastIndexOf(46);
                    } else if (QueueRecyclerAdapter.countMatches(pubDateStr, '-') == 2) {
                        index = pubDateStr.lastIndexOf(45);
                    } else if (QueueRecyclerAdapter.countMatches(pubDateStr, IOUtils.DIR_SEPARATOR_UNIX) == 2) {
                        index = pubDateStr.lastIndexOf(47);
                    }
                    if (index > 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(pubDateStr.substring(0, index + 1).trim());
                        stringBuilder.append("\n");
                        stringBuilder.append(pubDateStr.substring(index + 1));
                        pubDateStr = stringBuilder.toString();
                    }
                    this.pubDate.setText(pubDateStr);
                    if (media != null) {
                        isDownloadingMedia = DownloadRequester.getInstance().isDownloadingFile(media);
                        state = item.getState();
                        if (isDownloadingMedia) {
                            if (state != State.PLAYING) {
                                if (state == State.IN_PROGRESS) {
                                    if (media.getSize() <= 0) {
                                        this.progressLeft.setText(Converter.byteToString(media.getSize()));
                                    } else if (NetworkUtils.isDownloadAllowed() || media.checkedOnSizeButUnknown()) {
                                        this.progressLeft.setText("");
                                    } else {
                                        this.progressLeft.setText("{fa-spinner}");
                                        Iconify.addIcons(new TextView[]{this.progressLeft});
                                        NetworkUtils.getFeedMediaSizeObservable(media).subscribe(new C1024xa2d507e2(), new C1025x8d8b64c7());
                                    }
                                    this.progressRight.setText(Converter.getDurationStringLong(media.getDuration()));
                                    this.progressBar.setVisibility(4);
                                }
                            }
                            if (media.getDuration() > 0) {
                                double position = (double) media.getPosition();
                                Double.isNaN(position);
                                position *= 100.0d;
                                double duration = (double) media.getDuration();
                                Double.isNaN(duration);
                                this.progressBar.setProgress((int) (position / duration));
                                this.progressBar.setVisibility(0);
                                this.progressLeft.setText(Converter.getDurationStringLong(media.getPosition()));
                                this.progressRight.setText(Converter.getDurationStringLong(media.getDuration()));
                            }
                        } else {
                            this.progressLeft.setText(Converter.byteToString(QueueRecyclerAdapter.this.itemAccess.getItemDownloadedBytes(item)));
                            if (QueueRecyclerAdapter.this.itemAccess.getItemDownloadSize(item) <= 0) {
                                this.progressRight.setText(Converter.byteToString(QueueRecyclerAdapter.this.itemAccess.getItemDownloadSize(item)));
                            } else {
                                this.progressRight.setText(Converter.byteToString(media.getSize()));
                            }
                            this.progressBar.setProgress(QueueRecyclerAdapter.this.itemAccess.getItemDownloadProgressPercent(item));
                            this.progressBar.setVisibility(0);
                        }
                        if (media.isCurrentlyPlaying()) {
                            this.container.setBackgroundColor(QueueRecyclerAdapter.this.normalBackGroundColor);
                        } else {
                            this.container.setBackgroundColor(QueueRecyclerAdapter.this.playingBackGroundColor);
                        }
                    }
                    QueueRecyclerAdapter.this.actionButtonUtils.configureActionButton(this.butSecondary, item, true);
                    this.butSecondary.setFocusable(false);
                    this.butSecondary.setTag(item);
                    this.butSecondary.setOnClickListener(QueueRecyclerAdapter.this.secondaryActionListener);
                    new CoverLoader((MainActivity) QueueRecyclerAdapter.this.mainActivity.get()).withUri(item.getImageLocation()).withFallbackUri(item.getFeed().getImageLocation()).withPlaceholderView(this.placeholder).withCoverView(this.cover).load();
                }
            }
            index = pubDateStr.lastIndexOf(32);
            if (index > 0) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(pubDateStr.substring(0, index + 1).trim());
                stringBuilder2.append("\n");
                stringBuilder2.append(pubDateStr.substring(index + 1));
                pubDateStr = stringBuilder2.toString();
            }
            this.pubDate.setText(pubDateStr);
            if (media != null) {
                isDownloadingMedia = DownloadRequester.getInstance().isDownloadingFile(media);
                state = item.getState();
                if (isDownloadingMedia) {
                    if (state != State.PLAYING) {
                        if (state == State.IN_PROGRESS) {
                            if (media.getSize() <= 0) {
                                if (NetworkUtils.isDownloadAllowed()) {
                                }
                                this.progressLeft.setText("");
                            } else {
                                this.progressLeft.setText(Converter.byteToString(media.getSize()));
                            }
                            this.progressRight.setText(Converter.getDurationStringLong(media.getDuration()));
                            this.progressBar.setVisibility(4);
                        }
                    }
                    if (media.getDuration() > 0) {
                        double position2 = (double) media.getPosition();
                        Double.isNaN(position2);
                        position2 *= 100.0d;
                        double duration2 = (double) media.getDuration();
                        Double.isNaN(duration2);
                        this.progressBar.setProgress((int) (position2 / duration2));
                        this.progressBar.setVisibility(0);
                        this.progressLeft.setText(Converter.getDurationStringLong(media.getPosition()));
                        this.progressRight.setText(Converter.getDurationStringLong(media.getDuration()));
                    }
                } else {
                    this.progressLeft.setText(Converter.byteToString(QueueRecyclerAdapter.this.itemAccess.getItemDownloadedBytes(item)));
                    if (QueueRecyclerAdapter.this.itemAccess.getItemDownloadSize(item) <= 0) {
                        this.progressRight.setText(Converter.byteToString(media.getSize()));
                    } else {
                        this.progressRight.setText(Converter.byteToString(QueueRecyclerAdapter.this.itemAccess.getItemDownloadSize(item)));
                    }
                    this.progressBar.setProgress(QueueRecyclerAdapter.this.itemAccess.getItemDownloadProgressPercent(item));
                    this.progressBar.setVisibility(0);
                }
                if (media.isCurrentlyPlaying()) {
                    this.container.setBackgroundColor(QueueRecyclerAdapter.this.normalBackGroundColor);
                } else {
                    this.container.setBackgroundColor(QueueRecyclerAdapter.this.playingBackGroundColor);
                }
            }
            QueueRecyclerAdapter.this.actionButtonUtils.configureActionButton(this.butSecondary, item, true);
            this.butSecondary.setFocusable(false);
            this.butSecondary.setTag(item);
            this.butSecondary.setOnClickListener(QueueRecyclerAdapter.this.secondaryActionListener);
            new CoverLoader((MainActivity) QueueRecyclerAdapter.this.mainActivity.get()).withUri(item.getImageLocation()).withFallbackUri(item.getFeed().getImageLocation()).withPlaceholderView(this.placeholder).withCoverView(this.cover).load();
        }

        public static /* synthetic */ void lambda$bind$2(ViewHolder viewHolder, Long size) throws Exception {
            if (size.longValue() > 0) {
                viewHolder.progressLeft.setText(Converter.byteToString(size.longValue()));
            } else {
                viewHolder.progressLeft.setText("");
            }
        }

        public static /* synthetic */ void lambda$bind$3(ViewHolder viewHolder, Throwable error) throws Exception {
            viewHolder.progressLeft.setText("");
            Log.e(QueueRecyclerAdapter.TAG, Log.getStackTraceString(error));
        }
    }

    public QueueRecyclerAdapter(MainActivity mainActivity, ItemAccess itemAccess, ActionButtonCallback actionButtonCallback, ItemTouchHelper itemTouchHelper) {
        this.mainActivity = new WeakReference(mainActivity);
        this.itemAccess = itemAccess;
        this.actionButtonUtils = new ActionButtonUtils(mainActivity);
        this.actionButtonCallback = actionButtonCallback;
        this.itemTouchHelper = itemTouchHelper;
        this.locked = UserPreferences.isQueueLocked();
        this.playingBackGroundColor = ThemeUtils.getColorFromAttr(mainActivity, R.attr.currently_playing_background);
        this.normalBackGroundColor = ContextCompat.getColor(mainActivity, 17170445);
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        notifyDataSetChanged();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_listitem, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int pos) {
        FeedItem item = this.itemAccess.getItem(pos);
        holder.bind(item);
        holder.itemView.setOnLongClickListener(new -$$Lambda$QueueRecyclerAdapter$jbJ-28P9hjo1lNcKYomsx1T0iCg(this, item));
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

    private static int countMatches(CharSequence str, char ch) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (ch == str.charAt(i)) {
                count++;
            }
        }
        return count;
    }
}

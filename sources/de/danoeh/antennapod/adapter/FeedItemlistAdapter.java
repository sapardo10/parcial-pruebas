package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.feed.MediaType;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.core.util.DateUtils;
import de.danoeh.antennapod.core.util.LongList;
import de.danoeh.antennapod.core.util.ThemeUtils;
import de.danoeh.antennapod.debug.R;

public class FeedItemlistAdapter extends BaseAdapter {
    private static final int SELECTION_NONE = -1;
    private final ActionButtonUtils actionButtonUtils;
    private final OnClickListener butActionListener = new C07321();
    private final ActionButtonCallback callback;
    private final Context context;
    private final ItemAccess itemAccess;
    private final boolean makePlayedItemsTransparent;
    private final int normalBackGroundColor;
    private final int playingBackGroundColor;
    private final int selectedItemIndex;
    private final boolean showFeedtitle;

    /* renamed from: de.danoeh.antennapod.adapter.FeedItemlistAdapter$1 */
    class C07321 implements OnClickListener {
        C07321() {
        }

        public void onClick(View v) {
            FeedItemlistAdapter.this.callback.onActionButtonPressed((FeedItem) v.getTag(), FeedItemlistAdapter.this.itemAccess.getQueueIds());
        }
    }

    static class Holder {
        ImageButton butAction;
        LinearLayout container;
        ProgressBar episodeProgress;
        ImageView inPlaylist;
        TextView lenSize;
        TextView published;
        View statusUnread;
        TextView title;
        ImageView type;

        Holder() {
        }
    }

    public interface ItemAccess {
        int getCount();

        FeedItem getItem(int i);

        int getItemDownloadProgressPercent(FeedItem feedItem);

        LongList getQueueIds();
    }

    public FeedItemlistAdapter(Context context, ItemAccess itemAccess, ActionButtonCallback callback, boolean showFeedtitle, boolean makePlayedItemsTransparent) {
        this.callback = callback;
        this.context = context;
        this.itemAccess = itemAccess;
        this.showFeedtitle = showFeedtitle;
        this.selectedItemIndex = -1;
        this.actionButtonUtils = new ActionButtonUtils(context);
        this.makePlayedItemsTransparent = makePlayedItemsTransparent;
        this.playingBackGroundColor = ThemeUtils.getColorFromAttr(context, R.attr.currently_playing_background);
        this.normalBackGroundColor = ContextCompat.getColor(context, 17170445);
    }

    public int getCount() {
        return this.itemAccess.getCount();
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public FeedItem getItem(int position) {
        return this.itemAccess.getItem(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View convertView2;
        FeedItemlistAdapter feedItemlistAdapter = this;
        FeedItem item = getItem(position);
        if (convertView == null) {
            holder = new Holder();
            convertView2 = ((LayoutInflater) feedItemlistAdapter.context.getSystemService("layout_inflater")).inflate(R.layout.feeditemlist_item, parent, false);
            holder.container = (LinearLayout) convertView2.findViewById(R.id.container);
            holder.title = (TextView) convertView2.findViewById(R.id.txtvItemname);
            if (VERSION.SDK_INT >= 23) {
                holder.title.setHyphenationFrequency(2);
            }
            holder.lenSize = (TextView) convertView2.findViewById(R.id.txtvLenSize);
            holder.butAction = (ImageButton) convertView2.findViewById(R.id.butSecondaryAction);
            holder.published = (TextView) convertView2.findViewById(R.id.txtvPublished);
            holder.inPlaylist = (ImageView) convertView2.findViewById(R.id.imgvInPlaylist);
            holder.type = (ImageView) convertView2.findViewById(R.id.imgvType);
            holder.statusUnread = convertView2.findViewById(R.id.statusUnread);
            holder.episodeProgress = (ProgressBar) convertView2.findViewById(R.id.pbar_episode_progress);
            convertView2.setTag(holder);
        } else {
            ViewGroup viewGroup = parent;
            holder = (Holder) convertView.getTag();
            convertView2 = convertView;
        }
        if (getItemViewType(position) != -1) {
            convertView2.setVisibility(0);
            if (position == feedItemlistAdapter.selectedItemIndex) {
                convertView2.setBackgroundColor(ContextCompat.getColor(convertView2.getContext(), ThemeUtils.getSelectionBackgroundColor()));
            } else {
                convertView2.setBackgroundResource(0);
            }
            StringBuilder buffer = new StringBuilder(item.getTitle());
            if (feedItemlistAdapter.showFeedtitle) {
                buffer.append(" (");
                buffer.append(item.getFeed().getTitle());
                buffer.append(")");
            }
            holder.title.setText(buffer.toString());
            if (item.isNew()) {
                holder.statusUnread.setVisibility(0);
            } else {
                holder.statusUnread.setVisibility(4);
            }
            if (item.isPlayed() && feedItemlistAdapter.makePlayedItemsTransparent) {
                convertView2.setAlpha(0.5f);
            } else {
                convertView2.setAlpha(1.0f);
            }
            holder.published.setText(DateUtils.formatAbbrev(feedItemlistAdapter.context, item.getPubDate()));
            boolean isInQueue = item.isTagged(FeedItem.TAG_QUEUE);
            FeedMedia media = item.getMedia();
            if (media == null) {
                holder.episodeProgress.setVisibility(4);
                holder.inPlaylist.setVisibility(4);
                holder.type.setVisibility(4);
                holder.lenSize.setVisibility(4);
                StringBuilder stringBuilder = buffer;
            } else {
                AdapterUtils.updateEpisodePlaybackProgress(item, holder.lenSize, holder.episodeProgress);
                if (isInQueue) {
                    holder.inPlaylist.setVisibility(0);
                } else {
                    holder.inPlaylist.setVisibility(4);
                }
                if (DownloadRequester.getInstance().isDownloadingFile(item.getMedia())) {
                    holder.episodeProgress.setVisibility(0);
                    holder.episodeProgress.setProgress(feedItemlistAdapter.itemAccess.getItemDownloadProgressPercent(item));
                } else if (media.getPosition() == 0) {
                    holder.episodeProgress.setVisibility(4);
                }
                TypedArray typeDrawables = feedItemlistAdapter.context.obtainStyledAttributes(new int[]{R.attr.type_audio, R.attr.type_video});
                int[] labels = new int[]{R.string.media_type_audio_label, R.string.media_type_video_label};
                MediaType mediaType = item.getMedia().getMediaType();
                if (mediaType == MediaType.AUDIO) {
                    holder.type.setImageDrawable(typeDrawables.getDrawable(0));
                    holder.type.setContentDescription(feedItemlistAdapter.context.getString(labels[0]));
                    holder.type.setVisibility(0);
                } else {
                    if (mediaType == MediaType.VIDEO) {
                        holder.type.setImageDrawable(typeDrawables.getDrawable(1));
                        holder.type.setContentDescription(feedItemlistAdapter.context.getString(labels[1]));
                        holder.type.setVisibility(0);
                    } else {
                        holder.type.setImageBitmap(null);
                        holder.type.setVisibility(8);
                    }
                }
                typeDrawables.recycle();
                if (media.isCurrentlyPlaying()) {
                    holder.container.setBackgroundColor(feedItemlistAdapter.playingBackGroundColor);
                } else {
                    holder.container.setBackgroundColor(feedItemlistAdapter.normalBackGroundColor);
                }
            }
            feedItemlistAdapter.actionButtonUtils.configureActionButton(holder.butAction, item, isInQueue);
            holder.butAction.setFocusable(false);
            holder.butAction.setTag(item);
            holder.butAction.setOnClickListener(feedItemlistAdapter.butActionListener);
        } else {
            int i = position;
            convertView2.setVisibility(8);
        }
        return convertView2;
    }
}

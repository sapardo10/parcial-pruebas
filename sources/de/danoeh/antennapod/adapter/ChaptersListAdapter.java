package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import de.danoeh.antennapod.core.feed.Chapter;
import de.danoeh.antennapod.core.util.ChapterUtils;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.ThemeUtils;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.debug.R;

public class ChaptersListAdapter extends ArrayAdapter<Chapter> {
    private static final String TAG = "ChapterListAdapter";
    private final Callback callback;
    private int defaultTextColor;
    private Playable media;

    public interface Callback {
        void onPlayChapterButtonClicked(int i);
    }

    static class Holder {
        ImageButton butPlayChapter;
        TextView duration;
        TextView link;
        TextView start;
        TextView title;
        View view;

        Holder() {
        }
    }

    public ChaptersListAdapter(Context context, int textViewResourceId, Callback callback) {
        super(context, textViewResourceId);
        this.callback = callback;
    }

    public void setMedia(Playable media) {
        this.media = media;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Holder holder;
        long duration;
        Chapter sc = getItem(position);
        if (convertView == null) {
            holder = new Holder();
            convertView = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.simplechapter_item, parent, false);
            holder.view = convertView;
            holder.title = (TextView) convertView.findViewById(R.id.txtvTitle);
            this.defaultTextColor = holder.title.getTextColors().getDefaultColor();
            holder.start = (TextView) convertView.findViewById(R.id.txtvStart);
            holder.link = (TextView) convertView.findViewById(R.id.txtvLink);
            holder.duration = (TextView) convertView.findViewById(R.id.txtvDuration);
            holder.butPlayChapter = (ImageButton) convertView.findViewById(R.id.butPlayChapter);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.title.setText(sc.getTitle());
        holder.start.setText(Converter.getDurationStringLong((int) sc.getStart()));
        if (position + 1 < this.media.getChapters().size()) {
            duration = ((Chapter) this.media.getChapters().get(position + 1)).getStart() - sc.getStart();
        } else {
            duration = ((long) this.media.getDuration()) - sc.getStart();
        }
        holder.duration.setText(getContext().getString(R.string.chapter_duration, new Object[]{Converter.getDurationStringLong((int) duration)}));
        if (sc.getLink() != null) {
            holder.link.setVisibility(0);
            holder.link.setText(sc.getLink());
            Linkify.addLinks(holder.link, 1);
        } else {
            holder.link.setVisibility(8);
        }
        holder.link.setMovementMethod(null);
        holder.link.setOnTouchListener(-$$Lambda$ChaptersListAdapter$aUq0z1-ndPEPR-acIL5Ic6bVmLo.INSTANCE);
        holder.butPlayChapter.setOnClickListener(new -$$Lambda$ChaptersListAdapter$AslOC4UpEQTym2fK1dLzIA7JiSE(this, position));
        if (ChapterUtils.getCurrentChapter(this.media) == sc) {
            holder.view.setBackgroundColor(ThemeUtils.getColorFromAttr(getContext(), R.attr.currently_playing_background));
        } else {
            holder.view.setBackgroundColor(ContextCompat.getColor(getContext(), 17170445));
            holder.title.setTextColor(this.defaultTextColor);
            holder.start.setTextColor(this.defaultTextColor);
        }
        return convertView;
    }

    static /* synthetic */ boolean lambda$getView$0(View v, MotionEvent event) {
        TextView widget = (TextView) v;
        Spannable text = widget.getText();
        if (text instanceof Spanned) {
            Spannable buffer = text;
            int action = event.getAction();
            if (action != 1) {
                if (action == 0) {
                }
            }
            int x = (((int) event.getX()) - widget.getTotalPaddingLeft()) + widget.getScrollX();
            int y = (((int) event.getY()) - widget.getTotalPaddingTop()) + widget.getScrollY();
            Layout layout = widget.getLayout();
            int off = layout.getOffsetForHorizontal(layout.getLineForVertical(y), (float) x);
            ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
            if (link.length != 0) {
                if (action == 1) {
                    link[0].onClick(widget);
                } else if (action == 0) {
                    Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
                }
                return true;
            }
        }
        return false;
    }

    public static /* synthetic */ void lambda$getView$1(ChaptersListAdapter chaptersListAdapter, int position, View v) {
        Callback callback = chaptersListAdapter.callback;
        if (callback != null) {
            callback.onPlayChapterButtonClicked(position);
        }
    }

    public int getCount() {
        Playable playable = this.media;
        if (playable != null) {
            if (playable.getChapters() != null) {
                int counter = 0;
                for (Chapter chapter : this.media.getChapters()) {
                    if (!ignoreChapter(chapter)) {
                        counter++;
                    }
                }
                return counter;
            }
        }
        return 0;
    }

    private boolean ignoreChapter(Chapter c) {
        return this.media.getDuration() > 0 && ((long) this.media.getDuration()) < c.getStart();
    }

    public Chapter getItem(int position) {
        int i = 0;
        for (Chapter chapter : this.media.getChapters()) {
            if (!ignoreChapter(chapter)) {
                if (i == position) {
                    return chapter;
                }
                i++;
            }
        }
        return (Chapter) super.getItem(position);
    }
}

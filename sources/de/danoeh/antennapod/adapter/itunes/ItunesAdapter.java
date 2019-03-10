package de.danoeh.antennapod.adapter.itunes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.debug.R;
import de.mfietz.fyydlin.SearchHit;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ItunesAdapter extends ArrayAdapter<Podcast> {
    private final Context context;
    private final List<Podcast> data;

    public static class Podcast {
        @Nullable
        public final String feedUrl;
        @Nullable
        public final String imageUrl;
        public final String title;

        private Podcast(String title, @Nullable String imageUrl, @Nullable String feedUrl) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.feedUrl = feedUrl;
        }

        public static Podcast fromSearch(JSONObject json) {
            return new Podcast(json.optString("collectionName", ""), json.optString("artworkUrl100", null), json.optString("feedUrl", null));
        }

        public static Podcast fromSearch(SearchHit searchHit) {
            return new Podcast(searchHit.getTitle(), searchHit.getThumbImageURL(), searchHit.getXmlUrl());
        }

        public static Podcast fromToplist(JSONObject json) throws JSONException {
            String title = json.getJSONObject("title").getString("label");
            String imageUrl = null;
            JSONArray images = json.getJSONArray("im:image");
            int i = 0;
            while (imageUrl == null && i < images.length()) {
                JSONObject image = images.getJSONObject(i);
                if (Integer.parseInt(image.getJSONObject("attributes").getString("height")) >= 100) {
                    imageUrl = image.getString("label");
                }
                i++;
            }
            String feedUrl = new StringBuilder();
            feedUrl.append("https://itunes.apple.com/lookup?id=");
            feedUrl.append(json.getJSONObject("id").getJSONObject("attributes").getString("im:id"));
            return new Podcast(title, imageUrl, feedUrl.toString());
        }
    }

    static class PodcastViewHolder {
        final ImageView coverView;
        final TextView titleView;
        final TextView urlView;

        PodcastViewHolder(View view) {
            this.coverView = (ImageView) view.findViewById(R.id.imgvCover);
            this.titleView = (TextView) view.findViewById(R.id.txtvTitle);
            this.urlView = (TextView) view.findViewById(R.id.txtvUrl);
        }
    }

    public ItunesAdapter(Context context, List<Podcast> objects) {
        super(context, 0, objects);
        this.data = objects;
        this.context = context;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        PodcastViewHolder viewHolder;
        Podcast podcast = (Podcast) this.data.get(position);
        if (convertView == null) {
            view = ((MainActivity) this.context).getLayoutInflater().inflate(R.layout.itunes_podcast_listitem, parent, false);
            viewHolder = new PodcastViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (PodcastViewHolder) view.getTag();
        }
        viewHolder.titleView.setText(podcast.title);
        if (podcast.feedUrl == null || podcast.feedUrl.contains("itunes.apple.com")) {
            viewHolder.urlView.setVisibility(8);
        } else {
            viewHolder.urlView.setText(podcast.feedUrl);
            viewHolder.urlView.setVisibility(0);
        }
        Glide.with(this.context).load(podcast.imageUrl).apply(new RequestOptions().placeholder((int) R.color.light_gray).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().dontAnimate()).into(viewHolder.coverView);
        return view;
    }
}

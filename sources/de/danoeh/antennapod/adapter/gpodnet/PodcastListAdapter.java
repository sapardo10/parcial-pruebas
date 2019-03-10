package de.danoeh.antennapod.adapter.gpodnet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import de.danoeh.antennapod.core.glide.ApGlideSettings;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetPodcast;
import de.danoeh.antennapod.debug.R;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class PodcastListAdapter extends ArrayAdapter<GpodnetPodcast> {

    static class Holder {
        ImageView image;
        TextView subscribers;
        TextView title;
        TextView url;

        Holder() {
        }
    }

    public PodcastListAdapter(Context context, int resource, List<GpodnetPodcast> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        GpodnetPodcast podcast = (GpodnetPodcast) getItem(position);
        if (convertView == null) {
            holder = new Holder();
            convertView = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.gpodnet_podcast_listitem, parent, false);
            holder.image = (ImageView) convertView.findViewById(R.id.imgvCover);
            holder.title = (TextView) convertView.findViewById(R.id.txtvTitle);
            holder.subscribers = (TextView) convertView.findViewById(R.id.txtvSubscribers);
            holder.url = (TextView) convertView.findViewById(R.id.txtvUrl);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (StringUtils.isNotBlank(podcast.getLogoUrl())) {
            Glide.with(convertView.getContext()).load(podcast.getLogoUrl()).apply(new RequestOptions().placeholder((int) R.color.light_gray).error((int) R.color.light_gray).diskCacheStrategy(ApGlideSettings.AP_DISK_CACHE_STRATEGY).fitCenter().dontAnimate()).into(holder.image);
        }
        holder.title.setText(podcast.getTitle());
        holder.subscribers.setText(String.valueOf(podcast.getSubscribers()));
        holder.url.setText(podcast.getUrl());
        return convertView;
    }
}

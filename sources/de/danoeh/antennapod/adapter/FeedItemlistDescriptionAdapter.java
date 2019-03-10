package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.util.DateUtils;
import de.danoeh.antennapod.debug.R;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class FeedItemlistDescriptionAdapter extends ArrayAdapter<FeedItem> {

    static class Holder {
        TextView description;
        TextView pubDate;
        TextView title;

        Holder() {
        }
    }

    public FeedItemlistDescriptionAdapter(Context context, int resource, List<FeedItem> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        FeedItem item = (FeedItem) getItem(position);
        if (convertView == null) {
            holder = new Holder();
            convertView = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.itemdescription_listitem, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.txtvTitle);
            holder.pubDate = (TextView) convertView.findViewById(R.id.txtvPubDate);
            holder.description = (TextView) convertView.findViewById(R.id.txtvDescription);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.title.setText(item.getTitle());
        holder.pubDate.setText(DateUtils.formatAbbrev(getContext(), item.getPubDate()));
        if (item.getDescription() != null) {
            holder.description.setText(item.getDescription().replaceAll("\n", StringUtils.SPACE).replaceAll("\\s+", StringUtils.SPACE).trim());
        }
        return convertView;
    }
}

package de.danoeh.antennapod.adapter.gpodnet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetTag;
import de.danoeh.antennapod.debug.R;
import java.util.List;

public class TagListAdapter extends ArrayAdapter<GpodnetTag> {

    static class Holder {
        TextView title;
        TextView usage;

        Holder() {
        }
    }

    public TagListAdapter(Context context, int resource, List<GpodnetTag> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        GpodnetTag tag = (GpodnetTag) getItem(position);
        if (convertView == null) {
            holder = new Holder();
            convertView = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.gpodnet_tag_listitem, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.txtvTitle);
            holder.usage = (TextView) convertView.findViewById(R.id.txtvUsage);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.title.setText(tag.getTitle());
        holder.usage.setText(String.valueOf(tag.getUsage()));
        return convertView;
    }
}

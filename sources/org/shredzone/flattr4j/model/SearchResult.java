package org.shredzone.flattr4j.model;

import de.danoeh.antennapod.core.storage.DownloadRequester;
import java.util.ArrayList;
import java.util.List;
import org.shredzone.flattr4j.connector.FlattrObject;

public class SearchResult extends Resource {
    private static final long serialVersionUID = -3762044230769599498L;
    private transient ArrayList<Thing> result = null;

    public SearchResult(FlattrObject data) {
        super(data);
    }

    public int getTotalCount() {
        return this.data.getInt("total_items");
    }

    public int getItemCount() {
        return this.data.getInt("items");
    }

    public int getPage() {
        return this.data.getInt(DownloadRequester.REQUEST_ARG_PAGE_NR);
    }

    public List<Thing> getThings() {
        if (this.result == null) {
            List<FlattrObject> objects = this.data.getObjects("things");
            this.result = new ArrayList(objects.size());
            for (FlattrObject obj : objects) {
                this.result.add(new Thing(obj));
            }
        }
        return this.result;
    }
}

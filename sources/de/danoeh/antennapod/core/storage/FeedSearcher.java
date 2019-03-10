package de.danoeh.antennapod.core.storage;

import android.content.Context;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.SearchResult;
import de.danoeh.antennapod.core.util.comparator.SearchResultValueComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.FutureTask;

public class FeedSearcher {
    private static final String TAG = "FeedSearcher";

    private FeedSearcher() {
    }

    public static List<SearchResult> performSearch(Context context, String query, long selectedFeed) {
        Context context2 = context;
        String str = query;
        long j = selectedFeed;
        int[] values = new int[]{2, 1, 0, 0, 0, 0};
        r0 = new String[6];
        int i = 0;
        r0[0] = context2.getString(C0734R.string.found_in_title_label);
        r0[1] = context2.getString(C0734R.string.found_in_chapters_label);
        r0[2] = context2.getString(C0734R.string.found_in_shownotes_label);
        r0[3] = context2.getString(C0734R.string.found_in_shownotes_label);
        r0[4] = context2.getString(C0734R.string.found_in_authors_label);
        r0[5] = context2.getString(C0734R.string.found_in_feeds_label);
        String[] subtitles = r0;
        ArrayList result = new ArrayList();
        ArrayList<FutureTask<List<FeedItem>>> tasks = new ArrayList();
        tasks.add(DBTasks.searchFeedItemTitle(context2, j, str));
        tasks.add(DBTasks.searchFeedItemChapters(context2, j, str));
        tasks.add(DBTasks.searchFeedItemDescription(context2, j, str));
        tasks.add(DBTasks.searchFeedItemContentEncoded(context2, j, str));
        tasks.add(DBTasks.searchFeedItemAuthor(context2, j, str));
        tasks.add(DBTasks.searchFeedItemFeedIdentifier(context2, j, str));
        for (FutureTask<List<FeedItem>> task : tasks) {
            task.run();
        }
        try {
            Set<Long> set = new HashSet();
            while (i < tasks.size()) {
                for (FeedItem item : (List) ((FutureTask) tasks.get(i)).get()) {
                    if (!set.contains(Long.valueOf(item.getId()))) {
                        result.add(new SearchResult(item, values[i], subtitles[i]));
                        set.add(Long.valueOf(item.getId()));
                    }
                    context2 = context;
                }
                i++;
                context2 = context;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(result, new SearchResultValueComparator());
        return result;
    }
}

package de.danoeh.antennapod.core.feed;

import android.text.TextUtils;
import de.danoeh.antennapod.core.feed.FeedItem.State;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.util.LongList;
import java.util.ArrayList;
import java.util.List;

public class FeedItemFilter {
    private final String[] mProperties;
    private boolean showDownloaded;
    private boolean showHasMedia;
    private boolean showIsFavorite;
    private boolean showNotDownloaded;
    private boolean showNotQueued;
    private boolean showPaused;
    private boolean showPlayed;
    private boolean showQueued;
    private boolean showUnplayed;

    public FeedItemFilter(String properties) {
        this(TextUtils.split(properties, ","));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FeedItemFilter(java.lang.String[] r8) {
        /*
        r7 = this;
        r7.<init>();
        r0 = 0;
        r7.showPlayed = r0;
        r7.showUnplayed = r0;
        r7.showPaused = r0;
        r7.showQueued = r0;
        r7.showNotQueued = r0;
        r7.showDownloaded = r0;
        r7.showNotDownloaded = r0;
        r7.showHasMedia = r0;
        r7.showIsFavorite = r0;
        r7.mProperties = r8;
        r1 = r8.length;
        r2 = 0;
    L_0x001a:
        if (r2 >= r1) goto L_0x00a7;
    L_0x001c:
        r3 = r8[r2];
        r4 = -1;
        r5 = r3.hashCode();
        r6 = 1;
        switch(r5) {
            case -1358656493: goto L_0x007b;
            case -1138288545: goto L_0x0071;
            case -995321554: goto L_0x0067;
            case -985752877: goto L_0x005d;
            case -948696717: goto L_0x0052;
            case -99249492: goto L_0x0047;
            case 64457215: goto L_0x003d;
            case 315759889: goto L_0x0032;
            case 2039141159: goto L_0x0028;
            default: goto L_0x0027;
        };
    L_0x0027:
        goto L_0x0084;
    L_0x0028:
        r5 = "downloaded";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x0027;
    L_0x0030:
        r4 = 5;
        goto L_0x0084;
    L_0x0032:
        r5 = "is_favorite";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x0027;
    L_0x003a:
        r4 = 8;
        goto L_0x0084;
    L_0x003d:
        r5 = "has_media";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x0027;
    L_0x0045:
        r4 = 7;
        goto L_0x0084;
    L_0x0047:
        r5 = "unplayed";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x0027;
    L_0x0050:
        r4 = 0;
        goto L_0x0084;
    L_0x0052:
        r5 = "queued";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x0027;
    L_0x005b:
        r4 = 3;
        goto L_0x0084;
    L_0x005d:
        r5 = "played";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x0027;
    L_0x0065:
        r4 = 2;
        goto L_0x0084;
    L_0x0067:
        r5 = "paused";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x0027;
    L_0x006f:
        r4 = 1;
        goto L_0x0084;
    L_0x0071:
        r5 = "not_queued";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x0027;
    L_0x0079:
        r4 = 4;
        goto L_0x0084;
    L_0x007b:
        r5 = "not_downloaded";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x0027;
    L_0x0083:
        r4 = 6;
    L_0x0084:
        switch(r4) {
            case 0: goto L_0x00a0;
            case 1: goto L_0x009d;
            case 2: goto L_0x009a;
            case 3: goto L_0x0097;
            case 4: goto L_0x0094;
            case 5: goto L_0x0091;
            case 6: goto L_0x008e;
            case 7: goto L_0x008b;
            case 8: goto L_0x0088;
            default: goto L_0x0087;
        };
    L_0x0087:
        goto L_0x00a3;
    L_0x0088:
        r7.showIsFavorite = r6;
        goto L_0x00a3;
    L_0x008b:
        r7.showHasMedia = r6;
        goto L_0x00a3;
    L_0x008e:
        r7.showNotDownloaded = r6;
        goto L_0x00a3;
    L_0x0091:
        r7.showDownloaded = r6;
        goto L_0x00a3;
    L_0x0094:
        r7.showNotQueued = r6;
        goto L_0x00a3;
    L_0x0097:
        r7.showQueued = r6;
        goto L_0x00a3;
    L_0x009a:
        r7.showPlayed = r6;
        goto L_0x00a3;
    L_0x009d:
        r7.showPaused = r6;
        goto L_0x00a3;
    L_0x00a0:
        r7.showUnplayed = r6;
    L_0x00a3:
        r2 = r2 + 1;
        goto L_0x001a;
    L_0x00a7:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.feed.FeedItemFilter.<init>(java.lang.String[]):void");
    }

    public List<FeedItem> filter(List<FeedItem> items) {
        if (this.mProperties.length == 0) {
            return items;
        }
        List<FeedItem> result = new ArrayList();
        if (this.showPlayed && this.showUnplayed) {
            return result;
        }
        if (this.showQueued && this.showNotQueued) {
            return result;
        }
        if (this.showDownloaded && this.showNotDownloaded) {
            return result;
        }
        LongList queuedIds = DBReader.getQueueIDList();
        for (FeedItem item : items) {
            if (!this.showPlayed || item.isPlayed()) {
                if (!this.showUnplayed || !item.isPlayed()) {
                    if (!this.showPaused || item.getState() == State.IN_PROGRESS) {
                        boolean queued = queuedIds.contains(item.getId());
                        if (!this.showQueued || queued) {
                            if (!this.showNotQueued || !queued) {
                                boolean downloaded = item.getMedia() != null && item.getMedia().isDownloaded();
                                if (!this.showDownloaded || downloaded) {
                                    if (!this.showNotDownloaded || !downloaded) {
                                        if (!this.showHasMedia || item.hasMedia()) {
                                            if (!this.showIsFavorite || item.isTagged(FeedItem.TAG_FAVORITE)) {
                                                result.add(item);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public String[] getValues() {
        return (String[]) this.mProperties.clone();
    }
}

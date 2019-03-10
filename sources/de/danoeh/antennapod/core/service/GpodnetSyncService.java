package de.danoeh.antennapod.core.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.SafeJobIntentService;
import android.util.Log;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.gpoddernet.GpodnetService;
import de.danoeh.antennapod.core.gpoddernet.GpodnetServiceAuthenticationException;
import de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction.Action;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeActionGetResponse;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeActionPostResponse;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetSubscriptionChange;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetUploadChangesResponse;
import de.danoeh.antennapod.core.preferences.GpodnetPreferences;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.storage.DownloadRequestException;
import de.danoeh.antennapod.core.util.NetworkUtils;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GpodnetSyncService extends SafeJobIntentService {
    private static final String ACTION_SYNC = "de.danoeh.antennapod.intent.action.sync";
    private static final String ACTION_SYNC_ACTIONS = "de.danoeh.antennapod.intent.action.sync_ACTIONS";
    private static final String ACTION_SYNC_SUBSCRIPTIONS = "de.danoeh.antennapod.intent.action.sync_subscriptions";
    private static final String ARG_ACTION = "action";
    private static final int JOB_ID = -17000;
    private static final String TAG = "GpodnetSyncService";
    private static final long WAIT_INTERVAL = 5000;
    private static final AtomicInteger syncActionCount = new AtomicInteger(0);
    private static boolean syncActions = false;
    private static boolean syncSubscriptions = false;
    private GpodnetService service;

    /* renamed from: de.danoeh.antennapod.core.service.GpodnetSyncService$1 */
    static /* synthetic */ class C07431 {
        /* renamed from: $SwitchMap$de$danoeh$antennapod$core$gpoddernet$model$GpodnetEpisodeAction$Action */
        static final /* synthetic */ int[] f20xe162e976 = new int[Action.values().length];

        static {
            try {
                f20xe162e976[Action.NEW.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f20xe162e976[Action.DOWNLOAD.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f20xe162e976[Action.PLAY.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f20xe162e976[Action.DELETE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private synchronized void processEpisodeActions(java.util.List<de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction> r10, java.util.List<de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction> r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:73:0x0157 in {5, 14, 17, 18, 19, 20, 26, 33, 34, 35, 36, 41, 46, 47, 48, 49, 50, 53, 54, 55, 64, 65, 66, 67, 69, 72} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r9 = this;
        monitor-enter(r9);
        r0 = r11.size();	 Catch:{ all -> 0x0154 }
        if (r0 != 0) goto L_0x0009;
    L_0x0007:
        monitor-exit(r9);
        return;
    L_0x0009:
        r0 = new android.support.v4.util.ArrayMap;	 Catch:{ all -> 0x0154 }
        r0.<init>();	 Catch:{ all -> 0x0154 }
        r1 = r10.iterator();	 Catch:{ all -> 0x0154 }
    L_0x0012:
        r2 = r1.hasNext();	 Catch:{ all -> 0x0154 }
        if (r2 == 0) goto L_0x0052;	 Catch:{ all -> 0x0154 }
    L_0x0018:
        r2 = r1.next();	 Catch:{ all -> 0x0154 }
        r2 = (de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction) r2;	 Catch:{ all -> 0x0154 }
        r3 = new android.util.Pair;	 Catch:{ all -> 0x0154 }
        r4 = r2.getPodcast();	 Catch:{ all -> 0x0154 }
        r5 = r2.getEpisode();	 Catch:{ all -> 0x0154 }
        r3.<init>(r4, r5);	 Catch:{ all -> 0x0154 }
        r4 = r0.get(r3);	 Catch:{ all -> 0x0154 }
        r4 = (de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction) r4;	 Catch:{ all -> 0x0154 }
        if (r4 == 0) goto L_0x004d;	 Catch:{ all -> 0x0154 }
    L_0x0033:
        r5 = r4.getTimestamp();	 Catch:{ all -> 0x0154 }
        if (r5 != 0) goto L_0x003a;	 Catch:{ all -> 0x0154 }
    L_0x0039:
        goto L_0x004d;	 Catch:{ all -> 0x0154 }
    L_0x003a:
        r5 = r4.getTimestamp();	 Catch:{ all -> 0x0154 }
        r6 = r2.getTimestamp();	 Catch:{ all -> 0x0154 }
        r5 = r5.before(r6);	 Catch:{ all -> 0x0154 }
        if (r5 == 0) goto L_0x004c;	 Catch:{ all -> 0x0154 }
    L_0x0048:
        r0.put(r3, r2);	 Catch:{ all -> 0x0154 }
        goto L_0x0051;	 Catch:{ all -> 0x0154 }
    L_0x004c:
        goto L_0x0051;	 Catch:{ all -> 0x0154 }
        r0.put(r3, r2);	 Catch:{ all -> 0x0154 }
    L_0x0051:
        goto L_0x0012;	 Catch:{ all -> 0x0154 }
    L_0x0052:
        r1 = new android.support.v4.util.ArrayMap;	 Catch:{ all -> 0x0154 }
        r1.<init>();	 Catch:{ all -> 0x0154 }
        r2 = r11.iterator();	 Catch:{ all -> 0x0154 }
    L_0x005b:
        r3 = r2.hasNext();	 Catch:{ all -> 0x0154 }
        r4 = 1;	 Catch:{ all -> 0x0154 }
        if (r3 == 0) goto L_0x0108;	 Catch:{ all -> 0x0154 }
    L_0x0062:
        r3 = r2.next();	 Catch:{ all -> 0x0154 }
        r3 = (de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction) r3;	 Catch:{ all -> 0x0154 }
        r5 = de.danoeh.antennapod.core.service.GpodnetSyncService.C07431.f20xe162e976;	 Catch:{ all -> 0x0154 }
        r6 = r3.getAction();	 Catch:{ all -> 0x0154 }
        r6 = r6.ordinal();	 Catch:{ all -> 0x0154 }
        r5 = r5[r6];	 Catch:{ all -> 0x0154 }
        switch(r5) {
            case 1: goto L_0x00dc;
            case 2: goto L_0x00db;
            case 3: goto L_0x0079;
            default: goto L_0x0077;
        };	 Catch:{ all -> 0x0154 }
    L_0x0077:
        goto L_0x0106;	 Catch:{ all -> 0x0154 }
    L_0x0079:
        r4 = new android.util.Pair;	 Catch:{ all -> 0x0154 }
        r5 = r3.getPodcast();	 Catch:{ all -> 0x0154 }
        r6 = r3.getEpisode();	 Catch:{ all -> 0x0154 }
        r4.<init>(r5, r6);	 Catch:{ all -> 0x0154 }
        r5 = r0.get(r4);	 Catch:{ all -> 0x0154 }
        r5 = (de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction) r5;	 Catch:{ all -> 0x0154 }
        if (r5 == 0) goto L_0x00a5;	 Catch:{ all -> 0x0154 }
    L_0x008e:
        r6 = r5.getTimestamp();	 Catch:{ all -> 0x0154 }
        if (r6 == 0) goto L_0x00a4;	 Catch:{ all -> 0x0154 }
    L_0x0094:
        r6 = r5.getTimestamp();	 Catch:{ all -> 0x0154 }
        r7 = r3.getTimestamp();	 Catch:{ all -> 0x0154 }
        r6 = r6.before(r7);	 Catch:{ all -> 0x0154 }
        if (r6 == 0) goto L_0x00a3;	 Catch:{ all -> 0x0154 }
    L_0x00a2:
        goto L_0x00a6;	 Catch:{ all -> 0x0154 }
    L_0x00a3:
        goto L_0x0106;	 Catch:{ all -> 0x0154 }
    L_0x00a4:
        goto L_0x00a6;	 Catch:{ all -> 0x0154 }
    L_0x00a6:
        r6 = r1.get(r4);	 Catch:{ all -> 0x0154 }
        r6 = (de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction) r6;	 Catch:{ all -> 0x0154 }
        if (r6 == 0) goto L_0x00d6;	 Catch:{ all -> 0x0154 }
    L_0x00ae:
        r7 = r6.getTimestamp();	 Catch:{ all -> 0x0154 }
        if (r7 != 0) goto L_0x00b5;	 Catch:{ all -> 0x0154 }
    L_0x00b4:
        goto L_0x00d6;	 Catch:{ all -> 0x0154 }
    L_0x00b5:
        r7 = r3.getTimestamp();	 Catch:{ all -> 0x0154 }
        if (r7 == 0) goto L_0x00cd;	 Catch:{ all -> 0x0154 }
    L_0x00bb:
        r7 = r6.getTimestamp();	 Catch:{ all -> 0x0154 }
        r8 = r3.getTimestamp();	 Catch:{ all -> 0x0154 }
        r7 = r7.before(r8);	 Catch:{ all -> 0x0154 }
        if (r7 == 0) goto L_0x00cd;	 Catch:{ all -> 0x0154 }
    L_0x00c9:
        r1.put(r4, r3);	 Catch:{ all -> 0x0154 }
        goto L_0x00da;	 Catch:{ all -> 0x0154 }
        r7 = "GpodnetSyncService";	 Catch:{ all -> 0x0154 }
        r8 = "No date information in action, skipping it";	 Catch:{ all -> 0x0154 }
        android.util.Log.d(r7, r8);	 Catch:{ all -> 0x0154 }
        goto L_0x00da;	 Catch:{ all -> 0x0154 }
        r1.put(r4, r3);	 Catch:{ all -> 0x0154 }
    L_0x00da:
        goto L_0x0106;	 Catch:{ all -> 0x0154 }
    L_0x00db:
        goto L_0x0106;	 Catch:{ all -> 0x0154 }
    L_0x00dc:
        r5 = r3.getPodcast();	 Catch:{ all -> 0x0154 }
        r6 = r3.getEpisode();	 Catch:{ all -> 0x0154 }
        r5 = de.danoeh.antennapod.core.storage.DBReader.getFeedItem(r5, r6);	 Catch:{ all -> 0x0154 }
        if (r5 == 0) goto L_0x00ef;	 Catch:{ all -> 0x0154 }
    L_0x00ea:
        r6 = 0;	 Catch:{ all -> 0x0154 }
        de.danoeh.antennapod.core.storage.DBWriter.markItemPlayed(r5, r6, r4);	 Catch:{ all -> 0x0154 }
        goto L_0x0106;	 Catch:{ all -> 0x0154 }
    L_0x00ef:
        r4 = "GpodnetSyncService";	 Catch:{ all -> 0x0154 }
        r6 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0154 }
        r6.<init>();	 Catch:{ all -> 0x0154 }
        r7 = "Unknown feed item: ";	 Catch:{ all -> 0x0154 }
        r6.append(r7);	 Catch:{ all -> 0x0154 }
        r6.append(r3);	 Catch:{ all -> 0x0154 }
        r6 = r6.toString();	 Catch:{ all -> 0x0154 }
        android.util.Log.i(r4, r6);	 Catch:{ all -> 0x0154 }
    L_0x0106:
        goto L_0x005b;	 Catch:{ all -> 0x0154 }
    L_0x0108:
        r2 = r1.values();	 Catch:{ all -> 0x0154 }
        r2 = r2.iterator();	 Catch:{ all -> 0x0154 }
    L_0x0110:
        r3 = r2.hasNext();	 Catch:{ all -> 0x0154 }
        if (r3 == 0) goto L_0x0152;	 Catch:{ all -> 0x0154 }
    L_0x0116:
        r3 = r2.next();	 Catch:{ all -> 0x0154 }
        r3 = (de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction) r3;	 Catch:{ all -> 0x0154 }
        r5 = r3.getPodcast();	 Catch:{ all -> 0x0154 }
        r6 = r3.getEpisode();	 Catch:{ all -> 0x0154 }
        r5 = de.danoeh.antennapod.core.storage.DBReader.getFeedItem(r5, r6);	 Catch:{ all -> 0x0154 }
        if (r5 == 0) goto L_0x0150;	 Catch:{ all -> 0x0154 }
    L_0x012a:
        r6 = r5.getMedia();	 Catch:{ all -> 0x0154 }
        r7 = r3.getPosition();	 Catch:{ all -> 0x0154 }
        r7 = r7 * 1000;	 Catch:{ all -> 0x0154 }
        r6.setPosition(r7);	 Catch:{ all -> 0x0154 }
        de.danoeh.antennapod.core.storage.DBWriter.setFeedMedia(r6);	 Catch:{ all -> 0x0154 }
        r7 = r5.getMedia();	 Catch:{ all -> 0x0154 }
        r7 = r7.hasAlmostEnded();	 Catch:{ all -> 0x0154 }
        if (r7 == 0) goto L_0x014f;	 Catch:{ all -> 0x0154 }
    L_0x0144:
        de.danoeh.antennapod.core.storage.DBWriter.markItemPlayed(r5, r4, r4);	 Catch:{ all -> 0x0154 }
        r7 = r5.getMedia();	 Catch:{ all -> 0x0154 }
        de.danoeh.antennapod.core.storage.DBWriter.addItemToPlaybackHistory(r7);	 Catch:{ all -> 0x0154 }
        goto L_0x0151;
    L_0x014f:
        goto L_0x0151;
    L_0x0151:
        goto L_0x0110;
    L_0x0152:
        monitor-exit(r9);
        return;
    L_0x0154:
        r10 = move-exception;
        monitor-exit(r9);
        throw r10;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.service.GpodnetSyncService.processEpisodeActions(java.util.List, java.util.List):void");
    }

    private synchronized void processSubscriptionChanges(java.util.List<java.lang.String> r5, java.util.Collection<java.lang.String> r6, java.util.Collection<java.lang.String> r7, de.danoeh.antennapod.core.gpoddernet.model.GpodnetSubscriptionChange r8) throws de.danoeh.antennapod.core.storage.DownloadRequestException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:27:0x0057 in {9, 10, 11, 12, 19, 20, 21, 23, 26} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r4 = this;
        monitor-enter(r4);
        r0 = r8.getAdded();	 Catch:{ all -> 0x0054 }
        r0 = r0.iterator();	 Catch:{ all -> 0x0054 }
    L_0x0009:
        r1 = r0.hasNext();	 Catch:{ all -> 0x0054 }
        if (r1 == 0) goto L_0x0032;	 Catch:{ all -> 0x0054 }
    L_0x000f:
        r1 = r0.next();	 Catch:{ all -> 0x0054 }
        r1 = (java.lang.String) r1;	 Catch:{ all -> 0x0054 }
        r2 = r5.contains(r1);	 Catch:{ all -> 0x0054 }
        if (r2 != 0) goto L_0x0030;	 Catch:{ all -> 0x0054 }
    L_0x001b:
        r2 = r7.contains(r1);	 Catch:{ all -> 0x0054 }
        if (r2 != 0) goto L_0x002f;	 Catch:{ all -> 0x0054 }
    L_0x0021:
        r2 = new de.danoeh.antennapod.core.feed.Feed;	 Catch:{ all -> 0x0054 }
        r3 = 0;	 Catch:{ all -> 0x0054 }
        r2.<init>(r1, r3);	 Catch:{ all -> 0x0054 }
        r3 = de.danoeh.antennapod.core.storage.DownloadRequester.getInstance();	 Catch:{ all -> 0x0054 }
        r3.downloadFeed(r4, r2);	 Catch:{ all -> 0x0054 }
        goto L_0x0031;	 Catch:{ all -> 0x0054 }
    L_0x002f:
        goto L_0x0031;	 Catch:{ all -> 0x0054 }
    L_0x0031:
        goto L_0x0009;	 Catch:{ all -> 0x0054 }
    L_0x0032:
        r0 = r8.getRemoved();	 Catch:{ all -> 0x0054 }
        r0 = r0.iterator();	 Catch:{ all -> 0x0054 }
    L_0x003a:
        r1 = r0.hasNext();	 Catch:{ all -> 0x0054 }
        if (r1 == 0) goto L_0x0052;	 Catch:{ all -> 0x0054 }
    L_0x0040:
        r1 = r0.next();	 Catch:{ all -> 0x0054 }
        r1 = (java.lang.String) r1;	 Catch:{ all -> 0x0054 }
        r2 = r6.contains(r1);	 Catch:{ all -> 0x0054 }
        if (r2 != 0) goto L_0x0050;	 Catch:{ all -> 0x0054 }
    L_0x004c:
        de.danoeh.antennapod.core.storage.DBTasks.removeFeedWithDownloadUrl(r4, r1);	 Catch:{ all -> 0x0054 }
        goto L_0x0051;
    L_0x0051:
        goto L_0x003a;
    L_0x0052:
        monitor-exit(r4);
        return;
    L_0x0054:
        r5 = move-exception;
        monitor-exit(r4);
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.service.GpodnetSyncService.processSubscriptionChanges(java.util.List, java.util.Collection, java.util.Collection, de.danoeh.antennapod.core.gpoddernet.model.GpodnetSubscriptionChange):void");
    }

    private static void enqueueWork(Context context, Intent intent) {
        JobIntentService.enqueueWork(context, GpodnetSyncService.class, (int) JOB_ID, intent);
    }

    protected void onHandleWork(@NonNull Intent intent) {
        String action = intent.getStringExtra(ARG_ACTION);
        if (action != null) {
            int syncActionId;
            Object obj = -1;
            int hashCode = action.hashCode();
            if (hashCode != -1744995379) {
                if (hashCode != 29421060) {
                    if (hashCode == 1497029227 && action.equals(ACTION_SYNC_ACTIONS)) {
                        obj = 2;
                        switch (obj) {
                            case null:
                                syncSubscriptions = true;
                                syncActions = true;
                                break;
                            case 1:
                                syncSubscriptions = true;
                                break;
                            case 2:
                                syncActions = true;
                                break;
                            default:
                                Log.e(TAG, "Received invalid intent: action argument is invalid");
                                break;
                        }
                        if (!syncSubscriptions) {
                            if (!syncActions) {
                                return;
                            }
                        }
                        Log.d(TAG, String.format("Waiting %d milliseconds before uploading changes", new Object[]{Long.valueOf(5000)}));
                        syncActionId = syncActionCount.incrementAndGet();
                        Thread.sleep(5000);
                        if (syncActionId != syncActionCount.get()) {
                            sync();
                        }
                        return;
                    }
                } else if (action.equals(ACTION_SYNC_SUBSCRIPTIONS)) {
                    obj = 1;
                    switch (obj) {
                        case null:
                            syncSubscriptions = true;
                            syncActions = true;
                            break;
                        case 1:
                            syncSubscriptions = true;
                            break;
                        case 2:
                            syncActions = true;
                            break;
                        default:
                            Log.e(TAG, "Received invalid intent: action argument is invalid");
                            break;
                    }
                    if (syncSubscriptions) {
                        if (!syncActions) {
                            return;
                        }
                    }
                    Log.d(TAG, String.format("Waiting %d milliseconds before uploading changes", new Object[]{Long.valueOf(5000)}));
                    syncActionId = syncActionCount.incrementAndGet();
                    Thread.sleep(5000);
                    if (syncActionId != syncActionCount.get()) {
                        sync();
                    }
                    return;
                }
            } else if (action.equals(ACTION_SYNC)) {
                obj = null;
                switch (obj) {
                    case null:
                        syncSubscriptions = true;
                        syncActions = true;
                        break;
                    case 1:
                        syncSubscriptions = true;
                        break;
                    case 2:
                        syncActions = true;
                        break;
                    default:
                        Log.e(TAG, "Received invalid intent: action argument is invalid");
                        break;
                }
                if (syncSubscriptions) {
                    if (!syncActions) {
                        return;
                    }
                }
                Log.d(TAG, String.format("Waiting %d milliseconds before uploading changes", new Object[]{Long.valueOf(5000)}));
                syncActionId = syncActionCount.incrementAndGet();
                Thread.sleep(5000);
                if (syncActionId != syncActionCount.get()) {
                    sync();
                }
                return;
            }
            switch (obj) {
                case null:
                    syncSubscriptions = true;
                    syncActions = true;
                    break;
                case 1:
                    syncSubscriptions = true;
                    break;
                case 2:
                    syncActions = true;
                    break;
                default:
                    Log.e(TAG, "Received invalid intent: action argument is invalid");
                    break;
            }
            if (syncSubscriptions) {
                if (!syncActions) {
                    return;
                }
            }
            Log.d(TAG, String.format("Waiting %d milliseconds before uploading changes", new Object[]{Long.valueOf(5000)}));
            syncActionId = syncActionCount.incrementAndGet();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (syncActionId != syncActionCount.get()) {
                sync();
            }
            return;
        }
        Log.e(TAG, "Received invalid intent: action argument is null");
    }

    private synchronized GpodnetService tryLogin() throws GpodnetServiceException {
        if (this.service == null) {
            this.service = new GpodnetService();
            this.service.authenticate(GpodnetPreferences.getUsername(), GpodnetPreferences.getPassword());
        }
        return this.service;
    }

    private synchronized void sync() {
        boolean initialSync = true;
        if (GpodnetPreferences.loggedIn()) {
            if (NetworkUtils.networkAvailable()) {
                if (GpodnetPreferences.getLastSubscriptionSyncTimestamp() == 0) {
                    if (GpodnetPreferences.getLastEpisodeActionsSyncTimestamp() == 0) {
                        if (syncSubscriptions) {
                            syncSubscriptionChanges();
                            syncSubscriptions = false;
                        }
                        if (syncActions) {
                            if (!initialSync) {
                                syncEpisodeActions();
                            }
                            syncActions = false;
                        }
                        return;
                    }
                }
                initialSync = false;
                if (syncSubscriptions) {
                    syncSubscriptionChanges();
                    syncSubscriptions = false;
                }
                if (syncActions) {
                    if (!initialSync) {
                        syncEpisodeActions();
                    }
                    syncActions = false;
                }
                return;
            }
        }
        stopForeground(true);
        stopSelf();
    }

    private synchronized void syncSubscriptionChanges() {
        long timestamp = GpodnetPreferences.getLastSubscriptionSyncTimestamp();
        try {
            List<String> localSubscriptions = DBReader.getFeedListDownloadUrls();
            Collection<String> localAdded = GpodnetPreferences.getAddedFeedsCopy();
            Collection<String> localRemoved = GpodnetPreferences.getRemovedFeedsCopy();
            GpodnetService service = tryLogin();
            GpodnetSubscriptionChange subscriptionChanges = service.getSubscriptionChanges(GpodnetPreferences.getUsername(), GpodnetPreferences.getDeviceID(), timestamp);
            long newTimeStamp = subscriptionChanges.getTimestamp();
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Downloaded subscription changes: ");
            stringBuilder.append(subscriptionChanges);
            Log.d(str, stringBuilder.toString());
            processSubscriptionChanges(localSubscriptions, localAdded, localRemoved, subscriptionChanges);
            if (timestamp == 0) {
                localAdded = localSubscriptions;
                localAdded.removeAll(subscriptionChanges.getAdded());
                localRemoved.removeAll(subscriptionChanges.getRemoved());
            }
            if (localAdded.size() <= 0) {
                if (localRemoved.size() <= 0) {
                    GpodnetPreferences.setLastSubscriptionSyncTimestamp(newTimeStamp);
                    GpodnetPreferences.setLastSyncAttempt(true, System.currentTimeMillis());
                    clearErrorNotifications();
                }
            }
            Log.d(TAG, String.format("Uploading subscriptions, Added: %s\nRemoved: %s", new Object[]{localAdded, localRemoved}));
            GpodnetUploadChangesResponse uploadResponse = service.uploadChanges(GpodnetPreferences.getUsername(), GpodnetPreferences.getDeviceID(), localAdded, localRemoved);
            newTimeStamp = uploadResponse.timestamp;
            String str2 = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Upload changes response: ");
            stringBuilder2.append(uploadResponse);
            Log.d(str2, stringBuilder2.toString());
            GpodnetPreferences.removeAddedFeeds(localAdded);
            GpodnetPreferences.removeRemovedFeeds(localRemoved);
            GpodnetPreferences.setLastSubscriptionSyncTimestamp(newTimeStamp);
            GpodnetPreferences.setLastSyncAttempt(true, System.currentTimeMillis());
            clearErrorNotifications();
        } catch (GpodnetServiceException e) {
            e.printStackTrace();
            updateErrorNotification(e);
        } catch (DownloadRequestException e2) {
            e2.printStackTrace();
        }
    }

    private synchronized void syncEpisodeActions() {
        long timestamp = GpodnetPreferences.getLastEpisodeActionsSyncTimestamp();
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("last episode actions sync timestamp: ");
        stringBuilder.append(timestamp);
        Log.d(str, stringBuilder.toString());
        try {
            GpodnetService service = tryLogin();
            GpodnetEpisodeActionGetResponse getResponse = service.getEpisodeChanges(timestamp);
            long lastUpdate = getResponse.getTimestamp();
            String str2 = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Downloaded episode actions: ");
            stringBuilder2.append(getResponse);
            Log.d(str2, stringBuilder2.toString());
            List<GpodnetEpisodeAction> remoteActions = getResponse.getEpisodeActions();
            List<GpodnetEpisodeAction> localActions = GpodnetPreferences.getQueuedEpisodeActions();
            processEpisodeActions(localActions, remoteActions);
            if (localActions.size() > 0) {
                String str3 = TAG;
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("Uploading episode actions: ");
                stringBuilder3.append(localActions);
                Log.d(str3, stringBuilder3.toString());
                GpodnetEpisodeActionPostResponse postResponse = service.uploadEpisodeActions(localActions);
                lastUpdate = postResponse.timestamp;
                String str4 = TAG;
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append("Upload episode response: ");
                stringBuilder4.append(postResponse);
                Log.d(str4, stringBuilder4.toString());
                GpodnetPreferences.removeQueuedEpisodeActions(localActions);
            }
            GpodnetPreferences.setLastEpisodeActionsSyncTimestamp(lastUpdate);
            GpodnetPreferences.setLastSyncAttempt(true, System.currentTimeMillis());
            clearErrorNotifications();
        } catch (GpodnetServiceException e) {
            e.printStackTrace();
            updateErrorNotification(e);
        }
        return;
    }

    private void clearErrorNotifications() {
        NotificationManager nm = (NotificationManager) getSystemService("notification");
        nm.cancel(C0734R.id.notification_gpodnet_sync_error);
        nm.cancel(C0734R.id.notification_gpodnet_sync_autherror);
    }

    private void updateErrorNotification(GpodnetServiceException exception) {
        String title;
        String description;
        int id;
        Log.d(TAG, "Posting error notification");
        GpodnetPreferences.setLastSyncAttempt(false, System.currentTimeMillis());
        if (exception instanceof GpodnetServiceAuthenticationException) {
            title = getString(C0734R.string.gpodnetsync_auth_error_title);
            description = getString(C0734R.string.gpodnetsync_auth_error_descr);
            id = C0734R.id.notification_gpodnet_sync_autherror;
        } else if (UserPreferences.gpodnetNotificationsEnabled()) {
            title = getString(C0734R.string.gpodnetsync_error_title);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getString(C0734R.string.gpodnetsync_error_descr));
            stringBuilder.append(exception.getMessage());
            description = stringBuilder.toString();
            id = C0734R.id.notification_gpodnet_sync_error;
        } else {
            return;
        }
        ((NotificationManager) getSystemService("notification")).notify(id, new Builder(this, "error").setContentTitle(title).setContentText(description).setContentIntent(ClientConfig.gpodnetCallbacks.getGpodnetSyncServiceErrorNotificationPendingIntent(this)).setSmallIcon(C0734R.drawable.stat_notify_sync_error).setAutoCancel(true).setVisibility(1).build());
    }

    public static void sendSyncIntent(Context context) {
        if (GpodnetPreferences.loggedIn()) {
            Intent intent = new Intent(context, GpodnetSyncService.class);
            intent.putExtra(ARG_ACTION, ACTION_SYNC);
            enqueueWork(context, intent);
        }
    }

    public static void sendSyncSubscriptionsIntent(Context context) {
        if (GpodnetPreferences.loggedIn()) {
            Intent intent = new Intent(context, GpodnetSyncService.class);
            intent.putExtra(ARG_ACTION, ACTION_SYNC_SUBSCRIPTIONS);
            enqueueWork(context, intent);
        }
    }

    public static void sendSyncActionsIntent(Context context) {
        if (GpodnetPreferences.loggedIn()) {
            Intent intent = new Intent(context, GpodnetSyncService.class);
            intent.putExtra(ARG_ACTION, ACTION_SYNC_ACTIONS);
            enqueueWork(context, intent);
        }
    }
}

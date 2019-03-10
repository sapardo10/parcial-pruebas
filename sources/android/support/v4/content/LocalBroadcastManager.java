package android.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.HashMap;

public final class LocalBroadcastManager {
    private static final boolean DEBUG = false;
    static final int MSG_EXEC_PENDING_BROADCASTS = 1;
    private static final String TAG = "LocalBroadcastManager";
    private static LocalBroadcastManager mInstance;
    private static final Object mLock = new Object();
    private final HashMap<String, ArrayList<ReceiverRecord>> mActions = new HashMap();
    private final Context mAppContext;
    private final Handler mHandler;
    private final ArrayList<BroadcastRecord> mPendingBroadcasts = new ArrayList();
    private final HashMap<BroadcastReceiver, ArrayList<ReceiverRecord>> mReceivers = new HashMap();

    private static final class BroadcastRecord {
        final Intent intent;
        final ArrayList<ReceiverRecord> receivers;

        BroadcastRecord(Intent _intent, ArrayList<ReceiverRecord> _receivers) {
            this.intent = _intent;
            this.receivers = _receivers;
        }
    }

    private static final class ReceiverRecord {
        boolean broadcasting;
        boolean dead;
        final IntentFilter filter;
        final BroadcastReceiver receiver;

        ReceiverRecord(IntentFilter _filter, BroadcastReceiver _receiver) {
            this.filter = _filter;
            this.receiver = _receiver;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder(128);
            builder.append("Receiver{");
            builder.append(this.receiver);
            builder.append(" filter=");
            builder.append(this.filter);
            if (this.dead) {
                builder.append(" DEAD");
            }
            builder.append("}");
            return builder.toString();
        }
    }

    private void executePendingBroadcasts() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x004b in {7, 17, 18, 19, 20, 21, 25} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r9 = this;
        r0 = 0;
    L_0x0001:
        r1 = r9.mReceivers;
        monitor-enter(r1);
        r2 = r9.mPendingBroadcasts;	 Catch:{ all -> 0x0048 }
        r2 = r2.size();	 Catch:{ all -> 0x0048 }
        if (r2 > 0) goto L_0x000e;	 Catch:{ all -> 0x0048 }
    L_0x000c:
        monitor-exit(r1);	 Catch:{ all -> 0x0048 }
        return;	 Catch:{ all -> 0x0048 }
    L_0x000e:
        r0 = new android.support.v4.content.LocalBroadcastManager.BroadcastRecord[r2];	 Catch:{ all -> 0x0048 }
        r3 = r9.mPendingBroadcasts;	 Catch:{ all -> 0x0048 }
        r3.toArray(r0);	 Catch:{ all -> 0x0048 }
        r3 = r9.mPendingBroadcasts;	 Catch:{ all -> 0x0048 }
        r3.clear();	 Catch:{ all -> 0x0048 }
        monitor-exit(r1);	 Catch:{ all -> 0x0048 }
        r1 = 0;
    L_0x001c:
        r2 = r0.length;
        if (r1 >= r2) goto L_0x0047;
    L_0x001f:
        r2 = r0[r1];
        r3 = r2.receivers;
        r3 = r3.size();
        r4 = 0;
    L_0x0028:
        if (r4 >= r3) goto L_0x0044;
    L_0x002a:
        r5 = r2.receivers;
        r5 = r5.get(r4);
        r5 = (android.support.v4.content.LocalBroadcastManager.ReceiverRecord) r5;
        r6 = r5.dead;
        if (r6 != 0) goto L_0x0040;
    L_0x0036:
        r6 = r5.receiver;
        r7 = r9.mAppContext;
        r8 = r2.intent;
        r6.onReceive(r7, r8);
        goto L_0x0041;
    L_0x0041:
        r4 = r4 + 1;
        goto L_0x0028;
    L_0x0044:
        r1 = r1 + 1;
        goto L_0x001c;
    L_0x0047:
        goto L_0x0001;
    L_0x0048:
        r2 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0048 }
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.content.LocalBroadcastManager.executePendingBroadcasts():void");
    }

    public void registerReceiver(@android.support.annotation.NonNull android.content.BroadcastReceiver r9, @android.support.annotation.NonNull android.content.IntentFilter r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x0051 in {5, 6, 12, 13, 14, 16, 19} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r8 = this;
        r0 = r8.mReceivers;
        monitor-enter(r0);
        r1 = new android.support.v4.content.LocalBroadcastManager$ReceiverRecord;	 Catch:{ all -> 0x004e }
        r1.<init>(r10, r9);	 Catch:{ all -> 0x004e }
        r2 = r8.mReceivers;	 Catch:{ all -> 0x004e }
        r2 = r2.get(r9);	 Catch:{ all -> 0x004e }
        r2 = (java.util.ArrayList) r2;	 Catch:{ all -> 0x004e }
        r3 = 1;	 Catch:{ all -> 0x004e }
        if (r2 != 0) goto L_0x001f;	 Catch:{ all -> 0x004e }
    L_0x0013:
        r4 = new java.util.ArrayList;	 Catch:{ all -> 0x004e }
        r4.<init>(r3);	 Catch:{ all -> 0x004e }
        r2 = r4;	 Catch:{ all -> 0x004e }
        r4 = r8.mReceivers;	 Catch:{ all -> 0x004e }
        r4.put(r9, r2);	 Catch:{ all -> 0x004e }
        goto L_0x0020;	 Catch:{ all -> 0x004e }
    L_0x0020:
        r2.add(r1);	 Catch:{ all -> 0x004e }
        r4 = 0;	 Catch:{ all -> 0x004e }
    L_0x0024:
        r5 = r10.countActions();	 Catch:{ all -> 0x004e }
        if (r4 >= r5) goto L_0x004c;	 Catch:{ all -> 0x004e }
    L_0x002a:
        r5 = r10.getAction(r4);	 Catch:{ all -> 0x004e }
        r6 = r8.mActions;	 Catch:{ all -> 0x004e }
        r6 = r6.get(r5);	 Catch:{ all -> 0x004e }
        r6 = (java.util.ArrayList) r6;	 Catch:{ all -> 0x004e }
        if (r6 != 0) goto L_0x0044;	 Catch:{ all -> 0x004e }
    L_0x0038:
        r7 = new java.util.ArrayList;	 Catch:{ all -> 0x004e }
        r7.<init>(r3);	 Catch:{ all -> 0x004e }
        r6 = r7;	 Catch:{ all -> 0x004e }
        r7 = r8.mActions;	 Catch:{ all -> 0x004e }
        r7.put(r5, r6);	 Catch:{ all -> 0x004e }
        goto L_0x0045;	 Catch:{ all -> 0x004e }
    L_0x0045:
        r6.add(r1);	 Catch:{ all -> 0x004e }
        r4 = r4 + 1;	 Catch:{ all -> 0x004e }
        goto L_0x0024;	 Catch:{ all -> 0x004e }
    L_0x004c:
        monitor-exit(r0);	 Catch:{ all -> 0x004e }
        return;	 Catch:{ all -> 0x004e }
    L_0x004e:
        r1 = move-exception;	 Catch:{ all -> 0x004e }
        monitor-exit(r0);	 Catch:{ all -> 0x004e }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.content.LocalBroadcastManager.registerReceiver(android.content.BroadcastReceiver, android.content.IntentFilter):void");
    }

    public boolean sendBroadcast(@android.support.annotation.NonNull android.content.Intent r19) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:75:0x01e3 in {5, 6, 9, 13, 19, 24, 25, 30, 33, 34, 35, 41, 42, 43, 44, 45, 46, 47, 48, 49, 55, 58, 59, 63, 64, 65, 69, 74} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r18 = this;
        r1 = r18;
        r2 = r19;
        r3 = r1.mReceivers;
        monitor-enter(r3);
        r5 = r19.getAction();	 Catch:{ all -> 0x01de }
        r0 = r1.mAppContext;	 Catch:{ all -> 0x01de }
        r0 = r0.getContentResolver();	 Catch:{ all -> 0x01de }
        r0 = r2.resolveTypeIfNeeded(r0);	 Catch:{ all -> 0x01de }
        r8 = r19.getData();	 Catch:{ all -> 0x01de }
        r4 = r19.getScheme();	 Catch:{ all -> 0x01de }
        r11 = r4;	 Catch:{ all -> 0x01de }
        r9 = r19.getCategories();	 Catch:{ all -> 0x01de }
        r4 = r19.getFlags();	 Catch:{ all -> 0x01de }
        r4 = r4 & 8;	 Catch:{ all -> 0x01de }
        r12 = 0;	 Catch:{ all -> 0x01de }
        if (r4 == 0) goto L_0x002e;	 Catch:{ all -> 0x01de }
    L_0x002c:
        r4 = 1;	 Catch:{ all -> 0x01de }
        goto L_0x002f;	 Catch:{ all -> 0x01de }
    L_0x002e:
        r4 = 0;	 Catch:{ all -> 0x01de }
    L_0x002f:
        r14 = r4;	 Catch:{ all -> 0x01de }
        if (r14 == 0) goto L_0x0058;	 Catch:{ all -> 0x01de }
    L_0x0032:
        r4 = "LocalBroadcastManager";	 Catch:{ all -> 0x01de }
        r6 = new java.lang.StringBuilder;	 Catch:{ all -> 0x01de }
        r6.<init>();	 Catch:{ all -> 0x01de }
        r7 = "Resolving type ";	 Catch:{ all -> 0x01de }
        r6.append(r7);	 Catch:{ all -> 0x01de }
        r6.append(r0);	 Catch:{ all -> 0x01de }
        r7 = " scheme ";	 Catch:{ all -> 0x01de }
        r6.append(r7);	 Catch:{ all -> 0x01de }
        r6.append(r11);	 Catch:{ all -> 0x01de }
        r7 = " of intent ";	 Catch:{ all -> 0x01de }
        r6.append(r7);	 Catch:{ all -> 0x01de }
        r6.append(r2);	 Catch:{ all -> 0x01de }
        r6 = r6.toString();	 Catch:{ all -> 0x01de }
        android.util.Log.v(r4, r6);	 Catch:{ all -> 0x01de }
    L_0x0058:
        r4 = r1.mActions;	 Catch:{ all -> 0x01de }
        r6 = r19.getAction();	 Catch:{ all -> 0x01de }
        r4 = r4.get(r6);	 Catch:{ all -> 0x01de }
        r4 = (java.util.ArrayList) r4;	 Catch:{ all -> 0x01de }
        r15 = r4;	 Catch:{ all -> 0x01de }
        if (r15 == 0) goto L_0x01d5;	 Catch:{ all -> 0x01de }
    L_0x0067:
        if (r14 == 0) goto L_0x007f;	 Catch:{ all -> 0x01de }
    L_0x0069:
        r4 = "LocalBroadcastManager";	 Catch:{ all -> 0x01de }
        r6 = new java.lang.StringBuilder;	 Catch:{ all -> 0x01de }
        r6.<init>();	 Catch:{ all -> 0x01de }
        r7 = "Action list: ";	 Catch:{ all -> 0x01de }
        r6.append(r7);	 Catch:{ all -> 0x01de }
        r6.append(r15);	 Catch:{ all -> 0x01de }
        r6 = r6.toString();	 Catch:{ all -> 0x01de }
        android.util.Log.v(r4, r6);	 Catch:{ all -> 0x01de }
    L_0x007f:
        r4 = 0;	 Catch:{ all -> 0x01de }
        r6 = r12;	 Catch:{ all -> 0x01de }
        r7 = r4;	 Catch:{ all -> 0x01de }
        r10 = r6;	 Catch:{ all -> 0x01de }
        r4 = r15.size();	 Catch:{ all -> 0x01de }
        if (r10 >= r4) goto L_0x0184;	 Catch:{ all -> 0x01de }
        r4 = r15.get(r10);	 Catch:{ all -> 0x01de }
        r4 = (android.support.v4.content.LocalBroadcastManager.ReceiverRecord) r4;	 Catch:{ all -> 0x01de }
        r6 = r4;	 Catch:{ all -> 0x01de }
        if (r14 == 0) goto L_0x00ba;	 Catch:{ all -> 0x01de }
        r4 = "LocalBroadcastManager";	 Catch:{ all -> 0x01de }
        r12 = new java.lang.StringBuilder;	 Catch:{ all -> 0x01de }
        r12.<init>();	 Catch:{ all -> 0x01de }
        r13 = "Matching against filter ";	 Catch:{ all -> 0x01de }
        r12.append(r13);	 Catch:{ all -> 0x01de }
        r13 = r6.filter;	 Catch:{ all -> 0x01de }
        r12.append(r13);	 Catch:{ all -> 0x01de }
        r12 = r12.toString();	 Catch:{ all -> 0x01de }
        android.util.Log.v(r4, r12);	 Catch:{ all -> 0x01de }
        r4 = r6.broadcasting;	 Catch:{ all -> 0x01de }
        if (r4 == 0) goto L_0x00de;	 Catch:{ all -> 0x01de }
        if (r14 == 0) goto L_0x00d6;	 Catch:{ all -> 0x01de }
        r4 = "LocalBroadcastManager";	 Catch:{ all -> 0x01de }
        r12 = "  Filter's target already added";	 Catch:{ all -> 0x01de }
        android.util.Log.v(r4, r12);	 Catch:{ all -> 0x01de }
        r16 = r0;	 Catch:{ all -> 0x01de }
        r0 = r7;	 Catch:{ all -> 0x01de }
        r17 = r10;	 Catch:{ all -> 0x01de }
        goto L_0x017b;	 Catch:{ all -> 0x01de }
        r16 = r0;	 Catch:{ all -> 0x01de }
        r0 = r7;	 Catch:{ all -> 0x01de }
        r17 = r10;	 Catch:{ all -> 0x01de }
        goto L_0x017b;	 Catch:{ all -> 0x01de }
        r4 = r6.filter;	 Catch:{ all -> 0x01de }
        r12 = "LocalBroadcastManager";	 Catch:{ all -> 0x01de }
        r13 = r6;	 Catch:{ all -> 0x01de }
        r6 = r0;	 Catch:{ all -> 0x01de }
        r16 = r0;	 Catch:{ all -> 0x01de }
        r0 = r7;	 Catch:{ all -> 0x01de }
        r7 = r11;	 Catch:{ all -> 0x01de }
        r17 = r10;	 Catch:{ all -> 0x01de }
        r10 = r12;	 Catch:{ all -> 0x01de }
        r4 = r4.match(r5, r6, r7, r8, r9, r10);	 Catch:{ all -> 0x01de }
        if (r4 < 0) goto L_0x0137;	 Catch:{ all -> 0x01de }
        if (r14 == 0) goto L_0x011c;	 Catch:{ all -> 0x01de }
        r6 = "LocalBroadcastManager";	 Catch:{ all -> 0x01de }
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x01de }
        r7.<init>();	 Catch:{ all -> 0x01de }
        r10 = "  Filter matched!  match=0x";	 Catch:{ all -> 0x01de }
        r7.append(r10);	 Catch:{ all -> 0x01de }
        r10 = java.lang.Integer.toHexString(r4);	 Catch:{ all -> 0x01de }
        r7.append(r10);	 Catch:{ all -> 0x01de }
        r7 = r7.toString();	 Catch:{ all -> 0x01de }
        android.util.Log.v(r6, r7);	 Catch:{ all -> 0x01de }
        if (r0 != 0) goto L_0x012b;	 Catch:{ all -> 0x01de }
        r6 = new java.util.ArrayList;	 Catch:{ all -> 0x01de }
        r6.<init>();	 Catch:{ all -> 0x01de }
        r7 = r6;	 Catch:{ all -> 0x01de }
        r0 = r7;	 Catch:{ all -> 0x01de }
        goto L_0x012c;	 Catch:{ all -> 0x01de }
        r0.add(r13);	 Catch:{ all -> 0x01de }
        r6 = 1;	 Catch:{ all -> 0x01de }
        r13.broadcasting = r6;	 Catch:{ all -> 0x01de }
        r7 = r0;	 Catch:{ all -> 0x01de }
        goto L_0x017c;	 Catch:{ all -> 0x01de }
        if (r14 == 0) goto L_0x017a;	 Catch:{ all -> 0x01de }
        switch(r4) {
            case -4: goto L_0x0153;
            case -3: goto L_0x014e;
            case -2: goto L_0x0149;
            case -1: goto L_0x0143;
            default: goto L_0x013e;
        };	 Catch:{ all -> 0x01de }
        r6 = "unknown reason";	 Catch:{ all -> 0x01de }
        goto L_0x0158;	 Catch:{ all -> 0x01de }
        r6 = "type";	 Catch:{ all -> 0x01de }
        goto L_0x015a;	 Catch:{ all -> 0x01de }
        r6 = "data";	 Catch:{ all -> 0x01de }
        goto L_0x015a;	 Catch:{ all -> 0x01de }
        r6 = "action";	 Catch:{ all -> 0x01de }
        goto L_0x015a;	 Catch:{ all -> 0x01de }
        r6 = "category";	 Catch:{ all -> 0x01de }
        goto L_0x015a;	 Catch:{ all -> 0x01de }
        r7 = "LocalBroadcastManager";	 Catch:{ all -> 0x01de }
        r10 = new java.lang.StringBuilder;	 Catch:{ all -> 0x01de }
        r10.<init>();	 Catch:{ all -> 0x01de }
        r12 = "  Filter did not match: ";	 Catch:{ all -> 0x01de }
        r10.append(r12);	 Catch:{ all -> 0x01de }
        r10.append(r6);	 Catch:{ all -> 0x01de }
        r10 = r10.toString();	 Catch:{ all -> 0x01de }
        android.util.Log.v(r7, r10);	 Catch:{ all -> 0x01de }
        goto L_0x017b;	 Catch:{ all -> 0x01de }
    L_0x017b:
        r7 = r0;	 Catch:{ all -> 0x01de }
        r10 = r17 + 1;	 Catch:{ all -> 0x01de }
        r0 = r16;	 Catch:{ all -> 0x01de }
        r12 = 0;	 Catch:{ all -> 0x01de }
        goto L_0x0084;	 Catch:{ all -> 0x01de }
    L_0x0184:
        r16 = r0;	 Catch:{ all -> 0x01de }
        r0 = r7;	 Catch:{ all -> 0x01de }
        r17 = r10;	 Catch:{ all -> 0x01de }
        if (r0 == 0) goto L_0x01d3;	 Catch:{ all -> 0x01de }
        r4 = 0;	 Catch:{ all -> 0x01de }
        r6 = r4;	 Catch:{ all -> 0x01de }
        r4 = r0.size();	 Catch:{ all -> 0x01de }
        if (r6 >= r4) goto L_0x01a9;	 Catch:{ all -> 0x01de }
        r4 = r0.get(r6);	 Catch:{ all -> 0x01de }
        r4 = (android.support.v4.content.LocalBroadcastManager.ReceiverRecord) r4;	 Catch:{ all -> 0x01de }
        r7 = 0;	 Catch:{ all -> 0x01de }
        r4.broadcasting = r7;	 Catch:{ all -> 0x01de }
        r6 = r6 + 1;	 Catch:{ all -> 0x01de }
        goto L_0x0191;	 Catch:{ all -> 0x01de }
        r4 = r1.mPendingBroadcasts;	 Catch:{ all -> 0x01de }
        r6 = new android.support.v4.content.LocalBroadcastManager$BroadcastRecord;	 Catch:{ all -> 0x01de }
        r6.<init>(r2, r0);	 Catch:{ all -> 0x01de }
        r4.add(r6);	 Catch:{ all -> 0x01de }
        r4 = r1.mHandler;	 Catch:{ all -> 0x01de }
        r6 = 1;	 Catch:{ all -> 0x01de }
        r4 = r4.hasMessages(r6);	 Catch:{ all -> 0x01de }
        if (r4 != 0) goto L_0x01cd;	 Catch:{ all -> 0x01de }
        r4 = r1.mHandler;	 Catch:{ all -> 0x01de }
        r4.sendEmptyMessage(r6);	 Catch:{ all -> 0x01de }
        goto L_0x01ce;	 Catch:{ all -> 0x01de }
        monitor-exit(r3);	 Catch:{ all -> 0x01de }
        r3 = 1;	 Catch:{ all -> 0x01de }
        return r3;	 Catch:{ all -> 0x01de }
        goto L_0x01d8;	 Catch:{ all -> 0x01de }
    L_0x01d5:
        r16 = r0;	 Catch:{ all -> 0x01de }
        monitor-exit(r3);	 Catch:{ all -> 0x01de }
        r0 = 0;	 Catch:{ all -> 0x01de }
        return r0;	 Catch:{ all -> 0x01de }
    L_0x01de:
        r0 = move-exception;	 Catch:{ all -> 0x01de }
        monitor-exit(r3);	 Catch:{ all -> 0x01de }
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.content.LocalBroadcastManager.sendBroadcast(android.content.Intent):boolean");
    }

    public void unregisterReceiver(@android.support.annotation.NonNull android.content.BroadcastReceiver r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:33:0x006c in {6, 18, 19, 20, 23, 24, 25, 26, 27, 29, 32} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r11 = this;
        r0 = r11.mReceivers;
        monitor-enter(r0);
        r1 = r11.mReceivers;	 Catch:{ all -> 0x0069 }
        r1 = r1.remove(r12);	 Catch:{ all -> 0x0069 }
        r1 = (java.util.ArrayList) r1;	 Catch:{ all -> 0x0069 }
        if (r1 != 0) goto L_0x000f;	 Catch:{ all -> 0x0069 }
    L_0x000d:
        monitor-exit(r0);	 Catch:{ all -> 0x0069 }
        return;	 Catch:{ all -> 0x0069 }
    L_0x000f:
        r2 = r1.size();	 Catch:{ all -> 0x0069 }
        r3 = 1;	 Catch:{ all -> 0x0069 }
        r2 = r2 - r3;	 Catch:{ all -> 0x0069 }
    L_0x0015:
        if (r2 < 0) goto L_0x0067;	 Catch:{ all -> 0x0069 }
    L_0x0017:
        r4 = r1.get(r2);	 Catch:{ all -> 0x0069 }
        r4 = (android.support.v4.content.LocalBroadcastManager.ReceiverRecord) r4;	 Catch:{ all -> 0x0069 }
        r4.dead = r3;	 Catch:{ all -> 0x0069 }
        r5 = 0;	 Catch:{ all -> 0x0069 }
    L_0x0020:
        r6 = r4.filter;	 Catch:{ all -> 0x0069 }
        r6 = r6.countActions();	 Catch:{ all -> 0x0069 }
        if (r5 >= r6) goto L_0x0064;	 Catch:{ all -> 0x0069 }
    L_0x0028:
        r6 = r4.filter;	 Catch:{ all -> 0x0069 }
        r6 = r6.getAction(r5);	 Catch:{ all -> 0x0069 }
        r7 = r11.mActions;	 Catch:{ all -> 0x0069 }
        r7 = r7.get(r6);	 Catch:{ all -> 0x0069 }
        r7 = (java.util.ArrayList) r7;	 Catch:{ all -> 0x0069 }
        if (r7 == 0) goto L_0x0060;	 Catch:{ all -> 0x0069 }
    L_0x0038:
        r8 = r7.size();	 Catch:{ all -> 0x0069 }
        r8 = r8 - r3;	 Catch:{ all -> 0x0069 }
    L_0x003d:
        if (r8 < 0) goto L_0x0053;	 Catch:{ all -> 0x0069 }
    L_0x003f:
        r9 = r7.get(r8);	 Catch:{ all -> 0x0069 }
        r9 = (android.support.v4.content.LocalBroadcastManager.ReceiverRecord) r9;	 Catch:{ all -> 0x0069 }
        r10 = r9.receiver;	 Catch:{ all -> 0x0069 }
        if (r10 != r12) goto L_0x004f;	 Catch:{ all -> 0x0069 }
    L_0x0049:
        r9.dead = r3;	 Catch:{ all -> 0x0069 }
        r7.remove(r8);	 Catch:{ all -> 0x0069 }
        goto L_0x0050;	 Catch:{ all -> 0x0069 }
    L_0x0050:
        r8 = r8 + -1;	 Catch:{ all -> 0x0069 }
        goto L_0x003d;	 Catch:{ all -> 0x0069 }
    L_0x0053:
        r8 = r7.size();	 Catch:{ all -> 0x0069 }
        if (r8 > 0) goto L_0x005f;	 Catch:{ all -> 0x0069 }
    L_0x0059:
        r8 = r11.mActions;	 Catch:{ all -> 0x0069 }
        r8.remove(r6);	 Catch:{ all -> 0x0069 }
        goto L_0x0061;	 Catch:{ all -> 0x0069 }
    L_0x005f:
        goto L_0x0061;	 Catch:{ all -> 0x0069 }
    L_0x0061:
        r5 = r5 + 1;	 Catch:{ all -> 0x0069 }
        goto L_0x0020;	 Catch:{ all -> 0x0069 }
    L_0x0064:
        r2 = r2 + -1;	 Catch:{ all -> 0x0069 }
        goto L_0x0015;	 Catch:{ all -> 0x0069 }
    L_0x0067:
        monitor-exit(r0);	 Catch:{ all -> 0x0069 }
        return;	 Catch:{ all -> 0x0069 }
    L_0x0069:
        r1 = move-exception;	 Catch:{ all -> 0x0069 }
        monitor-exit(r0);	 Catch:{ all -> 0x0069 }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.content.LocalBroadcastManager.unregisterReceiver(android.content.BroadcastReceiver):void");
    }

    @NonNull
    public static LocalBroadcastManager getInstance(@NonNull Context context) {
        LocalBroadcastManager localBroadcastManager;
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new LocalBroadcastManager(context.getApplicationContext());
            }
            localBroadcastManager = mInstance;
        }
        return localBroadcastManager;
    }

    private LocalBroadcastManager(Context context) {
        this.mAppContext = context;
        this.mHandler = new Handler(context.getMainLooper()) {
            public void handleMessage(Message msg) {
                if (msg.what != 1) {
                    super.handleMessage(msg);
                } else {
                    LocalBroadcastManager.this.executePendingBroadcasts();
                }
            }
        };
    }

    public void sendBroadcastSync(@NonNull Intent intent) {
        if (sendBroadcast(intent)) {
            executePendingBroadcasts();
        }
    }
}

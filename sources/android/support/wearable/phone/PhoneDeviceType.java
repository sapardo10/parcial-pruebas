package android.support.wearable.phone;

import android.net.Uri;
import android.net.Uri.Builder;
import de.danoeh.antennapod.core.syndication.namespace.NSContent;

public final class PhoneDeviceType {
    private static final int ANDROID_BLUETOOTH_MODE = 1;
    private static final String BLUETOOTH_MODE = "bluetooth_mode";
    private static final Uri BLUETOOTH_MODE_URI = new Builder().scheme(NSContent.NSTAG).authority("com.google.android.wearable.settings").path(BLUETOOTH_MODE).build();
    public static final int DEVICE_TYPE_ANDROID = 1;
    public static final int DEVICE_TYPE_ERROR_UNKNOWN = 0;
    public static final int DEVICE_TYPE_IOS = 2;
    private static final int IOS_BLUETOOTH_MODE = 2;

    public static int getPhoneDeviceType(android.content.Context r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x0047 in {2, 12, 14, 16, 17, 19, 22} preds:[]
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
        r0 = r6.getContentResolver();
        r1 = BLUETOOTH_MODE_URI;
        r2 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r0 = r0.query(r1, r2, r3, r4, r5);
        r1 = 0;
        if (r0 != 0) goto L_0x0012;
    L_0x0011:
        return r1;
    L_0x0013:
        r2 = r0.moveToNext();	 Catch:{ all -> 0x0042 }
        if (r2 == 0) goto L_0x003d;	 Catch:{ all -> 0x0042 }
    L_0x0019:
        r2 = "bluetooth_mode";	 Catch:{ all -> 0x0042 }
        r3 = r0.getString(r1);	 Catch:{ all -> 0x0042 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0042 }
        if (r2 == 0) goto L_0x003c;	 Catch:{ all -> 0x0042 }
    L_0x0025:
        r2 = 1;	 Catch:{ all -> 0x0042 }
        r3 = r0.getInt(r2);	 Catch:{ all -> 0x0042 }
        switch(r3) {
            case 1: goto L_0x0037;
            case 2: goto L_0x0032;
            default: goto L_0x002d;
        };
        r0.close();
        return r1;
    L_0x0032:
        r1 = 2;
        r0.close();
        return r1;
        r0.close();
        return r2;
    L_0x003c:
        goto L_0x0013;
    L_0x003d:
        r0.close();
        return r1;
    L_0x0042:
        r1 = move-exception;
        r0.close();
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.wearable.phone.PhoneDeviceType.getPhoneDeviceType(android.content.Context):int");
    }
}

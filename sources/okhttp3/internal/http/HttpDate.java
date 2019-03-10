package okhttp3.internal.http;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import okhttp3.internal.Util;

public final class HttpDate {
    private static final DateFormat[] BROWSER_COMPATIBLE_DATE_FORMATS = new DateFormat[BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS.length];
    private static final String[] BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS = new String[]{"EEE, dd MMM yyyy HH:mm:ss zzz", "EEEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy", "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z", "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z", "EEE MMM d yyyy HH:mm:ss z"};
    public static final long MAX_DATE = 253402300799999L;
    private static final ThreadLocal<DateFormat> STANDARD_DATE_FORMAT = new C11291();

    /* renamed from: okhttp3.internal.http.HttpDate$1 */
    class C11291 extends ThreadLocal<DateFormat> {
        C11291() {
        }

        protected DateFormat initialValue() {
            DateFormat rfc1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            rfc1123.setLenient(false);
            rfc1123.setTimeZone(Util.UTC);
            return rfc1123;
        }
    }

    public static java.util.Date parse(java.lang.String r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x0064 in {2, 5, 14, 15, 19, 20, 22, 25} preds:[]
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
        r0 = r11.length();
        r1 = 0;
        if (r0 != 0) goto L_0x0008;
    L_0x0007:
        return r1;
    L_0x0008:
        r0 = new java.text.ParsePosition;
        r2 = 0;
        r0.<init>(r2);
        r3 = STANDARD_DATE_FORMAT;
        r3 = r3.get();
        r3 = (java.text.DateFormat) r3;
        r3 = r3.parse(r11, r0);
        r4 = r0.getIndex();
        r5 = r11.length();
        if (r4 != r5) goto L_0x0025;
    L_0x0024:
        return r3;
    L_0x0025:
        r4 = BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS;
        monitor-enter(r4);
        r5 = 0;
        r6 = BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS;	 Catch:{ all -> 0x0061 }
        r6 = r6.length;	 Catch:{ all -> 0x0061 }
    L_0x002c:
        if (r5 >= r6) goto L_0x005f;	 Catch:{ all -> 0x0061 }
    L_0x002e:
        r7 = BROWSER_COMPATIBLE_DATE_FORMATS;	 Catch:{ all -> 0x0061 }
        r7 = r7[r5];	 Catch:{ all -> 0x0061 }
        if (r7 != 0) goto L_0x004a;	 Catch:{ all -> 0x0061 }
    L_0x0034:
        r8 = new java.text.SimpleDateFormat;	 Catch:{ all -> 0x0061 }
        r9 = BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS;	 Catch:{ all -> 0x0061 }
        r9 = r9[r5];	 Catch:{ all -> 0x0061 }
        r10 = java.util.Locale.US;	 Catch:{ all -> 0x0061 }
        r8.<init>(r9, r10);	 Catch:{ all -> 0x0061 }
        r7 = r8;	 Catch:{ all -> 0x0061 }
        r8 = okhttp3.internal.Util.UTC;	 Catch:{ all -> 0x0061 }
        r7.setTimeZone(r8);	 Catch:{ all -> 0x0061 }
        r8 = BROWSER_COMPATIBLE_DATE_FORMATS;	 Catch:{ all -> 0x0061 }
        r8[r5] = r7;	 Catch:{ all -> 0x0061 }
        goto L_0x004b;	 Catch:{ all -> 0x0061 }
    L_0x004b:
        r0.setIndex(r2);	 Catch:{ all -> 0x0061 }
        r8 = r7.parse(r11, r0);	 Catch:{ all -> 0x0061 }
        r3 = r8;	 Catch:{ all -> 0x0061 }
        r8 = r0.getIndex();	 Catch:{ all -> 0x0061 }
        if (r8 == 0) goto L_0x005b;	 Catch:{ all -> 0x0061 }
    L_0x0059:
        monitor-exit(r4);	 Catch:{ all -> 0x0061 }
        return r3;	 Catch:{ all -> 0x0061 }
        r5 = r5 + 1;	 Catch:{ all -> 0x0061 }
        goto L_0x002c;	 Catch:{ all -> 0x0061 }
    L_0x005f:
        monitor-exit(r4);	 Catch:{ all -> 0x0061 }
        return r1;	 Catch:{ all -> 0x0061 }
    L_0x0061:
        r1 = move-exception;	 Catch:{ all -> 0x0061 }
        monitor-exit(r4);	 Catch:{ all -> 0x0061 }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http.HttpDate.parse(java.lang.String):java.util.Date");
    }

    public static String format(Date value) {
        return ((DateFormat) STANDARD_DATE_FORMAT.get()).format(value);
    }

    private HttpDate() {
    }
}

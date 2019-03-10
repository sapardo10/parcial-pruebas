package android.support.v4.provider;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.GuardedBy;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.support.v4.graphics.TypefaceCompat;
import android.support.v4.graphics.TypefaceCompatUtil;
import android.support.v4.provider.SelfDestructiveThread.ReplyCallback;
import android.support.v4.util.LruCache;
import android.support.v4.util.Preconditions;
import android.support.v4.util.SimpleArrayMap;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontsContractCompat {
    private static final int BACKGROUND_THREAD_KEEP_ALIVE_DURATION_MS = 10000;
    @RestrictTo({Scope.LIBRARY_GROUP})
    public static final String PARCEL_FONT_RESULTS = "font_results";
    @RestrictTo({Scope.LIBRARY_GROUP})
    static final int RESULT_CODE_PROVIDER_NOT_FOUND = -1;
    @RestrictTo({Scope.LIBRARY_GROUP})
    static final int RESULT_CODE_WRONG_CERTIFICATES = -2;
    private static final String TAG = "FontsContractCompat";
    private static final SelfDestructiveThread sBackgroundThread = new SelfDestructiveThread("fonts", 10, 10000);
    private static final Comparator<byte[]> sByteArrayComparator = new C02185();
    private static final Object sLock = new Object();
    @GuardedBy("sLock")
    private static final SimpleArrayMap<String, ArrayList<ReplyCallback<TypefaceResult>>> sPendingReplies = new SimpleArrayMap();
    private static final LruCache<String, Typeface> sTypefaceCache = new LruCache(16);

    /* renamed from: android.support.v4.provider.FontsContractCompat$5 */
    static class C02185 implements Comparator<byte[]> {
        C02185() {
        }

        public int compare(byte[] l, byte[] r) {
            if (l.length != r.length) {
                return l.length - r.length;
            }
            for (int i = 0; i < l.length; i++) {
                if (l[i] != r[i]) {
                    return l[i] - r[i];
                }
            }
            return 0;
        }
    }

    public static final class Columns implements BaseColumns {
        public static final String FILE_ID = "file_id";
        public static final String ITALIC = "font_italic";
        public static final String RESULT_CODE = "result_code";
        public static final int RESULT_CODE_FONT_NOT_FOUND = 1;
        public static final int RESULT_CODE_FONT_UNAVAILABLE = 2;
        public static final int RESULT_CODE_MALFORMED_QUERY = 3;
        public static final int RESULT_CODE_OK = 0;
        public static final String TTC_INDEX = "font_ttc_index";
        public static final String VARIATION_SETTINGS = "font_variation_settings";
        public static final String WEIGHT = "font_weight";
    }

    public static class FontFamilyResult {
        public static final int STATUS_OK = 0;
        public static final int STATUS_UNEXPECTED_DATA_PROVIDED = 2;
        public static final int STATUS_WRONG_CERTIFICATES = 1;
        private final FontInfo[] mFonts;
        private final int mStatusCode;

        @RestrictTo({Scope.LIBRARY_GROUP})
        public FontFamilyResult(int statusCode, @Nullable FontInfo[] fonts) {
            this.mStatusCode = statusCode;
            this.mFonts = fonts;
        }

        public int getStatusCode() {
            return this.mStatusCode;
        }

        public FontInfo[] getFonts() {
            return this.mFonts;
        }
    }

    public static class FontInfo {
        private final boolean mItalic;
        private final int mResultCode;
        private final int mTtcIndex;
        private final Uri mUri;
        private final int mWeight;

        @RestrictTo({Scope.LIBRARY_GROUP})
        public FontInfo(@NonNull Uri uri, @IntRange(from = 0) int ttcIndex, @IntRange(from = 1, to = 1000) int weight, boolean italic, int resultCode) {
            this.mUri = (Uri) Preconditions.checkNotNull(uri);
            this.mTtcIndex = ttcIndex;
            this.mWeight = weight;
            this.mItalic = italic;
            this.mResultCode = resultCode;
        }

        @NonNull
        public Uri getUri() {
            return this.mUri;
        }

        @IntRange(from = 0)
        public int getTtcIndex() {
            return this.mTtcIndex;
        }

        @IntRange(from = 1, to = 1000)
        public int getWeight() {
            return this.mWeight;
        }

        public boolean isItalic() {
            return this.mItalic;
        }

        public int getResultCode() {
            return this.mResultCode;
        }
    }

    public static class FontRequestCallback {
        public static final int FAIL_REASON_FONT_LOAD_ERROR = -3;
        public static final int FAIL_REASON_FONT_NOT_FOUND = 1;
        public static final int FAIL_REASON_FONT_UNAVAILABLE = 2;
        public static final int FAIL_REASON_MALFORMED_QUERY = 3;
        public static final int FAIL_REASON_PROVIDER_NOT_FOUND = -1;
        public static final int FAIL_REASON_SECURITY_VIOLATION = -4;
        public static final int FAIL_REASON_WRONG_CERTIFICATES = -2;
        @RestrictTo({Scope.LIBRARY_GROUP})
        public static final int RESULT_OK = 0;

        public void onTypefaceRetrieved(Typeface typeface) {
        }

        public void onTypefaceRequestFailed(int reason) {
        }
    }

    private static final class TypefaceResult {
        final int mResult;
        final Typeface mTypeface;

        TypefaceResult(@Nullable Typeface typeface, int result) {
            this.mTypeface = typeface;
            this.mResult = result;
        }
    }

    @android.support.annotation.VisibleForTesting
    @android.support.annotation.NonNull
    static android.support.v4.provider.FontsContractCompat.FontInfo[] getFontFromProvider(android.content.Context r21, android.support.v4.provider.FontRequest r22, java.lang.String r23, android.os.CancellationSignal r24) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:44:0x0160 in {4, 5, 14, 15, 17, 18, 20, 21, 23, 24, 28, 29, 31, 32, 33, 35, 36, 38, 41, 42, 43} preds:[]
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
        r1 = r23;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r2 = r0;
        r0 = new android.net.Uri$Builder;
        r0.<init>();
        r3 = "content";
        r0 = r0.scheme(r3);
        r0 = r0.authority(r1);
        r10 = r0.build();
        r0 = new android.net.Uri$Builder;
        r0.<init>();
        r3 = "content";
        r0 = r0.scheme(r3);
        r0 = r0.authority(r1);
        r3 = "file";
        r0 = r0.appendPath(r3);
        r11 = r0.build();
        r12 = 0;
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x0157 }
        r3 = 16;	 Catch:{ all -> 0x0157 }
        r4 = 6;	 Catch:{ all -> 0x0157 }
        r5 = 5;	 Catch:{ all -> 0x0157 }
        r6 = 4;	 Catch:{ all -> 0x0157 }
        r7 = 3;	 Catch:{ all -> 0x0157 }
        r8 = 2;	 Catch:{ all -> 0x0157 }
        r9 = 7;	 Catch:{ all -> 0x0157 }
        r13 = 1;	 Catch:{ all -> 0x0157 }
        r14 = 0;	 Catch:{ all -> 0x0157 }
        if (r0 <= r3) goto L_0x007c;	 Catch:{ all -> 0x0157 }
    L_0x0043:
        r3 = r21.getContentResolver();	 Catch:{ all -> 0x0157 }
        r0 = new java.lang.String[r9];	 Catch:{ all -> 0x0157 }
        r9 = "_id";	 Catch:{ all -> 0x0157 }
        r0[r14] = r9;	 Catch:{ all -> 0x0157 }
        r9 = "file_id";	 Catch:{ all -> 0x0157 }
        r0[r13] = r9;	 Catch:{ all -> 0x0157 }
        r9 = "font_ttc_index";	 Catch:{ all -> 0x0157 }
        r0[r8] = r9;	 Catch:{ all -> 0x0157 }
        r8 = "font_variation_settings";	 Catch:{ all -> 0x0157 }
        r0[r7] = r8;	 Catch:{ all -> 0x0157 }
        r7 = "font_weight";	 Catch:{ all -> 0x0157 }
        r0[r6] = r7;	 Catch:{ all -> 0x0157 }
        r6 = "font_italic";	 Catch:{ all -> 0x0157 }
        r0[r5] = r6;	 Catch:{ all -> 0x0157 }
        r5 = "result_code";	 Catch:{ all -> 0x0157 }
        r0[r4] = r5;	 Catch:{ all -> 0x0157 }
        r6 = "query = ?";	 Catch:{ all -> 0x0157 }
        r7 = new java.lang.String[r13];	 Catch:{ all -> 0x0157 }
        r4 = r22.getQuery();	 Catch:{ all -> 0x0157 }
        r7[r14] = r4;	 Catch:{ all -> 0x0157 }
        r8 = 0;	 Catch:{ all -> 0x0157 }
        r4 = r10;	 Catch:{ all -> 0x0157 }
        r5 = r0;	 Catch:{ all -> 0x0157 }
        r9 = r24;	 Catch:{ all -> 0x0157 }
        r0 = r3.query(r4, r5, r6, r7, r8, r9);	 Catch:{ all -> 0x0157 }
        r12 = r0;	 Catch:{ all -> 0x0157 }
        goto L_0x00b2;	 Catch:{ all -> 0x0157 }
    L_0x007c:
        r3 = r21.getContentResolver();	 Catch:{ all -> 0x0157 }
        r0 = new java.lang.String[r9];	 Catch:{ all -> 0x0157 }
        r9 = "_id";	 Catch:{ all -> 0x0157 }
        r0[r14] = r9;	 Catch:{ all -> 0x0157 }
        r9 = "file_id";	 Catch:{ all -> 0x0157 }
        r0[r13] = r9;	 Catch:{ all -> 0x0157 }
        r9 = "font_ttc_index";	 Catch:{ all -> 0x0157 }
        r0[r8] = r9;	 Catch:{ all -> 0x0157 }
        r8 = "font_variation_settings";	 Catch:{ all -> 0x0157 }
        r0[r7] = r8;	 Catch:{ all -> 0x0157 }
        r7 = "font_weight";	 Catch:{ all -> 0x0157 }
        r0[r6] = r7;	 Catch:{ all -> 0x0157 }
        r6 = "font_italic";	 Catch:{ all -> 0x0157 }
        r0[r5] = r6;	 Catch:{ all -> 0x0157 }
        r5 = "result_code";	 Catch:{ all -> 0x0157 }
        r0[r4] = r5;	 Catch:{ all -> 0x0157 }
        r6 = "query = ?";	 Catch:{ all -> 0x0157 }
        r7 = new java.lang.String[r13];	 Catch:{ all -> 0x0157 }
        r4 = r22.getQuery();	 Catch:{ all -> 0x0157 }
        r7[r14] = r4;	 Catch:{ all -> 0x0157 }
        r8 = 0;	 Catch:{ all -> 0x0157 }
        r4 = r10;	 Catch:{ all -> 0x0157 }
        r5 = r0;	 Catch:{ all -> 0x0157 }
        r0 = r3.query(r4, r5, r6, r7, r8);	 Catch:{ all -> 0x0157 }
        r12 = r0;	 Catch:{ all -> 0x0157 }
    L_0x00b2:
        if (r12 == 0) goto L_0x0145;	 Catch:{ all -> 0x0157 }
    L_0x00b4:
        r0 = r12.getCount();	 Catch:{ all -> 0x0157 }
        if (r0 <= 0) goto L_0x0145;	 Catch:{ all -> 0x0157 }
    L_0x00ba:
        r0 = "result_code";	 Catch:{ all -> 0x0157 }
        r0 = r12.getColumnIndex(r0);	 Catch:{ all -> 0x0157 }
        r3 = new java.util.ArrayList;	 Catch:{ all -> 0x0157 }
        r3.<init>();	 Catch:{ all -> 0x0157 }
        r2 = r3;	 Catch:{ all -> 0x0157 }
        r3 = "_id";	 Catch:{ all -> 0x0157 }
        r3 = r12.getColumnIndex(r3);	 Catch:{ all -> 0x0157 }
        r4 = "file_id";	 Catch:{ all -> 0x0157 }
        r4 = r12.getColumnIndex(r4);	 Catch:{ all -> 0x0157 }
        r5 = "font_ttc_index";	 Catch:{ all -> 0x0157 }
        r5 = r12.getColumnIndex(r5);	 Catch:{ all -> 0x0157 }
        r6 = "font_weight";	 Catch:{ all -> 0x0157 }
        r6 = r12.getColumnIndex(r6);	 Catch:{ all -> 0x0157 }
        r7 = "font_italic";	 Catch:{ all -> 0x0157 }
        r7 = r12.getColumnIndex(r7);	 Catch:{ all -> 0x0157 }
    L_0x00e5:
        r8 = r12.moveToNext();	 Catch:{ all -> 0x0157 }
        if (r8 == 0) goto L_0x0144;	 Catch:{ all -> 0x0157 }
    L_0x00eb:
        r8 = -1;	 Catch:{ all -> 0x0157 }
        if (r0 == r8) goto L_0x00f5;	 Catch:{ all -> 0x0157 }
    L_0x00ee:
        r9 = r12.getInt(r0);	 Catch:{ all -> 0x0157 }
        r20 = r9;	 Catch:{ all -> 0x0157 }
        goto L_0x00f7;	 Catch:{ all -> 0x0157 }
    L_0x00f5:
        r20 = 0;	 Catch:{ all -> 0x0157 }
    L_0x00f7:
        if (r5 == r8) goto L_0x0100;	 Catch:{ all -> 0x0157 }
    L_0x00f9:
        r9 = r12.getInt(r5);	 Catch:{ all -> 0x0157 }
        r17 = r9;	 Catch:{ all -> 0x0157 }
        goto L_0x0102;	 Catch:{ all -> 0x0157 }
    L_0x0100:
        r17 = 0;	 Catch:{ all -> 0x0157 }
    L_0x0102:
        if (r4 != r8) goto L_0x0113;	 Catch:{ all -> 0x0157 }
    L_0x0104:
        r15 = r12.getLong(r3);	 Catch:{ all -> 0x0157 }
        r18 = r15;	 Catch:{ all -> 0x0157 }
        r14 = r18;	 Catch:{ all -> 0x0157 }
        r16 = android.content.ContentUris.withAppendedId(r10, r14);	 Catch:{ all -> 0x0157 }
        r14 = r16;	 Catch:{ all -> 0x0157 }
        goto L_0x011d;	 Catch:{ all -> 0x0157 }
    L_0x0113:
        r14 = r12.getLong(r4);	 Catch:{ all -> 0x0157 }
        r16 = android.content.ContentUris.withAppendedId(r11, r14);	 Catch:{ all -> 0x0157 }
        r14 = r16;	 Catch:{ all -> 0x0157 }
    L_0x011d:
        if (r6 == r8) goto L_0x0126;	 Catch:{ all -> 0x0157 }
    L_0x011f:
        r15 = r12.getInt(r6);	 Catch:{ all -> 0x0157 }
        r18 = r15;	 Catch:{ all -> 0x0157 }
        goto L_0x012a;	 Catch:{ all -> 0x0157 }
    L_0x0126:
        r15 = 400; // 0x190 float:5.6E-43 double:1.976E-321;	 Catch:{ all -> 0x0157 }
        r18 = 400; // 0x190 float:5.6E-43 double:1.976E-321;	 Catch:{ all -> 0x0157 }
    L_0x012a:
        if (r7 == r8) goto L_0x0135;	 Catch:{ all -> 0x0157 }
    L_0x012c:
        r8 = r12.getInt(r7);	 Catch:{ all -> 0x0157 }
        if (r8 != r13) goto L_0x0135;	 Catch:{ all -> 0x0157 }
    L_0x0132:
        r19 = 1;	 Catch:{ all -> 0x0157 }
        goto L_0x0137;	 Catch:{ all -> 0x0157 }
    L_0x0135:
        r19 = 0;	 Catch:{ all -> 0x0157 }
    L_0x0137:
        r8 = new android.support.v4.provider.FontsContractCompat$FontInfo;	 Catch:{ all -> 0x0157 }
        r15 = r8;	 Catch:{ all -> 0x0157 }
        r16 = r14;	 Catch:{ all -> 0x0157 }
        r15.<init>(r16, r17, r18, r19, r20);	 Catch:{ all -> 0x0157 }
        r2.add(r8);	 Catch:{ all -> 0x0157 }
        r14 = 0;
        goto L_0x00e5;
    L_0x0144:
        goto L_0x0146;
    L_0x0146:
        if (r12 == 0) goto L_0x014c;
    L_0x0148:
        r12.close();
        goto L_0x014d;
    L_0x014d:
        r0 = 0;
        r0 = new android.support.v4.provider.FontsContractCompat.FontInfo[r0];
        r0 = r2.toArray(r0);
        r0 = (android.support.v4.provider.FontsContractCompat.FontInfo[]) r0;
        return r0;
    L_0x0157:
        r0 = move-exception;
        if (r12 == 0) goto L_0x015e;
    L_0x015a:
        r12.close();
        goto L_0x015f;
    L_0x015f:
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.provider.FontsContractCompat.getFontFromProvider(android.content.Context, android.support.v4.provider.FontRequest, java.lang.String, android.os.CancellationSignal):android.support.v4.provider.FontsContractCompat$FontInfo[]");
    }

    @android.support.annotation.VisibleForTesting
    @android.support.annotation.RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    @android.support.annotation.Nullable
    public static android.content.pm.ProviderInfo getProvider(@android.support.annotation.NonNull android.content.pm.PackageManager r8, @android.support.annotation.NonNull android.support.v4.provider.FontRequest r9, @android.support.annotation.Nullable android.content.res.Resources r10) throws android.content.pm.PackageManager.NameNotFoundException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x008c in {9, 10, 12, 14, 16} preds:[]
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
        r0 = r9.getProviderAuthority();
        r1 = 0;
        r1 = r8.resolveContentProvider(r0, r1);
        if (r1 == 0) goto L_0x0075;
    L_0x000b:
        r2 = r1.packageName;
        r3 = r9.getProviderPackage();
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0052;
    L_0x0017:
        r2 = r1.packageName;
        r3 = 64;
        r2 = r8.getPackageInfo(r2, r3);
        r3 = r2.signatures;
        r3 = convertToByteArrayList(r3);
        r4 = sByteArrayComparator;
        java.util.Collections.sort(r3, r4);
        r4 = getCertificates(r9, r10);
        r5 = 0;
    L_0x002f:
        r6 = r4.size();
        if (r5 >= r6) goto L_0x0050;
    L_0x0035:
        r6 = new java.util.ArrayList;
        r7 = r4.get(r5);
        r7 = (java.util.Collection) r7;
        r6.<init>(r7);
        r7 = sByteArrayComparator;
        java.util.Collections.sort(r6, r7);
        r7 = equalsByteArrayList(r3, r6);
        if (r7 == 0) goto L_0x004c;
    L_0x004b:
        return r1;
        r5 = r5 + 1;
        goto L_0x002f;
    L_0x0050:
        r5 = 0;
        return r5;
    L_0x0052:
        r2 = new android.content.pm.PackageManager$NameNotFoundException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Found content provider ";
        r3.append(r4);
        r3.append(r0);
        r4 = ", but package was not ";
        r3.append(r4);
        r4 = r9.getProviderPackage();
        r3.append(r4);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
    L_0x0075:
        r2 = new android.content.pm.PackageManager$NameNotFoundException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "No package found for authority: ";
        r3.append(r4);
        r3.append(r0);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.provider.FontsContractCompat.getProvider(android.content.pm.PackageManager, android.support.v4.provider.FontRequest, android.content.res.Resources):android.content.pm.ProviderInfo");
    }

    private FontsContractCompat() {
    }

    @NonNull
    private static TypefaceResult getFontInternal(Context context, FontRequest request, int style) {
        try {
            FontFamilyResult result = fetchFonts(context, null, request);
            int i = -3;
            if (result.getStatusCode() == 0) {
                Typeface typeface = TypefaceCompat.createFromFontInfo(context, null, result.getFonts(), style);
                if (typeface != null) {
                    i = 0;
                }
                return new TypefaceResult(typeface, i);
            }
            if (result.getStatusCode() == 1) {
                i = -2;
            }
            return new TypefaceResult(null, i);
        } catch (NameNotFoundException e) {
            return new TypefaceResult(null, -1);
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static void resetCache() {
        sTypefaceCache.evictAll();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.support.annotation.RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public static android.graphics.Typeface getFontSync(final android.content.Context r8, final android.support.v4.provider.FontRequest r9, @android.support.annotation.Nullable final android.support.v4.content.res.ResourcesCompat.FontCallback r10, @android.support.annotation.Nullable final android.os.Handler r11, boolean r12, int r13, final int r14) {
        /*
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = r9.getIdentifier();
        r0.append(r1);
        r1 = "-";
        r0.append(r1);
        r0.append(r14);
        r0 = r0.toString();
        r1 = sTypefaceCache;
        r1 = r1.get(r0);
        r1 = (android.graphics.Typeface) r1;
        if (r1 == 0) goto L_0x002a;
    L_0x0022:
        if (r10 == 0) goto L_0x0028;
    L_0x0024:
        r10.onFontRetrieved(r1);
        goto L_0x0029;
    L_0x0029:
        return r1;
    L_0x002a:
        if (r12 == 0) goto L_0x0049;
    L_0x002c:
        r2 = -1;
        if (r13 != r2) goto L_0x0049;
    L_0x002f:
        r2 = getFontInternal(r8, r9, r14);
        if (r10 == 0) goto L_0x0045;
    L_0x0035:
        r3 = r2.mResult;
        if (r3 != 0) goto L_0x003f;
    L_0x0039:
        r3 = r2.mTypeface;
        r10.callbackSuccessAsync(r3, r11);
        goto L_0x0046;
    L_0x003f:
        r3 = r2.mResult;
        r10.callbackFailAsync(r3, r11);
        goto L_0x0046;
    L_0x0046:
        r3 = r2.mTypeface;
        return r3;
        r2 = new android.support.v4.provider.FontsContractCompat$1;
        r2.<init>(r8, r9, r14, r0);
        r3 = 0;
        if (r12 == 0) goto L_0x005f;
    L_0x0052:
        r4 = sBackgroundThread;	 Catch:{ InterruptedException -> 0x005d }
        r4 = r4.postAndWait(r2, r13);	 Catch:{ InterruptedException -> 0x005d }
        r4 = (android.support.v4.provider.FontsContractCompat.TypefaceResult) r4;	 Catch:{ InterruptedException -> 0x005d }
        r3 = r4.mTypeface;	 Catch:{ InterruptedException -> 0x005d }
        return r3;
    L_0x005d:
        r4 = move-exception;
        return r3;
    L_0x005f:
        if (r10 != 0) goto L_0x0063;
    L_0x0061:
        r4 = r3;
        goto L_0x0068;
    L_0x0063:
        r4 = new android.support.v4.provider.FontsContractCompat$2;
        r4.<init>(r10, r11);
    L_0x0068:
        r5 = sLock;
        monitor-enter(r5);
        r6 = sPendingReplies;	 Catch:{ all -> 0x00a1 }
        r6 = r6.containsKey(r0);	 Catch:{ all -> 0x00a1 }
        if (r6 == 0) goto L_0x0084;
    L_0x0073:
        if (r4 == 0) goto L_0x0081;
    L_0x0075:
        r6 = sPendingReplies;	 Catch:{ all -> 0x00a1 }
        r6 = r6.get(r0);	 Catch:{ all -> 0x00a1 }
        r6 = (java.util.ArrayList) r6;	 Catch:{ all -> 0x00a1 }
        r6.add(r4);	 Catch:{ all -> 0x00a1 }
        goto L_0x0082;
    L_0x0082:
        monitor-exit(r5);	 Catch:{ all -> 0x00a1 }
        return r3;
    L_0x0084:
        if (r4 == 0) goto L_0x0094;
    L_0x0086:
        r6 = new java.util.ArrayList;	 Catch:{ all -> 0x00a1 }
        r6.<init>();	 Catch:{ all -> 0x00a1 }
        r6.add(r4);	 Catch:{ all -> 0x00a1 }
        r7 = sPendingReplies;	 Catch:{ all -> 0x00a1 }
        r7.put(r0, r6);	 Catch:{ all -> 0x00a1 }
        goto L_0x0095;
    L_0x0095:
        monitor-exit(r5);	 Catch:{ all -> 0x00a1 }
        r5 = sBackgroundThread;
        r6 = new android.support.v4.provider.FontsContractCompat$3;
        r6.<init>(r0);
        r5.postAndReply(r2, r6);
        return r3;
    L_0x00a1:
        r3 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x00a1 }
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.provider.FontsContractCompat.getFontSync(android.content.Context, android.support.v4.provider.FontRequest, android.support.v4.content.res.ResourcesCompat$FontCallback, android.os.Handler, boolean, int, int):android.graphics.Typeface");
    }

    public static void requestFont(@NonNull final Context context, @NonNull final FontRequest request, @NonNull final FontRequestCallback callback, @NonNull Handler handler) {
        final Handler callerThreadHandler = new Handler();
        handler.post(new Runnable() {

            /* renamed from: android.support.v4.provider.FontsContractCompat$4$1 */
            class C02081 implements Runnable {
                C02081() {
                }

                public void run() {
                    callback.onTypefaceRequestFailed(-1);
                }
            }

            /* renamed from: android.support.v4.provider.FontsContractCompat$4$2 */
            class C02092 implements Runnable {
                C02092() {
                }

                public void run() {
                    callback.onTypefaceRequestFailed(-2);
                }
            }

            /* renamed from: android.support.v4.provider.FontsContractCompat$4$3 */
            class C02103 implements Runnable {
                C02103() {
                }

                public void run() {
                    callback.onTypefaceRequestFailed(-3);
                }
            }

            /* renamed from: android.support.v4.provider.FontsContractCompat$4$4 */
            class C02114 implements Runnable {
                C02114() {
                }

                public void run() {
                    callback.onTypefaceRequestFailed(-3);
                }
            }

            /* renamed from: android.support.v4.provider.FontsContractCompat$4$5 */
            class C02125 implements Runnable {
                C02125() {
                }

                public void run() {
                    callback.onTypefaceRequestFailed(1);
                }
            }

            /* renamed from: android.support.v4.provider.FontsContractCompat$4$6 */
            class C02136 implements Runnable {
                C02136() {
                }

                public void run() {
                    callback.onTypefaceRequestFailed(-3);
                }
            }

            /* renamed from: android.support.v4.provider.FontsContractCompat$4$8 */
            class C02158 implements Runnable {
                C02158() {
                }

                public void run() {
                    callback.onTypefaceRequestFailed(-3);
                }
            }

            public void run() {
                try {
                    FontFamilyResult result = FontsContractCompat.fetchFonts(context, null, request);
                    if (result.getStatusCode() != 0) {
                        switch (result.getStatusCode()) {
                            case 1:
                                callerThreadHandler.post(new C02092());
                                return;
                            case 2:
                                callerThreadHandler.post(new C02103());
                                return;
                            default:
                                callerThreadHandler.post(new C02114());
                                return;
                        }
                    }
                    FontInfo[] fonts = result.getFonts();
                    if (fonts != null) {
                        if (fonts.length != 0) {
                            for (FontInfo font : fonts) {
                                if (font.getResultCode() != 0) {
                                    final int resultCode = font.getResultCode();
                                    if (resultCode < 0) {
                                        callerThreadHandler.post(new C02136());
                                    } else {
                                        callerThreadHandler.post(new Runnable() {
                                            public void run() {
                                                callback.onTypefaceRequestFailed(resultCode);
                                            }
                                        });
                                    }
                                    return;
                                }
                            }
                            final Typeface typeface = FontsContractCompat.buildTypeface(context, null, fonts);
                            if (typeface == null) {
                                callerThreadHandler.post(new C02158());
                                return;
                            } else {
                                callerThreadHandler.post(new Runnable() {
                                    public void run() {
                                        callback.onTypefaceRetrieved(typeface);
                                    }
                                });
                                return;
                            }
                        }
                    }
                    callerThreadHandler.post(new C02125());
                } catch (NameNotFoundException e) {
                    callerThreadHandler.post(new C02081());
                }
            }
        });
    }

    @Nullable
    public static Typeface buildTypeface(@NonNull Context context, @Nullable CancellationSignal cancellationSignal, @NonNull FontInfo[] fonts) {
        return TypefaceCompat.createFromFontInfo(context, cancellationSignal, fonts, 0);
    }

    @RequiresApi(19)
    @RestrictTo({Scope.LIBRARY_GROUP})
    public static Map<Uri, ByteBuffer> prepareFontData(Context context, FontInfo[] fonts, CancellationSignal cancellationSignal) {
        HashMap<Uri, ByteBuffer> out = new HashMap();
        for (FontInfo font : fonts) {
            if (font.getResultCode() == 0) {
                Uri uri = font.getUri();
                if (!out.containsKey(uri)) {
                    out.put(uri, TypefaceCompatUtil.mmap(context, cancellationSignal, uri));
                }
            }
        }
        return Collections.unmodifiableMap(out);
    }

    @NonNull
    public static FontFamilyResult fetchFonts(@NonNull Context context, @Nullable CancellationSignal cancellationSignal, @NonNull FontRequest request) throws NameNotFoundException {
        ProviderInfo providerInfo = getProvider(context.getPackageManager(), request, context.getResources());
        if (providerInfo == null) {
            return new FontFamilyResult(1, null);
        }
        return new FontFamilyResult(0, getFontFromProvider(context, request, providerInfo.authority, cancellationSignal));
    }

    private static List<List<byte[]>> getCertificates(FontRequest request, Resources resources) {
        if (request.getCertificates() != null) {
            return request.getCertificates();
        }
        return FontResourcesParserCompat.readCerts(resources, request.getCertificatesArrayResId());
    }

    private static boolean equalsByteArrayList(List<byte[]> signatures, List<byte[]> requestSignatures) {
        if (signatures.size() != requestSignatures.size()) {
            return false;
        }
        for (int i = 0; i < signatures.size(); i++) {
            if (!Arrays.equals((byte[]) signatures.get(i), (byte[]) requestSignatures.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static List<byte[]> convertToByteArrayList(Signature[] signatures) {
        List<byte[]> shas = new ArrayList();
        for (Signature toByteArray : signatures) {
            shas.add(toByteArray.toByteArray());
        }
        return shas;
    }
}

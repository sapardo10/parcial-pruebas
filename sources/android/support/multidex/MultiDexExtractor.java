package android.support.multidex;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

final class MultiDexExtractor {
    private static final int BUFFER_SIZE = 16384;
    private static final String DEX_PREFIX = "classes";
    private static final String DEX_SUFFIX = ".dex";
    private static final String EXTRACTED_NAME_EXT = ".classes";
    private static final String EXTRACTED_SUFFIX = ".zip";
    private static final String KEY_CRC = "crc";
    private static final String KEY_DEX_CRC = "dex.crc.";
    private static final String KEY_DEX_NUMBER = "dex.number";
    private static final String KEY_DEX_TIME = "dex.time.";
    private static final String KEY_TIME_STAMP = "timestamp";
    private static final String LOCK_FILENAME = "MultiDex.lock";
    private static final int MAX_EXTRACT_ATTEMPTS = 3;
    private static final long NO_VALUE = -1;
    private static final String PREFS_FILE = "multidex.version";
    private static final String TAG = "MultiDex";

    private static class ExtractedDex extends File {
        public long crc = -1;

        public ExtractedDex(File dexDir, String fileName) {
            super(dexDir, fileName);
        }
    }

    private static void extract(java.util.zip.ZipFile r7, java.util.zip.ZipEntry r8, java.io.File r9, java.lang.String r10) throws java.io.IOException, java.io.FileNotFoundException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:28:0x010a in {8, 16, 19, 21, 24, 27} preds:[]
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
        r0 = r7.getInputStream(r8);
        r1 = 0;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "tmp-";
        r2.append(r3);
        r2.append(r10);
        r2 = r2.toString();
        r3 = ".zip";
        r4 = r9.getParentFile();
        r2 = java.io.File.createTempFile(r2, r3, r4);
        r3 = "MultiDex";
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Extracting ";
        r4.append(r5);
        r5 = r2.getPath();
        r4.append(r5);
        r4 = r4.toString();
        android.util.Log.i(r3, r4);
        r3 = new java.util.zip.ZipOutputStream;	 Catch:{ all -> 0x0102 }
        r4 = new java.io.BufferedOutputStream;	 Catch:{ all -> 0x0102 }
        r5 = new java.io.FileOutputStream;	 Catch:{ all -> 0x0102 }
        r5.<init>(r2);	 Catch:{ all -> 0x0102 }
        r4.<init>(r5);	 Catch:{ all -> 0x0102 }
        r3.<init>(r4);	 Catch:{ all -> 0x0102 }
        r1 = r3;
        r3 = new java.util.zip.ZipEntry;	 Catch:{ all -> 0x00fd }
        r4 = "classes.dex";	 Catch:{ all -> 0x00fd }
        r3.<init>(r4);	 Catch:{ all -> 0x00fd }
        r4 = r8.getTime();	 Catch:{ all -> 0x00fd }
        r3.setTime(r4);	 Catch:{ all -> 0x00fd }
        r1.putNextEntry(r3);	 Catch:{ all -> 0x00fd }
        r4 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;	 Catch:{ all -> 0x00fd }
        r4 = new byte[r4];	 Catch:{ all -> 0x00fd }
        r5 = r0.read(r4);	 Catch:{ all -> 0x00fd }
    L_0x0063:
        r6 = -1;	 Catch:{ all -> 0x00fd }
        if (r5 == r6) goto L_0x0070;	 Catch:{ all -> 0x00fd }
    L_0x0066:
        r6 = 0;	 Catch:{ all -> 0x00fd }
        r1.write(r4, r6, r5);	 Catch:{ all -> 0x00fd }
        r6 = r0.read(r4);	 Catch:{ all -> 0x00fd }
        r5 = r6;	 Catch:{ all -> 0x00fd }
        goto L_0x0063;	 Catch:{ all -> 0x00fd }
    L_0x0070:
        r1.closeEntry();	 Catch:{ all -> 0x00fd }
        r1.close();	 Catch:{ all -> 0x0102 }
        r3 = r2.setReadOnly();	 Catch:{ all -> 0x0102 }
        if (r3 == 0) goto L_0x00d1;	 Catch:{ all -> 0x0102 }
    L_0x007d:
        r3 = "MultiDex";	 Catch:{ all -> 0x0102 }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0102 }
        r4.<init>();	 Catch:{ all -> 0x0102 }
        r5 = "Renaming to ";	 Catch:{ all -> 0x0102 }
        r4.append(r5);	 Catch:{ all -> 0x0102 }
        r5 = r9.getPath();	 Catch:{ all -> 0x0102 }
        r4.append(r5);	 Catch:{ all -> 0x0102 }
        r4 = r4.toString();	 Catch:{ all -> 0x0102 }
        android.util.Log.i(r3, r4);	 Catch:{ all -> 0x0102 }
        r3 = r2.renameTo(r9);	 Catch:{ all -> 0x0102 }
        if (r3 == 0) goto L_0x00a5;
    L_0x009d:
        closeQuietly(r0);
        r2.delete();
        return;
    L_0x00a5:
        r3 = new java.io.IOException;	 Catch:{ all -> 0x0102 }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0102 }
        r4.<init>();	 Catch:{ all -> 0x0102 }
        r5 = "Failed to rename \"";	 Catch:{ all -> 0x0102 }
        r4.append(r5);	 Catch:{ all -> 0x0102 }
        r5 = r2.getAbsolutePath();	 Catch:{ all -> 0x0102 }
        r4.append(r5);	 Catch:{ all -> 0x0102 }
        r5 = "\" to \"";	 Catch:{ all -> 0x0102 }
        r4.append(r5);	 Catch:{ all -> 0x0102 }
        r5 = r9.getAbsolutePath();	 Catch:{ all -> 0x0102 }
        r4.append(r5);	 Catch:{ all -> 0x0102 }
        r5 = "\"";	 Catch:{ all -> 0x0102 }
        r4.append(r5);	 Catch:{ all -> 0x0102 }
        r4 = r4.toString();	 Catch:{ all -> 0x0102 }
        r3.<init>(r4);	 Catch:{ all -> 0x0102 }
        throw r3;	 Catch:{ all -> 0x0102 }
    L_0x00d1:
        r3 = new java.io.IOException;	 Catch:{ all -> 0x0102 }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0102 }
        r4.<init>();	 Catch:{ all -> 0x0102 }
        r5 = "Failed to mark readonly \"";	 Catch:{ all -> 0x0102 }
        r4.append(r5);	 Catch:{ all -> 0x0102 }
        r5 = r2.getAbsolutePath();	 Catch:{ all -> 0x0102 }
        r4.append(r5);	 Catch:{ all -> 0x0102 }
        r5 = "\" (tmp of \"";	 Catch:{ all -> 0x0102 }
        r4.append(r5);	 Catch:{ all -> 0x0102 }
        r5 = r9.getAbsolutePath();	 Catch:{ all -> 0x0102 }
        r4.append(r5);	 Catch:{ all -> 0x0102 }
        r5 = "\")";	 Catch:{ all -> 0x0102 }
        r4.append(r5);	 Catch:{ all -> 0x0102 }
        r4 = r4.toString();	 Catch:{ all -> 0x0102 }
        r3.<init>(r4);	 Catch:{ all -> 0x0102 }
        throw r3;	 Catch:{ all -> 0x0102 }
    L_0x00fd:
        r3 = move-exception;	 Catch:{ all -> 0x0102 }
        r1.close();	 Catch:{ all -> 0x0102 }
        throw r3;	 Catch:{ all -> 0x0102 }
    L_0x0102:
        r3 = move-exception;
        closeQuietly(r0);
        r2.delete();
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.multidex.MultiDexExtractor.extract(java.util.zip.ZipFile, java.util.zip.ZipEntry, java.io.File, java.lang.String):void");
    }

    private static java.util.List<android.support.multidex.MultiDexExtractor.ExtractedDex> performExtractions(java.io.File r13, java.io.File r14) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:46:0x0177 in {11, 15, 18, 19, 24, 25, 26, 29, 31, 35, 37, 38, 42, 44, 45} preds:[]
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
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = r13.getName();
        r0.append(r1);
        r1 = ".classes";
        r0.append(r1);
        r0 = r0.toString();
        prepareDexDir(r14, r0);
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = new java.util.zip.ZipFile;
        r2.<init>(r13);
        r3 = 2;
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0169 }
        r4.<init>();	 Catch:{ all -> 0x0169 }
        r5 = "classes";	 Catch:{ all -> 0x0169 }
        r4.append(r5);	 Catch:{ all -> 0x0169 }
        r4.append(r3);	 Catch:{ all -> 0x0169 }
        r5 = ".dex";	 Catch:{ all -> 0x0169 }
        r4.append(r5);	 Catch:{ all -> 0x0169 }
        r4 = r4.toString();	 Catch:{ all -> 0x0169 }
        r4 = r2.getEntry(r4);	 Catch:{ all -> 0x0169 }
    L_0x003d:
        if (r4 == 0) goto L_0x015a;	 Catch:{ all -> 0x0169 }
    L_0x003f:
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0169 }
        r5.<init>();	 Catch:{ all -> 0x0169 }
        r5.append(r0);	 Catch:{ all -> 0x0169 }
        r5.append(r3);	 Catch:{ all -> 0x0169 }
        r6 = ".zip";	 Catch:{ all -> 0x0169 }
        r5.append(r6);	 Catch:{ all -> 0x0169 }
        r5 = r5.toString();	 Catch:{ all -> 0x0169 }
        r6 = new android.support.multidex.MultiDexExtractor$ExtractedDex;	 Catch:{ all -> 0x0169 }
        r6.<init>(r14, r5);	 Catch:{ all -> 0x0169 }
        r1.add(r6);	 Catch:{ all -> 0x0169 }
        r7 = "MultiDex";	 Catch:{ all -> 0x0169 }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0169 }
        r8.<init>();	 Catch:{ all -> 0x0169 }
        r9 = "Extraction is needed for file ";	 Catch:{ all -> 0x0169 }
        r8.append(r9);	 Catch:{ all -> 0x0169 }
        r8.append(r6);	 Catch:{ all -> 0x0169 }
        r8 = r8.toString();	 Catch:{ all -> 0x0169 }
        android.util.Log.i(r7, r8);	 Catch:{ all -> 0x0169 }
        r7 = 0;	 Catch:{ all -> 0x0169 }
        r8 = 0;	 Catch:{ all -> 0x0169 }
    L_0x0073:
        r9 = 3;	 Catch:{ all -> 0x0169 }
        if (r7 >= r9) goto L_0x0110;	 Catch:{ all -> 0x0169 }
    L_0x0076:
        if (r8 != 0) goto L_0x0110;	 Catch:{ all -> 0x0169 }
    L_0x0078:
        r7 = r7 + 1;	 Catch:{ all -> 0x0169 }
        extract(r2, r4, r6, r0);	 Catch:{ all -> 0x0169 }
        r9 = getZipCrc(r6);	 Catch:{ IOException -> 0x0085 }
        r6.crc = r9;	 Catch:{ IOException -> 0x0085 }
        r8 = 1;
        goto L_0x00a1;
    L_0x0085:
        r9 = move-exception;
        r8 = 0;
        r10 = "MultiDex";	 Catch:{ all -> 0x0169 }
        r11 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0169 }
        r11.<init>();	 Catch:{ all -> 0x0169 }
        r12 = "Failed to read crc from ";	 Catch:{ all -> 0x0169 }
        r11.append(r12);	 Catch:{ all -> 0x0169 }
        r12 = r6.getAbsolutePath();	 Catch:{ all -> 0x0169 }
        r11.append(r12);	 Catch:{ all -> 0x0169 }
        r11 = r11.toString();	 Catch:{ all -> 0x0169 }
        android.util.Log.w(r10, r11, r9);	 Catch:{ all -> 0x0169 }
    L_0x00a1:
        r9 = "MultiDex";	 Catch:{ all -> 0x0169 }
        r10 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0169 }
        r10.<init>();	 Catch:{ all -> 0x0169 }
        r11 = "Extraction ";	 Catch:{ all -> 0x0169 }
        r10.append(r11);	 Catch:{ all -> 0x0169 }
        if (r8 == 0) goto L_0x00b2;	 Catch:{ all -> 0x0169 }
    L_0x00af:
        r11 = "succeeded";	 Catch:{ all -> 0x0169 }
        goto L_0x00b4;	 Catch:{ all -> 0x0169 }
    L_0x00b2:
        r11 = "failed";	 Catch:{ all -> 0x0169 }
    L_0x00b4:
        r10.append(r11);	 Catch:{ all -> 0x0169 }
        r11 = " - length ";	 Catch:{ all -> 0x0169 }
        r10.append(r11);	 Catch:{ all -> 0x0169 }
        r11 = r6.getAbsolutePath();	 Catch:{ all -> 0x0169 }
        r10.append(r11);	 Catch:{ all -> 0x0169 }
        r11 = ": ";	 Catch:{ all -> 0x0169 }
        r10.append(r11);	 Catch:{ all -> 0x0169 }
        r11 = r6.length();	 Catch:{ all -> 0x0169 }
        r10.append(r11);	 Catch:{ all -> 0x0169 }
        r11 = " - crc: ";	 Catch:{ all -> 0x0169 }
        r10.append(r11);	 Catch:{ all -> 0x0169 }
        r11 = r6.crc;	 Catch:{ all -> 0x0169 }
        r10.append(r11);	 Catch:{ all -> 0x0169 }
        r10 = r10.toString();	 Catch:{ all -> 0x0169 }
        android.util.Log.i(r9, r10);	 Catch:{ all -> 0x0169 }
        if (r8 != 0) goto L_0x010e;	 Catch:{ all -> 0x0169 }
    L_0x00e2:
        r6.delete();	 Catch:{ all -> 0x0169 }
        r9 = r6.exists();	 Catch:{ all -> 0x0169 }
        if (r9 == 0) goto L_0x010c;	 Catch:{ all -> 0x0169 }
    L_0x00eb:
        r9 = "MultiDex";	 Catch:{ all -> 0x0169 }
        r10 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0169 }
        r10.<init>();	 Catch:{ all -> 0x0169 }
        r11 = "Failed to delete corrupted secondary dex '";	 Catch:{ all -> 0x0169 }
        r10.append(r11);	 Catch:{ all -> 0x0169 }
        r11 = r6.getPath();	 Catch:{ all -> 0x0169 }
        r10.append(r11);	 Catch:{ all -> 0x0169 }
        r11 = "'";	 Catch:{ all -> 0x0169 }
        r10.append(r11);	 Catch:{ all -> 0x0169 }
        r10 = r10.toString();	 Catch:{ all -> 0x0169 }
        android.util.Log.w(r9, r10);	 Catch:{ all -> 0x0169 }
        goto L_0x0073;	 Catch:{ all -> 0x0169 }
    L_0x010c:
        goto L_0x0073;	 Catch:{ all -> 0x0169 }
    L_0x010e:
        goto L_0x0073;	 Catch:{ all -> 0x0169 }
        if (r8 == 0) goto L_0x0132;	 Catch:{ all -> 0x0169 }
    L_0x0113:
        r3 = r3 + 1;	 Catch:{ all -> 0x0169 }
        r9 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0169 }
        r9.<init>();	 Catch:{ all -> 0x0169 }
        r10 = "classes";	 Catch:{ all -> 0x0169 }
        r9.append(r10);	 Catch:{ all -> 0x0169 }
        r9.append(r3);	 Catch:{ all -> 0x0169 }
        r10 = ".dex";	 Catch:{ all -> 0x0169 }
        r9.append(r10);	 Catch:{ all -> 0x0169 }
        r9 = r9.toString();	 Catch:{ all -> 0x0169 }
        r9 = r2.getEntry(r9);	 Catch:{ all -> 0x0169 }
        r4 = r9;	 Catch:{ all -> 0x0169 }
        goto L_0x003d;	 Catch:{ all -> 0x0169 }
    L_0x0132:
        r9 = new java.io.IOException;	 Catch:{ all -> 0x0169 }
        r10 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0169 }
        r10.<init>();	 Catch:{ all -> 0x0169 }
        r11 = "Could not create zip file ";	 Catch:{ all -> 0x0169 }
        r10.append(r11);	 Catch:{ all -> 0x0169 }
        r11 = r6.getAbsolutePath();	 Catch:{ all -> 0x0169 }
        r10.append(r11);	 Catch:{ all -> 0x0169 }
        r11 = " for secondary dex (";	 Catch:{ all -> 0x0169 }
        r10.append(r11);	 Catch:{ all -> 0x0169 }
        r10.append(r3);	 Catch:{ all -> 0x0169 }
        r11 = ")";	 Catch:{ all -> 0x0169 }
        r10.append(r11);	 Catch:{ all -> 0x0169 }
        r10 = r10.toString();	 Catch:{ all -> 0x0169 }
        r9.<init>(r10);	 Catch:{ all -> 0x0169 }
        throw r9;	 Catch:{ all -> 0x0169 }
        r2.close();	 Catch:{ IOException -> 0x015f }
        goto L_0x0168;
    L_0x015f:
        r3 = move-exception;
        r4 = "MultiDex";
        r5 = "Failed to close resource";
        android.util.Log.w(r4, r5, r3);
    L_0x0168:
        return r1;
    L_0x0169:
        r3 = move-exception;
        r2.close();	 Catch:{ IOException -> 0x016e }
        goto L_0x0176;
    L_0x016e:
        r4 = move-exception;
        r5 = "MultiDex";
        r6 = "Failed to close resource";
        android.util.Log.w(r5, r6, r4);
    L_0x0176:
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.multidex.MultiDexExtractor.performExtractions(java.io.File, java.io.File):java.util.List<android.support.multidex.MultiDexExtractor$ExtractedDex>");
    }

    MultiDexExtractor() {
    }

    static List<? extends File> load(Context context, File sourceApk, File dexDir, String prefsKeyPrefix, boolean forceReload) throws IOException {
        FileChannel lockChannel;
        FileLock cacheLock;
        IOException e;
        Object obj;
        FileChannel fileChannel;
        String str = prefsKeyPrefix;
        boolean z = forceReload;
        String str2 = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MultiDexExtractor.load(");
        stringBuilder.append(sourceApk.getPath());
        stringBuilder.append(", ");
        stringBuilder.append(z);
        stringBuilder.append(", ");
        stringBuilder.append(str);
        stringBuilder.append(")");
        Log.i(str2, stringBuilder.toString());
        long currentCrc = getZipCrc(sourceApk);
        File lockFile = new File(dexDir, LOCK_FILENAME);
        RandomAccessFile lockRaf = new RandomAccessFile(lockFile, "rw");
        IOException releaseLockException = null;
        try {
            List<ExtractedDex> files;
            lockChannel = lockRaf.getChannel();
            try {
                str2 = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Blocking on lock ");
                stringBuilder.append(lockFile.getPath());
                Log.i(str2, stringBuilder.toString());
                cacheLock = lockChannel.lock();
                if (cacheLock != null) {
                    try {
                        cacheLock.release();
                    } catch (IOException e2) {
                        e2 = e2;
                        String str3 = TAG;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Failed to release lock on ");
                        stringBuilder2.append(lockFile.getPath());
                        Log.e(str3, stringBuilder2.toString());
                        releaseLockException = e2;
                    }
                }
                if (lockChannel != null) {
                    closeQuietly(lockChannel);
                }
                closeQuietly(lockRaf);
                if (releaseLockException == null) {
                    str2 = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("load found ");
                    stringBuilder.append(files.size());
                    stringBuilder.append(" secondary dex files");
                    Log.i(str2, stringBuilder.toString());
                    return files;
                }
                throw releaseLockException;
            } catch (Throwable th) {
                obj = null;
                cacheLock = null;
                fileChannel = th;
                if (cacheLock == null) {
                    try {
                        cacheLock.release();
                    } catch (IOException e22) {
                        e22 = e22;
                        String str4 = TAG;
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("Failed to release lock on ");
                        stringBuilder3.append(lockFile.getPath());
                        Log.e(str4, stringBuilder3.toString());
                        releaseLockException = e22;
                    }
                }
                if (lockChannel == null) {
                    closeQuietly(lockChannel);
                }
                closeQuietly(lockRaf);
                throw fileChannel;
            }
            try {
                str2 = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append(lockFile.getPath());
                stringBuilder.append(" locked");
                Log.i(str2, stringBuilder.toString());
                if (z || isModified(context, sourceApk, currentCrc, str)) {
                    Log.i(TAG, "Detected that extraction must be performed.");
                    files = performExtractions(sourceApk, dexDir);
                    putStoredApkInfo(context, prefsKeyPrefix, getTimeStamp(sourceApk), currentCrc, files);
                } else {
                    files = loadExistingExtractions(context, sourceApk, dexDir, prefsKeyPrefix);
                }
            } catch (IOException e222) {
                Log.w(TAG, "Failed to reload existing extracted secondary dex files, falling back to fresh extraction", e222);
                files = performExtractions(sourceApk, dexDir);
                putStoredApkInfo(context, prefsKeyPrefix, getTimeStamp(sourceApk), currentCrc, files);
            } catch (Throwable th2) {
                fileChannel = th2;
                if (cacheLock == null) {
                    cacheLock.release();
                }
                if (lockChannel == null) {
                    closeQuietly(lockChannel);
                }
                closeQuietly(lockRaf);
                throw fileChannel;
            }
        } catch (Throwable th22) {
            lockChannel = null;
            cacheLock = null;
            obj = null;
            fileChannel = th22;
            if (cacheLock == null) {
                cacheLock.release();
            }
            if (lockChannel == null) {
                closeQuietly(lockChannel);
            }
            closeQuietly(lockRaf);
            throw fileChannel;
        }
    }

    private static List<ExtractedDex> loadExistingExtractions(Context context, File sourceApk, File dexDir, String prefsKeyPrefix) throws IOException {
        String extractedFilePrefix;
        SharedPreferences multiDexPreferences;
        int i;
        String str = prefsKeyPrefix;
        Log.i(TAG, "loading existing secondary dex files");
        String extractedFilePrefix2 = new StringBuilder();
        extractedFilePrefix2.append(sourceApk.getName());
        extractedFilePrefix2.append(EXTRACTED_NAME_EXT);
        extractedFilePrefix2 = extractedFilePrefix2.toString();
        SharedPreferences multiDexPreferences2 = getMultiDexPreferences(context);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(KEY_DEX_NUMBER);
        int totalDexNumber = multiDexPreferences2.getInt(stringBuilder.toString(), 1);
        List<ExtractedDex> files = new ArrayList(totalDexNumber - 1);
        int secondaryNumber = 2;
        while (secondaryNumber <= totalDexNumber) {
            String fileName = new StringBuilder();
            fileName.append(extractedFilePrefix2);
            fileName.append(secondaryNumber);
            fileName.append(EXTRACTED_SUFFIX);
            fileName = fileName.toString();
            ExtractedDex extractedFile = new ExtractedDex(dexDir, fileName);
            if (extractedFile.isFile()) {
                extractedFile.crc = getZipCrc(extractedFile);
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(KEY_DEX_CRC);
                stringBuilder2.append(secondaryNumber);
                long expectedCrc = multiDexPreferences2.getLong(stringBuilder2.toString(), -1);
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(KEY_DEX_TIME);
                stringBuilder2.append(secondaryNumber);
                long expectedModTime = multiDexPreferences2.getLong(stringBuilder2.toString(), -1);
                long lastModified = extractedFile.lastModified();
                if (expectedModTime == lastModified) {
                    extractedFilePrefix = extractedFilePrefix2;
                    multiDexPreferences = multiDexPreferences2;
                    if (expectedCrc == extractedFile.crc) {
                        files.add(extractedFile);
                        secondaryNumber++;
                        extractedFilePrefix2 = extractedFilePrefix;
                        multiDexPreferences2 = multiDexPreferences;
                    }
                } else {
                    multiDexPreferences = multiDexPreferences2;
                }
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("Invalid extracted dex: ");
                stringBuilder3.append(extractedFile);
                stringBuilder3.append(" (key \"");
                stringBuilder3.append(str);
                stringBuilder3.append("\"), expected modification time: ");
                stringBuilder3.append(expectedModTime);
                stringBuilder3.append(", modification time: ");
                stringBuilder3.append(lastModified);
                stringBuilder3.append(", expected crc: ");
                stringBuilder3.append(expectedCrc);
                stringBuilder3.append(", file crc: ");
                totalDexNumber = secondaryNumber;
                stringBuilder3.append(extractedFile.crc);
                throw new IOException(stringBuilder3.toString());
            }
            multiDexPreferences = multiDexPreferences2;
            i = totalDexNumber;
            totalDexNumber = secondaryNumber;
            String str2 = fileName;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Missing extracted secondary dex file '");
            stringBuilder3.append(extractedFile.getPath());
            stringBuilder3.append("'");
            throw new IOException(stringBuilder3.toString());
        }
        File file = dexDir;
        extractedFilePrefix = extractedFilePrefix2;
        multiDexPreferences = multiDexPreferences2;
        i = totalDexNumber;
        totalDexNumber = secondaryNumber;
        return files;
    }

    private static boolean isModified(Context context, File archive, long currentCrc, String prefsKeyPrefix) {
        SharedPreferences prefs = getMultiDexPreferences(context);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(prefsKeyPrefix);
        stringBuilder.append(KEY_TIME_STAMP);
        if (prefs.getLong(stringBuilder.toString(), -1) == getTimeStamp(archive)) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(prefsKeyPrefix);
            stringBuilder.append(KEY_CRC);
            if (prefs.getLong(stringBuilder.toString(), -1) == currentCrc) {
                return false;
            }
        }
        return true;
    }

    private static long getTimeStamp(File archive) {
        long timeStamp = archive.lastModified();
        if (timeStamp == -1) {
            return timeStamp - 1;
        }
        return timeStamp;
    }

    private static long getZipCrc(File archive) throws IOException {
        long computedValue = ZipUtil.getZipCrc(archive);
        if (computedValue == -1) {
            return computedValue - 1;
        }
        return computedValue;
    }

    private static void putStoredApkInfo(Context context, String keyPrefix, long timeStamp, long crc, List<ExtractedDex> extractedDexes) {
        Editor edit = getMultiDexPreferences(context).edit();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(keyPrefix);
        stringBuilder.append(KEY_TIME_STAMP);
        edit.putLong(stringBuilder.toString(), timeStamp);
        stringBuilder = new StringBuilder();
        stringBuilder.append(keyPrefix);
        stringBuilder.append(KEY_CRC);
        edit.putLong(stringBuilder.toString(), crc);
        stringBuilder = new StringBuilder();
        stringBuilder.append(keyPrefix);
        stringBuilder.append(KEY_DEX_NUMBER);
        edit.putInt(stringBuilder.toString(), extractedDexes.size() + 1);
        int extractedDexId = 2;
        for (ExtractedDex dex : extractedDexes) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(keyPrefix);
            stringBuilder2.append(KEY_DEX_CRC);
            stringBuilder2.append(extractedDexId);
            edit.putLong(stringBuilder2.toString(), dex.crc);
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(keyPrefix);
            stringBuilder2.append(KEY_DEX_TIME);
            stringBuilder2.append(extractedDexId);
            edit.putLong(stringBuilder2.toString(), dex.lastModified());
            extractedDexId++;
        }
        edit.commit();
    }

    private static SharedPreferences getMultiDexPreferences(Context context) {
        return context.getSharedPreferences(PREFS_FILE, VERSION.SDK_INT < 11 ? 0 : 4);
    }

    private static void prepareDexDir(File dexDir, final String extractedFilePrefix) {
        File[] files = dexDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                if (!name.startsWith(extractedFilePrefix)) {
                    if (!name.equals(MultiDexExtractor.LOCK_FILENAME)) {
                        return true;
                    }
                }
                return false;
            }
        });
        if (files == null) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to list secondary dex dir content (");
            stringBuilder.append(dexDir.getPath());
            stringBuilder.append(").");
            Log.w(str, stringBuilder.toString());
            return;
        }
        for (File oldFile : files) {
            String str2 = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Trying to delete old file ");
            stringBuilder2.append(oldFile.getPath());
            stringBuilder2.append(" of size ");
            stringBuilder2.append(oldFile.length());
            Log.i(str2, stringBuilder2.toString());
            if (oldFile.delete()) {
                str2 = TAG;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Deleted old file ");
                stringBuilder2.append(oldFile.getPath());
                Log.i(str2, stringBuilder2.toString());
            } else {
                str2 = TAG;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Failed to delete old file ");
                stringBuilder2.append(oldFile.getPath());
                Log.w(str2, stringBuilder2.toString());
            }
        }
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            Log.w(TAG, "Failed to close resource", e);
        }
    }
}

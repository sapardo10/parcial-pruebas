package okhttp3.internal.cache;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Flushable;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okhttp3.internal.io.FileSystem;
import okhttp3.internal.platform.Platform;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.apache.commons.lang3.StringUtils;

public final class DiskLruCache implements Closeable, Flushable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    static final long ANY_SEQUENCE_NUMBER = -1;
    private static final String CLEAN = "CLEAN";
    private static final String DIRTY = "DIRTY";
    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_BACKUP = "journal.bkp";
    static final String JOURNAL_FILE_TEMP = "journal.tmp";
    static final Pattern LEGAL_KEY_PATTERN = Pattern.compile("[a-z0-9_-]{1,120}");
    static final String MAGIC = "libcore.io.DiskLruCache";
    private static final String READ = "READ";
    private static final String REMOVE = "REMOVE";
    static final String VERSION_1 = "1";
    private final int appVersion;
    private final Runnable cleanupRunnable = new DiskLruCache$1(this);
    boolean closed;
    final File directory;
    private final Executor executor;
    final FileSystem fileSystem;
    boolean hasJournalErrors;
    boolean initialized;
    private final File journalFile;
    private final File journalFileBackup;
    private final File journalFileTmp;
    BufferedSink journalWriter;
    final LinkedHashMap<String, DiskLruCache$Entry> lruEntries = new LinkedHashMap(0, 0.75f, true);
    private long maxSize;
    boolean mostRecentRebuildFailed;
    boolean mostRecentTrimFailed;
    private long nextSequenceNumber = 0;
    int redundantOpCount;
    private long size = 0;
    final int valueCount;

    public final class Snapshot implements Closeable {
        private final String key;
        private final long[] lengths;
        private final long sequenceNumber;
        private final Source[] sources;

        Snapshot(String key, long sequenceNumber, Source[] sources, long[] lengths) {
            this.key = key;
            this.sequenceNumber = sequenceNumber;
            this.sources = sources;
            this.lengths = lengths;
        }

        public String key() {
            return this.key;
        }

        @Nullable
        public DiskLruCache$Editor edit() throws IOException {
            return DiskLruCache.this.edit(this.key, this.sequenceNumber);
        }

        public Source getSource(int index) {
            return this.sources[index];
        }

        public long getLength(int index) {
            return this.lengths[index];
        }

        public void close() {
            for (Closeable in : this.sources) {
                Util.closeQuietly(in);
            }
        }
    }

    private void readJournal() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:36:0x00ba in {15, 21, 22, 24, 25, 26, 27, 28, 29, 32, 35} preds:[]
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
        r0 = r9.fileSystem;
        r1 = r9.journalFile;
        r0 = r0.source(r1);
        r0 = okio.Okio.buffer(r0);
        r1 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x00b5 }
        r2 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x00b5 }
        r3 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x00b5 }
        r4 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x00b5 }
        r5 = r0.readUtf8LineStrict();	 Catch:{ all -> 0x00b5 }
        r6 = "libcore.io.DiskLruCache";	 Catch:{ all -> 0x00b5 }
        r6 = r6.equals(r1);	 Catch:{ all -> 0x00b5 }
        if (r6 == 0) goto L_0x0080;	 Catch:{ all -> 0x00b5 }
    L_0x0028:
        r6 = "1";	 Catch:{ all -> 0x00b5 }
        r6 = r6.equals(r2);	 Catch:{ all -> 0x00b5 }
        if (r6 == 0) goto L_0x007f;	 Catch:{ all -> 0x00b5 }
    L_0x0030:
        r6 = r9.appVersion;	 Catch:{ all -> 0x00b5 }
        r6 = java.lang.Integer.toString(r6);	 Catch:{ all -> 0x00b5 }
        r6 = r6.equals(r3);	 Catch:{ all -> 0x00b5 }
        if (r6 == 0) goto L_0x007e;	 Catch:{ all -> 0x00b5 }
    L_0x003c:
        r6 = r9.valueCount;	 Catch:{ all -> 0x00b5 }
        r6 = java.lang.Integer.toString(r6);	 Catch:{ all -> 0x00b5 }
        r6 = r6.equals(r4);	 Catch:{ all -> 0x00b5 }
        if (r6 == 0) goto L_0x007d;	 Catch:{ all -> 0x00b5 }
    L_0x0048:
        r6 = "";	 Catch:{ all -> 0x00b5 }
        r6 = r6.equals(r5);	 Catch:{ all -> 0x00b5 }
        if (r6 == 0) goto L_0x007c;
    L_0x0050:
        r6 = 0;
    L_0x0051:
        r7 = r0.readUtf8LineStrict();	 Catch:{ EOFException -> 0x005b }
        r9.readJournalLine(r7);	 Catch:{ EOFException -> 0x005b }
        r6 = r6 + 1;
        goto L_0x0051;
    L_0x005b:
        r7 = move-exception;
        r7 = r9.lruEntries;	 Catch:{ all -> 0x00b5 }
        r7 = r7.size();	 Catch:{ all -> 0x00b5 }
        r7 = r6 - r7;	 Catch:{ all -> 0x00b5 }
        r9.redundantOpCount = r7;	 Catch:{ all -> 0x00b5 }
        r7 = r0.exhausted();	 Catch:{ all -> 0x00b5 }
        if (r7 != 0) goto L_0x0071;	 Catch:{ all -> 0x00b5 }
    L_0x006d:
        r9.rebuildJournal();	 Catch:{ all -> 0x00b5 }
        goto L_0x0077;	 Catch:{ all -> 0x00b5 }
    L_0x0071:
        r7 = r9.newJournalWriter();	 Catch:{ all -> 0x00b5 }
        r9.journalWriter = r7;	 Catch:{ all -> 0x00b5 }
    L_0x0077:
        okhttp3.internal.Util.closeQuietly(r0);
        return;
    L_0x007c:
        goto L_0x0081;
    L_0x007d:
        goto L_0x0081;
    L_0x007e:
        goto L_0x0081;
    L_0x007f:
        goto L_0x0081;
    L_0x0081:
        r6 = new java.io.IOException;	 Catch:{ all -> 0x00b5 }
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00b5 }
        r7.<init>();	 Catch:{ all -> 0x00b5 }
        r8 = "unexpected journal header: [";	 Catch:{ all -> 0x00b5 }
        r7.append(r8);	 Catch:{ all -> 0x00b5 }
        r7.append(r1);	 Catch:{ all -> 0x00b5 }
        r8 = ", ";	 Catch:{ all -> 0x00b5 }
        r7.append(r8);	 Catch:{ all -> 0x00b5 }
        r7.append(r2);	 Catch:{ all -> 0x00b5 }
        r8 = ", ";	 Catch:{ all -> 0x00b5 }
        r7.append(r8);	 Catch:{ all -> 0x00b5 }
        r7.append(r4);	 Catch:{ all -> 0x00b5 }
        r8 = ", ";	 Catch:{ all -> 0x00b5 }
        r7.append(r8);	 Catch:{ all -> 0x00b5 }
        r7.append(r5);	 Catch:{ all -> 0x00b5 }
        r8 = "]";	 Catch:{ all -> 0x00b5 }
        r7.append(r8);	 Catch:{ all -> 0x00b5 }
        r7 = r7.toString();	 Catch:{ all -> 0x00b5 }
        r6.<init>(r7);	 Catch:{ all -> 0x00b5 }
        throw r6;	 Catch:{ all -> 0x00b5 }
    L_0x00b5:
        r1 = move-exception;
        okhttp3.internal.Util.closeQuietly(r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.readJournal():void");
    }

    public synchronized void close() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:25:0x004a in {6, 11, 12, 13, 16, 21, 24} preds:[]
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
        r6 = this;
        monitor-enter(r6);
        r0 = r6.initialized;	 Catch:{ all -> 0x0047 }
        r1 = 1;	 Catch:{ all -> 0x0047 }
        if (r0 == 0) goto L_0x0042;	 Catch:{ all -> 0x0047 }
    L_0x0006:
        r0 = r6.closed;	 Catch:{ all -> 0x0047 }
        if (r0 == 0) goto L_0x000b;	 Catch:{ all -> 0x0047 }
    L_0x000a:
        goto L_0x0042;	 Catch:{ all -> 0x0047 }
    L_0x000b:
        r0 = r6.lruEntries;	 Catch:{ all -> 0x0047 }
        r0 = r0.values();	 Catch:{ all -> 0x0047 }
        r2 = r6.lruEntries;	 Catch:{ all -> 0x0047 }
        r2 = r2.size();	 Catch:{ all -> 0x0047 }
        r2 = new okhttp3.internal.cache.DiskLruCache$Entry[r2];	 Catch:{ all -> 0x0047 }
        r0 = r0.toArray(r2);	 Catch:{ all -> 0x0047 }
        r0 = (okhttp3.internal.cache.DiskLruCache$Entry[]) r0;	 Catch:{ all -> 0x0047 }
        r2 = r0.length;	 Catch:{ all -> 0x0047 }
        r3 = 0;	 Catch:{ all -> 0x0047 }
    L_0x0021:
        if (r3 >= r2) goto L_0x0033;	 Catch:{ all -> 0x0047 }
    L_0x0023:
        r4 = r0[r3];	 Catch:{ all -> 0x0047 }
        r5 = r4.currentEditor;	 Catch:{ all -> 0x0047 }
        if (r5 == 0) goto L_0x002f;	 Catch:{ all -> 0x0047 }
    L_0x0029:
        r5 = r4.currentEditor;	 Catch:{ all -> 0x0047 }
        r5.abort();	 Catch:{ all -> 0x0047 }
        goto L_0x0030;	 Catch:{ all -> 0x0047 }
    L_0x0030:
        r3 = r3 + 1;	 Catch:{ all -> 0x0047 }
        goto L_0x0021;	 Catch:{ all -> 0x0047 }
    L_0x0033:
        r6.trimToSize();	 Catch:{ all -> 0x0047 }
        r0 = r6.journalWriter;	 Catch:{ all -> 0x0047 }
        r0.close();	 Catch:{ all -> 0x0047 }
        r0 = 0;	 Catch:{ all -> 0x0047 }
        r6.journalWriter = r0;	 Catch:{ all -> 0x0047 }
        r6.closed = r1;	 Catch:{ all -> 0x0047 }
        monitor-exit(r6);
        return;
        r6.closed = r1;	 Catch:{ all -> 0x0047 }
        monitor-exit(r6);
        return;
    L_0x0047:
        r0 = move-exception;
        monitor-exit(r6);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.close():void");
    }

    synchronized void completeEdit(okhttp3.internal.cache.DiskLruCache$Editor r11, boolean r12) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:62:0x0176 in {18, 19, 23, 24, 25, 33, 34, 35, 36, 41, 42, 43, 48, 49, 51, 54, 58, 61} preds:[]
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
        r10 = this;
        monitor-enter(r10);
        r0 = r11.entry;	 Catch:{ all -> 0x0173 }
        r1 = r0.currentEditor;	 Catch:{ all -> 0x0173 }
        if (r1 != r11) goto L_0x016a;	 Catch:{ all -> 0x0173 }
    L_0x0007:
        r1 = 0;	 Catch:{ all -> 0x0173 }
        if (r12 == 0) goto L_0x0061;	 Catch:{ all -> 0x0173 }
    L_0x000a:
        r2 = r0.readable;	 Catch:{ all -> 0x0173 }
        if (r2 != 0) goto L_0x0061;	 Catch:{ all -> 0x0173 }
        r2 = r1;	 Catch:{ all -> 0x0173 }
        r3 = r10.valueCount;	 Catch:{ all -> 0x0173 }
        if (r2 >= r3) goto L_0x005f;	 Catch:{ all -> 0x0173 }
        r3 = r11.written;	 Catch:{ all -> 0x0173 }
        r3 = r3[r2];	 Catch:{ all -> 0x0173 }
        if (r3 == 0) goto L_0x003b;	 Catch:{ all -> 0x0173 }
        r3 = r10.fileSystem;	 Catch:{ all -> 0x0173 }
        r4 = r0.dirtyFiles;	 Catch:{ all -> 0x0173 }
        r4 = r4[r2];	 Catch:{ all -> 0x0173 }
        r3 = r3.exists(r4);	 Catch:{ all -> 0x0173 }
        if (r3 != 0) goto L_0x0037;	 Catch:{ all -> 0x0173 }
        r11.abort();	 Catch:{ all -> 0x0173 }
        monitor-exit(r10);
        return;
        r2 = r2 + 1;
        goto L_0x0010;
        r11.abort();	 Catch:{ all -> 0x0173 }
        r1 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0173 }
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0173 }
        r3.<init>();	 Catch:{ all -> 0x0173 }
        r4 = "Newly created entry didn't create value for index ";	 Catch:{ all -> 0x0173 }
        r3.append(r4);	 Catch:{ all -> 0x0173 }
        r3.append(r2);	 Catch:{ all -> 0x0173 }
        r3 = r3.toString();	 Catch:{ all -> 0x0173 }
        r1.<init>(r3);	 Catch:{ all -> 0x0173 }
        throw r1;	 Catch:{ all -> 0x0173 }
        goto L_0x0062;	 Catch:{ all -> 0x0173 }
        r2 = r10.valueCount;	 Catch:{ all -> 0x0173 }
        if (r1 >= r2) goto L_0x00bd;	 Catch:{ all -> 0x0173 }
        r2 = r0.dirtyFiles;	 Catch:{ all -> 0x0173 }
        r2 = r2[r1];	 Catch:{ all -> 0x0173 }
        if (r12 == 0) goto L_0x00b1;	 Catch:{ all -> 0x0173 }
        r3 = r10.fileSystem;	 Catch:{ all -> 0x0173 }
        r3 = r3.exists(r2);	 Catch:{ all -> 0x0173 }
        if (r3 == 0) goto L_0x00af;	 Catch:{ all -> 0x0173 }
        r3 = r0.cleanFiles;	 Catch:{ all -> 0x0173 }
        r3 = r3[r1];	 Catch:{ all -> 0x0173 }
        r4 = r10.fileSystem;	 Catch:{ all -> 0x0173 }
        r4.rename(r2, r3);	 Catch:{ all -> 0x0173 }
        r4 = r0.lengths;	 Catch:{ all -> 0x0173 }
        r5 = r4[r1];	 Catch:{ all -> 0x0173 }
        r4 = r5;	 Catch:{ all -> 0x0173 }
        r6 = r10.fileSystem;	 Catch:{ all -> 0x0173 }
        r6 = r6.size(r3);	 Catch:{ all -> 0x0173 }
        r8 = r0.lengths;	 Catch:{ all -> 0x0173 }
        r8[r1] = r6;	 Catch:{ all -> 0x0173 }
        r8 = r10.size;	 Catch:{ all -> 0x0173 }
        r8 = r8 - r4;	 Catch:{ all -> 0x0173 }
        r8 = r8 + r6;	 Catch:{ all -> 0x0173 }
        r10.size = r8;	 Catch:{ all -> 0x0173 }
        goto L_0x00b9;	 Catch:{ all -> 0x0173 }
        goto L_0x00b9;	 Catch:{ all -> 0x0173 }
        r3 = r10.fileSystem;	 Catch:{ all -> 0x0173 }
        r3.delete(r2);	 Catch:{ all -> 0x0173 }
        r1 = r1 + 1;	 Catch:{ all -> 0x0173 }
        goto L_0x0064;	 Catch:{ all -> 0x0173 }
        r1 = r10.redundantOpCount;	 Catch:{ all -> 0x0173 }
        r2 = 1;	 Catch:{ all -> 0x0173 }
        r1 = r1 + r2;	 Catch:{ all -> 0x0173 }
        r10.redundantOpCount = r1;	 Catch:{ all -> 0x0173 }
        r1 = 0;	 Catch:{ all -> 0x0173 }
        r0.currentEditor = r1;	 Catch:{ all -> 0x0173 }
        r1 = r0.readable;	 Catch:{ all -> 0x0173 }
        r1 = r1 | r12;	 Catch:{ all -> 0x0173 }
        r3 = 10;	 Catch:{ all -> 0x0173 }
        r4 = 32;	 Catch:{ all -> 0x0173 }
        if (r1 == 0) goto L_0x0112;	 Catch:{ all -> 0x0173 }
        r0.readable = r2;	 Catch:{ all -> 0x0173 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0173 }
        r2 = "CLEAN";	 Catch:{ all -> 0x0173 }
        r1 = r1.writeUtf8(r2);	 Catch:{ all -> 0x0173 }
        r1.writeByte(r4);	 Catch:{ all -> 0x0173 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0173 }
        r2 = r0.key;	 Catch:{ all -> 0x0173 }
        r1.writeUtf8(r2);	 Catch:{ all -> 0x0173 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0173 }
        r0.writeLengths(r1);	 Catch:{ all -> 0x0173 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0173 }
        r1.writeByte(r3);	 Catch:{ all -> 0x0173 }
        if (r12 == 0) goto L_0x0110;	 Catch:{ all -> 0x0173 }
        r1 = r10.nextSequenceNumber;	 Catch:{ all -> 0x0173 }
        r3 = 1;	 Catch:{ all -> 0x0173 }
        r3 = r3 + r1;	 Catch:{ all -> 0x0173 }
        r10.nextSequenceNumber = r3;	 Catch:{ all -> 0x0173 }
        r0.sequenceNumber = r1;	 Catch:{ all -> 0x0173 }
        goto L_0x013e;	 Catch:{ all -> 0x0173 }
        goto L_0x013e;	 Catch:{ all -> 0x0173 }
        r1 = r10.lruEntries;	 Catch:{ all -> 0x0173 }
        r2 = r0.key;	 Catch:{ all -> 0x0173 }
        r1.remove(r2);	 Catch:{ all -> 0x0173 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0173 }
        r2 = "REMOVE";	 Catch:{ all -> 0x0173 }
        r1 = r1.writeUtf8(r2);	 Catch:{ all -> 0x0173 }
        r1.writeByte(r4);	 Catch:{ all -> 0x0173 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0173 }
        r2 = r0.key;	 Catch:{ all -> 0x0173 }
        r1.writeUtf8(r2);	 Catch:{ all -> 0x0173 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0173 }
        r1.writeByte(r3);	 Catch:{ all -> 0x0173 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0173 }
        r1.flush();	 Catch:{ all -> 0x0173 }
        r1 = r10.size;	 Catch:{ all -> 0x0173 }
        r3 = r10.maxSize;	 Catch:{ all -> 0x0173 }
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));	 Catch:{ all -> 0x0173 }
        if (r5 > 0) goto L_0x015b;	 Catch:{ all -> 0x0173 }
        r1 = r10.journalRebuildRequired();	 Catch:{ all -> 0x0173 }
        if (r1 == 0) goto L_0x0159;	 Catch:{ all -> 0x0173 }
    L_0x0158:
        goto L_0x015b;	 Catch:{ all -> 0x0173 }
        goto L_0x0167;	 Catch:{ all -> 0x0173 }
        r1 = r10.executor;	 Catch:{ all -> 0x0173 }
        r2 = r10.cleanupRunnable;	 Catch:{ all -> 0x0173 }
        r1.execute(r2);	 Catch:{ all -> 0x0173 }
        monitor-exit(r10);
        return;
        r1 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0173 }
        r1.<init>();	 Catch:{ all -> 0x0173 }
        throw r1;	 Catch:{ all -> 0x0173 }
    L_0x0173:
        r11 = move-exception;
        monitor-exit(r10);
        throw r11;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.completeEdit(okhttp3.internal.cache.DiskLruCache$Editor, boolean):void");
    }

    public synchronized void evictAll() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x002d in {4, 7, 10} preds:[]
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
        r5 = this;
        monitor-enter(r5);
        r5.initialize();	 Catch:{ all -> 0x002a }
        r0 = r5.lruEntries;	 Catch:{ all -> 0x002a }
        r0 = r0.values();	 Catch:{ all -> 0x002a }
        r1 = r5.lruEntries;	 Catch:{ all -> 0x002a }
        r1 = r1.size();	 Catch:{ all -> 0x002a }
        r1 = new okhttp3.internal.cache.DiskLruCache$Entry[r1];	 Catch:{ all -> 0x002a }
        r0 = r0.toArray(r1);	 Catch:{ all -> 0x002a }
        r0 = (okhttp3.internal.cache.DiskLruCache$Entry[]) r0;	 Catch:{ all -> 0x002a }
        r1 = r0.length;	 Catch:{ all -> 0x002a }
        r2 = 0;	 Catch:{ all -> 0x002a }
        r3 = 0;	 Catch:{ all -> 0x002a }
    L_0x001b:
        if (r3 >= r1) goto L_0x0026;	 Catch:{ all -> 0x002a }
    L_0x001d:
        r4 = r0[r3];	 Catch:{ all -> 0x002a }
        r5.removeEntry(r4);	 Catch:{ all -> 0x002a }
        r3 = r3 + 1;	 Catch:{ all -> 0x002a }
        goto L_0x001b;	 Catch:{ all -> 0x002a }
    L_0x0026:
        r5.mostRecentTrimFailed = r2;	 Catch:{ all -> 0x002a }
        monitor-exit(r5);
        return;
    L_0x002a:
        r0 = move-exception;
        monitor-exit(r5);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.evictAll():void");
    }

    synchronized void rebuildJournal() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:31:0x00c4 in {4, 5, 13, 14, 15, 19, 20, 23, 27, 30} preds:[]
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
        r6 = this;
        monitor-enter(r6);
        r0 = r6.journalWriter;	 Catch:{ all -> 0x00c1 }
        if (r0 == 0) goto L_0x000b;	 Catch:{ all -> 0x00c1 }
    L_0x0005:
        r0 = r6.journalWriter;	 Catch:{ all -> 0x00c1 }
        r0.close();	 Catch:{ all -> 0x00c1 }
        goto L_0x000c;	 Catch:{ all -> 0x00c1 }
    L_0x000c:
        r0 = r6.fileSystem;	 Catch:{ all -> 0x00c1 }
        r1 = r6.journalFileTmp;	 Catch:{ all -> 0x00c1 }
        r0 = r0.sink(r1);	 Catch:{ all -> 0x00c1 }
        r0 = okio.Okio.buffer(r0);	 Catch:{ all -> 0x00c1 }
        r1 = "libcore.io.DiskLruCache";	 Catch:{ all -> 0x00bc }
        r1 = r0.writeUtf8(r1);	 Catch:{ all -> 0x00bc }
        r2 = 10;	 Catch:{ all -> 0x00bc }
        r1.writeByte(r2);	 Catch:{ all -> 0x00bc }
        r1 = "1";	 Catch:{ all -> 0x00bc }
        r1 = r0.writeUtf8(r1);	 Catch:{ all -> 0x00bc }
        r1.writeByte(r2);	 Catch:{ all -> 0x00bc }
        r1 = r6.appVersion;	 Catch:{ all -> 0x00bc }
        r3 = (long) r1;	 Catch:{ all -> 0x00bc }
        r1 = r0.writeDecimalLong(r3);	 Catch:{ all -> 0x00bc }
        r1.writeByte(r2);	 Catch:{ all -> 0x00bc }
        r1 = r6.valueCount;	 Catch:{ all -> 0x00bc }
        r3 = (long) r1;	 Catch:{ all -> 0x00bc }
        r1 = r0.writeDecimalLong(r3);	 Catch:{ all -> 0x00bc }
        r1.writeByte(r2);	 Catch:{ all -> 0x00bc }
        r0.writeByte(r2);	 Catch:{ all -> 0x00bc }
        r1 = r6.lruEntries;	 Catch:{ all -> 0x00bc }
        r1 = r1.values();	 Catch:{ all -> 0x00bc }
        r1 = r1.iterator();	 Catch:{ all -> 0x00bc }
    L_0x004d:
        r3 = r1.hasNext();	 Catch:{ all -> 0x00bc }
        if (r3 == 0) goto L_0x0086;	 Catch:{ all -> 0x00bc }
    L_0x0053:
        r3 = r1.next();	 Catch:{ all -> 0x00bc }
        r3 = (okhttp3.internal.cache.DiskLruCache$Entry) r3;	 Catch:{ all -> 0x00bc }
        r4 = r3.currentEditor;	 Catch:{ all -> 0x00bc }
        r5 = 32;	 Catch:{ all -> 0x00bc }
        if (r4 == 0) goto L_0x0071;	 Catch:{ all -> 0x00bc }
    L_0x005f:
        r4 = "DIRTY";	 Catch:{ all -> 0x00bc }
        r4 = r0.writeUtf8(r4);	 Catch:{ all -> 0x00bc }
        r4.writeByte(r5);	 Catch:{ all -> 0x00bc }
        r4 = r3.key;	 Catch:{ all -> 0x00bc }
        r0.writeUtf8(r4);	 Catch:{ all -> 0x00bc }
        r0.writeByte(r2);	 Catch:{ all -> 0x00bc }
        goto L_0x0085;	 Catch:{ all -> 0x00bc }
    L_0x0071:
        r4 = "CLEAN";	 Catch:{ all -> 0x00bc }
        r4 = r0.writeUtf8(r4);	 Catch:{ all -> 0x00bc }
        r4.writeByte(r5);	 Catch:{ all -> 0x00bc }
        r4 = r3.key;	 Catch:{ all -> 0x00bc }
        r0.writeUtf8(r4);	 Catch:{ all -> 0x00bc }
        r3.writeLengths(r0);	 Catch:{ all -> 0x00bc }
        r0.writeByte(r2);	 Catch:{ all -> 0x00bc }
    L_0x0085:
        goto L_0x004d;
    L_0x0086:
        r0.close();	 Catch:{ all -> 0x00c1 }
        r1 = r6.fileSystem;	 Catch:{ all -> 0x00c1 }
        r2 = r6.journalFile;	 Catch:{ all -> 0x00c1 }
        r1 = r1.exists(r2);	 Catch:{ all -> 0x00c1 }
        if (r1 == 0) goto L_0x009e;	 Catch:{ all -> 0x00c1 }
    L_0x0094:
        r1 = r6.fileSystem;	 Catch:{ all -> 0x00c1 }
        r2 = r6.journalFile;	 Catch:{ all -> 0x00c1 }
        r3 = r6.journalFileBackup;	 Catch:{ all -> 0x00c1 }
        r1.rename(r2, r3);	 Catch:{ all -> 0x00c1 }
        goto L_0x009f;	 Catch:{ all -> 0x00c1 }
    L_0x009f:
        r1 = r6.fileSystem;	 Catch:{ all -> 0x00c1 }
        r2 = r6.journalFileTmp;	 Catch:{ all -> 0x00c1 }
        r3 = r6.journalFile;	 Catch:{ all -> 0x00c1 }
        r1.rename(r2, r3);	 Catch:{ all -> 0x00c1 }
        r1 = r6.fileSystem;	 Catch:{ all -> 0x00c1 }
        r2 = r6.journalFileBackup;	 Catch:{ all -> 0x00c1 }
        r1.delete(r2);	 Catch:{ all -> 0x00c1 }
        r1 = r6.newJournalWriter();	 Catch:{ all -> 0x00c1 }
        r6.journalWriter = r1;	 Catch:{ all -> 0x00c1 }
        r1 = 0;	 Catch:{ all -> 0x00c1 }
        r6.hasJournalErrors = r1;	 Catch:{ all -> 0x00c1 }
        r6.mostRecentRebuildFailed = r1;	 Catch:{ all -> 0x00c1 }
        monitor-exit(r6);
        return;
    L_0x00bc:
        r1 = move-exception;
        r0.close();	 Catch:{ all -> 0x00c1 }
        throw r1;	 Catch:{ all -> 0x00c1 }
    L_0x00c1:
        r0 = move-exception;
        monitor-exit(r6);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.rebuildJournal():void");
    }

    DiskLruCache(FileSystem fileSystem, File directory, int appVersion, int valueCount, long maxSize, Executor executor) {
        this.fileSystem = fileSystem;
        this.directory = directory;
        this.appVersion = appVersion;
        this.journalFile = new File(directory, JOURNAL_FILE);
        this.journalFileTmp = new File(directory, JOURNAL_FILE_TEMP);
        this.journalFileBackup = new File(directory, JOURNAL_FILE_BACKUP);
        this.valueCount = valueCount;
        this.maxSize = maxSize;
        this.executor = executor;
    }

    public synchronized void initialize() throws IOException {
        if (!this.initialized) {
            if (this.fileSystem.exists(this.journalFileBackup)) {
                if (this.fileSystem.exists(this.journalFile)) {
                    this.fileSystem.delete(this.journalFileBackup);
                } else {
                    this.fileSystem.rename(this.journalFileBackup, this.journalFile);
                }
            }
            if (this.fileSystem.exists(this.journalFile)) {
                try {
                    readJournal();
                    processJournal();
                    this.initialized = true;
                } catch (IOException journalIsCorrupt) {
                    Platform platform = Platform.get();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("DiskLruCache ");
                    stringBuilder.append(this.directory);
                    stringBuilder.append(" is corrupt: ");
                    stringBuilder.append(journalIsCorrupt.getMessage());
                    stringBuilder.append(", removing");
                    platform.log(5, stringBuilder.toString(), journalIsCorrupt);
                    delete();
                } finally {
                    this.closed = false;
                }
            } else {
                rebuildJournal();
                this.initialized = true;
            }
        }
    }

    public static DiskLruCache create(FileSystem fileSystem, File directory, int appVersion, int valueCount, long maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        } else if (valueCount > 0) {
            return new DiskLruCache(fileSystem, directory, appVersion, valueCount, maxSize, new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory("OkHttp DiskLruCache", true)));
        } else {
            throw new IllegalArgumentException("valueCount <= 0");
        }
    }

    private BufferedSink newJournalWriter() throws FileNotFoundException {
        return Okio.buffer(new DiskLruCache$2(this, this.fileSystem.appendingSink(this.journalFile)));
    }

    private void readJournalLine(String line) throws IOException {
        int firstSpace = line.indexOf(32);
        if (firstSpace != -1) {
            String key;
            int keyBegin = firstSpace + 1;
            int secondSpace = line.indexOf(32, keyBegin);
            if (secondSpace == -1) {
                key = line.substring(keyBegin);
                if (firstSpace == REMOVE.length() && line.startsWith(REMOVE)) {
                    this.lruEntries.remove(key);
                    return;
                }
            } else {
                key = line.substring(keyBegin, secondSpace);
            }
            DiskLruCache$Entry entry = (DiskLruCache$Entry) this.lruEntries.get(key);
            if (entry == null) {
                entry = new DiskLruCache$Entry(this, key);
                this.lruEntries.put(key, entry);
            }
            if (secondSpace != -1 && firstSpace == CLEAN.length() && line.startsWith(CLEAN)) {
                String[] parts = line.substring(secondSpace + 1).split(StringUtils.SPACE);
                entry.readable = true;
                entry.currentEditor = null;
                entry.setLengths(parts);
            } else if (secondSpace == -1 && firstSpace == DIRTY.length() && line.startsWith(DIRTY)) {
                entry.currentEditor = new DiskLruCache$Editor(this, entry);
            } else if (!(secondSpace == -1 && firstSpace == READ.length() && line.startsWith(READ))) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("unexpected journal line: ");
                stringBuilder.append(line);
                throw new IOException(stringBuilder.toString());
            }
            return;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("unexpected journal line: ");
        stringBuilder2.append(line);
        throw new IOException(stringBuilder2.toString());
    }

    private void processJournal() throws IOException {
        this.fileSystem.delete(this.journalFileTmp);
        Iterator<DiskLruCache$Entry> i = this.lruEntries.values().iterator();
        while (i.hasNext()) {
            DiskLruCache$Entry entry = (DiskLruCache$Entry) i.next();
            int t;
            if (entry.currentEditor == null) {
                for (t = 0; t < this.valueCount; t++) {
                    this.size += entry.lengths[t];
                }
            } else {
                entry.currentEditor = null;
                for (t = 0; t < this.valueCount; t++) {
                    this.fileSystem.delete(entry.cleanFiles[t]);
                    this.fileSystem.delete(entry.dirtyFiles[t]);
                }
                i.remove();
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized okhttp3.internal.cache.DiskLruCache.Snapshot get(java.lang.String r5) throws java.io.IOException {
        /*
        r4 = this;
        monitor-enter(r4);
        r4.initialize();	 Catch:{ all -> 0x0053 }
        r4.checkNotClosed();	 Catch:{ all -> 0x0053 }
        r4.validateKey(r5);	 Catch:{ all -> 0x0053 }
        r0 = r4.lruEntries;	 Catch:{ all -> 0x0053 }
        r0 = r0.get(r5);	 Catch:{ all -> 0x0053 }
        r0 = (okhttp3.internal.cache.DiskLruCache$Entry) r0;	 Catch:{ all -> 0x0053 }
        r1 = 0;
        if (r0 == 0) goto L_0x0050;
    L_0x0015:
        r2 = r0.readable;	 Catch:{ all -> 0x0053 }
        if (r2 != 0) goto L_0x001a;
    L_0x0019:
        goto L_0x0050;
    L_0x001a:
        r2 = r0.snapshot();	 Catch:{ all -> 0x0053 }
        if (r2 != 0) goto L_0x0022;
    L_0x0020:
        monitor-exit(r4);
        return r1;
    L_0x0022:
        r1 = r4.redundantOpCount;	 Catch:{ all -> 0x0053 }
        r1 = r1 + 1;
        r4.redundantOpCount = r1;	 Catch:{ all -> 0x0053 }
        r1 = r4.journalWriter;	 Catch:{ all -> 0x0053 }
        r3 = "READ";
        r1 = r1.writeUtf8(r3);	 Catch:{ all -> 0x0053 }
        r3 = 32;
        r1 = r1.writeByte(r3);	 Catch:{ all -> 0x0053 }
        r1 = r1.writeUtf8(r5);	 Catch:{ all -> 0x0053 }
        r3 = 10;
        r1.writeByte(r3);	 Catch:{ all -> 0x0053 }
        r1 = r4.journalRebuildRequired();	 Catch:{ all -> 0x0053 }
        if (r1 == 0) goto L_0x004d;
    L_0x0045:
        r1 = r4.executor;	 Catch:{ all -> 0x0053 }
        r3 = r4.cleanupRunnable;	 Catch:{ all -> 0x0053 }
        r1.execute(r3);	 Catch:{ all -> 0x0053 }
        goto L_0x004e;
    L_0x004e:
        monitor-exit(r4);
        return r2;
        monitor-exit(r4);
        return r1;
    L_0x0053:
        r5 = move-exception;
        monitor-exit(r4);
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.get(java.lang.String):okhttp3.internal.cache.DiskLruCache$Snapshot");
    }

    @Nullable
    public DiskLruCache$Editor edit(String key) throws IOException {
        return edit(key, -1);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    synchronized okhttp3.internal.cache.DiskLruCache$Editor edit(java.lang.String r6, long r7) throws java.io.IOException {
        /*
        r5 = this;
        monitor-enter(r5);
        r5.initialize();	 Catch:{ all -> 0x007a }
        r5.checkNotClosed();	 Catch:{ all -> 0x007a }
        r5.validateKey(r6);	 Catch:{ all -> 0x007a }
        r0 = r5.lruEntries;	 Catch:{ all -> 0x007a }
        r0 = r0.get(r6);	 Catch:{ all -> 0x007a }
        r0 = (okhttp3.internal.cache.DiskLruCache$Entry) r0;	 Catch:{ all -> 0x007a }
        r1 = -1;
        r3 = 0;
        r4 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1));
        if (r4 == 0) goto L_0x0023;
    L_0x0019:
        if (r0 == 0) goto L_0x0021;
    L_0x001b:
        r1 = r0.sequenceNumber;	 Catch:{ all -> 0x007a }
        r4 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1));
        if (r4 == 0) goto L_0x0023;
    L_0x0021:
        monitor-exit(r5);
        return r3;
        if (r0 == 0) goto L_0x002c;
    L_0x0026:
        r1 = r0.currentEditor;	 Catch:{ all -> 0x007a }
        if (r1 == 0) goto L_0x002c;
    L_0x002a:
        monitor-exit(r5);
        return r3;
        r1 = r5.mostRecentTrimFailed;	 Catch:{ all -> 0x007a }
        if (r1 != 0) goto L_0x0070;
    L_0x0031:
        r1 = r5.mostRecentRebuildFailed;	 Catch:{ all -> 0x007a }
        if (r1 == 0) goto L_0x0036;
    L_0x0035:
        goto L_0x0070;
    L_0x0036:
        r1 = r5.journalWriter;	 Catch:{ all -> 0x007a }
        r2 = "DIRTY";
        r1 = r1.writeUtf8(r2);	 Catch:{ all -> 0x007a }
        r2 = 32;
        r1 = r1.writeByte(r2);	 Catch:{ all -> 0x007a }
        r1 = r1.writeUtf8(r6);	 Catch:{ all -> 0x007a }
        r2 = 10;
        r1.writeByte(r2);	 Catch:{ all -> 0x007a }
        r1 = r5.journalWriter;	 Catch:{ all -> 0x007a }
        r1.flush();	 Catch:{ all -> 0x007a }
        r1 = r5.hasJournalErrors;	 Catch:{ all -> 0x007a }
        if (r1 == 0) goto L_0x0058;
    L_0x0056:
        monitor-exit(r5);
        return r3;
    L_0x0058:
        if (r0 != 0) goto L_0x0066;
    L_0x005a:
        r1 = new okhttp3.internal.cache.DiskLruCache$Entry;	 Catch:{ all -> 0x007a }
        r1.<init>(r5, r6);	 Catch:{ all -> 0x007a }
        r0 = r1;
        r1 = r5.lruEntries;	 Catch:{ all -> 0x007a }
        r1.put(r6, r0);	 Catch:{ all -> 0x007a }
        goto L_0x0067;
    L_0x0067:
        r1 = new okhttp3.internal.cache.DiskLruCache$Editor;	 Catch:{ all -> 0x007a }
        r1.<init>(r5, r0);	 Catch:{ all -> 0x007a }
        r0.currentEditor = r1;	 Catch:{ all -> 0x007a }
        monitor-exit(r5);
        return r1;
        r1 = r5.executor;	 Catch:{ all -> 0x007a }
        r2 = r5.cleanupRunnable;	 Catch:{ all -> 0x007a }
        r1.execute(r2);	 Catch:{ all -> 0x007a }
        monitor-exit(r5);
        return r3;
    L_0x007a:
        r6 = move-exception;
        monitor-exit(r5);
        throw r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.edit(java.lang.String, long):okhttp3.internal.cache.DiskLruCache$Editor");
    }

    public File getDirectory() {
        return this.directory;
    }

    public synchronized long getMaxSize() {
        return this.maxSize;
    }

    public synchronized void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
        if (this.initialized) {
            this.executor.execute(this.cleanupRunnable);
        }
    }

    public synchronized long size() throws IOException {
        initialize();
        return this.size;
    }

    boolean journalRebuildRequired() {
        int i = this.redundantOpCount;
        if (i >= 2000) {
            if (i >= this.lruEntries.size()) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean remove(java.lang.String r9) throws java.io.IOException {
        /*
        r8 = this;
        monitor-enter(r8);
        r8.initialize();	 Catch:{ all -> 0x0029 }
        r8.checkNotClosed();	 Catch:{ all -> 0x0029 }
        r8.validateKey(r9);	 Catch:{ all -> 0x0029 }
        r0 = r8.lruEntries;	 Catch:{ all -> 0x0029 }
        r0 = r0.get(r9);	 Catch:{ all -> 0x0029 }
        r0 = (okhttp3.internal.cache.DiskLruCache$Entry) r0;	 Catch:{ all -> 0x0029 }
        r1 = 0;
        if (r0 != 0) goto L_0x0017;
    L_0x0015:
        monitor-exit(r8);
        return r1;
    L_0x0017:
        r2 = r8.removeEntry(r0);	 Catch:{ all -> 0x0029 }
        if (r2 == 0) goto L_0x0027;
    L_0x001d:
        r3 = r8.size;	 Catch:{ all -> 0x0029 }
        r5 = r8.maxSize;	 Catch:{ all -> 0x0029 }
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 > 0) goto L_0x0027;
    L_0x0025:
        r8.mostRecentTrimFailed = r1;	 Catch:{ all -> 0x0029 }
    L_0x0027:
        monitor-exit(r8);
        return r2;
    L_0x0029:
        r9 = move-exception;
        monitor-exit(r8);
        throw r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.remove(java.lang.String):boolean");
    }

    boolean removeEntry(DiskLruCache$Entry entry) throws IOException {
        if (entry.currentEditor != null) {
            entry.currentEditor.detach();
        }
        for (int i = 0; i < this.valueCount; i++) {
            this.fileSystem.delete(entry.cleanFiles[i]);
            this.size -= entry.lengths[i];
            entry.lengths[i] = 0;
        }
        this.redundantOpCount++;
        this.journalWriter.writeUtf8(REMOVE).writeByte(32).writeUtf8(entry.key).writeByte(10);
        this.lruEntries.remove(entry.key);
        if (journalRebuildRequired()) {
            this.executor.execute(this.cleanupRunnable);
        }
        return true;
    }

    public synchronized boolean isClosed() {
        return this.closed;
    }

    private synchronized void checkNotClosed() {
        if (isClosed()) {
            throw new IllegalStateException("cache is closed");
        }
    }

    public synchronized void flush() throws IOException {
        if (this.initialized) {
            checkNotClosed();
            trimToSize();
            this.journalWriter.flush();
        }
    }

    void trimToSize() throws IOException {
        while (this.size > this.maxSize) {
            removeEntry((DiskLruCache$Entry) this.lruEntries.values().iterator().next());
        }
        this.mostRecentTrimFailed = false;
    }

    public void delete() throws IOException {
        close();
        this.fileSystem.deleteContents(this.directory);
    }

    private void validateKey(String key) {
        if (!LEGAL_KEY_PATTERN.matcher(key).matches()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("keys must match regex [a-z0-9_-]{1,120}: \"");
            stringBuilder.append(key);
            stringBuilder.append("\"");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public synchronized Iterator<Snapshot> snapshots() throws IOException {
        initialize();
        return new DiskLruCache$3(this);
    }
}

package com.bumptech.glide.disklrucache;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;

public final class DiskLruCache implements Closeable {
    static final long ANY_SEQUENCE_NUMBER = -1;
    private static final String CLEAN = "CLEAN";
    private static final String DIRTY = "DIRTY";
    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_BACKUP = "journal.bkp";
    static final String JOURNAL_FILE_TEMP = "journal.tmp";
    static final String MAGIC = "libcore.io.DiskLruCache";
    private static final String READ = "READ";
    private static final String REMOVE = "REMOVE";
    static final String VERSION_1 = "1";
    private final int appVersion;
    private final Callable<Void> cleanupCallable = new C05231();
    private final File directory;
    final ThreadPoolExecutor executorService = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(), new DiskLruCacheThreadFactory());
    private final File journalFile;
    private final File journalFileBackup;
    private final File journalFileTmp;
    private Writer journalWriter;
    private final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap(0, 0.75f, true);
    private long maxSize;
    private long nextSequenceNumber = 0;
    private int redundantOpCount;
    private long size = 0;
    private final int valueCount;

    /* renamed from: com.bumptech.glide.disklrucache.DiskLruCache$1 */
    class C05231 implements Callable<Void> {
        C05231() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Void call() throws java.lang.Exception {
            /*
            r4 = this;
            r0 = com.bumptech.glide.disklrucache.DiskLruCache.this;
            monitor-enter(r0);
            r1 = com.bumptech.glide.disklrucache.DiskLruCache.this;	 Catch:{ all -> 0x002a }
            r1 = r1.journalWriter;	 Catch:{ all -> 0x002a }
            r2 = 0;
            if (r1 != 0) goto L_0x000e;
        L_0x000c:
            monitor-exit(r0);	 Catch:{ all -> 0x002a }
            return r2;
        L_0x000e:
            r1 = com.bumptech.glide.disklrucache.DiskLruCache.this;	 Catch:{ all -> 0x002a }
            r1.trimToSize();	 Catch:{ all -> 0x002a }
            r1 = com.bumptech.glide.disklrucache.DiskLruCache.this;	 Catch:{ all -> 0x002a }
            r1 = r1.journalRebuildRequired();	 Catch:{ all -> 0x002a }
            if (r1 == 0) goto L_0x0027;
        L_0x001b:
            r1 = com.bumptech.glide.disklrucache.DiskLruCache.this;	 Catch:{ all -> 0x002a }
            r1.rebuildJournal();	 Catch:{ all -> 0x002a }
            r1 = com.bumptech.glide.disklrucache.DiskLruCache.this;	 Catch:{ all -> 0x002a }
            r3 = 0;
            r1.redundantOpCount = r3;	 Catch:{ all -> 0x002a }
            goto L_0x0028;
        L_0x0028:
            monitor-exit(r0);	 Catch:{ all -> 0x002a }
            return r2;
        L_0x002a:
            r1 = move-exception;
            monitor-exit(r0);	 Catch:{ all -> 0x002a }
            throw r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.DiskLruCache.1.call():java.lang.Void");
        }
    }

    private static final class DiskLruCacheThreadFactory implements ThreadFactory {
        private DiskLruCacheThreadFactory() {
        }

        public synchronized Thread newThread(Runnable runnable) {
            Thread result;
            result = new Thread(runnable, "glide-disk-lru-cache-thread");
            result.setPriority(1);
            return result;
        }
    }

    public final class Editor {
        private boolean committed;
        private final Entry entry;
        private final boolean[] written;

        private Editor(Entry entry) {
            this.entry = entry;
            this.written = entry.readable ? null : new boolean[DiskLruCache.this.valueCount];
        }

        private InputStream newInputStream(int index) throws IOException {
            synchronized (DiskLruCache.this) {
                if (this.entry.currentEditor != this) {
                    throw new IllegalStateException();
                } else if (this.entry.readable) {
                    try {
                        InputStream fileInputStream = new FileInputStream(this.entry.getCleanFile(index));
                        return fileInputStream;
                    } catch (FileNotFoundException e) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }

        public String getString(int index) throws IOException {
            InputStream in = newInputStream(index);
            return in != null ? DiskLruCache.inputStreamToString(in) : null;
        }

        public File getFile(int index) throws IOException {
            File dirtyFile;
            synchronized (DiskLruCache.this) {
                if (this.entry.currentEditor == this) {
                    if (!this.entry.readable) {
                        this.written[index] = true;
                    }
                    dirtyFile = this.entry.getDirtyFile(index);
                    if (!DiskLruCache.this.directory.exists()) {
                        DiskLruCache.this.directory.mkdirs();
                    }
                } else {
                    throw new IllegalStateException();
                }
            }
            return dirtyFile;
        }

        public void set(int index, String value) throws IOException {
            Writer writer = null;
            try {
                writer = new OutputStreamWriter(new FileOutputStream(getFile(index)), Util.UTF_8);
                writer.write(value);
            } finally {
                Util.closeQuietly(writer);
            }
        }

        public void commit() throws IOException {
            DiskLruCache.this.completeEdit(this, true);
            this.committed = true;
        }

        public void abort() throws IOException {
            DiskLruCache.this.completeEdit(this, false);
        }

        public void abortUnlessCommitted() {
            if (!this.committed) {
                try {
                    abort();
                } catch (IOException e) {
                }
            }
        }
    }

    private final class Entry {
        File[] cleanFiles;
        private Editor currentEditor;
        File[] dirtyFiles;
        private final String key;
        private final long[] lengths;
        private boolean readable;
        private long sequenceNumber;

        private void setLengths(java.lang.String[] r5) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0027 in {7, 9, 12, 14} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r4 = this;
            r0 = r5.length;
            r1 = com.bumptech.glide.disklrucache.DiskLruCache.this;
            r1 = r1.valueCount;
            if (r0 != r1) goto L_0x0022;
        L_0x0009:
            r0 = 0;
        L_0x000a:
            r1 = r5.length;	 Catch:{ NumberFormatException -> 0x001c }
            if (r0 >= r1) goto L_0x001a;	 Catch:{ NumberFormatException -> 0x001c }
        L_0x000d:
            r1 = r4.lengths;	 Catch:{ NumberFormatException -> 0x001c }
            r2 = r5[r0];	 Catch:{ NumberFormatException -> 0x001c }
            r2 = java.lang.Long.parseLong(r2);	 Catch:{ NumberFormatException -> 0x001c }
            r1[r0] = r2;	 Catch:{ NumberFormatException -> 0x001c }
            r0 = r0 + 1;
            goto L_0x000a;
            return;
        L_0x001c:
            r0 = move-exception;
            r1 = r4.invalidLengths(r5);
            throw r1;
        L_0x0022:
            r0 = r4.invalidLengths(r5);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.DiskLruCache.Entry.setLengths(java.lang.String[]):void");
        }

        private Entry(String key) {
            this.key = key;
            this.lengths = new long[DiskLruCache.this.valueCount];
            this.cleanFiles = new File[DiskLruCache.this.valueCount];
            this.dirtyFiles = new File[DiskLruCache.this.valueCount];
            StringBuilder fileBuilder = new StringBuilder(key).append('.');
            int truncateTo = fileBuilder.length();
            for (int i = 0; i < DiskLruCache.this.valueCount; i++) {
                fileBuilder.append(i);
                this.cleanFiles[i] = new File(DiskLruCache.this.directory, fileBuilder.toString());
                fileBuilder.append(".tmp");
                this.dirtyFiles[i] = new File(DiskLruCache.this.directory, fileBuilder.toString());
                fileBuilder.setLength(truncateTo);
            }
        }

        public String getLengths() throws IOException {
            StringBuilder result = new StringBuilder();
            for (long size : this.lengths) {
                result.append(' ');
                result.append(size);
            }
            return result.toString();
        }

        private IOException invalidLengths(String[] strings) throws IOException {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("unexpected journal line: ");
            stringBuilder.append(Arrays.toString(strings));
            throw new IOException(stringBuilder.toString());
        }

        public File getCleanFile(int i) {
            return this.cleanFiles[i];
        }

        public File getDirtyFile(int i) {
            return this.dirtyFiles[i];
        }
    }

    public final class Value {
        private final File[] files;
        private final String key;
        private final long[] lengths;
        private final long sequenceNumber;

        private Value(String key, long sequenceNumber, File[] files, long[] lengths) {
            this.key = key;
            this.sequenceNumber = sequenceNumber;
            this.files = files;
            this.lengths = lengths;
        }

        public Editor edit() throws IOException {
            return DiskLruCache.this.edit(this.key, this.sequenceNumber);
        }

        public File getFile(int index) {
            return this.files[index];
        }

        public String getString(int index) throws IOException {
            return DiskLruCache.inputStreamToString(new FileInputStream(this.files[index]));
        }

        public long getLength(int index) {
            return this.lengths[index];
        }
    }

    private synchronized void completeEdit(com.bumptech.glide.disklrucache.DiskLruCache.Editor r11, boolean r12) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:62:0x0184 in {18, 19, 23, 24, 25, 33, 34, 35, 36, 41, 42, 43, 48, 49, 51, 54, 58, 61} preds:[]
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
        r10 = this;
        monitor-enter(r10);
        r0 = r11.entry;	 Catch:{ all -> 0x0181 }
        r1 = r0.currentEditor;	 Catch:{ all -> 0x0181 }
        if (r1 != r11) goto L_0x0178;	 Catch:{ all -> 0x0181 }
    L_0x000b:
        r1 = 0;	 Catch:{ all -> 0x0181 }
        if (r12 == 0) goto L_0x0065;	 Catch:{ all -> 0x0181 }
    L_0x000e:
        r2 = r0.readable;	 Catch:{ all -> 0x0181 }
        if (r2 != 0) goto L_0x0065;	 Catch:{ all -> 0x0181 }
        r2 = r1;	 Catch:{ all -> 0x0181 }
        r3 = r10.valueCount;	 Catch:{ all -> 0x0181 }
        if (r2 >= r3) goto L_0x0063;	 Catch:{ all -> 0x0181 }
        r3 = r11.written;	 Catch:{ all -> 0x0181 }
        r3 = r3[r2];	 Catch:{ all -> 0x0181 }
        if (r3 == 0) goto L_0x003f;	 Catch:{ all -> 0x0181 }
        r3 = r0.getDirtyFile(r2);	 Catch:{ all -> 0x0181 }
        r3 = r3.exists();	 Catch:{ all -> 0x0181 }
        if (r3 != 0) goto L_0x003b;	 Catch:{ all -> 0x0181 }
        r11.abort();	 Catch:{ all -> 0x0181 }
        monitor-exit(r10);
        return;
        r2 = r2 + 1;
        goto L_0x0016;
        r11.abort();	 Catch:{ all -> 0x0181 }
        r1 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0181 }
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0181 }
        r3.<init>();	 Catch:{ all -> 0x0181 }
        r4 = "Newly created entry didn't create value for index ";	 Catch:{ all -> 0x0181 }
        r3.append(r4);	 Catch:{ all -> 0x0181 }
        r3.append(r2);	 Catch:{ all -> 0x0181 }
        r3 = r3.toString();	 Catch:{ all -> 0x0181 }
        r1.<init>(r3);	 Catch:{ all -> 0x0181 }
        throw r1;	 Catch:{ all -> 0x0181 }
        goto L_0x0066;	 Catch:{ all -> 0x0181 }
        r2 = r10.valueCount;	 Catch:{ all -> 0x0181 }
        if (r1 >= r2) goto L_0x00b7;	 Catch:{ all -> 0x0181 }
        r2 = r0.getDirtyFile(r1);	 Catch:{ all -> 0x0181 }
        if (r12 == 0) goto L_0x00ae;	 Catch:{ all -> 0x0181 }
        r3 = r2.exists();	 Catch:{ all -> 0x0181 }
        if (r3 == 0) goto L_0x00ac;	 Catch:{ all -> 0x0181 }
        r3 = r0.getCleanFile(r1);	 Catch:{ all -> 0x0181 }
        r2.renameTo(r3);	 Catch:{ all -> 0x0181 }
        r4 = r0.lengths;	 Catch:{ all -> 0x0181 }
        r5 = r4[r1];	 Catch:{ all -> 0x0181 }
        r4 = r5;	 Catch:{ all -> 0x0181 }
        r6 = r3.length();	 Catch:{ all -> 0x0181 }
        r8 = r0.lengths;	 Catch:{ all -> 0x0181 }
        r8[r1] = r6;	 Catch:{ all -> 0x0181 }
        r8 = r10.size;	 Catch:{ all -> 0x0181 }
        r8 = r8 - r4;	 Catch:{ all -> 0x0181 }
        r8 = r8 + r6;	 Catch:{ all -> 0x0181 }
        r10.size = r8;	 Catch:{ all -> 0x0181 }
        goto L_0x00b3;	 Catch:{ all -> 0x0181 }
        goto L_0x00b3;	 Catch:{ all -> 0x0181 }
        deleteIfExists(r2);	 Catch:{ all -> 0x0181 }
        r1 = r1 + 1;	 Catch:{ all -> 0x0181 }
        goto L_0x0068;	 Catch:{ all -> 0x0181 }
        r1 = r10.redundantOpCount;	 Catch:{ all -> 0x0181 }
        r2 = 1;	 Catch:{ all -> 0x0181 }
        r1 = r1 + r2;	 Catch:{ all -> 0x0181 }
        r10.redundantOpCount = r1;	 Catch:{ all -> 0x0181 }
        r1 = 0;	 Catch:{ all -> 0x0181 }
        r0.currentEditor = r1;	 Catch:{ all -> 0x0181 }
        r1 = r0.readable;	 Catch:{ all -> 0x0181 }
        r1 = r1 | r12;	 Catch:{ all -> 0x0181 }
        r3 = 10;	 Catch:{ all -> 0x0181 }
        r4 = 32;	 Catch:{ all -> 0x0181 }
        if (r1 == 0) goto L_0x011a;	 Catch:{ all -> 0x0181 }
        r0.readable = r2;	 Catch:{ all -> 0x0181 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0181 }
        r2 = "CLEAN";	 Catch:{ all -> 0x0181 }
        r1.append(r2);	 Catch:{ all -> 0x0181 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0181 }
        r1.append(r4);	 Catch:{ all -> 0x0181 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0181 }
        r2 = r0.key;	 Catch:{ all -> 0x0181 }
        r1.append(r2);	 Catch:{ all -> 0x0181 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0181 }
        r2 = r0.getLengths();	 Catch:{ all -> 0x0181 }
        r1.append(r2);	 Catch:{ all -> 0x0181 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0181 }
        r1.append(r3);	 Catch:{ all -> 0x0181 }
        if (r12 == 0) goto L_0x0118;	 Catch:{ all -> 0x0181 }
        r1 = r10.nextSequenceNumber;	 Catch:{ all -> 0x0181 }
        r3 = 1;	 Catch:{ all -> 0x0181 }
        r3 = r3 + r1;	 Catch:{ all -> 0x0181 }
        r10.nextSequenceNumber = r3;	 Catch:{ all -> 0x0181 }
        r0.sequenceNumber = r1;	 Catch:{ all -> 0x0181 }
        goto L_0x014c;	 Catch:{ all -> 0x0181 }
        goto L_0x014c;	 Catch:{ all -> 0x0181 }
        r1 = r10.lruEntries;	 Catch:{ all -> 0x0181 }
        r2 = r0.key;	 Catch:{ all -> 0x0181 }
        r1.remove(r2);	 Catch:{ all -> 0x0181 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0181 }
        r2 = "REMOVE";	 Catch:{ all -> 0x0181 }
        r1.append(r2);	 Catch:{ all -> 0x0181 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0181 }
        r1.append(r4);	 Catch:{ all -> 0x0181 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0181 }
        r2 = r0.key;	 Catch:{ all -> 0x0181 }
        r1.append(r2);	 Catch:{ all -> 0x0181 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0181 }
        r1.append(r3);	 Catch:{ all -> 0x0181 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0181 }
        r1.flush();	 Catch:{ all -> 0x0181 }
        r1 = r10.size;	 Catch:{ all -> 0x0181 }
        r3 = r10.maxSize;	 Catch:{ all -> 0x0181 }
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));	 Catch:{ all -> 0x0181 }
        if (r5 > 0) goto L_0x0169;	 Catch:{ all -> 0x0181 }
        r1 = r10.journalRebuildRequired();	 Catch:{ all -> 0x0181 }
        if (r1 == 0) goto L_0x0167;	 Catch:{ all -> 0x0181 }
    L_0x0166:
        goto L_0x0169;	 Catch:{ all -> 0x0181 }
        goto L_0x0175;	 Catch:{ all -> 0x0181 }
        r1 = r10.executorService;	 Catch:{ all -> 0x0181 }
        r2 = r10.cleanupCallable;	 Catch:{ all -> 0x0181 }
        r1.submit(r2);	 Catch:{ all -> 0x0181 }
        monitor-exit(r10);
        return;
        r1 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0181 }
        r1.<init>();	 Catch:{ all -> 0x0181 }
        throw r1;	 Catch:{ all -> 0x0181 }
    L_0x0181:
        r11 = move-exception;
        monitor-exit(r10);
        throw r11;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.DiskLruCache.completeEdit(com.bumptech.glide.disklrucache.DiskLruCache$Editor, boolean):void");
    }

    private void readJournal() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:36:0x00cd in {15, 21, 22, 24, 25, 26, 27, 28, 29, 32, 35} preds:[]
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
        r12 = this;
        r0 = new com.bumptech.glide.disklrucache.StrictLineReader;
        r1 = new java.io.FileInputStream;
        r2 = r12.journalFile;
        r1.<init>(r2);
        r2 = com.bumptech.glide.disklrucache.Util.US_ASCII;
        r0.<init>(r1, r2);
        r1 = r0.readLine();	 Catch:{ all -> 0x00c8 }
        r2 = r0.readLine();	 Catch:{ all -> 0x00c8 }
        r3 = r0.readLine();	 Catch:{ all -> 0x00c8 }
        r4 = r0.readLine();	 Catch:{ all -> 0x00c8 }
        r5 = r0.readLine();	 Catch:{ all -> 0x00c8 }
        r6 = "libcore.io.DiskLruCache";	 Catch:{ all -> 0x00c8 }
        r6 = r6.equals(r1);	 Catch:{ all -> 0x00c8 }
        if (r6 == 0) goto L_0x0092;	 Catch:{ all -> 0x00c8 }
    L_0x002a:
        r6 = "1";	 Catch:{ all -> 0x00c8 }
        r6 = r6.equals(r2);	 Catch:{ all -> 0x00c8 }
        if (r6 == 0) goto L_0x0091;	 Catch:{ all -> 0x00c8 }
    L_0x0032:
        r6 = r12.appVersion;	 Catch:{ all -> 0x00c8 }
        r6 = java.lang.Integer.toString(r6);	 Catch:{ all -> 0x00c8 }
        r6 = r6.equals(r3);	 Catch:{ all -> 0x00c8 }
        if (r6 == 0) goto L_0x0090;	 Catch:{ all -> 0x00c8 }
    L_0x003e:
        r6 = r12.valueCount;	 Catch:{ all -> 0x00c8 }
        r6 = java.lang.Integer.toString(r6);	 Catch:{ all -> 0x00c8 }
        r6 = r6.equals(r4);	 Catch:{ all -> 0x00c8 }
        if (r6 == 0) goto L_0x008f;	 Catch:{ all -> 0x00c8 }
    L_0x004a:
        r6 = "";	 Catch:{ all -> 0x00c8 }
        r6 = r6.equals(r5);	 Catch:{ all -> 0x00c8 }
        if (r6 == 0) goto L_0x008e;
    L_0x0052:
        r6 = 0;
    L_0x0053:
        r7 = r0.readLine();	 Catch:{ EOFException -> 0x005d }
        r12.readJournalLine(r7);	 Catch:{ EOFException -> 0x005d }
        r6 = r6 + 1;
        goto L_0x0053;
    L_0x005d:
        r7 = move-exception;
        r7 = r12.lruEntries;	 Catch:{ all -> 0x00c8 }
        r7 = r7.size();	 Catch:{ all -> 0x00c8 }
        r7 = r6 - r7;	 Catch:{ all -> 0x00c8 }
        r12.redundantOpCount = r7;	 Catch:{ all -> 0x00c8 }
        r7 = r0.hasUnterminatedLine();	 Catch:{ all -> 0x00c8 }
        if (r7 == 0) goto L_0x0073;	 Catch:{ all -> 0x00c8 }
    L_0x006f:
        r12.rebuildJournal();	 Catch:{ all -> 0x00c8 }
        goto L_0x0089;	 Catch:{ all -> 0x00c8 }
    L_0x0073:
        r7 = new java.io.BufferedWriter;	 Catch:{ all -> 0x00c8 }
        r8 = new java.io.OutputStreamWriter;	 Catch:{ all -> 0x00c8 }
        r9 = new java.io.FileOutputStream;	 Catch:{ all -> 0x00c8 }
        r10 = r12.journalFile;	 Catch:{ all -> 0x00c8 }
        r11 = 1;	 Catch:{ all -> 0x00c8 }
        r9.<init>(r10, r11);	 Catch:{ all -> 0x00c8 }
        r10 = com.bumptech.glide.disklrucache.Util.US_ASCII;	 Catch:{ all -> 0x00c8 }
        r8.<init>(r9, r10);	 Catch:{ all -> 0x00c8 }
        r7.<init>(r8);	 Catch:{ all -> 0x00c8 }
        r12.journalWriter = r7;	 Catch:{ all -> 0x00c8 }
    L_0x0089:
        com.bumptech.glide.disklrucache.Util.closeQuietly(r0);
        return;
    L_0x008e:
        goto L_0x0093;
    L_0x008f:
        goto L_0x0093;
    L_0x0090:
        goto L_0x0093;
    L_0x0091:
        goto L_0x0093;
    L_0x0093:
        r6 = new java.io.IOException;	 Catch:{ all -> 0x00c8 }
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00c8 }
        r7.<init>();	 Catch:{ all -> 0x00c8 }
        r8 = "unexpected journal header: [";	 Catch:{ all -> 0x00c8 }
        r7.append(r8);	 Catch:{ all -> 0x00c8 }
        r7.append(r1);	 Catch:{ all -> 0x00c8 }
        r8 = ", ";	 Catch:{ all -> 0x00c8 }
        r7.append(r8);	 Catch:{ all -> 0x00c8 }
        r7.append(r2);	 Catch:{ all -> 0x00c8 }
        r8 = ", ";	 Catch:{ all -> 0x00c8 }
        r7.append(r8);	 Catch:{ all -> 0x00c8 }
        r7.append(r4);	 Catch:{ all -> 0x00c8 }
        r8 = ", ";	 Catch:{ all -> 0x00c8 }
        r7.append(r8);	 Catch:{ all -> 0x00c8 }
        r7.append(r5);	 Catch:{ all -> 0x00c8 }
        r8 = "]";	 Catch:{ all -> 0x00c8 }
        r7.append(r8);	 Catch:{ all -> 0x00c8 }
        r7 = r7.toString();	 Catch:{ all -> 0x00c8 }
        r6.<init>(r7);	 Catch:{ all -> 0x00c8 }
        throw r6;	 Catch:{ all -> 0x00c8 }
    L_0x00c8:
        r1 = move-exception;
        com.bumptech.glide.disklrucache.Util.closeQuietly(r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.DiskLruCache.readJournal():void");
    }

    private synchronized void rebuildJournal() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:31:0x00f3 in {4, 5, 13, 14, 15, 19, 20, 23, 27, 30} preds:[]
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
        r6 = this;
        monitor-enter(r6);
        r0 = r6.journalWriter;	 Catch:{ all -> 0x00f0 }
        if (r0 == 0) goto L_0x000b;	 Catch:{ all -> 0x00f0 }
    L_0x0005:
        r0 = r6.journalWriter;	 Catch:{ all -> 0x00f0 }
        r0.close();	 Catch:{ all -> 0x00f0 }
        goto L_0x000c;	 Catch:{ all -> 0x00f0 }
    L_0x000c:
        r0 = new java.io.BufferedWriter;	 Catch:{ all -> 0x00f0 }
        r1 = new java.io.OutputStreamWriter;	 Catch:{ all -> 0x00f0 }
        r2 = new java.io.FileOutputStream;	 Catch:{ all -> 0x00f0 }
        r3 = r6.journalFileTmp;	 Catch:{ all -> 0x00f0 }
        r2.<init>(r3);	 Catch:{ all -> 0x00f0 }
        r3 = com.bumptech.glide.disklrucache.Util.US_ASCII;	 Catch:{ all -> 0x00f0 }
        r1.<init>(r2, r3);	 Catch:{ all -> 0x00f0 }
        r0.<init>(r1);	 Catch:{ all -> 0x00f0 }
        r1 = "libcore.io.DiskLruCache";	 Catch:{ all -> 0x00eb }
        r0.write(r1);	 Catch:{ all -> 0x00eb }
        r1 = "\n";	 Catch:{ all -> 0x00eb }
        r0.write(r1);	 Catch:{ all -> 0x00eb }
        r1 = "1";	 Catch:{ all -> 0x00eb }
        r0.write(r1);	 Catch:{ all -> 0x00eb }
        r1 = "\n";	 Catch:{ all -> 0x00eb }
        r0.write(r1);	 Catch:{ all -> 0x00eb }
        r1 = r6.appVersion;	 Catch:{ all -> 0x00eb }
        r1 = java.lang.Integer.toString(r1);	 Catch:{ all -> 0x00eb }
        r0.write(r1);	 Catch:{ all -> 0x00eb }
        r1 = "\n";	 Catch:{ all -> 0x00eb }
        r0.write(r1);	 Catch:{ all -> 0x00eb }
        r1 = r6.valueCount;	 Catch:{ all -> 0x00eb }
        r1 = java.lang.Integer.toString(r1);	 Catch:{ all -> 0x00eb }
        r0.write(r1);	 Catch:{ all -> 0x00eb }
        r1 = "\n";	 Catch:{ all -> 0x00eb }
        r0.write(r1);	 Catch:{ all -> 0x00eb }
        r1 = "\n";	 Catch:{ all -> 0x00eb }
        r0.write(r1);	 Catch:{ all -> 0x00eb }
        r1 = r6.lruEntries;	 Catch:{ all -> 0x00eb }
        r1 = r1.values();	 Catch:{ all -> 0x00eb }
        r1 = r1.iterator();	 Catch:{ all -> 0x00eb }
    L_0x005e:
        r2 = r1.hasNext();	 Catch:{ all -> 0x00eb }
        if (r2 == 0) goto L_0x00b1;	 Catch:{ all -> 0x00eb }
    L_0x0064:
        r2 = r1.next();	 Catch:{ all -> 0x00eb }
        r2 = (com.bumptech.glide.disklrucache.DiskLruCache.Entry) r2;	 Catch:{ all -> 0x00eb }
        r3 = r2.currentEditor;	 Catch:{ all -> 0x00eb }
        r4 = 10;	 Catch:{ all -> 0x00eb }
        if (r3 == 0) goto L_0x008e;	 Catch:{ all -> 0x00eb }
    L_0x0072:
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00eb }
        r3.<init>();	 Catch:{ all -> 0x00eb }
        r5 = "DIRTY ";	 Catch:{ all -> 0x00eb }
        r3.append(r5);	 Catch:{ all -> 0x00eb }
        r5 = r2.key;	 Catch:{ all -> 0x00eb }
        r3.append(r5);	 Catch:{ all -> 0x00eb }
        r3.append(r4);	 Catch:{ all -> 0x00eb }
        r3 = r3.toString();	 Catch:{ all -> 0x00eb }
        r0.write(r3);	 Catch:{ all -> 0x00eb }
        goto L_0x00b0;	 Catch:{ all -> 0x00eb }
    L_0x008e:
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00eb }
        r3.<init>();	 Catch:{ all -> 0x00eb }
        r5 = "CLEAN ";	 Catch:{ all -> 0x00eb }
        r3.append(r5);	 Catch:{ all -> 0x00eb }
        r5 = r2.key;	 Catch:{ all -> 0x00eb }
        r3.append(r5);	 Catch:{ all -> 0x00eb }
        r5 = r2.getLengths();	 Catch:{ all -> 0x00eb }
        r3.append(r5);	 Catch:{ all -> 0x00eb }
        r3.append(r4);	 Catch:{ all -> 0x00eb }
        r3 = r3.toString();	 Catch:{ all -> 0x00eb }
        r0.write(r3);	 Catch:{ all -> 0x00eb }
    L_0x00b0:
        goto L_0x005e;
    L_0x00b1:
        r0.close();	 Catch:{ all -> 0x00f0 }
        r1 = r6.journalFile;	 Catch:{ all -> 0x00f0 }
        r1 = r1.exists();	 Catch:{ all -> 0x00f0 }
        r2 = 1;	 Catch:{ all -> 0x00f0 }
        if (r1 == 0) goto L_0x00c6;	 Catch:{ all -> 0x00f0 }
    L_0x00be:
        r1 = r6.journalFile;	 Catch:{ all -> 0x00f0 }
        r3 = r6.journalFileBackup;	 Catch:{ all -> 0x00f0 }
        renameTo(r1, r3, r2);	 Catch:{ all -> 0x00f0 }
        goto L_0x00c7;	 Catch:{ all -> 0x00f0 }
    L_0x00c7:
        r1 = r6.journalFileTmp;	 Catch:{ all -> 0x00f0 }
        r3 = r6.journalFile;	 Catch:{ all -> 0x00f0 }
        r4 = 0;	 Catch:{ all -> 0x00f0 }
        renameTo(r1, r3, r4);	 Catch:{ all -> 0x00f0 }
        r1 = r6.journalFileBackup;	 Catch:{ all -> 0x00f0 }
        r1.delete();	 Catch:{ all -> 0x00f0 }
        r1 = new java.io.BufferedWriter;	 Catch:{ all -> 0x00f0 }
        r3 = new java.io.OutputStreamWriter;	 Catch:{ all -> 0x00f0 }
        r4 = new java.io.FileOutputStream;	 Catch:{ all -> 0x00f0 }
        r5 = r6.journalFile;	 Catch:{ all -> 0x00f0 }
        r4.<init>(r5, r2);	 Catch:{ all -> 0x00f0 }
        r2 = com.bumptech.glide.disklrucache.Util.US_ASCII;	 Catch:{ all -> 0x00f0 }
        r3.<init>(r4, r2);	 Catch:{ all -> 0x00f0 }
        r1.<init>(r3);	 Catch:{ all -> 0x00f0 }
        r6.journalWriter = r1;	 Catch:{ all -> 0x00f0 }
        monitor-exit(r6);
        return;
    L_0x00eb:
        r1 = move-exception;
        r0.close();	 Catch:{ all -> 0x00f0 }
        throw r1;	 Catch:{ all -> 0x00f0 }
    L_0x00f0:
        r0 = move-exception;
        monitor-exit(r6);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.DiskLruCache.rebuildJournal():void");
    }

    public synchronized void close() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:21:0x0042 in {5, 12, 13, 14, 17, 20} preds:[]
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
        r3 = this;
        monitor-enter(r3);
        r0 = r3.journalWriter;	 Catch:{ all -> 0x003f }
        if (r0 != 0) goto L_0x0007;
    L_0x0005:
        monitor-exit(r3);
        return;
    L_0x0007:
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x003f }
        r1 = r3.lruEntries;	 Catch:{ all -> 0x003f }
        r1 = r1.values();	 Catch:{ all -> 0x003f }
        r0.<init>(r1);	 Catch:{ all -> 0x003f }
        r0 = r0.iterator();	 Catch:{ all -> 0x003f }
    L_0x0016:
        r1 = r0.hasNext();	 Catch:{ all -> 0x003f }
        if (r1 == 0) goto L_0x0032;	 Catch:{ all -> 0x003f }
    L_0x001c:
        r1 = r0.next();	 Catch:{ all -> 0x003f }
        r1 = (com.bumptech.glide.disklrucache.DiskLruCache.Entry) r1;	 Catch:{ all -> 0x003f }
        r2 = r1.currentEditor;	 Catch:{ all -> 0x003f }
        if (r2 == 0) goto L_0x0030;	 Catch:{ all -> 0x003f }
    L_0x0028:
        r2 = r1.currentEditor;	 Catch:{ all -> 0x003f }
        r2.abort();	 Catch:{ all -> 0x003f }
        goto L_0x0031;	 Catch:{ all -> 0x003f }
    L_0x0031:
        goto L_0x0016;	 Catch:{ all -> 0x003f }
    L_0x0032:
        r3.trimToSize();	 Catch:{ all -> 0x003f }
        r0 = r3.journalWriter;	 Catch:{ all -> 0x003f }
        r0.close();	 Catch:{ all -> 0x003f }
        r0 = 0;	 Catch:{ all -> 0x003f }
        r3.journalWriter = r0;	 Catch:{ all -> 0x003f }
        monitor-exit(r3);
        return;
    L_0x003f:
        r0 = move-exception;
        monitor-exit(r3);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.DiskLruCache.close():void");
    }

    public synchronized com.bumptech.glide.disklrucache.DiskLruCache.Value get(java.lang.String r11) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:31:0x0074 in {6, 11, 18, 19, 23, 24, 27, 30} preds:[]
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
        r10 = this;
        monitor-enter(r10);
        r10.checkNotClosed();	 Catch:{ all -> 0x0071 }
        r0 = r10.lruEntries;	 Catch:{ all -> 0x0071 }
        r0 = r0.get(r11);	 Catch:{ all -> 0x0071 }
        r0 = (com.bumptech.glide.disklrucache.DiskLruCache.Entry) r0;	 Catch:{ all -> 0x0071 }
        r1 = 0;
        if (r0 != 0) goto L_0x0011;
    L_0x000f:
        monitor-exit(r10);
        return r1;
    L_0x0011:
        r2 = r0.readable;	 Catch:{ all -> 0x0071 }
        if (r2 != 0) goto L_0x0019;
    L_0x0017:
        monitor-exit(r10);
        return r1;
    L_0x0019:
        r2 = r0.cleanFiles;	 Catch:{ all -> 0x0071 }
        r3 = r2.length;	 Catch:{ all -> 0x0071 }
        r4 = 0;	 Catch:{ all -> 0x0071 }
    L_0x001d:
        if (r4 >= r3) goto L_0x002d;	 Catch:{ all -> 0x0071 }
    L_0x001f:
        r5 = r2[r4];	 Catch:{ all -> 0x0071 }
        r6 = r5.exists();	 Catch:{ all -> 0x0071 }
        if (r6 != 0) goto L_0x0029;
    L_0x0027:
        monitor-exit(r10);
        return r1;
        r4 = r4 + 1;
        goto L_0x001d;
    L_0x002d:
        r1 = r10.redundantOpCount;	 Catch:{ all -> 0x0071 }
        r1 = r1 + 1;	 Catch:{ all -> 0x0071 }
        r10.redundantOpCount = r1;	 Catch:{ all -> 0x0071 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0071 }
        r2 = "READ";	 Catch:{ all -> 0x0071 }
        r1.append(r2);	 Catch:{ all -> 0x0071 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0071 }
        r2 = 32;	 Catch:{ all -> 0x0071 }
        r1.append(r2);	 Catch:{ all -> 0x0071 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0071 }
        r1.append(r11);	 Catch:{ all -> 0x0071 }
        r1 = r10.journalWriter;	 Catch:{ all -> 0x0071 }
        r2 = 10;	 Catch:{ all -> 0x0071 }
        r1.append(r2);	 Catch:{ all -> 0x0071 }
        r1 = r10.journalRebuildRequired();	 Catch:{ all -> 0x0071 }
        if (r1 == 0) goto L_0x005b;	 Catch:{ all -> 0x0071 }
    L_0x0053:
        r1 = r10.executorService;	 Catch:{ all -> 0x0071 }
        r2 = r10.cleanupCallable;	 Catch:{ all -> 0x0071 }
        r1.submit(r2);	 Catch:{ all -> 0x0071 }
        goto L_0x005c;	 Catch:{ all -> 0x0071 }
    L_0x005c:
        r9 = new com.bumptech.glide.disklrucache.DiskLruCache$Value;	 Catch:{ all -> 0x0071 }
        r4 = r0.sequenceNumber;	 Catch:{ all -> 0x0071 }
        r6 = r0.cleanFiles;	 Catch:{ all -> 0x0071 }
        r7 = r0.lengths;	 Catch:{ all -> 0x0071 }
        r8 = 0;	 Catch:{ all -> 0x0071 }
        r1 = r9;	 Catch:{ all -> 0x0071 }
        r2 = r10;	 Catch:{ all -> 0x0071 }
        r3 = r11;	 Catch:{ all -> 0x0071 }
        r1.<init>(r3, r4, r6, r7);	 Catch:{ all -> 0x0071 }
        monitor-exit(r10);
        return r9;
    L_0x0071:
        r11 = move-exception;
        monitor-exit(r10);
        throw r11;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.DiskLruCache.get(java.lang.String):com.bumptech.glide.disklrucache.DiskLruCache$Value");
    }

    public synchronized boolean remove(java.lang.String r9) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:31:0x0097 in {6, 14, 16, 17, 21, 22, 24, 27, 30} preds:[]
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
        monitor-enter(r8);
        r8.checkNotClosed();	 Catch:{ all -> 0x0094 }
        r0 = r8.lruEntries;	 Catch:{ all -> 0x0094 }
        r0 = r0.get(r9);	 Catch:{ all -> 0x0094 }
        r0 = (com.bumptech.glide.disklrucache.DiskLruCache.Entry) r0;	 Catch:{ all -> 0x0094 }
        r1 = 0;	 Catch:{ all -> 0x0094 }
        if (r0 == 0) goto L_0x0091;	 Catch:{ all -> 0x0094 }
    L_0x000f:
        r2 = r0.currentEditor;	 Catch:{ all -> 0x0094 }
        if (r2 == 0) goto L_0x0017;	 Catch:{ all -> 0x0094 }
    L_0x0015:
        goto L_0x0091;	 Catch:{ all -> 0x0094 }
    L_0x0018:
        r2 = r8.valueCount;	 Catch:{ all -> 0x0094 }
        if (r1 >= r2) goto L_0x005b;	 Catch:{ all -> 0x0094 }
    L_0x001c:
        r2 = r0.getCleanFile(r1);	 Catch:{ all -> 0x0094 }
        r3 = r2.exists();	 Catch:{ all -> 0x0094 }
        if (r3 == 0) goto L_0x0044;	 Catch:{ all -> 0x0094 }
    L_0x0026:
        r3 = r2.delete();	 Catch:{ all -> 0x0094 }
        if (r3 == 0) goto L_0x002d;	 Catch:{ all -> 0x0094 }
    L_0x002c:
        goto L_0x0044;	 Catch:{ all -> 0x0094 }
    L_0x002d:
        r3 = new java.io.IOException;	 Catch:{ all -> 0x0094 }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0094 }
        r4.<init>();	 Catch:{ all -> 0x0094 }
        r5 = "failed to delete ";	 Catch:{ all -> 0x0094 }
        r4.append(r5);	 Catch:{ all -> 0x0094 }
        r4.append(r2);	 Catch:{ all -> 0x0094 }
        r4 = r4.toString();	 Catch:{ all -> 0x0094 }
        r3.<init>(r4);	 Catch:{ all -> 0x0094 }
        throw r3;	 Catch:{ all -> 0x0094 }
        r3 = r8.size;	 Catch:{ all -> 0x0094 }
        r5 = r0.lengths;	 Catch:{ all -> 0x0094 }
        r6 = r5[r1];	 Catch:{ all -> 0x0094 }
        r3 = r3 - r6;	 Catch:{ all -> 0x0094 }
        r8.size = r3;	 Catch:{ all -> 0x0094 }
        r3 = r0.lengths;	 Catch:{ all -> 0x0094 }
        r4 = 0;	 Catch:{ all -> 0x0094 }
        r3[r1] = r4;	 Catch:{ all -> 0x0094 }
        r1 = r1 + 1;	 Catch:{ all -> 0x0094 }
        goto L_0x0018;	 Catch:{ all -> 0x0094 }
    L_0x005b:
        r1 = r8.redundantOpCount;	 Catch:{ all -> 0x0094 }
        r2 = 1;	 Catch:{ all -> 0x0094 }
        r1 = r1 + r2;	 Catch:{ all -> 0x0094 }
        r8.redundantOpCount = r1;	 Catch:{ all -> 0x0094 }
        r1 = r8.journalWriter;	 Catch:{ all -> 0x0094 }
        r3 = "REMOVE";	 Catch:{ all -> 0x0094 }
        r1.append(r3);	 Catch:{ all -> 0x0094 }
        r1 = r8.journalWriter;	 Catch:{ all -> 0x0094 }
        r3 = 32;	 Catch:{ all -> 0x0094 }
        r1.append(r3);	 Catch:{ all -> 0x0094 }
        r1 = r8.journalWriter;	 Catch:{ all -> 0x0094 }
        r1.append(r9);	 Catch:{ all -> 0x0094 }
        r1 = r8.journalWriter;	 Catch:{ all -> 0x0094 }
        r3 = 10;	 Catch:{ all -> 0x0094 }
        r1.append(r3);	 Catch:{ all -> 0x0094 }
        r1 = r8.lruEntries;	 Catch:{ all -> 0x0094 }
        r1.remove(r9);	 Catch:{ all -> 0x0094 }
        r1 = r8.journalRebuildRequired();	 Catch:{ all -> 0x0094 }
        if (r1 == 0) goto L_0x008e;	 Catch:{ all -> 0x0094 }
    L_0x0086:
        r1 = r8.executorService;	 Catch:{ all -> 0x0094 }
        r3 = r8.cleanupCallable;	 Catch:{ all -> 0x0094 }
        r1.submit(r3);	 Catch:{ all -> 0x0094 }
        goto L_0x008f;
    L_0x008f:
        monitor-exit(r8);
        return r2;
        monitor-exit(r8);
        return r1;
    L_0x0094:
        r9 = move-exception;
        monitor-exit(r8);
        throw r9;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.DiskLruCache.remove(java.lang.String):boolean");
    }

    private DiskLruCache(File directory, int appVersion, int valueCount, long maxSize) {
        File file = directory;
        this.directory = file;
        this.appVersion = appVersion;
        this.journalFile = new File(file, JOURNAL_FILE);
        this.journalFileTmp = new File(file, JOURNAL_FILE_TEMP);
        this.journalFileBackup = new File(file, JOURNAL_FILE_BACKUP);
        this.valueCount = valueCount;
        this.maxSize = maxSize;
    }

    public static DiskLruCache open(File directory, int appVersion, int valueCount, long maxSize) throws IOException {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        } else if (valueCount > 0) {
            File backupFile = new File(directory, JOURNAL_FILE_BACKUP);
            if (backupFile.exists()) {
                File journalFile = new File(directory, JOURNAL_FILE);
                if (journalFile.exists()) {
                    backupFile.delete();
                } else {
                    renameTo(backupFile, journalFile, false);
                }
            }
            DiskLruCache diskLruCache = new DiskLruCache(directory, appVersion, valueCount, maxSize);
            if (diskLruCache.journalFile.exists()) {
                try {
                    diskLruCache.readJournal();
                    diskLruCache.processJournal();
                    return diskLruCache;
                } catch (IOException journalIsCorrupt) {
                    PrintStream printStream = System.out;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("DiskLruCache ");
                    stringBuilder.append(directory);
                    stringBuilder.append(" is corrupt: ");
                    stringBuilder.append(journalIsCorrupt.getMessage());
                    stringBuilder.append(", removing");
                    printStream.println(stringBuilder.toString());
                    diskLruCache.delete();
                }
            } else {
                directory.mkdirs();
                DiskLruCache cache = new DiskLruCache(directory, appVersion, valueCount, maxSize);
                cache.rebuildJournal();
                return cache;
            }
        } else {
            throw new IllegalArgumentException("valueCount <= 0");
        }
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
            Entry entry = (Entry) this.lruEntries.get(key);
            if (entry == null) {
                entry = new Entry(key);
                this.lruEntries.put(key, entry);
            }
            if (secondSpace != -1 && firstSpace == CLEAN.length() && line.startsWith(CLEAN)) {
                String[] parts = line.substring(secondSpace + 1).split(StringUtils.SPACE);
                entry.readable = true;
                entry.currentEditor = null;
                entry.setLengths(parts);
            } else if (secondSpace == -1 && firstSpace == DIRTY.length() && line.startsWith(DIRTY)) {
                entry.currentEditor = new Editor(entry);
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
        deleteIfExists(this.journalFileTmp);
        Iterator<Entry> i = this.lruEntries.values().iterator();
        while (i.hasNext()) {
            Entry entry = (Entry) i.next();
            int t;
            if (entry.currentEditor == null) {
                for (t = 0; t < this.valueCount; t++) {
                    this.size += entry.lengths[t];
                }
            } else {
                entry.currentEditor = null;
                for (t = 0; t < this.valueCount; t++) {
                    deleteIfExists(entry.getCleanFile(t));
                    deleteIfExists(entry.getDirtyFile(t));
                }
                i.remove();
            }
        }
    }

    private static void deleteIfExists(File file) throws IOException {
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException();
            }
        }
    }

    private static void renameTo(File from, File to, boolean deleteDestination) throws IOException {
        if (deleteDestination) {
            deleteIfExists(to);
        }
        if (!from.renameTo(to)) {
            throw new IOException();
        }
    }

    public Editor edit(String key) throws IOException {
        return edit(key, -1);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized com.bumptech.glide.disklrucache.DiskLruCache.Editor edit(java.lang.String r6, long r7) throws java.io.IOException {
        /*
        r5 = this;
        monitor-enter(r5);
        r5.checkNotClosed();	 Catch:{ all -> 0x0063 }
        r0 = r5.lruEntries;	 Catch:{ all -> 0x0063 }
        r0 = r0.get(r6);	 Catch:{ all -> 0x0063 }
        r0 = (com.bumptech.glide.disklrucache.DiskLruCache.Entry) r0;	 Catch:{ all -> 0x0063 }
        r1 = -1;
        r3 = 0;
        r4 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1));
        if (r4 == 0) goto L_0x0022;
    L_0x0013:
        if (r0 == 0) goto L_0x001f;
    L_0x0015:
        r1 = r0.sequenceNumber;	 Catch:{ all -> 0x0063 }
        r4 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1));
        if (r4 == 0) goto L_0x001e;
    L_0x001d:
        goto L_0x0020;
    L_0x001e:
        goto L_0x0023;
    L_0x0020:
        monitor-exit(r5);
        return r3;
    L_0x0023:
        if (r0 != 0) goto L_0x0031;
    L_0x0025:
        r1 = new com.bumptech.glide.disklrucache.DiskLruCache$Entry;	 Catch:{ all -> 0x0063 }
        r1.<init>(r6);	 Catch:{ all -> 0x0063 }
        r0 = r1;
        r1 = r5.lruEntries;	 Catch:{ all -> 0x0063 }
        r1.put(r6, r0);	 Catch:{ all -> 0x0063 }
        goto L_0x003a;
    L_0x0031:
        r1 = r0.currentEditor;	 Catch:{ all -> 0x0063 }
        if (r1 == 0) goto L_0x0039;
    L_0x0037:
        monitor-exit(r5);
        return r3;
    L_0x003a:
        r1 = new com.bumptech.glide.disklrucache.DiskLruCache$Editor;	 Catch:{ all -> 0x0063 }
        r1.<init>(r0);	 Catch:{ all -> 0x0063 }
        r0.currentEditor = r1;	 Catch:{ all -> 0x0063 }
        r2 = r5.journalWriter;	 Catch:{ all -> 0x0063 }
        r3 = "DIRTY";
        r2.append(r3);	 Catch:{ all -> 0x0063 }
        r2 = r5.journalWriter;	 Catch:{ all -> 0x0063 }
        r3 = 32;
        r2.append(r3);	 Catch:{ all -> 0x0063 }
        r2 = r5.journalWriter;	 Catch:{ all -> 0x0063 }
        r2.append(r6);	 Catch:{ all -> 0x0063 }
        r2 = r5.journalWriter;	 Catch:{ all -> 0x0063 }
        r3 = 10;
        r2.append(r3);	 Catch:{ all -> 0x0063 }
        r2 = r5.journalWriter;	 Catch:{ all -> 0x0063 }
        r2.flush();	 Catch:{ all -> 0x0063 }
        monitor-exit(r5);
        return r1;
    L_0x0063:
        r6 = move-exception;
        monitor-exit(r5);
        throw r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.DiskLruCache.edit(java.lang.String, long):com.bumptech.glide.disklrucache.DiskLruCache$Editor");
    }

    public File getDirectory() {
        return this.directory;
    }

    public synchronized long getMaxSize() {
        return this.maxSize;
    }

    public synchronized void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
        this.executorService.submit(this.cleanupCallable);
    }

    public synchronized long size() {
        return this.size;
    }

    private boolean journalRebuildRequired() {
        int i = this.redundantOpCount;
        if (i >= 2000) {
            if (i >= this.lruEntries.size()) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isClosed() {
        return this.journalWriter == null;
    }

    private void checkNotClosed() {
        if (this.journalWriter == null) {
            throw new IllegalStateException("cache is closed");
        }
    }

    public synchronized void flush() throws IOException {
        checkNotClosed();
        trimToSize();
        this.journalWriter.flush();
    }

    private void trimToSize() throws IOException {
        while (this.size > this.maxSize) {
            remove((String) ((java.util.Map.Entry) this.lruEntries.entrySet().iterator().next()).getKey());
        }
    }

    public void delete() throws IOException {
        close();
        Util.deleteContents(this.directory);
    }

    private static String inputStreamToString(InputStream in) throws IOException {
        return Util.readFully(new InputStreamReader(in, Util.UTF_8));
    }
}

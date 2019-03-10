package org.apache.commons.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.output.NullOutputStream;

public class FileUtils {
    public static final File[] EMPTY_FILE_ARRAY = new File[0];
    private static final long FILE_COPY_BUFFER_SIZE = 31457280;
    public static final long ONE_EB = 1152921504606846976L;
    public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);
    public static final long ONE_GB = 1073741824;
    public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);
    public static final long ONE_KB = 1024;
    public static final BigInteger ONE_KB_BI = BigInteger.valueOf(1024);
    public static final long ONE_MB = 1048576;
    public static final BigInteger ONE_MB_BI;
    public static final long ONE_PB = 1125899906842624L;
    public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);
    public static final long ONE_TB = 1099511627776L;
    public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);
    public static final BigInteger ONE_YB = ONE_KB_BI.multiply(ONE_ZB);
    public static final BigInteger ONE_ZB = BigInteger.valueOf(1024).multiply(BigInteger.valueOf(ONE_EB));

    public static void cleanDirectory(java.io.File r7) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x0019 in {5, 7, 8, 10, 11} preds:[]
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
        r0 = verifiedListFiles(r7);
        r1 = 0;
        r2 = r0;
        r3 = r2.length;
        r4 = 0;
    L_0x0008:
        if (r4 >= r3) goto L_0x0015;
    L_0x000a:
        r5 = r2[r4];
        forceDelete(r5);	 Catch:{ IOException -> 0x0010 }
        goto L_0x0012;
    L_0x0010:
        r6 = move-exception;
        r1 = r6;
    L_0x0012:
        r4 = r4 + 1;
        goto L_0x0008;
    L_0x0015:
        if (r1 != 0) goto L_0x0018;
    L_0x0017:
        return;
    L_0x0018:
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.FileUtils.cleanDirectory(java.io.File):void");
    }

    private static void cleanDirectoryOnExit(java.io.File r7) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x0019 in {5, 7, 8, 10, 11} preds:[]
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
        r0 = verifiedListFiles(r7);
        r1 = 0;
        r2 = r0;
        r3 = r2.length;
        r4 = 0;
    L_0x0008:
        if (r4 >= r3) goto L_0x0015;
    L_0x000a:
        r5 = r2[r4];
        forceDeleteOnExit(r5);	 Catch:{ IOException -> 0x0010 }
        goto L_0x0012;
    L_0x0010:
        r6 = move-exception;
        r1 = r6;
    L_0x0012:
        r4 = r4 + 1;
        goto L_0x0008;
    L_0x0015:
        if (r1 != 0) goto L_0x0018;
    L_0x0017:
        return;
    L_0x0018:
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.FileUtils.cleanDirectoryOnExit(java.io.File):void");
    }

    public static void copyDirectory(java.io.File r8, java.io.File r9, java.io.FileFilter r10, boolean r11) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:24:0x009e in {7, 8, 14, 15, 16, 17, 19, 21, 23} preds:[]
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
        checkFileRequirements(r8, r9);
        r0 = r8.isDirectory();
        if (r0 == 0) goto L_0x0082;
    L_0x0009:
        r0 = r8.getCanonicalPath();
        r1 = r9.getCanonicalPath();
        r0 = r0.equals(r1);
        if (r0 != 0) goto L_0x005e;
    L_0x0017:
        r0 = 0;
        r1 = r9.getCanonicalPath();
        r2 = r8.getCanonicalPath();
        r1 = r1.startsWith(r2);
        if (r1 == 0) goto L_0x0059;
    L_0x0026:
        if (r10 != 0) goto L_0x002d;
    L_0x0028:
        r1 = r8.listFiles();
        goto L_0x0031;
    L_0x002d:
        r1 = r8.listFiles(r10);
    L_0x0031:
        if (r1 == 0) goto L_0x0058;
    L_0x0033:
        r2 = r1.length;
        if (r2 <= 0) goto L_0x0058;
    L_0x0036:
        r2 = new java.util.ArrayList;
        r3 = r1.length;
        r2.<init>(r3);
        r0 = r2;
        r2 = r1;
        r3 = r2.length;
        r4 = 0;
    L_0x0040:
        if (r4 >= r3) goto L_0x0057;
    L_0x0042:
        r5 = r2[r4];
        r6 = new java.io.File;
        r7 = r5.getName();
        r6.<init>(r9, r7);
        r7 = r6.getCanonicalPath();
        r0.add(r7);
        r4 = r4 + 1;
        goto L_0x0040;
    L_0x0057:
        goto L_0x005a;
    L_0x0058:
        goto L_0x005a;
    L_0x005a:
        doCopyDirectory(r8, r9, r10, r11, r0);
        return;
    L_0x005e:
        r0 = new java.io.IOException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Source '";
        r1.append(r2);
        r1.append(r8);
        r2 = "' and destination '";
        r1.append(r2);
        r1.append(r9);
        r2 = "' are the same";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0082:
        r0 = new java.io.IOException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Source '";
        r1.append(r2);
        r1.append(r8);
        r2 = "' exists but is not a directory";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.FileUtils.copyDirectory(java.io.File, java.io.File, java.io.FileFilter, boolean):void");
    }

    private static void doCopyDirectory(java.io.File r7, java.io.File r8, java.io.FileFilter r9, boolean r10, java.util.List<java.lang.String> r11) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:42:0x00d3 in {1, 2, 8, 10, 15, 17, 18, 27, 28, 31, 32, 33, 35, 36, 37, 39, 41} preds:[]
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
        if (r9 != 0) goto L_0x0007;
    L_0x0002:
        r0 = r7.listFiles();
        goto L_0x000b;
    L_0x0007:
        r0 = r7.listFiles(r9);
    L_0x000b:
        if (r0 == 0) goto L_0x00bc;
    L_0x000d:
        r1 = r8.exists();
        if (r1 == 0) goto L_0x0036;
    L_0x0013:
        r1 = r8.isDirectory();
        if (r1 == 0) goto L_0x001a;
    L_0x0019:
        goto L_0x0060;
    L_0x001a:
        r1 = new java.io.IOException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Destination '";
        r2.append(r3);
        r2.append(r8);
        r3 = "' exists but is not a directory";
        r2.append(r3);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
    L_0x0036:
        r1 = r8.mkdirs();
        if (r1 != 0) goto L_0x005f;
    L_0x003c:
        r1 = r8.isDirectory();
        if (r1 == 0) goto L_0x0043;
    L_0x0042:
        goto L_0x005f;
    L_0x0043:
        r1 = new java.io.IOException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Destination '";
        r2.append(r3);
        r2.append(r8);
        r3 = "' directory cannot be created";
        r2.append(r3);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
    L_0x0060:
        r1 = r8.canWrite();
        if (r1 == 0) goto L_0x00a0;
    L_0x0066:
        r1 = r0;
        r2 = r1.length;
        r3 = 0;
    L_0x0069:
        if (r3 >= r2) goto L_0x0094;
    L_0x006b:
        r4 = r1[r3];
        r5 = new java.io.File;
        r6 = r4.getName();
        r5.<init>(r8, r6);
        if (r11 == 0) goto L_0x0084;
    L_0x0078:
        r6 = r4.getCanonicalPath();
        r6 = r11.contains(r6);
        if (r6 != 0) goto L_0x0083;
    L_0x0082:
        goto L_0x0084;
    L_0x0083:
        goto L_0x0091;
    L_0x0084:
        r6 = r4.isDirectory();
        if (r6 == 0) goto L_0x008e;
    L_0x008a:
        doCopyDirectory(r4, r5, r9, r10, r11);
        goto L_0x0091;
    L_0x008e:
        doCopyFile(r4, r5, r10);
    L_0x0091:
        r3 = r3 + 1;
        goto L_0x0069;
    L_0x0094:
        if (r10 == 0) goto L_0x009e;
    L_0x0096:
        r1 = r7.lastModified();
        r8.setLastModified(r1);
        goto L_0x009f;
    L_0x009f:
        return;
    L_0x00a0:
        r1 = new java.io.IOException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Destination '";
        r2.append(r3);
        r2.append(r8);
        r3 = "' cannot be written to";
        r2.append(r3);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
    L_0x00bc:
        r1 = new java.io.IOException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Failed to list contents of ";
        r2.append(r3);
        r2.append(r7);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.FileUtils.doCopyDirectory(java.io.File, java.io.File, java.io.FileFilter, boolean, java.util.List):void");
    }

    private static void doCopyFile(java.io.File r24, java.io.File r25, boolean r26) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:33:0x00e4 in {4, 6, 14, 15, 19, 20, 21, 25, 26, 27, 29, 32} preds:[]
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
        r1 = r24;
        r2 = r25;
        r0 = r25.exists();
        if (r0 == 0) goto L_0x002d;
    L_0x000a:
        r0 = r25.isDirectory();
        if (r0 != 0) goto L_0x0011;
    L_0x0010:
        goto L_0x002d;
    L_0x0011:
        r0 = new java.io.IOException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Destination '";
        r3.append(r4);
        r3.append(r2);
        r4 = "' exists but is a directory";
        r3.append(r4);
        r3 = r3.toString();
        r0.<init>(r3);
        throw r0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = 3;
        r8 = 2;
        r9 = 1;
        r10 = 0;
        r11 = 4;
        r0 = new java.io.FileInputStream;	 Catch:{ all -> 0x00d5 }
        r0.<init>(r1);	 Catch:{ all -> 0x00d5 }
        r3 = r0;	 Catch:{ all -> 0x00d5 }
        r0 = new java.io.FileOutputStream;	 Catch:{ all -> 0x00d5 }
        r0.<init>(r2);	 Catch:{ all -> 0x00d5 }
        r4 = r0;	 Catch:{ all -> 0x00d5 }
        r0 = r3.getChannel();	 Catch:{ all -> 0x00d5 }
        r5 = r0;	 Catch:{ all -> 0x00d5 }
        r0 = r4.getChannel();	 Catch:{ all -> 0x00d5 }
        r6 = r0;	 Catch:{ all -> 0x00d5 }
        r12 = r5.size();	 Catch:{ all -> 0x00d5 }
        r18 = r12;	 Catch:{ all -> 0x00d5 }
        r12 = 0;	 Catch:{ all -> 0x00d5 }
        r14 = 0;	 Catch:{ all -> 0x00d5 }
        r20 = r12;	 Catch:{ all -> 0x00d5 }
    L_0x0059:
        r0 = (r20 > r18 ? 1 : (r20 == r18 ? 0 : -1));	 Catch:{ all -> 0x00d5 }
        if (r0 >= 0) goto L_0x007f;	 Catch:{ all -> 0x00d5 }
    L_0x005d:
        r22 = r18 - r20;	 Catch:{ all -> 0x00d5 }
        r12 = 31457280; // 0x1e00000 float:8.2284605E-38 double:1.55419614E-316;	 Catch:{ all -> 0x00d5 }
        r0 = (r22 > r12 ? 1 : (r22 == r12 ? 0 : -1));	 Catch:{ all -> 0x00d5 }
        if (r0 <= 0) goto L_0x0069;	 Catch:{ all -> 0x00d5 }
    L_0x0066:
        r16 = r12;	 Catch:{ all -> 0x00d5 }
        goto L_0x006b;	 Catch:{ all -> 0x00d5 }
    L_0x0069:
        r16 = r22;	 Catch:{ all -> 0x00d5 }
    L_0x006b:
        r12 = r6;	 Catch:{ all -> 0x00d5 }
        r13 = r5;	 Catch:{ all -> 0x00d5 }
        r14 = r20;	 Catch:{ all -> 0x00d5 }
        r12 = r12.transferFrom(r13, r14, r16);	 Catch:{ all -> 0x00d5 }
        r14 = 0;
        r0 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r0 != 0) goto L_0x007a;
    L_0x0079:
        goto L_0x0080;
    L_0x007a:
        r20 = r20 + r12;
        r14 = r16;
        goto L_0x0059;
    L_0x0080:
        r0 = new java.io.Closeable[r11];
        r0[r10] = r6;
        r0[r9] = r4;
        r0[r8] = r5;
        r0[r7] = r3;
        org.apache.commons.io.IOUtils.closeQuietly(r0);
        r7 = r24.length();
        r9 = r25.length();
        r0 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
        if (r0 != 0) goto L_0x00a6;
    L_0x009a:
        if (r26 == 0) goto L_0x00a4;
    L_0x009c:
        r11 = r24.lastModified();
        r2.setLastModified(r11);
        goto L_0x00a5;
    L_0x00a5:
        return;
    L_0x00a6:
        r0 = new java.io.IOException;
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r12 = "Failed to copy full contents from '";
        r11.append(r12);
        r11.append(r1);
        r12 = "' to '";
        r11.append(r12);
        r11.append(r2);
        r12 = "' Expected length: ";
        r11.append(r12);
        r11.append(r7);
        r12 = " Actual: ";
        r11.append(r12);
        r11.append(r9);
        r11 = r11.toString();
        r0.<init>(r11);
        throw r0;
    L_0x00d5:
        r0 = move-exception;
        r11 = new java.io.Closeable[r11];
        r11[r10] = r6;
        r11[r9] = r4;
        r11[r8] = r5;
        r11[r7] = r3;
        org.apache.commons.io.IOUtils.closeQuietly(r11);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.FileUtils.doCopyFile(java.io.File, java.io.File, boolean):void");
    }

    public static java.io.File getFile(java.io.File r6, java.lang.String... r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0026 in {4, 5, 7, 9} preds:[]
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
        if (r6 == 0) goto L_0x001e;
    L_0x0002:
        if (r7 == 0) goto L_0x0016;
    L_0x0004:
        r0 = r6;
        r1 = r7;
        r2 = r1.length;
        r3 = 0;
    L_0x0008:
        if (r3 >= r2) goto L_0x0015;
    L_0x000a:
        r4 = r1[r3];
        r5 = new java.io.File;
        r5.<init>(r0, r4);
        r0 = r5;
        r3 = r3 + 1;
        goto L_0x0008;
    L_0x0015:
        return r0;
    L_0x0016:
        r0 = new java.lang.NullPointerException;
        r1 = "names must not be null";
        r0.<init>(r1);
        throw r0;
    L_0x001e:
        r0 = new java.lang.NullPointerException;
        r1 = "directory must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.FileUtils.getFile(java.io.File, java.lang.String[]):java.io.File");
    }

    public static java.io.File getFile(java.lang.String... r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0025 in {5, 6, 7, 8, 10} preds:[]
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
        if (r6 == 0) goto L_0x001d;
    L_0x0002:
        r0 = 0;
        r1 = r6;
        r2 = r1.length;
        r3 = 0;
    L_0x0006:
        if (r3 >= r2) goto L_0x001c;
    L_0x0008:
        r4 = r1[r3];
        if (r0 != 0) goto L_0x0013;
    L_0x000c:
        r5 = new java.io.File;
        r5.<init>(r4);
        r0 = r5;
        goto L_0x0019;
    L_0x0013:
        r5 = new java.io.File;
        r5.<init>(r0, r4);
        r0 = r5;
    L_0x0019:
        r3 = r3 + 1;
        goto L_0x0006;
    L_0x001c:
        return r0;
    L_0x001d:
        r0 = new java.lang.NullPointerException;
        r1 = "names must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.FileUtils.getFile(java.lang.String[]):java.io.File");
    }

    public static boolean waitFor(java.io.File r8, int r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:32:0x0055 in {9, 10, 11, 15, 17, 19, 20, 21, 23, 24, 26, 29, 30, 31} preds:[]
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
        r0 = java.lang.System.currentTimeMillis();
        r2 = (long) r9;
        r4 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r2 = r2 * r4;
        r0 = r0 + r2;
        r2 = 0;
    L_0x000b:
        r3 = r8.exists();	 Catch:{ all -> 0x0048 }
        if (r3 != 0) goto L_0x003a;	 Catch:{ all -> 0x0048 }
    L_0x0011:
        r3 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0048 }
        r3 = r0 - r3;
        r5 = 0;
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 >= 0) goto L_0x002a;
    L_0x001d:
        r5 = 0;
        if (r2 == 0) goto L_0x0028;
    L_0x0020:
        r6 = java.lang.Thread.currentThread();
        r6.interrupt();
        goto L_0x0029;
    L_0x0029:
        return r5;
    L_0x002a:
        r5 = 100;
        r5 = java.lang.Math.min(r5, r3);	 Catch:{ InterruptedException -> 0x0036, Exception -> 0x0034 }
        java.lang.Thread.sleep(r5);	 Catch:{ InterruptedException -> 0x0036, Exception -> 0x0034 }
        goto L_0x0038;
    L_0x0034:
        r5 = move-exception;
        goto L_0x003b;
    L_0x0036:
        r5 = move-exception;
        r2 = 1;
        goto L_0x000b;
    L_0x003b:
        if (r2 == 0) goto L_0x0045;
    L_0x003d:
        r3 = java.lang.Thread.currentThread();
        r3.interrupt();
        goto L_0x0046;
    L_0x0046:
        r3 = 1;
        return r3;
    L_0x0048:
        r3 = move-exception;
        if (r2 == 0) goto L_0x0053;
    L_0x004b:
        r4 = java.lang.Thread.currentThread();
        r4.interrupt();
        goto L_0x0054;
    L_0x0054:
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.FileUtils.waitFor(java.io.File, int):boolean");
    }

    static {
        BigInteger bigInteger = ONE_KB_BI;
        ONE_MB_BI = bigInteger.multiply(bigInteger);
    }

    public static String getTempDirectoryPath() {
        return System.getProperty("java.io.tmpdir");
    }

    public static File getTempDirectory() {
        return new File(getTempDirectoryPath());
    }

    public static String getUserDirectoryPath() {
        return System.getProperty("user.home");
    }

    public static File getUserDirectory() {
        return new File(getUserDirectoryPath());
    }

    public static FileInputStream openInputStream(File file) throws IOException {
        StringBuilder stringBuilder;
        if (!file.exists()) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("File '");
            stringBuilder.append(file);
            stringBuilder.append("' does not exist");
            throw new FileNotFoundException(stringBuilder.toString());
        } else if (file.isDirectory()) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("File '");
            stringBuilder.append(file);
            stringBuilder.append("' exists but is a directory");
            throw new IOException(stringBuilder.toString());
        } else if (file.canRead()) {
            return new FileInputStream(file);
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("File '");
            stringBuilder.append(file);
            stringBuilder.append("' cannot be read");
            throw new IOException(stringBuilder.toString());
        }
    }

    public static FileOutputStream openOutputStream(File file) throws IOException {
        return openOutputStream(file, false);
    }

    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs()) {
                    if (!parent.isDirectory()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Directory '");
                        stringBuilder.append(parent);
                        stringBuilder.append("' could not be created");
                        throw new IOException(stringBuilder.toString());
                    }
                }
            }
        } else if (file.isDirectory()) {
            r1 = new StringBuilder();
            r1.append("File '");
            r1.append(file);
            r1.append("' exists but is a directory");
            throw new IOException(r1.toString());
        } else if (!file.canWrite()) {
            r1 = new StringBuilder();
            r1.append("File '");
            r1.append(file);
            r1.append("' cannot be written to");
            throw new IOException(r1.toString());
        }
        return new FileOutputStream(file, append);
    }

    public static String byteCountToDisplaySize(BigInteger size) {
        StringBuilder stringBuilder;
        if (size.divide(ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(String.valueOf(size.divide(ONE_EB_BI)));
            stringBuilder.append(" EB");
            return stringBuilder.toString();
        } else if (size.divide(ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(String.valueOf(size.divide(ONE_PB_BI)));
            stringBuilder.append(" PB");
            return stringBuilder.toString();
        } else if (size.divide(ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(String.valueOf(size.divide(ONE_TB_BI)));
            stringBuilder.append(" TB");
            return stringBuilder.toString();
        } else if (size.divide(ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(String.valueOf(size.divide(ONE_GB_BI)));
            stringBuilder.append(" GB");
            return stringBuilder.toString();
        } else if (size.divide(ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(String.valueOf(size.divide(ONE_MB_BI)));
            stringBuilder.append(" MB");
            return stringBuilder.toString();
        } else if (size.divide(ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(String.valueOf(size.divide(ONE_KB_BI)));
            stringBuilder.append(" KB");
            return stringBuilder.toString();
        } else {
            String displaySize = new StringBuilder();
            displaySize.append(String.valueOf(size));
            displaySize.append(" bytes");
            return displaySize.toString();
        }
    }

    public static String byteCountToDisplaySize(long size) {
        return byteCountToDisplaySize(BigInteger.valueOf(size));
    }

    public static void touch(File file) throws IOException {
        if (!file.exists()) {
            IOUtils.closeQuietly(openOutputStream(file));
        }
        if (!file.setLastModified(System.currentTimeMillis())) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unable to set the last modification time for ");
            stringBuilder.append(file);
            throw new IOException(stringBuilder.toString());
        }
    }

    public static File[] convertFileCollectionToFileArray(Collection<File> files) {
        return (File[]) files.toArray(new File[files.size()]);
    }

    private static void innerListFiles(Collection<File> files, File directory, IOFileFilter filter, boolean includeSubDirectories) {
        File[] found = directory.listFiles(filter);
        if (found != null) {
            for (File file : found) {
                if (file.isDirectory()) {
                    if (includeSubDirectories) {
                        files.add(file);
                    }
                    innerListFiles(files, file, filter, includeSubDirectories);
                } else {
                    files.add(file);
                }
            }
        }
    }

    public static Collection<File> listFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        validateListFilesParameters(directory, fileFilter);
        IOFileFilter effFileFilter = setUpEffectiveFileFilter(fileFilter);
        IOFileFilter effDirFilter = setUpEffectiveDirFilter(dirFilter);
        Collection<File> files = new LinkedList();
        innerListFiles(files, directory, FileFilterUtils.or(effFileFilter, effDirFilter), false);
        return files;
    }

    private static void validateListFilesParameters(File directory, IOFileFilter fileFilter) {
        if (!directory.isDirectory()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Parameter 'directory' is not a directory: ");
            stringBuilder.append(directory);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (fileFilter == null) {
            throw new NullPointerException("Parameter 'fileFilter' is null");
        }
    }

    private static IOFileFilter setUpEffectiveFileFilter(IOFileFilter fileFilter) {
        return FileFilterUtils.and(fileFilter, FileFilterUtils.notFileFilter(DirectoryFileFilter.INSTANCE));
    }

    private static IOFileFilter setUpEffectiveDirFilter(IOFileFilter dirFilter) {
        if (dirFilter == null) {
            return FalseFileFilter.INSTANCE;
        }
        return FileFilterUtils.and(dirFilter, DirectoryFileFilter.INSTANCE);
    }

    public static Collection<File> listFilesAndDirs(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        validateListFilesParameters(directory, fileFilter);
        IOFileFilter effFileFilter = setUpEffectiveFileFilter(fileFilter);
        IOFileFilter effDirFilter = setUpEffectiveDirFilter(dirFilter);
        Collection<File> files = new LinkedList();
        if (directory.isDirectory()) {
            files.add(directory);
        }
        innerListFiles(files, directory, FileFilterUtils.or(effFileFilter, effDirFilter), true);
        return files;
    }

    public static Iterator<File> iterateFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        return listFiles(directory, fileFilter, dirFilter).iterator();
    }

    public static Iterator<File> iterateFilesAndDirs(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        return listFilesAndDirs(directory, fileFilter, dirFilter).iterator();
    }

    private static String[] toSuffixes(String[] extensions) {
        String[] suffixes = new String[extensions.length];
        for (int i = 0; i < extensions.length; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(".");
            stringBuilder.append(extensions[i]);
            suffixes[i] = stringBuilder.toString();
        }
        return suffixes;
    }

    public static Collection<File> listFiles(File directory, String[] extensions, boolean recursive) {
        IOFileFilter filter;
        if (extensions == null) {
            filter = TrueFileFilter.INSTANCE;
        } else {
            filter = new SuffixFileFilter(toSuffixes(extensions));
        }
        return listFiles(directory, filter, recursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE);
    }

    public static Iterator<File> iterateFiles(File directory, String[] extensions, boolean recursive) {
        return listFiles(directory, extensions, recursive).iterator();
    }

    public static boolean contentEquals(File file1, File file2) throws IOException {
        boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        }
        if (!file1Exists) {
            return true;
        }
        if (file1.isDirectory() || file2.isDirectory()) {
            throw new IOException("Can't compare directories, only files");
        } else if (file1.length() != file2.length()) {
            return false;
        } else {
            if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
                return true;
            }
            InputStream input1 = null;
            InputStream input2 = null;
            try {
                input1 = new FileInputStream(file1);
                input2 = new FileInputStream(file2);
                boolean contentEquals = IOUtils.contentEquals(input1, input2);
                return contentEquals;
            } finally {
                IOUtils.closeQuietly(input1);
                IOUtils.closeQuietly(input2);
            }
        }
    }

    public static boolean contentEqualsIgnoreEOL(File file1, File file2, String charsetName) throws IOException {
        boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        }
        if (!file1Exists) {
            return true;
        }
        if (file1.isDirectory() || file2.isDirectory()) {
            throw new IOException("Can't compare directories, only files");
        } else if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
            return true;
        } else {
            Reader input1;
            Reader input2;
            if (charsetName == null) {
                try {
                    input1 = new InputStreamReader(new FileInputStream(file1), Charset.defaultCharset());
                    input2 = new InputStreamReader(new FileInputStream(file2), Charset.defaultCharset());
                } catch (Throwable th) {
                    IOUtils.closeQuietly(null);
                    IOUtils.closeQuietly(null);
                }
            } else {
                input1 = new InputStreamReader(new FileInputStream(file1), charsetName);
                input2 = new InputStreamReader(new FileInputStream(file2), charsetName);
            }
            boolean contentEqualsIgnoreEOL = IOUtils.contentEqualsIgnoreEOL(input1, input2);
            IOUtils.closeQuietly(input1);
            IOUtils.closeQuietly(input2);
            return contentEqualsIgnoreEOL;
        }
    }

    public static File toFile(URL url) {
        if (url != null) {
            if ("file".equalsIgnoreCase(url.getProtocol())) {
                return new File(decodeUrl(url.getFile().replace(IOUtils.DIR_SEPARATOR_UNIX, File.separatorChar)));
            }
        }
        return null;
    }

    static String decodeUrl(String url) {
        String decoded = url;
        if (url == null || url.indexOf(37) < 0) {
            return decoded;
        }
        int n = url.length();
        StringBuilder buffer = new StringBuilder();
        ByteBuffer bytes = ByteBuffer.allocate(n);
        int i = 0;
        while (i < n) {
            if (url.charAt(i) == '%') {
                while (true) {
                    try {
                        bytes.put((byte) Integer.parseInt(url.substring(i + 1, i + 3), 16));
                        i += 3;
                        if (i >= n) {
                            break;
                        } else if (url.charAt(i) != '%') {
                            break;
                        }
                    } catch (RuntimeException e) {
                        if (bytes.position() > 0) {
                            bytes.flip();
                            buffer.append(Charsets.UTF_8.decode(bytes).toString());
                            bytes.clear();
                        }
                    } catch (Throwable th) {
                        if (bytes.position() > 0) {
                            bytes.flip();
                            buffer.append(Charsets.UTF_8.decode(bytes).toString());
                            bytes.clear();
                        }
                    }
                }
                if (bytes.position() > 0) {
                    bytes.flip();
                    buffer.append(Charsets.UTF_8.decode(bytes).toString());
                    bytes.clear();
                }
            } else {
                int i2 = i + 1;
                buffer.append(url.charAt(i));
                i = i2;
            }
        }
        return buffer.toString();
    }

    public static File[] toFiles(URL[] urls) {
        if (urls != null) {
            if (urls.length != 0) {
                File[] files = new File[urls.length];
                for (int i = 0; i < urls.length; i++) {
                    URL url = urls[i];
                    if (url != null) {
                        if (url.getProtocol().equals("file")) {
                            files[i] = toFile(url);
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("URL could not be converted to a File: ");
                            stringBuilder.append(url);
                            throw new IllegalArgumentException(stringBuilder.toString());
                        }
                    }
                }
                return files;
            }
        }
        return EMPTY_FILE_ARRAY;
    }

    public static URL[] toURLs(File[] files) throws IOException {
        URL[] urls = new URL[files.length];
        for (int i = 0; i < urls.length; i++) {
            urls[i] = files[i].toURI().toURL();
        }
        return urls;
    }

    public static void copyFileToDirectory(File srcFile, File destDir) throws IOException {
        copyFileToDirectory(srcFile, destDir, true);
    }

    public static void copyFileToDirectory(File srcFile, File destDir, boolean preserveFileDate) throws IOException {
        if (destDir != null) {
            if (destDir.exists()) {
                if (!destDir.isDirectory()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Destination '");
                    stringBuilder.append(destDir);
                    stringBuilder.append("' is not a directory");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
            }
            copyFile(srcFile, new File(destDir, srcFile.getName()), preserveFileDate);
            return;
        }
        throw new NullPointerException("Destination must not be null");
    }

    public static void copyFile(File srcFile, File destFile) throws IOException {
        copyFile(srcFile, destFile, true);
    }

    public static void copyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        checkFileRequirements(srcFile, destFile);
        StringBuilder stringBuilder;
        if (srcFile.isDirectory()) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Source '");
            stringBuilder.append(srcFile);
            stringBuilder.append("' exists but is a directory");
            throw new IOException(stringBuilder.toString());
        } else if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Source '");
            stringBuilder.append(srcFile);
            stringBuilder.append("' and destination '");
            stringBuilder.append(destFile);
            stringBuilder.append("' are the same");
            throw new IOException(stringBuilder.toString());
        } else {
            StringBuilder stringBuilder2;
            File parentFile = destFile.getParentFile();
            if (parentFile != null) {
                if (!parentFile.mkdirs()) {
                    if (!parentFile.isDirectory()) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Destination '");
                        stringBuilder2.append(parentFile);
                        stringBuilder2.append("' directory cannot be created");
                        throw new IOException(stringBuilder2.toString());
                    }
                }
            }
            if (destFile.exists()) {
                if (!destFile.canWrite()) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Destination '");
                    stringBuilder2.append(destFile);
                    stringBuilder2.append("' exists but is read-only");
                    throw new IOException(stringBuilder2.toString());
                }
            }
            doCopyFile(srcFile, destFile, preserveFileDate);
        }
    }

    public static long copyFile(File input, OutputStream output) throws IOException {
        InputStream fis = new FileInputStream(input);
        try {
            long copyLarge = IOUtils.copyLarge(fis, output);
            return copyLarge;
        } finally {
            fis.close();
        }
    }

    public static void copyDirectoryToDirectory(File srcDir, File destDir) throws IOException {
        if (srcDir != null) {
            StringBuilder stringBuilder;
            if (srcDir.exists()) {
                if (!srcDir.isDirectory()) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Source '");
                    stringBuilder.append(destDir);
                    stringBuilder.append("' is not a directory");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
            }
            if (destDir != null) {
                if (destDir.exists()) {
                    if (!destDir.isDirectory()) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Destination '");
                        stringBuilder.append(destDir);
                        stringBuilder.append("' is not a directory");
                        throw new IllegalArgumentException(stringBuilder.toString());
                    }
                }
                copyDirectory(srcDir, new File(destDir, srcDir.getName()), true);
                return;
            }
            throw new NullPointerException("Destination must not be null");
        }
        throw new NullPointerException("Source must not be null");
    }

    public static void copyDirectory(File srcDir, File destDir) throws IOException {
        copyDirectory(srcDir, destDir, true);
    }

    public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {
        copyDirectory(srcDir, destDir, null, preserveFileDate);
    }

    public static void copyDirectory(File srcDir, File destDir, FileFilter filter) throws IOException {
        copyDirectory(srcDir, destDir, filter, true);
    }

    private static void checkFileRequirements(File src, File dest) throws FileNotFoundException {
        if (src == null) {
            throw new NullPointerException("Source must not be null");
        } else if (dest == null) {
            throw new NullPointerException("Destination must not be null");
        } else if (!src.exists()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Source '");
            stringBuilder.append(src);
            stringBuilder.append("' does not exist");
            throw new FileNotFoundException(stringBuilder.toString());
        }
    }

    public static void copyURLToFile(URL source, File destination) throws IOException {
        copyInputStreamToFile(source.openStream(), destination);
    }

    public static void copyURLToFile(URL source, File destination, int connectionTimeout, int readTimeout) throws IOException {
        URLConnection connection = source.openConnection();
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(readTimeout);
        copyInputStreamToFile(connection.getInputStream(), destination);
    }

    public static void copyInputStreamToFile(InputStream source, File destination) throws IOException {
        try {
            copyToFile(source, destination);
        } finally {
            IOUtils.closeQuietly(source);
        }
    }

    public static void copyToFile(InputStream source, File destination) throws IOException {
        OutputStream output = openOutputStream(destination);
        try {
            IOUtils.copy(source, output);
            output.close();
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    public static void deleteDirectory(File directory) throws IOException {
        if (directory.exists()) {
            if (!isSymlink(directory)) {
                cleanDirectory(directory);
            }
            if (!directory.delete()) {
                String message = new StringBuilder();
                message.append("Unable to delete directory ");
                message.append(directory);
                message.append(".");
                throw new IOException(message.toString());
            }
        }
    }

    public static boolean deleteQuietly(File file) {
        boolean z = false;
        if (file == null) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                cleanDirectory(file);
            }
        } catch (Exception e) {
        }
        try {
            z = file.delete();
            return z;
        } catch (Exception e2) {
            return z;
        }
    }

    public static boolean directoryContains(File directory, File child) throws IOException {
        if (directory == null) {
            throw new IllegalArgumentException("Directory must not be null");
        } else if (!directory.isDirectory()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Not a directory: ");
            stringBuilder.append(directory);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (child == null) {
            return false;
        } else {
            if (directory.exists()) {
                if (child.exists()) {
                    return FilenameUtils.directoryContains(directory.getCanonicalPath(), child.getCanonicalPath());
                }
            }
            return false;
        }
    }

    private static File[] verifiedListFiles(File directory) throws IOException {
        if (!directory.exists()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(directory);
            stringBuilder.append(" does not exist");
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                return files;
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Failed to list contents of ");
            stringBuilder2.append(directory);
            throw new IOException(stringBuilder2.toString());
        } else {
            String message = new StringBuilder();
            message.append(directory);
            message.append(" is not a directory");
            throw new IllegalArgumentException(message.toString());
        }
    }

    public static String readFileToString(File file, Charset encoding) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            String iOUtils = IOUtils.toString(in, Charsets.toCharset(encoding));
            return iOUtils;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static String readFileToString(File file, String encoding) throws IOException {
        return readFileToString(file, Charsets.toCharset(encoding));
    }

    @Deprecated
    public static String readFileToString(File file) throws IOException {
        return readFileToString(file, Charset.defaultCharset());
    }

    public static byte[] readFileToByteArray(File file) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            byte[] toByteArray = IOUtils.toByteArray(in);
            return toByteArray;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static List<String> readLines(File file, Charset encoding) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            List<String> readLines = IOUtils.readLines(in, Charsets.toCharset(encoding));
            return readLines;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static List<String> readLines(File file, String encoding) throws IOException {
        return readLines(file, Charsets.toCharset(encoding));
    }

    @Deprecated
    public static List<String> readLines(File file) throws IOException {
        return readLines(file, Charset.defaultCharset());
    }

    public static LineIterator lineIterator(File file, String encoding) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            return IOUtils.lineIterator(in, encoding);
        } catch (IOException ex) {
            IOUtils.closeQuietly(in);
            throw ex;
        } catch (RuntimeException ex2) {
            IOUtils.closeQuietly(in);
            throw ex2;
        }
    }

    public static LineIterator lineIterator(File file) throws IOException {
        return lineIterator(file, null);
    }

    public static void writeStringToFile(File file, String data, Charset encoding) throws IOException {
        writeStringToFile(file, data, encoding, false);
    }

    public static void writeStringToFile(File file, String data, String encoding) throws IOException {
        writeStringToFile(file, data, encoding, false);
    }

    public static void writeStringToFile(File file, String data, Charset encoding, boolean append) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, append);
            IOUtils.write(data, out, encoding);
            out.close();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public static void writeStringToFile(File file, String data, String encoding, boolean append) throws IOException {
        writeStringToFile(file, data, Charsets.toCharset(encoding), append);
    }

    @Deprecated
    public static void writeStringToFile(File file, String data) throws IOException {
        writeStringToFile(file, data, Charset.defaultCharset(), false);
    }

    @Deprecated
    public static void writeStringToFile(File file, String data, boolean append) throws IOException {
        writeStringToFile(file, data, Charset.defaultCharset(), append);
    }

    @Deprecated
    public static void write(File file, CharSequence data) throws IOException {
        write(file, data, Charset.defaultCharset(), false);
    }

    @Deprecated
    public static void write(File file, CharSequence data, boolean append) throws IOException {
        write(file, data, Charset.defaultCharset(), append);
    }

    public static void write(File file, CharSequence data, Charset encoding) throws IOException {
        write(file, data, encoding, false);
    }

    public static void write(File file, CharSequence data, String encoding) throws IOException {
        write(file, data, encoding, false);
    }

    public static void write(File file, CharSequence data, Charset encoding, boolean append) throws IOException {
        writeStringToFile(file, data == null ? null : data.toString(), encoding, append);
    }

    public static void write(File file, CharSequence data, String encoding, boolean append) throws IOException {
        write(file, data, Charsets.toCharset(encoding), append);
    }

    public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
        writeByteArrayToFile(file, data, false);
    }

    public static void writeByteArrayToFile(File file, byte[] data, boolean append) throws IOException {
        writeByteArrayToFile(file, data, 0, data.length, append);
    }

    public static void writeByteArrayToFile(File file, byte[] data, int off, int len) throws IOException {
        writeByteArrayToFile(file, data, off, len, false);
    }

    public static void writeByteArrayToFile(File file, byte[] data, int off, int len, boolean append) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, append);
            out.write(data, off, len);
            out.close();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public static void writeLines(File file, String encoding, Collection<?> lines) throws IOException {
        writeLines(file, encoding, lines, null, false);
    }

    public static void writeLines(File file, String encoding, Collection<?> lines, boolean append) throws IOException {
        writeLines(file, encoding, lines, null, append);
    }

    public static void writeLines(File file, Collection<?> lines) throws IOException {
        writeLines(file, null, lines, null, false);
    }

    public static void writeLines(File file, Collection<?> lines, boolean append) throws IOException {
        writeLines(file, null, lines, null, append);
    }

    public static void writeLines(File file, String encoding, Collection<?> lines, String lineEnding) throws IOException {
        writeLines(file, encoding, lines, lineEnding, false);
    }

    public static void writeLines(File file, String encoding, Collection<?> lines, String lineEnding, boolean append) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, append);
            OutputStream buffer = new BufferedOutputStream(out);
            IOUtils.writeLines((Collection) lines, lineEnding, buffer, encoding);
            buffer.flush();
            out.close();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public static void writeLines(File file, Collection<?> lines, String lineEnding) throws IOException {
        writeLines(file, null, lines, lineEnding, false);
    }

    public static void writeLines(File file, Collection<?> lines, String lineEnding, boolean append) throws IOException {
        writeLines(file, null, lines, lineEnding, append);
    }

    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
            return;
        }
        boolean filePresent = file.exists();
        if (!file.delete()) {
            if (filePresent) {
                String message = new StringBuilder();
                message.append("Unable to delete file: ");
                message.append(file);
                throw new IOException(message.toString());
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("File does not exist: ");
            stringBuilder.append(file);
            throw new FileNotFoundException(stringBuilder.toString());
        }
    }

    public static void forceDeleteOnExit(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectoryOnExit(file);
        } else {
            file.deleteOnExit();
        }
    }

    private static void deleteDirectoryOnExit(File directory) throws IOException {
        if (directory.exists()) {
            directory.deleteOnExit();
            if (!isSymlink(directory)) {
                cleanDirectoryOnExit(directory);
            }
        }
    }

    public static void forceMkdir(File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                String message = new StringBuilder();
                message.append("File ");
                message.append(directory);
                message.append(" exists and is ");
                message.append("not a directory. Unable to create directory.");
                throw new IOException(message.toString());
            }
        } else if (!directory.mkdirs()) {
            if (!directory.isDirectory()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unable to create directory ");
                stringBuilder.append(directory);
                throw new IOException(stringBuilder.toString());
            }
        }
    }

    public static void forceMkdirParent(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null) {
            forceMkdir(parent);
        }
    }

    public static long sizeOf(File file) {
        if (!file.exists()) {
            String message = new StringBuilder();
            message.append(file);
            message.append(" does not exist");
            throw new IllegalArgumentException(message.toString());
        } else if (file.isDirectory()) {
            return sizeOfDirectory0(file);
        } else {
            return file.length();
        }
    }

    public static BigInteger sizeOfAsBigInteger(File file) {
        if (!file.exists()) {
            String message = new StringBuilder();
            message.append(file);
            message.append(" does not exist");
            throw new IllegalArgumentException(message.toString());
        } else if (file.isDirectory()) {
            return sizeOfDirectoryBig0(file);
        } else {
            return BigInteger.valueOf(file.length());
        }
    }

    public static long sizeOfDirectory(File directory) {
        checkDirectory(directory);
        return sizeOfDirectory0(directory);
    }

    private static long sizeOfDirectory0(File directory) {
        File[] files = directory.listFiles();
        if (files == null) {
            return 0;
        }
        long size = 0;
        for (File file : files) {
            try {
                if (!isSymlink(file)) {
                    size += sizeOf0(file);
                    if (size < 0) {
                        break;
                    }
                }
            } catch (IOException e) {
            }
        }
        return size;
    }

    private static long sizeOf0(File file) {
        if (file.isDirectory()) {
            return sizeOfDirectory0(file);
        }
        return file.length();
    }

    public static BigInteger sizeOfDirectoryAsBigInteger(File directory) {
        checkDirectory(directory);
        return sizeOfDirectoryBig0(directory);
    }

    private static BigInteger sizeOfDirectoryBig0(File directory) {
        File[] files = directory.listFiles();
        if (files == null) {
            return BigInteger.ZERO;
        }
        BigInteger size = BigInteger.ZERO;
        for (File file : files) {
            try {
                if (!isSymlink(file)) {
                    size = size.add(sizeOfBig0(file));
                }
            } catch (IOException e) {
            }
        }
        return size;
    }

    private static BigInteger sizeOfBig0(File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            return sizeOfDirectoryBig0(fileOrDir);
        }
        return BigInteger.valueOf(fileOrDir.length());
    }

    private static void checkDirectory(File directory) {
        StringBuilder stringBuilder;
        if (!directory.exists()) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(directory);
            stringBuilder.append(" does not exist");
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (!directory.isDirectory()) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(directory);
            stringBuilder.append(" is not a directory");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public static boolean isFileNewer(File file, File reference) {
        if (reference == null) {
            throw new IllegalArgumentException("No specified reference file");
        } else if (reference.exists()) {
            return isFileNewer(file, reference.lastModified());
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("The reference file '");
            stringBuilder.append(reference);
            stringBuilder.append("' doesn't exist");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public static boolean isFileNewer(File file, Date date) {
        if (date != null) {
            return isFileNewer(file, date.getTime());
        }
        throw new IllegalArgumentException("No specified date");
    }

    public static boolean isFileNewer(File file, long timeMillis) {
        if (file != null) {
            boolean z = false;
            if (!file.exists()) {
                return false;
            }
            if (file.lastModified() > timeMillis) {
                z = true;
            }
            return z;
        }
        throw new IllegalArgumentException("No specified file");
    }

    public static boolean isFileOlder(File file, File reference) {
        if (reference == null) {
            throw new IllegalArgumentException("No specified reference file");
        } else if (reference.exists()) {
            return isFileOlder(file, reference.lastModified());
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("The reference file '");
            stringBuilder.append(reference);
            stringBuilder.append("' doesn't exist");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public static boolean isFileOlder(File file, Date date) {
        if (date != null) {
            return isFileOlder(file, date.getTime());
        }
        throw new IllegalArgumentException("No specified date");
    }

    public static boolean isFileOlder(File file, long timeMillis) {
        if (file != null) {
            boolean z = false;
            if (!file.exists()) {
                return false;
            }
            if (file.lastModified() < timeMillis) {
                z = true;
            }
            return z;
        }
        throw new IllegalArgumentException("No specified file");
    }

    public static long checksumCRC32(File file) throws IOException {
        CRC32 crc = new CRC32();
        checksum(file, crc);
        return crc.getValue();
    }

    public static Checksum checksum(File file, Checksum checksum) throws IOException {
        if (file.isDirectory()) {
            throw new IllegalArgumentException("Checksums can't be computed on directories");
        }
        InputStream in = null;
        try {
            in = new CheckedInputStream(new FileInputStream(file), checksum);
            IOUtils.copy(in, new NullOutputStream());
            return checksum;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static void moveDirectory(File srcDir, File destDir) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        } else if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        } else if (!srcDir.exists()) {
            r1 = new StringBuilder();
            r1.append("Source '");
            r1.append(srcDir);
            r1.append("' does not exist");
            throw new FileNotFoundException(r1.toString());
        } else if (!srcDir.isDirectory()) {
            r1 = new StringBuilder();
            r1.append("Source '");
            r1.append(srcDir);
            r1.append("' is not a directory");
            throw new IOException(r1.toString());
        } else if (destDir.exists()) {
            r1 = new StringBuilder();
            r1.append("Destination '");
            r1.append(destDir);
            r1.append("' already exists");
            throw new FileExistsException(r1.toString());
        } else if (!srcDir.renameTo(destDir)) {
            String canonicalPath = destDir.getCanonicalPath();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(srcDir.getCanonicalPath());
            stringBuilder.append(File.separator);
            if (canonicalPath.startsWith(stringBuilder.toString())) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Cannot move directory: ");
                stringBuilder.append(srcDir);
                stringBuilder.append(" to a subdirectory of itself: ");
                stringBuilder.append(destDir);
                throw new IOException(stringBuilder.toString());
            }
            copyDirectory(srcDir, destDir);
            deleteDirectory(srcDir);
            if (srcDir.exists()) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Failed to delete original directory '");
                stringBuilder.append(srcDir);
                stringBuilder.append("' after copy to '");
                stringBuilder.append(destDir);
                stringBuilder.append("'");
                throw new IOException(stringBuilder.toString());
            }
        }
    }

    public static void moveDirectoryToDirectory(File src, File destDir, boolean createDestDir) throws IOException {
        if (src == null) {
            throw new NullPointerException("Source must not be null");
        } else if (destDir != null) {
            if (!destDir.exists() && createDestDir) {
                destDir.mkdirs();
            }
            StringBuilder stringBuilder;
            if (!destDir.exists()) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Destination directory '");
                stringBuilder.append(destDir);
                stringBuilder.append("' does not exist [createDestDir=");
                stringBuilder.append(createDestDir);
                stringBuilder.append("]");
                throw new FileNotFoundException(stringBuilder.toString());
            } else if (destDir.isDirectory()) {
                moveDirectory(src, new File(destDir, src.getName()));
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Destination '");
                stringBuilder.append(destDir);
                stringBuilder.append("' is not a directory");
                throw new IOException(stringBuilder.toString());
            }
        } else {
            throw new NullPointerException("Destination directory must not be null");
        }
    }

    public static void moveFile(File srcFile, File destFile) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        } else if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        } else if (!srcFile.exists()) {
            r1 = new StringBuilder();
            r1.append("Source '");
            r1.append(srcFile);
            r1.append("' does not exist");
            throw new FileNotFoundException(r1.toString());
        } else if (srcFile.isDirectory()) {
            r1 = new StringBuilder();
            r1.append("Source '");
            r1.append(srcFile);
            r1.append("' is a directory");
            throw new IOException(r1.toString());
        } else if (destFile.exists()) {
            r1 = new StringBuilder();
            r1.append("Destination '");
            r1.append(destFile);
            r1.append("' already exists");
            throw new FileExistsException(r1.toString());
        } else if (destFile.isDirectory()) {
            r1 = new StringBuilder();
            r1.append("Destination '");
            r1.append(destFile);
            r1.append("' is a directory");
            throw new IOException(r1.toString());
        } else if (!srcFile.renameTo(destFile)) {
            copyFile(srcFile, destFile);
            if (!srcFile.delete()) {
                deleteQuietly(destFile);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Failed to delete original file '");
                stringBuilder.append(srcFile);
                stringBuilder.append("' after copy to '");
                stringBuilder.append(destFile);
                stringBuilder.append("'");
                throw new IOException(stringBuilder.toString());
            }
        }
    }

    public static void moveFileToDirectory(File srcFile, File destDir, boolean createDestDir) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        } else if (destDir != null) {
            if (!destDir.exists() && createDestDir) {
                destDir.mkdirs();
            }
            StringBuilder stringBuilder;
            if (!destDir.exists()) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Destination directory '");
                stringBuilder.append(destDir);
                stringBuilder.append("' does not exist [createDestDir=");
                stringBuilder.append(createDestDir);
                stringBuilder.append("]");
                throw new FileNotFoundException(stringBuilder.toString());
            } else if (destDir.isDirectory()) {
                moveFile(srcFile, new File(destDir, srcFile.getName()));
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Destination '");
                stringBuilder.append(destDir);
                stringBuilder.append("' is not a directory");
                throw new IOException(stringBuilder.toString());
            }
        } else {
            throw new NullPointerException("Destination directory must not be null");
        }
    }

    public static void moveToDirectory(File src, File destDir, boolean createDestDir) throws IOException {
        if (src == null) {
            throw new NullPointerException("Source must not be null");
        } else if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        } else if (!src.exists()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Source '");
            stringBuilder.append(src);
            stringBuilder.append("' does not exist");
            throw new FileNotFoundException(stringBuilder.toString());
        } else if (src.isDirectory()) {
            moveDirectoryToDirectory(src, destDir, createDestDir);
        } else {
            moveFileToDirectory(src, destDir, createDestDir);
        }
    }

    public static boolean isSymlink(File file) throws IOException {
        if (Java7Support.isAtLeastJava7()) {
            return Java7Support.isSymLink(file);
        }
        if (file == null) {
            throw new NullPointerException("File must not be null");
        } else if (FilenameUtils.isSystemWindows()) {
            return false;
        } else {
            File fileInCanonicalDir;
            if (file.getParent() == null) {
                fileInCanonicalDir = file;
            } else {
                fileInCanonicalDir = new File(file.getParentFile().getCanonicalFile(), file.getName());
            }
            if (fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile())) {
                return isBrokenSymlink(file);
            }
            return true;
        }
    }

    private static boolean isBrokenSymlink(File file) throws IOException {
        boolean z = false;
        if (file.exists()) {
            return false;
        }
        final File canon = file.getCanonicalFile();
        File parentDir = canon.getParentFile();
        if (parentDir != null) {
            if (parentDir.exists()) {
                File[] fileInDir = parentDir.listFiles(new FileFilter() {
                    public boolean accept(File aFile) {
                        return aFile.equals(canon);
                    }
                });
                if (fileInDir != null && fileInDir.length > 0) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }
}

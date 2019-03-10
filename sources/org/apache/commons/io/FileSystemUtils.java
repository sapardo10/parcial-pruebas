package org.apache.commons.io;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;

public class FileSystemUtils {
    private static final String DF;
    private static final int INIT_PROBLEM = -1;
    private static final FileSystemUtils INSTANCE = new FileSystemUtils();
    private static final int OS;
    private static final int OTHER = 0;
    private static final int POSIX_UNIX = 3;
    private static final int UNIX = 2;
    private static final int WINDOWS = 1;

    long freeSpaceWindows(java.lang.String r7, long r8) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x008b in {4, 5, 11, 12, 14} preds:[]
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
        r0 = 0;
        r7 = org.apache.commons.io.FilenameUtils.normalize(r7, r0);
        r1 = r7.length();
        if (r1 <= 0) goto L_0x002a;
    L_0x000b:
        r1 = r7.charAt(r0);
        r2 = 34;
        if (r1 == r2) goto L_0x002a;
    L_0x0013:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "\"";
        r1.append(r2);
        r1.append(r7);
        r2 = "\"";
        r1.append(r2);
        r7 = r1.toString();
        goto L_0x002b;
    L_0x002b:
        r1 = 3;
        r1 = new java.lang.String[r1];
        r2 = "cmd.exe";
        r1[r0] = r2;
        r0 = "/C";
        r2 = 1;
        r1[r2] = r0;
        r0 = 2;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "dir /a /-c ";
        r3.append(r4);
        r3.append(r7);
        r3 = r3.toString();
        r1[r0] = r3;
        r0 = r1;
        r1 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r1 = r6.performCommand(r0, r1, r8);
        r3 = r1.size();
        r3 = r3 - r2;
    L_0x0058:
        if (r3 < 0) goto L_0x006f;
    L_0x005a:
        r2 = r1.get(r3);
        r2 = (java.lang.String) r2;
        r4 = r2.length();
        if (r4 <= 0) goto L_0x006b;
    L_0x0066:
        r4 = r6.parseDir(r2, r7);
        return r4;
        r3 = r3 + -1;
        goto L_0x0058;
    L_0x006f:
        r2 = new java.io.IOException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Command line 'dir /-c' did not return any info for path '";
        r3.append(r4);
        r3.append(r7);
        r4 = "'";
        r3.append(r4);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.FileSystemUtils.freeSpaceWindows(java.lang.String, long):long");
    }

    long parseDir(java.lang.String r9, java.lang.String r10) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:32:0x0087 in {4, 5, 6, 13, 14, 15, 24, 25, 26, 27, 29, 31} preds:[]
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
        r0 = 0;
        r1 = 0;
        r2 = r9.length();
        r2 = r2 + -1;
    L_0x0008:
        if (r2 < 0) goto L_0x001b;
    L_0x000a:
        r3 = r9.charAt(r2);
        r4 = java.lang.Character.isDigit(r3);
        if (r4 == 0) goto L_0x0017;
    L_0x0014:
        r1 = r2 + 1;
        goto L_0x001c;
        r2 = r2 + -1;
        goto L_0x0008;
    L_0x001c:
        r3 = 46;
        r4 = 44;
        if (r2 < 0) goto L_0x0038;
    L_0x0022:
        r5 = r9.charAt(r2);
        r6 = java.lang.Character.isDigit(r5);
        if (r6 != 0) goto L_0x0033;
    L_0x002c:
        if (r5 == r4) goto L_0x0033;
    L_0x002e:
        if (r5 == r3) goto L_0x0033;
    L_0x0030:
        r0 = r2 + 1;
        goto L_0x0039;
        r2 = r2 + -1;
        goto L_0x001c;
    L_0x0039:
        if (r2 < 0) goto L_0x006b;
    L_0x003b:
        r5 = new java.lang.StringBuilder;
        r6 = r9.substring(r0, r1);
        r5.<init>(r6);
        r6 = 0;
    L_0x0045:
        r7 = r5.length();
        if (r6 >= r7) goto L_0x0062;
    L_0x004b:
        r7 = r5.charAt(r6);
        if (r7 == r4) goto L_0x0059;
    L_0x0051:
        r7 = r5.charAt(r6);
        if (r7 != r3) goto L_0x0058;
    L_0x0057:
        goto L_0x0059;
    L_0x0058:
        goto L_0x005f;
    L_0x0059:
        r7 = r6 + -1;
        r5.deleteCharAt(r6);
        r6 = r7;
    L_0x005f:
        r6 = r6 + 1;
        goto L_0x0045;
    L_0x0062:
        r3 = r5.toString();
        r3 = r8.parseBytes(r3, r10);
        return r3;
    L_0x006b:
        r3 = new java.io.IOException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Command line 'dir /-c' did not return valid info for path '";
        r4.append(r5);
        r4.append(r10);
        r5 = "'";
        r4.append(r5);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.FileSystemUtils.parseDir(java.lang.String, java.lang.String):long");
    }

    java.util.List<java.lang.String> performCommand(java.lang.String[] r12, int r13, long r14) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:32:0x00f7 in {6, 13, 14, 15, 18, 20, 26, 29, 30, 31} preds:[]
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
        r0 = new java.util.ArrayList;
        r1 = 20;
        r0.<init>(r1);
        r1 = 0;
        r2 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = org.apache.commons.io.ThreadMonitor.start(r14);	 Catch:{ InterruptedException -> 0x00bf }
        r7 = r11.openProcess(r12);	 Catch:{ InterruptedException -> 0x00bf }
        r1 = r7;	 Catch:{ InterruptedException -> 0x00bf }
        r7 = r1.getInputStream();	 Catch:{ InterruptedException -> 0x00bf }
        r2 = r7;	 Catch:{ InterruptedException -> 0x00bf }
        r7 = r1.getOutputStream();	 Catch:{ InterruptedException -> 0x00bf }
        r3 = r7;	 Catch:{ InterruptedException -> 0x00bf }
        r7 = r1.getErrorStream();	 Catch:{ InterruptedException -> 0x00bf }
        r4 = r7;	 Catch:{ InterruptedException -> 0x00bf }
        r7 = new java.io.BufferedReader;	 Catch:{ InterruptedException -> 0x00bf }
        r8 = new java.io.InputStreamReader;	 Catch:{ InterruptedException -> 0x00bf }
        r9 = java.nio.charset.Charset.defaultCharset();	 Catch:{ InterruptedException -> 0x00bf }
        r8.<init>(r2, r9);	 Catch:{ InterruptedException -> 0x00bf }
        r7.<init>(r8);	 Catch:{ InterruptedException -> 0x00bf }
        r5 = r7;	 Catch:{ InterruptedException -> 0x00bf }
        r7 = r5.readLine();	 Catch:{ InterruptedException -> 0x00bf }
    L_0x0037:
        if (r7 == 0) goto L_0x0053;	 Catch:{ InterruptedException -> 0x00bf }
    L_0x0039:
        r8 = r0.size();	 Catch:{ InterruptedException -> 0x00bf }
        if (r8 >= r13) goto L_0x0053;	 Catch:{ InterruptedException -> 0x00bf }
    L_0x003f:
        r8 = java.util.Locale.ENGLISH;	 Catch:{ InterruptedException -> 0x00bf }
        r8 = r7.toLowerCase(r8);	 Catch:{ InterruptedException -> 0x00bf }
        r8 = r8.trim();	 Catch:{ InterruptedException -> 0x00bf }
        r7 = r8;	 Catch:{ InterruptedException -> 0x00bf }
        r0.add(r7);	 Catch:{ InterruptedException -> 0x00bf }
        r8 = r5.readLine();	 Catch:{ InterruptedException -> 0x00bf }
        r7 = r8;	 Catch:{ InterruptedException -> 0x00bf }
        goto L_0x0037;	 Catch:{ InterruptedException -> 0x00bf }
        r1.waitFor();	 Catch:{ InterruptedException -> 0x00bf }
        org.apache.commons.io.ThreadMonitor.stop(r6);	 Catch:{ InterruptedException -> 0x00bf }
        r8 = r1.exitValue();	 Catch:{ InterruptedException -> 0x00bf }
        if (r8 != 0) goto L_0x0096;	 Catch:{ InterruptedException -> 0x00bf }
    L_0x0060:
        r8 = r0.isEmpty();	 Catch:{ InterruptedException -> 0x00bf }
        if (r8 != 0) goto L_0x007b;
        org.apache.commons.io.IOUtils.closeQuietly(r2);
        org.apache.commons.io.IOUtils.closeQuietly(r3);
        org.apache.commons.io.IOUtils.closeQuietly(r4);
        org.apache.commons.io.IOUtils.closeQuietly(r5);
        if (r1 == 0) goto L_0x0079;
    L_0x0075:
        r1.destroy();
        goto L_0x007a;
    L_0x007a:
        return r0;
    L_0x007b:
        r8 = new java.io.IOException;	 Catch:{ InterruptedException -> 0x00bf }
        r9 = new java.lang.StringBuilder;	 Catch:{ InterruptedException -> 0x00bf }
        r9.<init>();	 Catch:{ InterruptedException -> 0x00bf }
        r10 = "Command line did not return any info for command ";	 Catch:{ InterruptedException -> 0x00bf }
        r9.append(r10);	 Catch:{ InterruptedException -> 0x00bf }
        r10 = java.util.Arrays.asList(r12);	 Catch:{ InterruptedException -> 0x00bf }
        r9.append(r10);	 Catch:{ InterruptedException -> 0x00bf }
        r9 = r9.toString();	 Catch:{ InterruptedException -> 0x00bf }
        r8.<init>(r9);	 Catch:{ InterruptedException -> 0x00bf }
        throw r8;	 Catch:{ InterruptedException -> 0x00bf }
    L_0x0096:
        r8 = new java.io.IOException;	 Catch:{ InterruptedException -> 0x00bf }
        r9 = new java.lang.StringBuilder;	 Catch:{ InterruptedException -> 0x00bf }
        r9.<init>();	 Catch:{ InterruptedException -> 0x00bf }
        r10 = "Command line returned OS error code '";	 Catch:{ InterruptedException -> 0x00bf }
        r9.append(r10);	 Catch:{ InterruptedException -> 0x00bf }
        r10 = r1.exitValue();	 Catch:{ InterruptedException -> 0x00bf }
        r9.append(r10);	 Catch:{ InterruptedException -> 0x00bf }
        r10 = "' for command ";	 Catch:{ InterruptedException -> 0x00bf }
        r9.append(r10);	 Catch:{ InterruptedException -> 0x00bf }
        r10 = java.util.Arrays.asList(r12);	 Catch:{ InterruptedException -> 0x00bf }
        r9.append(r10);	 Catch:{ InterruptedException -> 0x00bf }
        r9 = r9.toString();	 Catch:{ InterruptedException -> 0x00bf }
        r8.<init>(r9);	 Catch:{ InterruptedException -> 0x00bf }
        throw r8;	 Catch:{ InterruptedException -> 0x00bf }
    L_0x00bd:
        r6 = move-exception;
        goto L_0x00e3;
    L_0x00bf:
        r6 = move-exception;
        r7 = new java.io.IOException;	 Catch:{ all -> 0x00bd }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00bd }
        r8.<init>();	 Catch:{ all -> 0x00bd }
        r9 = "Command line threw an InterruptedException for command ";	 Catch:{ all -> 0x00bd }
        r8.append(r9);	 Catch:{ all -> 0x00bd }
        r9 = java.util.Arrays.asList(r12);	 Catch:{ all -> 0x00bd }
        r8.append(r9);	 Catch:{ all -> 0x00bd }
        r9 = " timeout=";	 Catch:{ all -> 0x00bd }
        r8.append(r9);	 Catch:{ all -> 0x00bd }
        r8.append(r14);	 Catch:{ all -> 0x00bd }
        r8 = r8.toString();	 Catch:{ all -> 0x00bd }
        r7.<init>(r8, r6);	 Catch:{ all -> 0x00bd }
        throw r7;	 Catch:{ all -> 0x00bd }
    L_0x00e3:
        org.apache.commons.io.IOUtils.closeQuietly(r2);
        org.apache.commons.io.IOUtils.closeQuietly(r3);
        org.apache.commons.io.IOUtils.closeQuietly(r4);
        org.apache.commons.io.IOUtils.closeQuietly(r5);
        if (r1 == 0) goto L_0x00f5;
    L_0x00f1:
        r1.destroy();
        goto L_0x00f6;
    L_0x00f6:
        throw r6;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.FileSystemUtils.performCommand(java.lang.String[], int, long):java.util.List<java.lang.String>");
    }

    static {
        String dfPath = "df";
        int os;
        try {
            String osName = System.getProperty("os.name");
            if (osName != null) {
                osName = osName.toLowerCase(Locale.ENGLISH);
                if (osName.contains("windows")) {
                    os = 1;
                } else {
                    if (!(osName.contains("linux") || osName.contains("mpe/ix") || osName.contains("freebsd") || osName.contains("irix") || osName.contains("digital unix") || osName.contains("unix"))) {
                        if (!osName.contains("mac os x")) {
                            if (!(osName.contains("sun os") || osName.contains("sunos"))) {
                                if (!osName.contains("solaris")) {
                                    if (!osName.contains("hp-ux")) {
                                        if (!osName.contains("aix")) {
                                            os = 0;
                                        }
                                    }
                                    os = 3;
                                }
                            }
                            os = 3;
                            dfPath = "/usr/xpg4/bin/df";
                        }
                    }
                    os = 2;
                }
                OS = os;
                DF = dfPath;
                return;
            }
            throw new IOException("os.name not found");
        } catch (Exception e) {
            os = -1;
        }
    }

    @Deprecated
    public static long freeSpace(String path) throws IOException {
        return INSTANCE.freeSpaceOS(path, OS, false, -1);
    }

    public static long freeSpaceKb(String path) throws IOException {
        return freeSpaceKb(path, -1);
    }

    public static long freeSpaceKb(String path, long timeout) throws IOException {
        return INSTANCE.freeSpaceOS(path, OS, true, timeout);
    }

    public static long freeSpaceKb() throws IOException {
        return freeSpaceKb(-1);
    }

    public static long freeSpaceKb(long timeout) throws IOException {
        return freeSpaceKb(new File(".").getAbsolutePath(), timeout);
    }

    long freeSpaceOS(String path, int os, boolean kb, long timeout) throws IOException {
        if (path != null) {
            switch (os) {
                case 0:
                    throw new IllegalStateException("Unsupported operating system");
                case 1:
                    return kb ? freeSpaceWindows(path, timeout) / 1024 : freeSpaceWindows(path, timeout);
                case 2:
                    return freeSpaceUnix(path, kb, false, timeout);
                case 3:
                    return freeSpaceUnix(path, kb, true, timeout);
                default:
                    throw new IllegalStateException("Exception caught when determining operating system");
            }
        }
        throw new IllegalArgumentException("Path must not be null");
    }

    long freeSpaceUnix(String path, boolean kb, boolean posix, long timeout) throws IOException {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Path must not be empty");
        }
        String flags = "-";
        if (kb) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(flags);
            stringBuilder.append("k");
            flags = stringBuilder.toString();
        }
        if (posix) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(flags);
            stringBuilder.append("P");
            flags = stringBuilder.toString();
        }
        List<String> lines = performCommand(flags.length() > 1 ? new String[]{DF, flags, path} : new String[]{DF, path}, 3, timeout);
        if (lines.size() >= 2) {
            StringTokenizer tok = new StringTokenizer((String) lines.get(1), StringUtils.SPACE);
            if (tok.countTokens() >= 4) {
                tok.nextToken();
            } else if (tok.countTokens() != 1 || lines.size() < 3) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Command line '");
                stringBuilder2.append(DF);
                stringBuilder2.append("' did not return data as expected ");
                stringBuilder2.append("for path '");
                stringBuilder2.append(path);
                stringBuilder2.append("'- check path is valid");
                throw new IOException(stringBuilder2.toString());
            } else {
                tok = new StringTokenizer((String) lines.get(2), StringUtils.SPACE);
            }
            tok.nextToken();
            tok.nextToken();
            return parseBytes(tok.nextToken(), path);
        }
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Command line '");
        stringBuilder2.append(DF);
        stringBuilder2.append("' did not return info as expected ");
        stringBuilder2.append("for path '");
        stringBuilder2.append(path);
        stringBuilder2.append("'- response was ");
        stringBuilder2.append(lines);
        throw new IOException(stringBuilder2.toString());
    }

    long parseBytes(String freeSpace, String path) throws IOException {
        try {
            long bytes = Long.parseLong(freeSpace);
            if (bytes >= 0) {
                return bytes;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Command line '");
            stringBuilder.append(DF);
            stringBuilder.append("' did not find free space in response ");
            stringBuilder.append("for path '");
            stringBuilder.append(path);
            stringBuilder.append("'- check path is valid");
            throw new IOException(stringBuilder.toString());
        } catch (NumberFormatException ex) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Command line '");
            stringBuilder2.append(DF);
            stringBuilder2.append("' did not return numeric data as expected ");
            stringBuilder2.append("for path '");
            stringBuilder2.append(path);
            stringBuilder2.append("'- check path is valid");
            throw new IOException(stringBuilder2.toString(), ex);
        }
    }

    Process openProcess(String[] cmdAttribs) throws IOException {
        return Runtime.getRuntime().exec(cmdAttribs);
    }
}

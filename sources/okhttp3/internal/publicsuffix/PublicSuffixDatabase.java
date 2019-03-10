package okhttp3.internal.publicsuffix;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.internal.Util;
import okhttp3.internal.platform.Platform;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;
import org.objenesis.instantiator.util.ClassDefinitionUtils;

public final class PublicSuffixDatabase {
    private static final String[] EMPTY_RULE = new String[0];
    private static final byte EXCEPTION_MARKER = (byte) 33;
    private static final String[] PREVAILING_RULE = new String[]{"*"};
    public static final String PUBLIC_SUFFIX_RESOURCE = "publicsuffixes.gz";
    private static final byte[] WILDCARD_LABEL = new byte[]{ClassDefinitionUtils.OPS_aload_0};
    private static final PublicSuffixDatabase instance = new PublicSuffixDatabase();
    private final AtomicBoolean listRead = new AtomicBoolean(false);
    private byte[] publicSuffixExceptionListBytes;
    private byte[] publicSuffixListBytes;
    private final CountDownLatch readCompleteLatch = new CountDownLatch(1);

    private java.lang.String[] findMatchingRule(java.lang.String[] r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:74:0x00d3 in {4, 8, 9, 18, 24, 25, 33, 34, 35, 36, 44, 45, 46, 47, 50, 54, 57, 58, 61, 62, 65, 66, 67, 70, 73} preds:[]
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
        r0 = r8.listRead;
        r0 = r0.get();
        r1 = 1;
        if (r0 != 0) goto L_0x0016;
    L_0x0009:
        r0 = r8.listRead;
        r2 = 0;
        r0 = r0.compareAndSet(r2, r1);
        if (r0 == 0) goto L_0x0016;
    L_0x0012:
        r8.readTheListUninterruptibly();
        goto L_0x001e;
        r0 = r8.readCompleteLatch;	 Catch:{ InterruptedException -> 0x001d }
        r0.await();	 Catch:{ InterruptedException -> 0x001d }
        goto L_0x001e;
    L_0x001d:
        r0 = move-exception;
    L_0x001e:
        monitor-enter(r8);
        r0 = r8.publicSuffixListBytes;	 Catch:{ all -> 0x00d0 }
        if (r0 == 0) goto L_0x00c8;	 Catch:{ all -> 0x00d0 }
    L_0x0023:
        monitor-exit(r8);	 Catch:{ all -> 0x00d0 }
        r0 = r9.length;
        r0 = new byte[r0][];
        r2 = 0;
    L_0x0028:
        r3 = r9.length;
        if (r2 >= r3) goto L_0x0038;
    L_0x002b:
        r3 = r9[r2];
        r4 = okhttp3.internal.Util.UTF_8;
        r3 = r3.getBytes(r4);
        r0[r2] = r3;
        r2 = r2 + 1;
        goto L_0x0028;
    L_0x0038:
        r2 = 0;
        r3 = 0;
    L_0x003a:
        r4 = r0.length;
        if (r3 >= r4) goto L_0x004b;
    L_0x003d:
        r4 = r8.publicSuffixListBytes;
        r4 = binarySearchBytes(r4, r0, r3);
        if (r4 == 0) goto L_0x0047;
    L_0x0045:
        r2 = r4;
        goto L_0x004b;
        r3 = r3 + 1;
        goto L_0x003a;
    L_0x004b:
        r3 = 0;
        r4 = r0.length;
        if (r4 <= r1) goto L_0x006d;
    L_0x004f:
        r4 = r0.clone();
        r4 = (byte[][]) r4;
        r5 = 0;
    L_0x0056:
        r6 = r4.length;
        r6 = r6 - r1;
        if (r5 >= r6) goto L_0x006c;
    L_0x005a:
        r6 = WILDCARD_LABEL;
        r4[r5] = r6;
        r6 = r8.publicSuffixListBytes;
        r6 = binarySearchBytes(r6, r4, r5);
        if (r6 == 0) goto L_0x0068;
    L_0x0066:
        r3 = r6;
        goto L_0x006e;
        r5 = r5 + 1;
        goto L_0x0056;
    L_0x006c:
        goto L_0x006e;
    L_0x006e:
        r4 = 0;
        if (r3 == 0) goto L_0x0085;
    L_0x0071:
        r5 = 0;
    L_0x0072:
        r6 = r0.length;
        r6 = r6 - r1;
        if (r5 >= r6) goto L_0x0084;
    L_0x0076:
        r6 = r8.publicSuffixExceptionListBytes;
        r6 = binarySearchBytes(r6, r0, r5);
        if (r6 == 0) goto L_0x0080;
    L_0x007e:
        r4 = r6;
        goto L_0x0086;
        r5 = r5 + 1;
        goto L_0x0072;
    L_0x0084:
        goto L_0x0086;
    L_0x0086:
        if (r4 == 0) goto L_0x00a0;
    L_0x0088:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r5 = "!";
        r1.append(r5);
        r1.append(r4);
        r1 = r1.toString();
        r4 = "\\.";
        r4 = r1.split(r4);
        return r4;
    L_0x00a0:
        if (r2 != 0) goto L_0x00a7;
    L_0x00a2:
        if (r3 != 0) goto L_0x00a7;
    L_0x00a4:
        r1 = PREVAILING_RULE;
        return r1;
        if (r2 == 0) goto L_0x00b1;
    L_0x00aa:
        r1 = "\\.";
        r1 = r2.split(r1);
        goto L_0x00b3;
    L_0x00b1:
        r1 = EMPTY_RULE;
        if (r3 == 0) goto L_0x00bd;
    L_0x00b6:
        r5 = "\\.";
        r5 = r3.split(r5);
        goto L_0x00bf;
    L_0x00bd:
        r5 = EMPTY_RULE;
        r6 = r1.length;
        r7 = r5.length;
        if (r6 <= r7) goto L_0x00c6;
    L_0x00c4:
        r6 = r1;
        goto L_0x00c7;
    L_0x00c6:
        r6 = r5;
    L_0x00c7:
        return r6;
    L_0x00c8:
        r0 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x00d0 }
        r1 = "Unable to load publicsuffixes.gz resource from the classpath.";	 Catch:{ all -> 0x00d0 }
        r0.<init>(r1);	 Catch:{ all -> 0x00d0 }
        throw r0;	 Catch:{ all -> 0x00d0 }
    L_0x00d0:
        r0 = move-exception;	 Catch:{ all -> 0x00d0 }
        monitor-exit(r8);	 Catch:{ all -> 0x00d0 }
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.publicsuffix.PublicSuffixDatabase.findMatchingRule(java.lang.String[]):java.lang.String[]");
    }

    public java.lang.String getEffectiveTldPlusOne(java.lang.String r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x0065 in {6, 9, 10, 14, 16, 18} preds:[]
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
        if (r9 == 0) goto L_0x005d;
    L_0x0002:
        r0 = java.net.IDN.toUnicode(r9);
        r1 = "\\.";
        r1 = r0.split(r1);
        r2 = r8.findMatchingRule(r1);
        r3 = r1.length;
        r4 = r2.length;
        r5 = 33;
        r6 = 0;
        if (r3 != r4) goto L_0x0021;
    L_0x0017:
        r3 = r2[r6];
        r3 = r3.charAt(r6);
        if (r3 == r5) goto L_0x0021;
    L_0x001f:
        r3 = 0;
        return r3;
        r3 = r2[r6];
        r3 = r3.charAt(r6);
        if (r3 != r5) goto L_0x002e;
    L_0x002a:
        r3 = r1.length;
        r4 = r2.length;
        r3 = r3 - r4;
        goto L_0x0033;
    L_0x002e:
        r3 = r1.length;
        r4 = r2.length;
        r4 = r4 + 1;
        r3 = r3 - r4;
    L_0x0033:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "\\.";
        r5 = r9.split(r5);
        r6 = r3;
    L_0x003f:
        r7 = r5.length;
        if (r6 >= r7) goto L_0x004f;
    L_0x0042:
        r7 = r5[r6];
        r4.append(r7);
        r7 = 46;
        r4.append(r7);
        r6 = r6 + 1;
        goto L_0x003f;
    L_0x004f:
        r6 = r4.length();
        r6 = r6 + -1;
        r4.deleteCharAt(r6);
        r6 = r4.toString();
        return r6;
    L_0x005d:
        r0 = new java.lang.NullPointerException;
        r1 = "domain == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.publicsuffix.PublicSuffixDatabase.getEffectiveTldPlusOne(java.lang.String):java.lang.String");
    }

    public static PublicSuffixDatabase get() {
        return instance;
    }

    private static String binarySearchBytes(byte[] bytesToSearch, byte[][] labels, int labelIndex) {
        byte[] bArr = bytesToSearch;
        byte[][] bArr2 = labels;
        int low = 0;
        int high = bArr.length;
        while (low < high) {
            int byte0;
            int low2;
            int byte1;
            int mid = (low + high) / 2;
            while (mid > -1 && bArr[mid] != (byte) 10) {
                mid--;
            }
            mid++;
            int end = 1;
            while (bArr[mid + end] != (byte) 10) {
                end++;
            }
            int publicSuffixLength = (mid + end) - mid;
            int currentLabelIndex = labelIndex;
            int currentLabelByteIndex = 0;
            int publicSuffixByteIndex = 0;
            boolean expectDot = false;
            while (true) {
                int i;
                if (expectDot) {
                    byte0 = 46;
                    expectDot = false;
                } else {
                    byte0 = bArr2[currentLabelIndex][currentLabelByteIndex] & 255;
                }
                int compareResult = byte0 - (bArr[mid + publicSuffixByteIndex] & 255);
                if (compareResult != 0) {
                    break;
                }
                publicSuffixByteIndex++;
                currentLabelByteIndex++;
                if (publicSuffixByteIndex == publicSuffixLength) {
                    break;
                }
                if (bArr2[currentLabelIndex].length != currentLabelByteIndex) {
                    low2 = low;
                } else if (currentLabelIndex == bArr2.length - 1) {
                    break;
                } else {
                    low2 = low;
                    currentLabelIndex++;
                    expectDot = true;
                    currentLabelByteIndex = -1;
                }
                low = low2;
                if (compareResult < 0) {
                    high = mid - 1;
                } else if (compareResult <= 0) {
                    low = (mid + end) + 1;
                } else {
                    byte0 = publicSuffixLength - publicSuffixByteIndex;
                    byte1 = bArr2[currentLabelIndex].length - currentLabelByteIndex;
                    i = currentLabelIndex + 1;
                    while (true) {
                        low2 = low;
                        if (i < bArr2.length) {
                            break;
                        }
                        byte1 += bArr2[i].length;
                        i++;
                        low = low2;
                    }
                    if (byte1 < byte0) {
                        high = mid - 1;
                        low = low2;
                    } else if (byte1 > byte0) {
                        return new String(bArr, mid, publicSuffixLength, Util.UTF_8);
                    } else {
                        low = (mid + end) + 1;
                    }
                }
            }
            if (compareResult < 0) {
                high = mid - 1;
            } else if (compareResult <= 0) {
                byte0 = publicSuffixLength - publicSuffixByteIndex;
                byte1 = bArr2[currentLabelIndex].length - currentLabelByteIndex;
                i = currentLabelIndex + 1;
                while (true) {
                    low2 = low;
                    if (i < bArr2.length) {
                        break;
                    }
                    byte1 += bArr2[i].length;
                    i++;
                    low = low2;
                }
                if (byte1 < byte0) {
                    high = mid - 1;
                    low = low2;
                } else if (byte1 > byte0) {
                    return new String(bArr, mid, publicSuffixLength, Util.UTF_8);
                } else {
                    low = (mid + end) + 1;
                }
            } else {
                low = (mid + end) + 1;
            }
        }
        return null;
    }

    private void readTheListUninterruptibly() {
        boolean interrupted = false;
        while (true) {
            try {
                readTheList();
                break;
            } catch (InterruptedIOException e) {
                interrupted = true;
            } catch (IOException e2) {
                Platform.get().log(5, "Failed to read public suffix list", e2);
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
                return;
            } catch (Throwable th) {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private void readTheList() throws IOException {
        InputStream resource = PublicSuffixDatabase.class.getResourceAsStream(PUBLIC_SUFFIX_RESOURCE);
        if (resource != null) {
            BufferedSource bufferedSource = Okio.buffer(new GzipSource(Okio.source(resource)));
            try {
                byte[] publicSuffixListBytes = new byte[bufferedSource.readInt()];
                bufferedSource.readFully(publicSuffixListBytes);
                byte[] publicSuffixExceptionListBytes = new byte[bufferedSource.readInt()];
                bufferedSource.readFully(publicSuffixExceptionListBytes);
                synchronized (this) {
                    this.publicSuffixListBytes = publicSuffixListBytes;
                    this.publicSuffixExceptionListBytes = publicSuffixExceptionListBytes;
                }
                this.readCompleteLatch.countDown();
            } finally {
                Util.closeQuietly(bufferedSource);
            }
        }
    }

    void setListBytes(byte[] publicSuffixListBytes, byte[] publicSuffixExceptionListBytes) {
        this.publicSuffixListBytes = publicSuffixListBytes;
        this.publicSuffixExceptionListBytes = publicSuffixExceptionListBytes;
        this.listRead.set(true);
        this.readCompleteLatch.countDown();
    }
}

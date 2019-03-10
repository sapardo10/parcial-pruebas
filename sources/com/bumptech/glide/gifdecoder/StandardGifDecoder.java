package com.bumptech.glide.gifdecoder;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class StandardGifDecoder implements GifDecoder {
    private static final int BYTES_PER_INTEGER = 4;
    @ColorInt
    private static final int COLOR_TRANSPARENT_BLACK = 0;
    private static final int INITIAL_FRAME_POINTER = -1;
    private static final int MASK_INT_LOWEST_BYTE = 255;
    private static final int MAX_STACK_SIZE = 4096;
    private static final int NULL_CODE = -1;
    private static final String TAG = StandardGifDecoder.class.getSimpleName();
    @ColorInt
    private int[] act;
    @NonNull
    private Config bitmapConfig;
    private final BitmapProvider bitmapProvider;
    private byte[] block;
    private int downsampledHeight;
    private int downsampledWidth;
    private int framePointer;
    private GifHeader header;
    @Nullable
    private Boolean isFirstFrameTransparent;
    private byte[] mainPixels;
    @ColorInt
    private int[] mainScratch;
    private GifHeaderParser parser;
    @ColorInt
    private final int[] pct;
    private byte[] pixelStack;
    private short[] prefix;
    private Bitmap previousImage;
    private ByteBuffer rawData;
    private int sampleSize;
    private boolean savePrevious;
    private int status;
    private byte[] suffix;

    public synchronized void setData(@android.support.annotation.NonNull com.bumptech.glide.gifdecoder.GifHeader r5, @android.support.annotation.NonNull java.nio.ByteBuffer r6, int r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:21:0x0087 in {8, 9, 10, 13, 18, 20} preds:[]
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
        r4 = this;
        monitor-enter(r4);
        if (r7 <= 0) goto L_0x006e;
    L_0x0003:
        r0 = java.lang.Integer.highestOneBit(r7);	 Catch:{ all -> 0x006c }
        r7 = r0;	 Catch:{ all -> 0x006c }
        r0 = 0;	 Catch:{ all -> 0x006c }
        r4.status = r0;	 Catch:{ all -> 0x006c }
        r4.header = r5;	 Catch:{ all -> 0x006c }
        r1 = -1;	 Catch:{ all -> 0x006c }
        r4.framePointer = r1;	 Catch:{ all -> 0x006c }
        r1 = r6.asReadOnlyBuffer();	 Catch:{ all -> 0x006c }
        r4.rawData = r1;	 Catch:{ all -> 0x006c }
        r1 = r4.rawData;	 Catch:{ all -> 0x006c }
        r1.position(r0);	 Catch:{ all -> 0x006c }
        r1 = r4.rawData;	 Catch:{ all -> 0x006c }
        r2 = java.nio.ByteOrder.LITTLE_ENDIAN;	 Catch:{ all -> 0x006c }
        r1.order(r2);	 Catch:{ all -> 0x006c }
        r4.savePrevious = r0;	 Catch:{ all -> 0x006c }
        r0 = r5.frames;	 Catch:{ all -> 0x006c }
        r0 = r0.iterator();	 Catch:{ all -> 0x006c }
    L_0x002a:
        r1 = r0.hasNext();	 Catch:{ all -> 0x006c }
        if (r1 == 0) goto L_0x0041;	 Catch:{ all -> 0x006c }
    L_0x0030:
        r1 = r0.next();	 Catch:{ all -> 0x006c }
        r1 = (com.bumptech.glide.gifdecoder.GifFrame) r1;	 Catch:{ all -> 0x006c }
        r2 = r1.dispose;	 Catch:{ all -> 0x006c }
        r3 = 3;	 Catch:{ all -> 0x006c }
        if (r2 != r3) goto L_0x003f;	 Catch:{ all -> 0x006c }
    L_0x003b:
        r0 = 1;	 Catch:{ all -> 0x006c }
        r4.savePrevious = r0;	 Catch:{ all -> 0x006c }
        goto L_0x0042;	 Catch:{ all -> 0x006c }
        goto L_0x002a;	 Catch:{ all -> 0x006c }
    L_0x0042:
        r4.sampleSize = r7;	 Catch:{ all -> 0x006c }
        r0 = r5.width;	 Catch:{ all -> 0x006c }
        r0 = r0 / r7;	 Catch:{ all -> 0x006c }
        r4.downsampledWidth = r0;	 Catch:{ all -> 0x006c }
        r0 = r5.height;	 Catch:{ all -> 0x006c }
        r0 = r0 / r7;	 Catch:{ all -> 0x006c }
        r4.downsampledHeight = r0;	 Catch:{ all -> 0x006c }
        r0 = r4.bitmapProvider;	 Catch:{ all -> 0x006c }
        r1 = r5.width;	 Catch:{ all -> 0x006c }
        r2 = r5.height;	 Catch:{ all -> 0x006c }
        r1 = r1 * r2;	 Catch:{ all -> 0x006c }
        r0 = r0.obtainByteArray(r1);	 Catch:{ all -> 0x006c }
        r4.mainPixels = r0;	 Catch:{ all -> 0x006c }
        r0 = r4.bitmapProvider;	 Catch:{ all -> 0x006c }
        r1 = r4.downsampledWidth;	 Catch:{ all -> 0x006c }
        r2 = r4.downsampledHeight;	 Catch:{ all -> 0x006c }
        r1 = r1 * r2;	 Catch:{ all -> 0x006c }
        r0 = r0.obtainIntArray(r1);	 Catch:{ all -> 0x006c }
        r4.mainScratch = r0;	 Catch:{ all -> 0x006c }
        monitor-exit(r4);
        return;
    L_0x006c:
        r5 = move-exception;
        goto L_0x0085;
    L_0x006e:
        r0 = new java.lang.IllegalArgumentException;	 Catch:{ all -> 0x006c }
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x006c }
        r1.<init>();	 Catch:{ all -> 0x006c }
        r2 = "Sample size must be >=0, not: ";	 Catch:{ all -> 0x006c }
        r1.append(r2);	 Catch:{ all -> 0x006c }
        r1.append(r7);	 Catch:{ all -> 0x006c }
        r1 = r1.toString();	 Catch:{ all -> 0x006c }
        r0.<init>(r1);	 Catch:{ all -> 0x006c }
        throw r0;	 Catch:{ all -> 0x006c }
    L_0x0085:
        monitor-exit(r4);
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.gifdecoder.StandardGifDecoder.setData(com.bumptech.glide.gifdecoder.GifHeader, java.nio.ByteBuffer, int):void");
    }

    public StandardGifDecoder(@NonNull BitmapProvider provider, GifHeader gifHeader, ByteBuffer rawData) {
        this(provider, gifHeader, rawData, 1);
    }

    public StandardGifDecoder(@NonNull BitmapProvider provider, GifHeader gifHeader, ByteBuffer rawData, int sampleSize) {
        this(provider);
        setData(gifHeader, rawData, sampleSize);
    }

    public StandardGifDecoder(@NonNull BitmapProvider provider) {
        this.pct = new int[256];
        this.bitmapConfig = Config.ARGB_8888;
        this.bitmapProvider = provider;
        this.header = new GifHeader();
    }

    public int getWidth() {
        return this.header.width;
    }

    public int getHeight() {
        return this.header.height;
    }

    @NonNull
    public ByteBuffer getData() {
        return this.rawData;
    }

    public int getStatus() {
        return this.status;
    }

    public void advance() {
        this.framePointer = (this.framePointer + 1) % this.header.frameCount;
    }

    public int getDelay(int n) {
        if (n < 0 || n >= this.header.frameCount) {
            return -1;
        }
        return ((GifFrame) this.header.frames.get(n)).delay;
    }

    public int getNextDelay() {
        if (this.header.frameCount > 0) {
            int i = this.framePointer;
            if (i >= 0) {
                return getDelay(i);
            }
        }
        return 0;
    }

    public int getFrameCount() {
        return this.header.frameCount;
    }

    public int getCurrentFrameIndex() {
        return this.framePointer;
    }

    public void resetFrameIndex() {
        this.framePointer = -1;
    }

    @Deprecated
    public int getLoopCount() {
        if (this.header.loopCount == -1) {
            return 1;
        }
        return this.header.loopCount;
    }

    public int getNetscapeLoopCount() {
        return this.header.loopCount;
    }

    public int getTotalIterationCount() {
        if (this.header.loopCount == -1) {
            return 1;
        }
        if (this.header.loopCount == 0) {
            return 0;
        }
        return this.header.loopCount + 1;
    }

    public int getByteSize() {
        return (this.rawData.limit() + this.mainPixels.length) + (this.mainScratch.length * 4);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.support.annotation.Nullable
    public synchronized android.graphics.Bitmap getNextFrame() {
        /*
        r8 = this;
        monitor-enter(r8);
        r0 = r8.header;	 Catch:{ all -> 0x00f9 }
        r0 = r0.frameCount;	 Catch:{ all -> 0x00f9 }
        r1 = 3;
        r2 = 1;
        if (r0 <= 0) goto L_0x000f;
    L_0x0009:
        r0 = r8.framePointer;	 Catch:{ all -> 0x00f9 }
        if (r0 >= 0) goto L_0x000e;
    L_0x000d:
        goto L_0x000f;
    L_0x000e:
        goto L_0x003f;
    L_0x000f:
        r0 = TAG;	 Catch:{ all -> 0x00f9 }
        r0 = android.util.Log.isLoggable(r0, r1);	 Catch:{ all -> 0x00f9 }
        if (r0 == 0) goto L_0x003c;
    L_0x0017:
        r0 = TAG;	 Catch:{ all -> 0x00f9 }
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00f9 }
        r3.<init>();	 Catch:{ all -> 0x00f9 }
        r4 = "Unable to decode frame, frameCount=";
        r3.append(r4);	 Catch:{ all -> 0x00f9 }
        r4 = r8.header;	 Catch:{ all -> 0x00f9 }
        r4 = r4.frameCount;	 Catch:{ all -> 0x00f9 }
        r3.append(r4);	 Catch:{ all -> 0x00f9 }
        r4 = ", framePointer=";
        r3.append(r4);	 Catch:{ all -> 0x00f9 }
        r4 = r8.framePointer;	 Catch:{ all -> 0x00f9 }
        r3.append(r4);	 Catch:{ all -> 0x00f9 }
        r3 = r3.toString();	 Catch:{ all -> 0x00f9 }
        android.util.Log.d(r0, r3);	 Catch:{ all -> 0x00f9 }
        goto L_0x003d;
    L_0x003d:
        r8.status = r2;	 Catch:{ all -> 0x00f9 }
    L_0x003f:
        r0 = r8.status;	 Catch:{ all -> 0x00f9 }
        r3 = 0;
        if (r0 == r2) goto L_0x00d4;
    L_0x0044:
        r0 = r8.status;	 Catch:{ all -> 0x00f9 }
        r4 = 2;
        if (r0 != r4) goto L_0x004b;
    L_0x0049:
        goto L_0x00d4;
    L_0x004b:
        r0 = 0;
        r8.status = r0;	 Catch:{ all -> 0x00f9 }
        r4 = r8.block;	 Catch:{ all -> 0x00f9 }
        if (r4 != 0) goto L_0x005d;
    L_0x0052:
        r4 = r8.bitmapProvider;	 Catch:{ all -> 0x00f9 }
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r4 = r4.obtainByteArray(r5);	 Catch:{ all -> 0x00f9 }
        r8.block = r4;	 Catch:{ all -> 0x00f9 }
        goto L_0x005e;
    L_0x005e:
        r4 = r8.header;	 Catch:{ all -> 0x00f9 }
        r4 = r4.frames;	 Catch:{ all -> 0x00f9 }
        r5 = r8.framePointer;	 Catch:{ all -> 0x00f9 }
        r4 = r4.get(r5);	 Catch:{ all -> 0x00f9 }
        r4 = (com.bumptech.glide.gifdecoder.GifFrame) r4;	 Catch:{ all -> 0x00f9 }
        r5 = 0;
        r6 = r8.framePointer;	 Catch:{ all -> 0x00f9 }
        r6 = r6 - r2;
        if (r6 < 0) goto L_0x007c;
    L_0x0070:
        r7 = r8.header;	 Catch:{ all -> 0x00f9 }
        r7 = r7.frames;	 Catch:{ all -> 0x00f9 }
        r7 = r7.get(r6);	 Catch:{ all -> 0x00f9 }
        r7 = (com.bumptech.glide.gifdecoder.GifFrame) r7;	 Catch:{ all -> 0x00f9 }
        r5 = r7;
        goto L_0x007d;
    L_0x007d:
        r7 = r4.lct;	 Catch:{ all -> 0x00f9 }
        if (r7 == 0) goto L_0x0084;
    L_0x0081:
        r7 = r4.lct;	 Catch:{ all -> 0x00f9 }
        goto L_0x0088;
    L_0x0084:
        r7 = r8.header;	 Catch:{ all -> 0x00f9 }
        r7 = r7.gct;	 Catch:{ all -> 0x00f9 }
    L_0x0088:
        r8.act = r7;	 Catch:{ all -> 0x00f9 }
        r7 = r8.act;	 Catch:{ all -> 0x00f9 }
        if (r7 != 0) goto L_0x00b4;
    L_0x008e:
        r0 = TAG;	 Catch:{ all -> 0x00f9 }
        r0 = android.util.Log.isLoggable(r0, r1);	 Catch:{ all -> 0x00f9 }
        if (r0 == 0) goto L_0x00af;
    L_0x0096:
        r0 = TAG;	 Catch:{ all -> 0x00f9 }
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00f9 }
        r1.<init>();	 Catch:{ all -> 0x00f9 }
        r7 = "No valid color table found for frame #";
        r1.append(r7);	 Catch:{ all -> 0x00f9 }
        r7 = r8.framePointer;	 Catch:{ all -> 0x00f9 }
        r1.append(r7);	 Catch:{ all -> 0x00f9 }
        r1 = r1.toString();	 Catch:{ all -> 0x00f9 }
        android.util.Log.d(r0, r1);	 Catch:{ all -> 0x00f9 }
        goto L_0x00b0;
    L_0x00b0:
        r8.status = r2;	 Catch:{ all -> 0x00f9 }
        monitor-exit(r8);
        return r3;
    L_0x00b4:
        r1 = r4.transparency;	 Catch:{ all -> 0x00f9 }
        if (r1 == 0) goto L_0x00cd;
    L_0x00b8:
        r1 = r8.act;	 Catch:{ all -> 0x00f9 }
        r2 = r8.pct;	 Catch:{ all -> 0x00f9 }
        r3 = r8.act;	 Catch:{ all -> 0x00f9 }
        r3 = r3.length;	 Catch:{ all -> 0x00f9 }
        java.lang.System.arraycopy(r1, r0, r2, r0, r3);	 Catch:{ all -> 0x00f9 }
        r1 = r8.pct;	 Catch:{ all -> 0x00f9 }
        r8.act = r1;	 Catch:{ all -> 0x00f9 }
        r1 = r8.act;	 Catch:{ all -> 0x00f9 }
        r2 = r4.transIndex;	 Catch:{ all -> 0x00f9 }
        r1[r2] = r0;	 Catch:{ all -> 0x00f9 }
        goto L_0x00ce;
    L_0x00ce:
        r0 = r8.setPixels(r4, r5);	 Catch:{ all -> 0x00f9 }
        monitor-exit(r8);
        return r0;
        r0 = TAG;	 Catch:{ all -> 0x00f9 }
        r0 = android.util.Log.isLoggable(r0, r1);	 Catch:{ all -> 0x00f9 }
        if (r0 == 0) goto L_0x00f6;
    L_0x00dd:
        r0 = TAG;	 Catch:{ all -> 0x00f9 }
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00f9 }
        r1.<init>();	 Catch:{ all -> 0x00f9 }
        r2 = "Unable to decode frame, status=";
        r1.append(r2);	 Catch:{ all -> 0x00f9 }
        r2 = r8.status;	 Catch:{ all -> 0x00f9 }
        r1.append(r2);	 Catch:{ all -> 0x00f9 }
        r1 = r1.toString();	 Catch:{ all -> 0x00f9 }
        android.util.Log.d(r0, r1);	 Catch:{ all -> 0x00f9 }
        goto L_0x00f7;
    L_0x00f7:
        monitor-exit(r8);
        return r3;
    L_0x00f9:
        r0 = move-exception;
        monitor-exit(r8);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.gifdecoder.StandardGifDecoder.getNextFrame():android.graphics.Bitmap");
    }

    public int read(@Nullable InputStream is, int contentLength) {
        if (is != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream(contentLength > 0 ? contentLength + 4096 : 16384);
                byte[] data = new byte[16384];
                while (true) {
                    int read = is.read(data, 0, data.length);
                    int nRead = read;
                    if (read == -1) {
                        break;
                    }
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                read(buffer.toByteArray());
            } catch (IOException e) {
                Log.w(TAG, "Error reading data from stream", e);
            }
        } else {
            this.status = 2;
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e2) {
                Log.w(TAG, "Error closing stream", e2);
            }
        }
        return this.status;
    }

    public void clear() {
        this.header = null;
        byte[] bArr = this.mainPixels;
        if (bArr != null) {
            this.bitmapProvider.release(bArr);
        }
        int[] iArr = this.mainScratch;
        if (iArr != null) {
            this.bitmapProvider.release(iArr);
        }
        Bitmap bitmap = this.previousImage;
        if (bitmap != null) {
            this.bitmapProvider.release(bitmap);
        }
        this.previousImage = null;
        this.rawData = null;
        this.isFirstFrameTransparent = null;
        byte[] bArr2 = this.block;
        if (bArr2 != null) {
            this.bitmapProvider.release(bArr2);
        }
    }

    public synchronized void setData(@NonNull GifHeader header, @NonNull byte[] data) {
        setData(header, ByteBuffer.wrap(data));
    }

    public synchronized void setData(@NonNull GifHeader header, @NonNull ByteBuffer buffer) {
        setData(header, buffer, 1);
    }

    @NonNull
    private GifHeaderParser getHeaderParser() {
        if (this.parser == null) {
            this.parser = new GifHeaderParser();
        }
        return this.parser;
    }

    public synchronized int read(@Nullable byte[] data) {
        this.header = getHeaderParser().setData(data).parseHeader();
        if (data != null) {
            setData(this.header, data);
        }
        return this.status;
    }

    public void setDefaultBitmapConfig(@NonNull Config config) {
        if (config != Config.ARGB_8888) {
            if (config != Config.RGB_565) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unsupported format: ");
                stringBuilder.append(config);
                stringBuilder.append(", must be one of ");
                stringBuilder.append(Config.ARGB_8888);
                stringBuilder.append(" or ");
                stringBuilder.append(Config.RGB_565);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
        this.bitmapConfig = config;
    }

    private Bitmap setPixels(GifFrame currentFrame, GifFrame previousFrame) {
        int topLeft;
        Bitmap bitmap;
        Bitmap result;
        int[] dest = this.mainScratch;
        if (previousFrame == null) {
            Bitmap bitmap2 = this.previousImage;
            if (bitmap2 != null) {
                this.bitmapProvider.release(bitmap2);
            }
            this.previousImage = null;
            Arrays.fill(dest, 0);
        }
        if (previousFrame != null && previousFrame.dispose == 3 && this.previousImage == null) {
            Arrays.fill(dest, 0);
        }
        if (previousFrame != null && previousFrame.dispose > 0) {
            if (previousFrame.dispose == 2) {
                int c = 0;
                if (!currentFrame.transparency) {
                    c = this.header.bgColor;
                    if (currentFrame.lct != null && this.header.bgIndex == currentFrame.transIndex) {
                        c = 0;
                    }
                } else if (this.framePointer == 0) {
                    this.isFirstFrameTransparent = Boolean.valueOf(true);
                }
                int downsampledIH = previousFrame.ih / this.sampleSize;
                int downsampledIY = previousFrame.iy / this.sampleSize;
                int downsampledIW = previousFrame.iw / this.sampleSize;
                int downsampledIX = previousFrame.ix / this.sampleSize;
                int i = this.downsampledWidth;
                topLeft = (downsampledIY * i) + downsampledIX;
                i = (i * downsampledIH) + topLeft;
                int left = topLeft;
                while (left < i) {
                    int right = left + downsampledIW;
                    for (int pointer = left; pointer < right; pointer++) {
                        dest[pointer] = c;
                    }
                    left += this.downsampledWidth;
                }
            } else if (previousFrame.dispose == 3) {
                bitmap = this.previousImage;
                if (bitmap != null) {
                    topLeft = this.downsampledWidth;
                    bitmap.getPixels(dest, 0, topLeft, 0, 0, topLeft, this.downsampledHeight);
                }
            }
        }
        decodeBitmapData(currentFrame);
        if (!currentFrame.interlace) {
            if (this.sampleSize == 1) {
                copyIntoScratchFast(currentFrame);
                if (!this.savePrevious && (currentFrame.dispose == 0 || currentFrame.dispose == 1)) {
                    if (this.previousImage == null) {
                        this.previousImage = getNextBitmap();
                    }
                    bitmap = this.previousImage;
                    topLeft = this.downsampledWidth;
                    bitmap.setPixels(dest, 0, topLeft, 0, 0, topLeft, this.downsampledHeight);
                }
                result = getNextBitmap();
                topLeft = this.downsampledWidth;
                result.setPixels(dest, 0, topLeft, 0, 0, topLeft, this.downsampledHeight);
                return result;
            }
        }
        copyCopyIntoScratchRobust(currentFrame);
        if (!this.savePrevious) {
        }
        result = getNextBitmap();
        topLeft = this.downsampledWidth;
        result.setPixels(dest, 0, topLeft, 0, 0, topLeft, this.downsampledHeight);
        return result;
    }

    private void copyIntoScratchFast(GifFrame currentFrame) {
        int downsampledIY;
        GifFrame gifFrame = currentFrame;
        int[] dest = this.mainScratch;
        int downsampledIH = gifFrame.ih;
        int downsampledIY2 = gifFrame.iy;
        int downsampledIW = gifFrame.iw;
        int downsampledIX = gifFrame.ix;
        boolean isFirstFrame = this.framePointer == 0;
        int width = r0.downsampledWidth;
        byte[] mainPixels = r0.mainPixels;
        int[] act = r0.act;
        byte transparentColorIndex = (byte) -1;
        int i = 0;
        while (i < downsampledIH) {
            int k = (i + downsampledIY2) * width;
            int dx = k + downsampledIX;
            int dlim = dx + downsampledIW;
            if (k + width < dlim) {
                dlim = k + width;
            }
            byte transparentColorIndex2 = transparentColorIndex;
            int sx = gifFrame.iw * i;
            int dx2 = dx;
            while (dx2 < dlim) {
                dx = downsampledIH;
                byte downsampledIH2 = mainPixels[sx];
                downsampledIY = downsampledIY2;
                byte currentColorIndex = downsampledIH2 & 255;
                if (currentColorIndex != transparentColorIndex2) {
                    int color = act[currentColorIndex];
                    if (color != 0) {
                        dest[dx2] = color;
                    } else {
                        transparentColorIndex2 = downsampledIH2;
                    }
                }
                sx++;
                dx2++;
                downsampledIH = dx;
                downsampledIY2 = downsampledIY;
            }
            downsampledIY = downsampledIY2;
            i++;
            transparentColorIndex = transparentColorIndex2;
            gifFrame = currentFrame;
        }
        downsampledIY = downsampledIY2;
        boolean z = r0.isFirstFrameTransparent == null && isFirstFrame && transparentColorIndex != (byte) -1;
        r0.isFirstFrameTransparent = Boolean.valueOf(z);
    }

    private void copyCopyIntoScratchRobust(GifFrame currentFrame) {
        Boolean isFirstFrameTransparent;
        int downsampledIY;
        int downsampledIW;
        int downsampledIX;
        GifFrame gifFrame = currentFrame;
        int[] dest = this.mainScratch;
        int downsampledIH = gifFrame.ih / this.sampleSize;
        int downsampledIY2 = gifFrame.iy / this.sampleSize;
        int downsampledIW2 = gifFrame.iw / this.sampleSize;
        int downsampledIX2 = gifFrame.ix / this.sampleSize;
        int iline = 0;
        boolean isFirstFrame = this.framePointer == 0;
        int sampleSize = r0.sampleSize;
        int downsampledWidth = r0.downsampledWidth;
        int downsampledHeight = r0.downsampledHeight;
        byte[] mainPixels = r0.mainPixels;
        int[] act = r0.act;
        int pass = 1;
        Boolean isFirstFrameTransparent2 = r0.isFirstFrameTransparent;
        int i = 0;
        int inc = 8;
        while (i < downsampledIH) {
            int line = i;
            isFirstFrameTransparent = isFirstFrameTransparent2;
            if (gifFrame.interlace) {
                if (iline >= downsampledIH) {
                    pass++;
                    switch (pass) {
                        case 2:
                            iline = 4;
                            break;
                        case 3:
                            iline = 2;
                            inc = 4;
                            break;
                        case 4:
                            iline = 1;
                            inc = 2;
                            break;
                        default:
                            break;
                    }
                }
                line = iline;
                iline += inc;
            }
            int line2 = line + downsampledIY2;
            line = downsampledIH;
            boolean downsampledIH2 = sampleSize == 1;
            boolean isNotDownsampling;
            if (line2 < downsampledHeight) {
                int k = line2 * downsampledWidth;
                int dx = k + downsampledIX2;
                downsampledIY = downsampledIY2;
                downsampledIY2 = dx + downsampledIW2;
                downsampledIW = downsampledIW2;
                if (k + downsampledWidth < downsampledIY2) {
                    downsampledIY2 = k + downsampledWidth;
                }
                downsampledIX = downsampledIX2;
                downsampledIW2 = (i * sampleSize) * gifFrame.iw;
                if (downsampledIH2) {
                    downsampledIX2 = downsampledIW2;
                    downsampledIW2 = dx;
                    while (downsampledIW2 < downsampledIY2) {
                        isNotDownsampling = downsampledIH2;
                        dx = act[mainPixels[downsampledIX2] & 255];
                        if (dx != 0) {
                            dest[downsampledIW2] = dx;
                        } else if (isFirstFrame && isFirstFrameTransparent == null) {
                            isFirstFrameTransparent = Boolean.valueOf(true);
                        }
                        downsampledIX2 += sampleSize;
                        downsampledIW2++;
                        downsampledIH2 = isNotDownsampling;
                    }
                    isNotDownsampling = downsampledIH2;
                    isFirstFrameTransparent2 = isFirstFrameTransparent;
                } else {
                    isNotDownsampling = downsampledIH2;
                    downsampledIH = ((downsampledIY2 - dx) * sampleSize) + downsampledIW2;
                    downsampledIX2 = downsampledIW2;
                    downsampledIW2 = dx;
                    while (downsampledIW2 < downsampledIY2) {
                        dx = downsampledIY2;
                        downsampledIY2 = averageColorsNear(downsampledIX2, downsampledIH, gifFrame.iw);
                        if (downsampledIY2 != 0) {
                            dest[downsampledIW2] = downsampledIY2;
                        } else if (isFirstFrame && isFirstFrameTransparent == null) {
                            isFirstFrameTransparent = Boolean.valueOf(true);
                        }
                        downsampledIX2 += sampleSize;
                        downsampledIW2++;
                        downsampledIY2 = dx;
                    }
                    dx = downsampledIY2;
                    isFirstFrameTransparent2 = isFirstFrameTransparent;
                }
            } else {
                isNotDownsampling = downsampledIH2;
                downsampledIY = downsampledIY2;
                downsampledIW = downsampledIW2;
                downsampledIX = downsampledIX2;
                isFirstFrameTransparent2 = isFirstFrameTransparent;
            }
            i++;
            downsampledIH = line;
            downsampledIY2 = downsampledIY;
            downsampledIW2 = downsampledIW;
            downsampledIX2 = downsampledIX;
        }
        downsampledIY = downsampledIY2;
        downsampledIW = downsampledIW2;
        downsampledIX = downsampledIX2;
        isFirstFrameTransparent = isFirstFrameTransparent2;
        if (r0.isFirstFrameTransparent == null) {
            boolean z;
            if (isFirstFrameTransparent == null) {
                z = false;
            } else {
                z = isFirstFrameTransparent.booleanValue();
            }
            r0.isFirstFrameTransparent = Boolean.valueOf(z);
        }
    }

    @ColorInt
    private int averageColorsNear(int positionInMainPixels, int maxPositionInMainPixels, int currentFrameIw) {
        int alphaSum = 0;
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        int totalAdded = 0;
        int i = positionInMainPixels;
        while (i < this.sampleSize + positionInMainPixels) {
            byte[] bArr = this.mainPixels;
            if (i >= bArr.length || i >= maxPositionInMainPixels) {
                break;
            }
            int currentColor = this.act[bArr[i] & 255];
            if (currentColor != 0) {
                alphaSum += (currentColor >> 24) & 255;
                redSum += (currentColor >> 16) & 255;
                greenSum += (currentColor >> 8) & 255;
                blueSum += currentColor & 255;
                totalAdded++;
            }
            i++;
        }
        i = positionInMainPixels + currentFrameIw;
        while (i < (positionInMainPixels + currentFrameIw) + this.sampleSize) {
            bArr = this.mainPixels;
            if (i >= bArr.length || i >= maxPositionInMainPixels) {
                break;
            }
            currentColor = this.act[bArr[i] & 255];
            if (currentColor != 0) {
                alphaSum += (currentColor >> 24) & 255;
                redSum += (currentColor >> 16) & 255;
                greenSum += (currentColor >> 8) & 255;
                blueSum += currentColor & 255;
                totalAdded++;
            }
            i++;
        }
        if (totalAdded == 0) {
            return 0;
        }
        return ((((alphaSum / totalAdded) << 24) | ((redSum / totalAdded) << 16)) | ((greenSum / totalAdded) << 8)) | (blueSum / totalAdded);
    }

    private void decodeBitmapData(GifFrame frame) {
        int npix;
        int i;
        short[] prefix;
        byte[] suffix;
        byte[] pixelStack;
        int dataSize;
        int clear;
        int endOfInformation;
        int available;
        int oldCode;
        int codeSize;
        int codeMask;
        int code;
        byte[] block;
        int bi;
        int top;
        int first;
        int count;
        int bits;
        int datum;
        int i2;
        int codeSize2;
        int codeMask2;
        int i3;
        int first2;
        int first3;
        StandardGifDecoder standardGifDecoder = this;
        GifFrame gifFrame = frame;
        if (gifFrame != null) {
            standardGifDecoder.rawData.position(gifFrame.bufferFrameStart);
        }
        if (gifFrame == null) {
            npix = standardGifDecoder.header.width;
            i = standardGifDecoder.header.height;
        } else {
            npix = gifFrame.iw;
            i = gifFrame.ih;
        }
        npix *= i;
        byte[] bArr = standardGifDecoder.mainPixels;
        if (bArr != null) {
            if (bArr.length >= npix) {
                bArr = standardGifDecoder.mainPixels;
                if (standardGifDecoder.prefix == null) {
                    standardGifDecoder.prefix = new short[4096];
                }
                prefix = standardGifDecoder.prefix;
                if (standardGifDecoder.suffix == null) {
                    standardGifDecoder.suffix = new byte[4096];
                }
                suffix = standardGifDecoder.suffix;
                if (standardGifDecoder.pixelStack == null) {
                    standardGifDecoder.pixelStack = new byte[FragmentTransaction.TRANSIT_FRAGMENT_OPEN];
                }
                pixelStack = standardGifDecoder.pixelStack;
                dataSize = readByte();
                clear = 1 << dataSize;
                endOfInformation = clear + 1;
                available = clear + 2;
                oldCode = -1;
                codeSize = dataSize + 1;
                codeMask = (1 << codeSize) - 1;
                code = 0;
                while (code < clear) {
                    prefix[code] = (short) 0;
                    suffix[code] = (byte) code;
                    code++;
                }
                block = standardGifDecoder.block;
                bi = 0;
                top = 0;
                first = 0;
                count = 0;
                bits = 0;
                datum = 0;
                i2 = available;
                available = code;
                code = (short) 0;
                codeSize2 = codeSize;
                codeSize = 0;
                codeMask2 = codeMask;
                codeMask = i2;
                while (code < npix) {
                    if (count == 0) {
                        count = readBlock();
                        if (count <= 0) {
                            standardGifDecoder.status = 3;
                            break;
                        }
                        bi = 0;
                    }
                    datum += (block[bi] & 255) << bits;
                    bi++;
                    i3 = -1;
                    count--;
                    first2 = first;
                    first = top;
                    top = code;
                    code = bits + 8;
                    bits = codeSize;
                    codeSize = oldCode;
                    oldCode = available;
                    available = codeSize2;
                    while (code >= available) {
                        oldCode = datum & codeMask2;
                        datum >>= available;
                        code -= available;
                        if (oldCode != clear) {
                            available = dataSize + 1;
                            codeMask2 = (1 << available) - 1;
                            codeMask = clear + 2;
                            codeSize = -1;
                        } else if (oldCode == endOfInformation) {
                            codeSize2 = available;
                            available = oldCode;
                            oldCode = codeSize;
                            codeSize = bits;
                            bits = code;
                            code = top;
                            top = first;
                            first = first2;
                            break;
                        } else if (codeSize != i3) {
                            bArr[bits] = suffix[oldCode];
                            bits++;
                            top++;
                            codeSize = oldCode;
                            first2 = oldCode;
                        } else {
                            codeSize2 = oldCode;
                            if (oldCode < codeMask) {
                                pixelStack[first] = (byte) first2;
                                first++;
                                oldCode = codeSize;
                            }
                            while (oldCode >= clear) {
                                pixelStack[first] = suffix[oldCode];
                                first++;
                                oldCode = prefix[oldCode];
                            }
                            first3 = suffix[oldCode] & 255;
                            bArr[bits] = (byte) first3;
                            bits++;
                            top++;
                            while (first > 0) {
                                first--;
                                bArr[bits] = pixelStack[first];
                                bits++;
                                top++;
                            }
                            if (codeMask < 4096) {
                                prefix[codeMask] = (short) codeSize;
                                suffix[codeMask] = (byte) first3;
                                codeMask++;
                                if ((codeMask & codeMask2) == 0) {
                                    if (codeMask < 4096) {
                                        available++;
                                        codeMask2 += codeMask;
                                    }
                                }
                            }
                            codeSize = codeSize2;
                            first2 = first3;
                            standardGifDecoder = this;
                            i3 = -1;
                        }
                    }
                    codeSize2 = available;
                    available = oldCode;
                    oldCode = codeSize;
                    codeSize = bits;
                    standardGifDecoder = this;
                    bits = code;
                    code = top;
                    top = first;
                    first = first2;
                }
                Arrays.fill(bArr, codeSize, npix, (byte) 0);
            }
        }
        standardGifDecoder.mainPixels = standardGifDecoder.bitmapProvider.obtainByteArray(npix);
        bArr = standardGifDecoder.mainPixels;
        if (standardGifDecoder.prefix == null) {
            standardGifDecoder.prefix = new short[4096];
        }
        prefix = standardGifDecoder.prefix;
        if (standardGifDecoder.suffix == null) {
            standardGifDecoder.suffix = new byte[4096];
        }
        suffix = standardGifDecoder.suffix;
        if (standardGifDecoder.pixelStack == null) {
            standardGifDecoder.pixelStack = new byte[FragmentTransaction.TRANSIT_FRAGMENT_OPEN];
        }
        pixelStack = standardGifDecoder.pixelStack;
        dataSize = readByte();
        clear = 1 << dataSize;
        endOfInformation = clear + 1;
        available = clear + 2;
        oldCode = -1;
        codeSize = dataSize + 1;
        codeMask = (1 << codeSize) - 1;
        code = 0;
        while (code < clear) {
            prefix[code] = (short) 0;
            suffix[code] = (byte) code;
            code++;
        }
        block = standardGifDecoder.block;
        bi = 0;
        top = 0;
        first = 0;
        count = 0;
        bits = 0;
        datum = 0;
        i2 = available;
        available = code;
        code = (short) 0;
        codeSize2 = codeSize;
        codeSize = 0;
        codeMask2 = codeMask;
        codeMask = i2;
        while (code < npix) {
            if (count == 0) {
                count = readBlock();
                if (count <= 0) {
                    standardGifDecoder.status = 3;
                    break;
                }
                bi = 0;
            }
            datum += (block[bi] & 255) << bits;
            bi++;
            i3 = -1;
            count--;
            first2 = first;
            first = top;
            top = code;
            code = bits + 8;
            bits = codeSize;
            codeSize = oldCode;
            oldCode = available;
            available = codeSize2;
            while (code >= available) {
                oldCode = datum & codeMask2;
                datum >>= available;
                code -= available;
                if (oldCode != clear) {
                    if (oldCode == endOfInformation) {
                        codeSize2 = available;
                        available = oldCode;
                        oldCode = codeSize;
                        codeSize = bits;
                        bits = code;
                        code = top;
                        top = first;
                        first = first2;
                        break;
                    } else if (codeSize != i3) {
                        codeSize2 = oldCode;
                        if (oldCode < codeMask) {
                        } else {
                            pixelStack[first] = (byte) first2;
                            first++;
                            oldCode = codeSize;
                        }
                        while (oldCode >= clear) {
                            pixelStack[first] = suffix[oldCode];
                            first++;
                            oldCode = prefix[oldCode];
                        }
                        first3 = suffix[oldCode] & 255;
                        bArr[bits] = (byte) first3;
                        bits++;
                        top++;
                        while (first > 0) {
                            first--;
                            bArr[bits] = pixelStack[first];
                            bits++;
                            top++;
                        }
                        if (codeMask < 4096) {
                            prefix[codeMask] = (short) codeSize;
                            suffix[codeMask] = (byte) first3;
                            codeMask++;
                            if ((codeMask & codeMask2) == 0) {
                                if (codeMask < 4096) {
                                    available++;
                                    codeMask2 += codeMask;
                                }
                            }
                        }
                        codeSize = codeSize2;
                        first2 = first3;
                        standardGifDecoder = this;
                        i3 = -1;
                    } else {
                        bArr[bits] = suffix[oldCode];
                        bits++;
                        top++;
                        codeSize = oldCode;
                        first2 = oldCode;
                    }
                } else {
                    available = dataSize + 1;
                    codeMask2 = (1 << available) - 1;
                    codeMask = clear + 2;
                    codeSize = -1;
                }
            }
            codeSize2 = available;
            available = oldCode;
            oldCode = codeSize;
            codeSize = bits;
            standardGifDecoder = this;
            bits = code;
            code = top;
            top = first;
            first = first2;
        }
        Arrays.fill(bArr, codeSize, npix, (byte) 0);
    }

    private int readByte() {
        return this.rawData.get() & 255;
    }

    private int readBlock() {
        int blockSize = readByte();
        if (blockSize <= 0) {
            return blockSize;
        }
        ByteBuffer byteBuffer = this.rawData;
        byteBuffer.get(this.block, 0, Math.min(blockSize, byteBuffer.remaining()));
        return blockSize;
    }

    private Bitmap getNextBitmap() {
        Config config;
        Bitmap result;
        Boolean bool = this.isFirstFrameTransparent;
        if (bool != null) {
            if (!bool.booleanValue()) {
                config = this.bitmapConfig;
                result = this.bitmapProvider.obtain(this.downsampledWidth, this.downsampledHeight, config);
                result.setHasAlpha(true);
                return result;
            }
        }
        config = Config.ARGB_8888;
        result = this.bitmapProvider.obtain(this.downsampledWidth, this.downsampledHeight, config);
        result.setHasAlpha(true);
        return result;
    }
}

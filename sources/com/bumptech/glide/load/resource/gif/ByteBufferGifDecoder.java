package com.bumptech.glide.load.resource.gif;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider;
import com.bumptech.glide.gifdecoder.GifHeader;
import com.bumptech.glide.gifdecoder.GifHeaderParser;
import com.bumptech.glide.gifdecoder.StandardGifDecoder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParser.ImageType;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.UnitTransformation;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Util;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Queue;

public class ByteBufferGifDecoder implements ResourceDecoder<ByteBuffer, GifDrawable> {
    private static final GifDecoderFactory GIF_DECODER_FACTORY = new GifDecoderFactory();
    private static final GifHeaderParserPool PARSER_POOL = new GifHeaderParserPool();
    private static final String TAG = "BufferGifDecoder";
    private final Context context;
    private final GifDecoderFactory gifDecoderFactory;
    private final GifHeaderParserPool parserPool;
    private final List<ImageHeaderParser> parsers;
    private final GifBitmapProvider provider;

    @VisibleForTesting
    static class GifDecoderFactory {
        GifDecoderFactory() {
        }

        GifDecoder build(BitmapProvider provider, GifHeader header, ByteBuffer data, int sampleSize) {
            return new StandardGifDecoder(provider, header, data, sampleSize);
        }
    }

    @VisibleForTesting
    static class GifHeaderParserPool {
        private final Queue<GifHeaderParser> pool = Util.createQueue(0);

        GifHeaderParserPool() {
        }

        synchronized GifHeaderParser obtain(ByteBuffer buffer) {
            GifHeaderParser result;
            result = (GifHeaderParser) this.pool.poll();
            if (result == null) {
                result = new GifHeaderParser();
            }
            return result.setData(buffer);
        }

        synchronized void release(GifHeaderParser parser) {
            parser.clear();
            this.pool.offer(parser);
        }
    }

    public ByteBufferGifDecoder(Context context) {
        this(context, Glide.get(context).getRegistry().getImageHeaderParsers(), Glide.get(context).getBitmapPool(), Glide.get(context).getArrayPool());
    }

    public ByteBufferGifDecoder(Context context, List<ImageHeaderParser> parsers, BitmapPool bitmapPool, ArrayPool arrayPool) {
        this(context, parsers, bitmapPool, arrayPool, PARSER_POOL, GIF_DECODER_FACTORY);
    }

    @VisibleForTesting
    ByteBufferGifDecoder(Context context, List<ImageHeaderParser> parsers, BitmapPool bitmapPool, ArrayPool arrayPool, GifHeaderParserPool parserPool, GifDecoderFactory gifDecoderFactory) {
        this.context = context.getApplicationContext();
        this.parsers = parsers;
        this.gifDecoderFactory = gifDecoderFactory;
        this.provider = new GifBitmapProvider(bitmapPool, arrayPool);
        this.parserPool = parserPool;
    }

    public boolean handles(@NonNull ByteBuffer source, @NonNull Options options) throws IOException {
        if (!((Boolean) options.get(GifOptions.DISABLE_ANIMATION)).booleanValue()) {
            if (ImageHeaderParserUtils.getType(this.parsers, source) == ImageType.GIF) {
                return true;
            }
        }
        return false;
    }

    public GifDrawableResource decode(@NonNull ByteBuffer source, int width, int height, @NonNull Options options) {
        GifHeaderParser parser = this.parserPool.obtain(source);
        try {
            GifDrawableResource decode = decode(source, width, height, parser, options);
            return decode;
        } finally {
            this.parserPool.release(parser);
        }
    }

    @Nullable
    private GifDrawableResource decode(ByteBuffer byteBuffer, int width, int height, GifHeaderParser parser, Options options) {
        Throwable th;
        StringBuilder stringBuilder;
        ByteBufferGifDecoder byteBufferGifDecoder = this;
        long startTime = LogTime.getLogTime();
        int i;
        try {
            String str;
            GifHeader header = parser.parseHeader();
            if (header.getNumFrames() <= 0) {
                i = width;
            } else if (header.getStatus() != 0) {
                i = width;
            } else {
                Config config = options.get(GifOptions.DECODE_FORMAT) == DecodeFormat.PREFER_RGB_565 ? Config.RGB_565 : Config.ARGB_8888;
                try {
                    int sampleSize = getSampleSize(header, width, height);
                    GifDecoder build = byteBufferGifDecoder.gifDecoderFactory.build(byteBufferGifDecoder.provider, header, byteBuffer, sampleSize);
                    build.setDefaultBitmapConfig(config);
                    build.advance();
                    Bitmap firstFrame = build.getNextFrame();
                    if (firstFrame == null) {
                        if (Log.isLoggable(TAG, 2)) {
                            str = TAG;
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Decoded GIF from stream in ");
                            stringBuilder2.append(LogTime.getElapsedMillis(startTime));
                            Log.v(str, stringBuilder2.toString());
                        }
                        return null;
                    }
                    GifDecoder gifDecoder = build;
                    GifDrawableResource gifDrawableResource = new GifDrawableResource(new GifDrawable(byteBufferGifDecoder.context, build, UnitTransformation.get(), width, height, firstFrame));
                    if (Log.isLoggable(TAG, 2)) {
                        str = TAG;
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("Decoded GIF from stream in ");
                        stringBuilder3.append(LogTime.getElapsedMillis(startTime));
                        Log.v(str, stringBuilder3.toString());
                    }
                    return gifDrawableResource;
                } catch (Throwable th2) {
                    th = th2;
                    if (Log.isLoggable(TAG, 2)) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Decoded GIF from stream in ");
                        stringBuilder.append(LogTime.getElapsedMillis(startTime));
                        Log.v(TAG, stringBuilder.toString());
                    }
                    throw th;
                }
            }
            if (Log.isLoggable(TAG, 2)) {
                str = TAG;
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append("Decoded GIF from stream in ");
                stringBuilder4.append(LogTime.getElapsedMillis(startTime));
                Log.v(str, stringBuilder4.toString());
            }
            return null;
        } catch (Throwable th3) {
            th = th3;
            i = width;
            if (Log.isLoggable(TAG, 2)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Decoded GIF from stream in ");
                stringBuilder.append(LogTime.getElapsedMillis(startTime));
                Log.v(TAG, stringBuilder.toString());
            }
            throw th;
        }
    }

    private static int getSampleSize(GifHeader gifHeader, int targetWidth, int targetHeight) {
        int exactSampleSize = Math.min(gifHeader.getHeight() / targetHeight, gifHeader.getWidth() / targetWidth);
        int sampleSize = Math.max(1, exactSampleSize == 0 ? 0 : Integer.highestOneBit(exactSampleSize));
        if (Log.isLoggable(TAG, 2) && sampleSize > 1) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Downsampling GIF, sampleSize: ");
            stringBuilder.append(sampleSize);
            stringBuilder.append(", target dimens: [");
            stringBuilder.append(targetWidth);
            stringBuilder.append("x");
            stringBuilder.append(targetHeight);
            stringBuilder.append("], actual dimens: [");
            stringBuilder.append(gifHeader.getWidth());
            stringBuilder.append("x");
            stringBuilder.append(gifHeader.getHeight());
            stringBuilder.append("]");
            Log.v(str, stringBuilder.toString());
        }
        return sampleSize;
    }
}

package com.bumptech.glide.load.resource.bitmap;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParser.ImageType;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy.SampleSizeRounding;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public final class Downsampler {
    public static final Option<Boolean> ALLOW_HARDWARE_CONFIG = Option.memory("com.bumptech.glide.load.resource.bitmap.Downsampler.AllowHardwareDecode", Boolean.valueOf(false));
    public static final Option<DecodeFormat> DECODE_FORMAT = Option.memory("com.bumptech.glide.load.resource.bitmap.Downsampler.DecodeFormat", DecodeFormat.DEFAULT);
    @Deprecated
    public static final Option<DownsampleStrategy> DOWNSAMPLE_STRATEGY = DownsampleStrategy.OPTION;
    private static final DecodeCallbacks EMPTY_CALLBACKS = new C09661();
    public static final Option<Boolean> FIX_BITMAP_SIZE_TO_REQUESTED_DIMENSIONS = Option.memory("com.bumptech.glide.load.resource.bitmap.Downsampler.FixBitmapSize", Boolean.valueOf(false));
    private static final String ICO_MIME_TYPE = "image/x-ico";
    private static final int MARK_POSITION = 10485760;
    private static final Set<String> NO_DOWNSAMPLE_PRE_N_MIME_TYPES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[]{WBMP_MIME_TYPE, ICO_MIME_TYPE})));
    private static final Queue<Options> OPTIONS_QUEUE = Util.createQueue(0);
    static final String TAG = "Downsampler";
    private static final Set<ImageType> TYPES_THAT_USE_POOL_PRE_KITKAT = Collections.unmodifiableSet(EnumSet.of(ImageType.JPEG, ImageType.PNG_A, ImageType.PNG));
    private static final String WBMP_MIME_TYPE = "image/vnd.wap.wbmp";
    private final BitmapPool bitmapPool;
    private final ArrayPool byteArrayPool;
    private final DisplayMetrics displayMetrics;
    private final HardwareConfigState hardwareConfigState = HardwareConfigState.getInstance();
    private final List<ImageHeaderParser> parsers;

    public interface DecodeCallbacks {
        void onDecodeComplete(BitmapPool bitmapPool, Bitmap bitmap) throws IOException;

        void onObtainBounds();
    }

    /* renamed from: com.bumptech.glide.load.resource.bitmap.Downsampler$1 */
    class C09661 implements DecodeCallbacks {
        C09661() {
        }

        public void onObtainBounds() {
        }

        public void onDecodeComplete(BitmapPool bitmapPool, Bitmap downsampled) {
        }
    }

    public Downsampler(List<ImageHeaderParser> parsers, DisplayMetrics displayMetrics, BitmapPool bitmapPool, ArrayPool byteArrayPool) {
        this.parsers = parsers;
        this.displayMetrics = (DisplayMetrics) Preconditions.checkNotNull(displayMetrics);
        this.bitmapPool = (BitmapPool) Preconditions.checkNotNull(bitmapPool);
        this.byteArrayPool = (ArrayPool) Preconditions.checkNotNull(byteArrayPool);
    }

    public boolean handles(InputStream is) {
        return true;
    }

    public boolean handles(ByteBuffer byteBuffer) {
        return true;
    }

    public Resource<Bitmap> decode(InputStream is, int outWidth, int outHeight, com.bumptech.glide.load.Options options) throws IOException {
        return decode(is, outWidth, outHeight, options, EMPTY_CALLBACKS);
    }

    public Resource<Bitmap> decode(InputStream is, int requestedWidth, int requestedHeight, com.bumptech.glide.load.Options options, DecodeCallbacks callbacks) throws IOException {
        com.bumptech.glide.load.Options options2 = options;
        Preconditions.checkArgument(is.markSupported(), "You must provide an InputStream that supports mark()");
        byte[] bytesForOptions = (byte[]) this.byteArrayPool.get(65536, byte[].class);
        Options bitmapFactoryOptions = getDefaultOptions();
        bitmapFactoryOptions.inTempStorage = bytesForOptions;
        DecodeFormat decodeFormat = (DecodeFormat) options2.get(DECODE_FORMAT);
        DownsampleStrategy downsampleStrategy = (DownsampleStrategy) options2.get(DownsampleStrategy.OPTION);
        boolean fixBitmapToRequestedDimensions = ((Boolean) options2.get(FIX_BITMAP_SIZE_TO_REQUESTED_DIMENSIONS)).booleanValue();
        boolean isHardwareConfigAllowed = options2.get(ALLOW_HARDWARE_CONFIG) != null && ((Boolean) options2.get(ALLOW_HARDWARE_CONFIG)).booleanValue();
        Resource<Bitmap> obtain;
        try {
            obtain = BitmapResource.obtain(decodeFromWrappedStreams(is, bitmapFactoryOptions, downsampleStrategy, decodeFormat, isHardwareConfigAllowed, requestedWidth, requestedHeight, fixBitmapToRequestedDimensions, callbacks), r11.bitmapPool);
            return obtain;
        } finally {
            releaseOptions(bitmapFactoryOptions);
            obtain = r11.byteArrayPool;
            obtain.put(bytesForOptions);
        }
    }

    private Bitmap decodeFromWrappedStreams(InputStream is, Options options, DownsampleStrategy downsampleStrategy, DecodeFormat decodeFormat, boolean isHardwareConfigAllowed, int requestedWidth, int requestedHeight, boolean fixBitmapToRequestedDimensions, DecodeCallbacks callbacks) throws IOException {
        boolean isHardwareConfigAllowed2;
        int orientation;
        int degreesToRotate;
        boolean isExifOrientationRequired;
        int i;
        int targetWidth;
        int i2;
        int targetHeight;
        ImageType imageType;
        BitmapPool bitmapPool;
        ImageType imageType2;
        int orientation2;
        String sourceMimeType;
        int sourceHeight;
        int sourceWidth;
        DecodeCallbacks decodeCallbacks;
        Options options2;
        boolean isKitKatOrGreater;
        ImageType imageType3;
        Downsampler downsampler;
        Bitmap downsampled;
        Bitmap rotated;
        ImageType imageType4;
        float densityMultiplier;
        int sampleSize;
        int expectedWidth;
        int expectedHeight;
        InputStream inputStream = is;
        Options options3 = options;
        DecodeCallbacks decodeCallbacks2 = callbacks;
        long startTime = LogTime.getLogTime();
        int[] sourceDimensions = getDimensions(inputStream, options3, decodeCallbacks2, this.bitmapPool);
        boolean z = false;
        int sourceWidth2 = sourceDimensions[0];
        int sourceHeight2 = sourceDimensions[1];
        String sourceMimeType2 = options3.outMimeType;
        if (sourceWidth2 != -1) {
            if (sourceHeight2 != -1) {
                isHardwareConfigAllowed2 = isHardwareConfigAllowed;
                orientation = ImageHeaderParserUtils.getOrientation(r8.parsers, inputStream, r8.byteArrayPool);
                degreesToRotate = TransformationUtils.getExifOrientationDegrees(orientation);
                isExifOrientationRequired = TransformationUtils.isExifOrientationRequired(orientation);
                i = requestedWidth;
                targetWidth = i != Integer.MIN_VALUE ? sourceWidth2 : i;
                i2 = requestedHeight;
                targetHeight = i2 != Integer.MIN_VALUE ? sourceHeight2 : i2;
                imageType = ImageHeaderParserUtils.getType(r8.parsers, inputStream, r8.byteArrayPool);
                bitmapPool = r8.bitmapPool;
                imageType2 = imageType;
                calculateScaling(imageType, is, callbacks, bitmapPool, downsampleStrategy, degreesToRotate, sourceWidth2, sourceHeight2, targetWidth, targetHeight, options);
                orientation2 = orientation;
                sourceMimeType = sourceMimeType2;
                sourceHeight = sourceHeight2;
                sourceWidth = sourceWidth2;
                decodeCallbacks = decodeCallbacks2;
                options2 = options3;
                calculateConfig(is, decodeFormat, isHardwareConfigAllowed2, isExifOrientationRequired, options, targetWidth, targetHeight);
                if (VERSION.SDK_INT >= 19) {
                    z = true;
                }
                isKitKatOrGreater = z;
                if (options2.inSampleSize != 1) {
                    if (isKitKatOrGreater) {
                        isHardwareConfigAllowed = isKitKatOrGreater;
                        imageType3 = imageType2;
                        downsampler = this;
                        downsampled = decodeStream(is, options2, decodeCallbacks, downsampler.bitmapPool);
                        decodeCallbacks.onDecodeComplete(downsampler.bitmapPool, downsampled);
                        if (!Log.isLoggable(TAG, 2)) {
                            logDecode(sourceWidth, sourceHeight, sourceMimeType, options, downsampled, requestedWidth, requestedHeight, startTime);
                        }
                        rotated = null;
                        if (downsampled == null) {
                            downsampled.setDensity(downsampler.displayMetrics.densityDpi);
                            rotated = TransformationUtils.rotateImageExif(downsampler.bitmapPool, downsampled, orientation2);
                            if (downsampled.equals(rotated)) {
                                downsampler.bitmapPool.put(downsampled);
                            }
                        }
                        return rotated;
                    }
                }
                imageType4 = imageType2;
                if (shouldUsePool(imageType4)) {
                    imageType3 = imageType4;
                } else {
                    if (sourceWidth >= 0 || sourceHeight < 0 || !fixBitmapToRequestedDimensions || !isKitKatOrGreater) {
                        densityMultiplier = isScaling(options) ? ((float) options2.inTargetDensity) / ((float) options2.inDensity) : 1.0f;
                        sampleSize = options2.inSampleSize;
                        int downsampledHeight = (int) Math.ceil((double) (((float) sourceHeight) / ((float) sampleSize)));
                        expectedWidth = Math.round(((float) ((int) Math.ceil((double) (((float) sourceWidth) / ((float) sampleSize))))) * densityMultiplier);
                        expectedHeight = Math.round(((float) downsampledHeight) * densityMultiplier);
                        if (Log.isLoggable(TAG, 2)) {
                        } else {
                            String str = TAG;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Calculated target [");
                            stringBuilder.append(expectedWidth);
                            stringBuilder.append("x");
                            stringBuilder.append(expectedHeight);
                            stringBuilder.append("] for source [");
                            stringBuilder.append(sourceWidth);
                            stringBuilder.append("x");
                            stringBuilder.append(sourceHeight);
                            stringBuilder.append("], sampleSize: ");
                            stringBuilder.append(sampleSize);
                            stringBuilder.append(", targetDensity: ");
                            stringBuilder.append(options2.inTargetDensity);
                            stringBuilder.append(", density: ");
                            stringBuilder.append(options2.inDensity);
                            stringBuilder.append(", density multiplier: ");
                            stringBuilder.append(densityMultiplier);
                            Log.v(str, stringBuilder.toString());
                        }
                    } else {
                        isHardwareConfigAllowed = isKitKatOrGreater;
                        imageType3 = imageType4;
                        expectedWidth = targetWidth;
                        expectedHeight = targetHeight;
                    }
                    if (expectedWidth <= 0 && expectedHeight > 0) {
                        setInBitmap(options2, downsampler.bitmapPool, expectedWidth, expectedHeight);
                    }
                }
                downsampled = decodeStream(is, options2, decodeCallbacks, downsampler.bitmapPool);
                decodeCallbacks.onDecodeComplete(downsampler.bitmapPool, downsampled);
                if (!Log.isLoggable(TAG, 2)) {
                    logDecode(sourceWidth, sourceHeight, sourceMimeType, options, downsampled, requestedWidth, requestedHeight, startTime);
                }
                rotated = null;
                if (downsampled == null) {
                    downsampled.setDensity(downsampler.displayMetrics.densityDpi);
                    rotated = TransformationUtils.rotateImageExif(downsampler.bitmapPool, downsampled, orientation2);
                    if (downsampled.equals(rotated)) {
                        downsampler.bitmapPool.put(downsampled);
                    }
                }
                return rotated;
            }
        }
        isHardwareConfigAllowed2 = false;
        orientation = ImageHeaderParserUtils.getOrientation(r8.parsers, inputStream, r8.byteArrayPool);
        degreesToRotate = TransformationUtils.getExifOrientationDegrees(orientation);
        isExifOrientationRequired = TransformationUtils.isExifOrientationRequired(orientation);
        i = requestedWidth;
        if (i != Integer.MIN_VALUE) {
        }
        i2 = requestedHeight;
        if (i2 != Integer.MIN_VALUE) {
        }
        imageType = ImageHeaderParserUtils.getType(r8.parsers, inputStream, r8.byteArrayPool);
        bitmapPool = r8.bitmapPool;
        imageType2 = imageType;
        calculateScaling(imageType, is, callbacks, bitmapPool, downsampleStrategy, degreesToRotate, sourceWidth2, sourceHeight2, targetWidth, targetHeight, options);
        orientation2 = orientation;
        sourceMimeType = sourceMimeType2;
        sourceHeight = sourceHeight2;
        sourceWidth = sourceWidth2;
        decodeCallbacks = decodeCallbacks2;
        options2 = options3;
        calculateConfig(is, decodeFormat, isHardwareConfigAllowed2, isExifOrientationRequired, options, targetWidth, targetHeight);
        if (VERSION.SDK_INT >= 19) {
            z = true;
        }
        isKitKatOrGreater = z;
        if (options2.inSampleSize != 1) {
            if (isKitKatOrGreater) {
                isHardwareConfigAllowed = isKitKatOrGreater;
                imageType3 = imageType2;
                downsampler = this;
                downsampled = decodeStream(is, options2, decodeCallbacks, downsampler.bitmapPool);
                decodeCallbacks.onDecodeComplete(downsampler.bitmapPool, downsampled);
                if (!Log.isLoggable(TAG, 2)) {
                    logDecode(sourceWidth, sourceHeight, sourceMimeType, options, downsampled, requestedWidth, requestedHeight, startTime);
                }
                rotated = null;
                if (downsampled == null) {
                    downsampled.setDensity(downsampler.displayMetrics.densityDpi);
                    rotated = TransformationUtils.rotateImageExif(downsampler.bitmapPool, downsampled, orientation2);
                    if (downsampled.equals(rotated)) {
                        downsampler.bitmapPool.put(downsampled);
                    }
                }
                return rotated;
            }
        }
        imageType4 = imageType2;
        if (shouldUsePool(imageType4)) {
            imageType3 = imageType4;
        } else {
            if (sourceWidth >= 0) {
            }
            if (isScaling(options)) {
            }
            sampleSize = options2.inSampleSize;
            int downsampledHeight2 = (int) Math.ceil((double) (((float) sourceHeight) / ((float) sampleSize)));
            expectedWidth = Math.round(((float) ((int) Math.ceil((double) (((float) sourceWidth) / ((float) sampleSize))))) * densityMultiplier);
            expectedHeight = Math.round(((float) downsampledHeight2) * densityMultiplier);
            if (Log.isLoggable(TAG, 2)) {
            } else {
                String str2 = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Calculated target [");
                stringBuilder2.append(expectedWidth);
                stringBuilder2.append("x");
                stringBuilder2.append(expectedHeight);
                stringBuilder2.append("] for source [");
                stringBuilder2.append(sourceWidth);
                stringBuilder2.append("x");
                stringBuilder2.append(sourceHeight);
                stringBuilder2.append("], sampleSize: ");
                stringBuilder2.append(sampleSize);
                stringBuilder2.append(", targetDensity: ");
                stringBuilder2.append(options2.inTargetDensity);
                stringBuilder2.append(", density: ");
                stringBuilder2.append(options2.inDensity);
                stringBuilder2.append(", density multiplier: ");
                stringBuilder2.append(densityMultiplier);
                Log.v(str2, stringBuilder2.toString());
            }
            if (expectedWidth <= 0) {
            }
        }
        downsampled = decodeStream(is, options2, decodeCallbacks, downsampler.bitmapPool);
        decodeCallbacks.onDecodeComplete(downsampler.bitmapPool, downsampled);
        if (!Log.isLoggable(TAG, 2)) {
            logDecode(sourceWidth, sourceHeight, sourceMimeType, options, downsampled, requestedWidth, requestedHeight, startTime);
        }
        rotated = null;
        if (downsampled == null) {
            downsampled.setDensity(downsampler.displayMetrics.densityDpi);
            rotated = TransformationUtils.rotateImageExif(downsampler.bitmapPool, downsampled, orientation2);
            if (downsampled.equals(rotated)) {
                downsampler.bitmapPool.put(downsampled);
            }
        }
        return rotated;
    }

    private static void calculateScaling(ImageType imageType, InputStream is, DecodeCallbacks decodeCallbacks, BitmapPool bitmapPool, DownsampleStrategy downsampleStrategy, int degreesToRotate, int sourceWidth, int sourceHeight, int targetWidth, int targetHeight, Options options) throws IOException {
        Enum enumR = imageType;
        DownsampleStrategy downsampleStrategy2 = downsampleStrategy;
        int i = degreesToRotate;
        int i2 = sourceWidth;
        int i3 = sourceHeight;
        int i4 = targetWidth;
        int i5 = targetHeight;
        Options options2 = options;
        if (i2 > 0) {
            if (i3 > 0) {
                float exactScaleFactor;
                SampleSizeRounding rounding;
                int outWidth;
                int outHeight;
                int widthScaleFactor;
                int heightScaleFactor;
                int scaleFactor;
                int nativeScaling;
                int i6;
                BitmapPool bitmapPool2;
                InputStream rounding2;
                DecodeCallbacks decodeCallbacks2;
                int powerOfTwoWidth;
                double adjustedScaleFactor;
                String str;
                StringBuilder stringBuilder;
                StringBuilder stringBuilder2;
                if (i != 90) {
                    if (i != 270) {
                        exactScaleFactor = downsampleStrategy2.getScaleFactor(i2, i3, i4, i5);
                        if (exactScaleFactor <= 0.0f) {
                            rounding = downsampleStrategy2.getSampleSizeRounding(i2, i3, i4, i5);
                            if (rounding == null) {
                                outWidth = round((double) (((float) i2) * exactScaleFactor));
                                outHeight = round((double) (((float) i3) * exactScaleFactor));
                                widthScaleFactor = i2 / outWidth;
                                heightScaleFactor = i3 / outHeight;
                                if (rounding != SampleSizeRounding.MEMORY) {
                                    scaleFactor = Math.max(widthScaleFactor, heightScaleFactor);
                                } else {
                                    scaleFactor = Math.min(widthScaleFactor, heightScaleFactor);
                                }
                                if (VERSION.SDK_INT <= 23) {
                                    if (NO_DOWNSAMPLE_PRE_N_MIME_TYPES.contains(options2.outMimeType)) {
                                        i = 1;
                                        options2.inSampleSize = i;
                                        if (enumR != ImageType.JPEG) {
                                            nativeScaling = Math.min(i, 8);
                                            rounding = (int) Math.ceil((double) (((float) i2) / ((float) nativeScaling)));
                                            outWidth = (int) Math.ceil((double) (((float) i3) / ((float) nativeScaling)));
                                            outHeight = i / 8;
                                            if (outHeight <= 0) {
                                                rounding /= outHeight;
                                                outWidth /= outHeight;
                                            }
                                            outHeight = bitmapPool;
                                        } else {
                                            i6 = outHeight;
                                            if (enumR == ImageType.PNG) {
                                                bitmapPool2 = bitmapPool;
                                            } else if (enumR != ImageType.PNG_A) {
                                                bitmapPool2 = bitmapPool;
                                            } else {
                                                if (enumR == ImageType.WEBP) {
                                                    rounding2 = is;
                                                    decodeCallbacks2 = decodeCallbacks;
                                                    bitmapPool2 = bitmapPool;
                                                } else if (enumR != ImageType.WEBP_A) {
                                                    rounding2 = is;
                                                    decodeCallbacks2 = decodeCallbacks;
                                                    bitmapPool2 = bitmapPool;
                                                } else {
                                                    if (i2 % i == null) {
                                                        if (i3 % i == null) {
                                                            rounding = i2 / i;
                                                            outWidth = i3 / i;
                                                            bitmapPool2 = bitmapPool;
                                                        }
                                                    }
                                                    nativeScaling = getDimensions(is, options2, decodeCallbacks, bitmapPool);
                                                    powerOfTwoWidth = nativeScaling[0];
                                                    outWidth = nativeScaling[1];
                                                    rounding = powerOfTwoWidth;
                                                }
                                                if (VERSION.SDK_INT < 24) {
                                                    powerOfTwoWidth = Math.round(((float) i2) / ((float) i));
                                                    outWidth = Math.round(((float) i3) / ((float) i));
                                                    rounding = powerOfTwoWidth;
                                                } else {
                                                    outWidth = (int) Math.floor((double) (((float) i3) / ((float) i)));
                                                    rounding = (int) Math.floor((double) (((float) i2) / ((float) i)));
                                                }
                                            }
                                            outWidth = (int) Math.floor((double) (((float) i3) / ((float) i)));
                                            rounding = (int) Math.floor((double) (((float) i2) / ((float) i)));
                                        }
                                        adjustedScaleFactor = (double) downsampleStrategy2.getScaleFactor(rounding, outWidth, i4, i5);
                                        if (VERSION.SDK_INT < 19) {
                                            options2.inTargetDensity = adjustTargetDensityForError(adjustedScaleFactor);
                                            options2.inDensity = getDensityMultiplier(adjustedScaleFactor);
                                        }
                                        if (isScaling(options)) {
                                            options2.inTargetDensity = 0;
                                            options2.inDensity = 0;
                                        } else {
                                            options2.inScaled = true;
                                        }
                                        if (Log.isLoggable(TAG, 2)) {
                                        } else {
                                            str = TAG;
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("Calculate scaling, source: [");
                                            stringBuilder.append(i2);
                                            stringBuilder.append("x");
                                            stringBuilder.append(i3);
                                            stringBuilder.append("], target: [");
                                            stringBuilder.append(i4);
                                            stringBuilder.append("x");
                                            stringBuilder.append(i5);
                                            stringBuilder.append("], power of two scaled: [");
                                            stringBuilder.append(rounding);
                                            stringBuilder.append("x");
                                            stringBuilder.append(outWidth);
                                            stringBuilder.append("], exact scale factor: ");
                                            stringBuilder.append(exactScaleFactor);
                                            stringBuilder.append(", power of 2 sample size: ");
                                            stringBuilder.append(i);
                                            stringBuilder.append(", adjusted scale factor: ");
                                            stringBuilder.append(adjustedScaleFactor);
                                            stringBuilder.append(", target density: ");
                                            stringBuilder.append(options2.inTargetDensity);
                                            stringBuilder.append(", density: ");
                                            stringBuilder.append(options2.inDensity);
                                            Log.v(str, stringBuilder.toString());
                                        }
                                        return;
                                    }
                                }
                                i = Math.max(1, Integer.highestOneBit(scaleFactor));
                                if (rounding != SampleSizeRounding.MEMORY && ((float) i) < 1.0f / exactScaleFactor) {
                                    i <<= 1;
                                }
                                options2.inSampleSize = i;
                                if (enumR != ImageType.JPEG) {
                                    i6 = outHeight;
                                    if (enumR == ImageType.PNG) {
                                        bitmapPool2 = bitmapPool;
                                    } else if (enumR != ImageType.PNG_A) {
                                        if (enumR == ImageType.WEBP) {
                                            rounding2 = is;
                                            decodeCallbacks2 = decodeCallbacks;
                                            bitmapPool2 = bitmapPool;
                                        } else if (enumR != ImageType.WEBP_A) {
                                            if (i2 % i == null) {
                                                if (i3 % i == null) {
                                                    rounding = i2 / i;
                                                    outWidth = i3 / i;
                                                    bitmapPool2 = bitmapPool;
                                                }
                                            }
                                            nativeScaling = getDimensions(is, options2, decodeCallbacks, bitmapPool);
                                            powerOfTwoWidth = nativeScaling[0];
                                            outWidth = nativeScaling[1];
                                            rounding = powerOfTwoWidth;
                                        } else {
                                            rounding2 = is;
                                            decodeCallbacks2 = decodeCallbacks;
                                            bitmapPool2 = bitmapPool;
                                        }
                                        if (VERSION.SDK_INT < 24) {
                                            outWidth = (int) Math.floor((double) (((float) i3) / ((float) i)));
                                            rounding = (int) Math.floor((double) (((float) i2) / ((float) i)));
                                        } else {
                                            powerOfTwoWidth = Math.round(((float) i2) / ((float) i));
                                            outWidth = Math.round(((float) i3) / ((float) i));
                                            rounding = powerOfTwoWidth;
                                        }
                                    } else {
                                        bitmapPool2 = bitmapPool;
                                    }
                                    outWidth = (int) Math.floor((double) (((float) i3) / ((float) i)));
                                    rounding = (int) Math.floor((double) (((float) i2) / ((float) i)));
                                } else {
                                    nativeScaling = Math.min(i, 8);
                                    rounding = (int) Math.ceil((double) (((float) i2) / ((float) nativeScaling)));
                                    outWidth = (int) Math.ceil((double) (((float) i3) / ((float) nativeScaling)));
                                    outHeight = i / 8;
                                    if (outHeight <= 0) {
                                        rounding /= outHeight;
                                        outWidth /= outHeight;
                                    }
                                    outHeight = bitmapPool;
                                }
                                adjustedScaleFactor = (double) downsampleStrategy2.getScaleFactor(rounding, outWidth, i4, i5);
                                if (VERSION.SDK_INT < 19) {
                                    options2.inTargetDensity = adjustTargetDensityForError(adjustedScaleFactor);
                                    options2.inDensity = getDensityMultiplier(adjustedScaleFactor);
                                }
                                if (isScaling(options)) {
                                    options2.inTargetDensity = 0;
                                    options2.inDensity = 0;
                                } else {
                                    options2.inScaled = true;
                                }
                                if (Log.isLoggable(TAG, 2)) {
                                } else {
                                    str = TAG;
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("Calculate scaling, source: [");
                                    stringBuilder.append(i2);
                                    stringBuilder.append("x");
                                    stringBuilder.append(i3);
                                    stringBuilder.append("], target: [");
                                    stringBuilder.append(i4);
                                    stringBuilder.append("x");
                                    stringBuilder.append(i5);
                                    stringBuilder.append("], power of two scaled: [");
                                    stringBuilder.append(rounding);
                                    stringBuilder.append("x");
                                    stringBuilder.append(outWidth);
                                    stringBuilder.append("], exact scale factor: ");
                                    stringBuilder.append(exactScaleFactor);
                                    stringBuilder.append(", power of 2 sample size: ");
                                    stringBuilder.append(i);
                                    stringBuilder.append(", adjusted scale factor: ");
                                    stringBuilder.append(adjustedScaleFactor);
                                    stringBuilder.append(", target density: ");
                                    stringBuilder.append(options2.inTargetDensity);
                                    stringBuilder.append(", density: ");
                                    stringBuilder.append(options2.inDensity);
                                    Log.v(str, stringBuilder.toString());
                                }
                                return;
                            }
                            throw new IllegalArgumentException("Cannot round with null rounding");
                        }
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Cannot scale with factor: ");
                        stringBuilder2.append(exactScaleFactor);
                        stringBuilder2.append(" from: ");
                        stringBuilder2.append(downsampleStrategy2);
                        stringBuilder2.append(", source: [");
                        stringBuilder2.append(i2);
                        stringBuilder2.append("x");
                        stringBuilder2.append(i3);
                        stringBuilder2.append("], target: [");
                        stringBuilder2.append(i4);
                        stringBuilder2.append("x");
                        stringBuilder2.append(i5);
                        stringBuilder2.append("]");
                        throw new IllegalArgumentException(stringBuilder2.toString());
                    }
                }
                exactScaleFactor = downsampleStrategy2.getScaleFactor(i3, i2, i4, i5);
                if (exactScaleFactor <= 0.0f) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Cannot scale with factor: ");
                    stringBuilder2.append(exactScaleFactor);
                    stringBuilder2.append(" from: ");
                    stringBuilder2.append(downsampleStrategy2);
                    stringBuilder2.append(", source: [");
                    stringBuilder2.append(i2);
                    stringBuilder2.append("x");
                    stringBuilder2.append(i3);
                    stringBuilder2.append("], target: [");
                    stringBuilder2.append(i4);
                    stringBuilder2.append("x");
                    stringBuilder2.append(i5);
                    stringBuilder2.append("]");
                    throw new IllegalArgumentException(stringBuilder2.toString());
                }
                rounding = downsampleStrategy2.getSampleSizeRounding(i2, i3, i4, i5);
                if (rounding == null) {
                    throw new IllegalArgumentException("Cannot round with null rounding");
                }
                outWidth = round((double) (((float) i2) * exactScaleFactor));
                outHeight = round((double) (((float) i3) * exactScaleFactor));
                widthScaleFactor = i2 / outWidth;
                heightScaleFactor = i3 / outHeight;
                if (rounding != SampleSizeRounding.MEMORY) {
                    scaleFactor = Math.min(widthScaleFactor, heightScaleFactor);
                } else {
                    scaleFactor = Math.max(widthScaleFactor, heightScaleFactor);
                }
                if (VERSION.SDK_INT <= 23) {
                    if (NO_DOWNSAMPLE_PRE_N_MIME_TYPES.contains(options2.outMimeType)) {
                        i = 1;
                        options2.inSampleSize = i;
                        if (enumR != ImageType.JPEG) {
                            nativeScaling = Math.min(i, 8);
                            rounding = (int) Math.ceil((double) (((float) i2) / ((float) nativeScaling)));
                            outWidth = (int) Math.ceil((double) (((float) i3) / ((float) nativeScaling)));
                            outHeight = i / 8;
                            if (outHeight <= 0) {
                                rounding /= outHeight;
                                outWidth /= outHeight;
                            }
                            outHeight = bitmapPool;
                        } else {
                            i6 = outHeight;
                            if (enumR == ImageType.PNG) {
                                bitmapPool2 = bitmapPool;
                            } else if (enumR != ImageType.PNG_A) {
                                bitmapPool2 = bitmapPool;
                            } else {
                                if (enumR == ImageType.WEBP) {
                                    rounding2 = is;
                                    decodeCallbacks2 = decodeCallbacks;
                                    bitmapPool2 = bitmapPool;
                                } else if (enumR != ImageType.WEBP_A) {
                                    rounding2 = is;
                                    decodeCallbacks2 = decodeCallbacks;
                                    bitmapPool2 = bitmapPool;
                                } else {
                                    if (i2 % i == null) {
                                        if (i3 % i == null) {
                                            rounding = i2 / i;
                                            outWidth = i3 / i;
                                            bitmapPool2 = bitmapPool;
                                        }
                                    }
                                    nativeScaling = getDimensions(is, options2, decodeCallbacks, bitmapPool);
                                    powerOfTwoWidth = nativeScaling[0];
                                    outWidth = nativeScaling[1];
                                    rounding = powerOfTwoWidth;
                                }
                                if (VERSION.SDK_INT < 24) {
                                    powerOfTwoWidth = Math.round(((float) i2) / ((float) i));
                                    outWidth = Math.round(((float) i3) / ((float) i));
                                    rounding = powerOfTwoWidth;
                                } else {
                                    outWidth = (int) Math.floor((double) (((float) i3) / ((float) i)));
                                    rounding = (int) Math.floor((double) (((float) i2) / ((float) i)));
                                }
                            }
                            outWidth = (int) Math.floor((double) (((float) i3) / ((float) i)));
                            rounding = (int) Math.floor((double) (((float) i2) / ((float) i)));
                        }
                        adjustedScaleFactor = (double) downsampleStrategy2.getScaleFactor(rounding, outWidth, i4, i5);
                        if (VERSION.SDK_INT < 19) {
                            options2.inTargetDensity = adjustTargetDensityForError(adjustedScaleFactor);
                            options2.inDensity = getDensityMultiplier(adjustedScaleFactor);
                        }
                        if (isScaling(options)) {
                            options2.inScaled = true;
                        } else {
                            options2.inTargetDensity = 0;
                            options2.inDensity = 0;
                        }
                        if (Log.isLoggable(TAG, 2)) {
                            str = TAG;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Calculate scaling, source: [");
                            stringBuilder.append(i2);
                            stringBuilder.append("x");
                            stringBuilder.append(i3);
                            stringBuilder.append("], target: [");
                            stringBuilder.append(i4);
                            stringBuilder.append("x");
                            stringBuilder.append(i5);
                            stringBuilder.append("], power of two scaled: [");
                            stringBuilder.append(rounding);
                            stringBuilder.append("x");
                            stringBuilder.append(outWidth);
                            stringBuilder.append("], exact scale factor: ");
                            stringBuilder.append(exactScaleFactor);
                            stringBuilder.append(", power of 2 sample size: ");
                            stringBuilder.append(i);
                            stringBuilder.append(", adjusted scale factor: ");
                            stringBuilder.append(adjustedScaleFactor);
                            stringBuilder.append(", target density: ");
                            stringBuilder.append(options2.inTargetDensity);
                            stringBuilder.append(", density: ");
                            stringBuilder.append(options2.inDensity);
                            Log.v(str, stringBuilder.toString());
                        }
                        return;
                    }
                }
                i = Math.max(1, Integer.highestOneBit(scaleFactor));
                if (rounding != SampleSizeRounding.MEMORY) {
                }
                options2.inSampleSize = i;
                if (enumR != ImageType.JPEG) {
                    i6 = outHeight;
                    if (enumR == ImageType.PNG) {
                        bitmapPool2 = bitmapPool;
                    } else if (enumR != ImageType.PNG_A) {
                        if (enumR == ImageType.WEBP) {
                            rounding2 = is;
                            decodeCallbacks2 = decodeCallbacks;
                            bitmapPool2 = bitmapPool;
                        } else if (enumR != ImageType.WEBP_A) {
                            if (i2 % i == null) {
                                if (i3 % i == null) {
                                    rounding = i2 / i;
                                    outWidth = i3 / i;
                                    bitmapPool2 = bitmapPool;
                                }
                            }
                            nativeScaling = getDimensions(is, options2, decodeCallbacks, bitmapPool);
                            powerOfTwoWidth = nativeScaling[0];
                            outWidth = nativeScaling[1];
                            rounding = powerOfTwoWidth;
                        } else {
                            rounding2 = is;
                            decodeCallbacks2 = decodeCallbacks;
                            bitmapPool2 = bitmapPool;
                        }
                        if (VERSION.SDK_INT < 24) {
                            outWidth = (int) Math.floor((double) (((float) i3) / ((float) i)));
                            rounding = (int) Math.floor((double) (((float) i2) / ((float) i)));
                        } else {
                            powerOfTwoWidth = Math.round(((float) i2) / ((float) i));
                            outWidth = Math.round(((float) i3) / ((float) i));
                            rounding = powerOfTwoWidth;
                        }
                    } else {
                        bitmapPool2 = bitmapPool;
                    }
                    outWidth = (int) Math.floor((double) (((float) i3) / ((float) i)));
                    rounding = (int) Math.floor((double) (((float) i2) / ((float) i)));
                } else {
                    nativeScaling = Math.min(i, 8);
                    rounding = (int) Math.ceil((double) (((float) i2) / ((float) nativeScaling)));
                    outWidth = (int) Math.ceil((double) (((float) i3) / ((float) nativeScaling)));
                    outHeight = i / 8;
                    if (outHeight <= 0) {
                        rounding /= outHeight;
                        outWidth /= outHeight;
                    }
                    outHeight = bitmapPool;
                }
                adjustedScaleFactor = (double) downsampleStrategy2.getScaleFactor(rounding, outWidth, i4, i5);
                if (VERSION.SDK_INT < 19) {
                    options2.inTargetDensity = adjustTargetDensityForError(adjustedScaleFactor);
                    options2.inDensity = getDensityMultiplier(adjustedScaleFactor);
                }
                if (isScaling(options)) {
                    options2.inTargetDensity = 0;
                    options2.inDensity = 0;
                } else {
                    options2.inScaled = true;
                }
                if (Log.isLoggable(TAG, 2)) {
                } else {
                    str = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Calculate scaling, source: [");
                    stringBuilder.append(i2);
                    stringBuilder.append("x");
                    stringBuilder.append(i3);
                    stringBuilder.append("], target: [");
                    stringBuilder.append(i4);
                    stringBuilder.append("x");
                    stringBuilder.append(i5);
                    stringBuilder.append("], power of two scaled: [");
                    stringBuilder.append(rounding);
                    stringBuilder.append("x");
                    stringBuilder.append(outWidth);
                    stringBuilder.append("], exact scale factor: ");
                    stringBuilder.append(exactScaleFactor);
                    stringBuilder.append(", power of 2 sample size: ");
                    stringBuilder.append(i);
                    stringBuilder.append(", adjusted scale factor: ");
                    stringBuilder.append(adjustedScaleFactor);
                    stringBuilder.append(", target density: ");
                    stringBuilder.append(options2.inTargetDensity);
                    stringBuilder.append(", density: ");
                    stringBuilder.append(options2.inDensity);
                    Log.v(str, stringBuilder.toString());
                }
                return;
            }
        }
        if (Log.isLoggable(TAG, 3)) {
            String str2 = TAG;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Unable to determine dimensions for: ");
            stringBuilder3.append(enumR);
            stringBuilder3.append(" with target [");
            stringBuilder3.append(i4);
            stringBuilder3.append("x");
            stringBuilder3.append(i5);
            stringBuilder3.append("]");
            Log.d(str2, stringBuilder3.toString());
        }
    }

    private static int adjustTargetDensityForError(double adjustedScaleFactor) {
        int densityMultiplier = getDensityMultiplier(adjustedScaleFactor);
        double d = (double) densityMultiplier;
        Double.isNaN(d);
        int targetDensity = round(d * adjustedScaleFactor);
        double difference = (double) (((float) targetDensity) / ((float) densityMultiplier));
        Double.isNaN(difference);
        difference = adjustedScaleFactor / difference;
        double d2 = (double) targetDensity;
        Double.isNaN(d2);
        return round(d2 * difference);
    }

    private static int getDensityMultiplier(double adjustedScaleFactor) {
        return (int) Math.round((adjustedScaleFactor <= 1.0d ? adjustedScaleFactor : 1.0d / adjustedScaleFactor) * 2.147483647E9d);
    }

    private static int round(double value) {
        return (int) (0.5d + value);
    }

    private boolean shouldUsePool(ImageType imageType) {
        if (VERSION.SDK_INT >= 19) {
            return true;
        }
        return TYPES_THAT_USE_POOL_PRE_KITKAT.contains(imageType);
    }

    private void calculateConfig(InputStream is, DecodeFormat format, boolean isHardwareConfigAllowed, boolean isExifOrientationRequired, Options optionsWithScaling, int targetWidth, int targetHeight) {
        if (!this.hardwareConfigState.setHardwareConfigIfAllowed(targetWidth, targetHeight, optionsWithScaling, format, isHardwareConfigAllowed, isExifOrientationRequired)) {
            if (format != DecodeFormat.PREFER_ARGB_8888) {
                if (VERSION.SDK_INT != 16) {
                    boolean hasAlpha = false;
                    try {
                        hasAlpha = ImageHeaderParserUtils.getType(this.parsers, is, this.byteArrayPool).hasAlpha();
                    } catch (IOException e) {
                        if (Log.isLoggable(TAG, 3)) {
                            String str = TAG;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Cannot determine whether the image has alpha or not from header, format ");
                            stringBuilder.append(format);
                            Log.d(str, stringBuilder.toString(), e);
                        }
                    }
                    optionsWithScaling.inPreferredConfig = hasAlpha ? Config.ARGB_8888 : Config.RGB_565;
                    if (optionsWithScaling.inPreferredConfig == Config.RGB_565) {
                        optionsWithScaling.inDither = true;
                    }
                    return;
                }
            }
            optionsWithScaling.inPreferredConfig = Config.ARGB_8888;
        }
    }

    private static int[] getDimensions(InputStream is, Options options, DecodeCallbacks decodeCallbacks, BitmapPool bitmapPool) throws IOException {
        options.inJustDecodeBounds = true;
        decodeStream(is, options, decodeCallbacks, bitmapPool);
        options.inJustDecodeBounds = false;
        return new int[]{options.outWidth, options.outHeight};
    }

    private static Bitmap decodeStream(InputStream is, Options options, DecodeCallbacks callbacks, BitmapPool bitmapPool) throws IOException {
        IOException bitmapAssertionException;
        if (options.inJustDecodeBounds) {
            is.mark(MARK_POSITION);
        } else {
            callbacks.onObtainBounds();
        }
        int sourceWidth = options.outWidth;
        int sourceHeight = options.outHeight;
        String outMimeType = options.outMimeType;
        TransformationUtils.getBitmapDrawableLock().lock();
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(is, null, options);
            TransformationUtils.getBitmapDrawableLock().unlock();
            if (options.inJustDecodeBounds) {
                is.reset();
            }
            return bitmap;
        } catch (IOException e) {
            throw bitmapAssertionException;
        } catch (IllegalArgumentException e2) {
            bitmapAssertionException = newIoExceptionForInBitmapAssertion(e2, sourceWidth, sourceHeight, outMimeType, options);
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Failed to decode with inBitmap, trying again without Bitmap re-use", bitmapAssertionException);
            }
            if (options.inBitmap != null) {
                is.reset();
                bitmapPool.put(options.inBitmap);
                options.inBitmap = bitmap;
                bitmap = decodeStream(is, options, callbacks, bitmapPool);
                TransformationUtils.getBitmapDrawableLock().unlock();
                return bitmap;
            }
            throw bitmapAssertionException;
        } catch (Throwable th) {
            TransformationUtils.getBitmapDrawableLock().unlock();
        }
    }

    private static boolean isScaling(Options options) {
        return options.inTargetDensity > 0 && options.inDensity > 0 && options.inTargetDensity != options.inDensity;
    }

    private static void logDecode(int sourceWidth, int sourceHeight, String outMimeType, Options options, Bitmap result, int requestedWidth, int requestedHeight, long startTime) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Decoded ");
        stringBuilder.append(getBitmapString(result));
        stringBuilder.append(" from [");
        stringBuilder.append(sourceWidth);
        stringBuilder.append("x");
        stringBuilder.append(sourceHeight);
        stringBuilder.append("] ");
        stringBuilder.append(outMimeType);
        stringBuilder.append(" with inBitmap ");
        stringBuilder.append(getInBitmapString(options));
        stringBuilder.append(" for [");
        stringBuilder.append(requestedWidth);
        stringBuilder.append("x");
        stringBuilder.append(requestedHeight);
        stringBuilder.append("], sample size: ");
        stringBuilder.append(options.inSampleSize);
        stringBuilder.append(", density: ");
        stringBuilder.append(options.inDensity);
        stringBuilder.append(", target density: ");
        stringBuilder.append(options.inTargetDensity);
        stringBuilder.append(", thread: ");
        stringBuilder.append(Thread.currentThread().getName());
        stringBuilder.append(", duration: ");
        stringBuilder.append(LogTime.getElapsedMillis(startTime));
        Log.v(str, stringBuilder.toString());
    }

    private static String getInBitmapString(Options options) {
        return getBitmapString(options.inBitmap);
    }

    @Nullable
    @TargetApi(19)
    private static String getBitmapString(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        String sizeString;
        if (VERSION.SDK_INT >= 19) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" (");
            stringBuilder.append(bitmap.getAllocationByteCount());
            stringBuilder.append(")");
            sizeString = stringBuilder.toString();
        } else {
            sizeString = "";
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("[");
        stringBuilder2.append(bitmap.getWidth());
        stringBuilder2.append("x");
        stringBuilder2.append(bitmap.getHeight());
        stringBuilder2.append("] ");
        stringBuilder2.append(bitmap.getConfig());
        stringBuilder2.append(sizeString);
        return stringBuilder2.toString();
    }

    private static IOException newIoExceptionForInBitmapAssertion(IllegalArgumentException e, int outWidth, int outHeight, String outMimeType, Options options) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Exception decoding bitmap, outWidth: ");
        stringBuilder.append(outWidth);
        stringBuilder.append(", outHeight: ");
        stringBuilder.append(outHeight);
        stringBuilder.append(", outMimeType: ");
        stringBuilder.append(outMimeType);
        stringBuilder.append(", inBitmap: ");
        stringBuilder.append(getInBitmapString(options));
        return new IOException(stringBuilder.toString(), e);
    }

    @TargetApi(26)
    private static void setInBitmap(Options options, BitmapPool bitmapPool, int width, int height) {
        Config expectedConfig = null;
        if (VERSION.SDK_INT >= 26) {
            if (options.inPreferredConfig != Config.HARDWARE) {
                expectedConfig = options.outConfig;
            } else {
                return;
            }
        }
        if (expectedConfig == null) {
            expectedConfig = options.inPreferredConfig;
        }
        options.inBitmap = bitmapPool.getDirty(width, height, expectedConfig);
    }

    private static synchronized Options getDefaultOptions() {
        Options decodeBitmapOptions;
        synchronized (Downsampler.class) {
            synchronized (OPTIONS_QUEUE) {
                decodeBitmapOptions = (Options) OPTIONS_QUEUE.poll();
            }
            if (decodeBitmapOptions == null) {
                decodeBitmapOptions = new Options();
                resetOptions(decodeBitmapOptions);
            }
        }
        return decodeBitmapOptions;
    }

    private static void releaseOptions(Options decodeBitmapOptions) {
        resetOptions(decodeBitmapOptions);
        synchronized (OPTIONS_QUEUE) {
            OPTIONS_QUEUE.offer(decodeBitmapOptions);
        }
    }

    private static void resetOptions(Options decodeBitmapOptions) {
        decodeBitmapOptions.inTempStorage = null;
        decodeBitmapOptions.inDither = false;
        decodeBitmapOptions.inScaled = false;
        decodeBitmapOptions.inSampleSize = 1;
        decodeBitmapOptions.inPreferredConfig = null;
        decodeBitmapOptions.inJustDecodeBounds = false;
        decodeBitmapOptions.inDensity = 0;
        decodeBitmapOptions.inTargetDensity = 0;
        decodeBitmapOptions.outWidth = 0;
        decodeBitmapOptions.outHeight = 0;
        decodeBitmapOptions.outMimeType = null;
        decodeBitmapOptions.inBitmap = null;
        decodeBitmapOptions.inMutable = true;
    }
}

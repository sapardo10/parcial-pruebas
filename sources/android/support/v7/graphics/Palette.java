package android.support.v7.graphics;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TimingLogger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class Palette {
    static final int DEFAULT_CALCULATE_NUMBER_COLORS = 16;
    static final Filter DEFAULT_FILTER = new C08621();
    static final int DEFAULT_RESIZE_BITMAP_AREA = 12544;
    static final String LOG_TAG = "Palette";
    static final boolean LOG_TIMINGS = false;
    static final float MIN_CONTRAST_BODY_TEXT = 4.5f;
    static final float MIN_CONTRAST_TITLE_TEXT = 3.0f;
    private final Swatch mDominantSwatch = findDominantSwatch();
    private final Map<Target, Swatch> mSelectedSwatches = new ArrayMap();
    private final List<Swatch> mSwatches;
    private final List<Target> mTargets;
    private final SparseBooleanArray mUsedColors = new SparseBooleanArray();

    public static final class Builder {
        private final Bitmap mBitmap;
        private final List<Filter> mFilters = new ArrayList();
        private int mMaxColors = 16;
        private Rect mRegion;
        private int mResizeArea = Palette.DEFAULT_RESIZE_BITMAP_AREA;
        private int mResizeMaxDimension = -1;
        private final List<Swatch> mSwatches;
        private final List<Target> mTargets = new ArrayList();

        public Builder(@NonNull Bitmap bitmap) {
            if (bitmap == null || bitmap.isRecycled()) {
                throw new IllegalArgumentException("Bitmap is not valid");
            }
            this.mFilters.add(Palette.DEFAULT_FILTER);
            this.mBitmap = bitmap;
            this.mSwatches = null;
            this.mTargets.add(Target.LIGHT_VIBRANT);
            this.mTargets.add(Target.VIBRANT);
            this.mTargets.add(Target.DARK_VIBRANT);
            this.mTargets.add(Target.LIGHT_MUTED);
            this.mTargets.add(Target.MUTED);
            this.mTargets.add(Target.DARK_MUTED);
        }

        public Builder(@NonNull List<Swatch> swatches) {
            if (swatches == null || swatches.isEmpty()) {
                throw new IllegalArgumentException("List of Swatches is not valid");
            }
            this.mFilters.add(Palette.DEFAULT_FILTER);
            this.mSwatches = swatches;
            this.mBitmap = null;
        }

        @NonNull
        public Builder maximumColorCount(int colors) {
            this.mMaxColors = colors;
            return this;
        }

        @Deprecated
        @NonNull
        public Builder resizeBitmapSize(int maxDimension) {
            this.mResizeMaxDimension = maxDimension;
            this.mResizeArea = -1;
            return this;
        }

        @NonNull
        public Builder resizeBitmapArea(int area) {
            this.mResizeArea = area;
            this.mResizeMaxDimension = -1;
            return this;
        }

        @NonNull
        public Builder clearFilters() {
            this.mFilters.clear();
            return this;
        }

        @NonNull
        public Builder addFilter(Filter filter) {
            if (filter != null) {
                this.mFilters.add(filter);
            }
            return this;
        }

        @NonNull
        public Builder setRegion(int left, int top, int right, int bottom) {
            if (this.mBitmap != null) {
                if (this.mRegion == null) {
                    this.mRegion = new Rect();
                }
                this.mRegion.set(0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight());
                if (!this.mRegion.intersect(left, top, right, bottom)) {
                    throw new IllegalArgumentException("The given region must intersect with the Bitmap's dimensions.");
                }
            }
            return this;
        }

        @NonNull
        public Builder clearRegion() {
            this.mRegion = null;
            return this;
        }

        @NonNull
        public Builder addTarget(@NonNull Target target) {
            if (!this.mTargets.contains(target)) {
                this.mTargets.add(target);
            }
            return this;
        }

        @NonNull
        public Builder clearTargets() {
            List list = this.mTargets;
            if (list != null) {
                list.clear();
            }
            return this;
        }

        @NonNull
        public Palette generate() {
            List<Swatch> swatches;
            TimingLogger logger = null;
            Bitmap bitmap = this.mBitmap;
            if (bitmap != null) {
                Filter[] filterArr;
                bitmap = scaleBitmapDown(bitmap);
                if (logger != null) {
                    logger.addSplit("Processed Bitmap");
                }
                Rect region = this.mRegion;
                if (bitmap != this.mBitmap && region != null) {
                    double scale = (double) bitmap.getWidth();
                    double width = (double) this.mBitmap.getWidth();
                    Double.isNaN(scale);
                    Double.isNaN(width);
                    scale /= width;
                    width = (double) region.left;
                    Double.isNaN(width);
                    region.left = (int) Math.floor(width * scale);
                    width = (double) region.top;
                    Double.isNaN(width);
                    region.top = (int) Math.floor(width * scale);
                    width = (double) region.right;
                    Double.isNaN(width);
                    region.right = Math.min((int) Math.ceil(width * scale), bitmap.getWidth());
                    width = (double) region.bottom;
                    Double.isNaN(width);
                    region.bottom = Math.min((int) Math.ceil(width * scale), bitmap.getHeight());
                }
                int[] pixelsFromBitmap = getPixelsFromBitmap(bitmap);
                int i = this.mMaxColors;
                if (this.mFilters.isEmpty()) {
                    filterArr = null;
                } else {
                    List list = this.mFilters;
                    filterArr = (Filter[]) list.toArray(new Filter[list.size()]);
                }
                ColorCutQuantizer quantizer = new ColorCutQuantizer(pixelsFromBitmap, i, filterArr);
                if (bitmap != this.mBitmap) {
                    bitmap.recycle();
                }
                swatches = quantizer.getQuantizedColors();
                if (logger != null) {
                    logger.addSplit("Color quantization completed");
                }
            } else {
                swatches = this.mSwatches;
            }
            Palette p = new Palette(swatches, this.mTargets);
            p.generate();
            if (logger != null) {
                logger.addSplit("Created Palette");
                logger.dumpToLog();
            }
            return p;
        }

        @NonNull
        public AsyncTask<Bitmap, Void, Palette> generate(@NonNull final PaletteAsyncListener listener) {
            if (listener != null) {
                return new AsyncTask<Bitmap, Void, Palette>() {
                    protected Palette doInBackground(Bitmap... params) {
                        try {
                            return Builder.this.generate();
                        } catch (Exception e) {
                            Log.e(Palette.LOG_TAG, "Exception thrown during async generate", e);
                            return null;
                        }
                    }

                    protected void onPostExecute(Palette colorExtractor) {
                        listener.onGenerated(colorExtractor);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Bitmap[]{this.mBitmap});
            }
            throw new IllegalArgumentException("listener can not be null");
        }

        private int[] getPixelsFromBitmap(Bitmap bitmap) {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            int[] pixels = new int[(bitmapWidth * bitmapHeight)];
            bitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
            int regionWidth = this.mRegion;
            if (regionWidth == 0) {
                return pixels;
            }
            regionWidth = regionWidth.width();
            int regionHeight = this.mRegion.height();
            int[] subsetPixels = new int[(regionWidth * regionHeight)];
            for (int row = 0; row < regionHeight; row++) {
                System.arraycopy(pixels, ((this.mRegion.top + row) * bitmapWidth) + this.mRegion.left, subsetPixels, row * regionWidth, regionWidth);
            }
            return subsetPixels;
        }

        private Bitmap scaleBitmapDown(Bitmap bitmap) {
            int bitmapArea;
            double d;
            double scaleRatio = -1.0d;
            int i;
            double d2;
            if (this.mResizeArea > 0) {
                bitmapArea = bitmap.getWidth() * bitmap.getHeight();
                i = this.mResizeArea;
                if (bitmapArea > i) {
                    d = (double) i;
                    d2 = (double) bitmapArea;
                    Double.isNaN(d);
                    Double.isNaN(d2);
                    scaleRatio = Math.sqrt(d / d2);
                }
            } else if (this.mResizeMaxDimension > 0) {
                bitmapArea = Math.max(bitmap.getWidth(), bitmap.getHeight());
                i = this.mResizeMaxDimension;
                if (bitmapArea > i) {
                    d = (double) i;
                    d2 = (double) bitmapArea;
                    Double.isNaN(d);
                    Double.isNaN(d2);
                    scaleRatio = d / d2;
                }
                if (scaleRatio <= 0.0d) {
                    return bitmap;
                }
                double width = (double) bitmap.getWidth();
                Double.isNaN(width);
                bitmapArea = (int) Math.ceil(width * scaleRatio);
                d = (double) bitmap.getHeight();
                Double.isNaN(d);
                return Bitmap.createScaledBitmap(bitmap, bitmapArea, (int) Math.ceil(d * scaleRatio), false);
            }
            if (scaleRatio <= 0.0d) {
                return bitmap;
            }
            double width2 = (double) bitmap.getWidth();
            Double.isNaN(width2);
            bitmapArea = (int) Math.ceil(width2 * scaleRatio);
            d = (double) bitmap.getHeight();
            Double.isNaN(d);
            return Bitmap.createScaledBitmap(bitmap, bitmapArea, (int) Math.ceil(d * scaleRatio), false);
        }
    }

    public interface Filter {
        boolean isAllowed(@ColorInt int i, @NonNull float[] fArr);
    }

    public interface PaletteAsyncListener {
        void onGenerated(@NonNull Palette palette);
    }

    public static final class Swatch {
        private final int mBlue;
        private int mBodyTextColor;
        private boolean mGeneratedTextColors;
        private final int mGreen;
        private float[] mHsl;
        private final int mPopulation;
        private final int mRed;
        private final int mRgb;
        private int mTitleTextColor;

        public Swatch(@ColorInt int color, int population) {
            this.mRed = Color.red(color);
            this.mGreen = Color.green(color);
            this.mBlue = Color.blue(color);
            this.mRgb = color;
            this.mPopulation = population;
        }

        Swatch(int red, int green, int blue, int population) {
            this.mRed = red;
            this.mGreen = green;
            this.mBlue = blue;
            this.mRgb = Color.rgb(red, green, blue);
            this.mPopulation = population;
        }

        Swatch(float[] hsl, int population) {
            this(ColorUtils.HSLToColor(hsl), population);
            this.mHsl = hsl;
        }

        @ColorInt
        public int getRgb() {
            return this.mRgb;
        }

        @NonNull
        public float[] getHsl() {
            if (this.mHsl == null) {
                this.mHsl = new float[3];
            }
            ColorUtils.RGBToHSL(this.mRed, this.mGreen, this.mBlue, this.mHsl);
            return this.mHsl;
        }

        public int getPopulation() {
            return this.mPopulation;
        }

        @ColorInt
        public int getTitleTextColor() {
            ensureTextColorsGenerated();
            return this.mTitleTextColor;
        }

        @ColorInt
        public int getBodyTextColor() {
            ensureTextColorsGenerated();
            return this.mBodyTextColor;
        }

        private void ensureTextColorsGenerated() {
            if (!this.mGeneratedTextColors) {
                int lightBodyAlpha = ColorUtils.calculateMinimumAlpha(-1, this.mRgb, Palette.MIN_CONTRAST_BODY_TEXT);
                int lightTitleAlpha = ColorUtils.calculateMinimumAlpha(-1, this.mRgb, Palette.MIN_CONTRAST_TITLE_TEXT);
                if (lightBodyAlpha == -1 || lightTitleAlpha == -1) {
                    int darkBodyAlpha = ColorUtils.calculateMinimumAlpha(ViewCompat.MEASURED_STATE_MASK, this.mRgb, Palette.MIN_CONTRAST_BODY_TEXT);
                    int darkTitleAlpha = ColorUtils.calculateMinimumAlpha(ViewCompat.MEASURED_STATE_MASK, this.mRgb, Palette.MIN_CONTRAST_TITLE_TEXT);
                    if (darkBodyAlpha == -1 || darkTitleAlpha == -1) {
                        int alphaComponent;
                        int alphaComponent2;
                        if (lightBodyAlpha != -1) {
                            alphaComponent = ColorUtils.setAlphaComponent(-1, lightBodyAlpha);
                        } else {
                            alphaComponent = ColorUtils.setAlphaComponent(ViewCompat.MEASURED_STATE_MASK, darkBodyAlpha);
                        }
                        this.mBodyTextColor = alphaComponent;
                        if (lightTitleAlpha != -1) {
                            alphaComponent2 = ColorUtils.setAlphaComponent(-1, lightTitleAlpha);
                        } else {
                            alphaComponent2 = ColorUtils.setAlphaComponent(ViewCompat.MEASURED_STATE_MASK, darkTitleAlpha);
                        }
                        this.mTitleTextColor = alphaComponent2;
                        this.mGeneratedTextColors = true;
                    } else {
                        this.mBodyTextColor = ColorUtils.setAlphaComponent(ViewCompat.MEASURED_STATE_MASK, darkBodyAlpha);
                        this.mTitleTextColor = ColorUtils.setAlphaComponent(ViewCompat.MEASURED_STATE_MASK, darkTitleAlpha);
                        this.mGeneratedTextColors = true;
                        return;
                    }
                }
                this.mBodyTextColor = ColorUtils.setAlphaComponent(-1, lightBodyAlpha);
                this.mTitleTextColor = ColorUtils.setAlphaComponent(-1, lightTitleAlpha);
                this.mGeneratedTextColors = true;
            }
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(getClass().getSimpleName());
            stringBuilder.append(" [RGB: #");
            stringBuilder.append(Integer.toHexString(getRgb()));
            stringBuilder.append(']');
            stringBuilder.append(" [HSL: ");
            stringBuilder.append(Arrays.toString(getHsl()));
            stringBuilder.append(']');
            stringBuilder.append(" [Population: ");
            stringBuilder.append(this.mPopulation);
            stringBuilder.append(']');
            stringBuilder.append(" [Title Text: #");
            stringBuilder.append(Integer.toHexString(getTitleTextColor()));
            stringBuilder.append(']');
            stringBuilder.append(" [Body Text: #");
            stringBuilder.append(Integer.toHexString(getBodyTextColor()));
            stringBuilder.append(']');
            return stringBuilder.toString();
        }

        public boolean equals(Object o) {
            boolean z = true;
            if (this == o) {
                return true;
            }
            if (o != null) {
                if (getClass() == o.getClass()) {
                    Swatch swatch = (Swatch) o;
                    if (this.mPopulation != swatch.mPopulation || this.mRgb != swatch.mRgb) {
                        z = false;
                    }
                    return z;
                }
            }
            return false;
        }

        public int hashCode() {
            return (this.mRgb * 31) + this.mPopulation;
        }
    }

    /* renamed from: android.support.v7.graphics.Palette$1 */
    static class C08621 implements Filter {
        private static final float BLACK_MAX_LIGHTNESS = 0.05f;
        private static final float WHITE_MIN_LIGHTNESS = 0.95f;

        C08621() {
        }

        public boolean isAllowed(int rgb, float[] hsl) {
            return (isWhite(hsl) || isBlack(hsl) || isNearRedILine(hsl)) ? false : true;
        }

        private boolean isBlack(float[] hslColor) {
            return hslColor[2] <= BLACK_MAX_LIGHTNESS;
        }

        private boolean isWhite(float[] hslColor) {
            return hslColor[2] >= WHITE_MIN_LIGHTNESS;
        }

        private boolean isNearRedILine(float[] hslColor) {
            return hslColor[0] >= 10.0f && hslColor[0] <= 37.0f && hslColor[1] <= 0.82f;
        }
    }

    @NonNull
    public static Builder from(@NonNull Bitmap bitmap) {
        return new Builder(bitmap);
    }

    @NonNull
    public static Palette from(@NonNull List<Swatch> swatches) {
        return new Builder((List) swatches).generate();
    }

    @Deprecated
    public static Palette generate(Bitmap bitmap) {
        return from(bitmap).generate();
    }

    @Deprecated
    public static Palette generate(Bitmap bitmap, int numColors) {
        return from(bitmap).maximumColorCount(numColors).generate();
    }

    @Deprecated
    public static AsyncTask<Bitmap, Void, Palette> generateAsync(Bitmap bitmap, PaletteAsyncListener listener) {
        return from(bitmap).generate(listener);
    }

    @Deprecated
    public static AsyncTask<Bitmap, Void, Palette> generateAsync(Bitmap bitmap, int numColors, PaletteAsyncListener listener) {
        return from(bitmap).maximumColorCount(numColors).generate(listener);
    }

    Palette(List<Swatch> swatches, List<Target> targets) {
        this.mSwatches = swatches;
        this.mTargets = targets;
    }

    @NonNull
    public List<Swatch> getSwatches() {
        return Collections.unmodifiableList(this.mSwatches);
    }

    @NonNull
    public List<Target> getTargets() {
        return Collections.unmodifiableList(this.mTargets);
    }

    @Nullable
    public Swatch getVibrantSwatch() {
        return getSwatchForTarget(Target.VIBRANT);
    }

    @Nullable
    public Swatch getLightVibrantSwatch() {
        return getSwatchForTarget(Target.LIGHT_VIBRANT);
    }

    @Nullable
    public Swatch getDarkVibrantSwatch() {
        return getSwatchForTarget(Target.DARK_VIBRANT);
    }

    @Nullable
    public Swatch getMutedSwatch() {
        return getSwatchForTarget(Target.MUTED);
    }

    @Nullable
    public Swatch getLightMutedSwatch() {
        return getSwatchForTarget(Target.LIGHT_MUTED);
    }

    @Nullable
    public Swatch getDarkMutedSwatch() {
        return getSwatchForTarget(Target.DARK_MUTED);
    }

    @ColorInt
    public int getVibrantColor(@ColorInt int defaultColor) {
        return getColorForTarget(Target.VIBRANT, defaultColor);
    }

    @ColorInt
    public int getLightVibrantColor(@ColorInt int defaultColor) {
        return getColorForTarget(Target.LIGHT_VIBRANT, defaultColor);
    }

    @ColorInt
    public int getDarkVibrantColor(@ColorInt int defaultColor) {
        return getColorForTarget(Target.DARK_VIBRANT, defaultColor);
    }

    @ColorInt
    public int getMutedColor(@ColorInt int defaultColor) {
        return getColorForTarget(Target.MUTED, defaultColor);
    }

    @ColorInt
    public int getLightMutedColor(@ColorInt int defaultColor) {
        return getColorForTarget(Target.LIGHT_MUTED, defaultColor);
    }

    @ColorInt
    public int getDarkMutedColor(@ColorInt int defaultColor) {
        return getColorForTarget(Target.DARK_MUTED, defaultColor);
    }

    @Nullable
    public Swatch getSwatchForTarget(@NonNull Target target) {
        return (Swatch) this.mSelectedSwatches.get(target);
    }

    @ColorInt
    public int getColorForTarget(@NonNull Target target, @ColorInt int defaultColor) {
        Swatch swatch = getSwatchForTarget(target);
        return swatch != null ? swatch.getRgb() : defaultColor;
    }

    @Nullable
    public Swatch getDominantSwatch() {
        return this.mDominantSwatch;
    }

    @ColorInt
    public int getDominantColor(@ColorInt int defaultColor) {
        Swatch swatch = this.mDominantSwatch;
        return swatch != null ? swatch.getRgb() : defaultColor;
    }

    void generate() {
        int count = this.mTargets.size();
        for (int i = 0; i < count; i++) {
            Target target = (Target) this.mTargets.get(i);
            target.normalizeWeights();
            this.mSelectedSwatches.put(target, generateScoredTarget(target));
        }
        this.mUsedColors.clear();
    }

    private Swatch generateScoredTarget(Target target) {
        Swatch maxScoreSwatch = getMaxScoredSwatchForTarget(target);
        if (maxScoreSwatch != null && target.isExclusive()) {
            this.mUsedColors.append(maxScoreSwatch.getRgb(), true);
        }
        return maxScoreSwatch;
    }

    private Swatch getMaxScoredSwatchForTarget(Target target) {
        float maxScore = 0.0f;
        Swatch maxScoreSwatch = null;
        int count = this.mSwatches.size();
        for (int i = 0; i < count; i++) {
            Swatch swatch = (Swatch) this.mSwatches.get(i);
            if (shouldBeScoredForTarget(swatch, target)) {
                float score = generateScore(swatch, target);
                if (maxScoreSwatch != null) {
                    if (score > maxScore) {
                    }
                }
                maxScoreSwatch = swatch;
                maxScore = score;
            }
        }
        return maxScoreSwatch;
    }

    private boolean shouldBeScoredForTarget(Swatch swatch, Target target) {
        float[] hsl = swatch.getHsl();
        if (hsl[1] >= target.getMinimumSaturation() && hsl[1] <= target.getMaximumSaturation()) {
            if (hsl[2] >= target.getMinimumLightness() && hsl[2] <= target.getMaximumLightness()) {
                if (!this.mUsedColors.get(swatch.getRgb())) {
                    return true;
                }
            }
        }
        return false;
    }

    private float generateScore(Swatch swatch, Target target) {
        float[] hsl = swatch.getHsl();
        float saturationScore = 0.0f;
        float luminanceScore = 0.0f;
        float populationScore = 0.0f;
        Swatch swatch2 = this.mDominantSwatch;
        int maxPopulation = swatch2 != null ? swatch2.getPopulation() : 1;
        if (target.getSaturationWeight() > 0.0f) {
            saturationScore = target.getSaturationWeight() * (1.0f - Math.abs(hsl[1] - target.getTargetSaturation()));
        }
        if (target.getLightnessWeight() > 0.0f) {
            luminanceScore = target.getLightnessWeight() * (1.0f - Math.abs(hsl[2] - target.getTargetLightness()));
        }
        if (target.getPopulationWeight() > 0.0f) {
            populationScore = target.getPopulationWeight() * (((float) swatch.getPopulation()) / ((float) maxPopulation));
        }
        return (saturationScore + luminanceScore) + populationScore;
    }

    private Swatch findDominantSwatch() {
        int maxPop = Integer.MIN_VALUE;
        Swatch maxSwatch = null;
        int count = this.mSwatches.size();
        for (int i = 0; i < count; i++) {
            Swatch swatch = (Swatch) this.mSwatches.get(i);
            if (swatch.getPopulation() > maxPop) {
                maxSwatch = swatch;
                maxPop = swatch.getPopulation();
            }
        }
        return maxSwatch;
    }

    private static float[] copyHslValues(Swatch color) {
        float[] newHsl = new float[3];
        System.arraycopy(color.getHsl(), 0, newHsl, 0, 3);
        return newHsl;
    }
}

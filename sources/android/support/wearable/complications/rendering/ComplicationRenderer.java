package android.support.wearable.complications.rendering;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.Icon.OnDrawableLoadedListener;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationData.Builder;
import android.support.wearable.complications.ComplicationText;
import android.support.wearable.complications.rendering.utils.IconLayoutHelper;
import android.support.wearable.complications.rendering.utils.LargeImageLayoutHelper;
import android.support.wearable.complications.rendering.utils.LayoutHelper;
import android.support.wearable.complications.rendering.utils.LayoutUtils;
import android.support.wearable.complications.rendering.utils.LongTextLayoutHelper;
import android.support.wearable.complications.rendering.utils.RangedValueLayoutHelper;
import android.support.wearable.complications.rendering.utils.ShortTextLayoutHelper;
import android.support.wearable.complications.rendering.utils.SmallImageLayoutHelper;
import android.text.Layout.Alignment;
import android.text.TextPaint;
import java.util.Objects;

@TargetApi(24)
class ComplicationRenderer {
    @VisibleForTesting
    static final boolean DEBUG_MODE = false;
    private static final float ICON_SIZE_FRACTION = 1.0f;
    private static final float LARGE_IMAGE_SIZE_FRACTION = 1.0f;
    @VisibleForTesting
    static final int RANGED_VALUE_START_ANGLE = -90;
    private static final float SMALL_IMAGE_SIZE_FRACTION = 0.95f;
    @VisibleForTesting
    static final int STROKE_GAP_IN_DEGREES = 4;
    private static final String TAG = "ComplicationRenderer";
    private static final float TEXT_PADDING_HEIGHT_FRACTION = 0.1f;
    @VisibleForTesting
    PaintSet mActivePaintSet = null;
    private ComplicationStyle mActiveStyle;
    @VisibleForTesting
    PaintSet mAmbientPaintSet = null;
    private ComplicationStyle mAmbientStyle;
    private final Rect mBackgroundBounds = new Rect();
    private final RectF mBackgroundBoundsF = new RectF();
    private final Rect mBounds = new Rect();
    @Nullable
    private Drawable mBurnInProtectionIcon;
    @Nullable
    private Drawable mBurnInProtectionSmallImage;
    private ComplicationData mComplicationData;
    private final Context mContext;
    @Nullable
    private Paint mDebugPaint;
    private boolean mHasNoData;
    @Nullable
    private Drawable mIcon;
    private final Rect mIconBounds = new Rect();
    @Nullable
    private OnInvalidateListener mInvalidateListener;
    @Nullable
    private Drawable mLargeImage;
    private final Rect mLargeImageBounds = new Rect();
    private final Rect mMainTextBounds = new Rect();
    @Nullable
    private TextPaint mMainTextPaint = null;
    private final TextRenderer mMainTextRenderer = new TextRenderer();
    private CharSequence mNoDataText = "";
    private final Rect mRangedValueBounds = new Rect();
    private final RectF mRangedValueBoundsF = new RectF();
    private boolean mRangedValueProgressHidden;
    private final RoundedDrawable mRoundedBackgroundDrawable = new RoundedDrawable();
    private final RoundedDrawable mRoundedLargeImage = new RoundedDrawable();
    private final RoundedDrawable mRoundedSmallImage = new RoundedDrawable();
    @Nullable
    private Drawable mSmallImage;
    private final Rect mSmallImageBounds = new Rect();
    private final Rect mSubTextBounds = new Rect();
    @Nullable
    private TextPaint mSubTextPaint = null;
    private final TextRenderer mSubTextRenderer = new TextRenderer();

    /* renamed from: android.support.wearable.complications.rendering.ComplicationRenderer$1 */
    class C04161 implements OnDrawableLoadedListener {
        C04161() {
        }

        public void onDrawableLoaded(Drawable d) {
            if (d != null) {
                ComplicationRenderer.this.mIcon = d;
                ComplicationRenderer.this.mIcon.mutate();
                ComplicationRenderer.this.invalidate();
            }
        }
    }

    /* renamed from: android.support.wearable.complications.rendering.ComplicationRenderer$2 */
    class C04172 implements OnDrawableLoadedListener {
        C04172() {
        }

        public void onDrawableLoaded(Drawable d) {
            if (d != null) {
                ComplicationRenderer.this.mBurnInProtectionIcon = d;
                ComplicationRenderer.this.mBurnInProtectionIcon.mutate();
                ComplicationRenderer.this.invalidate();
            }
        }
    }

    /* renamed from: android.support.wearable.complications.rendering.ComplicationRenderer$3 */
    class C04183 implements OnDrawableLoadedListener {
        C04183() {
        }

        public void onDrawableLoaded(Drawable d) {
            if (d != null) {
                ComplicationRenderer.this.mSmallImage = d;
                ComplicationRenderer.this.invalidate();
            }
        }
    }

    /* renamed from: android.support.wearable.complications.rendering.ComplicationRenderer$4 */
    class C04194 implements OnDrawableLoadedListener {
        C04194() {
        }

        public void onDrawableLoaded(Drawable d) {
            if (d != null) {
                ComplicationRenderer.this.mBurnInProtectionSmallImage = d;
                ComplicationRenderer.this.invalidate();
            }
        }
    }

    /* renamed from: android.support.wearable.complications.rendering.ComplicationRenderer$5 */
    class C04205 implements OnDrawableLoadedListener {
        C04205() {
        }

        public void onDrawableLoaded(Drawable d) {
            if (d != null) {
                ComplicationRenderer.this.mLargeImage = d;
                ComplicationRenderer.this.invalidate();
            }
        }
    }

    interface OnInvalidateListener {
        void onInvalidate();
    }

    @VisibleForTesting
    static class PaintSet {
        private static final int SINGLE_COLOR_FILTER_ALPHA_CUTOFF = 127;
        final Paint backgroundPaint;
        final Paint borderPaint;
        final boolean burnInProtection;
        final Paint highlightPaint;
        final ColorFilter iconColorFilter;
        final Paint inProgressPaint;
        final boolean isAmbientStyle;
        final boolean lowBitAmbient;
        final TextPaint primaryTextPaint;
        final Paint remainingPaint;
        final TextPaint secondaryTextPaint;
        final ComplicationStyle style;

        PaintSet(ComplicationStyle style, boolean isAmbientStyle, boolean lowBitAmbient, boolean burnInProtection) {
            boolean antiAlias;
            ColorFilter colorMatrixColorFilter;
            this.style = style;
            this.isAmbientStyle = isAmbientStyle;
            this.lowBitAmbient = lowBitAmbient;
            this.burnInProtection = burnInProtection;
            if (isAmbientStyle) {
                if (lowBitAmbient) {
                    antiAlias = false;
                    this.primaryTextPaint = new TextPaint();
                    this.primaryTextPaint.setColor(style.getTextColor());
                    this.primaryTextPaint.setAntiAlias(antiAlias);
                    this.primaryTextPaint.setTypeface(style.getTextTypeface());
                    this.primaryTextPaint.setTextSize((float) style.getTextSize());
                    this.primaryTextPaint.setAntiAlias(antiAlias);
                    if (antiAlias) {
                        colorMatrixColorFilter = new ColorMatrixColorFilter(createSingleColorMatrix(style.getIconColor()));
                    } else {
                        colorMatrixColorFilter = new PorterDuffColorFilter(style.getIconColor(), Mode.SRC_IN);
                    }
                    this.iconColorFilter = colorMatrixColorFilter;
                    this.secondaryTextPaint = new TextPaint();
                    this.secondaryTextPaint.setColor(style.getTitleColor());
                    this.secondaryTextPaint.setAntiAlias(antiAlias);
                    this.secondaryTextPaint.setTypeface(style.getTitleTypeface());
                    this.secondaryTextPaint.setTextSize((float) style.getTitleSize());
                    this.secondaryTextPaint.setAntiAlias(antiAlias);
                    this.inProgressPaint = new Paint();
                    this.inProgressPaint.setColor(style.getRangedValuePrimaryColor());
                    this.inProgressPaint.setStyle(Style.STROKE);
                    this.inProgressPaint.setAntiAlias(antiAlias);
                    this.inProgressPaint.setStrokeWidth((float) style.getRangedValueRingWidth());
                    this.remainingPaint = new Paint();
                    this.remainingPaint.setColor(style.getRangedValueSecondaryColor());
                    this.remainingPaint.setStyle(Style.STROKE);
                    this.remainingPaint.setAntiAlias(antiAlias);
                    this.remainingPaint.setStrokeWidth((float) style.getRangedValueRingWidth());
                    this.borderPaint = new Paint();
                    this.borderPaint.setStyle(Style.STROKE);
                    this.borderPaint.setColor(style.getBorderColor());
                    if (style.getBorderStyle() == 2) {
                        this.borderPaint.setPathEffect(new DashPathEffect(new float[]{(float) style.getBorderDashWidth(), (float) style.getBorderDashGap()}, 0.0f));
                    }
                    if (style.getBorderStyle() == 0) {
                        this.borderPaint.setAlpha(0);
                    }
                    this.borderPaint.setStrokeWidth((float) style.getBorderWidth());
                    this.borderPaint.setAntiAlias(antiAlias);
                    this.backgroundPaint = new Paint();
                    this.backgroundPaint.setColor(style.getBackgroundColor());
                    this.backgroundPaint.setAntiAlias(antiAlias);
                    this.highlightPaint = new Paint();
                    this.highlightPaint.setColor(style.getHighlightColor());
                    this.highlightPaint.setAntiAlias(antiAlias);
                }
            }
            antiAlias = true;
            this.primaryTextPaint = new TextPaint();
            this.primaryTextPaint.setColor(style.getTextColor());
            this.primaryTextPaint.setAntiAlias(antiAlias);
            this.primaryTextPaint.setTypeface(style.getTextTypeface());
            this.primaryTextPaint.setTextSize((float) style.getTextSize());
            this.primaryTextPaint.setAntiAlias(antiAlias);
            if (antiAlias) {
                colorMatrixColorFilter = new ColorMatrixColorFilter(createSingleColorMatrix(style.getIconColor()));
            } else {
                colorMatrixColorFilter = new PorterDuffColorFilter(style.getIconColor(), Mode.SRC_IN);
            }
            this.iconColorFilter = colorMatrixColorFilter;
            this.secondaryTextPaint = new TextPaint();
            this.secondaryTextPaint.setColor(style.getTitleColor());
            this.secondaryTextPaint.setAntiAlias(antiAlias);
            this.secondaryTextPaint.setTypeface(style.getTitleTypeface());
            this.secondaryTextPaint.setTextSize((float) style.getTitleSize());
            this.secondaryTextPaint.setAntiAlias(antiAlias);
            this.inProgressPaint = new Paint();
            this.inProgressPaint.setColor(style.getRangedValuePrimaryColor());
            this.inProgressPaint.setStyle(Style.STROKE);
            this.inProgressPaint.setAntiAlias(antiAlias);
            this.inProgressPaint.setStrokeWidth((float) style.getRangedValueRingWidth());
            this.remainingPaint = new Paint();
            this.remainingPaint.setColor(style.getRangedValueSecondaryColor());
            this.remainingPaint.setStyle(Style.STROKE);
            this.remainingPaint.setAntiAlias(antiAlias);
            this.remainingPaint.setStrokeWidth((float) style.getRangedValueRingWidth());
            this.borderPaint = new Paint();
            this.borderPaint.setStyle(Style.STROKE);
            this.borderPaint.setColor(style.getBorderColor());
            if (style.getBorderStyle() == 2) {
                this.borderPaint.setPathEffect(new DashPathEffect(new float[]{(float) style.getBorderDashWidth(), (float) style.getBorderDashGap()}, 0.0f));
            }
            if (style.getBorderStyle() == 0) {
                this.borderPaint.setAlpha(0);
            }
            this.borderPaint.setStrokeWidth((float) style.getBorderWidth());
            this.borderPaint.setAntiAlias(antiAlias);
            this.backgroundPaint = new Paint();
            this.backgroundPaint.setColor(style.getBackgroundColor());
            this.backgroundPaint.setAntiAlias(antiAlias);
            this.highlightPaint = new Paint();
            this.highlightPaint.setColor(style.getHighlightColor());
            this.highlightPaint.setAntiAlias(antiAlias);
        }

        boolean isInBurnInProtectionMode() {
            return this.isAmbientStyle && this.burnInProtection;
        }

        @VisibleForTesting
        static ColorMatrix createSingleColorMatrix(int color) {
            return new ColorMatrix(new float[]{0.0f, 0.0f, 0.0f, 0.0f, (float) Color.red(color), 0.0f, 0.0f, 0.0f, 0.0f, (float) Color.green(color), 0.0f, 0.0f, 0.0f, 0.0f, (float) Color.blue(color), 0.0f, 0.0f, 0.0f, 255.0f, -32385.0f});
        }
    }

    public ComplicationRenderer(Context context, ComplicationStyle activeStyle, ComplicationStyle ambientStyle) {
        this.mContext = context;
        updateStyle(activeStyle, ambientStyle);
    }

    public void updateStyle(ComplicationStyle activeStyle, ComplicationStyle ambientStyle) {
        this.mActiveStyle = activeStyle;
        this.mAmbientStyle = ambientStyle;
        this.mActivePaintSet = new PaintSet(activeStyle, false, false, false);
        this.mAmbientPaintSet = new PaintSet(ambientStyle, true, false, false);
        calculateBounds();
    }

    public void setComplicationData(@Nullable ComplicationData data) {
        if (!Objects.equals(this.mComplicationData, data)) {
            if (data == null) {
                this.mComplicationData = null;
                return;
            }
            if (data.getType() != 10) {
                this.mComplicationData = data;
                this.mHasNoData = false;
            } else if (!this.mHasNoData) {
                this.mHasNoData = true;
                this.mComplicationData = new Builder(3).setShortText(ComplicationText.plainText(this.mNoDataText)).build();
            } else {
                return;
            }
            loadDrawableIconAndImages();
            calculateBounds();
        }
    }

    public boolean setBounds(Rect bounds) {
        boolean shouldCalculateBounds = true;
        if (this.mBounds.width() == bounds.width() && this.mBounds.height() == bounds.height()) {
            shouldCalculateBounds = false;
        }
        this.mBounds.set(bounds);
        if (shouldCalculateBounds) {
            calculateBounds();
        }
        return shouldCalculateBounds;
    }

    public void setNoDataText(@Nullable CharSequence noDataText) {
        if (noDataText == null) {
            noDataText = "";
        }
        this.mNoDataText = noDataText.subSequence(0, noDataText.length());
        if (this.mHasNoData) {
            this.mHasNoData = false;
            setComplicationData(new Builder(10).build());
        }
    }

    public void setRangedValueProgressHidden(boolean hidden) {
        if (this.mRangedValueProgressHidden != hidden) {
            this.mRangedValueProgressHidden = hidden;
            calculateBounds();
        }
    }

    boolean isRangedValueProgressHidden() {
        return this.mRangedValueProgressHidden;
    }

    public void draw(Canvas canvas, long currentTimeMillis, boolean inAmbientMode, boolean lowBitAmbient, boolean burnInProtection, boolean showTapHighlight) {
        ComplicationData complicationData = this.mComplicationData;
        if (complicationData != null) {
            if (complicationData.getType() != 2) {
                if (this.mComplicationData.getType() != 1) {
                    if (this.mComplicationData.isActive(currentTimeMillis)) {
                        if (!this.mBounds.isEmpty()) {
                            if (inAmbientMode && (this.mAmbientPaintSet.lowBitAmbient != lowBitAmbient || this.mAmbientPaintSet.burnInProtection != burnInProtection)) {
                                this.mAmbientPaintSet = new PaintSet(this.mAmbientStyle, true, lowBitAmbient, burnInProtection);
                            }
                            PaintSet currentPaintSet = inAmbientMode ? this.mAmbientPaintSet : this.mActivePaintSet;
                            updateComplicationTexts(currentTimeMillis);
                            canvas.save();
                            canvas.translate((float) this.mBounds.left, (float) this.mBounds.top);
                            drawBackground(canvas, currentPaintSet);
                            drawIcon(canvas, currentPaintSet);
                            drawSmallImage(canvas, currentPaintSet);
                            drawLargeImage(canvas, currentPaintSet);
                            drawRangedValue(canvas, currentPaintSet);
                            drawMainText(canvas, currentPaintSet);
                            drawSubText(canvas, currentPaintSet);
                            if (showTapHighlight) {
                                drawHighlight(canvas, currentPaintSet);
                            }
                            drawBorders(canvas, currentPaintSet);
                            canvas.restore();
                        }
                    }
                }
            }
        }
    }

    public void setOnInvalidateListener(OnInvalidateListener listener) {
        this.mInvalidateListener = listener;
    }

    private void invalidate() {
        OnInvalidateListener onInvalidateListener = this.mInvalidateListener;
        if (onInvalidateListener != null) {
            onInvalidateListener.onInvalidate();
        }
    }

    private void updateComplicationTexts(long currentTimeMillis) {
        if (this.mComplicationData.getShortText() != null) {
            this.mMainTextRenderer.setMaxLines(1);
            this.mMainTextRenderer.setText(this.mComplicationData.getShortText().getText(this.mContext, currentTimeMillis));
            if (this.mComplicationData.getShortTitle() != null) {
                this.mSubTextRenderer.setText(this.mComplicationData.getShortTitle().getText(this.mContext, currentTimeMillis));
            } else {
                this.mSubTextRenderer.setText("");
            }
        }
        if (this.mComplicationData.getLongText() != null) {
            this.mMainTextRenderer.setText(this.mComplicationData.getLongText().getText(this.mContext, currentTimeMillis));
            if (this.mComplicationData.getLongTitle() != null) {
                this.mSubTextRenderer.setText(this.mComplicationData.getLongTitle().getText(this.mContext, currentTimeMillis));
                this.mMainTextRenderer.setMaxLines(1);
                return;
            }
            this.mSubTextRenderer.setText("");
            this.mMainTextRenderer.setMaxLines(2);
        }
    }

    private void drawBackground(Canvas canvas, PaintSet paintSet) {
        int radius = getBorderRadius(paintSet.style);
        canvas.drawRoundRect(this.mBackgroundBoundsF, (float) radius, (float) radius, paintSet.backgroundPaint);
        if (paintSet.style.getBackgroundDrawable() != null && !paintSet.isInBurnInProtectionMode()) {
            this.mRoundedBackgroundDrawable.setDrawable(paintSet.style.getBackgroundDrawable());
            this.mRoundedBackgroundDrawable.setRadius(radius);
            this.mRoundedBackgroundDrawable.setBounds(this.mBackgroundBounds);
            this.mRoundedBackgroundDrawable.draw(canvas);
        }
    }

    private void drawBorders(Canvas canvas, PaintSet paintSet) {
        if (paintSet.style.getBorderStyle() != 0) {
            int radius = getBorderRadius(paintSet.style);
            canvas.drawRoundRect(this.mBackgroundBoundsF, (float) radius, (float) radius, paintSet.borderPaint);
        }
    }

    private void drawHighlight(Canvas canvas, PaintSet paintSet) {
        if (!paintSet.isAmbientStyle) {
            int radius = getBorderRadius(paintSet.style);
            canvas.drawRoundRect(this.mBackgroundBoundsF, (float) radius, (float) radius, paintSet.highlightPaint);
        }
    }

    private void drawMainText(Canvas canvas, PaintSet paintSet) {
        if (!this.mMainTextBounds.isEmpty()) {
            if (this.mMainTextPaint != paintSet.primaryTextPaint) {
                this.mMainTextPaint = paintSet.primaryTextPaint;
                this.mMainTextRenderer.setPaint(this.mMainTextPaint);
                this.mMainTextRenderer.setInAmbientMode(paintSet.isAmbientStyle);
            }
            this.mMainTextRenderer.draw(canvas, this.mMainTextBounds);
        }
    }

    private void drawSubText(Canvas canvas, PaintSet paintSet) {
        if (!this.mSubTextBounds.isEmpty()) {
            if (this.mSubTextPaint != paintSet.secondaryTextPaint) {
                this.mSubTextPaint = paintSet.secondaryTextPaint;
                this.mSubTextRenderer.setPaint(this.mSubTextPaint);
                this.mSubTextRenderer.setInAmbientMode(paintSet.isAmbientStyle);
            }
            this.mSubTextRenderer.draw(canvas, this.mSubTextBounds);
        }
    }

    private void drawRangedValue(Canvas canvas, PaintSet paintSet) {
        if (!this.mRangedValueBoundsF.isEmpty()) {
            float interval = this.mComplicationData.getMaxValue() - this.mComplicationData.getMinValue();
            float progress = 0.0f;
            if (interval > 0.0f) {
                progress = this.mComplicationData.getValue() / interval;
            }
            float inProgressAngle = 352.0f * progress;
            float remainderAngle = 352.0f - inProgressAngle;
            int insetAmount = (int) Math.ceil((double) paintSet.inProgressPaint.getStrokeWidth());
            this.mRangedValueBoundsF.inset((float) insetAmount, (float) insetAmount);
            canvas.drawArc(this.mRangedValueBoundsF, -88.0f, inProgressAngle, false, paintSet.inProgressPaint);
            canvas.drawArc(this.mRangedValueBoundsF, 4.0f + (-88.0f + inProgressAngle), remainderAngle, false, paintSet.remainingPaint);
            this.mRangedValueBoundsF.inset((float) (-insetAmount), (float) (-insetAmount));
        }
    }

    private void drawIcon(Canvas canvas, PaintSet paintSet) {
        if (!this.mIconBounds.isEmpty()) {
            Drawable icon = this.mIcon;
            if (icon != null) {
                if (paintSet.isInBurnInProtectionMode() && this.mBurnInProtectionIcon != null) {
                    icon = this.mBurnInProtectionIcon;
                }
                icon.setColorFilter(paintSet.iconColorFilter);
                drawIconOnCanvas(canvas, this.mIconBounds, icon);
            }
        }
    }

    private void drawSmallImage(Canvas canvas, PaintSet paintSet) {
        if (!this.mSmallImageBounds.isEmpty()) {
            if (paintSet.isInBurnInProtectionMode()) {
                this.mRoundedSmallImage.setDrawable(this.mBurnInProtectionSmallImage);
                if (this.mBurnInProtectionSmallImage == null) {
                    return;
                }
            } else {
                this.mRoundedSmallImage.setDrawable(this.mSmallImage);
                if (this.mSmallImage == null) {
                    return;
                }
            }
            if (this.mComplicationData.getImageStyle() == 2) {
                this.mRoundedSmallImage.setColorFilter(null);
                this.mRoundedSmallImage.setRadius(0);
            } else {
                this.mRoundedSmallImage.setColorFilter(paintSet.style.getColorFilter());
                this.mRoundedSmallImage.setRadius(getImageBorderRadius(paintSet.style, this.mSmallImageBounds));
            }
            this.mRoundedSmallImage.setBounds(this.mSmallImageBounds);
            this.mRoundedSmallImage.draw(canvas);
        }
    }

    private void drawLargeImage(Canvas canvas, PaintSet paintSet) {
        if (!this.mLargeImageBounds.isEmpty()) {
            if (!paintSet.isInBurnInProtectionMode()) {
                this.mRoundedLargeImage.setDrawable(this.mLargeImage);
                this.mRoundedLargeImage.setRadius(getImageBorderRadius(paintSet.style, this.mLargeImageBounds));
                this.mRoundedLargeImage.setBounds(this.mLargeImageBounds);
                this.mRoundedLargeImage.setColorFilter(paintSet.style.getColorFilter());
                this.mRoundedLargeImage.draw(canvas);
            }
        }
    }

    private static void drawIconOnCanvas(Canvas canvas, Rect bounds, Drawable icon) {
        icon.setBounds(0, 0, bounds.width(), bounds.height());
        canvas.save();
        canvas.translate((float) bounds.left, (float) bounds.top);
        icon.draw(canvas);
        canvas.restore();
    }

    private int getBorderRadius(ComplicationStyle currentStyle) {
        if (this.mBounds.isEmpty()) {
            return 0;
        }
        return Math.min(Math.min(this.mBounds.height(), this.mBounds.width()) / 2, currentStyle.getBorderRadius());
    }

    @VisibleForTesting
    int getImageBorderRadius(ComplicationStyle currentStyle, Rect imageBounds) {
        if (this.mBounds.isEmpty()) {
            return 0;
        }
        return Math.max(getBorderRadius(currentStyle) - Math.min(Math.min(imageBounds.left, this.mBounds.width() - imageBounds.right), Math.min(imageBounds.top, this.mBounds.height() - imageBounds.bottom)), 0);
    }

    private void calculateBounds() {
        if (this.mComplicationData != null) {
            if (!this.mBounds.isEmpty()) {
                LayoutHelper currentLayoutHelper;
                Alignment alignment;
                Rect rect;
                this.mBackgroundBounds.set(0, 0, this.mBounds.width(), this.mBounds.height());
                this.mBackgroundBoundsF.set(0.0f, 0.0f, (float) this.mBounds.width(), (float) this.mBounds.height());
                switch (this.mComplicationData.getType()) {
                    case 3:
                    case 9:
                        currentLayoutHelper = new ShortTextLayoutHelper();
                        break;
                    case 4:
                        currentLayoutHelper = new LongTextLayoutHelper();
                        break;
                    case 5:
                        if (this.mRangedValueProgressHidden) {
                            if (this.mComplicationData.getShortText() != null) {
                                currentLayoutHelper = new ShortTextLayoutHelper();
                                break;
                            } else {
                                currentLayoutHelper = new IconLayoutHelper();
                                break;
                            }
                        }
                        currentLayoutHelper = new RangedValueLayoutHelper();
                        break;
                    case 6:
                        currentLayoutHelper = new IconLayoutHelper();
                        break;
                    case 7:
                        currentLayoutHelper = new SmallImageLayoutHelper();
                        break;
                    case 8:
                        currentLayoutHelper = new LargeImageLayoutHelper();
                        break;
                    default:
                        currentLayoutHelper = new LayoutHelper();
                        break;
                }
                currentLayoutHelper.update(this.mBounds.width(), this.mBounds.height(), this.mComplicationData);
                currentLayoutHelper.getRangedValueBounds(this.mRangedValueBounds);
                this.mRangedValueBoundsF.set(this.mRangedValueBounds);
                currentLayoutHelper.getIconBounds(this.mIconBounds);
                currentLayoutHelper.getSmallImageBounds(this.mSmallImageBounds);
                currentLayoutHelper.getLargeImageBounds(this.mLargeImageBounds);
                if (this.mComplicationData.getType() == 4) {
                    alignment = currentLayoutHelper.getLongTextAlignment();
                    currentLayoutHelper.getLongTextBounds(this.mMainTextBounds);
                    this.mMainTextRenderer.setAlignment(alignment);
                    this.mMainTextRenderer.setGravity(currentLayoutHelper.getLongTextGravity());
                    currentLayoutHelper.getLongTitleBounds(this.mSubTextBounds);
                    this.mSubTextRenderer.setAlignment(currentLayoutHelper.getLongTitleAlignment());
                    this.mSubTextRenderer.setGravity(currentLayoutHelper.getLongTitleGravity());
                } else {
                    alignment = currentLayoutHelper.getShortTextAlignment();
                    currentLayoutHelper.getShortTextBounds(this.mMainTextBounds);
                    this.mMainTextRenderer.setAlignment(alignment);
                    this.mMainTextRenderer.setGravity(currentLayoutHelper.getShortTextGravity());
                    currentLayoutHelper.getShortTitleBounds(this.mSubTextBounds);
                    this.mSubTextRenderer.setAlignment(currentLayoutHelper.getShortTitleAlignment());
                    this.mSubTextRenderer.setGravity(currentLayoutHelper.getShortTitleGravity());
                }
                if (alignment != Alignment.ALIGN_CENTER) {
                    float paddingAmount = ((float) this.mBounds.height()) * 0.1f;
                    this.mMainTextRenderer.setRelativePadding(paddingAmount / ((float) this.mMainTextBounds.width()), 0.0f, 0.0f, 0.0f);
                    this.mSubTextRenderer.setRelativePadding(paddingAmount / ((float) this.mMainTextBounds.width()), 0.0f, 0.0f, 0.0f);
                } else {
                    this.mMainTextRenderer.setRelativePadding(0.0f, 0.0f, 0.0f, 0.0f);
                    this.mSubTextRenderer.setRelativePadding(0.0f, 0.0f, 0.0f, 0.0f);
                }
                Rect innerBounds = new Rect();
                LayoutUtils.getInnerBounds(innerBounds, this.mBackgroundBounds, (float) Math.max(getBorderRadius(this.mActiveStyle), getBorderRadius(this.mAmbientStyle)));
                if (!this.mMainTextBounds.intersect(innerBounds)) {
                    this.mMainTextBounds.setEmpty();
                }
                if (!this.mSubTextBounds.intersect(innerBounds)) {
                    this.mSubTextBounds.setEmpty();
                }
                if (!this.mIconBounds.isEmpty()) {
                    rect = this.mIconBounds;
                    LayoutUtils.scaledAroundCenter(rect, rect, 1.0f);
                    LayoutUtils.fitSquareToBounds(this.mIconBounds, innerBounds);
                }
                if (!this.mSmallImageBounds.isEmpty()) {
                    rect = this.mSmallImageBounds;
                    LayoutUtils.scaledAroundCenter(rect, rect, SMALL_IMAGE_SIZE_FRACTION);
                    if (this.mComplicationData.getImageStyle() == 2) {
                        LayoutUtils.fitSquareToBounds(this.mSmallImageBounds, innerBounds);
                    }
                }
                if (!this.mLargeImageBounds.isEmpty()) {
                    rect = this.mLargeImageBounds;
                    LayoutUtils.scaledAroundCenter(rect, rect, 1.0f);
                }
            }
        }
    }

    private void loadDrawableIconAndImages() {
        Handler handler = new Handler(Looper.getMainLooper());
        Icon icon = null;
        Icon smallImage = null;
        Icon burnInProtectionSmallImage = null;
        Icon largeImage = null;
        Icon burnInProtectionIcon = null;
        this.mIcon = null;
        this.mSmallImage = null;
        this.mBurnInProtectionSmallImage = null;
        this.mLargeImage = null;
        this.mBurnInProtectionIcon = null;
        ComplicationData complicationData = this.mComplicationData;
        if (complicationData != null) {
            icon = complicationData.getIcon();
            burnInProtectionIcon = this.mComplicationData.getBurnInProtectionIcon();
            burnInProtectionSmallImage = this.mComplicationData.getBurnInProtectionSmallImage();
            smallImage = this.mComplicationData.getSmallImage();
            largeImage = this.mComplicationData.getLargeImage();
        }
        if (icon != null) {
            icon.loadDrawableAsync(this.mContext, new C04161(), handler);
        }
        if (burnInProtectionIcon != null) {
            burnInProtectionIcon.loadDrawableAsync(this.mContext, new C04172(), handler);
        }
        if (smallImage != null) {
            smallImage.loadDrawableAsync(this.mContext, new C04183(), handler);
        }
        if (burnInProtectionSmallImage != null) {
            burnInProtectionSmallImage.loadDrawableAsync(this.mContext, new C04194(), handler);
        }
        if (largeImage != null) {
            largeImage.loadDrawableAsync(this.mContext, new C04205(), handler);
        }
    }

    @VisibleForTesting
    Rect getBounds() {
        return this.mBounds;
    }

    @VisibleForTesting
    Rect getIconBounds() {
        return this.mIconBounds;
    }

    @VisibleForTesting
    Drawable getIcon() {
        return this.mIcon;
    }

    @VisibleForTesting
    Drawable getSmallImage() {
        return this.mSmallImage;
    }

    @VisibleForTesting
    Drawable getBurnInProtectionIcon() {
        return this.mBurnInProtectionIcon;
    }

    @VisibleForTesting
    Drawable getBurnInProtectionSmallImage() {
        return this.mBurnInProtectionSmallImage;
    }

    @VisibleForTesting
    RoundedDrawable getRoundedSmallImage() {
        return this.mRoundedSmallImage;
    }

    @VisibleForTesting
    Rect getMainTextBounds() {
        return this.mMainTextBounds;
    }

    @VisibleForTesting
    Rect getSubTextBounds() {
        return this.mSubTextBounds;
    }

    @VisibleForTesting
    void getComplicationInnerBounds(Rect outRect) {
        LayoutUtils.getInnerBounds(outRect, this.mBounds, (float) Math.max(getBorderRadius(this.mActiveStyle), getBorderRadius(this.mAmbientStyle)));
    }

    @VisibleForTesting
    boolean hasSameLayout(ComplicationRenderer drawable) {
        if (this.mBounds.equals(drawable.mBounds)) {
            if (this.mBackgroundBounds.equals(drawable.mBackgroundBounds)) {
                if (this.mIconBounds.equals(drawable.mIconBounds)) {
                    if (this.mLargeImageBounds.equals(drawable.mLargeImageBounds)) {
                        if (this.mSmallImageBounds.equals(drawable.mSmallImageBounds)) {
                            if (this.mMainTextBounds.equals(drawable.mMainTextBounds)) {
                                if (this.mSubTextBounds.equals(drawable.mSubTextBounds)) {
                                    if (this.mRangedValueBounds.equals(drawable.mRangedValueBounds)) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    ComplicationData getComplicationData() {
        return this.mComplicationData;
    }
}

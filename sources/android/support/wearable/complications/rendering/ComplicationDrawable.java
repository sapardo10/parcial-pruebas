package android.support.wearable.complications.rendering;

import android.annotation.TargetApi;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.wearable.C0395R;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.rendering.ComplicationStyle.Builder;
import android.support.wearable.watchface.WatchFaceService;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import java.io.IOException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@TargetApi(24)
public class ComplicationDrawable extends Drawable implements Parcelable {
    public static final int BORDER_STYLE_DASHED = 2;
    public static final int BORDER_STYLE_NONE = 0;
    public static final int BORDER_STYLE_SOLID = 1;
    public static final Creator<ComplicationDrawable> CREATOR = new C04141();
    private static final String FIELD_ACTIVE_STYLE_BUILDER = "active_style_builder";
    private static final String FIELD_AMBIENT_STYLE_BUILDER = "ambient_style_builder";
    private static final String FIELD_BOUNDS = "bounds";
    private static final String FIELD_HIGHLIGHT_DURATION = "highlight_duration";
    private static final String FIELD_NO_DATA_TEXT = "no_data_text";
    private static final String FIELD_RANGED_VALUE_PROGRESS_HIDDEN = "ranged_value_progress_hidden";
    private static final String TAG = "ComplicationDrawable";
    private final Builder mActiveStyleBuilder;
    private boolean mAlreadyStyled;
    private final Builder mAmbientStyleBuilder;
    private boolean mBurnInProtection;
    private ComplicationRenderer mComplicationRenderer;
    private Context mContext;
    private long mCurrentTimeMillis;
    private long mHighlightDuration;
    private boolean mInAmbientMode;
    private boolean mIsHighlighted;
    private boolean mIsInflatedFromXml;
    private boolean mIsStyleUpToDate;
    private boolean mLowBitAmbient;
    private final Handler mMainThreadHandler;
    private CharSequence mNoDataText;
    private boolean mRangedValueProgressHidden;
    private final OnInvalidateListener mRendererInvalidateListener;
    private final Runnable mUnhighlightRunnable;

    /* renamed from: android.support.wearable.complications.rendering.ComplicationDrawable$1 */
    class C04141 implements Creator<ComplicationDrawable> {
        C04141() {
        }

        public ComplicationDrawable createFromParcel(Parcel source) {
            return new ComplicationDrawable(source);
        }

        public ComplicationDrawable[] newArray(int size) {
            return new ComplicationDrawable[size];
        }
    }

    /* renamed from: android.support.wearable.complications.rendering.ComplicationDrawable$2 */
    class C04152 implements Runnable {
        C04152() {
        }

        public void run() {
            ComplicationDrawable.this.setIsHighlighted(false);
            ComplicationDrawable.this.invalidateSelf();
        }
    }

    /* renamed from: android.support.wearable.complications.rendering.ComplicationDrawable$3 */
    class C09113 implements OnInvalidateListener {
        C09113() {
        }

        public void onInvalidate() {
            ComplicationDrawable.this.invalidateSelf();
        }
    }

    public ComplicationDrawable() {
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
        this.mUnhighlightRunnable = new C04152();
        this.mRendererInvalidateListener = new C09113();
        this.mActiveStyleBuilder = new Builder();
        this.mAmbientStyleBuilder = new Builder();
    }

    public ComplicationDrawable(Context context) {
        this();
        setContext(context);
    }

    public ComplicationDrawable(ComplicationDrawable drawable) {
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
        this.mUnhighlightRunnable = new C04152();
        this.mRendererInvalidateListener = new C09113();
        this.mActiveStyleBuilder = new Builder(drawable.mActiveStyleBuilder);
        this.mAmbientStyleBuilder = new Builder(drawable.mAmbientStyleBuilder);
        CharSequence charSequence = drawable.mNoDataText;
        this.mNoDataText = charSequence.subSequence(0, charSequence.length());
        this.mHighlightDuration = drawable.mHighlightDuration;
        this.mRangedValueProgressHidden = drawable.mRangedValueProgressHidden;
        setBounds(drawable.getBounds());
        this.mAlreadyStyled = true;
    }

    private ComplicationDrawable(Parcel in) {
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
        this.mUnhighlightRunnable = new C04152();
        this.mRendererInvalidateListener = new C09113();
        Bundle bundle = in.readBundle(getClass().getClassLoader());
        this.mActiveStyleBuilder = (Builder) bundle.getParcelable(FIELD_ACTIVE_STYLE_BUILDER);
        this.mAmbientStyleBuilder = (Builder) bundle.getParcelable(FIELD_AMBIENT_STYLE_BUILDER);
        this.mNoDataText = bundle.getCharSequence(FIELD_NO_DATA_TEXT);
        this.mHighlightDuration = bundle.getLong(FIELD_HIGHLIGHT_DURATION);
        this.mRangedValueProgressHidden = bundle.getBoolean(FIELD_RANGED_VALUE_PROGRESS_HIDDEN);
        setBounds((Rect) bundle.getParcelable(FIELD_BOUNDS));
        this.mAlreadyStyled = true;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(FIELD_ACTIVE_STYLE_BUILDER, this.mActiveStyleBuilder);
        bundle.putParcelable(FIELD_AMBIENT_STYLE_BUILDER, this.mAmbientStyleBuilder);
        bundle.putCharSequence(FIELD_NO_DATA_TEXT, this.mNoDataText);
        bundle.putLong(FIELD_HIGHLIGHT_DURATION, this.mHighlightDuration);
        bundle.putBoolean(FIELD_RANGED_VALUE_PROGRESS_HIDDEN, this.mRangedValueProgressHidden);
        bundle.putParcelable(FIELD_BOUNDS, getBounds());
        dest.writeBundle(bundle);
    }

    public int describeContents() {
        return 0;
    }

    private static void setStyleToDefaultValues(Builder styleBuilder, Resources r) {
        styleBuilder.setBackgroundColor(r.getColor(C0395R.color.complicationDrawable_backgroundColor, null));
        styleBuilder.setTextColor(r.getColor(C0395R.color.complicationDrawable_textColor, null));
        styleBuilder.setTitleColor(r.getColor(C0395R.color.complicationDrawable_titleColor, null));
        styleBuilder.setTextTypeface(Typeface.create(r.getString(C0395R.string.complicationDrawable_textTypeface), 0));
        styleBuilder.setTitleTypeface(Typeface.create(r.getString(C0395R.string.complicationDrawable_titleTypeface), 0));
        styleBuilder.setTextSize(r.getDimensionPixelSize(C0395R.dimen.complicationDrawable_textSize));
        styleBuilder.setTitleSize(r.getDimensionPixelSize(C0395R.dimen.complicationDrawable_titleSize));
        styleBuilder.setIconColor(r.getColor(C0395R.color.complicationDrawable_iconColor, null));
        styleBuilder.setBorderColor(r.getColor(C0395R.color.complicationDrawable_borderColor, null));
        styleBuilder.setBorderWidth(r.getDimensionPixelSize(C0395R.dimen.complicationDrawable_borderWidth));
        styleBuilder.setBorderRadius(r.getDimensionPixelSize(C0395R.dimen.complicationDrawable_borderRadius));
        styleBuilder.setBorderStyle(r.getInteger(C0395R.integer.complicationDrawable_borderStyle));
        styleBuilder.setBorderDashWidth(r.getDimensionPixelSize(C0395R.dimen.complicationDrawable_borderDashWidth));
        styleBuilder.setBorderDashGap(r.getDimensionPixelSize(C0395R.dimen.complicationDrawable_borderDashGap));
        styleBuilder.setRangedValueRingWidth(r.getDimensionPixelSize(C0395R.dimen.complicationDrawable_rangedValueRingWidth));
        styleBuilder.setRangedValuePrimaryColor(r.getColor(C0395R.color.complicationDrawable_rangedValuePrimaryColor, null));
        styleBuilder.setRangedValueSecondaryColor(r.getColor(C0395R.color.complicationDrawable_rangedValueSecondaryColor, null));
        styleBuilder.setHighlightColor(r.getColor(C0395R.color.complicationDrawable_highlightColor, null));
    }

    public void setContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Argument \"context\" should not be null.");
        } else if (!Objects.equals(context, this.mContext)) {
            this.mContext = context;
            if (!this.mIsInflatedFromXml && !this.mAlreadyStyled) {
                setStyleToDefaultValues(this.mActiveStyleBuilder, context.getResources());
                setStyleToDefaultValues(this.mAmbientStyleBuilder, context.getResources());
            }
            if (!this.mAlreadyStyled) {
                this.mHighlightDuration = (long) context.getResources().getInteger(C0395R.integer.complicationDrawable_highlightDurationMs);
            }
            this.mComplicationRenderer = new ComplicationRenderer(this.mContext, this.mActiveStyleBuilder.build(), this.mAmbientStyleBuilder.build());
            this.mComplicationRenderer.setOnInvalidateListener(this.mRendererInvalidateListener);
            CharSequence charSequence = this.mNoDataText;
            if (charSequence == null) {
                setNoDataText(context.getString(C0395R.string.complicationDrawable_noDataText));
            } else {
                this.mComplicationRenderer.setNoDataText(charSequence);
            }
            this.mComplicationRenderer.setRangedValueProgressHidden(this.mRangedValueProgressHidden);
            this.mComplicationRenderer.setBounds(getBounds());
            this.mIsStyleUpToDate = true;
        }
    }

    private void inflateAttributes(Resources r, XmlPullParser parser) {
        TypedArray a = r.obtainAttributes(Xml.asAttributeSet(parser), C0395R.styleable.ComplicationDrawable);
        setRangedValueProgressHidden(a.getBoolean(C0395R.styleable.ComplicationDrawable_rangedValueProgressHidden, false));
        a.recycle();
    }

    private void inflateStyle(boolean isAmbient, Resources r, XmlPullParser parser) {
        TypedArray a = r.obtainAttributes(Xml.asAttributeSet(parser), C0395R.styleable.ComplicationDrawable);
        Builder currentBuilder = getComplicationStyleBuilder(isAmbient);
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_backgroundColor)) {
            currentBuilder.setBackgroundColor(a.getColor(C0395R.styleable.ComplicationDrawable_backgroundColor, r.getColor(C0395R.color.complicationDrawable_backgroundColor, null)));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_backgroundDrawable)) {
            currentBuilder.setBackgroundDrawable(a.getDrawable(C0395R.styleable.ComplicationDrawable_backgroundDrawable));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_textColor)) {
            currentBuilder.setTextColor(a.getColor(C0395R.styleable.ComplicationDrawable_textColor, r.getColor(C0395R.color.complicationDrawable_textColor, null)));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_titleColor)) {
            currentBuilder.setTitleColor(a.getColor(C0395R.styleable.ComplicationDrawable_titleColor, r.getColor(C0395R.color.complicationDrawable_titleColor, null)));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_textTypeface)) {
            currentBuilder.setTextTypeface(Typeface.create(a.getString(C0395R.styleable.ComplicationDrawable_textTypeface), 0));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_titleTypeface)) {
            currentBuilder.setTitleTypeface(Typeface.create(a.getString(C0395R.styleable.ComplicationDrawable_titleTypeface), 0));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_textSize)) {
            currentBuilder.setTextSize(a.getDimensionPixelSize(C0395R.styleable.ComplicationDrawable_textSize, r.getDimensionPixelSize(C0395R.dimen.complicationDrawable_textSize)));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_titleSize)) {
            currentBuilder.setTitleSize(a.getDimensionPixelSize(C0395R.styleable.ComplicationDrawable_titleSize, r.getDimensionPixelSize(C0395R.dimen.complicationDrawable_titleSize)));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_iconColor)) {
            currentBuilder.setIconColor(a.getColor(C0395R.styleable.ComplicationDrawable_iconColor, r.getColor(C0395R.color.complicationDrawable_iconColor, null)));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_borderColor)) {
            currentBuilder.setBorderColor(a.getColor(C0395R.styleable.ComplicationDrawable_borderColor, r.getColor(C0395R.color.complicationDrawable_borderColor, null)));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_borderRadius)) {
            currentBuilder.setBorderRadius(a.getDimensionPixelSize(C0395R.styleable.ComplicationDrawable_borderRadius, r.getDimensionPixelSize(C0395R.dimen.complicationDrawable_borderRadius)));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_borderStyle)) {
            currentBuilder.setBorderStyle(a.getInt(C0395R.styleable.ComplicationDrawable_borderStyle, r.getInteger(C0395R.integer.complicationDrawable_borderStyle)));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_borderDashWidth)) {
            currentBuilder.setBorderDashWidth(a.getDimensionPixelSize(C0395R.styleable.ComplicationDrawable_borderDashWidth, r.getDimensionPixelSize(C0395R.dimen.complicationDrawable_borderDashWidth)));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_borderDashGap)) {
            currentBuilder.setBorderDashGap(a.getDimensionPixelSize(C0395R.styleable.ComplicationDrawable_borderDashGap, r.getDimensionPixelSize(C0395R.dimen.complicationDrawable_borderDashGap)));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_borderWidth)) {
            currentBuilder.setBorderWidth(a.getDimensionPixelSize(C0395R.styleable.ComplicationDrawable_borderWidth, r.getDimensionPixelSize(C0395R.dimen.complicationDrawable_borderWidth)));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_rangedValueRingWidth)) {
            currentBuilder.setRangedValueRingWidth(a.getDimensionPixelSize(C0395R.styleable.ComplicationDrawable_rangedValueRingWidth, r.getDimensionPixelSize(C0395R.dimen.complicationDrawable_rangedValueRingWidth)));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_rangedValuePrimaryColor)) {
            currentBuilder.setRangedValuePrimaryColor(a.getColor(C0395R.styleable.ComplicationDrawable_rangedValuePrimaryColor, r.getColor(C0395R.color.complicationDrawable_rangedValuePrimaryColor, null)));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_rangedValueSecondaryColor)) {
            currentBuilder.setRangedValueSecondaryColor(a.getColor(C0395R.styleable.ComplicationDrawable_rangedValueSecondaryColor, r.getColor(C0395R.color.complicationDrawable_rangedValueSecondaryColor, null)));
        }
        if (a.hasValue(C0395R.styleable.ComplicationDrawable_highlightColor)) {
            currentBuilder.setHighlightColor(a.getColor(C0395R.styleable.ComplicationDrawable_highlightColor, r.getColor(C0395R.color.complicationDrawable_highlightColor, null)));
        }
        a.recycle();
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        this.mIsInflatedFromXml = true;
        int outerDepth = parser.getDepth();
        inflateAttributes(r, parser);
        setStyleToDefaultValues(this.mActiveStyleBuilder, r);
        setStyleToDefaultValues(this.mAmbientStyleBuilder, r);
        inflateStyle(false, r, parser);
        inflateStyle(true, r, parser);
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                break;
            }
            if (type == 3) {
                if (parser.getDepth() <= outerDepth) {
                    break;
                }
            }
            if (type == 2) {
                String name = parser.getName();
                if (TextUtils.equals(name, "ambient")) {
                    inflateStyle(true, r, parser);
                } else {
                    String str = TAG;
                    String valueOf = String.valueOf(this);
                    StringBuilder stringBuilder = new StringBuilder((String.valueOf(name).length() + 43) + String.valueOf(valueOf).length());
                    stringBuilder.append("Unknown element: ");
                    stringBuilder.append(name);
                    stringBuilder.append(" for ComplicationDrawable ");
                    stringBuilder.append(valueOf);
                    Log.w(str, stringBuilder.toString());
                }
            }
            this.mIsStyleUpToDate = false;
        }
        this.mIsStyleUpToDate = false;
    }

    public void draw(Canvas canvas, long currentTimeMillis) {
        assertInitialized();
        setCurrentTimeMillis(currentTimeMillis);
        draw(canvas);
    }

    public void draw(Canvas canvas) {
        assertInitialized();
        updateStyleIfRequired();
        this.mComplicationRenderer.draw(canvas, this.mCurrentTimeMillis, this.mInAmbientMode, this.mLowBitAmbient, this.mBurnInProtection, this.mIsHighlighted);
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public int getOpacity() {
        return -3;
    }

    protected void onBoundsChange(Rect bounds) {
        ComplicationRenderer complicationRenderer = this.mComplicationRenderer;
        if (complicationRenderer != null) {
            complicationRenderer.setBounds(bounds);
        }
    }

    public void setNoDataText(@Nullable CharSequence noDataText) {
        if (noDataText == null) {
            this.mNoDataText = "";
        } else {
            this.mNoDataText = noDataText.subSequence(0, noDataText.length());
        }
        ComplicationRenderer complicationRenderer = this.mComplicationRenderer;
        if (complicationRenderer != null) {
            complicationRenderer.setNoDataText(this.mNoDataText);
        }
    }

    public void setRangedValueProgressHidden(boolean rangedValueProgressHidden) {
        this.mRangedValueProgressHidden = rangedValueProgressHidden;
        ComplicationRenderer complicationRenderer = this.mComplicationRenderer;
        if (complicationRenderer != null) {
            complicationRenderer.setRangedValueProgressHidden(rangedValueProgressHidden);
        }
    }

    public boolean isRangedValueProgressHidden() {
        return this.mRangedValueProgressHidden;
    }

    public void setComplicationData(@Nullable ComplicationData complicationData) {
        assertInitialized();
        this.mComplicationRenderer.setComplicationData(complicationData);
    }

    public void setInAmbientMode(boolean inAmbientMode) {
        this.mInAmbientMode = inAmbientMode;
    }

    public void setLowBitAmbient(boolean lowBitAmbient) {
        this.mLowBitAmbient = lowBitAmbient;
    }

    public void setBurnInProtection(boolean burnInProtection) {
        this.mBurnInProtection = burnInProtection;
    }

    public void setCurrentTimeMillis(long currentTimeMillis) {
        this.mCurrentTimeMillis = currentTimeMillis;
    }

    public void setIsHighlighted(boolean isHighlighted) {
        this.mIsHighlighted = isHighlighted;
    }

    public void setBackgroundColorActive(int backgroundColor) {
        getComplicationStyleBuilder(false).setBackgroundColor(backgroundColor);
        this.mIsStyleUpToDate = false;
    }

    public void setBackgroundDrawableActive(Drawable drawable) {
        getComplicationStyleBuilder(false).setBackgroundDrawable(drawable);
        this.mIsStyleUpToDate = false;
    }

    public void setTextColorActive(int textColor) {
        getComplicationStyleBuilder(false).setTextColor(textColor);
        this.mIsStyleUpToDate = false;
    }

    public void setTitleColorActive(int titleColor) {
        getComplicationStyleBuilder(false).setTitleColor(titleColor);
        this.mIsStyleUpToDate = false;
    }

    public void setImageColorFilterActive(ColorFilter colorFilter) {
        getComplicationStyleBuilder(false).setColorFilter(colorFilter);
        this.mIsStyleUpToDate = false;
    }

    public void setIconColorActive(int iconColor) {
        getComplicationStyleBuilder(false).setIconColor(iconColor);
        this.mIsStyleUpToDate = false;
    }

    public void setTextTypefaceActive(Typeface textTypeface) {
        getComplicationStyleBuilder(false).setTextTypeface(textTypeface);
        this.mIsStyleUpToDate = false;
    }

    public void setTitleTypefaceActive(Typeface titleTypeface) {
        getComplicationStyleBuilder(false).setTitleTypeface(titleTypeface);
        this.mIsStyleUpToDate = false;
    }

    public void setTextSizeActive(int textSize) {
        getComplicationStyleBuilder(false).setTextSize(textSize);
        this.mIsStyleUpToDate = false;
    }

    public void setTitleSizeActive(int titleSize) {
        getComplicationStyleBuilder(false).setTitleSize(titleSize);
        this.mIsStyleUpToDate = false;
    }

    public void setBorderColorActive(int borderColor) {
        getComplicationStyleBuilder(false).setBorderColor(borderColor);
        this.mIsStyleUpToDate = false;
    }

    public void setBorderStyleActive(int borderStyle) {
        getComplicationStyleBuilder(false).setBorderStyle(borderStyle);
        this.mIsStyleUpToDate = false;
    }

    public void setBorderDashWidthActive(int borderDashWidth) {
        getComplicationStyleBuilder(false).setBorderDashWidth(borderDashWidth);
        this.mIsStyleUpToDate = false;
    }

    public void setBorderDashGapActive(int borderDashGap) {
        getComplicationStyleBuilder(false).setBorderDashGap(borderDashGap);
        this.mIsStyleUpToDate = false;
    }

    public void setBorderRadiusActive(int borderRadius) {
        getComplicationStyleBuilder(false).setBorderRadius(borderRadius);
        this.mIsStyleUpToDate = false;
    }

    public void setBorderWidthActive(int borderWidth) {
        getComplicationStyleBuilder(false).setBorderWidth(borderWidth);
        this.mIsStyleUpToDate = false;
    }

    public void setRangedValueRingWidthActive(int rangedValueRingWidth) {
        getComplicationStyleBuilder(false).setRangedValueRingWidth(rangedValueRingWidth);
        this.mIsStyleUpToDate = false;
    }

    public void setRangedValuePrimaryColorActive(int rangedValuePrimaryColor) {
        getComplicationStyleBuilder(false).setRangedValuePrimaryColor(rangedValuePrimaryColor);
        this.mIsStyleUpToDate = false;
    }

    public void setRangedValueSecondaryColorActive(int rangedValueSecondaryColor) {
        getComplicationStyleBuilder(false).setRangedValueSecondaryColor(rangedValueSecondaryColor);
        this.mIsStyleUpToDate = false;
    }

    public void setHighlightColorActive(int highlightColor) {
        getComplicationStyleBuilder(false).setHighlightColor(highlightColor);
        this.mIsStyleUpToDate = false;
    }

    public void setBackgroundColorAmbient(int backgroundColor) {
        getComplicationStyleBuilder(true).setBackgroundColor(backgroundColor);
        this.mIsStyleUpToDate = false;
    }

    public void setBackgroundDrawableAmbient(Drawable drawable) {
        getComplicationStyleBuilder(true).setBackgroundDrawable(drawable);
        this.mIsStyleUpToDate = false;
    }

    public void setTextColorAmbient(int textColor) {
        getComplicationStyleBuilder(true).setTextColor(textColor);
        this.mIsStyleUpToDate = false;
    }

    public void setTitleColorAmbient(int titleColor) {
        getComplicationStyleBuilder(true).setTitleColor(titleColor);
        this.mIsStyleUpToDate = false;
    }

    public void setImageColorFilterAmbient(ColorFilter colorFilter) {
        getComplicationStyleBuilder(true).setColorFilter(colorFilter);
        this.mIsStyleUpToDate = false;
    }

    public void setIconColorAmbient(int iconColor) {
        getComplicationStyleBuilder(true).setIconColor(iconColor);
        this.mIsStyleUpToDate = false;
    }

    public void setTextTypefaceAmbient(Typeface textTypeface) {
        getComplicationStyleBuilder(true).setTextTypeface(textTypeface);
        this.mIsStyleUpToDate = false;
    }

    public void setTitleTypefaceAmbient(Typeface titleTypeface) {
        getComplicationStyleBuilder(true).setTitleTypeface(titleTypeface);
        this.mIsStyleUpToDate = false;
    }

    public void setTextSizeAmbient(int textSize) {
        getComplicationStyleBuilder(true).setTextSize(textSize);
        this.mIsStyleUpToDate = false;
    }

    public void setTitleSizeAmbient(int titleSize) {
        getComplicationStyleBuilder(true).setTitleSize(titleSize);
        this.mIsStyleUpToDate = false;
    }

    public void setBorderColorAmbient(int borderColor) {
        getComplicationStyleBuilder(true).setBorderColor(borderColor);
        this.mIsStyleUpToDate = false;
    }

    public void setBorderStyleAmbient(int borderStyle) {
        getComplicationStyleBuilder(true).setBorderStyle(borderStyle);
        this.mIsStyleUpToDate = false;
    }

    public void setBorderDashWidthAmbient(int borderDashWidth) {
        getComplicationStyleBuilder(true).setBorderDashWidth(borderDashWidth);
        this.mIsStyleUpToDate = false;
    }

    public void setBorderDashGapAmbient(int borderDashGap) {
        getComplicationStyleBuilder(true).setBorderDashGap(borderDashGap);
        this.mIsStyleUpToDate = false;
    }

    public void setBorderRadiusAmbient(int borderRadius) {
        getComplicationStyleBuilder(true).setBorderRadius(borderRadius);
        this.mIsStyleUpToDate = false;
    }

    public void setBorderWidthAmbient(int borderWidth) {
        getComplicationStyleBuilder(true).setBorderWidth(borderWidth);
        this.mIsStyleUpToDate = false;
    }

    public void setRangedValueRingWidthAmbient(int rangedValueRingWidth) {
        getComplicationStyleBuilder(true).setRangedValueRingWidth(rangedValueRingWidth);
        this.mIsStyleUpToDate = false;
    }

    public void setRangedValuePrimaryColorAmbient(int rangedValuePrimaryColor) {
        getComplicationStyleBuilder(true).setRangedValuePrimaryColor(rangedValuePrimaryColor);
        this.mIsStyleUpToDate = false;
    }

    public void setRangedValueSecondaryColorAmbient(int rangedValueSecondaryColor) {
        getComplicationStyleBuilder(true).setRangedValueSecondaryColor(rangedValueSecondaryColor);
        this.mIsStyleUpToDate = false;
    }

    public void setHighlightColorAmbient(int highlightColor) {
        getComplicationStyleBuilder(true).setHighlightColor(highlightColor);
        this.mIsStyleUpToDate = false;
    }

    @Deprecated
    public boolean onTap(int x, int y, long tapTimeMillis) {
        return onTap(x, y);
    }

    public boolean onTap(int x, int y) {
        ComplicationData data = this.mComplicationRenderer;
        if (data == null) {
            return false;
        }
        data = data.getComplicationData();
        if (data != null) {
            if (data.getTapAction() == null) {
                if (data.getType() == 9) {
                }
            }
            if (getBounds().contains(x, y)) {
                if (data.getType() == 9) {
                    Context context = this.mContext;
                    if (!(context instanceof WatchFaceService)) {
                        return false;
                    }
                    context.startActivity(ComplicationHelperActivity.createPermissionRequestHelperIntent(context, new ComponentName(context, context.getClass())));
                } else {
                    try {
                        data.getTapAction().send();
                    } catch (CanceledException e) {
                        return false;
                    }
                }
                if (getHighlightDuration() > 0) {
                    setIsHighlighted(true);
                    invalidateSelf();
                    this.mMainThreadHandler.removeCallbacks(this.mUnhighlightRunnable);
                    this.mMainThreadHandler.postDelayed(this.mUnhighlightRunnable, getHighlightDuration());
                }
                return true;
            }
        }
        return false;
    }

    public boolean isHighlighted() {
        return this.mIsHighlighted;
    }

    public void setHighlightDuration(long highlightDurationMillis) {
        if (highlightDurationMillis >= 0) {
            this.mHighlightDuration = highlightDurationMillis;
            return;
        }
        throw new IllegalArgumentException("Highlight duration should be non-negative.");
    }

    public long getHighlightDuration() {
        return this.mHighlightDuration;
    }

    private Builder getComplicationStyleBuilder(boolean isAmbient) {
        return isAmbient ? this.mAmbientStyleBuilder : this.mActiveStyleBuilder;
    }

    private void updateStyleIfRequired() {
        if (!this.mIsStyleUpToDate) {
            this.mComplicationRenderer.updateStyle(this.mActiveStyleBuilder.build(), this.mAmbientStyleBuilder.build());
            this.mIsStyleUpToDate = true;
        }
    }

    private void assertInitialized() {
        if (this.mContext == null) {
            throw new IllegalStateException("ComplicationDrawable does not have a context. Use setContext(Context) to set it first.");
        }
    }

    @VisibleForTesting
    ComplicationStyle getActiveStyle() {
        return this.mActiveStyleBuilder.build();
    }

    @VisibleForTesting
    ComplicationStyle getAmbientStyle() {
        return this.mAmbientStyleBuilder.build();
    }

    @VisibleForTesting
    ComplicationRenderer getComplicationRenderer() {
        return this.mComplicationRenderer;
    }

    @VisibleForTesting
    CharSequence getNoDataText() {
        return this.mNoDataText;
    }
}

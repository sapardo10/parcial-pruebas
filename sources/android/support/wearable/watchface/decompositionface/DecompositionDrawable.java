package android.support.wearable.watchface.decompositionface;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Icon;
import android.graphics.drawable.Icon.OnDrawableLoadedListener;
import android.graphics.drawable.RotateDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.wearable.C0395R;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationData.Builder;
import android.support.wearable.complications.rendering.ComplicationDrawable;
import android.support.wearable.watchface.decomposition.ComplicationComponent;
import android.support.wearable.watchface.decomposition.FontComponent;
import android.support.wearable.watchface.decomposition.ImageComponent;
import android.support.wearable.watchface.decomposition.NumberComponent;
import android.support.wearable.watchface.decomposition.WatchFaceDecomposition;
import android.support.wearable.watchface.decomposition.WatchFaceDecomposition.DrawnComponent;
import android.util.ArrayMap;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@TargetApi(23)
public class DecompositionDrawable extends Drawable {
    private ComplicationData blankConfigComplicationData;
    private final Rect boundsRect = new Rect();
    private boolean burnInProtection;
    private boolean clipToCircle;
    private SparseArray<ComplicationDrawable> complicationDrawables;
    private final Context context;
    private final CoordConverter converter = new CoordConverter();
    private long currentTimeMillis;
    private WatchFaceDecomposition decomposition;
    private final Callback drawableCallback = new C04851();
    private ArrayList<DrawnComponent> drawnComponents;
    private SparseArray<DigitDrawable> fontDrawables;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Map<Icon, RotateDrawable> imageDrawables;
    private boolean inAmbientMode;
    private boolean inConfigMode;
    private boolean lowBitAmbient;
    private final Path roundPath = new Path();

    /* renamed from: android.support.wearable.watchface.decompositionface.DecompositionDrawable$1 */
    class C04851 implements Callback {
        C04851() {
        }

        public void invalidateDrawable(@NonNull Drawable who) {
            DecompositionDrawable.this.invalidateSelf();
        }

        public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
        }

        public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
        }
    }

    /* renamed from: android.support.wearable.watchface.decompositionface.DecompositionDrawable$2 */
    class C04862 implements Comparator<DrawnComponent> {
        C04862(DecompositionDrawable this$0) {
        }

        public int compare(DrawnComponent o1, DrawnComponent o2) {
            return o1.getZOrder() - o2.getZOrder();
        }
    }

    public DecompositionDrawable(Context context) {
        this.context = context;
    }

    public void draw(Canvas canvas) {
        if (this.decomposition != null) {
            DrawnComponent component;
            Rect bounds = getBounds();
            if (this.clipToCircle) {
                canvas.save();
                canvas.clipPath(this.roundPath);
            }
            this.converter.setPixelBounds(bounds);
            Iterator it = this.drawnComponents.iterator();
            while (it.hasNext()) {
                component = (DrawnComponent) it.next();
                if (component instanceof ImageComponent) {
                    drawImage((ImageComponent) component, canvas, this.converter);
                } else if (component instanceof NumberComponent) {
                    drawNumber((NumberComponent) component, canvas, this.converter);
                } else if (!this.inConfigMode && (component instanceof ComplicationComponent)) {
                    drawComplication((ComplicationComponent) component, canvas, this.converter);
                }
            }
            if (this.inConfigMode) {
                canvas.drawColor(this.context.getColor(C0395R.color.config_scrim_color));
                it = this.drawnComponents.iterator();
                while (it.hasNext()) {
                    component = (DrawnComponent) it.next();
                    if (component instanceof ComplicationComponent) {
                        drawComplication((ComplicationComponent) component, canvas, this.converter);
                    }
                }
            }
            if (this.clipToCircle) {
                canvas.restore();
            }
        }
    }

    public void setDecomposition(WatchFaceDecomposition decomposition, boolean inConfigMode) {
        this.decomposition = decomposition;
        this.inConfigMode = inConfigMode;
        this.drawnComponents = new ArrayList();
        this.drawnComponents.addAll(decomposition.getImageComponents());
        this.drawnComponents.addAll(decomposition.getNumberComponents());
        this.drawnComponents.addAll(decomposition.getComplicationComponents());
        Collections.sort(this.drawnComponents, new C04862(this));
        loadDrawables();
    }

    protected void onBoundsChange(Rect bounds) {
        this.roundPath.reset();
        this.roundPath.addOval((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom, Direction.CW);
    }

    private void drawImage(ImageComponent imageComponent, Canvas canvas, CoordConverter converter) {
        RotateDrawable drawable = (RotateDrawable) this.imageDrawables.get(imageComponent.getImage());
        if (drawable != null) {
            if (!this.inAmbientMode || imageComponent.getDegreesPerDay() < 518400.0f) {
                converter.getPixelRectFromProportional(imageComponent.getBounds(), this.boundsRect);
                drawable.setBounds(this.boundsRect);
                float angle = angleForTime(imageComponent.getOffsetDegrees(), imageComponent.getDegreesPerDay());
                drawable.setFromDegrees(angle);
                drawable.setToDegrees(angle);
                if (angle > 0.0f) {
                    drawable.setPivotX((float) (converter.getPixelX(imageComponent.getPivot().x) - this.boundsRect.left));
                    drawable.setPivotY((float) (converter.getPixelY(imageComponent.getPivot().y) - this.boundsRect.top));
                }
                drawable.setLevel(drawable.getLevel() + 1);
                drawable.draw(canvas);
            }
        }
    }

    @VisibleForTesting
    float angleForTime(float offset, float degreesPerDay) {
        return (((((float) (getTimeZoneAdjustedTime() % TimeUnit.DAYS.toMillis(1))) * degreesPerDay) / ((float) TimeUnit.DAYS.toMillis(1))) + offset) % 360.0f;
    }

    private void drawNumber(NumberComponent numberComponent, Canvas canvas, CoordConverter converter) {
        if (!this.inAmbientMode || numberComponent.getMsPerIncrement() >= TimeUnit.MINUTES.toMillis(1)) {
            DigitDrawable digitDrawable = (DigitDrawable) this.fontDrawables.get(numberComponent.getFontComponentId());
            if (digitDrawable != null) {
                String digitString = numberComponent.getDisplayStringForTime(getTimeZoneAdjustedTime());
                PointF position = numberComponent.getPosition();
                int digitWidth = digitDrawable.getIntrinsicWidth();
                int digitHeight = digitDrawable.getIntrinsicHeight();
                int x = converter.getPixelX(position.x);
                int y = converter.getPixelY(position.y);
                this.boundsRect.set(x, y, x + digitWidth, y + digitHeight);
                for (int i = 0; i < digitString.length(); i++) {
                    digitDrawable.setBounds(this.boundsRect);
                    digitDrawable.setCurrentDigit(Character.digit(digitString.charAt(i), 10));
                    digitDrawable.draw(canvas);
                    this.boundsRect.offset(digitWidth, 0);
                }
            }
        }
    }

    private void drawComplication(ComplicationComponent component, Canvas canvas, CoordConverter converter) {
        ComplicationDrawable drawable = (ComplicationDrawable) this.complicationDrawables.get(component.getWatchFaceComplicationId());
        drawable.setCurrentTimeMillis(this.currentTimeMillis);
        drawable.setInAmbientMode(this.inAmbientMode);
        drawable.setBurnInProtection(this.burnInProtection);
        drawable.setLowBitAmbient(this.lowBitAmbient);
        RectF proportionalBounds = component.getBounds();
        if (proportionalBounds != null) {
            converter.getPixelRectFromProportional(proportionalBounds, this.boundsRect);
            drawable.setBounds(this.boundsRect);
        }
        drawable.draw(canvas);
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    public int getOpacity() {
        return -3;
    }

    public void setCurrentTimeMillis(long time) {
        this.currentTimeMillis = time;
    }

    public void setInAmbientMode(boolean inAmbientMode) {
        this.inAmbientMode = inAmbientMode;
    }

    public void setBurnInProtection(boolean burnInProtection) {
        this.burnInProtection = burnInProtection;
    }

    public void setLowBitAmbient(boolean lowBitAmbient) {
        this.lowBitAmbient = lowBitAmbient;
    }

    public void setComplicationData(int watchFaceComplicationId, ComplicationData data) {
        ComplicationDrawable drawable = (ComplicationDrawable) this.complicationDrawables.get(watchFaceComplicationId);
        if (drawable != null) {
            if (this.inConfigMode) {
                if (data == null) {
                    data = getBlankConfigComplicationData();
                    drawable.setBorderStyleActive(2);
                } else {
                    drawable.setBorderStyleActive(1);
                }
            }
            drawable.setComplicationData(data);
        }
        invalidateSelf();
    }

    public void setClipToCircle(boolean clip) {
        this.clipToCircle = clip;
    }

    public boolean onTap(int x, int y) {
        for (int i = 0; i < this.complicationDrawables.size(); i++) {
            if (((ComplicationDrawable) this.complicationDrawables.valueAt(i)).onTap(x, y)) {
                return true;
            }
        }
        return false;
    }

    private long getTimeZoneAdjustedTime() {
        return this.currentTimeMillis + ((long) TimeZone.getDefault().getOffset(this.currentTimeMillis));
    }

    private void loadDrawables() {
        this.imageDrawables = new ArrayMap();
        for (ImageComponent imageComponent : this.decomposition.getImageComponents()) {
            final Icon image = imageComponent.getImage();
            image.loadDrawableAsync(this.context, new OnDrawableLoadedListener() {
                public void onDrawableLoaded(Drawable drawable) {
                    RotateDrawable rotater = new RotateDrawable();
                    rotater.setDrawable(drawable);
                    rotater.setPivotXRelative(false);
                    rotater.setPivotYRelative(false);
                    DecompositionDrawable.this.imageDrawables.put(image, rotater);
                    DecompositionDrawable.this.invalidateSelf();
                }
            }, this.handler);
        }
        this.fontDrawables = new SparseArray();
        for (final FontComponent fontComponent : this.decomposition.getFontComponents()) {
            fontComponent.getImage().loadDrawableAsync(this.context, new OnDrawableLoadedListener() {
                public void onDrawableLoaded(Drawable drawable) {
                    DigitDrawable digitDrawable = new DigitDrawable();
                    digitDrawable.setFontDrawable(drawable);
                    digitDrawable.setDigitCount(fontComponent.getDigitCount());
                    DecompositionDrawable.this.fontDrawables.put(fontComponent.getComponentId(), digitDrawable);
                    DecompositionDrawable.this.invalidateSelf();
                }
            }, this.handler);
        }
        this.complicationDrawables = new SparseArray();
        for (ComplicationComponent complication : this.decomposition.getComplicationComponents()) {
            ComplicationDrawable drawable;
            ComplicationDrawable providedDrawable = complication.getComplicationDrawable();
            if (this.inConfigMode) {
                drawable = buildConfigComplicationDrawable();
                if (providedDrawable != null) {
                    drawable.setBounds(providedDrawable.getBounds());
                }
            } else {
                drawable = providedDrawable == null ? new ComplicationDrawable() : new ComplicationDrawable(providedDrawable);
            }
            drawable.setContext(this.context);
            drawable.setCallback(this.drawableCallback);
            drawable.setLowBitAmbient(true);
            drawable.setBurnInProtection(true);
            this.complicationDrawables.put(complication.getWatchFaceComplicationId(), drawable);
            if (this.inConfigMode) {
                setComplicationData(complication.getWatchFaceComplicationId(), null);
            }
        }
    }

    private ComplicationDrawable buildConfigComplicationDrawable() {
        ComplicationDrawable drawable = new ComplicationDrawable(this.context);
        drawable.setBorderColorActive(-1);
        drawable.setBorderDashWidthActive(this.context.getResources().getDimensionPixelSize(C0395R.dimen.blank_config_dash_width));
        drawable.setBorderDashGapActive(this.context.getResources().getDimensionPixelSize(C0395R.dimen.blank_config_dash_gap));
        return drawable;
    }

    private ComplicationData getBlankConfigComplicationData() {
        if (this.blankConfigComplicationData == null) {
            this.blankConfigComplicationData = new Builder(6).setIcon(Icon.createWithResource(this.context, C0395R.drawable.ic_add_white_24dp)).build();
        }
        return this.blankConfigComplicationData;
    }
}

package android.support.wearable.watchface.decompositionface;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationData.Builder;
import android.support.wearable.complications.ComplicationProviderInfo;
import android.support.wearable.watchface.decomposition.ComplicationComponent;
import android.support.wearable.watchface.decomposition.WatchFaceDecomposition;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

@TargetApi(24)
public class DecompositionConfigView extends ImageView {
    private final Rect boundsRect = new Rect();
    private ArrayList<ComplicationComponent> complications;
    private final CoordConverter converter = new CoordConverter();
    private final DecompositionDrawable decompositionDrawable = new DecompositionDrawable(getContext());
    private final GestureDetector gestureDetector = new GestureDetector(getContext(), this.gestureListener);
    private final SimpleOnGestureListener gestureListener = new C04831();
    private OnComplicationTapListener tapListener;

    /* renamed from: android.support.wearable.watchface.decompositionface.DecompositionConfigView$1 */
    class C04831 extends SimpleOnGestureListener {
        C04831() {
        }

        public boolean onDown(MotionEvent e) {
            return true;
        }

        public boolean onSingleTapUp(MotionEvent e) {
            if (DecompositionConfigView.this.complications != null) {
                if (DecompositionConfigView.this.tapListener != null) {
                    DecompositionConfigView.this.converter.setPixelBounds(0, 0, DecompositionConfigView.this.getWidth(), DecompositionConfigView.this.getHeight());
                    Iterator it = DecompositionConfigView.this.complications.iterator();
                    while (it.hasNext()) {
                        ComplicationComponent complication = (ComplicationComponent) it.next();
                        DecompositionConfigView.this.converter.getPixelRectFromProportional(complication.getBounds(), DecompositionConfigView.this.boundsRect);
                        if (DecompositionConfigView.this.boundsRect.contains((int) e.getX(), (int) e.getY())) {
                            DecompositionConfigView.this.tapListener.onComplicationTap(complication.getWatchFaceComplicationId(), complication.getComplicationTypes());
                            return true;
                        }
                    }
                    return false;
                }
            }
            return false;
        }
    }

    /* renamed from: android.support.wearable.watchface.decompositionface.DecompositionConfigView$2 */
    class C04842 implements Comparator<ComplicationComponent> {
        C04842(DecompositionConfigView this$0) {
        }

        public int compare(ComplicationComponent o1, ComplicationComponent o2) {
            return o2.getZOrder() - o1.getZOrder();
        }
    }

    public interface OnComplicationTapListener {
        void onComplicationTap(int i, int[] iArr);
    }

    public DecompositionConfigView(Context context) {
        super(context);
    }

    public DecompositionConfigView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return this.gestureDetector.onTouchEvent(event);
    }

    public void setOnComplicationTapListener(OnComplicationTapListener listener) {
        this.tapListener = listener;
    }

    public void setDecomposition(WatchFaceDecomposition decomposition) {
        this.decompositionDrawable.setDecomposition(decomposition, true);
        this.decompositionDrawable.setClipToCircle(getResources().getConfiguration().isScreenRound());
        setImageDrawable(this.decompositionDrawable);
        this.complications = new ArrayList(decomposition.getComplicationComponents());
        Collections.sort(this.complications, new C04842(this));
    }

    public void setDisplayTime(long time) {
        this.decompositionDrawable.setCurrentTimeMillis(time);
        invalidate();
    }

    public void setProviderInfo(int watchFaceComplicationId, @Nullable ComplicationProviderInfo info) {
        this.decompositionDrawable.setComplicationData(watchFaceComplicationId, buildComplicationDataForInfo(info));
        invalidate();
    }

    @Nullable
    private ComplicationData buildComplicationDataForInfo(@Nullable ComplicationProviderInfo info) {
        if (info == null) {
            return null;
        }
        return new Builder(6).setIcon(info.providerIcon).build();
    }

    public int[] getWatchFaceComplicationIds() {
        int[] result = new int[this.complications.size()];
        for (int i = 0; i < this.complications.size(); i++) {
            result[i] = ((ComplicationComponent) this.complications.get(i)).getWatchFaceComplicationId();
        }
        return result;
    }
}

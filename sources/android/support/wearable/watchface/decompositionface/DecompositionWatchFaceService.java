package android.support.wearable.watchface.decompositionface;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewCompat;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle.Builder;
import android.support.wearable.watchface.decomposition.ComplicationComponent;
import android.support.wearable.watchface.decomposition.WatchFaceDecomposition;
import android.view.SurfaceHolder;

public abstract class DecompositionWatchFaceService extends CanvasWatchFaceService {

    public class Engine extends android.support.wearable.watchface.CanvasWatchFaceService.Engine {
        private static final int MSG_CODE_UPDATE_TIME = 1;
        private static final int UPDATE_RATE_MS = 33;
        private WatchFaceDecomposition decomposition;
        private DecompositionDrawable decompositionDrawable;
        private final Callback drawableCallback = new C04902();
        private boolean inAmbientMode;
        private final Handler updateTimeHandler = new C04891();

        /* renamed from: android.support.wearable.watchface.decompositionface.DecompositionWatchFaceService$Engine$1 */
        class C04891 extends Handler {
            C04891() {
            }

            public void handleMessage(Message message) {
                Engine.this.invalidate();
            }
        }

        /* renamed from: android.support.wearable.watchface.decompositionface.DecompositionWatchFaceService$Engine$2 */
        class C04902 implements Callback {
            C04902() {
            }

            public void invalidateDrawable(Drawable who) {
                Engine.this.invalidate();
            }

            public void scheduleDrawable(Drawable who, Runnable what, long when) {
            }

            public void unscheduleDrawable(Drawable who, Runnable what) {
            }
        }

        public Engine() {
            super(DecompositionWatchFaceService.this);
        }

        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            this.decomposition = DecompositionWatchFaceService.this.buildDecomposition();
            this.decompositionDrawable = new DecompositionDrawable(DecompositionWatchFaceService.this);
            this.decompositionDrawable.setDecomposition(this.decomposition, false);
            this.decompositionDrawable.setCallback(this.drawableCallback);
            activateComplications();
            setWatchFaceStyle(new Builder(DecompositionWatchFaceService.this).setAcceptsTapEvents(true).build());
            updateDecomposition(this.decomposition);
        }

        public void onDraw(Canvas canvas, Rect bounds) {
            long frameStartTimeMs = SystemClock.elapsedRealtime();
            this.decompositionDrawable.setCurrentTimeMillis(System.currentTimeMillis());
            this.decompositionDrawable.setBounds(bounds);
            canvas.drawColor(ViewCompat.MEASURED_STATE_MASK);
            this.decompositionDrawable.draw(canvas);
            if (!this.inAmbientMode) {
                this.updateTimeHandler.sendEmptyMessageDelayed(1, Math.max(33 - (SystemClock.elapsedRealtime() - frameStartTimeMs), 0));
            }
        }

        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            this.inAmbientMode = inAmbientMode;
            this.decompositionDrawable.setInAmbientMode(inAmbientMode);
        }

        public void onPropertiesChanged(Bundle properties) {
            this.decompositionDrawable.setLowBitAmbient(properties.getBoolean(WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, false));
            this.decompositionDrawable.setBurnInProtection(properties.getBoolean(WatchFaceService.PROPERTY_BURN_IN_PROTECTION, false));
        }

        public void onTimeTick() {
            invalidate();
        }

        public void onComplicationDataUpdate(int watchFaceComplicationId, ComplicationData data) {
            this.decompositionDrawable.setComplicationData(watchFaceComplicationId, data);
        }

        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            if (tapType == 2) {
                this.decompositionDrawable.onTap(x, y);
            }
        }

        private void activateComplications() {
            int[] ids = new int[this.decomposition.getComplicationComponents().size()];
            for (int i = 0; i < this.decomposition.getComplicationComponents().size(); i++) {
                ids[i] = ((ComplicationComponent) this.decomposition.getComplicationComponents().get(i)).getWatchFaceComplicationId();
            }
            setActiveComplications(ids);
        }
    }

    protected abstract WatchFaceDecomposition buildDecomposition();

    public Engine onCreateEngine() {
        return new Engine();
    }
}

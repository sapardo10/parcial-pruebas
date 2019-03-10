package android.support.wearable.watchface;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.util.Log;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.SurfaceHolder;

@TargetApi(21)
public abstract class CanvasWatchFaceService extends WatchFaceService {
    private static final boolean LOG_VERBOSE = false;
    private static final String TAG = "CanvasWatchFaceService";
    private static final boolean TRACE_DRAW = false;

    public class Engine extends android.support.wearable.watchface.WatchFaceService.Engine {
        private static final int MSG_INVALIDATE = 0;
        private final Choreographer mChoreographer = Choreographer.getInstance();
        private boolean mDestroyed;
        private boolean mDrawRequested;
        private final FrameCallback mFrameCallback = new C04711();
        private final Handler mHandler = new C04722();

        /* renamed from: android.support.wearable.watchface.CanvasWatchFaceService$Engine$1 */
        class C04711 implements FrameCallback {
            C04711() {
            }

            public void doFrame(long frameTimeNs) {
                if (!Engine.this.mDestroyed) {
                    if (Engine.this.mDrawRequested) {
                        Engine engine = Engine.this;
                        engine.draw(engine.getSurfaceHolder());
                    }
                }
            }
        }

        /* renamed from: android.support.wearable.watchface.CanvasWatchFaceService$Engine$2 */
        class C04722 extends Handler {
            C04722() {
            }

            public void handleMessage(Message message) {
                if (message.what == 0) {
                    Engine.this.invalidate();
                }
            }
        }

        public Engine(CanvasWatchFaceService this$0) {
            super();
        }

        @CallSuper
        public void onDestroy() {
            this.mDestroyed = true;
            this.mHandler.removeMessages(0);
            this.mChoreographer.removeFrameCallback(this.mFrameCallback);
            super.onDestroy();
        }

        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (Log.isLoggable(CanvasWatchFaceService.TAG, 3)) {
                Log.d(CanvasWatchFaceService.TAG, "onSurfaceChanged");
            }
            super.onSurfaceChanged(holder, format, width, height);
            invalidate();
        }

        public void onSurfaceRedrawNeeded(SurfaceHolder holder) {
            if (Log.isLoggable(CanvasWatchFaceService.TAG, 3)) {
                Log.d(CanvasWatchFaceService.TAG, "onSurfaceRedrawNeeded");
            }
            super.onSurfaceRedrawNeeded(holder);
            draw(holder);
        }

        public void onSurfaceCreated(SurfaceHolder holder) {
            if (Log.isLoggable(CanvasWatchFaceService.TAG, 3)) {
                Log.d(CanvasWatchFaceService.TAG, "onSurfaceCreated");
            }
            super.onSurfaceCreated(holder);
            invalidate();
        }

        public void invalidate() {
            if (!this.mDrawRequested) {
                this.mDrawRequested = true;
                this.mChoreographer.postFrameCallback(this.mFrameCallback);
            }
        }

        public void postInvalidate() {
            this.mHandler.sendEmptyMessage(0);
        }

        public void onDraw(Canvas canvas, Rect bounds) {
        }

        private void draw(SurfaceHolder holder) {
            this.mDrawRequested = false;
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                try {
                    onDraw(canvas, holder.getSurfaceFrame());
                } finally {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public Engine onCreateEngine() {
        return new Engine(this);
    }
}

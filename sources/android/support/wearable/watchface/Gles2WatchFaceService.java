package android.support.wearable.watchface;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.util.Log;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

@TargetApi(21)
public abstract class Gles2WatchFaceService extends WatchFaceService {
    private static final int[] EGL_CONFIG_ATTRIB_LIST = new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12344};
    private static final int[] EGL_CONTEXT_ATTRIB_LIST = new int[]{12440, 2, 12344};
    private static final int[] EGL_SURFACE_ATTRIB_LIST = new int[]{12344};
    private static final boolean LOG_VERBOSE = false;
    private static final String TAG = "Gles2WatchFaceService";
    private static final boolean TRACE_DRAW = false;

    public class Engine extends android.support.wearable.watchface.WatchFaceService.Engine {
        private static final int MSG_INVALIDATE = 0;
        private boolean mCalledOnGlContextCreated;
        private final Choreographer mChoreographer = Choreographer.getInstance();
        private boolean mDestroyed;
        private boolean mDrawRequested;
        private EGLConfig mEglConfig;
        private EGLContext mEglContext;
        private EGLDisplay mEglDisplay;
        private EGLSurface mEglSurface;
        private final FrameCallback mFrameCallback = new C04731();
        private final Handler mHandler = new C04742();
        private int mInsetBottom;
        private int mInsetLeft;

        /* renamed from: android.support.wearable.watchface.Gles2WatchFaceService$Engine$1 */
        class C04731 implements FrameCallback {
            C04731() {
            }

            public void doFrame(long frameTimeNs) {
                if (!Engine.this.mDestroyed) {
                    if (Engine.this.mDrawRequested) {
                        Engine.this.drawFrame();
                    }
                }
            }
        }

        /* renamed from: android.support.wearable.watchface.Gles2WatchFaceService$Engine$2 */
        class C04742 extends Handler {
            C04742() {
            }

            public void handleMessage(Message message) {
                if (message.what == 0) {
                    Engine.this.invalidate();
                }
            }
        }

        public Engine(Gles2WatchFaceService this$0) {
            super();
        }

        public EGLDisplay initializeEglDisplay() {
            EGLDisplay result = EGL14.eglGetDisplay(0);
            if (result != EGL14.EGL_NO_DISPLAY) {
                int[] version = new int[2];
                if (EGL14.eglInitialize(result, version, 0, version, 1)) {
                    if (Log.isLoggable(Gles2WatchFaceService.TAG, 3)) {
                        String str = Gles2WatchFaceService.TAG;
                        int i = version[0];
                        int i2 = version[1];
                        StringBuilder stringBuilder = new StringBuilder(35);
                        stringBuilder.append("EGL version ");
                        stringBuilder.append(i);
                        stringBuilder.append(".");
                        stringBuilder.append(i2);
                        Log.d(str, stringBuilder.toString());
                    }
                    return result;
                }
                throw new RuntimeException("eglInitialize failed");
            }
            throw new RuntimeException("eglGetDisplay returned EGL_NO_DISPLAY");
        }

        public EGLConfig chooseEglConfig(EGLDisplay eglDisplay) {
            int[] numEglConfigs = new int[1];
            EGLConfig[] eglConfigs = new EGLConfig[1];
            if (!EGL14.eglChooseConfig(eglDisplay, Gles2WatchFaceService.EGL_CONFIG_ATTRIB_LIST, 0, eglConfigs, 0, eglConfigs.length, numEglConfigs, 0)) {
                throw new RuntimeException("eglChooseConfig failed");
            } else if (numEglConfigs[0] != 0) {
                return eglConfigs[0];
            } else {
                throw new RuntimeException("no matching EGL configs");
            }
        }

        public EGLContext createEglContext(EGLDisplay eglDisplay, EGLConfig eglConfig) {
            EGLContext result = EGL14.eglCreateContext(eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT, Gles2WatchFaceService.EGL_CONTEXT_ATTRIB_LIST, 0);
            if (result != EGL14.EGL_NO_CONTEXT) {
                return result;
            }
            throw new RuntimeException("eglCreateContext failed");
        }

        public EGLSurface createWindowSurface(EGLDisplay eglDisplay, EGLConfig eglConfig, SurfaceHolder surfaceHolder) {
            EGLSurface result = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surfaceHolder.getSurface(), Gles2WatchFaceService.EGL_SURFACE_ATTRIB_LIST, 0);
            if (result != EGL14.EGL_NO_SURFACE) {
                return result;
            }
            throw new RuntimeException("eglCreateWindowSurface failed");
        }

        private void makeContextCurrent() {
            EGLDisplay eGLDisplay = this.mEglDisplay;
            EGLSurface eGLSurface = this.mEglSurface;
            if (!EGL14.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.mEglContext)) {
                throw new RuntimeException("eglMakeCurrent failed");
            }
        }

        @CallSuper
        public void onCreate(SurfaceHolder surfaceHolder) {
            if (Log.isLoggable(Gles2WatchFaceService.TAG, 3)) {
                Log.d(Gles2WatchFaceService.TAG, "onCreate");
            }
            super.onCreate(surfaceHolder);
            if (this.mEglDisplay == null) {
                this.mEglDisplay = initializeEglDisplay();
            }
            if (this.mEglConfig == null) {
                this.mEglConfig = chooseEglConfig(this.mEglDisplay);
            }
            if (this.mEglContext == null) {
                this.mEglContext = createEglContext(this.mEglDisplay, this.mEglConfig);
            }
        }

        @CallSuper
        public void onDestroy() {
            this.mDestroyed = true;
            this.mHandler.removeMessages(0);
            this.mChoreographer.removeFrameCallback(this.mFrameCallback);
            EGLSurface eGLSurface = this.mEglSurface;
            if (eGLSurface != null) {
                if (!EGL14.eglDestroySurface(this.mEglDisplay, eGLSurface)) {
                    Log.w(Gles2WatchFaceService.TAG, "eglDestroySurface failed");
                }
                this.mEglSurface = null;
            }
            EGLContext eGLContext = this.mEglContext;
            if (eGLContext != null) {
                if (!EGL14.eglDestroyContext(this.mEglDisplay, eGLContext)) {
                    Log.w(Gles2WatchFaceService.TAG, "eglDestroyContext failed");
                }
                this.mEglContext = null;
            }
            EGLDisplay eGLDisplay = this.mEglDisplay;
            if (eGLDisplay != null) {
                if (!EGL14.eglTerminate(eGLDisplay)) {
                    Log.w(Gles2WatchFaceService.TAG, "eglTerminate failed");
                }
                this.mEglDisplay = null;
            }
            super.onDestroy();
        }

        public void onGlContextCreated() {
        }

        public void onGlSurfaceCreated(int width, int height) {
        }

        public void onApplyWindowInsets(WindowInsets insets) {
            if (Log.isLoggable(Gles2WatchFaceService.TAG, 3)) {
                String str = Gles2WatchFaceService.TAG;
                String valueOf = String.valueOf(insets);
                StringBuilder stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 21);
                stringBuilder.append("onApplyWindowInsets: ");
                stringBuilder.append(valueOf);
                Log.d(str, stringBuilder.toString());
            }
            super.onApplyWindowInsets(insets);
            if (VERSION.SDK_INT <= 21) {
                Rect bounds = getSurfaceHolder().getSurfaceFrame();
                this.mInsetLeft = insets.getSystemWindowInsetLeft();
                this.mInsetBottom = insets.getSystemWindowInsetBottom();
                makeContextCurrent();
                GLES20.glViewport(-this.mInsetLeft, -this.mInsetBottom, bounds.width(), bounds.height());
            }
        }

        public final void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (Log.isLoggable(Gles2WatchFaceService.TAG, 3)) {
                Log.d(Gles2WatchFaceService.TAG, "onSurfaceChanged");
            }
            super.onSurfaceChanged(holder, format, width, height);
            EGLSurface eGLSurface = this.mEglSurface;
            if (eGLSurface != null) {
                if (!EGL14.eglDestroySurface(this.mEglDisplay, eGLSurface)) {
                    Log.w(Gles2WatchFaceService.TAG, "eglDestroySurface failed");
                }
            }
            this.mEglSurface = createWindowSurface(this.mEglDisplay, this.mEglConfig, holder);
            makeContextCurrent();
            GLES20.glViewport(-this.mInsetLeft, -this.mInsetBottom, width, height);
            if (!this.mCalledOnGlContextCreated) {
                this.mCalledOnGlContextCreated = true;
                onGlContextCreated();
            }
            onGlSurfaceCreated(width, height);
            invalidate();
        }

        public final void onSurfaceRedrawNeeded(SurfaceHolder holder) {
            if (Log.isLoggable(Gles2WatchFaceService.TAG, 3)) {
                Log.d(Gles2WatchFaceService.TAG, "onSurfaceRedrawNeeded");
            }
            super.onSurfaceRedrawNeeded(holder);
            drawFrame();
        }

        public final void onSurfaceDestroyed(SurfaceHolder holder) {
            if (Log.isLoggable(Gles2WatchFaceService.TAG, 3)) {
                Log.d(Gles2WatchFaceService.TAG, "onSurfaceDestroyed");
            }
            try {
                if (!EGL14.eglDestroySurface(this.mEglDisplay, this.mEglSurface)) {
                    Log.w(Gles2WatchFaceService.TAG, "eglDestroySurface failed");
                }
                this.mEglSurface = null;
            } finally {
                super.onSurfaceDestroyed(holder);
            }
        }

        public final void invalidate() {
            if (!this.mDrawRequested) {
                this.mDrawRequested = true;
                this.mChoreographer.postFrameCallback(this.mFrameCallback);
            }
        }

        public final void postInvalidate() {
            this.mHandler.sendEmptyMessage(0);
        }

        public void onDraw() {
        }

        private void drawFrame() {
            this.mDrawRequested = false;
            if (this.mEglSurface != null) {
                makeContextCurrent();
                onDraw();
                if (!EGL14.eglSwapBuffers(this.mEglDisplay, this.mEglSurface)) {
                    Log.w(Gles2WatchFaceService.TAG, "eglSwapBuffers failed");
                }
            }
        }
    }

    public Engine onCreateEngine() {
        return new Engine(this);
    }
}

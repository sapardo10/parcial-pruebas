package com.google.android.exoplayer2.util;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Handler;
import android.support.annotation.Nullable;

@TargetApi(17)
public final class EGLSurfaceTexture implements OnFrameAvailableListener, Runnable {
    private static final int[] EGL_CONFIG_ATTRIBUTES = new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12327, 12344, 12339, 4, 12344};
    private static final int EGL_PROTECTED_CONTENT_EXT = 12992;
    private static final int EGL_SURFACE_HEIGHT = 1;
    private static final int EGL_SURFACE_WIDTH = 1;
    public static final int SECURE_MODE_NONE = 0;
    public static final int SECURE_MODE_PROTECTED_PBUFFER = 2;
    public static final int SECURE_MODE_SURFACELESS_CONTEXT = 1;
    @Nullable
    private final TextureImageListener callback;
    @Nullable
    private EGLContext context;
    @Nullable
    private EGLDisplay display;
    private final Handler handler;
    @Nullable
    private EGLSurface surface;
    @Nullable
    private SurfaceTexture texture;
    private final int[] textureIdHolder;

    public static final class GlException extends RuntimeException {
        private GlException(String msg) {
            super(msg);
        }
    }

    public interface TextureImageListener {
        void onFrameAvailable();
    }

    public EGLSurfaceTexture(Handler handler) {
        this(handler, null);
    }

    public EGLSurfaceTexture(Handler handler, @Nullable TextureImageListener callback) {
        this.handler = handler;
        this.callback = callback;
        this.textureIdHolder = new int[1];
    }

    public void init(int secureMode) {
        this.display = getDefaultDisplay();
        EGLConfig config = chooseEGLConfig(this.display);
        this.context = createEGLContext(this.display, config, secureMode);
        this.surface = createEGLSurface(this.display, config, this.context, secureMode);
        generateTextureIds(this.textureIdHolder);
        this.texture = new SurfaceTexture(this.textureIdHolder[0]);
        this.texture.setOnFrameAvailableListener(this);
    }

    public void release() {
        EGLDisplay eGLDisplay;
        this.handler.removeCallbacks(this);
        try {
            if (this.texture != null) {
                this.texture.release();
                GLES20.glDeleteTextures(1, this.textureIdHolder, 0);
            }
            EGLDisplay eGLDisplay2 = this.display;
            if (eGLDisplay2 != null && !eGLDisplay2.equals(EGL14.EGL_NO_DISPLAY)) {
                EGL14.eglMakeCurrent(this.display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            }
            EGLSurface eGLSurface = this.surface;
            if (eGLSurface != null && !eGLSurface.equals(EGL14.EGL_NO_SURFACE)) {
                EGL14.eglDestroySurface(this.display, this.surface);
            }
            EGLContext eGLContext = this.context;
            if (eGLContext != null) {
                EGL14.eglDestroyContext(this.display, eGLContext);
            }
            if (Util.SDK_INT >= 19) {
                EGL14.eglReleaseThread();
            }
            eGLDisplay = this.display;
            if (eGLDisplay != null && !eGLDisplay.equals(EGL14.EGL_NO_DISPLAY)) {
                EGL14.eglTerminate(this.display);
            }
            this.display = null;
            this.context = null;
            this.surface = null;
            this.texture = null;
        } catch (Throwable th) {
            EGLDisplay eGLDisplay3 = this.display;
            if (eGLDisplay3 != null && !eGLDisplay3.equals(EGL14.EGL_NO_DISPLAY)) {
                EGL14.eglMakeCurrent(this.display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            }
            EGLSurface eGLSurface2 = this.surface;
            if (eGLSurface2 != null && !eGLSurface2.equals(EGL14.EGL_NO_SURFACE)) {
                EGL14.eglDestroySurface(this.display, this.surface);
            }
            EGLContext eGLContext2 = this.context;
            if (eGLContext2 != null) {
                EGL14.eglDestroyContext(this.display, eGLContext2);
            }
            if (Util.SDK_INT >= 19) {
                EGL14.eglReleaseThread();
            }
            eGLDisplay = this.display;
            if (eGLDisplay != null && !eGLDisplay.equals(EGL14.EGL_NO_DISPLAY)) {
                EGL14.eglTerminate(this.display);
            }
            this.display = null;
            this.context = null;
            this.surface = null;
            this.texture = null;
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return (SurfaceTexture) Assertions.checkNotNull(this.texture);
    }

    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.handler.post(this);
    }

    public void run() {
        dispatchOnFrameAvailable();
        SurfaceTexture surfaceTexture = this.texture;
        if (surfaceTexture != null) {
            try {
                surfaceTexture.updateTexImage();
            } catch (RuntimeException e) {
            }
        }
    }

    private void dispatchOnFrameAvailable() {
        TextureImageListener textureImageListener = this.callback;
        if (textureImageListener != null) {
            textureImageListener.onFrameAvailable();
        }
    }

    private static EGLDisplay getDefaultDisplay() {
        EGLDisplay display = EGL14.eglGetDisplay(0);
        if (display != null) {
            int[] version = new int[2];
            if (EGL14.eglInitialize(display, version, 0, version, 1)) {
                return display;
            }
            throw new GlException("eglInitialize failed");
        }
        throw new GlException("eglGetDisplay failed");
    }

    private static EGLConfig chooseEGLConfig(EGLDisplay display) {
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (EGL14.eglChooseConfig(display, EGL_CONFIG_ATTRIBUTES, 0, configs, 0, 1, numConfigs, 0) && numConfigs[0] > 0 && configs[0] != null) {
            return configs[0];
        }
        throw new GlException(Util.formatInvariant("eglChooseConfig failed: success=%b, numConfigs[0]=%d, configs[0]=%s", new Object[]{Boolean.valueOf(success), Integer.valueOf(numConfigs[0]), configs[0]}));
    }

    private static EGLContext createEGLContext(EGLDisplay display, EGLConfig config, int secureMode) {
        int[] glAttributes;
        if (secureMode == 0) {
            glAttributes = new int[]{12440, 2, 12344};
        } else {
            glAttributes = new int[]{12440, 2, EGL_PROTECTED_CONTENT_EXT, 1, 12344};
        }
        EGLContext context = EGL14.eglCreateContext(display, config, EGL14.EGL_NO_CONTEXT, glAttributes, 0);
        if (context != null) {
            return context;
        }
        throw new GlException("eglCreateContext failed");
    }

    private static EGLSurface createEGLSurface(EGLDisplay display, EGLConfig config, EGLContext context, int secureMode) {
        int[] pbufferAttributes;
        if (secureMode == 1) {
            pbufferAttributes = EGL14.EGL_NO_SURFACE;
        } else {
            EGLSurface surface = EGL14.eglCreatePbufferSurface(display, config, secureMode == 2 ? new int[]{12375, 1, 12374, 1, EGL_PROTECTED_CONTENT_EXT, 1, 12344} : new int[]{12375, 1, 12374, 1, 12344}, null);
            if (surface != null) {
                pbufferAttributes = surface;
            } else {
                throw new GlException("eglCreatePbufferSurface failed");
            }
        }
        if (EGL14.eglMakeCurrent(display, pbufferAttributes, pbufferAttributes, context) != null) {
            return pbufferAttributes;
        }
        throw new GlException("eglMakeCurrent failed");
    }

    private static void generateTextureIds(int[] textureIdHolder) {
        GLES20.glGenTextures(1, textureIdHolder, 0);
        int errorCode = GLES20.glGetError();
        if (errorCode != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("glGenTextures failed. Error: ");
            stringBuilder.append(Integer.toHexString(errorCode));
            throw new GlException(stringBuilder.toString());
        }
    }
}

package com.robotium.solo;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.view.View;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class GLRenderWrapper implements Renderer {
    private int glVersion;
    private int height;
    private CountDownLatch latch;
    private Renderer renderer;
    private boolean takeScreenshot = true;
    private final GLSurfaceView view;
    private int width;

    public GLRenderWrapper(GLSurfaceView view, Renderer renderer, CountDownLatch latch) {
        this.view = view;
        this.renderer = renderer;
        this.latch = latch;
        this.width = view.getWidth();
        this.height = view.getHeight();
        Integer out = (Integer) new Reflect(view).field("mEGLContextClientVersion").out(Integer.class);
        if (out != null) {
            this.glVersion = out.intValue();
            return;
        }
        this.glVersion = -1;
        this.takeScreenshot = false;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        this.renderer.onSurfaceCreated(gl, config);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        this.renderer.onSurfaceChanged(gl, width, height);
    }

    public void onDrawFrame(GL10 gl) {
        this.renderer.onDrawFrame(gl);
        if (this.takeScreenshot) {
            Bitmap screenshot;
            if (this.glVersion >= 2) {
                screenshot = savePixels(0, 0, this.width, this.height);
            } else {
                screenshot = savePixels(0, 0, this.width, this.height, gl);
            }
            new Reflect(this.view).field("mDrawingCache").type(View.class).in(screenshot);
            this.latch.countDown();
            this.takeScreenshot = false;
        }
    }

    public void setTakeScreenshot() {
        this.takeScreenshot = true;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    private Bitmap savePixels(int x, int y, int w, int h) {
        int[] b = new int[((y + h) * w)];
        int[] bt = new int[(w * h)];
        Buffer ib = IntBuffer.wrap(b);
        ib.position(0);
        GLES20.glReadPixels(x, 0, w, y + h, 6408, 5121, ib);
        int i = 0;
        int k = 0;
        while (i < h) {
            for (int j = 0; j < w; j++) {
                int pix = b[(i * w) + j];
                int i2 = (((h - k) - 1) * w) + j;
                bt[i2] = ((-16711936 & pix) | ((pix << 16) & 16711680)) | ((pix >> 16) & 255);
            }
            i++;
            k++;
        }
        return Bitmap.createBitmap(bt, w, h, Config.ARGB_8888);
    }

    private static Bitmap savePixels(int x, int y, int w, int h, GL10 gl) {
        int[] b = new int[((y + h) * w)];
        int[] bt = new int[(w * h)];
        Buffer ib = IntBuffer.wrap(b);
        ib.position(0);
        gl.glReadPixels(x, 0, w, y + h, 6408, 5121, ib);
        int i = 0;
        int k = 0;
        while (i < h) {
            for (int j = 0; j < w; j++) {
                int pix = b[(i * w) + j];
                int i2 = (((h - k) - 1) * w) + j;
                bt[i2] = ((-16711936 & pix) | ((pix << 16) & 16711680)) | ((pix >> 16) & 255);
            }
            i++;
            k++;
        }
        return Bitmap.createBitmap(bt, w, h, Config.ARGB_8888);
    }
}

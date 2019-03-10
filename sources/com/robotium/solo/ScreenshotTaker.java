package com.robotium.solo;

import android.app.Activity;
import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import com.robotium.solo.Solo.Config;
import com.robotium.solo.Solo.Config.ScreenshotFileType;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class ScreenshotTaker {
    private static final long TIMEOUT_SCREENSHOT_MUTEX = TimeUnit.SECONDS.toMillis(2);
    private final String LOG_TAG = "Robotium";
    private final ActivityUtils activityUtils;
    private final Config config;
    private final Instrumentation instrumentation;
    private ScreenShotSaver screenShotSaver = null;
    private HandlerThread screenShotSaverThread = null;
    private final Object screenshotMutex = new Object();
    private ScreenshotSequenceThread screenshotSequenceThread = null;
    private final Sleeper sleeper;
    private final ViewFetcher viewFetcher;

    private class ScreenShotSaver extends Handler {
        public ScreenShotSaver(HandlerThread thread) {
            super(thread.getLooper());
        }

        public void saveBitmap(Bitmap bitmap, String name, int quality) {
            Message message = obtainMessage();
            message.arg1 = quality;
            message.obj = bitmap;
            message.getData().putString(PodDBAdapter.KEY_NAME, name);
            sendMessage(message);
        }

        public void handleMessage(Message message) {
            synchronized (ScreenshotTaker.this.screenshotMutex) {
                String name = message.getData().getString(PodDBAdapter.KEY_NAME);
                int quality = message.arg1;
                Bitmap b = message.obj;
                if (b != null) {
                    saveFile(name, b, quality);
                    b.recycle();
                } else {
                    Log.d("Robotium", "NULL BITMAP!!");
                }
                ScreenshotTaker.this.screenshotMutex.notify();
            }
        }

        private void saveFile(String name, Bitmap b, int quality) {
            String fileName = ScreenshotTaker.this.getFileName(name);
            File directory = new File(ScreenshotTaker.this.config.screenshotSavePath);
            directory.mkdir();
            try {
                FileOutputStream fos = new FileOutputStream(new File(directory, fileName));
                if (ScreenshotTaker.this.config.screenshotFileType == ScreenshotFileType.JPEG) {
                    if (!b.compress(CompressFormat.JPEG, quality, fos)) {
                        Log.d("Robotium", "Compress/Write failed");
                    }
                } else if (!b.compress(CompressFormat.PNG, quality, fos)) {
                    Log.d("Robotium", "Compress/Write failed");
                }
                fos.flush();
                fos.close();
            } catch (Exception e) {
                Log.d("Robotium", "Can't save the screenshot! Requires write permission (android.permission.WRITE_EXTERNAL_STORAGE) in AndroidManifest.xml of the application under test.");
                e.printStackTrace();
            }
        }
    }

    private class ScreenshotRunnable implements Runnable {
        private String name;
        private int quality;
        private View view;

        public ScreenshotRunnable(View _view, String _name, int _quality) {
            this.view = _view;
            this.name = _name;
            this.quality = _quality;
        }

        public void run() {
            View view = this.view;
            if (view != null) {
                Bitmap b;
                if (view instanceof WebView) {
                    b = ScreenshotTaker.this.getBitmapOfWebView((WebView) view);
                } else {
                    b = ScreenshotTaker.this.getBitmapOfView(view);
                }
                if (b != null) {
                    ScreenshotTaker.this.screenShotSaver.saveBitmap(b, this.name, this.quality);
                    return;
                }
                Log.d("Robotium", "NULL BITMAP!!");
            }
            synchronized (ScreenshotTaker.this.screenshotMutex) {
                ScreenshotTaker.this.screenshotMutex.notify();
            }
        }
    }

    private class ScreenshotSequenceThread extends Thread {
        private int frameDelay;
        private boolean keepRunning = true;
        private int maxFrames;
        private String name;
        private int quality;
        private int seqno = null;

        public ScreenshotSequenceThread(String _name, int _quality, int _frameDelay, int _maxFrames) {
            this.name = _name;
            this.quality = _quality;
            this.frameDelay = _frameDelay;
            this.maxFrames = _maxFrames;
        }

        public void run() {
            while (this.seqno < this.maxFrames) {
                if (this.keepRunning) {
                    if (!Thread.interrupted()) {
                        doScreenshot();
                        this.seqno++;
                        try {
                            Thread.sleep((long) this.frameDelay);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
            ScreenshotTaker.this.screenshotSequenceThread = null;
        }

        public void doScreenshot() {
            View v = ScreenshotTaker.this.getScreenshotView();
            if (v == null) {
                this.keepRunning = false;
            }
            String final_name = new StringBuilder();
            final_name.append(this.name);
            final_name.append("_");
            final_name.append(this.seqno);
            final_name = final_name.toString();
            ScreenshotRunnable r = new ScreenshotRunnable(v, final_name, this.quality);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("taking screenshot ");
            stringBuilder.append(final_name);
            Log.d("Robotium", stringBuilder.toString());
            Activity activity = ScreenshotTaker.this.activityUtils.getCurrentActivity(false);
            if (activity != null) {
                activity.runOnUiThread(r);
            } else {
                ScreenshotTaker.this.instrumentation.runOnMainSync(r);
            }
        }

        public void interrupt() {
            this.keepRunning = false;
            super.interrupt();
        }
    }

    ScreenshotTaker(Config config, Instrumentation instrumentation, ActivityUtils activityUtils, ViewFetcher viewFetcher, Sleeper sleeper) {
        this.config = config;
        this.instrumentation = instrumentation;
        this.activityUtils = activityUtils;
        this.viewFetcher = viewFetcher;
        this.sleeper = sleeper;
    }

    public void takeScreenshot(String name, int quality) {
        View decorView = getScreenshotView();
        if (decorView != null) {
            initScreenShotSaver();
            ScreenshotRunnable runnable = new ScreenshotRunnable(decorView, name, quality);
            synchronized (this.screenshotMutex) {
                Activity activity = this.activityUtils.getCurrentActivity(false);
                if (activity != null) {
                    activity.runOnUiThread(runnable);
                } else {
                    this.instrumentation.runOnMainSync(runnable);
                }
                try {
                    this.screenshotMutex.wait(TIMEOUT_SCREENSHOT_MUTEX);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public void startScreenshotSequence(String name, int quality, int frameDelay, int maxFrames) {
        initScreenShotSaver();
        if (this.screenshotSequenceThread == null) {
            this.screenshotSequenceThread = new ScreenshotSequenceThread(name, quality, frameDelay, maxFrames);
            this.screenshotSequenceThread.start();
            return;
        }
        throw new RuntimeException("only one screenshot sequence is supported at a time");
    }

    public void stopScreenshotSequence() {
        ScreenshotSequenceThread screenshotSequenceThread = this.screenshotSequenceThread;
        if (screenshotSequenceThread != null) {
            screenshotSequenceThread.interrupt();
            this.screenshotSequenceThread = null;
        }
    }

    private View getScreenshotView() {
        View decorView = this.viewFetcher;
        decorView = decorView.getRecentDecorView(decorView.getWindowDecorViews());
        long endTime = SystemClock.uptimeMillis() + ((long) Timeout.getSmallTimeout());
        while (decorView == null) {
            if (SystemClock.uptimeMillis() > endTime) {
                return null;
            }
            this.sleeper.sleepMini();
            ViewFetcher viewFetcher = this.viewFetcher;
            decorView = viewFetcher.getRecentDecorView(viewFetcher.getWindowDecorViews());
        }
        wrapAllGLViews(decorView);
        return decorView;
    }

    private void wrapAllGLViews(View decorView) {
        ArrayList<GLSurfaceView> currentViews = this.viewFetcher.getCurrentViews(GLSurfaceView.class, true, decorView);
        CountDownLatch latch = new CountDownLatch(currentViews.size());
        Iterator i$ = currentViews.iterator();
        while (i$.hasNext()) {
            GLSurfaceView glView = (GLSurfaceView) i$.next();
            Object renderContainer = new Reflect(glView).field("mGLThread").type(GLSurfaceView.class).out(Object.class);
            Renderer renderer = (Renderer) new Reflect(renderContainer).field("mRenderer").out(Renderer.class);
            if (renderer == null) {
                renderer = (Renderer) new Reflect(glView).field("mRenderer").out(Renderer.class);
                renderContainer = glView;
            }
            if (renderer == null) {
                latch.countDown();
            } else if (renderer instanceof GLRenderWrapper) {
                GLRenderWrapper wrapper = (GLRenderWrapper) renderer;
                wrapper.setTakeScreenshot();
                wrapper.setLatch(latch);
            } else {
                new Reflect(renderContainer).field("mRenderer").in(new GLRenderWrapper(glView, renderer, latch));
            }
        }
        try {
            latch.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private Bitmap getBitmapOfWebView(WebView webView) {
        Picture picture = webView.capturePicture();
        Bitmap b = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
        picture.draw(new Canvas(b));
        return b;
    }

    private Bitmap getBitmapOfView(View view) {
        view.destroyDrawingCache();
        view.buildDrawingCache(false);
        Bitmap orig = view.getDrawingCache();
        if (orig == null) {
            return null;
        }
        Bitmap.Config config = orig.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        Bitmap b = orig.copy(config, false);
        orig.recycle();
        view.destroyDrawingCache();
        return b;
    }

    private String getFileName(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy-hhmmss");
        StringBuilder stringBuilder;
        if (name == null) {
            if (this.config.screenshotFileType == ScreenshotFileType.JPEG) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(sdf.format(new Date()).toString());
                stringBuilder.append(".jpg");
                return stringBuilder.toString();
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append(sdf.format(new Date()).toString());
            stringBuilder.append(".png");
            return stringBuilder.toString();
        } else if (this.config.screenshotFileType == ScreenshotFileType.JPEG) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(name);
            stringBuilder.append(".jpg");
            return stringBuilder.toString();
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append(name);
            stringBuilder.append(".png");
            return stringBuilder.toString();
        }
    }

    private void initScreenShotSaver() {
        if (this.screenShotSaverThread != null) {
            if (this.screenShotSaver != null) {
                return;
            }
        }
        this.screenShotSaverThread = new HandlerThread("ScreenShotSaver");
        this.screenShotSaverThread.start();
        this.screenShotSaver = new ScreenShotSaver(this.screenShotSaverThread);
    }
}

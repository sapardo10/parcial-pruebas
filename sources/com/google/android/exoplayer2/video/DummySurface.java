package com.google.android.exoplayer2.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.view.Surface;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.EGLSurfaceTexture;
import com.google.android.exoplayer2.util.Util;

@TargetApi(17)
public final class DummySurface extends Surface {
    private static final String EXTENSION_PROTECTED_CONTENT = "EGL_EXT_protected_content";
    private static final String EXTENSION_SURFACELESS_CONTEXT = "EGL_KHR_surfaceless_context";
    private static final String TAG = "DummySurface";
    private static int secureMode;
    private static boolean secureModeInitialized;
    public final boolean secure;
    private final DummySurfaceThread thread;
    private boolean threadReleased;

    private static class DummySurfaceThread extends HandlerThread implements Callback {
        private static final int MSG_INIT = 1;
        private static final int MSG_RELEASE = 2;
        private EGLSurfaceTexture eglSurfaceTexture;
        private Handler handler;
        @Nullable
        private Error initError;
        @Nullable
        private RuntimeException initException;
        @Nullable
        private DummySurface surface;

        public boolean handleMessage(android.os.Message r5) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:59:0x005e in {2, 4, 11, 12, 14, 20, 24, 34, 37, 45, 46, 49, 54, 58} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r4 = this;
            r0 = r5.what;
            r1 = 1;
            switch(r0) {
                case 1: goto L_0x001e;
                case 2: goto L_0x0007;
                default: goto L_0x0006;
            };
        L_0x0006:
            return r1;
        L_0x0007:
            r4.releaseInternal();	 Catch:{ Throwable -> 0x0010 }
        L_0x000a:
            r4.quit();
            goto L_0x0019;
        L_0x000e:
            r0 = move-exception;
            goto L_0x001a;
        L_0x0010:
            r0 = move-exception;
            r2 = "DummySurface";	 Catch:{ all -> 0x000e }
            r3 = "Failed to release dummy surface";	 Catch:{ all -> 0x000e }
            com.google.android.exoplayer2.util.Log.m7e(r2, r3, r0);	 Catch:{ all -> 0x000e }
            goto L_0x000a;
        L_0x0019:
            return r1;
        L_0x001a:
            r4.quit();
            throw r0;
        L_0x001e:
            r0 = r5.arg1;	 Catch:{ RuntimeException -> 0x0041, Error -> 0x002e }
            r4.initInternal(r0);	 Catch:{ RuntimeException -> 0x0041, Error -> 0x002e }
            monitor-enter(r4);
            r4.notify();	 Catch:{ all -> 0x0029 }
            monitor-exit(r4);	 Catch:{ all -> 0x0029 }
        L_0x0028:
            goto L_0x0051;	 Catch:{ all -> 0x0029 }
        L_0x0029:
            r0 = move-exception;	 Catch:{ all -> 0x0029 }
            monitor-exit(r4);	 Catch:{ all -> 0x0029 }
            throw r0;
        L_0x002c:
            r0 = move-exception;
            goto L_0x0055;
        L_0x002e:
            r0 = move-exception;
            r2 = "DummySurface";	 Catch:{ all -> 0x002c }
            r3 = "Failed to initialize dummy surface";	 Catch:{ all -> 0x002c }
            com.google.android.exoplayer2.util.Log.m7e(r2, r3, r0);	 Catch:{ all -> 0x002c }
            r4.initError = r0;	 Catch:{ all -> 0x002c }
            monitor-enter(r4);
            r4.notify();	 Catch:{ all -> 0x003e }
            monitor-exit(r4);	 Catch:{ all -> 0x003e }
            goto L_0x0028;	 Catch:{ all -> 0x003e }
        L_0x003e:
            r0 = move-exception;	 Catch:{ all -> 0x003e }
            monitor-exit(r4);	 Catch:{ all -> 0x003e }
            throw r0;
        L_0x0041:
            r0 = move-exception;
            r2 = "DummySurface";	 Catch:{ all -> 0x002c }
            r3 = "Failed to initialize dummy surface";	 Catch:{ all -> 0x002c }
            com.google.android.exoplayer2.util.Log.m7e(r2, r3, r0);	 Catch:{ all -> 0x002c }
            r4.initException = r0;	 Catch:{ all -> 0x002c }
            monitor-enter(r4);
            r4.notify();	 Catch:{ all -> 0x0052 }
            monitor-exit(r4);	 Catch:{ all -> 0x0052 }
            goto L_0x0028;	 Catch:{ all -> 0x0052 }
        L_0x0051:
            return r1;	 Catch:{ all -> 0x0052 }
        L_0x0052:
            r0 = move-exception;	 Catch:{ all -> 0x0052 }
            monitor-exit(r4);	 Catch:{ all -> 0x0052 }
            throw r0;
        L_0x0055:
            monitor-enter(r4);
            r4.notify();	 Catch:{ all -> 0x005b }
            monitor-exit(r4);	 Catch:{ all -> 0x005b }
            throw r0;
        L_0x005b:
            r0 = move-exception;
            monitor-exit(r4);	 Catch:{ all -> 0x005b }
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.video.DummySurface.DummySurfaceThread.handleMessage(android.os.Message):boolean");
        }

        public com.google.android.exoplayer2.video.DummySurface init(int r5) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:33:0x005a in {11, 12, 14, 19, 20, 26, 27, 28, 32} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r4 = this;
            r4.start();
            r0 = new android.os.Handler;
            r1 = r4.getLooper();
            r0.<init>(r1, r4);
            r4.handler = r0;
            r0 = new com.google.android.exoplayer2.util.EGLSurfaceTexture;
            r1 = r4.handler;
            r0.<init>(r1);
            r4.eglSurfaceTexture = r0;
            r0 = 0;
            monitor-enter(r4);
            r1 = r4.handler;	 Catch:{ all -> 0x0057 }
            r2 = 1;	 Catch:{ all -> 0x0057 }
            r3 = 0;	 Catch:{ all -> 0x0057 }
            r1 = r1.obtainMessage(r2, r5, r3);	 Catch:{ all -> 0x0057 }
            r1.sendToTarget();	 Catch:{ all -> 0x0057 }
        L_0x0024:
            r1 = r4.surface;	 Catch:{ all -> 0x0057 }
            if (r1 != 0) goto L_0x0037;	 Catch:{ all -> 0x0057 }
        L_0x0028:
            r1 = r4.initException;	 Catch:{ all -> 0x0057 }
            if (r1 != 0) goto L_0x0037;	 Catch:{ all -> 0x0057 }
        L_0x002c:
            r1 = r4.initError;	 Catch:{ all -> 0x0057 }
            if (r1 != 0) goto L_0x0037;
        L_0x0030:
            r4.wait();	 Catch:{ InterruptedException -> 0x0034 }
        L_0x0033:
            goto L_0x0024;
        L_0x0034:
            r1 = move-exception;
            r0 = 1;
            goto L_0x0033;
            monitor-exit(r4);	 Catch:{ all -> 0x0057 }
            if (r0 == 0) goto L_0x0043;
        L_0x003b:
            r1 = java.lang.Thread.currentThread();
            r1.interrupt();
            goto L_0x0044;
        L_0x0044:
            r1 = r4.initException;
            if (r1 != 0) goto L_0x0056;
        L_0x0048:
            r1 = r4.initError;
            if (r1 != 0) goto L_0x0055;
        L_0x004c:
            r1 = r4.surface;
            r1 = com.google.android.exoplayer2.util.Assertions.checkNotNull(r1);
            r1 = (com.google.android.exoplayer2.video.DummySurface) r1;
            return r1;
        L_0x0055:
            throw r1;
        L_0x0056:
            throw r1;
        L_0x0057:
            r1 = move-exception;
            monitor-exit(r4);	 Catch:{ all -> 0x0057 }
            throw r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.video.DummySurface.DummySurfaceThread.init(int):com.google.android.exoplayer2.video.DummySurface");
        }

        public DummySurfaceThread() {
            super("dummySurface");
        }

        public void release() {
            Assertions.checkNotNull(this.handler);
            this.handler.sendEmptyMessage(2);
        }

        private void initInternal(int secureMode) {
            Assertions.checkNotNull(this.eglSurfaceTexture);
            this.eglSurfaceTexture.init(secureMode);
            this.surface = new DummySurface(this, this.eglSurfaceTexture.getSurfaceTexture(), secureMode != 0);
        }

        private void releaseInternal() {
            Assertions.checkNotNull(this.eglSurfaceTexture);
            this.eglSurfaceTexture.release();
        }
    }

    public static synchronized boolean isSecureSupported(Context context) {
        boolean z;
        synchronized (DummySurface.class) {
            z = true;
            if (!secureModeInitialized) {
                secureMode = Util.SDK_INT < 24 ? 0 : getSecureModeV24(context);
                secureModeInitialized = true;
            }
            if (secureMode == 0) {
                z = false;
            }
        }
        return z;
    }

    public static DummySurface newInstanceV17(Context context, boolean secure) {
        boolean z;
        DummySurfaceThread thread;
        assertApiLevel17OrHigher();
        int i = 0;
        if (secure) {
            if (!isSecureSupported(context)) {
                z = false;
                Assertions.checkState(z);
                thread = new DummySurfaceThread();
                if (secure) {
                    i = secureMode;
                }
                return thread.init(i);
            }
        }
        z = true;
        Assertions.checkState(z);
        thread = new DummySurfaceThread();
        if (secure) {
            i = secureMode;
        }
        return thread.init(i);
    }

    private DummySurface(DummySurfaceThread thread, SurfaceTexture surfaceTexture, boolean secure) {
        super(surfaceTexture);
        this.thread = thread;
        this.secure = secure;
    }

    public void release() {
        super.release();
        synchronized (this.thread) {
            if (!this.threadReleased) {
                this.thread.release();
                this.threadReleased = true;
            }
        }
    }

    private static void assertApiLevel17OrHigher() {
        if (Util.SDK_INT < 17) {
            throw new UnsupportedOperationException("Unsupported prior to API level 17");
        }
    }

    @TargetApi(24)
    private static int getSecureModeV24(Context context) {
        if (Util.SDK_INT < 26 && ("samsung".equals(Util.MANUFACTURER) || "XT1650".equals(Util.MODEL))) {
            return 0;
        }
        if (Util.SDK_INT < 26 && !context.getPackageManager().hasSystemFeature("android.hardware.vr.high_performance")) {
            return 0;
        }
        String eglExtensions = EGL14.eglQueryString(EGL14.eglGetDisplay(0), 12373);
        if (eglExtensions == null || !eglExtensions.contains(EXTENSION_PROTECTED_CONTENT)) {
            return 0;
        }
        return eglExtensions.contains(EXTENSION_SURFACELESS_CONTEXT) ? 1 : 2;
    }
}

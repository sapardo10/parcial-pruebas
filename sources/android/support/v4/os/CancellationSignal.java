package android.support.v4.os;

import android.os.Build.VERSION;

public final class CancellationSignal {
    private boolean mCancelInProgress;
    private Object mCancellationSignalObj;
    private boolean mIsCanceled;
    private OnCancelListener mOnCancelListener;

    public interface OnCancelListener {
        void onCancel();
    }

    public boolean isCanceled() {
        boolean z;
        synchronized (this) {
            z = this.mIsCanceled;
        }
        return z;
    }

    public void throwIfCanceled() {
        if (isCanceled()) {
            throw new OperationCanceledException();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void cancel() {
        /*
        r5 = this;
        monitor-enter(r5);
        r0 = 0;
        r1 = r5.mIsCanceled;	 Catch:{ all -> 0x0044 }
        if (r1 == 0) goto L_0x0008;
    L_0x0006:
        monitor-exit(r5);	 Catch:{ all -> 0x0044 }
        return;
    L_0x0008:
        r1 = 1;
        r5.mIsCanceled = r1;	 Catch:{ all -> 0x0044 }
        r5.mCancelInProgress = r1;	 Catch:{ all -> 0x0044 }
        r1 = r5.mOnCancelListener;	 Catch:{ all -> 0x0044 }
        r2 = r1;
        r0 = r5.mCancellationSignalObj;	 Catch:{ all -> 0x0048 }
        monitor-exit(r5);	 Catch:{ all -> 0x0048 }
        r1 = 0;
        if (r2 == 0) goto L_0x001c;
    L_0x0016:
        r2.onCancel();	 Catch:{ all -> 0x001a }
        goto L_0x001d;
    L_0x001a:
        r3 = move-exception;
        goto L_0x002c;
    L_0x001d:
        if (r0 == 0) goto L_0x0037;
    L_0x001f:
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x001a }
        r4 = 16;
        if (r3 < r4) goto L_0x0037;
    L_0x0025:
        r3 = r0;
        r3 = (android.os.CancellationSignal) r3;	 Catch:{ all -> 0x001a }
        r3.cancel();	 Catch:{ all -> 0x001a }
        goto L_0x0038;
    L_0x002c:
        monitor-enter(r5);
        r5.mCancelInProgress = r1;	 Catch:{ all -> 0x0034 }
        r5.notifyAll();	 Catch:{ all -> 0x0034 }
        monitor-exit(r5);	 Catch:{ all -> 0x0034 }
        throw r3;
    L_0x0034:
        r1 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0034 }
        throw r1;
    L_0x0038:
        monitor-enter(r5);
        r5.mCancelInProgress = r1;	 Catch:{ all -> 0x0041 }
        r5.notifyAll();	 Catch:{ all -> 0x0041 }
        monitor-exit(r5);	 Catch:{ all -> 0x0041 }
        return;
    L_0x0041:
        r1 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0041 }
        throw r1;
    L_0x0044:
        r1 = move-exception;
        r2 = r0;
    L_0x0046:
        monitor-exit(r5);	 Catch:{ all -> 0x0048 }
        throw r1;
    L_0x0048:
        r1 = move-exception;
        goto L_0x0046;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.os.CancellationSignal.cancel():void");
    }

    public void setOnCancelListener(OnCancelListener listener) {
        synchronized (this) {
            waitForCancelFinishedLocked();
            if (this.mOnCancelListener == listener) {
                return;
            }
            this.mOnCancelListener = listener;
            if (this.mIsCanceled) {
                if (listener != null) {
                    listener.onCancel();
                    return;
                }
            }
        }
    }

    public Object getCancellationSignalObject() {
        if (VERSION.SDK_INT < 16) {
            return null;
        }
        Object obj;
        synchronized (this) {
            if (this.mCancellationSignalObj == null) {
                this.mCancellationSignalObj = new android.os.CancellationSignal();
                if (this.mIsCanceled) {
                    ((android.os.CancellationSignal) this.mCancellationSignalObj).cancel();
                }
            }
            obj = this.mCancellationSignalObj;
        }
        return obj;
    }

    private void waitForCancelFinishedLocked() {
        while (this.mCancelInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }
}

package com.bumptech.glide.request.target;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.transition.Transition;

public final class PreloadTarget<Z> extends SimpleTarget<Z> {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper(), new C05351());
    private static final int MESSAGE_CLEAR = 1;
    private final RequestManager requestManager;

    /* renamed from: com.bumptech.glide.request.target.PreloadTarget$1 */
    class C05351 implements Callback {
        C05351() {
        }

        public boolean handleMessage(Message message) {
            if (message.what != 1) {
                return false;
            }
            ((PreloadTarget) message.obj).clear();
            return true;
        }
    }

    public static <Z> PreloadTarget<Z> obtain(RequestManager requestManager, int width, int height) {
        return new PreloadTarget(requestManager, width, height);
    }

    private PreloadTarget(RequestManager requestManager, int width, int height) {
        super(width, height);
        this.requestManager = requestManager;
    }

    public void onResourceReady(@NonNull Z z, @Nullable Transition<? super Z> transition) {
        HANDLER.obtainMessage(1, this).sendToTarget();
    }

    void clear() {
        this.requestManager.clear((Target) this);
    }
}

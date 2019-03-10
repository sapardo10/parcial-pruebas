package com.bumptech.glide.request;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

public class ThumbnailRequestCoordinator implements RequestCoordinator, Request {
    private Request full;
    private boolean isRunning;
    @Nullable
    private final RequestCoordinator parent;
    private Request thumb;

    @VisibleForTesting
    ThumbnailRequestCoordinator() {
        this(null);
    }

    public ThumbnailRequestCoordinator(@Nullable RequestCoordinator parent) {
        this.parent = parent;
    }

    public void setRequests(Request full, Request thumb) {
        this.full = full;
        this.thumb = thumb;
    }

    public boolean canSetImage(Request request) {
        return parentCanSetImage() && (request.equals(this.full) || !this.full.isResourceSet());
    }

    private boolean parentCanSetImage() {
        RequestCoordinator requestCoordinator = this.parent;
        if (requestCoordinator != null) {
            if (!requestCoordinator.canSetImage(this)) {
                return false;
            }
        }
        return true;
    }

    public boolean canNotifyStatusChanged(Request request) {
        return parentCanNotifyStatusChanged() && request.equals(this.full) && !isAnyResourceSet();
    }

    public boolean canNotifyCleared(Request request) {
        return parentCanNotifyCleared() && request.equals(this.full);
    }

    private boolean parentCanNotifyCleared() {
        RequestCoordinator requestCoordinator = this.parent;
        if (requestCoordinator != null) {
            if (!requestCoordinator.canNotifyCleared(this)) {
                return false;
            }
        }
        return true;
    }

    private boolean parentCanNotifyStatusChanged() {
        RequestCoordinator requestCoordinator = this.parent;
        if (requestCoordinator != null) {
            if (!requestCoordinator.canNotifyStatusChanged(this)) {
                return false;
            }
        }
        return true;
    }

    public boolean isAnyResourceSet() {
        if (!parentIsAnyResourceSet()) {
            if (!isResourceSet()) {
                return false;
            }
        }
        return true;
    }

    public void onRequestSuccess(Request request) {
        if (!request.equals(this.thumb)) {
            RequestCoordinator requestCoordinator = this.parent;
            if (requestCoordinator != null) {
                requestCoordinator.onRequestSuccess(this);
            }
            if (!this.thumb.isComplete()) {
                this.thumb.clear();
            }
        }
    }

    public void onRequestFailed(Request request) {
        if (request.equals(this.full)) {
            RequestCoordinator requestCoordinator = this.parent;
            if (requestCoordinator != null) {
                requestCoordinator.onRequestFailed(this);
            }
        }
    }

    private boolean parentIsAnyResourceSet() {
        RequestCoordinator requestCoordinator = this.parent;
        return requestCoordinator != null && requestCoordinator.isAnyResourceSet();
    }

    public void begin() {
        this.isRunning = true;
        if (!this.full.isComplete() && !this.thumb.isRunning()) {
            this.thumb.begin();
        }
        if (this.isRunning && !this.full.isRunning()) {
            this.full.begin();
        }
    }

    public void clear() {
        this.isRunning = false;
        this.thumb.clear();
        this.full.clear();
    }

    public boolean isRunning() {
        return this.full.isRunning();
    }

    public boolean isComplete() {
        if (!this.full.isComplete()) {
            if (!this.thumb.isComplete()) {
                return false;
            }
        }
        return true;
    }

    public boolean isResourceSet() {
        if (!this.full.isResourceSet()) {
            if (!this.thumb.isResourceSet()) {
                return false;
            }
        }
        return true;
    }

    public boolean isCleared() {
        return this.full.isCleared();
    }

    public boolean isFailed() {
        return this.full.isFailed();
    }

    public void recycle() {
        this.full.recycle();
        this.thumb.recycle();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isEquivalentTo(com.bumptech.glide.request.Request r5) {
        /*
        r4 = this;
        r0 = r5 instanceof com.bumptech.glide.request.ThumbnailRequestCoordinator;
        r1 = 0;
        if (r0 == 0) goto L_0x0030;
    L_0x0005:
        r0 = r5;
        r0 = (com.bumptech.glide.request.ThumbnailRequestCoordinator) r0;
        r2 = r4.full;
        if (r2 != 0) goto L_0x0011;
    L_0x000c:
        r2 = r0.full;
        if (r2 != 0) goto L_0x002d;
    L_0x0010:
        goto L_0x0019;
    L_0x0011:
        r3 = r0.full;
        r2 = r2.isEquivalentTo(r3);
        if (r2 == 0) goto L_0x002d;
    L_0x0019:
        r2 = r4.thumb;
        if (r2 != 0) goto L_0x0022;
    L_0x001d:
        r2 = r0.thumb;
        if (r2 != 0) goto L_0x002d;
    L_0x0021:
        goto L_0x002a;
    L_0x0022:
        r3 = r0.thumb;
        r2 = r2.isEquivalentTo(r3);
        if (r2 == 0) goto L_0x002c;
    L_0x002a:
        r1 = 1;
        goto L_0x002f;
    L_0x002c:
        goto L_0x002e;
    L_0x002f:
        return r1;
    L_0x0030:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.request.ThumbnailRequestCoordinator.isEquivalentTo(com.bumptech.glide.request.Request):boolean");
    }
}

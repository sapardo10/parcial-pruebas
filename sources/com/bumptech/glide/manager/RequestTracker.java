package com.bumptech.glide.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public class RequestTracker {
    private static final String TAG = "RequestTracker";
    private boolean isPaused;
    private final List<Request> pendingRequests = new ArrayList();
    private final Set<Request> requests = Collections.newSetFromMap(new WeakHashMap());

    public void runRequest(@NonNull Request request) {
        this.requests.add(request);
        if (this.isPaused) {
            request.clear();
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "Paused, delaying request");
            }
            this.pendingRequests.add(request);
            return;
        }
        request.begin();
    }

    @VisibleForTesting
    void addRequest(Request request) {
        this.requests.add(request);
    }

    public boolean clearRemoveAndRecycle(@Nullable Request request) {
        return clearRemoveAndMaybeRecycle(request, true);
    }

    private boolean clearRemoveAndMaybeRecycle(@Nullable Request request, boolean isSafeToRecycle) {
        boolean isOwnedByUs = true;
        if (request == null) {
            return true;
        }
        boolean isOwnedByUs2 = this.requests.remove(request);
        if (!this.pendingRequests.remove(request)) {
            if (!isOwnedByUs2) {
                isOwnedByUs = false;
            }
        }
        if (isOwnedByUs) {
            request.clear();
            if (isSafeToRecycle) {
                request.recycle();
            }
        }
        return isOwnedByUs;
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public void pauseRequests() {
        this.isPaused = true;
        for (Request request : Util.getSnapshot(this.requests)) {
            if (request.isRunning()) {
                request.clear();
                this.pendingRequests.add(request);
            }
        }
    }

    public void pauseAllRequests() {
        this.isPaused = true;
        for (Request request : Util.getSnapshot(this.requests)) {
            if (!request.isRunning()) {
                if (request.isComplete()) {
                }
            }
            request.clear();
            this.pendingRequests.add(request);
        }
    }

    public void resumeRequests() {
        this.isPaused = false;
        for (Request request : Util.getSnapshot(this.requests)) {
            if (!request.isComplete() && !request.isRunning()) {
                request.begin();
            }
        }
        this.pendingRequests.clear();
    }

    public void clearRequests() {
        for (Request request : Util.getSnapshot(this.requests)) {
            clearRemoveAndMaybeRecycle(request, false);
        }
        this.pendingRequests.clear();
    }

    public void restartRequests() {
        for (Request request : Util.getSnapshot(this.requests)) {
            if (!request.isComplete() && !request.isCleared()) {
                request.clear();
                if (this.isPaused) {
                    this.pendingRequests.add(request);
                } else {
                    request.begin();
                }
            }
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append("{numRequests=");
        stringBuilder.append(this.requests.size());
        stringBuilder.append(", isPaused=");
        stringBuilder.append(this.isPaused);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}

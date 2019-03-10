package com.bumptech.glide.request;

import android.support.annotation.Nullable;

public final class ErrorRequestCoordinator implements RequestCoordinator, Request {
    private Request error;
    @Nullable
    private final RequestCoordinator parent;
    private Request primary;

    public ErrorRequestCoordinator(@Nullable RequestCoordinator parent) {
        this.parent = parent;
    }

    public void setRequests(Request primary, Request error) {
        this.primary = primary;
        this.error = error;
    }

    public void begin() {
        if (!this.primary.isRunning()) {
            this.primary.begin();
        }
    }

    public void clear() {
        this.primary.clear();
        if (this.error.isRunning()) {
            this.error.clear();
        }
    }

    public boolean isRunning() {
        return (this.primary.isFailed() ? this.error : this.primary).isRunning();
    }

    public boolean isComplete() {
        return (this.primary.isFailed() ? this.error : this.primary).isComplete();
    }

    public boolean isResourceSet() {
        return (this.primary.isFailed() ? this.error : this.primary).isResourceSet();
    }

    public boolean isCleared() {
        return (this.primary.isFailed() ? this.error : this.primary).isCleared();
    }

    public boolean isFailed() {
        return this.primary.isFailed() && this.error.isFailed();
    }

    public void recycle() {
        this.primary.recycle();
        this.error.recycle();
    }

    public boolean isEquivalentTo(Request o) {
        boolean z = false;
        if (!(o instanceof ErrorRequestCoordinator)) {
            return false;
        }
        ErrorRequestCoordinator other = (ErrorRequestCoordinator) o;
        if (this.primary.isEquivalentTo(other.primary) && this.error.isEquivalentTo(other.error)) {
            z = true;
        }
        return z;
    }

    public boolean canSetImage(Request request) {
        return parentCanSetImage() && isValidRequest(request);
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
        return parentCanNotifyStatusChanged() && isValidRequest(request);
    }

    public boolean canNotifyCleared(Request request) {
        return parentCanNotifyCleared() && isValidRequest(request);
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

    private boolean isValidRequest(Request request) {
        if (!request.equals(this.primary)) {
            if (!this.primary.isFailed() || !request.equals(this.error)) {
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

    private boolean parentIsAnyResourceSet() {
        RequestCoordinator requestCoordinator = this.parent;
        return requestCoordinator != null && requestCoordinator.isAnyResourceSet();
    }

    public void onRequestSuccess(Request request) {
        RequestCoordinator requestCoordinator = this.parent;
        if (requestCoordinator != null) {
            requestCoordinator.onRequestSuccess(this);
        }
    }

    public void onRequestFailed(Request request) {
        if (request.equals(this.error)) {
            RequestCoordinator requestCoordinator = this.parent;
            if (requestCoordinator != null) {
                requestCoordinator.onRequestFailed(this);
            }
            return;
        }
        if (!this.error.isRunning()) {
            this.error.begin();
        }
    }
}

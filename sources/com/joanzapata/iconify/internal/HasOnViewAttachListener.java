package com.joanzapata.iconify.internal;

import android.support.v4.view.ViewCompat;
import android.widget.TextView;

public interface HasOnViewAttachListener {

    public static class HasOnViewAttachListenerDelegate {
        private OnViewAttachListener listener;
        private final TextView view;

        public HasOnViewAttachListenerDelegate(TextView view) {
            this.view = view;
        }

        public void setOnViewAttachListener(OnViewAttachListener listener) {
            OnViewAttachListener onViewAttachListener = this.listener;
            if (onViewAttachListener != null) {
                onViewAttachListener.onDetach();
            }
            this.listener = listener;
            if (ViewCompat.isAttachedToWindow(this.view) && listener != null) {
                listener.onAttach();
            }
        }

        public void onAttachedToWindow() {
            OnViewAttachListener onViewAttachListener = this.listener;
            if (onViewAttachListener != null) {
                onViewAttachListener.onAttach();
            }
        }

        public void onDetachedFromWindow() {
            OnViewAttachListener onViewAttachListener = this.listener;
            if (onViewAttachListener != null) {
                onViewAttachListener.onDetach();
            }
        }
    }

    public interface OnViewAttachListener {
        void onAttach();

        void onDetach();
    }

    void setOnViewAttachListener(OnViewAttachListener onViewAttachListener);
}

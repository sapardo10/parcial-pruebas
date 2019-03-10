package com.google.android.exoplayer2.util;

import android.os.Handler;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventDispatcher<T> {
    private final CopyOnWriteArrayList<HandlerAndListener<T>> listeners = new CopyOnWriteArrayList();

    public interface Event<T> {
        void sendTo(T t);
    }

    private static final class HandlerAndListener<T> {
        private final Handler handler;
        private final T listener;
        private boolean released;

        public HandlerAndListener(Handler handler, T eventListener) {
            this.handler = handler;
            this.listener = eventListener;
        }

        public void release() {
            this.released = true;
        }

        public void dispatch(Event<T> event) {
            this.handler.post(new C0654x1ab2001a(this, event));
        }

        public static /* synthetic */ void lambda$dispatch$0(HandlerAndListener this_, Event event) {
            if (!this_.released) {
                event.sendTo(this_.listener);
            }
        }
    }

    public void addListener(Handler handler, T eventListener) {
        boolean z = (handler == null || eventListener == null) ? false : true;
        Assertions.checkArgument(z);
        removeListener(eventListener);
        this.listeners.add(new HandlerAndListener(handler, eventListener));
    }

    public void removeListener(T eventListener) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            HandlerAndListener<T> handlerAndListener = (HandlerAndListener) it.next();
            if (handlerAndListener.listener == eventListener) {
                handlerAndListener.release();
                this.listeners.remove(handlerAndListener);
            }
        }
    }

    public void dispatch(Event<T> event) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((HandlerAndListener) it.next()).dispatch(event);
        }
    }
}

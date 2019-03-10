package com.robotium.solo;

import android.app.Instrumentation;
import android.graphics.PointF;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;

class Swiper {
    public static final int EVENT_TIME_INTERVAL_MS = 10;
    public static final int GESTURE_DURATION_MS = 1000;
    private final Instrumentation _instrument;

    public Swiper(Instrumentation inst) {
        this._instrument = inst;
    }

    public void generateSwipeGesture(PointF startPoint1, PointF startPoint2, PointF endPoint1, PointF endPoint2) {
        int numMoves;
        PointF pointF = startPoint1;
        PointF pointF2 = startPoint2;
        PointF pointF3 = endPoint1;
        PointF pointF4 = endPoint2;
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        float startX1 = pointF.x;
        float startY1 = pointF.y;
        float startX2 = pointF2.x;
        float startY2 = pointF2.y;
        float endX1 = pointF3.x;
        float endY1 = pointF3.y;
        float endX2 = pointF4.x;
        float endY2 = pointF4.y;
        float x1 = startX1;
        float y1 = startY1;
        float x2 = startX2;
        float y2 = startY2;
        float startX12 = startX1;
        PointerCoords[] pointerCoords = new PointerCoords[2];
        PointerCoords pc1 = new PointerCoords();
        PointerCoords pc2 = new PointerCoords();
        float startY12 = startY1;
        PointerCoords pc12 = pc1;
        pc12.x = x1;
        pc12.y = y1;
        pc12.pressure = 1.0f;
        pc12.size = 1.0f;
        float startX22 = startX2;
        PointerCoords pc22 = pc2;
        pc22.x = x2;
        float endY12 = endY1;
        endY1 = y2;
        pc22.y = endY1;
        pc22.pressure = 1.0f;
        pc22.size = 1.0f;
        pointerCoords[0] = pc12;
        PointerCoords pc13 = pc12;
        pointerCoords[1] = pc22;
        PointerProperties[] pointerProperties = new PointerProperties[2];
        PointerProperties pp1 = new PointerProperties();
        PointerProperties pp2 = new PointerProperties();
        PointerCoords pc23 = pc22;
        PointerProperties pp12 = pp1;
        pp12.id = 0;
        float y22 = endY1;
        pp12.toolType = 1;
        float endX22 = endX2;
        PointerProperties pp22 = pp2;
        pp22.id = 1;
        pp22.toolType = 1;
        pointerProperties[0] = pp12;
        pointerProperties[1] = pp22;
        float endY22 = endY2;
        long j = eventTime;
        PointerProperties pp23 = pp22;
        y22 = endY12;
        float endX12 = endX1;
        float startY22 = startY2;
        float startX23 = startX22;
        PointerProperties[] pointerProperties2 = pointerProperties;
        y2 = startY12;
        PointerCoords[] pointerCoords2 = pointerCoords;
        float startX13 = startX12;
        MotionEvent event = MotionEvent.obtain(downTime, j, 0, 1, pointerProperties, pointerCoords, 0, 0, 1065353216, 1.0f, 0, 0, 0, 0);
        this._instrument.sendPointerSync(event);
        MotionEvent event2 = MotionEvent.obtain(downTime, j, (pp23.id << 8) + 5, 2, pointerProperties2, pointerCoords2, 0, 0, 1.0f, 1.0f, 0, 0, 0, 0);
        this._instrument.sendPointerSync(event2);
        int numMoves2 = 100;
        float stepX1 = (endX12 - startX13) / ((float) 100);
        float stepY1 = (y22 - y2) / ((float) 100);
        float stepX2 = (endX22 - startX23) / ((float) 100);
        float stepY2 = (endY22 - startY22) / ((float) 100);
        int i = 0;
        long j2 = eventTime;
        long eventTime2 = j2;
        while (i < numMoves2) {
            long eventTime3 = eventTime2 + 10;
            PointerCoords pointerCoords3 = pointerCoords2[0];
            pointerCoords3.x += stepX1;
            pointerCoords3 = pointerCoords2[0];
            pointerCoords3.y += stepY1;
            pointerCoords3 = pointerCoords2[1];
            pointerCoords3.x += stepX2;
            pointerCoords3 = pointerCoords2[1];
            pointerCoords3.y += stepY2;
            int i2 = i;
            numMoves = numMoves2;
            event2 = MotionEvent.obtain(downTime, eventTime3, 2, 2, pointerProperties2, pointerCoords2, 0, 0, 1.0f, 1.0f, 0, 0, 0, 0);
            r0._instrument.sendPointerSync(event2);
            i = i2 + 1;
            MotionEvent motionEvent = event2;
            eventTime2 = eventTime3;
            numMoves2 = numMoves;
        }
        numMoves = numMoves2;
    }
}

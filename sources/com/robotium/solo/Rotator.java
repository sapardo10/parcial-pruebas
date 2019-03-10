package com.robotium.solo;

import android.app.Instrumentation;
import android.graphics.PointF;
import android.os.SystemClock;
import android.support.v4.view.InputDeviceCompat;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;

class Rotator {
    private static final int EVENT_TIME_INTERVAL_MS = 10;
    public static final int LARGE = 0;
    public static final int SMALL = 1;
    private final Instrumentation _instrument;

    public Rotator(Instrumentation inst) {
        this._instrument = inst;
    }

    public void generateRotateGesture(int size, PointF center1, PointF center2) {
        PointF pointF = center1;
        PointF pointF2 = center2;
        double incrementFactor = 0.0d;
        float startX1 = pointF.x;
        float startY1 = pointF.y;
        float startX2 = pointF2.x;
        float startY2 = pointF2.y;
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        float x1 = startX1;
        float y1 = startY1;
        float x2 = startX2;
        float y2 = startY2;
        PointerCoords[] pointerCoords = new PointerCoords[2];
        PointerCoords pc1 = new PointerCoords();
        PointerCoords pc2 = new PointerCoords();
        pc1.x = x1;
        pc1.y = y1;
        pc1.pressure = 1.0f;
        pc1.size = 1.0f;
        float y12 = y1;
        PointerCoords pc22 = pc2;
        pc22.x = x2;
        pc22.y = y2;
        pc22.pressure = 1.0f;
        pc22.size = 1.0f;
        pointerCoords[0] = pc1;
        float x22 = x2;
        pointerCoords[1] = pc22;
        PointerProperties[] pointerProperties = new PointerProperties[2];
        PointerProperties pp1 = new PointerProperties();
        PointerProperties pp2 = new PointerProperties();
        PointerCoords pc23 = pc22;
        PointerProperties pp12 = pp1;
        pp12.id = 0;
        pp12.toolType = 1;
        PointerProperties pp22 = pp2;
        pp22.id = 1;
        pp22.toolType = 1;
        pointerProperties[0] = pp12;
        pointerProperties[1] = pp22;
        PointerCoords[] pointerCoords2 = pointerCoords;
        long j = eventTime;
        PointerProperties[] pointerProperties2 = pointerProperties;
        PointerCoords[] pointerCoordsArr = pointerCoords2;
        MotionEvent event = MotionEvent.obtain(downTime, j, 0, 1, pointerProperties2, pointerCoordsArr, 0, 0, 1.0f, 1.0f, 0, 0, InputDeviceCompat.SOURCE_TOUCHSCREEN, 0);
        this._instrument.sendPointerSync(event);
        MotionEvent event2 = MotionEvent.obtain(downTime, j, (pp22.id << 8) + 5, 2, pointerProperties2, pointerCoordsArr, 0, 0, 1.0f, 1.0f, 0, 0, InputDeviceCompat.SOURCE_TOUCHSCREEN, 0);
        this._instrument.sendPointerSync(event2);
        switch (size) {
            case 0:
                incrementFactor = 0.01d;
                break;
            case 1:
                incrementFactor = 0.1d;
                break;
            default:
                break;
        }
        MotionEvent motionEvent = event2;
        long eventTime2 = eventTime;
        double i = 0.0d;
        while (i < 3.141592653589793d) {
            long eventTime3 = eventTime2 + 10;
            eventTime2 = pointerCoords2[0];
            double d = (double) eventTime2.x;
            double cos = Math.cos(i);
            Double.isNaN(d);
            eventTime2.x = (float) (d + cos);
            eventTime2 = pointerCoords2[0];
            d = (double) eventTime2.y;
            cos = Math.sin(i);
            Double.isNaN(d);
            eventTime2.y = (float) (d + cos);
            eventTime2 = pointerCoords2[1];
            d = (double) eventTime2.x;
            cos = Math.cos(i + 3.141592653589793d);
            Double.isNaN(d);
            eventTime2.x = (float) (d + cos);
            eventTime2 = pointerCoords2[1];
            d = (double) eventTime2.y;
            double sin = Math.sin(i + 3.141592653589793d);
            Double.isNaN(d);
            eventTime2.y = (float) (d + sin);
            eventTime2 = MotionEvent.obtain(downTime, eventTime3, 2, 2, pointerProperties2, pointerCoords2, 0, 0, 1.0f, 1.0f, 0, 0, InputDeviceCompat.SOURCE_TOUCHSCREEN, 0);
            r0._instrument.sendPointerSync(eventTime2);
            i += incrementFactor;
            Object obj = eventTime2;
            eventTime2 = eventTime3;
        }
        eventTime = eventTime2 + 10;
        pointerCoordsArr = pointerCoords2;
        event = MotionEvent.obtain(downTime, eventTime, (pp22.id << 8) + 6, 2, pointerProperties2, pointerCoordsArr, 0, 0, 1.0f, 1.0f, 0, 0, InputDeviceCompat.SOURCE_TOUCHSCREEN, 0);
        r0._instrument.sendPointerSync(event);
        motionEvent = event;
        r0._instrument.sendPointerSync(MotionEvent.obtain(downTime, eventTime + 10, 1, 1, pointerProperties2, pointerCoordsArr, 0, 0, 1.0f, 1.0f, 0, 0, InputDeviceCompat.SOURCE_TOUCHSCREEN, 0));
    }
}

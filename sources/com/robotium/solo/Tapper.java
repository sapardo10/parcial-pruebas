package com.robotium.solo;

import android.app.Instrumentation;
import android.graphics.PointF;
import android.os.SystemClock;
import android.support.v4.view.InputDeviceCompat;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;

class Tapper {
    public static final int EVENT_TIME_INTERVAL_MS = 10;
    public static final int GESTURE_DURATION_MS = 1000;
    private final Instrumentation _instrument;

    public Tapper(Instrumentation inst) {
        this._instrument = inst;
    }

    public void generateTapGesture(int numTaps, PointF... points) {
        float x2;
        float y2;
        PointerProperties pp1;
        PointerProperties[] pointerProperties;
        PointerCoords pc2;
        int i;
        PointerCoords pc1;
        PointerCoords[] pointerCoords;
        float y22;
        float x22;
        float y1;
        float x1;
        Tapper tapper = this;
        PointF[] pointFArr = points;
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        float x12 = pointFArr[0].x;
        float y12 = pointFArr[0].y;
        if (pointFArr.length == 2) {
            x2 = pointFArr[1].x;
            y2 = pointFArr[1].y;
        } else {
            x2 = 0.0f;
            y2 = 0.0f;
        }
        PointerCoords[] pointerCoords2 = new PointerCoords[pointFArr.length];
        PointerCoords pc12 = new PointerCoords();
        pc12.x = x12;
        pc12.y = y12;
        pc12.pressure = 1.0f;
        pc12.size = 1.0f;
        pointerCoords2[0] = pc12;
        PointerCoords pc22 = new PointerCoords();
        if (pointFArr.length == 2) {
            pc22.x = x2;
            pc22.y = y2;
            pc22.pressure = 1.0f;
            pc22.size = 1.0f;
            pointerCoords2[1] = pc22;
        }
        PointerProperties[] pointerProperties2 = new PointerProperties[pointFArr.length];
        PointerProperties pp12 = new PointerProperties();
        pp12.id = 0;
        pp12.toolType = 1;
        pointerProperties2[0] = pp12;
        PointerProperties pp2 = new PointerProperties();
        if (pointFArr.length == 2) {
            pp2.id = 1;
            pp2.toolType = 1;
            pointerProperties2[1] = pp2;
        }
        int i2 = 0;
        long eventTime2 = eventTime;
        while (i2 != numTaps) {
            PointerProperties pp22 = pp2;
            pp1 = pp12;
            pointerProperties = pointerProperties2;
            pc2 = pc22;
            i = i2;
            pc1 = pc12;
            pointerCoords = pointerCoords2;
            y22 = y2;
            x22 = x2;
            y1 = y12;
            x1 = x12;
            MotionEvent event = MotionEvent.obtain(downTime, eventTime2, 0, pointFArr.length, pointerProperties, pointerCoords2, 0, 0, 1.0f, 1.0f, 0, null, InputDeviceCompat.SOURCE_TOUCHSCREEN, 0);
            tapper._instrument.sendPointerSync(event);
            MotionEvent motionEvent;
            if (pointFArr.length == 2) {
                PointerProperties pp23 = pp22;
                PointerProperties[] pointerPropertiesArr = pointerProperties;
                PointerCoords[] pointerCoordsArr = pointerCoords;
                PointerProperties pp24 = pp23;
                event = MotionEvent.obtain(downTime, eventTime2, (pp23.id << 8) + 5, pointFArr.length, pointerPropertiesArr, pointerCoordsArr, 0, 0, 1.0f, 1.0f, 0, 0, InputDeviceCompat.SOURCE_TOUCHSCREEN, 0);
                tapper._instrument.sendPointerSync(event);
                eventTime2 += 10;
                PointerProperties pp25 = pp24;
                pp22 = pp25;
                event = MotionEvent.obtain(downTime, eventTime2, (pp25.id << 8) + 6, pointFArr.length, pointerPropertiesArr, pointerCoordsArr, 0, 0, 1.0f, 1.0f, 0, 0, InputDeviceCompat.SOURCE_TOUCHSCREEN, 0);
                tapper._instrument.sendPointerSync(event);
                motionEvent = event;
            } else {
                motionEvent = event;
            }
            eventTime2 += 10;
            tapper._instrument.sendPointerSync(MotionEvent.obtain(downTime, eventTime2, 1, pointFArr.length, pointerProperties, pointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, InputDeviceCompat.SOURCE_TOUCHSCREEN, 0));
            i2 = i + 1;
            x2 = x22;
            y12 = y1;
            x12 = x1;
            pp12 = pp1;
            pp2 = pp22;
            pointerProperties2 = pointerProperties;
            pc22 = pc2;
            pc12 = pc1;
            pointerCoords2 = pointerCoords;
            y2 = y22;
        }
        pp1 = pp12;
        pointerProperties = pointerProperties2;
        pc2 = pc22;
        i = i2;
        pc1 = pc12;
        pointerCoords = pointerCoords2;
        y22 = y2;
        x22 = x2;
        y1 = y12;
        x1 = x12;
    }
}

package com.robotium.solo;

class PressurePoint {
    public final float pressure;
    /* renamed from: x */
    public final float f11x;
    /* renamed from: y */
    public final float f12y;

    public PressurePoint(float x, float y, float pressure) {
        this.f11x = x;
        this.f12y = y;
        this.pressure = pressure;
    }
}

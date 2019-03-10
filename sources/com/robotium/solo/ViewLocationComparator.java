package com.robotium.solo;

import android.view.View;
import java.util.Comparator;

class ViewLocationComparator implements Comparator<View> {
    /* renamed from: a */
    private final int[] f13a;
    private final int axis1;
    private final int axis2;
    /* renamed from: b */
    private final int[] f14b;

    public ViewLocationComparator() {
        this(true);
    }

    public ViewLocationComparator(boolean yAxisFirst) {
        this.f13a = new int[2];
        this.f14b = new int[2];
        this.axis1 = yAxisFirst;
        this.axis2 = yAxisFirst ^ 1;
    }

    public int compare(View lhs, View rhs) {
        lhs.getLocationOnScreen(this.f13a);
        rhs.getLocationOnScreen(this.f14b);
        int[] iArr = this.f13a;
        int i = this.axis1;
        int i2 = iArr[i];
        int[] iArr2 = this.f14b;
        int i3 = 1;
        if (i2 != iArr2[i]) {
            if (iArr[i] < iArr2[i]) {
                i3 = -1;
            }
            return i3;
        }
        i = this.axis2;
        if (iArr[i] < iArr2[i]) {
            return -1;
        }
        if (iArr[i] == iArr2[i]) {
            i3 = 0;
        }
        return i3;
    }
}

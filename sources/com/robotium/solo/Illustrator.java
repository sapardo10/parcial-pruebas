package com.robotium.solo;

import android.app.Instrumentation;

class Illustrator {
    private Instrumentation inst;

    public void illustrate(com.robotium.solo.Illustration r37) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x0133 in {9, 10, 14, 15, 16, 20, 21, 23, 25} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r36 = this;
        r1 = r36;
        if (r37 == 0) goto L_0x012a;
    L_0x0004:
        r0 = r37.getPoints();
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x012a;
    L_0x000e:
        r18 = android.os.SystemClock.uptimeMillis();
        r2 = android.os.SystemClock.uptimeMillis();
        r15 = 1;
        r14 = new android.view.MotionEvent.PointerCoords[r15];
        r0 = new android.view.MotionEvent$PointerCoords;
        r0.<init>();
        r13 = r0;
        r12 = new android.view.MotionEvent.PointerProperties[r15];
        r0 = new android.view.MotionEvent$PointerProperties;
        r0.<init>();
        r11 = r0;
        r10 = 0;
        r11.id = r10;
        r0 = r37.getToolType();
        r11.toolType = r0;
        r12[r10] = r11;
        r14[r10] = r13;
        r9 = r37.getPoints();
        r0 = 0;
        r8 = r0;
    L_0x003a:
        r0 = r9.size();
        r4 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        if (r8 >= r0) goto L_0x00be;
    L_0x0042:
        r0 = r9.get(r8);
        r7 = r0;
        r7 = (com.robotium.solo.PressurePoint) r7;
        r0 = r7.f11x;
        r13.x = r0;
        r0 = r7.f12y;
        r13.y = r0;
        r0 = r7.pressure;
        r13.pressure = r0;
        r13.size = r4;
        if (r8 != 0) goto L_0x005d;
    L_0x0059:
        r0 = 0;
        r20 = r0;
        goto L_0x0060;
    L_0x005d:
        r0 = 2;
        r20 = r0;
    L_0x0060:
        r21 = android.os.SystemClock.uptimeMillis();
        r0 = 1;
        r16 = 0;
        r17 = 0;
        r23 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r24 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r25 = 0;
        r26 = 0;
        r27 = 4098; // 0x1002 float:5.743E-42 double:2.0247E-320;
        r28 = 0;
        r2 = r18;
        r4 = r21;
        r6 = r20;
        r29 = r7;
        r7 = r0;
        r30 = r8;
        r8 = r12;
        r31 = r9;
        r9 = r14;
        r32 = 0;
        r10 = r16;
        r33 = r11;
        r11 = r17;
        r34 = r12;
        r12 = r23;
        r35 = r13;
        r13 = r24;
        r23 = r14;
        r14 = r25;
        r24 = 1;
        r15 = r26;
        r16 = r27;
        r17 = r28;
        r2 = android.view.MotionEvent.obtain(r2, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17);
        r0 = r1.inst;	 Catch:{ SecurityException -> 0x00aa }
        r0.sendPointerSync(r2);	 Catch:{ SecurityException -> 0x00aa }
        goto L_0x00ab;
    L_0x00aa:
        r0 = move-exception;
        r8 = r30 + 1;
        r2 = r21;
        r14 = r23;
        r9 = r31;
        r11 = r33;
        r12 = r34;
        r13 = r35;
        r10 = 0;
        r15 = 1;
        goto L_0x003a;
    L_0x00be:
        r30 = r8;
        r31 = r9;
        r33 = r11;
        r34 = r12;
        r35 = r13;
        r23 = r14;
        r24 = 1;
        r32 = 0;
        r20 = 1;
        r15 = r35;
        r23[r32] = r15;
        r0 = r31.size();
        r0 = r0 + -1;
        r14 = r31;
        r0 = r14.get(r0);
        r13 = r0;
        r13 = (com.robotium.solo.PressurePoint) r13;
        r0 = r13.f11x;
        r15.x = r0;
        r0 = r13.f12y;
        r15.y = r0;
        r0 = r13.pressure;
        r15.pressure = r0;
        r15.size = r4;
        r21 = android.os.SystemClock.uptimeMillis();
        r7 = 1;
        r10 = 0;
        r11 = 0;
        r12 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r16 = 0;
        r17 = 0;
        r24 = 4098; // 0x1002 float:5.743E-42 double:2.0247E-320;
        r25 = 0;
        r2 = r18;
        r4 = r21;
        r6 = r20;
        r8 = r34;
        r9 = r23;
        r26 = r13;
        r13 = r0;
        r27 = r14;
        r14 = r16;
        r28 = r15;
        r15 = r17;
        r16 = r24;
        r17 = r25;
        r2 = android.view.MotionEvent.obtain(r2, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17);
        r0 = r1.inst;	 Catch:{ SecurityException -> 0x0127 }
        r0.sendPointerSync(r2);	 Catch:{ SecurityException -> 0x0127 }
        goto L_0x0128;
    L_0x0127:
        r0 = move-exception;
        return;
        r0 = new java.lang.IllegalArgumentException;
        r2 = "Illustration must not be null and requires at least one point.";
        r0.<init>(r2);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.robotium.solo.Illustrator.illustrate(com.robotium.solo.Illustration):void");
    }

    public Illustrator(Instrumentation inst) {
        this.inst = inst;
    }
}

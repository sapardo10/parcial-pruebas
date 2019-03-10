package com.robotium.solo;

import android.app.Instrumentation;
import junit.framework.Assert;

class Sender {
    private final Instrumentation inst;
    private final Sleeper sleeper;

    Sender(Instrumentation inst, Sleeper sleeper) {
        this.inst = inst;
        this.sleeper = sleeper;
    }

    public void sendKeyCode(int keycode) {
        this.sleeper.sleep();
        try {
            this.inst.sendCharacterSync(keycode);
        } catch (SecurityException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Can not complete action! (");
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(e.getClass().getName());
            stringBuilder2.append(": ");
            stringBuilder2.append(e.getMessage());
            stringBuilder.append(stringBuilder2.toString());
            stringBuilder.append(")");
            Assert.fail(stringBuilder.toString());
        }
    }

    public void goBack() {
        this.sleeper.sleep();
        try {
            this.inst.sendKeyDownUpSync(4);
            this.sleeper.sleep();
        } catch (Throwable th) {
        }
    }
}

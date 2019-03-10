package com.robotium.solo;

import android.app.Instrumentation;
import android.widget.EditText;
import android.widget.Spinner;
import junit.framework.Assert;

class Presser {
    private final Clicker clicker;
    private final DialogUtils dialogUtils;
    private final Instrumentation inst;
    private final Sleeper sleeper;
    private final ViewFetcher viewFetcher;
    private final Waiter waiter;

    public Presser(ViewFetcher viewFetcher, Clicker clicker, Instrumentation inst, Sleeper sleeper, Waiter waiter, DialogUtils dialogUtils) {
        this.viewFetcher = viewFetcher;
        this.clicker = clicker;
        this.inst = inst;
        this.sleeper = sleeper;
        this.waiter = waiter;
        this.dialogUtils = dialogUtils;
    }

    public void pressMenuItem(int index) {
        pressMenuItem(index, 3);
    }

    public void pressMenuItem(int index, int itemsPerRow) {
        int i;
        int[] row = new int[4];
        for (i = 1; i <= 3; i++) {
            row[i] = itemsPerRow * i;
        }
        this.sleeper.sleep();
        try {
            this.inst.sendKeyDownUpSync(82);
            this.dialogUtils.waitForDialogToOpen((long) Timeout.getSmallTimeout(), true);
            this.inst.sendKeyDownUpSync(19);
            this.inst.sendKeyDownUpSync(19);
        } catch (SecurityException e) {
            Assert.fail("Can not press the menu!");
        }
        if (index < row[1]) {
            for (i = 0; i < index; i++) {
                this.sleeper.sleepMini();
                this.inst.sendKeyDownUpSync(22);
            }
        } else if (index >= row[1] && index < row[2]) {
            this.inst.sendKeyDownUpSync(20);
            for (i = row[1]; i < index; i++) {
                this.sleeper.sleepMini();
                this.inst.sendKeyDownUpSync(22);
            }
        } else if (index >= row[2]) {
            this.inst.sendKeyDownUpSync(20);
            this.inst.sendKeyDownUpSync(20);
            for (i = row[2]; i < index; i++) {
                this.sleeper.sleepMini();
                this.inst.sendKeyDownUpSync(22);
            }
        }
        try {
            this.inst.sendKeyDownUpSync(66);
        } catch (SecurityException e2) {
        }
    }

    public void pressSoftKeyboard(final int imeAction) {
        ViewFetcher viewFetcher = this.viewFetcher;
        final EditText freshestEditText = (EditText) viewFetcher.getFreshestView(viewFetcher.getCurrentViews(EditText.class, true));
        if (freshestEditText != null) {
            this.inst.runOnMainSync(new Runnable() {
                public void run() {
                    freshestEditText.onEditorAction(imeAction);
                }
            });
        }
    }

    public void pressSpinnerItem(int spinnerIndex, int itemIndex) {
        this.clicker.clickOnScreen(this.waiter.waitForAndGetView(spinnerIndex, Spinner.class));
        this.dialogUtils.waitForDialogToOpen((long) Timeout.getSmallTimeout(), true);
        try {
            this.inst.sendKeyDownUpSync(20);
        } catch (SecurityException e) {
        }
        boolean countingUp = true;
        if (itemIndex < 0) {
            countingUp = false;
            itemIndex *= -1;
        }
        for (int i = 0; i < itemIndex; i++) {
            this.sleeper.sleepMini();
            if (countingUp) {
                try {
                    this.inst.sendKeyDownUpSync(20);
                } catch (SecurityException e2) {
                }
            } else {
                try {
                    this.inst.sendKeyDownUpSync(19);
                } catch (SecurityException e3) {
                }
            }
        }
        try {
            this.inst.sendKeyDownUpSync(66);
        } catch (SecurityException e4) {
        }
    }
}

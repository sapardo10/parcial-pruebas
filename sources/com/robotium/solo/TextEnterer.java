package com.robotium.solo;

import android.app.Instrumentation;
import android.view.View;
import android.widget.EditText;
import junit.framework.Assert;

class TextEnterer {
    private final Clicker clicker;
    private final DialogUtils dialogUtils;
    private final Instrumentation inst;

    public TextEnterer(Instrumentation inst, Clicker clicker, DialogUtils dialogUtils) {
        this.inst = inst;
        this.clicker = clicker;
        this.dialogUtils = dialogUtils;
    }

    public void setEditText(final EditText editText, final String text) {
        if (editText != null) {
            final String previousText = editText.getText().toString();
            this.inst.runOnMainSync(new Runnable() {
                public void run() {
                    editText.setInputType(0);
                    editText.performClick();
                    TextEnterer.this.dialogUtils.hideSoftKeyboard(editText, false, false);
                    if (text.equals("")) {
                        editText.setText(text);
                        return;
                    }
                    EditText editText = editText;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(previousText);
                    stringBuilder.append(text);
                    editText.setText(stringBuilder.toString());
                    editText.setCursorVisible(false);
                }
            });
        }
    }

    public void typeText(final EditText editText, String text) {
        if (editText != null) {
            this.inst.runOnMainSync(new Runnable() {
                public void run() {
                    editText.setInputType(0);
                }
            });
            this.clicker.clickOnScreen((View) editText, false, 0);
            this.dialogUtils.hideSoftKeyboard(editText, true, true);
            boolean successfull = false;
            int retry = 0;
            while (!successfull && retry < 10) {
                try {
                    this.inst.sendStringSync(text);
                    successfull = true;
                } catch (SecurityException e) {
                    this.dialogUtils.hideSoftKeyboard(editText, true, true);
                    retry++;
                }
            }
            if (!successfull) {
                Assert.fail("Text can not be typed!");
            }
        }
    }
}

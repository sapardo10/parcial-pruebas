package com.robotium.solo;

import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

class Checker {
    private final ViewFetcher viewFetcher;
    private final Waiter waiter;

    public Checker(ViewFetcher viewFetcher, Waiter waiter) {
        this.viewFetcher = viewFetcher;
        this.waiter = waiter;
    }

    public <T extends CompoundButton> boolean isButtonChecked(Class<T> expectedClass, int index) {
        return ((CompoundButton) this.waiter.waitForAndGetView(index, expectedClass)).isChecked();
    }

    public <T extends CompoundButton> boolean isButtonChecked(Class<T> expectedClass, String text) {
        CompoundButton button = (CompoundButton) this.waiter.waitForText(expectedClass, text, 0, (long) Timeout.getSmallTimeout(), true);
        if (button == null || !button.isChecked()) {
            return false;
        }
        return true;
    }

    public boolean isCheckedTextChecked(String text) {
        CheckedTextView checkedTextView = (CheckedTextView) this.waiter.waitForText(CheckedTextView.class, text, 0, (long) Timeout.getSmallTimeout(), true);
        if (checkedTextView == null || !checkedTextView.isChecked()) {
            return false;
        }
        return true;
    }

    public boolean isSpinnerTextSelected(String text) {
        this.waiter.waitForAndGetView(0, Spinner.class);
        ArrayList<Spinner> spinnerList = this.viewFetcher.getCurrentViews(Spinner.class, true);
        for (int i = 0; i < spinnerList.size(); i++) {
            if (isSpinnerTextSelected(i, text)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSpinnerTextSelected(int spinnerIndex, String text) {
        if (((TextView) ((Spinner) this.waiter.waitForAndGetView(spinnerIndex, Spinner.class)).getChildAt(0)).getText().equals(text)) {
            return true;
        }
        return false;
    }
}

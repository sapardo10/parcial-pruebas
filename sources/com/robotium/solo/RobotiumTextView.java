package com.robotium.solo;

import android.content.Context;
import android.widget.TextView;

class RobotiumTextView extends TextView {
    private int locationX = 0;
    private int locationY = 0;

    public RobotiumTextView(Context context) {
        super(context);
    }

    public RobotiumTextView(Context context, String text, int locationX, int locationY) {
        super(context);
        setText(text);
        setLocationX(locationX);
        setLocationY(locationY);
    }

    public void getLocationOnScreen(int[] location) {
        location[0] = this.locationX;
        location[1] = this.locationY;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }
}

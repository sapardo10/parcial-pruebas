package android.support.v7.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v7.preference.C0315R;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

@RestrictTo({Scope.LIBRARY_GROUP})
public class PreferenceImageView extends ImageView {
    private int mMaxHeight;
    private int mMaxWidth;

    public PreferenceImageView(Context context) {
        this(context, null);
    }

    public PreferenceImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mMaxWidth = Integer.MAX_VALUE;
        this.mMaxHeight = Integer.MAX_VALUE;
        TypedArray a = context.obtainStyledAttributes(attrs, C0315R.styleable.PreferenceImageView, defStyleAttr, 0);
        setMaxWidth(a.getDimensionPixelSize(C0315R.styleable.PreferenceImageView_maxWidth, Integer.MAX_VALUE));
        setMaxHeight(a.getDimensionPixelSize(C0315R.styleable.PreferenceImageView_maxHeight, Integer.MAX_VALUE));
        a.recycle();
    }

    public void setMaxWidth(int maxWidth) {
        this.mMaxWidth = maxWidth;
        super.setMaxWidth(maxWidth);
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    public void setMaxHeight(int maxHeight) {
        this.mMaxHeight = maxHeight;
        super.setMaxHeight(maxHeight);
    }

    public int getMaxHeight() {
        return this.mMaxHeight;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode;
        int heightSize;
        int maxHeight;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != Integer.MIN_VALUE) {
            if (widthMode != 0) {
                heightMode = MeasureSpec.getMode(heightMeasureSpec);
                if (heightMode != Integer.MIN_VALUE) {
                    if (heightMode == 0) {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    }
                }
                heightSize = MeasureSpec.getSize(heightMeasureSpec);
                maxHeight = getMaxHeight();
                if (maxHeight == Integer.MAX_VALUE && (maxHeight < heightSize || heightMode == 0)) {
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, Integer.MIN_VALUE);
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
        heightMode = MeasureSpec.getSize(widthMeasureSpec);
        heightSize = getMaxWidth();
        if (heightSize != Integer.MAX_VALUE && (heightSize < heightMode || widthMode == 0)) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, Integer.MIN_VALUE);
        }
        heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != Integer.MIN_VALUE) {
            if (heightMode == 0) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
        heightSize = MeasureSpec.getSize(heightMeasureSpec);
        maxHeight = getMaxHeight();
        if (maxHeight == Integer.MAX_VALUE) {
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

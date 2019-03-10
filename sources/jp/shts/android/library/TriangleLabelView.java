package jp.shts.android.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public class TriangleLabelView extends View {
    private static final int DEGREES_LEFT = -45;
    private static final int DEGREES_RIGHT = 45;
    private static final String TAG = TriangleLabelView.class.getSimpleName();
    private int backGroundColor;
    private float bottomPadding;
    private float centerPadding;
    private Corner corner;
    private int height;
    private PaintHolder primary;
    private PaintHolder secondary;
    private float topPadding;
    private Paint trianglePaint;
    private int width;

    public enum Corner {
        TOP_LEFT(1),
        TOP_RIGHT(2),
        BOTTOM_LEFT(3),
        BOTTOM_RIGHT(4);
        
        private final int type;

        private Corner(int type) {
            this.type = type;
        }

        private boolean top() {
            if (this != TOP_LEFT) {
                if (this != TOP_RIGHT) {
                    return false;
                }
            }
            return true;
        }

        private boolean left() {
            if (this != TOP_LEFT) {
                if (this != BOTTOM_LEFT) {
                    return false;
                }
            }
            return true;
        }

        private static Corner from(int type) {
            for (Corner c : values()) {
                if (c.type == type) {
                    return c;
                }
            }
            return TOP_LEFT;
        }
    }

    private static class PaintHolder {
        int color;
        float height;
        Paint paint;
        float size;
        int style;
        String text;
        float width;

        private PaintHolder() {
            this.text = "";
        }

        void initPaint() {
            this.paint = new Paint(1);
            this.paint.setColor(this.color);
            this.paint.setTextAlign(Align.CENTER);
            this.paint.setTextSize(this.size);
            int i = this.style;
            if (i == 1) {
                this.paint.setTypeface(Typeface.SANS_SERIF);
            } else if (i == 2) {
                this.paint.setTypeface(Typeface.DEFAULT_BOLD);
            }
        }

        void resetStatus() {
            Rect rectText = new Rect();
            Paint paint = this.paint;
            String str = this.text;
            paint.getTextBounds(str, 0, str.length(), rectText);
            this.width = (float) rectText.width();
            this.height = (float) rectText.height();
        }
    }

    public TriangleLabelView(Context context) {
        this(context, null);
    }

    public TriangleLabelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TriangleLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.primary = new PaintHolder();
        this.secondary = new PaintHolder();
        init(context, attrs);
    }

    @TargetApi(21)
    public TriangleLabelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.primary = new PaintHolder();
        this.secondary = new PaintHolder();
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, C0804R.styleable.TriangleLabelView);
        this.topPadding = ta.getDimension(C0804R.styleable.TriangleLabelView_labelTopPadding, (float) dp2px(7.0f));
        this.centerPadding = ta.getDimension(C0804R.styleable.TriangleLabelView_labelCenterPadding, (float) dp2px(3.0f));
        this.bottomPadding = ta.getDimension(C0804R.styleable.TriangleLabelView_labelBottomPadding, (float) dp2px(3.0f));
        this.backGroundColor = ta.getColor(C0804R.styleable.TriangleLabelView_backgroundColor, Color.parseColor("#66000000"));
        this.primary.color = ta.getColor(C0804R.styleable.TriangleLabelView_primaryTextColor, -1);
        this.secondary.color = ta.getColor(C0804R.styleable.TriangleLabelView_secondaryTextColor, -1);
        this.primary.size = ta.getDimension(C0804R.styleable.TriangleLabelView_primaryTextSize, sp2px(11.0f));
        this.secondary.size = ta.getDimension(C0804R.styleable.TriangleLabelView_secondaryTextSize, sp2px(8.0f));
        String primary = ta.getString(C0804R.styleable.TriangleLabelView_primaryText);
        if (primary != null) {
            this.primary.text = primary;
        }
        String secondary = ta.getString(C0804R.styleable.TriangleLabelView_secondaryText);
        if (secondary != null) {
            this.secondary.text = secondary;
        }
        this.primary.style = ta.getInt(C0804R.styleable.TriangleLabelView_primaryTextStyle, 2);
        this.secondary.style = ta.getInt(C0804R.styleable.TriangleLabelView_secondaryTextStyle, 0);
        this.corner = Corner.from(ta.getInt(C0804R.styleable.TriangleLabelView_corner, 1));
        ta.recycle();
        this.primary.initPaint();
        this.secondary.initPaint();
        this.trianglePaint = new Paint(1);
        this.trianglePaint.setColor(this.backGroundColor);
        this.primary.resetStatus();
        this.secondary.resetStatus();
    }

    public void setLabelTopPadding(float dp) {
        this.topPadding = (float) dp2px(dp);
    }

    public float getLabelTopPadding() {
        return this.topPadding;
    }

    public void setLabelCenterPadding(float dp) {
        this.centerPadding = (float) dp2px(dp);
        relayout();
    }

    public float getLabelCenterPadding() {
        return this.centerPadding;
    }

    public void setLabelBottomPadding(float dp) {
        this.bottomPadding = (float) dp2px(dp);
        relayout();
    }

    public float getLabelBottomPadding() {
        return this.bottomPadding;
    }

    public void setPrimaryText(String text) {
        PaintHolder paintHolder = this.primary;
        paintHolder.text = text;
        paintHolder.resetStatus();
        relayout();
    }

    public void setPrimaryText(@StringRes int textRes) {
        this.primary.text = getContext().getString(textRes);
        this.primary.resetStatus();
        relayout();
    }

    public String getPrimaryText() {
        return this.primary.text;
    }

    public void setSecondaryText(String smallText) {
        PaintHolder paintHolder = this.secondary;
        paintHolder.text = smallText;
        paintHolder.resetStatus();
        relayout();
    }

    public void setSecondaryText(@StringRes int textRes) {
        this.secondary.text = getContext().getString(textRes);
        this.secondary.resetStatus();
        relayout();
    }

    public String getSecondaryText() {
        return this.secondary.text;
    }

    public void setPrimaryTextColor(@ColorInt int color) {
        PaintHolder paintHolder = this.primary;
        paintHolder.color = color;
        paintHolder.initPaint();
        this.primary.resetStatus();
        relayout();
    }

    public void setPrimaryTextColorResource(@ColorRes int colorResource) {
        this.primary.color = ContextCompat.getColor(getContext(), colorResource);
        this.primary.initPaint();
        this.primary.resetStatus();
        relayout();
    }

    public void setSecondaryTextColor(@ColorInt int color) {
        PaintHolder paintHolder = this.secondary;
        paintHolder.color = color;
        paintHolder.initPaint();
        this.secondary.resetStatus();
        relayout();
    }

    public void setSecondaryTextColorResource(@ColorRes int colorResource) {
        this.secondary.color = ContextCompat.getColor(getContext(), colorResource);
        this.secondary.initPaint();
        this.secondary.resetStatus();
        relayout();
    }

    public void setPrimaryTextSize(float sp) {
        this.primary.size = sp2px(sp);
        relayout();
    }

    public void setSecondaryTextSize(float sp) {
        this.secondary.size = sp2px(sp);
        relayout();
    }

    public float getPrimaryTextSize() {
        return this.primary.size;
    }

    public float getSecondaryTextSize() {
        return this.secondary.size;
    }

    public void setTriangleBackgroundColor(@ColorInt int color) {
        this.backGroundColor = color;
        this.trianglePaint.setColor(this.backGroundColor);
        relayout();
    }

    public void setTriangleBackgroundColorResource(@ColorRes int colorResource) {
        this.backGroundColor = ContextCompat.getColor(getContext(), colorResource);
        this.trianglePaint.setColor(this.backGroundColor);
        relayout();
    }

    public int getTriangleBackGroundColor() {
        return this.backGroundColor;
    }

    public void setCorner(Corner corner) {
        this.corner = corner;
        relayout();
    }

    public Corner getCorner() {
        return this.corner;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        if (this.corner.top()) {
            double d = (double) this.height;
            double sqrt = Math.sqrt(2.0d);
            Double.isNaN(d);
            d *= sqrt;
            sqrt = (double) this.height;
            Double.isNaN(sqrt);
            canvas.translate(0.0f, (float) (d - sqrt));
        }
        if (this.corner.top()) {
            if (this.corner.left()) {
                canvas.rotate(-45.0f, 0.0f, (float) this.height);
            } else {
                canvas.rotate(45.0f, (float) this.width, (float) this.height);
            }
        } else if (this.corner.left()) {
            canvas.rotate(45.0f, 0.0f, 0.0f);
        } else {
            canvas.rotate(-45.0f, (float) this.width, 0.0f);
        }
        Path path = new Path();
        if (this.corner.top()) {
            path.moveTo(0.0f, (float) this.height);
            path.lineTo((float) (this.width / 2), 0.0f);
            path.lineTo((float) this.width, (float) this.height);
        } else {
            path.moveTo(0.0f, 0.0f);
            path.lineTo((float) (this.width / 2), (float) this.height);
            path.lineTo((float) this.width, 0.0f);
        }
        path.close();
        canvas.drawPath(path, this.trianglePaint);
        if (this.corner.top()) {
            canvas.drawText(this.secondary.text, (float) (this.width / 2), this.topPadding + this.secondary.height, this.secondary.paint);
            canvas.drawText(this.primary.text, (float) (this.width / 2), ((this.topPadding + this.secondary.height) + this.centerPadding) + this.primary.height, this.primary.paint);
        } else {
            canvas.drawText(this.secondary.text, (float) (this.width / 2), ((this.bottomPadding + this.secondary.height) + this.centerPadding) + this.primary.height, this.secondary.paint);
            canvas.drawText(this.primary.text, (float) (this.width / 2), this.bottomPadding + this.primary.height, this.primary.paint);
        }
        canvas.restore();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.height = (int) ((((this.topPadding + this.centerPadding) + this.bottomPadding) + this.secondary.height) + this.primary.height);
        int i = this.height;
        this.width = i * 2;
        double d = (double) i;
        double sqrt = Math.sqrt(2.0d);
        Double.isNaN(d);
        setMeasuredDimension(this.width, (int) (d * sqrt));
    }

    public int dp2px(float dpValue) {
        return (int) ((dpValue * getContext().getResources().getDisplayMetrics().density) + 0.5f);
    }

    public float sp2px(float spValue) {
        return spValue * getContext().getResources().getDisplayMetrics().scaledDensity;
    }

    private void relayout() {
        invalidate();
        requestLayout();
    }
}

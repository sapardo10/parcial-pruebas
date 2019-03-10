package android.support.transition;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;

public class ArcMotion extends PathMotion {
    private static final float DEFAULT_MAX_ANGLE_DEGREES = 70.0f;
    private static final float DEFAULT_MAX_TANGENT = ((float) Math.tan(Math.toRadians(35.0d)));
    private static final float DEFAULT_MIN_ANGLE_DEGREES = 0.0f;
    private float mMaximumAngle = DEFAULT_MAX_ANGLE_DEGREES;
    private float mMaximumTangent = DEFAULT_MAX_TANGENT;
    private float mMinimumHorizontalAngle = 0.0f;
    private float mMinimumHorizontalTangent = 0.0f;
    private float mMinimumVerticalAngle = 0.0f;
    private float mMinimumVerticalTangent = 0.0f;

    public ArcMotion(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, Styleable.ARC_MOTION);
        XmlPullParser parser = (XmlPullParser) attrs;
        setMinimumVerticalAngle(TypedArrayUtils.getNamedFloat(a, parser, "minimumVerticalAngle", 1, 0.0f));
        setMinimumHorizontalAngle(TypedArrayUtils.getNamedFloat(a, parser, "minimumHorizontalAngle", 0, 0.0f));
        setMaximumAngle(TypedArrayUtils.getNamedFloat(a, parser, "maximumAngle", 2, DEFAULT_MAX_ANGLE_DEGREES));
        a.recycle();
    }

    public void setMinimumHorizontalAngle(float angleInDegrees) {
        this.mMinimumHorizontalAngle = angleInDegrees;
        this.mMinimumHorizontalTangent = toTangent(angleInDegrees);
    }

    public float getMinimumHorizontalAngle() {
        return this.mMinimumHorizontalAngle;
    }

    public void setMinimumVerticalAngle(float angleInDegrees) {
        this.mMinimumVerticalAngle = angleInDegrees;
        this.mMinimumVerticalTangent = toTangent(angleInDegrees);
    }

    public float getMinimumVerticalAngle() {
        return this.mMinimumVerticalAngle;
    }

    public void setMaximumAngle(float angleInDegrees) {
        this.mMaximumAngle = angleInDegrees;
        this.mMaximumTangent = toTangent(angleInDegrees);
    }

    public float getMaximumAngle() {
        return this.mMaximumAngle;
    }

    private static float toTangent(float arcInDegrees) {
        if (arcInDegrees >= 0.0f && arcInDegrees <= 90.0f) {
            return (float) Math.tan(Math.toRadians((double) (arcInDegrees / 2.0f)));
        }
        throw new IllegalArgumentException("Arc must be between 0 and 90 degrees");
    }

    public Path getPath(float startX, float startY, float endX, float endY) {
        float eDistY;
        float f;
        float f2;
        float f3;
        float minimumArcDist2;
        float newArcDistance2;
        float ey;
        float ex;
        ArcMotion arcMotion = this;
        float f4 = startX;
        float f5 = startY;
        Path path = new Path();
        path.moveTo(f4, f5);
        float deltaX = endX - f4;
        float deltaY = endY - f5;
        float h2 = (deltaX * deltaX) + (deltaY * deltaY);
        float dx = (f4 + endX) / 2.0f;
        float dy = (f5 + endY) / 2.0f;
        float midDist2 = h2 * 0.25f;
        boolean isMovingUpwards = f5 > endY;
        if (Math.abs(deltaX) < Math.abs(deltaY)) {
            eDistY = Math.abs(h2 / (deltaY * 2.0f));
            if (isMovingUpwards) {
                f = endY + eDistY;
                f2 = endX;
            } else {
                f = f5 + eDistY;
                f2 = startX;
            }
            f3 = arcMotion.mMinimumVerticalTangent;
            minimumArcDist2 = (midDist2 * f3) * f3;
        } else {
            eDistY = h2 / (deltaX * 2.0f);
            if (isMovingUpwards) {
                f2 = f4 + eDistY;
                f = startY;
            } else {
                f2 = endX - eDistY;
                f = endY;
            }
            f3 = arcMotion.mMinimumHorizontalTangent;
            minimumArcDist2 = (midDist2 * f3) * f3;
        }
        float arcDistX = dx - f2;
        float arcDistY = dy - f;
        float arcDist2 = (arcDistX * arcDistX) + (arcDistY * arcDistY);
        eDistY = arcMotion.mMaximumTangent;
        float maximumArcDist2 = (midDist2 * eDistY) * eDistY;
        if (arcDist2 < minimumArcDist2) {
            newArcDistance2 = minimumArcDist2;
        } else if (arcDist2 > maximumArcDist2) {
            newArcDistance2 = maximumArcDist2;
        } else {
            newArcDistance2 = 0.0f;
        }
        if (newArcDistance2 != 0.0f) {
            f3 = (float) Math.sqrt((double) (newArcDistance2 / arcDist2));
            ey = dy + ((f - dy) * f3);
            ex = dx + ((f2 - dx) * f3);
        } else {
            ey = f;
            ex = f2;
        }
        path.cubicTo((f4 + ex) / 2.0f, (f5 + ey) / 2.0f, (ex + endX) / 2.0f, (ey + endY) / 2.0f, endX, endY);
        return path;
    }
}

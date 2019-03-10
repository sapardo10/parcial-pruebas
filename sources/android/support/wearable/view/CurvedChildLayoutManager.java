package android.support.wearable.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.VisibleForTesting;
import android.support.wearable.C0395R;
import android.support.wearable.view.WearableRecyclerView.ChildLayoutManager;
import android.view.View;

@TargetApi(23)
@Deprecated
public class CurvedChildLayoutManager extends ChildLayoutManager {
    private static final float EPSILON = 0.001f;
    private final float[] mAnchorOffsetXY = new float[2];
    private float mCurveBottom;
    private final Path mCurvePath = new Path();
    private int mCurvePathHeight;
    private float mCurveTop;
    private boolean mIsScreenRound;
    private int mLayoutHeight;
    private int mLayoutWidth;
    private float mLineGradient;
    private WearableRecyclerView mParentView;
    private float mPathLength;
    private final PathMeasure mPathMeasure = new PathMeasure();
    private final float[] mPathPoints = new float[2];
    private final float[] mPathTangent = new float[2];
    private int mXCurveOffset;

    public CurvedChildLayoutManager(Context context) {
        super(context);
        this.mIsScreenRound = context.getResources().getConfiguration().isScreenRound();
        this.mXCurveOffset = context.getResources().getDimensionPixelSize(C0395R.dimen.wrv_curve_default_x_offset);
    }

    @VisibleForTesting
    void setRound(boolean isScreenRound) {
        this.mIsScreenRound = isScreenRound;
    }

    @VisibleForTesting
    void setOffset(int offset) {
        this.mXCurveOffset = offset;
    }

    public void updateChild(View child, WearableRecyclerView parent) {
        if (this.mParentView != parent) {
            this.mParentView = parent;
            this.mLayoutWidth = this.mParentView.getWidth();
            this.mLayoutHeight = this.mParentView.getHeight();
        }
        if (this.mIsScreenRound) {
            maybeSetUpCircularInitialLayout(this.mLayoutWidth, this.mLayoutHeight);
            float[] fArr = this.mAnchorOffsetXY;
            fArr[0] = (float) this.mXCurveOffset;
            fArr[1] = ((float) child.getHeight()) / 2.0f;
            adjustAnchorOffsetXY(child, this.mAnchorOffsetXY);
            float minCenter = (-((float) child.getHeight())) / 2.0f;
            float maxCenter = ((float) this.mLayoutHeight) + (((float) child.getHeight()) / 2.0f);
            float verticalAnchor = ((float) child.getTop()) + this.mAnchorOffsetXY[1];
            this.mPathMeasure.getPosTan(this.mPathLength * ((Math.abs(minCenter) + verticalAnchor) / (maxCenter - minCenter)), this.mPathPoints, this.mPathTangent);
            boolean topClusterRisk = Math.abs(this.mPathPoints[1] - this.mCurveBottom) < EPSILON && minCenter < this.mPathPoints[1];
            boolean bottomClusterRisk = Math.abs(this.mPathPoints[1] - this.mCurveTop) < EPSILON && maxCenter > this.mPathPoints[1];
            if (!topClusterRisk) {
                if (!bottomClusterRisk) {
                    child.offsetLeftAndRight(((int) (this.mPathPoints[0] - this.mAnchorOffsetXY[0])) - child.getLeft());
                    child.setTranslationY(this.mPathPoints[1] - verticalAnchor);
                }
            }
            float[] fArr2 = this.mPathPoints;
            fArr2[1] = verticalAnchor;
            fArr2[0] = Math.abs(verticalAnchor) * this.mLineGradient;
            child.offsetLeftAndRight(((int) (this.mPathPoints[0] - this.mAnchorOffsetXY[0])) - child.getLeft());
            child.setTranslationY(this.mPathPoints[1] - verticalAnchor);
        }
    }

    public void adjustAnchorOffsetXY(View child, float[] anchorOffsetXY) {
    }

    private void maybeSetUpCircularInitialLayout(int width, int height) {
        int i = width;
        int i2 = height;
        if (this.mCurvePathHeight != i2) {
            r0.mCurvePathHeight = i2;
            r0.mCurveBottom = ((float) i2) * -0.048f;
            r0.mCurveTop = ((float) i2) * 1.048f;
            r0.mLineGradient = 10.416667f;
            r0.mCurvePath.reset();
            r0.mCurvePath.moveTo(((float) i) * 0.5f, r0.mCurveBottom);
            r0.mCurvePath.lineTo(((float) i) * 0.34f, ((float) i2) * 0.075f);
            r0.mCurvePath.cubicTo(((float) i) * 0.22f, ((float) i2) * 0.17f, ((float) i) * 0.13f, ((float) i2) * 0.32f, ((float) i) * 0.13f, (float) (i2 / 2));
            r0.mCurvePath.cubicTo(((float) i) * 0.13f, ((float) i2) * 0.68f, ((float) i) * 0.22f, ((float) i2) * 0.83f, ((float) i) * 0.34f, ((float) i2) * 0.925f);
            r0.mCurvePath.lineTo((float) (i / 2), r0.mCurveTop);
            r0.mPathMeasure.setPath(r0.mCurvePath, false);
            r0.mPathLength = r0.mPathMeasure.getLength();
        }
    }
}

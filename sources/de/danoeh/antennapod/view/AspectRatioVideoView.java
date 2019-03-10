package de.danoeh.antennapod.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class AspectRatioVideoView extends VideoView {
    private float mAvailableHeight;
    private float mAvailableWidth;
    private int mVideoHeight;
    private int mVideoWidth;

    public AspectRatioVideoView(Context context) {
        this(context, null);
    }

    public AspectRatioVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AspectRatioVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAvailableWidth = -1.0f;
        this.mAvailableHeight = -1.0f;
        this.mVideoWidth = 0;
        this.mVideoHeight = 0;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mVideoWidth > 0) {
            if (this.mVideoHeight > 0) {
                int scaledHeight;
                float heightRatio;
                float widthRatio;
                int scaledWidth;
                if (this.mAvailableWidth >= 0.0f) {
                    if (this.mAvailableHeight >= 0.0f) {
                        scaledHeight = this.mVideoHeight;
                        heightRatio = ((float) scaledHeight) / this.mAvailableHeight;
                        widthRatio = ((float) this.mVideoWidth) / this.mAvailableWidth;
                        if (heightRatio <= widthRatio) {
                            scaledHeight = (int) Math.ceil((double) (((float) scaledHeight) / heightRatio));
                            scaledWidth = (int) Math.ceil((double) (((float) this.mVideoWidth) / heightRatio));
                        } else {
                            scaledHeight = (int) Math.ceil((double) (((float) scaledHeight) / widthRatio));
                            scaledWidth = (int) Math.ceil((double) (((float) this.mVideoWidth) / widthRatio));
                        }
                        setMeasuredDimension(scaledWidth, scaledHeight);
                        return;
                    }
                }
                this.mAvailableWidth = (float) getWidth();
                this.mAvailableHeight = (float) getHeight();
                scaledHeight = this.mVideoHeight;
                heightRatio = ((float) scaledHeight) / this.mAvailableHeight;
                widthRatio = ((float) this.mVideoWidth) / this.mAvailableWidth;
                if (heightRatio <= widthRatio) {
                    scaledHeight = (int) Math.ceil((double) (((float) scaledHeight) / widthRatio));
                    scaledWidth = (int) Math.ceil((double) (((float) this.mVideoWidth) / widthRatio));
                } else {
                    scaledHeight = (int) Math.ceil((double) (((float) scaledHeight) / heightRatio));
                    scaledWidth = (int) Math.ceil((double) (((float) this.mVideoWidth) / heightRatio));
                }
                setMeasuredDimension(scaledWidth, scaledHeight);
                return;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setVideoSize(int videoWidth, int videoHeight) {
        this.mVideoWidth = videoWidth;
        this.mVideoHeight = videoHeight;
        getHolder().setFixedSize(videoWidth, videoHeight);
        requestLayout();
        invalidate();
    }

    public void setAvailableSize(float width, float height) {
        this.mAvailableWidth = width;
        this.mAvailableHeight = height;
        requestLayout();
    }
}

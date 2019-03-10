package com.yqritc.recyclerviewflexibledivider;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.view.View;

public class HorizontalDividerItemDecoration extends FlexibleDividerDecoration {
    private MarginProvider mMarginProvider;

    public interface MarginProvider {
        int dividerLeftMargin(int i, RecyclerView recyclerView);

        int dividerRightMargin(int i, RecyclerView recyclerView);
    }

    public static class Builder extends com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration.Builder<Builder> {
        private MarginProvider mMarginProvider = new C10111();

        /* renamed from: com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration$Builder$1 */
        class C10111 implements MarginProvider {
            C10111() {
            }

            public int dividerLeftMargin(int position, RecyclerView parent) {
                return 0;
            }

            public int dividerRightMargin(int position, RecyclerView parent) {
                return 0;
            }
        }

        public Builder(Context context) {
            super(context);
        }

        public Builder margin(final int leftMargin, final int rightMargin) {
            return marginProvider(new MarginProvider() {
                public int dividerLeftMargin(int position, RecyclerView parent) {
                    return leftMargin;
                }

                public int dividerRightMargin(int position, RecyclerView parent) {
                    return rightMargin;
                }
            });
        }

        public Builder margin(int horizontalMargin) {
            return margin(horizontalMargin, horizontalMargin);
        }

        public Builder marginResId(@DimenRes int leftMarginId, @DimenRes int rightMarginId) {
            return margin(this.mResources.getDimensionPixelSize(leftMarginId), this.mResources.getDimensionPixelSize(rightMarginId));
        }

        public Builder marginResId(@DimenRes int horizontalMarginId) {
            return marginResId(horizontalMarginId, horizontalMarginId);
        }

        public Builder marginProvider(MarginProvider provider) {
            this.mMarginProvider = provider;
            return this;
        }

        public HorizontalDividerItemDecoration build() {
            checkBuilderParams();
            return new HorizontalDividerItemDecoration(this);
        }
    }

    protected HorizontalDividerItemDecoration(Builder builder) {
        super(builder);
        this.mMarginProvider = builder.mMarginProvider;
    }

    protected Rect getDividerBound(int position, RecyclerView parent, View child) {
        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) ViewCompat.getTranslationX(child);
        int transitionY = (int) ViewCompat.getTranslationY(child);
        LayoutParams params = (LayoutParams) child.getLayoutParams();
        bounds.left = (parent.getPaddingLeft() + this.mMarginProvider.dividerLeftMargin(position, parent)) + transitionX;
        bounds.right = ((parent.getWidth() - parent.getPaddingRight()) - this.mMarginProvider.dividerRightMargin(position, parent)) + transitionX;
        int dividerSize = getDividerSize(position, parent);
        boolean isReverseLayout = isReverseLayout(parent);
        if (this.mDividerType != DividerType.DRAWABLE) {
            int halfSize = dividerSize / 2;
            if (isReverseLayout) {
                bounds.top = ((child.getTop() - params.topMargin) - halfSize) + transitionY;
            } else {
                bounds.top = ((child.getBottom() + params.bottomMargin) + halfSize) + transitionY;
            }
            bounds.bottom = bounds.top;
        } else if (isReverseLayout) {
            bounds.bottom = (child.getTop() - params.topMargin) + transitionY;
            bounds.top = bounds.bottom - dividerSize;
        } else {
            bounds.top = (child.getBottom() + params.bottomMargin) + transitionY;
            bounds.bottom = bounds.top + dividerSize;
        }
        if (this.mPositionInsideItem) {
            if (isReverseLayout) {
                bounds.top += dividerSize;
                bounds.bottom += dividerSize;
            } else {
                bounds.top -= dividerSize;
                bounds.bottom -= dividerSize;
            }
        }
        return bounds;
    }

    protected void setItemOffsets(Rect outRect, int position, RecyclerView parent) {
        if (this.mPositionInsideItem) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        if (isReverseLayout(parent)) {
            outRect.set(0, getDividerSize(position, parent), 0, 0);
        } else {
            outRect.set(0, 0, 0, getDividerSize(position, parent));
        }
    }

    private int getDividerSize(int position, RecyclerView parent) {
        if (this.mPaintProvider != null) {
            return (int) this.mPaintProvider.dividerPaint(position, parent).getStrokeWidth();
        }
        if (this.mSizeProvider != null) {
            return this.mSizeProvider.dividerSize(position, parent);
        }
        if (this.mDrawableProvider != null) {
            return this.mDrawableProvider.drawableProvider(position, parent).getIntrinsicHeight();
        }
        throw new RuntimeException("failed to get size");
    }
}

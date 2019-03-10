package com.yqritc.recyclerviewflexibledivider;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.view.View;

public class VerticalDividerItemDecoration extends FlexibleDividerDecoration {
    private MarginProvider mMarginProvider;

    public interface MarginProvider {
        int dividerBottomMargin(int i, RecyclerView recyclerView);

        int dividerTopMargin(int i, RecyclerView recyclerView);
    }

    public static class Builder extends com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration.Builder<Builder> {
        private MarginProvider mMarginProvider = new C10131();

        /* renamed from: com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration$Builder$1 */
        class C10131 implements MarginProvider {
            C10131() {
            }

            public int dividerTopMargin(int position, RecyclerView parent) {
                return 0;
            }

            public int dividerBottomMargin(int position, RecyclerView parent) {
                return 0;
            }
        }

        public Builder(Context context) {
            super(context);
        }

        public Builder margin(final int topMargin, final int bottomMargin) {
            return marginProvider(new MarginProvider() {
                public int dividerTopMargin(int position, RecyclerView parent) {
                    return topMargin;
                }

                public int dividerBottomMargin(int position, RecyclerView parent) {
                    return bottomMargin;
                }
            });
        }

        public Builder margin(int verticalMargin) {
            return margin(verticalMargin, verticalMargin);
        }

        public Builder marginResId(@DimenRes int topMarginId, @DimenRes int bottomMarginId) {
            return margin(this.mResources.getDimensionPixelSize(topMarginId), this.mResources.getDimensionPixelSize(bottomMarginId));
        }

        public Builder marginResId(@DimenRes int verticalMarginId) {
            return marginResId(verticalMarginId, verticalMarginId);
        }

        public Builder marginProvider(MarginProvider provider) {
            this.mMarginProvider = provider;
            return this;
        }

        public VerticalDividerItemDecoration build() {
            checkBuilderParams();
            return new VerticalDividerItemDecoration(this);
        }
    }

    protected VerticalDividerItemDecoration(Builder builder) {
        super(builder);
        this.mMarginProvider = builder.mMarginProvider;
    }

    protected Rect getDividerBound(int position, RecyclerView parent, View child) {
        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) ViewCompat.getTranslationX(child);
        int transitionY = (int) ViewCompat.getTranslationY(child);
        LayoutParams params = (LayoutParams) child.getLayoutParams();
        bounds.top = (parent.getPaddingTop() + this.mMarginProvider.dividerTopMargin(position, parent)) + transitionY;
        bounds.bottom = ((parent.getHeight() - parent.getPaddingBottom()) - this.mMarginProvider.dividerBottomMargin(position, parent)) + transitionY;
        int dividerSize = getDividerSize(position, parent);
        boolean isReverseLayout = isReverseLayout(parent);
        if (this.mDividerType != DividerType.DRAWABLE) {
            int halfSize = dividerSize / 2;
            if (isReverseLayout) {
                bounds.left = ((child.getLeft() - params.leftMargin) - halfSize) + transitionX;
            } else {
                bounds.left = ((child.getRight() + params.rightMargin) + halfSize) + transitionX;
            }
            bounds.right = bounds.left;
        } else if (isReverseLayout) {
            bounds.right = (child.getLeft() - params.leftMargin) + transitionX;
            bounds.left = bounds.right - dividerSize;
        } else {
            bounds.left = (child.getRight() + params.rightMargin) + transitionX;
            bounds.right = bounds.left + dividerSize;
        }
        if (this.mPositionInsideItem) {
            if (isReverseLayout) {
                bounds.left += dividerSize;
                bounds.right += dividerSize;
            } else {
                bounds.left -= dividerSize;
                bounds.right -= dividerSize;
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
            outRect.set(getDividerSize(position, parent), 0, 0, 0);
        } else {
            outRect.set(0, 0, getDividerSize(position, parent), 0);
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
            return this.mDrawableProvider.drawableProvider(position, parent).getIntrinsicWidth();
        }
        throw new RuntimeException("failed to get size");
    }
}

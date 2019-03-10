package com.yqritc.recyclerviewflexibledivider;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

public abstract class FlexibleDividerDecoration extends ItemDecoration {
    private static final int[] ATTRS = new int[]{16843284};
    private static final int DEFAULT_SIZE = 2;
    protected ColorProvider mColorProvider;
    protected DividerType mDividerType = DividerType.DRAWABLE;
    protected DrawableProvider mDrawableProvider;
    private Paint mPaint;
    protected PaintProvider mPaintProvider;
    protected boolean mPositionInsideItem;
    protected boolean mShowLastDivider;
    protected SizeProvider mSizeProvider;
    protected VisibilityProvider mVisibilityProvider;

    public static class Builder<T extends Builder> {
        private ColorProvider mColorProvider;
        private Context mContext;
        private DrawableProvider mDrawableProvider;
        private PaintProvider mPaintProvider;
        private boolean mPositionInsideItem = false;
        protected Resources mResources;
        private boolean mShowLastDivider = false;
        private SizeProvider mSizeProvider;
        private VisibilityProvider mVisibilityProvider = new C10061();

        /* renamed from: com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration$Builder$1 */
        class C10061 implements VisibilityProvider {
            C10061() {
            }

            public boolean shouldHideDivider(int position, RecyclerView parent) {
                return false;
            }
        }

        public Builder(Context context) {
            this.mContext = context;
            this.mResources = context.getResources();
        }

        public T paint(final Paint paint) {
            return paintProvider(new PaintProvider() {
                public Paint dividerPaint(int position, RecyclerView parent) {
                    return paint;
                }
            });
        }

        public T paintProvider(PaintProvider provider) {
            this.mPaintProvider = provider;
            return this;
        }

        public T color(final int color) {
            return colorProvider(new ColorProvider() {
                public int dividerColor(int position, RecyclerView parent) {
                    return color;
                }
            });
        }

        public T colorResId(@ColorRes int colorId) {
            return color(ContextCompat.getColor(this.mContext, colorId));
        }

        public T colorProvider(ColorProvider provider) {
            this.mColorProvider = provider;
            return this;
        }

        public T drawable(@DrawableRes int id) {
            return drawable(ContextCompat.getDrawable(this.mContext, id));
        }

        public T drawable(final Drawable drawable) {
            return drawableProvider(new DrawableProvider() {
                public Drawable drawableProvider(int position, RecyclerView parent) {
                    return drawable;
                }
            });
        }

        public T drawableProvider(DrawableProvider provider) {
            this.mDrawableProvider = provider;
            return this;
        }

        public T size(final int size) {
            return sizeProvider(new SizeProvider() {
                public int dividerSize(int position, RecyclerView parent) {
                    return size;
                }
            });
        }

        public T sizeResId(@DimenRes int sizeId) {
            return size(this.mResources.getDimensionPixelSize(sizeId));
        }

        public T sizeProvider(SizeProvider provider) {
            this.mSizeProvider = provider;
            return this;
        }

        public T visibilityProvider(VisibilityProvider provider) {
            this.mVisibilityProvider = provider;
            return this;
        }

        public T showLastDivider() {
            this.mShowLastDivider = true;
            return this;
        }

        public T positionInsideItem(boolean positionInsideItem) {
            this.mPositionInsideItem = positionInsideItem;
            return this;
        }

        protected void checkBuilderParams() {
            if (this.mPaintProvider == null) {
                return;
            }
            if (this.mColorProvider != null) {
                throw new IllegalArgumentException("Use setColor method of Paint class to specify line color. Do not provider ColorProvider if you set PaintProvider.");
            } else if (this.mSizeProvider != null) {
                throw new IllegalArgumentException("Use setStrokeWidth method of Paint class to specify line size. Do not provider SizeProvider if you set PaintProvider.");
            }
        }
    }

    public interface ColorProvider {
        int dividerColor(int i, RecyclerView recyclerView);
    }

    protected enum DividerType {
        DRAWABLE,
        PAINT,
        COLOR
    }

    public interface DrawableProvider {
        Drawable drawableProvider(int i, RecyclerView recyclerView);
    }

    public interface PaintProvider {
        Paint dividerPaint(int i, RecyclerView recyclerView);
    }

    public interface SizeProvider {
        int dividerSize(int i, RecyclerView recyclerView);
    }

    public interface VisibilityProvider {
        boolean shouldHideDivider(int i, RecyclerView recyclerView);
    }

    /* renamed from: com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration$2 */
    class C10052 implements SizeProvider {
        C10052() {
        }

        public int dividerSize(int position, RecyclerView parent) {
            return 2;
        }
    }

    protected abstract Rect getDividerBound(int i, RecyclerView recyclerView, View view);

    protected abstract void setItemOffsets(Rect rect, int i, RecyclerView recyclerView);

    protected FlexibleDividerDecoration(Builder builder) {
        if (builder.mPaintProvider != null) {
            this.mDividerType = DividerType.PAINT;
            this.mPaintProvider = builder.mPaintProvider;
        } else if (builder.mColorProvider != null) {
            this.mDividerType = DividerType.COLOR;
            this.mColorProvider = builder.mColorProvider;
            this.mPaint = new Paint();
            setSizeProvider(builder);
        } else {
            this.mDividerType = DividerType.DRAWABLE;
            if (builder.mDrawableProvider == null) {
                TypedArray a = builder.mContext.obtainStyledAttributes(ATTRS);
                final Drawable divider = a.getDrawable(null);
                a.recycle();
                this.mDrawableProvider = new DrawableProvider() {
                    public Drawable drawableProvider(int position, RecyclerView parent) {
                        return divider;
                    }
                };
            } else {
                this.mDrawableProvider = builder.mDrawableProvider;
            }
            this.mSizeProvider = builder.mSizeProvider;
        }
        this.mVisibilityProvider = builder.mVisibilityProvider;
        this.mShowLastDivider = builder.mShowLastDivider;
        this.mPositionInsideItem = builder.mPositionInsideItem;
    }

    private void setSizeProvider(Builder builder) {
        this.mSizeProvider = builder.mSizeProvider;
        if (this.mSizeProvider == null) {
            this.mSizeProvider = new C10052();
        }
    }

    public void onDraw(Canvas c, RecyclerView parent, State state) {
        FlexibleDividerDecoration flexibleDividerDecoration = this;
        RecyclerView recyclerView = parent;
        Adapter adapter = parent.getAdapter();
        if (adapter != null) {
            int itemCount;
            int itemCount2 = adapter.getItemCount();
            int lastDividerOffset = getLastDividerOffset(recyclerView);
            int validChildCount = parent.getChildCount();
            int lastChildPosition = -1;
            int i = 0;
            Drawable drawable = null;
            while (i < validChildCount) {
                Adapter adapter2;
                View child = recyclerView.getChildAt(i);
                int childPosition = recyclerView.getChildAdapterPosition(child);
                if (childPosition < lastChildPosition) {
                    adapter2 = adapter;
                    itemCount = itemCount2;
                    itemCount2 = c;
                } else {
                    lastChildPosition = childPosition;
                    if (flexibleDividerDecoration.mShowLastDivider || childPosition < itemCount2 - lastDividerOffset) {
                        if (!wasDividerAlreadyDrawn(childPosition, recyclerView)) {
                            int groupIndex = getGroupIndex(childPosition, recyclerView);
                            if (!flexibleDividerDecoration.mVisibilityProvider.shouldHideDivider(groupIndex, recyclerView)) {
                                Rect bounds = getDividerBound(groupIndex, recyclerView, child);
                                Canvas canvas;
                                switch (flexibleDividerDecoration.mDividerType) {
                                    case DRAWABLE:
                                        adapter2 = adapter;
                                        itemCount = itemCount2;
                                        adapter = flexibleDividerDecoration.mDrawableProvider.drawableProvider(groupIndex, recyclerView);
                                        adapter.setBounds(bounds);
                                        adapter.draw(c);
                                        drawable = adapter;
                                        break;
                                    case PAINT:
                                        adapter2 = adapter;
                                        itemCount = itemCount2;
                                        Drawable adapter3 = drawable;
                                        flexibleDividerDecoration.mPaint = flexibleDividerDecoration.mPaintProvider.dividerPaint(groupIndex, recyclerView);
                                        c.drawLine((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom, flexibleDividerDecoration.mPaint);
                                        canvas = c;
                                        drawable = adapter3;
                                        break;
                                    case COLOR:
                                        flexibleDividerDecoration.mPaint.setColor(flexibleDividerDecoration.mColorProvider.dividerColor(groupIndex, recyclerView));
                                        flexibleDividerDecoration.mPaint.setStrokeWidth((float) flexibleDividerDecoration.mSizeProvider.dividerSize(groupIndex, recyclerView));
                                        adapter2 = adapter;
                                        itemCount = itemCount2;
                                        float f = (float) bounds.right;
                                        c.drawLine((float) bounds.left, (float) bounds.top, f, (float) bounds.bottom, flexibleDividerDecoration.mPaint);
                                        canvas = c;
                                        break;
                                    default:
                                        adapter2 = adapter;
                                        itemCount = itemCount2;
                                        itemCount2 = c;
                                        break;
                                }
                            }
                            adapter2 = adapter;
                            itemCount = itemCount2;
                            itemCount2 = c;
                        } else {
                            adapter2 = adapter;
                            itemCount = itemCount2;
                            itemCount2 = c;
                        }
                    } else {
                        adapter2 = adapter;
                        itemCount = itemCount2;
                        itemCount2 = c;
                    }
                }
                i++;
                adapter = adapter2;
                itemCount2 = itemCount;
            }
            itemCount = itemCount2;
            itemCount2 = c;
        }
    }

    public void getItemOffsets(Rect rect, View v, RecyclerView parent, State state) {
        int position = parent.getChildAdapterPosition(v);
        int itemCount = parent.getAdapter().getItemCount();
        int lastDividerOffset = getLastDividerOffset(parent);
        if (this.mShowLastDivider || position < itemCount - lastDividerOffset) {
            int groupIndex = getGroupIndex(position, parent);
            if (!this.mVisibilityProvider.shouldHideDivider(groupIndex, parent)) {
                setItemOffsets(rect, groupIndex, parent);
            }
        }
    }

    protected boolean isReverseLayout(RecyclerView parent) {
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).getReverseLayout();
        }
        return false;
    }

    private int getLastDividerOffset(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
            int spanCount = layoutManager.getSpanCount();
            int itemCount = parent.getAdapter().getItemCount();
            for (int i = itemCount - 1; i >= 0; i--) {
                if (spanSizeLookup.getSpanIndex(i, spanCount) == 0) {
                    return itemCount - i;
                }
            }
        }
        return 1;
    }

    private boolean wasDividerAlreadyDrawn(int position, RecyclerView parent) {
        boolean z = false;
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            return false;
        }
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        if (layoutManager.getSpanSizeLookup().getSpanIndex(position, layoutManager.getSpanCount()) > 0) {
            z = true;
        }
        return z;
    }

    private int getGroupIndex(int position, RecyclerView parent) {
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) {
            return position;
        }
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        return layoutManager.getSpanSizeLookup().getSpanGroupIndex(position, layoutManager.getSpanCount());
    }
}

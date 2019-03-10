package android.support.wearable.view;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.util.LruCache;
import android.support.wearable.view.GridViewPager.OnAdapterChangeListener;
import android.support.wearable.view.GridViewPager.OnPageChangeListener;
import android.view.View;

@TargetApi(20)
@Deprecated
class BackgroundController implements OnPageChangeListener, OnAdapterChangeListener, OnBackgroundChangeListener {
    private GridPagerAdapter mAdapter;
    private final CrossfadeDrawable mBackground = new CrossfadeDrawable();
    private final ViewportDrawable mBaseLayer = new ViewportDrawable();
    private final Point mBaseSourcePage = new Point();
    private float mBaseXPos;
    private int mBaseXSteps;
    private float mBaseYPos;
    private int mBaseYSteps;
    private final ViewportDrawable mCrossfadeLayer = new ViewportDrawable();
    private float mCrossfadeXPos;
    private float mCrossfadeYPos;
    private final Point mCurrentPage = new Point();
    private Direction mDirection = Direction.NONE;
    private final Point mFadeSourcePage = new Point();
    private int mFadeXSteps;
    private int mFadeYSteps;
    private final Point mLastPageScrolled = new Point();
    private final Point mLastSelectedPage = new Point();
    private final LruCache<Integer, Drawable> mPageBackgrounds = new LruCache<Integer, Drawable>(5) {
        protected Drawable create(Integer key) {
            int col = BackgroundController.unpackX(key.intValue());
            return BackgroundController.this.mAdapter.getBackgroundForPage(BackgroundController.unpackY(key.intValue()), col).mutate();
        }
    };
    private final LruCache<Integer, Drawable> mRowBackgrounds = new LruCache<Integer, Drawable>(3) {
        protected Drawable create(Integer key) {
            return BackgroundController.this.mAdapter.getBackgroundForRow(key.intValue()).mutate();
        }
    };
    private float mScrollRelativeX;
    private float mScrollRelativeY;
    private boolean mUsingCrossfadeLayer;

    private enum Direction {
        LEFT(-1, 0),
        UP(0, -1),
        RIGHT(1, 0),
        DOWN(0, 1),
        NONE(0, 0);
        
        /* renamed from: x */
        private final int f7x;
        /* renamed from: y */
        private final int f8y;

        private Direction(int x, int y) {
            this.f7x = x;
            this.f8y = y;
        }

        boolean isVertical() {
            return this.f8y != 0;
        }

        boolean isHorizontal() {
            return this.f7x != 0;
        }

        static Direction fromOffset(float x, float y) {
            if (y != 0.0f) {
                return y > 0.0f ? DOWN : UP;
            } else if (x == 0.0f) {
                return NONE;
            } else {
                return x > 0.0f ? RIGHT : LEFT;
            }
        }
    }

    private static int pack(int x, int y) {
        return (y << 16) | (SupportMenu.USER_MASK & x);
    }

    private static int pack(Point p) {
        return pack(p.x, p.y);
    }

    private static int unpackX(int key) {
        return SupportMenu.USER_MASK & key;
    }

    private static int unpackY(int key) {
        return key >>> 16;
    }

    public BackgroundController() {
        this.mBackground.setFilterBitmap(true);
        this.mCrossfadeLayer.setFilterBitmap(true);
        this.mBaseLayer.setFilterBitmap(true);
    }

    public Drawable getBackground() {
        return this.mBackground;
    }

    public void attachTo(View v) {
        v.setBackground(this.mBackground);
    }

    public void onPageScrollStateChanged(int state) {
        if (state == 0) {
            this.mDirection = Direction.NONE;
        }
    }

    public void onPageScrolled(int row, int column, float rowOffset, float colOffset, int rowOffsetPx, int colOffsetPx) {
        float relX;
        float relY;
        float relX2;
        int i = row;
        int i2 = column;
        if (this.mDirection != Direction.NONE) {
            if (r6.mCurrentPage.equals(r6.mLastSelectedPage)) {
                if (r6.mLastPageScrolled.equals(column, row)) {
                    if (r6.mDirection.isVertical()) {
                        relX = 0.0f;
                        relY = ((float) Func.clamp(i - r6.mCurrentPage.y, -1, 0)) + rowOffset;
                    } else {
                        relX = ((float) Func.clamp(i2 - r6.mCurrentPage.x, -1, 0)) + colOffset;
                        relY = 0.0f;
                    }
                    r6.mScrollRelativeX = relX;
                    r6.mScrollRelativeY = relY;
                    r6.mBaseLayer.setPosition(r6.mBaseXPos + relX, r6.mBaseYPos + relY);
                    if (r6.mUsingCrossfadeLayer) {
                        r6.mBackground.setProgress(r6.mDirection.isVertical() ? Math.abs(relY) : Math.abs(relX));
                        r6.mCrossfadeLayer.setPosition(r6.mCrossfadeXPos + relX, r6.mCrossfadeYPos + relY);
                    }
                }
            }
        }
        r6.mLastPageScrolled.set(column, row);
        r6.mCurrentPage.set(r6.mLastSelectedPage.x, r6.mLastSelectedPage.y);
        float relY2 = ((float) Func.clamp(i - r6.mCurrentPage.y, -1, 0)) + rowOffset;
        if (relY2 == 0.0f) {
            relX2 = ((float) Func.clamp(i2 - r6.mCurrentPage.x, -1, 0)) + colOffset;
        } else {
            relX2 = 0.0f;
        }
        r6.mDirection = Direction.fromOffset(relX2, relY2);
        updateBackgrounds(r6.mCurrentPage, r6.mLastPageScrolled, r6.mDirection, relX2, relY2);
        relY = relY2;
        relX = relX2;
        r6.mScrollRelativeX = relX;
        r6.mScrollRelativeY = relY;
        r6.mBaseLayer.setPosition(r6.mBaseXPos + relX, r6.mBaseYPos + relY);
        if (r6.mUsingCrossfadeLayer) {
            if (r6.mDirection.isVertical()) {
            }
            r6.mBackground.setProgress(r6.mDirection.isVertical() ? Math.abs(relY) : Math.abs(relX));
            r6.mCrossfadeLayer.setPosition(r6.mCrossfadeXPos + relX, r6.mCrossfadeYPos + relY);
        }
    }

    private void updateBackgrounds(Point current, Point scrolling, Direction dir, float relX, float relY) {
        Point point = current;
        Point point2 = scrolling;
        float f = relX;
        float f2 = relY;
        GridPagerAdapter gridPagerAdapter = this.mAdapter;
        if (gridPagerAdapter == null || gridPagerAdapter.getRowCount() <= 0) {
            r7.mUsingCrossfadeLayer = false;
            r7.mBaseLayer.setDrawable(null);
            r7.mCrossfadeLayer.setDrawable(null);
        } else {
            boolean overScrolling;
            Drawable base = updateBaseLayer(current, f, f2);
            boolean z = true;
            if (((float) point.x) + f >= 0.0f && ((float) point.y) + f2 >= 0.0f) {
                if (((float) point2.x) + f <= ((float) (r7.mAdapter.getColumnCount(point.y) - 1))) {
                    if (((float) point2.y) + f2 <= ((float) (r7.mAdapter.getRowCount() - 1))) {
                        z = false;
                        overScrolling = z;
                        if (r7.mDirection != Direction.NONE) {
                            if (overScrolling) {
                                updateFadingLayer(current, scrolling, dir, relX, relY, base);
                            }
                        }
                        r7.mUsingCrossfadeLayer = false;
                        r7.mCrossfadeLayer.setDrawable(null);
                        r7.mBackground.setProgress(0.0f);
                    }
                }
            }
            overScrolling = z;
            if (r7.mDirection != Direction.NONE) {
                if (overScrolling) {
                    updateFadingLayer(current, scrolling, dir, relX, relY, base);
                }
            }
            r7.mUsingCrossfadeLayer = false;
            r7.mCrossfadeLayer.setDrawable(null);
            r7.mBackground.setProgress(0.0f);
        }
    }

    private Drawable updateBaseLayer(Point current, float relX, float relY) {
        Drawable base = (Drawable) this.mPageBackgrounds.get(Integer.valueOf(pack(current)));
        this.mBaseSourcePage.set(current.x, current.y);
        if (base == GridPagerAdapter.BACKGROUND_NONE) {
            base = (Drawable) this.mRowBackgrounds.get(Integer.valueOf(current.y));
            this.mBaseXSteps = this.mAdapter.getColumnCount(current.y) + 2;
            this.mBaseXPos = (float) (current.x + 1);
        } else {
            this.mBaseXSteps = 3;
            this.mBaseXPos = 1.0f;
        }
        this.mBaseYSteps = 3;
        this.mBaseYPos = 1.0f;
        this.mBaseLayer.setDrawable(base);
        this.mBaseLayer.setStops(this.mBaseXSteps, this.mBaseYSteps);
        this.mBaseLayer.setPosition(this.mBaseXPos + relX, this.mBaseYPos + relY);
        this.mBackground.setBase(this.mBaseLayer);
        return base;
    }

    private void updateFadingLayer(Point current, Point scrolling, Direction dir, float relX, float relY, Drawable base) {
        int crossfadeY = scrolling.y + (dir == Direction.DOWN ? 1 : 0);
        int crossfadeX = scrolling.x + (dir == Direction.RIGHT ? 1 : 0);
        if (crossfadeY != this.mCurrentPage.y) {
            crossfadeX = this.mAdapter.getCurrentColumnForRow(crossfadeY, current.x);
        }
        Drawable fade = (Drawable) this.mPageBackgrounds.get(Integer.valueOf(pack(crossfadeX, crossfadeY)));
        this.mFadeSourcePage.set(crossfadeX, crossfadeY);
        boolean fadeIsRowBg = false;
        if (fade == GridPagerAdapter.BACKGROUND_NONE) {
            fade = (Drawable) this.mRowBackgrounds.get(Integer.valueOf(crossfadeY));
            fadeIsRowBg = true;
        }
        if (base == fade) {
            this.mUsingCrossfadeLayer = false;
            this.mCrossfadeLayer.setDrawable(null);
            this.mBackground.setFading(null);
            this.mBackground.setProgress(0.0f);
            return;
        }
        if (fadeIsRowBg) {
            this.mFadeXSteps = this.mAdapter.getColumnCount(Func.clamp(crossfadeY, 0, this.mAdapter.getRowCount() - 1)) + 2;
            if (dir.isHorizontal()) {
                this.mCrossfadeXPos = (float) (current.x + 1);
            } else {
                this.mCrossfadeXPos = (float) (crossfadeX + 1);
            }
        } else {
            this.mFadeXSteps = 3;
            this.mCrossfadeXPos = (float) (1 - dir.f7x);
        }
        this.mFadeYSteps = 3;
        this.mCrossfadeYPos = (float) (1 - dir.f8y);
        this.mUsingCrossfadeLayer = true;
        this.mCrossfadeLayer.setDrawable(fade);
        this.mCrossfadeLayer.setStops(this.mFadeXSteps, this.mFadeYSteps);
        this.mCrossfadeLayer.setPosition(this.mCrossfadeXPos + relX, this.mCrossfadeYPos + relY);
        this.mBackground.setFading(this.mCrossfadeLayer);
    }

    public void onPageSelected(int row, int column) {
        this.mLastSelectedPage.set(column, row);
    }

    public void onPageBackgroundChanged(int row, int column) {
        this.mPageBackgrounds.remove(Integer.valueOf(pack(column, row)));
        GridPagerAdapter gridPagerAdapter = this.mAdapter;
        if (gridPagerAdapter != null && gridPagerAdapter.getRowCount() > 0) {
            Point point = this.mCurrentPage;
            updateBackgrounds(point, point, Direction.NONE, this.mScrollRelativeX, this.mScrollRelativeY);
        }
    }

    public void onRowBackgroundChanged(int row) {
        this.mRowBackgrounds.remove(Integer.valueOf(row));
        GridPagerAdapter gridPagerAdapter = this.mAdapter;
        if (gridPagerAdapter != null && gridPagerAdapter.getRowCount() > 0) {
            Point point = this.mCurrentPage;
            updateBackgrounds(point, point, Direction.NONE, this.mScrollRelativeX, this.mScrollRelativeY);
        }
    }

    public void onAdapterChanged(GridPagerAdapter oldAdapter, GridPagerAdapter newAdapter) {
        reset();
        this.mLastSelectedPage.set(0, 0);
        this.mCurrentPage.set(0, 0);
        this.mAdapter = newAdapter;
    }

    public void onDataSetChanged() {
        reset();
    }

    private void reset() {
        this.mDirection = Direction.NONE;
        this.mPageBackgrounds.evictAll();
        this.mRowBackgrounds.evictAll();
        this.mCrossfadeLayer.setDrawable(null);
        this.mBaseLayer.setDrawable(null);
    }
}

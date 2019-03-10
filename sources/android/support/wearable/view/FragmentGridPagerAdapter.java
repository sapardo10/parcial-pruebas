package android.support.wearable.view;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.GridPageOptions.BackgroundListener;
import android.view.View;
import android.view.ViewGroup;
import java.util.HashMap;
import java.util.Map;

@TargetApi(20)
@Deprecated
public abstract class FragmentGridPagerAdapter extends GridPagerAdapter {
    private static final int MAX_ROWS = 65535;
    private static final BackgroundListener NOOP_BACKGROUND_OBSERVER = new C09161();
    private FragmentTransaction mCurTransaction;
    private final FragmentManager mFragmentManager;
    private final Map<String, Point> mFragmentPositions = new HashMap();
    private final Map<Point, String> mFragmentTags = new HashMap();

    /* renamed from: android.support.wearable.view.FragmentGridPagerAdapter$1 */
    class C09161 implements BackgroundListener {
        C09161() {
        }

        public void notifyBackgroundChanged() {
        }
    }

    private class BackgroundObserver implements BackgroundListener {
        private final String mTag;

        private BackgroundObserver(String tag) {
            this.mTag = tag;
        }

        public void notifyBackgroundChanged() {
            Point pos = (Point) FragmentGridPagerAdapter.this.mFragmentPositions.get(this.mTag);
            if (pos != null) {
                FragmentGridPagerAdapter.this.notifyPageBackgroundChanged(pos.y, pos.x);
            }
        }
    }

    public abstract Fragment getFragment(int i, int i2);

    public FragmentGridPagerAdapter(FragmentManager fm) {
        this.mFragmentManager = fm;
    }

    private static String makeFragmentName(int viewId, long id) {
        StringBuilder stringBuilder = new StringBuilder(49);
        stringBuilder.append("android:switcher:");
        stringBuilder.append(viewId);
        stringBuilder.append(":");
        stringBuilder.append(id);
        return stringBuilder.toString();
    }

    public long getFragmentId(int row, int column) {
        return (long) ((65535 * column) + row);
    }

    public Fragment instantiateItem(ViewGroup container, int row, int column) {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }
        String tag = makeFragmentName(container.getId(), getFragmentId(row, column));
        Fragment fragment = this.mFragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = getFragment(row, column);
            this.mCurTransaction.add(container.getId(), fragment, tag);
        } else {
            restoreFragment(fragment, this.mCurTransaction);
        }
        Point position = new Point(column, row);
        this.mFragmentTags.put(position, tag);
        this.mFragmentPositions.put(tag, position);
        if (fragment instanceof GridPageOptions) {
            ((GridPageOptions) fragment).setBackgroundListener(new BackgroundObserver(tag));
        }
        return fragment;
    }

    protected void restoreFragment(Fragment fragment, FragmentTransaction transaction) {
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == ((Fragment) object).getView();
    }

    public void destroyItem(ViewGroup container, int row, int column, Object object) {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }
        Fragment fragment = (Fragment) object;
        if (fragment instanceof GridPageOptions) {
            ((GridPageOptions) fragment).setBackgroundListener(NOOP_BACKGROUND_OBSERVER);
        }
        removeFragment(fragment, this.mCurTransaction);
    }

    protected void removeFragment(Fragment fragment, FragmentTransaction transaction) {
        transaction.remove(fragment);
    }

    protected void applyItemPosition(Object object, Point position) {
        if (position != GridPagerAdapter.POSITION_UNCHANGED) {
            Fragment fragment = (Fragment) object;
            if (fragment.getTag().equals(this.mFragmentTags.get(position))) {
                this.mFragmentTags.remove(position);
            }
            if (position == GridPagerAdapter.POSITION_NONE) {
                this.mFragmentPositions.remove(fragment.getTag());
            } else {
                this.mFragmentPositions.put(fragment.getTag(), position);
                this.mFragmentTags.put(position, fragment.getTag());
            }
        }
    }

    public final Drawable getFragmentBackground(int row, int column) {
        Fragment f = this.mFragmentManager.findFragmentByTag((String) this.mFragmentTags.get(new Point(column, row)));
        Drawable bg = BACKGROUND_NONE;
        if (f instanceof GridPageOptions) {
            return ((GridPageOptions) f).getBackground();
        }
        return bg;
    }

    public Drawable getBackgroundForPage(int row, int column) {
        return getFragmentBackground(row, column);
    }

    public void finishUpdate(ViewGroup container) {
        if (this.mFragmentManager.isDestroyed()) {
            this.mCurTransaction = null;
            return;
        }
        FragmentTransaction fragmentTransaction = this.mCurTransaction;
        if (fragmentTransaction != null) {
            fragmentTransaction.commitAllowingStateLoss();
            this.mCurTransaction = null;
            this.mFragmentManager.executePendingTransactions();
        }
    }

    public Fragment findExistingFragment(int row, int column) {
        String tag = (String) this.mFragmentTags.get(new Point(column, row));
        if (tag != null) {
            return this.mFragmentManager.findFragmentByTag(tag);
        }
        return null;
    }
}

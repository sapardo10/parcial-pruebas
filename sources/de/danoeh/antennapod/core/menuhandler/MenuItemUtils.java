package de.danoeh.antennapod.core.menuhandler;

import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import de.danoeh.antennapod.core.C0734R;

public class MenuItemUtils {

    public interface UpdateRefreshMenuItemChecker {
        boolean isRefreshing();
    }

    public static boolean updateRefreshMenuItem(Menu menu, int resId, UpdateRefreshMenuItemChecker checker) {
        if (!checker.isRefreshing()) {
            return false;
        }
        MenuItemCompat.setActionView(menu.findItem(resId), C0734R.layout.refresh_action_view);
        return true;
    }
}

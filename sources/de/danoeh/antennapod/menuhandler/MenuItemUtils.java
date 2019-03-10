package de.danoeh.antennapod.menuhandler;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.debug.R;

public class MenuItemUtils extends de.danoeh.antennapod.core.menuhandler.MenuItemUtils {
    public static void adjustTextColor(Context context, SearchView sv) {
        if (VERSION.SDK_INT < 14) {
            EditText searchEditText = (EditText) sv.findViewById(R.id.search_src_text);
            if (UserPreferences.getTheme() != R.style.Theme.AntennaPod.Dark) {
                if (UserPreferences.getTheme() != R.style.Theme.AntennaPod.TrueBlack) {
                    searchEditText.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                    return;
                }
            }
            searchEditText.setTextColor(-1);
        }
    }

    public static void refreshLockItem(Context context, Menu menu) {
        MenuItem queueLock = menu.findItem(R.id.queue_lock);
        TypedArray ta = context.obtainStyledAttributes(new int[]{R.attr.ic_lock_open, R.attr.ic_lock_closed});
        if (UserPreferences.isQueueLocked()) {
            queueLock.setTitle(R.string.unlock_queue);
            queueLock.setIcon(ta.getDrawable(0));
        } else {
            queueLock.setTitle(R.string.lock_queue);
            queueLock.setIcon(ta.getDrawable(1));
        }
        ta.recycle();
    }
}

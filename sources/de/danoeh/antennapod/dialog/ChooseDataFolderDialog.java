package de.danoeh.antennapod.dialog;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.StorageUtils;
import de.danoeh.antennapod.debug.R;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChooseDataFolderDialog {

    public static abstract class RunnableWithString implements Runnable {
        public abstract void run(String str);

        public void run() {
            throw new IllegalArgumentException("Expect one String parameter.");
        }
    }

    private ChooseDataFolderDialog() {
    }

    public static void showDialog(Context context, RunnableWithString handlerFunc) {
        Context context2 = context;
        File dataFolder = UserPreferences.getDataFolder(null);
        if (dataFolder == null) {
            new Builder(context2).title((int) R.string.error_label).content((int) R.string.external_storage_error_msg).neutralText(17039370).show();
            return;
        }
        String dataFolderPath = dataFolder.getAbsolutePath();
        File[] mediaDirs = ContextCompat.getExternalFilesDirs(context2, null);
        List<String> folders = new ArrayList(mediaDirs.length);
        Collection choices = new ArrayList(mediaDirs.length);
        int index = 0;
        int selectedIndex = -1;
        for (File dir : mediaDirs) {
            if (dir != null && dir.exists() && dir.canRead()) {
                if (dir.canWrite()) {
                    String path = dir.getAbsolutePath();
                    folders.add(path);
                    if (dataFolderPath.equals(path)) {
                        selectedIndex = index;
                    }
                    int prefixIndex = path.indexOf("Android");
                    String choice = prefixIndex > 0 ? path.substring(0, prefixIndex) : path;
                    long bytes = StorageUtils.getFreeSpaceAvailable(path);
                    choices.add(fromHtmlVersioned(String.format("<small>%1$s [%2$s]</small>", new Object[]{choice, Converter.byteToString(bytes)})));
                    index++;
                }
            }
        }
        if (choices.isEmpty()) {
            new Builder(context2).title((int) R.string.error_label).content((int) R.string.external_storage_error_msg).neutralText(17039370).show();
        } else {
            new Builder(context2).title((int) R.string.choose_data_directory).content((int) R.string.choose_data_directory_message).items(choices).itemsCallbackSingleChoice(selectedIndex, new -$$Lambda$ChooseDataFolderDialog$GYPL1g7C5vGUhkhTeZppzwS9RRk(folders, handlerFunc)).negativeText((int) R.string.cancel_label).cancelable(true).build().show();
        }
    }

    private static CharSequence fromHtmlVersioned(String html) {
        if (VERSION.SDK_INT < 24) {
            return Html.fromHtml(html);
        }
        return Html.fromHtml(html, 0);
    }
}

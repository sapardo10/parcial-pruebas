package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.dialog.ChooseDataFolderDialog.RunnableWithString;

class StorageErrorActivity$1 extends RunnableWithString {
    final /* synthetic */ StorageErrorActivity this$0;

    StorageErrorActivity$1(StorageErrorActivity this$0) {
        this.this$0 = this$0;
    }

    public void run(String folder) {
        UserPreferences.setDataFolder(folder);
        StorageErrorActivity.access$000(this.this$0);
    }
}

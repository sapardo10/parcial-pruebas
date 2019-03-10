package de.danoeh.antennapod.core;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public interface ApplicationCallbacks {
    Application getApplicationInstance();

    Intent getStorageErrorActivity(Context context);
}

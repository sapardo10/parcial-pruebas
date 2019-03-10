package com.google.android.wearable.playstore;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import de.danoeh.antennapod.core.syndication.namespace.NSContent;

@TargetApi(24)
public class PlayStoreAvailability {
    private static final String KEY_PLAY_STORE_AVAILABILITY = "play_store_availability";
    private static final String PLAY_STORE_AVAILABILITY_PATH = "play_store_availability";
    private static final Uri PLAY_STORE_AVAILABILITY_URI = new Builder().scheme(NSContent.NSTAG).authority("com.google.android.wearable.settings").path("play_store_availability").build();
    public static final int PLAY_STORE_ON_PHONE_AVAILABLE = 1;
    public static final int PLAY_STORE_ON_PHONE_ERROR_UNKNOWN = 0;
    public static final int PLAY_STORE_ON_PHONE_UNAVAILABLE = 2;

    public static int getPlayStoreAvailabilityOnPhone(Context context) {
        Cursor cursor = context.getContentResolver().query(PLAY_STORE_AVAILABILITY_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    if ("play_store_availability".equals(cursor.getString(0))) {
                        int i = cursor.getInt(1);
                        return i;
                    }
                } finally {
                    cursor.close();
                }
            }
            cursor.close();
        }
        return 0;
    }
}

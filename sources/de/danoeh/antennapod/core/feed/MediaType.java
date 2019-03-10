package de.danoeh.antennapod.core.feed;

import android.text.TextUtils;
import com.google.android.exoplayer2.util.MimeTypes;

public enum MediaType {
    AUDIO,
    VIDEO,
    UNKNOWN;

    public static MediaType fromMimeType(String mime_type) {
        if (TextUtils.isEmpty(mime_type)) {
            return UNKNOWN;
        }
        if (mime_type.startsWith(MimeTypes.BASE_TYPE_AUDIO)) {
            return AUDIO;
        }
        if (mime_type.startsWith(MimeTypes.BASE_TYPE_VIDEO)) {
            return VIDEO;
        }
        if (mime_type.equals("application/ogg")) {
            return AUDIO;
        }
        return UNKNOWN;
    }
}

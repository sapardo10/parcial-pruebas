package de.danoeh.antennapod.core.syndication.util;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import java.util.Arrays;
import org.apache.commons.io.FilenameUtils;

public class SyndTypeUtils {
    private static final String VALID_IMAGE_MIMETYPE = "image/.*";
    private static final String VALID_MEDIA_MIMETYPE = TextUtils.join("|", Arrays.asList(new String[]{"audio/.*", "video/.*", "application/ogg", "application/octet-stream"}));

    private SyndTypeUtils() {
    }

    public static boolean enclosureTypeValid(String type) {
        if (type == null) {
            return false;
        }
        return type.matches(VALID_MEDIA_MIMETYPE);
    }

    public static boolean imageTypeValid(String type) {
        if (type == null) {
            return false;
        }
        return type.matches(VALID_IMAGE_MIMETYPE);
    }

    public static String getValidMimeTypeFromUrl(String url) {
        String type = getMimeTypeFromUrl(url);
        if (enclosureTypeValid(type)) {
            return type;
        }
        return null;
    }

    public static String getMimeTypeFromUrl(String url) {
        if (url == null) {
            return null;
        }
        String extension = FilenameUtils.getExtension(url);
        if (extension == null) {
            return null;
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}

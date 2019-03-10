package com.google.android.exoplayer2.source.smoothstreaming.manifest;

import android.net.Uri;
import com.google.android.exoplayer2.util.Util;

public final class SsUtil {
    public static Uri fixManifestUri(Uri manifestUri) {
        if (Util.toLowerInvariant(manifestUri.getLastPathSegment()).matches("manifest(\\(.+\\))?")) {
            return manifestUri;
        }
        return Uri.withAppendedPath(manifestUri, "Manifest");
    }

    private SsUtil() {
    }
}

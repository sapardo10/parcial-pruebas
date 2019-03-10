package com.google.android.exoplayer2.ui;

import android.content.res.Resources;
import android.text.TextUtils;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.util.Locale;

public class DefaultTrackNameProvider implements TrackNameProvider {
    private final Resources resources;

    public DefaultTrackNameProvider(Resources resources) {
        this.resources = (Resources) Assertions.checkNotNull(resources);
    }

    public String getTrackName(Format format) {
        String trackName;
        int trackType = inferPrimaryTrackType(format);
        if (trackType == 2) {
            trackName = joinWithSeparator(buildResolutionString(format), buildBitrateString(format));
        } else if (trackType == 1) {
            trackName = joinWithSeparator(buildLabelString(format), buildAudioChannelString(format), buildBitrateString(format));
        } else {
            trackName = buildLabelString(format);
        }
        return trackName.length() == 0 ? this.resources.getString(C0649R.string.exo_track_unknown) : trackName;
    }

    private String buildResolutionString(Format format) {
        String string;
        int width = format.width;
        int height = format.height;
        if (width != -1) {
            if (height != -1) {
                string = this.resources.getString(C0649R.string.exo_track_resolution, new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
                return string;
            }
        }
        string = "";
        return string;
    }

    private String buildBitrateString(Format format) {
        if (format.bitrate == -1) {
            return "";
        }
        return this.resources.getString(C0649R.string.exo_track_bitrate, new Object[]{Float.valueOf(((float) bitrate) / 1000000.0f)});
    }

    private String buildAudioChannelString(Format format) {
        int channelCount = format.channelCount;
        if (channelCount != -1) {
            if (channelCount >= 1) {
                switch (channelCount) {
                    case 1:
                        return this.resources.getString(C0649R.string.exo_track_mono);
                    case 2:
                        return this.resources.getString(C0649R.string.exo_track_stereo);
                    case 6:
                    case 7:
                        return this.resources.getString(C0649R.string.exo_track_surround_5_point_1);
                    case 8:
                        return this.resources.getString(C0649R.string.exo_track_surround_7_point_1);
                    default:
                        return this.resources.getString(C0649R.string.exo_track_surround);
                }
            }
        }
        return "";
    }

    private String buildLabelString(Format format) {
        if (!TextUtils.isEmpty(format.label)) {
            return format.label;
        }
        String buildLanguageString;
        String language = format.language;
        if (!TextUtils.isEmpty(language)) {
            if (!C0555C.LANGUAGE_UNDETERMINED.equals(language)) {
                buildLanguageString = buildLanguageString(language);
                return buildLanguageString;
            }
        }
        buildLanguageString = "";
        return buildLanguageString;
    }

    private String buildLanguageString(String language) {
        return (Util.SDK_INT >= 21 ? Locale.forLanguageTag(language) : new Locale(language)).getDisplayLanguage();
    }

    private String joinWithSeparator(String... items) {
        String itemList = "";
        for (String item : items) {
            if (item.length() > 0) {
                if (TextUtils.isEmpty(itemList)) {
                    itemList = item;
                } else {
                    itemList = this.resources.getString(C0649R.string.exo_item_list, new Object[]{itemList, item});
                }
            }
        }
        return itemList;
    }

    private static int inferPrimaryTrackType(Format format) {
        int trackType = MimeTypes.getTrackType(format.sampleMimeType);
        if (trackType != -1) {
            return trackType;
        }
        if (MimeTypes.getVideoMediaMimeType(format.codecs) != null) {
            return 2;
        }
        if (MimeTypes.getAudioMediaMimeType(format.codecs) != null) {
            return 1;
        }
        if (format.width == -1) {
            if (format.height == -1) {
                if (format.channelCount == -1) {
                    if (format.sampleRate == -1) {
                        return -1;
                    }
                }
                return 1;
            }
        }
        return 2;
    }
}

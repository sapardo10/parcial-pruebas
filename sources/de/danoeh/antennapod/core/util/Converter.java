package de.danoeh.antennapod.core.util;

import android.content.Context;
import android.util.Log;
import de.danoeh.antennapod.core.C0734R;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

public final class Converter {
    private static final int B_RANGE = 0;
    private static final int GB_RANGE = 3;
    private static final int HOURS_MIL = 3600000;
    private static final int KB_RANGE = 1;
    private static final int MB_RANGE = 2;
    private static final int MINUTES_MIL = 60000;
    private static final int NUM_LENGTH = 1024;
    private static final int SECONDS_MIL = 1000;
    private static final String TAG = "Converter";

    private Converter() {
    }

    public static String byteToString(long input) {
        int result = 0;
        int i = 0;
        while (i < 4) {
            double d = (double) input;
            double pow = Math.pow(1024.0d, (double) i);
            Double.isNaN(d);
            result = (int) (d / pow);
            if (result < 1024) {
                break;
            }
            i++;
        }
        StringBuilder stringBuilder;
        switch (i) {
            case 0:
                stringBuilder = new StringBuilder();
                stringBuilder.append(result);
                stringBuilder.append(" B");
                return stringBuilder.toString();
            case 1:
                stringBuilder = new StringBuilder();
                stringBuilder.append(result);
                stringBuilder.append(" KB");
                return stringBuilder.toString();
            case 2:
                stringBuilder = new StringBuilder();
                stringBuilder.append(result);
                stringBuilder.append(" MB");
                return stringBuilder.toString();
            case 3:
                stringBuilder = new StringBuilder();
                stringBuilder.append(result);
                stringBuilder.append(" GB");
                return stringBuilder.toString();
            default:
                Log.e(TAG, "Error happened in byteToString");
                return "ERROR";
        }
    }

    public static String getDurationStringLong(int duration) {
        int rest = duration - (HOURS_MIL * (duration / HOURS_MIL));
        int s = (rest - (MINUTES_MIL * (rest / MINUTES_MIL))) / 1000;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", new Object[]{Integer.valueOf(duration / HOURS_MIL), Integer.valueOf(rest / MINUTES_MIL), Integer.valueOf(s)});
    }

    public static String getDurationStringShort(int duration, boolean durationIsInHours) {
        int secondPart = MINUTES_MIL;
        int firstPartBase = durationIsInHours ? HOURS_MIL : MINUTES_MIL;
        int leftoverFromFirstPart = duration - ((duration / firstPartBase) * firstPartBase);
        if (!durationIsInHours) {
            secondPart = 1000;
        }
        secondPart = leftoverFromFirstPart / secondPart;
        return String.format(Locale.getDefault(), "%02d:%02d", new Object[]{Integer.valueOf(firstPart), Integer.valueOf(secondPart)});
    }

    public static int durationStringLongToMs(String input) {
        String[] parts = input.split(":");
        if (parts.length != 3) {
            return 0;
        }
        return (((Integer.parseInt(parts[0]) * 3600) * 1000) + ((Integer.parseInt(parts[1]) * 60) * 1000)) + (Integer.parseInt(parts[2]) * 1000);
    }

    public static int durationStringShortToMs(String input, boolean durationIsInHours) {
        String[] parts = input.split(":");
        if (parts.length != 2) {
            return 0;
        }
        int modifier = durationIsInHours ? 60 : 1;
        return (((Integer.parseInt(parts[0]) * 60) * 1000) * modifier) + ((Integer.parseInt(parts[1]) * 1000) * modifier);
    }

    public static String getDurationStringLocalized(Context context, long duration) {
        int h = (int) (duration / DateUtils.MILLIS_PER_HOUR);
        int m = ((int) (duration - ((long) (HOURS_MIL * h)))) / MINUTES_MIL;
        String result = "";
        if (h > 0) {
            String hours = context.getResources().getQuantityString(C0734R.plurals.time_hours_quantified, h, new Object[]{Integer.valueOf(h)});
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(result);
            stringBuilder.append(hours);
            stringBuilder.append(StringUtils.SPACE);
            result = stringBuilder.toString();
        }
        String minutes = context.getResources().getQuantityString(C0734R.plurals.time_minutes_quantified, m, new Object[]{Integer.valueOf(m)});
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(result);
        stringBuilder2.append(minutes);
        return stringBuilder2.toString();
    }

    public static String shortLocalizedDuration(Context context, long time) {
        float hours = ((float) time) / 3600.0f;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(Locale.getDefault(), "%.1f ", new Object[]{Float.valueOf(hours)}));
        stringBuilder.append(context.getString(C0734R.string.time_hours));
        return stringBuilder.toString();
    }

    public static float getVolumeFromPercentage(int progress) {
        if (progress == 100) {
            return 1.0f;
        }
        return (float) (1.0d - (Math.log((double) (101 - progress)) / Math.log(101.0d)));
    }
}

package org.apache.commons.lang3.time;

import java.util.Date;
import java.util.TimeZone;

class GmtTimeZone extends TimeZone {
    private static final int HOURS_PER_DAY = 24;
    private static final int MILLISECONDS_PER_MINUTE = 60000;
    private static final int MINUTES_PER_HOUR = 60;
    static final long serialVersionUID = 1;
    private final int offset;
    private final String zoneId;

    GmtTimeZone(boolean negate, int hours, int minutes) {
        StringBuilder stringBuilder;
        if (hours >= 24) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(hours);
            stringBuilder.append(" hours out of range");
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (minutes < 60) {
            int milliseconds = ((hours * 60) + minutes) * MILLISECONDS_PER_MINUTE;
            this.offset = negate ? -milliseconds : milliseconds;
            stringBuilder = new StringBuilder(9);
            stringBuilder.append(TimeZones.GMT_ID);
            stringBuilder.append(negate ? '-' : '+');
            stringBuilder = twoDigits(stringBuilder, hours);
            stringBuilder.append(':');
            this.zoneId = twoDigits(stringBuilder, minutes).toString();
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append(minutes);
            stringBuilder.append(" minutes out of range");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    private static StringBuilder twoDigits(StringBuilder sb, int n) {
        sb.append((char) ((n / 10) + 48));
        sb.append((char) ((n % 10) + 48));
        return sb;
    }

    public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds) {
        return this.offset;
    }

    public void setRawOffset(int offsetMillis) {
        throw new UnsupportedOperationException();
    }

    public int getRawOffset() {
        return this.offset;
    }

    public String getID() {
        return this.zoneId;
    }

    public boolean useDaylightTime() {
        return false;
    }

    public boolean inDaylightTime(Date date) {
        return false;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[GmtTimeZone id=\"");
        stringBuilder.append(this.zoneId);
        stringBuilder.append("\",offset=");
        stringBuilder.append(this.offset);
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    public int hashCode() {
        return this.offset;
    }

    public boolean equals(Object other) {
        boolean z = false;
        if (!(other instanceof GmtTimeZone)) {
            return false;
        }
        if (this.zoneId == ((GmtTimeZone) other).zoneId) {
            z = true;
        }
        return z;
    }
}

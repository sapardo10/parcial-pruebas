package de.danoeh.antennapod.core.util.flattr;

import java.util.Calendar;

public class FlattrStatus {
    private static final int STATUS_FLATTRED = 2;
    public static final int STATUS_QUEUE = 1;
    private static final int STATUS_UNFLATTERED = 0;
    private Calendar lastFlattred;
    private int status;

    public FlattrStatus() {
        this.status = 0;
        this.status = 0;
        this.lastFlattred = Calendar.getInstance();
    }

    public FlattrStatus(long status) {
        this.status = 0;
        this.lastFlattred = Calendar.getInstance();
        fromLong(status);
    }

    public void setFlattred() {
        this.status = 2;
        this.lastFlattred = Calendar.getInstance();
    }

    public void setUnflattred() {
        this.status = 0;
    }

    public boolean getUnflattred() {
        return this.status == 0;
    }

    public void setFlattrQueue() {
        if (flattrable()) {
            this.status = 1;
        }
    }

    private void fromLong(long status) {
        if (status != 0) {
            if (status != 1) {
                this.status = 2;
                this.lastFlattred.setTimeInMillis(status);
                return;
            }
        }
        this.status = (int) status;
    }

    public long toLong() {
        int i = this.status;
        if (i != 0) {
            if (i != 1) {
                return this.lastFlattred.getTimeInMillis();
            }
        }
        return (long) this.status;
    }

    public boolean flattrable() {
        Calendar firstOfMonth = Calendar.getInstance();
        firstOfMonth.set(5, Calendar.getInstance().getActualMinimum(5));
        int i = this.status;
        if (i != 0) {
            if (i != 2 || !firstOfMonth.after(this.lastFlattred)) {
                return false;
            }
        }
        return true;
    }

    public boolean getFlattrQueue() {
        return this.status == 1;
    }
}

package com.robotium.solo;

import android.app.Activity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.TimePicker;

class Setter {
    private final int CLOSED = 0;
    private final int OPENED = 1;
    private final ActivityUtils activityUtils;
    private final Clicker clicker;
    private final Getter getter;
    private final Waiter waiter;

    public Setter(ActivityUtils activityUtils, Getter getter, Clicker clicker, Waiter waiter) {
        this.activityUtils = activityUtils;
        this.getter = getter;
        this.clicker = clicker;
        this.waiter = waiter;
    }

    public void setDatePicker(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        if (datePicker != null) {
            Activity activity = this.activityUtils.getCurrentActivity(false);
            if (activity != null) {
                final DatePicker datePicker2 = datePicker;
                final int i = year;
                final int i2 = monthOfYear;
                final int i3 = dayOfMonth;
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            datePicker2.updateDate(i, i2, i3);
                        } catch (Exception e) {
                        }
                    }
                });
            }
        }
    }

    public void setTimePicker(final TimePicker timePicker, final int hour, final int minute) {
        if (timePicker != null) {
            Activity activity = this.activityUtils.getCurrentActivity(false);
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            timePicker.setCurrentHour(Integer.valueOf(hour));
                            timePicker.setCurrentMinute(Integer.valueOf(minute));
                        } catch (Exception e) {
                        }
                    }
                });
            }
        }
    }

    public void setProgressBar(final ProgressBar progressBar, final int progress) {
        if (progressBar != null) {
            Activity activity = this.activityUtils.getCurrentActivity(false);
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            progressBar.setProgress(progress);
                        } catch (Exception e) {
                        }
                    }
                });
            }
        }
    }

    public void setSlidingDrawer(final SlidingDrawer slidingDrawer, final int status) {
        if (slidingDrawer != null) {
            Activity activity = this.activityUtils.getCurrentActivity(false);
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            switch (status) {
                                case 0:
                                    slidingDrawer.close();
                                    break;
                                case 1:
                                    slidingDrawer.open();
                                    break;
                                default:
                                    break;
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        }
    }

    public void setNavigationDrawer(int status) {
        View homeView = this.getter.getView("home", 0);
        final View leftDrawer = this.getter.getView("left_drawer", 0);
        switch (status) {
            case 0:
                if (leftDrawer != null && homeView != null && leftDrawer.isShown()) {
                    this.clicker.clickOnScreen(homeView);
                    break;
                }
                break;
                break;
            case 1:
                if (!(leftDrawer == null || homeView == null)) {
                    try {
                        if (!leftDrawer.isShown()) {
                            this.clicker.clickOnScreen(homeView);
                            this.waiter.waitForCondition(new Condition() {
                                public boolean isSatisfied() {
                                    return leftDrawer.isShown();
                                }
                            }, Timeout.getSmallTimeout());
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
                break;
            default:
                break;
        }
    }
}

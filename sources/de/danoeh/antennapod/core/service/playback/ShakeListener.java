package de.danoeh.antennapod.core.service.playback;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

class ShakeListener implements SensorEventListener {
    private static final String TAG = ShakeListener.class.getSimpleName();
    private Sensor mAccelerometer;
    private final Context mContext;
    private SensorManager mSensorMgr;
    private final SleepTimer mSleepTimer;

    public ShakeListener(Context context, SleepTimer sleepTimer) {
        this.mContext = context;
        this.mSleepTimer = sleepTimer;
        resume();
    }

    private void resume() {
        this.mSensorMgr = (SensorManager) this.mContext.getSystemService("sensor");
        SensorManager sensorManager = this.mSensorMgr;
        if (sensorManager != null) {
            this.mAccelerometer = sensorManager.getDefaultSensor(1);
            if (!this.mSensorMgr.registerListener(this, this.mAccelerometer, 2)) {
                this.mSensorMgr.unregisterListener(this);
                throw new UnsupportedOperationException("Accelerometer not supported");
            }
            return;
        }
        throw new UnsupportedOperationException("Sensors not supported");
    }

    public void pause() {
        SensorManager sensorManager = this.mSensorMgr;
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            this.mSensorMgr = null;
        }
    }

    public void onSensorChanged(SensorEvent event) {
        float gX = event.values[0] / 9.80665f;
        float gY = event.values[1] / 9.80665f;
        float gZ = event.values[2] / 9.80665f;
        double gForce = Math.sqrt((double) (((gX * gX) + (gY * gY)) + (gZ * gZ)));
        if (gForce > 2.25d) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Detected shake ");
            stringBuilder.append(gForce);
            Log.d(str, stringBuilder.toString());
            this.mSleepTimer.onShake();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}

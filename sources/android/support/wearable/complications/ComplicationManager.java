package android.support.wearable.complications;

import android.annotation.TargetApi;
import android.os.RemoteException;
import android.util.Log;

@TargetApi(24)
public class ComplicationManager {
    private static final String TAG = "ComplicationManager";
    private final IComplicationManager mService;

    public ComplicationManager(IComplicationManager service) {
        this.mService = service;
    }

    public void updateComplicationData(int complicationId, ComplicationData data) {
        if (data.getType() != 1) {
            if (data.getType() != 2) {
                try {
                    this.mService.updateComplicationData(complicationId, data);
                    return;
                } catch (RemoteException e) {
                    Log.w(TAG, "Failed to send complication data.", e);
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Cannot send data of TYPE_NOT_CONFIGURED or TYPE_EMPTY. Use TYPE_NO_DATA instead.");
    }

    public void noUpdateRequired(int complicationId) {
        try {
            this.mService.updateComplicationData(complicationId, null);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to send complication data.", e);
        }
    }
}

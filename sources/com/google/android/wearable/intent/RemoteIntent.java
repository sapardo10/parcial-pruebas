package com.google.android.wearable.intent;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

@TargetApi(24)
public class RemoteIntent {
    public static final String ACTION_REMOTE_INTENT = "com.google.android.wearable.intent.action.REMOTE_INTENT";
    public static final String EXTRA_INTENT = "com.google.android.wearable.intent.extra.INTENT";
    public static final String EXTRA_NODE_ID = "com.google.android.wearable.intent.extra.NODE_ID";
    public static final String EXTRA_RESULT_RECEIVER = "com.google.android.wearable.intent.extra.RESULT_RECEIVER";
    public static final int RESULT_FAILED = 1;
    public static final int RESULT_OK = 0;

    public static void startRemoteActivity(Context context, Intent intent, @Nullable ResultReceiver resultReceiver) {
        startRemoteActivity(context, intent, resultReceiver, null);
    }

    public static void startRemoteActivity(Context context, Intent intent, @Nullable ResultReceiver resultReceiver, @Nullable String nodeId) {
        if (!"android.intent.action.VIEW".equals(intent.getAction())) {
            throw new IllegalArgumentException("Only android.intent.action.VIEW action is currently supported for starting a remote activity");
        } else if (intent.getData() != null) {
            if (intent.getCategories() != null) {
                if (intent.getCategories().contains("android.intent.category.BROWSABLE")) {
                    context.sendBroadcast(new Intent(ACTION_REMOTE_INTENT).setPackage("com.google.android.wearable.app").putExtra(EXTRA_INTENT, intent).putExtra(EXTRA_NODE_ID, nodeId).putExtra(EXTRA_RESULT_RECEIVER, getResultReceiverForSending(resultReceiver)));
                    return;
                }
            }
            throw new IllegalArgumentException("The category android.intent.category.BROWSABLE must be present on the intent");
        } else {
            throw new IllegalArgumentException("Data Uri is required when starting a remote activity");
        }
    }

    private static ResultReceiver getResultReceiverForSending(ResultReceiver receiver) {
        if (receiver == null) {
            return null;
        }
        Parcel parcel = Parcel.obtain();
        receiver.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        ResultReceiver receiverForSending = (ResultReceiver) ResultReceiver.CREATOR.createFromParcel(parcel);
        parcel.recycle();
        return receiverForSending;
    }
}

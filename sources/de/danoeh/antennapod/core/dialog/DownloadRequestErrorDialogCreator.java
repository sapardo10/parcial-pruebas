package de.danoeh.antennapod.core.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog.Builder;
import de.danoeh.antennapod.core.C0734R;

public class DownloadRequestErrorDialogCreator {
    private DownloadRequestErrorDialogCreator() {
    }

    public static void newRequestErrorDialog(Context context, String errorMessage) {
        Builder builder = new Builder(context);
        Builder title = builder.setNeutralButton(17039370, C0736xcbc2702c.INSTANCE).setTitle(C0734R.string.download_error_request_error);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(context.getString(C0734R.string.download_request_error_dialog_message_prefix));
        stringBuilder.append(errorMessage);
        title.setMessage(stringBuilder.toString());
        builder.create().show();
    }
}

package de.danoeh.antennapod.activity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$StorageErrorActivity$J3T72D4w9TR8XeBCKkvMCx5xA4I implements SingleButtonCallback {
    private final /* synthetic */ StorageErrorActivity f$0;

    public /* synthetic */ -$$Lambda$StorageErrorActivity$J3T72D4w9TR8XeBCKkvMCx5xA4I(StorageErrorActivity storageErrorActivity) {
        this.f$0 = storageErrorActivity;
    }

    public final void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
        this.f$0.requestPermission();
    }
}

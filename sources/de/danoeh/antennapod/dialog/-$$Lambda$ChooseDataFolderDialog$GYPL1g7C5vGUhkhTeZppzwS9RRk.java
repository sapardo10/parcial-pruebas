package de.danoeh.antennapod.dialog;

import android.view.View;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ListCallbackSingleChoice;
import de.danoeh.antennapod.dialog.ChooseDataFolderDialog.RunnableWithString;
import java.util.List;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChooseDataFolderDialog$GYPL1g7C5vGUhkhTeZppzwS9RRk implements ListCallbackSingleChoice {
    private final /* synthetic */ List f$0;
    private final /* synthetic */ RunnableWithString f$1;

    public /* synthetic */ -$$Lambda$ChooseDataFolderDialog$GYPL1g7C5vGUhkhTeZppzwS9RRk(List list, RunnableWithString runnableWithString) {
        this.f$0 = list;
        this.f$1 = runnableWithString;
    }

    public final boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
        return this.f$1.run((String) this.f$0.get(i));
    }
}

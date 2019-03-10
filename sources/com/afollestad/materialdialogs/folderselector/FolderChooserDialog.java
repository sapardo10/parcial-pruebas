package com.afollestad.materialdialogs.folderselector;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.InputCallback;
import com.afollestad.materialdialogs.MaterialDialog.ListCallback;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.afollestad.materialdialogs.commons.C0502R;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FolderChooserDialog extends DialogFragment implements ListCallback {
    private static final String DEFAULT_TAG = "[MD_FOLDER_SELECTOR]";
    private boolean canGoUp = true;
    private FolderCallback mCallback;
    private File[] parentContents;
    private File parentFolder;

    public static class Builder implements Serializable {
        protected boolean mAllowNewFolder;
        @StringRes
        protected int mCancelButton = 17039360;
        @StringRes
        protected int mChooseButton = C0502R.string.md_choose_label;
        @NonNull
        protected final transient AppCompatActivity mContext;
        protected String mGoUpLabel = "...";
        protected String mInitialPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        @StringRes
        protected int mNewFolderButton;
        protected String mTag;

        public <ActivityType extends AppCompatActivity & FolderCallback> Builder(@NonNull ActivityType context) {
            this.mContext = context;
        }

        @NonNull
        public Builder chooseButton(@StringRes int text) {
            this.mChooseButton = text;
            return this;
        }

        @NonNull
        public Builder cancelButton(@StringRes int text) {
            this.mCancelButton = text;
            return this;
        }

        @NonNull
        public Builder goUpLabel(String text) {
            this.mGoUpLabel = text;
            return this;
        }

        @NonNull
        public Builder allowNewFolder(boolean allow, @StringRes int buttonLabel) {
            this.mAllowNewFolder = allow;
            if (buttonLabel == 0) {
                buttonLabel = C0502R.string.new_folder;
            }
            this.mNewFolderButton = buttonLabel;
            return this;
        }

        @NonNull
        public Builder initialPath(@Nullable String initialPath) {
            if (initialPath == null) {
                initialPath = File.separator;
            }
            this.mInitialPath = initialPath;
            return this;
        }

        @NonNull
        public Builder tag(@Nullable String tag) {
            if (tag == null) {
                tag = FolderChooserDialog.DEFAULT_TAG;
            }
            this.mTag = tag;
            return this;
        }

        @NonNull
        public FolderChooserDialog build() {
            FolderChooserDialog dialog = new FolderChooserDialog();
            Bundle args = new Bundle();
            args.putSerializable("builder", this);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        public FolderChooserDialog show() {
            FolderChooserDialog dialog = build();
            dialog.show(this.mContext);
            return dialog;
        }
    }

    public interface FolderCallback {
        void onFolderSelection(@NonNull FolderChooserDialog folderChooserDialog, @NonNull File file);
    }

    private static class FolderSorter implements Comparator<File> {
        private FolderSorter() {
        }

        public int compare(File lhs, File rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }

    /* renamed from: com.afollestad.materialdialogs.folderselector.FolderChooserDialog$1 */
    class C09291 implements SingleButtonCallback {
        C09291() {
        }

        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            dialog.dismiss();
        }
    }

    /* renamed from: com.afollestad.materialdialogs.folderselector.FolderChooserDialog$2 */
    class C09302 implements SingleButtonCallback {
        C09302() {
        }

        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            dialog.dismiss();
            FolderCallback access$200 = FolderChooserDialog.this.mCallback;
            FolderChooserDialog folderChooserDialog = FolderChooserDialog.this;
            access$200.onFolderSelection(folderChooserDialog, folderChooserDialog.parentFolder);
        }
    }

    /* renamed from: com.afollestad.materialdialogs.folderselector.FolderChooserDialog$3 */
    class C09313 implements SingleButtonCallback {
        C09313() {
        }

        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            FolderChooserDialog.this.createNewFolder();
        }
    }

    /* renamed from: com.afollestad.materialdialogs.folderselector.FolderChooserDialog$4 */
    class C09324 implements InputCallback {
        C09324() {
        }

        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
            File newFi = new File(FolderChooserDialog.this.parentFolder, input.toString());
            if (newFi.mkdir()) {
                FolderChooserDialog.this.reload();
                return;
            }
            Context activity = FolderChooserDialog.this.getActivity();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unable to create folder ");
            stringBuilder.append(newFi.getAbsolutePath());
            stringBuilder.append(", make sure you have the WRITE_EXTERNAL_STORAGE permission.");
            Toast.makeText(activity, stringBuilder.toString(), 0).show();
        }
    }

    String[] getContentsArray() {
        File[] fileArr = this.parentContents;
        if (fileArr != null) {
            int length = fileArr.length;
            boolean z = this.canGoUp;
            String[] results = new String[(length + z)];
            if (z) {
                results[0] = "...";
            }
            int i = 0;
            while (i < this.parentContents.length) {
                results[this.canGoUp ? i + 1 : i] = this.parentContents[i].getName();
                i++;
            }
            return results;
        } else if (!this.canGoUp) {
            return new String[0];
        } else {
            return new String[]{getBuilder().mGoUpLabel};
        }
    }

    File[] listFiles() {
        File[] contents = this.parentFolder.listFiles();
        List<File> results = new ArrayList();
        if (contents == null) {
            return null;
        }
        for (File fi : contents) {
            if (fi.isDirectory()) {
                results.add(fi);
            }
        }
        Collections.sort(results, new FolderSorter());
        return (File[]) results.toArray(new File[results.size()]);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.READ_EXTERNAL_STORAGE") != 0) {
                return new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity()).title(C0502R.string.md_error_label).content(C0502R.string.md_storage_perm_error).positiveText(17039370).build();
            }
        }
        if (getArguments() == null || !getArguments().containsKey("builder")) {
            throw new IllegalStateException("You must create a FolderChooserDialog using the Builder.");
        }
        if (!getArguments().containsKey("current_path")) {
            getArguments().putString("current_path", getBuilder().mInitialPath);
        }
        this.parentFolder = new File(getArguments().getString("current_path"));
        this.parentContents = listFiles();
        com.afollestad.materialdialogs.MaterialDialog.Builder builder = new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity()).title(this.parentFolder.getAbsolutePath()).items(getContentsArray()).itemsCallback(this).onPositive(new C09302()).onNegative(new C09291()).autoDismiss(false).positiveText(getBuilder().mChooseButton).negativeText(getBuilder().mCancelButton);
        if (getBuilder().mAllowNewFolder) {
            builder.neutralText(getBuilder().mNewFolderButton);
            builder.onNeutral(new C09313());
        }
        return builder.build();
    }

    private void createNewFolder() {
        new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity()).title(getBuilder().mNewFolderButton).input(0, 0, false, new C09324()).show();
    }

    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence s) {
        boolean z = true;
        if (this.canGoUp && i == 0) {
            this.parentFolder = this.parentFolder.getParentFile();
            if (this.parentFolder.getAbsolutePath().equals("/storage/emulated")) {
                this.parentFolder = this.parentFolder.getParentFile();
            }
            if (this.parentFolder.getParent() == null) {
                z = false;
            }
            this.canGoUp = z;
        } else {
            this.parentFolder = this.parentContents[this.canGoUp ? i - 1 : i];
            this.canGoUp = true;
            if (this.parentFolder.getAbsolutePath().equals("/storage/emulated")) {
                this.parentFolder = Environment.getExternalStorageDirectory();
            }
        }
        reload();
    }

    private void reload() {
        this.parentContents = listFiles();
        MaterialDialog dialog = (MaterialDialog) getDialog();
        dialog.setTitle(this.parentFolder.getAbsolutePath());
        getArguments().putString("current_path", this.parentFolder.getAbsolutePath());
        dialog.setItems(getContentsArray());
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mCallback = (FolderCallback) activity;
    }

    public void show(FragmentActivity context) {
        String tag = getBuilder().mTag;
        Fragment frag = context.getSupportFragmentManager().findFragmentByTag(tag);
        if (frag != null) {
            ((DialogFragment) frag).dismiss();
            context.getSupportFragmentManager().beginTransaction().remove(frag).commit();
        }
        show(context.getSupportFragmentManager(), tag);
    }

    @NonNull
    private Builder getBuilder() {
        return (Builder) getArguments().getSerializable("builder");
    }
}

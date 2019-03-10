package com.afollestad.materialdialogs.folderselector;

import android.app.Activity;
import android.app.Dialog;
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
import android.webkit.MimeTypeMap;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ListCallback;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.afollestad.materialdialogs.commons.C0502R;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileChooserDialog extends DialogFragment implements ListCallback {
    private static final String DEFAULT_TAG = "[MD_FILE_SELECTOR]";
    private boolean canGoUp = true;
    private FileCallback mCallback;
    private File[] parentContents;
    private File parentFolder;

    public static class Builder implements Serializable {
        @StringRes
        protected int mCancelButton = 17039360;
        @NonNull
        protected final transient AppCompatActivity mContext;
        protected String[] mExtensions;
        protected String mGoUpLabel = "...";
        protected String mInitialPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        protected String mMimeType = null;
        protected String mTag;

        public <ActivityType extends AppCompatActivity & FileCallback> Builder(@NonNull ActivityType context) {
            this.mContext = context;
        }

        @NonNull
        public Builder cancelButton(@StringRes int text) {
            this.mCancelButton = text;
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
        public Builder mimeType(@Nullable String type) {
            this.mMimeType = type;
            return this;
        }

        @NonNull
        public Builder extensionsFilter(@Nullable String... extensions) {
            this.mExtensions = extensions;
            return this;
        }

        @NonNull
        public Builder tag(@Nullable String tag) {
            if (tag == null) {
                tag = FileChooserDialog.DEFAULT_TAG;
            }
            this.mTag = tag;
            return this;
        }

        @NonNull
        public Builder goUpLabel(String text) {
            this.mGoUpLabel = text;
            return this;
        }

        @NonNull
        public FileChooserDialog build() {
            FileChooserDialog dialog = new FileChooserDialog();
            Bundle args = new Bundle();
            args.putSerializable("builder", this);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        public FileChooserDialog show() {
            FileChooserDialog dialog = build();
            dialog.show(this.mContext);
            return dialog;
        }
    }

    public interface FileCallback {
        void onFileSelection(@NonNull FileChooserDialog fileChooserDialog, @NonNull File file);
    }

    private static class FileSorter implements Comparator<File> {
        private FileSorter() {
        }

        public int compare(File lhs, File rhs) {
            if (lhs.isDirectory() && !rhs.isDirectory()) {
                return -1;
            }
            if (lhs.isDirectory() || !rhs.isDirectory()) {
                return lhs.getName().compareTo(rhs.getName());
            }
            return 1;
        }
    }

    /* renamed from: com.afollestad.materialdialogs.folderselector.FileChooserDialog$1 */
    class C09281 implements SingleButtonCallback {
        C09281() {
        }

        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            dialog.dismiss();
        }
    }

    CharSequence[] getContentsArray() {
        File[] fileArr = this.parentContents;
        if (fileArr != null) {
            int length = fileArr.length;
            boolean z = this.canGoUp;
            String[] results = new String[(length + z)];
            if (z) {
                results[0] = getBuilder().mGoUpLabel;
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

    File[] listFiles(@Nullable String mimeType, @Nullable String[] extensions) {
        String str = mimeType;
        String[] strArr = extensions;
        File[] contents = this.parentFolder.listFiles();
        List<File> results = new ArrayList();
        if (contents == null) {
            return null;
        }
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        for (File fi : contents) {
            if (fi.isDirectory()) {
                results.add(fi);
            } else if (strArr != null) {
                boolean found = false;
                for (String ext : strArr) {
                    if (fi.getName().toLowerCase().contains(ext.toLowerCase())) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    results.add(fi);
                }
            } else if (str != null) {
                if (fileIsMimeType(fi, str, mimeTypeMap)) {
                    results.add(fi);
                }
            }
        }
        Collections.sort(results, new FileSorter());
        return (File[]) results.toArray(new File[results.size()]);
    }

    boolean fileIsMimeType(File file, String mimeType, MimeTypeMap mimeTypeMap) {
        if (mimeType != null) {
            if (!mimeType.equals("*/*")) {
                String filename = file.toURI().toString();
                int dotPos = filename.lastIndexOf(46);
                if (dotPos == -1) {
                    return false;
                }
                String fileExtension = filename.substring(dotPos + 1);
                if (fileExtension.endsWith("json")) {
                    return mimeType.startsWith("application/json");
                }
                String fileType = mimeTypeMap.getMimeTypeFromExtension(fileExtension);
                if (fileType == null) {
                    return false;
                }
                if (fileType.equals(mimeType)) {
                    return true;
                }
                int mimeTypeDelimiter = mimeType.lastIndexOf(47);
                if (mimeTypeDelimiter == -1) {
                    return false;
                }
                String mimeTypeMainType = mimeType.substring(0, mimeTypeDelimiter);
                if (!mimeType.substring(mimeTypeDelimiter + 1).equals("*")) {
                    return false;
                }
                int fileTypeDelimiter = fileType.lastIndexOf(47);
                if (fileTypeDelimiter == -1) {
                    return false;
                }
                if (fileType.substring(0, fileTypeDelimiter).equals(mimeTypeMainType)) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.READ_EXTERNAL_STORAGE") != 0) {
                return new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity()).title(C0502R.string.md_error_label).content(C0502R.string.md_storage_perm_error).positiveText(17039370).build();
            }
        }
        if (getArguments() == null || !getArguments().containsKey("builder")) {
            throw new IllegalStateException("You must create a FileChooserDialog using the Builder.");
        }
        if (!getArguments().containsKey("current_path")) {
            getArguments().putString("current_path", getBuilder().mInitialPath);
        }
        this.parentFolder = new File(getArguments().getString("current_path"));
        this.parentContents = listFiles(getBuilder().mMimeType, getBuilder().mExtensions);
        return new com.afollestad.materialdialogs.MaterialDialog.Builder(getActivity()).title(this.parentFolder.getAbsolutePath()).items(getContentsArray()).itemsCallback(this).onNegative(new C09281()).autoDismiss(false).negativeText(getBuilder().mCancelButton).build();
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
        if (this.parentFolder.isFile()) {
            this.mCallback.onFileSelection(this, this.parentFolder);
            dismiss();
            return;
        }
        this.parentContents = listFiles(getBuilder().mMimeType, getBuilder().mExtensions);
        MaterialDialog dialog = (MaterialDialog) getDialog();
        dialog.setTitle(this.parentFolder.getAbsolutePath());
        getArguments().putString("current_path", this.parentFolder.getAbsolutePath());
        dialog.setItems(getContentsArray());
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mCallback = (FileCallback) activity;
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
    public String getInitialPath() {
        return getBuilder().mInitialPath;
    }

    @NonNull
    private Builder getBuilder() {
        return (Builder) getArguments().getSerializable("builder");
    }
}

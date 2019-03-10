package android.support.v4.app;

import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import java.io.FileDescriptor;
import java.io.PrintWriter;

class FragmentActivity$HostCallbacks extends FragmentHostCallback<FragmentActivity> {
    final /* synthetic */ FragmentActivity this$0;

    public FragmentActivity$HostCallbacks(FragmentActivity this$0) {
        this.this$0 = this$0;
        super(this$0);
    }

    public void onDump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        this.this$0.dump(prefix, fd, writer, args);
    }

    public boolean onShouldSaveFragmentState(Fragment fragment) {
        return this.this$0.isFinishing() ^ 1;
    }

    public LayoutInflater onGetLayoutInflater() {
        return this.this$0.getLayoutInflater().cloneInContext(this.this$0);
    }

    public FragmentActivity onGetHost() {
        return this.this$0;
    }

    public void onSupportInvalidateOptionsMenu() {
        this.this$0.supportInvalidateOptionsMenu();
    }

    public void onStartActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        this.this$0.startActivityFromFragment(fragment, intent, requestCode);
    }

    public void onStartActivityFromFragment(Fragment fragment, Intent intent, int requestCode, @Nullable Bundle options) {
        this.this$0.startActivityFromFragment(fragment, intent, requestCode, options);
    }

    public void onStartIntentSenderFromFragment(Fragment fragment, IntentSender intent, int requestCode, @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws SendIntentException {
        this.this$0.startIntentSenderFromFragment(fragment, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }

    public void onRequestPermissionsFromFragment(@NonNull Fragment fragment, @NonNull String[] permissions, int requestCode) {
        this.this$0.requestPermissionsFromFragment(fragment, permissions, requestCode);
    }

    public boolean onShouldShowRequestPermissionRationale(@NonNull String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(this.this$0, permission);
    }

    public boolean onHasWindowAnimations() {
        return this.this$0.getWindow() != null;
    }

    public int onGetWindowAnimations() {
        Window w = this.this$0.getWindow();
        return w == null ? 0 : w.getAttributes().windowAnimations;
    }

    public void onAttachFragment(Fragment fragment) {
        this.this$0.onAttachFragment(fragment);
    }

    @Nullable
    public View onFindViewById(int id) {
        return this.this$0.findViewById(id);
    }

    public boolean onHasView() {
        Window w = this.this$0.getWindow();
        return (w == null || w.peekDecorView() == null) ? false : true;
    }
}

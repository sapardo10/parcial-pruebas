package de.danoeh.antennapod.preferences;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bytehamster.lib.preferencesearch.SearchConfiguration;
import com.bytehamster.lib.preferencesearch.SearchPreference;
import de.danoeh.antennapod.CrashReportWriter;
import de.danoeh.antennapod.activity.DirectoryChooserActivity;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.activity.PreferenceActivity;
import de.danoeh.antennapod.activity.PreferenceActivity.MainFragment;
import de.danoeh.antennapod.asynctask.ExportWorker;
import de.danoeh.antennapod.core.export.ExportWriter;
import de.danoeh.antennapod.core.preferences.GpodnetPreferences;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.GpodnetSyncService;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.core.util.flattr.FlattrUtils;
import de.danoeh.antennapod.core.util.gui.PictureInPictureUtil;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.dialog.AutoFlattrPreferenceDialog.AutoFlattrPreferenceDialogInterface;
import de.danoeh.antennapod.dialog.ChooseDataFolderDialog;
import de.danoeh.antennapod.dialog.ChooseDataFolderDialog.RunnableWithString;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class PreferenceController implements OnSharedPreferenceChangeListener {
    private static final String[] EXTERNAL_STORAGE_PERMISSIONS = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final String IMPORT_EXPORT = "importExport";
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 41;
    private static final String PREF_ABOUT = "prefAbout";
    private static final String PREF_AUTO_FLATTR_PREFS = "prefAutoFlattrPrefs";
    private static final String PREF_CHOOSE_DATA_DIR = "prefChooseDataDir";
    private static final String PREF_EXPANDED_NOTIFICATION = "prefExpandNotify";
    private static final String PREF_FAQ = "prefFaq";
    private static final String PREF_FLATTR_AUTH = "pref_flattr_authenticate";
    private static final String PREF_FLATTR_REVOKE = "prefRevokeAccess";
    private static final String PREF_GPODNET_FORCE_FULL_SYNC = "pref_gpodnet_force_full_sync";
    private static final String PREF_GPODNET_HOSTNAME = "pref_gpodnet_hostname";
    private static final String PREF_GPODNET_LOGIN = "pref_gpodnet_authenticate";
    private static final String PREF_GPODNET_LOGOUT = "pref_gpodnet_logout";
    private static final String PREF_GPODNET_NOTIFICATIONS = "pref_gpodnet_notifications";
    private static final String PREF_GPODNET_SETLOGIN_INFORMATION = "pref_gpodnet_setlogin_information";
    private static final String PREF_GPODNET_SYNC = "pref_gpodnet_sync";
    private static final String PREF_HTML_EXPORT = "prefHtmlExport";
    private static final String PREF_KNOWN_ISSUES = "prefKnownIssues";
    private static final String PREF_OPML_EXPORT = "prefOpmlExport";
    private static final String PREF_OPML_IMPORT = "prefOpmlImport";
    private static final String PREF_PLAYBACK_FAST_FORWARD_DELTA_LAUNCHER = "prefPlaybackFastForwardDeltaLauncher";
    private static final String PREF_PLAYBACK_REWIND_DELTA_LAUNCHER = "prefPlaybackRewindDeltaLauncher";
    private static final String PREF_PLAYBACK_SPEED_LAUNCHER = "prefPlaybackSpeedLauncher";
    private static final String PREF_PROXY = "prefProxy";
    private static final String PREF_SCREEN_AUTODL = "prefAutoDownloadSettings";
    private static final String PREF_SCREEN_FLATTR = "prefFlattrSettings";
    private static final String PREF_SCREEN_GPODDER = "prefGpodderSettings";
    private static final String PREF_SCREEN_INTEGRATIONS = "prefScreenIntegrations";
    private static final String PREF_SCREEN_NETWORK = "prefScreenNetwork";
    private static final String PREF_SCREEN_PLAYBACK = "prefScreenPlayback";
    private static final String PREF_SCREEN_STORAGE = "prefScreenStorage";
    private static final String PREF_SCREEN_USER_INTERFACE = "prefScreenInterface";
    private static final String PREF_SEND_CRASH_REPORT = "prefSendCrashReport";
    private static final String STATISTICS = "statistics";
    private static final String TAG = "PreferenceController";
    private Disposable disposable;
    private final OnSharedPreferenceChangeListener gpoddernetListener = new -$$Lambda$PreferenceController$C4xtCu7PzYfV7ZM_WxNpJNSwqXE();
    private CheckBoxPreference[] selectedNetworks;
    private final PreferenceUI ui;

    public interface PreferenceUI {
        Preference findPreference(CharSequence charSequence);

        AppCompatActivity getActivity();

        PreferenceFragmentCompat getFragment();

        PreferenceScreen getPreferenceScreen();

        void setFragment(PreferenceFragmentCompat preferenceFragmentCompat);
    }

    /* renamed from: de.danoeh.antennapod.preferences.PreferenceController$1 */
    class C10811 implements AutoFlattrPreferenceDialogInterface {
        C10811() {
        }

        public void onCancelled() {
        }

        public void onConfirmed(boolean autoFlattrEnabled, float autoFlattrValue) {
            UserPreferences.setAutoFlattrSettings(autoFlattrEnabled, autoFlattrValue);
            PreferenceController.this.checkFlattrItemVisibility();
        }
    }

    /* renamed from: de.danoeh.antennapod.preferences.PreferenceController$3 */
    class C10833 extends RunnableWithString {
        C10833() {
        }

        public void run(String folder) {
            UserPreferences.setDataFolder(folder);
            PreferenceController.this.setDataFolderText();
        }
    }

    public static /* synthetic */ void lambda$new$0(PreferenceController preferenceController, SharedPreferences sharedPreferences, String key) {
        if (GpodnetPreferences.PREF_LAST_SYNC_ATTEMPT_TIMESTAMP.equals(key)) {
            preferenceController.updateLastGpodnetSyncReport(GpodnetPreferences.getLastSyncAttemptResult(), GpodnetPreferences.getLastSyncAttemptTimestamp());
        }
    }

    public PreferenceController(PreferenceUI ui) {
        this.ui = ui;
        PreferenceManager.getDefaultSharedPreferences(ui.getActivity().getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    public void onCreate(int screen) {
        switch (screen) {
            case R.xml.preferences:
                setupMainScreen();
                return;
            case R.xml.preferences_autodownload:
                setupAutoDownloadScreen();
                buildAutodownloadSelectedNetworksPreference();
                setSelectedNetworksEnabled(UserPreferences.isEnableAutodownloadWifiFilter());
                buildEpisodeCleanupPreference();
                return;
            case R.xml.preferences_flattr:
                setupFlattrScreen();
                return;
            case R.xml.preferences_gpodder:
                setupGpodderScreen();
                return;
            case R.xml.preferences_integrations:
                setupIntegrationsScreen();
                return;
            case R.xml.preferences_network:
                setupNetworkScreen();
                return;
            case R.xml.preferences_playback:
                setupPlaybackScreen();
                PreferenceControllerFlavorHelper.setupFlavoredUI(this.ui);
                buildSmartMarkAsPlayedPreference();
                return;
            case R.xml.preferences_storage:
                setupStorageScreen();
                return;
            case R.xml.preferences_user_interface:
                setupInterfaceScreen();
                return;
            default:
                return;
        }
    }

    private void setupInterfaceScreen() {
        Activity activity = this.ui.getActivity();
        if (VERSION.SDK_INT < 16) {
            this.ui.findPreference("prefExpandNotify").setEnabled(false);
            this.ui.findPreference("prefExpandNotify").setOnPreferenceClickListener(new -$$Lambda$PreferenceController$9lubbDLWUEbJ6cOEw2gVWFpkENg(activity));
        }
        this.ui.findPreference(UserPreferences.PREF_THEME).setOnPreferenceChangeListener(new -$$Lambda$PreferenceController$i5akq6ullj5k-wtK3H_NZWWWo14(activity));
        this.ui.findPreference(UserPreferences.PREF_HIDDEN_DRAWER_ITEMS).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$y49dJ9vmvQXNP7ed5UF8L8mtjdg());
        this.ui.findPreference(UserPreferences.PREF_COMPACT_NOTIFICATION_BUTTONS).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$15t1PAKba7ruLaeg5KL2CHE3r2w());
        this.ui.findPreference(UserPreferences.PREF_BACK_BUTTON_BEHAVIOR).setOnPreferenceChangeListener(new -$$Lambda$PreferenceController$Jrw8jlvSP9VDs0LtFIrWc1u7DDI());
        if (VERSION.SDK_INT >= 26) {
            this.ui.findPreference("prefExpandNotify").setVisible(false);
        }
    }

    static /* synthetic */ boolean lambda$setupInterfaceScreen$2(Activity activity, Preference preference, Object newValue) {
        Intent i = new Intent(activity, MainActivity.class);
        i.setFlags(268468224);
        activity.finish();
        activity.startActivity(i);
        return true;
    }

    public static /* synthetic */ boolean lambda$setupInterfaceScreen$7(PreferenceController preferenceController, Preference preference, Object newValue) {
        if (!newValue.equals(DownloadRequester.REQUEST_ARG_PAGE_NR)) {
            return true;
        }
        Context context = preferenceController.ui.getActivity();
        CharSequence[] navTitles = context.getResources().getStringArray(R.array.back_button_go_to_pages);
        String[] navTags = context.getResources().getStringArray(R.array.back_button_go_to_pages_tags);
        String[] choice = new String[]{UserPreferences.getBackButtonGoToPage()};
        Builder builder = new Builder(context);
        builder.setTitle((int) R.string.back_button_go_to_page_title);
        builder.setSingleChoiceItems(navTitles, ArrayUtils.indexOf(navTags, UserPreferences.getBackButtonGoToPage()), new -$$Lambda$PreferenceController$3jGL5gJm6chygt2KjG6mY73_f7Y(choice, navTags));
        builder.setPositiveButton((int) R.string.confirm_label, new -$$Lambda$PreferenceController$7VECRdHWzyj-KSr1NCP-FAPC_bc(choice));
        builder.setNegativeButton((int) R.string.cancel_label, null);
        builder.create().show();
        return true;
    }

    static /* synthetic */ void lambda$null$5(String[] choice, String[] navTags, DialogInterface dialogInterface, int i) {
        if (i >= 0) {
            choice[0] = navTags[i];
        }
    }

    private void setupStorageScreen() {
        Activity activity = this.ui.getActivity();
        this.ui.findPreference(IMPORT_EXPORT).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$imX0jHegyk_2PS98RnesEnxurFA(activity));
        this.ui.findPreference(PREF_OPML_EXPORT).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$bn5g3_7-4MY11ahR6bWIlIIXheE());
        this.ui.findPreference(PREF_HTML_EXPORT).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$gB9WznxHBKQuHelIWiZMAwkCDRc());
        this.ui.findPreference(PREF_OPML_IMPORT).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$vPbwcskK_zJYL45KW2WZJezo0FM(activity));
        this.ui.findPreference(PREF_CHOOSE_DATA_DIR).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$91ygGKJdYprr8NHJQtxLyDBygoA(this, activity));
        this.ui.findPreference(PREF_CHOOSE_DATA_DIR).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$Gdhfm4nUP5ztAluw9zxvepi9miE(this, activity));
        this.ui.findPreference(UserPreferences.PREF_IMAGE_CACHE_SIZE).setOnPreferenceChangeListener(new -$$Lambda$PreferenceController$ltjpiCmnX_Ds-MAprw_uLExwcUc());
    }

    public static /* synthetic */ boolean lambda$setupStorageScreen$12(PreferenceController preferenceController, Activity activity, Preference preference) {
        if (19 > VERSION.SDK_INT || VERSION.SDK_INT > 22) {
            int readPermission = ContextCompat.checkSelfPermission(activity, "android.permission.READ_EXTERNAL_STORAGE");
            int writePermission = ContextCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (readPermission == 0 && writePermission == 0) {
                preferenceController.openDirectoryChooser();
            } else {
                preferenceController.requestPermission();
            }
        } else {
            preferenceController.showChooseDataFolderDialog();
        }
        return true;
    }

    public static /* synthetic */ boolean lambda$setupStorageScreen$13(PreferenceController preferenceController, Activity activity, Preference preference) {
        if (VERSION.SDK_INT >= 19) {
            preferenceController.showChooseDataFolderDialog();
        } else {
            activity.startActivityForResult(new Intent(activity, DirectoryChooserActivity.class), 1);
        }
        return true;
    }

    public static /* synthetic */ boolean lambda$setupStorageScreen$14(PreferenceController preferenceController, Preference preference, Object o) {
        if (!(o instanceof String)) {
            return false;
        }
        if ((Integer.parseInt((String) o) * 1024) * 1024 != UserPreferences.getImageCacheSize()) {
            Builder dialog = new Builder(preferenceController.ui.getActivity());
            dialog.setTitle(17039380);
            dialog.setMessage((int) R.string.pref_restart_required);
            dialog.setPositiveButton(17039370, null);
            dialog.show();
        }
        return true;
    }

    private void setupIntegrationsScreen() {
        AppCompatActivity activity = this.ui.getActivity();
        this.ui.findPreference(PREF_SCREEN_FLATTR).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$yXsyttR3tCpvBz-1wjp3JIavP3k(this, activity));
        this.ui.findPreference(PREF_SCREEN_GPODDER).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$6Mhc3PQaPiXoiw9NKIOpfhENg3I(this, activity));
    }

    private void setupFlattrScreen() {
        AppCompatActivity activity = this.ui.getActivity();
        this.ui.findPreference(PREF_FLATTR_REVOKE).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$ApU3wbh0Po0wjGgafr3NwaPqXVY(this, activity));
        this.ui.findPreference(PREF_AUTO_FLATTR_PREFS).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$EAbcOKuDICtPDC22GmUfDZ2COzE(this, activity));
    }

    public static /* synthetic */ boolean lambda$setupFlattrScreen$17(PreferenceController preferenceController, AppCompatActivity activity, Preference preference) {
        FlattrUtils.revokeAccessToken(activity);
        preferenceController.checkFlattrItemVisibility();
        return true;
    }

    private void setupGpodderScreen() {
        AppCompatActivity activity = this.ui.getActivity();
        this.ui.findPreference(PREF_GPODNET_SETLOGIN_INFORMATION).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$kjbJW0F64sUFwbfYt6TUZeCDsqU(this, activity));
        this.ui.findPreference(PREF_GPODNET_SYNC).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$_mBBmSlyFoK2JZIpPCY5RfKc9gc());
        this.ui.findPreference(PREF_GPODNET_FORCE_FULL_SYNC).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$c96QhSzC2IhXzeb601JLE3-9RMc());
        this.ui.findPreference(PREF_GPODNET_LOGOUT).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$DkIXDmnyWUIMQWLgpS7bLNMGwcY(this, activity));
        this.ui.findPreference(PREF_GPODNET_HOSTNAME).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$MIHS5QQPK9xyeLYv4_LAulz7ziQ(this, activity));
    }

    public static /* synthetic */ boolean lambda$setupGpodderScreen$20(PreferenceController preferenceController, Preference preference) {
        GpodnetSyncService.sendSyncIntent(preferenceController.ui.getActivity().getApplicationContext());
        Toast.makeText(preferenceController.ui.getActivity(), R.string.pref_gpodnet_sync_started, 0).show();
        return true;
    }

    public static /* synthetic */ boolean lambda$setupGpodderScreen$21(PreferenceController preferenceController, Preference preference) {
        GpodnetPreferences.setLastSubscriptionSyncTimestamp(0);
        GpodnetPreferences.setLastEpisodeActionsSyncTimestamp(0);
        GpodnetPreferences.setLastSyncAttempt(false, 0);
        preferenceController.updateLastGpodnetSyncReport(false, 0);
        GpodnetSyncService.sendSyncIntent(preferenceController.ui.getActivity().getApplicationContext());
        Toast.makeText(preferenceController.ui.getActivity(), R.string.pref_gpodnet_sync_started, 0).show();
        return true;
    }

    public static /* synthetic */ boolean lambda$setupGpodderScreen$22(PreferenceController preferenceController, AppCompatActivity activity, Preference preference) {
        GpodnetPreferences.logout();
        Toast.makeText(activity, R.string.pref_gpodnet_logout_toast, 0).show();
        preferenceController.updateGpodnetPreferenceScreen();
        return true;
    }

    private void setupPlaybackScreen() {
        Activity activity = this.ui.getActivity();
        this.ui.findPreference(PREF_PLAYBACK_SPEED_LAUNCHER).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$mfs5qs8WX_7zjvC_hCoige7TRhs(activity));
        this.ui.findPreference(PREF_PLAYBACK_REWIND_DELTA_LAUNCHER).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$pVFpvCjNZrM_GiQ7nZRMCbgi9YA(activity));
        this.ui.findPreference(PREF_PLAYBACK_FAST_FORWARD_DELTA_LAUNCHER).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$aSg8w4JN-ZJjKp3_KSPkE8nN_ok(activity));
        if (!PictureInPictureUtil.supportsPictureInPicture(activity)) {
            ListPreference behaviour = (ListPreference) this.ui.findPreference(UserPreferences.PREF_VIDEO_BEHAVIOR);
            behaviour.setEntries((int) R.array.video_background_behavior_options_without_pip);
            behaviour.setEntryValues((int) R.array.video_background_behavior_values_without_pip);
        }
    }

    private void setupAutoDownloadScreen() {
        this.ui.findPreference(UserPreferences.PREF_ENABLE_AUTODL).setOnPreferenceChangeListener(new -$$Lambda$PreferenceController$lUw9Pqfw4OYu6hHHw1I4qDklz_A());
        this.ui.findPreference(UserPreferences.PREF_ENABLE_AUTODL_WIFI_FILTER).setOnPreferenceChangeListener(new -$$Lambda$PreferenceController$LDAoqaotcFe9imjC5s7AUNqIQvI());
        this.ui.findPreference(UserPreferences.PREF_EPISODE_CACHE_SIZE).setOnPreferenceChangeListener(new -$$Lambda$PreferenceController$p0MymLeGzjrLWe4bRU6hnoDe0lg());
    }

    public static /* synthetic */ boolean lambda$setupAutoDownloadScreen$28(PreferenceController preferenceController, Preference preference, Object newValue) {
        if (newValue instanceof Boolean) {
            preferenceController.checkAutodownloadItemVisibility(((Boolean) newValue).booleanValue());
        }
        return true;
    }

    public static /* synthetic */ boolean lambda$setupAutoDownloadScreen$29(PreferenceController preferenceController, Preference preference, Object newValue) {
        if (!(newValue instanceof Boolean)) {
            return false;
        }
        preferenceController.setSelectedNetworksEnabled(((Boolean) newValue).booleanValue());
        return true;
    }

    public static /* synthetic */ boolean lambda$setupAutoDownloadScreen$30(PreferenceController preferenceController, Preference preference, Object o) {
        if (o instanceof String) {
            preferenceController.setEpisodeCacheSizeText(UserPreferences.readEpisodeCacheSize((String) o));
        }
        return true;
    }

    private void setupNetworkScreen() {
        this.ui.findPreference(PREF_SCREEN_AUTODL).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$zamxHBL8jScFInWXCCn27dt5mK4(this, this.ui.getActivity()));
        this.ui.findPreference(UserPreferences.PREF_UPDATE_INTERVAL).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$u1muCNrBUsc3mFrYaySLs-GiE9g());
        this.ui.findPreference(UserPreferences.PREF_PARALLEL_DOWNLOADS).setOnPreferenceChangeListener(new -$$Lambda$PreferenceController$1A8X3xZERkjjzlmnyvgx2CV6WRY());
        this.ui.findPreference(PREF_PROXY).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$4u8UothpnFPKWDyHXQC_ZYJsit8());
    }

    public static /* synthetic */ boolean lambda$setupNetworkScreen$33(PreferenceController preferenceController, Preference preference, Object o) {
        if (o instanceof Integer) {
            preferenceController.setParallelDownloadsText(((Integer) o).intValue());
        }
        return true;
    }

    private void setupMainScreen() {
        AppCompatActivity activity = this.ui.getActivity();
        setupSearch();
        this.ui.findPreference(PREF_SCREEN_USER_INTERFACE).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$SMkGtx3amma831ahKNZQFLHl2Qc(this, activity));
        this.ui.findPreference(PREF_SCREEN_PLAYBACK).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$vqTV_e3uxc-SU709kh3d9HSkjQ4(this, activity));
        this.ui.findPreference(PREF_SCREEN_NETWORK).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$ZbxbQRhOw_W6zKUwyCHJs9jtz3k(this, activity));
        this.ui.findPreference(PREF_SCREEN_INTEGRATIONS).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$uadHdR5hrz73AZ2vJk8VPhrINFw(this, activity));
        this.ui.findPreference(PREF_SCREEN_STORAGE).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$rZTIJps2py1snwkbLwtbnpRCYws(this, activity));
        this.ui.findPreference(PREF_ABOUT).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$APc6dM0zaZqClqheD_y2pECm9ok(activity));
        this.ui.findPreference(STATISTICS).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$jI5-KI0FQHGcRHbJZN_HZiJ0HYI(activity));
        this.ui.findPreference(PREF_KNOWN_ISSUES).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$3tJjwQNcEXts-CxJ3_luXbI_WTY());
        this.ui.findPreference(PREF_FAQ).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$4b77L9fzmDTlhvHG1FZ0QHRU7Co());
        this.ui.findPreference(PREF_SEND_CRASH_REPORT).setOnPreferenceClickListener(new -$$Lambda$PreferenceController$c_MBZ6Rhs5lzHjbNc5A1tgJg4Qg());
    }

    public static /* synthetic */ boolean lambda$setupMainScreen$44(PreferenceController preferenceController, Preference preference) {
        Context context = preferenceController.ui.getActivity().getApplicationContext();
        Intent emailIntent = new Intent("android.intent.action.SEND");
        emailIntent.setType("text/plain");
        emailIntent.putExtra("android.intent.extra.EMAIL", new String[]{"Martin.Fietz@gmail.com"});
        emailIntent.putExtra("android.intent.extra.SUBJECT", "AntennaPod Crash Report");
        emailIntent.putExtra("android.intent.extra.TEXT", "Please describe what you were doing when the app crashed");
        Uri fileUri = FileProvider.getUriForFile(context, context.getString(R.string.provider_authority), CrashReportWriter.getFile());
        emailIntent.putExtra("android.intent.extra.STREAM", fileUri);
        emailIntent.setFlags(1);
        String intentTitle = preferenceController.ui.getActivity().getString(R.string.send_email);
        if (VERSION.SDK_INT <= 19) {
            for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentActivities(emailIntent, 65536)) {
                context.grantUriPermission(resolveInfo.activityInfo.packageName, fileUri, 1);
            }
        }
        preferenceController.ui.getActivity().startActivity(Intent.createChooser(emailIntent, intentTitle));
        return true;
    }

    private void setupSearch() {
        AppCompatActivity activity = this.ui.getActivity();
        SearchConfiguration config = ((SearchPreference) this.ui.findPreference("searchPreference")).getSearchConfiguration();
        config.setActivity(activity);
        config.setFragmentContainerViewId(R.id.content);
        config.setBreadcrumbsEnabled(true);
        config.index(R.xml.preferences_user_interface).addBreadcrumb(getTitleOfPage(R.xml.preferences_user_interface));
        config.index(R.xml.preferences_playback).addBreadcrumb(getTitleOfPage(R.xml.preferences_playback));
        config.index(R.xml.preferences_network).addBreadcrumb(getTitleOfPage(R.xml.preferences_network));
        config.index(R.xml.preferences_storage).addBreadcrumb(getTitleOfPage(R.xml.preferences_storage));
        config.index(R.xml.preferences_autodownload).addBreadcrumb(getTitleOfPage(R.xml.preferences_network)).addBreadcrumb((int) R.string.automation).addBreadcrumb(getTitleOfPage(R.xml.preferences_autodownload));
        config.index(R.xml.preferences_gpodder).addBreadcrumb(getTitleOfPage(R.xml.preferences_integrations)).addBreadcrumb(getTitleOfPage(R.xml.preferences_gpodder));
        config.index(R.xml.preferences_flattr).addBreadcrumb(getTitleOfPage(R.xml.preferences_integrations)).addBreadcrumb(getTitleOfPage(R.xml.preferences_flattr));
    }

    public PreferenceFragmentCompat openScreen(int preferences, AppCompatActivity activity) {
        PreferenceFragmentCompat prefFragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(PreferenceActivity.PARAM_RESOURCE, preferences);
        prefFragment.setArguments(args);
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.content, prefFragment).addToBackStack(TAG).commit();
        return prefFragment;
    }

    public static int getTitleOfPage(int preferences) {
        switch (preferences) {
            case R.xml.preferences_autodownload:
                return R.string.pref_automatic_download_title;
            case R.xml.preferences_flattr:
                return R.string.flattr_label;
            case R.xml.preferences_gpodder:
                return R.string.gpodnet_main_label;
            case R.xml.preferences_integrations:
                return R.string.integrations_label;
            case R.xml.preferences_network:
                return R.string.network_pref;
            case R.xml.preferences_playback:
                return R.string.playback_pref;
            case R.xml.preferences_storage:
                return R.string.storage_pref;
            case R.xml.preferences_user_interface:
                return R.string.user_interface_label;
            default:
                return R.string.settings_label;
        }
    }

    private boolean export(ExportWriter exportWriter) {
        Context context = this.ui.getActivity();
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.exporting_label));
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        Builder alert = new Builder(context).setNeutralButton(17039370, -$$Lambda$PreferenceController$L9FSbGTmh9r8e50z7PUWPUpHT-8.INSTANCE);
        Observable observeOn = new ExportWorker(exportWriter).exportObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        Consumer -__lambda_preferencecontroller_hnell0sglbe_mps9hahoynmwy2w = new -$$Lambda$PreferenceController$HneLL0sglBe_mpS9hAhoynMWY2w(alert, context);
        Consumer -__lambda_preferencecontroller_slvahm_5gx5tfwhwzaxyrhacyss = new -$$Lambda$PreferenceController$slVahm_5GX5TFwhWzAXYRhacyss(alert);
        progressDialog.getClass();
        this.disposable = observeOn.subscribe(-__lambda_preferencecontroller_hnell0sglbe_mps9hahoynmwy2w, -__lambda_preferencecontroller_slvahm_5gx5tfwhwzaxyrhacyss, new -$$Lambda$dnY5B03wYo6lICUpHkkCK3ZdGTk(progressDialog));
        return true;
    }

    static /* synthetic */ void lambda$export$47(Builder alert, Context context, File output) throws Exception {
        alert.setTitle((int) R.string.export_success_title);
        alert.setMessage(context.getString(R.string.export_success_sum, new Object[]{output.toString()}));
        alert.setPositiveButton((int) R.string.send_label, new -$$Lambda$PreferenceController$ScS7S0RyuwttM1A9g1GC6xMOTdk(context, output));
        alert.create().show();
    }

    static /* synthetic */ void lambda$null$46(Context context, File output, DialogInterface dialog, int which) {
        Uri fileUri = FileProvider.getUriForFile(context.getApplicationContext(), context.getString(R.string.provider_authority), output);
        Intent sendIntent = new Intent("android.intent.action.SEND");
        sendIntent.putExtra("android.intent.extra.SUBJECT", context.getResources().getText(R.string.opml_export_label));
        sendIntent.putExtra("android.intent.extra.STREAM", fileUri);
        sendIntent.setType("text/plain");
        sendIntent.addFlags(1);
        if (VERSION.SDK_INT <= 19) {
            for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentActivities(sendIntent, 65536)) {
                context.grantUriPermission(resolveInfo.activityInfo.packageName, fileUri, 1);
            }
        }
        context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.send_label)));
    }

    static /* synthetic */ void lambda$export$48(Builder alert, Throwable error) throws Exception {
        alert.setTitle((int) R.string.export_error_label);
        alert.setMessage(error.getMessage());
        alert.show();
    }

    private void openInBrowser(String url) {
        try {
            this.ui.getActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this.ui.getActivity(), R.string.pref_no_browser_found, 1).show();
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public void onResume(int screen) {
        switch (screen) {
            case R.xml.preferences_autodownload:
                setEpisodeCacheSizeText(UserPreferences.getEpisodeCacheSize());
                checkAutodownloadItemVisibility(UserPreferences.isEnableAutodownload());
                break;
            case R.xml.preferences_flattr:
                checkFlattrItemVisibility();
                break;
            case R.xml.preferences_gpodder:
                GpodnetPreferences.registerOnSharedPreferenceChangeListener(this.gpoddernetListener);
                updateGpodnetPreferenceScreen();
                break;
            case R.xml.preferences_integrations:
                setIntegrationsItemVisibility();
                return;
            case R.xml.preferences_network:
                setUpdateIntervalText();
                setParallelDownloadsText(UserPreferences.getParallelDownloads());
                break;
            case R.xml.preferences_playback:
                checkSonicItemVisibility();
                break;
            case R.xml.preferences_storage:
                setDataFolderText();
                break;
            default:
                break;
        }
    }

    public void unregisterGpodnet() {
        GpodnetPreferences.unregisterOnSharedPreferenceChangeListener(this.gpoddernetListener);
    }

    public void unsubscribeExportSubscription() {
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @SuppressLint({"NewApi"})
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && requestCode == 1) {
            File path;
            String dir = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
            if (dir != null) {
                path = new File(dir);
            } else {
                path = this.ui.getActivity().getExternalFilesDir(null);
            }
            CharSequence message = null;
            Context context = this.ui.getActivity().getApplicationContext();
            if (!path.exists()) {
                message = String.format(context.getString(R.string.folder_does_not_exist_error), new Object[]{dir});
            } else if (!path.canRead()) {
                message = String.format(context.getString(R.string.folder_not_readable_error), new Object[]{dir});
            } else if (!path.canWrite()) {
                message = String.format(context.getString(R.string.folder_not_writable_error), new Object[]{dir});
            }
            if (message == null) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Setting data folder: ");
                stringBuilder.append(dir);
                Log.d(str, stringBuilder.toString());
                UserPreferences.setDataFolder(dir);
                setDataFolderText();
                return;
            }
            Builder ab = new Builder(this.ui.getActivity());
            ab.setMessage(message);
            ab.setPositiveButton(17039370, null);
            ab.show();
        }
    }

    private void updateGpodnetPreferenceScreen() {
        boolean loggedIn = GpodnetPreferences.loggedIn();
        this.ui.findPreference(PREF_GPODNET_LOGIN).setEnabled(loggedIn ^ 1);
        this.ui.findPreference(PREF_GPODNET_SETLOGIN_INFORMATION).setEnabled(loggedIn);
        this.ui.findPreference(PREF_GPODNET_SYNC).setEnabled(loggedIn);
        this.ui.findPreference(PREF_GPODNET_FORCE_FULL_SYNC).setEnabled(loggedIn);
        this.ui.findPreference(PREF_GPODNET_LOGOUT).setEnabled(loggedIn);
        this.ui.findPreference(PREF_GPODNET_NOTIFICATIONS).setEnabled(loggedIn);
        if (loggedIn) {
            this.ui.findPreference(PREF_GPODNET_LOGOUT).setSummary(Html.fromHtml(String.format(this.ui.getActivity().getString(R.string.pref_gpodnet_login_status), new Object[]{GpodnetPreferences.getUsername(), GpodnetPreferences.getDeviceID()})));
            updateLastGpodnetSyncReport(GpodnetPreferences.getLastSyncAttemptResult(), GpodnetPreferences.getLastSyncAttemptTimestamp());
        } else {
            this.ui.findPreference(PREF_GPODNET_LOGOUT).setSummary(null);
            updateLastGpodnetSyncReport(false, 0);
        }
        this.ui.findPreference(PREF_GPODNET_HOSTNAME).setSummary(GpodnetPreferences.getHostname());
    }

    private void updateLastGpodnetSyncReport(boolean successful, long lastTime) {
        Preference sync = this.ui.findPreference(PREF_GPODNET_SYNC);
        if (lastTime != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(r0.ui.getActivity().getString(R.string.pref_gpodnet_sync_changes_sum));
            stringBuilder.append("\n");
            AppCompatActivity activity = r0.ui.getActivity();
            Object[] objArr = new Object[2];
            objArr[0] = r0.ui.getActivity().getString(successful ? R.string.gpodnetsync_pref_report_successful : R.string.gpodnetsync_pref_report_failed);
            objArr[1] = DateUtils.getRelativeDateTimeString(r0.ui.getActivity(), lastTime, 60000, 604800000, 1);
            stringBuilder.append(activity.getString(R.string.pref_gpodnet_sync_sum_last_sync_line, objArr));
            sync.setSummary(stringBuilder.toString());
            return;
        }
        sync.setSummary(r0.ui.getActivity().getString(R.string.pref_gpodnet_sync_changes_sum));
    }

    private String[] getUpdateIntervalEntries(String[] values) {
        Resources res = this.ui.getActivity().getResources();
        String[] entries = new String[values.length];
        for (int x = 0; x < values.length; x++) {
            Integer v = Integer.valueOf(Integer.parseInt(values[x]));
            StringBuilder stringBuilder;
            switch (v.intValue()) {
                case 0:
                    entries[x] = res.getString(R.string.pref_update_interval_hours_manual);
                    break;
                case 1:
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(v);
                    stringBuilder.append(StringUtils.SPACE);
                    stringBuilder.append(res.getString(R.string.pref_update_interval_hours_singular));
                    entries[x] = stringBuilder.toString();
                    break;
                default:
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(v);
                    stringBuilder.append(StringUtils.SPACE);
                    stringBuilder.append(res.getString(R.string.pref_update_interval_hours_plural));
                    entries[x] = stringBuilder.toString();
                    break;
            }
        }
        return entries;
    }

    private void buildEpisodeCleanupPreference() {
        Resources res = this.ui.getActivity().getResources();
        ListPreference pref = (ListPreference) this.ui.findPreference(UserPreferences.PREF_EPISODE_CLEANUP);
        String[] values = res.getStringArray(2130903051);
        CharSequence[] entries = new String[values.length];
        for (int x = 0; x < values.length; x++) {
            int v = Integer.parseInt(values[x]);
            if (v == -1) {
                entries[x] = res.getString(R.string.episode_cleanup_queue_removal);
            } else if (v == -2) {
                entries[x] = res.getString(R.string.episode_cleanup_never);
            } else if (v == 0) {
                entries[x] = res.getString(R.string.episode_cleanup_after_listening);
            } else if (v <= 0 || v >= 24) {
                entries[x] = res.getQuantityString(R.plurals.episode_cleanup_days_after_listening, v / 24, new Object[]{Integer.valueOf(v / 24)});
            } else {
                entries[x] = res.getQuantityString(R.plurals.episode_cleanup_hours_after_listening, v, new Object[]{Integer.valueOf(v)});
            }
        }
        pref.setEntries(entries);
    }

    private void buildSmartMarkAsPlayedPreference() {
        Resources res = this.ui.getActivity().getResources();
        ListPreference pref = (ListPreference) this.ui.findPreference(UserPreferences.PREF_SMART_MARK_AS_PLAYED_SECS);
        String[] values = res.getStringArray(2130903068);
        CharSequence[] entries = new String[values.length];
        for (int x = 0; x < values.length; x++) {
            if (x == 0) {
                entries[x] = res.getString(R.string.pref_smart_mark_as_played_disabled);
            } else {
                Integer v = Integer.valueOf(Integer.parseInt(values[x]));
                if (v.intValue() < 60) {
                    entries[x] = res.getQuantityString(R.plurals.time_seconds_quantified, v.intValue(), new Object[]{v});
                } else {
                    entries[x] = res.getQuantityString(R.plurals.time_minutes_quantified, Integer.valueOf(v.intValue() / 60).intValue(), new Object[]{v});
                }
            }
        }
        pref.setEntries(entries);
    }

    private void setSelectedNetworksEnabled(boolean b) {
        CheckBoxPreference[] checkBoxPreferenceArr = this.selectedNetworks;
        if (checkBoxPreferenceArr != null) {
            for (Preference p : checkBoxPreferenceArr) {
                p.setEnabled(b);
            }
        }
    }

    private void setIntegrationsItemVisibility() {
        this.ui.findPreference(PREF_SCREEN_FLATTR).setEnabled(FlattrUtils.hasAPICredentials());
    }

    private void checkFlattrItemVisibility() {
        boolean hasFlattrToken = FlattrUtils.hasToken();
        this.ui.findPreference(PREF_FLATTR_AUTH).setEnabled(hasFlattrToken ^ 1);
        this.ui.findPreference(PREF_FLATTR_REVOKE).setEnabled(hasFlattrToken);
        this.ui.findPreference(PREF_AUTO_FLATTR_PREFS).setEnabled(hasFlattrToken);
    }

    private void checkAutodownloadItemVisibility(boolean autoDownload) {
        this.ui.findPreference(UserPreferences.PREF_EPISODE_CACHE_SIZE).setEnabled(autoDownload);
        this.ui.findPreference(UserPreferences.PREF_ENABLE_AUTODL_ON_BATTERY).setEnabled(autoDownload);
        this.ui.findPreference(UserPreferences.PREF_ENABLE_AUTODL_WIFI_FILTER).setEnabled(autoDownload);
        this.ui.findPreference(UserPreferences.PREF_EPISODE_CLEANUP).setEnabled(autoDownload);
        this.ui.findPreference(UserPreferences.PREF_ENABLE_AUTODL_ON_MOBILE).setEnabled(autoDownload);
        boolean z = autoDownload && UserPreferences.isEnableAutodownloadWifiFilter();
        setSelectedNetworksEnabled(z);
    }

    private void checkSonicItemVisibility() {
        if (VERSION.SDK_INT < 16) {
            ListPreference p = (ListPreference) this.ui.findPreference(UserPreferences.PREF_MEDIA_PLAYER);
            p.setEntries((int) R.array.media_player_options_no_sonic);
            p.setEntryValues((int) R.array.media_player_values_no_sonic);
        }
    }

    private void setUpdateIntervalText() {
        String val;
        Context context = this.ui.getActivity().getApplicationContext();
        long interval = UserPreferences.getUpdateInterval();
        if (interval > 0) {
            int hours = (int) TimeUnit.MILLISECONDS.toHours(interval);
            String hoursStr = context.getResources().getQuantityString(R.plurals.time_hours_quantified, hours, new Object[]{Integer.valueOf(hours)});
            val = String.format(context.getString(R.string.pref_autoUpdateIntervallOrTime_every), new Object[]{hoursStr});
        } else {
            int[] timeOfDay = UserPreferences.getUpdateTimeOfDay();
            if (timeOfDay.length == 2) {
                Calendar cal = new GregorianCalendar();
                cal.set(11, timeOfDay[0]);
                cal.set(12, timeOfDay[1]);
                String timeOfDayStr = DateFormat.getTimeFormat(context).format(cal.getTime());
                val = String.format(context.getString(R.string.pref_autoUpdateIntervallOrTime_at), new Object[]{timeOfDayStr});
            } else {
                val = context.getString(R.string.pref_smart_mark_as_played_disabled);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(context.getString(R.string.pref_autoUpdateIntervallOrTime_sum));
        stringBuilder.append("\n");
        stringBuilder.append(String.format(context.getString(R.string.pref_current_value), new Object[]{val}));
        this.ui.findPreference(UserPreferences.PREF_UPDATE_INTERVAL).setSummary(stringBuilder.toString());
    }

    private void setParallelDownloadsText(int downloads) {
        Resources res = this.ui.getActivity().getResources();
        String s = new StringBuilder();
        s.append(Integer.toString(downloads));
        s.append(res.getString(R.string.parallel_downloads_suffix));
        this.ui.findPreference(UserPreferences.PREF_PARALLEL_DOWNLOADS).setSummary(s.toString());
    }

    private void setEpisodeCacheSizeText(int cacheSize) {
        CharSequence s;
        Resources res = this.ui.getActivity().getResources();
        if (cacheSize == res.getInteger(R.integer.episode_cache_size_unlimited)) {
            s = res.getString(R.string.pref_episode_cache_unlimited);
        } else {
            String s2 = new StringBuilder();
            s2.append(Integer.toString(cacheSize));
            s2.append(res.getString(R.string.episodes_suffix));
            s = s2.toString();
        }
        this.ui.findPreference(UserPreferences.PREF_EPISODE_CACHE_SIZE).setSummary(s);
    }

    private void setDataFolderText() {
        File f = UserPreferences.getDataFolder(null);
        if (f != null) {
            this.ui.findPreference(PREF_CHOOSE_DATA_DIR).setSummary(f.getAbsolutePath());
        }
    }

    private static String blankIfNull(String val) {
        return val == null ? "" : val;
    }

    private void buildAutodownloadSelectedNetworksPreference() {
        Activity activity = this.ui.getActivity();
        if (this.selectedNetworks != null) {
            clearAutodownloadSelectedNetworsPreference();
        }
        List<WifiConfiguration> networks = ((WifiManager) activity.getApplicationContext().getSystemService("wifi")).getConfiguredNetworks();
        if (networks == null) {
            Log.e(TAG, "Couldn't get list of configure Wi-Fi networks");
            return;
        }
        Collections.sort(networks, -$$Lambda$PreferenceController$cnVoKc7stK4RWmxAYM4l_d8EXRQ.INSTANCE);
        this.selectedNetworks = new CheckBoxPreference[networks.size()];
        List<String> prefValues = Arrays.asList(UserPreferences.getAutodownloadSelectedNetworks());
        PreferenceScreen prefScreen = this.ui.getPreferenceScreen();
        OnPreferenceClickListener clickListener = -$$Lambda$PreferenceController$KgwyEzemdJWeULXuC4cuwvwSB18.INSTANCE;
        for (int i = 0; i < networks.size(); i++) {
            WifiConfiguration config = (WifiConfiguration) networks.get(i);
            CheckBoxPreference pref = new CheckBoxPreference(activity);
            String key = Integer.toString(config.networkId);
            pref.setTitle(config.SSID);
            pref.setKey(key);
            pref.setOnPreferenceClickListener(clickListener);
            pref.setPersistent(false);
            pref.setChecked(prefValues.contains(key));
            this.selectedNetworks[i] = pref;
            prefScreen.addPreference(pref);
        }
    }

    static /* synthetic */ boolean lambda$buildAutodownloadSelectedNetworksPreference$50(Preference preference) {
        if (!(preference instanceof CheckBoxPreference)) {
            return false;
        }
        String key = preference.getKey();
        List<String> prefValuesList = new ArrayList(Arrays.asList(UserPreferences.getAutodownloadSelectedNetworks()));
        boolean newValue = ((CheckBoxPreference) preference).isChecked();
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Selected network ");
        stringBuilder.append(key);
        stringBuilder.append(". New state: ");
        stringBuilder.append(newValue);
        Log.d(str, stringBuilder.toString());
        int index = prefValuesList.indexOf(key);
        if (index >= 0 && !newValue) {
            prefValuesList.remove(index);
        } else if (index < 0 && newValue) {
            prefValuesList.add(key);
        }
        UserPreferences.setAutodownloadSelectedNetworks((String[]) prefValuesList.toArray(new String[prefValuesList.size()]));
        return true;
    }

    private void clearAutodownloadSelectedNetworsPreference() {
        if (this.selectedNetworks != null) {
            PreferenceScreen prefScreen = this.ui.getPreferenceScreen();
            for (CheckBoxPreference network : this.selectedNetworks) {
                if (network != null) {
                    prefScreen.removePreference(network);
                }
            }
        }
    }

    private void showDrawerPreferencesDialog() {
        Context context = this.ui.getActivity();
        List<String> hiddenDrawerItems = UserPreferences.getHiddenDrawerItems();
        CharSequence[] navTitles = context.getResources().getStringArray(R.array.nav_drawer_titles);
        String[] NAV_DRAWER_TAGS = MainActivity.NAV_DRAWER_TAGS;
        boolean[] checked = new boolean[MainActivity.NAV_DRAWER_TAGS.length];
        for (int i = 0; i < NAV_DRAWER_TAGS.length; i++) {
            if (!hiddenDrawerItems.contains(NAV_DRAWER_TAGS[i])) {
                checked[i] = true;
            }
        }
        Builder builder = new Builder(context);
        builder.setTitle((int) R.string.drawer_preferences);
        builder.setMultiChoiceItems(navTitles, checked, new -$$Lambda$PreferenceController$hBl-ppZ0dlOiswwu4sY0LaxzAcA(hiddenDrawerItems, NAV_DRAWER_TAGS));
        builder.setPositiveButton((int) R.string.confirm_label, new -$$Lambda$PreferenceController$BrhrDlv0FXAzFmVjzMwS4AjSN6g(hiddenDrawerItems));
        builder.setNegativeButton((int) R.string.cancel_label, null);
        builder.create().show();
    }

    static /* synthetic */ void lambda$showDrawerPreferencesDialog$51(List hiddenDrawerItems, String[] NAV_DRAWER_TAGS, DialogInterface dialog, int which, boolean isChecked) {
        if (isChecked) {
            hiddenDrawerItems.remove(NAV_DRAWER_TAGS[which]);
        } else {
            hiddenDrawerItems.add(NAV_DRAWER_TAGS[which]);
        }
    }

    private void showNotificationButtonsDialog() {
        Context context = this.ui.getActivity();
        List<Integer> preferredButtons = UserPreferences.getCompactNotificationButtons();
        CharSequence[] allButtonNames = context.getResources().getStringArray(R.array.compact_notification_buttons_options);
        boolean[] checked = new boolean[allButtonNames.length];
        for (int i = 0; i < checked.length; i++) {
            if (preferredButtons.contains(Integer.valueOf(i))) {
                checked[i] = true;
            }
        }
        Builder builder = new Builder(context);
        builder.setTitle(String.format(context.getResources().getString(R.string.pref_compact_notification_buttons_dialog_title), new Object[]{Integer.valueOf(2)}));
        builder.setMultiChoiceItems(allButtonNames, checked, new -$$Lambda$PreferenceController$s_CUTHnHR3HkKr9Xn35aTmDDky4(checked, preferredButtons, context));
        builder.setPositiveButton((int) R.string.confirm_label, new -$$Lambda$PreferenceController$BhH5KsBoiKRC6IQDZIjjmPsCS18(preferredButtons));
        builder.setNegativeButton((int) R.string.cancel_label, null);
        builder.create().show();
    }

    static /* synthetic */ void lambda$showNotificationButtonsDialog$53(boolean[] checked, List preferredButtons, Context context, DialogInterface dialog, int which, boolean isChecked) {
        checked[which] = isChecked;
        if (!isChecked) {
            preferredButtons.remove(Integer.valueOf(which));
        } else if (preferredButtons.size() < 2) {
            preferredButtons.add(Integer.valueOf(which));
        } else {
            checked[which] = false;
            View selectionView = ((AlertDialog) dialog).getListView();
            selectionView.setItemChecked(which, false);
            Snackbar.make(selectionView, String.format(context.getResources().getString(R.string.pref_compact_notification_buttons_dialog_error), new Object[]{Integer.valueOf(2)}), -1).show();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this.ui.getActivity(), EXTERNAL_STORAGE_PERMISSIONS, 41);
    }

    private void openDirectoryChooser() {
        Activity activity = this.ui.getActivity();
        activity.startActivityForResult(new Intent(activity, DirectoryChooserActivity.class), 1);
    }

    private void showChooseDataFolderDialog() {
        ChooseDataFolderDialog.showDialog(this.ui.getActivity(), new C10833());
    }

    private void showUpdateIntervalTimePreferencesDialog() {
        Context context = this.ui.getActivity();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title((int) R.string.pref_autoUpdateIntervallOrTime_title);
        builder.content((int) R.string.pref_autoUpdateIntervallOrTime_message);
        builder.positiveText((int) R.string.pref_autoUpdateIntervallOrTime_Interval);
        builder.negativeText((int) R.string.pref_autoUpdateIntervallOrTime_TimeOfDay);
        builder.neutralText((int) R.string.pref_autoUpdateIntervallOrTime_Disable);
        builder.onPositive(new -$$Lambda$PreferenceController$e4ZgAL_IdOnJiPo8rW1eEv_t9Aw(this, context));
        builder.onNegative(new -$$Lambda$PreferenceController$vElVXMfIDk9ZtS2NRLo7kwcicnk(this, context));
        builder.onNeutral(new -$$Lambda$PreferenceController$lLEcGf8ZXapXa1oeY9xv9J2BcJU());
        builder.show();
    }

    public static /* synthetic */ void lambda$showUpdateIntervalTimePreferencesDialog$56(PreferenceController preferenceController, Context context, MaterialDialog dialog, DialogAction which) {
        Builder builder1 = new Builder(context);
        builder1.setTitle(context.getString(R.string.pref_autoUpdateIntervallOrTime_Interval));
        String[] values = context.getResources().getStringArray(R.array.update_intervall_values);
        CharSequence[] entries = preferenceController.getUpdateIntervalEntries(values);
        long currInterval = UserPreferences.getUpdateInterval();
        int checkedItem = -1;
        if (currInterval > 0) {
            checkedItem = ArrayUtils.indexOf(values, String.valueOf(TimeUnit.MILLISECONDS.toHours(currInterval)));
        }
        builder1.setSingleChoiceItems(entries, checkedItem, new -$$Lambda$PreferenceController$zkXY596TzaQ37dVyVDiWbXUbTL4(preferenceController, values));
        builder1.setNegativeButton(context.getString(R.string.cancel_label), null);
        builder1.show();
    }

    public static /* synthetic */ void lambda$null$55(PreferenceController preferenceController, String[] values, DialogInterface dialog1, int which1) {
        UserPreferences.setUpdateInterval((long) Integer.parseInt(values[which1]));
        dialog1.dismiss();
        preferenceController.setUpdateIntervalText();
    }

    public static /* synthetic */ void lambda$showUpdateIntervalTimePreferencesDialog$58(PreferenceController preferenceController, Context context, MaterialDialog dialog, DialogAction which) {
        int hourOfDay = 7;
        int minute = 0;
        int[] updateTime = UserPreferences.getUpdateTimeOfDay();
        if (updateTime.length == 2) {
            hourOfDay = updateTime[0];
            minute = updateTime[1];
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new -$$Lambda$PreferenceController$_oIdBQ7UpygmTw5q1Z1x4miJI2I(preferenceController), hourOfDay, minute, DateFormat.is24HourFormat(context));
        timePickerDialog.setTitle(context.getString(R.string.pref_autoUpdateIntervallOrTime_TimeOfDay));
        timePickerDialog.show();
    }

    public static /* synthetic */ void lambda$null$57(PreferenceController preferenceController, TimePicker view, int selectedHourOfDay, int selectedMinute) {
        if (view.getTag() == null) {
            view.setTag("TAGGED");
            UserPreferences.setUpdateTimeOfDay(selectedHourOfDay, selectedMinute);
            preferenceController.setUpdateIntervalText();
        }
    }

    public static /* synthetic */ void lambda$showUpdateIntervalTimePreferencesDialog$59(PreferenceController preferenceController, MaterialDialog dialog, DialogAction which) {
        UserPreferences.setUpdateInterval(0);
        preferenceController.setUpdateIntervalText();
    }
}

package de.danoeh.antennapod.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import de.danoeh.antennapod.adapter.NavListAdapter;
import de.danoeh.antennapod.adapter.NavListAdapter.ItemAccess;
import de.danoeh.antennapod.core.asynctask.FeedRemover;
import de.danoeh.antennapod.core.event.MessageEvent;
import de.danoeh.antennapod.core.event.ProgressEvent;
import de.danoeh.antennapod.core.event.QueueEvent;
import de.danoeh.antennapod.core.event.QueueEvent.Action;
import de.danoeh.antennapod.core.event.ServiceEvent;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.EventDistributor$EventListener;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.DBReader.NavDrawerData;
import de.danoeh.antennapod.core.util.StorageUtils;
import de.danoeh.antennapod.core.util.download.AutoUpdateManager;
import de.danoeh.antennapod.core.util.gui.NotificationUtils;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.dialog.RatingDialog;
import de.danoeh.antennapod.dialog.RenameFeedDialog;
import de.danoeh.antennapod.fragment.AddFeedFragment;
import de.danoeh.antennapod.fragment.DownloadsFragment;
import de.danoeh.antennapod.fragment.EpisodesFragment;
import de.danoeh.antennapod.fragment.ExternalPlayerFragment;
import de.danoeh.antennapod.fragment.ItemlistFragment;
import de.danoeh.antennapod.fragment.PlaybackHistoryFragment;
import de.danoeh.antennapod.fragment.QueueFragment;
import de.danoeh.antennapod.fragment.SubscriptionFragment;
import de.danoeh.antennapod.menuhandler.NavDrawerActivity;
import de.greenrobot.event.EventBus;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

public class MainActivity extends CastEnabledActivity implements NavDrawerActivity {
    private static final int EVENTS = 3;
    public static final String EXTRA_FEED_ID = "fragment_feed_id";
    public static final String EXTRA_FRAGMENT_ARGS = "fragment_args";
    public static final String EXTRA_FRAGMENT_TAG = "fragment_tag";
    public static final String EXTRA_NAV_INDEX = "nav_index";
    public static final String EXTRA_NAV_TYPE = "nav_type";
    public static final String[] NAV_DRAWER_TAGS = new String[]{QueueFragment.TAG, EpisodesFragment.TAG, SubscriptionFragment.TAG, DownloadsFragment.TAG, PlaybackHistoryFragment.TAG, AddFeedFragment.TAG, NavListAdapter.SUBSCRIPTION_LIST_TAG};
    public static final String PREF_IS_FIRST_LAUNCH = "prefMainActivityIsFirstLaunch";
    private static final String PREF_LAST_FRAGMENT_TAG = "prefMainActivityLastFragmentTag";
    public static final String PREF_NAME = "MainActivityPrefs";
    private static final String SAVE_BACKSTACK_COUNT = "backstackCount";
    private static final String SAVE_TITLE = "title";
    private static final String TAG = "MainActivity";
    private final EventDistributor$EventListener contentUpdate = new MainActivity$9(this);
    private CharSequence currentTitle;
    private Disposable disposable;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ExternalPlayerFragment externalPlayerFragment;
    private final ItemAccess itemAccess = new MainActivity$8(this);
    private long lastBackButtonPressTime = 0;
    private int mPosition = -1;
    private NavListAdapter navAdapter;
    private View navDrawer;
    private NavDrawerData navDrawerData;
    private ListView navList;
    private final OnItemClickListener navListClickListener = new MainActivity$2(this);
    private final OnItemLongClickListener newListLongClickListener = new MainActivity$3(this);
    private ProgressDialog pd;
    private int selectedNavListIndex = 0;
    private Toolbar toolbar;

    public void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getNoTitleTheme());
        super.onCreate(savedInstanceState);
        StorageUtils.checkStorageAvailability(this);
        setContentView((int) R.layout.main);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        if (VERSION.SDK_INT >= 21) {
            findViewById(R.id.shadow).setVisibility(8);
            getSupportActionBar().setElevation((float) ((int) TypedValue.applyDimension(1, 4.0f, getResources().getDisplayMetrics())));
        }
        this.currentTitle = getTitle();
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.navList = (ListView) findViewById(R.id.nav_list);
        this.navDrawer = findViewById(R.id.nav_layout);
        this.drawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout, R.string.drawer_open, R.string.drawer_close);
        if (savedInstanceState != null) {
            this.drawerToggle.setDrawerIndicatorEnabled(savedInstanceState.getInt(SAVE_BACKSTACK_COUNT, 0) == 0);
        }
        this.drawerLayout.setDrawerListener(this.drawerToggle);
        FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(new -$$Lambda$MainActivity$1tkrx2v7XSrThfkNWQa8-sCi4N0(this, fm));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        this.navAdapter = new NavListAdapter(this.itemAccess, this);
        this.navList.setAdapter(this.navAdapter);
        this.navList.setOnItemClickListener(this.navListClickListener);
        this.navList.setOnItemLongClickListener(this.newListLongClickListener);
        registerForContextMenu(this.navList);
        this.navAdapter.registerDataSetObserver(new MainActivity$1(this));
        findViewById(R.id.nav_settings).setOnClickListener(new -$$Lambda$MainActivity$WRcy1sXgeruSN86cAmRms8-3p8Y());
        FragmentTransaction transaction = fm.beginTransaction();
        Fragment mainFragment = fm.findFragmentByTag("main");
        if (mainFragment != null) {
            transaction.replace(R.id.main_view, mainFragment);
        } else {
            String lastFragment = getLastNavFragment();
            if (ArrayUtils.contains(NAV_DRAWER_TAGS, lastFragment)) {
                loadFragment(lastFragment, null);
            } else {
                try {
                    loadFeedFragmentById((long) Integer.parseInt(lastFragment), null);
                } catch (NumberFormatException e) {
                    loadFragment(QueueFragment.TAG, null);
                }
            }
        }
        this.externalPlayerFragment = new ExternalPlayerFragment();
        transaction.replace(R.id.playerFragment, this.externalPlayerFragment, ExternalPlayerFragment.TAG);
        transaction.commit();
        checkFirstLaunch();
        NotificationUtils.createChannels(this);
        UserPreferences.restartUpdateAlarm(false);
    }

    public static /* synthetic */ void lambda$onCreate$0(MainActivity mainActivity, FragmentManager fm) {
        mainActivity.drawerToggle.setDrawerIndicatorEnabled(fm.getBackStackEntryCount() == 0);
    }

    public static /* synthetic */ void lambda$onCreate$1(MainActivity mainActivity, View v) {
        mainActivity.drawerLayout.closeDrawer(mainActivity.navDrawer);
        mainActivity.startActivity(new Intent(mainActivity, PreferenceActivity.class));
    }

    private void saveLastNavFragment(String tag) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("saveLastNavFragment(tag: ");
        stringBuilder.append(tag);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        Editor edit = getSharedPreferences(PREF_NAME, 0).edit();
        if (tag != null) {
            edit.putString(PREF_LAST_FRAGMENT_TAG, tag);
        } else {
            edit.remove(PREF_LAST_FRAGMENT_TAG);
        }
        edit.apply();
    }

    private String getLastNavFragment() {
        String lastFragment = getSharedPreferences(PREF_NAME, 0).getString(PREF_LAST_FRAGMENT_TAG, QueueFragment.TAG);
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getLastNavFragment() -> ");
        stringBuilder.append(lastFragment);
        Log.d(str, stringBuilder.toString());
        return lastFragment;
    }

    private void checkFirstLaunch() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, 0);
        if (prefs.getBoolean(PREF_IS_FIRST_LAUNCH, true)) {
            new Handler().postDelayed(new -$$Lambda$MainActivity$jxsMZTkUwESLB3HQlfBQ9qKYE7A(), 1500);
            UserPreferences.setUpdateInterval(12);
            Editor edit = prefs.edit();
            edit.putBoolean(PREF_IS_FIRST_LAUNCH, false);
            edit.commit();
        }
    }

    private void showDrawerPreferencesDialog() {
        List<String> hiddenDrawerItems = UserPreferences.getHiddenDrawerItems();
        String[] strArr = NAV_DRAWER_TAGS;
        String[] navLabels = new String[strArr.length];
        boolean[] checked = new boolean[strArr.length];
        int i = 0;
        while (true) {
            String tag = NAV_DRAWER_TAGS;
            if (i < tag.length) {
                tag = tag[i];
                navLabels[i] = this.navAdapter.getLabel(tag);
                if (!hiddenDrawerItems.contains(tag)) {
                    checked[i] = true;
                }
                i++;
            } else {
                Builder builder = new Builder(this);
                builder.setTitle(R.string.drawer_preferences);
                builder.setMultiChoiceItems(navLabels, checked, new -$$Lambda$MainActivity$ivg-uGCC5v8N_bWeLPOfRfOlzPc(hiddenDrawerItems));
                builder.setPositiveButton(R.string.confirm_label, new -$$Lambda$MainActivity$-9Pc1qmKCBvdZamLLLLs78x8GnM(hiddenDrawerItems));
                builder.setNegativeButton(R.string.cancel_label, null);
                builder.create().show();
                return;
            }
        }
    }

    static /* synthetic */ void lambda$showDrawerPreferencesDialog$3(List hiddenDrawerItems, DialogInterface dialog, int which, boolean isChecked) {
        if (isChecked) {
            hiddenDrawerItems.remove(NAV_DRAWER_TAGS[which]);
        } else {
            hiddenDrawerItems.add(NAV_DRAWER_TAGS[which]);
        }
    }

    public boolean isDrawerOpen() {
        DrawerLayout drawerLayout = this.drawerLayout;
        if (drawerLayout != null) {
            View view = this.navDrawer;
            if (view != null && drawerLayout.isDrawerOpen(view)) {
                return true;
            }
        }
        return false;
    }

    public List<Feed> getFeeds() {
        NavDrawerData navDrawerData = this.navDrawerData;
        return navDrawerData != null ? navDrawerData.feeds : null;
    }

    private void loadFragment(int index, Bundle args) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("loadFragment(index: ");
        stringBuilder.append(index);
        stringBuilder.append(", args: ");
        stringBuilder.append(args);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        if (index < this.navAdapter.getSubscriptionOffset()) {
            loadFragment((String) this.navAdapter.getTags().get(index), args);
        } else {
            loadFeedFragmentByPosition(index - this.navAdapter.getSubscriptionOffset(), args);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadFragment(java.lang.String r4, android.os.Bundle r5) {
        /*
        r3 = this;
        r0 = "MainActivity";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "loadFragment(tag: ";
        r1.append(r2);
        r1.append(r4);
        r2 = ", args: ";
        r1.append(r2);
        r1.append(r5);
        r2 = ")";
        r1.append(r2);
        r1 = r1.toString();
        android.util.Log.d(r0, r1);
        r0 = 0;
        r1 = r4.hashCode();
        switch(r1) {
            case -1578080595: goto L_0x005e;
            case -58242769: goto L_0x0054;
            case 28587112: goto L_0x004a;
            case 378123323: goto L_0x0040;
            case 2051192649: goto L_0x0036;
            case 2146299489: goto L_0x002c;
            default: goto L_0x002b;
        };
    L_0x002b:
        goto L_0x0068;
    L_0x002c:
        r1 = "QueueFragment";
        r1 = r4.equals(r1);
        if (r1 == 0) goto L_0x002b;
    L_0x0034:
        r1 = 0;
        goto L_0x0069;
    L_0x0036:
        r1 = "PlaybackHistoryFragment";
        r1 = r4.equals(r1);
        if (r1 == 0) goto L_0x002b;
    L_0x003e:
        r1 = 3;
        goto L_0x0069;
    L_0x0040:
        r1 = "DownloadsFragment";
        r1 = r4.equals(r1);
        if (r1 == 0) goto L_0x002b;
    L_0x0048:
        r1 = 2;
        goto L_0x0069;
    L_0x004a:
        r1 = "EpisodesFragment";
        r1 = r4.equals(r1);
        if (r1 == 0) goto L_0x002b;
    L_0x0052:
        r1 = 1;
        goto L_0x0069;
    L_0x0054:
        r1 = "AddFeedFragment";
        r1 = r4.equals(r1);
        if (r1 == 0) goto L_0x002b;
    L_0x005c:
        r1 = 4;
        goto L_0x0069;
    L_0x005e:
        r1 = "SubscriptionFragment";
        r1 = r4.equals(r1);
        if (r1 == 0) goto L_0x002b;
    L_0x0066:
        r1 = 5;
        goto L_0x0069;
    L_0x0068:
        r1 = -1;
    L_0x0069:
        switch(r1) {
            case 0: goto L_0x008f;
            case 1: goto L_0x008d;
            case 2: goto L_0x0086;
            case 3: goto L_0x007f;
            case 4: goto L_0x007d;
            case 5: goto L_0x0076;
            default: goto L_0x006c;
        };
    L_0x006c:
        r4 = "QueueFragment";
        r1 = new de.danoeh.antennapod.fragment.QueueFragment;
        r1.<init>();
        r0 = r1;
        r5 = 0;
        goto L_0x0096;
    L_0x0076:
        r1 = new de.danoeh.antennapod.fragment.SubscriptionFragment;
        r1.<init>();
        r0 = r1;
        goto L_0x0096;
    L_0x007d:
        r0 = 0;
        goto L_0x0096;
    L_0x007f:
        r1 = new de.danoeh.antennapod.fragment.DownloadsFragment;
        r1.<init>();
        r0 = r1;
        goto L_0x0096;
    L_0x0086:
        r1 = new de.danoeh.antennapod.fragment.DownloadsFragment;
        r1.<init>();
        r0 = r1;
        goto L_0x0096;
    L_0x008d:
        r0 = 0;
        goto L_0x0096;
    L_0x008f:
        r1 = new de.danoeh.antennapod.fragment.QueueFragment;
        r1.<init>();
        r0 = r1;
    L_0x0096:
        r1 = r3.navAdapter;
        r1 = r1.getLabel(r4);
        r3.currentTitle = r1;
        r1 = r3.getSupportActionBar();
        r2 = r3.currentTitle;
        r1.setTitle(r2);
        r3.saveLastNavFragment(r4);
        if (r5 == 0) goto L_0x00b0;
    L_0x00ac:
        r0.setArguments(r5);
        goto L_0x00b1;
    L_0x00b1:
        r3.loadFragment(r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.activity.MainActivity.loadFragment(java.lang.String, android.os.Bundle):void");
    }

    private void loadFeedFragmentByPosition(int relPos, Bundle args) {
        if (relPos >= 0) {
            loadFeedFragmentById(this.itemAccess.getItem(relPos).getId(), args);
        }
    }

    public void loadFeedFragmentById(long feedId, Bundle args) {
        Fragment fragment = ItemlistFragment.newInstance(feedId);
        if (args != null) {
            fragment.setArguments(args);
        }
        saveLastNavFragment(String.valueOf(feedId));
        this.currentTitle = "";
        getSupportActionBar().setTitle(this.currentTitle);
        loadFragment(fragment);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }
        FragmentTransaction t = fragmentManager.beginTransaction();
        t.replace(R.id.main_view, fragment, "main");
        fragmentManager.popBackStack();
        t.commitAllowingStateLoss();
        NavListAdapter navListAdapter = this.navAdapter;
        if (navListAdapter != null) {
            navListAdapter.notifyDataSetChanged();
        }
    }

    public void loadChildFragment(Fragment fragment) {
        Validate.notNull(fragment);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_view, fragment, "main").addToBackStack(null).commit();
    }

    public void dismissChildFragment() {
        getSupportFragmentManager().popBackStack();
    }

    private int getSelectedNavListIndex() {
        String currentFragment = getLastNavFragment();
        if (currentFragment == null) {
            return -1;
        }
        int tagIndex = this.navAdapter.getTags().indexOf(currentFragment);
        if (tagIndex >= 0) {
            return tagIndex;
        }
        if (ArrayUtils.contains(NAV_DRAWER_TAGS, currentFragment)) {
            return -1;
        }
        long feedId = Long.parseLong(currentFragment);
        List<Feed> feeds = this.navDrawerData;
        if (feeds != null) {
            feeds = feeds.feeds;
            for (int i = 0; i < feeds.size(); i++) {
                if (((Feed) feeds.get(i)).getId() == feedId) {
                    return this.navAdapter.getSubscriptionOffset() + i;
                }
            }
        }
        return -1;
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.drawerToggle.syncState();
        if (savedInstanceState != null) {
            this.currentTitle = savedInstanceState.getString("title");
            if (!this.drawerLayout.isDrawerOpen(this.navDrawer)) {
                getSupportActionBar().setTitle(this.currentTitle);
            }
            this.selectedNavListIndex = getSelectedNavListIndex();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.drawerToggle.onConfigurationChanged(newConfig);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", getSupportActionBar().getTitle().toString());
        outState.putInt(SAVE_BACKSTACK_COUNT, getSupportFragmentManager().getBackStackEntryCount());
    }

    public void onStart() {
        super.onStart();
        EventDistributor.getInstance().register(this.contentUpdate);
        EventBus.getDefault().register(this);
        RatingDialog.init(this);
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        StorageUtils.checkStorageAvailability(this);
        AutoUpdateManager.checkShouldRefreshFeeds(getApplicationContext());
        Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_FEED_ID)) {
            if (this.navDrawerData != null) {
                if (intent.hasExtra(EXTRA_NAV_TYPE)) {
                    if (!intent.hasExtra(EXTRA_NAV_INDEX)) {
                        if (intent.hasExtra(EXTRA_FRAGMENT_TAG)) {
                        }
                    }
                }
            }
            loadData();
            RatingDialog.check();
        }
        handleNavIntent();
        loadData();
        RatingDialog.check();
    }

    protected void onStop() {
        super.onStop();
        EventDistributor.getInstance().unregister(this.contentUpdate);
        EventBus.getDefault().unregister(this);
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        ProgressDialog progressDialog = this.pd;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @TargetApi(14)
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }

    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onCreateOptionsMenu(android.view.Menu r7) {
        /*
        r6 = this;
        r0 = super.onCreateOptionsMenu(r7);
        r1 = de.danoeh.antennapod.core.util.Flavors.FLAVOR;
        r2 = de.danoeh.antennapod.core.util.Flavors.PLAY;
        if (r1 != r2) goto L_0x0060;
    L_0x000a:
        r1 = r6.getLastNavFragment();
        r2 = -1;
        r3 = r1.hashCode();
        r4 = 1;
        r5 = 0;
        switch(r3) {
            case -1578080595: goto L_0x004b;
            case -58242769: goto L_0x0041;
            case 28587112: goto L_0x0037;
            case 378123323: goto L_0x002d;
            case 2051192649: goto L_0x0023;
            case 2146299489: goto L_0x0019;
            default: goto L_0x0018;
        };
    L_0x0018:
        goto L_0x0054;
    L_0x0019:
        r3 = "QueueFragment";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0018;
    L_0x0021:
        r2 = 0;
        goto L_0x0054;
    L_0x0023:
        r3 = "PlaybackHistoryFragment";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0018;
    L_0x002b:
        r2 = 3;
        goto L_0x0054;
    L_0x002d:
        r3 = "DownloadsFragment";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0018;
    L_0x0035:
        r2 = 2;
        goto L_0x0054;
    L_0x0037:
        r3 = "EpisodesFragment";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0018;
    L_0x003f:
        r2 = 1;
        goto L_0x0054;
    L_0x0041:
        r3 = "AddFeedFragment";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0018;
    L_0x0049:
        r2 = 4;
        goto L_0x0054;
    L_0x004b:
        r3 = "SubscriptionFragment";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0018;
    L_0x0053:
        r2 = 5;
    L_0x0054:
        switch(r2) {
            case 0: goto L_0x005c;
            case 1: goto L_0x005c;
            case 2: goto L_0x005b;
            case 3: goto L_0x005b;
            case 4: goto L_0x005b;
            case 5: goto L_0x005b;
            default: goto L_0x0057;
        };
    L_0x0057:
        r6.requestCastButton(r5);
        return r0;
    L_0x005b:
        return r0;
    L_0x005c:
        r6.requestCastButton(r4);
        return r0;
    L_0x0060:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.activity.MainActivity.onCreateOptionsMenu(android.view.Menu):boolean");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            dismissChildFragment();
        }
        return true;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.nav_list) {
            int position = ((AdapterContextMenuInfo) menuInfo).position;
            if (position >= this.navAdapter.getSubscriptionOffset()) {
                getMenuInflater().inflate(R.menu.nav_feed_context, menu);
                menu.setHeaderTitle(((Feed) this.navDrawerData.feeds.get(position - this.navAdapter.getSubscriptionOffset())).getTitle());
            }
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        int position = this.mPosition;
        this.mPosition = -1;
        if (position < 0) {
            return false;
        }
        Feed feed = (Feed) this.navDrawerData.feeds.get(position - this.navAdapter.getSubscriptionOffset());
        switch (item.getItemId()) {
            case R.id.mark_all_read_item:
                new MainActivity$5(this, this, R.string.mark_all_read_label, R.string.mark_all_read_confirmation_msg, feed).createNewDialog().show();
                return true;
            case R.id.mark_all_seen_item:
                new MainActivity$4(this, this, R.string.mark_all_seen_label, R.string.mark_all_seen_confirmation_msg, feed).createNewDialog().show();
                return true;
            case R.id.remove_item:
                FeedRemover remover = new MainActivity$6(this, this, feed, position);
                new MainActivity$7(this, this, R.string.remove_feed_label, getString(R.string.feed_delete_confirmation_msg, new Object[]{feed.getTitle()}), feed, remover).createNewDialog().show();
                return true;
            case R.id.rename_item:
                new RenameFeedDialog(this, feed).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onBackPressed() {
        if (isDrawerOpen()) {
            this.drawerLayout.closeDrawer(this.navDrawer);
        } else if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            super.onBackPressed();
        } else {
            switch (MainActivity$10.f17xefc78c71[UserPreferences.getBackButtonBehavior().ordinal()]) {
                case 1:
                    this.drawerLayout.openDrawer(this.navDrawer);
                    return;
                case 2:
                    new Builder(this).setMessage(R.string.close_prompt).setPositiveButton(R.string.yes, new -$$Lambda$MainActivity$C7TxaB0nNZHRRNByPrwsIpRaRIk()).setNegativeButton(R.string.no, null).setCancelable(false).show();
                    return;
                case 3:
                    if (this.lastBackButtonPressTime < System.currentTimeMillis() - AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
                        Toast.makeText(this, R.string.double_tap_toast, 0).show();
                        this.lastBackButtonPressTime = System.currentTimeMillis();
                        return;
                    }
                    super.onBackPressed();
                    return;
                case 4:
                    if (getLastNavFragment().equals(UserPreferences.getBackButtonGoToPage())) {
                        super.onBackPressed();
                        return;
                    } else {
                        loadFragment(UserPreferences.getBackButtonGoToPage(), null);
                        return;
                    }
                default:
                    super.onBackPressed();
                    return;
            }
        }
    }

    private void loadData() {
        this.disposable = Observable.fromCallable(-$$Lambda$ERRoheq1FpvAoMtPU72LGBndRfQ.INSTANCE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$MainActivity$3qwr_xgwK_2qcuFFDI6ewwVOWw0(), -$$Lambda$MainActivity$dVPKZWSVlVppJCouiw0x-XbygMw.INSTANCE);
    }

    public static /* synthetic */ void lambda$loadData$6(MainActivity mainActivity, NavDrawerData result) throws Exception {
        boolean handleIntent = mainActivity.navDrawerData == null;
        mainActivity.navDrawerData = result;
        mainActivity.navAdapter.notifyDataSetChanged();
        if (handleIntent) {
            mainActivity.handleNavIntent();
        }
    }

    public void onEvent(QueueEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEvent(");
        stringBuilder.append(event);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        if (!(event.action == Action.DELETED_MEDIA || event.action == Action.SORTED)) {
            if (event.action != Action.MOVED) {
                loadData();
            }
        }
    }

    public void onEventMainThread(ServiceEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEvent(");
        stringBuilder.append(event);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        if (MainActivity$10.$SwitchMap$de$danoeh$antennapod$core$event$ServiceEvent$Action[event.action.ordinal()] == 1) {
            this.externalPlayerFragment.connectToPlaybackService();
        }
    }

    public void onEventMainThread(ProgressEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEvent(");
        stringBuilder.append(event);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        switch (MainActivity$10.$SwitchMap$de$danoeh$antennapod$core$event$ProgressEvent$Action[event.action.ordinal()]) {
            case 1:
                this.pd = new ProgressDialog(this);
                this.pd.setMessage(event.message);
                this.pd.setIndeterminate(true);
                this.pd.setCancelable(false);
                this.pd.show();
                return;
            case 2:
                ProgressDialog progressDialog = this.pd;
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void onEventMainThread(MessageEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEvent(");
        stringBuilder.append(event);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout), event.message, -1);
        if (event.action != null) {
            snackbar.setAction(getString(R.string.undo), new -$$Lambda$MainActivity$Bj08ATZ_8FlUnHN1CCZ8kHUvQNE(event));
        }
        snackbar.show();
    }

    private void handleNavIntent() {
        Log.d(TAG, "handleNavIntent()");
        Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_FEED_ID)) {
            if (intent.hasExtra(EXTRA_NAV_TYPE)) {
                if (!intent.hasExtra(EXTRA_NAV_INDEX)) {
                    if (intent.hasExtra(EXTRA_FRAGMENT_TAG)) {
                    }
                }
            }
            setIntent(new Intent(this, MainActivity.class));
        }
        int index = intent.getIntExtra(EXTRA_NAV_INDEX, -1);
        String tag = intent.getStringExtra(EXTRA_FRAGMENT_TAG);
        Bundle args = intent.getBundleExtra(EXTRA_FRAGMENT_ARGS);
        long feedId = intent.getLongExtra(EXTRA_FEED_ID, 0);
        if (index >= 0) {
            loadFragment(index, args);
        } else if (tag != null) {
            loadFragment(tag, args);
        } else if (feedId > 0) {
            loadFeedFragmentById(feedId, args);
        }
        setIntent(new Intent(this, MainActivity.class));
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}

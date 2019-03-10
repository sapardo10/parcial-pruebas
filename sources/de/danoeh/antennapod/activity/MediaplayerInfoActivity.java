package de.danoeh.antennapod.activity;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.viewpagerindicator.CirclePageIndicator;
import de.danoeh.antennapod.adapter.ChaptersListAdapter;
import de.danoeh.antennapod.adapter.NavListAdapter;
import de.danoeh.antennapod.adapter.NavListAdapter.ItemAccess;
import de.danoeh.antennapod.core.asynctask.FeedRemover;
import de.danoeh.antennapod.core.event.MessageEvent;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.EventDistributor$EventListener;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.DBReader.NavDrawerData;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.util.download.AutoUpdateManager;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.util.playback.PlaybackController;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.dialog.RenameFeedDialog;
import de.danoeh.antennapod.fragment.AddFeedFragment;
import de.danoeh.antennapod.fragment.ChaptersFragment;
import de.danoeh.antennapod.fragment.DownloadsFragment;
import de.danoeh.antennapod.fragment.EpisodesFragment;
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

public abstract class MediaplayerInfoActivity extends MediaplayerActivity implements NavDrawerActivity {
    private static final String[] NAV_DRAWER_TAGS = new String[]{QueueFragment.TAG, EpisodesFragment.TAG, SubscriptionFragment.TAG, DownloadsFragment.TAG, PlaybackHistoryFragment.TAG, AddFeedFragment.TAG, NavListAdapter.SUBSCRIPTION_LIST_TAG};
    private static final int NUM_CONTENT_FRAGMENTS = 3;
    private static final int POS_CHAPTERS = 2;
    private static final int POS_COVER = 0;
    private static final int POS_DESCR = 1;
    private static final String PREFS = "AudioPlayerActivityPreferences";
    private static final String PREF_KEY_SELECTED_FRAGMENT_POSITION = "selectedFragmentPosition";
    private static final String TAG = "MediaplayerInfoActivity";
    ImageButton butCastDisconnect;
    Button butPlaybackSpeed;
    private final EventDistributor$EventListener contentUpdate = new MediaplayerInfoActivity$2(this);
    private Disposable disposable;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private final ItemAccess itemAccess = new MediaplayerInfoActivity$3(this);
    private int mPosition = -1;
    private Playable media;
    private NavListAdapter navAdapter;
    private View navDrawer;
    private NavDrawerData navDrawerData;
    private ListView navList;
    private ViewPager pager;
    private MediaplayerInfoActivity$MediaplayerInfoPagerAdapter pagerAdapter;

    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportPostponeEnterTransition();
    }

    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        MediaplayerInfoActivity$MediaplayerInfoPagerAdapter mediaplayerInfoActivity$MediaplayerInfoPagerAdapter = this.pagerAdapter;
        if (mediaplayerInfoActivity$MediaplayerInfoPagerAdapter != null) {
            mediaplayerInfoActivity$MediaplayerInfoPagerAdapter.setController(null);
        }
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        EventDistributor.getInstance().unregister(this.contentUpdate);
        saveCurrentFragment();
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        this.drawerLayout = null;
        this.navAdapter = null;
        this.navList = null;
        this.navDrawer = null;
        this.drawerToggle = null;
        this.pager = null;
        this.pagerAdapter = null;
    }

    protected void chooseTheme() {
        setTheme(UserPreferences.getNoTitleTheme());
    }

    void saveCurrentFragment() {
        if (this.pager != null) {
            Log.d(TAG, "Saving preferences");
            getSharedPreferences(PREFS, 0).edit().putInt(PREF_KEY_SELECTED_FRAGMENT_POSITION, this.pager.getCurrentItem()).apply();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ActionBarDrawerToggle actionBarDrawerToggle = this.drawerToggle;
        if (actionBarDrawerToggle != null) {
            actionBarDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    private void loadLastFragment() {
        Log.d(TAG, "Restoring instance state");
        this.pager.setCurrentItem(getSharedPreferences(PREFS, 0).getInt(PREF_KEY_SELECTED_FRAGMENT_POSITION, -1));
    }

    protected void onResume() {
        super.onResume();
        if (this.pagerAdapter != null && this.controller != null && this.controller.getMedia() != this.media) {
            this.media = this.controller.getMedia();
            this.pagerAdapter.onMediaChanged(this.media);
            this.pagerAdapter.setController(this.controller);
        }
        AutoUpdateManager.checkShouldRefreshFeeds(getApplicationContext());
        EventDistributor.getInstance().register(this.contentUpdate);
        EventBus.getDefault().register(this);
        loadData();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    protected void onAwaitingVideoSurface() {
        Log.d(TAG, "onAwaitingVideoSurface was called in audio player -> switching to video player");
        startActivity(new Intent(this, VideoplayerActivity.class));
    }

    protected void postStatusMsg(int resId, boolean showToast) {
        if (resId != R.string.player_preparing_msg) {
        }
        if (showToast) {
            Toast.makeText(this, resId, 0).show();
        }
    }

    protected void clearStatusMsg() {
    }

    protected void setupGUI() {
        super.setupGUI();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        if (VERSION.SDK_INT >= 21) {
            findViewById(R.id.shadow).setVisibility(8);
            ((AppBarLayout) findViewById(R.id.appBar)).setElevation(TypedValue.applyDimension(1, 4.0f, getResources().getDisplayMetrics()));
        }
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.navList = (ListView) findViewById(R.id.nav_list);
        this.navDrawer = findViewById(R.id.nav_layout);
        this.drawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout, R.string.drawer_open, R.string.drawer_close);
        this.drawerToggle.setDrawerIndicatorEnabled(false);
        this.drawerLayout.addDrawerListener(this.drawerToggle);
        this.navAdapter = new NavListAdapter(this.itemAccess, this);
        this.navList.setAdapter(this.navAdapter);
        this.navList.setOnItemClickListener(new -$$Lambda$MediaplayerInfoActivity$fNDWcsi-rkHSXLvWGp_wTr4xAPE());
        this.navList.setOnItemLongClickListener(new -$$Lambda$MediaplayerInfoActivity$oVtvjgT7VSiBG76Ojo9DfpAlV0w());
        registerForContextMenu(this.navList);
        this.drawerToggle.syncState();
        findViewById(R.id.nav_settings).setOnClickListener(new -$$Lambda$MediaplayerInfoActivity$xDJtua3g0jjK1OXbJkc8CnIOAqI());
        this.butPlaybackSpeed = (Button) findViewById(R.id.butPlaybackSpeed);
        this.butCastDisconnect = (ImageButton) findViewById(R.id.butCastDisconnect);
        this.pager = (ViewPager) findViewById(R.id.pager);
        this.pager.setOffscreenPageLimit(3);
        this.pagerAdapter = new MediaplayerInfoActivity$MediaplayerInfoPagerAdapter(getSupportFragmentManager(), this.media);
        this.pagerAdapter.setController(this.controller);
        this.pager.setAdapter(this.pagerAdapter);
        ((CirclePageIndicator) findViewById(R.id.page_indicator)).setViewPager(this.pager);
        loadLastFragment();
        this.pager.onSaveInstanceState();
        this.navList.post(new -$$Lambda$70fzrVtrLjLq2O5mKULt92fKdvM());
    }

    public static /* synthetic */ void lambda$setupGUI$0(MediaplayerInfoActivity mediaplayerInfoActivity, AdapterView parent, View view, int position, long id) {
        int viewType = parent.getAdapter().getItemViewType(position);
        if (viewType != 1) {
            Intent intent = new Intent(mediaplayerInfoActivity, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_NAV_TYPE, viewType);
            intent.putExtra(MainActivity.EXTRA_NAV_INDEX, position);
            mediaplayerInfoActivity.startActivity(intent);
        }
        mediaplayerInfoActivity.drawerLayout.closeDrawer(mediaplayerInfoActivity.navDrawer);
    }

    public static /* synthetic */ boolean lambda$setupGUI$1(MediaplayerInfoActivity mediaplayerInfoActivity, AdapterView parent, View view, int position, long id) {
        if (position < mediaplayerInfoActivity.navAdapter.getTags().size()) {
            mediaplayerInfoActivity.showDrawerPreferencesDialog();
            return true;
        }
        mediaplayerInfoActivity.mPosition = position;
        return false;
    }

    public static /* synthetic */ void lambda$setupGUI$2(MediaplayerInfoActivity mediaplayerInfoActivity, View v) {
        mediaplayerInfoActivity.drawerLayout.closeDrawer(mediaplayerInfoActivity.navDrawer);
        mediaplayerInfoActivity.startActivity(new Intent(mediaplayerInfoActivity, PreferenceActivity.class));
    }

    protected void onPositionObserverUpdate() {
        super.onPositionObserverUpdate();
        notifyMediaPositionChanged();
    }

    protected boolean loadMediaInfo() {
        if (!super.loadMediaInfo()) {
            return false;
        }
        if (this.controller != null && this.controller.getMedia() != this.media) {
            this.media = this.controller.getMedia();
            this.pagerAdapter.onMediaChanged(this.media);
        }
        return true;
    }

    private void notifyMediaPositionChanged() {
        ChaptersFragment chaptersFragment = this.pagerAdapter;
        if (chaptersFragment != null) {
            chaptersFragment = chaptersFragment.getChaptersFragment();
            if (chaptersFragment != null) {
                ChaptersListAdapter adapter = (ChaptersListAdapter) chaptersFragment.getListAdapter();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    protected void onReloadNotification(int notificationCode) {
        if (notificationCode == 2) {
            Log.d(TAG, "ReloadNotification received, switching to Videoplayer now");
            finish();
            startActivity(new Intent(this, VideoplayerActivity.class));
        }
    }

    protected void onBufferStart() {
        postStatusMsg(R.string.player_buffering_msg, false);
    }

    protected void onBufferEnd() {
        clearStatusMsg();
    }

    public PlaybackController getPlaybackController() {
        return this.controller;
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

    protected int getContentViewResourceId() {
        return R.layout.mediaplayerinfo_activity;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        ActionBarDrawerToggle actionBarDrawerToggle = this.drawerToggle;
        return (actionBarDrawerToggle != null && actionBarDrawerToggle.onOptionsItemSelected(item)) || super.onOptionsItemSelected(item);
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
                DBWriter.markFeedRead(feed.getId());
                return true;
            case R.id.mark_all_seen_item:
                DBWriter.markFeedSeen(feed.getId());
                return true;
            case R.id.remove_item:
                FeedRemover remover = new FeedRemover(this, feed);
                new MediaplayerInfoActivity$1(this, this, R.string.remove_feed_label, getString(R.string.feed_delete_confirmation_msg, new Object[]{feed.getTitle()}), feed, remover).createNewDialog().show();
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
            return;
        }
        ViewPager viewPager = this.pager;
        if (viewPager != null) {
            if (viewPager.getCurrentItem() != 0) {
                viewPager = this.pager;
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                return;
            }
        }
        super.onBackPressed();
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
                builder.setMultiChoiceItems(navLabels, checked, new -$$Lambda$MediaplayerInfoActivity$4jtK0qJBzxabYbcpkkxRZFWQ1Ao(hiddenDrawerItems));
                builder.setPositiveButton(R.string.confirm_label, new -$$Lambda$MediaplayerInfoActivity$pkMDwj-fO4PrdNELQk-8NzutKIE(hiddenDrawerItems));
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

    private void loadData() {
        this.disposable = Observable.fromCallable(-$$Lambda$ERRoheq1FpvAoMtPU72LGBndRfQ.INSTANCE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$MediaplayerInfoActivity$fTQ0TWFucawgFNto8f35BtoGMEg(), -$$Lambda$MediaplayerInfoActivity$N_NY4Q-WVDpDKBdW--cY89ZDpG8.INSTANCE);
    }

    public static /* synthetic */ void lambda$loadData$5(MediaplayerInfoActivity mediaplayerInfoActivity, NavDrawerData result) throws Exception {
        mediaplayerInfoActivity.navDrawerData = result;
        NavListAdapter navListAdapter = mediaplayerInfoActivity.navAdapter;
        if (navListAdapter != null) {
            navListAdapter.notifyDataSetChanged();
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
            snackbar.setAction(getString(R.string.undo), new -$$Lambda$MediaplayerInfoActivity$nn4iclsTb5yH1aB4CXgrrx9-kW0(event));
        }
        snackbar.show();
    }
}

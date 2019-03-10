package de.danoeh.antennapod.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.GridView;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.adapter.SubscriptionsAdapter;
import de.danoeh.antennapod.adapter.SubscriptionsAdapter.ItemAccess;
import de.danoeh.antennapod.core.asynctask.FeedRemover;
import de.danoeh.antennapod.core.dialog.ConfirmationDialog;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.core.feed.EventDistributor$EventListener;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.storage.DBReader.NavDrawerData;
import de.danoeh.antennapod.core.util.FeedItemUtil;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.dialog.RenameFeedDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SubscriptionFragment extends Fragment {
    private static final int EVENTS = 3;
    public static final String TAG = "SubscriptionFragment";
    private final EventDistributor$EventListener contentUpdate = new C10755();
    private Disposable disposable;
    private final ItemAccess itemAccess = new C10766();
    private int mPosition = -1;
    private NavDrawerData navDrawerData;
    private SubscriptionsAdapter subscriptionAdapter;
    private GridView subscriptionGridLayout;

    /* renamed from: de.danoeh.antennapod.fragment.SubscriptionFragment$5 */
    class C10755 extends EventDistributor$EventListener {
        C10755() {
        }

        public void update(EventDistributor eventDistributor, Integer arg) {
            if ((arg.intValue() & 3) != 0) {
                Log.d(SubscriptionFragment.TAG, "Received contentUpdate Intent.");
                SubscriptionFragment.this.loadSubscriptions();
            }
        }
    }

    /* renamed from: de.danoeh.antennapod.fragment.SubscriptionFragment$6 */
    class C10766 implements ItemAccess {
        C10766() {
        }

        public int getCount() {
            if (SubscriptionFragment.this.navDrawerData != null) {
                return SubscriptionFragment.this.navDrawerData.feeds.size();
            }
            return 0;
        }

        public Feed getItem(int position) {
            if (SubscriptionFragment.this.navDrawerData == null || position < 0 || position >= SubscriptionFragment.this.navDrawerData.feeds.size()) {
                return null;
            }
            return (Feed) SubscriptionFragment.this.navDrawerData.feeds.get(position);
        }

        public int getFeedCounter(long feedId) {
            return SubscriptionFragment.this.navDrawerData != null ? SubscriptionFragment.this.navDrawerData.feedCounters.get(feedId) : 0;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_subscriptions, container, false);
        this.subscriptionGridLayout = (GridView) root.findViewById(R.id.subscriptions_grid);
        registerForContextMenu(this.subscriptionGridLayout);
        return root;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.subscriptionAdapter = new SubscriptionsAdapter((MainActivity) getActivity(), this.itemAccess);
        this.subscriptionGridLayout.setAdapter(this.subscriptionAdapter);
        loadSubscriptions();
        this.subscriptionGridLayout.setOnItemClickListener(this.subscriptionAdapter);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle((int) R.string.subscriptions_label);
        }
        EventDistributor.getInstance().register(this.contentUpdate);
    }

    public void onDestroy() {
        super.onDestroy();
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
    }

    private void loadSubscriptions() {
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        this.disposable = Observable.fromCallable(-$$Lambda$ERRoheq1FpvAoMtPU72LGBndRfQ.INSTANCE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$SubscriptionFragment$xippqkUTW_xnKr0RkotcPZCxgi8(), -$$Lambda$SubscriptionFragment$ugza2ilFMiXKkMUOYY7NhXX62XY.INSTANCE);
    }

    public static /* synthetic */ void lambda$loadSubscriptions$0(SubscriptionFragment subscriptionFragment, NavDrawerData result) throws Exception {
        subscriptionFragment.navDrawerData = result;
        subscriptionFragment.subscriptionAdapter.notifyDataSetChanged();
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int position = ((AdapterContextMenuInfo) menuInfo).position;
        Feed selectedObject = this.subscriptionAdapter.getItem(position);
        if (selectedObject.equals(SubscriptionsAdapter.ADD_ITEM_OBJ)) {
            this.mPosition = position;
            return;
        }
        Feed feed = selectedObject;
        getActivity().getMenuInflater().inflate(R.menu.nav_feed_context, menu);
        menu.setHeaderTitle(feed.getTitle());
        this.mPosition = position;
    }

    public boolean onContextItemSelected(MenuItem item) {
        int position = this.mPosition;
        this.mPosition = -1;
        if (position < 0) {
            return false;
        }
        Feed selectedObject = this.subscriptionAdapter.getItem(position);
        if (selectedObject.equals(SubscriptionsAdapter.ADD_ITEM_OBJ)) {
            return false;
        }
        Feed feed = selectedObject;
        final Feed feed2;
        switch (item.getItemId()) {
            case R.id.mark_all_read_item:
                feed2 = feed;
                new ConfirmationDialog(getActivity(), R.string.mark_all_read_label, R.string.mark_all_read_confirmation_msg) {
                    public void onConfirmButtonPressed(DialogInterface dialog) {
                        dialog.dismiss();
                        Observable.fromCallable(new -$$Lambda$SubscriptionFragment$2$59e9OUcGZu6-eWGr0GZ8XYi8zZ8(feed2)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$SubscriptionFragment$2$ENShgfzgq96N2owI_sE0m1PQJyw(), -$$Lambda$SubscriptionFragment$2$UvWOhkDK5YND2oYzVvMBYl3E8zQ.INSTANCE);
                    }
                }.createNewDialog().show();
                return true;
            case R.id.mark_all_seen_item:
                feed2 = feed;
                new ConfirmationDialog(getActivity(), R.string.mark_all_seen_label, R.string.mark_all_seen_confirmation_msg) {
                    public void onConfirmButtonPressed(DialogInterface dialog) {
                        dialog.dismiss();
                        Observable.fromCallable(new -$$Lambda$SubscriptionFragment$1$2JAntok1_ecx8sEonRleva7q8R0(feed2)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$SubscriptionFragment$1$4r8_TSwRtIE0teREH5ip5lKBhN8(), -$$Lambda$SubscriptionFragment$1$-qQyy3RTgTtI18pDsTxWOVe9z1Y.INSTANCE);
                    }
                }.createNewDialog().show();
                return true;
            case R.id.remove_item:
                final FeedRemover remover = new FeedRemover(getContext(), feed) {
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);
                        SubscriptionFragment.this.loadSubscriptions();
                    }
                };
                feed2 = feed;
                new ConfirmationDialog(getContext(), R.string.remove_feed_label, getString(R.string.feed_delete_confirmation_msg, feed.getTitle())) {
                    public void onConfirmButtonPressed(DialogInterface dialog) {
                        dialog.dismiss();
                        long mediaId = PlaybackPreferences.getCurrentlyPlayingFeedMediaId();
                        if (mediaId > 0) {
                            if (FeedItemUtil.indexOfItemWithMediaId(feed2.getItems(), mediaId) >= 0) {
                                Log.d(SubscriptionFragment.TAG, "Currently playing episode is about to be deleted, skipping");
                                remover.skipOnCompletion = true;
                                if (PlaybackPreferences.getCurrentPlayerStatus() == 1) {
                                    IntentUtils.sendLocalBroadcast(SubscriptionFragment.this.getContext(), PlaybackService.ACTION_PAUSE_PLAY_CURRENT_EPISODE);
                                }
                            }
                        }
                        remover.executeAsync();
                    }
                }.createNewDialog().show();
                return true;
            case R.id.rename_item:
                new RenameFeedDialog(getActivity(), feed).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onResume() {
        super.onResume();
        loadSubscriptions();
    }
}

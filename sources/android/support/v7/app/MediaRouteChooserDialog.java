package android.support.v7.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.Callback;
import android.support.v7.media.MediaRouter.RouteGroup;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.support.v7.mediarouter.C0299R;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MediaRouteChooserDialog extends AppCompatDialog {
    static final int MSG_UPDATE_ROUTES = 1;
    static final String TAG = "MediaRouteChooserDialog";
    private static final long UPDATE_ROUTES_DELAY_MS = 300;
    private RouteAdapter mAdapter;
    private boolean mAttachedToWindow;
    private final MediaRouterCallback mCallback;
    private final Handler mHandler;
    private long mLastUpdateTime;
    private ListView mListView;
    private final MediaRouter mRouter;
    private ArrayList<RouteInfo> mRoutes;
    private MediaRouteSelector mSelector;
    private TextView mTitleView;

    /* renamed from: android.support.v7.app.MediaRouteChooserDialog$1 */
    class C02731 extends Handler {
        C02731() {
        }

        public void handleMessage(Message message) {
            if (message.what == 1) {
                MediaRouteChooserDialog.this.updateRoutes((List) message.obj);
            }
        }
    }

    private final class RouteAdapter extends ArrayAdapter<RouteInfo> implements OnItemClickListener {
        private final Drawable mDefaultIcon;
        private final LayoutInflater mInflater;
        private final Drawable mSpeakerGroupIcon;
        private final Drawable mSpeakerIcon;
        private final Drawable mTvIcon;

        public RouteAdapter(Context context, List<RouteInfo> routes) {
            super(context, 0, routes);
            this.mInflater = LayoutInflater.from(context);
            TypedArray styledAttributes = getContext().obtainStyledAttributes(new int[]{C0299R.attr.mediaRouteDefaultIconDrawable, C0299R.attr.mediaRouteTvIconDrawable, C0299R.attr.mediaRouteSpeakerIconDrawable, C0299R.attr.mediaRouteSpeakerGroupIconDrawable});
            this.mDefaultIcon = styledAttributes.getDrawable(0);
            this.mTvIcon = styledAttributes.getDrawable(1);
            this.mSpeakerIcon = styledAttributes.getDrawable(2);
            this.mSpeakerGroupIcon = styledAttributes.getDrawable(3);
            styledAttributes.recycle();
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int position) {
            return ((RouteInfo) getItem(position)).isEnabled();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iconView;
            View view = convertView;
            if (view == null) {
                view = this.mInflater.inflate(C0299R.layout.mr_chooser_list_item, parent, false);
            }
            RouteInfo route = (RouteInfo) getItem(position);
            TextView text1 = (TextView) view.findViewById(C0299R.id.mr_chooser_route_name);
            TextView text2 = (TextView) view.findViewById(C0299R.id.mr_chooser_route_desc);
            text1.setText(route.getName());
            String description = route.getDescription();
            boolean z = true;
            if (route.getConnectionState() != 2) {
                if (route.getConnectionState() != 1) {
                    z = false;
                    if (z || TextUtils.isEmpty(description)) {
                        text1.setGravity(16);
                        text2.setVisibility(8);
                        text2.setText("");
                    } else {
                        text1.setGravity(80);
                        text2.setVisibility(0);
                        text2.setText(description);
                    }
                    view.setEnabled(route.isEnabled());
                    iconView = (ImageView) view.findViewById(C0299R.id.mr_chooser_route_icon);
                    if (iconView != null) {
                        iconView.setImageDrawable(getIconDrawable(route));
                    }
                    return view;
                }
            }
            if (z) {
            }
            text1.setGravity(16);
            text2.setVisibility(8);
            text2.setText("");
            view.setEnabled(route.isEnabled());
            iconView = (ImageView) view.findViewById(C0299R.id.mr_chooser_route_icon);
            if (iconView != null) {
                iconView.setImageDrawable(getIconDrawable(route));
            }
            return view;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            RouteInfo route = (RouteInfo) getItem(position);
            if (route.isEnabled()) {
                route.select();
                MediaRouteChooserDialog.this.dismiss();
            }
        }

        private Drawable getIconDrawable(RouteInfo route) {
            Uri iconUri = route.getIconUri();
            if (iconUri != null) {
                try {
                    Drawable drawable = Drawable.createFromStream(getContext().getContentResolver().openInputStream(iconUri), null);
                    if (drawable != null) {
                        return drawable;
                    }
                } catch (IOException e) {
                    String str = MediaRouteChooserDialog.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Failed to load ");
                    stringBuilder.append(iconUri);
                    Log.w(str, stringBuilder.toString(), e);
                }
            }
            return getDefaultIconDrawable(route);
        }

        private Drawable getDefaultIconDrawable(RouteInfo route) {
            switch (route.getDeviceType()) {
                case 1:
                    return this.mTvIcon;
                case 2:
                    return this.mSpeakerIcon;
                default:
                    if (route instanceof RouteGroup) {
                        return this.mSpeakerGroupIcon;
                    }
                    return this.mDefaultIcon;
            }
        }
    }

    static final class RouteComparator implements Comparator<RouteInfo> {
        public static final RouteComparator sInstance = new RouteComparator();

        RouteComparator() {
        }

        public int compare(RouteInfo lhs, RouteInfo rhs) {
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    }

    private final class MediaRouterCallback extends Callback {
        MediaRouterCallback() {
        }

        public void onRouteAdded(MediaRouter router, RouteInfo info) {
            MediaRouteChooserDialog.this.refreshRoutes();
        }

        public void onRouteRemoved(MediaRouter router, RouteInfo info) {
            MediaRouteChooserDialog.this.refreshRoutes();
        }

        public void onRouteChanged(MediaRouter router, RouteInfo info) {
            MediaRouteChooserDialog.this.refreshRoutes();
        }

        public void onRouteSelected(MediaRouter router, RouteInfo route) {
            MediaRouteChooserDialog.this.dismiss();
        }
    }

    public MediaRouteChooserDialog(Context context) {
        this(context, 0);
    }

    public MediaRouteChooserDialog(Context context, int theme) {
        Context createThemedDialogContext = MediaRouterThemeHelper.createThemedDialogContext(context, theme, false);
        super(createThemedDialogContext, MediaRouterThemeHelper.createThemedDialogStyle(createThemedDialogContext));
        this.mSelector = MediaRouteSelector.EMPTY;
        this.mHandler = new C02731();
        this.mRouter = MediaRouter.getInstance(getContext());
        this.mCallback = new MediaRouterCallback();
    }

    @NonNull
    public MediaRouteSelector getRouteSelector() {
        return this.mSelector;
    }

    public void setRouteSelector(@NonNull MediaRouteSelector selector) {
        if (selector == null) {
            throw new IllegalArgumentException("selector must not be null");
        } else if (!this.mSelector.equals(selector)) {
            this.mSelector = selector;
            if (this.mAttachedToWindow) {
                this.mRouter.removeCallback(this.mCallback);
                this.mRouter.addCallback(selector, this.mCallback, 1);
            }
            refreshRoutes();
        }
    }

    public void onFilterRoutes(@NonNull List<RouteInfo> routes) {
        int i = routes.size();
        while (true) {
            int i2 = i - 1;
            if (i > 0) {
                if (!onFilterRoute((RouteInfo) routes.get(i2))) {
                    routes.remove(i2);
                }
                i = i2;
            } else {
                return;
            }
        }
    }

    public boolean onFilterRoute(@NonNull RouteInfo route) {
        if (!route.isDefaultOrBluetooth() && route.isEnabled()) {
            if (route.matchesSelector(this.mSelector)) {
                return true;
            }
        }
        return false;
    }

    public void setTitle(CharSequence title) {
        this.mTitleView.setText(title);
    }

    public void setTitle(int titleId) {
        this.mTitleView.setText(titleId);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0299R.layout.mr_chooser_dialog);
        this.mRoutes = new ArrayList();
        this.mAdapter = new RouteAdapter(getContext(), this.mRoutes);
        this.mListView = (ListView) findViewById(C0299R.id.mr_chooser_list);
        this.mListView.setAdapter(this.mAdapter);
        this.mListView.setOnItemClickListener(this.mAdapter);
        this.mListView.setEmptyView(findViewById(16908292));
        this.mTitleView = (TextView) findViewById(C0299R.id.mr_chooser_title);
        updateLayout();
    }

    void updateLayout() {
        getWindow().setLayout(MediaRouteDialogHelper.getDialogWidth(getContext()), -2);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        this.mRouter.addCallback(this.mSelector, this.mCallback, 1);
        refreshRoutes();
    }

    public void onDetachedFromWindow() {
        this.mAttachedToWindow = false;
        this.mRouter.removeCallback(this.mCallback);
        this.mHandler.removeMessages(1);
        super.onDetachedFromWindow();
    }

    public void refreshRoutes() {
        if (this.mAttachedToWindow) {
            ArrayList<RouteInfo> routes = new ArrayList(this.mRouter.getRoutes());
            onFilterRoutes(routes);
            Collections.sort(routes, RouteComparator.sInstance);
            if (SystemClock.uptimeMillis() - this.mLastUpdateTime >= UPDATE_ROUTES_DELAY_MS) {
                updateRoutes(routes);
                return;
            }
            this.mHandler.removeMessages(1);
            Handler handler = this.mHandler;
            handler.sendMessageAtTime(handler.obtainMessage(1, routes), this.mLastUpdateTime + UPDATE_ROUTES_DELAY_MS);
        }
    }

    void updateRoutes(List<RouteInfo> routes) {
        this.mLastUpdateTime = SystemClock.uptimeMillis();
        this.mRoutes.clear();
        this.mRoutes.addAll(routes);
        this.mAdapter.notifyDataSetChanged();
    }
}

package de.danoeh.antennapod.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import de.danoeh.antennapod.activity.OnlineFeedViewActivity;
import de.danoeh.antennapod.adapter.itunes.ItunesAdapter;
import de.danoeh.antennapod.adapter.itunes.ItunesAdapter.Podcast;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.service.download.AntennapodHttpClient;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.menuhandler.MenuItemUtils;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class ItunesSearchFragment extends Fragment {
    private static final String API_URL = "https://itunes.apple.com/search?media=podcast&term=%s";
    private static final String TAG = "ItunesSearchFragment";
    private ItunesAdapter adapter;
    private Button butRetry;
    private Disposable disposable;
    private GridView gridView;
    private ProgressBar progressBar;
    private List<Podcast> searchResults;
    private List<Podcast> topList;
    private TextView txtvEmpty;
    private TextView txtvError;

    /* renamed from: de.danoeh.antennapod.fragment.ItunesSearchFragment$2 */
    class C10602 implements OnActionExpandListener {
        C10602() {
        }

        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        public boolean onMenuItemActionCollapse(MenuItem item) {
            if (ItunesSearchFragment.this.searchResults != null) {
                ItunesSearchFragment.this.searchResults = null;
                ItunesSearchFragment itunesSearchFragment = ItunesSearchFragment.this;
                itunesSearchFragment.updateData(itunesSearchFragment.topList);
            }
            return true;
        }
    }

    private void updateData(List<Podcast> result) {
        this.searchResults = result;
        this.adapter.clear();
        if (result == null || result.size() <= 0) {
            this.gridView.setVisibility(8);
            this.txtvEmpty.setVisibility(0);
            return;
        }
        this.gridView.setVisibility(0);
        this.txtvEmpty.setVisibility(8);
        for (Podcast p : result) {
            this.adapter.add(p);
        }
        this.adapter.notifyDataSetInvalidated();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_itunes_search, container, false);
        this.gridView = (GridView) root.findViewById(R.id.gridView);
        this.adapter = new ItunesAdapter(getActivity(), new ArrayList());
        this.gridView.setAdapter(this.adapter);
        this.gridView.setOnItemClickListener(new -$$Lambda$ItunesSearchFragment$XHoo8H1wlSrzOVDdct8BEN00aks());
        this.progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        this.txtvError = (TextView) root.findViewById(R.id.txtvError);
        this.butRetry = (Button) root.findViewById(R.id.butRetry);
        this.txtvEmpty = (TextView) root.findViewById(16908292);
        loadToplist();
        return root;
    }

    public static /* synthetic */ void lambda$onCreateView$3(ItunesSearchFragment itunesSearchFragment, AdapterView parent, View view1, int position, long id) {
        Podcast podcast = (Podcast) itunesSearchFragment.searchResults.get(position);
        if (podcast.feedUrl != null) {
            if (podcast.feedUrl.contains("itunes.apple.com")) {
                itunesSearchFragment.gridView.setVisibility(8);
                itunesSearchFragment.progressBar.setVisibility(0);
                itunesSearchFragment.disposable = Single.create(new -$$Lambda$ItunesSearchFragment$8SKiker3uTBCeDwaX4bzXtWDgUc(itunesSearchFragment, podcast)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$ItunesSearchFragment$jDcEhMn_TzkUFL7Xm9BBLOB_2L4(itunesSearchFragment), new -$$Lambda$ItunesSearchFragment$x6Hp6iTAyyO84gP97VXLzS2XzgA(itunesSearchFragment));
            } else {
                Intent intent = new Intent(itunesSearchFragment.getActivity(), OnlineFeedViewActivity.class);
                intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, podcast.feedUrl);
                intent.putExtra("title", "iTunes");
                itunesSearchFragment.startActivity(intent);
            }
        }
    }

    public static /* synthetic */ void lambda$null$0(ItunesSearchFragment itunesSearchFragment, Podcast podcast, SingleEmitter emitter) throws Exception {
        try {
            Response response = AntennapodHttpClient.getHttpClient().newCall(new Builder().url(podcast.feedUrl).header("User-Agent", ClientConfig.USER_AGENT).build()).execute();
            if (response.isSuccessful()) {
                emitter.onSuccess(new JSONObject(response.body().string()).getJSONArray("results").getJSONObject(0).getString("feedUrl"));
            } else {
                String prefix = itunesSearchFragment.getString(R.string.error_msg_prefix);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(prefix);
                stringBuilder.append(response);
                emitter.onError(new IOException(stringBuilder.toString()));
            }
        } catch (Exception e) {
            emitter.onError(e);
        }
    }

    public static /* synthetic */ void lambda$null$1(ItunesSearchFragment itunesSearchFragment, String feedUrl) throws Exception {
        itunesSearchFragment.progressBar.setVisibility(8);
        itunesSearchFragment.gridView.setVisibility(0);
        Intent intent = new Intent(itunesSearchFragment.getActivity(), OnlineFeedViewActivity.class);
        intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, feedUrl);
        intent.putExtra("title", "iTunes");
        itunesSearchFragment.startActivity(intent);
    }

    public static /* synthetic */ void lambda$null$2(ItunesSearchFragment itunesSearchFragment, Throwable error) throws Exception {
        Log.e(TAG, Log.getStackTraceString(error));
        itunesSearchFragment.progressBar.setVisibility(8);
        itunesSearchFragment.gridView.setVisibility(0);
        String prefix = itunesSearchFragment.getString(R.string.error_msg_prefix);
        MaterialDialog.Builder builder = new MaterialDialog.Builder(itunesSearchFragment.getActivity());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(prefix);
        stringBuilder.append(StringUtils.SPACE);
        stringBuilder.append(error.getMessage());
        builder.content(stringBuilder.toString()).neutralText(17039370).show();
    }

    public void onDestroy() {
        super.onDestroy();
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        this.adapter = null;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.itunes_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView sv = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemUtils.adjustTextColor(getActivity(), sv);
        sv.setQueryHint(getString(R.string.search_itunes_label));
        sv.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String s) {
                sv.clearFocus();
                ItunesSearchFragment.this.search(s);
                return true;
            }

            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchItem, new C10602());
    }

    private void loadToplist() {
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        this.gridView.setVisibility(8);
        this.txtvError.setVisibility(8);
        this.butRetry.setVisibility(8);
        this.txtvEmpty.setVisibility(8);
        this.progressBar.setVisibility(0);
        this.disposable = Single.create(new -$$Lambda$ItunesSearchFragment$cpqkFax07yhvoIxgFRFUjvetwkw()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$ItunesSearchFragment$Aou4kEGCMOAUhnFSugOJ_NZjb14(), new -$$Lambda$ItunesSearchFragment$sU4rBkYIBcsgllNDwArlPq--PRM());
    }

    public static /* synthetic */ void lambda$loadToplist$4(ItunesSearchFragment itunesSearchFragment, SingleEmitter emitter) throws Exception {
        String lang = Locale.getDefault().getLanguage();
        String url = new StringBuilder();
        url.append("https://itunes.apple.com/");
        url.append(lang);
        url.append("/rss/toppodcasts/limit=25/explicit=true/json");
        url = url.toString();
        OkHttpClient client = AntennapodHttpClient.getHttpClient();
        Builder httpReq = new Builder().url(url).header("User-Agent", ClientConfig.USER_AGENT);
        List<Podcast> results = new ArrayList();
        try {
            Response response = client.newCall(httpReq.build()).execute();
            if (!response.isSuccessful()) {
                response = client.newCall(new Builder().url("https://itunes.apple.com/us/rss/toppodcasts/limit=25/explicit=true/json").header("User-Agent", ClientConfig.USER_AGENT).build()).execute();
            }
            if (response.isSuccessful()) {
                JSONArray entries = new JSONObject(response.body().string()).getJSONObject(PodDBAdapter.KEY_FEED).getJSONArray("entry");
                for (int i = 0; i < entries.length(); i++) {
                    results.add(Podcast.fromToplist(entries.getJSONObject(i)));
                }
            } else {
                String prefix = itunesSearchFragment.getString(R.string.error_msg_prefix);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(prefix);
                stringBuilder.append(response);
                emitter.onError(new IOException(stringBuilder.toString()));
            }
        } catch (Exception e) {
            emitter.onError(e);
        }
        emitter.onSuccess(results);
    }

    public static /* synthetic */ void lambda$loadToplist$5(ItunesSearchFragment itunesSearchFragment, List podcasts) throws Exception {
        itunesSearchFragment.progressBar.setVisibility(8);
        itunesSearchFragment.topList = podcasts;
        itunesSearchFragment.updateData(itunesSearchFragment.topList);
    }

    public static /* synthetic */ void lambda$loadToplist$7(ItunesSearchFragment itunesSearchFragment, Throwable error) throws Exception {
        Log.e(TAG, Log.getStackTraceString(error));
        itunesSearchFragment.progressBar.setVisibility(8);
        itunesSearchFragment.txtvError.setText(error.toString());
        itunesSearchFragment.txtvError.setVisibility(0);
        itunesSearchFragment.butRetry.setOnClickListener(new -$$Lambda$ItunesSearchFragment$LnkVLde5MUclLzniKHOZjj99d_o(itunesSearchFragment));
        itunesSearchFragment.butRetry.setVisibility(0);
    }

    private void search(String query) {
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        this.gridView.setVisibility(8);
        this.txtvError.setVisibility(8);
        this.butRetry.setVisibility(8);
        this.txtvEmpty.setVisibility(8);
        this.progressBar.setVisibility(0);
        this.disposable = Single.create(new -$$Lambda$ItunesSearchFragment$eG3e11TmbFYWWwAoIgEqIx3Om8Y(this, query)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$ItunesSearchFragment$7QxV3OQ-b2JME64n2rum6VFSN9c(), new -$$Lambda$ItunesSearchFragment$41P3saGWHSd73UL5oBdrpZjxWEA(this, query));
    }

    public static /* synthetic */ void lambda$search$8(ItunesSearchFragment itunesSearchFragment, String query, SingleEmitter subscriber) throws Exception {
        String encodedQuery = null;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        if (encodedQuery == null) {
            encodedQuery = query;
        }
        String formattedUrl = API_URL;
        Object[] objArr = new Object[1];
        int i = 0;
        objArr[0] = encodedQuery;
        formattedUrl = String.format(formattedUrl, objArr);
        OkHttpClient client = AntennapodHttpClient.getHttpClient();
        Builder httpReq = new Builder().url(formattedUrl).header("User-Agent", ClientConfig.USER_AGENT);
        List<Podcast> podcasts = new ArrayList();
        try {
            Response response = client.newCall(httpReq.build()).execute();
            if (response.isSuccessful()) {
                JSONArray j = new JSONObject(response.body().string()).getJSONArray("results");
                while (i < j.length()) {
                    podcasts.add(Podcast.fromSearch(j.getJSONObject(i)));
                    i++;
                }
            } else {
                String prefix = itunesSearchFragment.getString(R.string.error_msg_prefix);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(prefix);
                stringBuilder.append(response);
                subscriber.onError(new IOException(stringBuilder.toString()));
            }
        } catch (Exception e2) {
            subscriber.onError(e2);
        }
        subscriber.onSuccess(podcasts);
    }

    public static /* synthetic */ void lambda$search$9(ItunesSearchFragment itunesSearchFragment, List podcasts) throws Exception {
        itunesSearchFragment.progressBar.setVisibility(8);
        itunesSearchFragment.updateData(podcasts);
    }

    public static /* synthetic */ void lambda$search$11(ItunesSearchFragment itunesSearchFragment, String query, Throwable error) throws Exception {
        Log.e(TAG, Log.getStackTraceString(error));
        itunesSearchFragment.progressBar.setVisibility(8);
        itunesSearchFragment.txtvError.setText(error.toString());
        itunesSearchFragment.txtvError.setVisibility(0);
        itunesSearchFragment.butRetry.setOnClickListener(new -$$Lambda$ItunesSearchFragment$9o5JrccVgCH8fsy4dZ24gApGXf0(itunesSearchFragment, query));
        itunesSearchFragment.butRetry.setVisibility(0);
    }
}

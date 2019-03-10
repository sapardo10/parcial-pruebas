package de.danoeh.antennapod.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.activity.OnlineFeedViewActivity;
import de.danoeh.antennapod.debug.R;

public class AddFeedFragment extends Fragment {
    private static final String ARG_FEED_URL = "feedurl";
    public static final String TAG = "AddFeedFragment";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.addfeed, container, false);
        EditText etxtFeedurl = (EditText) root.findViewById(R.id.etxtFeedurl);
        Bundle args = getArguments();
        if (args != null && args.getString(ARG_FEED_URL) != null) {
            etxtFeedurl.setText(args.getString(ARG_FEED_URL));
        }
        Button butSearchITunes = (Button) root.findViewById(R.id.butSearchItunes);
        Button butBrowserGpoddernet = (Button) root.findViewById(R.id.butBrowseGpoddernet);
        Button butSearchFyyd = (Button) root.findViewById(R.id.butSearchFyyd);
        Button butOpmlImport = (Button) root.findViewById(R.id.butOpmlImport);
        Button butConfirm = (Button) root.findViewById(R.id.butConfirm);
        MainActivity activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle((int) R.string.add_feed_label);
        butSearchITunes.setOnClickListener(new -$$Lambda$AddFeedFragment$1fIiP09xEWJCf9fLyhZxDwhbvcc(activity));
        butBrowserGpoddernet.setOnClickListener(new -$$Lambda$AddFeedFragment$9SuDjxEu-IGe8MEkmJxmLGknYtA(activity));
        butSearchFyyd.setOnClickListener(new -$$Lambda$AddFeedFragment$ZKURlzJ0T3XpQvsAFIGS2aSWL1k(activity));
        butOpmlImport.setOnClickListener(new -$$Lambda$AddFeedFragment$oryctsE8jlsxjUVjjHFnraJywww());
        butConfirm.setOnClickListener(new -$$Lambda$AddFeedFragment$VuanU8mlPZVidVGRVcKNRK3rBUg(this, etxtFeedurl));
        return root;
    }

    public static /* synthetic */ void lambda$onCreateView$4(AddFeedFragment addFeedFragment, EditText etxtFeedurl, View v) {
        Intent intent = new Intent(addFeedFragment.getActivity(), OnlineFeedViewActivity.class);
        intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, etxtFeedurl.getText().toString());
        intent.putExtra("title", addFeedFragment.getString(R.string.add_feed_label));
        addFeedFragment.startActivity(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }
}

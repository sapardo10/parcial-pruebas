package de.danoeh.antennapod.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import de.danoeh.antennapod.adapter.StatisticsListAdapter;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.DBReader.StatisticsData;
import de.danoeh.antennapod.core.storage.DBReader.StatisticsItem;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.debug.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StatisticsActivity extends AppCompatActivity implements OnItemClickListener {
    private static final String PREF_COUNT_ALL = "countAll";
    private static final String PREF_NAME = "StatisticsActivityPrefs";
    private static final String TAG = StatisticsActivity.class.getSimpleName();
    private boolean countAll = false;
    private Disposable disposable;
    private ListView feedStatisticsList;
    private StatisticsListAdapter listAdapter;
    private SharedPreferences prefs;
    private ProgressBar progressBar;
    private TextView totalTimeTextView;

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.statistics_activity);
        this.prefs = getSharedPreferences(PREF_NAME, 0);
        this.countAll = this.prefs.getBoolean(PREF_COUNT_ALL, false);
        this.totalTimeTextView = (TextView) findViewById(R.id.total_time);
        this.feedStatisticsList = (ListView) findViewById(R.id.statistics_list);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.listAdapter = new StatisticsListAdapter(this);
        this.listAdapter.setCountAll(this.countAll);
        this.feedStatisticsList.setAdapter(this.listAdapter);
        this.feedStatisticsList.setOnItemClickListener(this);
    }

    public void onResume() {
        super.onResume();
        refreshStatistics();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.statistics, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            finish();
            return true;
        } else if (item.getItemId() != R.id.statistics_mode) {
            return super.onOptionsItemSelected(item);
        } else {
            selectStatisticsMode();
            return true;
        }
    }

    private void selectStatisticsMode() {
        View contentView = View.inflate(this, R.layout.statistics_mode_select_dialog, null);
        Builder builder = new Builder(this);
        builder.setView(contentView);
        builder.setTitle((int) R.string.statistics_mode);
        if (this.countAll) {
            ((RadioButton) contentView.findViewById(R.id.statistics_mode_count_all)).setChecked(true);
        } else {
            ((RadioButton) contentView.findViewById(R.id.statistics_mode_normal)).setChecked(true);
        }
        builder.setPositiveButton(17039370, new -$$Lambda$StatisticsActivity$wEoUrCgc0e26yUEV6Z5GEX0DwRc(this, contentView));
        builder.show();
    }

    public static /* synthetic */ void lambda$selectStatisticsMode$0(StatisticsActivity statisticsActivity, View contentView, DialogInterface dialog, int which) {
        statisticsActivity.countAll = ((RadioButton) contentView.findViewById(R.id.statistics_mode_count_all)).isChecked();
        statisticsActivity.listAdapter.setCountAll(statisticsActivity.countAll);
        statisticsActivity.prefs.edit().putBoolean(PREF_COUNT_ALL, statisticsActivity.countAll).apply();
        statisticsActivity.refreshStatistics();
    }

    private void refreshStatistics() {
        this.progressBar.setVisibility(0);
        this.totalTimeTextView.setVisibility(8);
        this.feedStatisticsList.setVisibility(8);
        loadStatistics();
    }

    private void loadStatistics() {
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        this.disposable = Observable.fromCallable(new -$$Lambda$StatisticsActivity$uNQDYDR7ocMOvbQUdDpG1hpoC1o()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$StatisticsActivity$LNBY_wN7bEKOFNrRfWhiEFcZ0Ns(), -$$Lambda$StatisticsActivity$mugTSmo0dCvqF4-2NIBYHbNPsQU.INSTANCE);
    }

    public static /* synthetic */ void lambda$loadStatistics$2(StatisticsActivity statisticsActivity, StatisticsData result) throws Exception {
        statisticsActivity.totalTimeTextView.setText(Converter.shortLocalizedDuration(statisticsActivity, statisticsActivity.countAll ? result.totalTimeCountAll : result.totalTime));
        statisticsActivity.listAdapter.update(result.feedTime);
        statisticsActivity.progressBar.setVisibility(8);
        statisticsActivity.totalTimeTextView.setVisibility(0);
        statisticsActivity.feedStatisticsList.setVisibility(0);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        StatisticsItem stats = this.listAdapter.getItem(position);
        Builder dialog = new Builder(this);
        dialog.setTitle(stats.feed.getTitle());
        Object[] objArr = new Object[4];
        objArr[0] = Long.valueOf(this.countAll ? stats.episodesStartedIncludingMarked : stats.episodesStarted);
        objArr[1] = Long.valueOf(stats.episodes);
        objArr[2] = Converter.shortLocalizedDuration(this, this.countAll ? stats.timePlayedCountAll : stats.timePlayed);
        objArr[3] = Converter.shortLocalizedDuration(this, stats.time);
        dialog.setMessage(getString(R.string.statistics_details_dialog, objArr));
        dialog.setPositiveButton(17039370, null);
        dialog.show();
    }
}

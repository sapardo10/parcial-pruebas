package de.danoeh.antennapod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import de.danoeh.antennapod.core.export.opml.OpmlElement;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.debug.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OpmlFeedChooserActivity extends AppCompatActivity {
    public static final String EXTRA_SELECTED_ITEMS = "de.danoeh.antennapod.selectedItems";
    private static final String TAG = "OpmlFeedChooserActivity";
    private Button butCancel;
    private Button butConfirm;
    private MenuItem deselectAll;
    private ListView feedlist;
    private ArrayAdapter<String> listAdapter;
    private MenuItem selectAll;

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opml_selection);
        this.butConfirm = (Button) findViewById(R.id.butConfirm);
        this.butCancel = (Button) findViewById(R.id.butCancel);
        this.feedlist = (ListView) findViewById(R.id.feedlist);
        this.feedlist.setChoiceMode(2);
        this.listAdapter = new ArrayAdapter(this, 17367056, getTitleList());
        this.feedlist.setAdapter(this.listAdapter);
        this.feedlist.setOnItemClickListener(new -$$Lambda$OpmlFeedChooserActivity$Dm2_eQGJVBDtlXeCzwEzAeRIR00());
        this.butCancel.setOnClickListener(new -$$Lambda$OpmlFeedChooserActivity$whlirD2dWAL3yyi_LVNaBsT8HUI());
        this.butConfirm.setOnClickListener(new -$$Lambda$OpmlFeedChooserActivity$xYT9g_YMM421-hq2MGf9sWxQ-Nk());
    }

    public static /* synthetic */ void lambda$onCreate$0(OpmlFeedChooserActivity opmlFeedChooserActivity, AdapterView parent, View view, int position, long id) {
        SparseBooleanArray checked = opmlFeedChooserActivity.feedlist.getCheckedItemPositions();
        int checkedCount = 0;
        for (int i = 0; i < checked.size(); i++) {
            if (checked.valueAt(i)) {
                checkedCount++;
            }
        }
        if (checkedCount == opmlFeedChooserActivity.listAdapter.getCount()) {
            opmlFeedChooserActivity.selectAll.setVisible(false);
            opmlFeedChooserActivity.deselectAll.setVisible(true);
            return;
        }
        opmlFeedChooserActivity.deselectAll.setVisible(false);
        opmlFeedChooserActivity.selectAll.setVisible(true);
    }

    public static /* synthetic */ void lambda$onCreate$1(OpmlFeedChooserActivity opmlFeedChooserActivity, View v) {
        opmlFeedChooserActivity.setResult(0);
        opmlFeedChooserActivity.finish();
    }

    public static /* synthetic */ void lambda$onCreate$2(OpmlFeedChooserActivity opmlFeedChooserActivity, View v) {
        Intent intent = new Intent();
        SparseBooleanArray checked = opmlFeedChooserActivity.feedlist.getCheckedItemPositions();
        int checkedCount = 0;
        for (int i = 0; i < checked.size(); i++) {
            if (checked.valueAt(i)) {
                checkedCount++;
            }
        }
        int[] selection = new int[checkedCount];
        int i2 = 0;
        int collected = 0;
        while (collected < checkedCount) {
            if (checked.valueAt(i2)) {
                selection[collected] = checked.keyAt(i2);
                collected++;
            }
            i2++;
        }
        intent.putExtra(EXTRA_SELECTED_ITEMS, selection);
        opmlFeedChooserActivity.setResult(-1, intent);
        opmlFeedChooserActivity.finish();
    }

    private List<String> getTitleList() {
        List<String> result = new ArrayList();
        if (OpmlImportHolder.getReadElements() != null) {
            Iterator it = OpmlImportHolder.getReadElements().iterator();
            while (it.hasNext()) {
                result.add(((OpmlElement) it.next()).getText());
            }
        }
        return result;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opml_selection_options, menu);
        this.selectAll = menu.findItem(R.id.select_all_item);
        this.deselectAll = menu.findItem(R.id.deselect_all_item);
        this.deselectAll.setVisible(false);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.deselect_all_item) {
            this.deselectAll.setVisible(false);
            selectAllItems(false);
            this.selectAll.setVisible(true);
            return true;
        } else if (itemId != R.id.select_all_item) {
            return false;
        } else {
            this.selectAll.setVisible(false);
            selectAllItems(true);
            this.deselectAll.setVisible(true);
            return true;
        }
    }

    private void selectAllItems(boolean b) {
        for (int i = 0; i < this.feedlist.getCount(); i++) {
            this.feedlist.setItemChecked(i, b);
        }
    }
}

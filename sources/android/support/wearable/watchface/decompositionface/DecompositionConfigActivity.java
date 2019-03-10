package android.support.wearable.watchface.decompositionface;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.C0395R;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.ComplicationProviderInfo;
import android.support.wearable.complications.ProviderChooserIntent;
import android.support.wearable.complications.ProviderInfoRetriever;
import android.support.wearable.complications.ProviderInfoRetriever.OnProviderInfoReceivedCallback;
import android.support.wearable.watchface.decomposition.WatchFaceDecomposition;
import android.support.wearable.watchface.decompositionface.DecompositionConfigView.OnComplicationTapListener;
import java.util.concurrent.Executors;

public abstract class DecompositionConfigActivity extends Activity {
    private static final String ACTION_SUFFIX = ".CONFIG";
    private static final int PROVIDER_CHOOSER_REQUEST_CODE = 1;
    private DecompositionConfigView configView;
    private final OnProviderInfoReceivedCallback infoCallback = new C09231();
    private ProviderInfoRetriever providerInfoRetriever;
    private int tappedComplication;
    private ComponentName watchFace;

    /* renamed from: android.support.wearable.watchface.decompositionface.DecompositionConfigActivity$1 */
    class C09231 extends OnProviderInfoReceivedCallback {
        C09231() {
        }

        public void onProviderInfoReceived(int watchFaceComplicationId, @Nullable ComplicationProviderInfo info) {
            DecompositionConfigActivity.this.configView.setProviderInfo(watchFaceComplicationId, info);
        }
    }

    /* renamed from: android.support.wearable.watchface.decompositionface.DecompositionConfigActivity$2 */
    class C09242 implements OnComplicationTapListener {
        C09242() {
        }

        public void onComplicationTap(int wfCompId, int[] types) {
            DecompositionConfigActivity.this.tappedComplication = wfCompId;
            if (types == null) {
                types = new int[]{5, 3, 7, 6};
            }
            Context context = DecompositionConfigActivity.this;
            context.startActivityForResult(ComplicationHelperActivity.createProviderChooserHelperIntent(context, context.watchFace, wfCompId, types), 1);
        }
    }

    protected abstract WatchFaceDecomposition buildDecompositionForWatchFace(String str);

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.watchFace = getWatchFaceFromAction();
        if (this.watchFace == null) {
            finish();
            return;
        }
        setContentView(C0395R.layout.decomposition_config_activity);
        this.configView = (DecompositionConfigView) findViewById(C0395R.id.configView);
        this.configView.setDecomposition(buildDecompositionForWatchFace(this.watchFace.getClassName()));
        this.configView.setDisplayTime(System.currentTimeMillis());
        this.configView.setOnComplicationTapListener(new C09242());
        this.providerInfoRetriever = new ProviderInfoRetriever(this, Executors.newCachedThreadPool());
        this.providerInfoRetriever.init();
        this.providerInfoRetriever.retrieveProviderInfo(this.infoCallback, this.watchFace, this.configView.getWatchFaceComplicationIds());
    }

    @Nullable
    private ComponentName getWatchFaceFromAction() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null) {
            if (action.endsWith(ACTION_SUFFIX)) {
                return new ComponentName(this, intent.getAction().substring(0, action.length() - ACTION_SUFFIX.length()));
            }
        }
        return null;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == -1) {
            this.configView.setProviderInfo(this.tappedComplication, (ComplicationProviderInfo) data.getParcelableExtra(ProviderChooserIntent.EXTRA_PROVIDER_INFO));
        }
    }

    protected void onDestroy() {
        this.providerInfoRetriever.release();
        super.onDestroy();
    }
}

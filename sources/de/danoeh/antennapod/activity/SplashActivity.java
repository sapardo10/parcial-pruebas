package de.danoeh.antennapod.activity;

import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.debug.R;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (VERSION.SDK_INT < 21) {
            Drawable wrapDrawable = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, -1);
            progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            progressBar.getIndeterminateDrawable().setColorFilter(-1, Mode.SRC_IN);
        }
        Completable.create(-$$Lambda$SplashActivity$4cCte6ENkBZApVMn4A_M2Taf_7I.INSTANCE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$SplashActivity$LBJTCTLDpTbQPht69scbbCR-jl0());
    }

    static /* synthetic */ void lambda$onCreate$0(CompletableEmitter subscriber) throws Exception {
        PodDBAdapter.getInstance().open();
        PodDBAdapter.getInstance().close();
        subscriber.onComplete();
    }

    public static /* synthetic */ void lambda$onCreate$1(SplashActivity splashActivity) throws Exception {
        splashActivity.startActivity(new Intent(splashActivity, MainActivity.class));
        splashActivity.finish();
    }
}

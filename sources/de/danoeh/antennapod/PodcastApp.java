package de.danoeh.antennapod;

import android.app.Application;
import android.os.Build.VERSION;
import android.os.StrictMode;
import android.os.StrictMode.VmPolicy.Builder;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialModule;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.feed.EventDistributor;
import de.danoeh.antennapod.spa.SPAUtil;

public class PodcastApp extends Application {
    private static PodcastApp singleton;

    static {
        try {
            Class.forName("de.danoeh.antennapod.config.ClientConfigurator");
        } catch (Exception e) {
            throw new RuntimeException("ClientConfigurator not found", e);
        }
    }

    public static PodcastApp getInstance() {
        return singleton;
    }

    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new CrashReportWriter());
        if (BuildConfig.DEBUG) {
            Builder builder = new Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDropBox();
            builder.detectActivityLeaks();
            builder.detectLeakedClosableObjects();
            if (VERSION.SDK_INT >= 16) {
                builder.detectLeakedRegistrationObjects();
            }
            StrictMode.setVmPolicy(builder.build());
        }
        singleton = this;
        ClientConfig.initialize(this);
        EventDistributor.getInstance();
        Iconify.with(new FontAwesomeModule());
        Iconify.with(new MaterialModule());
        SPAUtil.sendSPAppsQueryFeedsIntent(this);
    }
}

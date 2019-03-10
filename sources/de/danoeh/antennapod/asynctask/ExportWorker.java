package de.danoeh.antennapod.asynctask;

import android.support.annotation.NonNull;
import android.util.Log;
import de.danoeh.antennapod.core.export.ExportWriter;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.util.LangUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ExportWorker {
    private static final String DEFAULT_OUTPUT_NAME = "antennapod-feeds";
    private static final String EXPORT_DIR = "export/";
    private static final String TAG = "ExportWorker";
    private final ExportWriter exportWriter;
    private final File output;

    public ExportWorker(ExportWriter exportWriter) {
        File dataFolder = UserPreferences.getDataFolder(EXPORT_DIR);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("antennapod-feeds.");
        stringBuilder.append(exportWriter.fileExtension());
        this(exportWriter, new File(dataFolder, stringBuilder.toString()));
    }

    private ExportWorker(ExportWriter exportWriter, @NonNull File output) {
        this.exportWriter = exportWriter;
        this.output = output;
    }

    public Observable<File> exportObservable() {
        if (this.output.exists()) {
            Log.w(TAG, "Overwriting previously exported file.");
            this.output.delete();
        }
        return Observable.create(new -$$Lambda$ExportWorker$-myZCdCAwEaEnpnQSJwi_slHoBM());
    }

    public static /* synthetic */ void lambda$exportObservable$0(ExportWorker exportWorker, ObservableEmitter subscriber) throws Exception {
        IOException e;
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(exportWorker.output), LangUtils.UTF_8);
            exportWorker.exportWriter.writeDocument(DBReader.getFeedList(), writer);
            subscriber.onNext(exportWorker.output);
            try {
                writer.close();
            } catch (IOException e2) {
                e = e2;
                subscriber.onError(e);
                subscriber.onComplete();
            }
        } catch (IOException e3) {
            subscriber.onError(e3);
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e4) {
                    e3 = e4;
                    subscriber.onError(e3);
                    subscriber.onComplete();
                }
            }
        } catch (Throwable th) {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e5) {
                    subscriber.onError(e5);
                }
            }
            subscriber.onComplete();
        }
        subscriber.onComplete();
    }
}

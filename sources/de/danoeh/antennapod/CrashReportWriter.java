package de.danoeh.antennapod;

import android.os.Build;
import android.os.Build.VERSION;
import android.util.Log;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import org.apache.commons.io.IOUtils;

public class CrashReportWriter implements UncaughtExceptionHandler {
    private static final String TAG = "CrashReportWriter";
    private final UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

    public static File getFile() {
        return new File(UserPreferences.getDataFolder(null), "crash-report.log");
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(getFile()));
            out.println("[ Environment ]");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Android version: ");
            stringBuilder.append(VERSION.RELEASE);
            out.println(stringBuilder.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append("OS version: ");
            stringBuilder.append(System.getProperty("os.version"));
            out.println(stringBuilder.toString());
            out.println("AntennaPod version: 1.7.1");
            stringBuilder = new StringBuilder();
            stringBuilder.append("Model: ");
            stringBuilder.append(Build.MODEL);
            out.println(stringBuilder.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append("Device: ");
            stringBuilder.append(Build.DEVICE);
            out.println(stringBuilder.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append("Product: ");
            stringBuilder.append(Build.PRODUCT);
            out.println(stringBuilder.toString());
            out.println();
            out.println("[ StackTrace ]");
            ex.printStackTrace(out);
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (Throwable th) {
            IOUtils.closeQuietly(out);
        }
        IOUtils.closeQuietly(out);
        this.defaultHandler.uncaughtException(thread, ex);
    }
}

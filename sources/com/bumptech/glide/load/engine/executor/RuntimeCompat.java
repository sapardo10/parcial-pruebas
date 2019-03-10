package com.bumptech.glide.load.engine.executor;

import android.os.Build.VERSION;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

final class RuntimeCompat {
    private static final String CPU_LOCATION = "/sys/devices/system/cpu/";
    private static final String CPU_NAME_REGEX = "cpu[0-9]+";
    private static final String TAG = "GlideRuntimeCompat";

    /* renamed from: com.bumptech.glide.load.engine.executor.RuntimeCompat$1 */
    class C05321 implements FilenameFilter {
        final /* synthetic */ Pattern val$cpuNamePattern;

        C05321(Pattern pattern) {
            this.val$cpuNamePattern = pattern;
        }

        public boolean accept(File file, String s) {
            return this.val$cpuNamePattern.matcher(s).matches();
        }
    }

    private RuntimeCompat() {
    }

    static int availableProcessors() {
        int cpus = Runtime.getRuntime().availableProcessors();
        if (VERSION.SDK_INT < 17) {
            return Math.max(getCoreCountPre17(), cpus);
        }
        return cpus;
    }

    private static int getCoreCountPre17() {
        File[] cpus = null;
        ThreadPolicy originalPolicy = StrictMode.allowThreadDiskReads();
        try {
            cpus = new File(CPU_LOCATION).listFiles(new C05321(Pattern.compile(CPU_NAME_REGEX)));
        } catch (Throwable th) {
            StrictMode.setThreadPolicy(originalPolicy);
        }
        StrictMode.setThreadPolicy(originalPolicy);
        return Math.max(1, cpus != null ? cpus.length : 0);
    }
}

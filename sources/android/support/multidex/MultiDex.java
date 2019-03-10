package android.support.multidex;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;
import android.util.Log;
import dalvik.system.DexFile;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

public final class MultiDex {
    private static final String CODE_CACHE_NAME = "code_cache";
    private static final String CODE_CACHE_SECONDARY_FOLDER_NAME = "secondary-dexes";
    private static final boolean IS_VM_MULTIDEX_CAPABLE = isVMMultidexCapable(System.getProperty("java.vm.version"));
    private static final int MAX_SUPPORTED_SDK_VERSION = 20;
    private static final int MIN_SDK_VERSION = 4;
    private static final String NO_KEY_PREFIX = "";
    private static final String OLD_SECONDARY_FOLDER_NAME = "secondary-dexes";
    static final String TAG = "MultiDex";
    private static final int VM_WITH_MULTIDEX_VERSION_MAJOR = 2;
    private static final int VM_WITH_MULTIDEX_VERSION_MINOR = 1;
    private static final Set<File> installedApk = new HashSet();

    private static final class V14 {
        private V14() {
        }

        private static void install(ClassLoader loader, List<? extends File> additionalClassPathEntries, File optimizedDirectory) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
            Object dexPathList = MultiDex.findField(loader, "pathList").get(loader);
            MultiDex.expandFieldArray(dexPathList, "dexElements", makeDexElements(dexPathList, new ArrayList(additionalClassPathEntries), optimizedDirectory));
        }

        private static Object[] makeDexElements(Object dexPathList, ArrayList<File> files, File optimizedDirectory) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            return (Object[]) MultiDex.findMethod(dexPathList, "makeDexElements", ArrayList.class, File.class).invoke(dexPathList, new Object[]{files, optimizedDirectory});
        }
    }

    private static final class V19 {
        private V19() {
        }

        private static void install(ClassLoader loader, List<? extends File> additionalClassPathEntries, File optimizedDirectory) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
            Object dexPathList = MultiDex.findField(loader, "pathList").get(loader);
            ArrayList<IOException> suppressedExceptions = new ArrayList();
            MultiDex.expandFieldArray(dexPathList, "dexElements", makeDexElements(dexPathList, new ArrayList(additionalClassPathEntries), optimizedDirectory, suppressedExceptions));
            if (suppressedExceptions.size() > 0) {
                Iterator it = suppressedExceptions.iterator();
                while (it.hasNext()) {
                    Log.w(MultiDex.TAG, "Exception in makeDexElement", (IOException) it.next());
                }
                Field suppressedExceptionsField = MultiDex.findField(dexPathList, "dexElementsSuppressedExceptions");
                IOException[] dexElementsSuppressedExceptions = (IOException[]) suppressedExceptionsField.get(dexPathList);
                if (dexElementsSuppressedExceptions == null) {
                    dexElementsSuppressedExceptions = (IOException[]) suppressedExceptions.toArray(new IOException[suppressedExceptions.size()]);
                } else {
                    IOException[] combined = new IOException[(suppressedExceptions.size() + dexElementsSuppressedExceptions.length)];
                    suppressedExceptions.toArray(combined);
                    System.arraycopy(dexElementsSuppressedExceptions, 0, combined, suppressedExceptions.size(), dexElementsSuppressedExceptions.length);
                    dexElementsSuppressedExceptions = combined;
                }
                suppressedExceptionsField.set(dexPathList, dexElementsSuppressedExceptions);
            }
        }

        private static Object[] makeDexElements(Object dexPathList, ArrayList<File> files, File optimizedDirectory, ArrayList<IOException> suppressedExceptions) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            return (Object[]) MultiDex.findMethod(dexPathList, "makeDexElements", ArrayList.class, File.class, ArrayList.class).invoke(dexPathList, new Object[]{files, optimizedDirectory, suppressedExceptions});
        }
    }

    private static final class V4 {
        private V4() {
        }

        private static void install(ClassLoader loader, List<? extends File> additionalClassPathEntries) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, IOException {
            int extraSize = additionalClassPathEntries.size();
            Field pathField = MultiDex.findField(loader, "path");
            StringBuilder path = new StringBuilder((String) pathField.get(loader));
            String[] extraPaths = new String[extraSize];
            File[] extraFiles = new File[extraSize];
            ZipFile[] extraZips = new ZipFile[extraSize];
            DexFile[] extraDexs = new DexFile[extraSize];
            ListIterator<? extends File> iterator = additionalClassPathEntries.listIterator();
            while (iterator.hasNext()) {
                File additionalEntry = (File) iterator.next();
                String entryPath = additionalEntry.getAbsolutePath();
                path.append(':');
                path.append(entryPath);
                int index = iterator.previousIndex();
                extraPaths[index] = entryPath;
                extraFiles[index] = additionalEntry;
                extraZips[index] = new ZipFile(additionalEntry);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(entryPath);
                stringBuilder.append(".dex");
                extraDexs[index] = DexFile.loadDex(entryPath, stringBuilder.toString(), 0);
            }
            pathField.set(loader, path.toString());
            MultiDex.expandFieldArray(loader, "mPaths", extraPaths);
            MultiDex.expandFieldArray(loader, "mFiles", extraFiles);
            MultiDex.expandFieldArray(loader, "mZips", extraZips);
            MultiDex.expandFieldArray(loader, "mDexs", extraDexs);
        }
    }

    private static java.lang.reflect.Field findField(java.lang.Object r3, java.lang.String r4) throws java.lang.NoSuchFieldException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0040 in {6, 7, 8, 10, 12} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r0 = r3.getClass();
    L_0x0004:
        if (r0 == 0) goto L_0x001d;
    L_0x0006:
        r1 = r0.getDeclaredField(r4);	 Catch:{ NoSuchFieldException -> 0x0017 }
        r2 = r1.isAccessible();	 Catch:{ NoSuchFieldException -> 0x0017 }
        if (r2 != 0) goto L_0x0015;	 Catch:{ NoSuchFieldException -> 0x0017 }
    L_0x0010:
        r2 = 1;	 Catch:{ NoSuchFieldException -> 0x0017 }
        r1.setAccessible(r2);	 Catch:{ NoSuchFieldException -> 0x0017 }
        goto L_0x0016;
    L_0x0016:
        return r1;
    L_0x0017:
        r1 = move-exception;
        r0 = r0.getSuperclass();
        goto L_0x0004;
    L_0x001d:
        r0 = new java.lang.NoSuchFieldException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Field ";
        r1.append(r2);
        r1.append(r4);
        r2 = " not found in ";
        r1.append(r2);
        r2 = r3.getClass();
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.multidex.MultiDex.findField(java.lang.Object, java.lang.String):java.lang.reflect.Field");
    }

    private static java.lang.reflect.Method findMethod(java.lang.Object r3, java.lang.String r4, java.lang.Class<?>... r5) throws java.lang.NoSuchMethodException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x004c in {6, 7, 8, 10, 12} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r0 = r3.getClass();
    L_0x0004:
        if (r0 == 0) goto L_0x001d;
    L_0x0006:
        r1 = r0.getDeclaredMethod(r4, r5);	 Catch:{ NoSuchMethodException -> 0x0017 }
        r2 = r1.isAccessible();	 Catch:{ NoSuchMethodException -> 0x0017 }
        if (r2 != 0) goto L_0x0015;	 Catch:{ NoSuchMethodException -> 0x0017 }
    L_0x0010:
        r2 = 1;	 Catch:{ NoSuchMethodException -> 0x0017 }
        r1.setAccessible(r2);	 Catch:{ NoSuchMethodException -> 0x0017 }
        goto L_0x0016;
    L_0x0016:
        return r1;
    L_0x0017:
        r1 = move-exception;
        r0 = r0.getSuperclass();
        goto L_0x0004;
    L_0x001d:
        r0 = new java.lang.NoSuchMethodException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Method ";
        r1.append(r2);
        r1.append(r4);
        r2 = " with parameters ";
        r1.append(r2);
        r2 = java.util.Arrays.asList(r5);
        r1.append(r2);
        r2 = " not found in ";
        r1.append(r2);
        r2 = r3.getClass();
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.multidex.MultiDex.findMethod(java.lang.Object, java.lang.String, java.lang.Class[]):java.lang.reflect.Method");
    }

    private MultiDex() {
    }

    public static void install(Context context) {
        StringBuilder stringBuilder;
        Log.i(TAG, "Installing application");
        if (IS_VM_MULTIDEX_CAPABLE) {
            Log.i(TAG, "VM has multidex support, MultiDex support library is disabled.");
        } else if (VERSION.SDK_INT >= 4) {
            try {
                ApplicationInfo applicationInfo = getApplicationInfo(context);
                if (applicationInfo == null) {
                    Log.i(TAG, "No ApplicationInfo available, i.e. running on a test Context: MultiDex support library is disabled.");
                    return;
                }
                doInstallation(context, new File(applicationInfo.sourceDir), new File(applicationInfo.dataDir), "secondary-dexes", "");
                Log.i(TAG, "install done");
            } catch (Exception e) {
                Log.e(TAG, "MultiDex installation failure", e);
                stringBuilder = new StringBuilder();
                stringBuilder.append("MultiDex installation failed (");
                stringBuilder.append(e.getMessage());
                stringBuilder.append(").");
                throw new RuntimeException(stringBuilder.toString());
            }
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("MultiDex installation failed. SDK ");
            stringBuilder.append(VERSION.SDK_INT);
            stringBuilder.append(" is unsupported. Min SDK version is ");
            stringBuilder.append(4);
            stringBuilder.append(".");
            throw new RuntimeException(stringBuilder.toString());
        }
    }

    public static void installInstrumentation(Context instrumentationContext, Context targetContext) {
        StringBuilder stringBuilder;
        Log.i(TAG, "Installing instrumentation");
        if (IS_VM_MULTIDEX_CAPABLE) {
            Log.i(TAG, "VM has multidex support, MultiDex support library is disabled.");
        } else if (VERSION.SDK_INT >= 4) {
            try {
                ApplicationInfo instrumentationInfo = getApplicationInfo(instrumentationContext);
                if (instrumentationInfo == null) {
                    Log.i(TAG, "No ApplicationInfo available for instrumentation, i.e. running on a test Context: MultiDex support library is disabled.");
                    return;
                }
                ApplicationInfo applicationInfo = getApplicationInfo(targetContext);
                if (applicationInfo == null) {
                    Log.i(TAG, "No ApplicationInfo available, i.e. running on a test Context: MultiDex support library is disabled.");
                    return;
                }
                String instrumentationPrefix = new StringBuilder();
                instrumentationPrefix.append(instrumentationContext.getPackageName());
                instrumentationPrefix.append(".");
                instrumentationPrefix = instrumentationPrefix.toString();
                File dataDir = new File(applicationInfo.dataDir);
                File file = new File(instrumentationInfo.sourceDir);
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(instrumentationPrefix);
                stringBuilder2.append("secondary-dexes");
                doInstallation(targetContext, file, dataDir, stringBuilder2.toString(), instrumentationPrefix);
                doInstallation(targetContext, new File(applicationInfo.sourceDir), dataDir, "secondary-dexes", "");
                Log.i(TAG, "Installation done");
            } catch (Exception e) {
                Log.e(TAG, "MultiDex installation failure", e);
                stringBuilder = new StringBuilder();
                stringBuilder.append("MultiDex installation failed (");
                stringBuilder.append(e.getMessage());
                stringBuilder.append(").");
                throw new RuntimeException(stringBuilder.toString());
            }
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("MultiDex installation failed. SDK ");
            stringBuilder.append(VERSION.SDK_INT);
            stringBuilder.append(" is unsupported. Min SDK version is ");
            stringBuilder.append(4);
            stringBuilder.append(".");
            throw new RuntimeException(stringBuilder.toString());
        }
    }

    private static void doInstallation(Context mainContext, File sourceApk, File dataDir, String secondaryFolderName, String prefsKeyPrefix) throws IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
        synchronized (installedApk) {
            if (installedApk.contains(sourceApk)) {
                return;
            }
            installedApk.add(sourceApk);
            if (VERSION.SDK_INT > 20) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("MultiDex is not guaranteed to work in SDK version ");
                stringBuilder.append(VERSION.SDK_INT);
                stringBuilder.append(": SDK version higher than ");
                stringBuilder.append(20);
                stringBuilder.append(" should be backed by ");
                stringBuilder.append("runtime with built-in multidex capabilty but it's not the ");
                stringBuilder.append("case here: java.vm.version=\"");
                stringBuilder.append(System.getProperty("java.vm.version"));
                stringBuilder.append("\"");
                Log.w(str, stringBuilder.toString());
            }
            try {
                ClassLoader loader = mainContext.getClassLoader();
                if (loader == null) {
                    Log.e(TAG, "Context class loader is null. Must be running in test mode. Skip patching.");
                    return;
                }
                try {
                    clearOldDexDir(mainContext);
                } catch (Throwable t) {
                    Log.w(TAG, "Something went wrong when trying to clear old MultiDex extraction, continuing without cleaning.", t);
                }
                File dexDir = getDexDir(mainContext, dataDir, secondaryFolderName);
                installSecondaryDexes(loader, dexDir, MultiDexExtractor.load(mainContext, sourceApk, dexDir, prefsKeyPrefix, null));
            } catch (RuntimeException e) {
                Log.w(TAG, "Failure while trying to obtain Context class loader. Must be running in test mode. Skip patching.", e);
            }
        }
    }

    private static ApplicationInfo getApplicationInfo(Context context) {
        try {
            return context.getApplicationInfo();
        } catch (RuntimeException e) {
            Log.w(TAG, "Failure while trying to obtain ApplicationInfo from Context. Must be running in test mode. Skip patching.", e);
            return null;
        }
    }

    static boolean isVMMultidexCapable(String versionString) {
        boolean isMultidexCapable = false;
        if (versionString != null) {
            Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(versionString);
            if (matcher.matches()) {
                boolean z = true;
                try {
                    int major = Integer.parseInt(matcher.group(1));
                    int minor = Integer.parseInt(matcher.group(2));
                    if (major <= 2) {
                        if (major != 2 || minor < 1) {
                            z = false;
                        }
                    }
                    isMultidexCapable = z;
                } catch (NumberFormatException e) {
                }
            }
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("VM with version ");
        stringBuilder.append(versionString);
        stringBuilder.append(isMultidexCapable ? " has multidex support" : " does not have multidex support");
        Log.i(str, stringBuilder.toString());
        return isMultidexCapable;
    }

    private static void installSecondaryDexes(ClassLoader loader, File dexDir, List<? extends File> files) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IOException {
        if (!files.isEmpty()) {
            if (VERSION.SDK_INT >= 19) {
                V19.install(loader, files, dexDir);
            } else if (VERSION.SDK_INT >= 14) {
                V14.install(loader, files, dexDir);
            } else {
                V4.install(loader, files);
            }
        }
    }

    private static void expandFieldArray(Object instance, String fieldName, Object[] extraElements) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field jlrField = findField(instance, fieldName);
        Object[] original = (Object[]) jlrField.get(instance);
        Object[] combined = (Object[]) Array.newInstance(original.getClass().getComponentType(), original.length + extraElements.length);
        System.arraycopy(original, 0, combined, 0, original.length);
        System.arraycopy(extraElements, 0, combined, original.length, extraElements.length);
        jlrField.set(instance, combined);
    }

    private static void clearOldDexDir(Context context) throws Exception {
        File dexDir = new File(context.getFilesDir(), "secondary-dexes");
        if (dexDir.isDirectory()) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Clearing old secondary dex dir (");
            stringBuilder.append(dexDir.getPath());
            stringBuilder.append(").");
            Log.i(str, stringBuilder.toString());
            File[] files = dexDir.listFiles();
            String str2;
            StringBuilder stringBuilder2;
            if (files == null) {
                str2 = TAG;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Failed to list secondary dex dir content (");
                stringBuilder2.append(dexDir.getPath());
                stringBuilder2.append(").");
                Log.w(str2, stringBuilder2.toString());
                return;
            }
            for (File oldFile : files) {
                String str3 = TAG;
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("Trying to delete old file ");
                stringBuilder3.append(oldFile.getPath());
                stringBuilder3.append(" of size ");
                stringBuilder3.append(oldFile.length());
                Log.i(str3, stringBuilder3.toString());
                if (oldFile.delete()) {
                    str3 = TAG;
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("Deleted old file ");
                    stringBuilder3.append(oldFile.getPath());
                    Log.i(str3, stringBuilder3.toString());
                } else {
                    str3 = TAG;
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("Failed to delete old file ");
                    stringBuilder3.append(oldFile.getPath());
                    Log.w(str3, stringBuilder3.toString());
                }
            }
            if (dexDir.delete()) {
                str2 = TAG;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Deleted old secondary dex dir ");
                stringBuilder2.append(dexDir.getPath());
                Log.i(str2, stringBuilder2.toString());
            } else {
                str2 = TAG;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Failed to delete secondary dex dir ");
                stringBuilder2.append(dexDir.getPath());
                Log.w(str2, stringBuilder2.toString());
            }
        }
    }

    private static File getDexDir(Context context, File dataDir, String secondaryFolderName) throws IOException {
        File cache = new File(dataDir, CODE_CACHE_NAME);
        try {
            mkdirChecked(cache);
        } catch (IOException e) {
            cache = new File(context.getFilesDir(), CODE_CACHE_NAME);
            mkdirChecked(cache);
        }
        File dexDir = new File(cache, secondaryFolderName);
        mkdirChecked(dexDir);
        return dexDir;
    }

    private static void mkdirChecked(File dir) throws IOException {
        dir.mkdir();
        if (!dir.isDirectory()) {
            File parent = dir.getParentFile();
            StringBuilder stringBuilder;
            if (parent == null) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Failed to create dir ");
                stringBuilder.append(dir.getPath());
                stringBuilder.append(". Parent file is null.");
                Log.e(TAG, stringBuilder.toString());
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Failed to create dir ");
                stringBuilder.append(dir.getPath());
                stringBuilder.append(". parent file is a dir ");
                stringBuilder.append(parent.isDirectory());
                stringBuilder.append(", a file ");
                stringBuilder.append(parent.isFile());
                stringBuilder.append(", exists ");
                stringBuilder.append(parent.exists());
                stringBuilder.append(", readable ");
                stringBuilder.append(parent.canRead());
                stringBuilder.append(", writable ");
                stringBuilder.append(parent.canWrite());
                Log.e(TAG, stringBuilder.toString());
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Failed to create directory ");
            stringBuilder2.append(dir.getPath());
            throw new IOException(stringBuilder2.toString());
        }
    }
}

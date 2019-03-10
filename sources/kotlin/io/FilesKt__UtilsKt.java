package kotlin.io;

import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000<\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\u001a(\u0010\t\u001a\u00020\u00022\b\b\u0002\u0010\n\u001a\u00020\u00012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0002\u001a(\u0010\r\u001a\u00020\u00022\b\b\u0002\u0010\n\u001a\u00020\u00012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0002\u001a8\u0010\u000e\u001a\u00020\u000f*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00022\b\b\u0002\u0010\u0011\u001a\u00020\u000f2\u001a\b\u0002\u0010\u0012\u001a\u0014\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00150\u0013\u001a&\u0010\u0016\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00022\b\b\u0002\u0010\u0011\u001a\u00020\u000f2\b\b\u0002\u0010\u0017\u001a\u00020\u0018\u001a\n\u0010\u0019\u001a\u00020\u000f*\u00020\u0002\u001a\u0012\u0010\u001a\u001a\u00020\u000f*\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u0002\u001a\u0012\u0010\u001a\u001a\u00020\u000f*\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u0001\u001a\n\u0010\u001c\u001a\u00020\u0002*\u00020\u0002\u001a\u001d\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00020\u001d*\b\u0012\u0004\u0012\u00020\u00020\u001dH\u0002¢\u0006\u0002\b\u001e\u001a\u0011\u0010\u001c\u001a\u00020\u001f*\u00020\u001fH\u0002¢\u0006\u0002\b\u001e\u001a\u0012\u0010 \u001a\u00020\u0002*\u00020\u00022\u0006\u0010!\u001a\u00020\u0002\u001a\u0014\u0010\"\u001a\u0004\u0018\u00010\u0002*\u00020\u00022\u0006\u0010!\u001a\u00020\u0002\u001a\u0012\u0010#\u001a\u00020\u0002*\u00020\u00022\u0006\u0010!\u001a\u00020\u0002\u001a\u0012\u0010$\u001a\u00020\u0002*\u00020\u00022\u0006\u0010%\u001a\u00020\u0002\u001a\u0012\u0010$\u001a\u00020\u0002*\u00020\u00022\u0006\u0010%\u001a\u00020\u0001\u001a\u0012\u0010&\u001a\u00020\u0002*\u00020\u00022\u0006\u0010%\u001a\u00020\u0002\u001a\u0012\u0010&\u001a\u00020\u0002*\u00020\u00022\u0006\u0010%\u001a\u00020\u0001\u001a\u0012\u0010'\u001a\u00020\u000f*\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u0002\u001a\u0012\u0010'\u001a\u00020\u000f*\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u0001\u001a\u0012\u0010(\u001a\u00020\u0001*\u00020\u00022\u0006\u0010!\u001a\u00020\u0002\u001a\u001b\u0010)\u001a\u0004\u0018\u00010\u0001*\u00020\u00022\u0006\u0010!\u001a\u00020\u0002H\u0002¢\u0006\u0002\b*\"\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004\"\u0015\u0010\u0005\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0004\"\u0015\u0010\u0007\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\b\u0010\u0004¨\u0006+"}, d2 = {"extension", "", "Ljava/io/File;", "getExtension", "(Ljava/io/File;)Ljava/lang/String;", "invariantSeparatorsPath", "getInvariantSeparatorsPath", "nameWithoutExtension", "getNameWithoutExtension", "createTempDir", "prefix", "suffix", "directory", "createTempFile", "copyRecursively", "", "target", "overwrite", "onError", "Lkotlin/Function2;", "Ljava/io/IOException;", "Lkotlin/io/OnErrorAction;", "copyTo", "bufferSize", "", "deleteRecursively", "endsWith", "other", "normalize", "", "normalize$FilesKt__UtilsKt", "Lkotlin/io/FilePathComponents;", "relativeTo", "base", "relativeToOrNull", "relativeToOrSelf", "resolve", "relative", "resolveSibling", "startsWith", "toRelativeString", "toRelativeStringOrNull", "toRelativeStringOrNull$FilesKt__UtilsKt", "kotlin-stdlib"}, k = 5, mv = {1, 1, 10}, xi = 1, xs = "kotlin/io/FilesKt")
/* compiled from: Utils.kt */
class FilesKt__UtilsKt extends FilesKt__FileTreeWalkKt {
    @org.jetbrains.annotations.NotNull
    public static final java.io.File copyTo(@org.jetbrains.annotations.NotNull java.io.File r10, @org.jetbrains.annotations.NotNull java.io.File r11, boolean r12, int r13) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:55:0x00a4 in {6, 9, 10, 12, 14, 15, 20, 22, 25, 33, 35, 41, 44, 50, 52, 54} preds:[]
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
        r0 = "$receiver";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r10, r0);
        r0 = "target";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r11, r0);
        r0 = r10.exists();
        if (r0 == 0) goto L_0x0095;
    L_0x0011:
        r0 = r11.exists();
        r1 = 0;
        if (r0 == 0) goto L_0x0034;
    L_0x0018:
        r0 = 1;
        if (r12 != 0) goto L_0x001c;
    L_0x001b:
        goto L_0x0024;
    L_0x001c:
        r2 = r11.delete();
        if (r2 != 0) goto L_0x0023;
    L_0x0022:
        goto L_0x001b;
    L_0x0023:
        r0 = 0;
    L_0x0024:
        if (r0 != 0) goto L_0x0027;
    L_0x0026:
        goto L_0x0035;
    L_0x0027:
        r1 = new kotlin.io.FileAlreadyExistsException;
        r2 = "The destination file already exists.";
        r1.<init>(r10, r11, r2);
        r1 = (java.lang.Throwable) r1;
        throw r1;
    L_0x0035:
        r0 = r10.isDirectory();
        if (r0 == 0) goto L_0x004c;
    L_0x003b:
        r0 = r11.mkdirs();
        if (r0 == 0) goto L_0x0042;
    L_0x0041:
        goto L_0x0081;
    L_0x0042:
        r0 = new kotlin.io.FileSystemException;
        r1 = "Failed to create target directory.";
        r0.<init>(r10, r11, r1);
        r0 = (java.lang.Throwable) r0;
        throw r0;
    L_0x004c:
        r0 = r11.getParentFile();
        if (r0 == 0) goto L_0x0056;
    L_0x0052:
        r0.mkdirs();
    L_0x0056:
        r0 = new java.io.FileInputStream;
        r0.<init>(r10);
        r0 = (java.io.Closeable) r0;
        r2 = 0;
        r3 = r2;
        r3 = (java.lang.Throwable) r3;
        r4 = r0;	 Catch:{ Throwable -> 0x008e }
        r4 = (java.io.FileInputStream) r4;	 Catch:{ Throwable -> 0x008e }
        r5 = r1;	 Catch:{ Throwable -> 0x008e }
        r6 = new java.io.FileOutputStream;	 Catch:{ Throwable -> 0x008e }
        r6.<init>(r11);	 Catch:{ Throwable -> 0x008e }
        r6 = (java.io.Closeable) r6;	 Catch:{ Throwable -> 0x008e }
        r2 = (java.lang.Throwable) r2;	 Catch:{ Throwable -> 0x008e }
        r7 = r6;	 Catch:{ Throwable -> 0x0085 }
        r7 = (java.io.FileOutputStream) r7;	 Catch:{ Throwable -> 0x0085 }
        r8 = r4;	 Catch:{ Throwable -> 0x0085 }
        r8 = (java.io.InputStream) r8;	 Catch:{ Throwable -> 0x0085 }
        r9 = r7;	 Catch:{ Throwable -> 0x0085 }
        r9 = (java.io.OutputStream) r9;	 Catch:{ Throwable -> 0x0085 }
        kotlin.io.ByteStreamsKt.copyTo(r8, r9, r13);	 Catch:{ Throwable -> 0x0085 }
        kotlin.io.CloseableKt.closeFinally(r6, r2);	 Catch:{ Throwable -> 0x008e }
        kotlin.io.CloseableKt.closeFinally(r0, r3);
        return r11;
    L_0x0083:
        r1 = move-exception;
        goto L_0x0088;
    L_0x0085:
        r1 = move-exception;
        r2 = r1;
        throw r2;	 Catch:{ all -> 0x0083 }
    L_0x0088:
        kotlin.io.CloseableKt.closeFinally(r6, r2);	 Catch:{ Throwable -> 0x008e }
        throw r1;	 Catch:{ Throwable -> 0x008e }
    L_0x008c:
        r1 = move-exception;
        goto L_0x0091;
    L_0x008e:
        r1 = move-exception;
        r3 = r1;
        throw r3;	 Catch:{ all -> 0x008c }
    L_0x0091:
        kotlin.io.CloseableKt.closeFinally(r0, r3);
        throw r1;
    L_0x0095:
        r0 = new kotlin.io.NoSuchFileException;
        r6 = 0;
        r8 = 2;
        r9 = 0;
        r7 = "The source file doesn't exist.";
        r4 = r0;
        r5 = r10;
        r4.<init>(r5, r6, r7, r8, r9);
        r0 = (java.lang.Throwable) r0;
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__UtilsKt.copyTo(java.io.File, java.io.File, boolean, int):java.io.File");
    }

    @NotNull
    public static /* bridge */ /* synthetic */ File createTempDir$default(String str, String str2, File file, int i, Object obj) {
        if ((i & 1) != null) {
            str = "tmp";
        }
        if ((i & 2) != null) {
            str2 = null;
        }
        if ((i & 4) != 0) {
            file = null;
        }
        return createTempDir(str, str2, file);
    }

    @NotNull
    public static final File createTempDir(@NotNull String prefix, @Nullable String suffix, @Nullable File directory) {
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        File dir = File.createTempFile(prefix, suffix, directory);
        dir.delete();
        if (dir.mkdir()) {
            Intrinsics.checkExpressionValueIsNotNull(dir, "dir");
            return dir;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unable to create temporary directory ");
        stringBuilder.append(dir);
        stringBuilder.append('.');
        throw new IOException(stringBuilder.toString());
    }

    @NotNull
    public static /* bridge */ /* synthetic */ File createTempFile$default(String str, String str2, File file, int i, Object obj) {
        if ((i & 1) != null) {
            str = "tmp";
        }
        if ((i & 2) != null) {
            str2 = null;
        }
        if ((i & 4) != 0) {
            file = null;
        }
        return createTempFile(str, str2, file);
    }

    @NotNull
    public static final File createTempFile(@NotNull String prefix, @Nullable String suffix, @Nullable File directory) {
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        File createTempFile = File.createTempFile(prefix, suffix, directory);
        Intrinsics.checkExpressionValueIsNotNull(createTempFile, "File.createTempFile(prefix, suffix, directory)");
        return createTempFile;
    }

    @NotNull
    public static final String getExtension(@NotNull File $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        String name = $receiver.getName();
        Intrinsics.checkExpressionValueIsNotNull(name, PodDBAdapter.KEY_NAME);
        return StringsKt.substringAfterLast(name, '.', "");
    }

    @NotNull
    public static final String getInvariantSeparatorsPath(@NotNull File $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if (File.separatorChar != IOUtils.DIR_SEPARATOR_UNIX) {
            String path = $receiver.getPath();
            Intrinsics.checkExpressionValueIsNotNull(path, "path");
            return StringsKt.replace$default(path, File.separatorChar, IOUtils.DIR_SEPARATOR_UNIX, false, 4, null);
        }
        String path2 = $receiver.getPath();
        Intrinsics.checkExpressionValueIsNotNull(path2, "path");
        return path2;
    }

    @NotNull
    public static final String getNameWithoutExtension(@NotNull File $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        String name = $receiver.getName();
        Intrinsics.checkExpressionValueIsNotNull(name, PodDBAdapter.KEY_NAME);
        return StringsKt.substringBeforeLast$default(name, ".", null, 2, null);
    }

    @NotNull
    public static final String toRelativeString(@NotNull File $receiver, @NotNull File base) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(base, "base");
        String toRelativeStringOrNull$FilesKt__UtilsKt = toRelativeStringOrNull$FilesKt__UtilsKt($receiver, base);
        if (toRelativeStringOrNull$FilesKt__UtilsKt != null) {
            return toRelativeStringOrNull$FilesKt__UtilsKt;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("this and base files have different roots: ");
        stringBuilder.append($receiver);
        stringBuilder.append(" and ");
        stringBuilder.append(base);
        stringBuilder.append('.');
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    @NotNull
    public static final File relativeTo(@NotNull File $receiver, @NotNull File base) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(base, "base");
        return new File(toRelativeString($receiver, base));
    }

    @NotNull
    public static final File relativeToOrSelf(@NotNull File $receiver, @NotNull File base) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(base, "base");
        String p1 = toRelativeStringOrNull$FilesKt__UtilsKt($receiver, base);
        return p1 != null ? new File(p1) : $receiver;
    }

    @Nullable
    public static final File relativeToOrNull(@NotNull File $receiver, @NotNull File base) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(base, "base");
        String p1 = toRelativeStringOrNull$FilesKt__UtilsKt($receiver, base);
        return p1 != null ? new File(p1) : null;
    }

    private static final String toRelativeStringOrNull$FilesKt__UtilsKt(@NotNull File $receiver, File base) {
        FilePathComponents thisComponents = normalize$FilesKt__UtilsKt(FilesKt__FilePathComponentsKt.toComponents($receiver));
        FilePathComponents baseComponents = normalize$FilesKt__UtilsKt(FilesKt__FilePathComponentsKt.toComponents(base));
        if ((Intrinsics.areEqual(thisComponents.getRoot(), baseComponents.getRoot()) ^ 1) != 0) {
            return null;
        }
        int baseCount = baseComponents.getSize();
        int thisCount = thisComponents.getSize();
        int i = 0;
        int maxSameCount = Math.min(thisCount, baseCount);
        while (i < maxSameCount && Intrinsics.areEqual((File) thisComponents.getSegments().get(i), (File) baseComponents.getSegments().get(i))) {
            i++;
        }
        int sameCount = i;
        StringBuilder res = new StringBuilder();
        i = baseCount - 1;
        if (i >= sameCount) {
            while (!Intrinsics.areEqual(((File) baseComponents.getSegments().get(i)).getName(), (Object) "..")) {
                res.append("..");
                if (i != sameCount) {
                    res.append(File.separatorChar);
                }
                if (i != sameCount) {
                    i--;
                }
            }
            return null;
        }
        if (sameCount < thisCount) {
            if (sameCount < baseCount) {
                res.append(File.separatorChar);
            }
            Iterable drop = CollectionsKt___CollectionsKt.drop(thisComponents.getSegments(), sameCount);
            Appendable appendable = res;
            String str = File.separator;
            Intrinsics.checkExpressionValueIsNotNull(str, "File.separator");
            CollectionsKt___CollectionsKt.joinTo$default(drop, appendable, str, null, null, 0, null, null, 124, null);
        }
        return res.toString();
    }

    @NotNull
    public static /* bridge */ /* synthetic */ File copyTo$default(File file, File file2, boolean z, int i, int i2, Object obj) {
        if ((i2 & 2) != null) {
            z = false;
        }
        if ((i2 & 4) != 0) {
            i = 8192;
        }
        return copyTo(file, file2, z, i);
    }

    public static /* bridge */ /* synthetic */ boolean copyRecursively$default(File file, File file2, boolean z, Function2 function2, int i, Object obj) {
        if ((i & 2) != null) {
            z = false;
        }
        if ((i & 4) != 0) {
            function2 = FilesKt__UtilsKt$copyRecursively$1.INSTANCE;
        }
        return copyRecursively(file, file2, z, function2);
    }

    public static final boolean copyRecursively(@NotNull File $receiver, @NotNull File target, boolean overwrite, @NotNull Function2<? super File, ? super IOException, ? extends OnErrorAction> onError) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(target, "target");
        Intrinsics.checkParameterIsNotNull(onError, "onError");
        boolean z = true;
        if ($receiver.exists()) {
            try {
                Iterator it = FilesKt__FileTreeWalkKt.walkTopDown($receiver).onFail(new FilesKt__UtilsKt$copyRecursively$2(onError)).iterator();
                while (it.hasNext()) {
                    File src = (File) it.next();
                    if (src.exists()) {
                        File dstFile = new File(target, toRelativeString(src, $receiver));
                        if (dstFile.exists() && (!src.isDirectory() || !dstFile.isDirectory())) {
                            boolean stillExists = !overwrite ? true : dstFile.isDirectory() ? !deleteRecursively(dstFile) : !dstFile.delete();
                            if (stillExists) {
                                if (((OnErrorAction) onError.invoke(dstFile, new FileAlreadyExistsException(src, dstFile, "The destination file already exists."))) == OnErrorAction.TERMINATE) {
                                    return false;
                                }
                            }
                        }
                        if (src.isDirectory()) {
                            dstFile.mkdirs();
                        } else if (copyTo$default(src, dstFile, overwrite, 0, 4, null).length() != src.length()) {
                            if (((OnErrorAction) onError.invoke(src, new IOException("Source file wasn't copied completely, length of destination file differs."))) == OnErrorAction.TERMINATE) {
                                return false;
                            }
                        }
                    } else {
                        if (((OnErrorAction) onError.invoke(src, new NoSuchFileException(src, null, "The source file doesn't exist.", 2, null))) == OnErrorAction.TERMINATE) {
                            return false;
                        }
                    }
                }
                return true;
            } catch (TerminateException e) {
                return false;
            }
        }
        if (((OnErrorAction) onError.invoke($receiver, new NoSuchFileException($receiver, null, "The source file doesn't exist.", 2, null))) == OnErrorAction.TERMINATE) {
            z = false;
        }
        return z;
    }

    public static final boolean deleteRecursively(@NotNull File $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int $i$f$fold = 0;
        boolean accumulator$iv = true;
        for (File it : FilesKt__FileTreeWalkKt.walkBottomUp($receiver)) {
            boolean z = (it.delete() || !it.exists()) && accumulator$iv;
            accumulator$iv = z;
        }
        return accumulator$iv;
    }

    public static final boolean startsWith(@NotNull File $receiver, @NotNull File other) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        FilePathComponents components = FilesKt__FilePathComponentsKt.toComponents($receiver);
        FilePathComponents otherComponents = FilesKt__FilePathComponentsKt.toComponents(other);
        boolean z = false;
        if ((Intrinsics.areEqual(components.getRoot(), otherComponents.getRoot()) ^ 1) != 0) {
            return false;
        }
        if (components.getSize() >= otherComponents.getSize()) {
            z = components.getSegments().subList(0, otherComponents.getSize()).equals(otherComponents.getSegments());
        }
        return z;
    }

    public static final boolean startsWith(@NotNull File $receiver, @NotNull String other) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        return startsWith($receiver, new File(other));
    }

    public static final boolean endsWith(@NotNull File $receiver, @NotNull File other) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        FilePathComponents components = FilesKt__FilePathComponentsKt.toComponents($receiver);
        FilePathComponents otherComponents = FilesKt__FilePathComponentsKt.toComponents(other);
        if (otherComponents.isRooted()) {
            return Intrinsics.areEqual((Object) $receiver, (Object) other);
        }
        boolean z;
        int shift = components.getSize() - otherComponents.getSize();
        if (shift < 0) {
            z = false;
        } else {
            z = components.getSegments().subList(shift, components.getSize()).equals(otherComponents.getSegments());
        }
        return z;
    }

    public static final boolean endsWith(@NotNull File $receiver, @NotNull String other) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        return endsWith($receiver, new File(other));
    }

    @NotNull
    public static final File normalize(@NotNull File $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        FilePathComponents $receiver2 = FilesKt__FilePathComponentsKt.toComponents($receiver);
        File root = $receiver2.getRoot();
        Iterable normalize$FilesKt__UtilsKt = normalize$FilesKt__UtilsKt($receiver2.getSegments());
        String str = File.separator;
        Intrinsics.checkExpressionValueIsNotNull(str, "File.separator");
        return resolve(root, CollectionsKt___CollectionsKt.joinToString$default(normalize$FilesKt__UtilsKt, str, null, null, 0, null, null, 62, null));
    }

    private static final FilePathComponents normalize$FilesKt__UtilsKt(@NotNull FilePathComponents $receiver) {
        return new FilePathComponents($receiver.getRoot(), normalize$FilesKt__UtilsKt($receiver.getSegments()));
    }

    private static final List<File> normalize$FilesKt__UtilsKt(@NotNull List<? extends File> $receiver) {
        List list = new ArrayList($receiver.size());
        for (File file : $receiver) {
            String name = file.getName();
            if (name != null) {
                int hashCode = name.hashCode();
                if (hashCode != 46) {
                    if (hashCode == 1472 && name.equals("..")) {
                        if (list.isEmpty() || (Intrinsics.areEqual(((File) CollectionsKt___CollectionsKt.last(list)).getName(), (Object) "..") ^ 1) == 0) {
                            list.add(file);
                        } else {
                            list.remove(list.size() - 1);
                        }
                    }
                } else if (name.equals(".")) {
                }
            }
            list.add(file);
        }
        return list;
    }

    @NotNull
    public static final File resolve(@NotNull File $receiver, @NotNull File relative) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(relative, "relative");
        if (FilesKt__FilePathComponentsKt.isRooted(relative)) {
            return relative;
        }
        StringBuilder stringBuilder;
        File file;
        String baseName = $receiver.toString();
        Intrinsics.checkExpressionValueIsNotNull(baseName, "baseName");
        if ((((CharSequence) baseName).length() == 0 ? 1 : null) == null) {
            if (!StringsKt.endsWith$default(baseName, File.separatorChar, false, 2, null)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(baseName);
                stringBuilder.append(File.separatorChar);
                stringBuilder.append(relative);
                file = new File(stringBuilder.toString());
                return file;
            }
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(baseName);
        stringBuilder.append(relative);
        file = new File(stringBuilder.toString());
        return file;
    }

    @NotNull
    public static final File resolve(@NotNull File $receiver, @NotNull String relative) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(relative, "relative");
        return resolve($receiver, new File(relative));
    }

    @NotNull
    public static final File resolveSibling(@NotNull File $receiver, @NotNull File relative) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(relative, "relative");
        FilePathComponents components = FilesKt__FilePathComponentsKt.toComponents($receiver);
        return resolve(resolve(components.getRoot(), components.getSize() == 0 ? new File("..") : components.subPath(null, components.getSize() - 1)), relative);
    }

    @NotNull
    public static final File resolveSibling(@NotNull File $receiver, @NotNull String relative) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(relative, "relative");
        return resolveSibling($receiver, new File(relative));
    }
}

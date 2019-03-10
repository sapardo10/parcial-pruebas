package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.IOCase;

public class FileFilterUtils {
    private static final IOFileFilter cvsFilter = notFileFilter(and(directoryFileFilter(), nameFileFilter("CVS")));
    private static final IOFileFilter svnFilter = notFileFilter(and(directoryFileFilter(), nameFileFilter(".svn")));

    private static <T extends java.util.Collection<java.io.File>> T filter(org.apache.commons.io.filefilter.IOFileFilter r4, java.lang.Iterable<java.io.File> r5, T r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x0035 in {9, 10, 11, 13, 14, 15, 16, 18} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        if (r4 == 0) goto L_0x002d;
    L_0x0002:
        if (r5 == 0) goto L_0x002b;
    L_0x0004:
        r0 = r5.iterator();
    L_0x0008:
        r1 = r0.hasNext();
        if (r1 == 0) goto L_0x002a;
    L_0x000e:
        r1 = r0.next();
        r1 = (java.io.File) r1;
        if (r1 == 0) goto L_0x0022;
    L_0x0016:
        r2 = r4.accept(r1);
        if (r2 == 0) goto L_0x0020;
    L_0x001c:
        r6.add(r1);
        goto L_0x0021;
    L_0x0021:
        goto L_0x0008;
    L_0x0022:
        r2 = new java.lang.IllegalArgumentException;
        r3 = "file collection contains null";
        r2.<init>(r3);
        throw r2;
    L_0x002a:
        goto L_0x002c;
    L_0x002c:
        return r6;
    L_0x002d:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "file filter is null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.filefilter.FileFilterUtils.filter(org.apache.commons.io.filefilter.IOFileFilter, java.lang.Iterable, java.util.Collection):T");
    }

    public static java.io.File[] filter(org.apache.commons.io.filefilter.IOFileFilter r7, java.io.File... r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x0042 in {3, 10, 11, 12, 14, 16, 18} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        if (r7 == 0) goto L_0x003a;
    L_0x0002:
        if (r8 != 0) goto L_0x0008;
    L_0x0004:
        r0 = 0;
        r0 = new java.io.File[r0];
        return r0;
    L_0x0008:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = r8;
        r2 = r1.length;
        r3 = 0;
    L_0x0010:
        if (r3 >= r2) goto L_0x002c;
    L_0x0012:
        r4 = r1[r3];
        if (r4 == 0) goto L_0x0024;
    L_0x0016:
        r5 = r7.accept(r4);
        if (r5 == 0) goto L_0x0020;
    L_0x001c:
        r0.add(r4);
        goto L_0x0021;
    L_0x0021:
        r3 = r3 + 1;
        goto L_0x0010;
    L_0x0024:
        r5 = new java.lang.IllegalArgumentException;
        r6 = "file array contains null";
        r5.<init>(r6);
        throw r5;
        r1 = r0.size();
        r1 = new java.io.File[r1];
        r1 = r0.toArray(r1);
        r1 = (java.io.File[]) r1;
        return r1;
    L_0x003a:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "file filter is null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.filefilter.FileFilterUtils.filter(org.apache.commons.io.filefilter.IOFileFilter, java.io.File[]):java.io.File[]");
    }

    public static java.util.List<org.apache.commons.io.filefilter.IOFileFilter> toList(org.apache.commons.io.filefilter.IOFileFilter... r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x003e in {6, 8, 10, 12} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        if (r5 == 0) goto L_0x0036;
    L_0x0002:
        r0 = new java.util.ArrayList;
        r1 = r5.length;
        r0.<init>(r1);
        r1 = 0;
    L_0x0009:
        r2 = r5.length;
        if (r1 >= r2) goto L_0x0034;
    L_0x000c:
        r2 = r5[r1];
        if (r2 == 0) goto L_0x0018;
    L_0x0010:
        r2 = r5[r1];
        r0.add(r2);
        r1 = r1 + 1;
        goto L_0x0009;
    L_0x0018:
        r2 = new java.lang.IllegalArgumentException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "The filter[";
        r3.append(r4);
        r3.append(r1);
        r4 = "] is null";
        r3.append(r4);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
        return r0;
    L_0x0036:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "The filters must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.filefilter.FileFilterUtils.toList(org.apache.commons.io.filefilter.IOFileFilter[]):java.util.List<org.apache.commons.io.filefilter.IOFileFilter>");
    }

    public static File[] filter(IOFileFilter filter, Iterable<File> files) {
        List<File> acceptedFiles = filterList(filter, (Iterable) files);
        return (File[]) acceptedFiles.toArray(new File[acceptedFiles.size()]);
    }

    public static List<File> filterList(IOFileFilter filter, Iterable<File> files) {
        return (List) filter(filter, files, new ArrayList());
    }

    public static List<File> filterList(IOFileFilter filter, File... files) {
        return Arrays.asList(filter(filter, files));
    }

    public static Set<File> filterSet(IOFileFilter filter, File... files) {
        return new HashSet(Arrays.asList(filter(filter, files)));
    }

    public static Set<File> filterSet(IOFileFilter filter, Iterable<File> files) {
        return (Set) filter(filter, files, new HashSet());
    }

    public static IOFileFilter prefixFileFilter(String prefix) {
        return new PrefixFileFilter(prefix);
    }

    public static IOFileFilter prefixFileFilter(String prefix, IOCase caseSensitivity) {
        return new PrefixFileFilter(prefix, caseSensitivity);
    }

    public static IOFileFilter suffixFileFilter(String suffix) {
        return new SuffixFileFilter(suffix);
    }

    public static IOFileFilter suffixFileFilter(String suffix, IOCase caseSensitivity) {
        return new SuffixFileFilter(suffix, caseSensitivity);
    }

    public static IOFileFilter nameFileFilter(String name) {
        return new NameFileFilter(name);
    }

    public static IOFileFilter nameFileFilter(String name, IOCase caseSensitivity) {
        return new NameFileFilter(name, caseSensitivity);
    }

    public static IOFileFilter directoryFileFilter() {
        return DirectoryFileFilter.DIRECTORY;
    }

    public static IOFileFilter fileFileFilter() {
        return FileFileFilter.FILE;
    }

    @Deprecated
    public static IOFileFilter andFileFilter(IOFileFilter filter1, IOFileFilter filter2) {
        return new AndFileFilter(filter1, filter2);
    }

    @Deprecated
    public static IOFileFilter orFileFilter(IOFileFilter filter1, IOFileFilter filter2) {
        return new OrFileFilter(filter1, filter2);
    }

    public static IOFileFilter and(IOFileFilter... filters) {
        return new AndFileFilter(toList(filters));
    }

    public static IOFileFilter or(IOFileFilter... filters) {
        return new OrFileFilter(toList(filters));
    }

    public static IOFileFilter notFileFilter(IOFileFilter filter) {
        return new NotFileFilter(filter);
    }

    public static IOFileFilter trueFileFilter() {
        return TrueFileFilter.TRUE;
    }

    public static IOFileFilter falseFileFilter() {
        return FalseFileFilter.FALSE;
    }

    public static IOFileFilter asFileFilter(FileFilter filter) {
        return new DelegateFileFilter(filter);
    }

    public static IOFileFilter asFileFilter(FilenameFilter filter) {
        return new DelegateFileFilter(filter);
    }

    public static IOFileFilter ageFileFilter(long cutoff) {
        return new AgeFileFilter(cutoff);
    }

    public static IOFileFilter ageFileFilter(long cutoff, boolean acceptOlder) {
        return new AgeFileFilter(cutoff, acceptOlder);
    }

    public static IOFileFilter ageFileFilter(Date cutoffDate) {
        return new AgeFileFilter(cutoffDate);
    }

    public static IOFileFilter ageFileFilter(Date cutoffDate, boolean acceptOlder) {
        return new AgeFileFilter(cutoffDate, acceptOlder);
    }

    public static IOFileFilter ageFileFilter(File cutoffReference) {
        return new AgeFileFilter(cutoffReference);
    }

    public static IOFileFilter ageFileFilter(File cutoffReference, boolean acceptOlder) {
        return new AgeFileFilter(cutoffReference, acceptOlder);
    }

    public static IOFileFilter sizeFileFilter(long threshold) {
        return new SizeFileFilter(threshold);
    }

    public static IOFileFilter sizeFileFilter(long threshold, boolean acceptLarger) {
        return new SizeFileFilter(threshold, acceptLarger);
    }

    public static IOFileFilter sizeRangeFileFilter(long minSizeInclusive, long maxSizeInclusive) {
        return new AndFileFilter(new SizeFileFilter(minSizeInclusive, true), new SizeFileFilter(1 + maxSizeInclusive, false));
    }

    public static IOFileFilter magicNumberFileFilter(String magicNumber) {
        return new MagicNumberFileFilter(magicNumber);
    }

    public static IOFileFilter magicNumberFileFilter(String magicNumber, long offset) {
        return new MagicNumberFileFilter(magicNumber, offset);
    }

    public static IOFileFilter magicNumberFileFilter(byte[] magicNumber) {
        return new MagicNumberFileFilter(magicNumber);
    }

    public static IOFileFilter magicNumberFileFilter(byte[] magicNumber, long offset) {
        return new MagicNumberFileFilter(magicNumber, offset);
    }

    public static IOFileFilter makeCVSAware(IOFileFilter filter) {
        if (filter == null) {
            return cvsFilter;
        }
        return and(filter, cvsFilter);
    }

    public static IOFileFilter makeSVNAware(IOFileFilter filter) {
        if (filter == null) {
            return svnFilter;
        }
        return and(filter, svnFilter);
    }

    public static IOFileFilter makeDirectoryOnly(IOFileFilter filter) {
        if (filter == null) {
            return DirectoryFileFilter.DIRECTORY;
        }
        return new AndFileFilter(DirectoryFileFilter.DIRECTORY, filter);
    }

    public static IOFileFilter makeFileOnly(IOFileFilter filter) {
        if (filter == null) {
            return FileFileFilter.FILE;
        }
        return new AndFileFilter(FileFileFilter.FILE, filter);
    }
}

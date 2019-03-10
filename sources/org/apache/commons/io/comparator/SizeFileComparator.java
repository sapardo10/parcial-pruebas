package org.apache.commons.io.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.io.FileUtils;

public class SizeFileComparator extends AbstractFileComparator implements Serializable {
    public static final Comparator<File> SIZE_COMPARATOR = new SizeFileComparator();
    public static final Comparator<File> SIZE_REVERSE = new ReverseComparator(SIZE_COMPARATOR);
    public static final Comparator<File> SIZE_SUMDIR_COMPARATOR = new SizeFileComparator(true);
    public static final Comparator<File> SIZE_SUMDIR_REVERSE = new ReverseComparator(SIZE_SUMDIR_COMPARATOR);
    private static final long serialVersionUID = -1201561106411416190L;
    private final boolean sumDirectoryContents;

    public SizeFileComparator() {
        this.sumDirectoryContents = false;
    }

    public SizeFileComparator(boolean sumDirectoryContents) {
        this.sumDirectoryContents = sumDirectoryContents;
    }

    public int compare(File file1, File file2) {
        long sizeOfDirectory;
        long size1;
        long sizeOfDirectory2;
        if (file1.isDirectory()) {
            sizeOfDirectory = (this.sumDirectoryContents && file1.exists()) ? FileUtils.sizeOfDirectory(file1) : 0;
            size1 = sizeOfDirectory;
        } else {
            size1 = file1.length();
        }
        if (file2.isDirectory()) {
            sizeOfDirectory2 = (this.sumDirectoryContents && file2.exists()) ? FileUtils.sizeOfDirectory(file2) : 0;
            sizeOfDirectory = sizeOfDirectory2;
        } else {
            sizeOfDirectory = file2.length();
        }
        sizeOfDirectory2 = size1 - sizeOfDirectory;
        if (sizeOfDirectory2 < 0) {
            return -1;
        }
        if (sizeOfDirectory2 > 0) {
            return 1;
        }
        return 0;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append("[sumDirectoryContents=");
        stringBuilder.append(this.sumDirectoryContents);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}

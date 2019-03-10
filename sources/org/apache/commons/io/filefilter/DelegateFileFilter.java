package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.Serializable;

public class DelegateFileFilter extends AbstractFileFilter implements Serializable {
    private static final long serialVersionUID = -8723373124984771318L;
    private final FileFilter fileFilter;
    private final FilenameFilter filenameFilter;

    public DelegateFileFilter(FilenameFilter filter) {
        if (filter != null) {
            this.filenameFilter = filter;
            this.fileFilter = null;
            return;
        }
        throw new IllegalArgumentException("The FilenameFilter must not be null");
    }

    public DelegateFileFilter(FileFilter filter) {
        if (filter != null) {
            this.fileFilter = filter;
            this.filenameFilter = null;
            return;
        }
        throw new IllegalArgumentException("The FileFilter must not be null");
    }

    public boolean accept(File file) {
        FileFilter fileFilter = this.fileFilter;
        if (fileFilter != null) {
            return fileFilter.accept(file);
        }
        return super.accept(file);
    }

    public boolean accept(File dir, String name) {
        FilenameFilter filenameFilter = this.filenameFilter;
        if (filenameFilter != null) {
            return filenameFilter.accept(dir, name);
        }
        return super.accept(dir, name);
    }

    public String toString() {
        String delegate = this.fileFilter;
        if (delegate == null) {
            delegate = this.filenameFilter;
        }
        delegate = delegate.toString();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append("(");
        stringBuilder.append(delegate);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}

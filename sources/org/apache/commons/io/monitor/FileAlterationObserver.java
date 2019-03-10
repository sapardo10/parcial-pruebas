package org.apache.commons.io.monitor;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.comparator.NameFileComparator;

public class FileAlterationObserver implements Serializable {
    private static final long serialVersionUID = 1185122225658782848L;
    private final Comparator<File> comparator;
    private final FileFilter fileFilter;
    private final List<FileAlterationListener> listeners;
    private final FileEntry rootEntry;

    public FileAlterationObserver(String directoryName) {
        this(new File(directoryName));
    }

    public FileAlterationObserver(String directoryName, FileFilter fileFilter) {
        this(new File(directoryName), fileFilter);
    }

    public FileAlterationObserver(String directoryName, FileFilter fileFilter, IOCase caseSensitivity) {
        this(new File(directoryName), fileFilter, caseSensitivity);
    }

    public FileAlterationObserver(File directory) {
        this(directory, null);
    }

    public FileAlterationObserver(File directory, FileFilter fileFilter) {
        this(directory, fileFilter, null);
    }

    public FileAlterationObserver(File directory, FileFilter fileFilter, IOCase caseSensitivity) {
        this(new FileEntry(directory), fileFilter, caseSensitivity);
    }

    protected FileAlterationObserver(FileEntry rootEntry, FileFilter fileFilter, IOCase caseSensitivity) {
        this.listeners = new CopyOnWriteArrayList();
        if (rootEntry == null) {
            throw new IllegalArgumentException("Root entry is missing");
        } else if (rootEntry.getFile() != null) {
            this.rootEntry = rootEntry;
            this.fileFilter = fileFilter;
            if (caseSensitivity != null) {
                if (!caseSensitivity.equals(IOCase.SYSTEM)) {
                    if (caseSensitivity.equals(IOCase.INSENSITIVE)) {
                        this.comparator = NameFileComparator.NAME_INSENSITIVE_COMPARATOR;
                        return;
                    } else {
                        this.comparator = NameFileComparator.NAME_COMPARATOR;
                        return;
                    }
                }
            }
            this.comparator = NameFileComparator.NAME_SYSTEM_COMPARATOR;
        } else {
            throw new IllegalArgumentException("Root directory is missing");
        }
    }

    public File getDirectory() {
        return this.rootEntry.getFile();
    }

    public FileFilter getFileFilter() {
        return this.fileFilter;
    }

    public void addListener(FileAlterationListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
    }

    public void removeListener(FileAlterationListener listener) {
        if (listener != null) {
            while (this.listeners.remove(listener)) {
            }
        }
    }

    public Iterable<FileAlterationListener> getListeners() {
        return this.listeners;
    }

    public void initialize() throws Exception {
        FileEntry fileEntry = this.rootEntry;
        fileEntry.refresh(fileEntry.getFile());
        this.rootEntry.setChildren(doListFiles(this.rootEntry.getFile(), this.rootEntry));
    }

    public void destroy() throws Exception {
    }

    public void checkAndNotify() {
        for (FileAlterationListener listener : this.listeners) {
            listener.onStart(this);
        }
        File rootFile = this.rootEntry.getFile();
        FileEntry fileEntry;
        if (rootFile.exists()) {
            fileEntry = this.rootEntry;
            checkAndNotify(fileEntry, fileEntry.getChildren(), listFiles(rootFile));
        } else if (this.rootEntry.isExists()) {
            fileEntry = this.rootEntry;
            checkAndNotify(fileEntry, fileEntry.getChildren(), FileUtils.EMPTY_FILE_ARRAY);
        }
        for (FileAlterationListener listener2 : this.listeners) {
            listener2.onStop(this);
        }
    }

    private void checkAndNotify(FileEntry parent, FileEntry[] previous, File[] files) {
        int c = 0;
        FileEntry[] current = files.length > 0 ? new FileEntry[files.length] : FileEntry.EMPTY_ENTRIES;
        for (FileEntry entry : previous) {
            while (c < files.length && this.comparator.compare(entry.getFile(), files[c]) > 0) {
                current[c] = createFileEntry(parent, files[c]);
                doCreate(current[c]);
                c++;
            }
            if (c >= files.length || this.comparator.compare(entry.getFile(), files[c]) != 0) {
                checkAndNotify(entry, entry.getChildren(), FileUtils.EMPTY_FILE_ARRAY);
                doDelete(entry);
            } else {
                doMatch(entry, files[c]);
                checkAndNotify(entry, entry.getChildren(), listFiles(files[c]));
                current[c] = entry;
                c++;
            }
        }
        while (c < files.length) {
            current[c] = createFileEntry(parent, files[c]);
            doCreate(current[c]);
            c++;
        }
        parent.setChildren(current);
    }

    private FileEntry createFileEntry(FileEntry parent, File file) {
        FileEntry entry = parent.newChildInstance(file);
        entry.refresh(file);
        entry.setChildren(doListFiles(file, entry));
        return entry;
    }

    private FileEntry[] doListFiles(File file, FileEntry entry) {
        File[] files = listFiles(file);
        FileEntry[] children = files.length > 0 ? new FileEntry[files.length] : FileEntry.EMPTY_ENTRIES;
        for (int i = 0; i < files.length; i++) {
            children[i] = createFileEntry(entry, files[i]);
        }
        return children;
    }

    private void doCreate(FileEntry entry) {
        for (FileAlterationListener listener : this.listeners) {
            if (entry.isDirectory()) {
                listener.onDirectoryCreate(entry.getFile());
            } else {
                listener.onFileCreate(entry.getFile());
            }
        }
        for (FileEntry aChildren : entry.getChildren()) {
            doCreate(aChildren);
        }
    }

    private void doMatch(FileEntry entry, File file) {
        if (entry.refresh(file)) {
            for (FileAlterationListener listener : this.listeners) {
                if (entry.isDirectory()) {
                    listener.onDirectoryChange(file);
                } else {
                    listener.onFileChange(file);
                }
            }
        }
    }

    private void doDelete(FileEntry entry) {
        for (FileAlterationListener listener : this.listeners) {
            if (entry.isDirectory()) {
                listener.onDirectoryDelete(entry.getFile());
            } else {
                listener.onFileDelete(entry.getFile());
            }
        }
    }

    private File[] listFiles(File file) {
        File[] children = null;
        if (file.isDirectory()) {
            FileFilter fileFilter = this.fileFilter;
            children = fileFilter == null ? file.listFiles() : file.listFiles(fileFilter);
        }
        if (children == null) {
            children = FileUtils.EMPTY_FILE_ARRAY;
        }
        Comparator comparator = this.comparator;
        if (comparator != null && children.length > 1) {
            Arrays.sort(children, comparator);
        }
        return children;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append("[file='");
        builder.append(getDirectory().getPath());
        builder.append('\'');
        if (this.fileFilter != null) {
            builder.append(", ");
            builder.append(this.fileFilter.toString());
        }
        builder.append(", listeners=");
        builder.append(this.listeners.size());
        builder.append("]");
        return builder.toString();
    }
}

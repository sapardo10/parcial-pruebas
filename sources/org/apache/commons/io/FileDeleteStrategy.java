package org.apache.commons.io;

import java.io.File;
import java.io.IOException;

public class FileDeleteStrategy {
    public static final FileDeleteStrategy FORCE = new ForceFileDeleteStrategy();
    public static final FileDeleteStrategy NORMAL = new FileDeleteStrategy("Normal");
    private final String name;

    static class ForceFileDeleteStrategy extends FileDeleteStrategy {
        ForceFileDeleteStrategy() {
            super("Force");
        }

        protected boolean doDelete(File fileToDelete) throws IOException {
            FileUtils.forceDelete(fileToDelete);
            return true;
        }
    }

    protected FileDeleteStrategy(String name) {
        this.name = name;
    }

    public boolean deleteQuietly(File fileToDelete) {
        if (fileToDelete != null) {
            if (fileToDelete.exists()) {
                try {
                    return doDelete(fileToDelete);
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return true;
    }

    public void delete(File fileToDelete) throws IOException {
        if (fileToDelete.exists()) {
            if (!doDelete(fileToDelete)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Deletion failed: ");
                stringBuilder.append(fileToDelete);
                throw new IOException(stringBuilder.toString());
            }
        }
    }

    protected boolean doDelete(File fileToDelete) throws IOException {
        return fileToDelete.delete();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FileDeleteStrategy[");
        stringBuilder.append(this.name);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}

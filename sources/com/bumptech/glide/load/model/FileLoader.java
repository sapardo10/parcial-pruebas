package com.bumptech.glide.load.model;

import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.exoplayer2.C0555C;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileLoader<Data> implements ModelLoader<File, Data> {
    private static final String TAG = "FileLoader";
    private final FileOpener<Data> fileOpener;

    public interface FileOpener<Data> {
        void close(Data data) throws IOException;

        Class<Data> getDataClass();

        Data open(File file) throws FileNotFoundException;
    }

    public static class Factory<Data> implements ModelLoaderFactory<File, Data> {
        private final FileOpener<Data> opener;

        public Factory(FileOpener<Data> opener) {
            this.opener = opener;
        }

        @NonNull
        public final ModelLoader<File, Data> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new FileLoader(this.opener);
        }

        public final void teardown() {
        }
    }

    private static final class FileFetcher<Data> implements DataFetcher<Data> {
        private Data data;
        private final File file;
        private final FileOpener<Data> opener;

        FileFetcher(File file, FileOpener<Data> opener) {
            this.file = file;
            this.opener = opener;
        }

        public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Data> callback) {
            try {
                this.data = this.opener.open(this.file);
                callback.onDataReady(this.data);
            } catch (FileNotFoundException e) {
                if (Log.isLoggable(FileLoader.TAG, 3)) {
                    Log.d(FileLoader.TAG, "Failed to open file", e);
                }
                callback.onLoadFailed(e);
            }
        }

        public void cleanup() {
            Object obj = this.data;
            if (obj != null) {
                try {
                    this.opener.close(obj);
                } catch (IOException e) {
                }
            }
        }

        public void cancel() {
        }

        @NonNull
        public Class<Data> getDataClass() {
            return this.opener.getDataClass();
        }

        @NonNull
        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }

    public static class FileDescriptorFactory extends Factory<ParcelFileDescriptor> {

        /* renamed from: com.bumptech.glide.load.model.FileLoader$FileDescriptorFactory$1 */
        class C09621 implements FileOpener<ParcelFileDescriptor> {
            C09621() {
            }

            public ParcelFileDescriptor open(File file) throws FileNotFoundException {
                return ParcelFileDescriptor.open(file, C0555C.ENCODING_PCM_MU_LAW);
            }

            public void close(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
                parcelFileDescriptor.close();
            }

            public Class<ParcelFileDescriptor> getDataClass() {
                return ParcelFileDescriptor.class;
            }
        }

        public FileDescriptorFactory() {
            super(new C09621());
        }
    }

    public static class StreamFactory extends Factory<InputStream> {

        /* renamed from: com.bumptech.glide.load.model.FileLoader$StreamFactory$1 */
        class C09631 implements FileOpener<InputStream> {
            C09631() {
            }

            public InputStream open(File file) throws FileNotFoundException {
                return new FileInputStream(file);
            }

            public void close(InputStream inputStream) throws IOException {
                inputStream.close();
            }

            public Class<InputStream> getDataClass() {
                return InputStream.class;
            }
        }

        public StreamFactory() {
            super(new C09631());
        }
    }

    public FileLoader(FileOpener<Data> fileOpener) {
        this.fileOpener = fileOpener;
    }

    public LoadData<Data> buildLoadData(@NonNull File model, int width, int height, @NonNull Options options) {
        return new LoadData(new ObjectKey(model), new FileFetcher(model, this.fileOpener));
    }

    public boolean handles(@NonNull File model) {
        return true;
    }
}

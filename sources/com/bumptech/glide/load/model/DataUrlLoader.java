package com.bumptech.glide.load.model;

import android.support.annotation.NonNull;
import android.util.Base64;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.signature.ObjectKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class DataUrlLoader<Model, Data> implements ModelLoader<Model, Data> {
    private static final String BASE64_TAG = ";base64";
    private static final String DATA_SCHEME_IMAGE = "data:image";
    private final DataDecoder<Data> dataDecoder;

    public interface DataDecoder<Data> {
        void close(Data data) throws IOException;

        Data decode(String str) throws IllegalArgumentException;

        Class<Data> getDataClass();
    }

    private static final class DataUriFetcher<Data> implements DataFetcher<Data> {
        private Data data;
        private final String dataUri;
        private final DataDecoder<Data> reader;

        DataUriFetcher(String dataUri, DataDecoder<Data> reader) {
            this.dataUri = dataUri;
            this.reader = reader;
        }

        public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Data> callback) {
            try {
                this.data = this.reader.decode(this.dataUri);
                callback.onDataReady(this.data);
            } catch (IllegalArgumentException e) {
                callback.onLoadFailed(e);
            }
        }

        public void cleanup() {
            try {
                this.reader.close(this.data);
            } catch (IOException e) {
            }
        }

        public void cancel() {
        }

        @NonNull
        public Class<Data> getDataClass() {
            return this.reader.getDataClass();
        }

        @NonNull
        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }

    public static final class StreamFactory<Model> implements ModelLoaderFactory<Model, InputStream> {
        private final DataDecoder<InputStream> opener = new C09611();

        /* renamed from: com.bumptech.glide.load.model.DataUrlLoader$StreamFactory$1 */
        class C09611 implements DataDecoder<InputStream> {
            C09611() {
            }

            public InputStream decode(String url) {
                if (url.startsWith(DataUrlLoader.DATA_SCHEME_IMAGE)) {
                    int commaIndex = url.indexOf(44);
                    if (commaIndex == -1) {
                        throw new IllegalArgumentException("Missing comma in data URL.");
                    } else if (url.substring(0, commaIndex).endsWith(DataUrlLoader.BASE64_TAG)) {
                        return new ByteArrayInputStream(Base64.decode(url.substring(commaIndex + 1), 0));
                    } else {
                        throw new IllegalArgumentException("Not a base64 image data URL.");
                    }
                }
                throw new IllegalArgumentException("Not a valid image data URL.");
            }

            public void close(InputStream inputStream) throws IOException {
                inputStream.close();
            }

            public Class<InputStream> getDataClass() {
                return InputStream.class;
            }
        }

        @NonNull
        public ModelLoader<Model, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new DataUrlLoader(this.opener);
        }

        public void teardown() {
        }
    }

    public DataUrlLoader(DataDecoder<Data> dataDecoder) {
        this.dataDecoder = dataDecoder;
    }

    public LoadData<Data> buildLoadData(@NonNull Model model, int width, int height, @NonNull Options options) {
        return new LoadData(new ObjectKey(model), new DataUriFetcher(model.toString(), this.dataDecoder));
    }

    public boolean handles(@NonNull Model model) {
        return model.toString().startsWith(DATA_SCHEME_IMAGE);
    }
}

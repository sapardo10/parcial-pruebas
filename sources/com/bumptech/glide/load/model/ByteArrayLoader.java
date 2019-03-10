package com.bumptech.glide.load.model;

import android.support.annotation.NonNull;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.signature.ObjectKey;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteArrayLoader<Data> implements ModelLoader<byte[], Data> {
    private final Converter<Data> converter;

    public interface Converter<Data> {
        Data convert(byte[] bArr);

        Class<Data> getDataClass();
    }

    public static class ByteBufferFactory implements ModelLoaderFactory<byte[], ByteBuffer> {

        /* renamed from: com.bumptech.glide.load.model.ByteArrayLoader$ByteBufferFactory$1 */
        class C09591 implements Converter<ByteBuffer> {
            C09591() {
            }

            public ByteBuffer convert(byte[] model) {
                return ByteBuffer.wrap(model);
            }

            public Class<ByteBuffer> getDataClass() {
                return ByteBuffer.class;
            }
        }

        @NonNull
        public ModelLoader<byte[], ByteBuffer> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new ByteArrayLoader(new C09591());
        }

        public void teardown() {
        }
    }

    private static class Fetcher<Data> implements DataFetcher<Data> {
        private final Converter<Data> converter;
        private final byte[] model;

        Fetcher(byte[] model, Converter<Data> converter) {
            this.model = model;
            this.converter = converter;
        }

        public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Data> callback) {
            callback.onDataReady(this.converter.convert(this.model));
        }

        public void cleanup() {
        }

        public void cancel() {
        }

        @NonNull
        public Class<Data> getDataClass() {
            return this.converter.getDataClass();
        }

        @NonNull
        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }

    public static class StreamFactory implements ModelLoaderFactory<byte[], InputStream> {

        /* renamed from: com.bumptech.glide.load.model.ByteArrayLoader$StreamFactory$1 */
        class C09601 implements Converter<InputStream> {
            C09601() {
            }

            public InputStream convert(byte[] model) {
                return new ByteArrayInputStream(model);
            }

            public Class<InputStream> getDataClass() {
                return InputStream.class;
            }
        }

        @NonNull
        public ModelLoader<byte[], InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new ByteArrayLoader(new C09601());
        }

        public void teardown() {
        }
    }

    public ByteArrayLoader(Converter<Data> converter) {
        this.converter = converter;
    }

    public LoadData<Data> buildLoadData(@NonNull byte[] model, int width, int height, @NonNull Options options) {
        return new LoadData(new ObjectKey(model), new Fetcher(model, this.converter));
    }

    public boolean handles(@NonNull byte[] model) {
        return true;
    }
}

package com.bumptech.glide.load.engine.prefill;

import android.graphics.Bitmap.Config;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.bumptech.glide.util.Preconditions;

public final class PreFillType {
    @VisibleForTesting
    static final Config DEFAULT_CONFIG = Config.RGB_565;
    private final Config config;
    private final int height;
    private final int weight;
    private final int width;

    public static class Builder {
        private Config config;
        private final int height;
        private int weight;
        private final int width;

        public Builder(int size) {
            this(size, size);
        }

        public Builder(int width, int height) {
            this.weight = 1;
            if (width <= 0) {
                throw new IllegalArgumentException("Width must be > 0");
            } else if (height > 0) {
                this.width = width;
                this.height = height;
            } else {
                throw new IllegalArgumentException("Height must be > 0");
            }
        }

        public Builder setConfig(@Nullable Config config) {
            this.config = config;
            return this;
        }

        Config getConfig() {
            return this.config;
        }

        public Builder setWeight(int weight) {
            if (weight > 0) {
                this.weight = weight;
                return this;
            }
            throw new IllegalArgumentException("Weight must be > 0");
        }

        PreFillType build() {
            return new PreFillType(this.width, this.height, this.config, this.weight);
        }
    }

    PreFillType(int width, int height, Config config, int weight) {
        this.config = (Config) Preconditions.checkNotNull(config, "Config must not be null");
        this.width = width;
        this.height = height;
        this.weight = weight;
    }

    int getWidth() {
        return this.width;
    }

    int getHeight() {
        return this.height;
    }

    Config getConfig() {
        return this.config;
    }

    int getWeight() {
        return this.weight;
    }

    public boolean equals(Object o) {
        boolean z = false;
        if (!(o instanceof PreFillType)) {
            return false;
        }
        PreFillType other = (PreFillType) o;
        if (this.height == other.height && this.width == other.width && this.weight == other.weight && this.config == other.config) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return (((((this.width * 31) + this.height) * 31) + this.config.hashCode()) * 31) + this.weight;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PreFillSize{width=");
        stringBuilder.append(this.width);
        stringBuilder.append(", height=");
        stringBuilder.append(this.height);
        stringBuilder.append(", config=");
        stringBuilder.append(this.config);
        stringBuilder.append(", weight=");
        stringBuilder.append(this.weight);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}

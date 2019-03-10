package android.support.v7.graphics;

import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

public final class Target {
    public static final Target DARK_MUTED = new Target();
    public static final Target DARK_VIBRANT = new Target();
    static final int INDEX_MAX = 2;
    static final int INDEX_MIN = 0;
    static final int INDEX_TARGET = 1;
    static final int INDEX_WEIGHT_LUMA = 1;
    static final int INDEX_WEIGHT_POP = 2;
    static final int INDEX_WEIGHT_SAT = 0;
    public static final Target LIGHT_MUTED = new Target();
    public static final Target LIGHT_VIBRANT = new Target();
    private static final float MAX_DARK_LUMA = 0.45f;
    private static final float MAX_MUTED_SATURATION = 0.4f;
    private static final float MAX_NORMAL_LUMA = 0.7f;
    private static final float MIN_LIGHT_LUMA = 0.55f;
    private static final float MIN_NORMAL_LUMA = 0.3f;
    private static final float MIN_VIBRANT_SATURATION = 0.35f;
    public static final Target MUTED = new Target();
    private static final float TARGET_DARK_LUMA = 0.26f;
    private static final float TARGET_LIGHT_LUMA = 0.74f;
    private static final float TARGET_MUTED_SATURATION = 0.3f;
    private static final float TARGET_NORMAL_LUMA = 0.5f;
    private static final float TARGET_VIBRANT_SATURATION = 1.0f;
    public static final Target VIBRANT = new Target();
    private static final float WEIGHT_LUMA = 0.52f;
    private static final float WEIGHT_POPULATION = 0.24f;
    private static final float WEIGHT_SATURATION = 0.24f;
    boolean mIsExclusive;
    final float[] mLightnessTargets;
    final float[] mSaturationTargets;
    final float[] mWeights;

    public static final class Builder {
        private final Target mTarget;

        public Builder() {
            this.mTarget = new Target();
        }

        public Builder(@NonNull Target target) {
            this.mTarget = new Target(target);
        }

        @NonNull
        public Builder setMinimumSaturation(@FloatRange(from = 0.0d, to = 1.0d) float value) {
            this.mTarget.mSaturationTargets[0] = value;
            return this;
        }

        @NonNull
        public Builder setTargetSaturation(@FloatRange(from = 0.0d, to = 1.0d) float value) {
            this.mTarget.mSaturationTargets[1] = value;
            return this;
        }

        @NonNull
        public Builder setMaximumSaturation(@FloatRange(from = 0.0d, to = 1.0d) float value) {
            this.mTarget.mSaturationTargets[2] = value;
            return this;
        }

        @NonNull
        public Builder setMinimumLightness(@FloatRange(from = 0.0d, to = 1.0d) float value) {
            this.mTarget.mLightnessTargets[0] = value;
            return this;
        }

        @NonNull
        public Builder setTargetLightness(@FloatRange(from = 0.0d, to = 1.0d) float value) {
            this.mTarget.mLightnessTargets[1] = value;
            return this;
        }

        @NonNull
        public Builder setMaximumLightness(@FloatRange(from = 0.0d, to = 1.0d) float value) {
            this.mTarget.mLightnessTargets[2] = value;
            return this;
        }

        @NonNull
        public Builder setSaturationWeight(@FloatRange(from = 0.0d) float weight) {
            this.mTarget.mWeights[0] = weight;
            return this;
        }

        @NonNull
        public Builder setLightnessWeight(@FloatRange(from = 0.0d) float weight) {
            this.mTarget.mWeights[1] = weight;
            return this;
        }

        @NonNull
        public Builder setPopulationWeight(@FloatRange(from = 0.0d) float weight) {
            this.mTarget.mWeights[2] = weight;
            return this;
        }

        @NonNull
        public Builder setExclusive(boolean exclusive) {
            this.mTarget.mIsExclusive = exclusive;
            return this;
        }

        @NonNull
        public Target build() {
            return this.mTarget;
        }
    }

    static {
        setDefaultLightLightnessValues(LIGHT_VIBRANT);
        setDefaultVibrantSaturationValues(LIGHT_VIBRANT);
        setDefaultNormalLightnessValues(VIBRANT);
        setDefaultVibrantSaturationValues(VIBRANT);
        setDefaultDarkLightnessValues(DARK_VIBRANT);
        setDefaultVibrantSaturationValues(DARK_VIBRANT);
        setDefaultLightLightnessValues(LIGHT_MUTED);
        setDefaultMutedSaturationValues(LIGHT_MUTED);
        setDefaultNormalLightnessValues(MUTED);
        setDefaultMutedSaturationValues(MUTED);
        setDefaultDarkLightnessValues(DARK_MUTED);
        setDefaultMutedSaturationValues(DARK_MUTED);
    }

    Target() {
        this.mSaturationTargets = new float[3];
        this.mLightnessTargets = new float[3];
        this.mWeights = new float[3];
        this.mIsExclusive = true;
        setTargetDefaultValues(this.mSaturationTargets);
        setTargetDefaultValues(this.mLightnessTargets);
        setDefaultWeights();
    }

    Target(@NonNull Target from) {
        this.mSaturationTargets = new float[3];
        this.mLightnessTargets = new float[3];
        this.mWeights = new float[3];
        this.mIsExclusive = true;
        Object obj = from.mSaturationTargets;
        Object obj2 = this.mSaturationTargets;
        System.arraycopy(obj, 0, obj2, 0, obj2.length);
        obj = from.mLightnessTargets;
        obj2 = this.mLightnessTargets;
        System.arraycopy(obj, 0, obj2, 0, obj2.length);
        obj = from.mWeights;
        obj2 = this.mWeights;
        System.arraycopy(obj, 0, obj2, 0, obj2.length);
    }

    @FloatRange(from = 0.0d, to = 1.0d)
    public float getMinimumSaturation() {
        return this.mSaturationTargets[0];
    }

    @FloatRange(from = 0.0d, to = 1.0d)
    public float getTargetSaturation() {
        return this.mSaturationTargets[1];
    }

    @FloatRange(from = 0.0d, to = 1.0d)
    public float getMaximumSaturation() {
        return this.mSaturationTargets[2];
    }

    @FloatRange(from = 0.0d, to = 1.0d)
    public float getMinimumLightness() {
        return this.mLightnessTargets[0];
    }

    @FloatRange(from = 0.0d, to = 1.0d)
    public float getTargetLightness() {
        return this.mLightnessTargets[1];
    }

    @FloatRange(from = 0.0d, to = 1.0d)
    public float getMaximumLightness() {
        return this.mLightnessTargets[2];
    }

    public float getSaturationWeight() {
        return this.mWeights[0];
    }

    public float getLightnessWeight() {
        return this.mWeights[1];
    }

    public float getPopulationWeight() {
        return this.mWeights[2];
    }

    public boolean isExclusive() {
        return this.mIsExclusive;
    }

    private static void setTargetDefaultValues(float[] values) {
        values[0] = 0.0f;
        values[1] = TARGET_NORMAL_LUMA;
        values[2] = 1.0f;
    }

    private void setDefaultWeights() {
        float[] fArr = this.mWeights;
        fArr[0] = 0.24f;
        fArr[1] = WEIGHT_LUMA;
        fArr[2] = 0.24f;
    }

    void normalizeWeights() {
        int i;
        int z;
        float sum = 0.0f;
        for (float weight : this.mWeights) {
            if (weight > 0.0f) {
                sum += weight;
            }
        }
        if (sum != 0.0f) {
            z = this.mWeights.length;
            for (i = 0; i < z; i++) {
                float[] fArr = this.mWeights;
                if (fArr[i] > 0.0f) {
                    fArr[i] = fArr[i] / sum;
                }
            }
        }
    }

    private static void setDefaultDarkLightnessValues(Target target) {
        float[] fArr = target.mLightnessTargets;
        fArr[1] = TARGET_DARK_LUMA;
        fArr[2] = MAX_DARK_LUMA;
    }

    private static void setDefaultNormalLightnessValues(Target target) {
        float[] fArr = target.mLightnessTargets;
        fArr[0] = 0.3f;
        fArr[1] = TARGET_NORMAL_LUMA;
        fArr[2] = MAX_NORMAL_LUMA;
    }

    private static void setDefaultLightLightnessValues(Target target) {
        float[] fArr = target.mLightnessTargets;
        fArr[0] = MIN_LIGHT_LUMA;
        fArr[1] = TARGET_LIGHT_LUMA;
    }

    private static void setDefaultVibrantSaturationValues(Target target) {
        float[] fArr = target.mSaturationTargets;
        fArr[0] = MIN_VIBRANT_SATURATION;
        fArr[1] = 1.0f;
    }

    private static void setDefaultMutedSaturationValues(Target target) {
        float[] fArr = target.mSaturationTargets;
        fArr[1] = 0.3f;
        fArr[2] = MAX_MUTED_SATURATION;
    }
}

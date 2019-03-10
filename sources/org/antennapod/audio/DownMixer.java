package org.antennapod.audio;

class DownMixer {
    DownMixer() {
    }

    static void downMix(byte[] modifiedSamples) {
        for (int i = 0; i + 3 < modifiedSamples.length; i += 4) {
            short right = (short) ((modifiedSamples[i + 2] & 255) | (modifiedSamples[i + 3] << 8));
            double d = (double) ((short) ((modifiedSamples[i] & 255) | (modifiedSamples[i + 1] << 8)));
            Double.isNaN(d);
            d *= 0.5d;
            double d2 = (double) right;
            Double.isNaN(d2);
            short value = (short) ((int) (d + (d2 * 0.5d)));
            modifiedSamples[i] = (byte) (value & 255);
            modifiedSamples[i + 1] = (byte) (value >> 8);
            modifiedSamples[i + 2] = (byte) (value & 255);
            modifiedSamples[i + 3] = (byte) (value >> 8);
        }
    }
}

package com.google.android.exoplayer2.video.spherical;

import com.google.android.exoplayer2.util.Assertions;

public final class Projection {
    public static final int DRAW_MODE_TRIANGLES = 0;
    public static final int DRAW_MODE_TRIANGLES_FAN = 2;
    public static final int DRAW_MODE_TRIANGLES_STRIP = 1;
    public static final int POSITION_COORDS_PER_VERTEX = 3;
    public static final int TEXTURE_COORDS_PER_VERTEX = 2;
    public final Mesh leftMesh;
    public final Mesh rightMesh;
    public final boolean singleMesh;
    public final int stereoMode;

    public static final class Mesh {
        private final SubMesh[] subMeshes;

        public Mesh(SubMesh... subMeshes) {
            this.subMeshes = subMeshes;
        }

        public int getSubMeshCount() {
            return this.subMeshes.length;
        }

        public SubMesh getSubMesh(int index) {
            return this.subMeshes[index];
        }
    }

    public static final class SubMesh {
        public static final int VIDEO_TEXTURE_ID = 0;
        public final int mode;
        public final float[] textureCoords;
        public final int textureId;
        public final float[] vertices;

        public SubMesh(int textureId, float[] vertices, float[] textureCoords, int mode) {
            this.textureId = textureId;
            Assertions.checkArgument(((long) vertices.length) * 2 == ((long) textureCoords.length) * 3);
            this.vertices = vertices;
            this.textureCoords = textureCoords;
            this.mode = mode;
        }

        public int getVertexCount() {
            return this.vertices.length / 3;
        }
    }

    public static Projection createEquirectangular(int stereoMode) {
        return createEquirectangular(50.0f, 36, 72, 180.0f, 360.0f, stereoMode);
    }

    public static Projection createEquirectangular(float radius, int latitudes, int longitudes, float verticalFovDegrees, float horizontalFovDegrees, int stereoMode) {
        int j;
        float[] fArr;
        float f = radius;
        int i = latitudes;
        float[] textureData = longitudes;
        float f2 = verticalFovDegrees;
        float f3 = horizontalFovDegrees;
        Assertions.checkArgument(f > 0.0f);
        Assertions.checkArgument(i >= 1);
        Assertions.checkArgument(textureData >= 1);
        boolean z = f2 > 0.0f && f2 <= 180.0f;
        Assertions.checkArgument(z);
        boolean z2 = f3 > 0.0f && f3 <= 360.0f;
        Assertions.checkArgument(z2);
        float verticalFovRads = (float) Math.toRadians((double) f2);
        float horizontalFovRads = (float) Math.toRadians((double) f3);
        float quadHeightRads = verticalFovRads / ((float) i);
        float quadWidthRads = horizontalFovRads / ((float) textureData);
        int vertexCount = (((textureData + 1) * 2) + 2) * i;
        float[] vertexData = new float[(vertexCount * 3)];
        float[] textureData2 = new float[(vertexCount * 2)];
        int vOffset = 0;
        int j2 = 0;
        int tOffset = 0;
        while (j2 < i) {
            float phiLow;
            float phiHigh;
            float phiLow2 = (((float) j2) * quadHeightRads) - (verticalFovRads / 2.0f);
            float phiHigh2 = (((float) (j2 + 1)) * quadHeightRads) - (verticalFovRads / 2.0f);
            int i2 = 0;
            while (i2 < textureData + 1) {
                int i3;
                i = 0;
                while (i < 2) {
                    int vOffset2;
                    f2 = i == 0 ? phiLow2 : phiHigh2;
                    f3 = ((((float) i2) * quadWidthRads) + 3.1415927f) - (horizontalFovRads / 2.0f);
                    int i4 = vOffset + 1;
                    phiLow = phiLow2;
                    phiHigh = phiHigh2;
                    phiLow2 = (double) f;
                    int k = i;
                    double sin = Math.sin((double) f3);
                    Double.isNaN(phiLow2);
                    vertexData[vOffset] = -((float) ((phiLow2 * sin) * Math.cos((double) f2)));
                    i = i4 + 1;
                    phiLow2 = (double) f;
                    textureData = textureData2;
                    j = j2;
                    double sin2 = Math.sin((double) f2);
                    Double.isNaN(phiLow2);
                    vertexData[i4] = (float) (phiLow2 * sin2);
                    sin2 = (double) f;
                    vOffset = i + 1;
                    phiLow2 = Math.cos((double) f3);
                    Double.isNaN(sin2);
                    vertexData[i] = (float) ((sin2 * phiLow2) * Math.cos((double) f2));
                    i = tOffset + 1;
                    textureData[tOffset] = (((float) i2) * quadWidthRads) / horizontalFovRads;
                    int phiLow3 = i + 1;
                    textureData[i] = (((float) (j + k)) * quadHeightRads) / verticalFovRads;
                    if (i2 == 0) {
                        if (k == 0) {
                            i3 = k;
                            i = longitudes;
                            vOffset2 = vOffset;
                            System.arraycopy(vertexData, vOffset - 3, vertexData, vOffset2, 3);
                            vOffset2 += 3;
                            System.arraycopy(textureData, phiLow3 - 2, textureData, phiLow3, 2);
                            vOffset = vOffset2;
                            tOffset = phiLow3 + 2;
                            vOffset2 = i3 + 1;
                            textureData2 = textureData;
                            phiLow2 = phiLow;
                            phiHigh2 = phiHigh;
                            j2 = j;
                            f2 = verticalFovDegrees;
                            f3 = horizontalFovDegrees;
                            textureData = i;
                            i = vOffset2;
                            f = radius;
                        }
                    }
                    i = longitudes;
                    if (i2 == i) {
                        i3 = k;
                        if (i3 != 1) {
                            vOffset2 = vOffset;
                        }
                        vOffset2 = vOffset;
                        System.arraycopy(vertexData, vOffset - 3, vertexData, vOffset2, 3);
                        vOffset2 += 3;
                        System.arraycopy(textureData, phiLow3 - 2, textureData, phiLow3, 2);
                        vOffset = vOffset2;
                        tOffset = phiLow3 + 2;
                        vOffset2 = i3 + 1;
                        textureData2 = textureData;
                        phiLow2 = phiLow;
                        phiHigh2 = phiHigh;
                        j2 = j;
                        f2 = verticalFovDegrees;
                        f3 = horizontalFovDegrees;
                        textureData = i;
                        i = vOffset2;
                        f = radius;
                    } else {
                        vOffset2 = vOffset;
                        i3 = k;
                    }
                    vOffset = vOffset2;
                    tOffset = phiLow3;
                    vOffset2 = i3 + 1;
                    textureData2 = textureData;
                    phiLow2 = phiLow;
                    phiHigh2 = phiHigh;
                    j2 = j;
                    f2 = verticalFovDegrees;
                    f3 = horizontalFovDegrees;
                    textureData = i;
                    i = vOffset2;
                    f = radius;
                }
                phiHigh = phiHigh2;
                i3 = i;
                fArr = textureData;
                textureData = textureData2;
                i2++;
                phiHigh2 = phiHigh;
                j2 = j2;
                f = radius;
                f2 = verticalFovDegrees;
                f3 = horizontalFovDegrees;
                textureData = fArr;
                i = latitudes;
            }
            fArr = textureData;
            phiLow = phiLow2;
            phiHigh = phiHigh2;
            textureData = textureData2;
            j2++;
            f = radius;
            f2 = verticalFovDegrees;
            f3 = horizontalFovDegrees;
            textureData = fArr;
            i = latitudes;
        }
        fArr = textureData;
        j = j2;
        return new Projection(new Mesh(new SubMesh(0, vertexData, textureData2, 1)), stereoMode);
    }

    public Projection(Mesh mesh, int stereoMode) {
        this(mesh, mesh, stereoMode);
    }

    public Projection(Mesh leftMesh, Mesh rightMesh, int stereoMode) {
        this.leftMesh = leftMesh;
        this.rightMesh = rightMesh;
        this.stereoMode = stereoMode;
        this.singleMesh = leftMesh == rightMesh;
    }
}

package com.google.android.exoplayer2.video.spherical;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.spherical.Projection.Mesh;
import com.google.android.exoplayer2.video.spherical.Projection.SubMesh;
import java.util.ArrayList;
import java.util.zip.Inflater;

public final class ProjectionDecoder {
    private static final int MAX_COORDINATE_COUNT = 10000;
    private static final int MAX_TRIANGLE_INDICES = 128000;
    private static final int MAX_VERTEX_COUNT = 32000;
    private static final int TYPE_DFL8 = Util.getIntegerCodeForString("dfl8");
    private static final int TYPE_MESH = Util.getIntegerCodeForString("mesh");
    private static final int TYPE_MSHP = Util.getIntegerCodeForString("mshp");
    private static final int TYPE_PROJ = Util.getIntegerCodeForString("proj");
    private static final int TYPE_RAW = Util.getIntegerCodeForString("raw ");
    private static final int TYPE_YTMP = Util.getIntegerCodeForString("ytmp");

    private ProjectionDecoder() {
    }

    @Nullable
    public static Projection decode(byte[] projectionData, int stereoMode) {
        ParsableByteArray input = new ParsableByteArray(projectionData);
        ArrayList<Mesh> meshes = null;
        try {
            meshes = isProj(input) ? parseProj(input) : parseMshp(input);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        if (meshes == null) {
            return null;
        }
        switch (meshes.size()) {
            case 1:
                return new Projection((Mesh) meshes.get(0), stereoMode);
            case 2:
                return new Projection((Mesh) meshes.get(0), (Mesh) meshes.get(1), stereoMode);
            default:
                return null;
        }
    }

    private static boolean isProj(ParsableByteArray input) {
        input.skipBytes(4);
        int type = input.readInt();
        input.setPosition(0);
        if (type == TYPE_PROJ) {
            return true;
        }
        return false;
    }

    @Nullable
    private static ArrayList<Mesh> parseProj(ParsableByteArray input) {
        input.skipBytes(8);
        int position = input.getPosition();
        int limit = input.limit();
        while (position < limit) {
            int childEnd = input.readInt() + position;
            if (childEnd > position) {
                if (childEnd <= limit) {
                    int childAtomType = input.readInt();
                    if (childAtomType != TYPE_YTMP) {
                        if (childAtomType != TYPE_MSHP) {
                            position = childEnd;
                            input.setPosition(position);
                        }
                    }
                    input.setLimit(childEnd);
                    return parseMshp(input);
                }
            }
            return null;
        }
        return null;
    }

    @Nullable
    private static ArrayList<Mesh> parseMshp(ParsableByteArray input) {
        if (input.readUnsignedByte() != 0) {
            return null;
        }
        input.skipBytes(7);
        int encoding = input.readInt();
        if (encoding == TYPE_DFL8) {
            ParsableByteArray output = new ParsableByteArray();
            Inflater inflater = new Inflater(true);
            try {
                if (!Util.inflate(input, output, inflater)) {
                    return null;
                }
                inflater.end();
                input = output;
            } finally {
                inflater.end();
            }
        } else if (encoding != TYPE_RAW) {
            return null;
        }
        return parseRawMshpData(input);
    }

    @Nullable
    private static ArrayList<Mesh> parseRawMshpData(ParsableByteArray input) {
        ArrayList<Mesh> meshes = new ArrayList();
        int position = input.getPosition();
        int limit = input.limit();
        while (position < limit) {
            int childEnd = input.readInt() + position;
            if (childEnd > position) {
                if (childEnd <= limit) {
                    if (input.readInt() == TYPE_MESH) {
                        Mesh mesh = parseMesh(input);
                        if (mesh == null) {
                            return null;
                        }
                        meshes.add(mesh);
                    }
                    position = childEnd;
                    input.setPosition(position);
                }
            }
            return null;
        }
        return meshes;
    }

    @Nullable
    private static Mesh parseMesh(ParsableByteArray input) {
        int coordinateCount = input.readInt();
        if (coordinateCount > 10000) {
            return null;
        }
        int coordinate;
        float[] coordinates = new float[coordinateCount];
        for (coordinate = 0; coordinate < coordinateCount; coordinate++) {
            coordinates[coordinate] = input.readFloat();
        }
        coordinate = input.readInt();
        if (coordinate > MAX_VERTEX_COUNT) {
            return null;
        }
        int vertex;
        int i;
        int coordinateIndex;
        int subMeshCount;
        float[] coordinates2;
        double log2;
        double log22 = Math.log(2.0d);
        double d = (double) coordinateCount;
        Double.isNaN(d);
        int coordinateCountSizeBits = (int) Math.ceil(Math.log(d * 2.0d) / log22);
        ParsableBitArray bitInput = new ParsableBitArray(input.data);
        int i2 = 8;
        bitInput.setPosition(input.getPosition() * 8);
        float[] vertices = new float[(coordinate * 5)];
        int[] coordinateIndices = new int[5];
        int vertexIndex = 0;
        for (vertex = 0; vertex < coordinate; vertex++) {
            i = 0;
            while (i < 5) {
                coordinateIndex = coordinateIndices[i] + decodeZigZag(bitInput.readBits(coordinateCountSizeBits));
                if (coordinateIndex < coordinateCount) {
                    if (coordinateIndex >= 0) {
                        int vertexIndex2 = vertexIndex + 1;
                        vertices[vertexIndex] = coordinates[coordinateIndex];
                        coordinateIndices[i] = coordinateIndex;
                        i++;
                        vertexIndex = vertexIndex2;
                    }
                }
                return null;
            }
        }
        bitInput.setPosition((bitInput.getPosition() + 7) & -8);
        i = 32;
        coordinateIndex = bitInput.readBits(32);
        SubMesh[] subMeshes = new SubMesh[coordinateIndex];
        vertex = 0;
        while (vertex < coordinateIndex) {
            int textureId = bitInput.readBits(i2);
            int coordinateCount2 = coordinateCount;
            coordinateCount = bitInput.readBits(i2);
            i2 = bitInput.readBits(i);
            if (i2 > MAX_TRIANGLE_INDICES) {
                return null;
            }
            subMeshCount = coordinateIndex;
            double d2 = (double) coordinate;
            Double.isNaN(d2);
            i = (int) Math.ceil(Math.log(d2 * 2.0d) / log22);
            coordinates2 = coordinates;
            coordinates = new float[(i2 * 3)];
            int index = 0;
            float[] textureCoords = new float[(i2 * 2)];
            log2 = log22;
            int counter = 0;
            while (counter < i2) {
                int index2 = index + decodeZigZag(bitInput.readBits(i));
                if (index2 >= 0) {
                    if (index2 < coordinate) {
                        coordinates[counter * 3] = vertices[index2 * 5];
                        coordinates[(counter * 3) + 1] = vertices[(index2 * 5) + 1];
                        coordinates[(counter * 3) + 2] = vertices[(index2 * 5) + 2];
                        textureCoords[counter * 2] = vertices[(index2 * 5) + 3];
                        textureCoords[(counter * 2) + 1] = vertices[(index2 * 5) + 4];
                        counter++;
                        index = index2;
                    }
                }
                return null;
            }
            subMeshes[vertex] = new SubMesh(textureId, coordinates, textureCoords, coordinateCount);
            vertex++;
            Object obj = null;
            coordinateCount = coordinateCount2;
            coordinateIndex = subMeshCount;
            coordinates = coordinates2;
            log22 = log2;
            i = 32;
            i2 = 8;
        }
        coordinates2 = coordinates;
        subMeshCount = coordinateIndex;
        log2 = log22;
        return new Mesh(subMeshes);
    }

    private static int decodeZigZag(int n) {
        return (n >> 1) ^ (-(n & 1));
    }
}

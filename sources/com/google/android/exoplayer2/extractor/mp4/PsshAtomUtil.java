package com.google.android.exoplayer2.extractor.mp4;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.nio.ByteBuffer;
import java.util.UUID;

public final class PsshAtomUtil {
    private static final String TAG = "PsshAtomUtil";

    private static class PsshAtom {
        private final byte[] schemeData;
        private final UUID uuid;
        private final int version;

        public PsshAtom(UUID uuid, int version, byte[] schemeData) {
            this.uuid = uuid;
            this.version = version;
            this.schemeData = schemeData;
        }
    }

    private PsshAtomUtil() {
    }

    public static byte[] buildPsshAtom(UUID systemId, @Nullable byte[] data) {
        return buildPsshAtom(systemId, null, data);
    }

    public static byte[] buildPsshAtom(UUID systemId, @Nullable UUID[] keyIds, @Nullable byte[] data) {
        int psshBoxLength = (data != null ? data.length : 0) + 32;
        if (keyIds != null) {
            psshBoxLength += (keyIds.length * 16) + 4;
        }
        ByteBuffer psshBox = ByteBuffer.allocate(psshBoxLength);
        psshBox.putInt(psshBoxLength);
        psshBox.putInt(Atom.TYPE_pssh);
        psshBox.putInt(keyIds != null ? 16777216 : 0);
        psshBox.putLong(systemId.getMostSignificantBits());
        psshBox.putLong(systemId.getLeastSignificantBits());
        if (keyIds != null) {
            psshBox.putInt(keyIds.length);
            for (UUID keyId : keyIds) {
                psshBox.putLong(keyId.getMostSignificantBits());
                psshBox.putLong(keyId.getLeastSignificantBits());
            }
        }
        if (data != null && data.length != 0) {
            psshBox.putInt(data.length);
            psshBox.put(data);
        }
        return psshBox.array();
    }

    public static boolean isPsshAtom(byte[] data) {
        return parsePsshAtom(data) != null;
    }

    @Nullable
    public static UUID parseUuid(byte[] atom) {
        PsshAtom parsedAtom = parsePsshAtom(atom);
        if (parsedAtom == null) {
            return null;
        }
        return parsedAtom.uuid;
    }

    public static int parseVersion(byte[] atom) {
        PsshAtom parsedAtom = parsePsshAtom(atom);
        if (parsedAtom == null) {
            return -1;
        }
        return parsedAtom.version;
    }

    @Nullable
    public static byte[] parseSchemeSpecificData(byte[] atom, UUID uuid) {
        PsshAtom parsedAtom = parsePsshAtom(atom);
        if (parsedAtom == null) {
            return null;
        }
        if (uuid == null || uuid.equals(parsedAtom.uuid)) {
            return parsedAtom.schemeData;
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UUID mismatch. Expected: ");
        stringBuilder.append(uuid);
        stringBuilder.append(", got: ");
        stringBuilder.append(parsedAtom.uuid);
        stringBuilder.append(".");
        Log.m10w(str, stringBuilder.toString());
        return null;
    }

    @Nullable
    private static PsshAtom parsePsshAtom(byte[] atom) {
        ParsableByteArray atomData = new ParsableByteArray(atom);
        if (atomData.limit() < 32) {
            return null;
        }
        atomData.setPosition(0);
        if (atomData.readInt() != atomData.bytesLeft() + 4 || atomData.readInt() != Atom.TYPE_pssh) {
            return null;
        }
        int atomVersion = Atom.parseFullAtomVersion(atomData.readInt());
        if (atomVersion > 1) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unsupported pssh version: ");
            stringBuilder.append(atomVersion);
            Log.m10w(str, stringBuilder.toString());
            return null;
        }
        UUID uuid = new UUID(atomData.readLong(), atomData.readLong());
        if (atomVersion == 1) {
            atomData.skipBytes(atomData.readUnsignedIntToInt() * 16);
        }
        int dataSize = atomData.readUnsignedIntToInt();
        if (dataSize != atomData.bytesLeft()) {
            return null;
        }
        byte[] data = new byte[dataSize];
        atomData.readBytes(data, 0, dataSize);
        return new PsshAtom(uuid, atomVersion, data);
    }
}

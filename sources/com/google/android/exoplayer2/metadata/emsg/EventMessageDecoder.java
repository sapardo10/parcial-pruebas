package com.google.android.exoplayer2.metadata.emsg;

import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataDecoder;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class EventMessageDecoder implements MetadataDecoder {
    public Metadata decode(MetadataInputBuffer inputBuffer) {
        ByteBuffer buffer = inputBuffer.data;
        ParsableByteArray emsgData = new ParsableByteArray(buffer.array(), buffer.limit());
        String schemeIdUri = (String) Assertions.checkNotNull(emsgData.readNullTerminatedString());
        String value = (String) Assertions.checkNotNull(emsgData.readNullTerminatedString());
        long readUnsignedInt = emsgData.readUnsignedInt();
        long presentationTimeUs = Util.scaleLargeTimestamp(emsgData.readUnsignedInt(), 1000000, readUnsignedInt);
        long durationMs = Util.scaleLargeTimestamp(emsgData.readUnsignedInt(), 1000, readUnsignedInt);
        long id = emsgData.readUnsignedInt();
        return new Metadata(new EventMessage(schemeIdUri, value, durationMs, id, Arrays.copyOfRange(data, emsgData.getPosition(), size), presentationTimeUs));
    }
}

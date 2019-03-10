package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import com.google.android.exoplayer2.text.cea.CeaUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.List;

final class UserDataReader {
    private static final int USER_DATA_START_CODE = 434;
    private final List<Format> closedCaptionFormats;
    private final TrackOutput[] outputs;

    public UserDataReader(List<Format> closedCaptionFormats) {
        this.closedCaptionFormats = closedCaptionFormats;
        this.outputs = new TrackOutput[closedCaptionFormats.size()];
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        UserDataReader userDataReader = this;
        for (int i = 0; i < userDataReader.outputs.length; i++) {
            boolean z;
            StringBuilder stringBuilder;
            idGenerator.generateNewId();
            TrackOutput output = extractorOutput.track(idGenerator.getTrackId(), 3);
            Format channelFormat = (Format) userDataReader.closedCaptionFormats.get(i);
            String channelMimeType = channelFormat.sampleMimeType;
            if (!MimeTypes.APPLICATION_CEA608.equals(channelMimeType)) {
                if (!MimeTypes.APPLICATION_CEA708.equals(channelMimeType)) {
                    z = false;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Invalid closed caption mime type provided: ");
                    stringBuilder.append(channelMimeType);
                    Assertions.checkArgument(z, stringBuilder.toString());
                    output.format(Format.createTextSampleFormat(idGenerator.getFormatId(), channelMimeType, null, -1, channelFormat.selectionFlags, channelFormat.language, channelFormat.accessibilityChannel, null, Long.MAX_VALUE, channelFormat.initializationData));
                    userDataReader.outputs[i] = output;
                }
            }
            z = true;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid closed caption mime type provided: ");
            stringBuilder.append(channelMimeType);
            Assertions.checkArgument(z, stringBuilder.toString());
            output.format(Format.createTextSampleFormat(idGenerator.getFormatId(), channelMimeType, null, -1, channelFormat.selectionFlags, channelFormat.language, channelFormat.accessibilityChannel, null, Long.MAX_VALUE, channelFormat.initializationData));
            userDataReader.outputs[i] = output;
        }
        ExtractorOutput extractorOutput2 = extractorOutput;
    }

    public void consume(long pesTimeUs, ParsableByteArray userDataPayload) {
        if (userDataPayload.bytesLeft() >= 9) {
            int userDataStartCode = userDataPayload.readInt();
            int userDataIdentifier = userDataPayload.readInt();
            int userDataTypeCode = userDataPayload.readUnsignedByte();
            if (userDataStartCode == USER_DATA_START_CODE && userDataIdentifier == CeaUtil.USER_DATA_IDENTIFIER_GA94 && userDataTypeCode == 3) {
                CeaUtil.consumeCcData(pesTimeUs, userDataPayload, this.outputs);
            }
        }
    }
}

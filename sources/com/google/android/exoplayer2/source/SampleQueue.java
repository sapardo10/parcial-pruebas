package com.google.android.exoplayer2.source;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.CryptoInfo;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.TrackOutput.CryptoData;
import com.google.android.exoplayer2.source.SampleMetadataQueue.SampleExtrasHolder;
import com.google.android.exoplayer2.upstream.Allocation;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SampleQueue implements TrackOutput {
    public static final int ADVANCE_FAILED = -1;
    private static final int INITIAL_SCRATCH_SIZE = 32;
    private final int allocationLength;
    private final Allocator allocator;
    private Format downstreamFormat;
    private final SampleExtrasHolder extrasHolder = new SampleExtrasHolder();
    private AllocationNode firstAllocationNode = new AllocationNode(0, this.allocationLength);
    private Format lastUnadjustedFormat;
    private final SampleMetadataQueue metadataQueue = new SampleMetadataQueue();
    private boolean pendingFormatAdjustment;
    private boolean pendingSplice;
    private AllocationNode readAllocationNode;
    private long sampleOffsetUs;
    private final ParsableByteArray scratch = new ParsableByteArray(32);
    private long totalBytesWritten;
    private UpstreamFormatChangedListener upstreamFormatChangeListener;
    private AllocationNode writeAllocationNode;

    private static final class AllocationNode {
        @Nullable
        public Allocation allocation;
        public final long endPosition;
        @Nullable
        public AllocationNode next;
        public final long startPosition;
        public boolean wasInitialized;

        public AllocationNode(long startPosition, int allocationLength) {
            this.startPosition = startPosition;
            this.endPosition = ((long) allocationLength) + startPosition;
        }

        public void initialize(Allocation allocation, AllocationNode next) {
            this.allocation = allocation;
            this.next = next;
            this.wasInitialized = true;
        }

        public int translateOffset(long absolutePosition) {
            return ((int) (absolutePosition - this.startPosition)) + this.allocation.offset;
        }

        public AllocationNode clear() {
            this.allocation = null;
            AllocationNode temp = this.next;
            this.next = null;
            return temp;
        }
    }

    public interface UpstreamFormatChangedListener {
        void onUpstreamFormatChanged(Format format);
    }

    public SampleQueue(Allocator allocator) {
        this.allocator = allocator;
        this.allocationLength = allocator.getIndividualAllocationLength();
        AllocationNode allocationNode = this.firstAllocationNode;
        this.readAllocationNode = allocationNode;
        this.writeAllocationNode = allocationNode;
    }

    public void reset() {
        reset(false);
    }

    public void reset(boolean resetUpstreamFormat) {
        this.metadataQueue.reset(resetUpstreamFormat);
        clearAllocationNodes(this.firstAllocationNode);
        this.firstAllocationNode = new AllocationNode(0, this.allocationLength);
        AllocationNode allocationNode = this.firstAllocationNode;
        this.readAllocationNode = allocationNode;
        this.writeAllocationNode = allocationNode;
        this.totalBytesWritten = 0;
        this.allocator.trim();
    }

    public void sourceId(int sourceId) {
        this.metadataQueue.sourceId(sourceId);
    }

    public void splice() {
        this.pendingSplice = true;
    }

    public int getWriteIndex() {
        return this.metadataQueue.getWriteIndex();
    }

    public void discardUpstreamSamples(int discardFromIndex) {
        AllocationNode lastNodeToKeep;
        this.totalBytesWritten = this.metadataQueue.discardUpstreamSamples(discardFromIndex);
        long j = this.totalBytesWritten;
        if (j != 0) {
            if (j != this.firstAllocationNode.startPosition) {
                lastNodeToKeep = this.firstAllocationNode;
                while (this.totalBytesWritten > lastNodeToKeep.endPosition) {
                    lastNodeToKeep = lastNodeToKeep.next;
                }
                AllocationNode firstNodeToDiscard = lastNodeToKeep.next;
                clearAllocationNodes(firstNodeToDiscard);
                lastNodeToKeep.next = new AllocationNode(lastNodeToKeep.endPosition, this.allocationLength);
                this.writeAllocationNode = this.totalBytesWritten == lastNodeToKeep.endPosition ? lastNodeToKeep.next : lastNodeToKeep;
                if (this.readAllocationNode == firstNodeToDiscard) {
                    this.readAllocationNode = lastNodeToKeep.next;
                    return;
                }
                return;
            }
        }
        clearAllocationNodes(this.firstAllocationNode);
        this.firstAllocationNode = new AllocationNode(this.totalBytesWritten, this.allocationLength);
        lastNodeToKeep = this.firstAllocationNode;
        this.readAllocationNode = lastNodeToKeep;
        this.writeAllocationNode = lastNodeToKeep;
    }

    public boolean hasNextSample() {
        return this.metadataQueue.hasNextSample();
    }

    public int getFirstIndex() {
        return this.metadataQueue.getFirstIndex();
    }

    public int getReadIndex() {
        return this.metadataQueue.getReadIndex();
    }

    public int peekSourceId() {
        return this.metadataQueue.peekSourceId();
    }

    public Format getUpstreamFormat() {
        return this.metadataQueue.getUpstreamFormat();
    }

    public long getLargestQueuedTimestampUs() {
        return this.metadataQueue.getLargestQueuedTimestampUs();
    }

    public long getFirstTimestampUs() {
        return this.metadataQueue.getFirstTimestampUs();
    }

    public void rewind() {
        this.metadataQueue.rewind();
        this.readAllocationNode = this.firstAllocationNode;
    }

    public void discardTo(long timeUs, boolean toKeyframe, boolean stopAtReadPosition) {
        discardDownstreamTo(this.metadataQueue.discardTo(timeUs, toKeyframe, stopAtReadPosition));
    }

    public void discardToRead() {
        discardDownstreamTo(this.metadataQueue.discardToRead());
    }

    public void discardToEnd() {
        discardDownstreamTo(this.metadataQueue.discardToEnd());
    }

    public int advanceToEnd() {
        return this.metadataQueue.advanceToEnd();
    }

    public int advanceTo(long timeUs, boolean toKeyframe, boolean allowTimeBeyondBuffer) {
        return this.metadataQueue.advanceTo(timeUs, toKeyframe, allowTimeBeyondBuffer);
    }

    public boolean setReadPosition(int sampleIndex) {
        return this.metadataQueue.setReadPosition(sampleIndex);
    }

    public int read(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired, boolean loadingFinished, long decodeOnlyUntilUs) {
        switch (this.metadataQueue.read(formatHolder, buffer, formatRequired, loadingFinished, this.downstreamFormat, this.extrasHolder)) {
            case C0555C.RESULT_FORMAT_READ /*-5*/:
                this.downstreamFormat = formatHolder.format;
                return -5;
            case -4:
                if (!buffer.isEndOfStream()) {
                    if (buffer.timeUs < decodeOnlyUntilUs) {
                        buffer.addFlag(Integer.MIN_VALUE);
                    }
                    if (buffer.isEncrypted()) {
                        readEncryptionData(buffer, this.extrasHolder);
                    }
                    buffer.ensureSpaceForWrite(this.extrasHolder.size);
                    readData(this.extrasHolder.offset, buffer.data, this.extrasHolder.size);
                }
                return -4;
            case -3:
                return -3;
            default:
                throw new IllegalStateException();
        }
    }

    private void readEncryptionData(DecoderInputBuffer buffer, SampleExtrasHolder extrasHolder) {
        int subsampleCount;
        int[] clearDataSizes;
        int[] encryptedDataSizes;
        int subsampleDataLength;
        int i;
        CryptoData cryptoData;
        CryptoInfo cryptoInfo;
        byte[] bArr;
        byte[] bArr2;
        int i2;
        int i3;
        int i4;
        int i5;
        DecoderInputBuffer decoderInputBuffer = buffer;
        SampleExtrasHolder sampleExtrasHolder = extrasHolder;
        long offset = sampleExtrasHolder.offset;
        boolean subsampleEncryption = true;
        this.scratch.reset(1);
        readData(offset, this.scratch.data, 1);
        offset++;
        byte signalByte = this.scratch.data[0];
        if ((signalByte & 128) == 0) {
            subsampleEncryption = false;
        }
        int ivSize = signalByte & 127;
        if (decoderInputBuffer.cryptoInfo.iv == null) {
            decoderInputBuffer.cryptoInfo.iv = new byte[16];
        }
        readData(offset, decoderInputBuffer.cryptoInfo.iv, ivSize);
        offset += (long) ivSize;
        if (subsampleEncryption) {
            r0.scratch.reset(2);
            readData(offset, r0.scratch.data, 2);
            offset += 2;
            subsampleCount = r0.scratch.readUnsignedShort();
        } else {
            subsampleCount = 1;
        }
        int[] clearDataSizes2 = decoderInputBuffer.cryptoInfo.numBytesOfClearData;
        if (clearDataSizes2 != null) {
            if (clearDataSizes2.length >= subsampleCount) {
                clearDataSizes = clearDataSizes2;
                clearDataSizes2 = decoderInputBuffer.cryptoInfo.numBytesOfEncryptedData;
                if (clearDataSizes2 != null) {
                    if (clearDataSizes2.length < subsampleCount) {
                        encryptedDataSizes = clearDataSizes2;
                        if (subsampleEncryption) {
                            clearDataSizes[0] = 0;
                            encryptedDataSizes[0] = sampleExtrasHolder.size - ((int) (offset - sampleExtrasHolder.offset));
                        } else {
                            subsampleDataLength = subsampleCount * 6;
                            r0.scratch.reset(subsampleDataLength);
                            readData(offset, r0.scratch.data, subsampleDataLength);
                            offset += (long) subsampleDataLength;
                            r0.scratch.setPosition(0);
                            for (i = 0; i < subsampleCount; i++) {
                                clearDataSizes[i] = r0.scratch.readUnsignedShort();
                                encryptedDataSizes[i] = r0.scratch.readUnsignedIntToInt();
                            }
                        }
                        cryptoData = sampleExtrasHolder.cryptoData;
                        cryptoInfo = decoderInputBuffer.cryptoInfo;
                        bArr = cryptoData.encryptionKey;
                        bArr2 = decoderInputBuffer.cryptoInfo.iv;
                        i2 = cryptoData.cryptoMode;
                        i3 = cryptoData.encryptedBlocks;
                        i4 = i3;
                        i5 = i2;
                        cryptoInfo.set(subsampleCount, clearDataSizes, encryptedDataSizes, bArr, bArr2, i5, i4, cryptoData.clearBlocks);
                        subsampleDataLength = (int) (offset - sampleExtrasHolder.offset);
                        sampleExtrasHolder.offset += (long) subsampleDataLength;
                        sampleExtrasHolder.size -= subsampleDataLength;
                    }
                }
                encryptedDataSizes = new int[subsampleCount];
                if (subsampleEncryption) {
                    clearDataSizes[0] = 0;
                    encryptedDataSizes[0] = sampleExtrasHolder.size - ((int) (offset - sampleExtrasHolder.offset));
                } else {
                    subsampleDataLength = subsampleCount * 6;
                    r0.scratch.reset(subsampleDataLength);
                    readData(offset, r0.scratch.data, subsampleDataLength);
                    offset += (long) subsampleDataLength;
                    r0.scratch.setPosition(0);
                    for (i = 0; i < subsampleCount; i++) {
                        clearDataSizes[i] = r0.scratch.readUnsignedShort();
                        encryptedDataSizes[i] = r0.scratch.readUnsignedIntToInt();
                    }
                }
                cryptoData = sampleExtrasHolder.cryptoData;
                cryptoInfo = decoderInputBuffer.cryptoInfo;
                bArr = cryptoData.encryptionKey;
                bArr2 = decoderInputBuffer.cryptoInfo.iv;
                i2 = cryptoData.cryptoMode;
                i3 = cryptoData.encryptedBlocks;
                i4 = i3;
                i5 = i2;
                cryptoInfo.set(subsampleCount, clearDataSizes, encryptedDataSizes, bArr, bArr2, i5, i4, cryptoData.clearBlocks);
                subsampleDataLength = (int) (offset - sampleExtrasHolder.offset);
                sampleExtrasHolder.offset += (long) subsampleDataLength;
                sampleExtrasHolder.size -= subsampleDataLength;
            }
        }
        clearDataSizes = new int[subsampleCount];
        clearDataSizes2 = decoderInputBuffer.cryptoInfo.numBytesOfEncryptedData;
        if (clearDataSizes2 != null) {
            if (clearDataSizes2.length < subsampleCount) {
                encryptedDataSizes = clearDataSizes2;
                if (subsampleEncryption) {
                    subsampleDataLength = subsampleCount * 6;
                    r0.scratch.reset(subsampleDataLength);
                    readData(offset, r0.scratch.data, subsampleDataLength);
                    offset += (long) subsampleDataLength;
                    r0.scratch.setPosition(0);
                    for (i = 0; i < subsampleCount; i++) {
                        clearDataSizes[i] = r0.scratch.readUnsignedShort();
                        encryptedDataSizes[i] = r0.scratch.readUnsignedIntToInt();
                    }
                } else {
                    clearDataSizes[0] = 0;
                    encryptedDataSizes[0] = sampleExtrasHolder.size - ((int) (offset - sampleExtrasHolder.offset));
                }
                cryptoData = sampleExtrasHolder.cryptoData;
                cryptoInfo = decoderInputBuffer.cryptoInfo;
                bArr = cryptoData.encryptionKey;
                bArr2 = decoderInputBuffer.cryptoInfo.iv;
                i2 = cryptoData.cryptoMode;
                i3 = cryptoData.encryptedBlocks;
                i4 = i3;
                i5 = i2;
                cryptoInfo.set(subsampleCount, clearDataSizes, encryptedDataSizes, bArr, bArr2, i5, i4, cryptoData.clearBlocks);
                subsampleDataLength = (int) (offset - sampleExtrasHolder.offset);
                sampleExtrasHolder.offset += (long) subsampleDataLength;
                sampleExtrasHolder.size -= subsampleDataLength;
            }
        }
        encryptedDataSizes = new int[subsampleCount];
        if (subsampleEncryption) {
            clearDataSizes[0] = 0;
            encryptedDataSizes[0] = sampleExtrasHolder.size - ((int) (offset - sampleExtrasHolder.offset));
        } else {
            subsampleDataLength = subsampleCount * 6;
            r0.scratch.reset(subsampleDataLength);
            readData(offset, r0.scratch.data, subsampleDataLength);
            offset += (long) subsampleDataLength;
            r0.scratch.setPosition(0);
            for (i = 0; i < subsampleCount; i++) {
                clearDataSizes[i] = r0.scratch.readUnsignedShort();
                encryptedDataSizes[i] = r0.scratch.readUnsignedIntToInt();
            }
        }
        cryptoData = sampleExtrasHolder.cryptoData;
        cryptoInfo = decoderInputBuffer.cryptoInfo;
        bArr = cryptoData.encryptionKey;
        bArr2 = decoderInputBuffer.cryptoInfo.iv;
        i2 = cryptoData.cryptoMode;
        i3 = cryptoData.encryptedBlocks;
        i4 = i3;
        i5 = i2;
        cryptoInfo.set(subsampleCount, clearDataSizes, encryptedDataSizes, bArr, bArr2, i5, i4, cryptoData.clearBlocks);
        subsampleDataLength = (int) (offset - sampleExtrasHolder.offset);
        sampleExtrasHolder.offset += (long) subsampleDataLength;
        sampleExtrasHolder.size -= subsampleDataLength;
    }

    private void readData(long absolutePosition, ByteBuffer target, int length) {
        advanceReadTo(absolutePosition);
        int remaining = length;
        while (remaining > 0) {
            int toCopy = Math.min(remaining, (int) (this.readAllocationNode.endPosition - absolutePosition));
            target.put(this.readAllocationNode.allocation.data, this.readAllocationNode.translateOffset(absolutePosition), toCopy);
            remaining -= toCopy;
            absolutePosition += (long) toCopy;
            if (absolutePosition == this.readAllocationNode.endPosition) {
                this.readAllocationNode = this.readAllocationNode.next;
            }
        }
    }

    private void readData(long absolutePosition, byte[] target, int length) {
        advanceReadTo(absolutePosition);
        int remaining = length;
        while (remaining > 0) {
            int toCopy = Math.min(remaining, (int) (this.readAllocationNode.endPosition - absolutePosition));
            System.arraycopy(this.readAllocationNode.allocation.data, this.readAllocationNode.translateOffset(absolutePosition), target, length - remaining, toCopy);
            remaining -= toCopy;
            absolutePosition += (long) toCopy;
            if (absolutePosition == this.readAllocationNode.endPosition) {
                this.readAllocationNode = this.readAllocationNode.next;
            }
        }
    }

    private void advanceReadTo(long absolutePosition) {
        while (absolutePosition >= this.readAllocationNode.endPosition) {
            this.readAllocationNode = this.readAllocationNode.next;
        }
    }

    private void discardDownstreamTo(long absolutePosition) {
        if (absolutePosition != -1) {
            while (absolutePosition >= this.firstAllocationNode.endPosition) {
                this.allocator.release(this.firstAllocationNode.allocation);
                this.firstAllocationNode = this.firstAllocationNode.clear();
            }
            if (this.readAllocationNode.startPosition < this.firstAllocationNode.startPosition) {
                this.readAllocationNode = this.firstAllocationNode;
            }
        }
    }

    public void setUpstreamFormatChangeListener(UpstreamFormatChangedListener listener) {
        this.upstreamFormatChangeListener = listener;
    }

    public void setSampleOffsetUs(long sampleOffsetUs) {
        if (this.sampleOffsetUs != sampleOffsetUs) {
            this.sampleOffsetUs = sampleOffsetUs;
            this.pendingFormatAdjustment = true;
        }
    }

    public void format(Format format) {
        Format adjustedFormat = getAdjustedSampleFormat(format, this.sampleOffsetUs);
        boolean formatChanged = this.metadataQueue.format(adjustedFormat);
        this.lastUnadjustedFormat = format;
        this.pendingFormatAdjustment = false;
        UpstreamFormatChangedListener upstreamFormatChangedListener = this.upstreamFormatChangeListener;
        if (upstreamFormatChangedListener != null && formatChanged) {
            upstreamFormatChangedListener.onUpstreamFormatChanged(adjustedFormat);
        }
    }

    public int sampleData(ExtractorInput input, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
        int bytesAppended = input.read(this.writeAllocationNode.allocation.data, this.writeAllocationNode.translateOffset(this.totalBytesWritten), preAppend(length));
        if (bytesAppended != -1) {
            postAppend(bytesAppended);
            return bytesAppended;
        } else if (allowEndOfInput) {
            return -1;
        } else {
            throw new EOFException();
        }
    }

    public void sampleData(ParsableByteArray buffer, int length) {
        while (length > 0) {
            int bytesAppended = preAppend(length);
            buffer.readBytes(this.writeAllocationNode.allocation.data, this.writeAllocationNode.translateOffset(this.totalBytesWritten), bytesAppended);
            length -= bytesAppended;
            postAppend(bytesAppended);
        }
    }

    public void sampleMetadata(long timeUs, int flags, int size, int offset, @Nullable CryptoData cryptoData) {
        if (this.pendingFormatAdjustment) {
            format(r0.lastUnadjustedFormat);
        }
        long timeUs2 = timeUs + r0.sampleOffsetUs;
        if (r0.pendingSplice) {
            if ((flags & 1) != 0) {
                if (r0.metadataQueue.attemptSplice(timeUs2)) {
                    r0.pendingSplice = false;
                }
            }
            return;
        }
        r0.metadataQueue.commitSample(timeUs2, flags, (r0.totalBytesWritten - ((long) size)) - ((long) offset), size, cryptoData);
    }

    private void clearAllocationNodes(AllocationNode fromNode) {
        if (fromNode.wasInitialized) {
            Allocation[] allocationsToRelease = new Allocation[(this.writeAllocationNode.wasInitialized + (((int) (this.writeAllocationNode.startPosition - fromNode.startPosition)) / this.allocationLength))];
            AllocationNode currentNode = fromNode;
            for (int i = 0; i < allocationsToRelease.length; i++) {
                allocationsToRelease[i] = currentNode.allocation;
                currentNode = currentNode.clear();
            }
            this.allocator.release(allocationsToRelease);
        }
    }

    private int preAppend(int length) {
        if (!this.writeAllocationNode.wasInitialized) {
            this.writeAllocationNode.initialize(this.allocator.allocate(), new AllocationNode(this.writeAllocationNode.endPosition, this.allocationLength));
        }
        return Math.min(length, (int) (this.writeAllocationNode.endPosition - this.totalBytesWritten));
    }

    private void postAppend(int length) {
        this.totalBytesWritten += (long) length;
        if (this.totalBytesWritten == this.writeAllocationNode.endPosition) {
            this.writeAllocationNode = this.writeAllocationNode.next;
        }
    }

    private static Format getAdjustedSampleFormat(Format format, long sampleOffsetUs) {
        if (format == null) {
            return null;
        }
        if (sampleOffsetUs != 0 && format.subsampleOffsetUs != Long.MAX_VALUE) {
            format = format.copyWithSubsampleOffsetUs(format.subsampleOffsetUs + sampleOffsetUs);
        }
        return format;
    }
}

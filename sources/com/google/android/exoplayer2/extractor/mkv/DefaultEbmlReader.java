package com.google.android.exoplayer2.extractor.mkv;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.ArrayDeque;

final class DefaultEbmlReader implements EbmlReader {
    private static final int ELEMENT_STATE_READ_CONTENT = 2;
    private static final int ELEMENT_STATE_READ_CONTENT_SIZE = 1;
    private static final int ELEMENT_STATE_READ_ID = 0;
    private static final int MAX_ID_BYTES = 4;
    private static final int MAX_INTEGER_ELEMENT_SIZE_BYTES = 8;
    private static final int MAX_LENGTH_BYTES = 8;
    private static final int VALID_FLOAT32_ELEMENT_SIZE_BYTES = 4;
    private static final int VALID_FLOAT64_ELEMENT_SIZE_BYTES = 8;
    private long elementContentSize;
    private int elementId;
    private int elementState;
    private final ArrayDeque<MasterElement> masterElementsStack = new ArrayDeque();
    private EbmlReaderOutput output;
    private final byte[] scratch = new byte[8];
    private final VarintReader varintReader = new VarintReader();

    private static final class MasterElement {
        private final long elementEndPosition;
        private final int elementId;

        private MasterElement(int elementId, long elementEndPosition) {
            this.elementId = elementId;
            this.elementEndPosition = elementEndPosition;
        }
    }

    public void init(EbmlReaderOutput eventHandler) {
        this.output = eventHandler;
    }

    public void reset() {
        this.elementState = 0;
        this.masterElementsStack.clear();
        this.varintReader.reset();
    }

    public boolean read(ExtractorInput input) throws IOException, InterruptedException {
        Assertions.checkState(this.output != null);
        while (true) {
            long result;
            if (!this.masterElementsStack.isEmpty()) {
                if (input.getPosition() >= ((MasterElement) this.masterElementsStack.peek()).elementEndPosition) {
                    this.output.endMasterElement(((MasterElement) this.masterElementsStack.pop()).elementId);
                    return true;
                }
            }
            if (this.elementState == 0) {
                result = this.varintReader.readUnsignedVarint(input, true, false, 4);
                if (result == -2) {
                    result = maybeResyncToNextLevel1Element(input);
                }
                if (result == -1) {
                    return false;
                }
                this.elementId = (int) result;
                this.elementState = 1;
            }
            if (this.elementState == 1) {
                this.elementContentSize = this.varintReader.readUnsignedVarint(input, false, true, 8);
                this.elementState = 2;
            }
            int type = this.output.getElementType(this.elementId);
            long j;
            StringBuilder stringBuilder;
            switch (type) {
                case 0:
                    input.skipFully((int) this.elementContentSize);
                    this.elementState = 0;
                case 1:
                    long elementContentPosition = input.getPosition();
                    this.masterElementsStack.push(new MasterElement(this.elementId, elementContentPosition + this.elementContentSize));
                    this.output.startMasterElement(this.elementId, elementContentPosition, this.elementContentSize);
                    this.elementState = 0;
                    return true;
                case 2:
                    j = this.elementContentSize;
                    if (j <= 8) {
                        this.output.integerElement(this.elementId, readInteger(input, (int) j));
                        this.elementState = 0;
                        return true;
                    }
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Invalid integer size: ");
                    stringBuilder.append(this.elementContentSize);
                    throw new ParserException(stringBuilder.toString());
                case 3:
                    result = this.elementContentSize;
                    if (result <= 2147483647L) {
                        this.output.stringElement(this.elementId, readString(input, (int) result));
                        this.elementState = 0;
                        return true;
                    }
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("String element size: ");
                    stringBuilder.append(this.elementContentSize);
                    throw new ParserException(stringBuilder.toString());
                case 4:
                    this.output.binaryElement(this.elementId, (int) this.elementContentSize, input);
                    this.elementState = 0;
                    return true;
                case 5:
                    j = this.elementContentSize;
                    if (j != 4) {
                        if (j != 8) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Invalid float size: ");
                            stringBuilder.append(this.elementContentSize);
                            throw new ParserException(stringBuilder.toString());
                        }
                    }
                    this.output.floatElement(this.elementId, readFloat(input, (int) this.elementContentSize));
                    this.elementState = 0;
                    return true;
                default:
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Invalid element type ");
                    stringBuilder.append(type);
                    throw new ParserException(stringBuilder.toString());
            }
        }
    }

    private long maybeResyncToNextLevel1Element(ExtractorInput input) throws IOException, InterruptedException {
        input.resetPeekPosition();
        while (true) {
            input.peekFully(this.scratch, 0, 4);
            int varintLength = VarintReader.parseUnsignedVarintLength(this.scratch[0]);
            if (varintLength != -1 && varintLength <= 4) {
                int potentialId = (int) VarintReader.assembleVarint(this.scratch, varintLength, false);
                if (this.output.isLevel1Element(potentialId)) {
                    input.skipFully(varintLength);
                    return (long) potentialId;
                }
            }
            input.skipFully(1);
        }
    }

    private long readInteger(ExtractorInput input, int byteLength) throws IOException, InterruptedException {
        input.readFully(this.scratch, 0, byteLength);
        long value = 0;
        for (int i = 0; i < byteLength; i++) {
            value = (value << 8) | ((long) (this.scratch[i] & 255));
        }
        return value;
    }

    private double readFloat(ExtractorInput input, int byteLength) throws IOException, InterruptedException {
        long integerValue = readInteger(input, byteLength);
        if (byteLength == 4) {
            return (double) Float.intBitsToFloat((int) integerValue);
        }
        return Double.longBitsToDouble(integerValue);
    }

    private String readString(ExtractorInput input, int byteLength) throws IOException, InterruptedException {
        if (byteLength == 0) {
            return "";
        }
        byte[] stringBytes = new byte[byteLength];
        input.readFully(stringBytes, 0, byteLength);
        int trimmedLength = byteLength;
        while (trimmedLength > 0 && stringBytes[trimmedLength - 1] == (byte) 0) {
            trimmedLength--;
        }
        return new String(stringBytes, 0, trimmedLength);
    }
}

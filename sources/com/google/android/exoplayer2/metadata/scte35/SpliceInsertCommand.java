package com.google.android.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SpliceInsertCommand extends SpliceCommand {
    public static final Creator<SpliceInsertCommand> CREATOR = new C06031();
    public final boolean autoReturn;
    public final int availNum;
    public final int availsExpected;
    public final long breakDurationUs;
    public final List<ComponentSplice> componentSpliceList;
    public final boolean outOfNetworkIndicator;
    public final boolean programSpliceFlag;
    public final long programSplicePlaybackPositionUs;
    public final long programSplicePts;
    public final boolean spliceEventCancelIndicator;
    public final long spliceEventId;
    public final boolean spliceImmediateFlag;
    public final int uniqueProgramId;

    /* renamed from: com.google.android.exoplayer2.metadata.scte35.SpliceInsertCommand$1 */
    static class C06031 implements Creator<SpliceInsertCommand> {
        C06031() {
        }

        public SpliceInsertCommand createFromParcel(Parcel in) {
            return new SpliceInsertCommand(in);
        }

        public SpliceInsertCommand[] newArray(int size) {
            return new SpliceInsertCommand[size];
        }
    }

    public static final class ComponentSplice {
        public final long componentSplicePlaybackPositionUs;
        public final long componentSplicePts;
        public final int componentTag;

        private ComponentSplice(int componentTag, long componentSplicePts, long componentSplicePlaybackPositionUs) {
            this.componentTag = componentTag;
            this.componentSplicePts = componentSplicePts;
            this.componentSplicePlaybackPositionUs = componentSplicePlaybackPositionUs;
        }

        public void writeToParcel(Parcel dest) {
            dest.writeInt(this.componentTag);
            dest.writeLong(this.componentSplicePts);
            dest.writeLong(this.componentSplicePlaybackPositionUs);
        }

        public static ComponentSplice createFromParcel(Parcel in) {
            return new ComponentSplice(in.readInt(), in.readLong(), in.readLong());
        }
    }

    private SpliceInsertCommand(long spliceEventId, boolean spliceEventCancelIndicator, boolean outOfNetworkIndicator, boolean programSpliceFlag, boolean spliceImmediateFlag, long programSplicePts, long programSplicePlaybackPositionUs, List<ComponentSplice> componentSpliceList, boolean autoReturn, long breakDurationUs, int uniqueProgramId, int availNum, int availsExpected) {
        this.spliceEventId = spliceEventId;
        this.spliceEventCancelIndicator = spliceEventCancelIndicator;
        this.outOfNetworkIndicator = outOfNetworkIndicator;
        this.programSpliceFlag = programSpliceFlag;
        this.spliceImmediateFlag = spliceImmediateFlag;
        this.programSplicePts = programSplicePts;
        this.programSplicePlaybackPositionUs = programSplicePlaybackPositionUs;
        this.componentSpliceList = Collections.unmodifiableList(componentSpliceList);
        this.autoReturn = autoReturn;
        this.breakDurationUs = breakDurationUs;
        this.uniqueProgramId = uniqueProgramId;
        this.availNum = availNum;
        this.availsExpected = availsExpected;
    }

    private SpliceInsertCommand(Parcel in) {
        this.spliceEventId = in.readLong();
        boolean z = false;
        this.spliceEventCancelIndicator = in.readByte() == (byte) 1;
        this.outOfNetworkIndicator = in.readByte() == (byte) 1;
        this.programSpliceFlag = in.readByte() == (byte) 1;
        this.spliceImmediateFlag = in.readByte() == (byte) 1;
        this.programSplicePts = in.readLong();
        this.programSplicePlaybackPositionUs = in.readLong();
        int componentSpliceListSize = in.readInt();
        List<ComponentSplice> componentSpliceList = new ArrayList(componentSpliceListSize);
        for (int i = 0; i < componentSpliceListSize; i++) {
            componentSpliceList.add(ComponentSplice.createFromParcel(in));
        }
        this.componentSpliceList = Collections.unmodifiableList(componentSpliceList);
        if (in.readByte() == (byte) 1) {
            z = true;
        }
        this.autoReturn = z;
        this.breakDurationUs = in.readLong();
        this.uniqueProgramId = in.readInt();
        this.availNum = in.readInt();
        this.availsExpected = in.readInt();
    }

    static SpliceInsertCommand parseFromSection(ParsableByteArray sectionData, long ptsAdjustment, TimestampAdjuster timestampAdjuster) {
        boolean outOfNetworkIndicator;
        boolean programSpliceFlag;
        boolean spliceImmediateFlag;
        List<ComponentSplice> componentSplices;
        int uniqueProgramId;
        int availNum;
        int availsExpected;
        boolean autoReturn;
        long breakDurationUs;
        long programSplicePts;
        TimestampAdjuster timestampAdjuster2 = timestampAdjuster;
        long spliceEventId = sectionData.readUnsignedInt();
        boolean spliceEventCancelIndicator = (sectionData.readUnsignedByte() & 128) != 0;
        long programSplicePts2 = C0555C.TIME_UNSET;
        List<ComponentSplice> componentSplices2 = Collections.emptyList();
        boolean autoReturn2 = false;
        long breakDurationUs2 = C0555C.TIME_UNSET;
        if (spliceEventCancelIndicator) {
            outOfNetworkIndicator = false;
            programSpliceFlag = false;
            spliceImmediateFlag = false;
            componentSplices = componentSplices2;
            uniqueProgramId = 0;
            availNum = 0;
            availsExpected = 0;
            autoReturn = false;
            breakDurationUs = C0555C.TIME_UNSET;
            programSplicePts = C0555C.TIME_UNSET;
        } else {
            boolean outOfNetworkIndicator2;
            int headerByte = sectionData.readUnsignedByte();
            boolean outOfNetworkIndicator3 = (headerByte & 128) != 0;
            boolean programSpliceFlag2 = (headerByte & 64) != 0;
            boolean programSpliceFlag3 = (headerByte & 32) != 0;
            boolean spliceImmediateFlag2 = (headerByte & 16) != 0;
            if (programSpliceFlag2 && !spliceImmediateFlag2) {
                programSplicePts2 = TimeSignalCommand.parseSpliceTime(sectionData, ptsAdjustment);
            }
            if (programSpliceFlag2) {
                outOfNetworkIndicator2 = outOfNetworkIndicator3;
                programSpliceFlag = programSpliceFlag2;
                spliceImmediateFlag = spliceImmediateFlag2;
            } else {
                boolean componentCount;
                boolean componentCount2 = sectionData.readUnsignedByte();
                outOfNetworkIndicator2 = outOfNetworkIndicator3;
                componentSplices2 = new ArrayList(componentCount2);
                outOfNetworkIndicator3 = false;
                while (outOfNetworkIndicator3 < componentCount2) {
                    availsExpected = sectionData.readUnsignedByte();
                    if (spliceImmediateFlag2) {
                        programSpliceFlag = programSpliceFlag2;
                        componentCount = componentCount2;
                        programSpliceFlag2 = C0555C.TIME_UNSET;
                    } else {
                        programSpliceFlag = programSpliceFlag2;
                        componentCount = componentCount2;
                        programSpliceFlag2 = TimeSignalCommand.parseSpliceTime(sectionData, ptsAdjustment);
                    }
                    spliceImmediateFlag = spliceImmediateFlag2;
                    componentSplices2.add(new ComponentSplice(availsExpected, programSpliceFlag2, timestampAdjuster2.adjustTsTimestamp(programSpliceFlag2)));
                    outOfNetworkIndicator3++;
                    programSpliceFlag2 = programSpliceFlag;
                    componentCount2 = componentCount;
                    spliceImmediateFlag2 = spliceImmediateFlag;
                }
                programSpliceFlag = programSpliceFlag2;
                componentCount = componentCount2;
                spliceImmediateFlag = spliceImmediateFlag2;
            }
            if (programSpliceFlag3) {
                outOfNetworkIndicator3 = (long) sectionData.readUnsignedByte();
                autoReturn2 = (outOfNetworkIndicator3 & 128) != 0;
                breakDurationUs2 = (1000 * (((outOfNetworkIndicator3 & 1) << 32) | sectionData.readUnsignedInt())) / 90;
            }
            uniqueProgramId = sectionData.readUnsignedShort();
            availNum = sectionData.readUnsignedByte();
            availsExpected = sectionData.readUnsignedByte();
            componentSplices = componentSplices2;
            autoReturn = autoReturn2;
            breakDurationUs = breakDurationUs2;
            outOfNetworkIndicator = outOfNetworkIndicator2;
            programSplicePts = programSplicePts2;
        }
        return new SpliceInsertCommand(spliceEventId, spliceEventCancelIndicator, outOfNetworkIndicator, programSpliceFlag, spliceImmediateFlag, programSplicePts, timestampAdjuster2.adjustTsTimestamp(programSplicePts), componentSplices, autoReturn, breakDurationUs, uniqueProgramId, availNum, availsExpected);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.spliceEventId);
        dest.writeByte((byte) this.spliceEventCancelIndicator);
        dest.writeByte((byte) this.outOfNetworkIndicator);
        dest.writeByte((byte) this.programSpliceFlag);
        dest.writeByte((byte) this.spliceImmediateFlag);
        dest.writeLong(this.programSplicePts);
        dest.writeLong(this.programSplicePlaybackPositionUs);
        int componentSpliceListSize = this.componentSpliceList.size();
        dest.writeInt(componentSpliceListSize);
        for (int i = 0; i < componentSpliceListSize; i++) {
            ((ComponentSplice) this.componentSpliceList.get(i)).writeToParcel(dest);
        }
        dest.writeByte((byte) this.autoReturn);
        dest.writeLong(this.breakDurationUs);
        dest.writeInt(this.uniqueProgramId);
        dest.writeInt(this.availNum);
        dest.writeInt(this.availsExpected);
    }
}

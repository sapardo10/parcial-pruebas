package com.google.android.exoplayer2.text.dvb;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region.Op;
import android.support.v4.view.ViewCompat;
import android.util.SparseArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.objenesis.instantiator.util.ClassDefinitionUtils;

final class DvbParser {
    private static final int DATA_TYPE_24_TABLE_DATA = 32;
    private static final int DATA_TYPE_28_TABLE_DATA = 33;
    private static final int DATA_TYPE_2BP_CODE_STRING = 16;
    private static final int DATA_TYPE_48_TABLE_DATA = 34;
    private static final int DATA_TYPE_4BP_CODE_STRING = 17;
    private static final int DATA_TYPE_8BP_CODE_STRING = 18;
    private static final int DATA_TYPE_END_LINE = 240;
    private static final int OBJECT_CODING_PIXELS = 0;
    private static final int OBJECT_CODING_STRING = 1;
    private static final int PAGE_STATE_NORMAL = 0;
    private static final int REGION_DEPTH_4_BIT = 2;
    private static final int REGION_DEPTH_8_BIT = 3;
    private static final int SEGMENT_TYPE_CLUT_DEFINITION = 18;
    private static final int SEGMENT_TYPE_DISPLAY_DEFINITION = 20;
    private static final int SEGMENT_TYPE_OBJECT_DATA = 19;
    private static final int SEGMENT_TYPE_PAGE_COMPOSITION = 16;
    private static final int SEGMENT_TYPE_REGION_COMPOSITION = 17;
    private static final String TAG = "DvbParser";
    private static final byte[] defaultMap2To4 = new byte[]{(byte) 0, (byte) 7, (byte) 8, (byte) 15};
    private static final byte[] defaultMap2To8 = new byte[]{(byte) 0, (byte) 119, (byte) -120, (byte) -1};
    private static final byte[] defaultMap4To8 = new byte[]{(byte) 0, (byte) 17, (byte) 34, (byte) 51, (byte) 68, (byte) 85, (byte) 102, (byte) 119, (byte) -120, (byte) -103, (byte) -86, ClassDefinitionUtils.OPS_new, (byte) -52, (byte) -35, (byte) -18, (byte) -1};
    private Bitmap bitmap;
    private final Canvas canvas;
    private final ClutDefinition defaultClutDefinition;
    private final DisplayDefinition defaultDisplayDefinition;
    private final Paint defaultPaint = new Paint();
    private final Paint fillRegionPaint;
    private final SubtitleService subtitleService;

    private static final class ClutDefinition {
        public final int[] clutEntries2Bit;
        public final int[] clutEntries4Bit;
        public final int[] clutEntries8Bit;
        public final int id;

        public ClutDefinition(int id, int[] clutEntries2Bit, int[] clutEntries4Bit, int[] clutEntries8bit) {
            this.id = id;
            this.clutEntries2Bit = clutEntries2Bit;
            this.clutEntries4Bit = clutEntries4Bit;
            this.clutEntries8Bit = clutEntries8bit;
        }
    }

    private static final class DisplayDefinition {
        public final int height;
        public final int horizontalPositionMaximum;
        public final int horizontalPositionMinimum;
        public final int verticalPositionMaximum;
        public final int verticalPositionMinimum;
        public final int width;

        public DisplayDefinition(int width, int height, int horizontalPositionMinimum, int horizontalPositionMaximum, int verticalPositionMinimum, int verticalPositionMaximum) {
            this.width = width;
            this.height = height;
            this.horizontalPositionMinimum = horizontalPositionMinimum;
            this.horizontalPositionMaximum = horizontalPositionMaximum;
            this.verticalPositionMinimum = verticalPositionMinimum;
            this.verticalPositionMaximum = verticalPositionMaximum;
        }
    }

    private static final class ObjectData {
        public final byte[] bottomFieldData;
        public final int id;
        public final boolean nonModifyingColorFlag;
        public final byte[] topFieldData;

        public ObjectData(int id, boolean nonModifyingColorFlag, byte[] topFieldData, byte[] bottomFieldData) {
            this.id = id;
            this.nonModifyingColorFlag = nonModifyingColorFlag;
            this.topFieldData = topFieldData;
            this.bottomFieldData = bottomFieldData;
        }
    }

    private static final class PageComposition {
        public final SparseArray<PageRegion> regions;
        public final int state;
        public final int timeOutSecs;
        public final int version;

        public PageComposition(int timeoutSecs, int version, int state, SparseArray<PageRegion> regions) {
            this.timeOutSecs = timeoutSecs;
            this.version = version;
            this.state = state;
            this.regions = regions;
        }
    }

    private static final class PageRegion {
        public final int horizontalAddress;
        public final int verticalAddress;

        public PageRegion(int horizontalAddress, int verticalAddress) {
            this.horizontalAddress = horizontalAddress;
            this.verticalAddress = verticalAddress;
        }
    }

    private static final class RegionComposition {
        public final int clutId;
        public final int depth;
        public final boolean fillFlag;
        public final int height;
        public final int id;
        public final int levelOfCompatibility;
        public final int pixelCode2Bit;
        public final int pixelCode4Bit;
        public final int pixelCode8Bit;
        public final SparseArray<RegionObject> regionObjects;
        public final int width;

        public RegionComposition(int id, boolean fillFlag, int width, int height, int levelOfCompatibility, int depth, int clutId, int pixelCode8Bit, int pixelCode4Bit, int pixelCode2Bit, SparseArray<RegionObject> regionObjects) {
            this.id = id;
            this.fillFlag = fillFlag;
            this.width = width;
            this.height = height;
            this.levelOfCompatibility = levelOfCompatibility;
            this.depth = depth;
            this.clutId = clutId;
            this.pixelCode8Bit = pixelCode8Bit;
            this.pixelCode4Bit = pixelCode4Bit;
            this.pixelCode2Bit = pixelCode2Bit;
            this.regionObjects = regionObjects;
        }

        public void mergeFrom(RegionComposition otherRegionComposition) {
            if (otherRegionComposition != null) {
                SparseArray<RegionObject> otherRegionObjects = otherRegionComposition.regionObjects;
                for (int i = 0; i < otherRegionObjects.size(); i++) {
                    this.regionObjects.put(otherRegionObjects.keyAt(i), otherRegionObjects.valueAt(i));
                }
            }
        }
    }

    private static final class RegionObject {
        public final int backgroundPixelCode;
        public final int foregroundPixelCode;
        public final int horizontalPosition;
        public final int provider;
        public final int type;
        public final int verticalPosition;

        public RegionObject(int type, int provider, int horizontalPosition, int verticalPosition, int foregroundPixelCode, int backgroundPixelCode) {
            this.type = type;
            this.provider = provider;
            this.horizontalPosition = horizontalPosition;
            this.verticalPosition = verticalPosition;
            this.foregroundPixelCode = foregroundPixelCode;
            this.backgroundPixelCode = backgroundPixelCode;
        }
    }

    private static final class SubtitleService {
        public final SparseArray<ClutDefinition> ancillaryCluts = new SparseArray();
        public final SparseArray<ObjectData> ancillaryObjects = new SparseArray();
        public final int ancillaryPageId;
        public final SparseArray<ClutDefinition> cluts = new SparseArray();
        public DisplayDefinition displayDefinition;
        public final SparseArray<ObjectData> objects = new SparseArray();
        public PageComposition pageComposition;
        public final SparseArray<RegionComposition> regions = new SparseArray();
        public final int subtitlePageId;

        public SubtitleService(int subtitlePageId, int ancillaryPageId) {
            this.subtitlePageId = subtitlePageId;
            this.ancillaryPageId = ancillaryPageId;
        }

        public void reset() {
            this.regions.clear();
            this.cluts.clear();
            this.objects.clear();
            this.ancillaryCluts.clear();
            this.ancillaryObjects.clear();
            this.displayDefinition = null;
            this.pageComposition = null;
        }
    }

    public DvbParser(int subtitlePageId, int ancillaryPageId) {
        this.defaultPaint.setStyle(Style.FILL_AND_STROKE);
        this.defaultPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
        this.defaultPaint.setPathEffect(null);
        this.fillRegionPaint = new Paint();
        this.fillRegionPaint.setStyle(Style.FILL);
        this.fillRegionPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OVER));
        this.fillRegionPaint.setPathEffect(null);
        this.canvas = new Canvas();
        this.defaultDisplayDefinition = new DisplayDefinition(719, 575, 0, 719, 0, 575);
        this.defaultClutDefinition = new ClutDefinition(0, generateDefault2BitClutEntries(), generateDefault4BitClutEntries(), generateDefault8BitClutEntries());
        this.subtitleService = new SubtitleService(subtitlePageId, ancillaryPageId);
    }

    public void reset() {
        this.subtitleService.reset();
    }

    public List<Cue> decode(byte[] data, int limit) {
        DvbParser dvbParser = this;
        ParsableBitArray dataBitArray = new ParsableBitArray(data, limit);
        while (dataBitArray.bitsLeft() >= 48) {
            if (dataBitArray.readBits(8) != 15) {
                break;
            }
            parseSubtitlingSegment(dataBitArray, dvbParser.subtitleService);
        }
        if (dvbParser.subtitleService.pageComposition == null) {
            return Collections.emptyList();
        }
        List<Cue> cues;
        SparseArray<PageRegion> pageRegions;
        int i;
        PageRegion pageRegion;
        RegionComposition regionComposition;
        int baseHorizontalAddress;
        int baseVerticalAddress;
        SparseArray<PageRegion> pageRegions2;
        SparseArray<RegionObject> regionObjects;
        int j;
        int objectId;
        RegionObject regionObject;
        ObjectData objectData;
        PageRegion pageRegion2;
        ParsableBitArray clutDefinition;
        int color;
        DisplayDefinition displayDefinition = dvbParser.subtitleService.displayDefinition != null ? dvbParser.subtitleService.displayDefinition : dvbParser.defaultDisplayDefinition;
        if (dvbParser.bitmap != null && displayDefinition.width + 1 == dvbParser.bitmap.getWidth()) {
            if (displayDefinition.height + 1 == dvbParser.bitmap.getHeight()) {
                cues = new ArrayList();
                pageRegions = dvbParser.subtitleService.pageComposition.regions;
                i = 0;
                while (i < pageRegions.size()) {
                    pageRegion = (PageRegion) pageRegions.valueAt(i);
                    regionComposition = (RegionComposition) dvbParser.subtitleService.regions.get(pageRegions.keyAt(i));
                    baseHorizontalAddress = pageRegion.horizontalAddress + displayDefinition.horizontalPositionMinimum;
                    baseVerticalAddress = pageRegion.verticalAddress + displayDefinition.verticalPositionMinimum;
                    ParsableBitArray dataBitArray2 = dataBitArray;
                    pageRegions2 = pageRegions;
                    dvbParser.canvas.clipRect((float) baseHorizontalAddress, (float) baseVerticalAddress, (float) Math.min(regionComposition.width + baseHorizontalAddress, displayDefinition.horizontalPositionMaximum), (float) Math.min(regionComposition.height + baseVerticalAddress, displayDefinition.verticalPositionMaximum), Op.REPLACE);
                    dataBitArray = (ClutDefinition) dvbParser.subtitleService.cluts.get(regionComposition.clutId);
                    if (dataBitArray == null) {
                        dataBitArray = (ClutDefinition) dvbParser.subtitleService.ancillaryCluts.get(regionComposition.clutId);
                        if (dataBitArray == null) {
                            dataBitArray = dvbParser.defaultClutDefinition;
                        }
                    }
                    regionObjects = regionComposition.regionObjects;
                    j = 0;
                    while (j < regionObjects.size()) {
                        objectId = regionObjects.keyAt(j);
                        regionObject = (RegionObject) regionObjects.valueAt(j);
                        SparseArray<RegionObject> regionObjects2 = regionObjects;
                        regionObjects = (ObjectData) dvbParser.subtitleService.objects.get(objectId);
                        if (regionObjects != null) {
                            objectData = regionObjects;
                            regionObjects = (ObjectData) dvbParser.subtitleService.ancillaryObjects.get(objectId);
                        } else {
                            objectData = regionObjects;
                        }
                        if (regionObjects == null) {
                            pageRegion2 = pageRegion;
                            paintPixelDataSubBlocks(regionObjects, dataBitArray, regionComposition.depth, baseHorizontalAddress + regionObject.horizontalPosition, baseVerticalAddress + regionObject.verticalPosition, regionObjects.nonModifyingColorFlag ? null : dvbParser.defaultPaint, dvbParser.canvas);
                        } else {
                            pageRegion2 = pageRegion;
                        }
                        j++;
                        regionObjects = regionObjects2;
                        pageRegion = pageRegion2;
                    }
                    pageRegion2 = pageRegion;
                    if (regionComposition.fillFlag) {
                        clutDefinition = dataBitArray;
                    } else {
                        if (regionComposition.depth == 3) {
                            color = dataBitArray.clutEntries8Bit[regionComposition.pixelCode8Bit];
                        } else if (regionComposition.depth != 2) {
                            color = dataBitArray.clutEntries4Bit[regionComposition.pixelCode4Bit];
                        } else {
                            color = dataBitArray.clutEntries2Bit[regionComposition.pixelCode2Bit];
                        }
                        dvbParser.fillRegionPaint.setColor(color);
                        clutDefinition = dataBitArray;
                        dvbParser.canvas.drawRect((float) baseHorizontalAddress, (float) baseVerticalAddress, (float) (regionComposition.width + baseHorizontalAddress), (float) (regionComposition.height + baseVerticalAddress), dvbParser.fillRegionPaint);
                    }
                    cues.add(new Cue(Bitmap.createBitmap(dvbParser.bitmap, baseHorizontalAddress, baseVerticalAddress, regionComposition.width, regionComposition.height), ((float) baseHorizontalAddress) / ((float) displayDefinition.width), 0, ((float) baseVerticalAddress) / ((float) displayDefinition.height), 0, ((float) regionComposition.width) / ((float) displayDefinition.width), ((float) regionComposition.height) / ((float) displayDefinition.height)));
                    dvbParser.canvas.drawColor(0, Mode.CLEAR);
                    i++;
                    dataBitArray = dataBitArray2;
                    pageRegions = pageRegions2;
                    byte[] bArr = data;
                    j = limit;
                }
                pageRegions2 = pageRegions;
                return cues;
            }
        }
        dvbParser.bitmap = Bitmap.createBitmap(displayDefinition.width + 1, displayDefinition.height + 1, Config.ARGB_8888);
        dvbParser.canvas.setBitmap(dvbParser.bitmap);
        cues = new ArrayList();
        pageRegions = dvbParser.subtitleService.pageComposition.regions;
        i = 0;
        while (i < pageRegions.size()) {
            pageRegion = (PageRegion) pageRegions.valueAt(i);
            regionComposition = (RegionComposition) dvbParser.subtitleService.regions.get(pageRegions.keyAt(i));
            baseHorizontalAddress = pageRegion.horizontalAddress + displayDefinition.horizontalPositionMinimum;
            baseVerticalAddress = pageRegion.verticalAddress + displayDefinition.verticalPositionMinimum;
            ParsableBitArray dataBitArray22 = dataBitArray;
            pageRegions2 = pageRegions;
            dvbParser.canvas.clipRect((float) baseHorizontalAddress, (float) baseVerticalAddress, (float) Math.min(regionComposition.width + baseHorizontalAddress, displayDefinition.horizontalPositionMaximum), (float) Math.min(regionComposition.height + baseVerticalAddress, displayDefinition.verticalPositionMaximum), Op.REPLACE);
            dataBitArray = (ClutDefinition) dvbParser.subtitleService.cluts.get(regionComposition.clutId);
            if (dataBitArray == null) {
                dataBitArray = (ClutDefinition) dvbParser.subtitleService.ancillaryCluts.get(regionComposition.clutId);
                if (dataBitArray == null) {
                    dataBitArray = dvbParser.defaultClutDefinition;
                }
            }
            regionObjects = regionComposition.regionObjects;
            j = 0;
            while (j < regionObjects.size()) {
                objectId = regionObjects.keyAt(j);
                regionObject = (RegionObject) regionObjects.valueAt(j);
                SparseArray<RegionObject> regionObjects22 = regionObjects;
                regionObjects = (ObjectData) dvbParser.subtitleService.objects.get(objectId);
                if (regionObjects != null) {
                    objectData = regionObjects;
                } else {
                    objectData = regionObjects;
                    regionObjects = (ObjectData) dvbParser.subtitleService.ancillaryObjects.get(objectId);
                }
                if (regionObjects == null) {
                    pageRegion2 = pageRegion;
                } else {
                    if (regionObjects.nonModifyingColorFlag) {
                    }
                    pageRegion2 = pageRegion;
                    paintPixelDataSubBlocks(regionObjects, dataBitArray, regionComposition.depth, baseHorizontalAddress + regionObject.horizontalPosition, baseVerticalAddress + regionObject.verticalPosition, regionObjects.nonModifyingColorFlag ? null : dvbParser.defaultPaint, dvbParser.canvas);
                }
                j++;
                regionObjects = regionObjects22;
                pageRegion = pageRegion2;
            }
            pageRegion2 = pageRegion;
            if (regionComposition.fillFlag) {
                clutDefinition = dataBitArray;
            } else {
                if (regionComposition.depth == 3) {
                    color = dataBitArray.clutEntries8Bit[regionComposition.pixelCode8Bit];
                } else if (regionComposition.depth != 2) {
                    color = dataBitArray.clutEntries2Bit[regionComposition.pixelCode2Bit];
                } else {
                    color = dataBitArray.clutEntries4Bit[regionComposition.pixelCode4Bit];
                }
                dvbParser.fillRegionPaint.setColor(color);
                clutDefinition = dataBitArray;
                dvbParser.canvas.drawRect((float) baseHorizontalAddress, (float) baseVerticalAddress, (float) (regionComposition.width + baseHorizontalAddress), (float) (regionComposition.height + baseVerticalAddress), dvbParser.fillRegionPaint);
            }
            cues.add(new Cue(Bitmap.createBitmap(dvbParser.bitmap, baseHorizontalAddress, baseVerticalAddress, regionComposition.width, regionComposition.height), ((float) baseHorizontalAddress) / ((float) displayDefinition.width), 0, ((float) baseVerticalAddress) / ((float) displayDefinition.height), 0, ((float) regionComposition.width) / ((float) displayDefinition.width), ((float) regionComposition.height) / ((float) displayDefinition.height)));
            dvbParser.canvas.drawColor(0, Mode.CLEAR);
            i++;
            dataBitArray = dataBitArray22;
            pageRegions = pageRegions2;
            byte[] bArr2 = data;
            j = limit;
        }
        pageRegions2 = pageRegions;
        return cues;
    }

    private static void parseSubtitlingSegment(ParsableBitArray data, SubtitleService service) {
        int segmentType = data.readBits(8);
        int pageId = data.readBits(16);
        int dataFieldLength = data.readBits(16);
        int dataFieldLimit = data.getBytePosition() + dataFieldLength;
        if (dataFieldLength * 8 > data.bitsLeft()) {
            Log.m10w(TAG, "Data field length exceeds limit");
            data.skipBits(data.bitsLeft());
            return;
        }
        PageComposition current;
        switch (segmentType) {
            case 16:
                if (pageId != service.subtitlePageId) {
                    break;
                }
                current = service.pageComposition;
                PageComposition pageComposition = parsePageComposition(data, dataFieldLength);
                if (pageComposition.state != 0) {
                    service.pageComposition = pageComposition;
                    service.regions.clear();
                    service.cluts.clear();
                    service.objects.clear();
                } else if (current != null && current.version != pageComposition.version) {
                    service.pageComposition = pageComposition;
                }
                break;
            case 17:
                current = service.pageComposition;
                if (pageId == service.subtitlePageId && current != null) {
                    RegionComposition regionComposition = parseRegionComposition(data, dataFieldLength);
                    if (current.state == 0) {
                        regionComposition.mergeFrom((RegionComposition) service.regions.get(regionComposition.id));
                    }
                    service.regions.put(regionComposition.id, regionComposition);
                    break;
                }
                break;
                break;
            case 18:
                ClutDefinition clutDefinition;
                if (pageId != service.subtitlePageId) {
                    if (pageId == service.ancillaryPageId) {
                        clutDefinition = parseClutDefinition(data, dataFieldLength);
                        service.ancillaryCluts.put(clutDefinition.id, clutDefinition);
                        break;
                    }
                }
                clutDefinition = parseClutDefinition(data, dataFieldLength);
                service.cluts.put(clutDefinition.id, clutDefinition);
                break;
            case 19:
                ObjectData objectData;
                if (pageId != service.subtitlePageId) {
                    if (pageId == service.ancillaryPageId) {
                        objectData = parseObjectData(data);
                        service.ancillaryObjects.put(objectData.id, objectData);
                        break;
                    }
                }
                objectData = parseObjectData(data);
                service.objects.put(objectData.id, objectData);
                break;
            case 20:
                if (pageId != service.subtitlePageId) {
                    break;
                }
                service.displayDefinition = parseDisplayDefinition(data);
                break;
            default:
                break;
        }
        data.skipBytes(dataFieldLimit - data.getBytePosition());
    }

    private static DisplayDefinition parseDisplayDefinition(ParsableBitArray data) {
        int verticalPositionMaximum;
        int horizontalPositionMinimum;
        int horizontalPositionMaximum;
        int verticalPositionMinimum;
        data.skipBits(4);
        boolean displayWindowFlag = data.readBit();
        data.skipBits(3);
        int width = data.readBits(16);
        int height = data.readBits(16);
        if (displayWindowFlag) {
            int horizontalPositionMinimum2 = data.readBits(16);
            int horizontalPositionMaximum2 = data.readBits(16);
            int verticalPositionMinimum2 = data.readBits(16);
            verticalPositionMaximum = data.readBits(16);
            horizontalPositionMinimum = horizontalPositionMinimum2;
            horizontalPositionMaximum = horizontalPositionMaximum2;
            verticalPositionMinimum = verticalPositionMinimum2;
        } else {
            horizontalPositionMinimum = 0;
            horizontalPositionMaximum = width;
            verticalPositionMinimum = 0;
            verticalPositionMaximum = height;
        }
        return new DisplayDefinition(width, height, horizontalPositionMinimum, horizontalPositionMaximum, verticalPositionMinimum, verticalPositionMaximum);
    }

    private static PageComposition parsePageComposition(ParsableBitArray data, int length) {
        int timeoutSecs = data.readBits(8);
        int version = data.readBits(4);
        int state = data.readBits(2);
        data.skipBits(2);
        int remainingLength = length - 2;
        SparseArray<PageRegion> regions = new SparseArray();
        while (remainingLength > 0) {
            int regionId = data.readBits(8);
            data.skipBits(8);
            remainingLength -= 6;
            regions.put(regionId, new PageRegion(data.readBits(16), data.readBits(16)));
        }
        return new PageComposition(timeoutSecs, version, state, regions);
    }

    private static RegionComposition parseRegionComposition(ParsableBitArray data, int length) {
        ParsableBitArray parsableBitArray = data;
        int i = 8;
        int id = parsableBitArray.readBits(8);
        parsableBitArray.skipBits(4);
        boolean fillFlag = data.readBit();
        parsableBitArray.skipBits(3);
        int width = parsableBitArray.readBits(16);
        int height = parsableBitArray.readBits(16);
        int levelOfCompatibility = parsableBitArray.readBits(3);
        int depth = parsableBitArray.readBits(3);
        parsableBitArray.skipBits(2);
        int clutId = parsableBitArray.readBits(8);
        int pixelCode8Bit = parsableBitArray.readBits(8);
        int pixelCode4Bit = parsableBitArray.readBits(4);
        int pixelCode2Bit = parsableBitArray.readBits(2);
        parsableBitArray.skipBits(2);
        int remainingLength = length - 10;
        SparseArray<RegionObject> regionObjects = new SparseArray();
        int remainingLength2 = remainingLength;
        while (remainingLength2 > 0) {
            int backgroundPixelCode;
            int remainingLength3;
            RegionObject regionObject;
            RegionObject regionObject2;
            remainingLength = parsableBitArray.readBits(16);
            int objectType = parsableBitArray.readBits(2);
            int objectProvider = parsableBitArray.readBits(2);
            int objectHorizontalPosition = parsableBitArray.readBits(12);
            parsableBitArray.skipBits(4);
            int objectVerticalPosition = parsableBitArray.readBits(12);
            remainingLength2 -= 6;
            if (objectType != 1) {
                if (objectType != 2) {
                    backgroundPixelCode = 0;
                    remainingLength3 = remainingLength2;
                    remainingLength2 = 0;
                    regionObject = regionObject2;
                    regionObject2 = new RegionObject(objectType, objectProvider, objectHorizontalPosition, objectVerticalPosition, remainingLength2, backgroundPixelCode);
                    regionObjects.put(remainingLength, regionObject);
                    remainingLength2 = remainingLength3;
                    i = 8;
                }
            }
            int foregroundPixelCode = parsableBitArray.readBits(i);
            backgroundPixelCode = parsableBitArray.readBits(i);
            remainingLength3 = remainingLength2 - 2;
            remainingLength2 = foregroundPixelCode;
            regionObject = regionObject2;
            regionObject2 = new RegionObject(objectType, objectProvider, objectHorizontalPosition, objectVerticalPosition, remainingLength2, backgroundPixelCode);
            regionObjects.put(remainingLength, regionObject);
            remainingLength2 = remainingLength3;
            i = 8;
        }
        return new RegionComposition(id, fillFlag, width, height, levelOfCompatibility, depth, clutId, pixelCode8Bit, pixelCode4Bit, pixelCode2Bit, regionObjects);
    }

    private static ClutDefinition parseClutDefinition(ParsableBitArray data, int length) {
        int[] clutEntries2Bit;
        int[] clutEntries4Bit;
        ParsableBitArray parsableBitArray = data;
        int i = 8;
        int clutId = parsableBitArray.readBits(8);
        parsableBitArray.skipBits(8);
        int remainingLength = length - 2;
        int[] clutEntries2Bit2 = generateDefault2BitClutEntries();
        int[] clutEntries4Bit2 = generateDefault4BitClutEntries();
        int[] clutEntries8Bit = generateDefault8BitClutEntries();
        while (remainingLength > 0) {
            int[] clutEntries;
            int y;
            int cr;
            int readBits;
            int readBits2;
            int i2;
            int entryId = parsableBitArray.readBits(i);
            int entryFlags = parsableBitArray.readBits(i);
            remainingLength -= 2;
            if ((entryFlags & 128) != 0) {
                clutEntries = clutEntries2Bit2;
            } else if ((entryFlags & 64) != 0) {
                clutEntries = clutEntries4Bit2;
            } else {
                clutEntries = clutEntries8Bit;
            }
            if ((entryFlags & 1) != 0) {
                y = parsableBitArray.readBits(i);
                cr = parsableBitArray.readBits(i);
                readBits = parsableBitArray.readBits(i);
                readBits2 = parsableBitArray.readBits(i);
                remainingLength -= 4;
                i2 = cr;
            } else {
                i2 = parsableBitArray.readBits(4) << 4;
                remainingLength -= 2;
                readBits = parsableBitArray.readBits(4) << 4;
                readBits2 = parsableBitArray.readBits(2) << 6;
                y = parsableBitArray.readBits(6) << 2;
            }
            if (y == 0) {
                i2 = 0;
                readBits = 0;
                readBits2 = 255;
            }
            cr = (byte) (255 - (readBits2 & 255));
            int clutId2 = clutId;
            double d = (double) y;
            int remainingLength2 = remainingLength;
            clutEntries2Bit = clutEntries2Bit2;
            double d2 = (double) (i2 - 128);
            Double.isNaN(d2);
            d2 *= 1.402d;
            Double.isNaN(d);
            i = (int) (d + d2);
            clutId = (double) y;
            clutEntries4Bit = clutEntries4Bit2;
            double d3 = (double) (readBits - 128);
            Double.isNaN(d3);
            d3 *= 0.34414d;
            Double.isNaN(clutId);
            clutId -= d3;
            d3 = (double) (i2 - 128);
            Double.isNaN(d3);
            clutId = (int) (clutId - (d3 * 0.71414d));
            d2 = (double) y;
            double d4 = (double) (readBits - 128);
            Double.isNaN(d4);
            d4 *= 1.772d;
            Double.isNaN(d2);
            clutEntries[entryId] = getColor(cr, Util.constrainValue(i, 0, 255), Util.constrainValue(clutId, 0, 255), Util.constrainValue((int) (d2 + d4), 0, 255));
            clutEntries4Bit2 = clutEntries4Bit;
            clutId = clutId2;
            clutEntries2Bit2 = clutEntries2Bit;
            remainingLength = remainingLength2;
            i = 8;
        }
        clutEntries2Bit = clutEntries2Bit2;
        clutEntries4Bit = clutEntries4Bit2;
        return new ClutDefinition(clutId, clutEntries2Bit2, clutEntries4Bit2, clutEntries8Bit);
    }

    private static ObjectData parseObjectData(ParsableBitArray data) {
        int objectId = data.readBits(16);
        data.skipBits(4);
        int objectCodingMethod = data.readBits(2);
        boolean nonModifyingColorFlag = data.readBit();
        data.skipBits(1);
        byte[] topFieldData = null;
        byte[] bottomFieldData = null;
        if (objectCodingMethod == 1) {
            data.skipBits(data.readBits(8) * 16);
        } else if (objectCodingMethod == 0) {
            int topFieldDataLength = data.readBits(16);
            int bottomFieldDataLength = data.readBits(16);
            if (topFieldDataLength > 0) {
                topFieldData = new byte[topFieldDataLength];
                data.readBytes(topFieldData, 0, topFieldDataLength);
            }
            if (bottomFieldDataLength > 0) {
                bottomFieldData = new byte[bottomFieldDataLength];
                data.readBytes(bottomFieldData, 0, bottomFieldDataLength);
            } else {
                bottomFieldData = topFieldData;
            }
            return new ObjectData(objectId, nonModifyingColorFlag, topFieldData, bottomFieldData);
        }
        return new ObjectData(objectId, nonModifyingColorFlag, topFieldData, bottomFieldData);
    }

    private static int[] generateDefault2BitClutEntries() {
        return new int[]{0, -1, ViewCompat.MEASURED_STATE_MASK, -8421505};
    }

    private static int[] generateDefault4BitClutEntries() {
        int[] entries = new int[16];
        entries[0] = 0;
        for (int i = 1; i < entries.length; i++) {
            if (i < 8) {
                entries[i] = getColor(255, (i & 1) != 0 ? 255 : 0, (i & 2) != 0 ? 255 : 0, (i & 4) != 0 ? 255 : 0);
            } else {
                int i2 = 127;
                int i3 = (i & 1) != 0 ? 127 : 0;
                int i4 = (i & 2) != 0 ? 127 : 0;
                if ((i & 4) == 0) {
                    i2 = 0;
                }
                entries[i] = getColor(255, i3, i4, i2);
            }
        }
        return entries;
    }

    private static int[] generateDefault8BitClutEntries() {
        int[] entries = new int[256];
        entries[0] = 0;
        for (int i = 0; i < entries.length; i++) {
            int i2 = 255;
            int i3;
            int i4;
            if (i < 8) {
                i3 = (i & 1) != 0 ? 255 : 0;
                i4 = (i & 2) != 0 ? 255 : 0;
                if ((i & 4) == 0) {
                    i2 = 0;
                }
                entries[i] = getColor(63, i3, i4, i2);
            } else {
                i3 = i & 136;
                i4 = 170;
                int i5 = 85;
                int i6;
                if (i3 == 0) {
                    i6 = ((i & 1) != 0 ? 85 : 0) + ((i & 16) != 0 ? 170 : 0);
                    i3 = ((i & 2) != 0 ? 85 : 0) + ((i & 32) != 0 ? 170 : 0);
                    if ((i & 4) == 0) {
                        i5 = 0;
                    }
                    if ((i & 64) == 0) {
                        i4 = 0;
                    }
                    entries[i] = getColor(255, i6, i3, i5 + i4);
                } else if (i3 != 8) {
                    i4 = 43;
                    if (i3 == 128) {
                        i6 = (((i & 1) != 0 ? 43 : 0) + 127) + ((i & 16) != 0 ? 85 : 0);
                        i3 = (((i & 2) != 0 ? 43 : 0) + 127) + ((i & 32) != 0 ? 85 : 0);
                        if ((i & 4) == 0) {
                            i4 = 0;
                        }
                        i4 += 127;
                        if ((i & 64) == 0) {
                            i5 = 0;
                        }
                        entries[i] = getColor(255, i6, i3, i4 + i5);
                    } else if (i3 == 136) {
                        i6 = ((i & 1) != 0 ? 43 : 0) + ((i & 16) != 0 ? 85 : 0);
                        i3 = ((i & 2) != 0 ? 43 : 0) + ((i & 32) != 0 ? 85 : 0);
                        if ((i & 4) == 0) {
                            i4 = 0;
                        }
                        if ((i & 64) == 0) {
                            i5 = 0;
                        }
                        entries[i] = getColor(255, i6, i3, i4 + i5);
                    }
                } else {
                    i6 = ((i & 1) != 0 ? 85 : 0) + ((i & 16) != 0 ? 170 : 0);
                    i2 = ((i & 2) != 0 ? 85 : 0) + ((i & 32) != 0 ? 170 : 0);
                    if ((i & 4) == 0) {
                        i5 = 0;
                    }
                    if ((i & 64) == 0) {
                        i4 = 0;
                    }
                    entries[i] = getColor(127, i6, i2, i5 + i4);
                }
            }
        }
        return entries;
    }

    private static int getColor(int a, int r, int g, int b) {
        return (((a << 24) | (r << 16)) | (g << 8)) | b;
    }

    private static void paintPixelDataSubBlocks(ObjectData objectData, ClutDefinition clutDefinition, int regionDepth, int horizontalAddress, int verticalAddress, Paint paint, Canvas canvas) {
        int[] clutEntries;
        if (regionDepth == 3) {
            clutEntries = clutDefinition.clutEntries8Bit;
        } else if (regionDepth == 2) {
            clutEntries = clutDefinition.clutEntries4Bit;
        } else {
            clutEntries = clutDefinition.clutEntries2Bit;
        }
        int[] iArr = clutEntries;
        int i = regionDepth;
        int i2 = horizontalAddress;
        Paint paint2 = paint;
        Canvas canvas2 = canvas;
        paintPixelDataSubBlock(objectData.topFieldData, iArr, i, i2, verticalAddress, paint2, canvas2);
        paintPixelDataSubBlock(objectData.bottomFieldData, iArr, i, i2, verticalAddress + 1, paint2, canvas2);
    }

    private static void paintPixelDataSubBlock(byte[] pixelData, int[] clutEntries, int regionDepth, int horizontalAddress, int verticalAddress, Paint paint, Canvas canvas) {
        int i = regionDepth;
        ParsableBitArray data = new ParsableBitArray(pixelData);
        int column = horizontalAddress;
        int line = verticalAddress;
        byte[] clutMapTable2To4 = null;
        byte[] clutMapTable2To8 = null;
        while (data.bitsLeft() != 0) {
            int dataType = data.readBits(8);
            if (dataType != 240) {
                byte[] clutMapTable2ToX;
                switch (dataType) {
                    case 16:
                        if (i == 3) {
                            clutMapTable2ToX = clutMapTable2To8 == null ? defaultMap2To8 : clutMapTable2To8;
                        } else if (i == 2) {
                            clutMapTable2ToX = clutMapTable2To4 == null ? defaultMap2To4 : clutMapTable2To4;
                        } else {
                            clutMapTable2ToX = null;
                        }
                        column = paint2BitPixelCodeString(data, clutEntries, clutMapTable2ToX, column, line, paint, canvas);
                        data.byteAlign();
                        break;
                    case 17:
                        if (i == 3) {
                            clutMapTable2ToX = null == null ? defaultMap4To8 : null;
                        } else {
                            clutMapTable2ToX = null;
                        }
                        column = paint4BitPixelCodeString(data, clutEntries, clutMapTable2ToX, column, line, paint, canvas);
                        data.byteAlign();
                        break;
                    case 18:
                        column = paint8BitPixelCodeString(data, clutEntries, null, column, line, paint, canvas);
                        break;
                    default:
                        switch (dataType) {
                            case 32:
                                clutMapTable2To4 = buildClutMapTable(4, 4, data);
                                break;
                            case 33:
                                clutMapTable2To8 = buildClutMapTable(4, 8, data);
                                break;
                            case 34:
                                clutMapTable2To8 = buildClutMapTable(16, 8, data);
                                break;
                            default:
                                break;
                        }
                }
            }
            column = horizontalAddress;
            line += 2;
        }
    }

    private static int paint2BitPixelCodeString(ParsableBitArray data, int[] clutEntries, byte[] clutMapTable, int column, int line, Paint paint, Canvas canvas) {
        ParsableBitArray parsableBitArray = data;
        int i = line;
        Paint paint2 = paint;
        boolean endOfPixelCodeString = false;
        int column2 = column;
        while (true) {
            boolean endOfPixelCodeString2;
            int runLength;
            int clutIndex;
            int peek = data.readBits(2);
            if (peek == 0) {
                if (!data.readBit()) {
                    if (!data.readBit()) {
                        switch (data.readBits(2)) {
                            case 0:
                                endOfPixelCodeString2 = true;
                                runLength = 0;
                                clutIndex = 0;
                                break;
                            case 1:
                                endOfPixelCodeString2 = endOfPixelCodeString;
                                runLength = 2;
                                clutIndex = 0;
                                break;
                            case 2:
                                endOfPixelCodeString2 = endOfPixelCodeString;
                                runLength = data.readBits(4) + 12;
                                clutIndex = data.readBits(2);
                                break;
                            case 3:
                                endOfPixelCodeString2 = endOfPixelCodeString;
                                runLength = data.readBits(8) + 29;
                                clutIndex = data.readBits(2);
                                break;
                            default:
                                endOfPixelCodeString2 = endOfPixelCodeString;
                                runLength = 0;
                                clutIndex = 0;
                                break;
                        }
                    }
                    endOfPixelCodeString2 = endOfPixelCodeString;
                    runLength = 1;
                    clutIndex = 0;
                } else {
                    endOfPixelCodeString2 = endOfPixelCodeString;
                    runLength = data.readBits(3) + 3;
                    clutIndex = data.readBits(2);
                }
            } else {
                endOfPixelCodeString2 = endOfPixelCodeString;
                runLength = 1;
                clutIndex = peek;
            }
            if (runLength != 0 && paint2 != null) {
                paint2.setColor(clutEntries[clutMapTable != null ? clutMapTable[clutIndex] : clutIndex]);
                canvas.drawRect((float) column2, (float) i, (float) (column2 + runLength), (float) (i + 1), paint);
            }
            column2 += runLength;
            if (endOfPixelCodeString2) {
                return column2;
            }
            endOfPixelCodeString = endOfPixelCodeString2;
        }
    }

    private static int paint4BitPixelCodeString(ParsableBitArray data, int[] clutEntries, byte[] clutMapTable, int column, int line, Paint paint, Canvas canvas) {
        ParsableBitArray parsableBitArray = data;
        int i = line;
        Paint paint2 = paint;
        boolean endOfPixelCodeString = false;
        int column2 = column;
        while (true) {
            boolean endOfPixelCodeString2;
            int runLength;
            int clutIndex;
            int peek = data.readBits(4);
            int i2;
            if (peek == 0) {
                if (data.readBit()) {
                    if (data.readBit()) {
                        switch (data.readBits(2)) {
                            case 0:
                                endOfPixelCodeString2 = endOfPixelCodeString;
                                runLength = 1;
                                clutIndex = 0;
                                break;
                            case 1:
                                endOfPixelCodeString2 = endOfPixelCodeString;
                                runLength = 2;
                                clutIndex = 0;
                                i2 = peek;
                                break;
                            case 2:
                                endOfPixelCodeString2 = endOfPixelCodeString;
                                runLength = data.readBits(4) + 9;
                                clutIndex = data.readBits(4);
                                i2 = peek;
                                break;
                            case 3:
                                endOfPixelCodeString2 = endOfPixelCodeString;
                                runLength = data.readBits(8) + 25;
                                clutIndex = data.readBits(4);
                                i2 = peek;
                                break;
                            default:
                                endOfPixelCodeString2 = endOfPixelCodeString;
                                runLength = 0;
                                clutIndex = 0;
                                i2 = peek;
                                break;
                        }
                    }
                    endOfPixelCodeString2 = endOfPixelCodeString;
                    runLength = data.readBits(2) + 4;
                    clutIndex = data.readBits(4);
                    i2 = peek;
                } else {
                    peek = data.readBits(3);
                    if (peek != 0) {
                        endOfPixelCodeString2 = endOfPixelCodeString;
                        runLength = peek + 2;
                        clutIndex = 0;
                        i2 = peek;
                    } else {
                        endOfPixelCodeString2 = true;
                        runLength = 0;
                        clutIndex = 0;
                        i2 = peek;
                    }
                }
            } else {
                endOfPixelCodeString2 = endOfPixelCodeString;
                runLength = 1;
                clutIndex = peek;
                i2 = peek;
            }
            if (runLength != 0 && paint2 != null) {
                paint2.setColor(clutEntries[clutMapTable != null ? clutMapTable[clutIndex] : clutIndex]);
                canvas.drawRect((float) column2, (float) i, (float) (column2 + runLength), (float) (i + 1), paint);
            }
            column2 += runLength;
            if (endOfPixelCodeString2) {
                return column2;
            }
            endOfPixelCodeString = endOfPixelCodeString2;
        }
    }

    private static int paint8BitPixelCodeString(ParsableBitArray data, int[] clutEntries, byte[] clutMapTable, int column, int line, Paint paint, Canvas canvas) {
        ParsableBitArray parsableBitArray = data;
        int i = line;
        Paint paint2 = paint;
        boolean endOfPixelCodeString = false;
        int column2 = column;
        while (true) {
            boolean endOfPixelCodeString2;
            int runLength;
            int clutIndex;
            int peek = data.readBits(8);
            int i2;
            if (peek != 0) {
                endOfPixelCodeString2 = endOfPixelCodeString;
                runLength = 1;
                clutIndex = peek;
                i2 = peek;
            } else if (data.readBit()) {
                endOfPixelCodeString2 = endOfPixelCodeString;
                runLength = data.readBits(7);
                clutIndex = data.readBits(8);
            } else {
                peek = data.readBits(7);
                if (peek != 0) {
                    endOfPixelCodeString2 = endOfPixelCodeString;
                    runLength = peek;
                    clutIndex = 0;
                    i2 = peek;
                } else {
                    endOfPixelCodeString2 = true;
                    runLength = 0;
                    clutIndex = 0;
                    i2 = peek;
                }
            }
            if (runLength != 0 && paint2 != null) {
                paint2.setColor(clutEntries[clutMapTable != null ? clutMapTable[clutIndex] : clutIndex]);
                canvas.drawRect((float) column2, (float) i, (float) (column2 + runLength), (float) (i + 1), paint);
            }
            column2 += runLength;
            if (endOfPixelCodeString2) {
                return column2;
            }
            endOfPixelCodeString = endOfPixelCodeString2;
        }
    }

    private static byte[] buildClutMapTable(int length, int bitsPerEntry, ParsableBitArray data) {
        byte[] clutMapTable = new byte[length];
        for (int i = 0; i < length; i++) {
            clutMapTable[i] = (byte) data.readBits(bitsPerEntry);
        }
        return clutMapTable;
    }
}

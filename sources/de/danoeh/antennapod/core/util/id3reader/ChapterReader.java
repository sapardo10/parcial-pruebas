package de.danoeh.antennapod.core.util.id3reader;

import android.util.Log;
import com.google.android.exoplayer2.metadata.id3.ApicFrame;
import de.danoeh.antennapod.core.feed.Chapter;
import de.danoeh.antennapod.core.feed.ID3Chapter;
import de.danoeh.antennapod.core.util.id3reader.model.FrameHeader;
import de.danoeh.antennapod.core.util.id3reader.model.TagHeader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class ChapterReader extends ID3Reader {
    private static final String FRAME_ID_CHAPTER = "CHAP";
    private static final String FRAME_ID_LINK = "WXXX";
    private static final String FRAME_ID_TITLE = "TIT2";
    private static final String TAG = "ID3ChapterReader";
    private List<Chapter> chapters;
    private ID3Chapter currentChapter;

    public int onStartTagHeader(TagHeader header) {
        this.chapters = new ArrayList();
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("header: ");
        stringBuilder.append(header);
        Log.d(str, stringBuilder.toString());
        return 2;
    }

    public int onStartFrameHeader(FrameHeader header, InputStream input) throws IOException, ID3ReaderException {
        Object obj;
        ID3Chapter iD3Chapter;
        StringBuilder stringBuilder;
        StringBuilder elementId;
        char[] startTimeSource;
        int descriptionLength;
        String str;
        StringBuilder stringBuilder2;
        String str2 = TAG;
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append("header: ");
        stringBuilder3.append(header);
        Log.d(str2, stringBuilder3.toString());
        str2 = header.getId();
        int hashCode = str2.hashCode();
        if (hashCode != 2015625) {
            if (hashCode != 2067284) {
                if (hashCode != 2575251) {
                    if (hashCode == 2679201 && str2.equals(FRAME_ID_LINK)) {
                        obj = 2;
                        switch (obj) {
                            case null:
                                iD3Chapter = this.currentChapter;
                                if (iD3Chapter != null) {
                                    if (!hasId3Chapter(iD3Chapter)) {
                                        this.chapters.add(this.currentChapter);
                                        str2 = TAG;
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("Found chapter: ");
                                        stringBuilder.append(this.currentChapter);
                                        Log.d(str2, stringBuilder.toString());
                                        this.currentChapter = null;
                                    }
                                }
                                elementId = new StringBuilder();
                                readISOString(elementId, input, Integer.MAX_VALUE);
                                startTimeSource = readBytes(input, 4);
                                this.currentChapter = new ID3Chapter(elementId.toString(), (long) ((((startTimeSource[0] << 24) | (startTimeSource[1] << 16)) | (startTimeSource[2] << 8)) | startTimeSource[3]));
                                skipBytes(input, 12);
                                return 2;
                            case 1:
                                iD3Chapter = this.currentChapter;
                                if (iD3Chapter != null || iD3Chapter.getTitle() != null) {
                                    break;
                                }
                                elementId = new StringBuilder();
                                readString(elementId, input, header.getSize());
                                this.currentChapter.setTitle(elementId.toString());
                                String str3 = TAG;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("Found title: ");
                                stringBuilder.append(this.currentChapter.getTitle());
                                Log.d(str3, stringBuilder.toString());
                                return 2;
                            case 2:
                                if (this.currentChapter == null) {
                                    break;
                                }
                                descriptionLength = readString(null, input, header.getSize());
                                stringBuilder3 = new StringBuilder();
                                readISOString(stringBuilder3, input, header.getSize() - descriptionLength);
                                this.currentChapter.setLink(URLDecoder.decode(stringBuilder3.toString(), "UTF-8"));
                                str = TAG;
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("Found link: ");
                                stringBuilder2.append(this.currentChapter.getLink());
                                Log.d(str, stringBuilder2.toString());
                                return 2;
                            case 3:
                                Log.d(TAG, header.toString());
                                break;
                            default:
                                break;
                        }
                        return super.onStartFrameHeader(header, input);
                    }
                } else if (str2.equals(FRAME_ID_TITLE)) {
                    obj = 1;
                    switch (obj) {
                        case null:
                            iD3Chapter = this.currentChapter;
                            if (iD3Chapter != null) {
                                if (!hasId3Chapter(iD3Chapter)) {
                                    this.chapters.add(this.currentChapter);
                                    str2 = TAG;
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("Found chapter: ");
                                    stringBuilder.append(this.currentChapter);
                                    Log.d(str2, stringBuilder.toString());
                                    this.currentChapter = null;
                                }
                            }
                            elementId = new StringBuilder();
                            readISOString(elementId, input, Integer.MAX_VALUE);
                            startTimeSource = readBytes(input, 4);
                            this.currentChapter = new ID3Chapter(elementId.toString(), (long) ((((startTimeSource[0] << 24) | (startTimeSource[1] << 16)) | (startTimeSource[2] << 8)) | startTimeSource[3]));
                            skipBytes(input, 12);
                            return 2;
                        case 1:
                            iD3Chapter = this.currentChapter;
                            if (iD3Chapter != null) {
                                break;
                            }
                            break;
                        case 2:
                            if (this.currentChapter == null) {
                                break;
                            }
                            descriptionLength = readString(null, input, header.getSize());
                            stringBuilder3 = new StringBuilder();
                            readISOString(stringBuilder3, input, header.getSize() - descriptionLength);
                            this.currentChapter.setLink(URLDecoder.decode(stringBuilder3.toString(), "UTF-8"));
                            str = TAG;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Found link: ");
                            stringBuilder2.append(this.currentChapter.getLink());
                            Log.d(str, stringBuilder2.toString());
                            return 2;
                        case 3:
                            Log.d(TAG, header.toString());
                            break;
                        default:
                            break;
                    }
                    return super.onStartFrameHeader(header, input);
                }
            } else if (str2.equals("CHAP")) {
                obj = null;
                switch (obj) {
                    case null:
                        iD3Chapter = this.currentChapter;
                        if (iD3Chapter != null) {
                            if (!hasId3Chapter(iD3Chapter)) {
                                this.chapters.add(this.currentChapter);
                                str2 = TAG;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("Found chapter: ");
                                stringBuilder.append(this.currentChapter);
                                Log.d(str2, stringBuilder.toString());
                                this.currentChapter = null;
                            }
                        }
                        elementId = new StringBuilder();
                        readISOString(elementId, input, Integer.MAX_VALUE);
                        startTimeSource = readBytes(input, 4);
                        this.currentChapter = new ID3Chapter(elementId.toString(), (long) ((((startTimeSource[0] << 24) | (startTimeSource[1] << 16)) | (startTimeSource[2] << 8)) | startTimeSource[3]));
                        skipBytes(input, 12);
                        return 2;
                    case 1:
                        iD3Chapter = this.currentChapter;
                        if (iD3Chapter != null) {
                            break;
                        }
                        break;
                    case 2:
                        if (this.currentChapter == null) {
                            break;
                        }
                        descriptionLength = readString(null, input, header.getSize());
                        stringBuilder3 = new StringBuilder();
                        readISOString(stringBuilder3, input, header.getSize() - descriptionLength);
                        this.currentChapter.setLink(URLDecoder.decode(stringBuilder3.toString(), "UTF-8"));
                        str = TAG;
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Found link: ");
                        stringBuilder2.append(this.currentChapter.getLink());
                        Log.d(str, stringBuilder2.toString());
                        return 2;
                    case 3:
                        Log.d(TAG, header.toString());
                        break;
                    default:
                        break;
                }
                return super.onStartFrameHeader(header, input);
            }
        } else if (str2.equals(ApicFrame.ID)) {
            obj = 3;
            switch (obj) {
                case null:
                    iD3Chapter = this.currentChapter;
                    if (iD3Chapter != null) {
                        if (!hasId3Chapter(iD3Chapter)) {
                            this.chapters.add(this.currentChapter);
                            str2 = TAG;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Found chapter: ");
                            stringBuilder.append(this.currentChapter);
                            Log.d(str2, stringBuilder.toString());
                            this.currentChapter = null;
                        }
                    }
                    elementId = new StringBuilder();
                    readISOString(elementId, input, Integer.MAX_VALUE);
                    startTimeSource = readBytes(input, 4);
                    this.currentChapter = new ID3Chapter(elementId.toString(), (long) ((((startTimeSource[0] << 24) | (startTimeSource[1] << 16)) | (startTimeSource[2] << 8)) | startTimeSource[3]));
                    skipBytes(input, 12);
                    return 2;
                case 1:
                    iD3Chapter = this.currentChapter;
                    if (iD3Chapter != null) {
                        break;
                    }
                    break;
                case 2:
                    if (this.currentChapter == null) {
                        break;
                    }
                    descriptionLength = readString(null, input, header.getSize());
                    stringBuilder3 = new StringBuilder();
                    readISOString(stringBuilder3, input, header.getSize() - descriptionLength);
                    this.currentChapter.setLink(URLDecoder.decode(stringBuilder3.toString(), "UTF-8"));
                    str = TAG;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Found link: ");
                    stringBuilder2.append(this.currentChapter.getLink());
                    Log.d(str, stringBuilder2.toString());
                    return 2;
                case 3:
                    Log.d(TAG, header.toString());
                    break;
                default:
                    break;
            }
            return super.onStartFrameHeader(header, input);
        }
        obj = -1;
        switch (obj) {
            case null:
                iD3Chapter = this.currentChapter;
                if (iD3Chapter != null) {
                    if (!hasId3Chapter(iD3Chapter)) {
                        this.chapters.add(this.currentChapter);
                        str2 = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Found chapter: ");
                        stringBuilder.append(this.currentChapter);
                        Log.d(str2, stringBuilder.toString());
                        this.currentChapter = null;
                    }
                }
                elementId = new StringBuilder();
                readISOString(elementId, input, Integer.MAX_VALUE);
                startTimeSource = readBytes(input, 4);
                this.currentChapter = new ID3Chapter(elementId.toString(), (long) ((((startTimeSource[0] << 24) | (startTimeSource[1] << 16)) | (startTimeSource[2] << 8)) | startTimeSource[3]));
                skipBytes(input, 12);
                return 2;
            case 1:
                iD3Chapter = this.currentChapter;
                if (iD3Chapter != null) {
                    break;
                }
                break;
            case 2:
                if (this.currentChapter == null) {
                    break;
                }
                descriptionLength = readString(null, input, header.getSize());
                stringBuilder3 = new StringBuilder();
                readISOString(stringBuilder3, input, header.getSize() - descriptionLength);
                this.currentChapter.setLink(URLDecoder.decode(stringBuilder3.toString(), "UTF-8"));
                str = TAG;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Found link: ");
                stringBuilder2.append(this.currentChapter.getLink());
                Log.d(str, stringBuilder2.toString());
                return 2;
            case 3:
                Log.d(TAG, header.toString());
                break;
            default:
                break;
        }
        return super.onStartFrameHeader(header, input);
    }

    private boolean hasId3Chapter(ID3Chapter chapter) {
        for (Chapter c : this.chapters) {
            if (((ID3Chapter) c).getId3ID().equals(chapter.getId3ID())) {
                return true;
            }
        }
        return false;
    }

    public void onEndTag() {
        ID3Chapter iD3Chapter = this.currentChapter;
        if (iD3Chapter != null) {
            if (!hasId3Chapter(iD3Chapter)) {
                this.chapters.add(this.currentChapter);
            }
        }
        Log.d(TAG, "Reached end of tag");
        List<Chapter> list = this.chapters;
        if (list != null) {
            for (Chapter c : list) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("chapter: ");
                stringBuilder.append(c);
                Log.d(str, stringBuilder.toString());
            }
        }
    }

    public void onNoTagHeaderFound() {
        Log.d(TAG, "No tag header found");
        super.onNoTagHeaderFound();
    }

    public List<Chapter> getChapters() {
        return this.chapters;
    }
}

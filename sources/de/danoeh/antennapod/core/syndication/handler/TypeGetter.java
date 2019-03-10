package de.danoeh.antennapod.core.syndication.handler;

import de.danoeh.antennapod.core.feed.Feed;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import org.apache.commons.io.input.XmlStreamReader;

public class TypeGetter {
    private static final String ATOM_ROOT = "feed";
    private static final String RSS_ROOT = "rss";
    private static final String TAG = "TypeGetter";

    public enum Type {
        RSS20,
        RSS091,
        ATOM,
        INVALID
    }

    public de.danoeh.antennapod.core.syndication.handler.TypeGetter.Type getType(de.danoeh.antennapod.core.feed.Feed r11) throws de.danoeh.antennapod.core.syndication.handler.UnsupportedFeedtypeException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:125:0x0168 in {15, 18, 19, 28, 30, 31, 32, 40, 42, 43, 44, 50, 52, 57, 59, 60, 61, 65, 66, 71, 73, 74, 75, 78, 80, 85, 93, 94, 96, 97, 106, 107, 108, 111, 113, 117, 119, 120, 121, 122, 124} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r10 = this;
        r0 = r11.getFile_url();
        if (r0 == 0) goto L_0x0158;
    L_0x0006:
        r0 = 0;
        r1 = 0;
        r2 = org.xmlpull.v1.XmlPullParserFactory.newInstance();	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r3 = 1;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r2.setNamespaceAware(r3);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r4 = r2.newPullParser();	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r5 = r10.createReader(r11);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r0 = r5;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r4.setInput(r0);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r5 = r4.getEventType();	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x0020:
        if (r5 == r3) goto L_0x010c;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x0022:
        r6 = 2;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        if (r5 != r6) goto L_0x0105;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x0025:
        r6 = r4.getName();	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r7 = -1;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r8 = r6.hashCode();	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r9 = 113234; // 0x1ba52 float:1.58675E-40 double:5.5945E-319;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        if (r8 == r9) goto L_0x0043;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x0033:
        r3 = 3138974; // 0x2fe59e float:4.39864E-39 double:1.550859E-317;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        if (r8 == r3) goto L_0x0039;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x0038:
        goto L_0x004d;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x0039:
        r3 = "feed";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r3 = r6.equals(r3);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        if (r3 == 0) goto L_0x0038;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x0041:
        r3 = 0;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        goto L_0x004e;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x0043:
        r8 = "rss";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r8 = r6.equals(r8);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        if (r8 == 0) goto L_0x0038;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x004c:
        goto L_0x004e;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x004d:
        r3 = -1;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x004e:
        switch(r3) {
            case 0: goto L_0x00ce;
            case 1: goto L_0x0055;
            default: goto L_0x0051;
        };	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x0051:
        r3 = "TypeGetter";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        goto L_0x00f8;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x0055:
        r3 = "version";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r3 = r4.getAttributeValue(r1, r3);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        if (r3 != 0) goto L_0x007a;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x005e:
        r7 = "rss";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r11.setType(r7);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r7 = "TypeGetter";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r8 = "Assuming type RSS 2.0";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        android.util.Log.d(r7, r8);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r1 = de.danoeh.antennapod.core.syndication.handler.TypeGetter.Type.RSS20;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        if (r0 == 0) goto L_0x0078;
    L_0x006f:
        r0.close();	 Catch:{ IOException -> 0x0073 }
        goto L_0x0079;
    L_0x0073:
        r7 = move-exception;
        r7.printStackTrace();
        goto L_0x0079;
    L_0x0079:
        return r1;
    L_0x007a:
        r7 = "2.0";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r7 = r3.equals(r7);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        if (r7 == 0) goto L_0x009e;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x0082:
        r7 = "rss";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r11.setType(r7);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r7 = "TypeGetter";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r8 = "Recognized type RSS 2.0";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        android.util.Log.d(r7, r8);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r1 = de.danoeh.antennapod.core.syndication.handler.TypeGetter.Type.RSS20;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        if (r0 == 0) goto L_0x009c;
    L_0x0093:
        r0.close();	 Catch:{ IOException -> 0x0097 }
        goto L_0x009d;
    L_0x0097:
        r7 = move-exception;
        r7.printStackTrace();
        goto L_0x009d;
    L_0x009d:
        return r1;
    L_0x009e:
        r7 = "0.91";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r7 = r3.equals(r7);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        if (r7 != 0) goto L_0x00b7;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x00a6:
        r7 = "0.92";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r7 = r3.equals(r7);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        if (r7 == 0) goto L_0x00af;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x00ae:
        goto L_0x00b7;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x00af:
        r7 = new de.danoeh.antennapod.core.syndication.handler.UnsupportedFeedtypeException;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r8 = "Unsupported rss version";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r7.<init>(r8);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        throw r7;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r7 = "TypeGetter";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r8 = "Recognized type RSS 0.91/0.92";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        android.util.Log.d(r7, r8);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r1 = de.danoeh.antennapod.core.syndication.handler.TypeGetter.Type.RSS091;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        if (r0 == 0) goto L_0x00cc;
    L_0x00c3:
        r0.close();	 Catch:{ IOException -> 0x00c7 }
        goto L_0x00cd;
    L_0x00c7:
        r7 = move-exception;
        r7.printStackTrace();
        goto L_0x00cd;
    L_0x00cd:
        return r1;
    L_0x00ce:
        r3 = "atom";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r11.setType(r3);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r3 = "TypeGetter";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r7 = "Recognized type Atom";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        android.util.Log.d(r3, r7);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r3 = "http://www.w3.org/XML/1998/namespace";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r7 = "lang";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r3 = r4.getAttributeValue(r3, r7);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        if (r3 == 0) goto L_0x00e8;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x00e4:
        r11.setLanguage(r3);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        goto L_0x00e9;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x00e9:
        r1 = de.danoeh.antennapod.core.syndication.handler.TypeGetter.Type.ATOM;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        if (r0 == 0) goto L_0x00f6;
    L_0x00ed:
        r0.close();	 Catch:{ IOException -> 0x00f1 }
        goto L_0x00f7;
    L_0x00f1:
        r7 = move-exception;
        r7.printStackTrace();
        goto L_0x00f7;
    L_0x00f7:
        return r1;
    L_0x00f8:
        r7 = "Type is invalid";	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        android.util.Log.d(r3, r7);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r3 = new de.danoeh.antennapod.core.syndication.handler.UnsupportedFeedtypeException;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r7 = de.danoeh.antennapod.core.syndication.handler.TypeGetter.Type.INVALID;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r3.<init>(r7, r6);	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        throw r3;	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
    L_0x0105:
        r6 = r4.next();	 Catch:{ XmlPullParserException -> 0x0125, IOException -> 0x0115 }
        r5 = r6;
        goto L_0x0020;
        if (r0 == 0) goto L_0x0124;
    L_0x010f:
        r0.close();	 Catch:{ IOException -> 0x011f }
        goto L_0x011e;
    L_0x0113:
        r1 = move-exception;
        goto L_0x014b;
    L_0x0115:
        r1 = move-exception;
        r1.printStackTrace();	 Catch:{ all -> 0x0113 }
        if (r0 == 0) goto L_0x0124;
    L_0x011b:
        r0.close();	 Catch:{ IOException -> 0x011f }
    L_0x011e:
        goto L_0x0159;
    L_0x011f:
        r1 = move-exception;
        r1.printStackTrace();
        goto L_0x011e;
    L_0x0124:
        goto L_0x0159;
    L_0x0125:
        r2 = move-exception;
        r2.printStackTrace();	 Catch:{ all -> 0x0113 }
        r3 = 0;
        r4 = new java.io.File;	 Catch:{ IOException -> 0x013f }
        r5 = r11.getFile_url();	 Catch:{ IOException -> 0x013f }
        r4.<init>(r5);	 Catch:{ IOException -> 0x013f }
        r1 = org.jsoup.Jsoup.parse(r4, r1);	 Catch:{ IOException -> 0x013f }
        if (r1 == 0) goto L_0x013d;	 Catch:{ IOException -> 0x013f }
    L_0x0139:
        r1 = "html";	 Catch:{ IOException -> 0x013f }
        r3 = r1;
        goto L_0x013e;
    L_0x013e:
        goto L_0x0143;
    L_0x013f:
        r1 = move-exception;
        r1.printStackTrace();	 Catch:{ all -> 0x0113 }
    L_0x0143:
        r1 = new de.danoeh.antennapod.core.syndication.handler.UnsupportedFeedtypeException;	 Catch:{ all -> 0x0113 }
        r4 = de.danoeh.antennapod.core.syndication.handler.TypeGetter.Type.INVALID;	 Catch:{ all -> 0x0113 }
        r1.<init>(r4, r3);	 Catch:{ all -> 0x0113 }
        throw r1;	 Catch:{ all -> 0x0113 }
    L_0x014b:
        if (r0 == 0) goto L_0x0156;
    L_0x014d:
        r0.close();	 Catch:{ IOException -> 0x0151 }
        goto L_0x0157;
    L_0x0151:
        r2 = move-exception;
        r2.printStackTrace();
        goto L_0x0157;
    L_0x0157:
        throw r1;
    L_0x0159:
        r0 = "TypeGetter";
        r1 = "Type is invalid";
        android.util.Log.d(r0, r1);
        r0 = new de.danoeh.antennapod.core.syndication.handler.UnsupportedFeedtypeException;
        r1 = de.danoeh.antennapod.core.syndication.handler.TypeGetter.Type.INVALID;
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.syndication.handler.TypeGetter.getType(de.danoeh.antennapod.core.feed.Feed):de.danoeh.antennapod.core.syndication.handler.TypeGetter$Type");
    }

    private Reader createReader(Feed feed) {
        try {
            return new XmlStreamReader(new File(feed.getFile_url()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }
}

package de.danoeh.antennapod.core.gpoddernet;

import android.support.annotation.NonNull;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetDevice;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetDevice.DeviceType;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeActionGetResponse;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetPodcast;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetSubscriptionChange;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetTag;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetUploadChangesResponse;
import de.danoeh.antennapod.core.preferences.GpodnetPreferences;
import de.danoeh.antennapod.core.service.download.AntennapodHttpClient;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GpodnetService {
    private static final String BASE_SCHEME = "https";
    public static final String DEFAULT_BASE_HOST = "gpodder.net";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "GpodnetService";
    private static final MediaType TEXT = MediaType.parse("plain/text; charset=utf-8");
    private final String BASE_HOST = GpodnetPreferences.getHostname();
    private final OkHttpClient httpClient = AntennapodHttpClient.getHttpClient();

    /* renamed from: de.danoeh.antennapod.core.gpoddernet.GpodnetService$1 */
    class C07401 extends Thread {
        C07401() {
        }

        public void run() {
            AntennapodHttpClient.cleanup();
        }
    }

    private java.lang.String getStringFromResponseBody(@android.support.annotation.NonNull okhttp3.ResponseBody r7) throws de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x0037 in {2, 3, 10, 12, 15} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r6 = this;
        r0 = r7.contentLength();
        r0 = (int) r0;
        if (r0 <= 0) goto L_0x000d;
    L_0x0007:
        r1 = new java.io.ByteArrayOutputStream;
        r1.<init>(r0);
        goto L_0x0012;
    L_0x000d:
        r1 = new java.io.ByteArrayOutputStream;
        r1.<init>();
    L_0x0012:
        r2 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r2 = new byte[r2];	 Catch:{ IOException -> 0x002d }
        r3 = r7.byteStream();	 Catch:{ IOException -> 0x002d }
    L_0x001a:
        r4 = r3.read(r2);	 Catch:{ IOException -> 0x002d }
        r5 = r4;	 Catch:{ IOException -> 0x002d }
        if (r4 <= 0) goto L_0x0026;	 Catch:{ IOException -> 0x002d }
    L_0x0021:
        r4 = 0;	 Catch:{ IOException -> 0x002d }
        r1.write(r2, r4, r5);	 Catch:{ IOException -> 0x002d }
        goto L_0x001a;
        r2 = r1.toString();
        return r2;
    L_0x002d:
        r2 = move-exception;
        r2.printStackTrace();
        r3 = new de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException;
        r3.<init>(r2);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.gpoddernet.GpodnetService.getStringFromResponseBody(okhttp3.ResponseBody):java.lang.String");
    }

    public java.util.List<de.danoeh.antennapod.core.gpoddernet.model.GpodnetTag> getTopTags(int r12) throws de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x0094 in {8, 10, 13, 16} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r11 = this;
        r0 = new java.net.URI;	 Catch:{ MalformedURLException -> 0x0087, MalformedURLException -> 0x0087 }
        r1 = "https";	 Catch:{ MalformedURLException -> 0x0087, MalformedURLException -> 0x0087 }
        r2 = r11.BASE_HOST;	 Catch:{ MalformedURLException -> 0x0087, MalformedURLException -> 0x0087 }
        r3 = "/api/2/tags/%d.json";	 Catch:{ MalformedURLException -> 0x0087, MalformedURLException -> 0x0087 }
        r4 = 1;	 Catch:{ MalformedURLException -> 0x0087, MalformedURLException -> 0x0087 }
        r4 = new java.lang.Object[r4];	 Catch:{ MalformedURLException -> 0x0087, MalformedURLException -> 0x0087 }
        r5 = java.lang.Integer.valueOf(r12);	 Catch:{ MalformedURLException -> 0x0087, MalformedURLException -> 0x0087 }
        r6 = 0;	 Catch:{ MalformedURLException -> 0x0087, MalformedURLException -> 0x0087 }
        r4[r6] = r5;	 Catch:{ MalformedURLException -> 0x0087, MalformedURLException -> 0x0087 }
        r3 = java.lang.String.format(r3, r4);	 Catch:{ MalformedURLException -> 0x0087, MalformedURLException -> 0x0087 }
        r4 = 0;	 Catch:{ MalformedURLException -> 0x0087, MalformedURLException -> 0x0087 }
        r0.<init>(r1, r2, r3, r4);	 Catch:{ MalformedURLException -> 0x0087, MalformedURLException -> 0x0087 }
        r0 = r0.toURL();	 Catch:{ MalformedURLException -> 0x0087, MalformedURLException -> 0x0087 }
        r1 = new okhttp3.Request$Builder;
        r1.<init>();
        r1 = r1.url(r0);
        r2 = r11.executeRequest(r1);
        r3 = new org.json.JSONArray;	 Catch:{ JSONException -> 0x007a }
        r3.<init>(r2);	 Catch:{ JSONException -> 0x007a }
        r4 = new java.util.ArrayList;	 Catch:{ JSONException -> 0x007a }
        r5 = r3.length();	 Catch:{ JSONException -> 0x007a }
        r4.<init>(r5);	 Catch:{ JSONException -> 0x007a }
        r5 = r6;	 Catch:{ JSONException -> 0x007a }
        r6 = r3.length();	 Catch:{ JSONException -> 0x007a }
        if (r5 >= r6) goto L_0x0077;	 Catch:{ JSONException -> 0x007a }
        r6 = r3.getJSONObject(r5);	 Catch:{ JSONException -> 0x007a }
        r7 = "title";	 Catch:{ JSONException -> 0x007a }
        r7 = r6.getString(r7);	 Catch:{ JSONException -> 0x007a }
        r8 = "tag";	 Catch:{ JSONException -> 0x007a }
        r8 = r6.getString(r8);	 Catch:{ JSONException -> 0x007a }
        r9 = "usage";	 Catch:{ JSONException -> 0x007a }
        r9 = r6.getInt(r9);	 Catch:{ JSONException -> 0x007a }
        r10 = new de.danoeh.antennapod.core.gpoddernet.model.GpodnetTag;	 Catch:{ JSONException -> 0x007a }
        r10.<init>(r7, r8, r9);	 Catch:{ JSONException -> 0x007a }
        r4.add(r10);	 Catch:{ JSONException -> 0x007a }
        r5 = r5 + 1;
        goto L_0x003c;
        return r4;
    L_0x007a:
        r3 = move-exception;
        r3.printStackTrace();
        r4 = new de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException;
        r4.<init>(r3);
        throw r4;
    L_0x0087:
        r0 = move-exception;
        r0.printStackTrace();
        r1 = new de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException;
        r1.<init>(r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.gpoddernet.GpodnetService.getTopTags(int):java.util.List<de.danoeh.antennapod.core.gpoddernet.model.GpodnetTag>");
    }

    public de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeActionPostResponse uploadEpisodeActions(@android.support.annotation.NonNull java.util.Collection<de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction> r8) throws de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0069 in {7, 8, 9, 11, 14} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r7 = this;
        r0 = de.danoeh.antennapod.core.preferences.GpodnetPreferences.getUsername();
        r1 = new java.net.URI;	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r2 = "https";	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r3 = r7.BASE_HOST;	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r4 = "/api/2/episodes/%s.json";	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r5 = 1;	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r5 = new java.lang.Object[r5];	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r6 = 0;	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r5[r6] = r0;	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r4 = java.lang.String.format(r4, r5);	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r5 = 0;	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r1.<init>(r2, r3, r4, r5);	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r1 = r1.toURL();	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r2 = new org.json.JSONArray;	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r2.<init>();	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r3 = r8.iterator();	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
    L_0x0027:
        r4 = r3.hasNext();	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        if (r4 == 0) goto L_0x003f;	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
    L_0x002d:
        r4 = r3.next();	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r4 = (de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeAction) r4;	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r5 = r4.writeToJSONObject();	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        if (r5 == 0) goto L_0x003d;	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
    L_0x0039:
        r2.put(r5);	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        goto L_0x003e;	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
    L_0x003e:
        goto L_0x0027;	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
    L_0x003f:
        r3 = JSON;	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r4 = r2.toString();	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r3 = okhttp3.RequestBody.create(r3, r4);	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r4 = new okhttp3.Request$Builder;	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r4.<init>();	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r4 = r4.post(r3);	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r4 = r4.url(r1);	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r5 = r7.executeRequest(r4);	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        r6 = de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeActionPostResponse.fromJSONObject(r5);	 Catch:{ JSONException -> 0x005f, JSONException -> 0x005f, JSONException -> 0x005f }
        return r6;
    L_0x005f:
        r1 = move-exception;
        r1.printStackTrace();
        r2 = new de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException;
        r2.<init>(r1);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.gpoddernet.GpodnetService.uploadEpisodeActions(java.util.Collection):de.danoeh.antennapod.core.gpoddernet.model.GpodnetEpisodeActionPostResponse");
    }

    public void uploadSubscriptions(@android.support.annotation.NonNull java.lang.String r7, @android.support.annotation.NonNull java.lang.String r8, @android.support.annotation.NonNull java.util.List<java.lang.String> r9) throws de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0061 in {4, 7, 10} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r6 = this;
        r0 = new java.net.URI;	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r1 = "https";	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r2 = r6.BASE_HOST;	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r3 = "/subscriptions/%s/%s.txt";	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r4 = 2;	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r4 = new java.lang.Object[r4];	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r5 = 0;	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r4[r5] = r7;	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r5 = 1;	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r4[r5] = r8;	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r3 = java.lang.String.format(r3, r4);	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r4 = 0;	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r0.<init>(r1, r2, r3, r4);	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r0 = r0.toURL();	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r1 = new java.lang.StringBuilder;	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r1.<init>();	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r2 = r9.iterator();	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
    L_0x0026:
        r3 = r2.hasNext();	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        if (r3 == 0) goto L_0x003b;	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
    L_0x002c:
        r3 = r2.next();	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r3 = (java.lang.String) r3;	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r1.append(r3);	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r4 = "\n";	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r1.append(r4);	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        goto L_0x0026;	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
    L_0x003b:
        r2 = TEXT;	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r3 = r1.toString();	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r2 = okhttp3.RequestBody.create(r2, r3);	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r3 = new okhttp3.Request$Builder;	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r3.<init>();	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r3 = r3.put(r2);	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r3 = r3.url(r0);	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        r6.executeRequest(r3);	 Catch:{ MalformedURLException -> 0x0057, MalformedURLException -> 0x0057 }
        return;
    L_0x0057:
        r0 = move-exception;
        r0.printStackTrace();
        r1 = new de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException;
        r1.<init>(r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.gpoddernet.GpodnetService.uploadSubscriptions(java.lang.String, java.lang.String, java.util.List):void");
    }

    public List<GpodnetPodcast> getPodcastsForTag(@NonNull GpodnetTag tag, int count) throws GpodnetServiceException {
        try {
            return readPodcastListFromJSONArray(new JSONArray(executeRequest(new Builder().url(new URI(BASE_SCHEME, this.BASE_HOST, String.format("/api/2/tag/%s/%d.json", new Object[]{tag.getTag(), Integer.valueOf(count)}), null).toURL()))));
        } catch (Throwable e) {
            e.printStackTrace();
            throw new GpodnetServiceException(e);
        }
    }

    public List<GpodnetPodcast> getPodcastToplist(int count) throws GpodnetServiceException {
        if (count < 1 || count > 100) {
            throw new IllegalArgumentException("Count must be in range 1..100");
        }
        try {
            return readPodcastListFromJSONArray(new JSONArray(executeRequest(new Builder().url(new URI(BASE_SCHEME, this.BASE_HOST, String.format("/toplist/%d.json", new Object[]{Integer.valueOf(count)}), null).toURL()))));
        } catch (Throwable e) {
            e.printStackTrace();
            throw new GpodnetServiceException(e);
        }
    }

    public List<GpodnetPodcast> getSuggestions(int count) throws GpodnetServiceException {
        if (count < 1 || count > 100) {
            throw new IllegalArgumentException("Count must be in range 1..100");
        }
        try {
            return readPodcastListFromJSONArray(new JSONArray(executeRequest(new Builder().url(new URI(BASE_SCHEME, this.BASE_HOST, String.format("/suggestions/%d.json", new Object[]{Integer.valueOf(count)}), null).toURL()))));
        } catch (Throwable e) {
            e.printStackTrace();
            throw new GpodnetServiceException(e);
        }
    }

    public List<GpodnetPodcast> searchPodcasts(String query, int scaledLogoSize) throws GpodnetServiceException {
        String parameters;
        if (scaledLogoSize <= 0 || scaledLogoSize > 256) {
            parameters = String.format("q=%s", new Object[]{query});
        } else {
            parameters = String.format("q=%s&scale_logo=%d", new Object[]{query, Integer.valueOf(scaledLogoSize)});
        }
        try {
            return readPodcastListFromJSONArray(new JSONArray(executeRequest(new Builder().url(new URI(BASE_SCHEME, null, this.BASE_HOST, -1, "/search.json", parameters, null).toURL()))));
        } catch (Throwable e) {
            e.printStackTrace();
            throw new GpodnetServiceException(e);
        } catch (URISyntaxException e2) {
            e2.printStackTrace();
            throw new IllegalStateException(e2);
        }
    }

    public List<GpodnetDevice> getDevices(@NonNull String username) throws GpodnetServiceException {
        try {
            return readDeviceListFromJSONArray(new JSONArray(executeRequest(new Builder().url(new URI(BASE_SCHEME, this.BASE_HOST, String.format("/api/2/devices/%s.json", new Object[]{username}), null).toURL()))));
        } catch (Throwable e) {
            e.printStackTrace();
            throw new GpodnetServiceException(e);
        }
    }

    public void configureDevice(@NonNull String username, @NonNull String deviceId, String caption, DeviceType type) throws GpodnetServiceException {
        try {
            String content;
            URL url = new URI(BASE_SCHEME, this.BASE_HOST, String.format("/api/2/devices/%s/%s.json", new Object[]{username, deviceId}), null).toURL();
            if (caption == null) {
                if (type == null) {
                    content = "";
                    executeRequest(new Builder().post(RequestBody.create(JSON, content)).url(url));
                }
            }
            JSONObject jsonContent = new JSONObject();
            if (caption != null) {
                jsonContent.put("caption", caption);
            }
            if (type != null) {
                jsonContent.put("type", type.toString());
            }
            content = jsonContent.toString();
            executeRequest(new Builder().post(RequestBody.create(JSON, content)).url(url));
        } catch (Throwable e) {
            e.printStackTrace();
            throw new GpodnetServiceException(e);
        }
    }

    public String getSubscriptionsOfDevice(@NonNull String username, @NonNull String deviceId) throws GpodnetServiceException {
        try {
            return executeRequest(new Builder().url(new URI(BASE_SCHEME, this.BASE_HOST, String.format("/subscriptions/%s/%s.opml", new Object[]{username, deviceId}), null).toURL()));
        } catch (Throwable e) {
            e.printStackTrace();
            throw new GpodnetServiceException(e);
        }
    }

    public String getSubscriptionsOfUser(@NonNull String username) throws GpodnetServiceException {
        try {
            return executeRequest(new Builder().url(new URI(BASE_SCHEME, this.BASE_HOST, String.format("/subscriptions/%s.opml", new Object[]{username}), null).toURL()));
        } catch (Throwable e) {
            e.printStackTrace();
            throw new GpodnetServiceException(e);
        }
    }

    public GpodnetUploadChangesResponse uploadChanges(@NonNull String username, @NonNull String deviceId, @NonNull Collection<String> added, @NonNull Collection<String> removed) throws GpodnetServiceException {
        try {
            URL url = new URI(BASE_SCHEME, this.BASE_HOST, String.format("/api/2/subscriptions/%s/%s.json", new Object[]{username, deviceId}), null).toURL();
            JSONObject requestObject = new JSONObject();
            requestObject.put("add", new JSONArray(added));
            requestObject.put("remove", new JSONArray(removed));
            return GpodnetUploadChangesResponse.fromJSONObject(executeRequest(new Builder().post(RequestBody.create(JSON, requestObject.toString())).url(url)));
        } catch (Throwable e) {
            e.printStackTrace();
            throw new GpodnetServiceException(e);
        }
    }

    public GpodnetSubscriptionChange getSubscriptionChanges(@NonNull String username, @NonNull String deviceId, long timestamp) throws GpodnetServiceException {
        GpodnetService gpodnetService = this;
        String params = String.format("since=%d", new Object[]{Long.valueOf(timestamp)});
        try {
            return readSubscriptionChangesFromJSONObject(new JSONObject(executeRequest(new Builder().url(new URI(BASE_SCHEME, null, gpodnetService.BASE_HOST, -1, String.format("/api/2/subscriptions/%s/%s.json", new Object[]{username, deviceId}), params, null).toURL()))));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        } catch (Throwable e2) {
            e2.printStackTrace();
            throw new GpodnetServiceException(e2);
        }
    }

    public GpodnetEpisodeActionGetResponse getEpisodeChanges(long timestamp) throws GpodnetServiceException {
        GpodnetService gpodnetService = this;
        String username = GpodnetPreferences.getUsername();
        String params = String.format("since=%d", new Object[]{Long.valueOf(timestamp)});
        try {
            return readEpisodeActionsFromJSONObject(new JSONObject(executeRequest(new Builder().url(new URI(BASE_SCHEME, null, gpodnetService.BASE_HOST, -1, String.format("/api/2/episodes/%s.json", new Object[]{username}), params, null).toURL()))));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        } catch (Throwable e2) {
            e2.printStackTrace();
            throw new GpodnetServiceException(e2);
        }
    }

    public void authenticate(@NonNull String username, @NonNull String password) throws GpodnetServiceException {
        try {
            URL url = new URI(BASE_SCHEME, this.BASE_HOST, String.format("/api/2/auth/%s/login.json", new Object[]{username}), null).toURL();
            executeRequestWithAuthentication(new Builder().url(url).post(RequestBody.create(TEXT, "")), username, password);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new GpodnetServiceException(e);
        }
    }

    public void shutdown() {
        new C07401().start();
    }

    private String executeRequest(@NonNull Builder requestB) throws GpodnetServiceException {
        ResponseBody body = null;
        try {
            Response response = this.httpClient.newCall(requestB.header("User-Agent", ClientConfig.USER_AGENT).build()).execute();
            checkStatusCode(response);
            body = response.body();
            String responseString = getStringFromResponseBody(body);
            if (body != null) {
                body.close();
            }
            return responseString;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new GpodnetServiceException(e);
        } catch (Throwable th) {
            if (body != null) {
                body.close();
            }
        }
    }

    private String executeRequestWithAuthentication(Builder requestB, String username, String password) throws GpodnetServiceException {
        if (requestB == null || username == null || password == null) {
            throw new IllegalArgumentException("request and credentials must not be null");
        }
        Request request = requestB.header("User-Agent", ClientConfig.USER_AGENT).build();
        ResponseBody body = null;
        try {
            Response response = this.httpClient.newCall(request.newBuilder().header("Authorization", Credentials.basic(username, password, Charset.forName("UTF-8"))).build()).execute();
            checkStatusCode(response);
            body = response.body();
            String result = getStringFromResponseBody(body);
            if (body != null) {
                body.close();
            }
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new GpodnetServiceException(e);
        } catch (Throwable th) {
            if (body != null) {
                body.close();
            }
        }
    }

    private void checkStatusCode(@NonNull Response response) throws GpodnetServiceException {
        int responseCode = response.code();
        if (responseCode == 200) {
            return;
        }
        if (responseCode == 401) {
            throw new GpodnetServiceAuthenticationException("Wrong username or password");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Bad response code: ");
        stringBuilder.append(responseCode);
        throw new GpodnetServiceBadStatusCodeException(stringBuilder.toString(), responseCode);
    }

    private List<GpodnetPodcast> readPodcastListFromJSONArray(@NonNull JSONArray array) throws JSONException {
        List<GpodnetPodcast> result = new ArrayList(array.length());
        for (int i = 0; i < array.length(); i++) {
            result.add(readPodcastFromJSONObject(array.getJSONObject(i)));
        }
        return result;
    }

    private GpodnetPodcast readPodcastFromJSONObject(JSONObject object) throws JSONException {
        String title;
        String description;
        String logoUrl;
        String websiteObj;
        String website;
        JSONObject jSONObject = object;
        String url = jSONObject.getString("url");
        Object titleObj = jSONObject.opt("title");
        if (titleObj == null || !(titleObj instanceof String)) {
            title = url;
        } else {
            title = (String) titleObj;
        }
        Object descriptionObj = jSONObject.opt(PodDBAdapter.KEY_DESCRIPTION);
        if (descriptionObj == null || !(descriptionObj instanceof String)) {
            description = "";
        } else {
            description = (String) descriptionObj;
        }
        int subscribers = jSONObject.getInt("subscribers");
        Object logoUrlObj = jSONObject.opt("logo_url");
        String logoUrl2 = logoUrlObj instanceof String ? (String) logoUrlObj : null;
        if (logoUrl2 == null) {
            String scaledLogoUrl = jSONObject.opt("scaled_logo_url");
            if (scaledLogoUrl != null && (scaledLogoUrl instanceof String)) {
                logoUrl = scaledLogoUrl;
                websiteObj = jSONObject.opt("website");
                if (websiteObj == null && (websiteObj instanceof String)) {
                    website = websiteObj;
                } else {
                    website = null;
                }
                return new GpodnetPodcast(url, title, description, subscribers, logoUrl, website, jSONObject.getString("mygpo_link"));
            }
        }
        logoUrl = logoUrl2;
        websiteObj = jSONObject.opt("website");
        if (websiteObj == null) {
        }
        website = null;
        return new GpodnetPodcast(url, title, description, subscribers, logoUrl, website, jSONObject.getString("mygpo_link"));
    }

    private List<GpodnetDevice> readDeviceListFromJSONArray(@NonNull JSONArray array) throws JSONException {
        List<GpodnetDevice> result = new ArrayList(array.length());
        for (int i = 0; i < array.length(); i++) {
            result.add(readDeviceFromJSONObject(array.getJSONObject(i)));
        }
        return result;
    }

    private GpodnetDevice readDeviceFromJSONObject(JSONObject object) throws JSONException {
        return new GpodnetDevice(object.getString("id"), object.getString("caption"), object.getString("type"), object.getInt("subscriptions"));
    }

    private GpodnetSubscriptionChange readSubscriptionChangesFromJSONObject(@NonNull JSONObject object) throws JSONException {
        List<String> added = new LinkedList();
        JSONArray jsonAdded = object.getJSONArray("add");
        for (int i = 0; i < jsonAdded.length(); i++) {
            added.add(jsonAdded.getString(i).replace("%3A", ":"));
        }
        List<String> removed = new LinkedList();
        JSONArray jsonRemoved = object.getJSONArray("remove");
        for (int i2 = 0; i2 < jsonRemoved.length(); i2++) {
            removed.add(jsonRemoved.getString(i2).replace("%3A", ":"));
        }
        return new GpodnetSubscriptionChange(added, removed, object.getLong("timestamp"));
    }

    private GpodnetEpisodeActionGetResponse readEpisodeActionsFromJSONObject(@NonNull JSONObject object) throws JSONException {
        List<GpodnetEpisodeAction> episodeActions = new ArrayList();
        long timestamp = object.getLong("timestamp");
        JSONArray jsonActions = object.getJSONArray("actions");
        for (int i = 0; i < jsonActions.length(); i++) {
            GpodnetEpisodeAction episodeAction = GpodnetEpisodeAction.readFromJSONObject(jsonActions.getJSONObject(i));
            if (episodeAction != null) {
                episodeActions.add(episodeAction);
            }
        }
        return new GpodnetEpisodeActionGetResponse(episodeActions, timestamp);
    }
}

package com.bumptech.glide.load.resource.drawable;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;

public class ResourceDrawableDecoder implements ResourceDecoder<Uri, Drawable> {
    private static final int ID_PATH_SEGMENTS = 1;
    private static final int NAME_PATH_SEGMENT_INDEX = 1;
    private static final int NAME_URI_PATH_SEGMENTS = 2;
    private static final int RESOURCE_ID_SEGMENT_INDEX = 0;
    private static final int TYPE_PATH_SEGMENT_INDEX = 0;
    private final Context context;

    @android.support.annotation.DrawableRes
    private int loadResourceIdFromUri(android.net.Uri r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x007b in {2, 3, 8, 9, 14, 16, 18} preds:[]
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
        r6 = this;
        r0 = r7.getPathSegments();
        r1 = 0;
        r2 = r0.size();
        r3 = 0;
        r4 = 1;
        r5 = 2;
        if (r2 != r5) goto L_0x002d;
    L_0x000e:
        r2 = r7.getAuthority();
        r3 = r0.get(r3);
        r3 = (java.lang.String) r3;
        r4 = r0.get(r4);
        r4 = (java.lang.String) r4;
        r5 = r6.context;
        r5 = r5.getResources();
        r5 = r5.getIdentifier(r4, r3, r2);
        r1 = java.lang.Integer.valueOf(r5);
    L_0x002c:
        goto L_0x0040;
    L_0x002d:
        r2 = r0.size();
        if (r2 != r4) goto L_0x002c;
    L_0x0033:
        r2 = r0.get(r3);	 Catch:{ NumberFormatException -> 0x003f }
        r2 = (java.lang.String) r2;	 Catch:{ NumberFormatException -> 0x003f }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ NumberFormatException -> 0x003f }
        r1 = r2;
        goto L_0x0040;
    L_0x003f:
        r2 = move-exception;
    L_0x0040:
        if (r1 == 0) goto L_0x0064;
    L_0x0042:
        r2 = r1.intValue();
        if (r2 == 0) goto L_0x004d;
    L_0x0048:
        r2 = r1.intValue();
        return r2;
    L_0x004d:
        r2 = new java.lang.IllegalArgumentException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Failed to obtain resource id for: ";
        r3.append(r4);
        r3.append(r7);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
    L_0x0064:
        r2 = new java.lang.IllegalArgumentException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Unrecognized Uri format: ";
        r3.append(r4);
        r3.append(r7);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.drawable.ResourceDrawableDecoder.loadResourceIdFromUri(android.net.Uri):int");
    }

    public ResourceDrawableDecoder(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean handles(@NonNull Uri source, @NonNull Options options) {
        return source.getScheme().equals("android.resource");
    }

    @Nullable
    public Resource<Drawable> decode(@NonNull Uri source, int width, int height, @NonNull Options options) {
        int resId = loadResourceIdFromUri(source);
        String packageName = source.getAuthority();
        return NonOwnedDrawableResource.newInstance(DrawableDecoderCompat.getDrawable(this.context, packageName.equals(this.context.getPackageName()) ? this.context : getContextForPackage(source, packageName), resId));
    }

    @NonNull
    private Context getContextForPackage(Uri source, String packageName) {
        try {
            return this.context.createPackageContext(packageName, 0);
        } catch (NameNotFoundException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to obtain context or unrecognized Uri format for: ");
            stringBuilder.append(source);
            throw new IllegalArgumentException(stringBuilder.toString(), e);
        }
    }
}

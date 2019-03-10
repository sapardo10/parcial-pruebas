package android.support.v7.preference;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.InflateException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class PreferenceInflater {
    private static final HashMap<String, Constructor> CONSTRUCTOR_MAP = new HashMap();
    private static final Class<?>[] CONSTRUCTOR_SIGNATURE = new Class[]{Context.class, AttributeSet.class};
    private static final String EXTRA_TAG_NAME = "extra";
    private static final String INTENT_TAG_NAME = "intent";
    private static final String TAG = "PreferenceInflater";
    private final Object[] mConstructorArgs = new Object[2];
    private final Context mContext;
    private String[] mDefaultPackages;
    private PreferenceManager mPreferenceManager;

    private android.support.v7.preference.Preference createItem(@android.support.annotation.NonNull java.lang.String r10, @android.support.annotation.Nullable java.lang.String[] r11, android.util.AttributeSet r12) throws java.lang.ClassNotFoundException, android.view.InflateException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:34:0x00a4 in {7, 13, 15, 20, 22, 23, 24, 25, 26, 28, 31, 33} preds:[]
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
        r9 = this;
        r0 = CONSTRUCTOR_MAP;
        r0 = r0.get(r10);
        r0 = (java.lang.reflect.Constructor) r0;
        r1 = 1;
        if (r0 != 0) goto L_0x0074;
    L_0x000b:
        r2 = r9.mContext;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r2 = r2.getClassLoader();	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r3 = 0;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        if (r11 == 0) goto L_0x005e;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
    L_0x0014:
        r4 = r11.length;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        if (r4 != 0) goto L_0x0018;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
    L_0x0017:
        goto L_0x005e;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
    L_0x0018:
        r4 = 0;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r5 = r11.length;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r6 = 0;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
    L_0x001b:
        if (r6 >= r5) goto L_0x0039;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
    L_0x001d:
        r7 = r11[r6];	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r8 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x0034, Exception -> 0x0080 }
        r8.<init>();	 Catch:{ ClassNotFoundException -> 0x0034, Exception -> 0x0080 }
        r8.append(r7);	 Catch:{ ClassNotFoundException -> 0x0034, Exception -> 0x0080 }
        r8.append(r10);	 Catch:{ ClassNotFoundException -> 0x0034, Exception -> 0x0080 }
        r8 = r8.toString();	 Catch:{ ClassNotFoundException -> 0x0034, Exception -> 0x0080 }
        r5 = r2.loadClass(r8);	 Catch:{ ClassNotFoundException -> 0x0034, Exception -> 0x0080 }
        r3 = r5;
        goto L_0x0039;
    L_0x0034:
        r8 = move-exception;
        r4 = r8;
        r6 = r6 + 1;
        goto L_0x001b;
    L_0x0039:
        if (r3 != 0) goto L_0x005d;
    L_0x003b:
        if (r4 != 0) goto L_0x005b;
    L_0x003d:
        r1 = new android.view.InflateException;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r5 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r5.<init>();	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r6 = r12.getPositionDescription();	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r5.append(r6);	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r6 = ": Error inflating class ";	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r5.append(r6);	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r5.append(r10);	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r5 = r5.toString();	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r1.<init>(r5);	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        throw r1;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        throw r4;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
    L_0x005d:
        goto L_0x0064;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r4 = r2.loadClass(r10);	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r3 = r4;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
    L_0x0064:
        r4 = CONSTRUCTOR_SIGNATURE;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r4 = r3.getConstructor(r4);	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r0 = r4;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r0.setAccessible(r1);	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r4 = CONSTRUCTOR_MAP;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r4.put(r10, r0);	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        goto L_0x0075;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
    L_0x0075:
        r2 = r9.mConstructorArgs;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r2[r1] = r12;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r1 = r0.newInstance(r2);	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        r1 = (android.support.v7.preference.Preference) r1;	 Catch:{ ClassNotFoundException -> 0x00a2, Exception -> 0x0080 }
        return r1;
    L_0x0080:
        r1 = move-exception;
        r2 = new android.view.InflateException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = r12.getPositionDescription();
        r3.append(r4);
        r4 = ": Error inflating class ";
        r3.append(r4);
        r3.append(r10);
        r3 = r3.toString();
        r2.<init>(r3);
        r2.initCause(r1);
        throw r2;
    L_0x00a2:
        r1 = move-exception;
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.preference.PreferenceInflater.createItem(java.lang.String, java.lang.String[], android.util.AttributeSet):android.support.v7.preference.Preference");
    }

    public android.support.v7.preference.Preference inflate(org.xmlpull.v1.XmlPullParser r7, @android.support.annotation.Nullable android.support.v7.preference.PreferenceGroup r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:33:0x0088 in {9, 10, 16, 19, 23, 26, 29, 32} preds:[]
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
        r0 = r6.mConstructorArgs;
        monitor-enter(r0);
        r1 = android.util.Xml.asAttributeSet(r7);	 Catch:{ all -> 0x0085 }
        r2 = r6.mConstructorArgs;	 Catch:{ all -> 0x0085 }
        r3 = 0;	 Catch:{ all -> 0x0085 }
        r4 = r6.mContext;	 Catch:{ all -> 0x0085 }
        r2[r3] = r4;	 Catch:{ all -> 0x0085 }
    L_0x000e:
        r2 = r7.next();	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        r3 = 2;	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        if (r2 == r3) goto L_0x001a;	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
    L_0x0015:
        r4 = 1;	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        if (r2 != r4) goto L_0x0019;	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
    L_0x0018:
        goto L_0x001a;	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
    L_0x0019:
        goto L_0x000e;	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
    L_0x001a:
        if (r2 != r3) goto L_0x0031;	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
    L_0x001c:
        r3 = r7.getName();	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        r3 = r6.createItemFromTag(r3, r1);	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        r4 = r3;	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        r4 = (android.support.v7.preference.PreferenceGroup) r4;	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        r4 = r6.onMergeRoots(r8, r4);	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        r6.rInflate(r7, r4, r1);	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        monitor-exit(r0);	 Catch:{ all -> 0x0085 }
        return r4;
    L_0x0031:
        r3 = new android.view.InflateException;	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        r4 = new java.lang.StringBuilder;	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        r4.<init>();	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        r5 = r7.getPositionDescription();	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        r4.append(r5);	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        r5 = ": No start tag found!";	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        r4.append(r5);	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        r4 = r4.toString();	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        r3.<init>(r4);	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
        throw r3;	 Catch:{ InflateException -> 0x0082, XmlPullParserException -> 0x0073, IOException -> 0x004c }
    L_0x004c:
        r2 = move-exception;
        r3 = new android.view.InflateException;	 Catch:{ all -> 0x0085 }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0085 }
        r4.<init>();	 Catch:{ all -> 0x0085 }
        r5 = r7.getPositionDescription();	 Catch:{ all -> 0x0085 }
        r4.append(r5);	 Catch:{ all -> 0x0085 }
        r5 = ": ";	 Catch:{ all -> 0x0085 }
        r4.append(r5);	 Catch:{ all -> 0x0085 }
        r5 = r2.getMessage();	 Catch:{ all -> 0x0085 }
        r4.append(r5);	 Catch:{ all -> 0x0085 }
        r4 = r4.toString();	 Catch:{ all -> 0x0085 }
        r3.<init>(r4);	 Catch:{ all -> 0x0085 }
        r3.initCause(r2);	 Catch:{ all -> 0x0085 }
        throw r3;	 Catch:{ all -> 0x0085 }
    L_0x0073:
        r2 = move-exception;	 Catch:{ all -> 0x0085 }
        r3 = new android.view.InflateException;	 Catch:{ all -> 0x0085 }
        r4 = r2.getMessage();	 Catch:{ all -> 0x0085 }
        r3.<init>(r4);	 Catch:{ all -> 0x0085 }
        r3.initCause(r2);	 Catch:{ all -> 0x0085 }
        throw r3;	 Catch:{ all -> 0x0085 }
    L_0x0082:
        r2 = move-exception;	 Catch:{ all -> 0x0085 }
        throw r2;	 Catch:{ all -> 0x0085 }
    L_0x0085:
        r1 = move-exception;	 Catch:{ all -> 0x0085 }
        monitor-exit(r0);	 Catch:{ all -> 0x0085 }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.preference.PreferenceInflater.inflate(org.xmlpull.v1.XmlPullParser, android.support.v7.preference.PreferenceGroup):android.support.v7.preference.Preference");
    }

    public PreferenceInflater(Context context, PreferenceManager preferenceManager) {
        this.mContext = context;
        init(preferenceManager);
    }

    private void init(PreferenceManager preferenceManager) {
        this.mPreferenceManager = preferenceManager;
        setDefaultPackages(new String[]{"android.support.v14.preference.", "android.support.v7.preference."});
    }

    public void setDefaultPackages(String[] defaultPackage) {
        this.mDefaultPackages = defaultPackage;
    }

    public String[] getDefaultPackages() {
        return this.mDefaultPackages;
    }

    public Context getContext() {
        return this.mContext;
    }

    public Preference inflate(int resource, @Nullable PreferenceGroup root) {
        XmlPullParser parser = getContext().getResources().getXml(resource);
        try {
            Preference inflate = inflate(parser, root);
            return inflate;
        } finally {
            parser.close();
        }
    }

    @NonNull
    private PreferenceGroup onMergeRoots(PreferenceGroup givenRoot, @NonNull PreferenceGroup xmlRoot) {
        if (givenRoot != null) {
            return givenRoot;
        }
        xmlRoot.onAttachedToHierarchy(this.mPreferenceManager);
        return xmlRoot;
    }

    protected Preference onCreateItem(String name, AttributeSet attrs) throws ClassNotFoundException {
        return createItem(name, this.mDefaultPackages, attrs);
    }

    private Preference createItemFromTag(String name, AttributeSet attrs) {
        StringBuilder stringBuilder;
        InflateException ie;
        try {
            if (-1 == name.indexOf(46)) {
                return onCreateItem(name, attrs);
            }
            return createItem(name, null, attrs);
        } catch (InflateException e) {
            throw e;
        } catch (ClassNotFoundException e2) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(attrs.getPositionDescription());
            stringBuilder.append(": Error inflating class (not found)");
            stringBuilder.append(name);
            ie = new InflateException(stringBuilder.toString());
            ie.initCause(e2);
            throw ie;
        } catch (Exception e3) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(attrs.getPositionDescription());
            stringBuilder.append(": Error inflating class ");
            stringBuilder.append(name);
            ie = new InflateException(stringBuilder.toString());
            ie.initCause(e3);
            throw ie;
        }
    }

    private void rInflate(XmlPullParser parser, Preference parent, AttributeSet attrs) throws XmlPullParserException, IOException {
        XmlPullParserException ex;
        int depth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 3) {
                if (parser.getDepth() <= depth) {
                    break;
                }
            }
            if (type == 1) {
                break;
            } else if (type == 2) {
                String name = parser.getName();
                if (INTENT_TAG_NAME.equals(name)) {
                    try {
                        parent.setIntent(Intent.parseIntent(getContext().getResources(), parser, attrs));
                    } catch (IOException e) {
                        ex = new XmlPullParserException("Error parsing preference");
                        ex.initCause(e);
                        throw ex;
                    }
                } else if (EXTRA_TAG_NAME.equals(name)) {
                    getContext().getResources().parseBundleExtra(EXTRA_TAG_NAME, attrs, parent.getExtras());
                    try {
                        skipCurrentTag(parser);
                    } catch (IOException e2) {
                        ex = new XmlPullParserException("Error parsing preference");
                        ex.initCause(e2);
                        throw ex;
                    }
                } else {
                    Preference item = createItemFromTag(name, attrs);
                    ((PreferenceGroup) parent).addItemFromInflater(item);
                    rInflate(parser, item, attrs);
                }
            }
        }
    }

    private static void skipCurrentTag(XmlPullParser parser) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type == 3) {
                if (parser.getDepth() <= outerDepth) {
                    return;
                }
            }
        }
    }
}

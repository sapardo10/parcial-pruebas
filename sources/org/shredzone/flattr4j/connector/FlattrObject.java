package org.shredzone.flattr4j.connector;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.shredzone.flattr4j.exception.MarshalException;

public class FlattrObject implements Serializable, Externalizable {
    private static final long serialVersionUID = -6640392574244365803L;
    private transient JSONObject data;

    public java.util.List<org.shredzone.flattr4j.connector.FlattrObject> getObjects(java.lang.String r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x002d in {5, 6, 9} preds:[]
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
        r5 = this;
        r0 = r5.data;	 Catch:{ JSONException -> 0x0026 }
        r0 = r0.getJSONArray(r6);	 Catch:{ JSONException -> 0x0026 }
        r1 = new java.util.ArrayList;	 Catch:{ JSONException -> 0x0026 }
        r2 = r0.length();	 Catch:{ JSONException -> 0x0026 }
        r1.<init>(r2);	 Catch:{ JSONException -> 0x0026 }
        r2 = 0;	 Catch:{ JSONException -> 0x0026 }
    L_0x0010:
        r3 = r0.length();	 Catch:{ JSONException -> 0x0026 }
        if (r2 >= r3) goto L_0x0025;	 Catch:{ JSONException -> 0x0026 }
    L_0x0016:
        r3 = new org.shredzone.flattr4j.connector.FlattrObject;	 Catch:{ JSONException -> 0x0026 }
        r4 = r0.getJSONObject(r2);	 Catch:{ JSONException -> 0x0026 }
        r3.<init>(r4);	 Catch:{ JSONException -> 0x0026 }
        r1.add(r3);	 Catch:{ JSONException -> 0x0026 }
        r2 = r2 + 1;
        goto L_0x0010;
    L_0x0025:
        return r1;
    L_0x0026:
        r0 = move-exception;
        r1 = new org.shredzone.flattr4j.exception.MarshalException;
        r1.<init>(r6, r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.shredzone.flattr4j.connector.FlattrObject.getObjects(java.lang.String):java.util.List<org.shredzone.flattr4j.connector.FlattrObject>");
    }

    public java.util.List<java.lang.String> getStrings(java.lang.String r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0028 in {5, 6, 9} preds:[]
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
        r4 = this;
        r0 = r4.data;	 Catch:{ JSONException -> 0x0021 }
        r0 = r0.getJSONArray(r5);	 Catch:{ JSONException -> 0x0021 }
        r1 = new java.util.ArrayList;	 Catch:{ JSONException -> 0x0021 }
        r2 = r0.length();	 Catch:{ JSONException -> 0x0021 }
        r1.<init>(r2);	 Catch:{ JSONException -> 0x0021 }
        r2 = 0;	 Catch:{ JSONException -> 0x0021 }
    L_0x0010:
        r3 = r0.length();	 Catch:{ JSONException -> 0x0021 }
        if (r2 >= r3) goto L_0x0020;	 Catch:{ JSONException -> 0x0021 }
    L_0x0016:
        r3 = r0.getString(r2);	 Catch:{ JSONException -> 0x0021 }
        r1.add(r3);	 Catch:{ JSONException -> 0x0021 }
        r2 = r2 + 1;
        goto L_0x0010;
    L_0x0020:
        return r1;
    L_0x0021:
        r0 = move-exception;
        r1 = new org.shredzone.flattr4j.exception.MarshalException;
        r1.<init>(r5, r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.shredzone.flattr4j.connector.FlattrObject.getStrings(java.lang.String):java.util.List<java.lang.String>");
    }

    public void putStrings(java.lang.String r4, java.util.Collection<java.lang.String> r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x002b in {6, 7, 8, 11, 14} preds:[]
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
        r3 = this;
        r0 = new org.json.JSONArray;	 Catch:{ JSONException -> 0x0024 }
        r0.<init>();	 Catch:{ JSONException -> 0x0024 }
        if (r5 == 0) goto L_0x001c;	 Catch:{ JSONException -> 0x0024 }
    L_0x0007:
        r1 = r5.iterator();	 Catch:{ JSONException -> 0x0024 }
    L_0x000b:
        r2 = r1.hasNext();	 Catch:{ JSONException -> 0x0024 }
        if (r2 == 0) goto L_0x001b;	 Catch:{ JSONException -> 0x0024 }
    L_0x0011:
        r2 = r1.next();	 Catch:{ JSONException -> 0x0024 }
        r2 = (java.lang.String) r2;	 Catch:{ JSONException -> 0x0024 }
        r0.put(r2);	 Catch:{ JSONException -> 0x0024 }
        goto L_0x000b;	 Catch:{ JSONException -> 0x0024 }
    L_0x001b:
        goto L_0x001d;	 Catch:{ JSONException -> 0x0024 }
    L_0x001d:
        r1 = r3.data;	 Catch:{ JSONException -> 0x0024 }
        r1.put(r4, r0);	 Catch:{ JSONException -> 0x0024 }
        return;
    L_0x0024:
        r0 = move-exception;
        r1 = new org.shredzone.flattr4j.exception.MarshalException;
        r1.<init>(r4, r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.shredzone.flattr4j.connector.FlattrObject.putStrings(java.lang.String, java.util.Collection):void");
    }

    public FlattrObject() {
        this.data = new JSONObject();
    }

    public FlattrObject(JSONObject data) {
        this.data = data;
    }

    public FlattrObject(String json) {
        try {
            this.data = (JSONObject) new JSONTokener(json).nextValue();
        } catch (Throwable ex) {
            throw new MarshalException(ex);
        }
    }

    public boolean has(String key) {
        return this.data.has(key);
    }

    public Object getObject(String key) {
        try {
            return this.data.get(key);
        } catch (JSONException ex) {
            throw new MarshalException(key, ex);
        }
    }

    public String get(String key) {
        try {
            return this.data.getString(key);
        } catch (JSONException ex) {
            throw new MarshalException(key, ex);
        }
    }

    public String getSubString(String key, String subKey) {
        try {
            return this.data.getJSONObject(key).getString(subKey);
        } catch (JSONException ex) {
            throw new MarshalException(key, ex);
        }
    }

    public FlattrObject getFlattrObject(String key) {
        try {
            return new FlattrObject(this.data.getJSONObject(key));
        } catch (JSONException ex) {
            throw new MarshalException(key, ex);
        }
    }

    public int getInt(String key) {
        try {
            return this.data.getInt(key);
        } catch (JSONException ex) {
            throw new MarshalException(key, ex);
        }
    }

    public long getLong(String key) {
        try {
            return this.data.getLong(key);
        } catch (JSONException ex) {
            throw new MarshalException(key, ex);
        }
    }

    public boolean getBoolean(String key) {
        try {
            return this.data.getBoolean(key);
        } catch (JSONException ex) {
            throw new MarshalException(key, ex);
        }
    }

    public Date getDate(String key) {
        try {
            Date date = null;
            if (this.data.isNull(key)) {
                return null;
            }
            long ts = this.data.getLong(key);
            if (ts != 0) {
                date = new Date(1000 * ts);
            }
            return date;
        } catch (JSONException ex) {
            throw new MarshalException(key, ex);
        }
    }

    public void put(String key, Object value) {
        try {
            this.data.put(key, value);
        } catch (JSONException ex) {
            throw new MarshalException(key, ex);
        }
    }

    public String toString() {
        return this.data.toString();
    }

    public JSONObject getJSONObject() {
        return this.data;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.data.toString());
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        try {
            this.data = new JSONObject(in.readUTF());
        } catch (JSONException ex) {
            throw new IOException("JSON deserialization failed", ex);
        }
    }
}

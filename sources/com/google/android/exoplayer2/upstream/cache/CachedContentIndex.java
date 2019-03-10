package com.google.android.exoplayer2.upstream.cache;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.google.android.exoplayer2.upstream.cache.Cache.CacheException;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.AtomicFile;
import com.google.android.exoplayer2.util.ReusableBufferedOutputStream;
import com.google.android.exoplayer2.util.Util;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class CachedContentIndex {
    public static final String FILE_NAME = "cached_content_index.exi";
    private static final int FLAG_ENCRYPTED_INDEX = 1;
    private static final int VERSION = 2;
    private final AtomicFile atomicFile;
    private ReusableBufferedOutputStream bufferedOutputStream;
    private boolean changed;
    private final Cipher cipher;
    private final boolean encrypt;
    private final SparseArray<String> idToKey;
    private final HashMap<String, CachedContent> keyToContent;
    private final SparseBooleanArray removedIds;
    private final SecretKeySpec secretKeySpec;

    private void writeFile() throws com.google.android.exoplayer2.upstream.cache.Cache.CacheException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:37:0x00ae in {4, 5, 8, 9, 17, 20, 21, 25, 28, 34, 36} preds:[]
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
        r10 = this;
        r0 = 0;
        r1 = r10.atomicFile;	 Catch:{ IOException -> 0x00a3 }
        r1 = r1.startWrite();	 Catch:{ IOException -> 0x00a3 }
        r2 = r10.bufferedOutputStream;	 Catch:{ IOException -> 0x00a3 }
        if (r2 != 0) goto L_0x0013;	 Catch:{ IOException -> 0x00a3 }
    L_0x000b:
        r2 = new com.google.android.exoplayer2.util.ReusableBufferedOutputStream;	 Catch:{ IOException -> 0x00a3 }
        r2.<init>(r1);	 Catch:{ IOException -> 0x00a3 }
        r10.bufferedOutputStream = r2;	 Catch:{ IOException -> 0x00a3 }
        goto L_0x0018;	 Catch:{ IOException -> 0x00a3 }
    L_0x0013:
        r2 = r10.bufferedOutputStream;	 Catch:{ IOException -> 0x00a3 }
        r2.reset(r1);	 Catch:{ IOException -> 0x00a3 }
    L_0x0018:
        r2 = new java.io.DataOutputStream;	 Catch:{ IOException -> 0x00a3 }
        r3 = r10.bufferedOutputStream;	 Catch:{ IOException -> 0x00a3 }
        r2.<init>(r3);	 Catch:{ IOException -> 0x00a3 }
        r0 = r2;	 Catch:{ IOException -> 0x00a3 }
        r2 = 2;	 Catch:{ IOException -> 0x00a3 }
        r0.writeInt(r2);	 Catch:{ IOException -> 0x00a3 }
        r3 = r10.encrypt;	 Catch:{ IOException -> 0x00a3 }
        r4 = 1;	 Catch:{ IOException -> 0x00a3 }
        if (r3 == 0) goto L_0x002b;	 Catch:{ IOException -> 0x00a3 }
    L_0x0029:
        r3 = 1;	 Catch:{ IOException -> 0x00a3 }
        goto L_0x002c;	 Catch:{ IOException -> 0x00a3 }
    L_0x002b:
        r3 = 0;	 Catch:{ IOException -> 0x00a3 }
    L_0x002c:
        r0.writeInt(r3);	 Catch:{ IOException -> 0x00a3 }
        r5 = r10.encrypt;	 Catch:{ IOException -> 0x00a3 }
        if (r5 == 0) goto L_0x0069;	 Catch:{ IOException -> 0x00a3 }
    L_0x0033:
        r5 = 16;	 Catch:{ IOException -> 0x00a3 }
        r5 = new byte[r5];	 Catch:{ IOException -> 0x00a3 }
        r6 = new java.util.Random;	 Catch:{ IOException -> 0x00a3 }
        r6.<init>();	 Catch:{ IOException -> 0x00a3 }
        r6.nextBytes(r5);	 Catch:{ IOException -> 0x00a3 }
        r0.write(r5);	 Catch:{ IOException -> 0x00a3 }
        r6 = new javax.crypto.spec.IvParameterSpec;	 Catch:{ IOException -> 0x00a3 }
        r6.<init>(r5);	 Catch:{ IOException -> 0x00a3 }
        r7 = r10.cipher;	 Catch:{ InvalidKeyException -> 0x0062, InvalidKeyException -> 0x0062 }
        r8 = r10.secretKeySpec;	 Catch:{ InvalidKeyException -> 0x0062, InvalidKeyException -> 0x0062 }
        r7.init(r4, r8, r6);	 Catch:{ InvalidKeyException -> 0x0062, InvalidKeyException -> 0x0062 }
        r0.flush();	 Catch:{ IOException -> 0x00a3 }
        r4 = new java.io.DataOutputStream;	 Catch:{ IOException -> 0x00a3 }
        r7 = new javax.crypto.CipherOutputStream;	 Catch:{ IOException -> 0x00a3 }
        r8 = r10.bufferedOutputStream;	 Catch:{ IOException -> 0x00a3 }
        r9 = r10.cipher;	 Catch:{ IOException -> 0x00a3 }
        r7.<init>(r8, r9);	 Catch:{ IOException -> 0x00a3 }
        r4.<init>(r7);	 Catch:{ IOException -> 0x00a3 }
        r0 = r4;	 Catch:{ IOException -> 0x00a3 }
        goto L_0x006a;	 Catch:{ IOException -> 0x00a3 }
    L_0x0062:
        r2 = move-exception;	 Catch:{ IOException -> 0x00a3 }
        r4 = new java.lang.IllegalStateException;	 Catch:{ IOException -> 0x00a3 }
        r4.<init>(r2);	 Catch:{ IOException -> 0x00a3 }
        throw r4;	 Catch:{ IOException -> 0x00a3 }
    L_0x006a:
        r4 = r10.keyToContent;	 Catch:{ IOException -> 0x00a3 }
        r4 = r4.size();	 Catch:{ IOException -> 0x00a3 }
        r0.writeInt(r4);	 Catch:{ IOException -> 0x00a3 }
        r4 = 0;	 Catch:{ IOException -> 0x00a3 }
        r5 = r10.keyToContent;	 Catch:{ IOException -> 0x00a3 }
        r5 = r5.values();	 Catch:{ IOException -> 0x00a3 }
        r5 = r5.iterator();	 Catch:{ IOException -> 0x00a3 }
    L_0x007e:
        r6 = r5.hasNext();	 Catch:{ IOException -> 0x00a3 }
        if (r6 == 0) goto L_0x0093;	 Catch:{ IOException -> 0x00a3 }
    L_0x0084:
        r6 = r5.next();	 Catch:{ IOException -> 0x00a3 }
        r6 = (com.google.android.exoplayer2.upstream.cache.CachedContent) r6;	 Catch:{ IOException -> 0x00a3 }
        r6.writeToStream(r0);	 Catch:{ IOException -> 0x00a3 }
        r7 = r6.headerHashCode(r2);	 Catch:{ IOException -> 0x00a3 }
        r4 = r4 + r7;	 Catch:{ IOException -> 0x00a3 }
        goto L_0x007e;	 Catch:{ IOException -> 0x00a3 }
    L_0x0093:
        r0.writeInt(r4);	 Catch:{ IOException -> 0x00a3 }
        r2 = r10.atomicFile;	 Catch:{ IOException -> 0x00a3 }
        r2.endWrite(r0);	 Catch:{ IOException -> 0x00a3 }
        r0 = 0;
        com.google.android.exoplayer2.util.Util.closeQuietly(r0);
        return;
    L_0x00a1:
        r1 = move-exception;
        goto L_0x00aa;
    L_0x00a3:
        r1 = move-exception;
        r2 = new com.google.android.exoplayer2.upstream.cache.Cache$CacheException;	 Catch:{ all -> 0x00a1 }
        r2.<init>(r1);	 Catch:{ all -> 0x00a1 }
        throw r2;	 Catch:{ all -> 0x00a1 }
    L_0x00aa:
        com.google.android.exoplayer2.util.Util.closeQuietly(r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.cache.CachedContentIndex.writeFile():void");
    }

    public CachedContentIndex(File cacheDir) {
        this(cacheDir, null);
    }

    public CachedContentIndex(File cacheDir, byte[] secretKey) {
        this(cacheDir, secretKey, secretKey != null);
    }

    public CachedContentIndex(File cacheDir, byte[] secretKey, boolean encrypt) {
        this.encrypt = encrypt;
        if (secretKey != null) {
            Assertions.checkArgument(secretKey.length == 16);
            try {
                this.cipher = getCipher();
                this.secretKeySpec = new SecretKeySpec(secretKey, "AES");
            } catch (GeneralSecurityException e) {
                throw new IllegalStateException(e);
            }
        }
        Assertions.checkState(encrypt ^ 1);
        this.cipher = null;
        this.secretKeySpec = null;
        this.keyToContent = new HashMap();
        this.idToKey = new SparseArray();
        this.removedIds = new SparseBooleanArray();
        this.atomicFile = new AtomicFile(new File(cacheDir, FILE_NAME));
    }

    public void load() {
        Assertions.checkState(this.changed ^ 1);
        if (!readFile()) {
            this.atomicFile.delete();
            this.keyToContent.clear();
            this.idToKey.clear();
        }
    }

    public void store() throws CacheException {
        if (this.changed) {
            writeFile();
            this.changed = false;
            int removedIdCount = this.removedIds.size();
            for (int i = 0; i < removedIdCount; i++) {
                this.idToKey.remove(this.removedIds.keyAt(i));
            }
            this.removedIds.clear();
        }
    }

    public CachedContent getOrAdd(String key) {
        CachedContent cachedContent = (CachedContent) this.keyToContent.get(key);
        return cachedContent == null ? addNew(key) : cachedContent;
    }

    public CachedContent get(String key) {
        return (CachedContent) this.keyToContent.get(key);
    }

    public Collection<CachedContent> getAll() {
        return this.keyToContent.values();
    }

    public int assignIdForKey(String key) {
        return getOrAdd(key).id;
    }

    public String getKeyForId(int id) {
        return (String) this.idToKey.get(id);
    }

    public void maybeRemove(String key) {
        CachedContent cachedContent = (CachedContent) this.keyToContent.get(key);
        if (cachedContent != null && cachedContent.isEmpty() && !cachedContent.isLocked()) {
            this.keyToContent.remove(key);
            this.changed = true;
            this.idToKey.put(cachedContent.id, null);
            this.removedIds.put(cachedContent.id, true);
        }
    }

    public void removeEmpty() {
        String[] keys = new String[this.keyToContent.size()];
        this.keyToContent.keySet().toArray(keys);
        for (String key : keys) {
            maybeRemove(key);
        }
    }

    public Set<String> getKeys() {
        return this.keyToContent.keySet();
    }

    public void applyContentMetadataMutations(String key, ContentMetadataMutations mutations) {
        if (getOrAdd(key).applyMetadataMutations(mutations)) {
            this.changed = true;
        }
    }

    public ContentMetadata getContentMetadata(String key) {
        CachedContent cachedContent = get(key);
        return cachedContent != null ? cachedContent.getMetadata() : DefaultContentMetadata.EMPTY;
    }

    private boolean readFile() {
        DataInputStream input = null;
        try {
            InputStream inputStream = new BufferedInputStream(this.atomicFile.openRead());
            input = new DataInputStream(inputStream);
            int version = input.readInt();
            if (version >= 0) {
                if (version <= 2) {
                    int count;
                    int hashCode;
                    int i;
                    CachedContent cachedContent;
                    boolean isEOF;
                    if ((input.readInt() & 1) != 0) {
                        if (this.cipher == null) {
                            Util.closeQuietly(input);
                            return false;
                        }
                        byte[] initializationVector = new byte[16];
                        input.readFully(initializationVector);
                        this.cipher.init(2, this.secretKeySpec, new IvParameterSpec(initializationVector));
                        input = new DataInputStream(new CipherInputStream(inputStream, this.cipher));
                    } else if (this.encrypt) {
                        this.changed = true;
                        count = input.readInt();
                        hashCode = 0;
                        for (i = 0; i < count; i++) {
                            cachedContent = CachedContent.readFromStream(version, input);
                            add(cachedContent);
                            hashCode += cachedContent.headerHashCode(version);
                        }
                        i = input.readInt();
                        isEOF = input.read() != -1;
                        if (i == hashCode) {
                            if (!isEOF) {
                                Util.closeQuietly(input);
                                return true;
                            }
                        }
                        Util.closeQuietly(input);
                        return false;
                    }
                    count = input.readInt();
                    hashCode = 0;
                    for (i = 0; i < count; i++) {
                        cachedContent = CachedContent.readFromStream(version, input);
                        add(cachedContent);
                        hashCode += cachedContent.headerHashCode(version);
                    }
                    i = input.readInt();
                    if (input.read() != -1) {
                    }
                    if (i == hashCode) {
                        if (!isEOF) {
                            Util.closeQuietly(input);
                            return true;
                        }
                    }
                    Util.closeQuietly(input);
                    return false;
                }
            }
            Util.closeQuietly(input);
            return false;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        } catch (IOException e2) {
            if (input != null) {
                Util.closeQuietly(input);
            }
            return false;
        } catch (Throwable th) {
            if (input != null) {
                Util.closeQuietly(input);
            }
        }
    }

    private CachedContent addNew(String key) {
        CachedContent cachedContent = new CachedContent(getNewId(this.idToKey), key);
        add(cachedContent);
        this.changed = true;
        return cachedContent;
    }

    private void add(CachedContent cachedContent) {
        this.keyToContent.put(cachedContent.key, cachedContent);
        this.idToKey.put(cachedContent.id, cachedContent.key);
    }

    private static Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        if (Util.SDK_INT != 18) {
            return Cipher.getInstance("AES/CBC/PKCS5PADDING");
        }
        try {
            return Cipher.getInstance("AES/CBC/PKCS5PADDING", "BC");
        } catch (Throwable th) {
        }
    }

    public static int getNewId(SparseArray<String> idToKey) {
        int size = idToKey.size();
        int id = size == 0 ? 0 : idToKey.keyAt(size - 1) + 1;
        if (id < 0) {
            id = 0;
            while (id < size) {
                if (id != idToKey.keyAt(id)) {
                    break;
                }
                id++;
            }
        }
        return id;
    }
}

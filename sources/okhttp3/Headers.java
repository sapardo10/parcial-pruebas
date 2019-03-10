package okhttp3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.Nullable;
import okhttp3.internal.http.HttpDate;

public final class Headers {
    private final String[] namesAndValues;

    public static okhttp3.Headers of(java.util.Map<java.lang.String, java.lang.String> r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x0094 in {14, 16, 18, 20, 22} preds:[]
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
        if (r9 == 0) goto L_0x008c;
    L_0x0002:
        r0 = r9.size();
        r0 = r0 * 2;
        r0 = new java.lang.String[r0];
        r1 = 0;
        r2 = r9.entrySet();
        r2 = r2.iterator();
    L_0x0013:
        r3 = r2.hasNext();
        if (r3 == 0) goto L_0x0086;
    L_0x0019:
        r3 = r2.next();
        r3 = (java.util.Map.Entry) r3;
        r4 = r3.getKey();
        if (r4 == 0) goto L_0x007d;
    L_0x0025:
        r4 = r3.getValue();
        if (r4 == 0) goto L_0x007d;
    L_0x002b:
        r4 = r3.getKey();
        r4 = (java.lang.String) r4;
        r4 = r4.trim();
        r5 = r3.getValue();
        r5 = (java.lang.String) r5;
        r5 = r5.trim();
        r6 = r4.length();
        if (r6 == 0) goto L_0x005d;
    L_0x0045:
        r6 = 0;
        r7 = r4.indexOf(r6);
        r8 = -1;
        if (r7 != r8) goto L_0x005d;
    L_0x004d:
        r6 = r5.indexOf(r6);
        if (r6 != r8) goto L_0x005d;
    L_0x0053:
        r0[r1] = r4;
        r6 = r1 + 1;
        r0[r6] = r5;
        r1 = r1 + 2;
        goto L_0x0013;
        r2 = new java.lang.IllegalArgumentException;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "Unexpected header: ";
        r6.append(r7);
        r6.append(r4);
        r7 = ": ";
        r6.append(r7);
        r6.append(r5);
        r6 = r6.toString();
        r2.<init>(r6);
        throw r2;
        r2 = new java.lang.IllegalArgumentException;
        r4 = "Headers cannot be null";
        r2.<init>(r4);
        throw r2;
    L_0x0086:
        r2 = new okhttp3.Headers;
        r2.<init>(r0);
        return r2;
    L_0x008c:
        r0 = new java.lang.NullPointerException;
        r1 = "headers == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.Headers.of(java.util.Map):okhttp3.Headers");
    }

    public static okhttp3.Headers of(java.lang.String... r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:29:0x0082 in {8, 10, 20, 22, 24, 26, 28} preds:[]
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
        if (r6 == 0) goto L_0x007a;
    L_0x0002:
        r0 = r6.length;
        r0 = r0 % 2;
        if (r0 != 0) goto L_0x0072;
    L_0x0007:
        r0 = r6.clone();
        r6 = r0;
        r6 = (java.lang.String[]) r6;
        r0 = 0;
    L_0x000f:
        r1 = r6.length;
        if (r0 >= r1) goto L_0x0029;
    L_0x0012:
        r1 = r6[r0];
        if (r1 == 0) goto L_0x0021;
    L_0x0016:
        r1 = r6[r0];
        r1 = r1.trim();
        r6[r0] = r1;
        r0 = r0 + 1;
        goto L_0x000f;
    L_0x0021:
        r1 = new java.lang.IllegalArgumentException;
        r2 = "Headers cannot be null";
        r1.<init>(r2);
        throw r1;
        r0 = 0;
    L_0x002b:
        r1 = r6.length;
        if (r0 >= r1) goto L_0x006b;
    L_0x002e:
        r1 = r6[r0];
        r2 = r0 + 1;
        r2 = r6[r2];
        r3 = r1.length();
        if (r3 == 0) goto L_0x004b;
    L_0x003a:
        r3 = 0;
        r4 = r1.indexOf(r3);
        r5 = -1;
        if (r4 != r5) goto L_0x004b;
    L_0x0042:
        r3 = r2.indexOf(r3);
        if (r3 != r5) goto L_0x004b;
    L_0x0048:
        r0 = r0 + 2;
        goto L_0x002b;
        r3 = new java.lang.IllegalArgumentException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Unexpected header: ";
        r4.append(r5);
        r4.append(r1);
        r5 = ": ";
        r4.append(r5);
        r4.append(r2);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
        r0 = new okhttp3.Headers;
        r0.<init>(r6);
        return r0;
    L_0x0072:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Expected alternating header names and values";
        r0.<init>(r1);
        throw r0;
    L_0x007a:
        r0 = new java.lang.NullPointerException;
        r1 = "namesAndValues == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.Headers.of(java.lang.String[]):okhttp3.Headers");
    }

    Headers(Headers$Builder builder) {
        this.namesAndValues = (String[]) builder.namesAndValues.toArray(new String[builder.namesAndValues.size()]);
    }

    private Headers(String[] namesAndValues) {
        this.namesAndValues = namesAndValues;
    }

    @Nullable
    public String get(String name) {
        return get(this.namesAndValues, name);
    }

    @Nullable
    public Date getDate(String name) {
        String value = get(name);
        return value != null ? HttpDate.parse(value) : null;
    }

    public int size() {
        return this.namesAndValues.length / 2;
    }

    public String name(int index) {
        return this.namesAndValues[index * 2];
    }

    public String value(int index) {
        return this.namesAndValues[(index * 2) + 1];
    }

    public Set<String> names() {
        TreeSet<String> result = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        int size = size();
        for (int i = 0; i < size; i++) {
            result.add(name(i));
        }
        return Collections.unmodifiableSet(result);
    }

    public List<String> values(String name) {
        List<String> result = null;
        int size = size();
        for (int i = 0; i < size; i++) {
            if (name.equalsIgnoreCase(name(i))) {
                if (result == null) {
                    result = new ArrayList(2);
                }
                result.add(value(i));
            }
        }
        if (result != null) {
            return Collections.unmodifiableList(result);
        }
        return Collections.emptyList();
    }

    public long byteCount() {
        int size = this.namesAndValues;
        long result = (long) (size.length * 2);
        for (int i = 0; i < size.length; i++) {
            result += (long) this.namesAndValues[i].length();
        }
        return result;
    }

    public Headers$Builder newBuilder() {
        Headers$Builder result = new Headers$Builder();
        Collections.addAll(result.namesAndValues, this.namesAndValues);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (other instanceof Headers) {
            if (Arrays.equals(((Headers) other).namesAndValues, this.namesAndValues)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(this.namesAndValues);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        int size = size();
        for (int i = 0; i < size; i++) {
            result.append(name(i));
            result.append(": ");
            result.append(value(i));
            result.append("\n");
        }
        return result.toString();
    }

    public Map<String, List<String>> toMultimap() {
        Map<String, List<String>> result = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        int size = size();
        for (int i = 0; i < size; i++) {
            String name = name(i).toLowerCase(Locale.US);
            List<String> values = (List) result.get(name);
            if (values == null) {
                values = new ArrayList(2);
                result.put(name, values);
            }
            values.add(value(i));
        }
        return result;
    }

    private static String get(String[] namesAndValues, String name) {
        for (int i = namesAndValues.length - 2; i >= 0; i -= 2) {
            if (name.equalsIgnoreCase(namesAndValues[i])) {
                return namesAndValues[i + 1];
            }
        }
        return null;
    }
}

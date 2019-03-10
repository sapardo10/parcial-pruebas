package org.apache.commons.text;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public final class AlphabetConverter {
    private static final String ARROW = " -> ";
    private final int encodedLetterLength;
    private final Map<String, String> encodedToOriginal;
    private final Map<Integer, String> originalToEncoded;

    public static org.apache.commons.text.AlphabetConverter createConverter(java.lang.Integer[] r16, java.lang.Integer[] r17, java.lang.Integer[] r18) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:38:0x0173 in {7, 9, 11, 19, 23, 24, 25, 27, 33, 35, 37} preds:[]
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
        r0 = new java.util.LinkedHashSet;
        r1 = java.util.Arrays.asList(r16);
        r0.<init>(r1);
        r1 = new java.util.LinkedHashSet;
        r2 = java.util.Arrays.asList(r17);
        r1.<init>(r2);
        r2 = new java.util.LinkedHashSet;
        r3 = java.util.Arrays.asList(r18);
        r2.<init>(r3);
        r3 = new java.util.LinkedHashMap;
        r3.<init>();
        r9 = r3;
        r3 = new java.util.LinkedHashMap;
        r3.<init>();
        r10 = r3;
        r3 = new java.util.HashMap;
        r3.<init>();
        r11 = r3;
        r3 = r2.iterator();
    L_0x0031:
        r4 = r3.hasNext();
        if (r4 == 0) goto L_0x00a1;
    L_0x0037:
        r4 = r3.next();
        r4 = (java.lang.Integer) r4;
        r4 = r4.intValue();
        r5 = java.lang.Integer.valueOf(r4);
        r5 = r0.contains(r5);
        if (r5 == 0) goto L_0x0081;
    L_0x004b:
        r5 = java.lang.Integer.valueOf(r4);
        r5 = r1.contains(r5);
        if (r5 == 0) goto L_0x0061;
    L_0x0055:
        r5 = java.lang.Integer.valueOf(r4);
        r6 = codePointToString(r4);
        r11.put(r5, r6);
        goto L_0x0031;
    L_0x0061:
        r3 = new java.lang.IllegalArgumentException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Can not use 'do not encode' list because encoding alphabet does not contain '";
        r5.append(r6);
        r6 = codePointToString(r4);
        r5.append(r6);
        r6 = "'";
        r5.append(r6);
        r5 = r5.toString();
        r3.<init>(r5);
        throw r3;
    L_0x0081:
        r3 = new java.lang.IllegalArgumentException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Can not use 'do not encode' list because original alphabet does not contain '";
        r5.append(r6);
        r6 = codePointToString(r4);
        r5.append(r6);
        r6 = "'";
        r5.append(r6);
        r5 = r5.toString();
        r3.<init>(r5);
        throw r3;
    L_0x00a1:
        r3 = r1.size();
        r4 = r0.size();
        if (r3 < r4) goto L_0x010b;
    L_0x00ab:
        r3 = 1;
        r4 = r1.iterator();
        r5 = r0.iterator();
    L_0x00b4:
        r6 = r5.hasNext();
        if (r6 == 0) goto L_0x0105;
    L_0x00ba:
        r6 = r5.next();
        r6 = (java.lang.Integer) r6;
        r6 = r6.intValue();
        r7 = codePointToString(r6);
        r8 = java.lang.Integer.valueOf(r6);
        r8 = r11.containsKey(r8);
        if (r8 == 0) goto L_0x00de;
    L_0x00d3:
        r8 = java.lang.Integer.valueOf(r6);
        r9.put(r8, r7);
        r10.put(r7, r7);
        goto L_0x0104;
    L_0x00de:
        r8 = r4.next();
        r8 = (java.lang.Integer) r8;
    L_0x00e4:
        r12 = r2.contains(r8);
        if (r12 == 0) goto L_0x00f2;
    L_0x00ea:
        r12 = r4.next();
        r8 = r12;
        r8 = (java.lang.Integer) r8;
        goto L_0x00e4;
    L_0x00f2:
        r12 = r8.intValue();
        r12 = codePointToString(r12);
        r13 = java.lang.Integer.valueOf(r6);
        r9.put(r13, r12);
        r10.put(r12, r7);
    L_0x0104:
        goto L_0x00b4;
    L_0x0105:
        r5 = new org.apache.commons.text.AlphabetConverter;
        r5.<init>(r9, r10, r3);
        return r5;
    L_0x010b:
        r3 = r1.size();
        r4 = r2.size();
        r3 = r3 - r4;
        r4 = 2;
        if (r3 < r4) goto L_0x0153;
    L_0x0117:
        r3 = 1;
        r4 = r0.size();
        r5 = r2.size();
        r4 = r4 - r5;
        r5 = r1.size();
        r6 = r2.size();
        r5 = r5 - r6;
        r4 = r4 / r5;
        r13 = r3;
        r12 = r4;
    L_0x012d:
        r3 = r1.size();
        r3 = r12 / r3;
        r4 = 1;
        if (r3 < r4) goto L_0x013e;
    L_0x0136:
        r3 = r1.size();
        r12 = r12 / r3;
        r13 = r13 + 1;
        goto L_0x012d;
    L_0x013e:
        r14 = r13 + 1;
        r3 = new org.apache.commons.text.AlphabetConverter;
        r3.<init>(r9, r10, r14);
        r15 = r3;
        r5 = "";
        r7 = r0.iterator();
        r4 = r14;
        r6 = r1;
        r8 = r11;
        r3.addSingleEncoding(r4, r5, r6, r7, r8);
        return r15;
    L_0x0153:
        r3 = new java.lang.IllegalArgumentException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Must have at least two encoding characters (excluding those in the 'do not encode' list), but has ";
        r4.append(r5);
        r5 = r1.size();
        r6 = r2.size();
        r5 = r5 - r6;
        r4.append(r5);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.text.AlphabetConverter.createConverter(java.lang.Integer[], java.lang.Integer[], java.lang.Integer[]):org.apache.commons.text.AlphabetConverter");
    }

    private AlphabetConverter(Map<Integer, String> originalToEncoded, Map<String, String> encodedToOriginal, int encodedLetterLength) {
        this.originalToEncoded = originalToEncoded;
        this.encodedToOriginal = encodedToOriginal;
        this.encodedLetterLength = encodedLetterLength;
    }

    public String encode(String original) throws UnsupportedEncodingException {
        if (original == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < original.length()) {
            int codepoint = original.codePointAt(i);
            String nextLetter = (String) this.originalToEncoded.get(Integer.valueOf(codepoint));
            if (nextLetter != null) {
                sb.append(nextLetter);
                i += Character.charCount(codepoint);
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Couldn't find encoding for '");
                stringBuilder.append(codePointToString(codepoint));
                stringBuilder.append("' in ");
                stringBuilder.append(original);
                throw new UnsupportedEncodingException(stringBuilder.toString());
            }
        }
        return sb.toString();
    }

    public String decode(String encoded) throws UnsupportedEncodingException {
        if (encoded == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        int j = 0;
        while (j < encoded.length()) {
            Integer i = Integer.valueOf(encoded.codePointAt(j));
            String s = codePointToString(i.intValue());
            if (s.equals(this.originalToEncoded.get(i))) {
                result.append(s);
                j++;
            } else if (this.encodedLetterLength + j <= encoded.length()) {
                String nextGroup = encoded.substring(j, this.encodedLetterLength + j);
                String next = (String) this.encodedToOriginal.get(nextGroup);
                if (next != null) {
                    result.append(next);
                    j += this.encodedLetterLength;
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Unexpected string without decoding (");
                    stringBuilder.append(nextGroup);
                    stringBuilder.append(") in ");
                    stringBuilder.append(encoded);
                    throw new UnsupportedEncodingException(stringBuilder.toString());
                }
            } else {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Unexpected end of string while decoding ");
                stringBuilder2.append(encoded);
                throw new UnsupportedEncodingException(stringBuilder2.toString());
            }
        }
        return result.toString();
    }

    public int getEncodedCharLength() {
        return this.encodedLetterLength;
    }

    public Map<Integer, String> getOriginalToEncoded() {
        return Collections.unmodifiableMap(this.originalToEncoded);
    }

    private void addSingleEncoding(int level, String currentEncoding, Collection<Integer> encoding, Iterator<Integer> originals, Map<Integer, String> doNotEncodeMap) {
        if (level > 0) {
            for (Integer encodingLetter : encoding) {
                int encodingLetter2 = encodingLetter.intValue();
                if (originals.hasNext()) {
                    if (level == this.encodedLetterLength) {
                        if (doNotEncodeMap.containsKey(Integer.valueOf(encodingLetter2))) {
                        }
                    }
                    int i = level - 1;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(currentEncoding);
                    stringBuilder.append(codePointToString(encodingLetter2));
                    addSingleEncoding(i, stringBuilder.toString(), encoding, originals, doNotEncodeMap);
                } else {
                    return;
                }
            }
        }
        String originalLetterAsString;
        Integer next = (Integer) originals.next();
        while (doNotEncodeMap.containsKey(next)) {
            originalLetterAsString = codePointToString(next.intValue());
            this.originalToEncoded.put(next, originalLetterAsString);
            this.encodedToOriginal.put(originalLetterAsString, originalLetterAsString);
            if (originals.hasNext()) {
                next = (Integer) originals.next();
            } else {
                return;
            }
        }
        originalLetterAsString = codePointToString(next.intValue());
        this.originalToEncoded.put(next, currentEncoding);
        this.encodedToOriginal.put(currentEncoding, originalLetterAsString);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry<Integer, String> entry : this.originalToEncoded.entrySet()) {
            sb.append(codePointToString(((Integer) entry.getKey()).intValue()));
            sb.append(ARROW);
            sb.append((String) entry.getValue());
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AlphabetConverter)) {
            return false;
        }
        AlphabetConverter other = (AlphabetConverter) obj;
        if (this.originalToEncoded.equals(other.originalToEncoded)) {
            if (this.encodedToOriginal.equals(other.encodedToOriginal) && this.encodedLetterLength == other.encodedLetterLength) {
                z = true;
                return z;
            }
        }
        return z;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.originalToEncoded, this.encodedToOriginal, Integer.valueOf(this.encodedLetterLength)});
    }

    public static AlphabetConverter createConverterFromMap(Map<Integer, String> originalToEncoded) {
        Map<Integer, String> unmodifiableOriginalToEncoded = Collections.unmodifiableMap(originalToEncoded);
        Map<String, String> encodedToOriginal = new LinkedHashMap();
        int encodedLetterLength = 1;
        for (Entry<Integer, String> e : unmodifiableOriginalToEncoded.entrySet()) {
            encodedToOriginal.put(e.getValue(), codePointToString(((Integer) e.getKey()).intValue()));
            if (((String) e.getValue()).length() > encodedLetterLength) {
                encodedLetterLength = ((String) e.getValue()).length();
            }
        }
        return new AlphabetConverter(unmodifiableOriginalToEncoded, encodedToOriginal, encodedLetterLength);
    }

    public static AlphabetConverter createConverterFromChars(Character[] original, Character[] encoding, Character[] doNotEncode) {
        return createConverter(convertCharsToIntegers(original), convertCharsToIntegers(encoding), convertCharsToIntegers(doNotEncode));
    }

    private static Integer[] convertCharsToIntegers(Character[] chars) {
        if (chars != null) {
            if (chars.length != 0) {
                Integer[] integers = new Integer[chars.length];
                for (int i = 0; i < chars.length; i++) {
                    integers[i] = Integer.valueOf(chars[i].charValue());
                }
                return integers;
            }
        }
        return new Integer[0];
    }

    private static String codePointToString(int i) {
        if (Character.charCount(i) == 1) {
            return String.valueOf((char) i);
        }
        return new String(Character.toChars(i));
    }
}

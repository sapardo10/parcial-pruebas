package org.apache.commons.lang3.text;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class StrSubstitutor {
    public static final char DEFAULT_ESCAPE = '$';
    public static final StrMatcher DEFAULT_PREFIX = StrMatcher.stringMatcher("${");
    public static final StrMatcher DEFAULT_SUFFIX = StrMatcher.stringMatcher("}");
    public static final StrMatcher DEFAULT_VALUE_DELIMITER = StrMatcher.stringMatcher(":-");
    private boolean enableSubstitutionInVariables;
    private char escapeChar;
    private StrMatcher prefixMatcher;
    private boolean preserveEscapes;
    private StrMatcher suffixMatcher;
    private StrMatcher valueDelimiterMatcher;
    private StrLookup<?> variableResolver;

    public static <V> String replace(Object source, Map<String, V> valueMap) {
        return new StrSubstitutor((Map) valueMap).replace(source);
    }

    public static <V> String replace(Object source, Map<String, V> valueMap, String prefix, String suffix) {
        return new StrSubstitutor(valueMap, prefix, suffix).replace(source);
    }

    public static String replace(Object source, Properties valueProperties) {
        if (valueProperties == null) {
            return source.toString();
        }
        Map valueMap = new HashMap();
        Enumeration<?> propNames = valueProperties.propertyNames();
        while (propNames.hasMoreElements()) {
            String propName = (String) propNames.nextElement();
            valueMap.put(propName, valueProperties.getProperty(propName));
        }
        return replace(source, valueMap);
    }

    public static String replaceSystemProperties(Object source) {
        return new StrSubstitutor(StrLookup.systemPropertiesLookup()).replace(source);
    }

    public StrSubstitutor() {
        this(null, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public <V> StrSubstitutor(Map<String, V> valueMap) {
        this(StrLookup.mapLookup(valueMap), DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public <V> StrSubstitutor(Map<String, V> valueMap, String prefix, String suffix) {
        this(StrLookup.mapLookup(valueMap), prefix, suffix, '$');
    }

    public <V> StrSubstitutor(Map<String, V> valueMap, String prefix, String suffix, char escape) {
        this(StrLookup.mapLookup(valueMap), prefix, suffix, escape);
    }

    public <V> StrSubstitutor(Map<String, V> valueMap, String prefix, String suffix, char escape, String valueDelimiter) {
        this(StrLookup.mapLookup(valueMap), prefix, suffix, escape, valueDelimiter);
    }

    public StrSubstitutor(StrLookup<?> variableResolver) {
        this((StrLookup) variableResolver, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public StrSubstitutor(StrLookup<?> variableResolver, String prefix, String suffix, char escape) {
        this.preserveEscapes = false;
        setVariableResolver(variableResolver);
        setVariablePrefix(prefix);
        setVariableSuffix(suffix);
        setEscapeChar(escape);
        setValueDelimiterMatcher(DEFAULT_VALUE_DELIMITER);
    }

    public StrSubstitutor(StrLookup<?> variableResolver, String prefix, String suffix, char escape, String valueDelimiter) {
        this.preserveEscapes = false;
        setVariableResolver(variableResolver);
        setVariablePrefix(prefix);
        setVariableSuffix(suffix);
        setEscapeChar(escape);
        setValueDelimiter(valueDelimiter);
    }

    public StrSubstitutor(StrLookup<?> variableResolver, StrMatcher prefixMatcher, StrMatcher suffixMatcher, char escape) {
        this((StrLookup) variableResolver, prefixMatcher, suffixMatcher, escape, DEFAULT_VALUE_DELIMITER);
    }

    public StrSubstitutor(StrLookup<?> variableResolver, StrMatcher prefixMatcher, StrMatcher suffixMatcher, char escape, StrMatcher valueDelimiterMatcher) {
        this.preserveEscapes = false;
        setVariableResolver(variableResolver);
        setVariablePrefixMatcher(prefixMatcher);
        setVariableSuffixMatcher(suffixMatcher);
        setEscapeChar(escape);
        setValueDelimiterMatcher(valueDelimiterMatcher);
    }

    public String replace(String source) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(source);
        if (substitute(buf, 0, source.length())) {
            return buf.toString();
        }
        return source;
    }

    public String replace(String source, int offset, int length) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        if (substitute(buf, 0, length)) {
            return buf.toString();
        }
        return source.substring(offset, offset + length);
    }

    public String replace(char[] source) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(source.length).append(source);
        substitute(buf, 0, source.length);
        return buf.toString();
    }

    public String replace(char[] source, int offset, int length) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(StringBuffer source) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(source.length()).append(source);
        substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public String replace(StringBuffer source, int offset, int length) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(CharSequence source) {
        if (source == null) {
            return null;
        }
        return replace(source, 0, source.length());
    }

    public String replace(CharSequence source, int offset, int length) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(StrBuilder source) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(source.length()).append(source);
        substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public String replace(StrBuilder source, int offset, int length) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(Object source) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder().append(source);
        substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public boolean replaceIn(StringBuffer source) {
        if (source == null) {
            return false;
        }
        return replaceIn(source, 0, source.length());
    }

    public boolean replaceIn(StringBuffer source, int offset, int length) {
        if (source == null) {
            return false;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        if (!substitute(buf, 0, length)) {
            return false;
        }
        source.replace(offset, offset + length, buf.toString());
        return true;
    }

    public boolean replaceIn(StringBuilder source) {
        if (source == null) {
            return false;
        }
        return replaceIn(source, 0, source.length());
    }

    public boolean replaceIn(StringBuilder source, int offset, int length) {
        if (source == null) {
            return false;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        if (!substitute(buf, 0, length)) {
            return false;
        }
        source.replace(offset, offset + length, buf.toString());
        return true;
    }

    public boolean replaceIn(StrBuilder source) {
        if (source == null) {
            return false;
        }
        return substitute(source, 0, source.length());
    }

    public boolean replaceIn(StrBuilder source, int offset, int length) {
        if (source == null) {
            return false;
        }
        return substitute(source, offset, length);
    }

    protected boolean substitute(StrBuilder buf, int offset, int length) {
        return substitute(buf, offset, length, null) > 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int substitute(org.apache.commons.lang3.text.StrBuilder r30, int r31, int r32, java.util.List<java.lang.String> r33) {
        /*
        r29 = this;
        r0 = r29;
        r1 = r30;
        r2 = r31;
        r3 = r32;
        r4 = r29.getVariablePrefixMatcher();
        r5 = r29.getVariableSuffixMatcher();
        r6 = r29.getEscapeChar();
        r7 = r29.getValueDelimiterMatcher();
        r8 = r29.isEnableSubstitutionInVariables();
        if (r33 != 0) goto L_0x0020;
    L_0x001e:
        r11 = 1;
        goto L_0x0021;
    L_0x0020:
        r11 = 0;
    L_0x0021:
        r12 = 0;
        r13 = 0;
        r14 = r1.buffer;
        r15 = r2 + r3;
        r16 = r31;
        r17 = r12;
        r12 = r16;
        r16 = r33;
    L_0x002f:
        if (r12 >= r15) goto L_0x017b;
    L_0x0031:
        r18 = r4.isMatch(r14, r12, r2, r15);
        if (r18 != 0) goto L_0x0043;
    L_0x0037:
        r12 = r12 + 1;
        r22 = r5;
        r24 = r6;
        r27 = r11;
        r21 = 1;
        goto L_0x0171;
    L_0x0043:
        if (r12 <= r2) goto L_0x006b;
    L_0x0045:
        r19 = r12 + -1;
        r10 = r14[r19];
        if (r10 != r6) goto L_0x006b;
    L_0x004b:
        r10 = r0.preserveEscapes;
        if (r10 == 0) goto L_0x0052;
    L_0x004f:
        r12 = r12 + 1;
        goto L_0x002f;
    L_0x0052:
        r10 = r12 + -1;
        r1.deleteCharAt(r10);
        r10 = r1.buffer;
        r13 = r13 + -1;
        r14 = 1;
        r15 = r15 + -1;
        r22 = r5;
        r24 = r6;
        r27 = r11;
        r17 = r14;
        r21 = 1;
        r14 = r10;
        goto L_0x0171;
        r10 = r12;
        r12 = r12 + r18;
        r19 = 0;
        r20 = 0;
    L_0x0073:
        if (r12 >= r15) goto L_0x0167;
    L_0x0075:
        if (r8 == 0) goto L_0x0084;
    L_0x0077:
        r21 = r4.isMatch(r14, r12, r2, r15);
        r19 = r21;
        if (r21 == 0) goto L_0x0084;
    L_0x007f:
        r20 = r20 + 1;
        r12 = r12 + r19;
        goto L_0x0073;
        r19 = r5.isMatch(r14, r12, r2, r15);
        if (r19 != 0) goto L_0x008e;
    L_0x008b:
        r12 = r12 + 1;
        goto L_0x0073;
    L_0x008e:
        if (r20 != 0) goto L_0x0155;
    L_0x0090:
        r9 = new java.lang.String;
        r22 = r5;
        r5 = r10 + r18;
        r23 = r12 - r10;
        r24 = r6;
        r6 = r23 - r18;
        r9.<init>(r14, r5, r6);
        r5 = r9;
        if (r8 == 0) goto L_0x00b6;
    L_0x00a2:
        r6 = new org.apache.commons.lang3.text.StrBuilder;
        r6.<init>(r5);
        r9 = r6.length();
        r33 = r5;
        r5 = 0;
        r0.substitute(r6, r5, r9);
        r5 = r6.toString();
        goto L_0x00b8;
    L_0x00b6:
        r33 = r5;
    L_0x00b8:
        r12 = r12 + r19;
        r6 = r12;
        r9 = r5;
        r23 = 0;
        if (r7 == 0) goto L_0x00fa;
    L_0x00c0:
        r33 = r9;
        r9 = r5.toCharArray();
        r25 = 0;
        r26 = 0;
        r27 = r11;
        r11 = r26;
    L_0x00ce:
        r26 = r13;
        r13 = r9.length;
        if (r11 >= r13) goto L_0x00f9;
    L_0x00d3:
        if (r8 != 0) goto L_0x00dd;
    L_0x00d5:
        r13 = r9.length;
        r13 = r4.isMatch(r9, r11, r11, r13);
        if (r13 == 0) goto L_0x00dd;
    L_0x00dc:
        goto L_0x0100;
        r13 = r7.isMatch(r9, r11);
        r25 = r13;
        if (r13 == 0) goto L_0x00f4;
    L_0x00e6:
        r13 = 0;
        r21 = r5.substring(r13, r11);
        r13 = r11 + r25;
        r23 = r5.substring(r13);
        r9 = r21;
        goto L_0x0102;
    L_0x00f4:
        r11 = r11 + 1;
        r13 = r26;
        goto L_0x00ce;
    L_0x00f9:
        goto L_0x0100;
    L_0x00fa:
        r33 = r9;
        r27 = r11;
        r26 = r13;
    L_0x0100:
        r9 = r33;
    L_0x0102:
        if (r16 != 0) goto L_0x0112;
    L_0x0104:
        r11 = new java.util.ArrayList;
        r11.<init>();
        r13 = new java.lang.String;
        r13.<init>(r14, r2, r3);
        r11.add(r13);
        goto L_0x0114;
    L_0x0112:
        r11 = r16;
    L_0x0114:
        r0.checkCyclicSubstitution(r9, r11);
        r11.add(r9);
        r13 = r0.resolveVariable(r9, r1, r10, r6);
        if (r13 != 0) goto L_0x0123;
    L_0x0120:
        r13 = r23;
        goto L_0x0124;
    L_0x0124:
        if (r13 == 0) goto L_0x0144;
    L_0x0126:
        r2 = r13.length();
        r1.replace(r10, r6, r13);
        r17 = 1;
        r16 = r0.substitute(r1, r10, r2, r11);
        r21 = r16 + r2;
        r25 = r6 - r10;
        r21 = r21 - r25;
        r12 = r12 + r21;
        r15 = r15 + r21;
        r16 = r26 + r21;
        r14 = r1.buffer;
        r26 = r16;
        goto L_0x0145;
    L_0x0145:
        r2 = r11.size();
        r21 = 1;
        r2 = r2 + -1;
        r11.remove(r2);
        r16 = r11;
        r13 = r26;
        goto L_0x0171;
    L_0x0155:
        r22 = r5;
        r24 = r6;
        r27 = r11;
        r26 = r13;
        r21 = 1;
        r20 = r20 + -1;
        r12 = r12 + r19;
        r2 = r31;
        goto L_0x0073;
    L_0x0167:
        r22 = r5;
        r24 = r6;
        r27 = r11;
        r26 = r13;
        r21 = 1;
    L_0x0171:
        r5 = r22;
        r6 = r24;
        r11 = r27;
        r2 = r31;
        goto L_0x002f;
    L_0x017b:
        r22 = r5;
        r24 = r6;
        r27 = r11;
        r26 = r13;
        r21 = 1;
        if (r27 == 0) goto L_0x018f;
    L_0x0187:
        if (r17 == 0) goto L_0x018c;
    L_0x0189:
        r28 = 1;
        goto L_0x018e;
    L_0x018c:
        r28 = 0;
    L_0x018e:
        return r28;
    L_0x018f:
        return r26;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.text.StrSubstitutor.substitute(org.apache.commons.lang3.text.StrBuilder, int, int, java.util.List):int");
    }

    private void checkCyclicSubstitution(String varName, List<String> priorVariables) {
        if (priorVariables.contains(varName)) {
            StrBuilder buf = new StrBuilder(256);
            buf.append("Infinite loop in property interpolation of ");
            buf.append((String) priorVariables.remove(0));
            buf.append(": ");
            buf.appendWithSeparators((Iterable) priorVariables, "->");
            throw new IllegalStateException(buf.toString());
        }
    }

    protected String resolveVariable(String variableName, StrBuilder buf, int startPos, int endPos) {
        StrLookup<?> resolver = getVariableResolver();
        if (resolver == null) {
            return null;
        }
        return resolver.lookup(variableName);
    }

    public char getEscapeChar() {
        return this.escapeChar;
    }

    public void setEscapeChar(char escapeCharacter) {
        this.escapeChar = escapeCharacter;
    }

    public StrMatcher getVariablePrefixMatcher() {
        return this.prefixMatcher;
    }

    public StrSubstitutor setVariablePrefixMatcher(StrMatcher prefixMatcher) {
        if (prefixMatcher != null) {
            this.prefixMatcher = prefixMatcher;
            return this;
        }
        throw new IllegalArgumentException("Variable prefix matcher must not be null!");
    }

    public StrSubstitutor setVariablePrefix(char prefix) {
        return setVariablePrefixMatcher(StrMatcher.charMatcher(prefix));
    }

    public StrSubstitutor setVariablePrefix(String prefix) {
        if (prefix != null) {
            return setVariablePrefixMatcher(StrMatcher.stringMatcher(prefix));
        }
        throw new IllegalArgumentException("Variable prefix must not be null!");
    }

    public StrMatcher getVariableSuffixMatcher() {
        return this.suffixMatcher;
    }

    public StrSubstitutor setVariableSuffixMatcher(StrMatcher suffixMatcher) {
        if (suffixMatcher != null) {
            this.suffixMatcher = suffixMatcher;
            return this;
        }
        throw new IllegalArgumentException("Variable suffix matcher must not be null!");
    }

    public StrSubstitutor setVariableSuffix(char suffix) {
        return setVariableSuffixMatcher(StrMatcher.charMatcher(suffix));
    }

    public StrSubstitutor setVariableSuffix(String suffix) {
        if (suffix != null) {
            return setVariableSuffixMatcher(StrMatcher.stringMatcher(suffix));
        }
        throw new IllegalArgumentException("Variable suffix must not be null!");
    }

    public StrMatcher getValueDelimiterMatcher() {
        return this.valueDelimiterMatcher;
    }

    public StrSubstitutor setValueDelimiterMatcher(StrMatcher valueDelimiterMatcher) {
        this.valueDelimiterMatcher = valueDelimiterMatcher;
        return this;
    }

    public StrSubstitutor setValueDelimiter(char valueDelimiter) {
        return setValueDelimiterMatcher(StrMatcher.charMatcher(valueDelimiter));
    }

    public StrSubstitutor setValueDelimiter(String valueDelimiter) {
        if (!StringUtils.isEmpty(valueDelimiter)) {
            return setValueDelimiterMatcher(StrMatcher.stringMatcher(valueDelimiter));
        }
        setValueDelimiterMatcher(null);
        return this;
    }

    public StrLookup<?> getVariableResolver() {
        return this.variableResolver;
    }

    public void setVariableResolver(StrLookup<?> variableResolver) {
        this.variableResolver = variableResolver;
    }

    public boolean isEnableSubstitutionInVariables() {
        return this.enableSubstitutionInVariables;
    }

    public void setEnableSubstitutionInVariables(boolean enableSubstitutionInVariables) {
        this.enableSubstitutionInVariables = enableSubstitutionInVariables;
    }

    public boolean isPreserveEscapes() {
        return this.preserveEscapes;
    }

    public void setPreserveEscapes(boolean preserveEscapes) {
        this.preserveEscapes = preserveEscapes;
    }
}

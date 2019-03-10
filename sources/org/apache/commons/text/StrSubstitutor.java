package org.apache.commons.text;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.Validate;

@Deprecated
public class StrSubstitutor {
    public static final char DEFAULT_ESCAPE = '$';
    public static final StrMatcher DEFAULT_PREFIX = StrMatcher.stringMatcher("${");
    public static final StrMatcher DEFAULT_SUFFIX = StrMatcher.stringMatcher("}");
    public static final StrMatcher DEFAULT_VALUE_DELIMITER = StrMatcher.stringMatcher(":-");
    private boolean disableSubstitutionInValues;
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
        this((StrLookup) null, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
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
    private int substitute(org.apache.commons.text.StrBuilder r33, int r34, int r35, java.util.List<java.lang.String> r36) {
        /*
        r32 = this;
        r0 = r32;
        r1 = r33;
        r2 = r34;
        r3 = r35;
        r4 = r32.getVariablePrefixMatcher();
        r5 = r32.getVariableSuffixMatcher();
        r6 = r32.getEscapeChar();
        r7 = r32.getValueDelimiterMatcher();
        r8 = r32.isEnableSubstitutionInVariables();
        r9 = r32.isDisableSubstitutionInValues();
        if (r36 != 0) goto L_0x0024;
    L_0x0022:
        r12 = 1;
        goto L_0x0025;
    L_0x0024:
        r12 = 0;
    L_0x0025:
        r13 = 0;
        r14 = 0;
        r15 = r1.buffer;
        r16 = r2 + r3;
        r17 = r34;
        r18 = r13;
        r13 = r17;
        r17 = r36;
        r31 = r16;
        r16 = r14;
        r14 = r31;
    L_0x0039:
        if (r13 >= r14) goto L_0x01a2;
    L_0x003b:
        r19 = r4.isMatch(r15, r13, r2, r14);
        if (r19 != 0) goto L_0x004f;
    L_0x0041:
        r13 = r13 + 1;
        r29 = r4;
        r23 = r5;
        r25 = r6;
        r28 = r12;
        r22 = 1;
        goto L_0x0198;
    L_0x004f:
        if (r13 <= r2) goto L_0x0079;
    L_0x0051:
        r20 = r13 + -1;
        r11 = r15[r20];
        if (r11 != r6) goto L_0x0079;
    L_0x0057:
        r11 = r0.preserveEscapes;
        if (r11 == 0) goto L_0x005e;
    L_0x005b:
        r13 = r13 + 1;
        goto L_0x0039;
    L_0x005e:
        r11 = r13 + -1;
        r1.deleteCharAt(r11);
        r11 = r1.buffer;
        r16 = r16 + -1;
        r15 = 1;
        r14 = r14 + -1;
        r29 = r4;
        r23 = r5;
        r25 = r6;
        r28 = r12;
        r18 = r15;
        r22 = 1;
        r15 = r11;
        goto L_0x0198;
        r11 = r13;
        r13 = r13 + r19;
        r20 = 0;
        r21 = 0;
    L_0x0081:
        if (r13 >= r14) goto L_0x018c;
    L_0x0083:
        if (r8 == 0) goto L_0x0095;
    L_0x0085:
        r22 = r4.isMatch(r15, r13, r2, r14);
        if (r22 == 0) goto L_0x0094;
    L_0x008b:
        r20 = r4.isMatch(r15, r13, r2, r14);
        r21 = r21 + 1;
        r13 = r13 + r20;
        goto L_0x0081;
    L_0x0094:
        goto L_0x0096;
    L_0x0096:
        r20 = r5.isMatch(r15, r13, r2, r14);
        if (r20 != 0) goto L_0x009f;
    L_0x009c:
        r13 = r13 + 1;
        goto L_0x0081;
    L_0x009f:
        if (r21 != 0) goto L_0x017a;
    L_0x00a1:
        r10 = new java.lang.String;
        r23 = r5;
        r5 = r11 + r19;
        r24 = r13 - r11;
        r25 = r6;
        r6 = r24 - r19;
        r10.<init>(r15, r5, r6);
        r5 = r10;
        if (r8 == 0) goto L_0x00c7;
    L_0x00b3:
        r6 = new org.apache.commons.text.StrBuilder;
        r6.<init>(r5);
        r10 = r6.length();
        r36 = r5;
        r5 = 0;
        r0.substitute(r6, r5, r10);
        r5 = r6.toString();
        goto L_0x00c9;
    L_0x00c7:
        r36 = r5;
    L_0x00c9:
        r13 = r13 + r20;
        r6 = r13;
        r10 = r5;
        r24 = 0;
        if (r7 == 0) goto L_0x0116;
    L_0x00d1:
        r36 = r10;
        r10 = r5.toCharArray();
        r26 = 0;
        r27 = 0;
        r28 = r12;
        r12 = r27;
    L_0x00df:
        r27 = r14;
        r14 = r10.length;
        if (r12 >= r14) goto L_0x0113;
    L_0x00e4:
        if (r8 != 0) goto L_0x00f1;
    L_0x00e6:
        r14 = r10.length;
        r14 = r4.isMatch(r10, r12, r12, r14);
        if (r14 == 0) goto L_0x00f0;
    L_0x00ed:
        r29 = r4;
        goto L_0x011e;
    L_0x00f0:
        goto L_0x00f2;
    L_0x00f2:
        r14 = r7.isMatch(r10, r12);
        if (r14 == 0) goto L_0x010c;
    L_0x00f8:
        r14 = r7.isMatch(r10, r12);
        r29 = r4;
        r4 = 0;
        r22 = r5.substring(r4, r12);
        r4 = r12 + r14;
        r24 = r5.substring(r4);
        r4 = r22;
        goto L_0x0120;
    L_0x010c:
        r29 = r4;
        r12 = r12 + 1;
        r14 = r27;
        goto L_0x00df;
    L_0x0113:
        r29 = r4;
        goto L_0x011e;
    L_0x0116:
        r29 = r4;
        r36 = r10;
        r28 = r12;
        r27 = r14;
    L_0x011e:
        r4 = r36;
    L_0x0120:
        if (r17 != 0) goto L_0x0130;
    L_0x0122:
        r10 = new java.util.ArrayList;
        r10.<init>();
        r12 = new java.lang.String;
        r12.<init>(r15, r2, r3);
        r10.add(r12);
        goto L_0x0132;
    L_0x0130:
        r10 = r17;
    L_0x0132:
        r0.checkCyclicSubstitution(r4, r10);
        r10.add(r4);
        r12 = r0.resolveVariable(r4, r1, r11, r6);
        if (r12 != 0) goto L_0x0141;
    L_0x013e:
        r12 = r24;
        goto L_0x0142;
    L_0x0142:
        if (r12 == 0) goto L_0x0168;
    L_0x0144:
        r14 = r12.length();
        r1.replace(r11, r6, r12);
        r18 = 1;
        r17 = 0;
        if (r9 != 0) goto L_0x0156;
    L_0x0151:
        r17 = r0.substitute(r1, r11, r14, r10);
        goto L_0x0157;
    L_0x0157:
        r22 = r17 + r14;
        r26 = r6 - r11;
        r22 = r22 - r26;
        r13 = r13 + r22;
        r17 = r27 + r22;
        r16 = r16 + r22;
        r15 = r1.buffer;
        r27 = r17;
        goto L_0x0169;
        r14 = r10.size();
        r22 = 1;
        r14 = r14 + -1;
        r10.remove(r14);
        r17 = r10;
        r14 = r27;
        goto L_0x0198;
    L_0x017a:
        r29 = r4;
        r23 = r5;
        r25 = r6;
        r28 = r12;
        r27 = r14;
        r22 = 1;
        r21 = r21 + -1;
        r13 = r13 + r20;
        goto L_0x0081;
    L_0x018c:
        r29 = r4;
        r23 = r5;
        r25 = r6;
        r28 = r12;
        r27 = r14;
        r22 = 1;
    L_0x0198:
        r5 = r23;
        r6 = r25;
        r12 = r28;
        r4 = r29;
        goto L_0x0039;
    L_0x01a2:
        r29 = r4;
        r23 = r5;
        r25 = r6;
        r28 = r12;
        r27 = r14;
        r22 = 1;
        if (r28 == 0) goto L_0x01b8;
    L_0x01b0:
        if (r18 == 0) goto L_0x01b5;
    L_0x01b2:
        r30 = 1;
        goto L_0x01b7;
    L_0x01b5:
        r30 = 0;
    L_0x01b7:
        return r30;
    L_0x01b8:
        return r16;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.text.StrSubstitutor.substitute(org.apache.commons.text.StrBuilder, int, int, java.util.List):int");
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
        Validate.isTrue(prefixMatcher != null, "Variable prefix matcher must not be null!", new Object[0]);
        this.prefixMatcher = prefixMatcher;
        return this;
    }

    public StrSubstitutor setVariablePrefix(char prefix) {
        return setVariablePrefixMatcher(StrMatcher.charMatcher(prefix));
    }

    public StrSubstitutor setVariablePrefix(String prefix) {
        Validate.isTrue(prefix != null, "Variable prefix must not be null!", new Object[0]);
        return setVariablePrefixMatcher(StrMatcher.stringMatcher(prefix));
    }

    public StrMatcher getVariableSuffixMatcher() {
        return this.suffixMatcher;
    }

    public StrSubstitutor setVariableSuffixMatcher(StrMatcher suffixMatcher) {
        Validate.isTrue(suffixMatcher != null, "Variable suffix matcher must not be null!", new Object[0]);
        this.suffixMatcher = suffixMatcher;
        return this;
    }

    public StrSubstitutor setVariableSuffix(char suffix) {
        return setVariableSuffixMatcher(StrMatcher.charMatcher(suffix));
    }

    public StrSubstitutor setVariableSuffix(String suffix) {
        Validate.isTrue(suffix != null, "Variable suffix must not be null!", new Object[0]);
        return setVariableSuffixMatcher(StrMatcher.stringMatcher(suffix));
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
        if (valueDelimiter != null) {
            if (valueDelimiter.length() != 0) {
                return setValueDelimiterMatcher(StrMatcher.stringMatcher(valueDelimiter));
            }
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

    public boolean isDisableSubstitutionInValues() {
        return this.disableSubstitutionInValues;
    }

    public void setDisableSubstitutionInValues(boolean disableSubstitutionInValues) {
        this.disableSubstitutionInValues = disableSubstitutionInValues;
    }

    public boolean isPreserveEscapes() {
        return this.preserveEscapes;
    }

    public void setPreserveEscapes(boolean preserveEscapes) {
        this.preserveEscapes = preserveEscapes;
    }
}

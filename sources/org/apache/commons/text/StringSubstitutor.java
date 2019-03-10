package org.apache.commons.text;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.lookup.StringLookup;
import org.apache.commons.text.lookup.StringLookupFactory;
import org.apache.commons.text.matcher.StringMatcher;
import org.apache.commons.text.matcher.StringMatcherFactory;

public class StringSubstitutor {
    public static final char DEFAULT_ESCAPE = '$';
    public static final StringMatcher DEFAULT_PREFIX = StringMatcherFactory.INSTANCE.stringMatcher("${");
    public static final StringMatcher DEFAULT_SUFFIX = StringMatcherFactory.INSTANCE.stringMatcher("}");
    public static final StringMatcher DEFAULT_VALUE_DELIMITER = StringMatcherFactory.INSTANCE.stringMatcher(":-");
    private boolean disableSubstitutionInValues;
    private boolean enableSubstitutionInVariables;
    private char escapeChar;
    private StringMatcher prefixMatcher;
    private boolean preserveEscapes;
    private StringMatcher suffixMatcher;
    private StringMatcher valueDelimiterMatcher;
    private StringLookup variableResolver;

    public static <V> String replace(Object source, Map<String, V> valueMap) {
        return new StringSubstitutor((Map) valueMap).replace(source);
    }

    public static <V> String replace(Object source, Map<String, V> valueMap, String prefix, String suffix) {
        return new StringSubstitutor(valueMap, prefix, suffix).replace(source);
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
        return new StringSubstitutor(StringLookupFactory.INSTANCE.systemPropertyStringLookup()).replace(source);
    }

    public StringSubstitutor() {
        this((StringLookup) null, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public <V> StringSubstitutor(Map<String, V> valueMap) {
        this(StringLookupFactory.INSTANCE.mapStringLookup(valueMap), DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public <V> StringSubstitutor(Map<String, V> valueMap, String prefix, String suffix) {
        this(StringLookupFactory.INSTANCE.mapStringLookup(valueMap), prefix, suffix, '$');
    }

    public <V> StringSubstitutor(Map<String, V> valueMap, String prefix, String suffix, char escape) {
        this(StringLookupFactory.INSTANCE.mapStringLookup(valueMap), prefix, suffix, escape);
    }

    public <V> StringSubstitutor(Map<String, V> valueMap, String prefix, String suffix, char escape, String valueDelimiter) {
        this(StringLookupFactory.INSTANCE.mapStringLookup(valueMap), prefix, suffix, escape, valueDelimiter);
    }

    public StringSubstitutor(StringLookup variableResolver) {
        this(variableResolver, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public StringSubstitutor(StringLookup variableResolver, String prefix, String suffix, char escape) {
        this.preserveEscapes = false;
        setVariableResolver(variableResolver);
        setVariablePrefix(prefix);
        setVariableSuffix(suffix);
        setEscapeChar(escape);
        setValueDelimiterMatcher(DEFAULT_VALUE_DELIMITER);
    }

    public StringSubstitutor(StringLookup variableResolver, String prefix, String suffix, char escape, String valueDelimiter) {
        this.preserveEscapes = false;
        setVariableResolver(variableResolver);
        setVariablePrefix(prefix);
        setVariableSuffix(suffix);
        setEscapeChar(escape);
        setValueDelimiter(valueDelimiter);
    }

    public StringSubstitutor(StringLookup variableResolver, StringMatcher prefixMatcher, StringMatcher suffixMatcher, char escape) {
        this(variableResolver, prefixMatcher, suffixMatcher, escape, DEFAULT_VALUE_DELIMITER);
    }

    public StringSubstitutor(StringLookup variableResolver, StringMatcher prefixMatcher, StringMatcher suffixMatcher, char escape, StringMatcher valueDelimiterMatcher) {
        this.preserveEscapes = false;
        setVariableResolver(variableResolver);
        setVariablePrefixMatcher(prefixMatcher);
        setVariableSuffixMatcher(suffixMatcher);
        setEscapeChar(escape);
        setValueDelimiterMatcher(valueDelimiterMatcher);
    }

    private void checkCyclicSubstitution(String varName, List<String> priorVariables) {
        if (priorVariables.contains(varName)) {
            TextStringBuilder buf = new TextStringBuilder(256);
            buf.append("Infinite loop in property interpolation of ");
            buf.append((String) priorVariables.remove(0));
            buf.append(": ");
            buf.appendWithSeparators((Iterable) priorVariables, "->");
            throw new IllegalStateException(buf.toString());
        }
    }

    public char getEscapeChar() {
        return this.escapeChar;
    }

    public StringLookup getStringLookup() {
        return this.variableResolver;
    }

    public StringMatcher getValueDelimiterMatcher() {
        return this.valueDelimiterMatcher;
    }

    public StringMatcher getVariablePrefixMatcher() {
        return this.prefixMatcher;
    }

    public StringMatcher getVariableSuffixMatcher() {
        return this.suffixMatcher;
    }

    public boolean isDisableSubstitutionInValues() {
        return this.disableSubstitutionInValues;
    }

    public boolean isEnableSubstitutionInVariables() {
        return this.enableSubstitutionInVariables;
    }

    public boolean isPreserveEscapes() {
        return this.preserveEscapes;
    }

    public String replace(char[] source) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(source.length).append(source);
        substitute(buf, 0, source.length);
        return buf.toString();
    }

    public String replace(char[] source, int offset, int length) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
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
        TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(Object source) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder().append(source);
        substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public String replace(TextStringBuilder source) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(source.length()).append(source);
        substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public String replace(TextStringBuilder source, int offset, int length) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(String source) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(source);
        if (substitute(buf, 0, source.length())) {
            return buf.toString();
        }
        return source;
    }

    public String replace(String source, int offset, int length) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        if (substitute(buf, 0, length)) {
            return buf.toString();
        }
        return source.substring(offset, offset + length);
    }

    public String replace(StringBuffer source) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(source.length()).append(source);
        substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public String replace(StringBuffer source, int offset, int length) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        substitute(buf, 0, length);
        return buf.toString();
    }

    public boolean replaceIn(TextStringBuilder source) {
        if (source == null) {
            return false;
        }
        return substitute(source, 0, source.length());
    }

    public boolean replaceIn(TextStringBuilder source, int offset, int length) {
        if (source == null) {
            return false;
        }
        return substitute(source, offset, length);
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
        TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
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
        TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        if (!substitute(buf, 0, length)) {
            return false;
        }
        source.replace(offset, offset + length, buf.toString());
        return true;
    }

    protected String resolveVariable(String variableName, TextStringBuilder buf, int startPos, int endPos) {
        StringLookup resolver = getStringLookup();
        if (resolver == null) {
            return null;
        }
        return resolver.lookup(variableName);
    }

    public StringSubstitutor setDisableSubstitutionInValues(boolean disableSubstitutionInValues) {
        this.disableSubstitutionInValues = disableSubstitutionInValues;
        return this;
    }

    public StringSubstitutor setEnableSubstitutionInVariables(boolean enableSubstitutionInVariables) {
        this.enableSubstitutionInVariables = enableSubstitutionInVariables;
        return this;
    }

    public StringSubstitutor setEscapeChar(char escapeCharacter) {
        this.escapeChar = escapeCharacter;
        return this;
    }

    public StringSubstitutor setPreserveEscapes(boolean preserveEscapes) {
        this.preserveEscapes = preserveEscapes;
        return this;
    }

    public StringSubstitutor setValueDelimiter(char valueDelimiter) {
        return setValueDelimiterMatcher(StringMatcherFactory.INSTANCE.charMatcher(valueDelimiter));
    }

    public StringSubstitutor setValueDelimiter(String valueDelimiter) {
        if (valueDelimiter != null) {
            if (valueDelimiter.length() != 0) {
                return setValueDelimiterMatcher(StringMatcherFactory.INSTANCE.stringMatcher(valueDelimiter));
            }
        }
        setValueDelimiterMatcher(null);
        return this;
    }

    public StringSubstitutor setValueDelimiterMatcher(StringMatcher valueDelimiterMatcher) {
        this.valueDelimiterMatcher = valueDelimiterMatcher;
        return this;
    }

    public StringSubstitutor setVariablePrefix(char prefix) {
        return setVariablePrefixMatcher(StringMatcherFactory.INSTANCE.charMatcher(prefix));
    }

    public StringSubstitutor setVariablePrefix(String prefix) {
        Validate.isTrue(prefix != null, "Variable prefix must not be null!", new Object[0]);
        return setVariablePrefixMatcher(StringMatcherFactory.INSTANCE.stringMatcher(prefix));
    }

    public StringSubstitutor setVariablePrefixMatcher(StringMatcher prefixMatcher) {
        Validate.isTrue(prefixMatcher != null, "Variable prefix matcher must not be null!", new Object[0]);
        this.prefixMatcher = prefixMatcher;
        return this;
    }

    public StringSubstitutor setVariableResolver(StringLookup variableResolver) {
        this.variableResolver = variableResolver;
        return this;
    }

    public StringSubstitutor setVariableSuffix(char suffix) {
        return setVariableSuffixMatcher(StringMatcherFactory.INSTANCE.charMatcher(suffix));
    }

    public StringSubstitutor setVariableSuffix(String suffix) {
        Validate.isTrue(suffix != null, "Variable suffix must not be null!", new Object[0]);
        return setVariableSuffixMatcher(StringMatcherFactory.INSTANCE.stringMatcher(suffix));
    }

    public StringSubstitutor setVariableSuffixMatcher(StringMatcher suffixMatcher) {
        Validate.isTrue(suffixMatcher != null, "Variable suffix matcher must not be null!", new Object[0]);
        this.suffixMatcher = suffixMatcher;
        return this;
    }

    protected boolean substitute(TextStringBuilder buf, int offset, int length) {
        return substitute(buf, offset, length, null) > 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int substitute(org.apache.commons.text.TextStringBuilder r33, int r34, int r35, java.util.List<java.lang.String> r36) {
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
        if (r13 >= r14) goto L_0x01a1;
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
        goto L_0x0197;
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
        goto L_0x0197;
        r11 = r13;
        r13 = r13 + r19;
        r20 = 0;
        r21 = 0;
    L_0x0081:
        if (r13 >= r14) goto L_0x018b;
    L_0x0083:
        if (r8 == 0) goto L_0x0094;
    L_0x0085:
        r22 = r4.isMatch(r15, r13, r2, r14);
        if (r22 == 0) goto L_0x0094;
    L_0x008b:
        r20 = r4.isMatch(r15, r13, r2, r14);
        r21 = r21 + 1;
        r13 = r13 + r20;
        goto L_0x0081;
        r20 = r5.isMatch(r15, r13, r2, r14);
        if (r20 != 0) goto L_0x009e;
    L_0x009b:
        r13 = r13 + 1;
        goto L_0x0081;
    L_0x009e:
        if (r21 != 0) goto L_0x0179;
    L_0x00a0:
        r10 = new java.lang.String;
        r23 = r5;
        r5 = r11 + r19;
        r24 = r13 - r11;
        r25 = r6;
        r6 = r24 - r19;
        r10.<init>(r15, r5, r6);
        r5 = r10;
        if (r8 == 0) goto L_0x00c6;
    L_0x00b2:
        r6 = new org.apache.commons.text.TextStringBuilder;
        r6.<init>(r5);
        r10 = r6.length();
        r36 = r5;
        r5 = 0;
        r0.substitute(r6, r5, r10);
        r5 = r6.toString();
        goto L_0x00c8;
    L_0x00c6:
        r36 = r5;
    L_0x00c8:
        r13 = r13 + r20;
        r6 = r13;
        r10 = r5;
        r24 = 0;
        if (r7 == 0) goto L_0x0116;
    L_0x00d0:
        r36 = r10;
        r10 = r5.toCharArray();
        r26 = 0;
        r27 = 0;
        r28 = r12;
        r12 = r27;
    L_0x00de:
        r27 = r14;
        r14 = r10.length;
        if (r12 >= r14) goto L_0x0113;
    L_0x00e3:
        if (r8 != 0) goto L_0x00ef;
    L_0x00e5:
        r14 = r10.length;
        r14 = r4.isMatch(r10, r12, r12, r14);
        if (r14 == 0) goto L_0x00ef;
    L_0x00ec:
        r29 = r4;
        goto L_0x011e;
        r14 = r10.length;
        r29 = r4;
        r4 = 0;
        r14 = r7.isMatch(r10, r12, r4, r14);
        if (r14 == 0) goto L_0x010c;
    L_0x00fa:
        r14 = r10.length;
        r14 = r7.isMatch(r10, r12, r4, r14);
        r22 = r5.substring(r4, r12);
        r4 = r12 + r14;
        r24 = r5.substring(r4);
        r4 = r22;
        goto L_0x0120;
    L_0x010c:
        r12 = r12 + 1;
        r14 = r27;
        r4 = r29;
        goto L_0x00de;
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
    L_0x0169:
        r14 = r10.size();
        r22 = 1;
        r14 = r14 + -1;
        r10.remove(r14);
        r17 = r10;
        r14 = r27;
        goto L_0x0197;
    L_0x0179:
        r29 = r4;
        r23 = r5;
        r25 = r6;
        r28 = r12;
        r27 = r14;
        r22 = 1;
        r21 = r21 + -1;
        r13 = r13 + r20;
        goto L_0x0081;
    L_0x018b:
        r29 = r4;
        r23 = r5;
        r25 = r6;
        r28 = r12;
        r27 = r14;
        r22 = 1;
    L_0x0197:
        r5 = r23;
        r6 = r25;
        r12 = r28;
        r4 = r29;
        goto L_0x0039;
    L_0x01a1:
        r29 = r4;
        r23 = r5;
        r25 = r6;
        r28 = r12;
        r27 = r14;
        r22 = 1;
        if (r28 == 0) goto L_0x01b7;
    L_0x01af:
        if (r18 == 0) goto L_0x01b4;
    L_0x01b1:
        r30 = 1;
        goto L_0x01b6;
    L_0x01b4:
        r30 = 0;
    L_0x01b6:
        return r30;
    L_0x01b7:
        return r16;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.text.StringSubstitutor.substitute(org.apache.commons.text.TextStringBuilder, int, int, java.util.List):int");
    }
}

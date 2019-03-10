package org.apache.commons.lang3.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

public class FastDateParser implements DateParser, Serializable {
    private static final Strategy ABBREVIATED_YEAR_STRATEGY = new NumberStrategy(1) {
        int modify(FastDateParser parser, int iValue) {
            return iValue < 100 ? parser.adjustYear(iValue) : iValue;
        }
    };
    private static final Strategy DAY_OF_MONTH_STRATEGY = new NumberStrategy(5);
    private static final Strategy DAY_OF_WEEK_IN_MONTH_STRATEGY = new NumberStrategy(8);
    private static final Strategy DAY_OF_WEEK_STRATEGY = new NumberStrategy(7) {
        int modify(FastDateParser parser, int iValue) {
            return iValue != 7 ? iValue + 1 : 1;
        }
    };
    private static final Strategy DAY_OF_YEAR_STRATEGY = new NumberStrategy(6);
    private static final Strategy HOUR12_STRATEGY = new NumberStrategy(10) {
        int modify(FastDateParser parser, int iValue) {
            return iValue == 12 ? 0 : iValue;
        }
    };
    private static final Strategy HOUR24_OF_DAY_STRATEGY = new NumberStrategy(11) {
        int modify(FastDateParser parser, int iValue) {
            return iValue == 24 ? 0 : iValue;
        }
    };
    private static final Strategy HOUR_OF_DAY_STRATEGY = new NumberStrategy(11);
    private static final Strategy HOUR_STRATEGY = new NumberStrategy(10);
    static final Locale JAPANESE_IMPERIAL = new Locale("ja", "JP", "JP");
    private static final Strategy LITERAL_YEAR_STRATEGY = new NumberStrategy(1);
    private static final Comparator<String> LONGER_FIRST_LOWERCASE = new C11661();
    private static final Strategy MILLISECOND_STRATEGY = new NumberStrategy(14);
    private static final Strategy MINUTE_STRATEGY = new NumberStrategy(12);
    private static final Strategy NUMBER_MONTH_STRATEGY = new NumberStrategy(2) {
        int modify(FastDateParser parser, int iValue) {
            return iValue - 1;
        }
    };
    private static final Strategy SECOND_STRATEGY = new NumberStrategy(13);
    private static final Strategy WEEK_OF_MONTH_STRATEGY = new NumberStrategy(4);
    private static final Strategy WEEK_OF_YEAR_STRATEGY = new NumberStrategy(3);
    private static final ConcurrentMap<Locale, Strategy>[] caches = new ConcurrentMap[17];
    private static final long serialVersionUID = 3;
    private final int century;
    private final Locale locale;
    private final String pattern;
    private transient List<StrategyAndWidth> patterns;
    private final int startYear;
    private final TimeZone timeZone;

    /* renamed from: org.apache.commons.lang3.time.FastDateParser$1 */
    static class C11661 implements Comparator<String> {
        C11661() {
        }

        public int compare(String left, String right) {
            return right.compareTo(left);
        }
    }

    private static abstract class Strategy {
        abstract boolean parse(FastDateParser fastDateParser, Calendar calendar, String str, ParsePosition parsePosition, int i);

        private Strategy() {
        }

        boolean isNumber() {
            return false;
        }
    }

    private static class StrategyAndWidth {
        final Strategy strategy;
        final int width;

        StrategyAndWidth(Strategy strategy, int width) {
            this.strategy = strategy;
            this.width = width;
        }

        int getMaxWidth(ListIterator<StrategyAndWidth> lt) {
            int i = 0;
            if (this.strategy.isNumber()) {
                if (lt.hasNext()) {
                    Strategy nextStrategy = ((StrategyAndWidth) lt.next()).strategy;
                    lt.previous();
                    if (nextStrategy.isNumber()) {
                        i = this.width;
                    }
                    return i;
                }
            }
            return 0;
        }
    }

    private class StrategyParser {
        private int currentIdx;
        private final Calendar definingCalendar;

        private org.apache.commons.lang3.time.FastDateParser.StrategyAndWidth literal() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:25:0x007c in {7, 13, 15, 16, 17, 18, 19, 22, 24} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r7 = this;
            r0 = 0;
            r1 = new java.lang.StringBuilder;
            r1.<init>();
        L_0x0006:
            r2 = r7.currentIdx;
            r3 = org.apache.commons.lang3.time.FastDateParser.this;
            r3 = r3.pattern;
            r3 = r3.length();
            if (r2 >= r3) goto L_0x005e;
        L_0x0014:
            r2 = org.apache.commons.lang3.time.FastDateParser.this;
            r2 = r2.pattern;
            r3 = r7.currentIdx;
            r2 = r2.charAt(r3);
            if (r0 != 0) goto L_0x0029;
        L_0x0022:
            r3 = org.apache.commons.lang3.time.FastDateParser.isFormatLetter(r2);
            if (r3 == 0) goto L_0x0029;
        L_0x0028:
            goto L_0x005f;
            r3 = 39;
            r4 = 1;
            if (r2 != r3) goto L_0x0054;
        L_0x002f:
            r5 = r7.currentIdx;
            r5 = r5 + r4;
            r7.currentIdx = r5;
            r6 = org.apache.commons.lang3.time.FastDateParser.this;
            r6 = r6.pattern;
            r6 = r6.length();
            if (r5 == r6) goto L_0x004e;
        L_0x0040:
            r5 = org.apache.commons.lang3.time.FastDateParser.this;
            r5 = r5.pattern;
            r6 = r7.currentIdx;
            r5 = r5.charAt(r6);
            if (r5 == r3) goto L_0x0054;
        L_0x004e:
            if (r0 != 0) goto L_0x0051;
        L_0x0050:
            goto L_0x0052;
        L_0x0051:
            r4 = 0;
        L_0x0052:
            r0 = r4;
            goto L_0x0006;
            r3 = r7.currentIdx;
            r3 = r3 + r4;
            r7.currentIdx = r3;
            r1.append(r2);
            goto L_0x0006;
        L_0x005f:
            if (r0 != 0) goto L_0x0074;
        L_0x0061:
            r2 = r1.toString();
            r3 = new org.apache.commons.lang3.time.FastDateParser$StrategyAndWidth;
            r4 = new org.apache.commons.lang3.time.FastDateParser$CopyQuotedStrategy;
            r4.<init>(r2);
            r5 = r2.length();
            r3.<init>(r4, r5);
            return r3;
        L_0x0074:
            r2 = new java.lang.IllegalArgumentException;
            r3 = "Unterminated quote";
            r2.<init>(r3);
            throw r2;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.FastDateParser.StrategyParser.literal():org.apache.commons.lang3.time.FastDateParser$StrategyAndWidth");
        }

        StrategyParser(Calendar definingCalendar) {
            this.definingCalendar = definingCalendar;
        }

        StrategyAndWidth getNextStrategy() {
            if (this.currentIdx >= FastDateParser.this.pattern.length()) {
                return null;
            }
            char c = FastDateParser.this.pattern.charAt(this.currentIdx);
            if (FastDateParser.isFormatLetter(c)) {
                return letterPattern(c);
            }
            return literal();
        }

        private StrategyAndWidth letterPattern(char c) {
            int i;
            int begin = this.currentIdx;
            while (true) {
                i = this.currentIdx + 1;
                this.currentIdx = i;
                if (i >= FastDateParser.this.pattern.length()) {
                    break;
                } else if (FastDateParser.this.pattern.charAt(this.currentIdx) != c) {
                    break;
                }
                i = this.currentIdx - begin;
                return new StrategyAndWidth(FastDateParser.this.getStrategy(c, i, this.definingCalendar), i);
            }
            i = this.currentIdx - begin;
            return new StrategyAndWidth(FastDateParser.this.getStrategy(c, i, this.definingCalendar), i);
        }
    }

    private static class CopyQuotedStrategy extends Strategy {
        private final String formatField;

        CopyQuotedStrategy(String formatField) {
            super();
            this.formatField = formatField;
        }

        boolean isNumber() {
            return false;
        }

        boolean parse(FastDateParser parser, Calendar calendar, String source, ParsePosition pos, int maxWidth) {
            int idx = 0;
            while (idx < this.formatField.length()) {
                int sIdx = pos.getIndex() + idx;
                if (sIdx == source.length()) {
                    pos.setErrorIndex(sIdx);
                    return false;
                } else if (this.formatField.charAt(idx) != source.charAt(sIdx)) {
                    pos.setErrorIndex(sIdx);
                    return false;
                } else {
                    idx++;
                }
            }
            pos.setIndex(this.formatField.length() + pos.getIndex());
            return true;
        }
    }

    private static class NumberStrategy extends Strategy {
        private final int field;

        NumberStrategy(int field) {
            super();
            this.field = field;
        }

        boolean isNumber() {
            return true;
        }

        boolean parse(FastDateParser parser, Calendar calendar, String source, ParsePosition pos, int maxWidth) {
            int end;
            int idx = pos.getIndex();
            int last = source.length();
            if (maxWidth == 0) {
                while (idx < last) {
                    if (!Character.isWhitespace(source.charAt(idx))) {
                        break;
                    }
                    idx++;
                }
                pos.setIndex(idx);
            } else {
                end = idx + maxWidth;
                if (last > end) {
                    last = end;
                }
            }
            while (idx < last) {
                if (!Character.isDigit(source.charAt(idx))) {
                    break;
                }
                idx++;
            }
            if (pos.getIndex() == idx) {
                pos.setErrorIndex(idx);
                return false;
            }
            end = Integer.parseInt(source.substring(pos.getIndex(), idx));
            pos.setIndex(idx);
            calendar.set(this.field, modify(parser, end));
            return true;
        }

        int modify(FastDateParser parser, int iValue) {
            return iValue;
        }
    }

    private static abstract class PatternStrategy extends Strategy {
        private Pattern pattern;

        abstract void setCalendar(FastDateParser fastDateParser, Calendar calendar, String str);

        private PatternStrategy() {
            super();
        }

        void createPattern(StringBuilder regex) {
            createPattern(regex.toString());
        }

        void createPattern(String regex) {
            this.pattern = Pattern.compile(regex);
        }

        boolean isNumber() {
            return false;
        }

        boolean parse(FastDateParser parser, Calendar calendar, String source, ParsePosition pos, int maxWidth) {
            Matcher matcher = this.pattern.matcher(source.substring(pos.getIndex()));
            if (matcher.lookingAt()) {
                pos.setIndex(pos.getIndex() + matcher.end(1));
                setCalendar(parser, calendar, matcher.group(1));
                return true;
            }
            pos.setErrorIndex(pos.getIndex());
            return false;
        }
    }

    private static class CaseInsensitiveTextStrategy extends PatternStrategy {
        private final int field;
        private final Map<String, Integer> lKeyValues;
        final Locale locale;

        CaseInsensitiveTextStrategy(int field, Calendar definingCalendar, Locale locale) {
            super();
            this.field = field;
            this.locale = locale;
            StringBuilder regex = new StringBuilder();
            regex.append("((?iu)");
            this.lKeyValues = FastDateParser.appendDisplayNames(definingCalendar, locale, field, regex);
            regex.setLength(regex.length() - 1);
            regex.append(")");
            createPattern(regex);
        }

        void setCalendar(FastDateParser parser, Calendar cal, String value) {
            cal.set(this.field, ((Integer) this.lKeyValues.get(value.toLowerCase(this.locale))).intValue());
        }
    }

    private static class ISO8601TimeZoneStrategy extends PatternStrategy {
        private static final Strategy ISO_8601_1_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}))");
        private static final Strategy ISO_8601_2_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}\\d{2}))");
        private static final Strategy ISO_8601_3_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}(?::)\\d{2}))");

        ISO8601TimeZoneStrategy(String pattern) {
            super();
            createPattern(pattern);
        }

        void setCalendar(FastDateParser parser, Calendar cal, String value) {
            cal.setTimeZone(FastTimeZone.getGmtTimeZone(value));
        }

        static Strategy getStrategy(int tokenLen) {
            switch (tokenLen) {
                case 1:
                    return ISO_8601_1_STRATEGY;
                case 2:
                    return ISO_8601_2_STRATEGY;
                case 3:
                    return ISO_8601_3_STRATEGY;
                default:
                    throw new IllegalArgumentException("invalid number of X");
            }
        }
    }

    static class TimeZoneStrategy extends PatternStrategy {
        private static final String GMT_OPTION = "GMT[+-]\\d{1,2}:\\d{2}";
        private static final int ID = 0;
        private static final String RFC_822_TIME_ZONE = "[+-]\\d{4}";
        private final Locale locale;
        private final Map<String, TzInfo> tzNames = new HashMap();

        private static class TzInfo {
            int dstOffset;
            TimeZone zone;

            TzInfo(TimeZone tz, boolean useDst) {
                this.zone = tz;
                this.dstOffset = useDst ? tz.getDSTSavings() : 0;
            }
        }

        TimeZoneStrategy(Locale locale) {
            super();
            this.locale = locale;
            StringBuilder sb = new StringBuilder();
            sb.append("((?iu)[+-]\\d{4}|GMT[+-]\\d{1,2}:\\d{2}");
            Set<String> sorted = new TreeSet(FastDateParser.LONGER_FIRST_LOWERCASE);
            for (String[] zoneNames : DateFormatSymbols.getInstance(locale).getZoneStrings()) {
                String tzId = zoneNames[0];
                if (!tzId.equalsIgnoreCase(TimeZones.GMT_ID)) {
                    TimeZone tz = TimeZone.getTimeZone(tzId);
                    TzInfo tzInfo = new TzInfo(tz, false);
                    TzInfo standard = tzInfo;
                    for (int i = 1; i < zoneNames.length; i++) {
                        if (i == 3) {
                            tzInfo = new TzInfo(tz, true);
                        } else if (i == 5) {
                            tzInfo = standard;
                        }
                        if (zoneNames[i] != null) {
                            String key = zoneNames[i].toLowerCase(locale);
                            if (sorted.add(key)) {
                                this.tzNames.put(key, tzInfo);
                            }
                        }
                    }
                }
            }
            for (String zoneName : sorted) {
                sb.append('|');
                FastDateParser.simpleQuote(sb, zoneName);
            }
            sb.append(")");
            createPattern(sb);
        }

        void setCalendar(FastDateParser parser, Calendar cal, String timeZone) {
            TimeZone tz = FastTimeZone.getGmtTimeZone(timeZone);
            if (tz != null) {
                cal.setTimeZone(tz);
                return;
            }
            TzInfo tzInfo = (TzInfo) this.tzNames.get(timeZone.toLowerCase(this.locale));
            cal.set(16, tzInfo.dstOffset);
            cal.set(15, tzInfo.zone.getRawOffset());
        }
    }

    protected FastDateParser(String pattern, TimeZone timeZone, Locale locale) {
        this(pattern, timeZone, locale, null);
    }

    protected FastDateParser(String pattern, TimeZone timeZone, Locale locale, Date centuryStart) {
        int centuryStartYear;
        this.pattern = pattern;
        this.timeZone = timeZone;
        this.locale = locale;
        Calendar definingCalendar = Calendar.getInstance(timeZone, locale);
        if (centuryStart != null) {
            definingCalendar.setTime(centuryStart);
            centuryStartYear = definingCalendar.get(1);
        } else if (locale.equals(JAPANESE_IMPERIAL)) {
            centuryStartYear = 0;
        } else {
            definingCalendar.setTime(new Date());
            centuryStartYear = definingCalendar.get(1) - 80;
        }
        this.century = (centuryStartYear / 100) * 100;
        this.startYear = centuryStartYear - this.century;
        init(definingCalendar);
    }

    private void init(Calendar definingCalendar) {
        this.patterns = new ArrayList();
        StrategyParser fm = new StrategyParser(definingCalendar);
        while (true) {
            StrategyAndWidth field = fm.getNextStrategy();
            if (field != null) {
                this.patterns.add(field);
            } else {
                return;
            }
        }
    }

    private static boolean isFormatLetter(char c) {
        if (c >= 'A') {
            if (c > 'Z') {
            }
        }
        return c >= 'a' && c <= 'z';
    }

    public String getPattern() {
        return this.pattern;
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof FastDateParser)) {
            return false;
        }
        FastDateParser other = (FastDateParser) obj;
        if (this.pattern.equals(other.pattern) && this.timeZone.equals(other.timeZone) && this.locale.equals(other.locale)) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return this.pattern.hashCode() + ((this.timeZone.hashCode() + (this.locale.hashCode() * 13)) * 13);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FastDateParser[");
        stringBuilder.append(this.pattern);
        stringBuilder.append(",");
        stringBuilder.append(this.locale);
        stringBuilder.append(",");
        stringBuilder.append(this.timeZone.getID());
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        init(Calendar.getInstance(this.timeZone, this.locale));
    }

    public Object parseObject(String source) throws ParseException {
        return parse(source);
    }

    public Date parse(String source) throws ParseException {
        ParsePosition pp = new ParsePosition(0);
        Date date = parse(source, pp);
        if (date != null) {
            return date;
        }
        if (this.locale.equals(JAPANESE_IMPERIAL)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(The ");
            stringBuilder.append(this.locale);
            stringBuilder.append(" locale does not support dates before 1868 AD)\n");
            stringBuilder.append("Unparseable date: \"");
            stringBuilder.append(source);
            throw new ParseException(stringBuilder.toString(), pp.getErrorIndex());
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("Unparseable date: ");
        stringBuilder.append(source);
        throw new ParseException(stringBuilder.toString(), pp.getErrorIndex());
    }

    public Object parseObject(String source, ParsePosition pos) {
        return parse(source, pos);
    }

    public Date parse(String source, ParsePosition pos) {
        Calendar cal = Calendar.getInstance(this.timeZone, this.locale);
        cal.clear();
        return parse(source, pos, cal) ? cal.getTime() : null;
    }

    public boolean parse(String source, ParsePosition pos, Calendar calendar) {
        ListIterator<StrategyAndWidth> lt = this.patterns.listIterator();
        while (lt.hasNext()) {
            StrategyAndWidth strategyAndWidth = (StrategyAndWidth) lt.next();
            if (!strategyAndWidth.strategy.parse(this, calendar, source, pos, strategyAndWidth.getMaxWidth(lt))) {
                return false;
            }
        }
        return true;
    }

    private static StringBuilder simpleQuote(StringBuilder sb, String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '$':
                case '(':
                case ')':
                case '*':
                case '+':
                case '.':
                case '?':
                case '[':
                case '\\':
                case '^':
                case '{':
                case '|':
                    sb.append(IOUtils.DIR_SEPARATOR_WINDOWS);
                    break;
                default:
                    break;
            }
            sb.append(c);
        }
        return sb;
    }

    private static Map<String, Integer> appendDisplayNames(Calendar cal, Locale locale, int field, StringBuilder regex) {
        Map<String, Integer> values = new HashMap();
        Map<String, Integer> displayNames = cal.getDisplayNames(field, null, locale);
        TreeSet<String> sorted = new TreeSet(LONGER_FIRST_LOWERCASE);
        for (Entry<String, Integer> displayName : displayNames.entrySet()) {
            String key = ((String) displayName.getKey()).toLowerCase(locale);
            if (sorted.add(key)) {
                values.put(key, displayName.getValue());
            }
        }
        Iterator i$ = sorted.iterator();
        while (i$.hasNext()) {
            simpleQuote(regex, (String) i$.next()).append('|');
        }
        return values;
    }

    private int adjustYear(int twoDigitYear) {
        int trial = this.century + twoDigitYear;
        return twoDigitYear >= this.startYear ? trial : trial + 100;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.apache.commons.lang3.time.FastDateParser.Strategy getStrategy(char r4, int r5, java.util.Calendar r6) {
        /*
        r3 = this;
        switch(r4) {
            case 68: goto L_0x0088;
            case 69: goto L_0x0082;
            case 70: goto L_0x007f;
            case 71: goto L_0x0079;
            case 72: goto L_0x0076;
            default: goto L_0x0003;
        };
    L_0x0003:
        r0 = 2;
        switch(r4) {
            case 87: goto L_0x0073;
            case 88: goto L_0x006e;
            case 89: goto L_0x0066;
            case 90: goto L_0x0057;
            default: goto L_0x0007;
        };
    L_0x0007:
        switch(r4) {
            case 121: goto L_0x0066;
            case 122: goto L_0x0056;
            default: goto L_0x000a;
        };
    L_0x000a:
        switch(r4) {
            case 75: goto L_0x0053;
            case 77: goto L_0x0048;
            case 83: goto L_0x0045;
            case 97: goto L_0x003e;
            case 100: goto L_0x003b;
            case 104: goto L_0x0038;
            case 107: goto L_0x0035;
            case 109: goto L_0x0032;
            case 115: goto L_0x002f;
            case 117: goto L_0x002c;
            case 119: goto L_0x0029;
            default: goto L_0x000d;
        };
    L_0x000d:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Format '";
        r1.append(r2);
        r1.append(r4);
        r2 = "' not supported";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0029:
        r0 = WEEK_OF_YEAR_STRATEGY;
        return r0;
    L_0x002c:
        r0 = DAY_OF_WEEK_STRATEGY;
        return r0;
    L_0x002f:
        r0 = SECOND_STRATEGY;
        return r0;
    L_0x0032:
        r0 = MINUTE_STRATEGY;
        return r0;
    L_0x0035:
        r0 = HOUR24_OF_DAY_STRATEGY;
        return r0;
    L_0x0038:
        r0 = HOUR12_STRATEGY;
        return r0;
    L_0x003b:
        r0 = DAY_OF_MONTH_STRATEGY;
        return r0;
    L_0x003e:
        r0 = 9;
        r0 = r3.getLocaleSpecificStrategy(r0, r6);
        return r0;
    L_0x0045:
        r0 = MILLISECOND_STRATEGY;
        return r0;
    L_0x0048:
        r1 = 3;
        if (r5 < r1) goto L_0x0050;
    L_0x004b:
        r0 = r3.getLocaleSpecificStrategy(r0, r6);
        goto L_0x0052;
    L_0x0050:
        r0 = NUMBER_MONTH_STRATEGY;
    L_0x0052:
        return r0;
    L_0x0053:
        r0 = HOUR_STRATEGY;
        return r0;
    L_0x0056:
        goto L_0x005f;
    L_0x0057:
        if (r5 != r0) goto L_0x005e;
    L_0x0059:
        r0 = org.apache.commons.lang3.time.FastDateParser.ISO8601TimeZoneStrategy.ISO_8601_3_STRATEGY;
        return r0;
    L_0x005f:
        r0 = 15;
        r0 = r3.getLocaleSpecificStrategy(r0, r6);
        return r0;
    L_0x0066:
        if (r5 <= r0) goto L_0x006b;
    L_0x0068:
        r0 = LITERAL_YEAR_STRATEGY;
        goto L_0x006d;
    L_0x006b:
        r0 = ABBREVIATED_YEAR_STRATEGY;
    L_0x006d:
        return r0;
    L_0x006e:
        r0 = org.apache.commons.lang3.time.FastDateParser.ISO8601TimeZoneStrategy.getStrategy(r5);
        return r0;
    L_0x0073:
        r0 = WEEK_OF_MONTH_STRATEGY;
        return r0;
    L_0x0076:
        r0 = HOUR_OF_DAY_STRATEGY;
        return r0;
    L_0x0079:
        r0 = 0;
        r0 = r3.getLocaleSpecificStrategy(r0, r6);
        return r0;
    L_0x007f:
        r0 = DAY_OF_WEEK_IN_MONTH_STRATEGY;
        return r0;
    L_0x0082:
        r0 = 7;
        r0 = r3.getLocaleSpecificStrategy(r0, r6);
        return r0;
    L_0x0088:
        r0 = DAY_OF_YEAR_STRATEGY;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.FastDateParser.getStrategy(char, int, java.util.Calendar):org.apache.commons.lang3.time.FastDateParser$Strategy");
    }

    private static ConcurrentMap<Locale, Strategy> getCache(int field) {
        ConcurrentMap<Locale, Strategy> concurrentMap;
        synchronized (caches) {
            if (caches[field] == null) {
                caches[field] = new ConcurrentHashMap(3);
            }
            concurrentMap = caches[field];
        }
        return concurrentMap;
    }

    private Strategy getLocaleSpecificStrategy(int field, Calendar definingCalendar) {
        ConcurrentMap<Locale, Strategy> cache = getCache(field);
        Strategy strategy = (Strategy) cache.get(this.locale);
        if (strategy == null) {
            strategy = field == 15 ? new TimeZoneStrategy(this.locale) : new CaseInsensitiveTextStrategy(field, definingCalendar, this.locale);
            Strategy inCache = (Strategy) cache.putIfAbsent(this.locale, strategy);
            if (inCache != null) {
                return inCache;
            }
        }
        return strategy;
    }
}

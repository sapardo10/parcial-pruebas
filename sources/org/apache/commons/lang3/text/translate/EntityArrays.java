package org.apache.commons.lang3.text.translate;

import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import java.lang.reflect.Array;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class EntityArrays {
    private static final String[][] APOS_ESCAPE;
    private static final String[][] APOS_UNESCAPE = invert(APOS_ESCAPE);
    private static final String[][] BASIC_ESCAPE;
    private static final String[][] BASIC_UNESCAPE = invert(BASIC_ESCAPE);
    private static final String[][] HTML40_EXTENDED_ESCAPE;
    private static final String[][] HTML40_EXTENDED_UNESCAPE = invert(HTML40_EXTENDED_ESCAPE);
    private static final String[][] ISO8859_1_ESCAPE;
    private static final String[][] ISO8859_1_UNESCAPE = invert(ISO8859_1_ESCAPE);
    private static final String[][] JAVA_CTRL_CHARS_ESCAPE;
    private static final String[][] JAVA_CTRL_CHARS_UNESCAPE = invert(JAVA_CTRL_CHARS_ESCAPE);

    public static String[][] ISO8859_1_ESCAPE() {
        return (String[][]) ISO8859_1_ESCAPE.clone();
    }

    static {
        r1 = new String[96][];
        r1[0] = new String[]{" ", "&nbsp;"};
        r1[1] = new String[]{"¡", "&iexcl;"};
        r1[2] = new String[]{"¢", "&cent;"};
        r1[3] = new String[]{"£", "&pound;"};
        r1[4] = new String[]{"¤", "&curren;"};
        r1[5] = new String[]{"¥", "&yen;"};
        r1[6] = new String[]{"¦", "&brvbar;"};
        r1[7] = new String[]{"§", "&sect;"};
        r1[8] = new String[]{"¨", "&uml;"};
        r1[9] = new String[]{"©", "&copy;"};
        r1[10] = new String[]{"ª", "&ordf;"};
        r1[11] = new String[]{"«", "&laquo;"};
        r1[12] = new String[]{"¬", "&not;"};
        r1[13] = new String[]{"­", "&shy;"};
        r1[14] = new String[]{"®", "&reg;"};
        r1[15] = new String[]{"¯", "&macr;"};
        r1[16] = new String[]{"°", "&deg;"};
        r1[17] = new String[]{"±", "&plusmn;"};
        r1[18] = new String[]{"²", "&sup2;"};
        r1[19] = new String[]{"³", "&sup3;"};
        r1[20] = new String[]{"´", "&acute;"};
        r1[21] = new String[]{"µ", "&micro;"};
        r1[22] = new String[]{"¶", "&para;"};
        r1[23] = new String[]{"·", "&middot;"};
        r1[24] = new String[]{"¸", "&cedil;"};
        r1[25] = new String[]{"¹", "&sup1;"};
        r1[26] = new String[]{"º", "&ordm;"};
        r1[27] = new String[]{"»", "&raquo;"};
        r1[28] = new String[]{"¼", "&frac14;"};
        r1[29] = new String[]{"½", "&frac12;"};
        r1[30] = new String[]{"¾", "&frac34;"};
        r1[31] = new String[]{"¿", "&iquest;"};
        r1[32] = new String[]{"À", "&Agrave;"};
        r1[33] = new String[]{"Á", "&Aacute;"};
        r1[34] = new String[]{"Â", "&Acirc;"};
        r1[35] = new String[]{"Ã", "&Atilde;"};
        r1[36] = new String[]{"Ä", "&Auml;"};
        r1[37] = new String[]{"Å", "&Aring;"};
        r1[38] = new String[]{"Æ", "&AElig;"};
        r1[39] = new String[]{"Ç", "&Ccedil;"};
        r1[40] = new String[]{"È", "&Egrave;"};
        r1[41] = new String[]{"É", "&Eacute;"};
        r1[42] = new String[]{"Ê", "&Ecirc;"};
        r1[43] = new String[]{"Ë", "&Euml;"};
        r1[44] = new String[]{"Ì", "&Igrave;"};
        r1[45] = new String[]{"Í", "&Iacute;"};
        r1[46] = new String[]{"Î", "&Icirc;"};
        r1[47] = new String[]{"Ï", "&Iuml;"};
        r1[48] = new String[]{"Ð", "&ETH;"};
        r1[49] = new String[]{"Ñ", "&Ntilde;"};
        r1[50] = new String[]{"Ò", "&Ograve;"};
        r1[51] = new String[]{"Ó", "&Oacute;"};
        r1[52] = new String[]{"Ô", "&Ocirc;"};
        r1[53] = new String[]{"Õ", "&Otilde;"};
        r1[54] = new String[]{"Ö", "&Ouml;"};
        r1[55] = new String[]{"×", "&times;"};
        r1[56] = new String[]{"Ø", "&Oslash;"};
        r1[57] = new String[]{"Ù", "&Ugrave;"};
        r1[58] = new String[]{"Ú", "&Uacute;"};
        r1[59] = new String[]{"Û", "&Ucirc;"};
        r1[60] = new String[]{"Ü", "&Uuml;"};
        r1[61] = new String[]{"Ý", "&Yacute;"};
        r1[62] = new String[]{"Þ", "&THORN;"};
        r1[63] = new String[]{"ß", "&szlig;"};
        r1[64] = new String[]{"à", "&agrave;"};
        r1[65] = new String[]{"á", "&aacute;"};
        r1[66] = new String[]{"â", "&acirc;"};
        r1[67] = new String[]{"ã", "&atilde;"};
        r1[68] = new String[]{"ä", "&auml;"};
        r1[69] = new String[]{"å", "&aring;"};
        r1[70] = new String[]{"æ", "&aelig;"};
        r1[71] = new String[]{"ç", "&ccedil;"};
        r1[72] = new String[]{"è", "&egrave;"};
        r1[73] = new String[]{"é", "&eacute;"};
        r1[74] = new String[]{"ê", "&ecirc;"};
        r1[75] = new String[]{"ë", "&euml;"};
        r1[76] = new String[]{"ì", "&igrave;"};
        r1[77] = new String[]{"í", "&iacute;"};
        r1[78] = new String[]{"î", "&icirc;"};
        r1[79] = new String[]{"ï", "&iuml;"};
        r1[80] = new String[]{"ð", "&eth;"};
        r1[81] = new String[]{"ñ", "&ntilde;"};
        r1[82] = new String[]{"ò", "&ograve;"};
        r1[83] = new String[]{"ó", "&oacute;"};
        r1[84] = new String[]{"ô", "&ocirc;"};
        r1[85] = new String[]{"õ", "&otilde;"};
        r1[86] = new String[]{"ö", "&ouml;"};
        r1[87] = new String[]{"÷", "&divide;"};
        r1[88] = new String[]{"ø", "&oslash;"};
        r1[89] = new String[]{"ù", "&ugrave;"};
        r1[90] = new String[]{"ú", "&uacute;"};
        r1[91] = new String[]{"û", "&ucirc;"};
        r1[92] = new String[]{"ü", "&uuml;"};
        r1[93] = new String[]{"ý", "&yacute;"};
        r1[94] = new String[]{"þ", "&thorn;"};
        r1[95] = new String[]{"ÿ", "&yuml;"};
        ISO8859_1_ESCAPE = r1;
        r1 = new String[152][];
        r1[0] = new String[]{"ƒ", "&fnof;"};
        r1[1] = new String[]{"Α", "&Alpha;"};
        r1[2] = new String[]{"Β", "&Beta;"};
        r1[3] = new String[]{"Γ", "&Gamma;"};
        r1[4] = new String[]{"Δ", "&Delta;"};
        r1[5] = new String[]{"Ε", "&Epsilon;"};
        r1[6] = new String[]{"Ζ", "&Zeta;"};
        r1[7] = new String[]{"Η", "&Eta;"};
        r1[8] = new String[]{"Θ", "&Theta;"};
        r1[9] = new String[]{"Ι", "&Iota;"};
        r1[10] = new String[]{"Κ", "&Kappa;"};
        r1[11] = new String[]{"Λ", "&Lambda;"};
        r1[12] = new String[]{"Μ", "&Mu;"};
        r1[13] = new String[]{"Ν", "&Nu;"};
        r1[14] = new String[]{"Ξ", "&Xi;"};
        r1[15] = new String[]{"Ο", "&Omicron;"};
        r1[16] = new String[]{"Π", "&Pi;"};
        r1[17] = new String[]{"Ρ", "&Rho;"};
        r1[18] = new String[]{"Σ", "&Sigma;"};
        r1[19] = new String[]{"Τ", "&Tau;"};
        r1[20] = new String[]{"Υ", "&Upsilon;"};
        r1[21] = new String[]{"Φ", "&Phi;"};
        r1[22] = new String[]{"Χ", "&Chi;"};
        r1[23] = new String[]{"Ψ", "&Psi;"};
        r1[24] = new String[]{"Ω", "&Omega;"};
        r1[25] = new String[]{"α", "&alpha;"};
        r1[26] = new String[]{"β", "&beta;"};
        r1[27] = new String[]{"γ", "&gamma;"};
        r1[28] = new String[]{"δ", "&delta;"};
        r1[29] = new String[]{"ε", "&epsilon;"};
        r1[30] = new String[]{"ζ", "&zeta;"};
        r1[31] = new String[]{"η", "&eta;"};
        r1[32] = new String[]{"θ", "&theta;"};
        r1[33] = new String[]{"ι", "&iota;"};
        r1[34] = new String[]{"κ", "&kappa;"};
        r1[35] = new String[]{"λ", "&lambda;"};
        r1[36] = new String[]{"μ", "&mu;"};
        r1[37] = new String[]{"ν", "&nu;"};
        r1[38] = new String[]{"ξ", "&xi;"};
        r1[39] = new String[]{"ο", "&omicron;"};
        r1[40] = new String[]{"π", "&pi;"};
        r1[41] = new String[]{"ρ", "&rho;"};
        r1[42] = new String[]{"ς", "&sigmaf;"};
        r1[43] = new String[]{"σ", "&sigma;"};
        r1[44] = new String[]{"τ", "&tau;"};
        r1[45] = new String[]{"υ", "&upsilon;"};
        r1[46] = new String[]{"φ", "&phi;"};
        r1[47] = new String[]{"χ", "&chi;"};
        r1[48] = new String[]{"ψ", "&psi;"};
        r1[49] = new String[]{"ω", "&omega;"};
        r1[50] = new String[]{"ϑ", "&thetasym;"};
        r1[51] = new String[]{"ϒ", "&upsih;"};
        r1[52] = new String[]{"ϖ", "&piv;"};
        r1[53] = new String[]{"•", "&bull;"};
        r1[54] = new String[]{"…", "&hellip;"};
        r1[55] = new String[]{"′", "&prime;"};
        r1[56] = new String[]{"″", "&Prime;"};
        r1[57] = new String[]{"‾", "&oline;"};
        r1[58] = new String[]{"⁄", "&frasl;"};
        r1[59] = new String[]{"℘", "&weierp;"};
        r1[60] = new String[]{"ℑ", "&image;"};
        r1[61] = new String[]{"ℜ", "&real;"};
        r1[62] = new String[]{"™", "&trade;"};
        r1[63] = new String[]{"ℵ", "&alefsym;"};
        r1[64] = new String[]{"←", "&larr;"};
        r1[65] = new String[]{"↑", "&uarr;"};
        r1[66] = new String[]{"→", "&rarr;"};
        r1[67] = new String[]{"↓", "&darr;"};
        r1[68] = new String[]{"↔", "&harr;"};
        r1[69] = new String[]{"↵", "&crarr;"};
        r1[70] = new String[]{"⇐", "&lArr;"};
        r1[71] = new String[]{"⇑", "&uArr;"};
        r1[72] = new String[]{"⇒", "&rArr;"};
        r1[73] = new String[]{"⇓", "&dArr;"};
        r1[74] = new String[]{"⇔", "&hArr;"};
        r1[75] = new String[]{"∀", "&forall;"};
        r1[76] = new String[]{"∂", "&part;"};
        r1[77] = new String[]{"∃", "&exist;"};
        r1[78] = new String[]{"∅", "&empty;"};
        r1[79] = new String[]{"∇", "&nabla;"};
        r1[80] = new String[]{"∈", "&isin;"};
        r1[81] = new String[]{"∉", "&notin;"};
        r1[82] = new String[]{"∋", "&ni;"};
        r1[83] = new String[]{"∏", "&prod;"};
        r1[84] = new String[]{"∑", "&sum;"};
        r1[85] = new String[]{"−", "&minus;"};
        r1[86] = new String[]{"∗", "&lowast;"};
        r1[87] = new String[]{"√", "&radic;"};
        r1[88] = new String[]{"∝", "&prop;"};
        r1[89] = new String[]{"∞", "&infin;"};
        r1[90] = new String[]{"∠", "&ang;"};
        r1[91] = new String[]{"∧", "&and;"};
        r1[92] = new String[]{"∨", "&or;"};
        r1[93] = new String[]{"∩", "&cap;"};
        r1[94] = new String[]{"∪", "&cup;"};
        r1[95] = new String[]{"∫", "&int;"};
        r1[96] = new String[]{"∴", "&there4;"};
        r1[97] = new String[]{"∼", "&sim;"};
        r1[98] = new String[]{"≅", "&cong;"};
        r1[99] = new String[]{"≈", "&asymp;"};
        r1[100] = new String[]{"≠", "&ne;"};
        r1[101] = new String[]{"≡", "&equiv;"};
        r1[102] = new String[]{"≤", "&le;"};
        r1[103] = new String[]{"≥", "&ge;"};
        r1[104] = new String[]{"⊂", "&sub;"};
        r1[105] = new String[]{"⊃", "&sup;"};
        r1[106] = new String[]{"⊄", "&nsub;"};
        r1[107] = new String[]{"⊆", "&sube;"};
        r1[108] = new String[]{"⊇", "&supe;"};
        r1[109] = new String[]{"⊕", "&oplus;"};
        r1[110] = new String[]{"⊗", "&otimes;"};
        r1[111] = new String[]{"⊥", "&perp;"};
        r1[112] = new String[]{"⋅", "&sdot;"};
        r1[113] = new String[]{"⌈", "&lceil;"};
        r1[114] = new String[]{"⌉", "&rceil;"};
        r1[115] = new String[]{"⌊", "&lfloor;"};
        r1[116] = new String[]{"⌋", "&rfloor;"};
        r1[117] = new String[]{"〈", "&lang;"};
        r1[118] = new String[]{"〉", "&rang;"};
        r1[119] = new String[]{"◊", "&loz;"};
        r1[120] = new String[]{"♠", "&spades;"};
        r1[121] = new String[]{"♣", "&clubs;"};
        r1[122] = new String[]{"♥", "&hearts;"};
        r1[123] = new String[]{"♦", "&diams;"};
        r1[124] = new String[]{"Œ", "&OElig;"};
        r1[125] = new String[]{"œ", "&oelig;"};
        r1[126] = new String[]{"Š", "&Scaron;"};
        r1[127] = new String[]{"š", "&scaron;"};
        r1[128] = new String[]{"Ÿ", "&Yuml;"};
        r1[TsExtractor.TS_STREAM_TYPE_AC3] = new String[]{"ˆ", "&circ;"};
        r1[TsExtractor.TS_STREAM_TYPE_HDMV_DTS] = new String[]{"˜", "&tilde;"};
        r1[131] = new String[]{" ", "&ensp;"};
        r1[132] = new String[]{" ", "&emsp;"};
        r1[133] = new String[]{" ", "&thinsp;"};
        r1[TsExtractor.TS_STREAM_TYPE_SPLICE_INFO] = new String[]{"‌", "&zwnj;"};
        r1[TsExtractor.TS_STREAM_TYPE_E_AC3] = new String[]{"‍", "&zwj;"};
        r1[136] = new String[]{"‎", "&lrm;"};
        r1[137] = new String[]{"‏", "&rlm;"};
        r1[TsExtractor.TS_STREAM_TYPE_DTS] = new String[]{"–", "&ndash;"};
        r1[139] = new String[]{"—", "&mdash;"};
        r1[140] = new String[]{"‘", "&lsquo;"};
        r1[141] = new String[]{"’", "&rsquo;"};
        r1[142] = new String[]{"‚", "&sbquo;"};
        r1[143] = new String[]{"“", "&ldquo;"};
        r1[144] = new String[]{"”", "&rdquo;"};
        r1[145] = new String[]{"„", "&bdquo;"};
        r1[146] = new String[]{"†", "&dagger;"};
        r1[147] = new String[]{"‡", "&Dagger;"};
        r1[148] = new String[]{"‰", "&permil;"};
        r1[149] = new String[]{"‹", "&lsaquo;"};
        r1[150] = new String[]{"›", "&rsaquo;"};
        r1[151] = new String[]{"€", "&euro;"};
        HTML40_EXTENDED_ESCAPE = r1;
        r0 = new String[4][];
        r0[0] = new String[]{"\"", "&quot;"};
        r0[1] = new String[]{"&", "&amp;"};
        r0[2] = new String[]{"<", "&lt;"};
        r0[3] = new String[]{">", "&gt;"};
        BASIC_ESCAPE = r0;
        r0 = new String[1][];
        r0[0] = new String[]{"'", "&apos;"};
        APOS_ESCAPE = r0;
        r0 = new String[5][];
        r0[0] = new String[]{"\b", "\\b"};
        r0[1] = new String[]{"\n", "\\n"};
        r0[2] = new String[]{"\t", "\\t"};
        r0[3] = new String[]{"\f", "\\f"};
        r0[4] = new String[]{StringUtils.CR, "\\r"};
        JAVA_CTRL_CHARS_ESCAPE = r0;
    }

    public static String[][] ISO8859_1_UNESCAPE() {
        return (String[][]) ISO8859_1_UNESCAPE.clone();
    }

    public static String[][] HTML40_EXTENDED_ESCAPE() {
        return (String[][]) HTML40_EXTENDED_ESCAPE.clone();
    }

    public static String[][] HTML40_EXTENDED_UNESCAPE() {
        return (String[][]) HTML40_EXTENDED_UNESCAPE.clone();
    }

    public static String[][] BASIC_ESCAPE() {
        return (String[][]) BASIC_ESCAPE.clone();
    }

    public static String[][] BASIC_UNESCAPE() {
        return (String[][]) BASIC_UNESCAPE.clone();
    }

    public static String[][] APOS_ESCAPE() {
        return (String[][]) APOS_ESCAPE.clone();
    }

    public static String[][] APOS_UNESCAPE() {
        return (String[][]) APOS_UNESCAPE.clone();
    }

    public static String[][] JAVA_CTRL_CHARS_ESCAPE() {
        return (String[][]) JAVA_CTRL_CHARS_ESCAPE.clone();
    }

    public static String[][] JAVA_CTRL_CHARS_UNESCAPE() {
        return (String[][]) JAVA_CTRL_CHARS_UNESCAPE.clone();
    }

    public static String[][] invert(String[][] array) {
        String[][] newarray = (String[][]) Array.newInstance(String.class, new int[]{array.length, 2});
        for (int i = 0; i < array.length; i++) {
            newarray[i][0] = array[i][1];
            newarray[i][1] = array[i][0];
        }
        return newarray;
    }
}

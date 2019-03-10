package org.jsoup.parser;

import android.support.v4.app.NotificationCompat;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import com.google.android.exoplayer2.util.MimeTypes;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.helper.Validate;

public class Tag {
    private static final String[] blockTags;
    private static final String[] emptyTags = new String[]{"meta", PodDBAdapter.KEY_LINK, "base", "frame", "img", TtmlNode.TAG_BR, "wbr", "embed", "hr", "input", "keygen", "col", "command", "device", "area", "basefont", "bgsound", "menuitem", "param", "source", "track"};
    private static final String[] formListedTags = new String[]{"button", "fieldset", "input", "keygen", "object", "output", "select", "textarea"};
    private static final String[] formSubmitTags = new String[]{"input", "keygen", "object", "select", "textarea"};
    private static final String[] formatAsInlineTags = new String[]{"title", "a", TtmlNode.TAG_P, "h1", "h2", "h3", "h4", "h5", "h6", "pre", "address", "li", "th", "td", "script", TtmlNode.TAG_STYLE, "ins", "del", "s"};
    private static final String[] inlineTags = new String[]{"object", "base", "font", TtmlNode.TAG_TT, "i", "b", "u", "big", "small", "em", "strong", "dfn", "code", "samp", "kbd", "var", "cite", "abbr", "time", "acronym", "mark", "ruby", "rt", "rp", "a", "img", TtmlNode.TAG_BR, "wbr", "map", "q", "sub", "sup", "bdo", "iframe", "embed", TtmlNode.TAG_SPAN, "input", "select", "textarea", "label", "button", "optgroup", "option", "legend", "datalist", "keygen", "output", NotificationCompat.CATEGORY_PROGRESS, "meter", "area", "param", "source", "track", "summary", "command", "device", "area", "basefont", "bgsound", "menuitem", "param", "source", "track", "data", "bdi", "s"};
    private static final String[] preserveWhitespaceTags = new String[]{"pre", "plaintext", "title", "textarea"};
    private static final Map<String, Tag> tags = new HashMap();
    private boolean canContainInline = true;
    private boolean empty = false;
    private boolean formList = false;
    private boolean formSubmit = false;
    private boolean formatAsBlock = true;
    private boolean isBlock = true;
    private boolean preserveWhitespace = false;
    private boolean selfClosing = false;
    private String tagName;

    static {
        r0 = new String[63];
        int i = 0;
        r0[0] = "html";
        r0[1] = "head";
        r0[2] = "body";
        r0[3] = "frameset";
        r0[4] = "script";
        r0[5] = "noscript";
        r0[6] = TtmlNode.TAG_STYLE;
        r0[7] = "meta";
        r0[8] = PodDBAdapter.KEY_LINK;
        r0[9] = "title";
        r0[10] = "frame";
        r0[11] = "noframes";
        r0[12] = "section";
        r0[13] = "nav";
        r0[14] = "aside";
        r0[15] = "hgroup";
        r0[16] = "header";
        r0[17] = "footer";
        r0[18] = TtmlNode.TAG_P;
        r0[19] = "h1";
        r0[20] = "h2";
        r0[21] = "h3";
        r0[22] = "h4";
        r0[23] = "h5";
        r0[24] = "h6";
        r0[25] = "ul";
        r0[26] = "ol";
        r0[27] = "pre";
        r0[28] = TtmlNode.TAG_DIV;
        r0[29] = "blockquote";
        r0[30] = "hr";
        r0[31] = "address";
        r0[32] = "figure";
        r0[33] = "figcaption";
        r0[34] = "form";
        r0[35] = "fieldset";
        r0[36] = "ins";
        r0[37] = "del";
        r0[38] = "dl";
        r0[39] = "dt";
        r0[40] = "dd";
        r0[41] = "li";
        r0[42] = "table";
        r0[43] = "caption";
        r0[44] = "thead";
        r0[45] = "tfoot";
        r0[46] = "tbody";
        r0[47] = "colgroup";
        r0[48] = "col";
        r0[49] = "tr";
        r0[50] = "th";
        r0[51] = "td";
        r0[52] = MimeTypes.BASE_TYPE_VIDEO;
        r0[53] = MimeTypes.BASE_TYPE_AUDIO;
        r0[54] = "canvas";
        r0[55] = "details";
        r0[56] = "menu";
        r0[57] = "plaintext";
        r0[58] = "template";
        r0[59] = "article";
        r0[60] = "main";
        r0[61] = "svg";
        r0[62] = "math";
        blockTags = r0;
        for (String tagName : blockTags) {
            register(new Tag(tagName));
        }
        for (String tagName2 : inlineTags) {
            Tag tag = new Tag(tagName2);
            tag.isBlock = false;
            tag.formatAsBlock = false;
            register(tag);
        }
        for (String tagName22 : emptyTags) {
            tag = (Tag) tags.get(tagName22);
            Validate.notNull(tag);
            tag.canContainInline = false;
            tag.empty = true;
        }
        for (String tagName222 : formatAsInlineTags) {
            tag = (Tag) tags.get(tagName222);
            Validate.notNull(tag);
            tag.formatAsBlock = false;
        }
        for (String tagName2222 : preserveWhitespaceTags) {
            tag = (Tag) tags.get(tagName2222);
            Validate.notNull(tag);
            tag.preserveWhitespace = true;
        }
        for (String tagName22222 : formListedTags) {
            tag = (Tag) tags.get(tagName22222);
            Validate.notNull(tag);
            tag.formList = true;
        }
        r0 = formSubmitTags;
        int length = r0.length;
        while (i < length) {
            Tag tag2 = (Tag) tags.get(r0[i]);
            Validate.notNull(tag2);
            tag2.formSubmit = true;
            i++;
        }
    }

    private Tag(String tagName) {
        this.tagName = tagName;
    }

    public String getName() {
        return this.tagName;
    }

    public static Tag valueOf(String tagName, ParseSettings settings) {
        Validate.notNull(tagName);
        Tag tag = (Tag) tags.get(tagName);
        if (tag != null) {
            return tag;
        }
        tagName = settings.normalizeTag(tagName);
        Validate.notEmpty(tagName);
        tag = (Tag) tags.get(tagName);
        if (tag != null) {
            return tag;
        }
        tag = new Tag(tagName);
        tag.isBlock = false;
        return tag;
    }

    public static Tag valueOf(String tagName) {
        return valueOf(tagName, ParseSettings.preserveCase);
    }

    public boolean isBlock() {
        return this.isBlock;
    }

    public boolean formatAsBlock() {
        return this.formatAsBlock;
    }

    public boolean canContainBlock() {
        return this.isBlock;
    }

    public boolean isInline() {
        return this.isBlock ^ 1;
    }

    public boolean isData() {
        return (this.canContainInline || isEmpty()) ? false : true;
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public boolean isSelfClosing() {
        if (!this.empty) {
            if (!this.selfClosing) {
                return false;
            }
        }
        return true;
    }

    public boolean isKnownTag() {
        return tags.containsKey(this.tagName);
    }

    public static boolean isKnownTag(String tagName) {
        return tags.containsKey(tagName);
    }

    public boolean preserveWhitespace() {
        return this.preserveWhitespace;
    }

    public boolean isFormListed() {
        return this.formList;
    }

    public boolean isFormSubmittable() {
        return this.formSubmit;
    }

    Tag setSelfClosing() {
        this.selfClosing = true;
        return this;
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        Tag tag = (Tag) o;
        if (!this.tagName.equals(tag.tagName) || this.canContainInline != tag.canContainInline || this.empty != tag.empty || this.formatAsBlock != tag.formatAsBlock || this.isBlock != tag.isBlock || this.preserveWhitespace != tag.preserveWhitespace || this.selfClosing != tag.selfClosing || this.formList != tag.formList) {
            return false;
        }
        if (this.formSubmit != tag.formSubmit) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return (((((((((((((((this.tagName.hashCode() * 31) + this.isBlock) * 31) + this.formatAsBlock) * 31) + this.canContainInline) * 31) + this.empty) * 31) + this.selfClosing) * 31) + this.preserveWhitespace) * 31) + this.formList) * 31) + this.formSubmit;
    }

    public String toString() {
        return this.tagName;
    }

    private static void register(Tag tag) {
        tags.put(tag.tagName, tag);
    }
}

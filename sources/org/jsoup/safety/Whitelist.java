package org.jsoup.safety;

import com.google.android.exoplayer2.text.ttml.TtmlNode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.jsoup.helper.Validate;
import org.jsoup.internal.Normalizer;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

public class Whitelist {
    private Map<TagName, Set<AttributeKey>> attributes = new HashMap();
    private Map<TagName, Map<AttributeKey, AttributeValue>> enforcedAttributes = new HashMap();
    private boolean preserveRelativeLinks = false;
    private Map<TagName, Map<AttributeKey, Set<Protocol>>> protocols = new HashMap();
    private Set<TagName> tagNames = new HashSet();

    static abstract class TypedValue {
        private String value;

        TypedValue(String value) {
            Validate.notNull(value);
            this.value = value;
        }

        public int hashCode() {
            int result = 1 * 31;
            String str = this.value;
            return result + (str == null ? 0 : str.hashCode());
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            TypedValue other = (TypedValue) obj;
            String str = this.value;
            if (str == null) {
                if (other.value != null) {
                    return false;
                }
            } else if (!str.equals(other.value)) {
                return false;
            }
            return true;
        }

        public String toString() {
            return this.value;
        }
    }

    static class AttributeKey extends TypedValue {
        AttributeKey(String value) {
            super(value);
        }

        static AttributeKey valueOf(String value) {
            return new AttributeKey(value);
        }
    }

    static class AttributeValue extends TypedValue {
        AttributeValue(String value) {
            super(value);
        }

        static AttributeValue valueOf(String value) {
            return new AttributeValue(value);
        }
    }

    static class Protocol extends TypedValue {
        Protocol(String value) {
            super(value);
        }

        static Protocol valueOf(String value) {
            return new Protocol(value);
        }
    }

    static class TagName extends TypedValue {
        TagName(String value) {
            super(value);
        }

        static TagName valueOf(String value) {
            return new TagName(value);
        }
    }

    public static Whitelist none() {
        return new Whitelist();
    }

    public static Whitelist simpleText() {
        return new Whitelist().addTags("b", "em", "i", "strong", "u");
    }

    public static Whitelist basic() {
        return new Whitelist().addTags("a", "b", "blockquote", TtmlNode.TAG_BR, "cite", "code", "dd", "dl", "dt", "em", "i", "li", "ol", TtmlNode.TAG_P, "pre", "q", "small", TtmlNode.TAG_SPAN, "strike", "strong", "sub", "sup", "u", "ul").addAttributes("a", "href").addAttributes("blockquote", "cite").addAttributes("q", "cite").addProtocols("a", "href", "ftp", "http", "https", "mailto").addProtocols("blockquote", "cite", "http", "https").addProtocols("cite", "cite", "http", "https").addEnforcedAttribute("a", "rel", "nofollow");
    }

    public static Whitelist basicWithImages() {
        return basic().addTags("img").addAttributes("img", "align", "alt", "height", "src", "title", "width").addProtocols("img", "src", "http", "https");
    }

    public static Whitelist relaxed() {
        return new Whitelist().addTags("a", "b", "blockquote", TtmlNode.TAG_BR, "caption", "cite", "code", "col", "colgroup", "dd", TtmlNode.TAG_DIV, "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6", "i", "img", "li", "ol", TtmlNode.TAG_P, "pre", "q", "small", TtmlNode.TAG_SPAN, "strike", "strong", "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul").addAttributes("a", "href", "title").addAttributes("blockquote", "cite").addAttributes("col", TtmlNode.TAG_SPAN, "width").addAttributes("colgroup", TtmlNode.TAG_SPAN, "width").addAttributes("img", "align", "alt", "height", "src", "title", "width").addAttributes("ol", "start", "type").addAttributes("q", "cite").addAttributes("table", "summary", "width").addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width").addAttributes("th", "abbr", "axis", "colspan", "rowspan", "scope", "width").addAttributes("ul", "type").addProtocols("a", "href", "ftp", "http", "https", "mailto").addProtocols("blockquote", "cite", "http", "https").addProtocols("cite", "cite", "http", "https").addProtocols("img", "src", "http", "https").addProtocols("q", "cite", "http", "https");
    }

    public Whitelist addTags(String... tags) {
        Validate.notNull(tags);
        for (String tagName : tags) {
            Validate.notEmpty(tagName);
            this.tagNames.add(TagName.valueOf(tagName));
        }
        return this;
    }

    public Whitelist removeTags(String... tags) {
        Validate.notNull(tags);
        for (String tag : tags) {
            Validate.notEmpty(tag);
            TagName tagName = TagName.valueOf(tag);
            if (this.tagNames.remove(tagName)) {
                this.attributes.remove(tagName);
                this.enforcedAttributes.remove(tagName);
                this.protocols.remove(tagName);
            }
        }
        return this;
    }

    public Whitelist addAttributes(String tag, String... attributes) {
        Validate.notEmpty(tag);
        Validate.notNull(attributes);
        Validate.isTrue(attributes.length > 0, "No attribute names supplied.");
        TagName tagName = TagName.valueOf(tag);
        if (!this.tagNames.contains(tagName)) {
            this.tagNames.add(tagName);
        }
        Set<AttributeKey> attributeSet = new HashSet();
        for (String key : attributes) {
            Validate.notEmpty(key);
            attributeSet.add(AttributeKey.valueOf(key));
        }
        if (this.attributes.containsKey(tagName)) {
            ((Set) this.attributes.get(tagName)).addAll(attributeSet);
        } else {
            this.attributes.put(tagName, attributeSet);
        }
        return this;
    }

    public Whitelist removeAttributes(String tag, String... attributes) {
        Validate.notEmpty(tag);
        Validate.notNull(attributes);
        Validate.isTrue(attributes.length > 0, "No attribute names supplied.");
        TagName tagName = TagName.valueOf(tag);
        Set<AttributeKey> attributeSet = new HashSet();
        for (String key : attributes) {
            Validate.notEmpty(key);
            attributeSet.add(AttributeKey.valueOf(key));
        }
        if (this.tagNames.contains(tagName) && this.attributes.containsKey(tagName)) {
            Set<AttributeKey> currentSet = (Set) this.attributes.get(tagName);
            currentSet.removeAll(attributeSet);
            if (currentSet.isEmpty()) {
                this.attributes.remove(tagName);
            }
        }
        if (tag.equals(":all")) {
            for (TagName name : this.attributes.keySet()) {
                Set<AttributeKey> currentSet2 = (Set) this.attributes.get(name);
                currentSet2.removeAll(attributeSet);
                if (currentSet2.isEmpty()) {
                    this.attributes.remove(name);
                }
            }
        }
        return this;
    }

    public Whitelist addEnforcedAttribute(String tag, String attribute, String value) {
        Validate.notEmpty(tag);
        Validate.notEmpty(attribute);
        Validate.notEmpty(value);
        TagName tagName = TagName.valueOf(tag);
        if (!this.tagNames.contains(tagName)) {
            this.tagNames.add(tagName);
        }
        AttributeKey attrKey = AttributeKey.valueOf(attribute);
        AttributeValue attrVal = AttributeValue.valueOf(value);
        if (this.enforcedAttributes.containsKey(tagName)) {
            ((Map) this.enforcedAttributes.get(tagName)).put(attrKey, attrVal);
        } else {
            Map<AttributeKey, AttributeValue> attrMap = new HashMap();
            attrMap.put(attrKey, attrVal);
            this.enforcedAttributes.put(tagName, attrMap);
        }
        return this;
    }

    public Whitelist removeEnforcedAttribute(String tag, String attribute) {
        Validate.notEmpty(tag);
        Validate.notEmpty(attribute);
        TagName tagName = TagName.valueOf(tag);
        if (this.tagNames.contains(tagName) && this.enforcedAttributes.containsKey(tagName)) {
            Map<AttributeKey, AttributeValue> attrMap = (Map) this.enforcedAttributes.get(tagName);
            attrMap.remove(AttributeKey.valueOf(attribute));
            if (attrMap.isEmpty()) {
                this.enforcedAttributes.remove(tagName);
            }
        }
        return this;
    }

    public Whitelist preserveRelativeLinks(boolean preserve) {
        this.preserveRelativeLinks = preserve;
        return this;
    }

    public Whitelist addProtocols(String tag, String attribute, String... protocols) {
        Map<AttributeKey, Set<Protocol>> attrMap;
        Set<Protocol> protSet;
        Validate.notEmpty(tag);
        Validate.notEmpty(attribute);
        Validate.notNull(protocols);
        TagName tagName = TagName.valueOf(tag);
        AttributeKey attrKey = AttributeKey.valueOf(attribute);
        if (this.protocols.containsKey(tagName)) {
            attrMap = (Map) this.protocols.get(tagName);
        } else {
            attrMap = new HashMap();
            this.protocols.put(tagName, attrMap);
        }
        if (attrMap.containsKey(attrKey)) {
            protSet = (Set) attrMap.get(attrKey);
        } else {
            protSet = new HashSet();
            attrMap.put(attrKey, protSet);
        }
        for (String protocol : protocols) {
            Validate.notEmpty(protocol);
            protSet.add(Protocol.valueOf(protocol));
        }
        return this;
    }

    public Whitelist removeProtocols(String tag, String attribute, String... removeProtocols) {
        Validate.notEmpty(tag);
        Validate.notEmpty(attribute);
        Validate.notNull(removeProtocols);
        TagName tagName = TagName.valueOf(tag);
        AttributeKey attr = AttributeKey.valueOf(attribute);
        Validate.isTrue(this.protocols.containsKey(tagName), "Cannot remove a protocol that is not set.");
        Map<AttributeKey, Set<Protocol>> tagProtocols = (Map) this.protocols.get(tagName);
        Validate.isTrue(tagProtocols.containsKey(attr), "Cannot remove a protocol that is not set.");
        Set<Protocol> attrProtocols = (Set) tagProtocols.get(attr);
        for (String protocol : removeProtocols) {
            Validate.notEmpty(protocol);
            attrProtocols.remove(Protocol.valueOf(protocol));
        }
        if (attrProtocols.isEmpty()) {
            tagProtocols.remove(attr);
            if (tagProtocols.isEmpty()) {
                this.protocols.remove(tagName);
            }
        }
        return this;
    }

    protected boolean isSafeTag(String tag) {
        return this.tagNames.contains(TagName.valueOf(tag));
    }

    protected boolean isSafeAttribute(String tagName, Element el, Attribute attr) {
        TagName tag = TagName.valueOf(tagName);
        AttributeKey key = AttributeKey.valueOf(attr.getKey());
        Set<AttributeKey> okSet = (Set) this.attributes.get(tag);
        boolean z = false;
        if (okSet == null || !okSet.contains(key)) {
            if (((Map) this.enforcedAttributes.get(tag)) != null) {
                Attributes expect = getEnforcedAttributes(tagName);
                String attrKey = attr.getKey();
                if (expect.hasKeyIgnoreCase(attrKey)) {
                    return expect.getIgnoreCase(attrKey).equals(attr.getValue());
                }
            }
            if (!tagName.equals(":all") && isSafeAttribute(":all", el, attr)) {
                z = true;
            }
            return z;
        } else if (!this.protocols.containsKey(tag)) {
            return true;
        } else {
            Map<AttributeKey, Set<Protocol>> attrProts = (Map) this.protocols.get(tag);
            if (attrProts.containsKey(key)) {
                if (!testValidProtocol(el, attr, (Set) attrProts.get(key))) {
                    return z;
                }
            }
            z = true;
            return z;
        }
    }

    private boolean testValidProtocol(Element el, Attribute attr, Set<Protocol> protocols) {
        String value = el.absUrl(attr.getKey());
        if (value.length() == 0) {
            value = attr.getValue();
        }
        if (!this.preserveRelativeLinks) {
            attr.setValue(value);
        }
        for (Protocol protocol : protocols) {
            String prot = protocol.toString();
            if (!prot.equals("#")) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(prot);
                stringBuilder.append(":");
                if (Normalizer.lowerCase(value).startsWith(stringBuilder.toString())) {
                    return true;
                }
            } else if (isValidAnchor(value)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidAnchor(String value) {
        return value.startsWith("#") && !value.matches(".*\\s.*");
    }

    Attributes getEnforcedAttributes(String tagName) {
        Attributes attrs = new Attributes();
        TagName tag = TagName.valueOf(tagName);
        if (this.enforcedAttributes.containsKey(tag)) {
            for (Entry<AttributeKey, AttributeValue> entry : ((Map) this.enforcedAttributes.get(tag)).entrySet()) {
                attrs.put(((AttributeKey) entry.getKey()).toString(), ((AttributeValue) entry.getValue()).toString());
            }
        }
        return attrs;
    }
}

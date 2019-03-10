package com.robotium.solo;

public abstract class By {

    static class ClassName extends By {
        private final String className;

        public ClassName(String className) {
            this.className = className;
        }

        public String getValue() {
            return this.className;
        }
    }

    static class CssSelector extends By {
        private final String selector;

        public CssSelector(String selector) {
            this.selector = selector;
        }

        public String getValue() {
            return this.selector;
        }
    }

    static class Id extends By {
        private final String id;

        public Id(String id) {
            this.id = id;
        }

        public String getValue() {
            return this.id;
        }
    }

    static class Name extends By {
        private final String name;

        public Name(String name) {
            this.name = name;
        }

        public String getValue() {
            return this.name;
        }
    }

    static class TagName extends By {
        private final String tagName;

        public TagName(String tagName) {
            this.tagName = tagName;
        }

        public String getValue() {
            return this.tagName;
        }
    }

    static class Text extends By {
        private final String textContent;

        public Text(String textContent) {
            this.textContent = textContent;
        }

        public String getValue() {
            return this.textContent;
        }
    }

    static class Xpath extends By {
        private final String xpath;

        public Xpath(String xpath) {
            this.xpath = xpath;
        }

        public String getValue() {
            return this.xpath;
        }
    }

    public static By id(String id) {
        return new Id(id);
    }

    public static By xpath(String xpath) {
        return new Xpath(xpath);
    }

    public static By cssSelector(String selectors) {
        return new CssSelector(selectors);
    }

    public static By name(String name) {
        return new Name(name);
    }

    public static By className(String className) {
        return new ClassName(className);
    }

    public static By textContent(String textContent) {
        return new Text(textContent);
    }

    public static By tagName(String tagName) {
        return new TagName(tagName);
    }

    public String getValue() {
        return "";
    }
}

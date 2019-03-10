package com.robotium.solo;

import java.util.Hashtable;

public class WebElement {
    private Hashtable<String, String> attributes;
    private String className;
    private String id;
    private int locationX = 0;
    private int locationY = 0;
    private String name;
    private String tagName;
    private String text;

    public WebElement(String webId, String textContent, String name, String className, String tagName, Hashtable<String, String> attributes) {
        setId(webId);
        setTextContent(textContent);
        setName(name);
        setClassName(className);
        setTagName(tagName);
        setAttributes(attributes);
    }

    public void getLocationOnScreen(int[] location) {
        location[0] = this.locationX;
        location[1] = this.locationY;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }

    public int getLocationX() {
        return this.locationX;
    }

    public int getLocationY() {
        return this.locationY;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTagName() {
        return this.tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getText() {
        return this.text;
    }

    public void setTextContent(String textContent) {
        this.text = textContent;
    }

    public String getAttribute(String attributeName) {
        if (attributeName != null) {
            return (String) this.attributes.get(attributeName);
        }
        return null;
    }

    public void setAttributes(Hashtable<String, String> attributes) {
        this.attributes = attributes;
    }
}

package org.shredzone.flattr4j.model;

import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.shredzone.flattr4j.connector.FlattrObject;
import org.shredzone.flattr4j.exception.FlattrException;
import org.shredzone.flattr4j.exception.MarshalException;

public class Submission implements Serializable {
    private static final long serialVersionUID = -6684005944290342599L;
    private String category;
    private String description;
    private Boolean hidden;
    private String language;
    private List<String> tags = new ArrayList();
    private String title;
    private String url;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public CategoryId getCategory() {
        String str = this.category;
        return str != null ? Category.withId(str) : null;
    }

    public void setCategory(CategoryId category) {
        this.category = category.getCategoryId();
    }

    public LanguageId getLanguage() {
        String str = this.language;
        return str != null ? Language.withId(str) : null;
    }

    public void setLanguage(LanguageId language) {
        this.language = language.getLanguageId();
    }

    public Boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getTagsAsString() {
        StringBuilder sb = new StringBuilder();
        for (String tag : this.tags) {
            if (tag.indexOf(44) < 0) {
                sb.append(',');
                sb.append(tag);
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("tag '");
                stringBuilder.append(tag);
                stringBuilder.append("' contains invalid character ','");
                throw new MarshalException(stringBuilder.toString());
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    public FlattrObject toFlattrObject() throws FlattrException {
        FlattrObject result = new FlattrObject();
        result.put("url", this.url);
        Boolean bool = this.hidden;
        if (bool != null) {
            result.put("hidden", bool);
        }
        String str = this.title;
        if (str != null) {
            result.put("title", str);
        }
        str = this.description;
        if (str != null) {
            result.put(PodDBAdapter.KEY_DESCRIPTION, str);
        }
        str = this.category;
        if (str != null) {
            result.put("category", str);
        }
        str = this.language;
        if (str != null) {
            result.put(PodDBAdapter.KEY_LANGUAGE, str);
        }
        List list = this.tags;
        if (list != null && !list.isEmpty()) {
            result.put("tags", getTagsAsString());
        }
        return result;
    }
}

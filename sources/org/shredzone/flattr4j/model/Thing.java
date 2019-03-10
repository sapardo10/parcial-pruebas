package org.shredzone.flattr4j.model;

import de.danoeh.antennapod.activity.PreferenceActivity;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.shredzone.flattr4j.connector.FlattrObject;
import org.shredzone.flattr4j.exception.MarshalException;

public class Thing extends Resource implements ThingId, UserId, CategoryId, LanguageId {
    private static final long serialVersionUID = 2822280427303390055L;
    private transient List<String> tags = null;
    private Set<String> updatedKeys = new HashSet();
    private transient User user = null;

    public static ThingId withId(final String id) {
        return new ThingId() {
            public String getThingId() {
                return id;
            }
        };
    }

    public Thing(FlattrObject data) {
        super(data);
    }

    public String getThingId() {
        return String.valueOf(this.data.getInt("id"));
    }

    public String getResource() {
        return this.data.get(PreferenceActivity.PARAM_RESOURCE);
    }

    public String getLink() {
        return this.data.get(PodDBAdapter.KEY_LINK);
    }

    public Date getCreated() {
        return this.data.getDate("created_at");
    }

    public int getClicks() {
        return this.data.getInt("flattrs");
    }

    public String getUrl() {
        return this.data.get("url");
    }

    public String getTitle() {
        return this.data.get("title");
    }

    public void setTitle(String title) {
        this.data.put("title", title);
        this.updatedKeys.add("title");
    }

    public String getImage() {
        return this.data.has("image") ? this.data.get("image") : "";
    }

    public String getUserId() {
        return this.data.getSubString("owner", PodDBAdapter.KEY_USERNAME);
    }

    public User getUser() {
        if (this.user == null) {
            this.user = new User(this.data.getFlattrObject("owner"));
        }
        return this.user;
    }

    public String getCategoryId() {
        return this.data.get("category");
    }

    public void setCategory(CategoryId category) {
        this.data.put("category", category != null ? category.getCategoryId() : null);
        this.updatedKeys.add("category");
    }

    public String getDescription() {
        return this.data.get(PodDBAdapter.KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        this.data.put(PodDBAdapter.KEY_DESCRIPTION, description);
        this.updatedKeys.add(PodDBAdapter.KEY_DESCRIPTION);
    }

    public List<String> getTags() {
        if (this.tags == null) {
            this.tags = this.data.getStrings("tags");
        }
        return this.tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
        this.updatedKeys.add("tags");
    }

    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = this.data.getStrings("tags");
        }
        this.tags.add(tag);
        this.updatedKeys.add("tags");
    }

    public String getLanguageId() {
        return this.data.get(PodDBAdapter.KEY_LANGUAGE);
    }

    public void setLanguage(LanguageId language) {
        this.data.put(PodDBAdapter.KEY_LANGUAGE, language != null ? language.getLanguageId() : null);
        this.updatedKeys.add(PodDBAdapter.KEY_LANGUAGE);
    }

    public boolean isHidden() {
        return this.data.getBoolean("hidden");
    }

    public void setHidden(boolean hidden) {
        this.data.put("hidden", Boolean.valueOf(hidden));
        this.updatedKeys.add("hidden");
    }

    public boolean isFlattred() {
        return this.data.getBoolean("flattred");
    }

    public boolean isSubscribed() {
        return this.data.getBoolean("subscribed");
    }

    public Date getLastFlattr() {
        return this.data.getDate("last_flattr_at");
    }

    public Date getUpdated() {
        return this.data.getDate("updated_at");
    }

    public void merge(Submission submission) {
        if (submission.getUrl() != null) {
            if (!getUrl().equals(submission.getUrl())) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Thing URL '");
                stringBuilder.append(getUrl());
                stringBuilder.append("' cannot be changed to '");
                stringBuilder.append(submission.getUrl());
                stringBuilder.append("'");
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
        setCategory(submission.getCategory());
        setTitle(submission.getTitle());
        setDescription(submission.getDescription());
        setLanguage(submission.getLanguage());
        setTags(submission.getTags());
        if (submission.isHidden() != null) {
            setHidden(submission.isHidden().booleanValue());
        }
    }

    public FlattrObject toUpdate() {
        if (this.updatedKeys.isEmpty()) {
            return null;
        }
        this.data.putStrings("tags", this.tags);
        FlattrObject result = new FlattrObject();
        for (String key : this.updatedKeys) {
            if ("tags".equals(key)) {
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
                result.put(key, sb.toString());
            } else if (this.data.has(key)) {
                result.put(key, this.data.getObject(key));
            }
        }
        return result;
    }

    public String getQrPdfUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://flattr.com/thing/qr/");
        stringBuilder.append(getThingId());
        return stringBuilder.toString();
    }

    public boolean equals(Object obj) {
        String pk = getThingId();
        if (!(pk == null || obj == null)) {
            if (obj instanceof Thing) {
                return pk.equals(((Thing) obj).getThingId());
            }
        }
        return false;
    }

    public int hashCode() {
        String pk = getThingId();
        return pk != null ? pk.hashCode() : 0;
    }
}

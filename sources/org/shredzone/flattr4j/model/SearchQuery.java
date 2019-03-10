package org.shredzone.flattr4j.model;

import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.shredzone.flattr4j.connector.Connection;

public class SearchQuery implements Serializable {
    private static final long serialVersionUID = 8144711465654878363L;
    private ArrayList<CategoryId> categoryList = new ArrayList();
    private ArrayList<LanguageId> languageList = new ArrayList();
    private String query;
    private Order sort;
    private String tags;
    private String url;
    private String user;

    public enum Order {
        RELEVANCE,
        TREND,
        FLATTRS,
        FLATTRS_MONTH,
        FLATTRS_WEEK,
        FLATTRS_DAY
    }

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTags() {
        return this.tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addLanguage(LanguageId language) {
        if (language != null) {
            this.languageList.add(language);
        }
    }

    public Collection<LanguageId> getLanguages() {
        return this.languageList;
    }

    public void setLanguages(Collection<LanguageId> languages) {
        if (languages != null) {
            this.languageList = new ArrayList(languages);
            return;
        }
        throw new IllegalArgumentException("languages list must not be null");
    }

    public UserId getUser() {
        String str = this.user;
        return str != null ? User.withId(str) : null;
    }

    public void setUser(UserId user) {
        this.user = user.getUserId();
    }

    public Order getSort() {
        return this.sort;
    }

    public void setSort(Order sort) {
        this.sort = sort;
    }

    public void addCategory(CategoryId category) {
        if (category != null) {
            this.categoryList.add(category);
        }
    }

    public Collection<CategoryId> getCategories() {
        return this.categoryList;
    }

    public void setCategories(Collection<CategoryId> categories) {
        if (categories != null) {
            this.categoryList = new ArrayList(categories);
            return;
        }
        throw new IllegalArgumentException("categories list must not be null");
    }

    public SearchQuery query(String query) {
        setQuery(query);
        return this;
    }

    public SearchQuery tags(String tags) {
        setTags(tags);
        return this;
    }

    public SearchQuery language(LanguageId language) {
        addLanguage(language);
        return this;
    }

    public SearchQuery category(CategoryId category) {
        addCategory(category);
        return this;
    }

    public SearchQuery user(UserId user) {
        setUser(user);
        return this;
    }

    public SearchQuery sort(Order order) {
        setSort(order);
        return this;
    }

    public void setupConnection(Connection conn) {
        StringBuilder sb;
        Iterator it;
        String str = this.query;
        if (str != null) {
            conn.query("query", str);
        }
        str = this.tags;
        if (str != null) {
            conn.query("tags", str);
        }
        str = this.url;
        if (str != null) {
            conn.query("url", str);
        }
        if (!this.languageList.isEmpty()) {
            sb = new StringBuilder();
            it = this.languageList.iterator();
            while (it.hasNext()) {
                LanguageId lid = (LanguageId) it.next();
                sb.append(',');
                sb.append(lid.getLanguageId());
            }
            if (sb.length() > 0) {
                conn.query(PodDBAdapter.KEY_LANGUAGE, sb.substring(1));
            }
        }
        if (!this.categoryList.isEmpty()) {
            sb = new StringBuilder();
            it = this.categoryList.iterator();
            while (it.hasNext()) {
                CategoryId cid = (CategoryId) it.next();
                sb.append(',');
                sb.append(cid.getCategoryId());
            }
            if (sb.length() > 0) {
                conn.query("category", sb.substring(1));
            }
        }
        str = this.user;
        if (str != null) {
            conn.query("user", str);
        }
        Order order = this.sort;
        if (order != null) {
            conn.query("sort", order.name().toLowerCase());
        }
    }
}

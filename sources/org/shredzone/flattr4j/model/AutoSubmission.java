package org.shredzone.flattr4j.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AutoSubmission extends Submission implements UserId, UserIdentifier {
    private static final String ENCODING = "utf-8";
    private static final long serialVersionUID = 469255989509420133L;
    private UserIdentifier identifier;
    private String userId;

    public UserId getUser() {
        String str = this.userId;
        return str != null ? User.withId(str) : null;
    }

    public void setUser(UserId user) {
        this.userId = user.getUserId();
    }

    public String getUserId() {
        return this.userId;
    }

    public UserIdentifier getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(UserIdentifier identifier) {
        this.identifier = identifier;
    }

    public String getUserIdentifier() {
        UserIdentifier userIdentifier = this.identifier;
        return userIdentifier != null ? userIdentifier.getUserIdentifier() : null;
    }

    public String toUrl() {
        if (this.userId == null) {
            if (this.identifier == null) {
                throw new IllegalArgumentException("Anonymous submissions are not allowed");
            }
        }
        if (this.userId != null) {
            if (this.identifier != null) {
                throw new IllegalArgumentException("Either user or identifier must be set, but not both");
            }
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("https://flattr.com/submit/auto");
            if (getUserId() != null) {
                sb.append("?user_id=");
                sb.append(URLEncoder.encode(getUserId(), ENCODING));
            } else {
                sb.append("?owner=");
                sb.append(URLEncoder.encode(getUserIdentifier(), ENCODING));
            }
            sb.append("&url=");
            sb.append(URLEncoder.encode(getUrl(), ENCODING));
            if (getCategory() != null) {
                sb.append("&category=");
                sb.append(URLEncoder.encode(getCategory().getCategoryId(), ENCODING));
            }
            if (getLanguage() != null) {
                sb.append("&language=");
                sb.append(URLEncoder.encode(getLanguage().getLanguageId(), ENCODING));
            }
            if (getTitle() != null) {
                sb.append("&title=");
                sb.append(URLEncoder.encode(getTitle(), ENCODING));
            }
            if (isHidden() != null && isHidden().booleanValue()) {
                sb.append("&hidden=1");
            }
            if (getTags() != null && !getTags().isEmpty()) {
                sb.append("&tags=");
                sb.append(URLEncoder.encode(getTagsAsString(), ENCODING));
            }
            if (getDescription() != null) {
                sb.append("&description=");
                sb.append(URLEncoder.encode(getDescription(), ENCODING));
            }
            return sb.toString();
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }
}

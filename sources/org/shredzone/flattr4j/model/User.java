package org.shredzone.flattr4j.model;

import android.support.v4.app.NotificationCompat;
import de.danoeh.antennapod.activity.PreferenceActivity;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.util.Date;
import org.shredzone.flattr4j.connector.FlattrObject;
import org.shredzone.flattr4j.oauth.RequiredScope;
import org.shredzone.flattr4j.oauth.Scope;

public class User extends Resource implements UserId {
    private static final long serialVersionUID = 594781523400164895L;

    public static UserId withId(String id) {
        return new User$1(id);
    }

    public User(FlattrObject data) {
        super(data);
    }

    public String getUserId() {
        return this.data.get(PodDBAdapter.KEY_USERNAME);
    }

    public String getIdentifier() {
        return this.data.get("id");
    }

    public String getUsername() {
        return this.data.get(PodDBAdapter.KEY_USERNAME);
    }

    public String getResource() {
        return this.data.get(PreferenceActivity.PARAM_RESOURCE);
    }

    public String getLink() {
        return this.data.get(PodDBAdapter.KEY_LINK);
    }

    public String getFirstname() {
        return this.data.get("firstname");
    }

    public String getLastname() {
        return this.data.get("lastname");
    }

    public String getCity() {
        return this.data.get("city");
    }

    public String getCountry() {
        return this.data.get("country");
    }

    public String getUrl() {
        return this.data.get("url");
    }

    @RequiredScope({Scope.EXTENDEDREAD})
    public String getEmail() {
        return this.data.get(NotificationCompat.CATEGORY_EMAIL);
    }

    public String getDescription() {
        return this.data.get("about");
    }

    public String getGravatar() {
        return this.data.get("avatar");
    }

    public boolean isActiveSupporter() {
        return this.data.getInt("active_supporter") == 1;
    }

    @RequiredScope({Scope.EXTENDEDREAD})
    public Date getRegisteredAt() {
        return this.data.getDate("registered_at");
    }

    public boolean equals(Object obj) {
        String pk = getUserId();
        if (!(pk == null || obj == null)) {
            if (obj instanceof User) {
                return pk.equals(((User) obj).getUserId());
            }
        }
        return false;
    }

    public int hashCode() {
        String pk = getUserId();
        return pk != null ? pk.hashCode() : 0;
    }
}

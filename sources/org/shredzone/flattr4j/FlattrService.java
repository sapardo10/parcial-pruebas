package org.shredzone.flattr4j;

import java.util.Collection;
import java.util.List;
import org.shredzone.flattr4j.connector.RateLimit;
import org.shredzone.flattr4j.exception.FlattrException;
import org.shredzone.flattr4j.model.Activity;
import org.shredzone.flattr4j.model.Activity.Type;
import org.shredzone.flattr4j.model.AutoSubmission;
import org.shredzone.flattr4j.model.Category;
import org.shredzone.flattr4j.model.Flattr;
import org.shredzone.flattr4j.model.Language;
import org.shredzone.flattr4j.model.MiniThing;
import org.shredzone.flattr4j.model.SearchQuery;
import org.shredzone.flattr4j.model.SearchResult;
import org.shredzone.flattr4j.model.Submission;
import org.shredzone.flattr4j.model.Subscription;
import org.shredzone.flattr4j.model.Thing;
import org.shredzone.flattr4j.model.ThingId;
import org.shredzone.flattr4j.model.User;
import org.shredzone.flattr4j.model.UserId;
import org.shredzone.flattr4j.oauth.RequiredScope;
import org.shredzone.flattr4j.oauth.Scope;

public interface FlattrService {
    @RequiredScope({Scope.THING})
    ThingId create(Submission submission) throws FlattrException;

    @RequiredScope({Scope.THING})
    void delete(ThingId thingId) throws FlattrException;

    @RequiredScope({Scope.FLATTR})
    MiniThing flattr(String str) throws FlattrException;

    @RequiredScope({Scope.FLATTR})
    MiniThing flattr(AutoSubmission autoSubmission) throws FlattrException;

    @RequiredScope({Scope.FLATTR})
    MiniThing flattr(ThingId thingId) throws FlattrException;

    List<Activity> getActivities(UserId userId, Type type) throws FlattrException;

    List<Category> getCategories() throws FlattrException;

    RateLimit getCurrentRateLimit() throws FlattrException;

    List<Flattr> getFlattrs(ThingId thingId) throws FlattrException;

    List<Flattr> getFlattrs(ThingId thingId, Integer num, Integer num2) throws FlattrException;

    List<Flattr> getFlattrs(UserId userId) throws FlattrException;

    List<Flattr> getFlattrs(UserId userId, Integer num, Integer num2) throws FlattrException;

    List<Language> getLanguages() throws FlattrException;

    RateLimit getLastRateLimit();

    @RequiredScope
    List<Activity> getMyActivities(Type type) throws FlattrException;

    @RequiredScope
    List<Flattr> getMyFlattrs() throws FlattrException;

    @RequiredScope
    List<Flattr> getMyFlattrs(Integer num, Integer num2) throws FlattrException;

    @RequiredScope({Scope.FLATTR})
    List<Subscription> getMySubscriptions() throws FlattrException;

    @RequiredScope
    List<Thing> getMyThings() throws FlattrException;

    @RequiredScope
    List<Thing> getMyThings(Integer num, Integer num2) throws FlattrException;

    @RequiredScope
    User getMyself() throws FlattrException;

    @RequiredScope({Scope.FLATTR})
    Subscription getSubscription(ThingId thingId) throws FlattrException;

    Thing getThing(ThingId thingId) throws FlattrException;

    Thing getThingBySubmission(AutoSubmission autoSubmission) throws FlattrException;

    Thing getThingByUrl(String str) throws FlattrException;

    List<Thing> getThings(Collection<? extends ThingId> collection) throws FlattrException;

    List<Thing> getThings(UserId userId) throws FlattrException;

    List<Thing> getThings(UserId userId, Integer num, Integer num2) throws FlattrException;

    User getUser(UserId userId) throws FlattrException;

    boolean isFullMode();

    @RequiredScope({Scope.FLATTR})
    void pauseSubscription(ThingId thingId, boolean z) throws FlattrException;

    SearchResult searchThings(SearchQuery searchQuery, Integer num, Integer num2) throws FlattrException;

    void setFullMode(boolean z);

    @RequiredScope({Scope.FLATTR})
    void subscribe(ThingId thingId) throws FlattrException;

    @RequiredScope({Scope.FLATTR})
    boolean toggleSubscription(ThingId thingId) throws FlattrException;

    @RequiredScope({Scope.FLATTR})
    void unsubscribe(ThingId thingId) throws FlattrException;

    @RequiredScope({Scope.THING})
    void update(Thing thing) throws FlattrException;
}

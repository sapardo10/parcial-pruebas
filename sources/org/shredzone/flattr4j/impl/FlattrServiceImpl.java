package org.shredzone.flattr4j.impl;

import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.shredzone.flattr4j.FlattrService;
import org.shredzone.flattr4j.connector.Connection;
import org.shredzone.flattr4j.connector.Connector;
import org.shredzone.flattr4j.connector.FlattrObject;
import org.shredzone.flattr4j.connector.RateLimit;
import org.shredzone.flattr4j.connector.RequestType;
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

public class FlattrServiceImpl implements FlattrService {
    private final Connector connector;
    private boolean fullMode = false;
    private RateLimit lastRateLimit = new RateLimit();

    public java.util.List<org.shredzone.flattr4j.model.Activity> getActivities(org.shredzone.flattr4j.model.UserId r7, org.shredzone.flattr4j.model.Activity.Type r8) throws org.shredzone.flattr4j.exception.FlattrException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0071 in {5, 6, 10, 12, 14} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r6 = this;
        if (r7 == 0) goto L_0x0068;
    L_0x0002:
        r0 = r7.getUserId();
        r0 = r0.length();
        if (r0 == 0) goto L_0x0068;
    L_0x000c:
        r0 = r6.getConnector();
        r0 = r0.create();
        r1 = "users/:username/activities.as";
        r0 = r0.call(r1);
        r1 = "username";
        r2 = r7.getUserId();
        r0 = r0.parameter(r1, r2);
        r1 = r6.lastRateLimit;
        r0 = r0.rateLimit(r1);
        if (r8 == 0) goto L_0x003a;
    L_0x002c:
        r1 = "type";
        r2 = r8.name();
        r2 = r2.toLowerCase();
        r0.query(r1, r2);
        goto L_0x003b;
    L_0x003b:
        r1 = r0.singleResult();
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = "items";
        r3 = r1.getObjects(r3);
        r3 = r3.iterator();
    L_0x004e:
        r4 = r3.hasNext();
        if (r4 == 0) goto L_0x0063;
    L_0x0054:
        r4 = r3.next();
        r4 = (org.shredzone.flattr4j.connector.FlattrObject) r4;
        r5 = new org.shredzone.flattr4j.model.Activity;
        r5.<init>(r4);
        r2.add(r5);
        goto L_0x004e;
    L_0x0063:
        r3 = java.util.Collections.unmodifiableList(r2);
        return r3;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "userId is required";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.shredzone.flattr4j.impl.FlattrServiceImpl.getActivities(org.shredzone.flattr4j.model.UserId, org.shredzone.flattr4j.model.Activity$Type):java.util.List<org.shredzone.flattr4j.model.Activity>");
    }

    public java.util.List<org.shredzone.flattr4j.model.Flattr> getFlattrs(org.shredzone.flattr4j.model.ThingId r6, java.lang.Integer r7, java.lang.Integer r8) throws org.shredzone.flattr4j.exception.FlattrException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x0077 in {5, 6, 8, 9, 13, 15, 17} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r5 = this;
        if (r6 == 0) goto L_0x006e;
    L_0x0002:
        r0 = r6.getThingId();
        r0 = r0.length();
        if (r0 == 0) goto L_0x006e;
    L_0x000c:
        r0 = r5.getConnector();
        r0 = r0.create();
        r1 = "things/:id/flattrs";
        r0 = r0.call(r1);
        r1 = "id";
        r2 = r6.getThingId();
        r0 = r0.parameter(r1, r2);
        r1 = r5.lastRateLimit;
        r0 = r0.rateLimit(r1);
        r5.setupFullMode(r0);
        if (r7 == 0) goto L_0x0039;
    L_0x002f:
        r1 = "count";
        r2 = r7.toString();
        r0.query(r1, r2);
        goto L_0x003a;
    L_0x003a:
        if (r8 == 0) goto L_0x0046;
    L_0x003c:
        r1 = "page";
        r2 = r8.toString();
        r0.query(r1, r2);
        goto L_0x0047;
    L_0x0047:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = r0.result();
        r2 = r2.iterator();
    L_0x0054:
        r3 = r2.hasNext();
        if (r3 == 0) goto L_0x0069;
    L_0x005a:
        r3 = r2.next();
        r3 = (org.shredzone.flattr4j.connector.FlattrObject) r3;
        r4 = new org.shredzone.flattr4j.model.Flattr;
        r4.<init>(r3);
        r1.add(r4);
        goto L_0x0054;
    L_0x0069:
        r2 = java.util.Collections.unmodifiableList(r1);
        return r2;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "thingId is required";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.shredzone.flattr4j.impl.FlattrServiceImpl.getFlattrs(org.shredzone.flattr4j.model.ThingId, java.lang.Integer, java.lang.Integer):java.util.List<org.shredzone.flattr4j.model.Flattr>");
    }

    public java.util.List<org.shredzone.flattr4j.model.Flattr> getFlattrs(org.shredzone.flattr4j.model.UserId r6, java.lang.Integer r7, java.lang.Integer r8) throws org.shredzone.flattr4j.exception.FlattrException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x0077 in {5, 6, 8, 9, 13, 15, 17} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r5 = this;
        if (r6 == 0) goto L_0x006e;
    L_0x0002:
        r0 = r6.getUserId();
        r0 = r0.length();
        if (r0 == 0) goto L_0x006e;
    L_0x000c:
        r0 = r5.getConnector();
        r0 = r0.create();
        r1 = "users/:username/flattrs";
        r0 = r0.call(r1);
        r1 = "username";
        r2 = r6.getUserId();
        r0 = r0.parameter(r1, r2);
        r1 = r5.lastRateLimit;
        r0 = r0.rateLimit(r1);
        r5.setupFullMode(r0);
        if (r7 == 0) goto L_0x0039;
    L_0x002f:
        r1 = "count";
        r2 = r7.toString();
        r0.query(r1, r2);
        goto L_0x003a;
    L_0x003a:
        if (r8 == 0) goto L_0x0046;
    L_0x003c:
        r1 = "page";
        r2 = r8.toString();
        r0.query(r1, r2);
        goto L_0x0047;
    L_0x0047:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = r0.result();
        r2 = r2.iterator();
    L_0x0054:
        r3 = r2.hasNext();
        if (r3 == 0) goto L_0x0069;
    L_0x005a:
        r3 = r2.next();
        r3 = (org.shredzone.flattr4j.connector.FlattrObject) r3;
        r4 = new org.shredzone.flattr4j.model.Flattr;
        r4.<init>(r3);
        r1.add(r4);
        goto L_0x0054;
    L_0x0069:
        r2 = java.util.Collections.unmodifiableList(r1);
        return r2;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "userId is required";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.shredzone.flattr4j.impl.FlattrServiceImpl.getFlattrs(org.shredzone.flattr4j.model.UserId, java.lang.Integer, java.lang.Integer):java.util.List<org.shredzone.flattr4j.model.Flattr>");
    }

    public org.shredzone.flattr4j.model.Subscription getSubscription(org.shredzone.flattr4j.model.ThingId r7) throws org.shredzone.flattr4j.exception.FlattrException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0061 in {9, 10, 12, 14} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r6 = this;
        if (r7 == 0) goto L_0x0058;
    L_0x0002:
        r0 = r7.getThingId();
        r0 = r0.length();
        if (r0 == 0) goto L_0x0058;
    L_0x000c:
        r0 = r6.getConnector();
        r0 = r0.create();
        r1 = "user/subscriptions";
        r0 = r0.call(r1);
        r1 = r6.lastRateLimit;
        r0 = r0.rateLimit(r1);
        r1 = r0.result();
        r1 = r1.iterator();
    L_0x0028:
        r2 = r1.hasNext();
        if (r2 == 0) goto L_0x0056;
    L_0x002e:
        r2 = r1.next();
        r2 = (org.shredzone.flattr4j.connector.FlattrObject) r2;
        r3 = r7.getThingId();
        r4 = "thing";
        r4 = r2.getFlattrObject(r4);
        r5 = "id";
        r4 = r4.getInt(r5);
        r4 = java.lang.String.valueOf(r4);
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x0054;
    L_0x004e:
        r1 = new org.shredzone.flattr4j.model.Subscription;
        r1.<init>(r2);
        return r1;
        goto L_0x0028;
    L_0x0056:
        r1 = 0;
        return r1;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "thingId is required";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.shredzone.flattr4j.impl.FlattrServiceImpl.getSubscription(org.shredzone.flattr4j.model.ThingId):org.shredzone.flattr4j.model.Subscription");
    }

    public java.util.List<org.shredzone.flattr4j.model.Thing> getThings(org.shredzone.flattr4j.model.UserId r6, java.lang.Integer r7, java.lang.Integer r8) throws org.shredzone.flattr4j.exception.FlattrException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x0077 in {5, 6, 8, 9, 13, 15, 17} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r5 = this;
        if (r6 == 0) goto L_0x006e;
    L_0x0002:
        r0 = r6.getUserId();
        r0 = r0.length();
        if (r0 == 0) goto L_0x006e;
    L_0x000c:
        r0 = r5.getConnector();
        r0 = r0.create();
        r1 = "users/:username/things";
        r0 = r0.call(r1);
        r1 = "username";
        r2 = r6.getUserId();
        r0 = r0.parameter(r1, r2);
        r1 = r5.lastRateLimit;
        r0 = r0.rateLimit(r1);
        r5.setupFullMode(r0);
        if (r7 == 0) goto L_0x0039;
    L_0x002f:
        r1 = "count";
        r2 = r7.toString();
        r0.query(r1, r2);
        goto L_0x003a;
    L_0x003a:
        if (r8 == 0) goto L_0x0046;
    L_0x003c:
        r1 = "page";
        r2 = r8.toString();
        r0.query(r1, r2);
        goto L_0x0047;
    L_0x0047:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = r0.result();
        r2 = r2.iterator();
    L_0x0054:
        r3 = r2.hasNext();
        if (r3 == 0) goto L_0x0069;
    L_0x005a:
        r3 = r2.next();
        r3 = (org.shredzone.flattr4j.connector.FlattrObject) r3;
        r4 = new org.shredzone.flattr4j.model.Thing;
        r4.<init>(r3);
        r1.add(r4);
        goto L_0x0054;
    L_0x0069:
        r2 = java.util.Collections.unmodifiableList(r1);
        return r2;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "user is required";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.shredzone.flattr4j.impl.FlattrServiceImpl.getThings(org.shredzone.flattr4j.model.UserId, java.lang.Integer, java.lang.Integer):java.util.List<org.shredzone.flattr4j.model.Thing>");
    }

    public FlattrServiceImpl(Connector connector) {
        this.connector = connector;
    }

    protected Connector getConnector() {
        return this.connector;
    }

    public void setFullMode(boolean full) {
        this.fullMode = full;
    }

    public boolean isFullMode() {
        return this.fullMode;
    }

    public ThingId create(Submission thing) throws FlattrException {
        if (thing != null) {
            if (thing instanceof AutoSubmission) {
                if (((AutoSubmission) thing).getUser() != null) {
                    throw new IllegalArgumentException("cannot create a thing on behalf of a user");
                }
            }
            return Thing.withId(String.valueOf(getConnector().create(RequestType.POST).call("things").data(thing.toFlattrObject()).rateLimit(this.lastRateLimit).singleResult().getInt("id")));
        }
        throw new IllegalArgumentException("thing is required");
    }

    public void update(Thing thing) throws FlattrException {
        if (thing != null) {
            FlattrObject update = thing.toUpdate();
            if (update != null) {
                update.getJSONObject().put("_method", "patch");
                getConnector().create(RequestType.POST).call("things/:id").parameter("id", thing.getThingId()).rateLimit(this.lastRateLimit).data(update).result();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("thing is required");
    }

    public void delete(ThingId thingId) throws FlattrException {
        if (thingId == null || thingId.getThingId().length() == 0) {
            throw new IllegalArgumentException("thing id is required");
        }
        getConnector().create(RequestType.DELETE).call("things/:id").parameter("id", thingId.getThingId()).rateLimit(this.lastRateLimit).result();
    }

    public MiniThing flattr(AutoSubmission submission) throws FlattrException {
        return flattr(submission.toUrl());
    }

    public MiniThing flattr(String url) throws FlattrException {
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("url is required");
        }
        FlattrObject data = new FlattrObject();
        data.put("url", url);
        return new MiniThing(getConnector().create(RequestType.POST).call("flattr").data(data).rateLimit(this.lastRateLimit).singleResult().getFlattrObject("thing"));
    }

    public MiniThing flattr(ThingId thingId) throws FlattrException {
        if (thingId != null && thingId.getThingId().length() != 0) {
            return new MiniThing(getConnector().create(RequestType.POST).call("things/:id/flattr").parameter("id", thingId.getThingId()).rateLimit(this.lastRateLimit).singleResult().getFlattrObject("thing"));
        }
        throw new IllegalArgumentException("thingId is required");
    }

    public User getMyself() throws FlattrException {
        return new User(getConnector().create().call("user").rateLimit(this.lastRateLimit).singleResult());
    }

    public List<Thing> getMyThings() throws FlattrException {
        return getMyThings(null, null);
    }

    public List<Thing> getMyThings(Integer count, Integer page) throws FlattrException {
        Connection conn = getConnector().create().call("user/things").rateLimit(this.lastRateLimit);
        setupFullMode(conn);
        if (count != null) {
            conn.query("count", count.toString());
        }
        if (page != null) {
            conn.query(DownloadRequester.REQUEST_ARG_PAGE_NR, page.toString());
        }
        List<Thing> list = new ArrayList();
        for (FlattrObject data : conn.result()) {
            list.add(new Thing(data));
        }
        return Collections.unmodifiableList(list);
    }

    public List<Flattr> getMyFlattrs() throws FlattrException {
        return getMyFlattrs(null, null);
    }

    public List<Flattr> getMyFlattrs(Integer count, Integer page) throws FlattrException {
        Connection conn = getConnector().create().call("user/flattrs").rateLimit(this.lastRateLimit);
        setupFullMode(conn);
        if (count != null) {
            conn.query("count", count.toString());
        }
        if (page != null) {
            conn.query(DownloadRequester.REQUEST_ARG_PAGE_NR, page.toString());
        }
        List<Flattr> list = new ArrayList();
        for (FlattrObject data : conn.result()) {
            list.add(new Flattr(data));
        }
        return Collections.unmodifiableList(list);
    }

    public Thing getThing(ThingId thingId) throws FlattrException {
        if (thingId == null || thingId.getThingId().length() == 0) {
            throw new IllegalArgumentException("thingId is required");
        }
        Connection conn = getConnector().create().call("things/:id").parameter("id", thingId.getThingId()).rateLimit(this.lastRateLimit);
        setupFullMode(conn);
        return new Thing(conn.singleResult());
    }

    public Thing getThingByUrl(String url) throws FlattrException {
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("url is required");
        }
        FlattrObject data = getConnector().create().call("things/lookup/").query("url", url).rateLimit(this.lastRateLimit).singleResult();
        if (data.has("message") && "not_found".equals(data.get("message"))) {
            return null;
        }
        return new Thing(data);
    }

    public Thing getThingBySubmission(AutoSubmission submission) throws FlattrException {
        return getThingByUrl(submission.toUrl());
    }

    public List<Thing> getThings(UserId user) throws FlattrException {
        return getThings(user, null, null);
    }

    public List<Thing> getThings(Collection<? extends ThingId> thingIds) throws FlattrException {
        if (thingIds.isEmpty()) {
            return Collections.emptyList();
        }
        String[] params = new String[thingIds.size()];
        int ix = 0;
        for (ThingId thingId : thingIds) {
            int ix2 = ix + 1;
            params[ix] = thingId.getThingId();
            ix = ix2;
        }
        Connection conn = getConnector().create().call("things/:ids").parameterArray("ids", params).rateLimit(this.lastRateLimit);
        setupFullMode(conn);
        List<Thing> list = new ArrayList();
        for (FlattrObject data : conn.result()) {
            list.add(new Thing(data));
        }
        return Collections.unmodifiableList(list);
    }

    public SearchResult searchThings(SearchQuery query, Integer count, Integer page) throws FlattrException {
        Connection conn = getConnector().create().call("things/search").rateLimit(this.lastRateLimit);
        if (query != null) {
            query.setupConnection(conn);
        }
        setupFullMode(conn);
        if (count != null) {
            conn.query("count", count.toString());
        }
        if (page != null) {
            conn.query(DownloadRequester.REQUEST_ARG_PAGE_NR, page.toString());
        }
        return new SearchResult(conn.singleResult());
    }

    public User getUser(UserId user) throws FlattrException {
        if (user != null && user.getUserId().length() != 0) {
            return new User(getConnector().create().call("users/:username").parameter(PodDBAdapter.KEY_USERNAME, user.getUserId()).rateLimit(this.lastRateLimit).singleResult());
        }
        throw new IllegalArgumentException("user is required");
    }

    public List<Flattr> getFlattrs(UserId user) throws FlattrException {
        return getFlattrs(user, null, null);
    }

    public List<Flattr> getFlattrs(ThingId thingId) throws FlattrException {
        return getFlattrs(thingId, null, null);
    }

    public List<Activity> getMyActivities(Type type) throws FlattrException {
        Connection conn = getConnector().create().call("user/activities.as").rateLimit(this.lastRateLimit);
        if (type != null) {
            conn.query("type", type.name().toLowerCase());
        }
        FlattrObject data = conn.singleResult();
        List<Activity> list = new ArrayList();
        for (FlattrObject item : data.getObjects("items")) {
            list.add(new Activity(item));
        }
        return Collections.unmodifiableList(list);
    }

    public List<Subscription> getMySubscriptions() throws FlattrException {
        Connection conn = getConnector().create().call("user/subscriptions").rateLimit(this.lastRateLimit);
        List<Subscription> list = new ArrayList();
        for (FlattrObject item : conn.result()) {
            list.add(new Subscription(item));
        }
        return Collections.unmodifiableList(list);
    }

    public void subscribe(ThingId thingId) throws FlattrException {
        if (thingId == null || thingId.getThingId().length() == 0) {
            throw new IllegalArgumentException("thingId is required");
        }
        getConnector().create(RequestType.POST).call("things/:id/subscriptions").parameter("id", thingId.getThingId()).rateLimit(this.lastRateLimit).result();
    }

    public void unsubscribe(ThingId thingId) throws FlattrException {
        if (thingId == null || thingId.getThingId().length() == 0) {
            throw new IllegalArgumentException("thingId is required");
        }
        getConnector().create(RequestType.DELETE).call("things/:id/subscriptions").parameter("id", thingId.getThingId()).rateLimit(this.lastRateLimit).result();
    }

    public boolean toggleSubscription(ThingId thingId) throws FlattrException {
        if (thingId != null && thingId.getThingId().length() != 0) {
            return "paused".equals(getConnector().create(RequestType.PUT).call("things/:id/subscriptions").parameter("id", thingId.getThingId()).rateLimit(this.lastRateLimit).singleResult().get("message"));
        }
        throw new IllegalArgumentException("thingId is required");
    }

    public void pauseSubscription(ThingId thingId, boolean paused) throws FlattrException {
        if (toggleSubscription(thingId) != paused) {
            toggleSubscription(thingId);
        }
    }

    public List<Category> getCategories() throws FlattrException {
        Connection conn = getConnector().create().call("categories").rateLimit(this.lastRateLimit);
        List<Category> list = new ArrayList();
        for (FlattrObject data : conn.result()) {
            list.add(new Category(data));
        }
        return Collections.unmodifiableList(list);
    }

    public List<Language> getLanguages() throws FlattrException {
        Connection conn = getConnector().create().call("languages").rateLimit(this.lastRateLimit);
        List<Language> list = new ArrayList();
        for (FlattrObject data : conn.result()) {
            list.add(new Language(data));
        }
        return Collections.unmodifiableList(list);
    }

    public RateLimit getCurrentRateLimit() throws FlattrException {
        return new RateLimit(getConnector().create().call("rate_limit").singleResult());
    }

    public RateLimit getLastRateLimit() {
        return this.lastRateLimit;
    }

    protected void setupFullMode(Connection conn) {
        if (this.fullMode) {
            conn.query("full", "1");
        }
    }
}

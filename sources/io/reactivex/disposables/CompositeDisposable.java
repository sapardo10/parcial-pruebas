package io.reactivex.disposables;

import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.disposables.DisposableContainer;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.internal.util.OpenHashSet;
import java.util.ArrayList;

public final class CompositeDisposable implements Disposable, DisposableContainer {
    volatile boolean disposed;
    OpenHashSet<Disposable> resources;

    public CompositeDisposable(@NonNull Disposable... resources) {
        ObjectHelper.requireNonNull((Object) resources, "resources is null");
        this.resources = new OpenHashSet(resources.length + 1);
        for (Object d : resources) {
            ObjectHelper.requireNonNull(d, "Disposable item is null");
            this.resources.add(d);
        }
    }

    public CompositeDisposable(@NonNull Iterable<? extends Disposable> resources) {
        ObjectHelper.requireNonNull((Object) resources, "resources is null");
        this.resources = new OpenHashSet();
        for (Object d : resources) {
            ObjectHelper.requireNonNull(d, "Disposable item is null");
            this.resources.add(d);
        }
    }

    public void dispose() {
        if (!this.disposed) {
            synchronized (this) {
                if (this.disposed) {
                    return;
                }
                this.disposed = true;
                OpenHashSet<Disposable> set = this.resources;
                this.resources = null;
                dispose(set);
            }
        }
    }

    public boolean isDisposed() {
        return this.disposed;
    }

    public boolean add(@NonNull Disposable d) {
        ObjectHelper.requireNonNull((Object) d, "d is null");
        if (!this.disposed) {
            synchronized (this) {
                if (!this.disposed) {
                    OpenHashSet<Disposable> set = this.resources;
                    if (set == null) {
                        set = new OpenHashSet();
                        this.resources = set;
                    }
                    set.add(d);
                    return true;
                }
            }
        }
        d.dispose();
        return false;
    }

    public boolean addAll(@NonNull Disposable... ds) {
        ObjectHelper.requireNonNull((Object) ds, "ds is null");
        int i = 0;
        if (!this.disposed) {
            synchronized (this) {
                if (!this.disposed) {
                    OpenHashSet<Disposable> set = this.resources;
                    if (set == null) {
                        set = new OpenHashSet(ds.length + 1);
                        this.resources = set;
                    }
                    int length = ds.length;
                    while (i < length) {
                        Object d = ds[i];
                        ObjectHelper.requireNonNull(d, "d is null");
                        set.add(d);
                        i++;
                    }
                    return true;
                }
            }
        }
        for (Disposable d2 : ds) {
            d2.dispose();
        }
        return false;
    }

    public boolean remove(@NonNull Disposable d) {
        if (!delete(d)) {
            return false;
        }
        d.dispose();
        return true;
    }

    public boolean delete(@NonNull Disposable d) {
        ObjectHelper.requireNonNull((Object) d, "Disposable item is null");
        if (this.disposed) {
            return false;
        }
        synchronized (this) {
            if (this.disposed) {
                return false;
            }
            OpenHashSet<Disposable> set = this.resources;
            if (set != null) {
                if (set.remove(d)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void clear() {
        if (!this.disposed) {
            synchronized (this) {
                if (this.disposed) {
                    return;
                }
                OpenHashSet<Disposable> set = this.resources;
                this.resources = null;
                dispose(set);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int size() {
        /*
        r2 = this;
        r0 = r2.disposed;
        r1 = 0;
        if (r0 == 0) goto L_0x0006;
    L_0x0005:
        return r1;
    L_0x0006:
        monitor-enter(r2);
        r0 = r2.disposed;	 Catch:{ all -> 0x0018 }
        if (r0 == 0) goto L_0x000d;
    L_0x000b:
        monitor-exit(r2);	 Catch:{ all -> 0x0018 }
        return r1;
    L_0x000d:
        r0 = r2.resources;	 Catch:{ all -> 0x0018 }
        if (r0 == 0) goto L_0x0016;
    L_0x0011:
        r1 = r0.size();	 Catch:{ all -> 0x0018 }
    L_0x0016:
        monitor-exit(r2);	 Catch:{ all -> 0x0018 }
        return r1;
    L_0x0018:
        r0 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0018 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.disposables.CompositeDisposable.size():int");
    }

    void dispose(OpenHashSet<Disposable> set) {
        if (set != null) {
            Iterable errors = null;
            for (Object o : set.keys()) {
                if (o instanceof Disposable) {
                    try {
                        ((Disposable) o).dispose();
                    } catch (Throwable ex) {
                        Exceptions.throwIfFatal(ex);
                        if (errors == null) {
                            errors = new ArrayList();
                        }
                        errors.add(ex);
                    }
                }
            }
            if (errors == null) {
                return;
            }
            if (errors.size() == 1) {
                throw ExceptionHelper.wrapOrThrow((Throwable) errors.get(0));
            }
            throw new CompositeException(errors);
        }
    }
}

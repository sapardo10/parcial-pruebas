package io.reactivex.internal.disposables;

import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.util.ExceptionHelper;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class ListCompositeDisposable implements Disposable, DisposableContainer {
    volatile boolean disposed;
    List<Disposable> resources;

    public ListCompositeDisposable(Disposable... resources) {
        ObjectHelper.requireNonNull((Object) resources, "resources is null");
        this.resources = new LinkedList();
        for (Object d : resources) {
            ObjectHelper.requireNonNull(d, "Disposable item is null");
            this.resources.add(d);
        }
    }

    public ListCompositeDisposable(Iterable<? extends Disposable> resources) {
        ObjectHelper.requireNonNull((Object) resources, "resources is null");
        this.resources = new LinkedList();
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
                List<Disposable> set = this.resources;
                this.resources = null;
                dispose(set);
            }
        }
    }

    public boolean isDisposed() {
        return this.disposed;
    }

    public boolean add(Disposable d) {
        ObjectHelper.requireNonNull((Object) d, "d is null");
        if (!this.disposed) {
            synchronized (this) {
                if (!this.disposed) {
                    List<Disposable> set = this.resources;
                    if (set == null) {
                        set = new LinkedList();
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

    public boolean addAll(Disposable... ds) {
        int length;
        ObjectHelper.requireNonNull((Object) ds, "ds is null");
        int i = 0;
        if (!this.disposed) {
            synchronized (this) {
                if (!this.disposed) {
                    List<Disposable> set = this.resources;
                    if (set == null) {
                        set = new LinkedList();
                        this.resources = set;
                    }
                    length = ds.length;
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

    public boolean remove(Disposable d) {
        if (!delete(d)) {
            return false;
        }
        d.dispose();
        return true;
    }

    public boolean delete(Disposable d) {
        ObjectHelper.requireNonNull((Object) d, "Disposable item is null");
        if (this.disposed) {
            return false;
        }
        synchronized (this) {
            if (this.disposed) {
                return false;
            }
            List<Disposable> set = this.resources;
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
                List<Disposable> set = this.resources;
                this.resources = null;
                dispose(set);
            }
        }
    }

    void dispose(List<Disposable> set) {
        if (set != null) {
            Iterable errors = null;
            for (Disposable o : set) {
                try {
                    o.dispose();
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    if (errors == null) {
                        errors = new ArrayList();
                    }
                    errors.add(ex);
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

package android.support.v4.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.util.Pools.Pool;
import android.support.v4.util.Pools.SimplePool;
import android.support.v4.util.SimpleArrayMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RestrictTo({Scope.LIBRARY})
public final class DirectedAcyclicGraph<T> {
    private final SimpleArrayMap<T, ArrayList<T>> mGraph = new SimpleArrayMap();
    private final Pool<ArrayList<T>> mListPool = new SimplePool(10);
    private final ArrayList<T> mSortResult = new ArrayList();
    private final HashSet<T> mSortTmpMarked = new HashSet();

    private void dfs(T r5, java.util.ArrayList<T> r6, java.util.HashSet<T> r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x003c in {2, 9, 10, 11, 13, 15} preds:[]
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
        r4 = this;
        r0 = r6.contains(r5);
        if (r0 == 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r0 = r7.contains(r5);
        if (r0 != 0) goto L_0x0034;
    L_0x000d:
        r7.add(r5);
        r0 = r4.mGraph;
        r0 = r0.get(r5);
        r0 = (java.util.ArrayList) r0;
        if (r0 == 0) goto L_0x002c;
    L_0x001a:
        r1 = 0;
        r2 = r0.size();
    L_0x001f:
        if (r1 >= r2) goto L_0x002b;
    L_0x0021:
        r3 = r0.get(r1);
        r4.dfs(r3, r6, r7);
        r1 = r1 + 1;
        goto L_0x001f;
    L_0x002b:
        goto L_0x002d;
    L_0x002d:
        r7.remove(r5);
        r6.add(r5);
        return;
    L_0x0034:
        r0 = new java.lang.RuntimeException;
        r1 = "This graph contains cyclic dependencies";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.DirectedAcyclicGraph.dfs(java.lang.Object, java.util.ArrayList, java.util.HashSet):void");
    }

    public void addNode(@NonNull T node) {
        if (!this.mGraph.containsKey(node)) {
            this.mGraph.put(node, null);
        }
    }

    public boolean contains(@NonNull T node) {
        return this.mGraph.containsKey(node);
    }

    public void addEdge(@NonNull T node, @NonNull T incomingEdge) {
        if (this.mGraph.containsKey(node) && this.mGraph.containsKey(incomingEdge)) {
            ArrayList<T> edges = (ArrayList) this.mGraph.get(node);
            if (edges == null) {
                edges = getEmptyList();
                this.mGraph.put(node, edges);
            }
            edges.add(incomingEdge);
            return;
        }
        throw new IllegalArgumentException("All nodes must be present in the graph before being added as an edge");
    }

    @Nullable
    public List getIncomingEdges(@NonNull T node) {
        return (List) this.mGraph.get(node);
    }

    @Nullable
    public List<T> getOutgoingEdges(@NonNull T node) {
        ArrayList<T> result = null;
        int size = this.mGraph.size();
        for (int i = 0; i < size; i++) {
            ArrayList<T> edges = (ArrayList) this.mGraph.valueAt(i);
            if (edges != null && edges.contains(node)) {
                if (result == null) {
                    result = new ArrayList();
                }
                result.add(this.mGraph.keyAt(i));
            }
        }
        return result;
    }

    public boolean hasOutgoingEdges(@NonNull T node) {
        int size = this.mGraph.size();
        for (int i = 0; i < size; i++) {
            ArrayList<T> edges = (ArrayList) this.mGraph.valueAt(i);
            if (edges != null && edges.contains(node)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        int size = this.mGraph.size();
        for (int i = 0; i < size; i++) {
            ArrayList<T> edges = (ArrayList) this.mGraph.valueAt(i);
            if (edges != null) {
                poolList(edges);
            }
        }
        this.mGraph.clear();
    }

    @NonNull
    public ArrayList<T> getSortedList() {
        this.mSortResult.clear();
        this.mSortTmpMarked.clear();
        int size = this.mGraph.size();
        for (int i = 0; i < size; i++) {
            dfs(this.mGraph.keyAt(i), this.mSortResult, this.mSortTmpMarked);
        }
        return this.mSortResult;
    }

    int size() {
        return this.mGraph.size();
    }

    @NonNull
    private ArrayList<T> getEmptyList() {
        ArrayList<T> list = (ArrayList) this.mListPool.acquire();
        if (list == null) {
            return new ArrayList();
        }
        return list;
    }

    private void poolList(@NonNull ArrayList<T> list) {
        list.clear();
        this.mListPool.release(list);
    }
}

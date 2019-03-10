package android.support.wearable.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public final class RecyclerViewMergeAdapter extends Adapter<ViewHolder> {
    private static final long CHILD_ID_RANGE = 10000000000000000L;
    private static final int MAX_ADAPTER_ID = 922;
    private static final String TAG = "MergeAdapter";
    private final List<AdapterHolder> mAdapters = new ArrayList();
    private int mItemCount;
    private int mNextAdapterId;
    private int mNextViewTypeId;
    @Nullable
    private RecyclerView mRecyclerView;

    private static final class AdapterHolder {
        final Adapter adapter;
        final int adapterId;
        int adapterPosition;
        int itemPositionOffset;
        ForwardingDataSetObserver observer;
        SparseIntArray viewTypes;

        public AdapterHolder(int id, Adapter adapter) {
            this.adapter = adapter;
            this.adapterId = id;
        }
    }

    private static final class ForwardingDataSetObserver extends AdapterDataObserver {
        private final AdapterHolder mAdapterHolder;
        private final RecyclerViewMergeAdapter mMergedAdapter;

        public ForwardingDataSetObserver(RecyclerViewMergeAdapter parent, AdapterHolder holder) {
            this.mAdapterHolder = holder;
            this.mMergedAdapter = parent;
        }

        public void onChanged() {
            this.mMergedAdapter.updateItemPositionOffsets(0);
            this.mMergedAdapter.notifyDataSetChanged();
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            this.mMergedAdapter.notifyItemRangeChanged(this.mAdapterHolder.itemPositionOffset + positionStart, itemCount);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            this.mMergedAdapter.updateItemPositionOffsets(this.mAdapterHolder.adapterPosition);
            this.mMergedAdapter.notifyItemRangeInserted(this.mAdapterHolder.itemPositionOffset + positionStart, itemCount);
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            this.mMergedAdapter.updateItemPositionOffsets(this.mAdapterHolder.adapterPosition);
            this.mMergedAdapter.notifyItemRangeRemoved(this.mAdapterHolder.itemPositionOffset + positionStart, itemCount);
        }
    }

    private int getAdapterIndexForPosition(int r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x003d in {6, 7, 9} preds:[]
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
        r0 = r6.mAdapters;
        r0 = r0.size();
        r1 = 0;
    L_0x0007:
        if (r1 >= r0) goto L_0x0024;
    L_0x0009:
        r2 = r6.mAdapters;
        r2 = r2.get(r1);
        r2 = (android.support.wearable.view.RecyclerViewMergeAdapter.AdapterHolder) r2;
        r3 = r2.itemPositionOffset;
        r4 = r2.adapter;
        r4 = r4.getItemCount();
        if (r7 < r3) goto L_0x0020;
    L_0x001b:
        r5 = r3 + r4;
        if (r7 >= r5) goto L_0x0020;
    L_0x001f:
        return r1;
        r1 = r1 + 1;
        goto L_0x0007;
    L_0x0024:
        r1 = new java.lang.IllegalStateException;
        r2 = 46;
        r3 = new java.lang.StringBuilder;
        r3.<init>(r2);
        r2 = "No adapter appears to own position ";
        r3.append(r2);
        r3.append(r7);
        r2 = r3.toString();
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.wearable.view.RecyclerViewMergeAdapter.getAdapterIndexForPosition(int):int");
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
    }

    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.mRecyclerView = null;
    }

    public void setHasStableIds(boolean hasStableIds) {
        if (hasStableIds) {
            int len = this.mAdapters.size();
            int i = 0;
            while (i < len) {
                if (((AdapterHolder) this.mAdapters.get(i)).adapter.hasStableIds()) {
                    i++;
                } else {
                    throw new IllegalStateException("All child adapters must have stable IDs when hasStableIds=true");
                }
            }
        }
        super.setHasStableIds(hasStableIds);
    }

    public void addAdapter(Adapter adapter) {
        addAdapter(this.mAdapters.size(), adapter);
    }

    public void addAdapter(int adapterPosition, Adapter<?> adapter) {
        if (this.mNextAdapterId != MAX_ADAPTER_ID) {
            if (hasStableIds()) {
                if (!adapter.hasStableIds()) {
                    throw new IllegalStateException("All child adapters must have stable IDs when hasStableIds=true");
                }
            }
            AdapterHolder adapterHolder = new AdapterHolder(this.mNextAdapterId, adapter);
            this.mNextAdapterId++;
            adapterHolder.observer = new ForwardingDataSetObserver(this, adapterHolder);
            adapterHolder.adapterPosition = adapterPosition;
            this.mAdapters.add(adapterPosition, adapterHolder);
            updateItemPositionOffsets(adapterPosition);
            adapter.registerAdapterDataObserver(adapterHolder.observer);
            notifyItemRangeInserted(adapterHolder.itemPositionOffset, adapterHolder.adapter.getItemCount());
            return;
        }
        throw new IllegalStateException("addAdapter cannot be called more than 922 times");
    }

    public int getAdapterPosition(@Nullable Adapter<?> adapter) {
        if (adapter != null) {
            int len = this.mAdapters.size();
            for (int i = 0; i < len; i++) {
                if (((AdapterHolder) this.mAdapters.get(i)).adapter == adapter) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void removeAdapter(Adapter<?> adapter) {
        int pos = getAdapterPosition(adapter);
        if (pos >= 0) {
            removeAdapter(pos);
        }
    }

    public void moveAdapter(int newPosition, Adapter<?> adapter) {
        if (newPosition < 0) {
            throw new IllegalArgumentException("newPosition cannot be < 0");
        } else if (getAdapterPosition(adapter) >= 0) {
            int previousPosition = getAdapterPosition(adapter);
            if (previousPosition != newPosition) {
                AdapterHolder holder = (AdapterHolder) this.mAdapters.remove(previousPosition);
                notifyItemRangeRemoved(holder.itemPositionOffset, holder.adapter.getItemCount());
                this.mAdapters.add(newPosition, holder);
                if (previousPosition < newPosition) {
                    updateItemPositionOffsets(previousPosition);
                } else {
                    updateItemPositionOffsets(newPosition);
                }
                notifyItemRangeInserted(holder.itemPositionOffset, holder.adapter.getItemCount());
            }
        } else {
            throw new IllegalStateException("adapter must already be added");
        }
    }

    public void removeAdapter(int adapterPosition) {
        if (adapterPosition >= 0) {
            if (adapterPosition < this.mAdapters.size()) {
                AdapterHolder holder = (AdapterHolder) this.mAdapters.remove(adapterPosition);
                updateItemPositionOffsets(adapterPosition);
                holder.adapter.unregisterAdapterDataObserver(holder.observer);
                if (this.mRecyclerView != null && holder.viewTypes != null) {
                    int size = holder.viewTypes.size();
                    for (int i = 0; i < size; i++) {
                        this.mRecyclerView.getRecycledViewPool().setMaxRecycledViews(holder.viewTypes.keyAt(i), 0);
                    }
                }
                notifyItemRangeRemoved(holder.itemPositionOffset, holder.adapter.getItemCount());
                return;
            }
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder(50);
        stringBuilder.append("removeAdapter(");
        stringBuilder.append(adapterPosition);
        stringBuilder.append("): position out of range!");
        Log.w(str, stringBuilder.toString());
    }

    public int getItemCount() {
        return this.mItemCount;
    }

    @NonNull
    public Adapter<?> getAdapterForPosition(int position) {
        return getAdapterHolderForPosition(position).adapter;
    }

    public long getChildItemId(int position) {
        AdapterHolder adapterHolder = getAdapterHolderForPosition(position);
        return adapterHolder.adapter.getItemId(position - adapterHolder.itemPositionOffset);
    }

    public int getParentPosition(Adapter childAdapter, int childPosition) {
        for (int i = 0; i < this.mAdapters.size(); i++) {
            if (((AdapterHolder) this.mAdapters.get(i)).adapter == childAdapter) {
                return ((AdapterHolder) this.mAdapters.get(i)).itemPositionOffset + childPosition;
            }
        }
        return -1;
    }

    public int getChildPosition(int position) {
        return position - getAdapterHolderForPosition(position).itemPositionOffset;
    }

    public int getItemViewType(int position) {
        int keyIndex;
        AdapterHolder adapterHolder = getAdapterHolderForPosition(position);
        int localViewType = adapterHolder.adapter.getItemViewType(position - adapterHolder.itemPositionOffset);
        if (adapterHolder.viewTypes == null) {
            adapterHolder.viewTypes = new SparseIntArray(1);
        } else {
            keyIndex = adapterHolder.viewTypes.indexOfValue(localViewType);
            if (keyIndex != -1) {
                return adapterHolder.viewTypes.keyAt(keyIndex);
            }
        }
        keyIndex = this.mNextViewTypeId;
        this.mNextViewTypeId++;
        adapterHolder.viewTypes.put(keyIndex, localViewType);
        return keyIndex;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int len = this.mAdapters.size();
        for (int i = 0; i < len; i++) {
            AdapterHolder adapterHolder = (AdapterHolder) this.mAdapters.get(i);
            if (adapterHolder.viewTypes != null) {
                int localViewType = adapterHolder.viewTypes.get(viewType, -1);
                if (localViewType != -1) {
                    return adapterHolder.adapter.onCreateViewHolder(viewGroup, localViewType);
                }
            }
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder(66);
        stringBuilder.append("onCreateViewHolder: No child adapters handle viewType: ");
        stringBuilder.append(viewType);
        Log.w(str, stringBuilder.toString());
        return null;
    }

    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        AdapterHolder adapterHolder = getAdapterHolderForPosition(position);
        adapterHolder.adapter.onBindViewHolder(viewHolder, position - adapterHolder.itemPositionOffset);
    }

    public void onViewRecycled(ViewHolder holder) {
        int viewType = holder.getItemViewType();
        if (viewType != -1) {
            AdapterHolder adapterHolder = findAdapterHolderForViewType(viewType);
            if (adapterHolder != null) {
                adapterHolder.adapter.onViewRecycled(holder);
            }
        }
    }

    public boolean onFailedToRecycleView(ViewHolder holder) {
        int viewType = holder.getItemViewType();
        if (viewType != -1) {
            AdapterHolder adapterHolder = findAdapterHolderForViewType(viewType);
            if (adapterHolder != null) {
                return adapterHolder.adapter.onFailedToRecycleView(holder);
            }
        }
        return true;
    }

    public void onViewAttachedToWindow(ViewHolder holder) {
        int viewType = holder.getItemViewType();
        if (viewType != -1) {
            AdapterHolder adapterHolder = findAdapterHolderForViewType(viewType);
            if (adapterHolder != null) {
                adapterHolder.adapter.onViewAttachedToWindow(holder);
            }
        }
    }

    public void onViewDetachedFromWindow(ViewHolder holder) {
        int viewType = holder.getItemViewType();
        if (viewType != -1) {
            AdapterHolder adapterHolder = findAdapterHolderForViewType(viewType);
            if (adapterHolder != null) {
                adapterHolder.adapter.onViewDetachedFromWindow(holder);
            }
        }
    }

    public long getItemId(int position) {
        if (!hasStableIds()) {
            return -1;
        }
        return createItemId(((AdapterHolder) this.mAdapters.get(getAdapterIndexForPosition(position))).adapterId, getChildItemId(position));
    }

    private void updateItemPositionOffsets(int startingAdapterIndex) {
        int nextOffset = 0;
        if (startingAdapterIndex > 0) {
            AdapterHolder holder = (AdapterHolder) this.mAdapters.get(startingAdapterIndex - 1);
            nextOffset = holder.itemPositionOffset + holder.adapter.getItemCount();
        }
        int len = this.mAdapters.size();
        for (int i = startingAdapterIndex; i < len; i++) {
            AdapterHolder adapterHolder = (AdapterHolder) this.mAdapters.get(i);
            adapterHolder.itemPositionOffset = nextOffset;
            adapterHolder.adapterPosition = i;
            nextOffset += adapterHolder.adapter.getItemCount();
        }
        this.mItemCount = nextOffset;
    }

    @Nullable
    private AdapterHolder findAdapterHolderForViewType(int viewType) {
        int adapterIndex = getAdapterIndexForViewType(viewType);
        if (adapterIndex == -1) {
            return null;
        }
        return (AdapterHolder) this.mAdapters.get(adapterIndex);
    }

    @NonNull
    private AdapterHolder getAdapterHolderForPosition(int position) {
        return (AdapterHolder) this.mAdapters.get(getAdapterIndexForPosition(position));
    }

    private int getAdapterIndexForViewType(int viewType) {
        int len = this.mAdapters.size();
        for (int i = 0; i < len; i++) {
            AdapterHolder adapterHolder = (AdapterHolder) this.mAdapters.get(i);
            if (adapterHolder.viewTypes != null) {
                if (adapterHolder.viewTypes.indexOfKey(viewType) >= 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static long createItemId(int adapterId, long childItemId) {
        return (((long) adapterId) * CHILD_ID_RANGE) + childItemId;
    }
}

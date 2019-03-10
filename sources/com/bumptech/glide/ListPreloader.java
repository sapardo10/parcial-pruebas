package com.bumptech.glide;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Util;
import java.util.List;
import java.util.Queue;

public class ListPreloader<T> implements OnScrollListener {
    private boolean isIncreasing = true;
    private int lastEnd;
    private int lastFirstVisible = -1;
    private int lastStart;
    private final int maxPreload;
    private final PreloadSizeProvider<T> preloadDimensionProvider;
    private final PreloadModelProvider<T> preloadModelProvider;
    private final PreloadTargetQueue preloadTargetQueue;
    private final RequestManager requestManager;
    private int totalItemCount;

    public interface PreloadModelProvider<U> {
        @NonNull
        List<U> getPreloadItems(int i);

        @Nullable
        RequestBuilder<?> getPreloadRequestBuilder(@NonNull U u);
    }

    public interface PreloadSizeProvider<T> {
        @Nullable
        int[] getPreloadSize(@NonNull T t, int i, int i2);
    }

    private static final class PreloadTargetQueue {
        private final Queue<PreloadTarget> queue;

        PreloadTargetQueue(int size) {
            this.queue = Util.createQueue(size);
            for (int i = 0; i < size; i++) {
                this.queue.offer(new PreloadTarget());
            }
        }

        public PreloadTarget next(int width, int height) {
            PreloadTarget result = (PreloadTarget) this.queue.poll();
            this.queue.offer(result);
            result.photoWidth = width;
            result.photoHeight = height;
            return result;
        }
    }

    private static final class PreloadTarget extends BaseTarget<Object> {
        int photoHeight;
        int photoWidth;

        PreloadTarget() {
        }

        public void onResourceReady(@NonNull Object resource, @Nullable Transition<? super Object> transition) {
        }

        public void getSize(@NonNull SizeReadyCallback cb) {
            cb.onSizeReady(this.photoWidth, this.photoHeight);
        }

        public void removeCallback(@NonNull SizeReadyCallback cb) {
        }
    }

    public ListPreloader(@NonNull RequestManager requestManager, @NonNull PreloadModelProvider<T> preloadModelProvider, @NonNull PreloadSizeProvider<T> preloadDimensionProvider, int maxPreload) {
        this.requestManager = requestManager;
        this.preloadModelProvider = preloadModelProvider;
        this.preloadDimensionProvider = preloadDimensionProvider;
        this.maxPreload = maxPreload;
        this.preloadTargetQueue = new PreloadTargetQueue(maxPreload + 1);
    }

    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
    }

    public void onScroll(AbsListView absListView, int firstVisible, int visibleCount, int totalCount) {
        this.totalItemCount = totalCount;
        int i = this.lastFirstVisible;
        if (firstVisible > i) {
            preload(firstVisible + visibleCount, true);
        } else if (firstVisible < i) {
            preload(firstVisible, false);
        }
        this.lastFirstVisible = firstVisible;
    }

    private void preload(int start, boolean increasing) {
        if (this.isIncreasing != increasing) {
            this.isIncreasing = increasing;
            cancelAll();
        }
        preload(start, (increasing ? this.maxPreload : -this.maxPreload) + start);
    }

    private void preload(int from, int to) {
        int start;
        int end;
        if (from < to) {
            start = Math.max(this.lastEnd, from);
            end = to;
        } else {
            start = to;
            end = Math.min(this.lastStart, from);
        }
        end = Math.min(this.totalItemCount, end);
        start = Math.min(this.totalItemCount, Math.max(0, start));
        int i;
        if (from < to) {
            for (i = start; i < end; i++) {
                preloadAdapterPosition(this.preloadModelProvider.getPreloadItems(i), i, true);
            }
        } else {
            for (i = end - 1; i >= start; i--) {
                preloadAdapterPosition(this.preloadModelProvider.getPreloadItems(i), i, false);
            }
        }
        this.lastStart = start;
        this.lastEnd = end;
    }

    private void preloadAdapterPosition(List<T> items, int position, boolean isIncreasing) {
        int numItems = items.size();
        int i;
        if (isIncreasing) {
            for (i = 0; i < numItems; i++) {
                preloadItem(items.get(i), position, i);
            }
            return;
        }
        for (i = numItems - 1; i >= 0; i--) {
            preloadItem(items.get(i), position, i);
        }
    }

    private void preloadItem(@Nullable T item, int position, int perItemPosition) {
        if (item != null) {
            int[] dimensions = this.preloadDimensionProvider.getPreloadSize(item, position, perItemPosition);
            if (dimensions != null) {
                RequestBuilder<Object> preloadRequestBuilder = this.preloadModelProvider.getPreloadRequestBuilder(item);
                if (preloadRequestBuilder != null) {
                    preloadRequestBuilder.into(this.preloadTargetQueue.next(dimensions[0], dimensions[1]));
                }
            }
        }
    }

    private void cancelAll() {
        for (int i = 0; i < this.maxPreload; i++) {
            this.requestManager.clear(this.preloadTargetQueue.next(0, 0));
        }
    }
}

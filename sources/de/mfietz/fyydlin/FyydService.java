package de.mfietz.fyydlin;

import io.reactivex.Single;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.http.GET;
import retrofit2.http.Query;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J)\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\n\b\u0001\u0010\u0007\u001a\u0004\u0018\u00010\bH'¢\u0006\u0002\u0010\t¨\u0006\n"}, d2 = {"Lde/mfietz/fyydlin/FyydService;", "", "searchPodcasts", "Lio/reactivex/Single;", "Lde/mfietz/fyydlin/FyydResponse;", "title", "", "limit", "", "(Ljava/lang/String;Ljava/lang/Integer;)Lio/reactivex/Single;", "fyydlin"}, k = 1, mv = {1, 1, 11})
/* compiled from: FyydService.kt */
public interface FyydService {
    @NotNull
    @GET("search/podcast")
    Single<FyydResponse> searchPodcasts(@NotNull @Query("title") String str, @Nullable @Query("count") Integer num);
}

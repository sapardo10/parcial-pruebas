package android.support.v7.media;

import android.support.annotation.RequiresApi;

@RequiresApi(24)
final class MediaRouterApi24 {

    public static final class RouteInfo {
        public static int getDeviceType(Object routeObj) {
            return ((android.media.MediaRouter.RouteInfo) routeObj).getDeviceType();
        }
    }

    MediaRouterApi24() {
    }
}

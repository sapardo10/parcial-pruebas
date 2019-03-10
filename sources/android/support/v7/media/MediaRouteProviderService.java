package android.support.v7.media;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.ObjectsCompat;
import android.support.v7.media.MediaRouteProvider.Callback;
import android.support.v7.media.MediaRouteProvider.RouteController;
import android.support.v7.media.MediaRouteProviderDescriptor.Builder;
import android.support.v7.media.MediaRouter.ControlRequestCallback;
import android.util.Log;
import android.util.SparseArray;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public abstract class MediaRouteProviderService extends Service {
    static final boolean DEBUG = Log.isLoggable(TAG, 3);
    static final int PRIVATE_MSG_CLIENT_DIED = 1;
    public static final String SERVICE_INTERFACE = "android.media.MediaRouteProviderService";
    static final String TAG = "MediaRouteProviderSrv";
    private final ArrayList<ClientRecord> mClients = new ArrayList();
    private MediaRouteDiscoveryRequest mCompositeDiscoveryRequest;
    final PrivateHandler mPrivateHandler = new PrivateHandler();
    MediaRouteProvider mProvider;
    private final ProviderCallback mProviderCallback = new ProviderCallback();
    private final ReceiveHandler mReceiveHandler = new ReceiveHandler(this);
    private final Messenger mReceiveMessenger = new Messenger(this.mReceiveHandler);

    private final class ClientRecord implements DeathRecipient {
        private final SparseArray<RouteController> mControllers = new SparseArray();
        public MediaRouteDiscoveryRequest mDiscoveryRequest;
        public final Messenger mMessenger;
        public final int mVersion;

        public ClientRecord(Messenger messenger, int version) {
            this.mMessenger = messenger;
            this.mVersion = version;
        }

        public boolean register() {
            try {
                this.mMessenger.getBinder().linkToDeath(this, 0);
                return true;
            } catch (RemoteException e) {
                binderDied();
                return false;
            }
        }

        public void dispose() {
            int count = this.mControllers.size();
            for (int i = 0; i < count; i++) {
                ((RouteController) this.mControllers.valueAt(i)).onRelease();
            }
            this.mControllers.clear();
            this.mMessenger.getBinder().unlinkToDeath(this, 0);
            setDiscoveryRequest(null);
        }

        public boolean hasMessenger(Messenger other) {
            return this.mMessenger.getBinder() == other.getBinder();
        }

        public boolean createRouteController(String routeId, String routeGroupId, int controllerId) {
            if (this.mControllers.indexOfKey(controllerId) < 0) {
                RouteController controller;
                if (routeGroupId == null) {
                    controller = MediaRouteProviderService.this.mProvider.onCreateRouteController(routeId);
                } else {
                    controller = MediaRouteProviderService.this.mProvider.onCreateRouteController(routeId, routeGroupId);
                }
                if (controller != null) {
                    this.mControllers.put(controllerId, controller);
                    return true;
                }
            }
            return false;
        }

        public boolean releaseRouteController(int controllerId) {
            RouteController controller = (RouteController) this.mControllers.get(controllerId);
            if (controller == null) {
                return false;
            }
            this.mControllers.remove(controllerId);
            controller.onRelease();
            return true;
        }

        public RouteController getRouteController(int controllerId) {
            return (RouteController) this.mControllers.get(controllerId);
        }

        public boolean setDiscoveryRequest(MediaRouteDiscoveryRequest request) {
            if (ObjectsCompat.equals(this.mDiscoveryRequest, request)) {
                return false;
            }
            this.mDiscoveryRequest = request;
            return MediaRouteProviderService.this.updateCompositeDiscoveryRequest();
        }

        public void binderDied() {
            MediaRouteProviderService.this.mPrivateHandler.obtainMessage(1, this.mMessenger).sendToTarget();
        }

        public String toString() {
            return MediaRouteProviderService.getClientId(this.mMessenger);
        }
    }

    private final class PrivateHandler extends Handler {
        PrivateHandler() {
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                MediaRouteProviderService.this.onBinderDied((Messenger) msg.obj);
            }
        }
    }

    private static final class ReceiveHandler extends Handler {
        private final WeakReference<MediaRouteProviderService> mServiceRef;

        public ReceiveHandler(MediaRouteProviderService service) {
            this.mServiceRef = new WeakReference(service);
        }

        public void handleMessage(Message msg) {
            Messenger messenger = msg.replyTo;
            if (MediaRouteProviderProtocol.isValidRemoteMessenger(messenger)) {
                int what = msg.what;
                int requestId = msg.arg1;
                int arg = msg.arg2;
                Object obj = msg.obj;
                Bundle data = msg.peekData();
                if (!processMessage(what, messenger, requestId, arg, obj, data)) {
                    if (MediaRouteProviderService.DEBUG) {
                        String str = MediaRouteProviderService.TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(MediaRouteProviderService.getClientId(messenger));
                        stringBuilder.append(": Message failed, what=");
                        stringBuilder.append(what);
                        stringBuilder.append(", requestId=");
                        stringBuilder.append(requestId);
                        stringBuilder.append(", arg=");
                        stringBuilder.append(arg);
                        stringBuilder.append(", obj=");
                        stringBuilder.append(obj);
                        stringBuilder.append(", data=");
                        stringBuilder.append(data);
                        Log.d(str, stringBuilder.toString());
                    }
                    MediaRouteProviderService.sendGenericFailure(messenger, requestId);
                }
            } else if (MediaRouteProviderService.DEBUG) {
                Log.d(MediaRouteProviderService.TAG, "Ignoring message without valid reply messenger.");
            }
        }

        private boolean processMessage(int what, Messenger messenger, int requestId, int arg, Object obj, Bundle data) {
            MediaRouteProviderService service = (MediaRouteProviderService) this.mServiceRef.get();
            int reason = 0;
            if (service != null) {
                int volume;
                switch (what) {
                    case 1:
                        return service.onRegisterClient(messenger, requestId, arg);
                    case 2:
                        return service.onUnregisterClient(messenger, requestId);
                    case 3:
                        String routeId = data.getString(MediaRouteProviderProtocol.CLIENT_DATA_ROUTE_ID);
                        String routeGroupId = data.getString(MediaRouteProviderProtocol.CLIENT_DATA_ROUTE_LIBRARY_GROUP);
                        if (routeId == null) {
                            break;
                        }
                        return service.onCreateRouteController(messenger, requestId, arg, routeId, routeGroupId);
                    case 4:
                        return service.onReleaseRouteController(messenger, requestId, arg);
                    case 5:
                        return service.onSelectRoute(messenger, requestId, arg);
                    case 6:
                        if (data != null) {
                            reason = data.getInt(MediaRouteProviderProtocol.CLIENT_DATA_UNSELECT_REASON, 0);
                        }
                        return service.onUnselectRoute(messenger, requestId, arg, reason);
                    case 7:
                        volume = data.getInt(MediaRouteProviderProtocol.CLIENT_DATA_VOLUME, -1);
                        if (volume < 0) {
                            break;
                        }
                        return service.onSetRouteVolume(messenger, requestId, arg, volume);
                    case 8:
                        volume = data.getInt(MediaRouteProviderProtocol.CLIENT_DATA_VOLUME, 0);
                        if (volume == 0) {
                            break;
                        }
                        return service.onUpdateRouteVolume(messenger, requestId, arg, volume);
                    case 9:
                        if (!(obj instanceof Intent)) {
                            break;
                        }
                        return service.onRouteControlRequest(messenger, requestId, arg, (Intent) obj);
                    case 10:
                        MediaRouteDiscoveryRequest mediaRouteDiscoveryRequest;
                        if (obj != null) {
                            if (!(obj instanceof Bundle)) {
                                break;
                            }
                        }
                        MediaRouteDiscoveryRequest request = MediaRouteDiscoveryRequest.fromBundle((Bundle) obj);
                        if (request != null) {
                            if (request.isValid()) {
                                mediaRouteDiscoveryRequest = request;
                                return service.onSetDiscoveryRequest(messenger, requestId, mediaRouteDiscoveryRequest);
                            }
                        }
                        mediaRouteDiscoveryRequest = null;
                        return service.onSetDiscoveryRequest(messenger, requestId, mediaRouteDiscoveryRequest);
                    default:
                        break;
                }
            }
            return false;
        }
    }

    private final class ProviderCallback extends Callback {
        ProviderCallback() {
        }

        public void onDescriptorChanged(MediaRouteProvider provider, MediaRouteProviderDescriptor descriptor) {
            MediaRouteProviderService.this.sendDescriptorChanged(descriptor);
        }
    }

    public abstract MediaRouteProvider onCreateMediaRouteProvider();

    public MediaRouteProvider getMediaRouteProvider() {
        return this.mProvider;
    }

    public IBinder onBind(Intent intent) {
        if (intent.getAction().equals("android.media.MediaRouteProviderService")) {
            if (this.mProvider == null) {
                MediaRouteProvider provider = onCreateMediaRouteProvider();
                if (provider != null) {
                    String providerPackage = provider.getMetadata().getPackageName();
                    if (providerPackage.equals(getPackageName())) {
                        this.mProvider = provider;
                        this.mProvider.setCallback(this.mProviderCallback);
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("onCreateMediaRouteProvider() returned a provider whose package name does not match the package name of the service.  A media route provider service can only export its own media route providers.  Provider package name: ");
                        stringBuilder.append(providerPackage);
                        stringBuilder.append(".  Service package name: ");
                        stringBuilder.append(getPackageName());
                        stringBuilder.append(".");
                        throw new IllegalStateException(stringBuilder.toString());
                    }
                }
            }
            if (this.mProvider != null) {
                return this.mReceiveMessenger.getBinder();
            }
        }
        return null;
    }

    public boolean onUnbind(Intent intent) {
        MediaRouteProvider mediaRouteProvider = this.mProvider;
        if (mediaRouteProvider != null) {
            mediaRouteProvider.setCallback(null);
        }
        return super.onUnbind(intent);
    }

    boolean onRegisterClient(Messenger messenger, int requestId, int version) {
        if (version >= 1) {
            if (findClient(messenger) < 0) {
                ClientRecord client = new ClientRecord(messenger, version);
                if (client.register()) {
                    this.mClients.add(client);
                    if (DEBUG) {
                        String str = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(client);
                        stringBuilder.append(": Registered, version=");
                        stringBuilder.append(version);
                        Log.d(str, stringBuilder.toString());
                    }
                    if (requestId != 0) {
                        sendReply(messenger, 2, requestId, 1, createDescriptorBundleForClientVersion(this.mProvider.getDescriptor(), client.mVersion), null);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    boolean onUnregisterClient(Messenger messenger, int requestId) {
        int index = findClient(messenger);
        if (index < 0) {
            return false;
        }
        ClientRecord client = (ClientRecord) this.mClients.remove(index);
        if (DEBUG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(client);
            stringBuilder.append(": Unregistered");
            Log.d(str, stringBuilder.toString());
        }
        client.dispose();
        sendGenericSuccess(messenger, requestId);
        return true;
    }

    void onBinderDied(Messenger messenger) {
        int index = findClient(messenger);
        if (index >= 0) {
            ClientRecord client = (ClientRecord) this.mClients.remove(index);
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(client);
                stringBuilder.append(": Binder died");
                Log.d(str, stringBuilder.toString());
            }
            client.dispose();
        }
    }

    boolean onCreateRouteController(Messenger messenger, int requestId, int controllerId, String routeId, String routeGroupId) {
        ClientRecord client = getClient(messenger);
        if (client != null) {
            if (client.createRouteController(routeId, routeGroupId, controllerId)) {
                if (DEBUG) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(client);
                    stringBuilder.append(": Route controller created, controllerId=");
                    stringBuilder.append(controllerId);
                    stringBuilder.append(", routeId=");
                    stringBuilder.append(routeId);
                    stringBuilder.append(", routeGroupId=");
                    stringBuilder.append(routeGroupId);
                    Log.d(str, stringBuilder.toString());
                }
                sendGenericSuccess(messenger, requestId);
                return true;
            }
        }
        return false;
    }

    boolean onReleaseRouteController(Messenger messenger, int requestId, int controllerId) {
        ClientRecord client = getClient(messenger);
        if (client != null) {
            if (client.releaseRouteController(controllerId)) {
                if (DEBUG) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(client);
                    stringBuilder.append(": Route controller released");
                    stringBuilder.append(", controllerId=");
                    stringBuilder.append(controllerId);
                    Log.d(str, stringBuilder.toString());
                }
                sendGenericSuccess(messenger, requestId);
                return true;
            }
        }
        return false;
    }

    boolean onSelectRoute(Messenger messenger, int requestId, int controllerId) {
        ClientRecord client = getClient(messenger);
        if (client != null) {
            RouteController controller = client.getRouteController(controllerId);
            if (controller != null) {
                controller.onSelect();
                if (DEBUG) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(client);
                    stringBuilder.append(": Route selected");
                    stringBuilder.append(", controllerId=");
                    stringBuilder.append(controllerId);
                    Log.d(str, stringBuilder.toString());
                }
                sendGenericSuccess(messenger, requestId);
                return true;
            }
        }
        return false;
    }

    boolean onUnselectRoute(Messenger messenger, int requestId, int controllerId, int reason) {
        ClientRecord client = getClient(messenger);
        if (client != null) {
            RouteController controller = client.getRouteController(controllerId);
            if (controller != null) {
                controller.onUnselect(reason);
                if (DEBUG) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(client);
                    stringBuilder.append(": Route unselected");
                    stringBuilder.append(", controllerId=");
                    stringBuilder.append(controllerId);
                    Log.d(str, stringBuilder.toString());
                }
                sendGenericSuccess(messenger, requestId);
                return true;
            }
        }
        return false;
    }

    boolean onSetRouteVolume(Messenger messenger, int requestId, int controllerId, int volume) {
        ClientRecord client = getClient(messenger);
        if (client != null) {
            RouteController controller = client.getRouteController(controllerId);
            if (controller != null) {
                controller.onSetVolume(volume);
                if (DEBUG) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(client);
                    stringBuilder.append(": Route volume changed");
                    stringBuilder.append(", controllerId=");
                    stringBuilder.append(controllerId);
                    stringBuilder.append(", volume=");
                    stringBuilder.append(volume);
                    Log.d(str, stringBuilder.toString());
                }
                sendGenericSuccess(messenger, requestId);
                return true;
            }
        }
        return false;
    }

    boolean onUpdateRouteVolume(Messenger messenger, int requestId, int controllerId, int delta) {
        ClientRecord client = getClient(messenger);
        if (client != null) {
            RouteController controller = client.getRouteController(controllerId);
            if (controller != null) {
                controller.onUpdateVolume(delta);
                if (DEBUG) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(client);
                    stringBuilder.append(": Route volume updated");
                    stringBuilder.append(", controllerId=");
                    stringBuilder.append(controllerId);
                    stringBuilder.append(", delta=");
                    stringBuilder.append(delta);
                    Log.d(str, stringBuilder.toString());
                }
                sendGenericSuccess(messenger, requestId);
                return true;
            }
        }
        return false;
    }

    boolean onRouteControlRequest(Messenger messenger, int requestId, int controllerId, Intent intent) {
        ClientRecord client = getClient(messenger);
        if (client != null) {
            RouteController controller = client.getRouteController(controllerId);
            if (controller != null) {
                ControlRequestCallback callback = null;
                if (requestId != 0) {
                    final ClientRecord clientRecord = client;
                    final int i = controllerId;
                    final Intent intent2 = intent;
                    final Messenger messenger2 = messenger;
                    final int i2 = requestId;
                    callback = new ControlRequestCallback() {
                        public void onResult(Bundle data) {
                            if (MediaRouteProviderService.DEBUG) {
                                String str = MediaRouteProviderService.TAG;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(clientRecord);
                                stringBuilder.append(": Route control request succeeded");
                                stringBuilder.append(", controllerId=");
                                stringBuilder.append(i);
                                stringBuilder.append(", intent=");
                                stringBuilder.append(intent2);
                                stringBuilder.append(", data=");
                                stringBuilder.append(data);
                                Log.d(str, stringBuilder.toString());
                            }
                            if (MediaRouteProviderService.this.findClient(messenger2) >= 0) {
                                MediaRouteProviderService.sendReply(messenger2, 3, i2, 0, data, null);
                            }
                        }

                        public void onError(String error, Bundle data) {
                            if (MediaRouteProviderService.DEBUG) {
                                String str = MediaRouteProviderService.TAG;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(clientRecord);
                                stringBuilder.append(": Route control request failed");
                                stringBuilder.append(", controllerId=");
                                stringBuilder.append(i);
                                stringBuilder.append(", intent=");
                                stringBuilder.append(intent2);
                                stringBuilder.append(", error=");
                                stringBuilder.append(error);
                                stringBuilder.append(", data=");
                                stringBuilder.append(data);
                                Log.d(str, stringBuilder.toString());
                            }
                            if (MediaRouteProviderService.this.findClient(messenger2) < 0) {
                                return;
                            }
                            if (error != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("error", error);
                                MediaRouteProviderService.sendReply(messenger2, 4, i2, 0, data, bundle);
                                return;
                            }
                            MediaRouteProviderService.sendReply(messenger2, 4, i2, 0, data, null);
                        }
                    };
                }
                if (controller.onControlRequest(intent, callback)) {
                    if (DEBUG) {
                        String str = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(client);
                        stringBuilder.append(": Route control request delivered");
                        stringBuilder.append(", controllerId=");
                        stringBuilder.append(controllerId);
                        stringBuilder.append(", intent=");
                        stringBuilder.append(intent);
                        Log.d(str, stringBuilder.toString());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    boolean onSetDiscoveryRequest(Messenger messenger, int requestId, MediaRouteDiscoveryRequest request) {
        ClientRecord client = getClient(messenger);
        if (client == null) {
            return false;
        }
        boolean actuallyChanged = client.setDiscoveryRequest(request);
        if (DEBUG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(client);
            stringBuilder.append(": Set discovery request, request=");
            stringBuilder.append(request);
            stringBuilder.append(", actuallyChanged=");
            stringBuilder.append(actuallyChanged);
            stringBuilder.append(", compositeDiscoveryRequest=");
            stringBuilder.append(this.mCompositeDiscoveryRequest);
            Log.d(str, stringBuilder.toString());
        }
        sendGenericSuccess(messenger, requestId);
        return true;
    }

    void sendDescriptorChanged(MediaRouteProviderDescriptor descriptor) {
        int count = this.mClients.size();
        for (int i = 0; i < count; i++) {
            ClientRecord client = (ClientRecord) this.mClients.get(i);
            sendReply(client.mMessenger, 5, 0, 0, createDescriptorBundleForClientVersion(descriptor, client.mVersion), null);
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(client);
                stringBuilder.append(": Sent descriptor change event, descriptor=");
                stringBuilder.append(descriptor);
                Log.d(str, stringBuilder.toString());
            }
        }
    }

    @VisibleForTesting
    static Bundle createDescriptorBundleForClientVersion(MediaRouteProviderDescriptor descriptor, int clientVersion) {
        if (descriptor == null) {
            return null;
        }
        Builder builder = new Builder(descriptor);
        builder.setRoutes(null);
        for (MediaRouteDescriptor route : descriptor.getRoutes()) {
            if (clientVersion >= route.getMinClientVersion()) {
                if (clientVersion <= route.getMaxClientVersion()) {
                    builder.addRoute(route);
                }
            }
        }
        return builder.build().asBundle();
    }

    boolean updateCompositeDiscoveryRequest() {
        MediaRouteDiscoveryRequest composite = null;
        MediaRouteSelector.Builder selectorBuilder = null;
        boolean activeScan = false;
        int count = this.mClients.size();
        for (int i = 0; i < count; i++) {
            MediaRouteDiscoveryRequest request = ((ClientRecord) this.mClients.get(i)).mDiscoveryRequest;
            if (request != null) {
                if (request.getSelector().isEmpty()) {
                    if (request.isActiveScan()) {
                    }
                }
                activeScan |= request.isActiveScan();
                if (composite == null) {
                    composite = request;
                } else {
                    if (selectorBuilder == null) {
                        selectorBuilder = new MediaRouteSelector.Builder(composite.getSelector());
                    }
                    selectorBuilder.addSelector(request.getSelector());
                }
            }
        }
        if (selectorBuilder != null) {
            composite = new MediaRouteDiscoveryRequest(selectorBuilder.build(), activeScan);
        }
        if (ObjectsCompat.equals(this.mCompositeDiscoveryRequest, composite)) {
            return false;
        }
        this.mCompositeDiscoveryRequest = composite;
        this.mProvider.setDiscoveryRequest(composite);
        return true;
    }

    private ClientRecord getClient(Messenger messenger) {
        int index = findClient(messenger);
        return index >= 0 ? (ClientRecord) this.mClients.get(index) : null;
    }

    int findClient(Messenger messenger) {
        int count = this.mClients.size();
        for (int i = 0; i < count; i++) {
            if (((ClientRecord) this.mClients.get(i)).hasMessenger(messenger)) {
                return i;
            }
        }
        return -1;
    }

    static void sendGenericFailure(Messenger messenger, int requestId) {
        if (requestId != 0) {
            sendReply(messenger, 0, requestId, 0, null, null);
        }
    }

    private static void sendGenericSuccess(Messenger messenger, int requestId) {
        if (requestId != 0) {
            sendReply(messenger, 1, requestId, 0, null, null);
        }
    }

    static void sendReply(Messenger messenger, int what, int requestId, int arg, Object obj, Bundle data) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = requestId;
        msg.arg2 = arg;
        msg.obj = obj;
        msg.setData(data);
        try {
            messenger.send(msg);
        } catch (DeadObjectException e) {
        } catch (RemoteException ex) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Could not send message to ");
            stringBuilder.append(getClientId(messenger));
            Log.e(str, stringBuilder.toString(), ex);
        }
    }

    static String getClientId(Messenger messenger) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Client connection ");
        stringBuilder.append(messenger.getBinder().toString());
        return stringBuilder.toString();
    }
}

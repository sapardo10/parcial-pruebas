package android.support.v7.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.media.MediaRouteProvider.ProviderMetadata;
import android.support.v7.media.MediaRouteProvider.RouteController;
import android.support.v7.media.MediaRouter.ControlRequestCallback;
import android.util.Log;
import android.util.SparseArray;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

final class RegisteredMediaRouteProvider extends MediaRouteProvider implements ServiceConnection {
    static final boolean DEBUG = Log.isLoggable(TAG, 3);
    static final String TAG = "MediaRouteProviderProxy";
    private Connection mActiveConnection;
    private boolean mBound;
    private final ComponentName mComponentName;
    private boolean mConnectionReady;
    private final ArrayList<Controller> mControllers = new ArrayList();
    final PrivateHandler mPrivateHandler;
    private boolean mStarted;

    private final class Connection implements DeathRecipient {
        private int mNextControllerId = 1;
        private int mNextRequestId = 1;
        private final SparseArray<ControlRequestCallback> mPendingCallbacks = new SparseArray();
        private int mPendingRegisterRequestId;
        private final ReceiveHandler mReceiveHandler;
        private final Messenger mReceiveMessenger;
        private final Messenger mServiceMessenger;
        private int mServiceVersion;

        /* renamed from: android.support.v7.media.RegisteredMediaRouteProvider$Connection$1 */
        class C02951 implements Runnable {
            C02951() {
            }

            public void run() {
                Connection.this.failPendingCallbacks();
            }
        }

        /* renamed from: android.support.v7.media.RegisteredMediaRouteProvider$Connection$2 */
        class C02962 implements Runnable {
            C02962() {
            }

            public void run() {
                RegisteredMediaRouteProvider.this.onConnectionDied(Connection.this);
            }
        }

        public Connection(Messenger serviceMessenger) {
            this.mServiceMessenger = serviceMessenger;
            this.mReceiveHandler = new ReceiveHandler(this);
            this.mReceiveMessenger = new Messenger(this.mReceiveHandler);
        }

        public boolean register() {
            int i = this.mNextRequestId;
            this.mNextRequestId = i + 1;
            this.mPendingRegisterRequestId = i;
            if (!sendRequest(1, this.mPendingRegisterRequestId, 2, null, null)) {
                return false;
            }
            try {
                this.mServiceMessenger.getBinder().linkToDeath(this, 0);
                return true;
            } catch (RemoteException e) {
                binderDied();
                return false;
            }
        }

        public void dispose() {
            sendRequest(2, 0, 0, null, null);
            this.mReceiveHandler.dispose();
            this.mServiceMessenger.getBinder().unlinkToDeath(this, 0);
            RegisteredMediaRouteProvider.this.mPrivateHandler.post(new C02951());
        }

        void failPendingCallbacks() {
            for (int i = 0; i < this.mPendingCallbacks.size(); i++) {
                ((ControlRequestCallback) this.mPendingCallbacks.valueAt(i)).onError(null, null);
            }
            this.mPendingCallbacks.clear();
        }

        public boolean onGenericFailure(int requestId) {
            if (requestId == this.mPendingRegisterRequestId) {
                this.mPendingRegisterRequestId = 0;
                RegisteredMediaRouteProvider.this.onConnectionError(this, "Registration failed");
            }
            ControlRequestCallback callback = (ControlRequestCallback) this.mPendingCallbacks.get(requestId);
            if (callback != null) {
                this.mPendingCallbacks.remove(requestId);
                callback.onError(null, null);
            }
            return true;
        }

        public boolean onGenericSuccess(int requestId) {
            return true;
        }

        public boolean onRegistered(int requestId, int serviceVersion, Bundle descriptorBundle) {
            if (this.mServiceVersion != 0 || requestId != this.mPendingRegisterRequestId || serviceVersion < 1) {
                return false;
            }
            this.mPendingRegisterRequestId = 0;
            this.mServiceVersion = serviceVersion;
            RegisteredMediaRouteProvider.this.onConnectionDescriptorChanged(this, MediaRouteProviderDescriptor.fromBundle(descriptorBundle));
            RegisteredMediaRouteProvider.this.onConnectionReady(this);
            return true;
        }

        public boolean onDescriptorChanged(Bundle descriptorBundle) {
            if (this.mServiceVersion == 0) {
                return false;
            }
            RegisteredMediaRouteProvider.this.onConnectionDescriptorChanged(this, MediaRouteProviderDescriptor.fromBundle(descriptorBundle));
            return true;
        }

        public boolean onControlRequestSucceeded(int requestId, Bundle data) {
            ControlRequestCallback callback = (ControlRequestCallback) this.mPendingCallbacks.get(requestId);
            if (callback == null) {
                return false;
            }
            this.mPendingCallbacks.remove(requestId);
            callback.onResult(data);
            return true;
        }

        public boolean onControlRequestFailed(int requestId, String error, Bundle data) {
            ControlRequestCallback callback = (ControlRequestCallback) this.mPendingCallbacks.get(requestId);
            if (callback == null) {
                return false;
            }
            this.mPendingCallbacks.remove(requestId);
            callback.onError(error, data);
            return true;
        }

        public void binderDied() {
            RegisteredMediaRouteProvider.this.mPrivateHandler.post(new C02962());
        }

        public int createRouteController(String routeId, String routeGroupId) {
            int controllerId = this.mNextControllerId;
            this.mNextControllerId = controllerId + 1;
            Bundle data = new Bundle();
            data.putString(MediaRouteProviderProtocol.CLIENT_DATA_ROUTE_ID, routeId);
            data.putString(MediaRouteProviderProtocol.CLIENT_DATA_ROUTE_LIBRARY_GROUP, routeGroupId);
            int i = this.mNextRequestId;
            this.mNextRequestId = i + 1;
            sendRequest(3, i, controllerId, null, data);
            return controllerId;
        }

        public void releaseRouteController(int controllerId) {
            int i = this.mNextRequestId;
            this.mNextRequestId = i + 1;
            sendRequest(4, i, controllerId, null, null);
        }

        public void selectRoute(int controllerId) {
            int i = this.mNextRequestId;
            this.mNextRequestId = i + 1;
            sendRequest(5, i, controllerId, null, null);
        }

        public void unselectRoute(int controllerId, int reason) {
            Bundle extras = new Bundle();
            extras.putInt(MediaRouteProviderProtocol.CLIENT_DATA_UNSELECT_REASON, reason);
            int i = this.mNextRequestId;
            this.mNextRequestId = i + 1;
            sendRequest(6, i, controllerId, null, extras);
        }

        public void setVolume(int controllerId, int volume) {
            Bundle data = new Bundle();
            data.putInt(MediaRouteProviderProtocol.CLIENT_DATA_VOLUME, volume);
            int i = this.mNextRequestId;
            this.mNextRequestId = i + 1;
            sendRequest(7, i, controllerId, null, data);
        }

        public void updateVolume(int controllerId, int delta) {
            Bundle data = new Bundle();
            data.putInt(MediaRouteProviderProtocol.CLIENT_DATA_VOLUME, delta);
            int i = this.mNextRequestId;
            this.mNextRequestId = i + 1;
            sendRequest(8, i, controllerId, null, data);
        }

        public boolean sendControlRequest(int controllerId, Intent intent, ControlRequestCallback callback) {
            int requestId = this.mNextRequestId;
            this.mNextRequestId = requestId + 1;
            if (!sendRequest(9, requestId, controllerId, intent, null)) {
                return false;
            }
            if (callback != null) {
                this.mPendingCallbacks.put(requestId, callback);
            }
            return true;
        }

        public void setDiscoveryRequest(MediaRouteDiscoveryRequest request) {
            int i = this.mNextRequestId;
            this.mNextRequestId = i + 1;
            sendRequest(10, i, 0, request != null ? request.asBundle() : null, null);
        }

        private boolean sendRequest(int what, int requestId, int arg, Object obj, Bundle data) {
            Message msg = Message.obtain();
            msg.what = what;
            msg.arg1 = requestId;
            msg.arg2 = arg;
            msg.obj = obj;
            msg.setData(data);
            msg.replyTo = this.mReceiveMessenger;
            try {
                this.mServiceMessenger.send(msg);
                return true;
            } catch (DeadObjectException e) {
                return false;
            } catch (RemoteException ex) {
                if (what != 2) {
                    Log.e(RegisteredMediaRouteProvider.TAG, "Could not send message to service.", ex);
                }
                return false;
            }
        }
    }

    private static final class PrivateHandler extends Handler {
        PrivateHandler() {
        }
    }

    private static final class ReceiveHandler extends Handler {
        private final WeakReference<Connection> mConnectionRef;

        public ReceiveHandler(Connection connection) {
            this.mConnectionRef = new WeakReference(connection);
        }

        public void dispose() {
            this.mConnectionRef.clear();
        }

        public void handleMessage(Message msg) {
            Connection connection = (Connection) this.mConnectionRef.get();
            if (connection != null) {
                if (!processMessage(connection, msg.what, msg.arg1, msg.arg2, msg.obj, msg.peekData())) {
                    if (RegisteredMediaRouteProvider.DEBUG) {
                        String str = RegisteredMediaRouteProvider.TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unhandled message from server: ");
                        stringBuilder.append(msg);
                        Log.d(str, stringBuilder.toString());
                    }
                }
            }
        }

        private boolean processMessage(Connection connection, int what, int requestId, int arg, Object obj, Bundle data) {
            switch (what) {
                case 0:
                    connection.onGenericFailure(requestId);
                    return true;
                case 1:
                    connection.onGenericSuccess(requestId);
                    return true;
                case 2:
                    if (obj != null) {
                        if (!(obj instanceof Bundle)) {
                            break;
                        }
                    }
                    return connection.onRegistered(requestId, arg, (Bundle) obj);
                case 3:
                    if (obj != null) {
                        if (!(obj instanceof Bundle)) {
                            break;
                        }
                    }
                    return connection.onControlRequestSucceeded(requestId, (Bundle) obj);
                case 4:
                    String error;
                    if (obj != null) {
                        if (!(obj instanceof Bundle)) {
                            break;
                        }
                    }
                    if (data == null) {
                        error = null;
                    } else {
                        error = data.getString("error");
                    }
                    return connection.onControlRequestFailed(requestId, error, (Bundle) obj);
                case 5:
                    if (obj != null) {
                        if (!(obj instanceof Bundle)) {
                            break;
                        }
                    }
                    return connection.onDescriptorChanged((Bundle) obj);
                default:
                    break;
            }
            return false;
        }
    }

    private final class Controller extends RouteController {
        private Connection mConnection;
        private int mControllerId;
        private int mPendingSetVolume = -1;
        private int mPendingUpdateVolumeDelta;
        private final String mRouteGroupId;
        private final String mRouteId;
        private boolean mSelected;

        public Controller(String routeId, String routeGroupId) {
            this.mRouteId = routeId;
            this.mRouteGroupId = routeGroupId;
        }

        public void attachConnection(Connection connection) {
            this.mConnection = connection;
            this.mControllerId = connection.createRouteController(this.mRouteId, this.mRouteGroupId);
            if (this.mSelected) {
                connection.selectRoute(this.mControllerId);
                int i = this.mPendingSetVolume;
                if (i >= 0) {
                    connection.setVolume(this.mControllerId, i);
                    this.mPendingSetVolume = -1;
                }
                i = this.mPendingUpdateVolumeDelta;
                if (i != 0) {
                    connection.updateVolume(this.mControllerId, i);
                    this.mPendingUpdateVolumeDelta = 0;
                }
            }
        }

        public void detachConnection() {
            Connection connection = this.mConnection;
            if (connection != null) {
                connection.releaseRouteController(this.mControllerId);
                this.mConnection = null;
                this.mControllerId = 0;
            }
        }

        public void onRelease() {
            RegisteredMediaRouteProvider.this.onControllerReleased(this);
        }

        public void onSelect() {
            this.mSelected = true;
            Connection connection = this.mConnection;
            if (connection != null) {
                connection.selectRoute(this.mControllerId);
            }
        }

        public void onUnselect() {
            onUnselect(0);
        }

        public void onUnselect(int reason) {
            this.mSelected = false;
            Connection connection = this.mConnection;
            if (connection != null) {
                connection.unselectRoute(this.mControllerId, reason);
            }
        }

        public void onSetVolume(int volume) {
            Connection connection = this.mConnection;
            if (connection != null) {
                connection.setVolume(this.mControllerId, volume);
                return;
            }
            this.mPendingSetVolume = volume;
            this.mPendingUpdateVolumeDelta = 0;
        }

        public void onUpdateVolume(int delta) {
            Connection connection = this.mConnection;
            if (connection != null) {
                connection.updateVolume(this.mControllerId, delta);
            } else {
                this.mPendingUpdateVolumeDelta += delta;
            }
        }

        public boolean onControlRequest(Intent intent, ControlRequestCallback callback) {
            Connection connection = this.mConnection;
            if (connection != null) {
                return connection.sendControlRequest(this.mControllerId, intent, callback);
            }
            return false;
        }
    }

    public RegisteredMediaRouteProvider(Context context, ComponentName componentName) {
        super(context, new ProviderMetadata(componentName));
        this.mComponentName = componentName;
        this.mPrivateHandler = new PrivateHandler();
    }

    public RouteController onCreateRouteController(@NonNull String routeId) {
        if (routeId != null) {
            return createRouteController(routeId, null);
        }
        throw new IllegalArgumentException("routeId cannot be null");
    }

    public RouteController onCreateRouteController(@NonNull String routeId, @NonNull String routeGroupId) {
        if (routeId == null) {
            throw new IllegalArgumentException("routeId cannot be null");
        } else if (routeGroupId != null) {
            return createRouteController(routeId, routeGroupId);
        } else {
            throw new IllegalArgumentException("routeGroupId cannot be null");
        }
    }

    public void onDiscoveryRequestChanged(MediaRouteDiscoveryRequest request) {
        if (this.mConnectionReady) {
            this.mActiveConnection.setDiscoveryRequest(request);
        }
        updateBinding();
    }

    public void onServiceConnected(ComponentName name, IBinder service) {
        if (DEBUG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this);
            stringBuilder.append(": Connected");
            Log.d(str, stringBuilder.toString());
        }
        if (this.mBound) {
            disconnect();
            Messenger messenger = service != null ? new Messenger(service) : null;
            if (MediaRouteProviderProtocol.isValidRemoteMessenger(messenger)) {
                Connection connection = new Connection(messenger);
                if (connection.register()) {
                    this.mActiveConnection = connection;
                } else if (DEBUG) {
                    String str2 = TAG;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(this);
                    stringBuilder2.append(": Registration failed");
                    Log.d(str2, stringBuilder2.toString());
                }
                return;
            }
            String str3 = TAG;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(this);
            stringBuilder3.append(": Service returned invalid messenger binder");
            Log.e(str3, stringBuilder3.toString());
        }
    }

    public void onServiceDisconnected(ComponentName name) {
        if (DEBUG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this);
            stringBuilder.append(": Service disconnected");
            Log.d(str, stringBuilder.toString());
        }
        disconnect();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Service connection ");
        stringBuilder.append(this.mComponentName.flattenToShortString());
        return stringBuilder.toString();
    }

    public boolean hasComponentName(String packageName, String className) {
        if (this.mComponentName.getPackageName().equals(packageName)) {
            if (this.mComponentName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    public void start() {
        if (!this.mStarted) {
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this);
                stringBuilder.append(": Starting");
                Log.d(str, stringBuilder.toString());
            }
            this.mStarted = true;
            updateBinding();
        }
    }

    public void stop() {
        if (this.mStarted) {
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this);
                stringBuilder.append(": Stopping");
                Log.d(str, stringBuilder.toString());
            }
            this.mStarted = false;
            updateBinding();
        }
    }

    public void rebindIfDisconnected() {
        if (this.mActiveConnection == null && shouldBind()) {
            unbind();
            bind();
        }
    }

    private void updateBinding() {
        if (shouldBind()) {
            bind();
        } else {
            unbind();
        }
    }

    private boolean shouldBind() {
        if (this.mStarted) {
            if (getDiscoveryRequest() != null || !this.mControllers.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void bind() {
        if (!this.mBound) {
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this);
                stringBuilder.append(": Binding");
                Log.d(str, stringBuilder.toString());
            }
            Intent service = new Intent("android.media.MediaRouteProviderService");
            service.setComponent(this.mComponentName);
            try {
                this.mBound = getContext().bindService(service, this, 1);
                if (!this.mBound && DEBUG) {
                    String str2 = TAG;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(this);
                    stringBuilder2.append(": Bind failed");
                    Log.d(str2, stringBuilder2.toString());
                }
            } catch (SecurityException ex) {
                if (DEBUG) {
                    String str3 = TAG;
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(this);
                    stringBuilder3.append(": Bind failed");
                    Log.d(str3, stringBuilder3.toString(), ex);
                }
            }
        }
    }

    private void unbind() {
        if (this.mBound) {
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this);
                stringBuilder.append(": Unbinding");
                Log.d(str, stringBuilder.toString());
            }
            this.mBound = false;
            disconnect();
            getContext().unbindService(this);
        }
    }

    private RouteController createRouteController(String routeId, String routeGroupId) {
        MediaRouteProviderDescriptor descriptor = getDescriptor();
        if (descriptor != null) {
            List<MediaRouteDescriptor> routes = descriptor.getRoutes();
            int count = routes.size();
            for (int i = 0; i < count; i++) {
                if (((MediaRouteDescriptor) routes.get(i)).getId().equals(routeId)) {
                    Controller controller = new Controller(routeId, routeGroupId);
                    this.mControllers.add(controller);
                    if (this.mConnectionReady) {
                        controller.attachConnection(this.mActiveConnection);
                    }
                    updateBinding();
                    return controller;
                }
            }
        }
        return null;
    }

    void onConnectionReady(Connection connection) {
        if (this.mActiveConnection == connection) {
            this.mConnectionReady = true;
            attachControllersToConnection();
            MediaRouteDiscoveryRequest request = getDiscoveryRequest();
            if (request != null) {
                this.mActiveConnection.setDiscoveryRequest(request);
            }
        }
    }

    void onConnectionDied(Connection connection) {
        if (this.mActiveConnection == connection) {
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this);
                stringBuilder.append(": Service connection died");
                Log.d(str, stringBuilder.toString());
            }
            disconnect();
        }
    }

    void onConnectionError(Connection connection, String error) {
        if (this.mActiveConnection == connection) {
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this);
                stringBuilder.append(": Service connection error - ");
                stringBuilder.append(error);
                Log.d(str, stringBuilder.toString());
            }
            unbind();
        }
    }

    void onConnectionDescriptorChanged(Connection connection, MediaRouteProviderDescriptor descriptor) {
        if (this.mActiveConnection == connection) {
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this);
                stringBuilder.append(": Descriptor changed, descriptor=");
                stringBuilder.append(descriptor);
                Log.d(str, stringBuilder.toString());
            }
            setDescriptor(descriptor);
        }
    }

    private void disconnect() {
        if (this.mActiveConnection != null) {
            setDescriptor(null);
            this.mConnectionReady = false;
            detachControllersFromConnection();
            this.mActiveConnection.dispose();
            this.mActiveConnection = null;
        }
    }

    void onControllerReleased(Controller controller) {
        this.mControllers.remove(controller);
        controller.detachConnection();
        updateBinding();
    }

    private void attachControllersToConnection() {
        int count = this.mControllers.size();
        for (int i = 0; i < count; i++) {
            ((Controller) this.mControllers.get(i)).attachConnection(this.mActiveConnection);
        }
    }

    private void detachControllersFromConnection() {
        int count = this.mControllers.size();
        for (int i = 0; i < count; i++) {
            ((Controller) this.mControllers.get(i)).detachConnection();
        }
    }
}

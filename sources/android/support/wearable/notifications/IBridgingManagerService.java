package android.support.wearable.notifications;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.wearable.internal.aidl.BaseProxy;
import android.support.wearable.internal.aidl.BaseStub;
import android.support.wearable.internal.aidl.Codecs;

public interface IBridgingManagerService extends IInterface {

    public static abstract class Stub extends BaseStub implements IBridgingManagerService {
        private static final String DESCRIPTOR = "android.support.wearable.notifications.IBridgingManagerService";
        static final int TRANSACTION_setBridgingConfig = 1;

        public static class Proxy extends BaseProxy implements IBridgingManagerService {
            Proxy(IBinder remote) {
                super(remote, Stub.DESCRIPTOR);
            }

            public void setBridgingConfig(Bundle bridgingConfig) throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                Codecs.writeParcelable(data, bridgingConfig);
                transactAndReadExceptionReturnVoid(1, data);
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBridgingManagerService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin instanceof IBridgingManagerService) {
                return (IBridgingManagerService) iin;
            }
            return new Proxy(obj);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (routeToSuperOrEnforceInterface(code, data, reply, flags)) {
                return true;
            }
            if (code != 1) {
                return false;
            }
            setBridgingConfig((Bundle) Codecs.createParcelable(data, Bundle.CREATOR));
            reply.writeNoException();
            return true;
        }
    }

    void setBridgingConfig(Bundle bundle) throws RemoteException;
}

package android.support.wearable.authentication;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.wearable.internal.aidl.BaseProxy;
import android.support.wearable.internal.aidl.BaseStub;
import android.support.wearable.internal.aidl.Codecs;

public interface IAuthenticationRequestService extends IInterface {

    public static abstract class Stub extends BaseStub implements IAuthenticationRequestService {
        private static final String DESCRIPTOR = "android.support.wearable.authentication.IAuthenticationRequestService";
        static final int TRANSACTION_openUrl = 1;

        public static class Proxy extends BaseProxy implements IAuthenticationRequestService {
            Proxy(IBinder remote) {
                super(remote, Stub.DESCRIPTOR);
            }

            public void openUrl(Bundle request, IAuthenticationRequestCallback authenticationRequestCallback) throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                Codecs.writeParcelable(data, request);
                Codecs.writeStrongBinder(data, authenticationRequestCallback);
                transactAndReadExceptionReturnVoid(1, data);
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAuthenticationRequestService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin instanceof IAuthenticationRequestService) {
                return (IAuthenticationRequestService) iin;
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
            openUrl((Bundle) Codecs.createParcelable(data, Bundle.CREATOR), android.support.wearable.authentication.IAuthenticationRequestCallback.Stub.asInterface(data.readStrongBinder()));
            reply.writeNoException();
            return true;
        }
    }

    void openUrl(Bundle bundle, IAuthenticationRequestCallback iAuthenticationRequestCallback) throws RemoteException;
}

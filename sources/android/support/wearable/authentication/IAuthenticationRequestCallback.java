package android.support.wearable.authentication;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.wearable.internal.aidl.BaseProxy;
import android.support.wearable.internal.aidl.BaseStub;
import android.support.wearable.internal.aidl.Codecs;

public interface IAuthenticationRequestCallback extends IInterface {

    public static abstract class Stub extends BaseStub implements IAuthenticationRequestCallback {
        private static final String DESCRIPTOR = "android.support.wearable.authentication.IAuthenticationRequestCallback";
        static final int TRANSACTION_onResult = 1;

        public static class Proxy extends BaseProxy implements IAuthenticationRequestCallback {
            Proxy(IBinder remote) {
                super(remote, Stub.DESCRIPTOR);
            }

            public void onResult(Bundle result) throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                Codecs.writeParcelable(data, result);
                transactAndReadExceptionReturnVoid(1, data);
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAuthenticationRequestCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin instanceof IAuthenticationRequestCallback) {
                return (IAuthenticationRequestCallback) iin;
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
            onResult((Bundle) Codecs.createParcelable(data, Bundle.CREATOR));
            reply.writeNoException();
            return true;
        }
    }

    void onResult(Bundle bundle) throws RemoteException;
}

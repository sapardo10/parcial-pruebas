package android.support.wearable.complications;

import android.content.ComponentName;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.wearable.internal.aidl.BaseProxy;
import android.support.wearable.internal.aidl.BaseStub;
import android.support.wearable.internal.aidl.Codecs;

public interface IProviderInfoService extends IInterface {

    public static abstract class Stub extends BaseStub implements IProviderInfoService {
        private static final String DESCRIPTOR = "android.support.wearable.complications.IProviderInfoService";
        static final int TRANSACTION_getProviderInfos = 1;

        public static class Proxy extends BaseProxy implements IProviderInfoService {
            Proxy(IBinder remote) {
                super(remote, Stub.DESCRIPTOR);
            }

            public ComplicationProviderInfo[] getProviderInfos(ComponentName watchFaceComponent, int[] ids) throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                Codecs.writeParcelable(data, watchFaceComponent);
                data.writeIntArray(ids);
                Parcel reply = transactAndReadException(1, data);
                ComplicationProviderInfo[] retval = (ComplicationProviderInfo[]) reply.createTypedArray(ComplicationProviderInfo.CREATOR);
                reply.recycle();
                return retval;
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IProviderInfoService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin instanceof IProviderInfoService) {
                return (IProviderInfoService) iin;
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
            ComplicationProviderInfo[] retval = getProviderInfos((ComponentName) Codecs.createParcelable(data, ComponentName.CREATOR), data.createIntArray());
            reply.writeNoException();
            reply.writeTypedArray(retval, 1);
            return true;
        }
    }

    ComplicationProviderInfo[] getProviderInfos(ComponentName componentName, int[] iArr) throws RemoteException;
}

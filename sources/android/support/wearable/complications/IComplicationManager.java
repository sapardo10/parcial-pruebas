package android.support.wearable.complications;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.wearable.internal.aidl.BaseProxy;
import android.support.wearable.internal.aidl.BaseStub;
import android.support.wearable.internal.aidl.Codecs;

public interface IComplicationManager extends IInterface {

    public static abstract class Stub extends BaseStub implements IComplicationManager {
        private static final String DESCRIPTOR = "android.support.wearable.complications.IComplicationManager";
        static final int TRANSACTION_updateComplicationData = 1;

        public static class Proxy extends BaseProxy implements IComplicationManager {
            Proxy(IBinder remote) {
                super(remote, Stub.DESCRIPTOR);
            }

            public void updateComplicationData(int complicationId, ComplicationData data_) throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                data.writeInt(complicationId);
                Codecs.writeParcelable(data, data_);
                transactAndReadExceptionReturnVoid(1, data);
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IComplicationManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin instanceof IComplicationManager) {
                return (IComplicationManager) iin;
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
            updateComplicationData(data.readInt(), (ComplicationData) Codecs.createParcelable(data, ComplicationData.CREATOR));
            reply.writeNoException();
            return true;
        }
    }

    void updateComplicationData(int i, ComplicationData complicationData) throws RemoteException;
}

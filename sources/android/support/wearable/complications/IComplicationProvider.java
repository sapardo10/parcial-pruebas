package android.support.wearable.complications;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.wearable.internal.aidl.BaseProxy;
import android.support.wearable.internal.aidl.BaseStub;

public interface IComplicationProvider extends IInterface {

    public static abstract class Stub extends BaseStub implements IComplicationProvider {
        private static final String DESCRIPTOR = "android.support.wearable.complications.IComplicationProvider";
        static final int TRANSACTION_onComplicationActivated = 3;
        static final int TRANSACTION_onComplicationDeactivated = 2;
        static final int TRANSACTION_onUpdate = 1;

        public static class Proxy extends BaseProxy implements IComplicationProvider {
            Proxy(IBinder remote) {
                super(remote, Stub.DESCRIPTOR);
            }

            public void onUpdate(int complicationId, int type, IBinder manager) throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                data.writeInt(complicationId);
                data.writeInt(type);
                data.writeStrongBinder(manager);
                transactAndReadExceptionReturnVoid(1, data);
            }

            public void onComplicationDeactivated(int complicationId) throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                data.writeInt(complicationId);
                transactAndReadExceptionReturnVoid(2, data);
            }

            public void onComplicationActivated(int complicationId, int type, IBinder manager) throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                data.writeInt(complicationId);
                data.writeInt(type);
                data.writeStrongBinder(manager);
                transactAndReadExceptionReturnVoid(3, data);
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IComplicationProvider asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin instanceof IComplicationProvider) {
                return (IComplicationProvider) iin;
            }
            return new Proxy(obj);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (routeToSuperOrEnforceInterface(code, data, reply, flags)) {
                return true;
            }
            switch (code) {
                case 1:
                    onUpdate(data.readInt(), data.readInt(), data.readStrongBinder());
                    break;
                case 2:
                    onComplicationDeactivated(data.readInt());
                    break;
                case 3:
                    onComplicationActivated(data.readInt(), data.readInt(), data.readStrongBinder());
                    break;
                default:
                    return false;
            }
            reply.writeNoException();
            return true;
        }
    }

    void onComplicationActivated(int i, int i2, IBinder iBinder) throws RemoteException;

    void onComplicationDeactivated(int i) throws RemoteException;

    void onUpdate(int i, int i2, IBinder iBinder) throws RemoteException;
}

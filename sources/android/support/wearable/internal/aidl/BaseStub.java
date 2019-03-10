package android.support.wearable.internal.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.view.ViewCompat;

public abstract class BaseStub extends Binder implements IInterface {
    public IBinder asBinder() {
        return this;
    }

    protected boolean routeToSuperOrEnforceInterface(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code > ViewCompat.MEASURED_SIZE_MASK) {
            return super.onTransact(code, data, reply, flags);
        }
        data.enforceInterface(getInterfaceDescriptor());
        return false;
    }
}

package com.aocate.presto.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IOnPitchAdjustmentAvailableChangedListenerCallback_0_8 extends IInterface {

    public static abstract class Stub extends Binder implements IOnPitchAdjustmentAvailableChangedListenerCallback_0_8 {
        private static final String DESCRIPTOR = "com.aocate.presto.service.IOnPitchAdjustmentAvailableChangedListenerCallback_0_8";
        static final int TRANSACTION_onPitchAdjustmentAvailableChanged = 1;

        private static class Proxy implements IOnPitchAdjustmentAvailableChangedListenerCallback_0_8 {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void onPitchAdjustmentAvailableChanged(boolean pitchAdjustmentAvailable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pitchAdjustmentAvailable ? 1 : 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IOnPitchAdjustmentAvailableChangedListenerCallback_0_8 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IOnPitchAdjustmentAvailableChangedListenerCallback_0_8)) {
                return new Proxy(obj);
            }
            return (IOnPitchAdjustmentAvailableChangedListenerCallback_0_8) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                onPitchAdjustmentAvailableChanged(data.readInt() != 0);
                reply.writeNoException();
                return true;
            } else if (code != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }
    }

    void onPitchAdjustmentAvailableChanged(boolean z) throws RemoteException;
}

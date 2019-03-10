package android.support.wearable.watchface;

import android.content.ComponentName;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.wearable.internal.aidl.BaseProxy;
import android.support.wearable.internal.aidl.BaseStub;
import android.support.wearable.internal.aidl.Codecs;
import android.support.wearable.watchface.accessibility.ContentDescriptionLabel;
import android.support.wearable.watchface.decomposition.WatchFaceDecomposition;

public interface IWatchFaceService extends IInterface {

    public static abstract class Stub extends BaseStub implements IWatchFaceService {
        private static final String DESCRIPTOR = "android.support.wearable.watchface.IWatchFaceService";
        static final int TRANSACTION_setActiveComplications = 2;
        static final int TRANSACTION_setContentDescriptionLabels = 5;
        static final int TRANSACTION_setDefaultComplicationProvider = 3;
        static final int TRANSACTION_setDefaultSystemComplicationProvider = 4;
        static final int TRANSACTION_setStyle = 1;
        static final int TRANSACTION_updateDecomposition = 6;

        public static class Proxy extends BaseProxy implements IWatchFaceService {
            Proxy(IBinder remote) {
                super(remote, Stub.DESCRIPTOR);
            }

            public void setStyle(WatchFaceStyle style) throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                Codecs.writeParcelable(data, style);
                transactAndReadExceptionReturnVoid(1, data);
            }

            public void setActiveComplications(int[] ids, boolean updateAll) throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                data.writeIntArray(ids);
                Codecs.writeBoolean(data, updateAll);
                transactAndReadExceptionReturnVoid(2, data);
            }

            public void setDefaultComplicationProvider(int watchFaceComplicationId, ComponentName provider, int type) throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                data.writeInt(watchFaceComplicationId);
                Codecs.writeParcelable(data, provider);
                data.writeInt(type);
                transactAndReadExceptionReturnVoid(3, data);
            }

            public void setDefaultSystemComplicationProvider(int watchFaceComplicationId, int systemProvider, int type) throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                data.writeInt(watchFaceComplicationId);
                data.writeInt(systemProvider);
                data.writeInt(type);
                transactAndReadExceptionReturnVoid(4, data);
            }

            public void setContentDescriptionLabels(ContentDescriptionLabel[] labels) throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                data.writeTypedArray(labels, 0);
                transactAndReadExceptionReturnVoid(5, data);
            }

            public void updateDecomposition(WatchFaceDecomposition decomposition) throws RemoteException {
                Parcel data = obtainAndWriteInterfaceToken();
                Codecs.writeParcelable(data, decomposition);
                transactAndReadExceptionReturnVoid(6, data);
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWatchFaceService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin instanceof IWatchFaceService) {
                return (IWatchFaceService) iin;
            }
            return new Proxy(obj);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (routeToSuperOrEnforceInterface(code, data, reply, flags)) {
                return true;
            }
            switch (code) {
                case 1:
                    setStyle((WatchFaceStyle) Codecs.createParcelable(data, WatchFaceStyle.CREATOR));
                    break;
                case 2:
                    setActiveComplications(data.createIntArray(), Codecs.createBoolean(data));
                    break;
                case 3:
                    setDefaultComplicationProvider(data.readInt(), (ComponentName) Codecs.createParcelable(data, ComponentName.CREATOR), data.readInt());
                    break;
                case 4:
                    setDefaultSystemComplicationProvider(data.readInt(), data.readInt(), data.readInt());
                    break;
                case 5:
                    setContentDescriptionLabels((ContentDescriptionLabel[]) data.createTypedArray(ContentDescriptionLabel.CREATOR));
                    break;
                case 6:
                    updateDecomposition((WatchFaceDecomposition) Codecs.createParcelable(data, WatchFaceDecomposition.CREATOR));
                    break;
                default:
                    return false;
            }
            reply.writeNoException();
            return true;
        }
    }

    void setActiveComplications(int[] iArr, boolean z) throws RemoteException;

    void setContentDescriptionLabels(ContentDescriptionLabel[] contentDescriptionLabelArr) throws RemoteException;

    void setDefaultComplicationProvider(int i, ComponentName componentName, int i2) throws RemoteException;

    void setDefaultSystemComplicationProvider(int i, int i2, int i3) throws RemoteException;

    void setStyle(WatchFaceStyle watchFaceStyle) throws RemoteException;

    void updateDecomposition(WatchFaceDecomposition watchFaceDecomposition) throws RemoteException;
}

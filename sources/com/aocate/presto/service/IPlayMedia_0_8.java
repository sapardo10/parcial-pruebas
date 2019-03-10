package com.aocate.presto.service;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IPlayMedia_0_8 extends IInterface {

    public static abstract class Stub extends Binder implements IPlayMedia_0_8 {
        private static final String DESCRIPTOR = "com.aocate.presto.service.IPlayMedia_0_8";
        static final int TRANSACTION_canSetPitch = 1;
        static final int TRANSACTION_canSetSpeed = 2;
        static final int TRANSACTION_getCurrentPitchStepsAdjustment = 3;
        static final int TRANSACTION_getCurrentPosition = 4;
        static final int TRANSACTION_getCurrentSpeedMultiplier = 5;
        static final int TRANSACTION_getDuration = 6;
        static final int TRANSACTION_getMaxSpeedMultiplier = 7;
        static final int TRANSACTION_getMinSpeedMultiplier = 8;
        static final int TRANSACTION_getVersionCode = 9;
        static final int TRANSACTION_getVersionName = 10;
        static final int TRANSACTION_isLooping = 11;
        static final int TRANSACTION_isPlaying = 12;
        static final int TRANSACTION_pause = 13;
        static final int TRANSACTION_prepare = 14;
        static final int TRANSACTION_prepareAsync = 15;
        static final int TRANSACTION_registerOnBufferingUpdateCallback = 16;
        static final int TRANSACTION_registerOnCompletionCallback = 17;
        static final int TRANSACTION_registerOnErrorCallback = 18;
        static final int TRANSACTION_registerOnInfoCallback = 19;
        static final int TRANSACTION_registerOnPitchAdjustmentAvailableChangedCallback = 20;
        static final int TRANSACTION_registerOnPreparedCallback = 21;
        static final int TRANSACTION_registerOnSeekCompleteCallback = 22;
        static final int TRANSACTION_registerOnSpeedAdjustmentAvailableChangedCallback = 23;
        static final int TRANSACTION_release = 24;
        static final int TRANSACTION_reset = 25;
        static final int TRANSACTION_seekTo = 26;
        static final int TRANSACTION_setAudioStreamType = 27;
        static final int TRANSACTION_setDataSourceString = 28;
        static final int TRANSACTION_setDataSourceUri = 29;
        static final int TRANSACTION_setEnableSpeedAdjustment = 30;
        static final int TRANSACTION_setLooping = 31;
        static final int TRANSACTION_setPitchStepsAdjustment = 32;
        static final int TRANSACTION_setPlaybackPitch = 33;
        static final int TRANSACTION_setPlaybackSpeed = 34;
        static final int TRANSACTION_setSpeedAdjustmentAlgorithm = 35;
        static final int TRANSACTION_setVolume = 36;
        static final int TRANSACTION_start = 37;
        static final int TRANSACTION_startSession = 38;
        static final int TRANSACTION_stop = 39;
        static final int TRANSACTION_unregisterOnBufferingUpdateCallback = 40;
        static final int TRANSACTION_unregisterOnCompletionCallback = 41;
        static final int TRANSACTION_unregisterOnErrorCallback = 42;
        static final int TRANSACTION_unregisterOnInfoCallback = 43;
        static final int TRANSACTION_unregisterOnPitchAdjustmentAvailableChangedCallback = 44;
        static final int TRANSACTION_unregisterOnPreparedCallback = 45;
        static final int TRANSACTION_unregisterOnSeekCompleteCallback = 46;
        static final int TRANSACTION_unregisterOnSpeedAdjustmentAvailableChangedCallback = 47;

        private static class Proxy implements IPlayMedia_0_8 {
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

            public boolean canSetPitch(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    boolean z = false;
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean canSetSpeed(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    boolean z = false;
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public float getCurrentPitchStepsAdjustment(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getCurrentPosition(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public float getCurrentSpeedMultiplier(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getDuration(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public float getMaxSpeedMultiplier(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public float getMinSpeedMultiplier(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getVersionCode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getVersionName() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isLooping(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    boolean z = false;
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isPlaying(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    boolean z = false;
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        z = true;
                    }
                    boolean _result = z;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void pause(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void prepare(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void prepareAsync(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerOnBufferingUpdateCallback(long sessionId, IOnBufferingUpdateListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerOnCompletionCallback(long sessionId, IOnCompletionListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerOnErrorCallback(long sessionId, IOnErrorListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerOnInfoCallback(long sessionId, IOnInfoListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerOnPitchAdjustmentAvailableChangedCallback(long sessionId, IOnPitchAdjustmentAvailableChangedListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerOnPreparedCallback(long sessionId, IOnPreparedListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerOnSeekCompleteCallback(long sessionId, IOnSeekCompleteListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerOnSpeedAdjustmentAvailableChangedCallback(long sessionId, IOnSpeedAdjustmentAvailableChangedListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void release(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void reset(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void seekTo(long sessionId, int msec) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeInt(msec);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setAudioStreamType(long sessionId, int streamtype) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeInt(streamtype);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setDataSourceString(long sessionId, String path) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeString(path);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setDataSourceUri(long sessionId, Uri uri) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    if (uri != null) {
                        _data.writeInt(1);
                        uri.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setEnableSpeedAdjustment(long sessionId, boolean enableSpeedAdjustment) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeInt(enableSpeedAdjustment ? 1 : 0);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setLooping(long sessionId, boolean looping) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeInt(looping ? 1 : 0);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setPitchStepsAdjustment(long sessionId, float pitchSteps) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeFloat(pitchSteps);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setPlaybackPitch(long sessionId, float f) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeFloat(f);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setPlaybackSpeed(long sessionId, float f) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeFloat(f);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setSpeedAdjustmentAlgorithm(long sessionId, int algorithm) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeInt(algorithm);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setVolume(long sessionId, float left, float right) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeFloat(left);
                    _data.writeFloat(right);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void start(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public long startSession(IDeathCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void stop(long sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterOnBufferingUpdateCallback(long sessionId, IOnBufferingUpdateListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterOnCompletionCallback(long sessionId, IOnCompletionListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterOnErrorCallback(long sessionId, IOnErrorListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterOnInfoCallback(long sessionId, IOnInfoListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterOnPitchAdjustmentAvailableChangedCallback(long sessionId, IOnPitchAdjustmentAvailableChangedListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterOnPreparedCallback(long sessionId, IOnPreparedListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterOnSeekCompleteCallback(long sessionId, IOnSeekCompleteListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterOnSpeedAdjustmentAvailableChangedCallback(long sessionId, IOnSpeedAdjustmentAvailableChangedListenerCallback_0_8 cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(sessionId);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(47, _data, _reply, 0);
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

        public static IPlayMedia_0_8 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IPlayMedia_0_8)) {
                return new Proxy(obj);
            }
            return (IPlayMedia_0_8) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code != 1598968902) {
                boolean _arg1 = false;
                float _result;
                int _result2;
                long _arg0;
                switch (code) {
                    case 1:
                        data.enforceInterface(DESCRIPTOR);
                        _arg1 = canSetPitch(data.readLong());
                        reply.writeNoException();
                        reply.writeInt(_arg1);
                        return true;
                    case 2:
                        data.enforceInterface(DESCRIPTOR);
                        _arg1 = canSetSpeed(data.readLong());
                        reply.writeNoException();
                        reply.writeInt(_arg1);
                        return true;
                    case 3:
                        data.enforceInterface(DESCRIPTOR);
                        _result = getCurrentPitchStepsAdjustment(data.readLong());
                        reply.writeNoException();
                        reply.writeFloat(_result);
                        return true;
                    case 4:
                        data.enforceInterface(DESCRIPTOR);
                        _result2 = getCurrentPosition(data.readLong());
                        reply.writeNoException();
                        reply.writeInt(_result2);
                        return true;
                    case 5:
                        data.enforceInterface(DESCRIPTOR);
                        _result = getCurrentSpeedMultiplier(data.readLong());
                        reply.writeNoException();
                        reply.writeFloat(_result);
                        return true;
                    case 6:
                        data.enforceInterface(DESCRIPTOR);
                        _result2 = getDuration(data.readLong());
                        reply.writeNoException();
                        reply.writeInt(_result2);
                        return true;
                    case 7:
                        data.enforceInterface(DESCRIPTOR);
                        _result = getMaxSpeedMultiplier(data.readLong());
                        reply.writeNoException();
                        reply.writeFloat(_result);
                        return true;
                    case 8:
                        data.enforceInterface(DESCRIPTOR);
                        _result = getMinSpeedMultiplier(data.readLong());
                        reply.writeNoException();
                        reply.writeFloat(_result);
                        return true;
                    case 9:
                        data.enforceInterface(DESCRIPTOR);
                        _result2 = getVersionCode();
                        reply.writeNoException();
                        reply.writeInt(_result2);
                        return true;
                    case 10:
                        data.enforceInterface(DESCRIPTOR);
                        String _result3 = getVersionName();
                        reply.writeNoException();
                        reply.writeString(_result3);
                        return true;
                    case 11:
                        data.enforceInterface(DESCRIPTOR);
                        _arg1 = isLooping(data.readLong());
                        reply.writeNoException();
                        reply.writeInt(_arg1);
                        return true;
                    case 12:
                        data.enforceInterface(DESCRIPTOR);
                        _arg1 = isPlaying(data.readLong());
                        reply.writeNoException();
                        reply.writeInt(_arg1);
                        return true;
                    case 13:
                        data.enforceInterface(DESCRIPTOR);
                        pause(data.readLong());
                        reply.writeNoException();
                        return true;
                    case 14:
                        data.enforceInterface(DESCRIPTOR);
                        prepare(data.readLong());
                        reply.writeNoException();
                        return true;
                    case 15:
                        data.enforceInterface(DESCRIPTOR);
                        prepareAsync(data.readLong());
                        reply.writeNoException();
                        return true;
                    case 16:
                        data.enforceInterface(DESCRIPTOR);
                        registerOnBufferingUpdateCallback(data.readLong(), com.aocate.presto.service.IOnBufferingUpdateListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 17:
                        data.enforceInterface(DESCRIPTOR);
                        registerOnCompletionCallback(data.readLong(), com.aocate.presto.service.IOnCompletionListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 18:
                        data.enforceInterface(DESCRIPTOR);
                        registerOnErrorCallback(data.readLong(), com.aocate.presto.service.IOnErrorListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 19:
                        data.enforceInterface(DESCRIPTOR);
                        registerOnInfoCallback(data.readLong(), com.aocate.presto.service.IOnInfoListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 20:
                        data.enforceInterface(DESCRIPTOR);
                        registerOnPitchAdjustmentAvailableChangedCallback(data.readLong(), com.aocate.presto.service.IOnPitchAdjustmentAvailableChangedListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 21:
                        data.enforceInterface(DESCRIPTOR);
                        registerOnPreparedCallback(data.readLong(), com.aocate.presto.service.IOnPreparedListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 22:
                        data.enforceInterface(DESCRIPTOR);
                        registerOnSeekCompleteCallback(data.readLong(), com.aocate.presto.service.IOnSeekCompleteListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 23:
                        data.enforceInterface(DESCRIPTOR);
                        registerOnSpeedAdjustmentAvailableChangedCallback(data.readLong(), com.aocate.presto.service.IOnSpeedAdjustmentAvailableChangedListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 24:
                        data.enforceInterface(DESCRIPTOR);
                        release(data.readLong());
                        reply.writeNoException();
                        return true;
                    case 25:
                        data.enforceInterface(DESCRIPTOR);
                        reset(data.readLong());
                        reply.writeNoException();
                        return true;
                    case 26:
                        data.enforceInterface(DESCRIPTOR);
                        seekTo(data.readLong(), data.readInt());
                        reply.writeNoException();
                        return true;
                    case 27:
                        data.enforceInterface(DESCRIPTOR);
                        setAudioStreamType(data.readLong(), data.readInt());
                        reply.writeNoException();
                        return true;
                    case 28:
                        data.enforceInterface(DESCRIPTOR);
                        setDataSourceString(data.readLong(), data.readString());
                        reply.writeNoException();
                        return true;
                    case 29:
                        Uri _arg12;
                        data.enforceInterface(DESCRIPTOR);
                        _arg0 = data.readLong();
                        if (data.readInt() != 0) {
                            _arg12 = (Uri) Uri.CREATOR.createFromParcel(data);
                        } else {
                            _arg12 = null;
                        }
                        setDataSourceUri(_arg0, _arg12);
                        reply.writeNoException();
                        return true;
                    case 30:
                        data.enforceInterface(DESCRIPTOR);
                        _arg0 = data.readLong();
                        if (data.readInt() != 0) {
                            _arg1 = true;
                        }
                        setEnableSpeedAdjustment(_arg0, _arg1);
                        reply.writeNoException();
                        return true;
                    case 31:
                        data.enforceInterface(DESCRIPTOR);
                        _arg0 = data.readLong();
                        if (data.readInt() != 0) {
                            _arg1 = true;
                        }
                        setLooping(_arg0, _arg1);
                        reply.writeNoException();
                        return true;
                    case 32:
                        data.enforceInterface(DESCRIPTOR);
                        setPitchStepsAdjustment(data.readLong(), data.readFloat());
                        reply.writeNoException();
                        return true;
                    case 33:
                        data.enforceInterface(DESCRIPTOR);
                        setPlaybackPitch(data.readLong(), data.readFloat());
                        reply.writeNoException();
                        return true;
                    case 34:
                        data.enforceInterface(DESCRIPTOR);
                        setPlaybackSpeed(data.readLong(), data.readFloat());
                        reply.writeNoException();
                        return true;
                    case 35:
                        data.enforceInterface(DESCRIPTOR);
                        setSpeedAdjustmentAlgorithm(data.readLong(), data.readInt());
                        reply.writeNoException();
                        return true;
                    case 36:
                        data.enforceInterface(DESCRIPTOR);
                        setVolume(data.readLong(), data.readFloat(), data.readFloat());
                        reply.writeNoException();
                        return true;
                    case 37:
                        data.enforceInterface(DESCRIPTOR);
                        start(data.readLong());
                        reply.writeNoException();
                        return true;
                    case 38:
                        data.enforceInterface(DESCRIPTOR);
                        _arg0 = startSession(com.aocate.presto.service.IDeathCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        reply.writeLong(_arg0);
                        return true;
                    case 39:
                        data.enforceInterface(DESCRIPTOR);
                        stop(data.readLong());
                        reply.writeNoException();
                        return true;
                    case 40:
                        data.enforceInterface(DESCRIPTOR);
                        unregisterOnBufferingUpdateCallback(data.readLong(), com.aocate.presto.service.IOnBufferingUpdateListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 41:
                        data.enforceInterface(DESCRIPTOR);
                        unregisterOnCompletionCallback(data.readLong(), com.aocate.presto.service.IOnCompletionListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 42:
                        data.enforceInterface(DESCRIPTOR);
                        unregisterOnErrorCallback(data.readLong(), com.aocate.presto.service.IOnErrorListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 43:
                        data.enforceInterface(DESCRIPTOR);
                        unregisterOnInfoCallback(data.readLong(), com.aocate.presto.service.IOnInfoListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 44:
                        data.enforceInterface(DESCRIPTOR);
                        unregisterOnPitchAdjustmentAvailableChangedCallback(data.readLong(), com.aocate.presto.service.IOnPitchAdjustmentAvailableChangedListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 45:
                        data.enforceInterface(DESCRIPTOR);
                        unregisterOnPreparedCallback(data.readLong(), com.aocate.presto.service.IOnPreparedListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 46:
                        data.enforceInterface(DESCRIPTOR);
                        unregisterOnSeekCompleteCallback(data.readLong(), com.aocate.presto.service.IOnSeekCompleteListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 47:
                        data.enforceInterface(DESCRIPTOR);
                        unregisterOnSpeedAdjustmentAvailableChangedCallback(data.readLong(), com.aocate.presto.service.IOnSpeedAdjustmentAvailableChangedListenerCallback_0_8.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            }
            reply.writeString(DESCRIPTOR);
            return true;
        }
    }

    boolean canSetPitch(long j) throws RemoteException;

    boolean canSetSpeed(long j) throws RemoteException;

    float getCurrentPitchStepsAdjustment(long j) throws RemoteException;

    int getCurrentPosition(long j) throws RemoteException;

    float getCurrentSpeedMultiplier(long j) throws RemoteException;

    int getDuration(long j) throws RemoteException;

    float getMaxSpeedMultiplier(long j) throws RemoteException;

    float getMinSpeedMultiplier(long j) throws RemoteException;

    int getVersionCode() throws RemoteException;

    String getVersionName() throws RemoteException;

    boolean isLooping(long j) throws RemoteException;

    boolean isPlaying(long j) throws RemoteException;

    void pause(long j) throws RemoteException;

    void prepare(long j) throws RemoteException;

    void prepareAsync(long j) throws RemoteException;

    void registerOnBufferingUpdateCallback(long j, IOnBufferingUpdateListenerCallback_0_8 iOnBufferingUpdateListenerCallback_0_8) throws RemoteException;

    void registerOnCompletionCallback(long j, IOnCompletionListenerCallback_0_8 iOnCompletionListenerCallback_0_8) throws RemoteException;

    void registerOnErrorCallback(long j, IOnErrorListenerCallback_0_8 iOnErrorListenerCallback_0_8) throws RemoteException;

    void registerOnInfoCallback(long j, IOnInfoListenerCallback_0_8 iOnInfoListenerCallback_0_8) throws RemoteException;

    void registerOnPitchAdjustmentAvailableChangedCallback(long j, IOnPitchAdjustmentAvailableChangedListenerCallback_0_8 iOnPitchAdjustmentAvailableChangedListenerCallback_0_8) throws RemoteException;

    void registerOnPreparedCallback(long j, IOnPreparedListenerCallback_0_8 iOnPreparedListenerCallback_0_8) throws RemoteException;

    void registerOnSeekCompleteCallback(long j, IOnSeekCompleteListenerCallback_0_8 iOnSeekCompleteListenerCallback_0_8) throws RemoteException;

    void registerOnSpeedAdjustmentAvailableChangedCallback(long j, IOnSpeedAdjustmentAvailableChangedListenerCallback_0_8 iOnSpeedAdjustmentAvailableChangedListenerCallback_0_8) throws RemoteException;

    void release(long j) throws RemoteException;

    void reset(long j) throws RemoteException;

    void seekTo(long j, int i) throws RemoteException;

    void setAudioStreamType(long j, int i) throws RemoteException;

    void setDataSourceString(long j, String str) throws RemoteException;

    void setDataSourceUri(long j, Uri uri) throws RemoteException;

    void setEnableSpeedAdjustment(long j, boolean z) throws RemoteException;

    void setLooping(long j, boolean z) throws RemoteException;

    void setPitchStepsAdjustment(long j, float f) throws RemoteException;

    void setPlaybackPitch(long j, float f) throws RemoteException;

    void setPlaybackSpeed(long j, float f) throws RemoteException;

    void setSpeedAdjustmentAlgorithm(long j, int i) throws RemoteException;

    void setVolume(long j, float f, float f2) throws RemoteException;

    void start(long j) throws RemoteException;

    long startSession(IDeathCallback_0_8 iDeathCallback_0_8) throws RemoteException;

    void stop(long j) throws RemoteException;

    void unregisterOnBufferingUpdateCallback(long j, IOnBufferingUpdateListenerCallback_0_8 iOnBufferingUpdateListenerCallback_0_8) throws RemoteException;

    void unregisterOnCompletionCallback(long j, IOnCompletionListenerCallback_0_8 iOnCompletionListenerCallback_0_8) throws RemoteException;

    void unregisterOnErrorCallback(long j, IOnErrorListenerCallback_0_8 iOnErrorListenerCallback_0_8) throws RemoteException;

    void unregisterOnInfoCallback(long j, IOnInfoListenerCallback_0_8 iOnInfoListenerCallback_0_8) throws RemoteException;

    void unregisterOnPitchAdjustmentAvailableChangedCallback(long j, IOnPitchAdjustmentAvailableChangedListenerCallback_0_8 iOnPitchAdjustmentAvailableChangedListenerCallback_0_8) throws RemoteException;

    void unregisterOnPreparedCallback(long j, IOnPreparedListenerCallback_0_8 iOnPreparedListenerCallback_0_8) throws RemoteException;

    void unregisterOnSeekCompleteCallback(long j, IOnSeekCompleteListenerCallback_0_8 iOnSeekCompleteListenerCallback_0_8) throws RemoteException;

    void unregisterOnSpeedAdjustmentAvailableChangedCallback(long j, IOnSpeedAdjustmentAvailableChangedListenerCallback_0_8 iOnSpeedAdjustmentAvailableChangedListenerCallback_0_8) throws RemoteException;
}

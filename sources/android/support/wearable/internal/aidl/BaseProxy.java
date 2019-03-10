package android.support.wearable.internal.aidl;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract class BaseProxy implements IInterface {
    private final String mDescriptor;
    private final IBinder mRemote;

    protected BaseProxy(IBinder remote, String descriptor) {
        this.mRemote = remote;
        this.mDescriptor = descriptor;
    }

    public IBinder asBinder() {
        return this.mRemote;
    }

    protected Parcel obtainAndWriteInterfaceToken() {
        Parcel parcel = Parcel.obtain();
        parcel.writeInterfaceToken(this.mDescriptor);
        return parcel;
    }

    protected Parcel transactAndReadException(int code, Parcel in) throws RemoteException {
        RuntimeException runtimeException;
        Parcel out = Parcel.obtain();
        try {
            runtimeException = this.mRemote;
            runtimeException.transact(code, in, out, 0);
            out.readException();
        } catch (RuntimeException e) {
            runtimeException = e;
            throw runtimeException;
        } finally {
            
/*
Method generation error in method: android.support.wearable.internal.aidl.BaseProxy.transactAndReadException(int, android.os.Parcel):android.os.Parcel, dex: classes2.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x001a: INVOKE  (wrap: android.os.Parcel
  ?: MERGE  (r5_1 'in' android.os.Parcel) = (r5_0 'in' android.os.Parcel), (r0_0 'out' android.os.Parcel)) android.os.Parcel.recycle():void type: VIRTUAL in method: android.support.wearable.internal.aidl.BaseProxy.transactAndReadException(int, android.os.Parcel):android.os.Parcel, dex: classes2.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:226)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:203)
	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:100)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:50)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:299)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:187)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:320)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:257)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:220)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:75)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:12)
	at jadx.core.ProcessClass.process(ProcessClass.java:40)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: ?: MERGE  (r5_1 'in' android.os.Parcel) = (r5_0 'in' android.os.Parcel), (r0_0 'out' android.os.Parcel) in method: android.support.wearable.internal.aidl.BaseProxy.transactAndReadException(int, android.os.Parcel):android.os.Parcel, dex: classes2.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:226)
	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:101)
	at jadx.core.codegen.InsnGen.addArgDot(InsnGen.java:84)
	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:632)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:338)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:220)
	... 22 more
Caused by: jadx.core.utils.exceptions.CodegenException: MERGE can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:537)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:509)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:211)
	... 27 more

*/

            protected void transactAndReadExceptionReturnVoid(int code, Parcel in) throws RemoteException {
                Parcel out = Parcel.obtain();
                try {
                    this.mRemote.transact(code, in, out, 0);
                    out.readException();
                } finally {
                    in.recycle();
                    out.recycle();
                }
            }

            protected void transactOneway(int code, Parcel in) throws RemoteException {
                try {
                    this.mRemote.transact(code, in, null, 1);
                } finally {
                    in.recycle();
                }
            }
        }

package android.support.v4.graphics;

import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import java.io.File;

@RequiresApi(21)
@RestrictTo({Scope.LIBRARY_GROUP})
class TypefaceCompatApi21Impl extends TypefaceCompatBaseImpl {
    private static final String TAG = "TypefaceCompatApi21Impl";

    TypefaceCompatApi21Impl() {
    }

    private File getFile(ParcelFileDescriptor fd) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("/proc/self/fd/");
            stringBuilder.append(fd.getFd());
            String path = Os.readlink(stringBuilder.toString());
            if (OsConstants.S_ISREG(Os.stat(path).st_mode)) {
                return new File(path);
            }
            return null;
        } catch (ErrnoException e) {
            return null;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.Typeface createFromFontInfo(android.content.Context r10, android.os.CancellationSignal r11, @android.support.annotation.NonNull android.support.v4.provider.FontsContractCompat.FontInfo[] r12, int r13) {
        /*
        r9 = this;
        r0 = r12.length;
        r1 = 0;
        r2 = 1;
        if (r0 >= r2) goto L_0x0006;
    L_0x0005:
        return r1;
    L_0x0006:
        r0 = r9.findBestInfo(r12, r13);
        r2 = r10.getContentResolver();
        r3 = r0.getUri();	 Catch:{ IOException -> 0x008c }
        r4 = "r";
        r3 = r2.openFileDescriptor(r3, r4, r11);	 Catch:{ IOException -> 0x008c }
        r4 = r9.getFile(r3);	 Catch:{ Throwable -> 0x0070, all -> 0x006d }
        if (r4 == 0) goto L_0x0032;
    L_0x0021:
        r5 = r4.canRead();	 Catch:{ Throwable -> 0x0070, all -> 0x006d }
        if (r5 != 0) goto L_0x0028;
    L_0x0027:
        goto L_0x0032;
    L_0x0028:
        r5 = android.graphics.Typeface.createFromFile(r4);	 Catch:{ Throwable -> 0x0070, all -> 0x006d }
        if (r3 == 0) goto L_0x0031;
    L_0x002e:
        r3.close();	 Catch:{ IOException -> 0x008c }
    L_0x0031:
        return r5;
        r5 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0070, all -> 0x006d }
        r6 = r3.getFileDescriptor();	 Catch:{ Throwable -> 0x0070, all -> 0x006d }
        r5.<init>(r6);	 Catch:{ Throwable -> 0x0070, all -> 0x006d }
        r6 = super.createFromInputStream(r10, r5);	 Catch:{ Throwable -> 0x0053, all -> 0x0050 }
        r5.close();	 Catch:{ Throwable -> 0x0070, all -> 0x006d }
        if (r3 == 0) goto L_0x004d;
        r3.close();	 Catch:{ IOException -> 0x008c }
        return r6;
    L_0x0050:
        r6 = move-exception;
        r7 = r1;
        goto L_0x005a;
    L_0x0053:
        r6 = move-exception;
        throw r6;	 Catch:{ all -> 0x0056 }
    L_0x0056:
        r7 = move-exception;
        r8 = r7;
        r7 = r6;
        r6 = r8;
        if (r7 == 0) goto L_0x0066;
        r5.close();	 Catch:{ Throwable -> 0x0063, all -> 0x006d }
        goto L_0x006a;
    L_0x0063:
        r7 = move-exception;
        goto L_0x006a;
        r5.close();	 Catch:{ Throwable -> 0x0070, all -> 0x006d }
        throw r6;	 Catch:{ Throwable -> 0x0070, all -> 0x006d }
    L_0x006d:
        r4 = move-exception;
        r5 = r1;
        goto L_0x0077;
    L_0x0070:
        r4 = move-exception;
        throw r4;	 Catch:{ all -> 0x0073 }
    L_0x0073:
        r5 = move-exception;
        r8 = r5;
        r5 = r4;
        r4 = r8;
        if (r3 == 0) goto L_0x0089;
        if (r5 == 0) goto L_0x0085;
        r3.close();	 Catch:{ Throwable -> 0x0082 }
        goto L_0x0089;
    L_0x0082:
        r5 = move-exception;
        goto L_0x0089;
        r3.close();	 Catch:{ IOException -> 0x008c }
        throw r4;	 Catch:{ IOException -> 0x008c }
    L_0x008c:
        r3 = move-exception;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatApi21Impl.createFromFontInfo(android.content.Context, android.os.CancellationSignal, android.support.v4.provider.FontsContractCompat$FontInfo[], int):android.graphics.Typeface");
    }
}

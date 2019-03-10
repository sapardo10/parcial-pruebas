package de.danoeh.antennapod.core.util;

import android.content.Context;
import de.danoeh.antennapod.core.C0734R;

public enum DownloadError {
    SUCCESS(0, C0734R.string.download_successful),
    ERROR_PARSER_EXCEPTION(1, C0734R.string.download_error_parser_exception),
    ERROR_UNSUPPORTED_TYPE(2, C0734R.string.download_error_unsupported_type),
    ERROR_CONNECTION_ERROR(3, C0734R.string.download_error_connection_error),
    ERROR_MALFORMED_URL(4, C0734R.string.download_error_error_unknown),
    ERROR_IO_ERROR(5, C0734R.string.download_error_io_error),
    ERROR_FILE_EXISTS(6, C0734R.string.download_error_error_unknown),
    ERROR_DOWNLOAD_CANCELLED(7, C0734R.string.download_error_error_unknown),
    ERROR_DEVICE_NOT_FOUND(8, C0734R.string.download_error_device_not_found),
    ERROR_HTTP_DATA_ERROR(9, C0734R.string.download_error_http_data_error),
    ERROR_NOT_ENOUGH_SPACE(10, C0734R.string.download_error_insufficient_space),
    ERROR_UNKNOWN_HOST(11, C0734R.string.download_error_unknown_host),
    ERROR_REQUEST_ERROR(12, C0734R.string.download_error_request_error),
    ERROR_DB_ACCESS_ERROR(13, C0734R.string.download_error_db_access),
    ERROR_UNAUTHORIZED(14, C0734R.string.download_error_unauthorized),
    ERROR_FILE_TYPE(15, C0734R.string.download_error_file_type_type),
    ERROR_FORBIDDEN(16, C0734R.string.download_error_forbidden);
    
    private final int code;
    private final int resId;

    public static de.danoeh.antennapod.core.util.DownloadError fromCode(int r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:8:0x002d in {4, 5, 7} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r0 = values();
        r1 = r0.length;
        r2 = 0;
    L_0x0006:
        if (r2 >= r1) goto L_0x0015;
    L_0x0008:
        r3 = r0[r2];
        r4 = r3.getCode();
        if (r4 != r5) goto L_0x0011;
    L_0x0010:
        return r3;
        r2 = r2 + 1;
        goto L_0x0006;
    L_0x0015:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "unknown code: ";
        r1.append(r2);
        r1.append(r5);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.util.DownloadError.fromCode(int):de.danoeh.antennapod.core.util.DownloadError");
    }

    private DownloadError(int code, int resId) {
        this.code = code;
        this.resId = resId;
    }

    public int getCode() {
        return this.code;
    }

    public String getErrorString(Context context) {
        return context.getString(this.resId);
    }
}

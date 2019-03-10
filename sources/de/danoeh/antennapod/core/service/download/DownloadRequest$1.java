package de.danoeh.antennapod.core.service.download;

import android.os.Parcel;
import android.os.Parcelable.Creator;

class DownloadRequest$1 implements Creator<DownloadRequest> {
    DownloadRequest$1() {
    }

    public DownloadRequest createFromParcel(Parcel in) {
        return new DownloadRequest(in, null);
    }

    public DownloadRequest[] newArray(int size) {
        return new DownloadRequest[size];
    }
}

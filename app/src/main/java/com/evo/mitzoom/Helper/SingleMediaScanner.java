package com.evo.mitzoom.Helper;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

    private final MediaScannerConnection mMs;
    private final File mFile;

    public SingleMediaScanner(Context mContext, File mFile) {
        this.mFile = mFile;
        mMs = new MediaScannerConnection(mContext,this);
        mMs.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(mFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mMs.disconnect();
    }
}

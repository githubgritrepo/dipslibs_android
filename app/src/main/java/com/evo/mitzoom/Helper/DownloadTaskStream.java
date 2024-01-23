package com.evo.mitzoom.Helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;

import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadTaskStream extends AsyncTask<InputStream, Integer, String> {

    private final Context context;
    private PowerManager.WakeLock mWakeLock;
    private final ProgressDialog mProgressDialog;

    public DownloadTaskStream(Context context, ProgressDialog mProgressDialog) {
        this.context = context;
        this.mProgressDialog = mProgressDialog;
    }

    @Override
    protected String doInBackground(InputStream... inputStreams) {
        OutputStream output = null;

        File dir = createDir();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
            }
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    private File createDir() {
        String appName = context.getString(R.string.app_name_dips);
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), appName);

        return mediaStorageDir;
    }

}

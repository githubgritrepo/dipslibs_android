package com.evo.mitzoom.Helper;

import android.content.Context;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = CrashHandler.class.getSimpleName();

    private static final CrashHandler sInstance = new CrashHandler();

    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        throwable.printStackTrace();
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, throwable);
        } else {
            Process.killProcess(Process.myPid());
        }
    }
}

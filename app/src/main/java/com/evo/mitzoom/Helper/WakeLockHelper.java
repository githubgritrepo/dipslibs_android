package com.evo.mitzoom.Helper;

import android.os.PowerManager;
import android.util.Log;

public class WakeLockHelper {
    public static void acquire(PowerManager.WakeLock wakeLock) {
        try {
            wakeLock.acquire(2000);
        } catch (RuntimeException e) {
            Log.d("WakeLockHelper", "unable to acquire wake lock", e);
        }
    }

    public static void release(PowerManager.WakeLock wakeLock) {
        if (wakeLock == null) {
            return;
        }
        try {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        } catch (RuntimeException e) {
            Log.d("WakeLockHelper", "unable to release wake lock", e);
        }
    }
}

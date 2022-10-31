package com.evo.mitzoom.Helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.widget.Toast;

import com.evo.mitzoom.ui.DipsCameraSource;
import com.evo.mitzoom.ui.DipsCapture;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;

public class GraphicFaceTracker extends Tracker<Face> {

    private static final float OPEN_THRESHOLD = 0.99f;
    private static final float CLOSE_THRESHOLD = 0.02f;
    private DipsCapture dipsCapture;
    private DipsCameraSource dipsCameraSource;
    private int state = 0;
    private boolean flag;
    private Context mContext;
    private String activitys;

    public GraphicFaceTracker( Context mContext, String activitys) {
        this.mContext = mContext;
        this.flag = false;
        this.activitys = activitys;
        if (activitys.equals("camerasource")) {
            dipsCameraSource = (DipsCameraSource) mContext;
        } else {
            dipsCapture = (DipsCapture) mContext;
        }
    }

    private void blink(float value) {
        switch (state) {
            case 0:
                if (value > OPEN_THRESHOLD) {
                    // Both eyes are initially open
                    state = 1;
                }
                break;
            case 1:
                if (value < CLOSE_THRESHOLD) {
                    // Both eyes become closed
                    state = 2;
                }
                break;
            case 2:
                if (value > OPEN_THRESHOLD) {
                    // Both eyes are open again
                    state = 0;
                    if (activitys.equals("camerasource")) {
                        dipsCameraSource.captureImage();
                    } else {
                        dipsCapture.captureImage();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * Update the position/characteristics of the face within the overlay.
     */
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        if (activitys.equals("camerasource")) {
            if (DipsCameraSource.flagCapture) {
                return;
            }
        } else {
            if (DipsCapture.flagCapture) {
                return;
            }

            PointF posF = face.getPosition();
            float posFX = posF.x;
            float posFY = posF.y;
            if (((posFX < 90 || posFX > 220) || (posFY < 120 || posFY > 320)) && flag == false) {
                Log.e("CEK","###### MASUK IF TOAST ######");
                flag = true;
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(mContext.getApplicationContext(), "Mohon tempatkan Wajah Anda sesuai Lingkaran atau Dekatkan Wajah Anda..!!!",Toast.LENGTH_SHORT).show();
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return;
            } else if ((posFX < 120 && posFY > 220) && flag == false) {
                Log.e("CEK","###### MASUK IF TOAST TERLALU DEKAT ######");
                flag = true;
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(mContext.getApplicationContext(),"Wajah Anda Terlalu Dekat..!!!",Toast.LENGTH_SHORT).show();
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return;
            }

        }
        float left = face.getIsLeftEyeOpenProbability();
        float right = face.getIsRightEyeOpenProbability();
        if ((left == Face.UNCOMPUTED_PROBABILITY) ||
                (right == Face.UNCOMPUTED_PROBABILITY)) {
            // One of the eyes was not detected.
            return;
        }
        float value = Math.min(left, right);
        flag = false;
        blink(value);
    }

}

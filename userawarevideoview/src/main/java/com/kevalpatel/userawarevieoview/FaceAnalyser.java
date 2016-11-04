package com.kevalpatel.userawarevieoview;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

/**
 * Created by Keval on 27-Oct-16.
 * This class is responsible for analysing and detecting the eyes and face from the frame of the
 * front camera using mobile vision library.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

class FaceAnalyser {
    private static final int RC_HANDLE_GMS = 4525;

    private final UserAwareVideoView mUserAwareVideoView;       //Instance of the video view
    private FaceDetector mDetector;                             //Face detector instance
    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;

    private boolean isTrackingRunning = false;                  //Bool to indicate is eye tracking is currently running or not?
    private Activity mActivity;

    /**
     * Public constructor.
     *
     * @param activity activity.
     */
    FaceAnalyser(Activity activity, UserAwareVideoView userAwareVideoView, CameraSourcePreview preview) {
        if (activity != null) {
            mActivity = activity;
            mUserAwareVideoView = userAwareVideoView;
        } else {
            throw new RuntimeException("Cannot start without callback listener.");
        }

        if (preview != null) {
            mPreview = preview;
        } else {
            throw new RuntimeException("Cannot start without camera source preview.");
        }
    }

    /**
     * Stop eye tracking.
     */
    void stopEyeTracker() {
        isTrackingRunning = false;

        if (mDetector != null) mDetector.release();
        if (mPreview != null) mPreview.release();
    }


    /**
     * Create face decoder and camera source.
     */
    private void creteCameraTracker() {
        mDetector = new FaceDetector.Builder(mActivity)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        mDetector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!mDetector.isOperational()) {
            mUserAwareVideoView.onErrorOccurred();
            Log.e("Start Tracking", "Face tracker is not operational.");
        }

        mCameraSource = new CameraSource.Builder(mActivity, mDetector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    /**
     * Start eye tracking.
     */
    void startFaceTracker() {
        //check if the device has front camera.
        if (!isFrontCameraAvailable()) {
            mUserAwareVideoView.onFrontCameraNotFound();
            Log.e("Start Tracking", "Front camera not found.");
            return;
        }

        //check for the camera permission
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            mUserAwareVideoView.onCameraPermissionNotAvailable();
            Log.e("Start Tracking", "Camera permission not found.");
            return;
        }

        // check that the device has play services available.
        int statusCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                mActivity.getApplicationContext());
        if (statusCode != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(mActivity, statusCode, RC_HANDLE_GMS);
            dlg.show();
            Log.e("Start Tracking", "Google Play Service not found.");

            mUserAwareVideoView.onErrorOccurred();
            return;
        }

        //create camera source
        creteCameraTracker();

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                Log.d("startTracking",e.getMessage());
                mUserAwareVideoView.onErrorOccurred();
                mCameraSource.release();
                mCameraSource = null;

                mUserAwareVideoView.onErrorOccurred();
            }
        }

        isTrackingRunning = true;
    }

    /**
     * Boolean to indicate if the tracking is running or not?
     *
     * @return true if eye tracking is running.
     */
    boolean isTrackingRunning() {
        return isTrackingRunning;
    }

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker();
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        int isEyesClosedCount = 0;

        private GraphicFaceTracker() {
        }


        @Override
        public void onNewItem(int faceId, Face item) {
            Log.d("onNewItem","" + faceId);
        }

        /**
         * When new frame analysed.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            Log.d("FaceTracker", "onUpdate" + face.getIsLeftEyeOpenProbability());

            //if left and right eyes are open. (Probability more than 10%)
            if (face.getIsLeftEyeOpenProbability() > 0.10 && face.getIsRightEyeOpenProbability() > 0.10) {
                isEyesClosedCount = 0;
                mUserAwareVideoView.onUserAttentionAvailable();
            } else {
                isEyesClosedCount++;
                if (isEyesClosedCount > 2) mUserAwareVideoView.onUserAttentionGone();
            }
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            Log.d("onMissing","" );
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mUserAwareVideoView.onUserAttentionGone();
        }
    }

    /**
     * Check if the device has front camera or not?
     *
     * @return true if the device has front camera.
     */
    private boolean isFrontCameraAvailable() {
        int numCameras = Camera.getNumberOfCameras();
        return numCameras > 0 && mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

}

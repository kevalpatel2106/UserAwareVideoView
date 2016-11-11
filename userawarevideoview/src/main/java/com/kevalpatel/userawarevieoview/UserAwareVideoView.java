/*
 * Copyright 2016 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel.userawarevieoview;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

/**
 * Created by Keval on 28-Oct-16.
 * This video view will track user's eyes and if the user is not looking at the screen this will automatically
 * pause the video and whenever user look at the screen this will start the video again from where it
 * was paused.
 * <p>
 * This users front facing camera of the device to detect user eyes. Whenever video playing starts,
 * this will start tracking the eyes and whenever video is stopped/paused by the application who integrates it
 * this will stop tracking to conserve the battery.
 * <p>
 * Make sure to handle all the errors
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class UserAwareVideoView extends VideoView {
    private FaceAnalyser mFaceAnalyser;
    private int mPauseTime;     //Time when video paused last time.
    private UserAwarenessListener mUserAwarenessListener;   //Callback to the parent activity.
    private LightIntensityManager mLightIntensityManager;   //Measure surrounding light conditions.


    //====================== Constructors ======================//

    public UserAwareVideoView(Context context) {
        super(context);
        init(context);
    }

    public UserAwareVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UserAwareVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UserAwareVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    /**
     * Initialize {@link FaceAnalyser}. At this stage only fake camera preview (See {@link CameraSourcePreview})
     * will be added to the root view of the activity/fragment. Eye tracking won't start.
     *
     * @param context instance of the caller. This instance should be activity only.
     * @throws RuntimeException if the context is not the instance of the activity.
     */
    private void init(@NonNull Context context) {

        //check if the context is of activity.
        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            throw new RuntimeException("Cannot initialize with other than Activity context.");
        }

        //initialize the eye tracking
        mFaceAnalyser = new FaceAnalyser(activity, this, addPreView(activity));

        //Initialize the light sensor.
        mLightIntensityManager = new LightIntensityManager(this, activity);
    }

    /**
     * Set the callback listener. Add the callback listener before start playing the video.
     *
     * @param listener {@link UserAwarenessListener}
     */
    public void setUserAwarenessListener(UserAwarenessListener listener) {
        mUserAwarenessListener = listener;
    }


    /**
     * Add camera preview to the root of the activity layout. This view will display the fake camera
     * preview the to layout. As the android camera cannot work without camera preview
     * this will create a fake preview surface and attach to the camera. This view is added to the
     * root layout of the activity view and having size 1px * 1px. That is almost invisible.
     *
     * @return {@link CameraSourcePreview}
     */
    private CameraSourcePreview addPreView(@NonNull Activity activity) {

        View rootView = ((ViewGroup) activity.getWindow().getDecorView().getRootView()).getChildAt(0);

        //create fake camera view
        CameraSourcePreview cameraSourcePreview = new CameraSourcePreview(activity, this);
        cameraSourcePreview.setLayoutParams(new ViewGroup
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (rootView instanceof LinearLayout) {
            LinearLayout linearLayout = (LinearLayout) rootView;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1, 1);
            linearLayout.addView(cameraSourcePreview, params);
        } else if (rootView instanceof RelativeLayout) {
            RelativeLayout relativeLayout = (RelativeLayout) rootView;

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1, 1);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            relativeLayout.addView(cameraSourcePreview, params);
        } else if (rootView instanceof FrameLayout) {
            FrameLayout frameLayout = (FrameLayout) rootView;

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(1, 1);
            frameLayout.addView(cameraSourcePreview, params);
        } else {
            throw new RuntimeException("Root view of the activity/fragment does not have supported view in parent.");
        }

        return cameraSourcePreview;
    }

    //This method is called externally by the user, never call it from SDK.
    //Instead use super method to start video playing.
    @Override
    public void start() {
        //start eye tracking if it is not running already
        startEyeTracking();

        super.start();

        //start light sensor
        mLightIntensityManager.startLightMonitoring();
    }

    @Override
    public void stopPlayback() {
        //Stop eye tracking.
        stopEyeTracking();

        super.stopPlayback();

        //stop light sensor
        mLightIntensityManager.stopLightMonitoring();
    }


    @Override
    public void pause() {
        super.pause();

        //Stop eye tracking.
        stopEyeTracking();

        //stop light sensor
        mLightIntensityManager.stopLightMonitoring();
    }

    /**
     * This method will called, whenever user is not looking at the display.
     * This will pause the video currently playing.
     */
    void onUserAttentionGone() {
        if (isPlaying()) {  //If video is playing pause it.
            mPauseTime = getCurrentPosition(); //mPauseTime is an int
            super.pause();
        }
    }

    /**
     * This method will called, whenever user is started looking at the display.
     * This will pause the video currently playing.
     */
    void onUserAttentionAvailable() {
        if (!isPlaying()) {
            seekTo(mPauseTime);
            super.start();
        }
    }

    /**
     * This method will called, whenever there is any error occurred while initializing Face decoder in
     * vision api.
     */
    void onErrorOccurred() {
        mUserAwarenessListener.onErrorOccurred(Errors.UNDEFINED);

        //start light sensor because eye tracking is not running we don't need light sensor now
        mLightIntensityManager.stopLightMonitoring();
    }

    /**
     * This method will called whenever camera permission is not available. If the camera permission
     * is not available this will stop the video untill user provides the camera permission.
     */
    void onCameraPermissionNotAvailable() {
        super.stopPlayback();

        //Stop eye tracking.
        stopEyeTracking();

        //start light sensor because eye tracking is not running we don't need light sensor now
        mLightIntensityManager.stopLightMonitoring();

        mUserAwarenessListener.onErrorOccurred(Errors.CAMERA_PERMISSION_NOT_AVAILABLE);
    }

    /**
     * This method will called whenever there is no front camera available. In this case video
     * playing will still continue only face tacking will stop.
     */
    void onFrontCameraNotFound() {
        mUserAwarenessListener.onErrorOccurred(Errors.FRONT_CAMERA_NOT_AVAILABLE);

        //Stop eye tracking.
        stopEyeTracking();

        //start light sensor because eye tracking is not running we don't need light sensor now
        mLightIntensityManager.stopLightMonitoring();
    }

    /**
     * This method will called whenever there is not enough light to tack and detect user's eyes using
     * front camera. This will stop the face tracking as the face tracking might be wrong. But, the video
     * keeps playing. Light sensor is still on and is monitoring lighting conditions. If the light intensity
     * is enough again than {@link #onEnoughLightAvailable()} will start eye tracker again.
     */
    void onLowLight() {
        mUserAwarenessListener.onErrorOccurred(Errors.LOW_LIGHT);

        //Stop eye tracking.
        stopEyeTracking();
    }

    /**
     * When there is enough light available after the low light condition this method will execute.
     * This will start eye tracking again.
     */
    void onEnoughLightAvailable() {
        //start eye tracking if it is not running already
        startEyeTracking();
    }

    /**
     * This method initialize the camera and start face tracking and eyes tracking using mobile
     * vision api.
     */
    private void startEyeTracking() {
        if (!mFaceAnalyser.isTrackingRunning()) {
            mFaceAnalyser.startFaceTracker();
            mUserAwarenessListener.onEyeTrackingStarted();  //notify caller
        }
    }

    /**
     * This method stops and release the camera and stop face tracking and eyes tracking.
     */
    private void stopEyeTracking() {
        //Stop eye tracking.
        if (mFaceAnalyser.isTrackingRunning()) {
            mFaceAnalyser.stopEyeTracker();
            mUserAwarenessListener.onEyeTrackingStop();
        }
    }
}

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

        //initialize
        mFaceAnalyser = new FaceAnalyser(activity, this, addPreView(activity));
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
        CameraSourcePreview cameraSourcePreview = new CameraSourcePreview(activity);
        cameraSourcePreview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

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

    @Override
    public void start() {
        super.start();

        //start eye tracking if it is not running already
        if (!mFaceAnalyser.isTrackingRunning()) {
            mFaceAnalyser.startFaceTracker();
            mUserAwarenessListener.onEyeTrackingStarted();  //notify caller
        }
    }

    @Override
    public void stopPlayback() {
        super.stopPlayback();

        //Stop eye tracking.
        if (mFaceAnalyser.isTrackingRunning()) {
            mFaceAnalyser.stopFaceTracker();
            mUserAwarenessListener.onEyeTrackingStop();     //notify caller
        }
    }


    @Override
    public void pause() {
        super.pause();

        //Stop eye tracking.
        if (mFaceAnalyser.isTrackingRunning()) {
            mFaceAnalyser.stopFaceTracker();
            mUserAwarenessListener.onEyeTrackingStop();
        }
    }

    /**
     * This method will called, whenever user is not looking at the display.
     * This will pause the video currently playing.
     */
    void onUserAttentionGone() {
        if (isPlaying()) {
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
    }

    void onCameraPermissionNotAvailable() {
        stopPlayback();
        mUserAwarenessListener.onErrorOccurred(Errors.CAMERA_PERMISSION_NOT_AVAILABLE);
    }
}

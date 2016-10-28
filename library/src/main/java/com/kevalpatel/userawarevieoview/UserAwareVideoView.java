package com.kevalpatel.userawarevieoview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

/**
 * Created by Keval on 28-Oct-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class UserAwareVideoView extends VideoView {
    private FaceAnalyser mFaceAnalyser;
    private int mPauseTime;
    private UserAwarenessListener mUserAwarenessListener;
    private Activity mActivity;


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

    private void init(@NonNull Context context) {
        //check if the context is of mActivity.
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        } else {
            throw new RuntimeException("Cannot initialize with other than Activity context.");
        }

        mFaceAnalyser = new FaceAnalyser(mActivity, this, addPreView(mActivity));
    }

    public void setUserAwarenessListener(UserAwarenessListener listener) {
        mUserAwarenessListener = listener;
    }


    /**
     * Add camera preview to the root of the mActivity layout.
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
            throw new RuntimeException("Root view of the mActivity/fragment cannot be frame layout");
        }

        return cameraSourcePreview;
    }

    @Override
    public void start() {
        super.start();

        if (!mFaceAnalyser.isTrackingRunning()) {
            mFaceAnalyser.startFaceTracker();
            mUserAwarenessListener.onEyeTrackingStarted();
        }
    }

    @Override
    public void stopPlayback() {
        super.stopPlayback();
        if (mFaceAnalyser.isTrackingRunning()) {
            mFaceAnalyser.stopFaceTracker();
            mUserAwarenessListener.onEyeTrackingStop();
        }
    }


    @Override
    public void pause() {
        super.pause();
        if (mFaceAnalyser.isTrackingRunning()) {
            mFaceAnalyser.stopFaceTracker();
            mUserAwarenessListener.onEyeTrackingStop();
        }
    }

    void onUserAttentionGone() {
        if (isPlaying()) {
            mPauseTime = getCurrentPosition(); //stopPosition is an int
            super.pause();
        }
    }

    void onUserAttentionAvailable() {
        if (!isPlaying()) {
            seekTo(mPauseTime);
            super.start();
        }
    }

    void onErrorOccurred() {
        mUserAwarenessListener.onErrorOccurred();
    }

    void onCameraPermissionNotAvailable() {
        stopPlayback();
        mUserAwarenessListener.onCameraPermissionNotAvailable();
    }
}

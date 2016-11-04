package sample.videoview.kevalpatel.com.userawarevideoview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;

import com.kevalpatel.userawarevieoview.Errors;
import com.kevalpatel.userawarevieoview.UserAwareVideoView;
import com.kevalpatel.userawarevieoview.UserAwarenessListener;

public class MainActivity extends AppCompatActivity implements UserAwarenessListener {
    private static final int RC_HANDLE_CAMERA_PERM = 123;
    private UserAwareVideoView mVideoView;

    private ImageView mTrackingStatusIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set the is tracking indicator
        mTrackingStatusIv = (ImageView) findViewById(R.id.eye_tracking);
        mTrackingStatusIv.setVisibility(View.GONE);

        //set user aware video view
        mVideoView = (UserAwareVideoView) findViewById(R.id.video_view);
        mVideoView.setUserAwarenessListener(this);
        mVideoView.setVideoURI(Uri.parse("http://clips.vorwaerts-gmbh.de/VfE_html5.mp4"));

        //attach tge media controller
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mVideoView);
        mediaController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVideoView.stopPlayback();
                mVideoView.start();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVideoView.stopPlayback();
                mVideoView.start();
            }
        });

        mVideoView.setMediaController(mediaController);

        //Start video playing. This will start the user eye tracking too.
        //Do not care about permissions. Permissions will check internally.
        //Just handle errors.
        mVideoView.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mVideoView.start();
        }
    }

    /**
     * This indicates that some error occurred while eye detection. This provides error code that are
     * defined for the particular situations.
     *
     * @see Errors
     */
    @Override
    public void onErrorOccurred(int errorCode) {

        switch (errorCode) {
            case Errors.UNDEFINED:
                Snackbar.make(findViewById(R.id.activity_main), "Error occurred while tracking your eyes. No worries video will keep playing.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, null)
                        .show();
                break;
            case Errors.FRONT_CAMERA_NOT_AVAILABLE:
                Snackbar.make(findViewById(R.id.activity_main), "Device does not have front camera. No worries video will keep playing.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, null)
                        .show();
                break;
            case Errors.CAMERA_PERMISSION_NOT_AVAILABLE:
                //This indicates that camera permission is not available.
                //Ask for the runtime camera permission.
                final String[] permissions = new String[]{Manifest.permission.CAMERA};
                ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
                break;
            case Errors.LOW_LIGHT:
                //This indicates that there is dark out side. We cannot detect user's face.
                Snackbar.make(findViewById(R.id.activity_main), "There is not enough light to detect your eyes. No worries video will keep playing.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, null)
                        .show();
                break;
        }
    }

    /**
     * Whenever the eye tracking starts this method will call.
     */
    @Override
    public void onEyeTrackingStarted() {
        mTrackingStatusIv.setVisibility(View.VISIBLE);
    }

    /**
     * Whenever the eye tracking stops this method will called.
     */
    @Override
    public void onEyeTrackingStop() {
        mTrackingStatusIv.setVisibility(View.GONE);
    }
}

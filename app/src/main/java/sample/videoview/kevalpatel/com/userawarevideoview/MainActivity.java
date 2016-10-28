package sample.videoview.kevalpatel.com.userawarevideoview;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;

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

        mTrackingStatusIv = (ImageView) findViewById(R.id.eye_tracking);
        mTrackingStatusIv.setVisibility(View.GONE);

        mVideoView = (UserAwareVideoView) findViewById(R.id.video_view);
        mVideoView.setUserAwarenessListener(this);
        mVideoView.setVideoURI(Uri.parse("http://clips.vorwaerts-gmbh.de/VfE_html5.mp4"));

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mVideoView);
        mediaController.setMediaPlayer(mVideoView);

        mVideoView.setMediaController(mediaController);
        mVideoView.start();
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(findViewById(R.id.activity_main), R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, listener)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mVideoView.start();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Prevent Screen off")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onErrorOccurred() {
        Snackbar.make(findViewById(R.id.activity_main), "Error occurred while tracking your eyes. No worries video will keep playing.",
                Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, null)
                .show();
    }

    @Override
    public void omLowLight() {
        Snackbar.make(findViewById(R.id.activity_main), "There is not enough light to detect your eyes. No worries video will keep playing.",
                Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onEyeTrackingStarted() {
        mTrackingStatusIv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEyeTrackingStop() {
        mTrackingStatusIv.setVisibility(View.GONE);
    }

    @Override
    public void onCameraPermissionNotAvailable() {
        requestCameraPermission();
    }
}

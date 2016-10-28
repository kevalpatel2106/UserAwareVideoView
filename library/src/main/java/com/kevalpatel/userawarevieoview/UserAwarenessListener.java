package com.kevalpatel.userawarevieoview;

/**
 * Created by Keval on 28-Oct-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public interface UserAwarenessListener {

    void onErrorOccurred();

    void omLowLight();

    void onEyeTrackingStarted();

    void onEyeTrackingStop();

    void onCameraPermissionNotAvailable();
}

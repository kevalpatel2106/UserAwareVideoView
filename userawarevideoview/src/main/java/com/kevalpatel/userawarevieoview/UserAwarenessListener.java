package com.kevalpatel.userawarevieoview;

/**
 * Created by Keval on 28-Oct-16.
 * This is the callback listener for the parent activity.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public interface UserAwarenessListener {

    /**
     * This indicates that some error occurred.
     *
     * @param errorCode error code of the particular error
     * @see Errors
     */
    void onErrorOccurred(int errorCode);

    /**
     * This callback will called when the eye tacking is started.
     */
    void onEyeTrackingStarted();

    /**
     * This callback will called when eye tracking is stopped.
     */
    void onEyeTrackingStop();
}

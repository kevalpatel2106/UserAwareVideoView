package com.kevalpatel.userawarevieoview;

/**
 * Created by Keval on 28-Oct-16.
 * This class contains different error codes, that can occur during eye tracking.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class Errors {

    private Errors() {
    }

    /**
     * This error code indicates that error is undefined. If this error occurs, eye tracking will stop
     * but the video will continue to play. User/Developer can play/pause/stop video manually.
     */
    public static final int UNDEFINED = 0;

    /**
     * This indicates that camera permission is not available.
     * Ask for the camera permission at runtime.
     */
    public static final int CAMERA_PERMISSION_NOT_AVAILABLE = 1;

    /**
     * This error code indicates that device dose not have the front camera.If this error occurs,
     * eye tracking will stop but the video will continue to play. User/Developer can play/pause/stop
     * video manually.
     */
    public static final int FRONT_CAMERA_NOT_AVAILABLE = 2;

    /**
     * This error code indicates that there is low light environment around. So eye detector cannot
     * detect user face/eyes. .If this error occurs,
     * eye tracking will stop but the video will continue to play. User/Developer can play/pause/stop
     * video manually.
     */
    public static final int LOW_LIGHT = 3;
}

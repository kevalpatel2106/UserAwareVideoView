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
     * Ask for the camera permission at runtime. This will stop the video playback.
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
     * detect user face/eyes. If this error occurs,
     * eye tracking will stop but the video will continue to play. User/Developer can play/pause/stop
     * video manually.
     */
    public static final int LOW_LIGHT = 3;
}

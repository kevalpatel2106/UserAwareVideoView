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

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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Keval on 30-Oct-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

class LightIntensityManager {
    //Minimum light intensity required to operate the eye detection.
    private static final float LIGHT_INTENSITY_THRESHOLD = 1F;

    private final UserAwareVideoView mUserAwareVideoView;

    private final SensorManager mSensorManager;
    private final Sensor mLightSensor;

    private float mLastIntensity = 0f;
    private CountDownTimer mCountDownTimer;

    private final SensorEventListener listener = new SensorEventListener() {

        boolean isInLowLightCondition;

        @Override
        public void onSensorChanged(SensorEvent event) {
            mLastIntensity = event.values[0];

            if (mLastIntensity < LIGHT_INTENSITY_THRESHOLD && mCountDownTimer == null) {
                mCountDownTimer = new CountDownTimer(5000, 5000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        if (mLastIntensity < LIGHT_INTENSITY_THRESHOLD) {
                            mUserAwareVideoView.onLowLight();
                            isInLowLightCondition = true;
                        }

                        mCountDownTimer = null;
                    }
                };
                mCountDownTimer.start();
            }else if (isInLowLightCondition){
                mUserAwareVideoView.onEnoughLightAvailable();
            }

            Log.d("light sensor", mLastIntensity + "");
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    /**
     * Public constructor. This will initialize the light sensor to detect light intensity.
     * To start listening light intensity changes call {@link #startLightMonitoring()}.
     *
     * @param context instance of the caller.
     */
    LightIntensityManager(@NonNull UserAwareVideoView context, Context context1) {
        mUserAwareVideoView = context;

        // Obtain references to the SensorManager and the Light Sensor
        mSensorManager = (SensorManager) context1.getSystemService(SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    void startLightMonitoring() {
        mSensorManager.registerListener(listener, mLightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    void stopLightMonitoring() {
        mSensorManager.unregisterListener(listener);

        //stop the timer if running
        if (mCountDownTimer != null) mCountDownTimer.cancel();
    }
}

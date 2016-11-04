package com.kevalpatel.userawarevieoview;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Keval on 30-Oct-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

class LightIntensityManager {

    private final SensorManager sensorManager;
    private final Sensor lightSensor;
    private final SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d("light sensor", event.values[0] + "");
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
    LightIntensityManager(@NonNull Context context) {
        // Obtain references to the SensorManager and the Light Sensor
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    void startLightMonitoring() {
        sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    void stopLightMonitoring() {
        sensorManager.unregisterListener(listener);
    }
}

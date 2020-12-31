package com.binzee.foxdevframe.utils.device.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * 传感器回调
 *
 * @author 狐彻
 * 2020/11/10 14:49
 */
public interface SensorListener {

    /**
     * 加速度传感器监听
     *
     * @author 狐彻
     * 2020/11/10 14:02
     */
    abstract class AccelerometerListener implements SensorEventListener {

        /**
         * 数值变化回调
         *
         * 参数为各方向加速度
         * @author 狐彻 2020/11/10 14:01
         */
        public abstract void onValueChanged(float x, float y, float z);

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            onValueChanged(values[0], values[1], values[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    /**
     * 陀螺仪传感器监听
     *
     * @author 狐彻
     * 2020/11/10 14:02
     */
    abstract class GyroscopeListener implements SensorEventListener {

        /**
         * 数值变化回调
         *
         * 参数为各方向角速度，单位是弧度/s，正值为逆时针
         * @author 狐彻 2020/11/10 14:01
         */
        public abstract void onValueChanged(float x, float y, float z);

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            onValueChanged(values[0], values[1], values[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}

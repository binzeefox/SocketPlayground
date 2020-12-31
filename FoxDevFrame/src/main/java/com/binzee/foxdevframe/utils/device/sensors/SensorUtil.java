package com.binzee.foxdevframe.utils.device.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.SparseArray;

import com.binzee.foxdevframe.FoxCore;

import java.util.HashMap;
import java.util.Map;

/**
 * 传感器工具类
 *
 * @author 狐彻
 * 2020/11/10 13:37
 */
public class SensorUtil {

    /**
     * 传感器类别枚举
     *
     * @author 狐彻 2020/11/10 14:10
     */
    public enum SensorType {
        ACCELEROMETER(Sensor.TYPE_ACCELEROMETER),  //加速度传感器
        GYROSCOPE(Sensor.TYPE_GYROSCOPE),   //陀螺仪传感器
        MAGNETIC_FIELD(Sensor.TYPE_MAGNETIC_FIELD), //磁场传感器
        GRAVITY(Sensor.TYPE_GRAVITY);   //重力传感器

        int value;  //值

        SensorType(int value) {
            this.value = value;
        }

        Sensor getSensor() {
            SensorManager manager = (SensorManager) FoxCore.getApplication()
                    .getSystemService(Context.SENSOR_SERVICE);
            return manager.getDefaultSensor(value);
        }
    }

    //传感器集合
    private static final SparseArray<Map<SensorType, SensorDevice>> DEVICE_COLLECTION = new SparseArray<>();
    static {
        DEVICE_COLLECTION.append(SensorManager.SENSOR_DELAY_FASTEST, new HashMap<>());
        DEVICE_COLLECTION.append(SensorManager.SENSOR_DELAY_GAME, new HashMap<>());
        DEVICE_COLLECTION.append(SensorManager.SENSOR_DELAY_NORMAL, new HashMap<>());
        DEVICE_COLLECTION.append(SensorManager.SENSOR_DELAY_UI, new HashMap<>());
    }

    /**
     * 获取传感器
     *
     * @param delay SensorManager.SENSOR_DELAY_FASTEST
     *              SensorManager.SENSOR_DELAY_GAME
     *              SensorManager.SENSOR_DELAY_NORMAL
     *              SensorManager.SENSOR_DELAY_UI
     * @author 狐彻 2020/11/10 14:04
     */
    public synchronized static SensorDevice getSensor(SensorType sensorType, int delay) {
        Map<SensorType, SensorDevice> deviceMap = DEVICE_COLLECTION.get(delay);
        SensorDevice device = deviceMap.get(sensorType);
        if (device != null) return device;
        device = createSensorDevice(sensorType, delay);
        deviceMap.put(sensorType, device);
        return device;
    }

    /**
     * 创建
     *
     * @author 狐彻 2020/11/10 14:12
     */
    private static SensorDevice createSensorDevice(SensorType sensorType, int delay) {
        return new SensorDevice(sensorType.getSensor(), delay);
    }
}

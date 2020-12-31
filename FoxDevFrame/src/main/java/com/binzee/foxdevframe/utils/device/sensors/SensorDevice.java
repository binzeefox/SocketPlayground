package com.binzee.foxdevframe.utils.device.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.binzee.foxdevframe.FoxCore;

import java.util.HashSet;
import java.util.Set;

/**
 * 传感器抽象
 * <p>
 * 用计数器记录当前连接的数量和活跃数量
 * 当暂停使用的连接与总连接数量一致，则传感器资源回收，直到有新的连接活跃
 *
 * @author 狐彻
 * 2020/11/10 10:54
 */
public class SensorDevice {
    private static final String TAG = "SensorDevice";
    private final Sensor mSensor;
    private final int mDelay;
    private final SensorManager mManager;

    private final Set<SensorEventListener> listenerSet = new HashSet<>();   //保存回调的池
    private final SensorEventListener mainListener = new MainListener();    //主回调

    /**
     * 构造器
     *
     * @param sensor 传感器实例
     * @param delay  SensorManager.SENSOR_DELAY_FASTEST
     *               SensorManager.SENSOR_DELAY_GAME
     *               SensorManager.SENSOR_DELAY_NORMAL
     *               SensorManager.SENSOR_DELAY_UI
     * @author 狐彻 2020/11/10 13:48
     */
    SensorDevice(Sensor sensor, int delay) {
        mSensor = sensor;
        mDelay = delay;
        mManager = (SensorManager) FoxCore.getApplication()
                .getSystemService(Context.SENSOR_SERVICE);
    }

    public Sensor getSensor() {
        return mSensor;
    }

    /**
     * 开始连接
     *
     * @author 狐彻 2020/11/10 11:25
     */
    public void connect(SensorEventListener listener) {
        if (listenerSet.isEmpty()) 
            mManager.registerListener(mainListener, mSensor, mDelay);
        listenerSet.add(listener);
    }

    /**
     * 断开连接
     *
     * @author 狐彻 2020/11/10 13:36
     */
    public void disconnect(SensorEventListener listener){
        listenerSet.remove(listener);
        if (listenerSet.isEmpty())
            mManager.unregisterListener(mainListener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 主回调
     * 
     * @author 狐彻 2020/11/10 13:58
     */
    private class MainListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            for (SensorEventListener listener: listenerSet)
                listener.onSensorChanged(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            for (SensorEventListener listener: listenerSet)
                listener.onAccuracyChanged(sensor, accuracy);
        }
    }
}

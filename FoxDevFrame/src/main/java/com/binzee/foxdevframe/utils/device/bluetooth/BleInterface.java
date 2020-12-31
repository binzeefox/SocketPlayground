package com.binzee.foxdevframe.utils.device.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import androidx.annotation.Nullable;

/**
 * 低功耗蓝牙接口
 *
 * @author 狐彻
 * 2020/11/10 8:56
 */
public interface BleInterface {

    /**
     * 是否支持蓝牙
     *
     * @author 狐彻 2020/11/10 9:22
     */
    boolean isSupportBle();

    /**
     * 是否已经开启蓝牙
     *
     * @author 狐彻 2020/11/10 9:22
     */
    boolean isEnableBle();

    /**
     * 开启蓝牙
     *
     * @param context   开启的上下文，会在弹窗上提示。默认顶部Activity，若没有则默认为Application
     * @author 狐彻 2020/11/10 9:14
     */
    void openBle(@Nullable Context context);

    /**
     * 获取扫描器
     *
     * @author 狐彻 2020/11/10 9:27
     */
    BleScanner getScanner();

    /**
     * 获取低功耗蓝牙包装类
     *
     * @author 狐彻 2020/11/10 9:45
     */
    BleDevice getToolDevice(BluetoothDevice device);

    BleDevice getToolDeviceByMac(String mac);
}

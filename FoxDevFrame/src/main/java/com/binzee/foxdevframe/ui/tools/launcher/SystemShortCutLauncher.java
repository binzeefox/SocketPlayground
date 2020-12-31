package com.binzee.foxdevframe.ui.tools.launcher;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.binzee.foxdevframe.FoxCore;

/**
 * 系统设置页面快捷入口
 *
 * @author 狐彻
 * 2020/10/27 8:23
 */
public class SystemShortCutLauncher {
    private final Launcher launcher;

    SystemShortCutLauncher(Launcher launcher) {
        this.launcher = launcher;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 业务方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 跳转应用详情信息页
     *
     * @author 狐彻 2020/10/27 8:26
     */
    public void launchApplicationDetails() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", FoxCore.getPackageName(), null);
        intent.setData(uri);
        launcher.toActivity(intent)
                .commit();
    }

    /**
     * 跳转飞行模式设置界面
     *
     * @author 狐彻 2020/10/27 8:47
     */
    public void launchConnectionSetting() {
        Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        launcher.toActivity(intent)
                .commit();
    }

    /**
     * 跳转连接与共享设置界面
     *
     * @author 狐彻 2020/10/27 8:47
     */
    public void launchWirelessSettings() {
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        launcher.toActivity(intent)
                .commit();
    }

    /**
     * 跳转蓝牙设置
     *
     * @author 狐彻 2020/10/27 8:53
     */
    public void launchBluetoothSettings() {
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        launcher.toActivity(intent)
                .commit();
    }

    /**
     * 跳转网络设置
     *
     * @author 狐彻 2020/10/27 8:55
     */
    public void launchNetworkSettings() {
        Intent intent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
        launcher.toActivity(intent)
                .commit();
    }

    /**
     * 跳转定位服务
     *
     * @author 狐彻 2020/10/27 8:56
     */
    public void launchLocationSetting() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        launcher.toActivity(intent)
                .commit();
    }

    /**
     * 跳转声音设置
     *
     * @author 狐彻 2020/10/27 8:57
     */
    public void launchSoundSetting() {
        Intent intent = new Intent(Settings.ACTION_SOUND_SETTINGS);
        launcher.toActivity(intent)
                .commit();
    }

    /**
     * 跳转wifi设置
     *
     * @author 狐彻 2020/10/27 8:58
     */
    public void launchWifiSetting() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        launcher.toActivity(intent)
                .commit();
    }
}

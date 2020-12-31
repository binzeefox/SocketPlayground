package com.binzee.foxdevframe.utils.device.bluetooth;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;

import java.util.List;

/**
 * 低功耗蓝牙扫描器
 *
 * @author 狐彻
 * 2020/11/10 9:26
 */
public interface BleScanner {

    /**
     * 设置回调
     *
     * @author 狐彻 2020/11/11 10:04
     */
    BleScanner setCallback(ScanCallback callback);

    /**
     * 设置扫描时间
     *
     * @author 狐彻 2020/11/10 9:28
     */
    BleScanner setTimeout(long timeout);

    /**
     * 设置扫描配置
     *
     * @author 狐彻 2020/11/10 9:49
     */
    BleScanner setScanSettings(ScanSettings settings);

    /**
     * 添加过滤器
     *
     * @author 狐彻 2020/11/10 9:50
     */
    BleScanner addFilter(ScanFilter filter);

    /**
     * 批量添加过滤器
     *
     * @author 狐彻 2020/11/10 9:50
     */
    BleScanner addFilters(List<ScanFilter> filters);

    /**
     * 开始扫描
     *
     * @author 狐彻 2020/11/10 9:23
     */
    void beginScan();

    /**
     * 停止扫描
     *
     * @author 狐彻 2020/11/10 9:24
     */
    void stopScan();

    ///////////////////////////////////////////////////////////////////////////
    // 回调
    ///////////////////////////////////////////////////////////////////////////

    abstract class BleScanCallback extends ScanCallback {
        public void onStop(){}
    }
}

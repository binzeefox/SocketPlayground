package com.binzee.foxdevframe.utils.device.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;

import androidx.annotation.RequiresPermission;

import java.util.ArrayList;
import java.util.List;

/**
 * 低功耗蓝牙扫描器实现
 *
 * @author 狐彻
 * 2020/11/10 9:31
 */
class FoxBleScanner implements BleScanner {
    private static final String TAG = "FoxBleScanner";
    private final BluetoothAdapter adapter;
    private ScanCallback callback;

    private final List<ScanFilter> filterList = new ArrayList<>();  //扫描筛选器
    private long timeout = -1;   //扫描时间
    private ScanSettings scanSettings = null;   //扫描设置

    FoxBleScanner(BluetoothAdapter adapter) {
        this.adapter = adapter;
    }


    @Override
    public BleScanner setCallback(ScanCallback callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public BleScanner setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public BleScanner setScanSettings(ScanSettings settings) {
        this.scanSettings = settings;
        return this;
    }

    @Override
    public BleScanner addFilter(ScanFilter filter) {
        filterList.add(filter);
        return this;
    }

    @Override
    public BleScanner addFilters(List<ScanFilter> filters) {
        filterList.addAll(filters);
        return this;
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public void beginScan() {
        if (callback == null) throw new RuntimeException(
                new IllegalAccessException("Call setCallback(ScanCallback callback) first!!!")
        );
        adapter.getBluetoothLeScanner().startScan(filterList, scanSettings, callback);
        if (timeout < 0) timeout = 0;
        if (timeout != -1) new Handler().postDelayed(this::stopScan, timeout);
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public void stopScan() {
        if (callback instanceof BleScanCallback)
            ((BleScanCallback) callback).onStop();
        adapter.getBluetoothLeScanner().stopScan(callback);
    }
}

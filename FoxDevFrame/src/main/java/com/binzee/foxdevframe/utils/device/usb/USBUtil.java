package com.binzee.foxdevframe.utils.device.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.binzee.foxdevframe.FoxCore;

import java.util.Map;

/**
 * USB工具类
 *
 * @author 狐彻
 * 2020/11/10 17:09
 */
public class USBUtil {
    private static final String TAG = "USBUtil";
    private final UsbManager mManager;

    private USBUtil() {
        mManager = (UsbManager) FoxCore.getApplication()
                .getSystemService(Context.USB_SERVICE);
    }

    /**
     * 获取所有usb device
     *
     * @author 狐彻 2020/11/10 17:12
     */
    public Map<String, UsbDevice> getList() {
        return mManager.getDeviceList();
    }

    void test(UsbDevice device) {
//        mManager.openDevice(device)
    }
}

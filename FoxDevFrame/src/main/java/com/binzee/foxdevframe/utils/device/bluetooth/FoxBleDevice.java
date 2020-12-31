package com.binzee.foxdevframe.utils.device.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.binzee.foxdevframe.FoxCore;

import java.util.UUID;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;

/**
 * 低功耗蓝牙设备
 *
 * @author 狐彻
 * 2020/11/10 8:49
 */
class FoxBleDevice implements BleDevice {
    private static final String TAG = "BleDevice";
    /* CCCD 的UUID */
    private final UUID ID_CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private final BluetoothDevice mDevice;   //设备实例

    private final int TRANSPORT = BluetoothDevice.TRANSPORT_AUTO;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private final int PHY = BluetoothDevice.PHY_LE_1M_MASK;
    private boolean mAutoConnect = false;    //自动连接
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Handler mConnectHandler = null; //连接线程

    private BluetoothGatt mGatt;    //Gatt客户端
    private boolean isConnected = false;   //是否连接中
    private ConnectCallback mCallback;  //连接回调

    FoxBleDevice(@NonNull BluetoothDevice device) {
        mDevice = device;
    }

    @Override
    public BleDevice setAutoConnect(boolean autoConnect) {
        mAutoConnect = autoConnect;
        return this;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public BleDevice setConnectHandler(Handler handler) {
        mConnectHandler = handler;
        return this;
    }

    @Override
    public void connect(ConnectCallback connectCallback) {
        //回调
        mCallback = connectCallback;
        BluetoothGattCallback callback = new GattCallback();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mDevice.connectGatt(FoxCore.getApplication()
                    , mAutoConnect, callback, TRANSPORT, PHY, mConnectHandler);
        } else mDevice.connectGatt(FoxCore.getApplication()
                , mAutoConnect, callback, TRANSPORT);
    }

    @Override
    public void disconnect() {
        if (mGatt != null) mGatt.disconnect();
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void recycle() {
        disconnect();
        mGatt.close();
    }

    @Override
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        mGatt.readCharacteristic(characteristic);
    }

    @Override
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        mGatt.writeCharacteristic(characteristic);
    }

    @Override
    public void readDescriptor(BluetoothGattDescriptor descriptor) {
        mGatt.readDescriptor(descriptor);
    }

    @Override
    public void writeDescriptor(BluetoothGattDescriptor descriptor) {
        mGatt.writeDescriptor(descriptor);
    }

    @Override
    public void subscribe(BluetoothGattCharacteristic characteristic, boolean enable) {
        mGatt.setCharacteristicNotification(characteristic, enable);
        /* 获取CCCD */
        BluetoothGattDescriptor cccd = characteristic.getDescriptor(ID_CCCD);
        cccd.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        mGatt.writeDescriptor(cccd);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////

    /**
     * GATT回调
     *
     * @author 狐彻 2020/11/10 8:54
     */
    private class GattCallback extends BluetoothGattCallback {

        /**
         * 连接状态变化回调
         *
         * @author 狐彻 2020/11/10 10:18
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (mGatt == null) mGatt = gatt;
            if (status == GATT_SUCCESS && newState == STATE_DISCONNECTED) {
                isConnected = false;
                mCallback.onDisconnected();
            }
            if (status == GATT_SUCCESS && newState == STATE_CONNECTED) {
                isConnected = true;
                mCallback.onConnected();
                gatt.discoverServices();
            }
            if (status == 133) mCallback.onError("连接设备数量超出限制，错误133", null);
        }

        /**
         * 服务发现回调
         *
         * @author 狐彻 2020/11/10 10:18
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status != GATT_SUCCESS) return;
            mCallback.onServicesDiscovered(gatt, gatt.getServices());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            mCallback.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            mCallback.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            mCallback.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            mCallback.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            mCallback.onCharacteristicChanged(gatt, characteristic);
        }
    }
}

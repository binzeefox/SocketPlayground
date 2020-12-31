package com.binzee.foxdevframe.utils.device.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.List;

/**
 * 低功耗蓝牙接口
 *
 * @author 狐彻
 * 2020/11/10 8:41
 */
public interface BleDevice {

    /**
     * 设置是否自动连接
     *
     * @author 狐彻 2020/11/10 10:07
     */
    BleDevice setAutoConnect(boolean autoConnect);

    /**
     * 设置连接回调
     *
     * @author 狐彻 2020/11/10 10:07
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    BleDevice setConnectHandler(Handler handler);

    /**
     * 开始连接
     *
     * @param connectCallback 连接回调
     * @author 狐彻 2020/11/10 10:07
     */
    void connect(ConnectCallback connectCallback);

    /**
     * 断开连接
     *
     * @author 狐彻 2020/11/10 10:07
     */
    void disconnect();

    /**
     * 是否连接
     *
     * @author 狐彻 2020/11/10 10:08
     */
    boolean isConnected();

    /**
     * 回收
     *
     * @author 狐彻 2020/11/10 10:08
     */
    void recycle();

    /**
     * 读特征
     *
     * @author 狐彻 2020/11/10 10:25
     */
    void readCharacteristic(BluetoothGattCharacteristic characteristic);

    /**
     * 写特征
     *
     * @author 狐彻 2020/11/10 10:25
     */
    void writeCharacteristic(BluetoothGattCharacteristic characteristic);

    /**
     * 读描述
     *
     * @author 狐彻 2020/11/10 10:43
     */
    void readDescriptor(BluetoothGattDescriptor descriptor);

    /**
     * 写描述
     *
     * @author 狐彻 2020/11/10 10:43
     */
    void writeDescriptor(BluetoothGattDescriptor descriptor);

    /**
     * 订阅
     *
     * @param characteristic 特征
     * @param enable         是否开启
     * @author 狐彻 2020/11/10 10:08
     */
    void subscribe(BluetoothGattCharacteristic characteristic, boolean enable);

    ///////////////////////////////////////////////////////////////////////////
    // 回调
    ///////////////////////////////////////////////////////////////////////////

    abstract class ConnectCallback {
        /**
         * 连接成功
         *
         * @author 狐彻 2020/11/10 10:33
         */
        public abstract void onConnected();

        /**
         * 断连成功
         *
         * @author 狐彻 2020/11/10 10:33
         */
        public abstract void onDisconnected();

        /**
         * 错误回调
         *
         * @author 狐彻 2020/11/10 10:33
         */
        public abstract void onError(String message, @Nullable Throwable throwable);

        /**
         * 发现服务
         *
         * @param gatt        客户端
         * @param serviceList 服务列表
         * @author 狐彻 2020/11/10 10:33
         */
        public abstract void onServicesDiscovered(BluetoothGatt gatt, List<BluetoothGattService> serviceList);

        /**
         * 读取回调
         *
         * @param gatt           客户端
         * @param characteristic 特征
         * @param status         结果状态
         * @author 狐彻 2020/11/10 10:33
         */
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
        }

        /**
         * 写入回调
         *
         * @param gatt           客户端
         * @param characteristic 特征，这里为当前特征。
         *                       应在回调总对比该特征内容是否符合期望值。
         *                       若不同，应重发或终止写入
         * @param status         结果状态
         * @author 狐彻 2020/11/10 10:35
         */
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                          int status) {
        }

        /**
         * 读取回调
         *
         * @param gatt       客户端
         * @param descriptor 描述
         * @param status     结果状态
         * @author 狐彻 2020/11/10 10:33
         */
        public void onDescriptorRead(BluetoothGatt gatt,
                                     BluetoothGattDescriptor descriptor,
                                     int status) {
        }

        /**
         * 读取回调
         *
         * @param gatt       客户端
         * @param descriptor 描述
         * @param status     结果状态
         * @author 狐彻 2020/11/10 10:33
         */
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor,
                                      int status) {
        }

        /**
         * 接受通知
         *
         * @author 狐彻 2020/11/10 10:47
         */
        public void onCharacteristicChanged(BluetoothGatt gatt
                , BluetoothGattCharacteristic characteristic) {
        }
    }
}

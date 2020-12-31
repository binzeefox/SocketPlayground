package com.binzee.example.socketplayground.iot;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.binzee.foxdevframe.utils.socket.ServerHelper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tong.xw
 * 2020/12/29 14:42
 */
public class IOTGateway implements ServerHelper.ServerCallback, ServerHelper.PulseInterceptor {
    public static final String METHOD_PULSE = "PULSE";
    public static final String METHOD_WRONG_FORMAT = "WRONG_FORMAT";
    public static final String METHOD_TO_DEVICE_NOT_FIND = "TO_DEVICE_NOT_FIND";
    public static final String METHOD_FROM_DEVICE_NOT_FIND = "FROM_TO_DEVICE_NOT_FIND";

    public static final String METHOD_REGISTER = "REGISTER";

    private static final String TAG = "IOTGateway";
    private static final String DEVICE_ID_GATEWAY = "GATEWAY";
    private final ServerHelper helper;
    private final Map<String, String> mDeviceMap = new ConcurrentHashMap<>();   // <address, deviceId>

    private final Gson gson = new Gson();

    private IOTGateway() throws IOException {
        helper = ServerHelper.create(65001);
    }

    public void start() {
        helper.setCallback(this);
        helper.setPulseInterceptor(this);
        helper.open();
    }


    @Override
    public void onAccept(Socket client) {
//        mDeviceMap.put(client.getRemoteSocketAddress().toString(), UUID.randomUUID().toString());
    }

    @Override
    public void onReceive(Socket client, String text) {
        try {
            Operation o = gson.fromJson(text, Operation.class);
            if (METHOD_REGISTER.equals(o.getMethodName())) {
                registerDevice(client, o);
            } else redirect(client, o);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "onReceive: ", e);
            helper.sendTo(client, createWrongFormatMessage(client, text));
        }
    }

    @Override
    public void onLost(Socket client) {

    }

    @Override
    public void onError(int errorCode, Throwable t) {

    }

    @Override
    public String onIntercept(Socket client) {
        String address = client.getLocalSocketAddress().toString();
        String deviceId = mDeviceMap.get(address);
        if (deviceId == null) deviceId = "unknown";
        Operation o = new Operation();
        o.setMethodName(METHOD_PULSE);
        o.setFromDeviceId(DEVICE_ID_GATEWAY);
        o.setToDeviceId(deviceId);
        o.setData(new Date().toString());
        return gson.toJson(o);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 通过设备号获取地址
     *
     * @return 若未绑定则未空
     */
    private String getAddressFromDeviceId(@NonNull String deviceId) {
        for (String address: mDeviceMap.keySet()) {
            String _deviceId = mDeviceMap.get(address);
            if (TextUtils.equals(_deviceId, deviceId))
                return address;
        }
        return null;
    }

    /**
     * 注册Device
     */
    private void registerDevice(Socket client, Operation o) {
        String deviceId = o.getFromDeviceId();
        mDeviceMap.put(client.getRemoteSocketAddress().toString(), deviceId);
    }

    /**
     * 重定向分发方法
     */
    private void redirect(Socket client, Operation o) {
        if (TextUtils.isEmpty(o.getFromDeviceId()) || !mDeviceMap.containsValue(o.getFromDeviceId())) {
            helper.sendTo(client, createFromDeviceNotFindMessage(client, o.getRequestCode()));
            return;
        }
        if (TextUtils.isEmpty(o.getToDeviceId()) || !mDeviceMap.containsValue(o.getToDeviceId())) {
            helper.sendTo(client, createToDeviceNotFindMessage(client, o.getRequestCode()));
            return;
        }
        helper.sendTo(getAddressFromDeviceId(o.getToDeviceId()), gson.toJson(o));
    }

    /**
     * 传入数据格式有误
     */
    private String createWrongFormatMessage(Socket client, String text) {
        Operation o = new Operation();
        o.setToDeviceId(mDeviceMap.getOrDefault(client.getRemoteSocketAddress().toString(), "unknown"));
        o.setFromDeviceId(DEVICE_ID_GATEWAY);
        o.setMethodName(METHOD_WRONG_FORMAT);
        o.setData(text);
        return gson.toJson(o);
    }

    /**
     * 目标设备号未找到
     */
    private String createToDeviceNotFindMessage(Socket client, String requestCode) {
        Operation o = new Operation();
        o.setToDeviceId(mDeviceMap.getOrDefault(client.getRemoteSocketAddress().toString(), "unknown"));
        o.setFromDeviceId(DEVICE_ID_GATEWAY);
        o.setMethodName(METHOD_TO_DEVICE_NOT_FIND);
        o.setRequestCode(requestCode);
        return gson.toJson(o);
    }

    /**
     * 目标设备号未找到
     */
    private String createFromDeviceNotFindMessage(Socket client, String requestCode) {
        Operation o = new Operation();
        o.setToDeviceId(mDeviceMap.getOrDefault(client.getRemoteSocketAddress().toString(), "unknown"));
        o.setFromDeviceId(DEVICE_ID_GATEWAY);
        o.setMethodName(METHOD_FROM_DEVICE_NOT_FIND);
        o.setRequestCode(requestCode);
        return gson.toJson(o);
    }
}

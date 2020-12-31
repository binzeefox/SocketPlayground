package com.binzee.foxdevframe.utils.device;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

import com.binzee.foxdevframe.FoxCore;
import com.binzee.foxdevframe.utils.LogUtil;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 手机状态工具类
 *
 * @author 狐彻
 * 2020/10/27 9:50
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class DeviceStatusUtil {
    private static final String TAG = "DeviceStatusUtil";

    /**
     * 网络状态枚举
     *
     * @author 狐彻 2020/10/27 9:54
     */
    public enum NetworkType {
        NONE(0),    //无网络
        DATA(1),    //数据网络
//        NR5G(2),    //5G
        WIFI(3);    //wifi网络

        // 值，int类型，方便对比
        // @author 狐彻 2020/10/27 9:53
        private final int level;

        NetworkType(int value) {
            this.level = value;
        }

        public int getLevel() {
            return level;
        }
    }

    /**
     * 静态获取
     *
     * @author 狐彻 2020/10/27 9:55
     */
    public static DeviceStatusUtil get() {
        return new DeviceStatusUtil();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 网络相关
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 当前网络是否是流量计费
     *
     * @author 狐彻 2020/11/17 13:27
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public boolean isNetworkNotMetered() {
        if (!isNetworkAvailable() || !isNetWorkConnected()) return true;
        ConnectivityManager manager = getConnectivityManager();
        if (manager == null) return true;
        Network network = manager.getActiveNetwork();
        if (network == null) return true;
        NetworkCapabilities capabilities = manager.getNetworkCapabilities(network);
        boolean netNotMetered = capabilities
                .hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
        boolean netTempNotMetered = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            netTempNotMetered = capabilities
                    .hasCapability(NetworkCapabilities.NET_CAPABILITY_TEMPORARILY_NOT_METERED);
        }
        return netNotMetered || netTempNotMetered;
    }

    /**
     * 判断手机是否连接到5G
     *
     * @author 狐彻 2020/11/17 13:35
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    public boolean is5GConnected() {
        TelephonyManager manager = (TelephonyManager) FoxCore.getApplication()
                .getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = manager.getNetworkType();
        return networkType == TelephonyManager.NETWORK_TYPE_NR;
    }

    /**
     * 获取网络状态
     *
     * @author 狐彻 2020/10/27 9:57
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public boolean isNetworkAvailable() {
        ConnectivityManager manager = getConnectivityManager();
        if (manager == null) return false;

        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected())
            return info.isAvailable();
        return false;
    }

    /**
     * 获取网络是否已经连接
     *
     * @return 是否链接
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public boolean isNetWorkConnected() {
        if (!isNetworkAvailable()) return false;
        ConnectivityManager manager = getConnectivityManager();
        if (manager != null) {
            NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
            if (activeNetworkInfo == null) return false;
            return activeNetworkInfo.isConnected();
        } else return false;
    }

    /**
     * 获取网络状态
     *
     * @author binze 2020/8/25 11:42
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public NetworkType getNetworkState() {
        if (!isNetworkAvailable() || !isNetWorkConnected()) return NetworkType.NONE;
        ConnectivityManager manager = getConnectivityManager();
        if (manager == null) return NetworkType.NONE;
        Network network = manager.getActiveNetwork();
        if (network == null) return NetworkType.NONE;
        NetworkCapabilities capabilities = manager.getNetworkCapabilities(network);
        if (capabilities == null) return NetworkType.NONE;
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
            return NetworkType.DATA;   //移动网络
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
            return NetworkType.WIFI;   //Wifi
        return NetworkType.NONE;
    }

    /**
     * 注册网络状态监听器
     *
     * @author binze 2019/11/5 12:02
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public void registerNetworkListener(ConnectivityManager.NetworkCallback callback) {
        ConnectivityManager manager = getConnectivityManager();
        if (manager != null) {
            manager.registerDefaultNetworkCallback(callback);
        } else LogUtil.e(TAG, "registerNetworkListener: 注册网络状态失败，没有网络连接");
    }

    /**
     * 注销网络状态监听器
     *
     * @author binze 2019/11/5 12:02
     */
    public void unregisterNetworkListener(ConnectivityManager.NetworkCallback callback) {
        ConnectivityManager manager = getConnectivityManager();
        if (manager != null) {
            manager.unregisterNetworkCallback(callback);
        } else LogUtil.e(TAG, "unregisterNetworkListener: 注销网络状态失败，没有网络连接");
    }

    /**
     * 获取IP
     *
     * @author 狐彻 2020/10/27 10:06
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public String getIPAddress() {
        NetworkType netState = getNetworkState();
        if (netState == NetworkType.NONE) return null;
        if (netState == NetworkType.DATA) return getDataIPAddress();
        if (netState == NetworkType.WIFI) return getWifiIPAddress();
        return null;
    }

    /**
     * 获取内网IP地址
     */
    public String getLocalIPAddress() throws SocketException {
        for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
            NetworkInterface intf = en.nextElement();
            for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
                InetAddress inetAddress = enumIpAddr.nextElement();
                if(!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)){
                    return inetAddress.getHostAddress().toString();
                }
            }
        }
        return "";
    }

    ///////////////////////////////////////////////////////////////////////////
    // 其它
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 判断GPS是否开启
     *
     * @author binze 2019/11/5 12:02
     */
    public boolean isGPSEnabled() {
        LocationManager manager = (LocationManager) FoxCore.getApplication()
                .getSystemService(Context.LOCATION_SERVICE);
        return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 获取可用内存kb值
     *
     * @return 单位kb
     */
    public long getFreeMemKB() {
        ActivityManager manager = (ActivityManager) FoxCore.getApplication()
                .getSystemService(Activity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        if (manager == null) return -1;
        manager.getMemoryInfo(info);
        return info.availMem / 1024;
    }

    /**
     * 显示软键盘
     *
     * @author binze 2019/12/26 16:27
     */
    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) FoxCore.getApplication()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 判断手机是否root
     *
     * @author 狐彻 2020/11/17 14:22
     */
    public boolean isPhoneRooted() {
        try {
            String result = ADBTools.execute("su");
            if (result.toLowerCase().contains("is already running as root"))
                return true;
        } catch (IOException e) {
            LogUtil.v(TAG, "isPhoneRooted: ", e);
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 获取连接管理者
     *
     * @author 狐彻 2020/10/27 9:59
     */
    private ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) FoxCore.getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * 获取数据移动的IP地址
     *
     * @author 狐彻 2020/09/12 10:50
     */
    private String getDataIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            LogUtil.e(TAG, "getDataIPAddress: ", e);
        }
        return null;
    }

    /**
     * 获取wifi的IP地址
     *
     * @author 狐彻 2020/09/12 10:50
     */
    private String getWifiIPAddress() {
        WifiManager manager = (WifiManager) FoxCore.getApplication()
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        int ip = info.getIpAddress();
        return intToIp(ip);
    }

    /**
     * ip转字符
     *
     * @author 狐彻 2020/09/12 10:53
     */
    private String intToIp(int intIp) {
        return (intIp >> 24) + "." +
                ((intIp & 0x00FFFFFF) >> 16) + "." +
                ((intIp & 0x0000FFFF) >> 8) + "." +
                (intIp & 0x000000FF);
    }
}

package com.binzee.example.httptest;

import android.util.Log;

import com.binzee.foxdevframe.FoxCore;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.util.concurrent.TimeUnit;

/**
 * 测试服务器
 *
 * @author tong.xw
 * 2020/12/30 15:13
 */
public class FoxServer implements Server.ServerListener {
    private static final String TAG = "FoxServer";
    private final Server server;

    private FoxServer() {
        server = AndServer.webServer(FoxCore.getApplication())
                .port(8080)
                .timeout(10, TimeUnit.SECONDS)
                .listener(this)
                .build();
    }

    public static FoxServer get() {
        return Holder.sInstance;
    }

    public void start() {
        server.startup();
    }

    public void shutdown() {
        server.shutdown();
    }

    public boolean isRunning() {
        return server.isRunning();
    }

    @Override
    public void onStarted() {
        Log.d(TAG, "onStarted: ");
    }

    @Override
    public void onStopped() {
        Log.d(TAG, "onStopped: ");
    }

    @Override
    public void onException(Exception e) {
        Log.e(TAG, "onException: ", e);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////

    private static class Holder {
        private static final FoxServer sInstance = new FoxServer();
    }
}

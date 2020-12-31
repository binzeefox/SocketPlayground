package com.binzee.foxdevframe.utils.socket;

import android.Manifest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 服务器帮助类
 *
 * @author tong.xw
 * 2020/12/28 16:39
 */
public class ServerHelper {
    public static final int ERROR_CODE_PULSE_FAILED = 0X01;
    public static final int ERROR_CODE_CLIENT_FAILED = 0X02;
    public static final int ERROR_CODE_SERVER_FAILED = 0X03;

    public static final String PULSE_PRE = "pulse//";   //心跳包头

    private static final String TAG = "ServerHelper";
    private final ServerSocket mServer;
    private final Object mInstanceLock = new Object();  //实例锁
    private final ExecutorService mClientListenExecutor = new ThreadPoolExecutor(
            0, 50, 20
            , TimeUnit.SECONDS, new LinkedBlockingQueue<>()
    );  // 最多监听50个客户端
    private final ExecutorService mClientSendExecutor = new ThreadPoolExecutor(
            0, 16, 20
            , TimeUnit.SECONDS, new LinkedBlockingQueue<>()
    );  // 发送数据，最高16线程
    private final ExecutorService mWorkExecutor = Executors.newFixedThreadPool(3);
    private final Map<String, Socket> mClientMap = new ConcurrentHashMap<>();
    private ServerCallback mCallback = null;
    @NonNull
    private volatile PulseInterceptor mPulseInterceptor = client -> PULSE_PRE + new Date();

    @RequiresPermission(Manifest.permission.INTERNET)
    private ServerHelper(ServerSocket serverSocket) {
        mServer = serverSocket;
    }

    /**
     * 静态构造
     *
     * @param port 端口号 1024~65535
     */
    @RequiresPermission(Manifest.permission.INTERNET)
    public static ServerHelper create(int port) throws IOException {
        return new ServerHelper(new ServerSocket(port));
    }

    /**
     * 设置回调
     */
    public void setCallback(ServerCallback callback) {
        mCallback = callback;
    }

    /**
     * 设置心跳包拦截器
     */
    public void setPulseInterceptor(@NonNull PulseInterceptor interceptor) {
        this.mPulseInterceptor = interceptor;
    }

    /**
     * 开启服务器
     */
    public void open() {
        if (mServer.isClosed()) return;
        mWorkExecutor.execute(() -> {
            while (!mServer.isClosed()) {
                try {
                    Socket client = mServer.accept();
                    String address = client.getRemoteSocketAddress().toString();
                    if (mClientMap.containsKey(address) && !mClientMap.get(address).isClosed())
                        continue;
                    mClientMap.put(address, client);
                    connectClient(client, address);
                } catch (IOException e) {
                    onError(ERROR_CODE_SERVER_FAILED, e);
                    Log.e(TAG, "start: ", e);
                }
            }
        });
        //启动心跳包
        mWorkExecutor.execute(() -> {
            while (!mServer.isClosed()) {
                try {
                    pulse();
                    Thread.sleep(5000); //每5秒一个心跳
                } catch (InterruptedException e) {
                    Log.e(TAG, "open: ", e);
                    if (mCallback != null) mCallback.onError(ERROR_CODE_PULSE_FAILED, e);
                    break;
                }
            }
        });
    }

    /**
     * 群发
     */
    public void broadcast(@NonNull String text) {
        for (Socket client : mClientMap.values()) {
            sendTo(client, text);
        }
    }

    /**
     * 发送
     */
    public void sendTo(@NonNull String address, @NonNull String text) {
        Socket client = mClientMap.get(address);
        if (client == null) return;
        sendTo(client, text);
    }

    /**
     * 发送
     */
    public void sendTo(@NonNull Socket client, @NonNull String text) {
        synchronized (mInstanceLock) {
            if (client.isClosed()) {
                mClientMap.remove(client.getRemoteSocketAddress().toString());
                return;
            }
            mClientSendExecutor.execute(new Runnable() {
                final WeakReference<Socket> clientReference = new WeakReference<>(client);

                @Override
                public void run() {
                    try {
                        OutputStream os = clientReference.get().getOutputStream();
                        os.write((text + "\n").getBytes(StandardCharsets.UTF_8));
                        os.flush();
                    } catch (Exception e) {
                        Log.e(TAG, "send: address = " + clientReference.get().getRemoteSocketAddress(), e);
                        lostClient(client);
                    }
                }
            });
        }
    }

    public ServerSocket getHost() {
        return mServer;
    }

    /**
     * 关闭服务器
     */
    public void close() throws IOException {
        synchronized (mInstanceLock) {
            mServer.close();
            mClientListenExecutor.shutdownNow();
            mWorkExecutor.shutdownNow();
            mClientMap.clear();
        }
    }

    /**
     * 是否已关闭服务器
     */
    public boolean isClosed() {
        synchronized (mInstanceLock) {
            return mServer.isClosed();
        }
    }

    /**
     * 信息获取回调
     */
    public void onMessageReceive(Socket socket, String text) {
        synchronized (mInstanceLock) {
            Log.d(TAG, "onMessageReceive: ip = " + socket.getRemoteSocketAddress().toString());
            Log.d(TAG, "onMessageReceive: text = " + text);
            if (mCallback != null) mCallback.onReceive(socket, text);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 发送心跳
     */
    private void pulse() {
        for (Socket client: mClientMap.values()) {
            String pulse = mPulseInterceptor.onIntercept(client);
            sendTo(client, pulse);
            Log.d(TAG, "pulse sent: " + pulse + " target = " + client.getRemoteSocketAddress());
        }
    }

    /**
     * 链接客户端
     */
    private void connectClient(Socket client, String address) {
        synchronized (mInstanceLock) {
            if (mCallback != null) mCallback.onAccept(client);
            mClientListenExecutor.execute(() -> {
                try {
                    InputStream is = client.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);

                    while (!mServer.isClosed() && !client.isClosed()) {
                        String text = br.readLine();
                        if (text == null) break;
                        onMessageReceive(client, text);
                    }
                } catch (Exception e) {
                    onError(ERROR_CODE_CLIENT_FAILED, e);
                    Log.e(TAG, "listen client ip: " + address, e);
                }
                lostClient(client);
            });
        }
    }

    private void onError(int errorCode, Throwable t) {
        if (mCallback != null) mCallback.onError(errorCode, t);
    }

    /**
     * 失去连接
     */
    private void lostClient(Socket client) {
        try {
            client.close();
            String address = client.getRemoteSocketAddress().toString();
            mClientMap.remove(address);
            if (mCallback != null) mCallback.onLost(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////

    public interface PulseInterceptor {
        String onIntercept(Socket client);
    }

    public interface ServerCallback {

        void onAccept(Socket client);

        void onReceive(Socket client, String text);

        void onLost(Socket client);

        void onError(int errorCode, Throwable t);
    }
}

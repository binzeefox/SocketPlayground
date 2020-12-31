package com.binzee.foxdevframe.utils.socket;

import android.Manifest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Socket帮助类
 *
 * @author tong.xw
 * 2020/12/28 14:35
 */
public class SocketHelper {
    private static final String TAG = "SocketHelper";
    private final Object mInstanceLock = new Object();  //实例锁
    private final ExecutorService mLoopExecutor = Executors.newSingleThreadExecutor();  //监听线程
    private final ExecutorService mSendExecutor = Executors.newSingleThreadExecutor();  //发送线程

    @NonNull
    private final Socket mSocket;
    private OutputStream mSendStream;

    @RequiresPermission(Manifest.permission.INTERNET)
    private SocketHelper(@NonNull Socket socket) {
        mSocket = socket;
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    public static SocketHelper create(@NonNull Socket socket) {
        return new SocketHelper(socket);
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    public static SocketHelper create(@NonNull String address, int port) throws IOException {
        return create(new Socket(address, port));
    }

    /**
     * 是否连接
     */
    public boolean isConnected() {
        synchronized (mInstanceLock) {
            return mSocket.isConnected();
        }
    }

    /**
     * 监听
     */
    public void listen(@NonNull ListenCallback callback) {
        mLoopExecutor.execute(new Runnable() {
            final WeakReference<ListenCallback> callbackReference = new WeakReference<>(callback);

            @Override
            public void run() {
                try {
                    while (mSocket.isConnected()) {
                        InputStream is = mSocket.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr);
                        String text = br.readLine();
                        if (callbackReference.get() != null)
                            callbackReference.get().onListen(mSocket, text);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "listen: ", e);
                    if (callbackReference.get() == null) return;
                    ListenCallback _callback = callbackReference.get();
                    _callback.onError(e);
                    _callback.onDisconnect(mSocket);
                }
            }
        });
    }

    /**
     * 发送信息
     */
    public void send(@NonNull String text, SendCallback callback) {
        mSendExecutor.execute(new Runnable() {
            final WeakReference<SendCallback> callbackReference = new WeakReference<>(callback);

            @Override
            public void run() {
                synchronized (mSocket) {
                    if (mSocket.isClosed()) return;
                    try {
                        mSendStream = mSocket.getOutputStream();
                        mSendStream.write((text + "\n").getBytes(StandardCharsets.UTF_8));
                        mSendStream.flush();
                        if (callbackReference.get() != null)
                            callbackReference.get().onResult(true, null);
                    } catch (Exception e) {
                        Log.e(TAG, "send: ", e);
                        if (callbackReference.get() != null)
                            callbackReference.get().onResult(false, e);
                    }
                }
            }
        });
    }

    /**
     * 关闭
     */
    public void close() {
        try {
            mLoopExecutor.shutdownNow();
            mSendExecutor.shutdown();
            mSendStream.close();
            if (!mSocket.isClosed()) {
                mSocket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "close: ", e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////

    public interface SendCallback {
        void onResult(boolean success, @Nullable Throwable e);
    }

    public interface ListenCallback {

        void onListen(Socket socket, String text);

        void onDisconnect(Socket socket);

        void onError(Throwable e);
    }
}

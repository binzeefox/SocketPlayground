package com.binzee.example.socketplayground;

import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.binzee.foxdevframe.ui.FoxActivity;
import com.binzee.foxdevframe.ui.tools.popup.ToastUtil;
import com.binzee.foxdevframe.utils.LogUtil;
import com.binzee.foxdevframe.utils.device.DeviceStatusUtil;
import com.binzee.foxdevframe.utils.socket.ServerHelper;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author tong.xw
 * 2020/12/29 10:50
 */
public class MainActivity extends FoxActivity implements ServerHelper.ServerCallback {
    private static final String TAG = "MainActivity";
    private EditText etSend;
    private boolean connected = false;
    private ServerHelper helper;
    private final ExecutorService mWorkExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate() {
        etSend = findViewById(R.id.etSend);
        findViewById(R.id.btnAction).setOnClickListener(v -> {
            mWorkExecutor.execute(() -> {
                if (!connected) {
                    getHelper().open();
                    connected = true;
                    return;
                }
                String text = etSend.getText().toString();
                getHelper().broadcast(text);
                runOnUiThread(() -> etSend.setText(null));
            });
        });
    }

    private ServerHelper getHelper() {
        if (helper != null) return helper;
        try {
            helper = ServerHelper.create(65000);
            helper.setCallback(this);
            Log.d(TAG, "getHelper: " + helper.getHost().getLocalSocketAddress());
            Log.d(TAG, "getHelper: " + DeviceStatusUtil.get().getIPAddress());
            Log.d(TAG, "getHelper: " + DeviceStatusUtil.get().getLocalIPAddress());
            runOnUiThread(() -> {
                try {
                    ToastUtil.toast(DeviceStatusUtil.get().getLocalIPAddress(), Toast.LENGTH_SHORT)
                            .showOnLastHide();
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            });
            return helper;
        } catch (IOException e) {
            LogUtil.e(TAG, "getHelper: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onAccept(Socket client) {
        Log.d(TAG, "onAccept: address = " + client.getRemoteSocketAddress());
        runOnUiThread(() -> {
            ToastUtil.toast("onAccept: address = " + client.getRemoteSocketAddress(), Toast.LENGTH_SHORT).showOnLastHide();
        });
    }

    @Override
    public void onReceive(Socket client, String text) {
        Log.d(TAG, "onReceive: " + client.getRemoteSocketAddress());
        Log.d(TAG, "onReceive: " + text);
        runOnUiThread(() -> {
            ToastUtil.toast(text, Toast.LENGTH_SHORT).showOnLastHide();
        });
    }

    @Override
    public void onLost(Socket client) {
        Log.d(TAG, "onLost: " + client.getRemoteSocketAddress());
    }

    @Override
    public void onError(int errorCode, Throwable t) {
        Log.e(TAG, "onError: ", t);
    }
}

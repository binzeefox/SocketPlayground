package com.binzee.example.socketplayground;

import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.binzee.foxdevframe.ui.FoxActivity;
import com.binzee.foxdevframe.ui.tools.popup.ToastUtil;
import com.binzee.foxdevframe.utils.socket.ServerHelper;
import com.binzee.foxdevframe.utils.socket.SocketHelper;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author tong.xw
 * 2020/12/29 11:20
 */
public class ClientActivity extends FoxActivity implements SocketHelper.ListenCallback {
    private static final String TAG = "ClientActivity";
    private EditText etSend;
    private boolean connected = false;
    private SocketHelper helper;
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
                    getHelper().listen(this);
                    connected = true;
                    return;
                }
                String text = etSend.getText().toString();
                if (TextUtils.isEmpty(text)) return;
                helper.send(text, null);
                runOnUiThread(() -> etSend.setText(null));
            });
        });
    }

    public SocketHelper getHelper() {
        if (helper != null) return helper;
        try {
            helper = SocketHelper.create("192.168.43.1", 65000);
            return helper;
        } catch (IOException e) {
            Log.e(TAG, "getHelper: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onListen(Socket socket, String text) {
        runOnUiThread(() -> {
            if (TextUtils.isEmpty(text)) return;
            if (text.startsWith(ServerHelper.PULSE_PRE)) {
                Log.d(TAG, "onListen: 心跳包 " + text);
            } else ToastUtil.toast(text, Toast.LENGTH_SHORT).showOnLastHide();
        });
    }

    @Override
    public void onDisconnect(Socket socket) {
        runOnUiThread(() -> ToastUtil.toast("断开连接", Toast.LENGTH_SHORT));
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onError: ", e);
    }
}

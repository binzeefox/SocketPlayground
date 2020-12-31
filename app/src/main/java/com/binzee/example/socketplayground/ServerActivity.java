package com.binzee.example.socketplayground;

import android.util.Log;
import android.widget.EditText;

import com.binzee.example.httptest.FoxServer;
import com.binzee.foxdevframe.ui.FoxActivity;
import com.binzee.foxdevframe.utils.device.DeviceStatusUtil;

import java.net.SocketException;

/**
 * @author tong.xw
 * 2020/12/30 15:40
 */
public class ServerActivity extends FoxActivity {
    private static final String TAG = "ServerActivity";
    private EditText etSend;

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        etSend = findViewById(R.id.etSend);
        findViewById(R.id.btnAction).setOnClickListener(v -> {
            if (FoxServer.get().isRunning()) {
                try {
                    Log.d(TAG, "getHelper: " + DeviceStatusUtil.get().getLocalIPAddress());
                } catch (SocketException e) {
                    Log.e(TAG, "onCreate: ", e);
                }
            } else {
                FoxServer.get().start();
            }
        });
    }
}

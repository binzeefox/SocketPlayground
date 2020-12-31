package com.binzee.foxdevframe.utils.http;

import com.binzee.foxdevframe.utils.LogUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * GET请求
 *
 * @author 狐彻
 * 2020/11/09 9:19
 */
class GetRequest implements ClientInterface {
    private static final String TAG = "GetRequest";
    private final HttpURLConnection connection;

    /**
     * 构造器
     *
     * @author 狐彻 2020/11/09 9:19
     */
    GetRequest(URL url) throws IOException {
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
    }

    @Override
    public void request(OnCallListener listener) {
        try {
            listener.onStart(connection);
            connection.connect();
            int responseCode = connection.getResponseCode();
            listener.onSuccess(connection, responseCode, connection.getInputStream());
        } catch (Exception e) {
            LogUtil.e(TAG, "request: 请求失败", e);
            listener.onError(e);
            connection.disconnect();
        }
    }

    @Override
    public ClientInterface setConnectTimeout(int miles) {
        connection.setConnectTimeout(miles);
        return this;
    }

    @Override
    public ClientInterface setReadTimeout(int miles) {
        connection.setReadTimeout(miles);
        return this;
    }
}
package com.binzee.foxdevframe.utils.http;

import com.binzee.foxdevframe.utils.LogUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

/**
 * Post请求
 * <p>
 * 默认允许写出、读入，禁用缓存。可在回调中的onStart进行最后调整
 *
 * @author 狐彻
 * 2020/11/09 10:36
 */
class PostRequest implements ClientInterface {
    private static final String TAG = "PostRequest";
    private final HttpURLConnection connection;
    private final String body;  //请求体
    private final String charsetName;

    PostRequest(URL url, String body) throws IOException {
        this(url, body, null);
    }

    PostRequest(URL url, String body, String charsetName) throws IOException {
        this.body = body;
        if (charsetName == null) charsetName = "utf-8";
        this.charsetName = charsetName;

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
    }

    @Override
    public void request(OnCallListener listener) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), charsetName));) {
            listener.onStart(connection);
            connection.connect();
            writer.write(body);
            writer.close();

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

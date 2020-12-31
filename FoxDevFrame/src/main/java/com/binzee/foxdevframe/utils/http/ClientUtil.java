package com.binzee.foxdevframe.utils.http;

import com.binzee.foxdevframe.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;


/**
 * 网络工具类
 *
 * @author 狐彻
 * 2020/10/27 16:45
 */
public class ClientUtil {
    private static final String TAG = "ClientUtil";
    private volatile String baseUrl = "";   //基本路径

    private ClientUtil() {
    }

    public static ClientUtil get() {
        return ClientUtilHolder.instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 工具方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 从输入流中获取String
     *
     * @author 狐彻 2020/11/09 9:50
     */
    public static String getStringFromInputStream(InputStream is, String charsetName) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, charsetName));) {
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString().trim();
        } catch (IOException e) {
            LogUtil.e(TAG, "getStringFromInputStream: ", e);
            return null;
        }
    }

    /**
     * 从输入流获取String，默认utf-8
     *
     * @author 狐彻 2020/11/09 9:56
     */
    public static String getStringFromInputStream(InputStream is) {
        return getStringFromInputStream(is, "utf-8");
    }

    ///////////////////////////////////////////////////////////////////////////
    // 业务方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 设置基本路径
     *
     * @author 狐彻 2020/10/27 17:13
     */
    public void setBaseUrl(String baseUrl) {
        synchronized (ClientUtil.class) {
            if (!baseUrl.endsWith("/")) baseUrl += "/";
            this.baseUrl = baseUrl;
        }
    }

    /**
     * GET请求
     *
     * @author 狐彻 2020/11/09 9:15
     */
    public ClientInterface GET(String urlString) throws IOException {
        URL url = convertUrl(urlString);
        return new GetRequest(url);
    }

    /**
     * GET请求
     *
     * @author 狐彻 2020/11/09 9:44
     */
    public ClientInterface GET(String urlString, Map<String, String> params) throws IOException {
        URL url = convertGETUrl(urlString, params);
        return new GetRequest(url);
    }

    /**
     * POST请求
     *
     * @author 狐彻 2020/11/09 10:49
     */
    public ClientInterface POST(String urlString, String body) throws IOException {
        URL url = convertUrl(urlString);
        return new PostRequest(url, body);
    }

    /**
     * POST请求
     *
     * @author 狐彻 2020/11/09 10:50
     */
    public ClientInterface POST(String urlString, String body, String charsetName) throws IOException {
        URL url = convertUrl(urlString);
        return new PostRequest(url, body, charsetName);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 处理url
     *
     * @author 狐彻 2020/11/09 9:33
     */
    private URL convertUrl(String urlString) throws MalformedURLException {
        if (urlString.startsWith("/"))
            urlString = urlString.substring(1);
        return new URL(baseUrl + urlString);
    }

    /**
     * 处理GetURL和其参数
     *
     * @author 狐彻 2020/11/09 9:36
     */
    private URL convertGETUrl(final String urlString, Map<String, String> params) throws MalformedURLException {
        String _urlString = urlString;
        if (_urlString.endsWith("/"))
            _urlString = _urlString.substring(0, _urlString.length() - 1);
        _urlString = baseUrl + _urlString;
        if (params.isEmpty()) return new URL(_urlString);
        _urlString += "?";
        StringBuilder sb = new StringBuilder(_urlString);
        for (String key : params.keySet()) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        _urlString = sb.substring(0, sb.length() - 1);
        return new URL(_urlString);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 单例容器
    ///////////////////////////////////////////////////////////////////////////

    private static class ClientUtilHolder {
        static ClientUtil instance = new ClientUtil();
    }
}

package com.binzee.foxdevframe.utils.http;

import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * 网络工具接口
 *
 * @author 狐彻
 * 2020/10/27 16:46
 */
public interface ClientInterface {

//    void get(OnCallListener listener);
//    void post(OnCallListener listener);

    /**
     * 开始请求
     *
     * @param listener 请求回调
     * @author 狐彻 2020/11/09 9:22
     */
    void request(OnCallListener listener);

    /**
     * 设置连接超时
     *
     * @author 狐彻 2020/11/09 9:22
     */
    ClientInterface setConnectTimeout(int miles);

    /**
     * 设置读取超时
     *
     * @param miles
     * @author 狐彻 2020/11/09 9:23
     */
    ClientInterface setReadTimeout(int miles);

    /**
     * 请求回调
     *
     * @author 狐彻 2020/11/09 9:47
     */
    interface OnCallListener {

        /**
         * 得到反映并获取相应码
         *
         * @param connection 用于请求前最后调整的连接实例
         * @author 狐彻 2020/11/09 9:47
         */
        void onStart(HttpURLConnection connection);

        /**
         * 请求成功
         *
         * @param connection   连接实例
         * @param responseCode 相应码
         * @param stream       输入流
         * @author 狐彻 2020/11/09 9:47
         */
        void onSuccess(HttpURLConnection connection, int responseCode, InputStream stream);

        /**
         * 获取出错
         *
         * @author 狐彻 2020/11/09 9:48
         */
        void onError(Throwable throwable);
    }
}

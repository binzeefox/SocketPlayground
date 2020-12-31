package com.binzee.foxdevframe.ui.tools.requests;

import android.content.Intent;
import android.os.Bundle;

import com.binzee.foxdevframe.utils.media.SysImageRequester;

/**
 * 活动请求业务接口
 *
 * @author 狐彻
 * 2020/10/30 11:36
 */
public interface ActivityRequestInterface {

    /**
     * 结果回调
     *
     * @author 狐彻 2020/10/30 11:37
     */
    interface OnResultCallback {

        /**
         * 回调
         *
         * @param requestCode   请求码
         * @param resultCode    结果码
         * @param resultData    返回结果
         * @author 狐彻 2020/10/30 11:37
         */
        void onResult(int requestCode, int resultCode, Intent resultData);
    }

    /**
     * 请求
     *
     * @author 狐彻 2020/10/30 11:39
     */
    void request(Intent intent, int requestCode, OnResultCallback callback);
    void request(Intent intent, int requestCode, OnResultCallback callback, Bundle options);
}

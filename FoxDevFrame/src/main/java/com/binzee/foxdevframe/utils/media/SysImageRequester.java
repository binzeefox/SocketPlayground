package com.binzee.foxdevframe.utils.media;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.binzee.foxdevframe.FoxCore;
import com.binzee.foxdevframe.ui.tools.requests.ActivityRequestInterface;
import com.binzee.foxdevframe.ui.tools.requests.ActivityRequester;

/**
 * 系统应用获取图片
 *
 * @author 狐彻
 * 2020/10/30 11:08
 */
public class SysImageRequester {
    private static final String TAG = "SysImageRequester";
    private final ActivityRequestInterface requester;

    /**
     * 构造器
     *
     * @author 狐彻 2020/10/30 11:13
     */
    public SysImageRequester(FragmentManager manager) {
        requester = new ActivityRequester(manager);
    }

    /**
     * 静态获取
     *
     * @author 狐彻 2020/10/30 11:14
     */
    public static SysImageRequester get(FragmentManager manager) {
        return new SysImageRequester(manager);
    }

    /**
     * 静态获取
     *
     * @author 狐彻 2020/10/30 11:14
     */
    public static SysImageRequester get(AppCompatActivity activity) {
        return new SysImageRequester(activity.getSupportFragmentManager());
    }

    /**
     * 静态获取
     *
     * @author 狐彻 2020/10/30 11:14
     */
    public static SysImageRequester get(Fragment fragment) {
        return new SysImageRequester(fragment.getChildFragmentManager());
    }

    ///////////////////////////////////////////////////////////////////////////
    // 业务方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 开启相册
     *
     * @author 狐彻 2020/10/30 11:18
     */
    public void openGallery(int requestCode, OnResultCallback callback) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        if (intent.resolveActivity(FoxCore.getPackageManager()) != null) {
            try {
                requester.request(intent, requestCode, createCallback(requestCode, callback));
            } catch (Exception e) {
                callback.onError(e);
            } finally {
                callback.onComplete();
            }
        }
    }

    /**
     * 开启相机
     *
     * @param requestCode 请求码
     * @param cacheUri  缓存路径
     * @param callback  获取回调
     * @author 狐彻 2020/10/30 17:14
     */
    public void openCamera(int requestCode, @NonNull Uri cacheUri, OnResultCallback callback) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cacheUri);
        try {
            requester.request(intent, requestCode, createCallback(requestCode, callback));
        } catch (Exception e) {
            callback.onError(e);
        } finally {
            callback.onComplete();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 创建监听器
     *
     * @author 狐彻 2020/10/30 17:24
     */
    private ActivityRequestInterface.OnResultCallback createCallback(int requestCode, OnResultCallback callback) {
        return (requestCode1, resultCode, resultData) -> {
            if (resultCode == Activity.RESULT_CANCELED)
                callback.onError(new RequestCancelException());
            else {
                Uri uri = resultData.getData();
                callback.onResult(requestCode, uri);
            }
        };
    }


    ///////////////////////////////////////////////////////////////////////////
    // 回调监听
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 获取结果回调
     *
     * @author 狐彻 2020/10/30 11:20
     */
    public interface OnResultCallback {
        void onResult(int requestCode, Uri uri);
        void onError(Throwable e);
        void onComplete();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 异常
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 用户取消请求
     *
     * @author 狐彻 2020/10/30 12:02
     */
    public static class RequestCancelException extends Exception {
        public RequestCancelException() {
            super("获取媒体失败，用户取消");
        }
    }
}

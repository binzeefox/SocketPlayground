package com.binzee.foxdevframe.ui.tools.requests;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * 请求跳转
 * <p>
 * 专门处理{@link androidx.core.app.ActivityCompat#startActivityForResult}
 *
 * @author 狐彻
 * 2020/10/30 11:27
 */
public class ActivityRequester extends BaseRequester implements ActivityRequestInterface {
    private static final String TAG = "ActivityRequester";

    /**
     * 构造器
     *
     * @param fragmentManager 碎片管理器
     * @author 狐彻 2020/10/21 16:51
     */
    public ActivityRequester(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    /**
     * 静态获取
     *
     * @author 狐彻 2020/10/30 11:51
     */
    public static ActivityRequester get(FragmentManager manager) {
        return new ActivityRequester(manager);
    }

    public static ActivityRequester get(AppCompatActivity activity) {
        return new ActivityRequester(activity.getSupportFragmentManager());
    }

    public static ActivityRequester get(Fragment fragment) {
        return new ActivityRequester(fragment.getChildFragmentManager());
    }

    @NonNull
    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @NonNull
    @Override
    protected Fragment createFragment() {
        return new InnerFragment();
    }

    @Override
    protected InnerFragment getFragment() {
        return (InnerFragment) super.getFragment();
    }

    @Override
    public void request(Intent intent, int requestCode, OnResultCallback callback) {
        request(intent, requestCode, callback, null);
    }

    @Override
    public void request(Intent intent, int requestCode, OnResultCallback callback, Bundle options) {
        if (intent == null) return;
        getFragment().callback = callback;
        if (options == null) getFragment().startActivityForResult(intent, requestCode);
        else getFragment().startActivityForResult(intent, requestCode, options);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 内部业务碎片
     *
     * @author 狐彻 2020/10/30 11:34
     */
    @RestrictTo(LIBRARY)
    public static class InnerFragment extends Fragment {
        // 回调
        private OnResultCallback callback = null;

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (callback == null) return;
            callback.onResult(requestCode, resultCode, data);
            callback = null;
        }
    }
}

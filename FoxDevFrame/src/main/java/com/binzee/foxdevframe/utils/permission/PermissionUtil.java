package com.binzee.foxdevframe.utils.permission;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.binzee.foxdevframe.utils.LogUtil;
import com.binzee.foxdevframe.ui.tools.requests.BaseRequester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static androidx.annotation.RestrictTo.Scope.LIBRARY;
import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

/**
 * 权限请求实现类
 *
 * @author 狐彻
 * 2020/10/21 16:40
 */
public class PermissionUtil extends BaseRequester implements PermissionCheckerInterface{
    private static final String TAG = "PermissionUtil";
    private final List<String> permissionList = new ArrayList<>();  //容器

    /**
     * 构造器
     *
     * @param fragmentManager   碎片管理器
     * @author 狐彻 2020/10/21 16:51
     */
    public PermissionUtil(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    /**
     * 静态获取 activity
     *
     * @author 狐彻 2020/10/25 15:24
     */
    public static PermissionUtil with(AppCompatActivity activity) {
        return new PermissionUtil(activity.getSupportFragmentManager());
    }

    /**
     * 静态获取 fragment
     *
     * @author 狐彻 2020/10/25 15:24
     */
    public static PermissionUtil with(Fragment fragment) {
        return new PermissionUtil(fragment.getChildFragmentManager());
    }

    /**
     * 静态获取
     *
     * @author 狐彻 2020/10/25 15:25
     */
    public static PermissionUtil with(FragmentManager fragmentManager) {
        return new PermissionUtil(fragmentManager);
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
    public PermissionCheckerInterface addPermission(String permission) {
         permissionList.add(permission);
         return this;
    }

    @Override
    public PermissionCheckerInterface addPermissions(Collection<String> permissionList) {
        this.permissionList.addAll(permissionList);
        return this;
    }

    @Override
    public void check(int requestCode, OnCheckResultListener listener) {
        List<String> failedList = ((InnerFragment) getFragment()).check(permissionList);
        List<String> noAskList = new ArrayList<>();
        for (String permission: failedList) {
            if (((InnerFragment) getFragment()).checkNoAsk(permission))
                noAskList.add(permission);
        }
        listener.onResult(requestCode, failedList, noAskList);
    }

    @Override
    public void checkAndRequest(int requestCode, OnCheckResultListener listener) {
        List<String> failedList = getFragment().check(permissionList);
        if (failedList.isEmpty()) listener.onResult(requestCode, new ArrayList<>(), new ArrayList<>());
        else getFragment().request(requestCode, failedList, listener);
    }

    @Override
    protected InnerFragment getFragment() {
        return (InnerFragment) super.getFragment();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部业务Fragment
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 内部业务碎片
     *
     * @author 狐彻 2020/10/21 17:04
     */
    @RestrictTo(LIBRARY)
    public static class InnerFragment extends Fragment {
        private volatile OnCheckResultListener listener = null;

        /**
         * 权限回调
         *
         * @author 狐彻 2020/10/21 17:32
         */
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            final List<String> failedList = new ArrayList<>();
            final List<String> noAskList = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++){
                if (grantResults[i] != PERMISSION_GRANTED) {
                    failedList.add(permissions[i]);
                    if (checkNoAsk(permissions[i]))
                        noAskList.add(permissions[i]);
                }
            }

            if (!failedList.isEmpty())
                LogUtil.v(TAG, "startCheckAndRequest: 权限未通过 => " + failedList);
            if (!failedList.isEmpty())
                LogUtil.v(TAG, "startCheckAndRequest: 权限不再询问 => " + noAskList);

            listener.onResult(requestCode, failedList, noAskList);
            listener = null;
        }

        /**
         * 检查权限
         *
         * @author 狐彻 2020/10/21 17:12
         */
        private List<String> check(List<String> permissionList) {
            LogUtil.v(TAG, "check: 检查权限 => " + permissionList);

            final List<String> failedList = new ArrayList<>();
            for (String permission: permissionList) {
                int result = ActivityCompat.checkSelfPermission(getContext(), permission);
                if (result != PERMISSION_GRANTED)
                    failedList.add(permission);
            }

            return failedList;
        }

        /**
         * 请求权限
         *
         * @author 狐彻 2020/10/21 17:28
         */
        private void request(int requestCode, List<String> permissionList, OnCheckResultListener listener) {
            // 若当前监听不为空，则上一次请求未完成，忽略此次请求
            if (this.listener != null) return;
            this.listener = listener;
            requestPermissions(permissionList.toArray(new String[0]), requestCode);
        }

        /**
         * 检查是否不在询问
         *
         * @author 狐彻 2020/10/21 17:13
         */
        private boolean checkNoAsk(String permission) {
            return !shouldShowRequestPermissionRationale(permission);
        }
    }
}

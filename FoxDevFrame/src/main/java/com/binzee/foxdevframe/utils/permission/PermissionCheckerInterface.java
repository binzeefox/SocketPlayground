package com.binzee.foxdevframe.utils.permission;

import java.util.Collection;
import java.util.List;

/**
 * 抽象 权限获取类
 *
 * @author 狐彻
 * 2020/10/21 16:32
 */
public interface PermissionCheckerInterface {

    /**
     * 权限检查结果
     *
     * @author 狐彻 2020/10/21 16:36
     */
    interface OnCheckResultListener {

        /**
         * 获取回调
         *
         * @param requestCode   请求码
         * @param failedList    失败的权限列表
         * @param noAskList 不再询问的权限列表，包含在失败列表里
         * @author 狐彻 2020/10/21 16:36
         */
        void onResult(int requestCode, List<String> failedList, List<String> noAskList);
    }

    /**
     * 添加待处理权限
     *
     * @author 狐彻 2020/10/21 16:33
     */
    PermissionCheckerInterface addPermission(String permission);

    /**
     * 批量添加待处理权限
     *
     * @author 狐彻 2020/10/21 16:34
     */
    PermissionCheckerInterface addPermissions(Collection<String> permissionList);

    /**
     * 仅检查
     *
     * @author 狐彻 2020/10/21 16:37
     */
    void check(int requestCode, OnCheckResultListener listener);

    /**
     * 检查并请求
     *
     * @author 狐彻 2020/10/21 16:37
     */
    void checkAndRequest(int requestCode, OnCheckResultListener listener);
}

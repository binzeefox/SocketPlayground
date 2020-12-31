package com.binzee.foxdevframe.ui.tools.launcher.target;

import android.os.Bundle;

/**
 * 抽象 跳转目标
 *
 * @author 狐彻
 * 2020/10/22 9:03
 */
public interface LauncherTarget {

    /**
     * 添加参数
     *
     * @author 狐彻 2020/10/22 9:04
     */
    LauncherTarget putExtra(String key, Bundle value);

    /**
     * 开始
     *
     * @author 狐彻 2020/10/22 9:04
     */
    void commit();
}

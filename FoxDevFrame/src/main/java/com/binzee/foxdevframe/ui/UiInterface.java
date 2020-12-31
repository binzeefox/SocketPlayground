package com.binzee.foxdevframe.ui;

import android.content.Context;
import android.os.Bundle;

import com.binzee.foxdevframe.ui.tools.launcher.Launcher;

import java.util.List;

/**
 * 交互页面抽象
 *
 * @author 狐彻
 * 2020/10/21 9:38
 */
public interface UiInterface {

//    /**
//     * 提示框
//     *
//     * @author 狐彻 2020/10/21 9:44
//     */
//    void toast(CharSequence text);

//    /**
//     * 跳转
//     *
//     * @author 狐彻 2020/10/21 9:46
//     */
//    Launcher navigate(String clsFullName, Bundle params);

    Context getContext();

    /**
     * 主线程运行
     *
     * @author 狐彻 2020/10/21 11:36
     */
    void runOnUiThread(Runnable runnable);
}

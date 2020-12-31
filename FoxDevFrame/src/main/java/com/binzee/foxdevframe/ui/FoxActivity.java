package com.binzee.foxdevframe.ui;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.binzee.foxdevframe.utils.LogUtil;

/**
 * Activity基类
 *
 * @author 狐彻
 * 2020/10/21 9:09
 */
public abstract class FoxActivity extends AppCompatActivity implements UiInterface {
    private static final String TAG = "FoxActivity";

    //二次点击专用
    private OnPressTwiceListener pressTwiceListener = null;
    private long lastBackPressedTimeMills = 0;  //上次点击返回键时间
    private long backPressPeriod = 0;   //验证两次点击时间间隔

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView();
        onCreate();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        initContentView();
        onCreate();
    }

//    @Override
//    public void toast(CharSequence text) {
//        ToastHelper.get().showToast(text, Toast.LENGTH_SHORT);
//    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onBackPressed() {
        long timeStamp = System.currentTimeMillis();
        if (pressTwiceListener == null) super.onBackPressed();
        else if (timeStamp > (backPressPeriod + lastBackPressedTimeMills)) {
            //当前时间大于返回键过期时间，时间过期
            lastBackPressedTimeMills = timeStamp;
            pressTwiceListener.onFirstPress();
        } else {
            //当前时间小于返回键过期时间。二次点击
            lastBackPressedTimeMills = 0;
            pressTwiceListener.onSecondPress();
        }
    }

    /**
     * 暴露父类onBackPressed
     *
     * fixme 我知道这么做挺蠢的。。。不过先这样，等我学会更聪明的写法就改掉
     * @author 狐彻 2020/11/06 17:10
     */
    public void superOnBackPressed() {
        super.onBackPressed();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 工具方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 设置检查二次点击返回键
     *
     * @param listener  二次点击回调
     * @param backPressPeriod   两次点击间隔毫秒
     * @author 狐彻 2020/11/06 17:04
     */
    public void setBackPressTwiceCheck(OnPressTwiceListener listener, long backPressPeriod) {
        this.pressTwiceListener = listener;
        this.backPressPeriod = backPressPeriod;
    }

    /**
     * 设置全屏
     *
     * @author 狐彻 2020/10/21 12:03
     */
    protected void setFullScreen() {
        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }



    ///////////////////////////////////////////////////////////////////////////
    // 保护方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * onCreate(Bundle)和onCreate(Bundle, PersistableBundle) 方法加载ContentView后都会调用的回调
     *
     * @author 狐彻 2020/10/25 14:46
     */
    protected void onCreate(){}

    /**
     * 创建布局，高优先级
     *
     * @author 狐彻 2020/10/21 11:59
     */
    protected View createContentView() {
        return null;
    }

    /**
     * 通过资源ID加载布局，低优先级
     *
     * @author 狐彻 2020/10/21 11:58
     */
    protected int getContentViewResource() {
        return -1;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 初始化ContentView
     *
     * @author 狐彻 2020/10/25 14:40
     */
    private void initContentView() {
        View layout = createContentView();
        if (layout != null) setContentView(layout);
        else if (getContentViewResource() != -1)
            setContentView(getContentViewResource());
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 生命周期回调
     *
     * @author 狐彻 2020/10/21 9:15
     */
    public static class FoxActivityCallback implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            LogUtil.v(TAG, "onActivityCreated: " + activity.getClass().getName());
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            LogUtil.v(TAG, "onActivityStarted: " + activity.getClass().getName());
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            LogUtil.v(TAG, "onActivityResumed: " + activity.getClass().getName());
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            LogUtil.v(TAG, "onActivityPaused: " + activity.getClass().getName());
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            LogUtil.v(TAG, "onActivityStopped: " + activity.getClass().getName());
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            LogUtil.v(TAG, "onActivitySaveInstanceState: " + activity.getClass().getName());
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            LogUtil.v(TAG, "onActivityDestroyed: " + activity.getClass().getName());
        }
    }

    /**
     * 二次点击监听器
     *
     * @author 狐彻 2020/11/06 16:30
     */
    public interface OnPressTwiceListener {

        /**
         * 第一次点击
         *
         * @author 狐彻 2020/11/06 16:31
         */
        void onFirstPress();

        /**
         * 第二次点击
         *
         * @author 狐彻 2020/11/06 16:31
         */
        void onSecondPress();
    }
}

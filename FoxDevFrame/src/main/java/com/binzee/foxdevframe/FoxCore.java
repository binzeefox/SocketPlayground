package com.binzee.foxdevframe;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.binzee.foxdevframe.ui.FoxActivity;
import com.binzee.foxdevframe.utils.LogUtil;
import com.binzee.foxdevframe.utils.device.resource.SharedPreferenceUtil;

import java.util.Locale;
import java.util.Stack;

/**
 * 核心类
 * <p>
 * 提供静态Activity单例
 * 提供应用信息
 * 注册LifeCycle
 *
 * @author 狐彻
 * 2020/10/21 8:10
 */
public class FoxCore {
    private static final String TAG = "FoxCore";

    // Application实例
    private Application application;

    // 返回栈
    // @author 狐彻 2020/10/21 8:44
    private final ActivityStack mActivityStack = new ActivityStack();

    // 私有化构造器
    // @author 狐彻 2020/10/21 8:54
    private FoxCore() {
        //private constructor
    }

    /**
     * 获取单例
     *
     * @author 狐彻 2020/10/21 9:33
     */
    @NonNull
    public static FoxCore get() {
        FoxCore core = FoxCoreHolder.instance;
        if (core.application == null)
            throw new UnInitializedException();
        return core;
    }

    /**
     * 初始化
     *
     * @author 狐彻 2020/10/21 8:13
     */
    public static void init(Context context) {
        if (context instanceof Application)
            FoxCoreHolder.instance.application = (Application) context;
        else
            FoxCoreHolder.instance.application = (Application) context.getApplicationContext();
        FoxCoreHolder.instance.registerActivityCallback();

        // 设置语言
        // @author 狐彻 2020/11/17 15:30
        String languageTag = SharedPreferenceUtil.get()
                .readString("FOX_SETTING", "languageTag"
                        , Locale.getDefault().toLanguageTag());
        Locale locale = Locale.forLanguageTag(languageTag);
        get().setLanguage(locale);
    }

    /**
     * 设置语言
     *
     * @author 狐彻 2020/11/17 15:26
     */
    private void setLanguage(@NonNull Locale locale) {
        Resources r = FoxCore.getApplication().getResources();
        DisplayMetrics m = r.getDisplayMetrics();
        Configuration c = r.getConfiguration();

        c.setLocale(locale);
        r.updateConfiguration(c, m);
    }

    /**
     * 获取Application单例
     *
     * @author 狐彻 2020/10/21 8:45
     */
    @NonNull
    public static Application getApplication() {
        return get().application;
    }

    /**
     * 获取返回栈
     *
     * @author 狐彻 2020/10/21 9:25
     */
    public static ActivityStack getActivityStack() {
        return get().mActivityStack;
    }

    /**
     * 获取栈顶Activity
     *
     * @author 狐彻 2020/10/21 9:25
     */
    public static Activity getTopActivity() {
        return getActivityStack().peek();
    }

    /**
     * 获取包名
     *
     * @author 狐彻 2020/10/21 9:30
     */
    public static String getPackageName() {
        return getApplication().getPackageName();
    }

    /**
     * 获取PackageManager
     *
     * @author 狐彻 2020/10/21 9:28
     */
    @NonNull
    public static PackageManager getPackageManager() {
        return getApplication().getPackageManager();
    }

    /**
     * 获取包信息
     *
     * @author 狐彻 2020/10/21 9:31
     */
    @NonNull
    public static PackageInfo getPackageInfo() throws PackageManager.NameNotFoundException {
        return getPackageManager().getPackageInfo(getPackageName(), 0);
    }

    /**
     * 获取版本名
     *
     * @author 狐彻 2020/10/21 9:31
     */
    @NonNull
    public static String getVersionName() {
        try {
            return getPackageInfo().versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(TAG, "getVersionName: 获取失败", e);
            return "";
        }
    }

    /**
     * 获取版本号，兼容Android P
     *
     * @return 获取失败则返回 -1
     * @author 狐彻 2020/10/21 9:34
     */
    public static long getVersionCode() {
        try {
            PackageInfo info = getPackageInfo();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return info.getLongVersionCode();
            } else return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getVersionCode: 获取失败", e);
            return -1;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 注册Activity回调
     *
     * @author 狐彻 2020/10/21 8:56
     */
    private void registerActivityCallback() {
        getApplication().registerActivityLifecycleCallbacks(new FoxActivity.FoxActivityCallback() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                super.onActivityCreated(activity, savedInstanceState);
                //生成Activity，压入栈
                mActivityStack.push(activity);
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                super.onActivityResumed(activity);
                //Activity可见，移至栈顶
                mActivityStack.moveToTop(activity);
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                super.onActivityDestroyed(activity);
                //Activity回收，移除
                mActivityStack.superRemove(activity);
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 返回栈
     *
     * @author 狐彻 2020/10/21 9:19
     */
    private static class ActivityStack extends Stack<Activity> {
        @Override
        public synchronized Activity pop() {
            Activity activity = super.pop();
            activity.finish();
            return activity;
        }

        @Override
        public synchronized Activity remove(int index) {
            Activity activity = super.remove(index);
            activity.finish();
            return activity;
        }

        @Override
        public boolean remove(@Nullable Object o) {
            if (o instanceof Activity) ((Activity) o).finish();
            return super.remove(o);
        }

        ///////////////////////////////////////////////////////////////////////////
        // 私有方法
        ///////////////////////////////////////////////////////////////////////////

        /**
         * 代理父类移除方法
         *
         * @author 狐彻 2020/10/21 9:23
         */
        private boolean superRemove(@NonNull Object o) {
            return super.remove(o);
        }

        /**
         * 把站内某值移至栈顶
         *
         * @author 狐彻 2020/10/21 9:20
         */
        private void moveToTop(Activity o) {
            super.remove(o);
            push(o);
        }
    }

    /**
     * 单例容器
     *
     * @author 狐彻 2020/10/21 8:16
     */
    private static class FoxCoreHolder {
        private static final FoxCore instance = new FoxCore();
    }

    /**
     * 未初始化异常
     *
     * @author 狐彻 2020/10/21 8:21
     */
    private static class UnInitializedException extends RuntimeException {
        private UnInitializedException() {
            super("未初始化，请调用 FoxCore.init(Context) 进行初始化");
        }
    }
}

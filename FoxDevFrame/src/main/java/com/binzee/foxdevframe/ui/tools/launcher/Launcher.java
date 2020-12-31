package com.binzee.foxdevframe.ui.tools.launcher;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.binzee.foxdevframe.FoxCore;
import com.binzee.foxdevframe.ui.tools.launcher.target.ActivityTarget;
import com.binzee.foxdevframe.ui.tools.launcher.target.LauncherTarget;
import com.binzee.foxdevframe.ui.tools.launcher.target.ServiceTarget;

import java.util.concurrent.Executor;

/**
 * 跳转工具
 *
 * @author 狐彻
 * 2020/10/22 8:31
 */
public class Launcher {
    @NonNull
    private final Context ctx;

    private Launcher(@NonNull Context ctx) {
        this.ctx = ctx;
    }

    /**
     * 以指定Context跳转
     *
     * @author 狐彻 2020/10/22 8:34
     */
    public static Launcher with(Context context) {
        return new Launcher(context);
    }

    /**
     * 以最顶部Activity开启
     *
     * @author 狐彻 2020/10/22 8:35
     */
    public static Launcher withTopActivity() {
        return new Launcher(FoxCore.getTopActivity());
    }

    /**
     * 以ApplicationContext开启
     *
     * @author 狐彻 2020/10/22 9:00
     */
    public static Launcher withApplication() {
        return new Launcher(FoxCore.getApplication());
    }

    ///////////////////////////////////////////////////////////////////////////
    // 参数方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 系统页面快捷方式
     *
     * @author 狐彻 2020/10/27 8:34
     */
    public SystemShortCutLauncher systemShortCuts() {
        return new SystemShortCutLauncher(this);
    }

    /**
     * 跳转至Activity
     *
     * @author 狐彻 2020/10/22 9:44
     */
    public LauncherTarget toActivity(@NonNull Intent intent) {
        return toActivity(intent, null);
    }

    /**
     * 跳转至Activity
     *
     * @author 狐彻 2020/10/22 9:45
     */
    public LauncherTarget toActivity(@NonNull Class<? extends Activity> cls) {
        return toActivity(cls, null);
    }

    /**
     * 跳转至Activity
     *
     * @param options startActivity()的第二个参数
     * @author 狐彻 2020/10/22 9:39
     */
    public LauncherTarget toActivity(@NonNull Intent intent, Bundle options) {
        ActivityTarget target =  new ActivityTarget(ctx, intent);
        if (options != null) target.setOptions(options);
        return target;
    }

    /**
     * 跳转至Activity
     *
     * @param options startActivity()的第二个参数
     * @author 狐彻 2020/10/22 9:42
     */
    public LauncherTarget toActivity(@NonNull Class<? extends Activity> cls, Bundle options) {
        Intent intent = new Intent(ctx, cls);
        ActivityTarget target =  new ActivityTarget(ctx, intent);
        if (options != null) target.setOptions(options);
        return target;
    }

    /**
     * 目标服务
     *
     * @author 狐彻 2020/10/22 10:01
     */
    public LauncherTarget toService(@NonNull Intent intent) {
        return new ServiceTarget(ctx, intent);
    }

    /**
     * 目标服务
     *
     * @author 狐彻 2020/10/22 10:01
     */
    public LauncherTarget toService(@NonNull Class<? extends Service> cls) {
        Intent intent = new Intent(ctx, cls);
        return new ServiceTarget(ctx, intent);
    }


    /**
     * 绑定服务
     *
     * @author 狐彻 2020/10/22 10:03
     */
    public LauncherTarget toServiceBind(@NonNull Intent intent, int flags, @NonNull ServiceConnection conn) {
        ServiceTarget target = (ServiceTarget) toService(intent);
        target.setBind(flags, conn);
        return target;
    }

    /**
     * 绑定服务
     *
     * @author 狐彻 2020/10/22 10:07
     */
    public LauncherTarget toServiceBind(@NonNull Class<? extends Service> cls, int flags, @NonNull ServiceConnection conn) {
        ServiceTarget target = (ServiceTarget) toService(cls);
        target.setBind(flags, conn);
        return target;
    }

    /**
     * 绑定服务
     *
     * @author 狐彻 2020/10/22 10:08
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public LauncherTarget toServiceBind(@NonNull Intent intent, int flags, @NonNull Executor executor, @NonNull ServiceConnection conn) {
        ServiceTarget target = (ServiceTarget) toService(intent);
        target.setBind(flags, executor, conn);
        return target;
    }

    /**
     * 绑定服务
     *
     * @author 狐彻 2020/10/22 10:08
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public LauncherTarget toServiceBind(@NonNull Class<? extends Service> cls, int flags, @NonNull Executor executor, @NonNull ServiceConnection conn) {
        ServiceTarget target = (ServiceTarget) toService(cls);
        target.setBind(flags, executor, conn);
        return target;
    }
}

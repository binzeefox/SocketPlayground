package com.binzee.foxdevframe.ui.tools.launcher.target;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;

import java.util.concurrent.Executor;

/**
 * 服务目标
 *
 * @author 狐彻
 * 2020/10/22 9:47
 */
public class ServiceTarget implements LauncherTarget {

    @NonNull
    private final Intent intent;  //跳转Intent
    @NonNull
    private final Context ctx;    //承载的ctx
    private Bundle options = null;  //作为startActivity()的第二个参数
    private boolean isBind;   //是否bind

    //下列为Bind参数
    private int flags;
    private Executor executor;
    private ServiceConnection conn;

    /**
     * 构造器
     *
     * @param context 承载的Context
     * @param intent  目标Intent
     * @author 狐彻 2020/10/22 9:28
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public ServiceTarget(@NonNull Context context, @NonNull Intent intent) {
        ctx = context;
        this.intent = intent;
    }

    @Override
    public LauncherTarget putExtra(String key, Bundle value) {
        intent.putExtra(key, value);
        return this;
    }

    @Override
    public void commit() {
        if (!isBind) ctx.startService(intent);
        else if (executor != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            ctx.bindService(intent, flags, executor, conn);
        else ctx.bindService(intent, conn, flags);
    }

    /**
     * 设置为Bind
     *
     * @author 狐彻 2020/10/22 9:50
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    public void setBind(int flags, @NonNull Executor executor, @NonNull ServiceConnection conn) {
        isBind = true;
        this.flags = flags;
        this.executor = executor;
        this.conn = conn;
    }

    /**
     * 设置为Bind
     *
     * @author 狐彻 2020/10/22 9:59
     */
    public void setBind(int flags, @NonNull ServiceConnection conn) {
        isBind = true;
        this.flags = flags;
        this.conn = conn;
    }
}

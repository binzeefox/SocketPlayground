package com.binzee.foxdevframe.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.binzee.foxdevframe.FoxCore;

/**
 * 碎片基类
 *
 * @author 狐彻
 * 2020/10/21 11:27
 */
public abstract class FoxFragment extends Fragment implements UiInterface {

//    @Override
//    public void toast(CharSequence text) {
//        ToastHelper.get().showToast(text, Toast.LENGTH_SHORT);
//    }


//    @Override
//    public void navigate(String clsFullName, Bundle params) {
//
//    }

    @NonNull
    @Override
    public Context getContext() {
        Context ctx = super.getContext();
        if (ctx == null) ctx = FoxCore.getApplication();
        return ctx;
    }

    @Override
    public void runOnUiThread(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }
}

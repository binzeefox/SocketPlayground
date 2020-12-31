package com.binzee.foxdevframe.ui.tools.popup.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import com.binzee.foxdevframe.FoxCore;

/**
 * 系统弹窗
 *
 * @author 狐彻
 * 2020/10/27 9:01
 */
public class SystemDialogHelper {
    private final Context mCtx;

    public SystemDialogHelper(){
        mCtx = FoxCore.getApplication();
    }

    /**
     * 网络设置弹窗
     *
     * @author 狐彻 2020/10/27 9:05
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void showInternetSetting() {
        Intent intent = getPopupIntent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
        mCtx.startActivity(intent);
    }

    /**
     * NFC设置弹窗
     *
     * @author binze 2019/12/24 12:05
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void showNFCSetting() {
        Intent intent = getPopupIntent(Settings.Panel.ACTION_NFC);
        mCtx.startActivity(intent);
    }

    /**
     * 音量设置弹窗
     *
     * @author binze 2019/12/24 12:05
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void showVolumeSetting() {
        Intent intent = getPopupIntent(Settings.Panel.ACTION_VOLUME);
        mCtx.startActivity(intent);
    }

    /**
     * WIFI设置弹窗
     *
     * @author binze 2019/12/24 12:05
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void showWifiSetting() {
        Intent intent = getPopupIntent(Settings.Panel.ACTION_WIFI);
        mCtx.startActivity(intent);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 一键获取Intent
     *
     * @author 狐彻 2020/10/27 9:11
     */
    private Intent getPopupIntent(String action) {
        Intent intent = new Intent(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}

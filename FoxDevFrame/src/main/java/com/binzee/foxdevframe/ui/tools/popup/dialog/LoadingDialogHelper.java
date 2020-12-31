package com.binzee.foxdevframe.ui.tools.popup.dialog;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.binzee.foxdevframe.R;
import com.binzee.foxdevframe.ui.views.CustomDialogFragment;
import com.binzee.foxdevframe.utils.device.resource.DimenUtil;

/**
 * 加载框助手
 *
 * @author tong.xw
 * 2020/12/25 11:34
 */
public class LoadingDialogHelper {
    private static final String FRAGMENT_TAG = "fox_loading_fragment_tag";
    private final FragmentManager mFManager;
    private final Context mCtx;
    private DialogFragment dFragment = null;
    @StyleRes
    private int styleRes = R.style.Theme_AppCompat_Dialog;

    /**
     * 构建器
     *
     * @param fm 承担业务的FragmentManager
     */
    public LoadingDialogHelper(@NonNull FragmentManager fm, Context context) {
        mFManager = fm;
        mCtx = context;
    }

    public LoadingDialogHelper(@NonNull Fragment fragment) {
        this(fragment.getChildFragmentManager(), fragment.getContext());
    }

    public LoadingDialogHelper(@NonNull AppCompatActivity activity) {
        this(activity.getSupportFragmentManager(), activity);
    }

    /**
     * 设置Style
     *
     * @author tong.xw 2020/12/25 11:48
     */
    public void setStyle(@StyleRes int styleRes) {
        this.styleRes = styleRes;
        dFragment = createFragment();
    }

    /**
     * 是否在显示
     *
     * @author tong.xw 2020/12/25 11:56
     */
    public boolean isShowing() {
        return dFragment != null && dFragment.isVisible();
    }

    /**
     * 显示
     *
     * @author tong.xw 2020/12/25 11:44
     */
    public void show(@NonNull CharSequence title) {
        DialogFragment dialog = getDFragment();
        if (dialog.getDialog() != null)
            dialog.getDialog().setTitle(title);
        dialog.show(mFManager, FRAGMENT_TAG);
    }

    /**
     * 显示
     *
     * @author tong.xw 2020/12/25 11:51
     */
    public void show(@StringRes int titleId) {
        DialogFragment dialog = getDFragment();
        if (dialog.getDialog() != null)
            dialog.getDialog().setTitle(titleId);
        dialog.show(mFManager, FRAGMENT_TAG);
    }

    /**
     * 显示
     *
     * @author tong.xw 2020/12/25 11:52
     */
    public void show() {
        show("请稍后...");
    }

    /**
     * 注销
     *
     * @author tong.xw 2020/12/25 11:57
     */
    public void dismiss() {
        getDFragment().dismiss();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 空安全获取Fragment
     *
     * @author tong.xw 2020/12/25 11:40
     */
    @NonNull
    private DialogFragment getDFragment() {
        if (dFragment != null) return dFragment;
        Fragment fragment = mFManager.findFragmentByTag(FRAGMENT_TAG);
        if (fragment instanceof DialogFragment) {
            dFragment = (DialogFragment) fragment;
        } else {
            dFragment = createFragment();
        }
        return dFragment;
    }

    @NonNull
    private DialogFragment createFragment() {
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        final int DP16 = DimenUtil.dipToPx(16);
        ProgressBar bar = new ProgressBar(mCtx);
        bar.setPadding(0, DP16, 0, DP16);
        bar.setLayoutParams(params);
        bar.setIndeterminate(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx, styleRes);
        builder.setView(bar).setCancelable(false);
        DialogFragment df = new CustomDialogFragment(builder.create());
        df.setCancelable(false);
        return df;
    }
}

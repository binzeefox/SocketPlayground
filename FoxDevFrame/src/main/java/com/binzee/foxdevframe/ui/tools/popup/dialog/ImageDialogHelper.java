package com.binzee.foxdevframe.ui.tools.popup.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.binzee.foxdevframe.R;
import com.binzee.foxdevframe.ui.views.AutoAdjustImageView;
import com.binzee.foxdevframe.ui.views.CustomDialogFragment;

/**
 * 图片弹窗助手
 *
 * @author tong.xw
 * 2020/12/25 12:05
 */
public class ImageDialogHelper {
    private static final String FRAGMENT_TAG = "fox_image_fragment_tag";
    private final FragmentManager mFManager;
    private final Context mCtx;
    private DialogFragment dFragment = null;
    @StyleRes
    private int styleRes = R.style.Theme_AppCompat_Dialog;
    private final View.OnClickListener onImageClickListener = v -> {
        if (dFragment == null) return;
        dFragment.dismiss();
    };


    /**
     * 构建器
     *
     * @param fm 承担业务的FragmentManager
     */
    public ImageDialogHelper(@NonNull FragmentManager fm, Context context) {
        mFManager = fm;
        mCtx = context;
    }

    public ImageDialogHelper(@NonNull Fragment fragment) {
        this(fragment.getChildFragmentManager(), fragment.getContext());
    }

    public ImageDialogHelper(@NonNull AppCompatActivity activity) {
        this(activity.getSupportFragmentManager(), activity);
    }

    /**
     * 设置Style
     *
     * @author tong.xw 2020/12/25 11:48
     */
    public void setStyle(@StyleRes int styleRes) {
        this.styleRes = styleRes;
    }

    public void show(@NonNull Bitmap bitmap) {
        if (dFragment != null) dFragment.dismiss();
        dFragment = createFragment(bitmap);
        dFragment.show(mFManager, FRAGMENT_TAG);
    }

    public void show(@NonNull Drawable drawable) {
        if (dFragment != null) dFragment.dismiss();
        dFragment = createFragment(drawable);
        dFragment.show(mFManager, FRAGMENT_TAG);
    }

    /**
     * 是否在显示
     *
     * @author tong.xw 2020/12/25 11:56
     */
    public boolean isShowing() {
        return dFragment != null && dFragment.isVisible();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法
    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    private DialogFragment createFragment(@NonNull Drawable drawable) {
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        ImageView imageView = new AutoAdjustImageView(mCtx);
        imageView.setLayoutParams(params);
        imageView.setImageDrawable(drawable);
        imageView.setOnClickListener(onImageClickListener);
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx, styleRes);
        builder.setView(imageView).setCancelable(true);
        DialogFragment df = new CustomDialogFragment(builder.create());
        df.setCancelable(false);
        return df;
    }

    @NonNull
    private DialogFragment createFragment(@NonNull Bitmap bitmap) {
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        ImageView imageView = new AutoAdjustImageView(mCtx);
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(onImageClickListener);
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx, styleRes);
        builder.setView(imageView).setCancelable(true);
        DialogFragment df = new CustomDialogFragment(builder.create());
        df.setCancelable(false);
        return df;
    }
}

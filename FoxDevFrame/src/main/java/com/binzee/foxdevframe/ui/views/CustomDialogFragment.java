package com.binzee.foxdevframe.ui.views;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


/**
 * 自定义弹窗碎片
 *
 * 通过Builder类似方法进行构造
 * 内部持有一个AlertDialog.Builder
 */
public class CustomDialogFragment extends DialogFragment {
    private static final String TAG = "CustomDialogFragment";
    private final Dialog mDialog;

    public CustomDialogFragment(@NonNull Dialog dialog) {
        mDialog = dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return mDialog;
    }

    @NonNull
    @Override
    public Dialog getDialog() {
        return mDialog;
    }
}

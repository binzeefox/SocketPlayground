package com.binzee.foxdevframe.ui.tools;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.binzee.foxdevframe.R;
import com.binzee.foxdevframe.utils.ThreadUtils;

import java.util.Hashtable;

/**
 * 视图工具
 *
 * @author 狐彻
 * 2020/10/24 10:06
 */
public class ViewUtil {
    //目标View
    @NonNull
    private final View target;

    /**
     * 构造器
     *
     * @author 狐彻 2020/10/24 10:08
     */
    protected ViewUtil(@NonNull View view) {
        this.target = view;
    }

    /**
     * 静态获取
     *
     * @author 狐彻 2020/10/24 10:09
     */
    public static ViewUtil with(@NonNull View view) {
        return new ViewUtil(view);
    }

    /**
     * 通过activity和targetID获取
     *
     * @author 狐彻 2020/10/24 10:38
     */
    public static ViewUtil with(@NonNull Activity activity, @IdRes int targetId) {
        View view = activity.findViewById(targetId);
        if (view == null) throw new TargetIsNullException();
        return with(view);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 工具方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 返回目标View
     *
     * @author 狐彻 2020/10/24 10:31
     */
    @NonNull
    public View getTarget() {
        return target;
    }

    /**
     * 将当前目标转为其子节点
     *
     * @author 狐彻 2020/10/24 10:30
     */
    public ViewUtil switchToChild(@IdRes int viewId) {
        View view = target.findViewById(viewId);
        if (view == null) throw new TargetIsNullException();
        return with(view);
    }

    /**
     * 设置二次点击监听器
     *
     * @param listener  二次点击监听器
     * @param period    第二次点击间隔
     * @author 狐彻 2020/11/06 16:31
     */
    public void setOnClickTwiceClickListener(@NonNull OnClickTwiceListener listener, long period) {
        target.setOnClickListener(new View.OnClickListener() {
            long disableTime = 0;

            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() < disableTime) {
                    //第二次点击
                    disableTime = 0;
                    listener.onSecondClick(v);
                } else {
                    disableTime = System.currentTimeMillis() + period;
                    listener.onFirstClick(v);
                }
            }
        });
    }

    /**
     * 添加防抖点击监听
     *
     * @author 狐彻 2020/10/24 10:07
     */
    public void setOnClickListenerDebounce(View.OnClickListener listener, long skip) {
        target.setOnClickListener(new View.OnClickListener() {
            final long _skip = skip;

            @Override
            public void onClick(View v) {
                long curTimeStamp = System.currentTimeMillis();
                Long tagTimeStamp = (Long) v.getTag(R.id.view_util_debounce_id);
                if (tagTimeStamp == null || tagTimeStamp + _skip < curTimeStamp) {
                    v.setTag(R.id.view_util_debounce_id, curTimeStamp);
                    listener.onClick(v);
                }
            }
        });
    }

    /**
     * 开启disco模式
     *
     * @author 狐彻 2020/11/12 15:06
     */
    public void setDiscoMode() {
        HandlerThread thread = new HandlerThread("view_util_disco");
        thread.start();
        new Handler(thread.getLooper()).post(() -> {
            final int[] r = new int[]{0, 1};
            final int[] g = new int[]{0, 10};
            final int[] b = new int[]{0, 20};

            while (true) {
                try {
                    Thread.sleep(5);
                    discoModeValueFnc(r);
                    discoModeValueFnc(g);
                    discoModeValueFnc(b);

                    ThreadUtils.runOnUiThread(() -> {
                        int colorValue = Color.argb(255, r[0], g[0], b[0]);
                        target.setBackgroundColor(colorValue);
                    });
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // TextView及其子类相关
    ///////////////////////////////////////////////////////////////////////////

    public void setError(CharSequence error) {
        setError(error, null);
    }

    /**
     * 设置错误状态
     *
     * @author 狐彻 2020/10/24 10:42
     */
    public void setError(CharSequence error, Drawable errorIcon) {
        if (!isTextView()) return;
        if (errorIcon != null)
            ((TextView) target).setError(error, errorIcon);
        else ((TextView) target).setError(error);
    }

    /**
     * 获取文字
     *
     * @return 若非TextView子类则返回空字符串
     * @author 狐彻 2020/10/24 10:41
     */
    public CharSequence getText() {
        if (!isTextView()) return "";
        return ((TextView) target).getText();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 类型判断
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 判断是否是TextView子类
     *
     * @author 狐彻 2020/10/24 10:44
     */
    public boolean isTextView() {
        return target instanceof TextView;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * disco模式工具方法
     *
     * @author 狐彻 2020/11/12 15:05
     */
    private void discoModeValueFnc(int[] values) {
        values[0] += values[1];
        if (values[0] > 255) {
            values[0] = 255;
            values[1] *= -1;
        }
        if (values[0] < 0) {
            values[0] = 0;
            values[1] *= -1;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 二次点击监听器
     *
     * @author 狐彻 2020/11/06 16:30
     */
    public interface OnClickTwiceListener {

        /**
         * 第一次点击
         *
         * @author 狐彻 2020/11/06 16:31
         */
        void onFirstClick(View view);

        /**
         * 第二次点击
         *
         * @author 狐彻 2020/11/06 16:31
         */
        void onSecondClick(View view);
    }

    /**
     * 空目标异常
     *
     * @author 狐彻 2020/10/24 10:36
     */
    private static class TargetIsNullException extends RuntimeException {
        TargetIsNullException() {
            super("目标View为空");
        }
    }
}

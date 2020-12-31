package com.binzee.foxdevframe.utils.device.resource;

import com.binzee.foxdevframe.FoxCore;

/**
 * 数值工具类
 *
 * @author 狐彻
 * 2020/10/21 11:13
 */
public class DimenUtil {

    /**
     * 获取乘数
     *
     * @author 狐彻 2020/10/21 11:15
     */
    public static float getDensity() {
        return FoxCore.getApplication()
                .getResources().getDisplayMetrics().density;
    }

    /**
     * dp转px
     *
     * @author 狐彻 2020/10/21 11:17
     */
    public static int dipToPx(int dipValue) {
        return (int) (dipValue * getDensity() + 0.5f);
    }

    /**
     * px转dp
     *
     * @author 狐彻 2020/10/21 11:17
     */
    public static int pxToDip(int pxValue) {
        return (int) (pxValue / getDensity() + 0.5f);
    }
}

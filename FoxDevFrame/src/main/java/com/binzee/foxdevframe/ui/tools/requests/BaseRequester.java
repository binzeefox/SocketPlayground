package com.binzee.foxdevframe.ui.tools.requests;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * 利用无页面Fragment请求数据的类
 *
 * @author 狐彻
 * 2020/10/21 16:49
 */
public abstract class BaseRequester {

    // 内部碎片
    // @author 狐彻 2020/10/21 16:59
    private final Fragment mFragment;

    /**
     * 构造器
     *
     * @param fragmentManager 碎片管理器
     * @author 狐彻 2020/10/21 16:51
     */
    public BaseRequester(FragmentManager fragmentManager) {
        Fragment fragment = fragmentManager.findFragmentByTag(getFragmentTag());
        if (fragment != null) {
            mFragment = fragment;
            return;
        }

        // fragment为空，创建新Fragment
        mFragment = createFragment();
        fragmentManager.beginTransaction()
                .add(mFragment, getFragmentTag())
                .commitNow();
    }

    /**
     * 获取子类的无视图Fragment的标签
     *
     * @author 狐彻 2020/10/21 16:53
     */
    @NonNull
    public abstract String getFragmentTag();

    /**
     * 创建内部Fragment
     *
     * @author 狐彻 2020/10/21 16:56
     */
    @NonNull
    protected abstract Fragment createFragment();

    /**
     * 获取碎片
     *
     * @author 狐彻 2020/10/21 16:59
     */
    protected Fragment getFragment() {
        return mFragment;
    }
}

package com.binzee.foxdevframe.ui;

/**
 * 懒加载碎片基类
 *
 * @author 狐彻
 * 2020/10/21 11:29
 */
public abstract class FoxLazyFragment extends FoxFragment {
    private boolean isLoaded = false;   //加载标志

    @Override
    public void onResume() {
        super.onResume();
        if (!isLoaded) {
            onLoad();
            isLoaded = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //若不将其在此置false，onDestroy后可能类没有被回收，而导致再次加载时会失败
        isLoaded = false;
    }

    /**
     * 手动设置该Fragment的加载状态
     *
     * @author 狐彻 2020/10/21 11:35
     */
    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    /**
     * 当 isLoaded为false时该页面可见会自动调用，然后将isLoaded改为true
     *
     * @author 狐彻 2020/10/21 11:34
     */
    protected abstract void onLoad();
}

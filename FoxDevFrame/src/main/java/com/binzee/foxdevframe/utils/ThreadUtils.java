package com.binzee.foxdevframe.utils;

// 线程池基础
//
// corePoolSize: 核心线程数，默认情况下会在线程池中一直存活
// maximumPoolSize: 最大线程数，当活跃线程到达该数目时，后续线程进入队列等待
// keepAliveTime: 非核心线程闲置超时，超时后，闲置的非核心线程将被回收
// workQueue: 任务队列，储存线程
//
// FixedThreadPool: 固定线程数，只有核心线程
// CachedThreadPool: 非固定线程数，只有非核心线程
// ScheduledThreadPool: 核心线程数固定，非核心线程数无限制，常用于执行定时任务和又周期性的任务
// SingleThreadPool: 只有一个核心线程，确保所有任务都在统一线程按顺序执行


import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Thread工具
 *
 * @author 狐彻
 * 2020/10/27 9:15
 */
public class ThreadUtils {
    private static ThreadUtils sInstance;   //单例
    private ConcurrentUtil mIOUtil;    //IO线程池
    private ConcurrentUtil mComputationUtil;   //计算线程池
    private final Map<String, ConcurrentUtil> mOtherUtilMap
            = new ConcurrentHashMap<>();  //其它线程池工具

    /**
     * 单例获取
     *
     * @author 狐彻 2020/10/27 9:24
     */
    public static ThreadUtils get() {
        if (sInstance == null) {
            synchronized (ThreadUtils.class) {
                if (sInstance == null)
                    sInstance = new ThreadUtils();
                return sInstance;
            }
        } else return sInstance;
    }

    /**
     * 主线程运行
     *
     * @author 狐彻 2020/10/27 9:28
     */
    public static void runOnUiThread(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }


    ///////////////////////////////////////////////////////////////////////////
    // 业务方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 在IO线程池内工作
     *
     * @author 狐彻 2020/10/27 9:45
     */
    public void executeIO(Runnable work) {
        if (mIOUtil == null) initIOExecutor();
        mIOUtil.execute(work);
    }

    /**
     * IO线程加载数据
     *
     * @author 狐彻 2020/10/27 17:10
     */
    public <T> void callIO(Callable<T> callable, ConcurrentUtil.FutureCallback<T> callback) {
        if (mIOUtil == null) initIOExecutor();
        mIOUtil.call(callable, callback);
    }

    /**
     * 在计算线程池内工作
     *
     * @author 狐彻 2020/10/27 9:46
     */
    public void executeComputation(Runnable work) {
        if (mComputationUtil == null) initComputationExecutor();
        mComputationUtil.execute(work);
    }

    /**
     * Computation线程加载数据
     *
     * @author 狐彻 2020/10/27 17:10
     */
    public <T> void callComputation(Callable<T> callable, ConcurrentUtil.FutureCallback<T> callback) {
        if (mComputationUtil == null) initComputationExecutor();
        mComputationUtil.call(callable, callback);
    }


    /**
     * 在新线程池工作
     *
     * @param defaultExecutor 默认线程池，若tag下无线程池则使用该线程池。若为空则默认为CachedThreadPool
     * @author 狐彻 2020/10/27 9:47
     */
    public void executeOther(@NonNull String tag, Runnable runnable, ExecutorService defaultExecutor) {
        ConcurrentUtil util = getOtherExecutor(tag, defaultExecutor);
        util.execute(runnable);
    }

    /**
     * 在新线程池工作
     *
     * @author 狐彻 2020/10/27 9:47
     */
    public void executeOther(@NonNull String tag, Runnable runnable) {
        executeOther(tag, runnable, null);
    }

    /**
     * 新线程池请求
     *
     * @author tong.xw 2020/12/24 16:07
     */
    public <T> void callOther(@NonNull String tag, Callable<T> callable, ExecutorService defaultExecutor
            , ConcurrentUtil.FutureCallback<T> callback) {
        ConcurrentUtil util = getOtherExecutor(tag, defaultExecutor);
        util.call(callable, callback);
    }

    /**
     * 新线程池请求
     *
     * @author tong.xw 2020/12/24 16:07
     */
    public <T> void callOther(@NonNull String tag, Callable<T> callable, ConcurrentUtil.FutureCallback<T> callback) {
        callOther(tag, callable, null, callback);
    }

    /**
     * 关闭线程池
     *
     * @author 狐彻 2020/10/27 9:32
     */
    public void shutdownExecutor(@NonNull String tag) {
        ConcurrentUtil util = mOtherUtilMap.get(tag);
        if (util == null) return;
        util.shutdown();
        mOtherUtilMap.remove(tag);
    }

    /**
     * 立即关闭线程池
     *
     * @author tong.xw 2020/12/24 16:16
     */
    public void shutdownExecutorNow(@NonNull String tag) {
        ConcurrentUtil util = mOtherUtilMap.get(tag);
        if (util == null) return;
        util.shutdownNow();
        mOtherUtilMap.remove(tag);
    }

    /**
     * 立即回收所有任务
     *
     * @author tong.xw 2020/12/24 16:18
     */
    public void shutdownAllNow() {
        for (ConcurrentUtil util: mOtherUtilMap.values()) {
            util.shutdownNow();
        }
        mOtherUtilMap.clear();
        mIOUtil.shutdownNow();
        mComputationUtil.shutdownNow();
        mIOUtil = null;
        mComputationUtil = null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 初始化IO线程池
     *
     * @author 狐彻 2020/10/27 9:36
     */
    protected void initIOExecutor() {
        ExecutorService service = new ThreadPoolExecutor(1, Integer.MAX_VALUE
                , 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());  //一个常驻线程，无限个缓存线程
        mIOUtil = new ConcurrentUtil(service);
    }

    /**
     * 初始化计算线程池
     *
     * @author 狐彻 2020/10/27 9:36
     */
    protected void initComputationExecutor() {
        ExecutorService service = Executors.newCachedThreadPool();  //缓存线程池
        mComputationUtil = new ConcurrentUtil(service);
    }

    /**
     * 获取/添加新线程
     *
     * @param tag 标签
     * @param defaultExecutor 若该标签下没有线程池实例，则以此创建新线程池。若为空则用CachedThreadPool
     * @author 狐彻 2020/10/27 9:41
     */
    @NonNull
    protected ConcurrentUtil getOtherExecutor(String tag, ExecutorService defaultExecutor) {
        ConcurrentUtil util = mOtherUtilMap.get(tag);
        if (util != null) return util;
        if (defaultExecutor == null) util = new ConcurrentUtil(Executors.newCachedThreadPool());
        else util = new ConcurrentUtil(defaultExecutor);
        mOtherUtilMap.put(tag, util);
        return util;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 接口
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Invoke方法回调
     *
     * @author 狐彻 2020/10/27 17:06
     */
    public interface OnFutureResultListener<T> {
        void onResult(T result);
        void onError(Throwable throwable);
        void onComplete();
    }
}

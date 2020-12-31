package com.binzee.foxdevframe.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 异步工具类
 *
 * @author tong.xw
 * 2020/12/24 15:39
 */
public class ConcurrentUtil {
    private final ExecutorService mExecutor;

    /**
     * 构造器
     *
     * @param executorService 用于实现功能的线程池
     */
    public ConcurrentUtil(ExecutorService executorService) {
        this.mExecutor = executorService;
    }

    /**
     * 线程运行
     */
    public void execute(Runnable runnable) {
        mExecutor.execute(runnable);
    }

    /**
     * 线程请求
     *
     * @param <T>      请求的数据类型
     * @param callable 请求体
     * @param callback 请求回调
     */
    public <T> void call(Callable<T> callable, FutureCallback<T> callback) {
        execute(() -> {
            try {
                Future<T> future = new FutureTask<>(callable);
                callback.onStart(future);
                T t = future.get();
                callback.onCallback(t);
            } catch (Exception e) {
                callback.onError(e);
            } finally {
                callback.onFinish();
            }
        });
    }


    /**
     * 是否已经回收
     */
    public boolean isRecycled() {
        return mExecutor.isShutdown() || mExecutor.isTerminated();
    }

    /**
     * 完成所有任务并回收
     */
    public void shutdown() {
        mExecutor.shutdown();
    }

    /**
     * 立即回收并结束所有任务
     */
    public void shutdownNow() {
        mExecutor.shutdownNow();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 接口
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 请求回调
     *
     * @param <T> 请求的数据类型
     * @author tong.xw 2020/12/24 15:52
     */
    public interface FutureCallback<T> {

        /**
         * 请求开始
         *
         * @param future 用于追踪情况和终止任务
         */
        void onStart(Future<T> future);

        /**
         * 请求完成
         *
         * @param t 返回结果
         */
        void onCallback(T t);

        /**
         * 请求异常
         *
         * @param t 异常
         */
        void onError(Throwable t);

        /**
         * 请求结束
         */
        void onFinish();
    }
}

package com.jiulongteng.pipeline;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: 九龙藤
 * @CreateDate: 2020/2/6 下午5:43.
 * @Description:
 * @UpdateUser:
 * @UpdateDate: 2020/2/6 下午5:43.
 * @UpdateRemark:
 */
public class DefaultExecutor implements Executor {

    private static final int DEFAULT_CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() + 1;
    private ThreadFactory mThreadFactory;
    private ExecutorService mExecutorService;


    public DefaultExecutor(ThreadFactory threadFactory) {
        this.mThreadFactory = threadFactory;
    }



    private void initExecutorService() {
        if (mExecutorService == null) {
            mExecutorService = getDefaultExecutor();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        initExecutorService();
        mExecutorService.execute(runnable);

    }

    private ExecutorService getDefaultExecutor() {
        int configCoreSize = PipeLine.getInstance().getPipeLineConfig().getCoreThreadNum();
        if(configCoreSize <= 0){
            configCoreSize = DEFAULT_CORE_POOL_SIZE;
        }
        ThreadPoolExecutor executor = new ThreadPoolExecutor(0, configCoreSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                getThreadFactory());
        executor.allowCoreThreadTimeOut(true);

        return executor;
    }

    private ThreadFactory getThreadFactory() {
        if (mThreadFactory == null) {
            return getDefaultThreadFactory();
        }
        return mThreadFactory;
    }

    private ThreadFactory getDefaultThreadFactory() {
        ThreadFactory defaultFactory = new ThreadFactory() {
            private final AtomicLong mCount = new AtomicLong(1);

            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "PipeLine Thread #" + mCount.getAndIncrement());
                thread.setDaemon(true);
                thread.setPriority(PipeLine.getInstance().getPipeLineConfig().getPriority());
                return thread;
            }
        };

        return defaultFactory;
    }

}

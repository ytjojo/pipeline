package com.jiulongteng.pipeline;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;


/**
 * @Author: 九龙藤
 * @CreateDate: 2020/2/4 上午9:35.
 * @Description:
 * @UpdateUser:
 * @UpdateDate: 2020/2/4 上午9:35.
 * @UpdateRemark:
 */
public class PipeLineConfig {

    private int mCoreThreadNum = -1 ;
    private ThreadFactory mThreadFactory;
    private ExecutorService mExecutorService;
    private Executor mExecutor;
    /**
     * 和主线程保持一直
     */
    private int mThreadPriority = Thread.NORM_PRIORITY;

    public int getCoreThreadNum() {
        return mCoreThreadNum;
    }

    public void setCoreThreadNum(int mCoreThreadNum) {
        this.mCoreThreadNum = mCoreThreadNum;
    }

    public ThreadFactory getThreadFactory() {
        return mThreadFactory;
    }

    public void setThreadFactory(ThreadFactory mThreadFactory) {
        this.mThreadFactory = mThreadFactory;
    }

    public ExecutorService getExecutorService() {
        return mExecutorService;
    }

    public void setExecutorService(ExecutorService mExecutorService) {
        this.mExecutorService = mExecutorService;
    }

    public Executor getExecutor() {
        return mExecutor;
    }

    public void setExecutor(Executor mExecutor) {
        this.mExecutor = mExecutor;
    }

    public int  getPriority(){
        return mThreadPriority;
    }
}

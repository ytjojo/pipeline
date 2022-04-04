package com.jiulongteng.pipeline;

import java.util.concurrent.Executor;
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

    private int mCoreThreadNum = -1;
    private ThreadFactory mThreadFactory;

    private Executor mExecutor;

    private MainThreadExecutor mainThreadExecutor;
    private boolean keepTaskInstance = false;

    private IPipeLineMonitor mIPipeLineMonitor;



    public void setThreadFactory(ThreadFactory mThreadFactory) {
        this.mThreadFactory = mThreadFactory;
    }



    public Executor getExecutor() {
        return mExecutor;
    }

    public void setExecutor(Executor mExecutor) {
        this.mExecutor = mExecutor;
    }



    public IPipeLineMonitor getPipeLineMonitor() {
        return mIPipeLineMonitor;
    }

    public MainThreadExecutor getMainThreadExecutor() {
        return mainThreadExecutor;
    }

    public void setKeepTaskInstance(boolean keepTaskInstance) {
        this.keepTaskInstance = keepTaskInstance;
    }

    public void setPipeLineMonitor(IPipeLineMonitor IPipeLineMonitor) {
        mIPipeLineMonitor = IPipeLineMonitor;
    }

    public void setMainThreadExecutor(MainThreadExecutor mainThreadExecutor) {
        this.mainThreadExecutor = mainThreadExecutor;
    }


    public static class Builder {
        PipeLineConfig mConfig;

        public Builder() {
            mConfig = new PipeLineConfig();

        }



        public Builder setThreadFactory(ThreadFactory mThreadFactory) {
            mConfig.setThreadFactory(mThreadFactory);
            return this;
        }




        public Builder setExecutor(Executor mExecutor) {
            mConfig.setExecutor(mExecutor);
            return this;
        }

        public Builder setPipeLineMonitor(IPipeLineMonitor pipeLineMonitor){
            mConfig.setPipeLineMonitor(pipeLineMonitor);
            return this;
        }


        public Builder setKeepTaskInstance(boolean keepTaskInstance) {
            mConfig.setKeepTaskInstance(keepTaskInstance);
            return this;
        }

        public Builder setMainThreadExecutor(MainThreadExecutor mainThreadExecutor) {
            mConfig.setMainThreadExecutor(mainThreadExecutor);
            return this;
        }


        public PipeLineConfig build(){
            return mConfig;
        }


    }
}

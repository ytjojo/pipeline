package com.jiulongteng.pipeline;

import android.os.Handler;
import android.os.Looper;
import com.jiulongteng.pipeline.dispatcher.DefaultTaskDispatcher;
import com.jiulongteng.pipeline.task.ITaskAction;
import com.jiulongteng.pipeline.dispatcher.ITaskDispatcher;
import com.jiulongteng.pipeline.task.ITaskFactory;
import com.jiulongteng.pipeline.task.TaskCenter;
import com.jiulongteng.pipeline.graph.TaskGraph;
import java.util.concurrent.Executor;

/**
 * @Author: 九龙藤
 * @CreateDate: 2020/1/22 上午9:57.
 * @Description:
 * @UpdateUser:
 * @UpdateDate: 2020/1/22 上午9:57.
 * @UpdateRemark:
 */
public class PipeLine {
    private static PipeLine sPipeLine;

    private MainThreadExecutor mMainThreadExecutor;

    private Executor mExecutor;
    private PipeLineConfig mPipeLineConfig;

    private IPipeLineMonitor mPipeLineMonitor;

    private TaskGraph mTaskGraph;

    private ITaskDispatcher mTaskDispatcher;
    private TaskCenter mTaskCenter;

    private PipeLine() {
        mTaskCenter = new TaskCenter();
        mTaskGraph = new TaskGraph();
        mTaskDispatcher = new DefaultTaskDispatcher(mTaskCenter, mTaskGraph);
    }

    public void config(PipeLineConfig pipeLineConfig) {
        this.mPipeLineConfig = pipeLineConfig;
        this.mExecutor = mPipeLineConfig.getExecutor();
        this.mMainThreadExecutor = mPipeLineConfig.getMainThreadExecutor();
        this.mPipeLineMonitor = mPipeLineConfig.getPipeLineMonitor();
        checkInit();

    }

    private void checkInit() {
        if (mMainThreadExecutor == null) {
            mMainThreadExecutor = new MainThreadExecutor() {
                @Override
                public boolean isMainThread() {
                    return Looper.getMainLooper() == Looper.myLooper();
                }

                Handler mHandler = new Handler(Looper.getMainLooper());

                @Override
                public void execute(Runnable runnable) {
                    mHandler.post(runnable);
                }
            };
        }

        if (mExecutor == null) {
            mExecutor = new DefaultExecutor();
        }

    }

    public static PipeLine getInstance() {
        if (sPipeLine == null) {
            synchronized (PipeLine.class) {
                if (sPipeLine == null) {
                    sPipeLine = new PipeLine();
                }
            }
        }
        return sPipeLine;
    }



    public void runOnUiThread(Runnable runnable) {
        mMainThreadExecutor.execute(runnable);
    }

    public void runOnBackground(Runnable runnable) {
        if (mExecutor == null) {
            mExecutor = new DefaultExecutor();
        }
        mExecutor.execute(runnable);
    }


    public void start() {
        checkInit();
        mTaskDispatcher.startBooster();
    }


    public PipeLineConfig getPipeLineConfig() {
        return mPipeLineConfig;
    }



    public IPipeLineMonitor getPipeLineMonitor() {
        if (mPipeLineMonitor == null) {
            mPipeLineMonitor = new PipeLineMonitor();
        }
        return mPipeLineMonitor;
    }

    public TaskGraph getTaskGraph() {
        return mTaskGraph;
    }

    public TaskCenter getTaskCenter() {
        return mTaskCenter;
    }

    public MainThreadExecutor getMainThreadExecutor() {
        return mMainThreadExecutor;
    }

    public ITaskDispatcher getTaskDispatcher() {
        return mTaskDispatcher;
    }


    public PipeLine putTaskFactor(String group, ITaskFactory factory){
        getTaskCenter().putFactory(group,factory);
        getTaskCenter().notifyTaskFactoryAdded(factory);
        return this;
    }

    public PipeLine putTaskFactory(String taskName,ITaskFactory factory){
        getTaskCenter().putFactory(taskName,factory);
        getTaskCenter().notifyTaskFactoryAdded(factory);
        return this;
    }

    public PipeLine addTaskFactory(ITaskFactory factory){
        getTaskCenter().addTaskFactory(factory);
        getTaskCenter().notifyTaskFactoryAdded(factory);
        return this;
    }

    public PipeLine addTask(ITaskAction taskAction){
        getTaskCenter().addTaskAction(taskAction);
        getTaskCenter().notifyTaskAdded(taskAction);
        return this;
    }

}

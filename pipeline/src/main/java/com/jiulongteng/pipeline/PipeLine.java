package com.jiulongteng.pipeline;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.jiulongteng.pipeline.graph.DefaultTaskDispatcher;
import com.jiulongteng.pipeline.graph.ITaskDispatcher;
import com.jiulongteng.pipeline.graph.TaskCenter;
import com.jiulongteng.pipeline.graph.TaskGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
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

    private Handler mMainHandler;

    private Executor mExecutor;
    private PipeLineConfig mPipeLineConfig;

    private PipeLineMonitor mPipeLineMonitor;

    private TaskGraph mTaskGraph;

    private ITaskDispatcher mTaskDispatcher;
    private TaskCenter mTaskCenter;

    private PipeLine() {
        mMainHandler = new Handler(Looper.getMainLooper());
        mTaskCenter = new TaskCenter();
        mTaskGraph = new TaskGraph();
        mTaskDispatcher = new DefaultTaskDispatcher(mTaskCenter, mTaskGraph);
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

    public void setPipeLineConfig(PipeLineConfig pipeLineConfig) {
        this.mPipeLineConfig = pipeLineConfig;
    }

    public void setExecutor(Executor executor) {
        this.mExecutor = executor;
    }


    public Handler getMainHandler() {
        return mMainHandler;
    }

    public void runOnUiThread(Runnable runnable) {
        mMainHandler.post(runnable);
    }

    public void runOnBackground(Runnable runnable) {
        if (mExecutor == null) {
            mExecutor = new DefaultExecutor(getPipeLineConfig().getThreadFactory());
        }
        mExecutor.execute(runnable);
    }


    public void start() {
        mTaskDispatcher.startBoost();
    }


    public PipeLineConfig getPipeLineConfig() {
        return mPipeLineConfig;
    }


    public void setPipeLineMonitor(PipeLineMonitor monitor) {
        this.mPipeLineMonitor = monitor;
    }

    public PipeLineMonitor getPipeLineMonitor() {
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

    //    public static class Deployer {
//        Stage currentStage;
//
//        public Deployer(Stage stage) {
//            currentStage = stage;
//        }
//
//        public Deployer after(String stageName) {
//            Stage target = PipeLine.getInstance().getStage(stageName);
//
//            currentStage.addParentTask(target);
//            target.addCompleteChildTask(currentStage);
//            final int deep = target.mDeep + 1;
//            if(currentStage.mDeep < deep){
//                currentStage.mDeep = deep;
//            }
//            return this;
//
//        }
//
//        public Deployer with(String stageName) {
//            Stage target = PipeLine.getInstance().getStage(stageName);
//            SiblingsTaskHolder holder = target.getSiblingsTaskHolder();
//            if (holder == null) {
//                holder = new SiblingsTaskHolder();
//                holder.mSiblingsTasks = new ArrayList<>();
//            }
//            if (!holder.mSiblingsTasks.contains(target)) {
//                holder.mSiblingsTasks.add(target);
//            }
//            if (!holder.mSiblingsTasks.contains(currentStage)) {
//                holder.mSiblingsTasks.add(currentStage);
//            }
//            currentStage.setSiblingsTask(holder);
//            currentStage.mDeep = target.mDeep;
//            return this;
//        }
//
//        public Deployer afterOnce(String stageName) {
//            Stage target = PipeLine.getInstance().getStage(stageName);
//            currentStage.addOnceOrParentTask(target);
//            target.addCompleteChildTask(currentStage);
//            final int deep = target.mDeep + 1;
//            if(currentStage.mDeep < deep){
//                currentStage.mDeep = deep;
//            }
//            return this;
//        }
//        public Deployer deploy(){
//            PipeLine.getInstance().addStageInternal(currentStage);
//            return this;
//        }
//
//
//    }
//    public static Deployer getDeployer(Stage stage){
//        return new Deployer(stage);
//    }

}

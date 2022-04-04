package com.jiulongteng.pipeline.task;

import com.jiulongteng.pipeline.PipeLine;
import com.jiulongteng.pipeline.dispatcher.ITaskDispatcher;
import com.jiulongteng.pipeline.graph.Stage;

import java.util.LinkedList;


/**
 * @des:
 * @author: Administrator
 * @createDate: 2022/3/31 0031 09:33
 * @version: 3.3.2
 * @updateDate:
 * @updateUser:
 * @updateRemark:
 * @see {@link }
 */
public abstract class AbstractTaskAction implements ITaskAction {


//    private int mThreadPriority = PipeLine.getInstance().getPipeLineConfig().getPriority();

    /**
     * 任务调度执行任务的线程类型
     * 默认后台线程
     */
    private int mDispatcherType = DISPATCHER_MAIN | DISPATCHER_ENQUEUE;

    private volatile byte mTaskState;

    private boolean canRepeat;

    protected String mName;


    private Runnable mInternalRunnable;

    protected Stage mStage;

    private ITaskDispatcher mTaskDispatcher;

    private long mStartTime;
    private LinkedList<TaskListener> mTaskListeners;

    private boolean isAutoCallComplete;

    public AbstractTaskAction(String name, int dispatcherType, boolean isAutoCallComplete) {
        this.mName = name;
        this.mDispatcherType = dispatcherType;
        this.isAutoCallComplete = isAutoCallComplete;
    }
    public AbstractTaskAction(String name, boolean isAutoCallComplete) {
        this.mName = name;
        this.isAutoCallComplete = isAutoCallComplete;
    }

    @Override
    public String getTaskName() {
        return mName;
    }

    @Override
    public void addTaskListener(TaskListener taskListener) {
        if (mTaskListeners == null) {
            mTaskListeners = new LinkedList<>();
        }
        mTaskListeners.add(taskListener);
    }

    @Override
    public void onAttachPipeLine(Stage stage) {
        this.mStage = stage;
    }

    @Override
    public void setDispatcherType(int dispatcherType) {

    }

    @Override
    public boolean start() {
        if (getTaskState() != STATE_IDLE) {
            PipeLine.getInstance().getPipeLineMonitor().startInWrongState(this, mTaskState);
            return false;
        }

        if (skipWhen()) {
            if (executeNextWhenSkip()) {
                setState(STATE_SKIP);
                mTaskDispatcher.startNext(this);
            }
            return false;
        }

        setState(STATE_PENDING);
        PipeLine.getInstance().getPipeLineMonitor().onTaskCommit(this);
        onTaskCommit();
        dispatchOnCommit();
        startInternal();
        return true;
    }

    private void dispatchOnCommit() {
        if (mTaskListeners != null) {
            for (TaskListener listener : mTaskListeners) {
                listener.onTaskCommit(this);
            }
        }
    }

    private void dispatchOnStarted() {
        if (mTaskListeners != null) {
            for (TaskListener listener : mTaskListeners) {
                listener.onStart(this);
            }
        }
    }

    private void dispatchOnCompleted() {
        if (mTaskListeners != null) {
            for (TaskListener l : mTaskListeners) {
                l.onCompleted(this);
            }
        }
    }

    private void dispatchOnSuccess() {
        if (mTaskListeners != null) {
            for (TaskListener l : mTaskListeners) {
                l.onSuccess(this);
            }
        }
    }

    private void dispatchOnFail() {
        if (mTaskListeners != null) {
            for (TaskListener l : mTaskListeners) {
                l.onFail(this);
            }
        }
    }

    private void startInternal() {
        startSiblings();

        Runnable execRunnable = new Runnable() {
            @Override
            public void run() {
//                //TODO
//                if(Looper.getMainLooper() != Looper.myLooper()){
//                    android.os.Process.setThreadPriority(mThreadPriority);
//                }


                setState(STATE_RUNNING);
                onTaskStarted();
                PipeLine.getInstance().getPipeLineMonitor().onTaskStart(AbstractTaskAction.this);
                mStartTime = now();
                if (mInternalRunnable != null) {
                    mInternalRunnable.run();
                }
                AbstractTaskAction.this.run();

                if (autoCallComplete()) {
                    setCompleted();
                }
            }
        };
        if ((mDispatcherType & DISPATCHER_UNCONFINED) == DISPATCHER_UNCONFINED) {
            if((mDispatcherType & DISPATCHER_ENQUEUE) == DISPATCHER_ENQUEUE){
                if(PipeLine.getInstance().getMainThreadExecutor().isMainThread()){
                   PipeLine.getInstance().runOnUiThread(execRunnable);
                }else {
                    PipeLine.getInstance().runOnBackground(execRunnable);
                }
            }else {
                execRunnable.run();
            }
        } else if ((mDispatcherType & DISPATCHER_IO) == DISPATCHER_IO) {
            PipeLine.getInstance().runOnBackground(execRunnable);
//            if((mDispatcherType & DISPATCHER_ENQUEUE) == DISPATCHER_ENQUEUE){
//                PipeLine.getInstance().runOnBackground(execRunnable);
//            }else {
//                if(PipeLine.getInstance().getMainThreadExecutor().isMainThread()){
//                    PipeLine.getInstance().runOnBackground(execRunnable);
//                }else {
//                    execRunnable.run();
//                }
//            }
        } else {
            if((mDispatcherType & DISPATCHER_ENQUEUE) == DISPATCHER_ENQUEUE){
                PipeLine.getInstance().runOnUiThread(execRunnable);
            }else {
                if(PipeLine.getInstance().getMainThreadExecutor().isMainThread()){
                    execRunnable.run();
                }else {
                    PipeLine.getInstance().runOnUiThread(execRunnable);
                }
            }
        }
    }

    private long now() {
        return System.currentTimeMillis();
    }


    private boolean startSiblings() {
//        if(isSiblingsTasksStarted) {
//            return false;
//        }
        //TODO

        return true;

    }


    protected void onTaskCommit() {

    }

    protected void onTaskStarted() {

    }


    protected boolean skipWhen() {

        return false;
    }

    protected boolean executeNextWhenSkip() {
        return false;
    }


    @Override
    public int getDispatcherType() {
        return mDispatcherType;
    }

    @Override
    public void setSuccess() {
        mTaskState |= STATE_SUCCESS;
        setCompleted();
        dispatchOnSuccess();
    }

    @Override
    public void setFail() {
        mTaskState |= STATE_FAIL;
        setCompleted();
        dispatchOnFail();
    }

    @Override
    public void setCompleted() {
        mTaskState |= STATE_COMPLETED;
        performComplete();
    }

    private void performComplete() {
        long finishTime = now();
        PipeLine.getInstance().getPipeLineMonitor().record(getTaskName(), finishTime - mStartTime);
        PipeLine.getInstance().getPipeLineMonitor().onTaskCompleted(this);
        onCompleted();
        dispatchOnCompleted();
        PipeLine.getInstance().getTaskDispatcher().startNext(this);


    }

    protected void onCompleted() {

    }

    @Override
    public byte getTaskState() {
        return mTaskState;
    }

    @Override
    public boolean autoCallComplete() {
        return isAutoCallComplete;
    }

    public boolean isTaskCommit() {
        return (mTaskState & STATE_PENDING) != 0;
    }

    private void setState(int state) {
        mTaskState |= state;
    }
}

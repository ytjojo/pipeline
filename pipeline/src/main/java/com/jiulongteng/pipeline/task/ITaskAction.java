package com.jiulongteng.pipeline.task;

import com.jiulongteng.pipeline.graph.Stage;

/**
 * @des:
 * @author: Administrator
 * @createDate: 2022/3/31 0031 08:56
 * @version: 3.3.2
 * @updateDate:
 * @updateUser:
 * @updateRemark:
 * @see {@link }
 */
public interface ITaskAction extends Runnable{

    /**
     * {@code Task}执行状态，{@code ITaskAction}尚未执行
     */
    byte STATE_IDLE = 0;

    /**
     * {@code Task}执行状态，{@code ITaskAction}等待执行
     */
    byte STATE_PENDING= 1;


    /**
     * {@code Task}执行状态，{@code ITaskAction}正在执行中
     */
    byte STATE_RUNNING = 2;

    /**
     * {@code Task}执行状态，{@code ITaskAction}已经执行完毕
     */
    byte STATE_SUCCESS = 4;
    /**
     * {@code Task}执行状态，{@code ITaskAction}已经执行完毕
     */
    byte STATE_FAIL= 8;

    /**
     * {@code Task}执行状态，{@code ITaskAction}已经执行完毕
     */
    byte STATE_COMPLETED = 16;

    /**
     * {@code Task}执行状态，{@code Task} 运行时被手动跳过
     */
    byte STATE_SKIP = 32;

    int DISPATCHER_ENQUEUE = 1;

    int DISPATCHER_MAIN = 2;

    int DISPATCHER_IO = 4 ;

    int DISPATCHER_IO_ENQUEUE = DISPATCHER_IO | DISPATCHER_ENQUEUE;


    int DISPATCHER_UNCONFINED = 8;



    String getTaskName();

    void addTaskListener(TaskListener taskListener);
    void onAttachPipeLine(Stage stage) ;

    public void setDispatcherType(int dispatcherType);

    boolean start() ;

    void setSuccess();
    void setFail();

    void setCompleted();

    byte getTaskState();

    int getDispatcherType();

    boolean autoCallComplete();


   interface TaskListener {
        public void onStart(ITaskAction task);
       public void onTaskCommit(ITaskAction task);
        public void onCompleted(ITaskAction task);
        public void onSuccess(ITaskAction task) ;
        public void onFail(ITaskAction task) ;
    }



}

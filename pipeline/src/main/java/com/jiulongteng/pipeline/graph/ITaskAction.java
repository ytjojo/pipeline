package com.jiulongteng.pipeline.graph;

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
    public static final byte STATE_IDLE = 0;

    /**
     * {@code Task}执行状态，{@code ITaskAction}等待执行
     */
    public static final byte STATE_PENDING= 1;


    /**
     * {@code Task}执行状态，{@code ITaskAction}正在执行中
     */
    public static final byte STATE_RUNNING = 2;

    /**
     * {@code Task}执行状态，{@code ITaskAction}已经执行完毕
     */
    public static final byte STATE_SUCCESS = 4;
    /**
     * {@code Task}执行状态，{@code ITaskAction}已经执行完毕
     */
    public static final byte STATE_FAIL= 8;

    /**
     * {@code Task}执行状态，{@code ITaskAction}已经执行完毕
     */
    public static final byte STATE_COMPLETED = 16;

    /**
     * {@code Task}执行状态，{@code Task} 运行时被手动跳过
     */
    public static final byte STATE_SKIP = 32;

    public static final int DISPATCHER_IO = 0;

    public static final int DISPATCHER_MAIN = 1;

    public static final int DISPATCHER_UNCONFINED = 2;

    String getTaskName();

    public void addTaskListener(TaskListener taskListener);
    void onAttachPipeLine(Stage stage) ;

    public void setDispatcherType(int dispatcherType);

    boolean start() ;

    void setSuccess();
    void setFail();

    void setCompleted();

    byte getTaskState();

    int getDispatcherType();

    boolean autoCallComplete();


    public static class TaskListener {
        public void onStart(ITaskAction task) {
        }

        public void onCompleted(ITaskAction task) {
        }

        public void onSuccess(ITaskAction task) {
        }

        public void onFail(ITaskAction task) {
        }

    }



}

package com.jiulongteng.pipeline;

import com.jiulongteng.pipeline.task.ITaskAction;

/**
 * @des:
 * @author: Administrator
 * @createDate: 2022/4/3 0003 21:43
 * @version: 3.3.2
 * @updateDate:
 * @updateUser:
 * @updateRemark:
 * @see {@link }
 */
public interface IPipeLineMonitor {

     void record(String taskName,long time);

     void startInWrongState(ITaskAction task, int currentState);

     void onTaskStart(ITaskAction task);

     void onTaskNotFound(String name);

     void onTaskCommit(ITaskAction task);
     void onTaskCompleted(ITaskAction task);

     void onPipeLineComplete();
}

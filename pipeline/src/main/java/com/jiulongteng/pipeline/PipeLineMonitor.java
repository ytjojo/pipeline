package com.jiulongteng.pipeline;


import com.jiulongteng.pipeline.graph.ITaskAction;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: 九龙藤
 * @CreateDate: 2020/2/7 下午5:01.
 * @Description:
 * @UpdateUser:
 * @UpdateDate: 2020/2/7 下午5:01.
 * @UpdateRemark*/
public class PipeLineMonitor {
    ConcurrentHashMap<String,Long> mExecuteTimeMap = new ConcurrentHashMap<>();


    public void record(String taskName,long time){
        mExecuteTimeMap.put(taskName,time);
    }

    public void startInWrongState(ITaskAction task, int currentState){

    }

    public void onTaskStart(ITaskAction task){

    }

    public void onTaskCommit(ITaskAction task){

    }
    public void onTaskCompleted(ITaskAction task){
    }

}

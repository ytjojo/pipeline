package com.jiulongteng.pipeline.graph;

import com.jiulongteng.pipeline.task.AbstractTaskAction;
import com.jiulongteng.pipeline.task.ITaskAction;

import java.util.LinkedHashSet;

/**
 * @des:
 * @author: Administrator
 * @createDate: 2022/3/31 0031 11:16
 * @version: 3.3.2
 * @updateDate:
 * @updateUser:
 * @updateRemark:
 * @see {@link }
 */
public class Stage extends AbstractTaskAction {

    public Stage(String name){
        this(name,false);
    }

    public Stage(String name, boolean isAutoCallComplete) {
        super(name, DISPATCHER_MAIN,false);
    }
    private String mStagePreTaskName;
    private String mStageCompleteTaskName;
    private LinkedHashSet<String> mAllStageTaskName = new LinkedHashSet<>();

    @Override
    public void run() {

    }

    

    public boolean onAttach(String taskName){
       return mAllStageTaskName.add(taskName);
    }
    public boolean onAttach(ITaskAction task){
        return mAllStageTaskName.add(task.getTaskName());
    }
}

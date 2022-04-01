package com.jiulongteng.pipeline.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @des:
 * @author: Administrator
 * @createDate: 2022/4/1 0001 13:16
 * @version: 3.3.2
 * @updateDate:
 * @updateUser:
 * @updateRemark:
 * @see {@link }
 */
public class TaskCenter {

    private ConcurrentHashMap<String,ITaskAction> taskTable = new ConcurrentHashMap<>();

    private LinkedList<ITaskFactory> mTaskFactories;
    private HashMap<String,ITaskFactory> mFactoriesMap;


    private ConcurrentHashMap<String,LinkedList<ITaskAction.TaskListener>> mTaskListenerMap;

    public boolean isTaskCreated(String name){
        return taskTable.get(name) !=null;
    }


    private TaskCenterListener mTaskCenterListener;

    public void setTaskCenterListener(TaskCenterListener listener){
        this.mTaskCenterListener = listener;
    }

    public void putFactory(String group,ITaskFactory factory){
        if(mFactoriesMap == null){
            mFactoriesMap = new HashMap<>();
        }
        mFactoriesMap.put(group,factory);
        if(mTaskCenterListener != null){
            mTaskCenterListener.onFactoryAdded(factory);
        }
    }

    public void addTaskAction(ITaskAction taskAction){
        taskTable.put(taskAction.getTaskName(),taskAction);
        if(mTaskCenterListener != null){
            mTaskCenterListener.onTaskAdded(taskAction);
        }
    }
    public void addTaskFactory(ITaskFactory factory){
        if(mTaskFactories == null){
            mTaskFactories = new LinkedList<>();
        }
        mTaskFactories.add(factory);
        if(mTaskCenterListener != null){
            mTaskCenterListener.onFactoryAdded(factory);
        }
    }



    public ITaskAction getTaskAction(String name){
        ITaskAction taskAction = taskTable.get(name);
        if(taskAction == null){
            taskAction = create(name);
            taskTable.put(name,taskAction);
        }
        return taskAction;
    }


    private ITaskAction create(String name){

        if(mFactoriesMap != null){
            ITaskFactory factory = mFactoriesMap.get(name);
            if(factory == null){
                String[] groups = name.split("/");
                if(groups != null&& groups.length >1) {
                    String group = groups[0];
                    factory = mFactoriesMap.get(group);
                }
            }
            if(factory != null){
                return factory.create(name);
            }
        }else if(mTaskFactories != null){
            for(ITaskFactory factory : mTaskFactories){
               ITaskAction taskAction =  factory.create(name);
               if(taskAction != null){
                   return taskAction;
               }
            }
        }
        return null;
    }


    public void addTaskListener(String taskName,ITaskAction.TaskListener taskListener) {
        if(mTaskCenterListener == null){
            mTaskListenerMap = new ConcurrentHashMap<>();
        }
        LinkedList<ITaskAction.TaskListener> taskListeners = mTaskListenerMap.get(taskName);
        if(taskListeners == null){
            taskListeners = new LinkedList<>();
            mTaskListenerMap.put(taskName, taskListeners);
        }
        taskListeners.add(taskListener);
    }

    public void removeTaskListener(String taskName,ITaskAction.TaskListener taskListener) {
        if(mTaskCenterListener == null){
           return;
        }
        LinkedList<ITaskAction.TaskListener> taskListeners = mTaskListenerMap.get(taskName);
        if(taskListeners == null){
            return;
        }
        taskListeners.remove(taskListener);
    }

    public interface TaskCenterListener{
        void onTaskAdded(ITaskAction iTaskAction);
        void onFactoryAdded(ITaskFactory factory);
    }
}

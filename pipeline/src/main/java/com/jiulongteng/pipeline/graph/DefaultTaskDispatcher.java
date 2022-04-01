package com.jiulongteng.pipeline.graph;

import com.jiulongteng.pipeline.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @des:
 * @author: Administrator
 * @createDate: 2022/4/1 0001 13:14
 * @version: 3.3.2
 * @updateDate:
 * @updateUser:
 * @updateRemark:
 * @see {@link }
 */
public class DefaultTaskDispatcher implements ITaskDispatcher {

    TaskCenter mTaskCenter;
    TaskGraph mTaskGraph;
    private ConcurrentHashMap<String, Byte> taskStateTable = new ConcurrentHashMap<>();

    private CopyOnWriteArrayList<String> mNotExecuteTasks = new CopyOnWriteArrayList<>();

    public DefaultTaskDispatcher(TaskCenter taskCenter, TaskGraph taskGraph) {
        this.mTaskCenter = taskCenter;
        this.mTaskGraph = taskGraph;
        mTaskCenter.setTaskCenterListener(new TaskCenter.TaskCenterListener() {
            @Override
            public void onTaskAdded(ITaskAction iTaskAction) {
                startWillTask();
            }

            @Override
            public void onFactoryAdded(ITaskFactory factory) {
                startWillTask();
            }
        });
    }

    @Override
    public void startNext(ITaskAction triggerTask) {
        String triggerTaskName  = triggerTask.getTaskName();
        byte taskState = triggerTask.getTaskState();
        GraphNode<String> triggerGraphNode = mTaskGraph.getNode(triggerTaskName);
        if((taskState & ITaskAction.STATE_FAIL) == ITaskAction.STATE_FAIL
                ||(taskState & ITaskAction.STATE_SUCCESS) == ITaskAction.STATE_SUCCESS){

           if(triggerGraphNode instanceof ConditionGraphNode){
               ConditionGraphNode<String> conditionGraphNode = (ConditionGraphNode<String>) triggerGraphNode;
               if((taskState & ITaskAction.STATE_FAIL) == ITaskAction.STATE_FAIL){
                   ArrayList<GraphNode<String>> allNodes = new ArrayList<>();
                   if(!CollectionUtils.isEmpty(conditionGraphNode.getNextNodes())){
                       allNodes.addAll(conditionGraphNode.getNextNodes());
                   }
                   ArrayList<GraphNode<String>> successNodes = new ArrayList<>();
                   if(!CollectionUtils.isEmpty(conditionGraphNode.getSuccessNextNodes())){
                       successNodes.addAll(conditionGraphNode.getSuccessNextNodes());
                   }
                   allNodes.removeAll(successNodes);
                   startCollection(allNodes);

               }else {
                   ArrayList<GraphNode<String>> allNodes = new ArrayList<>();
                   if(!CollectionUtils.isEmpty(conditionGraphNode.getNextNodes())){
                       allNodes.addAll(conditionGraphNode.getNextNodes());
                   }
                   ArrayList<GraphNode<String>> failNodes = new ArrayList<>();
                   if(!CollectionUtils.isEmpty(conditionGraphNode.getFailNextNodes())){
                       failNodes.addAll(conditionGraphNode.getFailNextNodes());
                   }
                   allNodes.removeAll(failNodes);
                   startCollection(allNodes);
               }
           }

        }else {
            startCollection(triggerGraphNode.getNextNodes());
        }

    }
    private void startCollection(Collection<GraphNode<String>> nodes){
        if (!CollectionUtils.isEmpty(nodes)) {
            for (GraphNode<String> node : nodes) {
                node.dependencyDecrement();
                if (node.isAllDependencyCompleted()) {
                    String name = node.getValue();
                    ITaskAction taskAction = mTaskCenter.getTaskAction(name);
                    startInternal(name, taskAction, true);
                }
            }
        }
    }

    @Override
    public void startBoost() {
        if (!mTaskGraph.isDAGraph()) {
            throw new IllegalStateException("found cycle dnpendence");
        }

        List<GraphNode<String>> boostNodes = mTaskGraph.getBoostNodes();
        Collections.sort(boostNodes);
        for (GraphNode<String> node : boostNodes) {
            ITaskAction taskAction = mTaskCenter.getTaskAction(node.getValue());
            startInternal(node.getValue(), taskAction, true);

        }


    }

    private boolean startInternal(String taskName, ITaskAction taskAction, boolean addNotExecute) {
        if (taskAction != null && !isTaskCommitted(taskName)) {
            putTaskState(taskName, ITaskAction.STATE_IDLE);
            taskAction.start();
            return true;
        } else {
            if (taskAction == null && addNotExecute) {
                addWillExecuteTask(taskName);
            }

        }
        return false;
    }


    public void addWillExecuteTask(String name) {
        mNotExecuteTasks.add(name);
    }

    private void startWillTask() {
        if (mNotExecuteTasks.isEmpty()) {
            return;
        }
        Iterator<String> iterator = mNotExecuteTasks.iterator();
        HashSet<String> executed = new HashSet<>();
        while (iterator.hasNext()) {
            String name = iterator.next();
            ITaskAction taskAction = mTaskCenter.getTaskAction(name);
            startInternal(name, taskAction, false);
        }
        mNotExecuteTasks.removeAll(executed);

    }


    public void putTaskState(String naem, Byte taskState) {
        taskStateTable.put(naem, taskState);
    }

    public boolean isTaskCommitted(String name) {
        Byte state = taskStateTable.get(name);
        return taskStateTable.get(name) != null && state != ITaskAction.STATE_IDLE;
    }

}

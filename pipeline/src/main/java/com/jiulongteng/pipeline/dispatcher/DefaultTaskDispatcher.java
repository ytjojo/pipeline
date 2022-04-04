package com.jiulongteng.pipeline.dispatcher;

import com.jiulongteng.pipeline.CollectionUtils;
import com.jiulongteng.pipeline.PipeLine;
import com.jiulongteng.pipeline.graph.ConditionGraphNode;
import com.jiulongteng.pipeline.graph.GraphNode;
import com.jiulongteng.pipeline.graph.TaskGraph;
import com.jiulongteng.pipeline.task.ITaskAction;
import com.jiulongteng.pipeline.dispatcher.ITaskDispatcher;
import com.jiulongteng.pipeline.task.ITaskFactory;
import com.jiulongteng.pipeline.task.TaskCenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
public class DefaultTaskDispatcher extends AtomicLong implements ITaskDispatcher {

    private TaskCenter mTaskCenter;
    private TaskGraph mTaskGraph;
    private volatile boolean isBoosterStarted;
    private ConcurrentHashMap<String, Byte> taskStateTable = new ConcurrentHashMap<>();
    private LinkedList<String> mReadyTasks = new LinkedList<>();

    private LinkedHashSet<String> mNotFoundTasks = new LinkedHashSet<>();

    public DefaultTaskDispatcher(TaskCenter taskCenter, TaskGraph taskGraph) {
        this.mTaskCenter = taskCenter;
        this.mTaskGraph = taskGraph;
        mTaskCenter.setTaskCenterListener(new TaskCenter.TaskCenterListener() {
            @Override
            public void onTaskAdded(ITaskAction iTaskAction) {
                tryStartNotFoundTask();
            }

            @Override
            public void onFactoryAdded(ITaskFactory factory) {
                tryStartNotFoundTask();
            }
        });
    }

    @Override
    public synchronized void startNext(ITaskAction triggerTask) {
        decrementAndGet();
        boolean isStartReadyQueue = mReadyTasks.isEmpty();

        String triggerTaskName = triggerTask.getTaskName();
        byte taskState = triggerTask.getTaskState();
        GraphNode<String> triggerGraphNode = mTaskGraph.getNode(triggerTaskName);
        if ((taskState & ITaskAction.STATE_FAIL) == ITaskAction.STATE_FAIL
                || (taskState & ITaskAction.STATE_SUCCESS) == ITaskAction.STATE_SUCCESS) {

            if (triggerGraphNode instanceof ConditionGraphNode) {
                ConditionGraphNode<String> conditionGraphNode = (ConditionGraphNode<String>) triggerGraphNode;
                if ((taskState & ITaskAction.STATE_FAIL) == ITaskAction.STATE_FAIL) {
                    ArrayList<GraphNode<String>> allNodes = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(conditionGraphNode.getNextNodes())) {
                        allNodes.addAll(conditionGraphNode.getNextNodes());
                    }
                    ArrayList<GraphNode<String>> successNodes = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(conditionGraphNode.getSuccessNextNodes())) {
                        successNodes.addAll(conditionGraphNode.getSuccessNextNodes());
                    }
                    allNodes.removeAll(successNodes);
                    startCollection(allNodes);

                } else {
                    ArrayList<GraphNode<String>> allNodes = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(conditionGraphNode.getNextNodes())) {
                        allNodes.addAll(conditionGraphNode.getNextNodes());
                    }
                    ArrayList<GraphNode<String>> failNodes = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(conditionGraphNode.getFailNextNodes())) {
                        failNodes.addAll(conditionGraphNode.getFailNextNodes());
                    }
                    allNodes.removeAll(failNodes);
                    startCollection(allNodes);
                }
            }

        } else {
            startCollection(new ArrayList<>(triggerGraphNode.getNextNodes()));
        }
        if (isStartReadyQueue) {
            startReadyQueue(true);
        }
        if(get() == 0){
            PipeLine.getInstance().getPipeLineMonitor().onPipeLineComplete();
        }


    }

    private void startReadyQueue(boolean addNotExecute) {
        if (mReadyTasks.isEmpty()) {
            return;
        }

        while (!mReadyTasks.isEmpty()) {
            String taskName = mReadyTasks.poll();
            ITaskAction taskAction = mTaskCenter.getTaskAction(taskName);
            startInternal(taskName, taskAction, addNotExecute);
        }
    }

    private void addToReadyQueue(String taskName) {
        mReadyTasks.offer(taskName);
    }

    private void startCollection(List<GraphNode<String>> nodes) {
        if (!CollectionUtils.isEmpty(nodes)) {
            Collections.sort(nodes);
            for (GraphNode<String> node : nodes) {
                node.dependencyDecrement();
                if (node.isAllDependencyCompleted()) {
                    String taskName = node.getValue();
                    addToReadyQueue(taskName);
                }
            }
        }
    }

    @Override
    public void startBooster() {
        if (!mTaskGraph.isDAGraph()) {
            throw new IllegalStateException("found cycle denpendence");
        }
        isBoosterStarted = true;
        List<GraphNode<String>> boostNodes = mTaskGraph.getBoostNodes();
        Collections.sort(boostNodes);
        for (GraphNode<String> node : boostNodes) {
            addToReadyQueue(node.getValue());
        }
        startReadyQueue(true);


    }

    @Override
    public boolean isBoosterStarted() {
        return isBoosterStarted;
    }

    private boolean startInternal(String taskName, ITaskAction taskAction, boolean addNotExecute) {
        if (taskAction != null && !isTaskCommitted(taskName)) {
            putTaskState(taskName, ITaskAction.STATE_IDLE);
            taskAction.start();
            incrementAndGet();
            return true;
        } else {
            if (taskAction == null && addNotExecute) {
                addNotFoundTaskNames(taskName);
                PipeLine.getInstance().getPipeLineMonitor().onTaskNotFound(taskName);
            }

        }
        return false;
    }


    public void addNotFoundTaskNames(String taskName) {
        mNotFoundTasks.add(taskName);
    }

    private synchronized void tryStartNotFoundTask() {
        if (mNotFoundTasks.isEmpty()) {
            return;
        }
        Iterator<String> iterator = mNotFoundTasks.iterator();
        HashSet<String> executed = new HashSet<>();
        while (iterator.hasNext()) {
            String name = iterator.next();
            addToReadyQueue(name);
        }

        if (mReadyTasks.isEmpty()) {
            return;
        }
        String taskName = null;
        while ((taskName = mReadyTasks.poll()) != null) {
            ITaskAction taskAction = mTaskCenter.getTaskAction(taskName);
            boolean started = startInternal(taskName, taskAction, false);
            if(started){
                executed.add(taskName);
            }
        }
        mNotFoundTasks.removeAll(executed);

    }


    public void putTaskState(String naem, Byte taskState) {
        taskStateTable.put(naem, taskState);
    }

    public boolean isTaskCommitted(String name) {
        Byte state = taskStateTable.get(name);
        return taskStateTable.get(name) != null && state != ITaskAction.STATE_IDLE;
    }

}

package com.jiulongteng.pipeline;

import com.jiulongteng.pipeline.graph.FakeGraphNode;
import com.jiulongteng.pipeline.graph.GraphNode;
import com.jiulongteng.pipeline.graph.Stage;
import com.jiulongteng.pipeline.task.AbstractTaskAction;
import com.jiulongteng.pipeline.task.ITaskAction;
import com.jiulongteng.pipeline.task.ITaskFactory;

public class Deployer {
    Stage mStage;
    PipeLine mPipeLine;
    String mTaskName;
    ITaskAction mITaskAction;
    ITaskFactory mITaskFactory;

    public Deployer(PipeLine pipeLine, String taskName) {
        this.mPipeLine = pipeLine;
        this.mTaskName = taskName;
        mPipeLine.getTaskGraph().addNode(taskName);
    }

    public Deployer dependenceOn(String dependencyName) {
        mPipeLine.getTaskGraph().putEdge(mTaskName, dependencyName);
        return this;

    }

    public Deployer dependenceOn(String... dependencyNames) {
        mPipeLine.getTaskGraph().putEdge(mTaskName, dependencyNames);
        return this;

    }

    public Deployer dependenceOnSuccess(String  dependencyName) {
        mPipeLine.getTaskGraph().putEdge(mTaskName, dependencyName);
        mPipeLine.getTaskGraph().addSuccessNext(mTaskName,dependencyName);
        return this;

    }

    public Deployer dependenceOnFail(String dependencyName) {
        mPipeLine.getTaskGraph().putEdge(mTaskName, dependencyName);
        mPipeLine.getTaskGraph().addFailNext(mTaskName,dependencyName);
        return this;

    }

    public Deployer setExecutePriority(int executePriority) {
        mPipeLine.getTaskGraph().getNode(mTaskName).setExecutePriority(executePriority);
        return this;

    }



    public Deployer with(String stageName) {

        return this;
    }

    public Deployer maybeDependenceOn(String ... dependencyNames) {
        if(dependencyNames .length < 2){
            throw new IllegalArgumentException("must more than 1");
        }
        String fakeTaskName = mTaskName+"#_fake_#";
        GraphNode<String> targetNode = mPipeLine.getTaskGraph().getNode(fakeTaskName);
        FakeGraphNode<String> fakeGraphNode = null;
        if(targetNode != null){
            fakeGraphNode = (FakeGraphNode<String>) targetNode;
        }else {
            fakeGraphNode =  new FakeGraphNode<>(fakeTaskName);
            mPipeLine.getTaskGraph().addNode(fakeGraphNode);
        }
        for (String dependenceName: dependencyNames) {
            mPipeLine.getTaskGraph().putEdge(fakeGraphNode.getValue(),dependenceName);
        }
        mPipeLine.getTaskCenter().addTaskAction(new AbstractTaskAction(mTaskName,ITaskAction.DISPATCHER_MAIN,true) {
            @Override
            public void run() {

            }
        });
        mPipeLine.getTaskGraph().putEdge(mTaskName,fakeGraphNode.getValue());
        return this;
    }
    public Deployer putGroupTaskFactor(String group,ITaskFactory factory){
        this.mITaskFactory = factory;
        mPipeLine.getTaskCenter().putFactory(group,factory);
        return this;
    }

    public Deployer putTaskFactory(String taskName,ITaskFactory factory){
        this.mITaskFactory = factory;
        mPipeLine.getTaskCenter().putFactory(taskName,factory);
        return this;
    }

    public Deployer addTaskFactory(ITaskFactory factory){
        mPipeLine.getTaskCenter().addTaskFactory(factory);
        return this;
    }

    public Deployer addTask(ITaskAction taskAction){
        this.mITaskAction = taskAction;
        mPipeLine.getTaskCenter().addTaskAction(taskAction);
        return this;
    }

    public Deployer deploy() {
        if(mITaskAction!= null){
            mPipeLine.getTaskCenter().notifyTaskAdded(mITaskAction);
        }
        if(mITaskFactory != null){
            mPipeLine.getTaskCenter().notifyTaskFactoryAdded(mITaskFactory);
        }
        return this;
    }


}
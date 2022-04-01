package com.jiulongteng.pipeline.graph;

import com.jiulongteng.pipeline.PipeLine;

public class Deployer {
    Stage currentStage;
    PipeLine mPipeLine;
    String mTaskName;

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
        mPipeLine.getTaskCenter().addTaskAction(new AbstractTaskAction(mTaskName,true) {
            @Override
            public void run() {

            }
        });
        mPipeLine.getTaskGraph().putEdge(mTaskName,fakeGraphNode.getValue());
        return this;
    }

    public Deployer deploy() {

        return this;
    }


}
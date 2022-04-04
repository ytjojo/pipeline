package com.jiulongteng.pipeline.debug;

import com.jiulongteng.pipeline.MainThreadExecutor;
import com.jiulongteng.pipeline.PipeLine;
import com.jiulongteng.pipeline.PipeLineConfig;
import com.jiulongteng.pipeline.PipeLineMonitor;
import com.jiulongteng.pipeline.Deployer;
import com.jiulongteng.pipeline.graph.GraphNode;
import com.jiulongteng.pipeline.task.ITaskAction;
import com.jiulongteng.pipeline.task.ITaskFactory;
import com.jiulongteng.pipeline.task.TaskAction;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @des:
 * @author: Administrator
 * @createDate: 2022/4/3 0003 12:38
 * @version: 3.3.2
 * @updateDate:
 * @updateUser:
 * @updateRemark:
 * @see {@link }
 */
public class JvmPipeline {

    private boolean complete;

    LinkedBlockingQueue<Runnable> mQueue = new LinkedBlockingQueue<>();
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "schedule_thread");
        }
    });
    String threadName = Thread.currentThread().getName();
    MainThreadExecutor mMainThreadExecutor = new MainThreadExecutor() {
        @Override
        public boolean isMainThread() {
            return Thread.currentThread().getName().equals(threadName);
        }

        @Override
        public void execute(Runnable runnable) {
            mQueue.add(runnable);
        }
    };

    public void init() {

        PipeLineConfig config = new PipeLineConfig.Builder()
                .setKeepTaskInstance(false)
                .setMainThreadExecutor(mMainThreadExecutor)
                .setPipeLineMonitor(new PipeLineMonitor() {
                    @Override
                    public void onTaskStart(ITaskAction task) {

                    }

                    @Override
                    public void record(String taskName, long time) {
                        System.out.println(taskName + "  cost" + time + "ms");
                    }

                    @Override
                    public void onTaskCompleted(ITaskAction task) {
                        System.out.println("onTaskCompleted" + task.getTaskName());
                    }

                    @Override
                    public void onTaskCommit(ITaskAction task) {
                        System.out.println("onTaskCommit" + task.getTaskName());
                    }

                    @Override
                    public void onPipeLineComplete() {
                        mMainThreadExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                complete = true;
                            }
                        });
                    }
                })
                .build();
        PipeLine.getInstance().config(config);
        PipeLine.getInstance().addTaskFactory(new ITaskFactory() {
            @Override
            public ITaskAction create(String name) {
                boolean autoCall = false;
                System.out.println("create" + name + " iaAutoCallComplete" + autoCall);
                final TaskAction task = new TaskAction(name, autoCall ? ITaskAction.DISPATCHER_MAIN : ITaskAction.DISPATCHER_IO, autoCall) {

                    @Override
                    public void run() {
                        TaskAction t = this;
                        System.out.println(name + "running");
                        if (!autoCall) {
                            long delay =  (long) (1000L + Math.random() * 1500L);
                            delayComplete(this,delay);
                            System.out.println(name + " 将于" + delay + "ms 后通知停止");
                        }
                    }


                };

                return task;
            }
        });
        new Deployer(PipeLine.getInstance(), "task100")

                .dependenceOn("task1", "task2").deploy();

        new Deployer(PipeLine.getInstance(), "task101")
                .dependenceOn("task100", "task2").deploy();
        new Deployer(PipeLine.getInstance(), "task102")
                .dependenceOn("task100", "task2").deploy();
        new Deployer(PipeLine.getInstance(), "task103")
                .dependenceOn("task1", "task2").deploy();

        new Deployer(PipeLine.getInstance(), "task104")
                .dependenceOn("task100", "task2", "task108").deploy();
        new Deployer(PipeLine.getInstance(), "task105")
                .dependenceOn("task1", "task2", "task3").deploy();

        new Deployer(PipeLine.getInstance(), "task106")
                .dependenceOn("task105", "task2").deploy();
        new Deployer(PipeLine.getInstance(), "task107")
                .dependenceOn("task3", "task1", "task108").deploy();

        new Deployer(PipeLine.getInstance(), "task108")
                .dependenceOn("task100", "task2").deploy();
        new Deployer(PipeLine.getInstance(), "task109")
                .dependenceOn("task100", "task1", "task102", "task2", "task103", "task104"
                        , "task105", "task106", "task107", "task108"
                ).deploy();


    }

    public void start() throws InterruptedException {

        List<GraphNode<String>> list =  PipeLine.getInstance().getTaskGraph().getTopologicalSortList();
        for (GraphNode<String> node :list){
            System.out.println(node.getValue().toString());
        }
        PipeLine.getInstance().start();
        while (true) {
            Runnable runnable = mQueue.take();
            runnable.run();
            if (complete) {
                break;
            }
        }
    }


    public void delayComplete(ITaskAction action, long delay) {
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                action.setCompleted();
                System.out.println(action.getTaskName() + "setCompleted");

            }
        }, delay, TimeUnit.MILLISECONDS);
    }
}

package com.jiulongteng.pipeline.graph;

/**
 * @des:
 * @author: Administrator
 * @createDate: 2022/3/31 0031 10:13
 * @version: 3.3.2
 * @updateDate:
 * @updateUser:
 * @updateRemark:
 * @see {@link }
 */
public interface ITaskDispatcher {


    void startNext(ITaskAction triggerTask);

    void startBoost();
}

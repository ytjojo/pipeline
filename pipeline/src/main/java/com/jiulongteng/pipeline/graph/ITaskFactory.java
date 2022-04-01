package com.jiulongteng.pipeline.graph;

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
public interface ITaskFactory {

    ITaskAction create(String name);

}

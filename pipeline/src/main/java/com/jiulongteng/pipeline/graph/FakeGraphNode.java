package com.jiulongteng.pipeline.graph;

/**
 * @des:
 * @author: Administrator
 * @createDate: 2022/3/30 0030 19:12
 * @version: 3.3.2
 * @updateDate:
 * @updateUser:
 * @updateRemark:
 * @see {@link }
 */
public class FakeGraphNode <V> extends GraphNode<V>{


    public FakeGraphNode(V value) {
        super(value);
    }

    @Override
    public void dependencyDecrement() {
        unCompletedCount.set(0);
    }
}

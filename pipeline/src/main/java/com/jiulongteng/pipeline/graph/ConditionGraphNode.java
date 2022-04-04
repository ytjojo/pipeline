package com.jiulongteng.pipeline.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * @des:
 * @author: Administrator
 * @createDate: 2022/4/1 0001 19:26
 * @version: 3.3.2
 * @updateDate:
 * @updateUser:
 * @updateRemark:
 * @see {@link }
 */
public class ConditionGraphNode <V> extends GraphNode<V>{

    private Set<GraphNode<V>> successNextNodes;
    private Set<GraphNode<V>> failNextNodes;
    public ConditionGraphNode(V value) {
        super(value);
    }

    public boolean addSuccessNextNodes(GraphNode<V> next) {
        if(successNextNodes ==null){
            successNextNodes = new HashSet<>();
        }
        return successNextNodes.add(next);
    }

    public boolean addFailNextNodes(GraphNode<V> next) {
        if(failNextNodes ==null){
            failNextNodes = new HashSet<>();
        }
        return failNextNodes.add(next);
    }
    public Set<GraphNode<V>> getSuccessNextNodes() {
        return successNextNodes;
    }

    public Set<GraphNode<V>> getFailNextNodes() {
        return failNextNodes;
    }
}

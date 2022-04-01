package com.jiulongteng.pipeline.graph;

import java.util.LinkedList;
import java.util.TreeSet;

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

    private TreeSet<GraphNode<V>> successNextNodes;
    private TreeSet<GraphNode<V>> failNextNodes;
    public ConditionGraphNode(V value) {
        super(value);
    }

    public boolean addSuccessNextNodes(GraphNode<V> next) {
        if(successNextNodes ==null){
            successNextNodes = new TreeSet<>();
        }
        return successNextNodes.add(next);
    }

    public boolean addFailNextNodes(GraphNode<V> next) {
        if(failNextNodes ==null){
            failNextNodes = new TreeSet<>();
        }
        return failNextNodes.add(next);
    }
    public TreeSet<GraphNode<V>> getSuccessNextNodes() {
        return successNextNodes;
    }

    public TreeSet<GraphNode<V>> getFailNextNodes() {
        return failNextNodes;
    }
}

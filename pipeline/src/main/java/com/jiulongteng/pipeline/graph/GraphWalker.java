package com.jiulongteng.pipeline.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * @des:
 * @author: Administrator
 * @createDate: 2022/3/30 0030 16:21
 * @version: 3.3.2
 * @updateDate:
 * @updateUser:
 * @updateRemark:
 * @see {@link }
 */
public abstract class GraphWalker<V> {

    private LinkedHashMap<V, GraphNode<V>> mNodes = new LinkedHashMap<>();
    private HashSet<Edge<V>> mEdges = new HashSet<>();

    public GraphNode<V> addNode(V value) {
        GraphNode<V> graphNode = mNodes.get(value);
        if (graphNode != null) {
            return graphNode;
        }
        return mNodes.put(value, new ConditionGraphNode<>(value));
    }

    public GraphNode<V> addNode(GraphNode<V> node){
        return mNodes.put(node.getValue(),node);
    }
    public void putEdge(GraphNode<V> nextNode ,GraphNode<V> dependencyNode){
        Edge edge =  new Edge(dependencyNode,nextNode);
        mEdges.add(edge);
        nextNode.addEdge(edge);
        dependencyNode.addEdge(edge);
    }



    public void putEdge(V next, V dependencyValue) {
        GraphNode<V> node = addNode(next);
        GraphNode<V> dependencyNode = addNode(dependencyValue);
        Edge edge =  new Edge(dependencyNode,node);
        mEdges.add(edge);
        node.addEdge(edge);
        dependencyNode.addEdge(edge);

    }

    public void putEdge(V value, V ... dependencyValues) {
        GraphNode<V> node = addNode(value);
        for(V dependencyValue : dependencyValues){
            GraphNode<V> dependencyNode = addNode(dependencyValue);
            Edge edge =  new Edge(dependencyNode,node);
            mEdges.add(edge);
            node.addEdge(edge);
            dependencyNode.addEdge(edge);
        }
    }

    public void addSuccessNext(V value ,V dependency) {
        GraphNode<V> dependencyNode = addNode(dependency);
        GraphNode<V> node = addNode(value);
        if(dependencyNode instanceof ConditionGraphNode){
            ConditionGraphNode<V> conditionGraphNode = (ConditionGraphNode<V>) dependencyNode;
            conditionGraphNode.addSuccessNextNodes(node);
        }

    }
    public void addFailNext(V value ,V dependency) {
        GraphNode<V> dependencyNode = addNode(dependency);
        GraphNode<V> node = addNode(value);
        if(dependencyNode instanceof ConditionGraphNode){
            ConditionGraphNode<V> conditionGraphNode = (ConditionGraphNode<V>) dependencyNode;
            conditionGraphNode.addFailNextNodes(node);
        }
    }



    public boolean isDAGraph() {
        HashMap<GraphNode<V>, Integer> nodeInDegreeMap = new HashMap<>();
        ArrayDeque<GraphNode<V>> queue = new ArrayDeque();
        ArrayList<GraphNode<V>> topologicalSortList = new ArrayList(); //拓扑排序列表维护

        // 获取所有入度为0的节点
        for (GraphNode<V> node : mNodes.values()) {
            int indegree = node.getIndegree();
            nodeInDegreeMap.put(node, indegree);
            if (indegree == 0) {
                queue.add(node);
                topologicalSortList.add(node);
            }
        }

        while (!queue.isEmpty()) {
            GraphNode<V> preNode = queue.poll(); //获取并删除
            if(preNode.getNextNodes() == null){
                continue;
            }
            for ( GraphNode<V> successorNode : preNode.getNextNodes()) {
                Integer indegree = nodeInDegreeMap.get(successorNode);
                if(indegree == null){
                    nodeInDegreeMap.put(successorNode,successorNode.getIndegree());
                    indegree = successorNode.getIndegree();
                }
                if (--indegree == 0) {//-1：等效删除父节点以及相应的边
                    queue.offer(successorNode); //insert
                    topologicalSortList.add(successorNode);
                }
                nodeInDegreeMap.put(successorNode, indegree);
            }
        }


        if (topologicalSortList.size() != mNodes.size()) {
            return false;
        }
        return true;
    }

    public List<GraphNode<V>> getBoostNodes(){
        ArrayList<GraphNode<V>> nodes = new ArrayList<>();
        // 获取所有入度为0的节点
        for (GraphNode<V> node : mNodes.values()) {
            int indegree = node.getIndegree();
            if (indegree == 0) {
                nodes.add(node);
            }
        }
        return nodes;

    }

    public GraphNode<V> getNode(V value){
        return mNodes.get(value);
    }

    @Nullable
    public Collection<GraphNode<V>> getNextNodes(V value){
       GraphNode<V> node =  mNodes.get(value);
       if(node != null){
           return node.getNextNodes();
       }
       return null;

    }




}

package com.jiulongteng.pipeline.graph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @des:
 * @author: Administrator
 * @createDate: 2022/3/30 0030 16:26
 * @version: 3.3.2
 * @updateDate:
 * @updateUser:
 * @updateRemark:
 * @see {@link }
 */
public class GraphNode<V> implements Comparable {
    /**
     * 默认的执行优先级
     */
    public static final int DEFAULT_EXECUTE_PRIORITY = 0;


    private V value;
    private Set<GraphNode<V>> nextNodes;
    private HashSet<Edge<V>> edges;

    private int indegree = 0;
    protected AtomicInteger unCompletedCount = new AtomicInteger(0);
    private int outdegree = 0;


    /**
     * 执行优先级，由于线程池是有限的，对于同一时机执行的task，其执行也可能存在先后顺序。值越小，越先执行。
     */
    private int executePriority = DEFAULT_EXECUTE_PRIORITY;


    public GraphNode(V value) {
        this.value = value;

    }


    public void addEdge(Edge<V> edge) {
        if (nextNodes == null) {
            nextNodes = new HashSet<>();
        }
        if (edges == null) {
            edges = new HashSet<>();
        }
        if (edges.contains(edge)) {
            throw new IllegalStateException("already add edge");
        }
        if (edge.from == this) {
            nextNodes.add(edge.to);
            outdegree++;
            if(nextNodes.size() != outdegree){
                throw new IllegalStateException(this.toString() + "not add to nextNode " + edge.to.getValue().toString());
            }
        } else if (edge.to == this) {
            indegree++;
            unCompletedCount.incrementAndGet();
        } else {
            throw new IllegalArgumentException("wrong edge");
        }
        edges.add(edge);
    }

    public void dependencyDecrement() {
        unCompletedCount.decrementAndGet();
    }


    public boolean isAllDependencyCompleted() {
        return unCompletedCount.get() == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphNode<?> graphNode = (GraphNode<?>) o;
        return Objects.equals(value, graphNode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    public V getValue() {
        return value;
    }

    public Set<GraphNode<V>> getNextNodes() {
        return nextNodes;
    }

    public HashSet<Edge<V>> getEdges() {
        return edges;
    }

    public void setIndegree(int indegree) {
        this.indegree = indegree;
    }

    public int getIndegree() {
        return indegree;
    }



    public int getOutdegree() {
        return outdegree;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof GraphNode) {
            GraphNode that = (GraphNode) o;
            if (that.getExecutePriority() > this.getExecutePriority()) {
                return 1;
            } else if (that.getExecutePriority() < this.getExecutePriority()) {
                return -1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    public int getExecutePriority() {
        return executePriority;
    }

    public void setExecutePriority(int executePriority) {
        this.executePriority = executePriority;
    }

    @Override
    public String toString() {
//        return "GraphNode{" +
//                "value=" + value +
//                ", edges=" + edges +
//                ", indegree=" + indegree +
//                ", outdegree=" + outdegree +
//                '}';
        return "GraphNode{" +
                "value=" + value +
                ", edges=" + edges +
                ", nextNodes " + getNextStrings() +


                ", indegree=" + indegree +
                ", outdegree=" + outdegree +
                '}';
    }
    private String getNextStrings(){
        String  result = "[";
        if(nextNodes!= null){
           for (GraphNode<V> node : getNextNodes()){
               result += node.getValue().toString();
               result += " ,";
           }
        }
        result +="]";
        return result;
    }
}

package com.jiulongteng.pipeline.graph;

import java.util.Objects;

/**
 * @des:
 * @author: Administrator
 * @createDate: 2022/3/30 0030 18:54
 * @version: 3.3.2
 * @updateDate:
 * @updateUser:
 * @updateRemark:
 * @see {@link }
 */
public class Edge<V> {
    public Edge(GraphNode<V> from,GraphNode<V> to){
        this.from = from;
        this.to = to;
    }

    public GraphNode<V> from;
    public GraphNode<V> to;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge<?> edge = (Edge<?>) o;
        return Objects.equals(from, edge.from) && Objects.equals(to, edge.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}

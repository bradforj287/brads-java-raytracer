package com.bradforj287.raytracer.model.kdtree;

public class KdTreeQueryStats {
    long nodesVisited;
    long shapesVisited;
    long raysCast;

    public long getNodesVisited() {
        return nodesVisited;
    }

    public void setNodesVisited(long nodesVisited) {
        this.nodesVisited = nodesVisited;
    }

    public long getShapesVisited() {
        return shapesVisited;
    }

    public void setShapesVisited(long shapesVisited) {
        this.shapesVisited = shapesVisited;
    }

    public long getRaysCast() {
        return raysCast;
    }

    public void setRaysCast(long raysCast) {
        this.raysCast = raysCast;
    }

    public void add(KdTreeQueryStats s) {
        this.nodesVisited += s.nodesVisited;
        this.shapesVisited += s.shapesVisited;
        this.raysCast += s.raysCast;
    }
}

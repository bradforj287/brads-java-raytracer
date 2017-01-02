package com.bradforj287.raytracer.model;

public class SpacialStructureQueryStats {
    private int nodesVisited;
    private int shapesVisited;

    public int getShapesVisited() {
        return shapesVisited;
    }

    public void incrementShapesVisited() {
        this.shapesVisited++;
    }

    public void incrementNodesVisited() {
        this.nodesVisited++;
    }

    public int getNodesVisited() {
        return nodesVisited;
    }
}

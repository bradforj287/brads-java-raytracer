package com.bradforj287.raytracer.model;

public class SpacialStructureQueryStats {
    private int nodesVisited;
    private int intersectionChecksPerformed;

    public void incrementNodesVisited() {
        this.nodesVisited++;
    }

    public void incrementIntersectChecks() {
        this.intersectionChecksPerformed++;
    }

    public int getNodesVisited() {
        return nodesVisited;
    }

    public int getIntersectionChecksPerformed() {
        return intersectionChecksPerformed;
    }
}

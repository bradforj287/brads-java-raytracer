package com.bradforj287.raytracer.model.kdtree;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class KDTreeStats {
    private int maxDepth = 0;
    private int numNodes = 0;
    private int numLeafNoes = 0;
    private int totalShapes;
    private DescriptiveStatistics leafNodeSizeStats = new DescriptiveStatistics();


    public void addLeaf() {
        this.numLeafNoes++;
    }

    public void addNode() {
        this.numNodes++;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void setNumLeafNoes(int numLeafNoes) {
        this.numLeafNoes = numLeafNoes;
    }

    public int getNumLeafNoes() {
        return numLeafNoes;
    }

    public int getTotalShapes() {
        return totalShapes;
    }

    public void setTotalShapes(int totalShapes) {
        this.totalShapes = totalShapes;
    }

    public DescriptiveStatistics getLeafNodeSizeStats() {
        return leafNodeSizeStats;
    }

    public void setLeafNodeSizeStats(DescriptiveStatistics leafNodeSizeStats) {
        this.leafNodeSizeStats = leafNodeSizeStats;
    }

    public int getNumNodes() {
        return numNodes;
    }

    public void setNumNodes(int numNodes) {
        this.numNodes = numNodes;
    }
}

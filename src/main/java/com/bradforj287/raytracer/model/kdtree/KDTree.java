package com.bradforj287.raytracer.model.kdtree;

import java.util.ArrayList;
import java.util.List;
import com.bradforj287.raytracer.geometry.AxisAlignedBoundingBox3d;
import com.bradforj287.raytracer.geometry.Ray3d;
import com.bradforj287.raytracer.geometry.Shape3d;
import com.bradforj287.raytracer.geometry.Vector3d;
import com.bradforj287.raytracer.model.ShapeVisitor;
import com.bradforj287.raytracer.model.SpacialStructure;
import com.bradforj287.raytracer.model.SpacialStructureQueryStats;
import com.bradforj287.raytracer.utils.ShapeUtils;

public class KDTree implements SpacialStructure {
    private KDNode root;
    private List<Shape3d> shapes;

    public KDTree(List<Shape3d> shapes) {
        this.shapes = shapes;
        init();
    }

    private void init() {
        root = buildNode(shapes);
        populateTree(root);
        printKdTreeStats();
    }

    private void printKdTreeStats() {
        KDTreeStats stats = getCreationStats();
        System.out.println("Num shapes: " + stats.getTotalShapes());
        System.out.println("Max Depth: " + stats.getMaxDepth());
        System.out.println("Num Leaf Nodes: " + stats.getNumLeafNoes());
        System.out.println("Num Nodes: " + stats.getNumNodes());
        System.out.println(stats.getLeafNodeSizeStats());
    }

    private KDNode buildNode(List<Shape3d> theShapes) {
        if (theShapes.isEmpty()) {
            return null;
        }
        AxisAlignedBoundingBox3d box = ShapeUtils.getBoundsForShapes(theShapes);
        KDNode node = new KDNode();
        node.setBoundingBox(box);
        node.setShapes(theShapes);
        return node;
    }

    private class SplitResult {
        List<Shape3d> leftShapes = new ArrayList<>();
        List<Shape3d> rightShapes = new ArrayList<>();
        public boolean isEmptySplit() {
            return leftShapes.isEmpty() || rightShapes.isEmpty();
        }

        // lower value is better. Ranges from 0 to .5
        public double getSplitScore() {
            double leftSize = leftShapes.size();
            double rightSize = rightShapes.size();
            double totalSize = leftSize + rightSize;
            double percentLeft = leftSize/totalSize;
            final double idealSplit = .50;
            return Math.abs(percentLeft - idealSplit);
        }
    }

    private SplitResult splitShapes(final List<Shape3d> shapes, final String coord, final double midpoint) {
        SplitResult res = new SplitResult();
        for (Shape3d shape : shapes) {
            Vector3d shapeMidpoint = shape.getCentroid();
            double shapeCoord = shapeMidpoint.getCoordiateByName(coord);
            if (shapeCoord <= midpoint) {
                res.leftShapes.add(shape);
            } else {
                res.rightShapes.add(shape);
            }
        }
        return res;
    }

    private void populateTree(KDNode node) {
        // base case #1
        if (node == null || node.getShapes().isEmpty() || node.getShapes().size() <=  10) {
            return;
        }

        // split shapes
        final String coord = node.getBoundingBox().getLongestAxis().toString();
        double midpointCoord =  ShapeUtils.getMedianCenterCoordiate(coord, node.getShapes());

        SplitResult splitResult = splitShapes(node.getShapes(), coord, midpointCoord);

        // base case #2 - if the split is poor don't continue to split
        // TODO: improve heuristic for splitting
        if (splitResult.isEmptySplit() || splitResult.getSplitScore() >= .25) {
            return;
        }

        // left node
        KDNode leftNode = buildNode(splitResult.leftShapes);

        // right node
        KDNode rightNode = buildNode(splitResult.rightShapes);

        // current node
        node.setShapes(null);
        node.setLeft(leftNode);
        node.setRight(rightNode);

        //recurse
        populateTree(leftNode);
        populateTree(rightNode);
    }

    @Override
    public SpacialStructureQueryStats visitPossibleIntersections(final Ray3d ray, final ShapeVisitor visitor) {
        SpacialStructureQueryStats queryStats = new SpacialStructureQueryStats();
        visitPossibleMatchesHelper(root, ray, visitor, queryStats);
        return queryStats;
    }

    private void visitPossibleMatchesHelper(KDNode node, final Ray3d ray, final ShapeVisitor visitor, SpacialStructureQueryStats queryStats) {
        queryStats.incrementNodesVisited();
        if (node.isLeaf()) {
            node.getShapes().forEach(s -> visitor.visit(s));
            return;
        }

        if (node.hasLeft() && node.intersectsBoundingBox(ray)) {
            queryStats.incrementIntersectChecks();
            visitPossibleMatchesHelper(node.getLeft(), ray, visitor, queryStats);
        }

        if (node.hasRight() && node.intersectsBoundingBox(ray)) {
            queryStats.incrementIntersectChecks();
            visitPossibleMatchesHelper(node.getRight(), ray, visitor, queryStats);
        }
    }

    @Override
    public AxisAlignedBoundingBox3d getBounds() {
        return root.getBoundingBox();
    }

    public KDTreeStats getCreationStats() {
        KDTreeStats stats = new KDTreeStats();
        stats.setTotalShapes(this.shapes.size());
        traverseCreationStats(this.root, stats, 0);
        return stats;
    }

    private void traverseCreationStats(KDNode node, KDTreeStats stats, int depth) {
        if (node == null) {
            return;
        }

        if (node.isLeaf()) {
            stats.addLeaf();
            stats.getLeafNodeSizeStats().addValue(node.getShapes().size());
        }

        if (depth > stats.getMaxDepth()) {
            stats.setMaxDepth(depth);
        }
        stats.addNode();
        traverseCreationStats(node.getLeft(), stats, depth + 1);
        traverseCreationStats(node.getRight(), stats, depth + 1);
    }
}

package com.bradforj287.raytracer.model.kdtree;

import java.util.ArrayList;
import java.util.List;
import com.bradforj287.raytracer.geometry.*;
import com.bradforj287.raytracer.model.ShapeVisitor;
import com.bradforj287.raytracer.utils.ShapeBoundsQueue;
import com.bradforj287.raytracer.utils.ShapeUtils;
import com.google.common.base.Preconditions;

public class KDTree {
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

    private PotentialTreeSplit getBestSplitSah(final List<PotentialTreeSplit> splits) {
        return splits.stream()
                .min((a, b) -> a.getSahHeuristic().compareTo(b.getSahHeuristic()))
                .get();
    }

    private PotentialTreeSplit getBestSplitOnAxisSah(final List<Shape3d> shapes, Axis axis) {
        Preconditions.checkArgument(shapes.size() > 1);

        List<Shape3d> sortedShapes = ShapeUtils.sortByAxis(shapes, axis);
        ShapeBoundsQueue rhs = new ShapeBoundsQueue();
        ShapeBoundsQueue lhs = new ShapeBoundsQueue();
        rhs.addAll(sortedShapes);

        double minSah = Double.MAX_VALUE;
        int minSahLhsSize = 1;

        while (rhs.size() > 1) {
            lhs.add(rhs.remove());

            double sah = lhs.getBoundingBox().getSurfaceArea() * lhs.size() + rhs.getBoundingBox().getSurfaceArea() * rhs.size();
            if (sah < minSah) {
                minSah = sah;
                minSahLhsSize = lhs.size();
            }
        }

        List<Shape3d> leftShapes = sortedShapes.subList(0, minSahLhsSize);
        List<Shape3d> rightShapes = sortedShapes.subList(minSahLhsSize, sortedShapes.size());

        return new PotentialTreeSplit(leftShapes, rightShapes);
    }

    private PotentialTreeSplit getBestSplitSahStrategy(final List<Shape3d> shapes) {
        List<PotentialTreeSplit> potentialTreeSplits = new ArrayList<>();
        potentialTreeSplits.add(getBestSplitOnAxisSah(shapes, Axis.X));
        potentialTreeSplits.add(getBestSplitOnAxisSah(shapes, Axis.Y));
        potentialTreeSplits.add(getBestSplitOnAxisSah(shapes, Axis.Z));
        return getBestSplitSah(potentialTreeSplits);
    }

    private void populateTree(KDNode node) {
        Preconditions.checkNotNull(node);
        Preconditions.checkNotNull(node.getShapes());
        Preconditions.checkArgument(!node.getShapes().isEmpty());

        // base case #1 - min split
        if (node.getShapes().size() <= 2) {
            return;
        }

        // split shapes
        PotentialTreeSplit optimal = getBestSplitSahStrategy(node.getShapes());

        // base case #2 - if the split is empty we cant split */
        if (optimal.isEmptySplit()) {
            return;
        }

        // left node
        KDNode leftNode = buildNode(optimal.getLeftShapes());

        // right node
        KDNode rightNode = buildNode(optimal.getRightShapes());

        // current node
        node.setShapes(null);
        node.setLeft(leftNode);
        node.setRight(rightNode);

        //recurse
        populateTree(leftNode);
        populateTree(rightNode);
    }

    public KdTreeQueryStats visitPossibleIntersections(final Ray3d ray, final ShapeVisitor visitor) {
        KdTreeQueryStats queryStats = new KdTreeQueryStats();
        queryStats.raysCast++;
        visitPossibleMatchesHelper(root, ray, visitor, queryStats);
        return queryStats;
    }

    private void visitPossibleMatchesHelper(KDNode node, final Ray3d ray, final ShapeVisitor visitor, KdTreeQueryStats queryStats) {
        queryStats.nodesVisited++;
        if (node.isLeaf()) {
            queryStats.shapesVisited += node.getShapes().size();
            for (Shape3d shape : node.getShapes()) {
                visitor.visit(shape);
            }
            return;
        }

        if (node.hasLeft() && node.intersectsBoundingBox(ray)) {
            visitPossibleMatchesHelper(node.getLeft(), ray, visitor, queryStats);
        }

        if (node.hasRight() && node.intersectsBoundingBox(ray)) {
            visitPossibleMatchesHelper(node.getRight(), ray, visitor, queryStats);
        }
    }

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

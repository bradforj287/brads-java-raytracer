package com.bradforj287.raytracer.model.kdtree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.bradforj287.raytracer.geometry.*;
import com.bradforj287.raytracer.model.ShapeVisitor;
import com.bradforj287.raytracer.model.SpacialStructure;
import com.bradforj287.raytracer.model.SpacialStructureQueryStats;
import com.bradforj287.raytracer.utils.ShapeUtils;
import com.google.common.base.Preconditions;

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

    private PotentialTreeSplit splitShapesByAxisPoint(final List<Shape3d> shapes, final Axis axis, double splitPoint) {
        final String coord = axis.toCoordinateName();
        List<Shape3d> left = new ArrayList<>();
        List<Shape3d> right = new ArrayList<>();
        List<Shape3d> equalShapes = new ArrayList<>();
        for (Shape3d shape : shapes) {
            Vector3d shapeMidpoint = shape.getCentroid();
            double shapeCoord = shapeMidpoint.getCoordiateByName(coord);
            if (shapeCoord == splitPoint) {
                equalShapes.add(shape);
            } else if (shapeCoord < splitPoint) {
                left.add(shape);
            } else {
                right.add(shape);
            }
        }
        // add equal shapes to the list that is smaller.
        if (left.size() < right.size()) {
            left.addAll(equalShapes);
        } else {
            right.addAll(equalShapes);
        }
        return new PotentialTreeSplit(left, right);
    }

    private PotentialTreeSplit getBestSplitOnAxisSah(final List<Shape3d> shapes, Axis axis) {
        List<Shape3d> sortedByAxis = shapes.stream().sorted((a, b) -> {
            Double aa = a.getCentroid().getCoordiateByAxis(axis);
            Double bb = b.getCentroid().getCoordiateByAxis(axis);
            return aa.compareTo(bb);
        }).collect(Collectors.toList());

        List<PotentialTreeSplit> splits = new ArrayList<>();
        for (Shape3d shape: sortedByAxis) {
            double splitPoint = shape.getCentroid().getCoordiateByAxis(axis);
            splits.add(splitShapesByAxisPoint(shapes, axis, splitPoint));
        }

        PotentialTreeSplit optimal = splits.stream()
                .min((a, b) -> a.getSahHeuristic().compareTo(b.getSahHeuristic()))
                .get();
        return optimal;
    }

    private PotentialTreeSplit splitShapesByAxisAvg(final List<Shape3d> shapes, final Axis axis) {
        final String coord = axis.toCoordinateName();
        double midpoint = ShapeUtils.getAverageCenterCoordiate(coord, shapes);
        return splitShapesByAxisPoint(shapes, axis, midpoint);
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
        List<PotentialTreeSplit> potentialTreeSplits = new ArrayList<>();
        potentialTreeSplits.add(splitShapesByAxisAvg(node.getShapes(), Axis.X));
        potentialTreeSplits.add(splitShapesByAxisAvg(node.getShapes(), Axis.Y));
        potentialTreeSplits.add(splitShapesByAxisAvg(node.getShapes(), Axis.Z));

        PotentialTreeSplit optimal = potentialTreeSplits.stream()
                .min((a, b) -> a.getSahHeuristic().compareTo(b.getSahHeuristic()))
                .get();

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

    @Override
    public SpacialStructureQueryStats visitPossibleIntersections(final Ray3d ray, final ShapeVisitor visitor) {
        SpacialStructureQueryStats queryStats = new SpacialStructureQueryStats();
        visitPossibleMatchesHelper(root, ray, visitor, queryStats);
        return queryStats;
    }

    private void visitPossibleMatchesHelper(KDNode node, final Ray3d ray, final ShapeVisitor visitor, SpacialStructureQueryStats queryStats) {
        queryStats.incrementNodesVisited();
        if (node.isLeaf()) {
            for (Shape3d shape : node.getShapes()) {
                queryStats.incrementShapesVisited();
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

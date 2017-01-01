package com.bradforj287.raytracer.model.kdtree;

import java.util.ArrayList;
import java.util.List;
import com.bradforj287.raytracer.geometry.AxisAlignedBoundingBox3d;
import com.bradforj287.raytracer.geometry.Ray3d;
import com.bradforj287.raytracer.geometry.Shape3d;
import com.bradforj287.raytracer.geometry.Vector3d;
import com.bradforj287.raytracer.model.SpacialStructure;
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

        // lower value is better
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
        if (node == null || node.getShapes().isEmpty() || node.getShapes().size() <=  1) {
            return; // todo: enhance logic for detecting when to stop
        }

        // split shapes
        final String coord = node.getBoundingBox().getLongestAxis().toString();
        double midpointCoord =  ShapeUtils.getMedianCenterCoordiate(coord, node.getShapes());
        double length = node.getBoundingBox().getMax().getCoordiateByName(coord) - node.getBoundingBox().getMin().getCoordiateByName(coord);
        final double randomShift = .01 * length;

        SplitResult splitResult = splitShapes(node.getShapes(), coord, midpointCoord);

        if (splitResult.isEmptySplit()) {
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
    public void visitPossibleIntersections(final Ray3d ray, final ShapeVisitor visitor) {
        visitPossibleMatchesHelper(root, ray, visitor);
    }

    private void visitPossibleMatchesHelper(KDNode node, final Ray3d ray, final ShapeVisitor visitor) {
        if (node.isLeaf()) {
            node.getShapes().forEach(s -> visitor.visit(s));
            return;
        }

        if (node.hasLeft() && node.intersectsBoundingBox(ray)) {
            visitPossibleMatchesHelper(node.getLeft(), ray, visitor);
        }

        if (node.hasRight() && node.intersectsBoundingBox(ray)) {
            visitPossibleMatchesHelper(node.getRight(), ray, visitor);
        }
    }

    @Override
    public AxisAlignedBoundingBox3d getBounds() {
        return root.getBoundingBox();
    }
}

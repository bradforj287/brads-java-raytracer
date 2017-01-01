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
        populateTree(root, "x");
    }

    private String getNextCoordInSequence(String currentCoord) {
        if (currentCoord.equals("x")) {
            return "y";
        }
        if (currentCoord.equals("y")) {
            return "z";
        }
        if (currentCoord.equals("z")) {
            return "x";
        }
        return null;
    }

    private KDNode buildNode(List<Shape3d> shapes) {
        AxisAlignedBoundingBox3d box = ShapeUtils.getBoundsForShapes(shapes);
        KDNode node = new KDNode();
        node.setBoundingBox(box);
        node.setShapes(shapes);
        return node;
    }

    private void populateTree(KDNode node, final String coord) {
        if (node.getShapes().isEmpty() || node.getShapes().size() <=  10) {
            return; // todo: enhance logic for detecting when to stop
        }

        List<Shape3d> leftShapes = new ArrayList<>();
        List<Shape3d> rightShapes = new ArrayList<>();

        Vector3d midpoint = ShapeUtils.getMidpoint(node.getShapes());
        double midpointCoord = midpoint.getCoordiateByName(coord);

        //bucket shapes
        for (Shape3d shape : node.getShapes()) {
            Vector3d shapeMidpoint = shape.getCentroid();
            double shapeCoord = shapeMidpoint.getCoordiateByName(coord);
            if (shapeCoord <= midpointCoord) {
                leftShapes.add(shape);
            } else {
                rightShapes.add(shape);
            }
        }

        // left node
        KDNode leftNode = buildNode(leftShapes);

        // right node
        KDNode rightNode = buildNode(rightShapes);

        // current node
        node.setShapes(null);
        node.setLeft(leftNode);
        node.setRight(rightNode);

        //recurse
        final String nextCoordToUse = getNextCoordInSequence(coord);
        populateTree(leftNode, nextCoordToUse);
        populateTree(rightNode, nextCoordToUse);
    }

    @Override
    public void visitPossibleMatches(final Ray3d ray, final ShapeVisitor visitor) {
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

package com.bradforj287.raytracer.model;

import java.util.ArrayList;
import java.util.List;
import com.bradforj287.raytracer.geometry.*;
import com.bradforj287.raytracer.utils.ShapeUtils;
import com.google.common.base.Preconditions;

public class KDTree {
    private KDNode root;
    private List<Shape3D> shapes;

    public KDTree(List<Shape3D> shapes) {
        this.shapes = shapes;
        init();
    }

    private void init() {
        // create root
        BoundingBox3D box = calculateBoundingBox(this.shapes);
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

    private KDNode buildNode(List<Shape3D> shapes) {
        BoundingBox3D box = calculateBoundingBox(shapes);
        KDNode node = new KDNode();
        node.setBoundingBox(box);
        node.setShapes(shapes);
        return node;
    }

    private void populateTree(KDNode node, final String coord) {
        if (node.getShapes().isEmpty() || node.getShapes().size() <=  10) {
            return; // todo: enhance logic for detecting when to stop
        }

        List<Shape3D> leftShapes = new ArrayList<>();
        List<Shape3D> rightShapes = new ArrayList<>();

        Vector3D midpoint = ShapeUtils.getMidpoint(node.getShapes());
        double midpointCoord = midpoint.getCoordiateByName(coord);

        //bucket shapes
        for (Shape3D shape : node.getShapes()) {
            Vector3D shapeMidpoint = shape.getCentroid();
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

    private BoundingBox3D calculateBoundingBox(List<Shape3D> shapes) {
        Preconditions.checkNotNull(shapes);
        Preconditions.checkArgument(!shapes.isEmpty());

        Bounds3D bounds = ShapeUtils.getBoundsForShapes(shapes);
        return new BoundingBox3D(bounds);
    }

}

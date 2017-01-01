package com.bradforj287.raytracer.model;

import java.util.List;
import com.bradforj287.raytracer.geometry.BoundingBox3D;
import com.bradforj287.raytracer.geometry.Shape3D;

public class KDNode {
    private BoundingBox3D boundingBox;
    private KDNode left;
    private KDNode right;
    private List<Shape3D> shapes;

    public boolean isLeaf() {
        return left == null && right == null;
    }
    public BoundingBox3D getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox3D boundingBox) {
        this.boundingBox = boundingBox;
    }

    public KDNode getLeft() {
        return left;
    }

    public void setLeft(KDNode left) {
        this.left = left;
    }

    public KDNode getRight() {
        return right;
    }

    public void setRight(KDNode right) {
        this.right = right;
    }

    public List<Shape3D> getShapes() {
        return shapes;
    }

    public void setShapes(List<Shape3D> shapes) {
        this.shapes = shapes;
    }
}

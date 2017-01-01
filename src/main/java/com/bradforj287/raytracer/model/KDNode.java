package com.bradforj287.raytracer.model;

import java.util.List;
import com.bradforj287.raytracer.geometry.AxisAlignedBoundingBox3d;
import com.bradforj287.raytracer.geometry.Shape3d;

public class KDNode {
    private AxisAlignedBoundingBox3d boundingBox;
    private KDNode left;
    private KDNode right;
    private List<Shape3d> shapes;

    public boolean isLeaf() {
        return left == null && right == null;
    }
    public AxisAlignedBoundingBox3d getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(AxisAlignedBoundingBox3d boundingBox) {
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

    public List<Shape3d> getShapes() {
        return shapes;
    }

    public void setShapes(List<Shape3d> shapes) {
        this.shapes = shapes;
    }
}

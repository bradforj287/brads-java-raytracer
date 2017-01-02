package com.bradforj287.raytracer.model.kdtree;

import java.util.List;
import com.bradforj287.raytracer.geometry.Shape3d;
import com.bradforj287.raytracer.utils.ShapeUtils;

public class PotentialTreeSplit {
    private final List<Shape3d> leftShapes;
    private final List<Shape3d> rightShapes;
    private Double surfaceAreaLeft;
    private Double surfaceAreaRight;

    public PotentialTreeSplit(List<Shape3d> leftShapes, List<Shape3d> rightShapes) {
        this.leftShapes = leftShapes;
        this.rightShapes = rightShapes;
    }

    private double getSurfaceAreaRight() {
        if (surfaceAreaRight == null) {
            surfaceAreaRight = rightShapes.isEmpty() ? 0 : ShapeUtils.getBoundsForShapes(rightShapes).getSurfaceArea();
        }
        return surfaceAreaRight;
    }

    private double getSurfaceAreaLeft() {
        if (surfaceAreaLeft == null) {
            surfaceAreaLeft = leftShapes.isEmpty() ? 0 : ShapeUtils.getBoundsForShapes(leftShapes).getSurfaceArea();
        }
        return surfaceAreaLeft;
    }

    public boolean isEmptySplit() {
        return leftShapes.isEmpty() || rightShapes.isEmpty();
    }

    // gets the Surface Area Heuristic (SAH) which is used for determining optimal split
    public Double getSahHeuristic() {
        double numLeft = leftShapes.size();
        double numRight = rightShapes.size();
        return getSurfaceAreaLeft()*numLeft + getSurfaceAreaRight()*numRight;
    }

    public List<Shape3d> getLeftShapes() {
        return leftShapes;
    }

    public List<Shape3d> getRightShapes() {
        return rightShapes;
    }
}

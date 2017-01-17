package com.bradforj287.raytracer.engine;

import com.bradforj287.raytracer.geometry.Shape3d;
import com.bradforj287.raytracer.geometry.ShapeHit;

public class RayHitResult {
    private Shape3d shape;
    private ShapeHit shapeHit;
    private double t;

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }

    public ShapeHit getShapeHit() {
        return shapeHit;
    }

    public void setShapeHit(ShapeHit shapeHit) {
        this.shapeHit = shapeHit;
    }

    public boolean didHitShape() {
        return shape != null;
    }

    public Shape3d getShape() {
        return shape;
    }

    public void setShape(Shape3d shape) {
        this.shape = shape;
    }

}

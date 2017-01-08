package com.bradforj287.raytracer.engine;

import com.bradforj287.raytracer.geometry.Shape3d;

public class RayHitResult {
    private Shape3d shape;
    private double t;

    public boolean didHitShape() {
        return shape != null;
    }

    public Shape3d getShape() {
        return shape;
    }

    public void setShape(Shape3d shape) {
        this.shape = shape;
    }

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }
}

package com.bradforj287.raytracer.geometry;

public class ShapeHit {
    private final double t;
    private final Shape3d hitShape;

    public ShapeHit(double t, Shape3d hitShape) {
        this.t = t;
        this.hitShape = hitShape;
    }

    public double getT() {
        return t;
    }

    public Shape3d getHitShape() {
        return hitShape;
    }
}

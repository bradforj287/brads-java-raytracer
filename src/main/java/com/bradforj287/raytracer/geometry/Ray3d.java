package com.bradforj287.raytracer.geometry;

public class Ray3d {
    private final Vector3d point;
    private final Vector3d direction;

    public Ray3d(Vector3d point, Vector3d direction) {
        this.point = point;
        this.direction = direction;
    }

    public Ray3d shiftByT(double t) {
        Vector3d newPoint = point.add(direction.multiply(t));
        return new Ray3d(newPoint, direction);
    }

    public Vector3d getPoint() {
        return point;
    }

    public Vector3d getDirection() {
        return direction;
    }
}

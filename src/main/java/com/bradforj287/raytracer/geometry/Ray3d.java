package com.bradforj287.raytracer.geometry;

public class Ray3d {
    // the purpose of this is to overcome the self intersection problem
    public static final double DEFAULT_SHIFT = .00001;

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

    public static Ray3d createShiftedRay(Vector3d point, Vector3d direction) {
        Vector3d newPoint = point.add(direction.multiply(DEFAULT_SHIFT));
        return new Ray3d(newPoint, direction);
    }

    public Vector3d getPoint() {
        return point;
    }

    public Vector3d getDirection() {
        return direction;
    }
}

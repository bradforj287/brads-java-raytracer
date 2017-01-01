package com.bradforj287.raytracer.geometry;

public class Ray3d {
    private Vector3d point;
    private Vector3d direction;

    public Ray3d(Vector3d point, Vector3d direction) {
        this.point = point;
        this.direction = direction;
    }

    public Vector3d getPoint() {
        return point;
    }

    public Vector3d getDirection() {
        return direction;
    }
}

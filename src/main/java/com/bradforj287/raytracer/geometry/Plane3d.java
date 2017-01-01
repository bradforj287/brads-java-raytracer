package com.bradforj287.raytracer.geometry;

public class Plane3d {
    private Vector3d normal;
    private Vector3d point;

    public Plane3d(Vector3d normal, Vector3d point) {
        this.normal = normal;
        this.point = point;
        normal.makeUnitVector();
    }

    public Vector3d getNormal() {
        return normal;
    }

    public Vector3d getPoint() {
        return point;
    }
}

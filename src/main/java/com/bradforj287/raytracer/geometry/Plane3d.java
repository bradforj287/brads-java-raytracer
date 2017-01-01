package com.bradforj287.raytracer.geometry;

public class Plane3d {
    private Vector3D normal;
    private Vector3D point;

    public Plane3d(Vector3D normal, Vector3D point) {
        this.normal = normal;
        this.point = point;
        normal.makeUnitVector();
    }

    public Vector3D getNormal() {
        return normal;
    }

    public Vector3D getPoint() {
        return point;
    }
}

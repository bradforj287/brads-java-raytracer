package com.bradforj287.raytracer.geometry;

public class AxisAlignedBoundingBox3D {
    private Vector3D min;
    private Vector3D max;

    public AxisAlignedBoundingBox3D(Vector3D min, Vector3D max) {
        this.min = min;
        this.max = max;
    }

    public Vector3D getMin() {
        return min;
    }

    public Vector3D getMax() {
        return max;
    }
}

package com.bradforj287.raytracer.geometry;

public class AxisAlignedBoundingBox3d {
    private Vector3d min;
    private Vector3d max;

    public AxisAlignedBoundingBox3d(Vector3d min, Vector3d max) {
        this.min = min;
        this.max = max;
    }

    public Vector3d getMin() {
        return min;
    }

    public Vector3d getMax() {
        return max;
    }

    public boolean rayItersects(Ray3d ray) {
        return false;
    }
}

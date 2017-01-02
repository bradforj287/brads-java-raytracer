package com.bradforj287.raytracer.geometry;

public abstract class Shape3d {
    protected int color = 0;

    public abstract AxisAlignedBoundingBox3d getBoundingBox();

    public abstract Vector3d normalAtSurfacePoint(Vector3d intersectPoint);

    public abstract Vector3d getCentroid();

    public abstract boolean isHitByRay(Ray3d ray, double t0, double t1, RayCastArguments returnArgs);

    public int getColor() {
        return color;
    }

    public double minX() {
        return getBoundingBox().getMin().getX();
    }

    public double minY() {
        return getBoundingBox().getMin().getY();
    }

    public double minZ() {
        return getBoundingBox().getMin().getZ();
    }

    public double maxX() {
        return getBoundingBox().getMax().getX();
    }

    public double maxY() {
        return getBoundingBox().getMax().getY();
    }

    public double maxZ() {
        return getBoundingBox().getMax().getZ();
    }
}

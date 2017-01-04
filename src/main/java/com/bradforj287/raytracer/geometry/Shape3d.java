package com.bradforj287.raytracer.geometry;

public abstract class Shape3d {

    public abstract Surface getSurface();

    public abstract AxisAlignedBoundingBox3d getBoundingBox();

    public abstract Vector3d normalAtSurfacePoint(Vector3d intersectPoint);

    public abstract Vector3d getCentroid();

    public abstract boolean isHitByRay(Ray3d ray, double t0, double t1, RayCastArguments returnArgs);

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

    public double getCosOfIntersectionAngle(Ray3d ray, Vector3d hitloc) {
        Vector3d dir = ray.getDirection().toUnitVector().flippedInOtherDiretion();
        Vector3d normal = this.normalAtSurfacePoint(hitloc);
        return dir.dot(normal);
    }

    public boolean isRayEnteringShape(Ray3d ray, Vector3d hitLoc) {
        return getCosOfIntersectionAngle(ray, hitLoc) > 0;
    }

    public boolean isRayExitingShape(Ray3d ray, Vector3d hitLoc) {
        return getCosOfIntersectionAngle(ray, hitLoc) < 0;
    }
}

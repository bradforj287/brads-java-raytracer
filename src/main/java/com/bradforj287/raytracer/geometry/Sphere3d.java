package com.bradforj287.raytracer.geometry;

public class Sphere3d extends Shape3d {
    private final Vector3d center;
    private final double radius;
    private final Surface surface;

    public Sphere3d(Vector3d center, double radius, Surface surface) {
        this.center = center;
        this.radius = radius;
        this.surface = surface;
    }

    @Override
    public AxisAlignedBoundingBox3d getBoundingBox() {
        Vector3d min = new Vector3d(center.x - radius, center.y - radius, center.z - radius);
        Vector3d max = new Vector3d(center.x + radius, center.y + radius, center.z + radius);
        return new AxisAlignedBoundingBox3d(min, max);
    }

    @Override
    public Vector3d normalAtSurfacePoint(Vector3d intersectPoint) {
        return intersectPoint.subtract(this.center).toUnitVector();
    }

    @Override
    public Vector3d getCentroid() {
        return this.center;
    }

    @Override
    public ShapeHit isHitByRay(Ray3d ray, double t1) {
        Vector3d dir = ray.getDirection();
        Vector3d eye = ray.getPoint();

        final Vector3d eyeMinusCenter = eye.subtract(center);
        double dis = dir.dot(eyeMinusCenter);

        dis = dis * dis;
        dis = dis - dir.dot(dir) * (eyeMinusCenter.dot(eyeMinusCenter) - radius * radius);

        if (dis < 0) {
            return null;
        }
        dis = Math.sqrt(dis);

        double shitTerm = dir.multiply(-1).dot(eyeMinusCenter);
        double bottomTerm = dir.dot(dir);

        double time_1 = (shitTerm - dis) / bottomTerm;
        double time_2 = (shitTerm + dis) / bottomTerm;

        double tToReturn;
        if (time_1 < 0 && time_2 < 0) {
            return null;
        } else if (time_1 < 0) {
            tToReturn = time_2;
        } else if (time_2 < 0) {
            tToReturn = time_1;
        } else {
            if (time_1 < time_2) {
                tToReturn = time_1;
            } else {
                tToReturn = time_2;
            }
        }
        if (tToReturn < 0 || tToReturn > t1) {
            return null;
        }

        return new ShapeHit(tToReturn, this);
    }

    @Override
    public Surface getSurface() {
        return surface;
    }
}

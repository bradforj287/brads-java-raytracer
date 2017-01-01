package com.bradforj287.raytracer.geometry;

public class AxisAlignedBoundingBox3d {
    private Vector3d min;
    private Vector3d max;
    private Vector3d[] bounds = new Vector3d[2];

    public AxisAlignedBoundingBox3d(Vector3d min, Vector3d max) {
        this.min = min;
        this.max = max;
        bounds[0] = min;
        bounds[1] = max;
    }

    public Vector3d getMin() {
        return min;
    }

    public Vector3d getMax() {
        return max;
    }

    /**
     * algorithm adatped from
     * https://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-box-intersection
     * @param ray
     * @return
     */
    public boolean rayItersects(Ray3d ray) {
        int[] sign = new int[3];
        Vector3d invdir = new Vector3d(1/ray.getDirection().x, 1/ray.getDirection().y, 1/ray.getDirection().z);
        Vector3d orig = ray.getPoint();
        sign[0] = (invdir.x < 0) ? 1 : 0;
        sign[1] = (invdir.y < 0) ? 1 : 0;
        sign[2] = (invdir.z < 0) ? 1 : 0;

        double tmin, tmax, tymin, tymax, tzmin, tzmax;

        tmin = (bounds[sign[0]].x - orig.x) * invdir.x;
        tmax = (bounds[1-sign[0]].x - orig.x) * invdir.x;
        tymin = (bounds[sign[1]].y - orig.y) * invdir.y;
        tymax = (bounds[1-sign[1]].y - orig.y) * invdir.y;

        if ((tmin > tymax) || (tymin > tmax))
            return false;
        if (tymin > tmin)
            tmin = tymin;
        if (tymax < tmax)
            tmax = tymax;

        tzmin = (bounds[sign[2]].z - orig.z) * invdir.z;
        tzmax = (bounds[1-sign[2]].z - orig.z) * invdir.z;

        if ((tmin > tzmax) || (tzmin > tmax))
            return false;
        /*if (tzmin > tmin)
            tmin = tzmin;
        if (tzmax < tmax)
            tmax = tzmax;*/

        return true;

    }
}

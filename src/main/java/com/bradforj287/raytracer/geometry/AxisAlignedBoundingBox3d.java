package com.bradforj287.raytracer.geometry;

import java.util.ArrayList;
import java.util.List;

public class AxisAlignedBoundingBox3d {
    private final Vector3d min;
    private final Vector3d max;
    private final Vector3d[] bounds = new Vector3d[2];

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

    public double xLength() {
        return this.max.x - this.min.x;
    }

    public double yLength() {
        return this.max.y - this.min.y;
    }

    public double zLength() {
        return this.max.z - this.min.z;
    }

    public double getSurfaceArea() {
        double botAndTop = 2*xLength()*yLength();
        double sides = 2*zLength()*xLength();
        double frontBack = 2*zLength()*yLength();
        return botAndTop + sides + frontBack;
    }

    public AxisAlignedBoundingBox3d translate(Vector3d v) {
        return new AxisAlignedBoundingBox3d(min.add(v), max.add(v));
    }

    private class AxisLength {
        private Axis axis;
        private double length;

        public AxisLength(Axis axis, double length) {
            this.axis = axis;
            this.length = length;
        }
    }

    public Axis getLongestAxis() {
        List<AxisLength> lengths = new ArrayList<>();
        lengths.add(new AxisLength(Axis.X, xLength()));
        lengths.add(new AxisLength(Axis.Y, yLength()));
        lengths.add(new AxisLength(Axis.Z, zLength()));

        return lengths.stream().max((a, b) -> {
            Double ad = a.length;
            Double ab = b.length;
            return ad.compareTo(ab);
        }).get().axis;
    }

    /**
     * algorithm adatped from
     * https://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-box-intersection
     *
     * @param ray
     * @return
     */
    public boolean rayItersects(Ray3d ray) {
        int[] sign = new int[3];
        Vector3d invdir = new Vector3d(1 / ray.getDirection().x, 1 / ray.getDirection().y, 1 / ray.getDirection().z);
        Vector3d orig = ray.getPoint();
        sign[0] = (invdir.x < 0) ? 1 : 0;
        sign[1] = (invdir.y < 0) ? 1 : 0;
        sign[2] = (invdir.z < 0) ? 1 : 0;

        double tmin, tmax, tymin, tymax, tzmin, tzmax;

        tmin = (bounds[sign[0]].x - orig.x) * invdir.x;
        tmax = (bounds[1 - sign[0]].x - orig.x) * invdir.x;
        tymin = (bounds[sign[1]].y - orig.y) * invdir.y;
        tymax = (bounds[1 - sign[1]].y - orig.y) * invdir.y;

        if ((tmin > tymax) || (tymin > tmax))
            return false;
        if (tymin > tmin)
            tmin = tymin;
        if (tymax < tmax)
            tmax = tymax;

        tzmin = (bounds[sign[2]].z - orig.z) * invdir.z;
        tzmax = (bounds[1 - sign[2]].z - orig.z) * invdir.z;

        if ((tmin > tzmax) || (tzmin > tmax))
            return false;
        if (tzmax < tmax)
            tmax = tzmax;

        return (tmax > 0);
    }
}

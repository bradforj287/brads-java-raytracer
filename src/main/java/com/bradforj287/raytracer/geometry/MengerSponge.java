package com.bradforj287.raytracer.geometry;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * recursively defined menger sponge
 */
public class MengerSponge extends Shape3d {
    final private int level;
    final private Surface surface;
    final private AxisAlignedBoundingBox3d aabb;

    public MengerSponge(AxisAlignedBoundingBox3d aabb, int level, Surface surface) {
        this.level = level;
        this.surface = surface;
        this.aabb = aabb;
    }

    @Override
    public Surface getSurface() {
        return this.surface;
    }

    @Override
    public AxisAlignedBoundingBox3d getBoundingBox() {
        return aabb;
    }

    @Override
    public Vector3d getCentroid() {
        return aabb.getMin().add(aabb.getMax()).multiply(.5);
    }

    @Override
    public Vector3d normalAtSurfacePoint(Vector3d intersectPoint) {
        throw new NotImplementedException();
    }

    private ShapeHit mengerTriangleHit(Ray3d ray, double t1, List<AxisAlignedBoundingBox3d> boxes) {
        List<Triangle3d> triangles = new ArrayList<>();
        boxes.forEach(b -> triangles.addAll(ShapeFactory.buildAxisAlignedTriangleBox(b, this.surface)));

        double minHitT = Double.MIN_VALUE;
        Triangle3d minHit = null;

        for (Triangle3d tri : triangles) {
            ShapeHit shapeHit = tri.isHitByRay(ray, t1);
            if (shapeHit != null) {
                if (minHit == null || shapeHit.getT() < minHitT) {
                    minHit = tri;
                    minHitT = shapeHit.getT();
                }
            }
        }

        if (minHit == null) {
            return null;
        } else {
           return new ShapeHit(minHitT, minHit);
        }
    }

    @Override
    public ShapeHit isHitByRay(Ray3d ray, double t1) {
        List<AxisAlignedBoundingBox3d> lookAt = new ArrayList<>();
        lookAt.add(aabb);

        for (int lvl = 0; lvl <= this.level; lvl++) {
            // filter
            List<AxisAlignedBoundingBox3d> filtered = lookAt.stream()
                    .filter(ab -> ab.rayItersects(ray))
                    .collect(Collectors.toList());

            if (filtered.isEmpty()) {
                return null;
            }

            // if last iteration then check triangles
            boolean lastItr = (lvl == this.level);
            if (lastItr) {
                return mengerTriangleHit(ray, t1, filtered);
            }

            // compute next iteration
            lookAt = new ArrayList<>();
            for (AxisAlignedBoundingBox3d box : filtered) {
                lookAt.addAll(ShapeFactory.mengerSplit(box));
            }
        }

        return null;
    }
}

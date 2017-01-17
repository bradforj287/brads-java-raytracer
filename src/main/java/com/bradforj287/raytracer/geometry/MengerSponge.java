package com.bradforj287.raytracer.geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * recursively defined menger sponge
 */
public class MengerSponge extends Shape3d {
    final int level;
    final Surface surface;
    final AxisAlignedBoundingBox3d aabb;

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
    public Vector3d normalAtSurfacePoint(Vector3d intersectPoint) {
        return null;
    }

    @Override
    public Vector3d getCentroid() {
        return null;
    }

    private boolean mengerTriangleHit(Ray3d ray, double t1, List<AxisAlignedBoundingBox3d> boxes, RayCastArguments returnArgs) {
        List<Triangle3d> triangles = new ArrayList<>();
        boxes.forEach(b -> triangles.addAll(ShapeFactory.buildAxisAlignedTriangleBox(b, this.surface)));

        double minHitT = Double.MIN_VALUE;
        Triangle3d minHit = null;

        for (Triangle3d tri : triangles) {
            RayCastArguments args = new RayCastArguments();
            if (tri.isHitByRay(ray, t1, args)) {
                if (minHit == null || args.t < minHitT) {
                    minHit = tri;
                    minHitT = args.t;
                }
            }
        }

        if (minHit == null) {
            return false;
        } else {
            returnArgs.t = minHitT;
            return true;
        }
    }

    @Override
    public boolean isHitByRay(Ray3d ray, double t1, RayCastArguments returnArgs) {
        List<AxisAlignedBoundingBox3d> lookAt = new ArrayList<>();
        lookAt.add(aabb);

        for (int lvl = 0; lvl <= this.level; lvl++) {
            // filter
            List<AxisAlignedBoundingBox3d> filtered = lookAt.stream()
                    .filter(ab -> ab.rayItersects(ray))
                    .collect(Collectors.toList());

            if (filtered.isEmpty()) {
                return false;
            }

            // if last iteration then check triangles
            boolean lastItr = (lvl == this.level);
            if (lastItr) {
                return mengerTriangleHit(ray, t1, filtered, returnArgs);
            }

            // compute next iteration
            lookAt = new ArrayList<>();
            for (AxisAlignedBoundingBox3d box : filtered) {
                lookAt.addAll(ShapeFactory.mengerSplit(box));
            }
        }

        return false;
    }
}
